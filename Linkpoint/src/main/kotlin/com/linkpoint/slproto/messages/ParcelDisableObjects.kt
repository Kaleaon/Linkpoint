package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelDisableObjects : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<OwnerIDs> OwnerIDs_Fields = ArrayList<>()
    public ParcelData ParcelData_Field
    public ArrayList<TaskIDs> TaskIDs_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class OwnerIDs {
        public UUID OwnerID
    }

    @JvmStatic
    class ParcelData {
        public Int LocalID
        public Int ReturnType
    }

    @JvmStatic
    class TaskIDs {
        public UUID TaskID
    }

    public ParcelDisableObjects() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ParcelData_Field = ParcelData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.TaskIDs_Fields.size() * 16) + 45 + 1 + (this.OwnerIDs_Fields.size() * 16)
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleParcelDisableObjects(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -55)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
        packInt(byteBuffer, this.ParcelData_Field.ReturnType)
        byteBuffer.put((Byte) this.TaskIDs_Fields.size())
        for (TaskIDs taskIDs : this.TaskIDs_Fields) {
            packUUID(byteBuffer, taskIDs.TaskID)
        }
        byteBuffer.put((Byte) this.OwnerIDs_Fields.size())
        for (OwnerIDs ownerIDs : this.OwnerIDs_Fields) {
            packUUID(byteBuffer, ownerIDs.OwnerID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val taskIDs: TaskIDs = TaskIDs()
            taskIDs.TaskID = unpackUUID(byteBuffer)
            this.TaskIDs_Fields.add(taskIDs)
        }
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val ownerIDs: OwnerIDs = OwnerIDs()
            ownerIDs.OwnerID = unpackUUID(byteBuffer)
            this.OwnerIDs_Fields.add(ownerIDs)
        }
    }
}
