package com.linkpoint.protocol.textures

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Parses Second Life TextureEntry binary data to extract texture UUIDs.
 * 
 * Based on the reference viewer's SLTextureEntry implementation and the official SL viewer.
 * TextureEntry format uses a compact binary encoding with face bitfields to
 * specify which faces have overridden values vs using the default.
 * 
 * Structure:
 * - Default texture UUID (16 bytes, big-endian)
 * - Face override blocks: [bitfield][UUID]* terminated by 0
 * - RGBA colors, repeat/offset/rotation values follow similar pattern
 */
object TextureEntryParser {
    private const val TAG = "TextureEntryParser"
    private const val MAX_FACES = 32
    
    /**
     * Extract all unique texture UUIDs from a TextureEntry byte array.
     * 
     * @param data The raw TextureEntry bytes from ObjectUpdate
     * @return Set of unique texture UUIDs found in the entry
     */
    fun extractTextureIds(data: ByteArray): Set<UUID> {
        if (data.size < 16) {
            // Need at least 16 bytes for default texture UUID
            return emptySet()
        }
        
        val textureIds = mutableSetOf<UUID>()
        // TextureEntry format: UUIDs are big-endian, other fields (floats, shorts) are little-endian
        // We start with little-endian order; readUUID() switches to big-endian for UUID reading
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        try {
            // Read default texture UUID (big-endian)
            val defaultTexture = readUUID(buffer)
            if (!isNullUUID(defaultTexture)) {
                textureIds.add(defaultTexture)
            }
            
            // Read face-specific texture overrides
            while (buffer.hasRemaining()) {
                val faceBits = readFaceBitfield(buffer)
                if (faceBits == 0) break
                
                val textureId = readUUID(buffer)
                if (!isNullUUID(textureId)) {
                    textureIds.add(textureId)
                }
            }
            
            // Skip remaining fields (RGBA, repeat, offset, rotation, material, media, glow)
            // We only need texture UUIDs for downloading
            
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing TextureEntry: ${e.message}")
        }
        
        return textureIds
    }
    
    /**
     * Per-face TextureEntry properties for [parseFull]. All values are
     * already resolved to per-face: every face starts with the default
     * value, then overrides from the face bitfield blocks are applied.
     * Faces beyond the parsed count keep the default.
     */
    data class FaceProperties(
        val textureId: UUID,
        val colorR: Float,
        val colorG: Float,
        val colorB: Float,
        val colorA: Float,
        val scaleS: Float,
        val scaleT: Float,
        val offsetS: Float,
        val offsetT: Float,
        val rotation: Float,
        val materialsId: UUID
    )

    /**
     * Decode a TextureEntry blob into per-face properties for the supplied
     * [faceCount]. SL prims have at most ~32 faces; mesh-asset prims can
     * have more (each LLMesh submesh = one face).
     *
     * Format reference (LL viewer indra/llprimitive/lltextureentry.cpp,
     * unpackBinary): each property has a "default" value followed by zero
     * or more (bitfield + override-value) blocks terminated by a zero
     * bitfield. Properties are emitted in this order:
     *
     *   1. textureId          (UUID)
     *   2. color              (4 × U8 = R, G, B, A)
     *   3. scaleS             (float)
     *   4. scaleT             (float)
     *   5. offsetS            (S16 quantised, /32767 → -1..1)
     *   6. offsetT            (S16)
     *   7. rotation           (S16 → -2π..2π)
     *   8. bumpmap (skipped)  (U8)
     *   9. mediaflags         (U8) – skipped
     *  10. glow               (U8) – skipped
     *  11. materialsId        (UUID, optional)
     *
     * Returns null if the blob is malformed.
     */
    fun parseFull(data: ByteArray, faceCount: Int): List<FaceProperties>? {
        if (data.size < 16 || faceCount <= 0) return null
        val faces = MutableList(faceCount) {
            FaceProperties(
                textureId = UUID(0, 0),
                colorR = 1f, colorG = 1f, colorB = 1f, colorA = 1f,
                scaleS = 1f, scaleT = 1f,
                offsetS = 0f, offsetT = 0f,
                rotation = 0f,
                materialsId = UUID(0, 0)
            )
        }
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        try {
            // 1. Texture
            applyOverrides(buffer, faces, ::readUUID) { f, v -> f.copy(textureId = v) }
            // 2. Color (4 × U8, stored as inverse — the LL viewer XORs each
            //    byte with 0xFF on the wire). 0 over the wire = white.
            applyOverrides(buffer, faces, ::readColor) { f, v ->
                f.copy(colorR = v[0], colorG = v[1], colorB = v[2], colorA = v[3])
            }
            // 3. ScaleS, ScaleT (each a float)
            applyOverrides(buffer, faces, ::readFloat) { f, v -> f.copy(scaleS = v) }
            applyOverrides(buffer, faces, ::readFloat) { f, v -> f.copy(scaleT = v) }
            // 4. OffsetS, OffsetT (each a S16 quantised to -1..1)
            applyOverrides(buffer, faces, ::readQuantS16) { f, v -> f.copy(offsetS = v) }
            applyOverrides(buffer, faces, ::readQuantS16) { f, v -> f.copy(offsetT = v) }
            // 5. Rotation (S16 quantised to ~-2π..2π)
            applyOverrides(buffer, faces, ::readQuantRot) { f, v -> f.copy(rotation = v) }
            // 6. Skip bumpmap / mediaflags / glow (U8 each, with face overrides).
            skipU8Overrides(buffer)
            skipU8Overrides(buffer)
            skipU8Overrides(buffer)
            // 7. MaterialsID (UUID, optional)
            if (buffer.remaining() >= 16) {
                applyOverrides(buffer, faces, ::readUUID) { f, v -> f.copy(materialsId = v) }
            }
            return faces
        } catch (e: Exception) {
            Log.w(TAG, "parseFull failed at byte ${buffer.position()}: ${e.message}")
            return null
        }
    }

