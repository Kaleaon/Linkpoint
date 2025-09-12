package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetScriptRunning extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Script Script_Field = new Script();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Script {
        public UUID ItemID;
        public UUID ObjectID;
        public boolean Running;
    }

    public SetScriptRunning() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 69;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetScriptRunning(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -11);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Script_Field.ObjectID);
        packUUID(byteBuffer, this.Script_Field.ItemID);
        packBoolean(byteBuffer, this.Script_Field.Running);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Script_Field.ObjectID = unpackUUID(byteBuffer);
        this.Script_Field.ItemID = unpackUUID(byteBuffer);
        this.Script_Field.Running = unpackBoolean(byteBuffer);
    }
}
