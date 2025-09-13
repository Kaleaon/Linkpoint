package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScriptMailRegistration extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public int Flags;
        public byte[] TargetIP;
        public int TargetPort;
        public UUID TaskID;
    }

    public ScriptMailRegistration() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataBlock_Field.TargetIP.length + 1 + 2 + 16 + 4 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptMailRegistration(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -94);
        packVariable(byteBuffer, this.DataBlock_Field.TargetIP, 1);
        packShort(byteBuffer, (short) this.DataBlock_Field.TargetPort);
        packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        packInt(byteBuffer, this.DataBlock_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.TargetIP = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535;
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Flags = unpackInt(byteBuffer);
    }
}
