package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ScriptDataRequest extends SLMessage {
    public ArrayList<DataBlock> DataBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class DataBlock {
        public long Hash;
        public byte[] Request;
        public int RequestType;
    }

    public ScriptDataRequest() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.DataBlock_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((DataBlock) it.next()).Request.length + 11 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDataRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 81);
        byteBuffer.put((byte) this.DataBlock_Fields.size());
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packLong(byteBuffer, dataBlock.Hash);
            packByte(byteBuffer, (byte) dataBlock.RequestType);
            packVariable(byteBuffer, dataBlock.Request, 2);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            DataBlock dataBlock = new DataBlock();
            dataBlock.Hash = unpackLong(byteBuffer);
            dataBlock.RequestType = unpackByte(byteBuffer);
            dataBlock.Request = unpackVariable(byteBuffer, 2);
            this.DataBlock_Fields.add(dataBlock);
        }
    }
}
