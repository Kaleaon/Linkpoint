package com.linkpoint.avatar

import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages avatar appearance and sends AgentSetAppearance to the simulator.
 * 
 * This is CRITICAL for other avatars to see your appearance. Without this,
 * you will appear as a cloud (default avatar) to others.
 * 
 * Based on LibreMetaverse AgentManager.SetAppearance() and
 * Firestorm LLAppearanceMgr::updateAppearanceFromCOF()
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/AgentSetAppearance">AgentSetAppearance Message</a>
 */
class AppearanceManager(
    private val udpConnection: UDPConnectionFixed,
    private val avatarBaker: AvatarBaker
) {
    companion object {
        private const val TAG = "AppearanceManager"
        private val MESSAGE_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN
        
        // Visual parameter count (218 params in current SL protocol)
        const val VISUAL_PARAM_COUNT = 218
        
        // Texture entry constants
        const val TEX_ENTRY_COUNT = 21
        
        // Baked texture indices in texture entry
        const val TEX_HEAD_BAKED = 8
        const val TEX_UPPER_BAKED = 9
        const val TEX_LOWER_BAKED = 10
        const val TEX_EYES_BAKED = 11
        const val TEX_SKIRT_BAKED = 12
        const val TEX_HAIR_BAKED = 13
        const val TEX_LEFTARM_BAKED = 14
        const val TEX_LEFTLEG_BAKED = 15
        const val TEX_AUX1_BAKED = 16
        const val TEX_AUX2_BAKED = 17
        const val TEX_AUX3_BAKED = 18
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Serial number for appearance updates (must increment)
    private val serialNum = AtomicInteger(0)
    
    // Current visual parameters (shape sliders)
    private var visualParams = ByteArray(VISUAL_PARAM_COUNT) { 127.toByte() } // Default to middle value
    
    // Agent and session info
    private var agentId: UUID = UUID(0, 0)
    private var sessionId: UUID = UUID(0, 0)
    
    // Avatar size (computed from visual params)
    private var avatarHeight: Float = 1.7f
    
    /**
     * Set agent and session info (required before sending appearance).
     */
    fun setSessionInfo(agentId: UUID, sessionId: UUID) {
        this.agentId = agentId
        this.sessionId = sessionId
    }
    
    /**
     * Set visual parameters (shape sliders).
     * Each byte represents a slider from 0-255.
     */
    fun setVisualParams(params: ByteArray) {
        if (params.size == VISUAL_PARAM_COUNT) {
            visualParams = params.copyOf()
            avatarHeight = calculateAvatarHeight()
        } else {
            Log.w(TAG, "Invalid visual param count: ${params.size}, expected $VISUAL_PARAM_COUNT")
        }
    }
    
    /**
     * Update a single visual parameter.
     * @param index Parameter index (0-217)
     * @param value Parameter value (0-255)
     */
    fun setVisualParam(index: Int, value: Int) {
        if (index in 0 until VISUAL_PARAM_COUNT) {
            visualParams[index] = value.coerceIn(0, 255).toByte()
            avatarHeight = calculateAvatarHeight()
        }
    }
    
    /**
     * Calculate avatar height from visual params.
     * Uses the height slider and body proportion sliders.
     */
    private fun calculateAvatarHeight(): Float {
        // Height slider is typically param 33
        val heightValue = (visualParams[33].toInt() and 0xFF) / 255f
        // Base height range: 1.2m - 2.2m
        return 1.2f + (heightValue * 1.0f)
    }
    
    /**
     * Send full appearance update to the simulator.
     * This bakes all textures and sends AgentSetAppearance.
     */
    suspend fun sendAppearanceUpdate() {
        Log.i(TAG, "Starting appearance update...")
        
        try {
            // 1. Bake all textures
            Log.d(TAG, "Baking textures...")
            val bakedTextures = avatarBaker.bakeAll(includeBoM = true)
            Log.d(TAG, "Baked ${bakedTextures.size} textures")
            
            // 2. Send AgentSetAppearance message
            sendAgentSetAppearance(bakedTextures)
            
            Log.i(TAG, "Appearance update sent successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send appearance update", e)
        }
    }
    
    /**
     * Send AgentSetAppearance message.
     * 
     * This is the UDP message that tells the simulator what your avatar looks like.
     * Other avatars will receive this information and render you accordingly.
     * Message block fields are little-endian; UUID bytes remain big-endian.
     */
    suspend fun sendAgentSetAppearance(bakedTextures: Map<Int, UUID>) {
        val serial = serialNum.incrementAndGet()
        
        // Calculate payload size:
        // AgentData block: 16 (AgentID) + 16 (SessionID) + 4 (SerialNum) + 12 (Size) = 48
        // WearableData block: 1 (count) + N * (1 (index) + 16 (textureId)) = 1 + N*17
        // VisualParam block: 1 (count) + params = 1 + 218
        
        val wearableCount = bakedTextures.size
        val payloadSize = 48 + 1 + (wearableCount * 17) + 1 + VISUAL_PARAM_COUNT
        
        val payload = ByteBuffer.allocate(payloadSize).order(MESSAGE_BYTE_ORDER)
        
        // AgentData block
        payload.putUUID(agentId)
        payload.putUUID(sessionId)
        payload.putInt(serial)
        
        // Size (LLVector3 - avatar dimensions)
        // Width, depth, height
        payload.putFloat(0.45f)        // Width
        payload.putFloat(0.6f)         // Depth
        payload.putFloat(avatarHeight) // Height
        
        // WearableData block - list of baked texture entries
        payload.put(wearableCount.toByte())
        bakedTextures.forEach { (channel, textureId) ->
            payload.put(channelToTextureIndex(channel).toByte())
            payload.putUUID(textureId)
        }
        
        // VisualParam block
        payload.put(VISUAL_PARAM_COUNT.toByte())
        payload.put(visualParams)
        
        Log.d(TAG, "Sending AgentSetAppearance (serial=$serial, wearables=$wearableCount, params=$VISUAL_PARAM_COUNT)")
        udpConnection.sendPacket(MessageIds.AGENT_SET_APPEARANCE, payload.array(), reliable = true)
    }
    
    /**
     * Send AgentIsNowWearing message after changing outfit.
     * This tells the simulator which wearables are equipped.
     */
    suspend fun sendAgentIsNowWearing(wearables: List<WearableEntry>) {
        // AgentIsNowWearing format:
        // AgentData: AgentID (16) + SessionID (16)
        // WearableData: Count (1) + N * (ItemID (16) + WearableType (1))
        
        val payloadSize = 32 + 1 + wearables.size * 17
        val payload = ByteBuffer.allocate(payloadSize).order(MESSAGE_BYTE_ORDER)
        
        // AgentData
        payload.putUUID(agentId)
        payload.putUUID(sessionId)
        
        // WearableData
        payload.put(wearables.size.toByte())
        wearables.forEach { entry ->
            payload.putUUID(entry.itemId)
            payload.put(entry.wearableType.ordinal.toByte())
        }
        
        Log.d(TAG, "Sending AgentIsNowWearing (${wearables.size} wearables)")
        udpConnection.sendPacket(MessageIds.AGENT_IS_NOW_WEARING, payload.array(), reliable = true)
    }
    
    /**
     * Convert bake channel to texture entry index.
     */
    private fun channelToTextureIndex(channel: Int): Int {
        return when (channel) {
            AvatarBaker.BAKE_HEAD -> TEX_HEAD_BAKED
            AvatarBaker.BAKE_UPPER -> TEX_UPPER_BAKED
            AvatarBaker.BAKE_LOWER -> TEX_LOWER_BAKED
            AvatarBaker.BAKE_EYES -> TEX_EYES_BAKED
            AvatarBaker.BAKE_SKIRT -> TEX_SKIRT_BAKED
            AvatarBaker.BAKE_HAIR -> TEX_HAIR_BAKED
            AvatarBaker.BAKE_LEFTARM -> TEX_LEFTARM_BAKED
            AvatarBaker.BAKE_LEFTLEG -> TEX_LEFTLEG_BAKED
            AvatarBaker.BAKE_AUX1 -> TEX_AUX1_BAKED
            AvatarBaker.BAKE_AUX2 -> TEX_AUX2_BAKED
            AvatarBaker.BAKE_AUX3 -> TEX_AUX3_BAKED
            else -> channel + TEX_HEAD_BAKED
        }
    }
    
    /**
     * Request a rebake of all textures.
     * Use this when wearables change.
     */
    fun requestRebake() {
        scope.launch {
            sendAppearanceUpdate()
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

/**
 * Represents a wearable entry for AgentIsNowWearing.
 */
data class WearableEntry(
    val itemId: UUID,
    val wearableType: WearableType
)
