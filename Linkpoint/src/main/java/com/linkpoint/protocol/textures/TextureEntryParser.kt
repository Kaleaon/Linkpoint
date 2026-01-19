package com.linkpoint.protocol.textures

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Parses Second Life TextureEntry binary data to extract texture UUIDs.
 * 
 * Based on Lumiya's SLTextureEntry implementation and the official SL viewer.
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
