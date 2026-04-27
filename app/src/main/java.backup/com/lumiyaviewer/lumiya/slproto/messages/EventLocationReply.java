package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EventLocationReply extends SLMessage {
    public EventData EventData_Field = new EventData();
    public QueryData QueryData_Field = new QueryData();

    public static class EventData {
        public UUID RegionID;
        public LLVector3 RegionPos;
        public boolean Success;
    }

    public static class QueryData {
        public UUID QueryID;
    }

    public EventLocationReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 49;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventLocationReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 52);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packBoolean(byteBuffer, this.EventData_Field.Success);
        packUUID(byteBuffer, this.EventData_Field.RegionID);
        packLLVector3(byteBuffer, this.EventData_Field.RegionPos);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.EventData_Field.Success = unpackBoolean(byteBuffer);
        this.EventData_Field.RegionID = unpackUUID(byteBuffer);
        this.EventData_Field.RegionPos = unpackLLVector3(byteBuffer);
    }
}
