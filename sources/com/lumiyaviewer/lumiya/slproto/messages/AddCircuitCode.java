// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AddCircuitCode extends SLMessage
{
    public static class CircuitCode
    {

        public UUID AgentID;
        public int Code;
        public UUID SessionID;

        public CircuitCode()
        {
        }
    }


    public CircuitCode CircuitCode_Field;

    public AddCircuitCode()
    {
        zeroCoded = false;
        CircuitCode_Field = new CircuitCode();
    }

    public int CalcPayloadSize()
    {
        return 40;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAddCircuitCode(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)2);
        packInt(bytebuffer, CircuitCode_Field.Code);
        packUUID(bytebuffer, CircuitCode_Field.SessionID);
        packUUID(bytebuffer, CircuitCode_Field.AgentID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        CircuitCode_Field.Code = unpackInt(bytebuffer);
        CircuitCode_Field.SessionID = unpackUUID(bytebuffer);
        CircuitCode_Field.AgentID = unpackUUID(bytebuffer);
    }
}
