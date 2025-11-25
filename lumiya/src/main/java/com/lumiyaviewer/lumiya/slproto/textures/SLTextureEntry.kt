package com.lumiyaviewer.lumiya.slproto.textures

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.utils.InternPool
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class SLTextureEntry {
    private val faceMask: Int
    private val defaultTexture: SLTextureEntryFace
    private val faceTextures: Array<SLTextureEntryFace?>
    private val hashValue: Int

    private constructor(defaultTexture: SLTextureEntryFace, faceTextures: Array<SLTextureEntryFace?>) {
        this.defaultTexture = defaultTexture
        this.faceTextures = faceTextures
        var mask = 0
        for (i in faceTextures.indices) {
            if (faceTextures[i] != null) {
                mask = mask or (1 shl i)
            }
        }
        this.faceMask = mask
        this.hashValue = calculateHashValue()
    }

    private constructor(byteBuffer: ByteBuffer) {
        val mutableDefault = MutableSLTextureEntryFace(-1)
        if (byteBuffer.limit() - byteBuffer.position() < 16) {
            this.defaultTexture = SLTextureEntryFace.create(mutableDefault)!!
            this.faceTextures = emptyFaces
            this.faceMask = 0
            this.hashValue = calculateHashValue()
            return
        }

        val mutableFaces = arrayOfNulls<MutableSLTextureEntryFace>(MAX_FACES)
        val faceBitfield = IntArray(1)
        
        // Default Texture ID
        mutableDefault.setTextureID(getUUID(byteBuffer))
        
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val uuid = getUUID(byteBuffer)
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setTextureID(uuid)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setRGBA(byteBuffer.getInt())
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val rgba = byteBuffer.getInt()
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setRGBA(rgba)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setRepeatU(byteBuffer.getFloat())
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val repeatU = byteBuffer.getFloat()
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setRepeatU(repeatU)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setRepeatV(byteBuffer.getFloat())
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val repeatV = byteBuffer.getFloat()
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setRepeatV(repeatV)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setOffsetU(getOffset(byteBuffer))
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val offsetU = getOffset(byteBuffer)
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setOffsetU(offsetU)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setOffsetV(getOffset(byteBuffer))
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val offsetV = getOffset(byteBuffer)
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setOffsetV(offsetV)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setRotation(getRotation(byteBuffer))
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val rotation = getRotation(byteBuffer)
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setRotation(rotation)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setMaterial(byteBuffer.get())
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val material = byteBuffer.get()
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setMaterial(material)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setMedia(byteBuffer.get())
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val media = byteBuffer.get()
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setMedia(media)
                }
                bit = bit shl 1
            }
        }

        mutableDefault.setGlow(getGlow(byteBuffer))
        while (true) {
            val bitfield = readFaceBitfield(byteBuffer, faceBitfield)
            if (bitfield == 0) break
            val glow = getGlow(byteBuffer)
            var bit = 1
            for (i in 0 until faceBitfield[0]) {
                if ((bitfield and bit) != 0) {
                    createMutableFace(mutableFaces, i).setGlow(glow)
                }
                bit = bit shl 1
            }
        }

        this.defaultTexture = SLTextureEntryFace.create(mutableDefault)!!
        
        var maxFaceIndex = 0
        for (i in mutableFaces.indices) {
            if (mutableFaces[i] != null) maxFaceIndex = i + 1
        }
        
        if (maxFaceIndex == 0) {
            this.faceTextures = emptyFaces
        } else {
            this.faceTextures = arrayOfNulls(maxFaceIndex)
            for (i in 0 until maxFaceIndex) {
                this.faceTextures[i] = SLTextureEntryFace.create(mutableFaces[i])
            }
        }
        
        var mask = 0
        for (i in faceTextures.indices) {
            if (faceTextures[i] != null) {
                mask = mask or (1 shl i)
            }
        }
        this.faceMask = mask
        this.hashValue = calculateHashValue()
    }

    private fun createMutableFace(faces: Array<MutableSLTextureEntryFace?>, index: Int): MutableSLTextureEntryFace {
        if (index >= MAX_FACES) return MutableSLTextureEntryFace(0)
        if (faces[index] == null) {
            faces[index] = MutableSLTextureEntryFace(0)
        }
        return faces[index]!!
    }

    private fun readFaceBitfield(byteBuffer: ByteBuffer, countOut: IntArray): Int {
        countOut[0] = 0
        if (byteBuffer.position() >= byteBuffer.limit()) return 0
        var result = 0
        var shift = 0
        var b: Int
        do {
            b = byteBuffer.get().toInt()
            result = result or ((b and 0x7F) shl shift)
            shift += 7
            countOut[0] += 7
        } while ((b and 0x80) != 0)
        return result
    }

    private fun getUUID(byteBuffer: ByteBuffer): UUID? {
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        val msb = byteBuffer.long
        val lsb = byteBuffer.long
        val uuid = UUID(msb, lsb)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return UUIDPool.getUUID(uuid.toString())
    }
    
    private fun getOffset(byteBuffer: ByteBuffer): Float {
        return byteBuffer.short.toFloat() / 32767.0f
    }
    
    private fun getRotation(byteBuffer: ByteBuffer): Float {
        return (byteBuffer.short.toFloat() / 32767.0f) * 2.0f * Math.PI.toFloat()
    }
    
    private fun getGlow(byteBuffer: ByteBuffer): Float {
        return (byteBuffer.get().toInt() and 0xFF).toFloat() / 255.0f
    }

    private fun calculateHashValue(): Int {
        var result = faceTextures.size + faceMask + defaultTexture.hashCode()
        var mask = 1
        for (face in faceTextures) {
            if ((faceMask and mask) != 0 && face != null) {
                result += face.hashCode()
            }
            mask = mask shl 1
        }
        return result
    }

    fun GetDefaultTexture(): SLTextureEntryFace = defaultTexture
    
    fun GetFace(index: Int): SLTextureEntryFace? {
        if (index >= MAX_FACES) return null
        if (index >= faceTextures.size) return defaultTexture
        return faceTextures[index] ?: defaultTexture
    }
    
    fun getFaceMask(): Int = faceMask
    
    fun isSingleFace(): Boolean = faceMask == 0

    companion object {
        const val MAX_FACES = 32
        private val emptyFaces = arrayOfNulls<SLTextureEntryFace>(0)
        private val pool = InternPool<SLTextureEntry>()

        fun create(defaultTexture: SLTextureEntryFace, faceTextures: Array<SLTextureEntryFace?>): SLTextureEntry {
            return pool.intern(SLTextureEntry(defaultTexture, faceTextures))
        }

        fun create(byteBuffer: ByteBuffer, ignored: Int): SLTextureEntry {
             return pool.intern(SLTextureEntry(byteBuffer))
        }
    }
}
