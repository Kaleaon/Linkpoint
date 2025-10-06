package com.linkpoint.openjpeg

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.opengl.ETC1
import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLES30
import android.os.Build.VERSION
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import com.linkpoint.Debug
import com.linkpoint.render.GLTexture
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.tex.TextureClass
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.Buffer
import java.nio.ByteBuffer

class OpenJPEG : GLTexture {
    /* renamed from: -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues */
    private const val /* synthetic */ Int[] syntheticField = null
    private const val ETC1_BYTES_PER_PIXEL: Int = 888
    public Int bytes_per_pixel
    public Int error_code
    public Int height
    private Boolean mmapped = false
    private Long mmappedAddr = 0
    private Long mmappedSize = 0
    public Int num_components
    public Int num_extra_components
    private ByteBuffer rawBuffer
    public Int width

    enum class ImageFormat {
        Raw,
        JPEG2000,
        TGA,
        KTX2  // Modern texture format with Basis Universal compression
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues */
    @JvmStatic
private /* synthetic */ Int[] m37-getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues() {
        if (syntheticField != null) {
            return syntheticField
        }
        Int[] iArr = Int[ImageFormat.values().length]
        try {
            iArr[ImageFormat.JPEG2000.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ImageFormat.Raw.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ImageFormat.TGA.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        syntheticField = iArr
        return iArr
    }

    static {
        System.loadLibrary("openjpeg")
        
        // Initialize Basis Universal transcoder for KTX2 support
        try {
            // This will be implemented in the native code
            // initBasisTranscoder(); // Called lazily when needed
        } catch (UnsatisfiedLinkError e) {
            Debug.Warning(e); // Log but don't fail - KTX2 support will be unavailable
        }
    }

    public OpenJPEG(Int i, Int i2, Int i3, Int i4, Int i5, Int i6) throws OutOfMemoryError {
        this.width = i
        this.height = i2
        this.num_components = i3
        this.num_extra_components = i5
        this.bytes_per_pixel = i4
        this.rawBuffer = allocateNew(i, i2, i3, i4, i5, i6)
        if (this.rawBuffer == null) {
            throw OutOfMemoryError("allocateNew() returned NULL")
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
    }

    public OpenJPEG(File file, Int i, Int i2, Boolean z) throws IOException {
        if (file == null) {
            throw IOException("Null source file")
        }
        this.rawBuffer = decompress(file.getAbsolutePath(), 0, 0, z, i, i2)
        if (this.rawBuffer == null) {
            throw IOException("Failed to decompress texture (" + this.error_code + ") " + file.getAbsolutePath())
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
    }

    public OpenJPEG(File file, TextureClass textureClass, ImageFormat imageFormat, Boolean z) throws IOException {
        Boolean z2 = true
        if (file == null) {
            throw IOException("Null source file")
        }
        Debug.Log("OpenJPEG: decompressing " + file.getName() + " class " + textureClass + " format " + imageFormat)
        Int i = textureClass == TextureClass.Prim ? z ^ 1 : 0
        switch (m37-getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues()[imageFormat.ordinal()]) {
            case 1:
                String absolutePath = file.getAbsolutePath()
                Int i2 = i != 0 ? 1 : 0
                Int i3 = i != 0 ? 6 : 0
                if (textureClass != TextureClass.Prim) {
                    z2 = false
                }
                this.rawBuffer = decompress(absolutePath, i2, i3, z2, 0, 0)
                if (this.rawBuffer == null) {
                    throw IOException("Failed to decompress texture (" + this.error_code + ") " + file.getAbsolutePath())
                }
                break
            case 2:
                this.rawBuffer = readRaw(file.getAbsolutePath())
                if (this.rawBuffer == null) {
                    throw IOException("Failed to read raw texture " + file.getAbsolutePath())
                }
                break
            case 3:
                throw IOException("TGA not supported for non-asset files")
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
    }

    public OpenJPEG(InputStream inputStream, ImageFormat imageFormat, Boolean z, Boolean z2, Float f, Float f2, Boolean z3) throws IOException {
        if (imageFormat == ImageFormat.TGA) {
            Byte[] bArr = Byte[inputStream.available()]
            inputStream.read(bArr)
            this.rawBuffer = decompressTGA(bArr, z, z2, f, f2, z3)
            if (this.rawBuffer == null) {
                throw IOException("Failed to decompress TGA texture.")
            }
            TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
            return
        }
        if (imageFormat == ImageFormat.KTX2) {
            // Initialize Basis Universal transcoder if needed
            if (!initBasisTranscoder()) {
                throw IOException("Failed to initialize Basis Universal transcoder")
            }
            
            Byte[] ktx2Data = Byte[inputStream.available()]
            inputStream.read(ktx2Data)
            
            // Verify it's actually KTX2 format
            if (!isKTX2Format(ktx2Data)) {
                throw IOException("Data is not in KTX2 format")
            }
            
            // Get dimensions
            Int[] dimensions = getKTX2Dimensions(ktx2Data)
            if (dimensions == null || dimensions.length != 3) {
                throw IOException("Failed to get KTX2 texture dimensions")
            }
            
            this.width = dimensions[0]
            this.height = dimensions[1]
            // dimensions[2] is mip levels - could be used later
            
            // Decompress with optimal format (RGBA32 for compatibility)
            this.rawBuffer = decompressKTX2(ktx2Data, 3); // 3 = RGBA32
            if (this.rawBuffer == null) {
                throw IOException("Failed to decompress KTX2 texture")
            }
            
            // Set texture properties for RGBA32 format
            this.num_components = 4; // RGBA
            this.bytes_per_pixel = 4; // 4 bytes per pixel
            this.error_code = 0
            
            TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
            return
        }
        throw IOException("Unsupported format for image stream: " + imageFormat)
    }

    private native ByteBuffer allocateNew(Int i, Int i2, Int i3, Int i4, Int i5, Int i6)

    private native ByteBuffer allocateRaw(Int i)

    @JvmStatic
    native Unit applyFlexibleMorph(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, Int i, Float[] fArr)

    @JvmStatic
    native Unit applyMeshMorph(Float f, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, Int i, ByteBuffer byteBuffer3, ByteBuffer byteBuffer4, ByteBuffer byteBuffer5, Int i2, Int i3, Int i4, ByteBuffer byteBuffer6)

    @JvmStatic
    native Unit applyMorphingTransform(Int i, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3, Int[] iArr, Float[] fArr)

    @JvmStatic
    native Unit applyRiggedMeshMorph(ByteBuffer byteBuffer, Int i, Float[] fArr, Float[] fArr2, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3, Int i2)

    @JvmStatic
    OpenJPEG bakeTerrain(Int i, Int i2, OpenJPEG[] openJPEGArr, Float[] fArr, Int i3, Int i4) {
        OpenJPEG openJPEG = OpenJPEG(i, i2, 3, 2, 0, 0)
        ByteBuffer[] byteBufferArr = ByteBuffer[openJPEGArr.length]
        Int[] iArr = Int[openJPEGArr.length]
        Int[] iArr2 = Int[openJPEGArr.length]
        Int[] iArr3 = Int[openJPEGArr.length]
        for (Int i5 = 0; i5 < openJPEGArr.length; i5++) {
            if (openJPEGArr[i5] != null) {
                byteBufferArr[i5] = openJPEGArr[i5].rawBuffer
                iArr[i5] = openJPEGArr[i5].width
                iArr2[i5] = openJPEGArr[i5].height
                iArr3[i5] = openJPEGArr[i5].num_components
            } else {
                byteBufferArr[i5] = null
                iArr[i5] = 0
                iArr2[i5] = 0
                iArr3[i5] = 0
            }
        }
        openJPEG.bakeTerrainRaw(openJPEG.rawBuffer, i, i2, byteBufferArr, iArr, iArr2, iArr3, fArr, i3, i4)
        return openJPEG
    }

    private native Unit bakeTerrainRaw(ByteBuffer byteBuffer, Int i, Int i2, ByteBuffer[] byteBufferArr, Int[] iArr, Int[] iArr2, Int[] iArr3, Float[] fArr, Int i3, Int i4)

    @JvmStatic
    native Unit calcFlexiSections(Float[] fArr, Int i, Float[] fArr2, Float[] fArr3, Int i2, Float f, Float f2, Float f3, Float f4, Float f5, Float f6, Float f7, Float f8, Float f9, Float f10, Boolean z)

    @JvmStatic
    native Int checkFrustrumOcclusion(Float[] fArr, Float[] fArr2, Float f, Float f2, Float f3)

    private native ByteBuffer decompress(String str, Int i, Int i2, Boolean z, Int i3, Int i4)

    private native ByteBuffer decompressTGA(Byte[] bArr, Boolean z, Boolean z2, Float f, Float f2, Boolean z3)

    private native Unit drawBuf(ByteBuffer byteBuffer, Int i, Int i2, Int i3, ByteBuffer byteBuffer2, Int i4, Int i5, Int i6, Int i7, Boolean z, Boolean z2, Boolean z3, Boolean z4)

    @JvmStatic
    native Int getFlexiDataSize(Int i)

    @JvmStatic
    native Unit meshPrepareInfluenceBuffer(ByteBuffer byteBuffer, Int i, ByteBuffer byteBuffer2, Int i2)

    @JvmStatic
    native Unit meshPrepareSeparateInfluenceBuffer(ByteBuffer byteBuffer, Int i, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3, Int i2)

    private native ByteBuffer readRaw(String str)

    private native Unit release(ByteBuffer byteBuffer)

    private native Unit setComponentBuf(ByteBuffer byteBuffer, Int i, Int i2, Int i3, Int i4, Int i5, Byte b)

    private native Int writeJPEG2K(String str, ByteBuffer byteBuffer, Int i, Int i2, Int i3, Int i4)

    private native Unit writeRaw(ByteBuffer byteBuffer, String str)

    // KTX2/Basis Universal transcoding methods
    private native Boolean initBasisTranscoder()
    private native ByteBuffer decompressKTX2(Byte[] ktx2Data, Int targetFormat)
    private native Int[] getKTX2Dimensions(Byte[] ktx2Data)
    private native Boolean isKTX2Format(Byte[] data)
    
    /**
     * Auto-detect texture format from data
     */
    @JvmStatic
    ImageFormat detectTextureFormat(Byte[] data) {
        if (data == null || data.length < 12) {
            return ImageFormat.JPEG2000; // Default fallback
        }
        
        // Check for KTX2 magic bytes first
        if (data[0] == (Byte)0xAB && data[1] == 0x4B && data[2] == 0x54 && data[3] == 0x58 &&
            data[4] == 0x20 && data[5] == 0x32 && data[6] == 0x30 && data[7] == (Byte)0xBB &&
            data[8] == 0x0D && data[9] == 0x0A && data[10] == 0x1A && data[11] == 0x0A) {
            return ImageFormat.KTX2
        }
        
        // Check for JPEG2000 magic bytes (JP2 format)
        if (data.length >= 8 && data[0] == 0x00 && data[1] == 0x00 && data[2] == 0x00 && data[3] == 0x0C &&
            data[4] == 0x6A && data[5] == 0x50 && data[6] == 0x20 && data[7] == 0x20) {
            return ImageFormat.JPEG2000
        }
        
        // Check for TGA format (basic detection)
        if (data.length >= 18) {
            // TGA files don't have a clear magic number, but we can check some characteristics
            // This is a basic check - may need refinement
            Byte imageType = data[2]
            if (imageType == 2 || imageType == 3 || imageType == 10 || imageType == 11) {
                return ImageFormat.TGA
            }
        }
        
        // Default to JPEG2000 for backward compatibility
        return ImageFormat.JPEG2000
    }

    public Boolean CompressETC1() throws IOException {
        if (VERSION.SDK_INT < 8 || this.rawBuffer == null || this.num_components != 3 || this.num_extra_components != 0 || (this.bytes_per_pixel != 2 && this.bytes_per_pixel != 3)) {
            return false
        }
        Int encodedDataSize = ETC1.getEncodedDataSize(this.width, this.height)
        Buffer allocateRaw = allocateRaw(encodedDataSize)
        if (allocateRaw == null) {
            throw IOException("Out of memory for " + Integer.toString(encodedDataSize) + " allocation")
        }
        ETC1.encodeImage(this.rawBuffer, this.width, this.height, this.bytes_per_pixel, this.width * this.bytes_per_pixel, allocateRaw)
        TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
        release(this.rawBuffer)
        this.rawBuffer = allocateRaw
        TextureMemoryTracker.allocOpenJpegMemory(allocateRaw.capacity(), this.mmapped)
        this.bytes_per_pixel = ETC1_BYTES_PER_PIXEL
        return true
    }

    fun SaveJPEG2K(File file) throws IOException {
        if (this.rawBuffer != null) {
            if (writeJPEG2K(file.getAbsolutePath(), this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components) != 0) {
                throw IOException("Failed to save JPEG2k to " + file.getAbsolutePath())
            }
        }
    }

    fun SaveRaw(File file) {
        if (this.rawBuffer != null) {
            writeRaw(this.rawBuffer, file.getAbsolutePath())
        }
    }

    fun SaveToFile(File file) {
        try {
            FileOutputStream fileOutputStream = FileOutputStream(file, false)
            fileOutputStream.getChannel().write(this.rawBuffer)
            fileOutputStream.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    @TargetApi(18)
    public Int SetAsImmutableTexture() {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                GLES30.glTexStorage2D(3553, 1, 37492, this.width, this.height)
                GLES30.glCompressedTexSubImage2D(3553, 0, 0, 0, this.width, this.height, 37492, this.rawBuffer.capacity(), this.rawBuffer)
            } else {
                Int i3 = 5121
                switch (this.num_components) {
                    case 1:
                        i = 33321
                        i2 = 6403
                        break
                    case 3:
                        i = this.bytes_per_pixel == 2 ? 36194 : 32849
                        i2 = 6407
                        if (this.bytes_per_pixel == 2) {
                            i3 = 33635
                            break
                        }
                        break
                    case 4:
                        i = 32856
                        i2 = 6408
                        break
                    default:
                        return SetAsTexture()
                }
                Int i4 = (this.bytes_per_pixel == 2 && this.num_components == 3) ? 33635 : i3
                GLES30.glTexStorage2D(3553, 1, i, this.width, this.height)
                GLES30.glTexSubImage2D(3553, 0, 0, 0, this.width, this.height, i2, i4, this.rawBuffer)
                if (this.num_components == 1) {
                    GLES30.glTexParameteri(3553, 36418, 1)
                    GLES30.glTexParameteri(3553, 36419, 1)
                    GLES30.glTexParameteri(3553, 36420, 1)
                    GLES30.glTexParameteri(3553, 36421, 6403)
                }
            }
        }
        return getLoadedSize()
    }

    public Int SetAsTexture() {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                GLES10.glCompressedTexImage2D(3553, 0, 36196, this.width, this.height, 0, this.rawBuffer.capacity(), this.rawBuffer)
            } else {
                Int i2 = 5121
                switch (this.num_components) {
                    case 1:
                        i = 6406
                        break
                    case 3:
                        i = 6407
                        break
                    case 4:
                        i = 6408
                        break
                    default:
                        i = this.num_components
                        break
                }
                if (this.bytes_per_pixel == 2 && this.num_components == 3) {
                    i2 = 33635
                }
                GLES10.glTexImage2D(3553, 0, i, this.width, this.height, 0, i, i2, this.rawBuffer)
            }
        }
        return getLoadedSize()
    }

    public Int SetAsTextureTarget(Int i) {
        if (this.rawBuffer == null) {
            return 0
        }
        if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
            Int capacity = this.rawBuffer.capacity()
            GLES20.glCompressedTexImage2D(i, 0, 36196, this.width, this.height, 0, capacity, this.rawBuffer)
            return capacity
        }
        Int i3 = 5121
        switch (this.num_components) {
            case 1:
                i2 = 6406
                break
            case 3:
                i2 = 6407
                break
            case 4:
                i2 = 6408
                break
            default:
                i2 = this.num_components
                break
        }
        if (this.bytes_per_pixel == 2 && this.num_components == 3) {
            i3 = 33635
        }
        GLES20.glTexImage2D(i, 0, i2, this.width, this.height, 0, i2, i3, this.rawBuffer)
        return (this.width * this.height) * this.bytes_per_pixel
    }

    fun blendAlpha(OpenJPEG openJPEG, Boolean z) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null && this.num_components >= 4 && openJPEG.num_components >= 4) {
            drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, true, z, false)
        }
    }

