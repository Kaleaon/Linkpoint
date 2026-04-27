package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class ReportAutosaveCrash extends SLMessage {
    public AutosaveData AutosaveData_Field = new AutosaveData();

    public static class AutosaveData {
        public int PID;
        public int Status;
    }

    public ReportAutosaveCrash() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 12;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleReportAutosaveCrash(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Byte.MIN_VALUE);
        packInt(byteBuffer, this.AutosaveData_Field.PID);
        packInt(byteBuffer, this.AutosaveData_Field.Status);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AutosaveData_Field.PID = unpackInt(byteBuffer);
        this.AutosaveData_Field.Status = unpackInt(byteBuffer);
    }
}
