// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GodlikeMessage extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;
        public UUID TransactionID;

        public AgentData()
        {
        }
    }

    public static class MethodData
    {

        public UUID Invoice;
        public byte Method[];

        public MethodData()
        {
        }
    }

    public static class ParamList
    {

        public byte Parameter[];

        public ParamList()
        {
        }
    }


    public AgentData AgentData_Field;
    public MethodData MethodData_Field;
    public ArrayList ParamList_Fields;

    public GodlikeMessage()
    {
        ParamList_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        MethodData_Field = new MethodData();
    }

    public int CalcPayloadSize()
    {
        int i = MethodData_Field.Method.length;
        Iterator iterator = ParamList_Fields.iterator();
        for (i = i + 1 + 16 + 52 + 1; iterator.hasNext(); i = ((ParamList)iterator.next()).Parameter.length + 1 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGodlikeMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)3);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.TransactionID);
        packVariable(bytebuffer, MethodData_Field.Method, 1);
        packUUID(bytebuffer, MethodData_Field.Invoice);
        bytebuffer.put((byte)ParamList_Fields.size());
        for (Iterator iterator = ParamList_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, ((ParamList)iterator.next()).Parameter, 1)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.TransactionID = unpackUUID(bytebuffer);
        MethodData_Field.Method = unpackVariable(bytebuffer, 1);
        MethodData_Field.Invoice = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ParamList paramlist = new ParamList();
            paramlist.Parameter = unpackVariable(bytebuffer, 1);
            ParamList_Fields.add(paramlist);
        }

    }
}
