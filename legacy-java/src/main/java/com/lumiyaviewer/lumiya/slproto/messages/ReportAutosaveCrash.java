package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ReportAutosaveCrash extends SLMessage {
    public AutosaveData AutosaveData_Field;

    /* loaded from: classes.dex */
    public static class AutosaveData {
        public int PID;
        public int Status;
    }

    public ReportAutosaveCrash() {
        this.zeroCoded = false;
        this.AutosaveData_Field = new AutosaveData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 12;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleReportAutosaveCrash(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Byte.MIN_VALUE);
        packInt(byteBuffer, this.AutosaveData_Field.PID);
        packInt(byteBuffer, this.AutosaveData_Field.Status);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AutosaveData_Field.PID = unpackInt(byteBuffer);
        this.AutosaveData_Field.Status = unpackInt(byteBuffer);
    }
}
