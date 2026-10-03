package com.linkpoint.objects

import com.linkpoint.protocol.types.LLColor4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Texture entry editor for object faces
 */
class TextureEditor {
    
    companion object {
        // Default textures
        val TEXTURE_BLANK = UUID.fromString("5748decc-f629-461c-9a36-a35a221fe21f")
        val TEXTURE_DEFAULT = UUID.fromString("89556747-24cb-43ed-920b-47caed15465f")
        val TEXTURE_MEDIA = UUID.fromString("8b5fec65-8d8d-9dc5-cda8-8fdf2716e361")
        val TEXTURE_PLYWOOD = UUID.fromString("89556747-24cb-43ed-920b-47caed15465f")
        val TEXTURE_TRANSPARENT = UUID.fromString("8dcd4a48-2d37-4909-9f78-f7a9eb4ef903")
        
        // Face constants
        const val ALL_FACES = -1
        const val MAX_FACES = 9
    }
    
    // Default texture entry
    private var defaultTexture = TEXTURE_DEFAULT
    private var defaultColor = LLColor4.white()
    private var defaultRepeatU = 1f
    private var defaultRepeatV = 1f
    private var defaultOffsetU = 0f
    private var defaultOffsetV = 0f
    private var defaultRotation = 0f
    private var defaultBump = 0
    private var defaultShiny = 0
    private var defaultFullbright = false
    private var defaultMediaFlags = 0
    private var defaultGlow = 0f
    private var defaultMaterial = UUID(0L, 0L)
    
    // Per-face data
    private val faceTextures = Array<UUID?>(MAX_FACES) { null }
    private val faceColors = Array<LLColor4?>(MAX_FACES) { null }
    private val faceRepeatU = FloatArray(MAX_FACES) { Float.NaN }
    private val faceRepeatV = FloatArray(MAX_FACES) { Float.NaN }
    private val faceOffsetU = FloatArray(MAX_FACES) { Float.NaN }
    private val faceOffsetV = FloatArray(MAX_FACES) { Float.NaN }
    private val faceRotation = FloatArray(MAX_FACES) { Float.NaN }
    private val faceBump = IntArray(MAX_FACES) { -1 }
    private val faceShiny = IntArray(MAX_FACES) { -1 }
    private val faceFullbright = BooleanArray(MAX_FACES)
    private val faceMediaFlags = IntArray(MAX_FACES) { -1 }
    private val faceGlow = FloatArray(MAX_FACES) { Float.NaN }
    private val faceMaterial = Array<UUID?>(MAX_FACES) { null }
    
    /**
     * Parse texture entry from bytes
     */
    fun parse(data: ByteArray) {
        if (data.isEmpty()) return
        
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        try {
            // Default texture
            if (buffer.remaining() >= 16) {
                val bytes = ByteArray(16)
                buffer.get(bytes)
                defaultTexture = bytesToUUID(bytes)
            }
            
            // Per-face textures
            while (buffer.remaining() > 0) {
                val faceMask = buffer.get().toInt() and 0xFF
                if (faceMask == 0) break
                
                if (buffer.remaining() >= 16) {
                    val bytes = ByteArray(16)
                    buffer.get(bytes)
                    val texture = bytesToUUID(bytes)
                    
                    for (face in 0 until MAX_FACES) {
                        if ((faceMask and (1 shl face)) != 0) {
                            faceTextures[face] = texture
                        }
                    }
                }
            }
            
            // Default color
            if (buffer.remaining() >= 4) {
                val r = (buffer.get().toInt() and 0xFF) / 255f
                val g = (buffer.get().toInt() and 0xFF) / 255f
                val b = (buffer.get().toInt() and 0xFF) / 255f
                val a = (buffer.get().toInt() and 0xFF) / 255f
                defaultColor = LLColor4(r, g, b, a)
            }
            
            // Per-face colors
            while (buffer.remaining() > 0) {
                val faceMask = buffer.get().toInt() and 0xFF
                if (faceMask == 0) break
                
                if (buffer.remaining() >= 4) {
                    val r = (buffer.get().toInt() and 0xFF) / 255f
                    val g = (buffer.get().toInt() and 0xFF) / 255f
                    val b = (buffer.get().toInt() and 0xFF) / 255f
                    val a = (buffer.get().toInt() and 0xFF) / 255f
                    val color = LLColor4(r, g, b, a)
                    
                    for (face in 0 until MAX_FACES) {
                        if ((faceMask and (1 shl face)) != 0) {
                            faceColors[face] = color
                        }
                    }
                }
            }
            
            // Default repeat U
            if (buffer.remaining() >= 4) {
                defaultRepeatU = buffer.float
            }
            
            // Per-face repeat U...
            // Continue parsing other fields similarly
            
        } catch (e: Exception) {
            // Handle parse error
        }
    }
    
