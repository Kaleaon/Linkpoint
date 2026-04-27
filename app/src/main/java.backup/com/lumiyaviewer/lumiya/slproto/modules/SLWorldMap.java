package com.lumiyaviewer.lumiya.slproto.modules;

import android.support.v4.view.InputDeviceCompat;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.FindAgent;
import com.lumiyaviewer.lumiya.slproto.messages.MapBlockReply;
import com.lumiyaviewer.lumiya.slproto.messages.MapNameRequest;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.UUID;

public class SLWorldMap extends SLModule {
    private String teleportTargetName;
    private int teleportTargetX;
    private int teleportTargetY;
    private int teleportTargetZ;
    private UUID teleportToAgentUUID = null;

    public SLWorldMap(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
    }

    public void CancelPendingTeleports() {
        this.teleportToAgentUUID = null;
        this.teleportTargetName = null;
    }

    @SLMessageHandler
    public void HandleFindAgent(FindAgent findAgent) {
        if (this.teleportToAgentUUID != null && findAgent.AgentBlock_Field.Prey.equals(this.teleportToAgentUUID)) {
            Debug.Printf("FindAgent: hunter %s prey %s", findAgent.AgentBlock_Field.Hunter.toString(), findAgent.AgentBlock_Field.Prey.toString());
            for (FindAgent.LocationBlock locationBlock : findAgent.LocationBlock_Fields) {
                Debug.Printf("FindAgent: GlobalX %f GlobalY %f", Double.valueOf(locationBlock.GlobalX), Double.valueOf(locationBlock.GlobalY));
            }
            if (findAgent.LocationBlock_Fields.size() > 0) {
                double d = findAgent.LocationBlock_Fields.get(0).GlobalX;
                double d2 = findAgent.LocationBlock_Fields.get(0).GlobalY;
                if (!(d == 0.0d && d2 == 0.0d)) {
                    int floor = (int) Math.floor(d);
                    int floor2 = (int) Math.floor(d2);
                    int i = floor & 255;
                    int i2 = floor2 & 255;
                    long j = (((long) (floor & InputDeviceCompat.SOURCE_ANY)) << 32) | (((long) (floor2 & InputDeviceCompat.SOURCE_ANY)) & 4294967295L);
                    Debug.Printf("Initiating teleport to regionHandle 0x%x x %d y %d", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2));
                    this.agentCircuit.TeleportToRegion(j, i, i2, 0);
                }
            }
            this.teleportToAgentUUID = null;
        }
    }

    @SLMessageHandler
    public void HandleMapBlockReply(MapBlockReply mapBlockReply) {
        boolean z;
        boolean z2 = false;
        boolean z3 = false;
        Iterator<T> it = mapBlockReply.Data_Fields.iterator();
        while (true) {
            z = z3;
            if (!it.hasNext()) {
                break;
            }
            MapBlockReply.Data data = (MapBlockReply.Data) it.next();
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(data.Name);
            long j = (((long) (data.X * 256)) << 32) | (((long) (data.Y * 256)) & 4294967295L);
            if (this.teleportTargetName == null || !this.teleportTargetName.equalsIgnoreCase(stringFromVariableOEM)) {
                z3 = z;
            } else if (j != 0) {
                Debug.Log("HandleMapBlockReply: regionName = '" + stringFromVariableOEM + "', X = " + data.X + " Y = " + data.Y);
                this.agentCircuit.TeleportToRegion(j, this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ);
                this.teleportTargetName = null;
                z3 = z;
                z2 = true;
            } else {
                z3 = true;
            }
        }
        if (this.teleportTargetName != null && z && (!z2)) {
            this.teleportTargetName = null;
            this.eventBus.publish(new SLTeleportResultEvent(false, "Destination region not found."));
        }
    }

    public boolean TeleportToAgent(UUID uuid) {
        if (!this.agentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return false;
        }
        this.teleportToAgentUUID = uuid;
        FindAgent findAgent = new FindAgent();
        findAgent.AgentBlock_Field.Hunter = this.circuitInfo.agentID;
        findAgent.AgentBlock_Field.Prey = uuid;
        try {
            findAgent.AgentBlock_Field.SpaceIP = (Inet4Address) Inet4Address.getByAddress(new byte[]{0, 0, 0, 0});
            findAgent.LocationBlock_Fields.add(new FindAgent.LocationBlock());
            findAgent.isReliable = true;
            SendMessage(findAgent);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public boolean TeleportToRegionByName(String str, int i, int i2, int i3) {
        if (!this.agentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return false;
        }
        this.teleportTargetName = str;
        this.teleportTargetX = i;
        this.teleportTargetY = i2;
        this.teleportTargetZ = i3;
        MapNameRequest mapNameRequest = new MapNameRequest();
        mapNameRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        mapNameRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        mapNameRequest.NameData_Field.Name = SLMessage.stringToVariableOEM(str);
        mapNameRequest.isReliable = true;
        SendMessage(mapNameRequest);
        return true;
    }
}
