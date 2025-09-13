package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class AlertMessage extends SLMessage {
    public AlertData AlertData_Field;
    public ArrayList<AlertInfo> AlertInfo_Fields = new ArrayList<>();

    public static class AlertData {
        public byte[] Message;
    }

    public static class AlertInfo {
        public byte[] ExtraParams;
        public byte[] Message;
    }

    public AlertMessage() {
        this.zeroCoded = false;
        this.AlertData_Field = new AlertData();
    }

    public int CalcPayloadSize() {
        int length = this.AlertData_Field.Message.length + 1 + 4 + 1;
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

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAlertMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -122);
        packVariable(byteBuffer, this.AlertData_Field.Message, 1);
        byteBuffer.put((byte) this.AlertInfo_Fields.size());
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1);
            packVariable(byteBuffer, alertInfo.ExtraParams, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            AlertInfo alertInfo = new AlertInfo();
            alertInfo.Message = unpackVariable(byteBuffer, 1);
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1);
            this.AlertInfo_Fields.add(alertInfo);
        }
    }
}
