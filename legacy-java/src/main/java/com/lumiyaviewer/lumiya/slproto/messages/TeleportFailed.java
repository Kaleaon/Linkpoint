package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class TeleportFailed extends SLMessage {
    public ArrayList<AlertInfo> AlertInfo_Fields = new ArrayList<>();
    public Info Info_Field;

    /* loaded from: classes.dex */
    public static class AlertInfo {
        public byte[] ExtraParams;
        public byte[] Message;
    }

    /* loaded from: classes.dex */
    public static class Info {
        public UUID AgentID;
        public byte[] Reason;
    }

    public TeleportFailed() {
        this.zeroCoded = false;
        this.Info_Field = new Info();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int length = this.Info_Field.Reason.length + 17 + 4 + 1;
        Iterator<T> it = this.AlertInfo_Fields.iterator();
        while (true) {
            int i = length;
            if (!it.hasNext()) {
                return i;
            }
            AlertInfo alertInfo = (AlertInfo) it.next();
            length = alertInfo.ExtraParams.length + alertInfo.Message.length + 1 + 1 + i;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportFailed(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 74);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packVariable(byteBuffer, this.Info_Field.Reason, 1);
        byteBuffer.put((byte) this.AlertInfo_Fields.size());
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1);
            packVariable(byteBuffer, alertInfo.ExtraParams, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.Reason = unpackVariable(byteBuffer, 1);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            AlertInfo alertInfo = new AlertInfo();
            alertInfo.Message = unpackVariable(byteBuffer, 1);
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1);
            this.AlertInfo_Fields.add(alertInfo);
        }
    }
}
