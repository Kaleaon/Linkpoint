package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class ScriptMailRegistration extends SLMessage {
    public DataBlock DataBlock_Field;

    /* loaded from: classes.dex */
    public static class DataBlock {
        public int Flags;
        public byte[] TargetIP;
        public int TargetPort;
        public UUID TaskID;
    }

    public ScriptMailRegistration() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.DataBlock_Field.TargetIP.length + 1 + 2 + 16 + 4 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptMailRegistration(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -94);
        packVariable(byteBuffer, this.DataBlock_Field.TargetIP, 1);
        packShort(byteBuffer, (short) this.DataBlock_Field.TargetPort);
        packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        packInt(byteBuffer, this.DataBlock_Field.Flags);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.TargetIP = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535;
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Flags = unpackInt(byteBuffer);
    }
}
