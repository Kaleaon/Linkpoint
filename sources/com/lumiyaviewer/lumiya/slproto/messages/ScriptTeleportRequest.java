// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptTeleportRequest extends SLMessage
{
    public static class Data
    {

        public LLVector3 LookAt;
        public byte ObjectName[];
        public byte SimName[];
        public LLVector3 SimPosition;

        public Data()
        {
        }
    }


    public Data Data_Field;

    public ScriptTeleportRequest()
    {
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.ObjectName.length + 1 + 1 + Data_Field.SimName.length + 12 + 12 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptTeleportRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-61);
        packVariable(bytebuffer, Data_Field.ObjectName, 1);
        packVariable(bytebuffer, Data_Field.SimName, 1);
        packLLVector3(bytebuffer, Data_Field.SimPosition);
        packLLVector3(bytebuffer, Data_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.ObjectName = unpackVariable(bytebuffer, 1);
        Data_Field.SimName = unpackVariable(bytebuffer, 1);
        Data_Field.SimPosition = unpackLLVector3(bytebuffer);
        Data_Field.LookAt = unpackLLVector3(bytebuffer);
    }
}
