package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.EnhancedLLSDUtils
import lindenlab.llsd.LLSD
import lindenlab.llsd.LLSDUtils
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Kotlin version of ObjectLink message using Kaleaon's LLSD-Java library
 * Benefits over Java version:
 * - Null safety built into the type system
 * - Data classes reduce boilerplate
 * - More concise syntax with property delegation
 * - Better collection handling with immutable lists
 * - Advanced LLSD features (JSON, Notation, Binary formats)
 */
class ObjectLinkKt : SLMessage() {
    
    private var messageLLSD: LLSD = createDefaultLLSD()
    
    // Legacy compatibility fields
    var AgentData_Field: AgentData = AgentData()
    val ObjectData_Fields: MutableList<ObjectData> = mutableListOf()

    data class AgentData(
        var AgentID: UUID? = null,
        var SessionID: UUID? = null
    )

    data class ObjectData(
        var ObjectLocalID: Int = 0
    )

    init {
        zeroCoded = false
    }
    
    private fun createDefaultLLSD(): LLSD {
        val messageData = mapOf(
            "AgentData" to mapOf(
                "AgentID" to UUID.randomUUID(),
                "SessionID" to UUID.randomUUID()
            ),
            "ObjectData" to emptyList<Map<String, Any>>()
        )
        return LLSD(messageData)
    }

    // Kotlin properties for LLSD access using enhanced utilities with proper nested navigation
    var agentID: UUID?
        get() = EnhancedLLSDUtils.safeGetUUID(messageLLSD, "AgentData.AgentID", null)
        set(value) = updateNestedLLSDField("AgentData", "AgentID", value)

    var sessionID: UUID?
        get() = EnhancedLLSDUtils.safeGetUUID(messageLLSD, "AgentData.SessionID", null)
        set(value) = updateNestedLLSDField("AgentData", "SessionID", value)

    val objectLocalIDs: List<Int>
        get() {
            val content = messageLLSD.content as? Map<*, *> ?: return emptyList()
            val objectDataList = content["ObjectData"] as? List<*> ?: return emptyList()
            return objectDataList.filterIsInstance<Map<*, *>>()
                .mapNotNull { it["ObjectLocalID"] as? Int }
        }

    // Functional-style operations on object data
    fun addObjectLocalID(localID: Int) {
        val content = messageLLSD.content as? MutableMap<String, Any> ?: return
        val objectDataList = content["ObjectData"] as? MutableList<Any> ?: return
        
        val objectItem = mapOf("ObjectLocalID" to localID)
        objectDataList.add(objectItem)
        syncToLegacyFields()
    }

    fun removeObjectLocalID(localID: Int) {
        val content = messageLLSD.content as? MutableMap<String, Any> ?: return
        val objectDataList = content["ObjectData"] as? MutableList<Any> ?: return
        
        objectDataList.removeAll { item ->
            (item as? Map<*, *>)?.get("ObjectLocalID") == localID
        }
        syncToLegacyFields()
    }

    fun clearObjectData() {
        val content = messageLLSD.content as? MutableMap<String, Any> ?: return
        val objectDataList = content["ObjectData"] as? MutableList<Any> ?: return
        objectDataList.clear()
        syncToLegacyFields()
    }

    // Bulk operations with functional style
    fun setObjectLocalIDs(localIDs: List<Int>) {
        clearObjectData()
        localIDs.forEach { addObjectLocalID(it) }
    }

    fun mapObjectData(transform: (Int) -> Int) {
        val currentIDs = objectLocalIDs
        clearObjectData()
        currentIDs.map(transform).forEach { addObjectLocalID(it) }
    }

