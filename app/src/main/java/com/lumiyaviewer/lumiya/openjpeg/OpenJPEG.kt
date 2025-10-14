package com.lumiyaviewer.lumiya.openjpeg

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.opengl.ETC1
import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLES30
import android.os.Build
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.GLTexture
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Modern Kotlin OpenJPEG texture decoder
 * Handles JPEG2000, TGA, KTX2, and raw texture formats with native code
 */
class OpenJPEG : GLTexture {
    var width: Int = 0
    var height: Int = 0
    var numComponents: Int = 0
    var numExtraComponents: Int = 0
    var bytesPerPixel: Int = 0
    var errorCode: Int = 0

    private var rawBuffer: ByteBuffer? = null
    private var mmapped: Boolean = false
    private var mmappedAddr: Long = 0
    private var mmappedSize: Long = 0

    /**
     * Supported image formats
     */
    enum class ImageFormat {
        Raw,
        JPEG2000,
        TGA,
        KTX2, // Modern texture format with Basis Universal compression
    }

    companion object {
        private const val ETC1_BYTES_PER_PIXEL = 888

        init {
            System.loadLibrary("openjpeg")

            // Initialize Basis Universal transcoder for KTX2 support
            try {
                // This will be implemented in the native code
                // initBasisTranscoder() // Called lazily when needed
            } catch (e: UnsatisfiedLinkError) {
                Debug.Warning(e) // Log but don't fail - KTX2 support will be unavailable
            }
        }

        /**
         * Auto-detect texture format from data
         */
        @JvmStatic
        fun detectTextureFormat(data: ByteArray?): ImageFormat {
            if (data == null || data.size < 12) {
                return ImageFormat.JPEG2000 // Default fallback
            }

            // Check for KTX2 magic bytes first
            if (data[0] == 0xAB.toByte() && data[1] == 0x4B.toByte() &&
                data[2] == 0x54.toByte() && data[3] == 0x58.toByte() &&
                data[4] == 0x20.toByte() && data[5] == 0x32.toByte() &&
                data[6] == 0x30.toByte() && data[7] == 0xBB.toByte() &&
                data[8] == 0x0D.toByte() && data[9] == 0x0A.toByte() &&
                data[10] == 0x1A.toByte() && data[11] == 0x0A.toByte()
            ) {
                return ImageFormat.KTX2
            }

            // Check for JPEG2000 magic bytes (JP2 format)
            if (data.size >= 8 && data[0] == 0x00.toByte() && data[1] == 0x00.toByte() &&
                data[2] == 0x00.toByte() && data[3] == 0x0C.toByte() &&
                data[4] == 0x6A.toByte() && data[5] == 0x50.toByte() &&
                data[6] == 0x20.toByte() && data[7] == 0x20.toByte()
            ) {
                return ImageFormat.JPEG2000
            }

            // Check for TGA format (basic detection)
            if (data.size >= 18) {
                val imageType = data[2]
                if (imageType == 2.toByte() || imageType == 3.toByte() ||
                    imageType == 10.toByte() || imageType == 11.toByte()
                ) {
                    return ImageFormat.TGA
                }
            }

            // Default to JPEG2000 for backward compatibility
            return ImageFormat.JPEG2000
        }

        // Static native methods
        @JvmStatic external fun applyFlexibleMorph(
            dst: ByteBuffer,
            src: ByteBuffer,
            count: Int,
            weights: FloatArray,
        )

        @JvmStatic external fun applyMeshMorph(
            weight: Float,
            positions: ByteBuffer,
            normals: ByteBuffer,
            vertexCount: Int,
            morphPositions: ByteBuffer,
            morphNormals: ByteBuffer,
            morphTexCoords: ByteBuffer,
            morphVertexCount: Int,
            positionOffset: Int,
            normalOffset: Int,
            output: ByteBuffer,
        )

        @JvmStatic external fun applyMorphingTransform(
            count: Int,
            positions: ByteBuffer,
            normals: ByteBuffer,
            output: ByteBuffer,
            indices: IntArray,
            weights: FloatArray,
        )

        @JvmStatic external fun applyRiggedMeshMorph(
            vertices: ByteBuffer,
            vertexCount: Int,
            boneMatrices: FloatArray,
            boneWeights: FloatArray,
            boneIndices: ByteBuffer,
            output: ByteBuffer,
            stride: Int,
        )

        @JvmStatic
        fun bakeTerrain(
            width: Int,
            height: Int,
            textures: Array<OpenJPEG?>,
            blendFactors: FloatArray,
            startX: Int,
            startY: Int,
        ): OpenJPEG {
            val result = OpenJPEG(width, height, 3, 2, 0, 0)

            val buffers = Array<ByteBuffer?>(textures.size) { textures[it]?.rawBuffer }
            val widths = IntArray(textures.size) { textures[it]?.width ?: 0 }
            val heights = IntArray(textures.size) { textures[it]?.height ?: 0 }
            val components = IntArray(textures.size) { textures[it]?.numComponents ?: 0 }

            result.bakeTerrainRaw(
                result.rawBuffer!!, width, height, buffers, widths,
                heights, components, blendFactors, startX, startY,
            )

            return result
        }

        @JvmStatic external fun calcFlexiSections(
            output: FloatArray,
            sections: Int,
            xScale: FloatArray,
            yScale: FloatArray,
            scaleCount: Int,
            twist: Float,
            topShearX: Float,
            topShearY: Float,
            taperX: Float,
            taperY: Float,
            radiusOffset: Float,
            revolutions: Float,
            skew: Float,
            pathCut: Float,
            pathCutEnd: Float,
            hollow: Boolean,
        )

        @JvmStatic external fun checkFrustrumOcclusion(
            mvpMatrix: FloatArray,
            bounds: FloatArray,
            centerX: Float,
            centerY: Float,
            centerZ: Float,
        ): Int

        @JvmStatic external fun getFlexiDataSize(sections: Int): Int

        @JvmStatic external fun meshPrepareInfluenceBuffer(
            vertices: ByteBuffer,
            vertexCount: Int,
            influences: ByteBuffer,
            stride: Int,
        )

        @JvmStatic external fun meshPrepareSeparateInfluenceBuffer(
            vertices: ByteBuffer,
            vertexCount: Int,
            boneIndices: ByteBuffer,
            boneWeights: ByteBuffer,
            stride: Int,
        )
    }

