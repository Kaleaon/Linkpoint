package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class SoundTrigger extends SLMessage {
    public SoundData SoundData_Field;

    /* loaded from: classes.dex */
    public static class SoundData {
        public float Gain;
        public long Handle;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID ParentID;
        public LLVector3 Position;
        public UUID SoundID;
    }

    public SoundTrigger() {
        this.zeroCoded = false;
        this.SoundData_Field = new SoundData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 89;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSoundTrigger(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.GS);
        packUUID(byteBuffer, this.SoundData_Field.SoundID);
        packUUID(byteBuffer, this.SoundData_Field.OwnerID);
        packUUID(byteBuffer, this.SoundData_Field.ObjectID);
        packUUID(byteBuffer, this.SoundData_Field.ParentID);
        packLong(byteBuffer, this.SoundData_Field.Handle);
        packLLVector3(byteBuffer, this.SoundData_Field.Position);
        packFloat(byteBuffer, this.SoundData_Field.Gain);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SoundData_Field.SoundID = unpackUUID(byteBuffer);
        this.SoundData_Field.OwnerID = unpackUUID(byteBuffer);
        this.SoundData_Field.ObjectID = unpackUUID(byteBuffer);
        this.SoundData_Field.ParentID = unpackUUID(byteBuffer);
        this.SoundData_Field.Handle = unpackLong(byteBuffer);
        this.SoundData_Field.Position = unpackLLVector3(byteBuffer);
        this.SoundData_Field.Gain = unpackFloat(byteBuffer);
    }
}
