package com.linkpoint.linden.llimage

class ImageRawGL(
    var width: Int = 0,
    var height: Int = 0,
    var components: Int = 4
) {
    var textureId: Int = 0
        private set

    private var bound: Boolean = false
    private var uploaded: Boolean = false
    private var cachedData: ImageRaw? = null


    enum class TexFormat(val glValue: Int) {
        RGBA(0x1908), RGB(0x1907), LUMINANCE(0x1909), LUMINANCE_ALPHA(0x190A)
    }

    fun getFormat(): TexFormat = when (components) {
        4    -> TexFormat.RGBA
        3    -> TexFormat.RGB
        2    -> TexFormat.LUMINANCE_ALPHA
        else -> TexFormat.LUMINANCE
    }

    fun setImage(image: ImageRaw) {
        width      = image.width
        height     = image.height
        components = image.components
        cachedData = image
        uploaded   = false
    }

    fun upload(): Boolean {
        if (cachedData == null) return false
        uploaded = true
        return true
    }

    fun bind(unit: Int = 0): Boolean {
        if (!uploaded) upload()
        bound = true
        return true
    }

    fun unbind() { bound = false }

    fun isBound(): Boolean = bound
    fun isUploaded(): Boolean = uploaded
    fun isValid(): Boolean = width > 0 && height > 0
    fun getCachedImage(): ImageRaw? = cachedData

    fun free() {
        textureId = 0
        uploaded  = false
        bound     = false
        cachedData = null
    }
}
