package com.lumiyaviewer.lumiya.openjpeg

import android.graphics.Bitmap
import com.lumiyaviewer.lumiya.render.GLTexture
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

class OpenJPEG : GLTexture {
    var width: Int = 0
    var height: Int = 0
    var numComponents: Int = 0
    var numExtraComponents: Int = 0
    var bytesPerPixel: Int = 0
    var errorCode: Int = 0

    private var rawBuffer: ByteBuffer? = null
    private var mmapped: Boolean = false
    private var loadedSize: Int = 0

    constructor(width: Int, height: Int, numComponents: Int, bytesPerPixel: Int, numExtraComponents: Int, flags: Int) {
        this.width = width
        this.height = height
        this.numComponents = numComponents
        this.bytesPerPixel = bytesPerPixel
        this.numExtraComponents = numExtraComponents
        // allocateNew would be native
    }

    override fun getWidth(): Int = width
    override fun getHeight(): Int = height
    override fun getNumComponents(): Int = numComponents
    override fun hasAlphaLayer(): Boolean = numComponents >= 4
    override fun getLoadedSize(): Int = loadedSize
    override fun setAsTexture(): Int {
        // Stub
        return 0
    }

    fun getAsBitmap(): Bitmap? {
        // Stub
        return null
    }

    companion object {
        // Stub static methods
        fun detectTextureFormat(data: ByteArray?): ImageFormat {
            return ImageFormat.JPEG2000
        }
    }

    enum class ImageFormat {
        Raw,
        JPEG2000,
        TGA,
        KTX2
    }
}
