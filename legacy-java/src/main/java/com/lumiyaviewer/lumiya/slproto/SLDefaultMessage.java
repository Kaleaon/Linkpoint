package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SLDefaultMessage extends SLMessage {
    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 0;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.DefaultMessageHandler(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
    }
}
