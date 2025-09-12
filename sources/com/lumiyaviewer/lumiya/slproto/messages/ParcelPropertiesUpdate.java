// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelPropertiesUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ParcelData
    {

        public UUID AuthBuyerID;
        public int Category;
        public byte Desc[];
        public int Flags;
        public UUID GroupID;
        public int LandingType;
        public int LocalID;
        public int MediaAutoScale;
        public UUID MediaID;
        public byte MediaURL[];
        public byte MusicURL[];
        public byte Name[];
        public int ParcelFlags;
        public float PassHours;
        public int PassPrice;
        public int SalePrice;
        public UUID SnapshotID;
        public LLVector3 UserLocation;
        public LLVector3 UserLookAt;

        public ParcelData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;

    public ParcelPropertiesUpdate()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Field.Name.length + 17 + 1 + ParcelData_Field.Desc.length + 1 + ParcelData_Field.MusicURL.length + 1 + ParcelData_Field.MediaURL.length + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelPropertiesUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-58);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, ParcelData_Field.LocalID);
        packInt(bytebuffer, ParcelData_Field.Flags);
        packInt(bytebuffer, ParcelData_Field.ParcelFlags);
        packInt(bytebuffer, ParcelData_Field.SalePrice);
        packVariable(bytebuffer, ParcelData_Field.Name, 1);
        packVariable(bytebuffer, ParcelData_Field.Desc, 1);
        packVariable(bytebuffer, ParcelData_Field.MusicURL, 1);
        packVariable(bytebuffer, ParcelData_Field.MediaURL, 1);
        packUUID(bytebuffer, ParcelData_Field.MediaID);
        packByte(bytebuffer, (byte)ParcelData_Field.MediaAutoScale);
        packUUID(bytebuffer, ParcelData_Field.GroupID);
        packInt(bytebuffer, ParcelData_Field.PassPrice);
        packFloat(bytebuffer, ParcelData_Field.PassHours);
        packByte(bytebuffer, (byte)ParcelData_Field.Category);
        packUUID(bytebuffer, ParcelData_Field.AuthBuyerID);
        packUUID(bytebuffer, ParcelData_Field.SnapshotID);
        packLLVector3(bytebuffer, ParcelData_Field.UserLocation);
        packLLVector3(bytebuffer, ParcelData_Field.UserLookAt);
        packByte(bytebuffer, (byte)ParcelData_Field.LandingType);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ParcelData_Field.LocalID = unpackInt(bytebuffer);
        ParcelData_Field.Flags = unpackInt(bytebuffer);
        ParcelData_Field.ParcelFlags = unpackInt(bytebuffer);
        ParcelData_Field.SalePrice = unpackInt(bytebuffer);
        ParcelData_Field.Name = unpackVariable(bytebuffer, 1);
        ParcelData_Field.Desc = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MusicURL = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MediaURL = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MediaID = unpackUUID(bytebuffer);
        ParcelData_Field.MediaAutoScale = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.GroupID = unpackUUID(bytebuffer);
        ParcelData_Field.PassPrice = unpackInt(bytebuffer);
        ParcelData_Field.PassHours = unpackFloat(bytebuffer);
        ParcelData_Field.Category = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.AuthBuyerID = unpackUUID(bytebuffer);
        ParcelData_Field.SnapshotID = unpackUUID(bytebuffer);
        ParcelData_Field.UserLocation = unpackLLVector3(bytebuffer);
        ParcelData_Field.UserLookAt = unpackLLVector3(bytebuffer);
        ParcelData_Field.LandingType = unpackByte(bytebuffer) & 0xff;
    }
}
