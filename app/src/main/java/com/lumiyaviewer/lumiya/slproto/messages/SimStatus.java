package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class SimStatus extends SLMessage {
    public SimStatusData SimStatusData_Field = new SimStatusData();

    public static class SimStatusData {
        public boolean CanAcceptAgents;
        public boolean CanAcceptTasks;
    }

    public SimStatus() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimStatus(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put(Ascii.FF);
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptAgents);
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptTasks);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimStatusData_Field.CanAcceptAgents = unpackBoolean(byteBuffer);
        this.SimStatusData_Field.CanAcceptTasks = unpackBoolean(byteBuffer);
    }
}
