// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RpcScriptRequestInboundForward extends SLMessage
{
    public static class DataBlock
    {

        public UUID ChannelID;
        public int IntValue;
        public UUID ItemID;
        public Inet4Address RPCServerIP;
        public int RPCServerPort;
        public byte StringValue[];
        public UUID TaskID;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public RpcScriptRequestInboundForward()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.StringValue.length + 60 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRpcScriptRequestInboundForward(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-96);
        packIPAddress(bytebuffer, DataBlock_Field.RPCServerIP);
        packShort(bytebuffer, (short)DataBlock_Field.RPCServerPort);
        packUUID(bytebuffer, DataBlock_Field.TaskID);
        packUUID(bytebuffer, DataBlock_Field.ItemID);
        packUUID(bytebuffer, DataBlock_Field.ChannelID);
        packInt(bytebuffer, DataBlock_Field.IntValue);
        packVariable(bytebuffer, DataBlock_Field.StringValue, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.RPCServerIP = unpackIPAddress(bytebuffer);
        DataBlock_Field.RPCServerPort = unpackShort(bytebuffer) & 0xffff;
        DataBlock_Field.TaskID = unpackUUID(bytebuffer);
        DataBlock_Field.ItemID = unpackUUID(bytebuffer);
        DataBlock_Field.ChannelID = unpackUUID(bytebuffer);
        DataBlock_Field.IntValue = unpackInt(bytebuffer);
        DataBlock_Field.StringValue = unpackVariable(bytebuffer, 2);
    }
}
