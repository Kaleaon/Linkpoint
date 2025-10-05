package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class RezMultipleAttachmentsFromInv : SLMessage() {
    public AgentData AgentData_Field
    public HeaderData HeaderData_Field
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class HeaderData {
        public UUID CompoundMsgID
        public Boolean FirstDetachAll
        public Int TotalObjects
    }

    @JvmStatic
    class ObjectData {
        public Int AttachmentPt
        public Byte[] Description
        public Int EveryoneMask
        public Int GroupMask
        public Int ItemFlags
        public UUID ItemID
        public Byte[] Name
        public Int NextOwnerMask
        public UUID OwnerID
    }

    public RezMultipleAttachmentsFromInv() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.HeaderData_Field = HeaderData()
    }

    public Int CalcPayloadSize() {
        Int i = 55
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ObjectData objectData = (ObjectData) it.next()
            i = objectData.Description.length + objectData.Name.length + 50 + 1 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezMultipleAttachmentsFromInv(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -116)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.HeaderData_Field.CompoundMsgID)
        packByte(byteBuffer, (Byte) this.HeaderData_Field.TotalObjects)
        packBoolean(byteBuffer, this.HeaderData_Field.FirstDetachAll)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.ItemID)
            packUUID(byteBuffer, objectData.OwnerID)
            packByte(byteBuffer, (Byte) objectData.AttachmentPt)
            packInt(byteBuffer, objectData.ItemFlags)
            packInt(byteBuffer, objectData.GroupMask)
            packInt(byteBuffer, objectData.EveryoneMask)
            packInt(byteBuffer, objectData.NextOwnerMask)
            packVariable(byteBuffer, objectData.Name, 1)
            packVariable(byteBuffer, objectData.Description, 1)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.HeaderData_Field.CompoundMsgID = unpackUUID(byteBuffer)
        this.HeaderData_Field.TotalObjects = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.HeaderData_Field.FirstDetachAll = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ItemID = unpackUUID(byteBuffer)
            objectData.OwnerID = unpackUUID(byteBuffer)
            objectData.AttachmentPt = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.ItemFlags = unpackInt(byteBuffer)
            objectData.GroupMask = unpackInt(byteBuffer)
            objectData.EveryoneMask = unpackInt(byteBuffer)
            objectData.NextOwnerMask = unpackInt(byteBuffer)
            objectData.Name = unpackVariable(byteBuffer, 1)
            objectData.Description = unpackVariable(byteBuffer, 1)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
