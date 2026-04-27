// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.messages.FindAgent;
import com.lumiyaviewer.lumiya.slproto.messages.MapBlockReply;
import com.lumiyaviewer.lumiya.slproto.messages.MapNameRequest;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLModules

public class SLWorldMap extends SLModule
{

    private String teleportTargetName;
    private int teleportTargetX;
    private int teleportTargetY;
    private int teleportTargetZ;
    private UUID teleportToAgentUUID;

    public SLWorldMap(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        teleportToAgentUUID = null;
    }

    public void CancelPendingTeleports()
    {
        teleportToAgentUUID = null;
        teleportTargetName = null;
    }

    public void HandleFindAgent(FindAgent findagent)
    {
        if (teleportToAgentUUID != null && findagent.AgentBlock_Field.Prey.equals(teleportToAgentUUID))
        {
            Debug.Printf("FindAgent: hunter %s prey %s", new Object[] {
                findagent.AgentBlock_Field.Hunter.toString(), findagent.AgentBlock_Field.Prey.toString()
            });
            com.lumiyaviewer.lumiya.slproto.messages.FindAgent.LocationBlock locationblock;
            for (Iterator iterator = findagent.LocationBlock_Fields.iterator(); iterator.hasNext(); Debug.Printf("FindAgent: GlobalX %f GlobalY %f", new Object[] {
    Double.valueOf(locationblock.GlobalX), Double.valueOf(locationblock.GlobalY)
}))
            {
                locationblock = (com.lumiyaviewer.lumiya.slproto.messages.FindAgent.LocationBlock)iterator.next();
            }

            if (findagent.LocationBlock_Fields.size() > 0)
            {
                double d = ((com.lumiyaviewer.lumiya.slproto.messages.FindAgent.LocationBlock)findagent.LocationBlock_Fields.get(0)).GlobalX;
                double d1 = ((com.lumiyaviewer.lumiya.slproto.messages.FindAgent.LocationBlock)findagent.LocationBlock_Fields.get(0)).GlobalY;
                if (d != 0.0D || d1 != 0.0D)
                {
                    int i = (int)Math.floor(d);
                    int j = (int)Math.floor(d1);
                    int k = i & 0xff;
                    int l = j & 0xff;
                    long l1 = (long)(i & 0xffffff00) << 32 | (long)(j & 0xffffff00) & 0xffffffffL;
                    Debug.Printf("Initiating teleport to regionHandle 0x%x x %d y %d", new Object[] {
                        Long.valueOf(l1), Integer.valueOf(k), Integer.valueOf(l)
                    });
                    agentCircuit.TeleportToRegion(l1, k, l, 0);
                }
            }
            teleportToAgentUUID = null;
        }
    }

    public void HandleMapBlockReply(MapBlockReply mapblockreply)
    {
        boolean flag1 = false;
        mapblockreply = mapblockreply.Data_Fields.iterator();
        boolean flag = false;
        do
        {
            if (!mapblockreply.hasNext())
            {
                break;
            }
            com.lumiyaviewer.lumiya.slproto.messages.MapBlockReply.Data data = (com.lumiyaviewer.lumiya.slproto.messages.MapBlockReply.Data)mapblockreply.next();
            String s = SLMessage.stringFromVariableOEM(data.Name);
            long l = (long)(data.X * 256) << 32 | (long)(data.Y * 256) & 0xffffffffL;
            if (teleportTargetName != null && teleportTargetName.equalsIgnoreCase(s))
            {
                if (l != 0L)
                {
                    Debug.Log((new StringBuilder()).append("HandleMapBlockReply: regionName = '").append(s).append("', X = ").append(data.X).append(" Y = ").append(data.Y).toString());
                    agentCircuit.TeleportToRegion(l, teleportTargetX, teleportTargetY, teleportTargetZ);
                    teleportTargetName = null;
                    flag1 = true;
                } else
                {
                    flag = true;
                }
            }
        } while (true);
        if (teleportTargetName != null && flag && flag1 ^ true)
        {
            teleportTargetName = null;
            eventBus.publish(new SLTeleportResultEvent(false, "Destination region not found."));
        }
    }

    public boolean TeleportToAgent(UUID uuid)
    {
        if (!agentCircuit.getModules().rlvController.canTeleportToLocation())
        {
            return false;
        }
        teleportToAgentUUID = uuid;
        FindAgent findagent = new FindAgent();
        findagent.AgentBlock_Field.Hunter = circuitInfo.agentID;
        findagent.AgentBlock_Field.Prey = uuid;
        try
        {
            findagent.AgentBlock_Field.SpaceIP = (Inet4Address)Inet4Address.getByAddress(new byte[] {
                0, 0, 0, 0
            });
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            return false;
        }
        uuid = new com.lumiyaviewer.lumiya.slproto.messages.FindAgent.LocationBlock();
        findagent.LocationBlock_Fields.add(uuid);
        findagent.isReliable = true;
        SendMessage(findagent);
        return true;
    }

    public boolean TeleportToRegionByName(String s, int i, int j, int k)
    {
        if (!agentCircuit.getModules().rlvController.canTeleportToLocation())
        {
            return false;
        } else
        {
            teleportTargetName = s;
            teleportTargetX = i;
            teleportTargetY = j;
            teleportTargetZ = k;
            MapNameRequest mapnamerequest = new MapNameRequest();
            mapnamerequest.AgentData_Field.AgentID = circuitInfo.agentID;
            mapnamerequest.AgentData_Field.SessionID = circuitInfo.sessionID;
            mapnamerequest.NameData_Field.Name = SLMessage.stringToVariableOEM(s);
            mapnamerequest.isReliable = true;
            SendMessage(mapnamerequest);
            return true;
        }
    }
}
