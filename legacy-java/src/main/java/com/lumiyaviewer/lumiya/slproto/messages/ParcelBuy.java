package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class ParcelBuy extends SLMessage {
    public AgentData AgentData_Field;
    public Data Data_Field;
    public ParcelData ParcelData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class Data {
        public boolean Final;
        public UUID GroupID;
        public boolean IsGroupOwned;
        public int LocalID;
        public boolean RemoveContribution;
    }

    /* loaded from: classes.dex */
    public static class ParcelData {
        public int Area;
        public int Price;
    }

    public ParcelBuy() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
        this.ParcelData_Field = new ParcelData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 67;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelBuy(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -43);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.GroupID);
        packBoolean(byteBuffer, this.Data_Field.IsGroupOwned);
        packBoolean(byteBuffer, this.Data_Field.RemoveContribution);
        packInt(byteBuffer, this.Data_Field.LocalID);
        packBoolean(byteBuffer, this.Data_Field.Final);
        packInt(byteBuffer, this.ParcelData_Field.Price);
        packInt(byteBuffer, this.ParcelData_Field.Area);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.GroupID = unpackUUID(byteBuffer);
        this.Data_Field.IsGroupOwned = unpackBoolean(byteBuffer);
        this.Data_Field.RemoveContribution = unpackBoolean(byteBuffer);
        this.Data_Field.LocalID = unpackInt(byteBuffer);
        this.Data_Field.Final = unpackBoolean(byteBuffer);
        this.ParcelData_Field.Price = unpackInt(byteBuffer);
        this.ParcelData_Field.Area = unpackInt(byteBuffer);
    }
}