    // Constructors

    /**
     * Creates a new empty texture with specified dimensions
     */
    @Throws(OutOfMemoryError::class)
    constructor(
        width: Int,
        height: Int,
        numComponents: Int,
        bytesPerPixel: Int,
        numExtraComponents: Int,
        flags: Int,
    ) {
        this.width = width
        this.height = height
        this.numComponents = numComponents
        this.numExtraComponents = numExtraComponents
        this.bytesPerPixel = bytesPerPixel

        this.rawBuffer = allocateNew(
            width, height, numComponents, bytesPerPixel, numExtraComponents, flags,
        ) ?: throw OutOfMemoryError("allocateNew() returned NULL")

        TextureMemoryTracker.allocOpenJpegMemory(rawBuffer!!.capacity(), mmapped)
    }

    /**
     * Loads a texture from a file
     */
    @Throws(IOException::class)
    constructor(file: File, maxWidth: Int, maxHeight: Int, useMipMaps: Boolean) {
        if (!file.exists()) {
            throw IOException("File does not exist: ${file.absolutePath}")
        }

        rawBuffer = decompress(file.absolutePath, 0, 0, useMipMaps, maxWidth, maxHeight)
            ?: throw IOException("Failed to decompress texture ($errorCode) ${file.absolutePath}")

        TextureMemoryTracker.allocOpenJpegMemory(rawBuffer!!.capacity(), mmapped)
    }

    /**
     * Loads a texture from a file with texture class and format
     */
    @Throws(IOException::class)
    constructor(
        file: File,
        textureClass: TextureClass,
        imageFormat: ImageFormat,
        compressedTextures: Boolean,
    ) {
        if (!file.exists()) {
            throw IOException("File does not exist: ${file.absolutePath}")
        }

        Debug.Log("OpenJPEG: decompressing ${file.name} class $textureClass format $imageFormat")

        val isPrim = textureClass == TextureClass.Prim

        rawBuffer = when (imageFormat) {
            ImageFormat.JPEG2000 -> {
                val discardLevel = if (!isPrim && compressedTextures) 1 else 0
                val components = if (!isPrim && compressedTextures) 6 else 0
                decompress(
                    file.absolutePath, discardLevel, components,
                    isPrim, 0, 0,
                )
            }
            ImageFormat.Raw -> {
                readRaw(file.absolutePath)
            }
            ImageFormat.TGA -> {
                throw IOException("TGA not supported for non-asset files")
            }
            ImageFormat.KTX2 -> {
                throw IOException("KTX2 not supported for file-based loading yet")
            }
        } ?: throw IOException("Failed to load texture ${file.absolutePath}")

        TextureMemoryTracker.allocOpenJpegMemory(rawBuffer!!.capacity(), mmapped)
    }