    fun draw(OpenJPEG openJPEG, Int i, Boolean z) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null) {
            drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, i, z, false, false, false)
        }
    }

    fun drawBump(OpenJPEG openJPEG, Int i, Boolean z, Boolean z2) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null && this.num_extra_components >= 1 && openJPEG.num_components >= 4) {
            drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, false, z2, true)
        }
    }

    protected Unit finalize() throws Throwable {
        if (this.rawBuffer != null) {
            TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped)
            release(this.rawBuffer)
            this.rawBuffer = null
        }
        super.finalize()
    }

    public Bitmap getAsBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888)
        if (createBitmap == null) {
            return null
        }
        for (Int i = 0; i < this.height; i++) {
            for (Int i2 = 0; i2 < this.width; i2++) {
                if (this.num_components == 1) {
                    i3 = getByte(((this.width * i) + i2) * this.num_components) & 255
                    i3 |= ((i3 << 16) | ViewCompat.MEASURED_STATE_MASK) | (i3 << 8)
                } else {
                    Int i4 = getByte((((this.width * i) + i2) * this.num_components) + 0) & 255
                    Int i5 = getByte((((this.width * i) + i2) * this.num_components) + 1) & 255
                    Int i6 = getByte((((this.width * i) + i2) * this.num_components) + 2) & 255
                    i3 = 255
                    if (this.num_components >= 4) {
                        i3 = getByte((((this.width * i) + i2) * this.num_components) + 3) & 255
                    }
                    i3 = (((i3 << 24) | (i4 << 16)) | (i5 << 8)) | i6
                }
                createBitmap.setPixel(i2, (this.height - 1) - i, i3)
            }
        }
        return createBitmap
    }

    public Byte getByte(Int i) {
        return this.rawBuffer != null ? this.rawBuffer.get(i) : (Byte) 0
    }

    public Bitmap getExtraAsBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888)
        for (Int i = 0; i < this.height; i++) {
            for (Int i2 = 0; i2 < this.width; i2++) {
                if (this.num_extra_components == 1) {
                    i3 = getByte((((this.width * this.height) * this.num_components) + (this.width * i)) + i2) & 255
                    i3 |= ((i3 << 16) | ViewCompat.MEASURED_STATE_MASK) | (i3 << 8)
                } else {
                    i3 = 0
                }
                createBitmap.setPixel(i2, (this.height - 1) - i, i3)
            }
        }
        return createBitmap
    }

    public ByteBuffer getExtraComponentsBuffer() {
        if (!(this.num_extra_components == 0 || this.rawBuffer == null)) {
            ByteBuffer asReadOnlyBuffer = this.rawBuffer.asReadOnlyBuffer()
            Int i = (this.width * this.height) * this.num_components
            if (i >= 0 && i <= asReadOnlyBuffer.limit()) {
                asReadOnlyBuffer.position(i)
                return asReadOnlyBuffer
            }
        }
        return null
    }

    public Int getHeight() {
        return this.height
    }

    public Int getLoadedSize() {
        return this.rawBuffer != null ? this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL ? this.rawBuffer.capacity() : (this.bytes_per_pixel == 3 && this.num_components == 3) ? (this.width * this.height) * (this.bytes_per_pixel + 1) : (this.width * this.height) * this.bytes_per_pixel : 0
    }

    public Int getNumComponents() {
        return this.num_components
    }

    public Int getRGB(Int i) {
        return this.rawBuffer != null ? (((this.rawBuffer.get(i) << 16) & 16711680) | ((this.rawBuffer.get(i + 1) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (this.rawBuffer.get(i + 2) & 255) : 0
    }

    public Int getWidth() {
        return this.width
    }

    public Boolean hasAlphaLayer() {
        return this.bytes_per_pixel != ETC1_BYTES_PER_PIXEL && (this.num_components >= 4 || this.num_components == 1)
    }

    fun putPixelRow(Int i, Int[] iArr, Int i2) {
        Int i3 = 0
        if (this.rawBuffer != null) {
            Int i4 = (this.width * this.num_components) * i
            if (this.num_components == 3) {
                while (i3 < i2) {
                    i5 = iArr[i3]
                    i6 = i4 + 1
                    this.rawBuffer.put(i4, (Byte) (i5 >> 16))
                    i7 = i6 + 1
                    this.rawBuffer.put(i6, (Byte) (i5 >> 8))
                    i4 = i7 + 1
                    this.rawBuffer.put(i7, (Byte) i5)
                    i3++
                }
            } else if (this.num_components == 4) {
                while (i3 < i2) {
                    i5 = iArr[i3]
                    i6 = i4 + 1
                    this.rawBuffer.put(i4, (Byte) (i5 >> 16))
                    i7 = i6 + 1
                    this.rawBuffer.put(i6, (Byte) (i5 >> 8))
                    i6 = i7 + 1
                    this.rawBuffer.put(i7, (Byte) i5)
                    i4 = i6 + 1
                    this.rawBuffer.put(i6, (Byte) (i5 >> 24))
                    i3++
                }
            }
        }
    }

    fun setComponent(Int i, Byte b) {
        if (this.rawBuffer != null) {
            setComponentBuf(this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components, i, b)
        }
    }
}