    /**
     * Serialize to bytes
     */
    fun toBytes(): ByteArray {
        val buffer = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN)
        
        // Default texture
        buffer.put(uuidToBytes(defaultTexture))
        
        // Per-face textures
        for (face in 0 until MAX_FACES) {
            val texture = faceTextures[face]
            if (texture != null && texture != defaultTexture) {
                buffer.put((1 shl face).toByte())
                buffer.put(uuidToBytes(texture))
            }
        }
        buffer.put(0) // End marker
        
        // Default color
        buffer.put((defaultColor.r * 255).toInt().toByte())
        buffer.put((defaultColor.g * 255).toInt().toByte())
        buffer.put((defaultColor.b * 255).toInt().toByte())
        buffer.put((defaultColor.a * 255).toInt().toByte())
        
        // Per-face colors
        for (face in 0 until MAX_FACES) {
            val color = faceColors[face]
            if (color != null && color != defaultColor) {
                buffer.put((1 shl face).toByte())
                buffer.put((color.r * 255).toInt().toByte())
                buffer.put((color.g * 255).toInt().toByte())
                buffer.put((color.b * 255).toInt().toByte())
                buffer.put((color.a * 255).toInt().toByte())
            }
        }
        buffer.put(0) // End marker
        
        // Repeat U
        buffer.putFloat(defaultRepeatU)
        for (face in 0 until MAX_FACES) {
            if (!faceRepeatU[face].isNaN() && faceRepeatU[face] != defaultRepeatU) {
                buffer.put((1 shl face).toByte())
                buffer.putFloat(faceRepeatU[face])
            }
        }
        buffer.put(0)
        
        // Repeat V
        buffer.putFloat(defaultRepeatV)
        for (face in 0 until MAX_FACES) {
            if (!faceRepeatV[face].isNaN() && faceRepeatV[face] != defaultRepeatV) {
                buffer.put((1 shl face).toByte())
                buffer.putFloat(faceRepeatV[face])
            }
        }
        buffer.put(0)
        
        // Continue with other fields...
        
