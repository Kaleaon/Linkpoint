package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScriptRunningReply extends SLMessage {
    public Script Script_Field = new Script();

    public static class Script {
        public UUID ItemID;
        public UUID ObjectID;
        public boolean Running;
    }

    public ScriptRunningReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptRunningReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -12);
        packUUID(byteBuffer, this.Script_Field.ObjectID);
        packUUID(byteBuffer, this.Script_Field.ItemID);
        packBoolean(byteBuffer, this.Script_Field.Running);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Script_Field.ObjectID = unpackUUID(byteBuffer);
        this.Script_Field.ItemID = unpackUUID(byteBuffer);
        this.Script_Field.Running = unpackBoolean(byteBuffer);
    }
}
