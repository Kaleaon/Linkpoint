package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class SimStatus extends SLMessage {
    public SimStatusData SimStatusData_Field;

    /* loaded from: classes.dex */
    public static class SimStatusData {
        public boolean CanAcceptAgents;
        public boolean CanAcceptTasks;
    }

    public SimStatus() {
        this.zeroCoded = false;
        this.SimStatusData_Field = new SimStatusData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimStatus(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put(Ascii.FF);
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptAgents);
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptTasks);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimStatusData_Field.CanAcceptAgents = unpackBoolean(byteBuffer);
        this.SimStatusData_Field.CanAcceptTasks = unpackBoolean(byteBuffer);
    }
}
