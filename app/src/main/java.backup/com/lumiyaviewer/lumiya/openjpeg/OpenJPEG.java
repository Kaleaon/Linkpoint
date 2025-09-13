package com.lumiyaviewer.lumiya.openjpeg;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.ETC1;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class OpenJPEG implements GLTexture {
    /* renamed from: -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues */
    private static final /* synthetic */ int[] f449-com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues = null;
    private static final int ETC1_BYTES_PER_PIXEL = 888;
    public int bytes_per_pixel;
    public int error_code;
    public int height;
    private boolean mmapped = false;
    private long mmappedAddr = 0;
    private long mmappedSize = 0;
    public int num_components;
    public int num_extra_components;
    private ByteBuffer rawBuffer;
    public int width;

    public enum ImageFormat {
        Raw,
        JPEG2000,
        TGA,
        KTX2  // Modern texture format with Basis Universal compression
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues */
    private static /* synthetic */ int[] m37-getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues() {
        if (f449-com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues != null) {
            return f449-com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues;
        }
        int[] iArr = new int[ImageFormat.values().length];
        try {
            iArr[ImageFormat.JPEG2000.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ImageFormat.Raw.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ImageFormat.TGA.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f449-com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues = iArr;
        return iArr;
    }

    static {
        System.loadLibrary("openjpeg");
        
        // Initialize Basis Universal transcoder for KTX2 support
        try {
            // This will be implemented in the native code
            // initBasisTranscoder(); // Called lazily when needed
        } catch (UnsatisfiedLinkError e) {
            Debug.Warning(e); // Log but don't fail - KTX2 support will be unavailable
        }
    }

    public OpenJPEG(int i, int i2, int i3, int i4, int i5, int i6) throws OutOfMemoryError {
        this.width = i;
        this.height = i2;
        this.num_components = i3;
        this.num_extra_components = i5;
        this.bytes_per_pixel = i4;
        this.rawBuffer = allocateNew(i, i2, i3, i4, i5, i6);
        if (this.rawBuffer == null) {
            throw new OutOfMemoryError("allocateNew() returned NULL");
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }

    public OpenJPEG(File file, int i, int i2, boolean z) throws IOException {
        if (file == null) {
            throw new IOException("Null source file");
        }
        this.rawBuffer = decompress(file.getAbsolutePath(), 0, 0, z, i, i2);
        if (this.rawBuffer == null) {
            throw new IOException("Failed to decompress texture (" + this.error_code + ") " + file.getAbsolutePath());
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }

    public OpenJPEG(File file, TextureClass textureClass, ImageFormat imageFormat, boolean z) throws IOException {
        boolean z2 = true;
        if (file == null) {
            throw new IOException("Null source file");
        }
        Debug.Log("OpenJPEG: decompressing " + file.getName() + " class " + textureClass + " format " + imageFormat);
        int i = textureClass == TextureClass.Prim ? z ^ 1 : 0;
        switch (m37-getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues()[imageFormat.ordinal()]) {
            case 1:
                String absolutePath = file.getAbsolutePath();
                int i2 = i != 0 ? 1 : 0;
                int i3 = i != 0 ? 6 : 0;
                if (textureClass != TextureClass.Prim) {
                    z2 = false;
                }
                this.rawBuffer = decompress(absolutePath, i2, i3, z2, 0, 0);
                if (this.rawBuffer == null) {
                    throw new IOException("Failed to decompress texture (" + this.error_code + ") " + file.getAbsolutePath());
                }
                break;
            case 2:
                this.rawBuffer = readRaw(file.getAbsolutePath());
                if (this.rawBuffer == null) {
                    throw new IOException("Failed to read raw texture " + file.getAbsolutePath());
                }
                break;
            case 3:
                throw new IOException("TGA not supported for non-asset files");
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }

    public OpenJPEG(InputStream inputStream, ImageFormat imageFormat, boolean z, boolean z2, float f, float f2, boolean z3) throws IOException {
        if (imageFormat == ImageFormat.TGA) {
            byte[] bArr = new byte[inputStream.available()];
            inputStream.read(bArr);
            this.rawBuffer = decompressTGA(bArr, z, z2, f, f2, z3);
            if (this.rawBuffer == null) {
                throw new IOException("Failed to decompress TGA texture.");
            }
            TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
            return;
        }
        if (imageFormat == ImageFormat.KTX2) {
            // Initialize Basis Universal transcoder if needed
            if (!initBasisTranscoder()) {
                throw new IOException("Failed to initialize Basis Universal transcoder");
            }
            
            byte[] ktx2Data = new byte[inputStream.available()];
            inputStream.read(ktx2Data);
            
            // Verify it's actually KTX2 format
            if (!isKTX2Format(ktx2Data)) {
                throw new IOException("Data is not in KTX2 format");
            }
            
            // Get dimensions
            int[] dimensions = getKTX2Dimensions(ktx2Data);
            if (dimensions == null || dimensions.length != 3) {
                throw new IOException("Failed to get KTX2 texture dimensions");
            }
            
            this.width = dimensions[0];
            this.height = dimensions[1];
            // dimensions[2] is mip levels - could be used later
            
            // Decompress with optimal format (RGBA32 for compatibility)
            this.rawBuffer = decompressKTX2(ktx2Data, 3); // 3 = RGBA32
            if (this.rawBuffer == null) {
                throw new IOException("Failed to decompress KTX2 texture");
            }
            
            // Set texture properties for RGBA32 format
            this.num_components = 4; // RGBA
            this.bytes_per_pixel = 4; // 4 bytes per pixel
            this.error_code = 0;
            
            TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
            return;
        }
        throw new IOException("Unsupported format for image stream: " + imageFormat);
    }

    private native ByteBuffer allocateNew(int i, int i2, int i3, int i4, int i5, int i6);

    private native ByteBuffer allocateRaw(int i);

    public static native void applyFlexibleMorph(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, float[] fArr);

    public static native void applyMeshMorph(float f, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, ByteBuffer byteBuffer3, ByteBuffer byteBuffer4, ByteBuffer byteBuffer5, int i2, int i3, int i4, ByteBuffer byteBuffer6);

    public static native void applyMorphingTransform(int i, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3, int[] iArr, float[] fArr);

    public static native void applyRiggedMeshMorph(ByteBuffer byteBuffer, int i, float[] fArr, float[] fArr2, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3, int i2);

    public static OpenJPEG bakeTerrain(int i, int i2, OpenJPEG[] openJPEGArr, float[] fArr, int i3, int i4) {
        OpenJPEG openJPEG = new OpenJPEG(i, i2, 3, 2, 0, 0);
        ByteBuffer[] byteBufferArr = new ByteBuffer[openJPEGArr.length];
        int[] iArr = new int[openJPEGArr.length];
        int[] iArr2 = new int[openJPEGArr.length];
        int[] iArr3 = new int[openJPEGArr.length];
        for (int i5 = 0; i5 < openJPEGArr.length; i5++) {
            if (openJPEGArr[i5] != null) {
                byteBufferArr[i5] = openJPEGArr[i5].rawBuffer;
                iArr[i5] = openJPEGArr[i5].width;
                iArr2[i5] = openJPEGArr[i5].height;
                iArr3[i5] = openJPEGArr[i5].num_components;
            } else {
                byteBufferArr[i5] = null;
                iArr[i5] = 0;
                iArr2[i5] = 0;
                iArr3[i5] = 0;
            }
        }
        openJPEG.bakeTerrainRaw(openJPEG.rawBuffer, i, i2, byteBufferArr, iArr, iArr2, iArr3, fArr, i3, i4);
        return openJPEG;
    }

    private native void bakeTerrainRaw(ByteBuffer byteBuffer, int i, int i2, ByteBuffer[] byteBufferArr, int[] iArr, int[] iArr2, int[] iArr3, float[] fArr, int i3, int i4);

    public static native void calcFlexiSections(float[] fArr, int i, float[] fArr2, float[] fArr3, int i2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, boolean z);

    public static native int checkFrustrumOcclusion(float[] fArr, float[] fArr2, float f, float f2, float f3);

    private native ByteBuffer decompress(String str, int i, int i2, boolean z, int i3, int i4);

    private native ByteBuffer decompressTGA(byte[] bArr, boolean z, boolean z2, float f, float f2, boolean z3);

    private native void drawBuf(ByteBuffer byteBuffer, int i, int i2, int i3, ByteBuffer byteBuffer2, int i4, int i5, int i6, int i7, boolean z, boolean z2, boolean z3, boolean z4);

    public static native int getFlexiDataSize(int i);

    public static native void meshPrepareInfluenceBuffer(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2);

    public static native void meshPrepareSeparateInfluenceBuffer(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, ByteBuffer byteBuffer3, int i2);

    private native ByteBuffer readRaw(String str);

    private native void release(ByteBuffer byteBuffer);

    private native void setComponentBuf(ByteBuffer byteBuffer, int i, int i2, int i3, int i4, int i5, byte b);

    private native int writeJPEG2K(String str, ByteBuffer byteBuffer, int i, int i2, int i3, int i4);

    private native void writeRaw(ByteBuffer byteBuffer, String str);

    // KTX2/Basis Universal transcoding methods
    private native boolean initBasisTranscoder();
    private native ByteBuffer decompressKTX2(byte[] ktx2Data, int targetFormat);
    private native int[] getKTX2Dimensions(byte[] ktx2Data);
    private native boolean isKTX2Format(byte[] data);
    
    /**
     * Auto-detect texture format from data
     */
    public static ImageFormat detectTextureFormat(byte[] data) {
        if (data == null || data.length < 12) {
            return ImageFormat.JPEG2000; // Default fallback
        }
        
        // Check for KTX2 magic bytes first
        if (data[0] == (byte)0xAB && data[1] == 0x4B && data[2] == 0x54 && data[3] == 0x58 &&
            data[4] == 0x20 && data[5] == 0x32 && data[6] == 0x30 && data[7] == (byte)0xBB &&
            data[8] == 0x0D && data[9] == 0x0A && data[10] == 0x1A && data[11] == 0x0A) {
            return ImageFormat.KTX2;
        }
        
        // Check for JPEG2000 magic bytes (JP2 format)
        if (data.length >= 8 && data[0] == 0x00 && data[1] == 0x00 && data[2] == 0x00 && data[3] == 0x0C &&
            data[4] == 0x6A && data[5] == 0x50 && data[6] == 0x20 && data[7] == 0x20) {
            return ImageFormat.JPEG2000;
        }
        
        // Check for TGA format (basic detection)
        if (data.length >= 18) {
            // TGA files don't have a clear magic number, but we can check some characteristics
            // This is a basic check - may need refinement
            byte imageType = data[2];
            if (imageType == 2 || imageType == 3 || imageType == 10 || imageType == 11) {
                return ImageFormat.TGA;
            }
        }
        
        // Default to JPEG2000 for backward compatibility
        return ImageFormat.JPEG2000;
    }

    public boolean CompressETC1() throws IOException {
        if (VERSION.SDK_INT < 8 || this.rawBuffer == null || this.num_components != 3 || this.num_extra_components != 0 || (this.bytes_per_pixel != 2 && this.bytes_per_pixel != 3)) {
            return false;
        }
        int encodedDataSize = ETC1.getEncodedDataSize(this.width, this.height);
        Buffer allocateRaw = allocateRaw(encodedDataSize);
        if (allocateRaw == null) {
            throw new IOException("Out of memory for " + Integer.toString(encodedDataSize) + " allocation");
        }
        ETC1.encodeImage(this.rawBuffer, this.width, this.height, this.bytes_per_pixel, this.width * this.bytes_per_pixel, allocateRaw);
        TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
        release(this.rawBuffer);
        this.rawBuffer = allocateRaw;
        TextureMemoryTracker.allocOpenJpegMemory(allocateRaw.capacity(), this.mmapped);
        this.bytes_per_pixel = ETC1_BYTES_PER_PIXEL;
        return true;
    }

    public void SaveJPEG2K(File file) throws IOException {
        if (this.rawBuffer != null) {
            if (writeJPEG2K(file.getAbsolutePath(), this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components) != 0) {
                throw new IOException("Failed to save JPEG2k to " + file.getAbsolutePath());
            }
        }
    }

    public void SaveRaw(File file) {
        if (this.rawBuffer != null) {
            writeRaw(this.rawBuffer, file.getAbsolutePath());
        }
    }

    public void SaveToFile(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.getChannel().write(this.rawBuffer);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(18)
    public int SetAsImmutableTexture() {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                GLES30.glTexStorage2D(3553, 1, 37492, this.width, this.height);
                GLES30.glCompressedTexSubImage2D(3553, 0, 0, 0, this.width, this.height, 37492, this.rawBuffer.capacity(), this.rawBuffer);
            } else {
                int i;
                int i2;
                int i3 = 5121;
                switch (this.num_components) {
                    case 1:
                        i = 33321;
                        i2 = 6403;
                        break;
                    case 3:
                        i = this.bytes_per_pixel == 2 ? 36194 : 32849;
                        i2 = 6407;
                        if (this.bytes_per_pixel == 2) {
                            i3 = 33635;
                            break;
                        }
                        break;
                    case 4:
                        i = 32856;
                        i2 = 6408;
                        break;
                    default:
                        return SetAsTexture();
                }
                int i4 = (this.bytes_per_pixel == 2 && this.num_components == 3) ? 33635 : i3;
                GLES30.glTexStorage2D(3553, 1, i, this.width, this.height);
                GLES30.glTexSubImage2D(3553, 0, 0, 0, this.width, this.height, i2, i4, this.rawBuffer);
                if (this.num_components == 1) {
                    GLES30.glTexParameteri(3553, 36418, 1);
                    GLES30.glTexParameteri(3553, 36419, 1);
                    GLES30.glTexParameteri(3553, 36420, 1);
                    GLES30.glTexParameteri(3553, 36421, 6403);
                }
            }
        }
        return getLoadedSize();
    }

    public int SetAsTexture() {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                GLES10.glCompressedTexImage2D(3553, 0, 36196, this.width, this.height, 0, this.rawBuffer.capacity(), this.rawBuffer);
            } else {
                int i;
                int i2 = 5121;
                switch (this.num_components) {
                    case 1:
                        i = 6406;
                        break;
                    case 3:
                        i = 6407;
                        break;
                    case 4:
                        i = 6408;
                        break;
                    default:
                        i = this.num_components;
                        break;
                }
                if (this.bytes_per_pixel == 2 && this.num_components == 3) {
                    i2 = 33635;
                }
                GLES10.glTexImage2D(3553, 0, i, this.width, this.height, 0, i, i2, this.rawBuffer);
            }
        }
        return getLoadedSize();
    }

    public int SetAsTextureTarget(int i) {
        if (this.rawBuffer == null) {
            return 0;
        }
        if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
            int capacity = this.rawBuffer.capacity();
            GLES20.glCompressedTexImage2D(i, 0, 36196, this.width, this.height, 0, capacity, this.rawBuffer);
            return capacity;
        }
        int i2;
        int i3 = 5121;
        switch (this.num_components) {
            case 1:
                i2 = 6406;
                break;
            case 3:
                i2 = 6407;
                break;
            case 4:
                i2 = 6408;
                break;
            default:
                i2 = this.num_components;
                break;
        }
        if (this.bytes_per_pixel == 2 && this.num_components == 3) {
            i3 = 33635;
        }
        GLES20.glTexImage2D(i, 0, i2, this.width, this.height, 0, i2, i3, this.rawBuffer);
        return (this.width * this.height) * this.bytes_per_pixel;
    }

    public void blendAlpha(OpenJPEG openJPEG, boolean z) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null && this.num_components >= 4 && openJPEG.num_components >= 4) {
            drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, true, z, false);
        }
    }

    public void draw(OpenJPEG openJPEG, int i, boolean z) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null) {
            drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, i, z, false, false, false);
        }
    }

    public void drawBump(OpenJPEG openJPEG, int i, boolean z, boolean z2) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null && this.num_extra_components >= 1 && openJPEG.num_components >= 4) {
            drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, false, z2, true);
        }
    }

    protected void finalize() throws Throwable {
        if (this.rawBuffer != null) {
            TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
            release(this.rawBuffer);
            this.rawBuffer = null;
        }
        super.finalize();
    }

    public Bitmap getAsBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        if (createBitmap == null) {
            return null;
        }
        for (int i = 0; i < this.height; i++) {
            for (int i2 = 0; i2 < this.width; i2++) {
                int i3;
                if (this.num_components == 1) {
                    i3 = getByte(((this.width * i) + i2) * this.num_components) & 255;
                    i3 |= ((i3 << 16) | ViewCompat.MEASURED_STATE_MASK) | (i3 << 8);
                } else {
                    int i4 = getByte((((this.width * i) + i2) * this.num_components) + 0) & 255;
                    int i5 = getByte((((this.width * i) + i2) * this.num_components) + 1) & 255;
                    int i6 = getByte((((this.width * i) + i2) * this.num_components) + 2) & 255;
                    i3 = 255;
                    if (this.num_components >= 4) {
                        i3 = getByte((((this.width * i) + i2) * this.num_components) + 3) & 255;
                    }
                    i3 = (((i3 << 24) | (i4 << 16)) | (i5 << 8)) | i6;
                }
                createBitmap.setPixel(i2, (this.height - 1) - i, i3);
            }
        }
        return createBitmap;
    }

    public byte getByte(int i) {
        return this.rawBuffer != null ? this.rawBuffer.get(i) : (byte) 0;
    }

    public Bitmap getExtraAsBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        for (int i = 0; i < this.height; i++) {
            for (int i2 = 0; i2 < this.width; i2++) {
                int i3;
                if (this.num_extra_components == 1) {
                    i3 = getByte((((this.width * this.height) * this.num_components) + (this.width * i)) + i2) & 255;
                    i3 |= ((i3 << 16) | ViewCompat.MEASURED_STATE_MASK) | (i3 << 8);
                } else {
                    i3 = 0;
                }
                createBitmap.setPixel(i2, (this.height - 1) - i, i3);
            }
        }
        return createBitmap;
    }

    public ByteBuffer getExtraComponentsBuffer() {
        if (!(this.num_extra_components == 0 || this.rawBuffer == null)) {
            ByteBuffer asReadOnlyBuffer = this.rawBuffer.asReadOnlyBuffer();
            int i = (this.width * this.height) * this.num_components;
            if (i >= 0 && i <= asReadOnlyBuffer.limit()) {
                asReadOnlyBuffer.position(i);
                return asReadOnlyBuffer;
            }
        }
        return null;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLoadedSize() {
        return this.rawBuffer != null ? this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL ? this.rawBuffer.capacity() : (this.bytes_per_pixel == 3 && this.num_components == 3) ? (this.width * this.height) * (this.bytes_per_pixel + 1) : (this.width * this.height) * this.bytes_per_pixel : 0;
    }

    public int getNumComponents() {
        return this.num_components;
    }

    public int getRGB(int i) {
        return this.rawBuffer != null ? (((this.rawBuffer.get(i) << 16) & 16711680) | ((this.rawBuffer.get(i + 1) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (this.rawBuffer.get(i + 2) & 255) : 0;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean hasAlphaLayer() {
        return this.bytes_per_pixel != ETC1_BYTES_PER_PIXEL && (this.num_components >= 4 || this.num_components == 1);
    }

    public void putPixelRow(int i, int[] iArr, int i2) {
        int i3 = 0;
        if (this.rawBuffer != null) {
            int i4 = (this.width * this.num_components) * i;
            int i5;
            int i6;
            int i7;
            if (this.num_components == 3) {
                while (i3 < i2) {
                    i5 = iArr[i3];
                    i6 = i4 + 1;
                    this.rawBuffer.put(i4, (byte) (i5 >> 16));
                    i7 = i6 + 1;
                    this.rawBuffer.put(i6, (byte) (i5 >> 8));
                    i4 = i7 + 1;
                    this.rawBuffer.put(i7, (byte) i5);
                    i3++;
                }
            } else if (this.num_components == 4) {
                while (i3 < i2) {
                    i5 = iArr[i3];
                    i6 = i4 + 1;
                    this.rawBuffer.put(i4, (byte) (i5 >> 16));
                    i7 = i6 + 1;
                    this.rawBuffer.put(i6, (byte) (i5 >> 8));
                    i6 = i7 + 1;
                    this.rawBuffer.put(i7, (byte) i5);
                    i4 = i6 + 1;
                    this.rawBuffer.put(i6, (byte) (i5 >> 24));
                    i3++;
                }
            }
        }
    }

    public void setComponent(int i, byte b) {
        if (this.rawBuffer != null) {
            setComponentBuf(this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components, i, b);
        }
    }
}
