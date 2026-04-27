package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class LogTextMessage extends SLMessage {
    public ArrayList<DataBlock> DataBlock_Fields = new ArrayList<>();

    public static class DataBlock {
        public UUID FromAgentId;
        public double GlobalX;
        public double GlobalY;
        public byte[] Message;
        public int Time;
        public UUID ToAgentId;
    }

    public LogTextMessage() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.DataBlock_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((DataBlock) it.next()).Message.length + 54 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogTextMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -121);
        byteBuffer.put((byte) this.DataBlock_Fields.size());
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.FromAgentId);
            packUUID(byteBuffer, dataBlock.ToAgentId);
            packDouble(byteBuffer, dataBlock.GlobalX);
            packDouble(byteBuffer, dataBlock.GlobalY);
            packInt(byteBuffer, dataBlock.Time);
            packVariable(byteBuffer, dataBlock.Message, 2);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            DataBlock dataBlock = new DataBlock();
            dataBlock.FromAgentId = unpackUUID(byteBuffer);
            dataBlock.ToAgentId = unpackUUID(byteBuffer);
            dataBlock.GlobalX = unpackDouble(byteBuffer);
            dataBlock.GlobalY = unpackDouble(byteBuffer);
            dataBlock.Time = unpackInt(byteBuffer);
            dataBlock.Message = unpackVariable(byteBuffer, 2);
            this.DataBlock_Fields.add(dataBlock);
        }
    }
}
