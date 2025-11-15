package com.lumiyaviewer.lumiya.animesh

import java.nio.ByteBuffer
import java.util.UUID

/**
 * Animesh Protocol Handler
 * Handles Second Life animesh protocol messages
 */
object AnimeshProtocol {
    
    // Animesh flags
    const val OBJECT_FLAG_ANIMESH = 0x80000
    const val OBJECT_FLAG_ANIMESH_ENABLE_PHYSICS = 0x100000
    
    /**
     * Parse ObjectUpdate message for animesh
     */
    fun parseObjectUpdate(data: ByteBuffer): AnimeshObjectUpdate? {
        try {
            // Read object ID
            val objectId = UUID(data.getLong(), data.getLong())
            
            // Read flags
            val flags = data.getInt()
            
            // Check if animesh
            if ((flags and OBJECT_FLAG_ANIMESH) == 0) {
                return null // Not animesh
            }
            
            // Read attachment ID
            val attachmentId = UUID(data.getLong(), data.getLong())
            
            // Read skeleton data size
            val skeletonDataSize = data.getInt()
            
            // Read skeleton data
            val skeletonData = ByteArray(skeletonDataSize)
            data.get(skeletonData)
            
            // Read mesh asset ID
            val meshAssetId = UUID(data.getLong(), data.getLong())
            
            return AnimeshObjectUpdate(
                objectId = objectId,
                attachmentId = attachmentId,
                flags = flags,
                skeletonData = ByteBuffer.wrap(skeletonData),
                meshAssetId = meshAssetId
            )
            
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Parse AgentAnimation message for animesh animations
     */
    fun parseAgentAnimation(data: ByteBuffer): List<AnimeshAnimationUpdate> {
        val updates = mutableListOf<AnimeshAnimationUpdate>()
        
        try {
            // Read agent ID
            val agentId = UUID(data.getLong(), data.getLong())
            
            // Read number of animations
            val numAnimations = data.getInt()
            
            for (i in 0 until numAnimations) {
                // Read animation ID
                val animationId = UUID(data.getLong(), data.getLong())
                
                // Read object ID (for animesh)
                val objectId = UUID(data.getLong(), data.getLong())
                
                // Read sequence ID
                val sequenceId = data.getInt()
                
                updates.add(AnimeshAnimationUpdate(
                    agentId = agentId,
                    animationId = animationId,
                    objectId = objectId,
                    sequenceId = sequenceId
                ))
            }
            
        } catch (e: Exception) {
            // Return partial results
        }
        
        return updates
    }
    
    /**
     * Parse ObjectAnimationNames for animesh
     */
    fun parseObjectAnimationNames(data: ByteBuffer): List<AnimeshAnimationInfo> {
        val animations = mutableListOf<AnimeshAnimationInfo>()
        
        try {
            // Read object ID
            val objectId = UUID(data.getLong(), data.getLong())
            
            // Read number of animations
            val numAnimations = data.getInt()
            
            for (i in 0 until numAnimations) {
                // Read animation ID
                val animationId = UUID(data.getLong(), data.getLong())
                
                // Read animation name length
                val nameLength = data.getInt()
                val nameBytes = ByteArray(nameLength)
                data.get(nameBytes)
                val name = String(nameBytes)
                
                animations.add(AnimeshAnimationInfo(
                    objectId = objectId,
                    animationId = animationId,
                    name = name
                ))
            }
            
        } catch (e: Exception) {
            // Return partial results
        }
        
        return animations
    }
    
    /**
     * Create StartAnimesh message
     */
    fun createStartAnimeshMessage(
        agentId: UUID,
        objectId: UUID,
        animationId: UUID
    ): ByteBuffer {
        val buffer = ByteBuffer.allocate(256)
        
        // Message header
        buffer.putInt(0x1234) // Message type
        buffer.putInt(48)     // Message size
        
        // Agent ID
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        
        // Object ID
        buffer.putLong(objectId.mostSignificantBits)
        buffer.putLong(objectId.leastSignificantBits)
        
        // Animation ID
        buffer.putLong(animationId.mostSignificantBits)
        buffer.putLong(animationId.leastSignificantBits)
        
        buffer.flip()
        return buffer
    }
    
    /**
     * Create StopAnimesh message
     */
    fun createStopAnimeshMessage(
        agentId: UUID,
        objectId: UUID,
        animationId: UUID
    ): ByteBuffer {
        val buffer = ByteBuffer.allocate(256)
        
        // Message header
        buffer.putInt(0x1235) // Message type
        buffer.putInt(48)     // Message size
        
        // Agent ID
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        
        // Object ID
        buffer.putLong(objectId.mostSignificantBits)
        buffer.putLong(objectId.leastSignificantBits)
        
        // Animation ID
        buffer.putLong(animationId.mostSignificantBits)
        buffer.putLong(animationId.leastSignificantBits)
        
        buffer.flip()
        return buffer
    }
    
    /**
     * Parse AnimeshObjectPhysics for physics-enabled animesh
     */
    fun parseAnimeshPhysics(data: ByteBuffer): AnimeshPhysicsData? {
        try {
            val objectId = UUID(data.getLong(), data.getLong())
            
            // Physics enabled?
            val physicsEnabled = data.get() != 0.toByte()
            if (!physicsEnabled) return null
            
            // Read physics shape type
            val shapeType = data.get().toInt()
            
            // Read mass
            val mass = data.getFloat()
            
            // Read friction
            val friction = data.getFloat()
            
            // Read restitution
            val restitution = data.getFloat()
            
            // Read gravity multiplier
            val gravityMultiplier = data.getFloat()
            
            return AnimeshPhysicsData(
                objectId = objectId,
                shapeType = shapeType,
                mass = mass,
                friction = friction,
                restitution = restitution,
                gravityMultiplier = gravityMultiplier
            )
            
        } catch (e: Exception) {
            return null
        }
    }
}

/**
 * Animesh object update data
 */
data class AnimeshObjectUpdate(
    val objectId: UUID,
    val attachmentId: UUID,
    val flags: Int,
    val skeletonData: ByteBuffer,
    val meshAssetId: UUID
)

/**
 * Animesh animation update data
 */
data class AnimeshAnimationUpdate(
    val agentId: UUID,
    val animationId: UUID,
    val objectId: UUID,
    val sequenceId: Int
)

/**
 * Animesh animation info
 */
data class AnimeshAnimationInfo(
    val objectId: UUID,
    val animationId: UUID,
    val name: String
)

/**
 * Animesh physics data
 */
data class AnimeshPhysicsData(
    val objectId: UUID,
    val shapeType: Int,
    val mass: Float,
    val friction: Float,
    val restitution: Float,
    val gravityMultiplier: Float
)