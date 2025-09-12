// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TransferInventory extends SLMessage
{
    public static class InfoBlock
    {

        public UUID DestID;
        public UUID SourceID;
        public UUID TransactionID;

        public InfoBlock()
        {
        }
    }

    public static class InventoryBlock
    {

        public UUID InventoryID;
        public int Type;

        public InventoryBlock()
        {
        }
    }


    public InfoBlock InfoBlock_Field;
    public ArrayList InventoryBlock_Fields;

    public TransferInventory()
    {
        InventoryBlock_Fields = new ArrayList();
        zeroCoded = true;
        InfoBlock_Field = new InfoBlock();
    }

    public int CalcPayloadSize()
    {
        return InventoryBlock_Fields.size() * 17 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTransferInventory(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)39);
        packUUID(bytebuffer, InfoBlock_Field.SourceID);
        packUUID(bytebuffer, InfoBlock_Field.DestID);
        packUUID(bytebuffer, InfoBlock_Field.TransactionID);
        bytebuffer.put((byte)InventoryBlock_Fields.size());
        InventoryBlock inventoryblock;
        for (Iterator iterator = InventoryBlock_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)inventoryblock.Type))
        {
            inventoryblock = (InventoryBlock)iterator.next();
            packUUID(bytebuffer, inventoryblock.InventoryID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        InfoBlock_Field.SourceID = unpackUUID(bytebuffer);
        InfoBlock_Field.DestID = unpackUUID(bytebuffer);
        InfoBlock_Field.TransactionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            InventoryBlock inventoryblock = new InventoryBlock();
            inventoryblock.InventoryID = unpackUUID(bytebuffer);
            inventoryblock.Type = unpackByte(bytebuffer);
            InventoryBlock_Fields.add(inventoryblock);
        }

    }
}