    /**
     * Loads a texture from an input stream
     */
    @Throws(IOException::class)
    constructor(
        inputStream: InputStream,
        imageFormat: ImageFormat,
        flipVertical: Boolean,
        premultiplyAlpha: Boolean,
        gammaR: Float,
        gammaG: Float,
        useMipMaps: Boolean,
    ) {
        when (imageFormat) {
            ImageFormat.TGA -> {
                val data = inputStream.readBytes()
                rawBuffer = decompressTGA(
                    data, flipVertical, premultiplyAlpha,
                    gammaR, gammaG, useMipMaps,
                ) ?: throw IOException("Failed to decompress TGA texture")

                TextureMemoryTracker.allocOpenJpegMemory(rawBuffer!!.capacity(), mmapped)
            }

            ImageFormat.KTX2 -> {
                // Initialize Basis Universal transcoder if needed
                if (!initBasisTranscoder()) {
                    throw IOException("Failed to initialize Basis Universal transcoder")
                }

                val ktx2Data = inputStream.readBytes()

                // Verify it's actually KTX2 format
                if (!isKTX2Format(ktx2Data)) {
                    throw IOException("Data is not in KTX2 format")
                }

                // Get dimensions
                val dimensions =
                    getKTX2Dimensions(ktx2Data)
                        ?: throw IOException("Failed to get KTX2 texture dimensions")

                if (dimensions.size != 3) {
                    throw IOException("Invalid KTX2 dimensions")
                }

                width = dimensions[0]
                height = dimensions[1]
                // dimensions[2] is mip levels - could be used later

                // Decompress with optimal format (RGBA32 for compatibility)
                rawBuffer = decompressKTX2(ktx2Data, 3) // 3 = RGBA32
                    ?: throw IOException("Failed to decompress KTX2 texture")

                // Set texture properties for RGBA32 format
                numComponents = 4 // RGBA
                bytesPerPixel = 4 // 4 bytes per pixel
                errorCode = 0

                TextureMemoryTracker.allocOpenJpegMemory(rawBuffer!!.capacity(), mmapped)
            }

            else -> {
                throw IOException("Unsupported format for image stream: $imageFormat")
            }
        }
    }

    // Native methods
    private external fun allocateNew(
        width: Int,
        height: Int,
        numComponents: Int,
        bytesPerPixel: Int,
        numExtraComponents: Int,
        flags: Int,
    ): ByteBuffer?

    private external fun allocateRaw(size: Int): ByteBuffer?

    private external fun decompress(
        path: String,
        discardLevel: Int,
        components: Int,
        useMipMaps: Boolean,
        maxWidth: Int,
        maxHeight: Int,
    ): ByteBuffer?

    private external fun decompressTGA(
        data: ByteArray,
        flipVertical: Boolean,
        premultiplyAlpha: Boolean,
        gammaR: Float,
        gammaG: Float,
        useMipMaps: Boolean,
    ): ByteBuffer?

    private external fun readRaw(path: String): ByteBuffer?

    private external fun release(buffer: ByteBuffer)

    private external fun writeJPEG2K(
        path: String,
        buffer: ByteBuffer,
        width: Int,
        height: Int,
        numComponents: Int,
        numExtraComponents: Int,
    ): Int

    private external fun writeRaw(
        buffer: ByteBuffer,
        path: String,
    )

    private external fun drawBuf(
        dstBuffer: ByteBuffer,
        dstWidth: Int,
        dstHeight: Int,
        dstComponents: Int,
        srcBuffer: ByteBuffer,
        srcWidth: Int,
        srcHeight: Int,
        srcComponents: Int,
        offset: Int,
        tile: Boolean,
        blendAlpha: Boolean,
        flipVertical: Boolean,
        isBump: Boolean,
    )

