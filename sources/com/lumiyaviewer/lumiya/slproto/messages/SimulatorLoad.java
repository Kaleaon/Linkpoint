// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimulatorLoad extends SLMessage
{
    public static class AgentList
    {

        public int CircuitCode;
        public int X;
        public int Y;

        public AgentList()
        {
        }
    }

    public static class SimulatorLoadData
    {

        public int AgentCount;
        public boolean CanAcceptAgents;
        public float TimeDilation;

        public SimulatorLoadData()
        {
        }
    }


    public ArrayList AgentList_Fields;
    public SimulatorLoadData SimulatorLoadData_Field;

    public SimulatorLoad()
    {
        AgentList_Fields = new ArrayList();
        zeroCoded = false;
        SimulatorLoadData_Field = new SimulatorLoadData();
    }

    public int CalcPayloadSize()
    {
        return AgentList_Fields.size() * 6 + 14;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimulatorLoad(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)12);
        packFloat(bytebuffer, SimulatorLoadData_Field.TimeDilation);
        packInt(bytebuffer, SimulatorLoadData_Field.AgentCount);
        packBoolean(bytebuffer, SimulatorLoadData_Field.CanAcceptAgents);
        bytebuffer.put((byte)AgentList_Fields.size());
        AgentList agentlist;
        for (Iterator iterator = AgentList_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)agentlist.Y))
        {
            agentlist = (AgentList)iterator.next();
            packInt(bytebuffer, agentlist.CircuitCode);
            packByte(bytebuffer, (byte)agentlist.X);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SimulatorLoadData_Field.TimeDilation = unpackFloat(bytebuffer);
        SimulatorLoadData_Field.AgentCount = unpackInt(bytebuffer);
        SimulatorLoadData_Field.CanAcceptAgents = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            AgentList agentlist = new AgentList();
            agentlist.CircuitCode = unpackInt(bytebuffer);
            agentlist.X = unpackByte(bytebuffer) & 0xff;
            agentlist.Y = unpackByte(bytebuffer) & 0xff;
            AgentList_Fields.add(agentlist);
        }

    }
}
