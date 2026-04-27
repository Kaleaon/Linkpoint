// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetGroupAcceptNotices extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public boolean AcceptNotices;
        public UUID GroupID;

        public Data()
        {
        }
    }

    public static class NewData
    {

        public boolean ListInProfile;

        public NewData()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;
    public NewData NewData_Field;

    public SetGroupAcceptNotices()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
        NewData_Field = new NewData();
    }

    public int CalcPayloadSize()
    {
        return 54;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetGroupAcceptNotices(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)114);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, Data_Field.GroupID);
        packBoolean(bytebuffer, Data_Field.AcceptNotices);
        packBoolean(bytebuffer, NewData_Field.ListInProfile);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Data_Field.GroupID = unpackUUID(bytebuffer);
        Data_Field.AcceptNotices = unpackBoolean(bytebuffer);
        NewData_Field.ListInProfile = unpackBoolean(bytebuffer);
    }
}
