// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.messages.MapNameRequest;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Inet4Address;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.MapBlockReply;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.FindAgent;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;

public class SLWorldMap extends SLModule
{
    private String teleportTargetName;
    private int teleportTargetX;
    private int teleportTargetY;
    private int teleportTargetZ;
    private UUID teleportToAgentUUID;
    
    public SLWorldMap(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.teleportToAgentUUID = null;
    }
    
    public void CancelPendingTeleports() {
        this.teleportToAgentUUID = null;
        this.teleportTargetName = null;
    }
    
    @SLMessageHandler
    public void HandleFindAgent(final FindAgent findAgent) {
        if (this.teleportToAgentUUID != null && findAgent.AgentBlock_Field.Prey.equals(this.teleportToAgentUUID)) {
            Debug.Printf("FindAgent: hunter %s prey %s", findAgent.AgentBlock_Field.Hunter.toString(), findAgent.AgentBlock_Field.Prey.toString());
            for (final FindAgent.LocationBlock locationBlock : findAgent.LocationBlock_Fields) {
                Debug.Printf("FindAgent: GlobalX %f GlobalY %f", locationBlock.GlobalX, locationBlock.GlobalY);
            }
            if (findAgent.LocationBlock_Fields.size() > 0) {
                final double globalX = ((FindAgent.LocationBlock)findAgent.LocationBlock_Fields.get(0)).GlobalX;
                final double globalY = ((FindAgent.LocationBlock)findAgent.LocationBlock_Fields.get(0)).GlobalY;
                if (globalX != 0.0 || globalY != 0.0) {
                    final int n = (int)Math.floor(globalX);
                    final int n2 = (int)Math.floor(globalY);
                    final int i = n & 0xFF;
                    final int j = n2 & 0xFF;
                    final long l = (long)(n & 0xFFFFFF00) << 32 | ((long)(n2 & 0xFFFFFF00) & 0xFFFFFFFFL);
                    Debug.Printf("Initiating teleport to regionHandle 0x%x x %d y %d", l, i, j);
                    this.agentCircuit.TeleportToRegion(l, i, j, 0);
                }
            }
            this.teleportToAgentUUID = null;
        }
    }
    
    @SLMessageHandler
    public void HandleMapBlockReply(final MapBlockReply mapBlockReply) {
        boolean b = false;
        final Iterator<Object> iterator = mapBlockReply.Data_Fields.iterator();
        boolean b2 = false;
        while (iterator.hasNext()) {
            final MapBlockReply.Data data = iterator.next();
            final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(data.Name);
            final long n = (long)(data.X * 256) << 32 | ((long)(data.Y * 256) & 0xFFFFFFFFL);
            if (this.teleportTargetName != null && this.teleportTargetName.equalsIgnoreCase(stringFromVariableOEM)) {
                if (n != 0L) {
                    Debug.Log("HandleMapBlockReply: regionName = '" + stringFromVariableOEM + "', X = " + data.X + " Y = " + data.Y);
                    this.agentCircuit.TeleportToRegion(n, this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ);
                    this.teleportTargetName = null;
                    b = true;
                }
                else {
                    b2 = true;
                }
            }
        }
        if (this.teleportTargetName != null && b2 && (b ^ true)) {
            this.teleportTargetName = null;
            this.eventBus.publish(new SLTeleportResultEvent(false, "Destination region not found."));
        }
    }
    
    public boolean TeleportToAgent(final UUID uuid) {
        if (!this.agentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return false;
        }
        this.teleportToAgentUUID = uuid;
        final FindAgent findAgent = new FindAgent();
        findAgent.AgentBlock_Field.Hunter = this.circuitInfo.agentID;
        findAgent.AgentBlock_Field.Prey = uuid;
        try {
            findAgent.AgentBlock_Field.SpaceIP = (Inet4Address)InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 });
            findAgent.LocationBlock_Fields.add(new FindAgent.LocationBlock());
            findAgent.isReliable = true;
            this.SendMessage(findAgent);
            return true;
        }
        catch (final UnknownHostException ex) {
            return false;
        }
    }
    
    public boolean TeleportToRegionByName(final String teleportTargetName, final int teleportTargetX, final int teleportTargetY, final int teleportTargetZ) {
        if (!this.agentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return false;
        }
        this.teleportTargetName = teleportTargetName;
        this.teleportTargetX = teleportTargetX;
        this.teleportTargetY = teleportTargetY;
        this.teleportTargetZ = teleportTargetZ;
        final MapNameRequest mapNameRequest = new MapNameRequest();
        mapNameRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        mapNameRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        mapNameRequest.NameData_Field.Name = SLMessage.stringToVariableOEM(teleportTargetName);
        mapNameRequest.isReliable = true;
        this.SendMessage(mapNameRequest);
        return true;
    }
}
