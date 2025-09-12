// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class UseCircuitCode extends SLMessage
{
    public static class CircuitCode
    {

        public int Code;
        public UUID ID;
        public UUID SessionID;

        public CircuitCode()
        {
        }
    }


    public CircuitCode CircuitCode_Field;

    public UseCircuitCode()
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
        slmessagehandler.HandleUseCircuitCode(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)3);
        packInt(bytebuffer, CircuitCode_Field.Code);
        packUUID(bytebuffer, CircuitCode_Field.SessionID);
        packUUID(bytebuffer, CircuitCode_Field.ID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        CircuitCode_Field.Code = unpackInt(bytebuffer);
        CircuitCode_Field.SessionID = unpackUUID(bytebuffer);
        CircuitCode_Field.ID = unpackUUID(bytebuffer);
    }
}
