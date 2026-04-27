package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EventInfoReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public EventData EventData_Field = new EventData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class EventData {
        public int Amount;
        public byte[] Category;
        public int Cover;
        public byte[] Creator;
        public byte[] Date;
        public int DateUTC;
        public byte[] Desc;
        public int Duration;
        public int EventFlags;
        public int EventID;
        public LLVector3d GlobalPos;
        public byte[] Name;
        public byte[] SimName;
    }

    public EventInfoReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.EventData_Field.Creator.length + 5 + 1 + this.EventData_Field.Name.length + 1 + this.EventData_Field.Category.length + 2 + this.EventData_Field.Desc.length + 1 + this.EventData_Field.Date.length + 4 + 4 + 4 + 4 + 1 + this.EventData_Field.SimName.length + 24 + 4 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventInfoReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -76);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packInt(byteBuffer, this.EventData_Field.EventID);
        packVariable(byteBuffer, this.EventData_Field.Creator, 1);
        packVariable(byteBuffer, this.EventData_Field.Name, 1);
        packVariable(byteBuffer, this.EventData_Field.Category, 1);
        packVariable(byteBuffer, this.EventData_Field.Desc, 2);
        packVariable(byteBuffer, this.EventData_Field.Date, 1);
        packInt(byteBuffer, this.EventData_Field.DateUTC);
        packInt(byteBuffer, this.EventData_Field.Duration);
        packInt(byteBuffer, this.EventData_Field.Cover);
        packInt(byteBuffer, this.EventData_Field.Amount);
        packVariable(byteBuffer, this.EventData_Field.SimName, 1);
        packLLVector3d(byteBuffer, this.EventData_Field.GlobalPos);
        packInt(byteBuffer, this.EventData_Field.EventFlags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.EventData_Field.EventID = unpackInt(byteBuffer);
        this.EventData_Field.Creator = unpackVariable(byteBuffer, 1);
        this.EventData_Field.Name = unpackVariable(byteBuffer, 1);
        this.EventData_Field.Category = unpackVariable(byteBuffer, 1);
        this.EventData_Field.Desc = unpackVariable(byteBuffer, 2);
        this.EventData_Field.Date = unpackVariable(byteBuffer, 1);
        this.EventData_Field.DateUTC = unpackInt(byteBuffer);
        this.EventData_Field.Duration = unpackInt(byteBuffer);
        this.EventData_Field.Cover = unpackInt(byteBuffer);
        this.EventData_Field.Amount = unpackInt(byteBuffer);
        this.EventData_Field.SimName = unpackVariable(byteBuffer, 1);
        this.EventData_Field.GlobalPos = unpackLLVector3d(byteBuffer);
        this.EventData_Field.EventFlags = unpackInt(byteBuffer);
    }
}
