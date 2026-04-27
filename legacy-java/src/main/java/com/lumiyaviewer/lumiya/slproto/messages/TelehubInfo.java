package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class TelehubInfo extends SLMessage {
    public ArrayList<SpawnPointBlock> SpawnPointBlock_Fields = new ArrayList<>();
    public TelehubBlock TelehubBlock_Field;

    /* loaded from: classes.dex */
    public static class SpawnPointBlock {
        public LLVector3 SpawnPointPos;
    }

    /* loaded from: classes.dex */
    public static class TelehubBlock {
        public UUID ObjectID;
        public byte[] ObjectName;
        public LLVector3 TelehubPos;
        public LLQuaternion TelehubRot;
    }

    public TelehubInfo() {
        this.zeroCoded = false;
        this.TelehubBlock_Field = new TelehubBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.TelehubBlock_Field.ObjectName.length + 17 + 12 + 12 + 4 + 1 + (this.SpawnPointBlock_Fields.size() * 12);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTelehubInfo(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 10);
        packUUID(byteBuffer, this.TelehubBlock_Field.ObjectID);
        packVariable(byteBuffer, this.TelehubBlock_Field.ObjectName, 1);
        packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos);
        packLLQuaternion(byteBuffer, this.TelehubBlock_Field.TelehubRot);
        byteBuffer.put((byte) this.SpawnPointBlock_Fields.size());
        Iterator<T> it = this.SpawnPointBlock_Fields.iterator();
        while (it.hasNext()) {
            packLLVector3(byteBuffer, ((SpawnPointBlock) it.next()).SpawnPointPos);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TelehubBlock_Field.ObjectID = unpackUUID(byteBuffer);
        this.TelehubBlock_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.TelehubBlock_Field.TelehubPos = unpackLLVector3(byteBuffer);
        this.TelehubBlock_Field.TelehubRot = unpackLLQuaternion(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            SpawnPointBlock spawnPointBlock = new SpawnPointBlock();
            spawnPointBlock.SpawnPointPos = unpackLLVector3(byteBuffer);
            this.SpawnPointBlock_Fields.add(spawnPointBlock);
        }
    }
}
