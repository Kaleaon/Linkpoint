// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptMailRegistration extends SLMessage
{
    public static class DataBlock
    {

        public int Flags;
        public byte TargetIP[];
        public int TargetPort;
        public UUID TaskID;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public ScriptMailRegistration()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.TargetIP.length + 1 + 2 + 16 + 4 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptMailRegistration(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-94);
        packVariable(bytebuffer, DataBlock_Field.TargetIP, 1);
        packShort(bytebuffer, (short)DataBlock_Field.TargetPort);
        packUUID(bytebuffer, DataBlock_Field.TaskID);
        packInt(bytebuffer, DataBlock_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.TargetIP = unpackVariable(bytebuffer, 1);
        DataBlock_Field.TargetPort = unpackShort(bytebuffer) & 0xffff;
        DataBlock_Field.TaskID = unpackUUID(bytebuffer);
        DataBlock_Field.Flags = unpackInt(bytebuffer);
    }
}