    fun filterObjectData(predicate: (Int) -> Boolean) {
        val currentIDs = objectLocalIDs
        clearObjectData()
        currentIDs.filter(predicate).forEach { addObjectLocalID(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateNestedLLSDField(parentKey: String, key: String, value: Any?) {
        val content = messageLLSD.content as? MutableMap<String, Any> ?: return
        val parentMap = content[parentKey] as? MutableMap<String, Any> ?: return
        parentMap[key] = value
        syncToLegacyFields()
    }

    private fun syncToLegacyFields() {
        AgentData_Field.AgentID = agentID
        AgentData_Field.SessionID = sessionID
        
        ObjectData_Fields.clear()
        objectLocalIDs.forEach { localID ->
            ObjectData_Fields.add(ObjectData(localID))
        }
    }

    private fun syncFromLegacyFields() {
        agentID = AgentData_Field.AgentID
        sessionID = AgentData_Field.SessionID
        
        setObjectLocalIDs(ObjectData_Fields.map { it.ObjectLocalID })
    }

    override fun CalcPayloadSize(): Int = (objectLocalIDs.size * 4) + 37

    override fun Handle(handler: SLMessageHandler?) {
        // Note: ObjectLinkKt is not an ObjectLink subclass, so handler may not accept it
        // This is a Kotlin alternative implementation with enhanced features
        // For full compatibility, use the Java ObjectLink class instead
        // handler?.HandleObjectLink(this)  // Would cause ClassCastException
    }

    override fun PackPayload(buffer: ByteBuffer?) {
        buffer ?: return

        syncToLegacyFields()

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
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        val objectCount = buffer.get() and UnsignedBytes.MAX_VALUE

        ObjectData_Fields.clear()
        repeat(objectCount) {
            val objectData = ObjectData(unpackInt(buffer))
            ObjectData_Fields.add(objectData)
        }

        // Sync to LLSD fields
        syncFromLegacyFields()
    }

    // Advanced serialization using Kaleaon's LLSD library features
    fun toXMLString(): String = try {
        EnhancedLLSDUtils.toXMLString(messageLLSD)
    } catch (e: Exception) {
        "<llsd><undef /></llsd>"
    }

    fun toJSONString(): String = try {
        EnhancedLLSDUtils.toJSONString(messageLLSD)
    } catch (e: Exception) {
        "{}"
    }

    fun toNotationString(): String = try {
        EnhancedLLSDUtils.toNotationString(messageLLSD)
    } catch (e: Exception) {
        "{}"
    }

    // Validation using Kaleaon's utilities with proper error handling
    fun isValid(): Boolean = try {
        val missing = EnhancedLLSDUtils.validateRequiredFields(
            messageLLSD,
            "AgentData.AgentID", 
            "AgentData.SessionID"
        )
        missing.isEmpty()
    } catch (e: Exception) {
        false
    }

    // Pretty printing for debugging
    fun prettyPrint(): String = try {
        EnhancedLLSDUtils.prettyPrint(messageLLSD)
    } catch (e: Exception) {
        toXMLString()
    }

    // Functional-style building
    companion object {
        fun builder(): ObjectLinkKtBuilder = ObjectLinkKtBuilder()
    }

    class ObjectLinkKtBuilder {
        private var agentID: UUID? = null
        private var sessionID: UUID? = null
        private val objectIDs = mutableListOf<Int>()

        fun agentID(id: UUID?) = apply { agentID = id }
        fun sessionID(id: UUID?) = apply { sessionID = id }
        fun addObjectID(id: Int) = apply { objectIDs.add(id) }
        fun addObjectIDs(ids: List<Int>) = apply { objectIDs.addAll(ids) }
        fun clearObjectIDs() = apply { objectIDs.clear() }

        fun build(): ObjectLinkKt {
            val message = ObjectLinkKt()
            message.agentID = agentID
            message.sessionID = sessionID
            message.setObjectLocalIDs(objectIDs)
            return message
        }
    }

    // Extension functions for better Kotlin integration
    operator fun plusAssign(localID: Int) {
        addObjectLocalID(localID)
    }

    operator fun minusAssign(localID: Int) {
        removeObjectLocalID(localID)
    }

    operator fun contains(localID: Int): Boolean = localID in objectLocalIDs

    // Destructuring support
    operator fun component1(): UUID? = agentID
    operator fun component2(): UUID? = sessionID
    operator fun component3(): List<Int> = objectLocalIDs
}