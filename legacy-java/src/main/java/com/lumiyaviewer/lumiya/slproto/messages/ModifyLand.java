package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class ModifyLand extends SLMessage {
    public AgentData AgentData_Field;
    public ModifyBlock ModifyBlock_Field;
    public ArrayList<ParcelData> ParcelData_Fields = new ArrayList<>();
    public ArrayList<ModifyBlockExtended> ModifyBlockExtended_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class ModifyBlock {
        public int Action;
        public int BrushSize;
        public float Height;
        public float Seconds;
    }

    /* loaded from: classes.dex */
    public static class ModifyBlockExtended {
        public float BrushSize;
    }

    /* loaded from: classes.dex */
    public static class ParcelData {
        public float East;
        public int LocalID;
        public float North;
        public float South;
        public float West;
    }

    public ModifyLand() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ModifyBlock_Field = new ModifyBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 20) + 47 + 1 + (this.ModifyBlockExtended_Fields.size() * 4);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleModifyLand(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 124);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.ModifyBlock_Field.Action);
        packByte(byteBuffer, (byte) this.ModifyBlock_Field.BrushSize);
        packFloat(byteBuffer, this.ModifyBlock_Field.Seconds);
        packFloat(byteBuffer, this.ModifyBlock_Field.Height);
        byteBuffer.put((byte) this.ParcelData_Fields.size());
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packInt(byteBuffer, parcelData.LocalID);
            packFloat(byteBuffer, parcelData.West);
            packFloat(byteBuffer, parcelData.South);
            packFloat(byteBuffer, parcelData.East);
            packFloat(byteBuffer, parcelData.North);
        }
        byteBuffer.put((byte) this.ModifyBlockExtended_Fields.size());
        Iterator<T> it = this.ModifyBlockExtended_Fields.iterator();
        while (it.hasNext()) {
            packFloat(byteBuffer, ((ModifyBlockExtended) it.next()).BrushSize);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ModifyBlock_Field.Action = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ModifyBlock_Field.BrushSize = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ModifyBlock_Field.Seconds = unpackFloat(byteBuffer);
        this.ModifyBlock_Field.Height = unpackFloat(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ParcelData parcelData = new ParcelData();
            parcelData.LocalID = unpackInt(byteBuffer);
            parcelData.West = unpackFloat(byteBuffer);
            parcelData.South = unpackFloat(byteBuffer);
            parcelData.East = unpackFloat(byteBuffer);
            parcelData.North = unpackFloat(byteBuffer);
            this.ParcelData_Fields.add(parcelData);
        }
        int i3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i4 = 0; i4 < i3; i4++) {
            ModifyBlockExtended modifyBlockExtended = new ModifyBlockExtended();
            modifyBlockExtended.BrushSize = unpackFloat(byteBuffer);
            this.ModifyBlockExtended_Fields.add(modifyBlockExtended);
        }
    }
}
