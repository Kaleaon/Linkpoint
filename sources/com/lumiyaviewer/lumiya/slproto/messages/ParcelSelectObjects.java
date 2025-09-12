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

public class ParcelSelectObjects extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ParcelData
    {

        public int LocalID;
        public int ReturnType;

        public ParcelData()
        {
        }
    }

    public static class ReturnIDs
    {

        public UUID ReturnID;

        public ReturnIDs()
        {
        }
    }


    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    public ArrayList ReturnIDs_Fields;

    public ParcelSelectObjects()
    {
        ReturnIDs_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return ReturnIDs_Fields.size() * 16 + 45;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelSelectObjects(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-54);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, ParcelData_Field.LocalID);
        packInt(bytebuffer, ParcelData_Field.ReturnType);
        bytebuffer.put((byte)ReturnIDs_Fields.size());
        for (Iterator iterator = ReturnIDs_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((ReturnIDs)iterator.next()).ReturnID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ParcelData_Field.LocalID = unpackInt(bytebuffer);
        ParcelData_Field.ReturnType = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ReturnIDs returnids = new ReturnIDs();
            returnids.ReturnID = unpackUUID(bytebuffer);
            ReturnIDs_Fields.add(returnids);
        }

    }
}
