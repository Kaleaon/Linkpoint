package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.*

/**
 * Agent update message - sent to update agent position and state
 */
class AgentUpdateMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var bodyRotation: LLQuaternion = LLQuaternion()
    var headRotation: LLQuaternion = LLQuaternion()
    var state: Byte = 0
    var cameraCenter: LLVector3 = LLVector3()
    var cameraAtAxis: LLVector3 = LLVector3()
    var cameraLeftAxis: LLVector3 = LLVector3()
    var cameraUpAxis: LLVector3 = LLVector3()
    var far: Float = 0f
    var controlFlags: Int = 0
    var flags: Byte = 0
    
    override fun packPayload(buffer: ByteBuffer) {
        // Agent data block
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        
        bodyRotation.pack(buffer)
        headRotation.pack(buffer)
        
        buffer.put(state)
        
        cameraCenter.pack(buffer)
        cameraAtAxis.pack(buffer)
        cameraLeftAxis.pack(buffer)
        cameraUpAxis.pack(buffer)
        
        buffer.putFloat(far)
        buffer.putInt(controlFlags)
        buffer.put(flags)
    }
    
    override fun unpackPayload(buffer: ByteBuffer) {
        // Agent data block
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)
        
        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)
        
        bodyRotation = LLQuaternion.unpack(buffer)
        headRotation = LLQuaternion.unpack(buffer)
        
        state = buffer.get()
        
        cameraCenter = LLVector3.unpack(buffer)
        cameraAtAxis = LLVector3.unpack(buffer)
        cameraLeftAxis = LLVector3.unpack(buffer)
        cameraUpAxis = LLVector3.unpack(buffer)
        
        far = buffer.getFloat()
        controlFlags = buffer.getInt()
        flags = buffer.get()
    }
    
    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_UPDATE
    override fun getMessageName(): String = "AgentUpdate"
}

/**
 * Agent animation message
 */
class AgentAnimationMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    
    data class AnimationBlock(
        var animId: UUID = UUID.randomUUID(),
        var startAnim: Boolean = true
    )
    
    val animationList = mutableListOf<AnimationBlock>()
    
    override fun packPayload(buffer: ByteBuffer) {
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        
        // Animation list
        buffer.put(animationList.size.toByte())
        for (anim in animationList) {
            buffer.putLong(anim.animId.mostSignificantBits)
            buffer.putLong(anim.animId.leastSignificantBits)
            buffer.put(if (anim.startAnim) 1 else 0)
        }
    }
    
    override fun unpackPayload(buffer: ByteBuffer) {
        // Agent data
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)
        
        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)
        
        // Animation list
        val count = buffer.get().toInt() and 0xFF
        animationList.clear()
        for (i in 0 until count) {
            val animMsb = buffer.getLong()
            val animLsb = buffer.getLong()
            val animId = UUID(animMsb, animLsb)
            val startAnim = buffer.get() != 0.toByte()
            animationList.add(AnimationBlock(animId, startAnim))
        }
    }
    
    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.AGENT_ANIMATION
    override fun getMessageName(): String = "AgentAnimation"
}