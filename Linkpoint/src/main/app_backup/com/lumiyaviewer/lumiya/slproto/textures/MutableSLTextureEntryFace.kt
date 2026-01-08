package com.lumiyaviewer.lumiya.slproto.textures

import java.util.UUID

class MutableSLTextureEntryFace(var hasAttribute: Int) {
    var textureID: UUID? = null
    var rgba: Int = 0
    var repeatU: Float = 0f
    var repeatV: Float = 0f
    var offsetU: Float = 0f
    var offsetV: Float = 0f
    var rotation: Float = 0f
    var glow: Float = 0f
    var materialb: Byte = 0
    var mediab: Byte = 0

    fun setTextureID(uuid: UUID?) {
        this.textureID = uuid
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeTextureID
    }
    
    fun setRGBA(value: Int) {
        this.rgba = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeRGBA
    }
    
    fun setRepeatU(value: Float) {
        this.repeatU = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeRepeatU
    }
    
    fun setRepeatV(value: Float) {
        this.repeatV = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeRepeatV
    }
    
    fun setOffsetU(value: Float) {
        this.offsetU = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeOffsetU
    }
    
    fun setOffsetV(value: Float) {
        this.offsetV = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeOffsetV
    }
    
    fun setRotation(value: Float) {
        this.rotation = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeRotation
    }
    
    fun setGlow(value: Float) {
        this.glow = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeGlow
    }
    
    fun setMaterial(value: Byte) {
        this.materialb = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeMaterial
    }
    
    fun setMedia(value: Byte) {
        this.mediab = value
        hasAttribute = hasAttribute or SLTextureEntryFace.AttributeMedia
    }
}
