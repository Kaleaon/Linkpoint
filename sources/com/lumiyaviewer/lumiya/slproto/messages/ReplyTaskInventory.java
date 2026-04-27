// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ReplyTaskInventory extends SLMessage
{
    public static class InventoryData
    {

        public byte Filename[];
        public int Serial;
        public UUID TaskID;

        public InventoryData()
        {
        }
    }


    public InventoryData InventoryData_Field;

    public ReplyTaskInventory()
    {
        zeroCoded = true;
        InventoryData_Field = new InventoryData();
    }

    public int CalcPayloadSize()
    {
        return InventoryData_Field.Filename.length + 19 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleReplyTaskInventory(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)34);
        packUUID(bytebuffer, InventoryData_Field.TaskID);
        packShort(bytebuffer, (short)InventoryData_Field.Serial);
        packVariable(bytebuffer, InventoryData_Field.Filename, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        InventoryData_Field.TaskID = unpackUUID(bytebuffer);
        InventoryData_Field.Serial = unpackShort(bytebuffer);
        InventoryData_Field.Filename = unpackVariable(bytebuffer, 1);
    }
}
