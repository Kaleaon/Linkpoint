// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptDataRequest extends SLMessage
{
    public static class DataBlock
    {

        public long Hash;
        public byte Request[];
        public int RequestType;

        public DataBlock()
        {
        }
    }


    public ArrayList DataBlock_Fields;

    public ScriptDataRequest()
    {
        DataBlock_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = DataBlock_Fields.iterator();
        int i;
        for (i = 5; iterator.hasNext(); i = ((DataBlock)iterator.next()).Request.length + 11 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptDataRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)81);
        bytebuffer.put((byte)DataBlock_Fields.size());
        DataBlock datablock;
        for (Iterator iterator = DataBlock_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, datablock.Request, 2))
        {
            datablock = (DataBlock)iterator.next();
            packLong(bytebuffer, datablock.Hash);
            packByte(bytebuffer, (byte)datablock.RequestType);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            DataBlock datablock = new DataBlock();
            datablock.Hash = unpackLong(bytebuffer);
            datablock.RequestType = unpackByte(bytebuffer);
            datablock.Request = unpackVariable(bytebuffer, 2);
            DataBlock_Fields.add(datablock);
        }

    }
}
