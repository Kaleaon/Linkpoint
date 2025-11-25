package com.lumiyaviewer.lumiya.slproto.textures

import com.lumiyaviewer.lumiya.utils.InternPool
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

data class SLTextureEntryFace(
    val textureID: UUID?,
    val rgba: Int,
    val repeatU: Float,
    val repeatV: Float,
    val offsetU: Float,
    val offsetV: Float,
    val rotation: Float,
    val glow: Float,
    val materialb: Byte,
    val mediab: Byte,
    val hasAttribute: Int
) {
    
    fun getGlow(defaultFace: SLTextureEntryFace): Float {
        return if ((hasAttribute and AttributeGlow) != 0) glow else defaultFace.glow
    }

    fun getMaterial(defaultFace: SLTextureEntryFace): Byte {
        return if ((hasAttribute and AttributeMaterial) != 0) materialb else defaultFace.materialb
    }

    fun getMedia(defaultFace: SLTextureEntryFace): Byte {
        return if ((hasAttribute and AttributeMedia) != 0) mediab else defaultFace.mediab
    }

    fun getOffsetU(defaultFace: SLTextureEntryFace): Float {
        return if ((hasAttribute and AttributeOffsetU) != 0) offsetU else defaultFace.offsetU
    }

    fun getOffsetV(defaultFace: SLTextureEntryFace): Float {
        return if ((hasAttribute and AttributeOffsetV) != 0) offsetV else defaultFace.offsetV
    }

    fun getRGBA(defaultFace: SLTextureEntryFace): Int {
        return if ((hasAttribute and AttributeRGBA) != 0) rgba else defaultFace.rgba
    }

    fun getRepeatU(defaultFace: SLTextureEntryFace): Float {
        return if ((hasAttribute and AttributeRepeatU) != 0) repeatU else defaultFace.repeatU
    }

    fun getRepeatV(defaultFace: SLTextureEntryFace): Float {
        return if ((hasAttribute and AttributeRepeatV) != 0) repeatV else defaultFace.repeatV
    }

    fun getRotation(defaultFace: SLTextureEntryFace): Float {
        return if ((hasAttribute and AttributeRotation) != 0) rotation else defaultFace.rotation
    }

    fun getTextureID(defaultFace: SLTextureEntryFace): UUID? {
        return if ((hasAttribute and AttributeTextureID) != 0) textureID else defaultFace.textureID
    }

    companion object {
        const val AttributeAll = -1
        const val AttributeGlow = 512
        const val AttributeMaterial = 128
        const val AttributeMedia = 256
        const val AttributeOffsetU = 16
        const val AttributeOffsetV = 32
        const val AttributeRGBA = 2
        const val AttributeRepeatU = 4
        const val AttributeRepeatV = 8
        const val AttributeRotation = 64
        const val AttributeTextureID = 1
        
        private val pool = InternPool<SLTextureEntryFace>()

        fun create(mutable: MutableSLTextureEntryFace?): SLTextureEntryFace? {
            if (mutable == null) return null
            return pool.intern(SLTextureEntryFace(
                mutable.textureID,
                mutable.rgba,
                mutable.repeatU,
                mutable.repeatV,
                mutable.offsetU,
                mutable.offsetV,
                mutable.rotation,
                mutable.glow,
                mutable.materialb,
                mutable.mediab,
                mutable.hasAttribute
            ))
        }
    }
    
    // Compatibility methods
    fun textureID() = textureID
    fun rgba() = rgba
    fun repeatU() = repeatU
    fun repeatV() = repeatV
    fun offsetU() = offsetU
    fun offsetV() = offsetV
    fun rotation() = rotation
    fun glow() = glow
    fun materialb() = materialb
    fun mediab() = mediab
}
