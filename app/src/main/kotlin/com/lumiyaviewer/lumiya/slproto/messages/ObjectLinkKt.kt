package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Kotlin version of ObjectLink message using LLSD types
 * Benefits over Java version:
 * - Null safety built into the type system
 * - Data classes reduce boilerplate
 * - More concise syntax
 * - Better collection handling with immutable lists
 */
class ObjectLinkKt : SLMessage() {
    
    private var agentDataField: AgentData = AgentData()
    private val objectDataList: MutableList<ObjectData> = mutableListOf()
    
    // Legacy compatibility fields
    var AgentData_Field: AgentData = agentDataField
    val ObjectData_Fields: MutableList<ObjectData> = objectDataList

    data class AgentData(
        private val agentID: LLSDUUID = LLSDUUID(),
        private val sessionID: LLSDUUID = LLSDUUID()
    ) {
        // Properties with null safety built-in
        val agentIDValue: UUID? get() = agentID.asUUID()
        val sessionIDValue: UUID? get() = sessionID.asUUID()
        
        // Mutable setters that create new LLSD instances
        fun withAgentID(id: UUID?) = copy(agentID = id?.let { LLSDUUID(it) } ?: LLSDUUID())
        fun withSessionID(id: UUID?) = copy(sessionID = id?.let { LLSDUUID(it) } ?: LLSDUUID())
        
        // Legacy compatibility
        var AgentID: UUID? 
            get() = agentIDValue
            set(value) { /* Read-only for compatibility */ }
            
        var SessionID: UUID?
            get() = sessionIDValue  
            set(value) { /* Read-only for compatibility */ }
    }

    data class ObjectData(
        private val objectLocalID: LLSDInt = LLSDInt(0)
    ) {
        // Property with null safety
        val objectLocalIDValue: Int get() = objectLocalID.asInt()
        
        // Constructor for easy creation
        constructor(localID: Int) : this(LLSDInt(localID))
        
        // Immutable update
        fun withObjectLocalID(localID: Int) = copy(objectLocalID = LLSDInt(localID))
        
        // Legacy compatibility
        var ObjectLocalID: Int
            get() = objectLocalIDValue
            set(value) { /* Read-only for compatibility */ }
    }

    init {
        zeroCoded = false
    }

    // Kotlin property for agent data
    var agentData: AgentData
        get() = agentDataField
        set(value) {
            agentDataField = value
            AgentData_Field = value  // Sync legacy field
        }

    // Immutable access to object data
    val objectData: List<ObjectData> get() = objectDataList.toList()

    // Safe methods for modifying object data
    fun addObjectData(objectData: ObjectData) {
        objectDataList.add(objectData)
        ObjectData_Fields.add(objectData)  // Sync legacy field
    }

    fun addObjectData(localID: Int) = addObjectData(ObjectData(localID))

    fun clearObjectData() {
        objectDataList.clear()
        ObjectData_Fields.clear()  // Sync legacy field
    }

    override fun CalcPayloadSize(): Int = (objectDataList.size * 4) + 37

    override fun Handle(handler: SLMessageHandler?) {
        handler?.HandleObjectLink(this as ObjectLink)  // Cast for legacy compatibility
    }

    override fun PackPayload(buffer: ByteBuffer?) {
        buffer ?: return

        // Sync to legacy fields for wire protocol compatibility
        AgentData_Field.AgentID = agentDataField.agentIDValue
        AgentData_Field.SessionID = agentDataField.sessionIDValue
        
        ObjectData_Fields.clear()
        objectDataList.forEach { data ->
            val legacyData = ObjectData(data.objectLocalIDValue)
            legacyData.ObjectLocalID = data.objectLocalIDValue
            ObjectData_Fields.add(legacyData)
        }

        buffer.apply {
            putShort(-1)
            put(0.toByte())
            put(115.toByte())
            packUUID(AgentData_Field.AgentID)
            packUUID(AgentData_Field.SessionID)
            put(ObjectData_Fields.size.toByte())
            ObjectData_Fields.forEach { data ->
                packInt(data.ObjectLocalID)
            }
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer?) {
        buffer ?: return

        // Legacy unpacking
        val agentID = unpackUUID(buffer)
        val sessionID = unpackUUID(buffer)
        val objectCount = buffer.get() and UnsignedBytes.MAX_VALUE

        AgentData_Field.AgentID = agentID
        AgentData_Field.SessionID = sessionID
        
        ObjectData_Fields.clear()
        repeat(objectCount) {
            val objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(buffer)
            ObjectData_Fields.add(objectData)
        }

        // Sync to LLSD fields
        agentDataField = agentDataField
            .withAgentID(agentID)
            .withSessionID(sessionID)

        objectDataList.clear()
        ObjectData_Fields.forEach { legacyData ->
            objectDataList.add(ObjectData(legacyData.ObjectLocalID))
        }
    }
}