    private external fun setComponentBuf(
        buffer: ByteBuffer,
        width: Int,
        height: Int,
        numComponents: Int,
        numExtraComponents: Int,
        component: Int,
        value: Byte,
    )

    private external fun bakeTerrainRaw(
        output: ByteBuffer,
        width: Int,
        height: Int,
        textures: Array<ByteBuffer?>,
        widths: IntArray,
        heights: IntArray,
        components: IntArray,
        blendFactors: FloatArray,
        startX: Int,
        startY: Int,
    )

    // KTX2/Basis Universal transcoding methods
    private external fun initBasisTranscoder(): Boolean

    private external fun decompressKTX2(
        ktx2Data: ByteArray,
        targetFormat: Int,
    ): ByteBuffer?

    private external fun getKTX2Dimensions(ktx2Data: ByteArray): IntArray?

    private external fun isKTX2Format(data: ByteArray): Boolean

    // Public methods

    /**
     * Compresses texture to ETC1 format
     */
    @Throws(IOException::class)
    fun compressETC1(): Boolean {
        if (Build.VERSION.SDK_INT < 8 || rawBuffer == null ||
            numComponents != 3 || numExtraComponents != 0 ||
            (bytesPerPixel != 2 && bytesPerPixel != 3)
        ) {
            return false
        }

        val encodedSize = ETC1.getEncodedDataSize(width, height)
        val compressed =
            allocateRaw(encodedSize)
                ?: throw IOException("Out of memory for $encodedSize allocation")

        ETC1.encodeImage(
            rawBuffer,
            width,
            height,
            bytesPerPixel,
            width * bytesPerPixel,
            compressed,
        )

        TextureMemoryTracker.releaseOpenJpegMemory(rawBuffer!!.capacity(), mmapped)
        release(rawBuffer!!)
        rawBuffer = compressed
        TextureMemoryTracker.allocOpenJpegMemory(compressed.capacity(), mmapped)
        bytesPerPixel = ETC1_BYTES_PER_PIXEL

        return true
    }

    /**
     * Saves texture as JPEG2000
     */
    @Throws(IOException::class)
    fun saveJPEG2K(file: File) {
        rawBuffer?.let { buffer ->
            if (writeJPEG2K(
                    file.absolutePath, buffer, width, height,
                    numComponents, numExtraComponents,
                ) != 0
            ) {
                throw IOException("Failed to save JPEG2k to ${file.absolutePath}")
            }
        }
    }

    /**
     * Saves texture as raw data
     */
    fun saveRaw(file: File) {
        rawBuffer?.let { buffer ->
            writeRaw(buffer, file.absolutePath)
        }
    }

