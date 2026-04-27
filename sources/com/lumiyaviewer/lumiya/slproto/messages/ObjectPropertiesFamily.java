// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ObjectPropertiesFamily extends SLMessage
{
    public static class ObjectData
    {

        public int BaseMask;
        public int Category;
        public byte Description[];
        public int EveryoneMask;
        public UUID GroupID;
        public int GroupMask;
        public UUID LastOwnerID;
        public byte Name[];
        public int NextOwnerMask;
        public UUID ObjectID;
        public UUID OwnerID;
        public int OwnerMask;
        public int OwnershipCost;
        public int RequestFlags;
        public int SalePrice;
        public int SaleType;

        public ObjectData()
        {
        }
    }


    public ObjectData ObjectData_Field;

    public ObjectPropertiesFamily()
    {
        zeroCoded = true;
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Field.Name.length + 102 + 1 + ObjectData_Field.Description.length + 2;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectPropertiesFamily(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)10);
        packInt(bytebuffer, ObjectData_Field.RequestFlags);
        packUUID(bytebuffer, ObjectData_Field.ObjectID);
        packUUID(bytebuffer, ObjectData_Field.OwnerID);
        packUUID(bytebuffer, ObjectData_Field.GroupID);
        packInt(bytebuffer, ObjectData_Field.BaseMask);
        packInt(bytebuffer, ObjectData_Field.OwnerMask);
        packInt(bytebuffer, ObjectData_Field.GroupMask);
        packInt(bytebuffer, ObjectData_Field.EveryoneMask);
        packInt(bytebuffer, ObjectData_Field.NextOwnerMask);
        packInt(bytebuffer, ObjectData_Field.OwnershipCost);
        packByte(bytebuffer, (byte)ObjectData_Field.SaleType);
        packInt(bytebuffer, ObjectData_Field.SalePrice);
        packInt(bytebuffer, ObjectData_Field.Category);
        packUUID(bytebuffer, ObjectData_Field.LastOwnerID);
        packVariable(bytebuffer, ObjectData_Field.Name, 1);
        packVariable(bytebuffer, ObjectData_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ObjectData_Field.RequestFlags = unpackInt(bytebuffer);
        ObjectData_Field.ObjectID = unpackUUID(bytebuffer);
        ObjectData_Field.OwnerID = unpackUUID(bytebuffer);
        ObjectData_Field.GroupID = unpackUUID(bytebuffer);
        ObjectData_Field.BaseMask = unpackInt(bytebuffer);
        ObjectData_Field.OwnerMask = unpackInt(bytebuffer);
        ObjectData_Field.GroupMask = unpackInt(bytebuffer);
        ObjectData_Field.EveryoneMask = unpackInt(bytebuffer);
        ObjectData_Field.NextOwnerMask = unpackInt(bytebuffer);
        ObjectData_Field.OwnershipCost = unpackInt(bytebuffer);
        ObjectData_Field.SaleType = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.SalePrice = unpackInt(bytebuffer);
        ObjectData_Field.Category = unpackInt(bytebuffer);
        ObjectData_Field.LastOwnerID = unpackUUID(bytebuffer);
        ObjectData_Field.Name = unpackVariable(bytebuffer, 1);
        ObjectData_Field.Description = unpackVariable(bytebuffer, 1);
    }
}
