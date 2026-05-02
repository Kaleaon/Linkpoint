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

    // ---------------- Diagnostics for end-to-end pipeline visibility -----
    // The bake → upload → AgentSetAppearance round trip happens on a
    // coroutine scope and is otherwise invisible from outside; surfacing
    // these counters (read by the debug report) is the only way to tell
    // whether the pipeline ran at all on a given session, how many
    // textures it actually managed to upload, and whether the simulator
    // ACK'd the resulting AgentSetAppearance packet.

    @Volatile
    private var lastUpdateStartedMs: Long = 0
    @Volatile
    private var lastUpdateDurationMs: Long = 0
    @Volatile
    private var lastBakedTextureCount: Int = 0
    @Volatile
    private var lastUpdateError: String? = null
    private val updatesSent = AtomicInteger(0)
    private val updatesFailed = AtomicInteger(0)

    data class DiagnosticsSnapshot(
        val updatesSent: Int,
        val updatesFailed: Int,
        val lastUpdateAgoMs: Long?,
        val lastUpdateDurationMs: Long,
        val lastBakedTextureCount: Int,
        val lastUpdateError: String?,
        val lastSerialSent: Int
    )

    fun getDiagnostics(): DiagnosticsSnapshot {
        val now = System.currentTimeMillis()
        return DiagnosticsSnapshot(
            updatesSent = updatesSent.get(),
            updatesFailed = updatesFailed.get(),
            lastUpdateAgoMs = if (lastUpdateStartedMs > 0) now - lastUpdateStartedMs else null,
            lastUpdateDurationMs = lastUpdateDurationMs,
            lastBakedTextureCount = lastBakedTextureCount,
            lastUpdateError = lastUpdateError,
            lastSerialSent = serialNum.get()
        )
    }

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
        val started = System.currentTimeMillis()
        lastUpdateStartedMs = started
        Log.i(TAG, "═══ Appearance update starting (serial+1=${serialNum.get() + 1}) ═══")

        try {
            Log.d(TAG, "  baking 11 channels (head/upper/lower/eyes/skirt/hair/leftarm/leftleg/aux1/aux2/aux3)...")
            val bakedTextures = avatarBaker.bakeAll(includeBoM = true)
            lastBakedTextureCount = bakedTextures.size
            Log.d(TAG, "  baked ${bakedTextures.size} channels: ${bakedTextures.keys.sorted().joinToString()}")

            sendAgentSetAppearance(bakedTextures)

            lastUpdateError = null
            updatesSent.incrementAndGet()
            lastUpdateDurationMs = System.currentTimeMillis() - started
            Log.i(TAG, "✓ Appearance update sent (took ${lastUpdateDurationMs}ms)")
        } catch (e: Exception) {
            lastUpdateError = "${e.javaClass.simpleName}: ${e.message}"
            updatesFailed.incrementAndGet()
            lastUpdateDurationMs = System.currentTimeMillis() - started
            Log.e(TAG, "✗ Appearance update failed after ${lastUpdateDurationMs}ms", e)
        }
    }
    
    /**
     * Send AgentSetAppearance message.
     *
     * This is the UDP message that tells the simulator what your avatar looks like.
     * Other avatars will receive this information and render you accordingly.
     * Message block fields are little-endian; UUID bytes remain big-endian.
     *
     * The VisualParam block byte order is the **first 218 params (or 251 with
     * Physics worn) iterated in numeric ParamID order** across the full
     * `avatar_lad.xml` set. This matches `cinderblocks/libremetaverse`'s Apr-30
     * 2026 fix (commit `36288373`, "Fix sorting of params for AgentSetAppearance"
     * — the previous group-0-only filter under-emitted bytes when the table
     * happened to have fewer than 218 group-0 params, leaving trailing slots
     * uninitialised). The current implementation falls back to per-param
     * `valueDefault` from the loader when no live weight is supplied; once
     * wearable-asset → weight wiring is in, replace the empty map with the
     * computed weights from `MakeParamValues` (see libremetaverse
     * `AppearanceManager.cs:1929`).
     */
    suspend fun sendAgentSetAppearance(bakedTextures: Map<Int, UUID>) {
        val serial = serialNum.incrementAndGet()

        // Field layout per secondlife/viewer scripts/messages/message_template.msg:
        //   AgentData    (Single):   AgentID LLUUID, SessionID LLUUID, SerialNum U32, Size LLVector3
        //   WearableData (Variable): CacheID LLUUID, TextureIndex U8   — CacheID FIRST
        //   ObjectData   (Single):   TextureEntry Variable 2  (U16 length prefix + bytes)
        //   VisualParam  (Variable): ParamValue U8
        //
        // The previous code wrote TextureIndex before CacheID and skipped the
        // ObjectData block entirely, which the simulator silently dropped on
        // the floor — bakes never registered against the agent.

        val wearableCount = bakedTextures.size
        val textureEntry = buildTextureEntryFromBakes(bakedTextures)
        val visualParamCount = if (com.linkpoint.avatar.VisualParamLoader.isReady) {
            // Real bound: first 251 if Physics worn, 218 otherwise. Our
            // wear-detection isn't plumbed yet, so default to 218 like
            // libremetaverse does for non-Physics avatars.
            218
        } else {
            VISUAL_PARAM_COUNT
        }
        val payloadSize =
            48 +                                    // AgentData
            1 + wearableCount * (16 + 1) +          // WearableData (count + N×(UUID + U8))
            2 + textureEntry.size +                 // ObjectData TextureEntry (U16 length + bytes)
            1 + visualParamCount                    // VisualParam

        val payload = ByteBuffer.allocate(payloadSize).order(MESSAGE_BYTE_ORDER)

        // AgentData
        payload.putUUID(agentId)
        payload.putUUID(sessionId)
        payload.putInt(serial)
        payload.putFloat(0.45f)         // Width
        payload.putFloat(0.6f)          // Depth
        payload.putFloat(avatarHeight)  // Height

        // WearableData — CacheID first, TextureIndex second (per template).
        payload.put(wearableCount.toByte())
        bakedTextures.forEach { (channel, textureId) ->
            payload.putUUID(textureId)
            payload.put(channelToTextureIndex(channel).toByte())
        }

        // ObjectData: TextureEntry (Variable 2 = U16 length prefix + bytes).
        payload.putShort(textureEntry.size.toShort())
        payload.put(textureEntry)

        // VisualParam — Variable. U8 count caps at 255; the canonical
        // SL set is 218 (251 with Physics). Bytes are emitted in
        // numeric ParamID order via the VisualParamLoader catalogue,
        // not by walking our `visualParams` ByteArray (which has no
        // mapping from index ↔ ParamID and was being shipped as 218×0x7F
        // = "every slider at the middle" regardless of what the avatar
        // actually wears). Falls back to that legacy buffer if the
        // loader hasn't populated yet so we always send something.
        val wireBytes = encodeVisualParamWireBytes()
        payload.put(wireBytes.size.toByte())
        payload.put(wireBytes)

        Log.d(TAG, "Sending AgentSetAppearance " +
            "(serial=$serial, wearables=$wearableCount, " +
            "textureEntry=${textureEntry.size}B, params=$VISUAL_PARAM_COUNT)")
        udpConnection.sendPacket(MessageIds.AGENT_SET_APPEARANCE, payload.array(), reliable = true)
    }

    /**
     * Build a minimal TextureEntry blob that places each baked texture at
     * its correct face slot (8..18 for head/upper/lower/eyes/skirt/hair/
     * leftarm/leftleg/aux1/aux2/aux3). Slots not in [bakedTextures] keep
     * the default (null) UUID.
     *
     * TextureEntry binary layout:
     *   default texture UUID (16 bytes)
     *   per-face overrides loop: face-bitfield + UUID, terminated by zero bitfield
     *   default color (4 bytes)
     *   per-face color overrides loop, terminated by zero bitfield
     *   ... same pattern for scaleS, scaleT, offsetS, offsetT, rotation,
     *       bumpmap, mediaflags, glow, materialsId
     *
     * For an avatar bake we only need the texture-UUID block populated; all
     * other property blocks are emitted as "default + zero-bitfield
     * terminator" so the parser sees a valid (if minimal) blob.
     */
    private fun buildTextureEntryFromBakes(bakedTextures: Map<Int, UUID>): ByteArray {
        // SL channel index → TextureEntry face slot.
        val faceForChannel = mapOf(
            0 to 8, 1 to 9, 2 to 10, 3 to 11, 4 to 12, 5 to 13,
            6 to 14, 7 to 15, 8 to 16, 9 to 17, 10 to 18
        )
        val out = java.io.ByteArrayOutputStream()
        val bb = ByteBuffer.allocate(16).order(MESSAGE_BYTE_ORDER)

        fun writeUuid(u: UUID) {
            bb.clear()
            bb.order(ByteOrder.BIG_ENDIAN)
            bb.putLong(u.mostSignificantBits)
            bb.putLong(u.leastSignificantBits)
            out.write(bb.array(), 0, 16)
        }
        fun writeBitfield(face: Int) {
            // Variable-length bitfield: 7 bits per byte, high bit = continuation.
            // For face indices ≤ 6 this fits in a single byte; for ≥7 we emit
            // multi-byte ULEB128-style. Matches LL viewer LLTextureEntry::pack.
            val bits = 1L shl face
            var v = bits
            while (v != 0L) {
                val low = (v and 0x7FL).toInt()
                v = v ushr 7
                val byte = if (v != 0L) (low or 0x80) else low
                out.write(byte)
            }
        }
        fun writeColorBytes(r: Int, g: Int, b: Int, a: Int) {
            // LL convention: bytes are XOR'd with 0xFF on the wire so 0 = white.
            out.write(r.xor(0xFF)); out.write(g.xor(0xFF))
            out.write(b.xor(0xFF)); out.write(a.xor(0xFF))
        }

        // 1. Texture: default = null UUID; override each baked face.
        writeUuid(UUID(0L, 0L))
        for ((channel, textureId) in bakedTextures) {
            val face = faceForChannel[channel] ?: continue
            writeBitfield(face)
            writeUuid(textureId)
        }
        out.write(0) // texture overrides terminator

        // 2. Color: default white, no overrides.
        writeColorBytes(255, 255, 255, 255)
        out.write(0)
        // 3. ScaleS, ScaleT (each a float, default 1.0).
        out.write(ByteBuffer.allocate(4).order(MESSAGE_BYTE_ORDER).putFloat(1f).array())
        out.write(0)
        out.write(ByteBuffer.allocate(4).order(MESSAGE_BYTE_ORDER).putFloat(1f).array())
        out.write(0)
        // 4. OffsetS, OffsetT (S16 default 0).
        out.write(ByteBuffer.allocate(2).order(MESSAGE_BYTE_ORDER).putShort(0).array())
        out.write(0)
        out.write(ByteBuffer.allocate(2).order(MESSAGE_BYTE_ORDER).putShort(0).array())
        out.write(0)
        // 5. Rotation (S16 default 0).
        out.write(ByteBuffer.allocate(2).order(MESSAGE_BYTE_ORDER).putShort(0).array())
        out.write(0)
        // 6. Bumpmap, mediaflags, glow (each U8 default 0).
        out.write(0); out.write(0)
        out.write(0); out.write(0)
        out.write(0); out.write(0)
        // 7. MaterialsID (UUID default null).
        writeUuid(UUID(0L, 0L))
        out.write(0)
        return out.toByteArray()
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
    
    /**
     * Live ParamID → weight overrides for the next AgentSetAppearance.
     * Empty by default: every byte falls back to per-param `valueDefault`
     * from `avatar_lad.xml`. When the wearable-asset → weight pipeline
     * lands, populate this from the active wearable assets before each
     * send (mirrors libremetaverse's `MakeParamValues` step).
     */
    @Volatile
    var visualParamWeightOverrides: Map<Int, Float> = emptyMap()

    /**
     * Encode the AgentSetAppearance VisualParam wire block.
     *
     * Strategy mirrors libremetaverse Apr-30 fix
     * (`AppearanceManager.cs:2326`):
     *   1. Iterate the catalogue in numeric ParamID order.
     *   2. Take the first 218 entries (the canonical SL count without
     *      Physics; 251 with).
     *   3. For each, use the supplied weight if present, else the
     *      per-param `valueDefault`. Quantise to 0..255 via the same
     *      `(weight - min) / (max - min)` map the receiving sim uses
     *      to invert in `Avatar.DecodeVisualParams`.
     *
     * Falls back to the legacy hand-set [visualParams] ByteArray if the
     * loader hasn't initialised yet (e.g. unit tests, race during
     * startup) — that path still ships 218 bytes so the simulator
     * accepts the packet, just with the all-mid defaults.
     */
    private fun encodeVisualParamWireBytes(): ByteArray {
        val catalog = com.linkpoint.avatar.VisualParamLoader
        if (!catalog.isReady) return visualParams
        val wire = catalog.wireOrderParams()
        if (wire.isEmpty()) return visualParams
        // SL spec: 218 bytes when not wearing Physics, 251 when worn.
        // Cap to whichever is smaller — the wire-order list size or
        // 218 — so older avatar_lad.xml files with fewer transmitted
        // params still produce a valid block.
        val n = minOf(218, wire.size)
        val out = ByteArray(n)
        val weights = visualParamWeightOverrides
        for (i in 0 until n) {
            val p = wire[i]
            val w = weights[p.id] ?: p.valueDefault
            val span = p.valueMax - p.valueMin
            val t = if (span > 1e-6f) ((w - p.valueMin) / span).coerceIn(0f, 1f) else 0f
            out[i] = (t * 255f).toInt().coerceIn(0, 255).toByte()
        }
        return out
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
