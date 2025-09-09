package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class EdgeDataPacket extends SLMessage {
    public EdgeData EdgeData_Field = new EdgeData();

    public static class EdgeData {
        public int Direction;
        public byte[] LayerData;
        public int LayerType;
    }

    public EdgeDataPacket() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.EdgeData_Field.LayerData.length + 4 + 1;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEdgeDataPacket(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.CAN);
        packByte(byteBuffer, (byte) this.EdgeData_Field.LayerType);
        packByte(byteBuffer, (byte) this.EdgeData_Field.Direction);
        packVariable(byteBuffer, this.EdgeData_Field.LayerData, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.EdgeData_Field.LayerType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.EdgeData_Field.Direction = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.EdgeData_Field.LayerData = unpackVariable(byteBuffer, 2);
    }
}
