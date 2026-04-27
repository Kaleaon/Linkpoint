package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SoundTrigger extends SLMessage {
    public SoundData SoundData_Field = new SoundData();

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
    }

    public int CalcPayloadSize() {
        return 89;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSoundTrigger(this);
    }

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
