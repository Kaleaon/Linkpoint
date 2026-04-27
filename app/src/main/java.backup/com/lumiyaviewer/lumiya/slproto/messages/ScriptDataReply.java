package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class ScriptDataReply extends SLMessage {
    public ArrayList<DataBlock> DataBlock_Fields = new ArrayList<>();

    public static class DataBlock {
        public long Hash;
        public byte[] Reply;
    }

    public ScriptDataReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.DataBlock_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((DataBlock) it.next()).Reply.length + 10 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDataReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 82);
        byteBuffer.put((byte) this.DataBlock_Fields.size());
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packLong(byteBuffer, dataBlock.Hash);
            packVariable(byteBuffer, dataBlock.Reply, 2);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            DataBlock dataBlock = new DataBlock();
            dataBlock.Hash = unpackLong(byteBuffer);
            dataBlock.Reply = unpackVariable(byteBuffer, 2);
            this.DataBlock_Fields.add(dataBlock);
        }
    }
}
