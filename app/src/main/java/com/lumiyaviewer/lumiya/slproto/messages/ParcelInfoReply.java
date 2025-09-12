package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ParcelInfoReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class Data {
        public int ActualArea;
        public int AuctionID;
        public int BillableArea;
        public byte[] Desc;
        public float Dwell;
        public int Flags;
        public float GlobalX;
        public float GlobalY;
        public float GlobalZ;
        public byte[] Name;
        public UUID OwnerID;
        public UUID ParcelID;
        public int SalePrice;
        public byte[] SimName;
        public UUID SnapshotID;
    }

    public ParcelInfoReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 33 + 1 + this.Data_Field.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1 + this.Data_Field.SimName.length + 16 + 4 + 4 + 4 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelInfoReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 55);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.Data_Field.ParcelID);
        packUUID(byteBuffer, this.Data_Field.OwnerID);
        packVariable(byteBuffer, this.Data_Field.Name, 1);
        packVariable(byteBuffer, this.Data_Field.Desc, 1);
        packInt(byteBuffer, this.Data_Field.ActualArea);
        packInt(byteBuffer, this.Data_Field.BillableArea);
        packByte(byteBuffer, (byte) this.Data_Field.Flags);
        packFloat(byteBuffer, this.Data_Field.GlobalX);
        packFloat(byteBuffer, this.Data_Field.GlobalY);
        packFloat(byteBuffer, this.Data_Field.GlobalZ);
        packVariable(byteBuffer, this.Data_Field.SimName, 1);
        packUUID(byteBuffer, this.Data_Field.SnapshotID);
        packFloat(byteBuffer, this.Data_Field.Dwell);
        packInt(byteBuffer, this.Data_Field.SalePrice);
        packInt(byteBuffer, this.Data_Field.AuctionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.Data_Field.ParcelID = unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = unpackUUID(byteBuffer);
        this.Data_Field.Name = unpackVariable(byteBuffer, 1);
        this.Data_Field.Desc = unpackVariable(byteBuffer, 1);
        this.Data_Field.ActualArea = unpackInt(byteBuffer);
        this.Data_Field.BillableArea = unpackInt(byteBuffer);
        this.Data_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Data_Field.GlobalX = unpackFloat(byteBuffer);
        this.Data_Field.GlobalY = unpackFloat(byteBuffer);
        this.Data_Field.GlobalZ = unpackFloat(byteBuffer);
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1);
        this.Data_Field.SnapshotID = unpackUUID(byteBuffer);
        this.Data_Field.Dwell = unpackFloat(byteBuffer);
        this.Data_Field.SalePrice = unpackInt(byteBuffer);
        this.Data_Field.AuctionID = unpackInt(byteBuffer);
    }
}
