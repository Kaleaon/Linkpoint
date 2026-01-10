package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelDisableObjects : SLMessage {
    AgentData AgentData_Field
    ArrayList<OwnerIDs> OwnerIDs_Fields = ArrayList<>()
    ParcelData ParcelData_Field
    ArrayList<TaskIDs> TaskIDs_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class OwnerIDs {
        UUID OwnerID
    }

    class ParcelData {
        Int LocalID
        Int ReturnType
    }

    class TaskIDs {
        UUID TaskID
    }

    ParcelDisableObjects() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.ParcelData_Field = ParcelData()
    }

    fun CalcPayloadSize(): Int {
        return (this.TaskIDs_Fields.size() * 16) + 45 + 1 + (this.OwnerIDs_Fields.size() * 16)
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelDisableObjects(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -55)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
        packInt(byteBuffer, this.ParcelData_Field.ReturnType)
        byteBuffer.put((this as Byte).TaskIDs_Fields.size())
        for (TaskIDs taskIDs : this.TaskIDs_Fields) {
            packUUID(byteBuffer, taskIDs.TaskID)
        }
        byteBuffer.put((this as Byte).OwnerIDs_Fields.size())
        for (OwnerIDs ownerIDs : this.OwnerIDs_Fields) {
            packUUID(byteBuffer, ownerIDs.OwnerID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            TaskIDs taskIDs = TaskIDs()
            taskIDs.TaskID = unpackUUID(byteBuffer)
            this.TaskIDs_Fields.add(taskIDs)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            OwnerIDs ownerIDs = OwnerIDs()
            ownerIDs.OwnerID = unpackUUID(byteBuffer)
            this.OwnerIDs_Fields.add(ownerIDs)
        }
    }
}