        val result = ByteArray(buffer.position())
        buffer.flip()
        buffer.get(result)
        return result
    }
    
    /**
     * Set texture for face(s)
     */
    fun setTexture(face: Int, textureId: UUID) {
        if (face == ALL_FACES) {
            defaultTexture = textureId
            for (i in 0 until MAX_FACES) {
                faceTextures[i] = null
            }
        } else if (face in 0 until MAX_FACES) {
            faceTextures[face] = textureId
        }
    }
    
    /**
     * Get texture for face
     */
    fun getTexture(face: Int): UUID {
        return if (face in 0 until MAX_FACES) {
            faceTextures[face] ?: defaultTexture
        } else {
            defaultTexture
        }
    }
    
    /**
     * Set color for face(s)
     */
    fun setColor(face: Int, color: LLColor4) {
        if (face == ALL_FACES) {
            defaultColor = color
            for (i in 0 until MAX_FACES) {
                faceColors[i] = null
            }
        } else if (face in 0 until MAX_FACES) {
            faceColors[face] = color
        }
    }
    
    /**
     * Get color for face
     */
    fun getColor(face: Int): LLColor4 {
        return if (face in 0 until MAX_FACES) {
            faceColors[face] ?: defaultColor
        } else {
            defaultColor
        }
    }
    
    /**
     * Set texture repeat
     */
    fun setRepeat(face: Int, repeatU: Float, repeatV: Float) {
        if (face == ALL_FACES) {
            defaultRepeatU = repeatU
            defaultRepeatV = repeatV
            for (i in 0 until MAX_FACES) {
                faceRepeatU[i] = Float.NaN
                faceRepeatV[i] = Float.NaN
            }
        } else if (face in 0 until MAX_FACES) {
            faceRepeatU[face] = repeatU
            faceRepeatV[face] = repeatV
        }
    }
    
    /**
     * Set texture offset
     */
    fun setOffset(face: Int, offsetU: Float, offsetV: Float) {
        if (face == ALL_FACES) {
            defaultOffsetU = offsetU
            defaultOffsetV = offsetV
            for (i in 0 until MAX_FACES) {
                faceOffsetU[i] = Float.NaN
                faceOffsetV[i] = Float.NaN
            }
        } else if (face in 0 until MAX_FACES) {
            faceOffsetU[face] = offsetU
            faceOffsetV[face] = offsetV
        }
    }
    
    /**
     * Set texture rotation
     */
    fun setRotation(face: Int, rotation: Float) {
        if (face == ALL_FACES) {
            defaultRotation = rotation
            for (i in 0 until MAX_FACES) {
                faceRotation[i] = Float.NaN
            }
        } else if (face in 0 until MAX_FACES) {
            faceRotation[face] = rotation
        }
    }
    
    /**
     * Set glow
     */
    fun setGlow(face: Int, glow: Float) {
        if (face == ALL_FACES) {
            defaultGlow = glow
            for (i in 0 until MAX_FACES) {
                faceGlow[i] = Float.NaN
            }
        } else if (face in 0 until MAX_FACES) {
            faceGlow[face] = glow
        }
    }
    
    /**
     * Set fullbright
     */
    fun setFullbright(face: Int, fullbright: Boolean) {
        if (face == ALL_FACES) {
            defaultFullbright = fullbright
            for (i in 0 until MAX_FACES) {
                faceFullbright[i] = fullbright
            }
        } else if (face in 0 until MAX_FACES) {
            faceFullbright[face] = fullbright
        }
    }
    
    /**
     * Set shiny level (0-3)
     */
    fun setShiny(face: Int, shiny: Int) {
        val level = shiny.coerceIn(0, 3)
        if (face == ALL_FACES) {
            defaultShiny = level
            for (i in 0 until MAX_FACES) {
                faceShiny[i] = -1
            }
        } else if (face in 0 until MAX_FACES) {
            faceShiny[face] = level
        }
    }
    
    /**
     * Set bump map type
     */
    fun setBump(face: Int, bump: Int) {
        if (face == ALL_FACES) {
            defaultBump = bump
            for (i in 0 until MAX_FACES) {
                faceBump[i] = -1
            }
        } else if (face in 0 until MAX_FACES) {
            faceBump[face] = bump
        }
    }
    
    /**
     * Set PBR material
     */
    fun setMaterial(face: Int, materialId: UUID) {
        if (face == ALL_FACES) {
            defaultMaterial = materialId
            for (i in 0 until MAX_FACES) {
                faceMaterial[i] = null
            }
        } else if (face in 0 until MAX_FACES) {
            faceMaterial[face] = materialId
        }
    }
    
    private fun bytesToUUID(bytes: ByteArray): UUID {
        val bb = ByteBuffer.wrap(bytes)
        return UUID(bb.long, bb.long)
    }
    
    private fun uuidToBytes(uuid: UUID): ByteArray {
        val bb = ByteBuffer.allocate(16)
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }
}
