package com.linkpoint.linden.llrender

import com.linkpoint.linden.llimage.ImageRaw
import com.linkpoint.linden.llmath.Matrix4

enum class CubeFace(val index: Int) {
    POSITIVE_X(0), NEGATIVE_X(1),
    POSITIVE_Y(2), NEGATIVE_Y(3),
    POSITIVE_Z(4), NEGATIVE_Z(5),
}

class LLCubeMap {

    var textureId: Int = 0
        private set
    var faceSize: Int = 0
        private set
    var isLoaded: Boolean = false
        private set
    var isBound: Boolean = false
        private set

    private val faceImages: Array<ImageRaw?> = arrayOfNulls(6)

    fun initGL(faces: List<ImageRaw>): Boolean {
        require(faces.size == 6) { "Cube map requires exactly 6 face images" }
        faceSize = faces[0].width
        if (faces.any { it.width != faceSize || it.height != faceSize }) return false
        faces.forEachIndexed { i, img -> faceImages[i] = img }
        textureId = faces.fold(0) { acc, img -> acc * 31 + img.width } + 1
        isLoaded = true
        return true
    }

    fun bind() {
        if (!isLoaded) return
        isBound = true
    }

    fun unbind() {
        isBound = false
    }

    fun enable() {
        LLGLState.enable(GLCapability.BLEND)
    }

    fun disable() {
        LLGLState.disable(GLCapability.BLEND)
    }

    fun getReflectionTransform(): Matrix4 = Matrix4()

    fun getFaceImage(face: CubeFace): ImageRaw? = faceImages[face.index]

    fun release() {
        textureId = 0
        isLoaded = false
        isBound = false
        for (i in faceImages.indices) faceImages[i] = null
        faceSize = 0
    }

    override fun toString(): String = "LLCubeMap(id=$textureId, size=$faceSize, loaded=$isLoaded)"
}
