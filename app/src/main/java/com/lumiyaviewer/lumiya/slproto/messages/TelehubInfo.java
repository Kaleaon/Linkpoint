package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class TelehubInfo extends SLMessage {
    public ArrayList<SpawnPointBlock> SpawnPointBlock_Fields = new ArrayList<>();
    public TelehubBlock TelehubBlock_Field;

    public static class SpawnPointBlock {
        public LLVector3 SpawnPointPos;
    }

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

    public int CalcPayloadSize() {
        return this.TelehubBlock_Field.ObjectName.length + 17 + 12 + 12 + 4 + 1 + (this.SpawnPointBlock_Fields.size() * 12);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTelehubInfo(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 10);
        packUUID(byteBuffer, this.TelehubBlock_Field.ObjectID);
        packVariable(byteBuffer, this.TelehubBlock_Field.ObjectName, 1);
        packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos);
        packLLQuaternion(byteBuffer, this.TelehubBlock_Field.TelehubRot);
        byteBuffer.put((byte) this.SpawnPointBlock_Fields.size());
        for (SpawnPointBlock spawnPointBlock : this.SpawnPointBlock_Fields) {
            packLLVector3(byteBuffer, spawnPointBlock.SpawnPointPos);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TelehubBlock_Field.ObjectID = unpackUUID(byteBuffer);
        this.TelehubBlock_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.TelehubBlock_Field.TelehubPos = unpackLLVector3(byteBuffer);
        this.TelehubBlock_Field.TelehubRot = unpackLLQuaternion(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            SpawnPointBlock spawnPointBlock = new SpawnPointBlock();
            spawnPointBlock.SpawnPointPos = unpackLLVector3(byteBuffer);
            this.SpawnPointBlock_Fields.add(spawnPointBlock);
        }
    }
}
