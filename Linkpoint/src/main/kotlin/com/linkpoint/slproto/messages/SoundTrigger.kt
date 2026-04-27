package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SoundTrigger : SLMessage() {
    public SoundData SoundData_Field = SoundData()

    @JvmStatic
    class SoundData {
        public Float Gain
        public Long Handle
        public UUID ObjectID
        public UUID OwnerID
        public UUID ParentID
        public LLVector3 Position
        public UUID SoundID
    }

    public SoundTrigger() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 89
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleSoundTrigger(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(Ascii.GS)
        packUUID(byteBuffer, this.SoundData_Field.SoundID)
        packUUID(byteBuffer, this.SoundData_Field.OwnerID)
        packUUID(byteBuffer, this.SoundData_Field.ObjectID)
        packUUID(byteBuffer, this.SoundData_Field.ParentID)
        packLong(byteBuffer, this.SoundData_Field.Handle)
        packLLVector3(byteBuffer, this.SoundData_Field.Position)
        packFloat(byteBuffer, this.SoundData_Field.Gain)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.SoundData_Field.SoundID = unpackUUID(byteBuffer)
        this.SoundData_Field.OwnerID = unpackUUID(byteBuffer)
        this.SoundData_Field.ObjectID = unpackUUID(byteBuffer)
        this.SoundData_Field.ParentID = unpackUUID(byteBuffer)
        this.SoundData_Field.Handle = unpackLong(byteBuffer)
        this.SoundData_Field.Position = unpackLLVector3(byteBuffer)
        this.SoundData_Field.Gain = unpackFloat(byteBuffer)
    }
}
