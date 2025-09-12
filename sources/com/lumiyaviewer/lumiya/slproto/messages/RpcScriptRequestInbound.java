// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RpcScriptRequestInbound extends SLMessage
{
    public static class DataBlock
    {

        public UUID ChannelID;
        public int IntValue;
        public UUID ItemID;
        public byte StringValue[];
        public UUID TaskID;

        public DataBlock()
        {
        }
    }

    public static class TargetBlock
    {

        public int GridX;
        public int GridY;

        public TargetBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;
    public TargetBlock TargetBlock_Field;

    public RpcScriptRequestInbound()
    {
        zeroCoded = false;
        TargetBlock_Field = new TargetBlock();
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.StringValue.length + 54 + 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRpcScriptRequestInbound(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-97);
        packInt(bytebuffer, TargetBlock_Field.GridX);
        packInt(bytebuffer, TargetBlock_Field.GridY);
        packUUID(bytebuffer, DataBlock_Field.TaskID);
        packUUID(bytebuffer, DataBlock_Field.ItemID);
        packUUID(bytebuffer, DataBlock_Field.ChannelID);
        packInt(bytebuffer, DataBlock_Field.IntValue);
        packVariable(bytebuffer, DataBlock_Field.StringValue, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TargetBlock_Field.GridX = unpackInt(bytebuffer);
        TargetBlock_Field.GridY = unpackInt(bytebuffer);
        DataBlock_Field.TaskID = unpackUUID(bytebuffer);
        DataBlock_Field.ItemID = unpackUUID(bytebuffer);
        DataBlock_Field.ChannelID = unpackUUID(bytebuffer);
        DataBlock_Field.IntValue = unpackInt(bytebuffer);
        DataBlock_Field.StringValue = unpackVariable(bytebuffer, 2);
    }
}
