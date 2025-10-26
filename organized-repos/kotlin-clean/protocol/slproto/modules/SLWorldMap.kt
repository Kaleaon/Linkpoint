package com.linkpoint.slproto.modules

import android.support.v4.view.InputDeviceCompat
import com.linkpoint.Debug
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.events.SLTeleportResultEvent
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.FindAgent
import com.linkpoint.slproto.messages.MapBlockReply
import com.linkpoint.slproto.messages.MapNameRequest
import java.net.Inet4Address
import java.net.UnknownHostException
import java.util.Iterator
import java.util.UUID

class SLWorldMap : SLModule() {
    private String teleportTargetName
    private Int teleportTargetX
    private Int teleportTargetY
    private Int teleportTargetZ
    private UUID teleportToAgentUUID = null

    public SLWorldMap(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
    }

    fun CancelPendingTeleports() {
        this.teleportToAgentUUID = null
        this.teleportTargetName = null
    }

    @SLMessageHandler
    fun HandleFindAgent(FindAgent findAgent) {
        if (this.teleportToAgentUUID != null && findAgent.AgentBlock_Field.Prey.equals(this.teleportToAgentUUID)) {
            Debug.Printf("FindAgent: hunter %s prey %s", findAgent.AgentBlock_Field.Hunter.toString(), findAgent.AgentBlock_Field.Prey.toString())
            for (FindAgent.LocationBlock locationBlock : findAgent.LocationBlock_Fields) {
                Debug.Printf("FindAgent: GlobalX %f GlobalY %f", Double.valueOf(locationBlock.GlobalX), Double.valueOf(locationBlock.GlobalY))
            }
            if (findAgent.LocationBlock_Fields.size() > 0) {
                Double d = findAgent.LocationBlock_Fields.get(0).GlobalX
                Double d2 = findAgent.LocationBlock_Fields.get(0).GlobalY
                if (!(d == 0.0d && d2 == 0.0d)) {
                    Int floor = (Int) Math.floor(d)
                    Int floor2 = (Int) Math.floor(d2)
                    Int i = floor & 255
                    Int i2 = floor2 & 255
                    Long j = (((Long) (floor & InputDeviceCompat.SOURCE_ANY)) << 32) | (((Long) (floor2 & InputDeviceCompat.SOURCE_ANY)) & 4294967295L)
                    Debug.Printf("Initiating teleport to regionHandle 0x%x x %d y %d", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2))
                    this.agentCircuit.TeleportToRegion(j, i, i2, 0)
                }
            }
            this.teleportToAgentUUID = null
        }
    }

    @SLMessageHandler
    fun HandleMapBlockReply(MapBlockReply mapBlockReply) {
        Boolean z2 = false
        Boolean z3 = false
        Iterator<T> it = mapBlockReply.Data_Fields.iterator()
        while (true) {
            z = z3
            if (!it.hasNext()) {
                break
            }
            MapBlockReply.Data data = (MapBlockReply.Data) it.next()
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(data.Name)
            Long j = (((Long) (data.X * 256)) << 32) | (((Long) (data.Y * 256)) & 4294967295L)
            if (this.teleportTargetName == null || !this.teleportTargetName.equalsIgnoreCase(stringFromVariableOEM)) {
                z3 = z
            } else if (j != 0) {
                Debug.Log("HandleMapBlockReply: regionName = '" + stringFromVariableOEM + "', X = " + data.X + " Y = " + data.Y)
                this.agentCircuit.TeleportToRegion(j, this.teleportTargetX, this.teleportTargetY, this.teleportTargetZ)
                this.teleportTargetName = null
                z3 = z
                z2 = true
            } else {
                z3 = true
            }
        }
        if (this.teleportTargetName != null && z && (!z2)) {
            this.teleportTargetName = null
            this.eventBus.publish(SLTeleportResultEvent(false, "Destination region not found."))
        }
    }

    public Boolean TeleportToAgent(UUID uuid) {
        if (!this.agentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return false
        }
        this.teleportToAgentUUID = uuid
        FindAgent findAgent = FindAgent()
        findAgent.AgentBlock_Field.Hunter = this.circuitInfo.agentID
        findAgent.AgentBlock_Field.Prey = uuid
        try {
            findAgent.AgentBlock_Field.SpaceIP = (Inet4Address) Inet4Address.getByAddress(ByteArray{0, 0, 0, 0})
            findAgent.LocationBlock_Fields.add(FindAgent.LocationBlock())
            findAgent.isReliable = true
            SendMessage(findAgent)
            return true
        } catch (UnknownHostException e) {
            return false
        }
    }

    public Boolean TeleportToRegionByName(String str, Int i, Int i2, Int i3) {
        if (!this.agentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return false
        }
        this.teleportTargetName = str
        this.teleportTargetX = i
        this.teleportTargetY = i2
        this.teleportTargetZ = i3
        MapNameRequest mapNameRequest = MapNameRequest()
        mapNameRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        mapNameRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        mapNameRequest.NameData_Field.Name = SLMessage.stringToVariableOEM(str)
        mapNameRequest.isReliable = true
        SendMessage(mapNameRequest)
        return true
    }
}
