package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetSimStatusInDatabase extends SLMessage {
    public Data Data_Field = new Data();

    public static class Data {
        public int AgentCount;
        public byte[] HostName;
        public int PID;
        public UUID RegionID;
        public byte[] Status;
        public int TimeToLive;
        public int X;
        public int Y;
    }

    public SetSimStatusInDatabase() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.Data_Field.Status.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetSimStatusInDatabase(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.SYN);
        packUUID(byteBuffer, this.Data_Field.RegionID);
        packVariable(byteBuffer, this.Data_Field.HostName, 1);
        packInt(byteBuffer, this.Data_Field.X);
        packInt(byteBuffer, this.Data_Field.Y);
        packInt(byteBuffer, this.Data_Field.PID);
        packInt(byteBuffer, this.Data_Field.AgentCount);
        packInt(byteBuffer, this.Data_Field.TimeToLive);
        packVariable(byteBuffer, this.Data_Field.Status, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.RegionID = unpackUUID(byteBuffer);
        this.Data_Field.HostName = unpackVariable(byteBuffer, 1);
        this.Data_Field.X = unpackInt(byteBuffer);
        this.Data_Field.Y = unpackInt(byteBuffer);
        this.Data_Field.PID = unpackInt(byteBuffer);
        this.Data_Field.AgentCount = unpackInt(byteBuffer);
        this.Data_Field.TimeToLive = unpackInt(byteBuffer);
        this.Data_Field.Status = unpackVariable(byteBuffer, 1);
    }
}
