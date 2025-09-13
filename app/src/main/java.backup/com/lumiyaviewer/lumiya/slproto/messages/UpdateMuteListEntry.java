package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UpdateMuteListEntry extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public MuteData MuteData_Field = new MuteData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class MuteData {
        public int MuteFlags;
        public UUID MuteID;
        public byte[] MuteName;
        public int MuteType;
    }

    public UpdateMuteListEntry() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.MuteData_Field.MuteName.length + 17 + 4 + 4 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateMuteListEntry(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 7);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.MuteData_Field.MuteID);
        packVariable(byteBuffer, this.MuteData_Field.MuteName, 1);
        packInt(byteBuffer, this.MuteData_Field.MuteType);
        packInt(byteBuffer, this.MuteData_Field.MuteFlags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.MuteData_Field.MuteID = unpackUUID(byteBuffer);
        this.MuteData_Field.MuteName = unpackVariable(byteBuffer, 1);
        this.MuteData_Field.MuteType = unpackInt(byteBuffer);
        this.MuteData_Field.MuteFlags = unpackInt(byteBuffer);
    }
}
