// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentAlertMessage extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class AlertData
    {

        public byte Message[];
        public boolean Modal;

        public AlertData()
        {
        }
    }


    public AgentData AgentData_Field;
    public AlertData AlertData_Field;

    public AgentAlertMessage()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        AlertData_Field = new AlertData();
    }

    public int CalcPayloadSize()
    {
        return AlertData_Field.Message.length + 2 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentAlertMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-121);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packBoolean(bytebuffer, AlertData_Field.Modal);
        packVariable(bytebuffer, AlertData_Field.Message, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AlertData_Field.Modal = unpackBoolean(bytebuffer);
        AlertData_Field.Message = unpackVariable(bytebuffer, 1);
    }
}
