package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EventLocationRequest extends SLMessage {
    public EventData EventData_Field = new EventData();
    public QueryData QueryData_Field = new QueryData();

    public static class EventData {
        public int EventID;
    }

    public static class QueryData {
        public UUID QueryID;
    }

    public EventLocationRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 24;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventLocationRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 51);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packInt(byteBuffer, this.EventData_Field.EventID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.EventData_Field.EventID = unpackInt(byteBuffer);
    }
}
