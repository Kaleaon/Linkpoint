package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;

public class ScriptTeleportRequest extends SLMessage {
    public Data Data_Field = new Data();

    public static class Data {
        public LLVector3 LookAt;
        public byte[] ObjectName;
        public byte[] SimName;
        public LLVector3 SimPosition;
    }

    public ScriptTeleportRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 1 + this.Data_Field.SimName.length + 12 + 12 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptTeleportRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -61);
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        packVariable(byteBuffer, this.Data_Field.SimName, 1);
        packLLVector3(byteBuffer, this.Data_Field.SimPosition);
        packLLVector3(byteBuffer, this.Data_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.Data_Field.SimName = unpackVariable(byteBuffer, 1);
        this.Data_Field.SimPosition = unpackLLVector3(byteBuffer);
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer);
    }
}