    /**
     * Saves texture to file
     */
    fun saveToFile(file: File) {
        try {
            FileOutputStream(file, false).use { fos ->
                rawBuffer?.let { fos.channel.write(it) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Sets texture as immutable OpenGL texture (API 18+)
     */
    @TargetApi(18)
    fun setAsImmutableTexture(): Int {
        rawBuffer?.let { buffer ->
            if (bytesPerPixel == ETC1_BYTES_PER_PIXEL) {
                GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_COMPRESSED_RGB8_ETC2, width, height)
                GLES30.glCompressedTexSubImage2D(
                    GLES30.GL_TEXTURE_2D, 0, 0, 0, width, height,
                    GLES30.GL_COMPRESSED_RGB8_ETC2, buffer.capacity(), buffer,
                )
            } else {
                val (internalFormat, format, type) =
                    when (numComponents) {
                        1 -> Triple(GLES30.GL_R8, GLES30.GL_RED, GLES30.GL_UNSIGNED_BYTE)
                        3 ->
                            if (bytesPerPixel == 2) {
                                Triple(GLES30.GL_RGB565, GLES30.GL_RGB, GLES30.GL_UNSIGNED_SHORT_5_6_5)
                            } else {
                                Triple(GLES30.GL_RGB8, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE)
                            }
                        4 -> Triple(GLES30.GL_RGBA8, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE)
                        else -> return setAsTexture()
                    }

                GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, internalFormat, width, height)
                GLES30.glTexSubImage2D(
                    GLES30.GL_TEXTURE_2D, 0, 0, 0, width, height,
                    format, type, buffer,
                )

                if (numComponents == 1) {
                    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_SWIZZLE_R, GLES30.GL_ONE)
                    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_SWIZZLE_G, GLES30.GL_ONE)
                    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_SWIZZLE_B, GLES30.GL_ONE)
                    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_SWIZZLE_A, GLES30.GL_RED)
                }
            }
        }
        return loadedSize
    }

    /**
     * Sets texture as OpenGL texture
     */
    override fun setAsTexture(): Int {
        rawBuffer?.let { buffer ->
            if (bytesPerPixel == ETC1_BYTES_PER_PIXEL) {
                GLES10.glCompressedTexImage2D(
                    GLES10.GL_TEXTURE_2D,
                    0,
                    ETC1.ETC1_RGB8_OES,
                    width,
                    height,
                    0,
                    buffer.capacity(),
                    buffer,
                )
            } else {
                val format =
                    when (numComponents) {
                        1 -> GLES10.GL_LUMINANCE
                        3 -> GLES10.GL_RGB
                        4 -> GLES10.GL_RGBA
                        else -> numComponents
                    }

                val type =
                    if (bytesPerPixel == 2 && numComponents == 3) {
                        GLES10.GL_UNSIGNED_SHORT_5_6_5
                    } else {
                        GLES10.GL_UNSIGNED_BYTE
                    }

                GLES10.glTexImage2D(
                    GLES10.GL_TEXTURE_2D, 0, format, width, height,
                    0, format, type, buffer,
                )
            }
        }
        return loadedSize
    }

    /**
     * Sets texture as OpenGL texture for specific target
     */
    fun setAsTextureTarget(target: Int): Int {
        rawBuffer?.let { buffer ->
            if (bytesPerPixel == ETC1_BYTES_PER_PIXEL) {
                val capacity = buffer.capacity()
                GLES20.glCompressedTexImage2D(
                    target,
                    0,
                    ETC1.ETC1_RGB8_OES,
                    width,
                    height,
                    0,
                    capacity,
                    buffer,
                )
                return capacity
            } else {
                val format =
                    when (numComponents) {
                        1 -> GLES20.GL_LUMINANCE
                        3 -> GLES20.GL_RGB
                        4 -> GLES20.GL_RGBA
                        else -> numComponents
                    }

                val type =
                    if (bytesPerPixel == 2 && numComponents == 3) {
                        GLES20.GL_UNSIGNED_SHORT_5_6_5
                    } else {
                        GLES20.GL_UNSIGNED_BYTE
                    }

                GLES20.glTexImage2D(
                    target, 0, format, width, height,
                    0, format, type, buffer,
                )
                return width * height * bytesPerPixel
            }
        }
        return 0
    }

    /**
     * Blends alpha channel from another texture
     */
    fun blendAlpha(
        source: OpenJPEG,
        flipVertical: Boolean,
    ) {
        if (rawBuffer != null && source.rawBuffer != null &&
            numComponents >= 4 && source.numComponents >= 4
        ) {
            drawBuf(
                rawBuffer!!, width, height, numComponents,
                source.rawBuffer!!, source.width, source.height, source.numComponents,
                0, false, true, flipVertical, false,
            )
        }
    }

    /**
     * Draws another texture onto this one
     */
    fun draw(
        source: OpenJPEG,
        offset: Int,
        tile: Boolean,
    ) {
        if (rawBuffer != null && source.rawBuffer != null) {
            drawBuf(
                rawBuffer!!, width, height, numComponents,
                source.rawBuffer!!, source.width, source.height, source.numComponents,
                offset, tile, false, false, false,
            )
        }
    }

    /**
     * Draws bump map from another texture
     */
    fun drawBump(
        source: OpenJPEG,
        offset: Int,
        tile: Boolean,
        flipVertical: Boolean,
    ) {
        if (rawBuffer != null && source.rawBuffer != null &&
            numExtraComponents >= 1 && source.numComponents >= 4
        ) {
            drawBuf(
                rawBuffer!!, width, height, numComponents,
                source.rawBuffer!!, source.width, source.height, source.numComponents,
                0, false, false, flipVertical, true,
            )
        }
    }

    /**
     * Converts texture to Android Bitmap
     */
    fun getAsBitmap(): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) ?: return null

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel =
                    if (numComponents == 1) {
                        val gray = getByte((width * y + x) * numComponents).toInt() and 0xFF
                        (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
                    } else {
                        val r = getByte((width * y + x) * numComponents + 0).toInt() and 0xFF
                        val g = getByte((width * y + x) * numComponents + 1).toInt() and 0xFF
                        val b = getByte((width * y + x) * numComponents + 2).toInt() and 0xFF
                        val a =
                            if (numComponents >= 4) {
                                getByte((width * y + x) * numComponents + 3).toInt() and 0xFF
                            } else {
                                255
                            }
                        (a shl 24) or (r shl 16) or (g shl 8) or b
                    }
                bitmap.setPixel(x, height - 1 - y, pixel)
            }
        }

        return bitmap
    }

    /**
     * Gets a byte from the texture buffer
     */
    fun getByte(index: Int): Byte {
        return rawBuffer?.get(index) ?: 0
    }

    /**
     * Gets extra components as Bitmap
     */
    fun getExtraAsBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel =
                    if (numExtraComponents == 1) {
                        val gray = getByte(width * height * numComponents + width * y + x).toInt() and 0xFF
                        (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
                    } else {
                        0
                    }
                bitmap.setPixel(x, height - 1 - y, pixel)
            }
        }

        return bitmap
    }

    /**
     * Gets extra components buffer
     */
    fun getExtraComponentsBuffer(): ByteBuffer? {
        if (numExtraComponents == 0 || rawBuffer == null) return null

        val buffer = rawBuffer!!.asReadOnlyBuffer()
        val offset = width * height * numComponents

        if (offset >= 0 && offset <= buffer.limit()) {
            buffer.position(offset)
            return buffer
        }

        return null
    }

    /**
     * Gets RGB value at index
     */
    fun getRGB(index: Int): Int {
        return rawBuffer?.let { buffer ->
            ((buffer.get(index).toInt() shl 16) and 0xFF0000) or
                ((buffer.get(index + 1).toInt() shl 8) and 0xFF00) or
                (buffer.get(index + 2).toInt() and 0xFF)
        } ?: 0
    }

    /**
     * Puts a pixel row
     */
    fun putPixelRow(
        row: Int,
        pixels: IntArray,
        count: Int,
    ) {
        rawBuffer?.let { buffer ->
            var offset = width * numComponents * row

            when (numComponents) {
                3 -> {
                    for (i in 0 until count) {
                        val pixel = pixels[i]
                        buffer.put(offset++, (pixel shr 16).toByte())
                        buffer.put(offset++, (pixel shr 8).toByte())
                        buffer.put(offset++, pixel.toByte())
                    }
                }
                4 -> {
                    for (i in 0 until count) {
                        val pixel = pixels[i]
                        buffer.put(offset++, (pixel shr 16).toByte())
                        buffer.put(offset++, (pixel shr 8).toByte())
                        buffer.put(offset++, pixel.toByte())
                        buffer.put(offset++, (pixel shr 24).toByte())
                    }
                }
            }
        }
    }

    /**
     * Sets a component value for all pixels
     */
    fun setComponent(
        component: Int,
        value: Byte,
    ) {
        rawBuffer?.let { buffer ->
            setComponentBuf(
                buffer,
                width,
                height,
                numComponents,
                numExtraComponents,
                component,
                value,
            )
        }
    }

    // GLTexture interface implementation
    override fun getWidth(): Int = width

    override fun getHeight(): Int = height

    override fun getNumComponents(): Int = numComponents

    override fun hasAlphaLayer(): Boolean = bytesPerPixel != ETC1_BYTES_PER_PIXEL && (numComponents >= 4 || numComponents == 1)

    override fun getLoadedSize(): Int {
        return rawBuffer?.let { buffer ->
            when {
                bytesPerPixel == ETC1_BYTES_PER_PIXEL -> buffer.capacity()
                bytesPerPixel == 3 && numComponents == 3 -> width * height * (bytesPerPixel + 1)
                else -> width * height * bytesPerPixel
            }
        } ?: 0
    }

    /**
     * Cleanup when object is finalized
     */
    protected fun finalize() {
        rawBuffer?.let { buffer ->
            TextureMemoryTracker.releaseOpenJpegMemory(buffer.capacity(), mmapped)
            release(buffer)
            rawBuffer = null
        }
    }
}