    /**
     * Read default value, then per-face override blocks until a zero
     * bitfield. Apply each override to the corresponding face indices.
     */
    private inline fun <V> applyOverrides(
        buffer: ByteBuffer,
        faces: MutableList<FaceProperties>,
        readValue: (ByteBuffer) -> V,
        merge: (FaceProperties, V) -> FaceProperties
    ) {
        if (!buffer.hasRemaining()) return
        val defaultValue = readValue(buffer)
        for (i in faces.indices) {
            faces[i] = merge(faces[i], defaultValue)
        }
        while (buffer.hasRemaining()) {
            val bits = readFaceBitfield(buffer)
            if (bits == 0) break
            val v = readValue(buffer)
            for (face in faces.indices) {
                if ((bits ushr face) and 1 == 1) {
                    faces[face] = merge(faces[face], v)
                }
            }
        }
    }

    private fun skipU8Overrides(buffer: ByteBuffer) {
        if (!buffer.hasRemaining()) return
        buffer.get() // default
        while (buffer.hasRemaining()) {
            val bits = readFaceBitfield(buffer)
            if (bits == 0) break
            if (buffer.hasRemaining()) buffer.get()
        }
    }

    private fun readFloat(buffer: ByteBuffer): Float = buffer.float

    private fun readColor(buffer: ByteBuffer): FloatArray {
        // Stored as 4 bytes XOR'd with 0xFF; 0xFF on the wire = 0 (black).
        val r = ((buffer.get().toInt() and 0xFF) xor 0xFF) / 255f
        val g = ((buffer.get().toInt() and 0xFF) xor 0xFF) / 255f
        val b = ((buffer.get().toInt() and 0xFF) xor 0xFF) / 255f
        val a = ((buffer.get().toInt() and 0xFF) xor 0xFF) / 255f
        return floatArrayOf(r, g, b, a)
    }

    private fun readQuantS16(buffer: ByteBuffer): Float {
        // S16 in the range -32768..32767 maps linearly to -1..1.
        return buffer.short.toInt() / 32767f
    }

    private fun readQuantRot(buffer: ByteBuffer): Float {
        // S16 maps to -2π..2π (LL viewer LLTextureEntry::rotationToBytes).
        return buffer.short.toInt() / 32767f * (2f * Math.PI.toFloat())
    }

    /**
     * Read a face bitfield from the buffer.
     *
     * The bitfield uses variable-length encoding where each byte contributes
     * 7 bits, and the high bit indicates continuation.
     */
    private fun readFaceBitfield(buffer: ByteBuffer): Int {
        if (!buffer.hasRemaining()) return 0
        
        var result = 0
        var b: Byte
        do {
            if (!buffer.hasRemaining()) return 0
            b = buffer.get()
            result = (result shl 7) or (b.toInt() and 0x7F)
        } while ((b.toInt() and 0x80) != 0)
        
        return result
    }
    
    /**
     * Read a UUID from the buffer (big-endian for UUIDs).
     */
    private fun readUUID(buffer: ByteBuffer): UUID {
        // UUIDs are stored big-endian in SL protocol
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        val msb = buffer.long
        val lsb = buffer.long
        
        buffer.order(originalOrder)
        return UUID(msb, lsb)
    }
    
    /**
     * Check if UUID is null (all zeros).
     */
    private fun isNullUUID(uuid: UUID): Boolean {
        return uuid.mostSignificantBits == 0L && uuid.leastSignificantBits == 0L
    }
    
    /**
     * Common SL "blank" texture UUIDs that should be skipped.
     * Precomputed to avoid UUID.fromString() overhead on class load.
     */
    private val SKIP_TEXTURES = setOf(
        UUID(0L, 0L),  // Null UUID
        UUID(0x5748deccf629461cL, -0x5cc962e01de0ba1L.toLong()),  // Blank (5748decc-f629-461c-9a36-a35a221fe21f)
        UUID(-0x723252b7c26836f7L.toLong(), -0x6087056b82a6f6dL.toLong()),  // Default sculpt (8dcd4a48-2d37-4909-9f78-f7a9eb4ef903)
        UUID(-0x76aa98b8db4312edL.toLong(), -0x6fd4eff8d231eca1L.toLong()),  // Default (89556747-24cb-43ed-920b-47caed15465f)
    )
    
    /**
     * Check if a texture should be downloaded (not a known system texture).
     */
    fun shouldDownload(uuid: UUID): Boolean {
        return uuid !in SKIP_TEXTURES
    }
}
