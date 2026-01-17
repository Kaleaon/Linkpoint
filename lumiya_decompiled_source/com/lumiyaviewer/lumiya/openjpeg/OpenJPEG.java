package com.lumiyaviewer.lumiya.openjpeg;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class OpenJPEG implements GLTexture {

    /* renamed from: -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f5x2156f8d4 = null;
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

    /* loaded from: classes.dex */
    public enum ImageFormat {
        Raw,
        JPEG2000,
        TGA;

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static ImageFormat[] valuesCustom() {
            return values();
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m23x89faf778() {
        if (f5x2156f8d4 != null) {
            return f5x2156f8d4;
        }
        int[] iArr = new int[ImageFormat.valuesCustom().length];
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
        f5x2156f8d4 = iArr;
        return iArr;
    }

    static {
        System.loadLibrary("openjpeg");
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
        if (file == null) {
            throw new IOException("Null source file");
        }
        Debug.Log("OpenJPEG: decompressing " + file.getName() + " class " + textureClass + " format " + imageFormat);
        boolean z2 = textureClass == TextureClass.Prim ? !z : false;
        switch (m23x89faf778()[imageFormat.ordinal()]) {
            case 1:
                this.rawBuffer = decompress(file.getAbsolutePath(), z2 ? 1 : 0, z2 ? 6 : 0, textureClass == TextureClass.Prim, 0, 0);
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
        if (imageFormat != ImageFormat.TGA) {
            throw new IOException("Unsupported format for image stream.");
        }
        byte[] bArr = new byte[inputStream.available()];
        inputStream.read(bArr);
        this.rawBuffer = decompressTGA(bArr, z, z2, f, f2, z3);
        if (this.rawBuffer == null) {
            throw new IOException("Failed to decompress TGA texture.");
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
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

    public boolean CompressETC1() throws IOException {
        if (Build.VERSION.SDK_INT >= 8 && this.rawBuffer != null && this.num_components == 3 && this.num_extra_components == 0 && (this.bytes_per_pixel == 2 || this.bytes_per_pixel == 3)) {
            int encodedDataSize = ETC1.getEncodedDataSize(this.width, this.height);
            ByteBuffer allocateRaw = allocateRaw(encodedDataSize);
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
        return false;
    }

    public void SaveJPEG2K(File file) throws IOException {
        if (this.rawBuffer != null && writeJPEG2K(file.getAbsolutePath(), this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components) != 0) {
            throw new IOException("Failed to save JPEG2k to " + file.getAbsolutePath());
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
        int i;
        int i2;
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                GLES30.glTexStorage2D(3553, 1, 37492, this.width, this.height);
                GLES30.glCompressedTexSubImage2D(3553, 0, 0, 0, this.width, this.height, 37492, this.rawBuffer.capacity(), this.rawBuffer);
            } else {
                int i3 = 5121;
                switch (this.num_components) {
                    case 1:
                        i = 33321;
                        i2 = 6403;
                        break;
                    case 2:
                    default:
                        return SetAsTexture();
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

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public int SetAsTexture() {
        int i;
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                GLES10.glCompressedTexImage2D(3553, 0, 36196, this.width, this.height, 0, this.rawBuffer.capacity(), this.rawBuffer);
            } else {
                int i2 = 5121;
                switch (this.num_components) {
                    case 1:
                        i = 6406;
                        break;
                    case 2:
                    default:
                        i = this.num_components;
                        break;
                    case 3:
                        i = 6407;
                        break;
                    case 4:
                        i = 6408;
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
        int i2;
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL) {
                int capacity = this.rawBuffer.capacity();
                GLES20.glCompressedTexImage2D(i, 0, 36196, this.width, this.height, 0, capacity, this.rawBuffer);
                return capacity;
            }
            int i3 = 5121;
            switch (this.num_components) {
                case 1:
                    i2 = 6406;
                    break;
                case 2:
                default:
                    i2 = this.num_components;
                    break;
                case 3:
                    i2 = 6407;
                    break;
                case 4:
                    i2 = 6408;
                    break;
            }
            if (this.bytes_per_pixel == 2 && this.num_components == 3) {
                i3 = 33635;
            }
            GLES20.glTexImage2D(i, 0, i2, this.width, this.height, 0, i2, i3, this.rawBuffer);
            return this.width * this.height * this.bytes_per_pixel;
        }
        return 0;
    }

    public void blendAlpha(OpenJPEG openJPEG, boolean z) {
        if (this.rawBuffer == null || openJPEG.rawBuffer == null || this.num_components < 4 || openJPEG.num_components < 4) {
            return;
        }
        drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, true, z, false);
    }

    public void draw(OpenJPEG openJPEG, int i, boolean z) {
        if (this.rawBuffer == null || openJPEG.rawBuffer == null) {
            return;
        }
        drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, i, z, false, false, false);
    }

    public void drawBump(OpenJPEG openJPEG, int i, boolean z, boolean z2) {
        if (this.rawBuffer == null || openJPEG.rawBuffer == null || this.num_extra_components < 1 || openJPEG.num_components < 4) {
            return;
        }
        drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, false, z2, true);
    }

    protected void finalize() throws Throwable {
        if (this.rawBuffer != null) {
            TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
            release(this.rawBuffer);
            this.rawBuffer = null;
        }
        super.finalize();
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public Bitmap getAsBitmap() {
        int i;
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        if (createBitmap == null) {
            return null;
        }
        for (int i2 = 0; i2 < this.height; i2++) {
            for (int i3 = 0; i3 < this.width; i3++) {
                if (this.num_components == 1) {
                    int i4 = getByte(((this.width * i2) + i3) * this.num_components) & UnsignedBytes.MAX_VALUE;
                    i = i4 | (i4 << 16) | ViewCompat.MEASURED_STATE_MASK | (i4 << 8);
                } else {
                    i = ((this.num_components >= 4 ? getByte((((this.width * i2) + i3) * this.num_components) + 3) & UnsignedBytes.MAX_VALUE : 255) << 24) | ((getByte((((this.width * i2) + i3) * this.num_components) + 0) & UnsignedBytes.MAX_VALUE) << 16) | ((getByte((((this.width * i2) + i3) * this.num_components) + 1) & UnsignedBytes.MAX_VALUE) << 8) | (getByte((((this.width * i2) + i3) * this.num_components) + 2) & UnsignedBytes.MAX_VALUE);
                }
                createBitmap.setPixel(i3, (this.height - 1) - i2, i);
            }
        }
        return createBitmap;
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public byte getByte(int i) {
        if (this.rawBuffer != null) {
            return this.rawBuffer.get(i);
        }
        return (byte) 0;
    }

    public Bitmap getExtraAsBitmap() {
        int i;
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        for (int i2 = 0; i2 < this.height; i2++) {
            for (int i3 = 0; i3 < this.width; i3++) {
                if (this.num_extra_components == 1) {
                    int i4 = getByte((this.width * this.height * this.num_components) + (this.width * i2) + i3) & UnsignedBytes.MAX_VALUE;
                    i = i4 | (i4 << 16) | ViewCompat.MEASURED_STATE_MASK | (i4 << 8);
                } else {
                    i = 0;
                }
                createBitmap.setPixel(i3, (this.height - 1) - i2, i);
            }
        }
        return createBitmap;
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public ByteBuffer getExtraComponentsBuffer() {
        if (this.num_extra_components != 0 && this.rawBuffer != null) {
            ByteBuffer asReadOnlyBuffer = this.rawBuffer.asReadOnlyBuffer();
            int i = this.width * this.height * this.num_components;
            if (i >= 0 && i <= asReadOnlyBuffer.limit()) {
                asReadOnlyBuffer.position(i);
                return asReadOnlyBuffer;
            }
        }
        return null;
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public int getHeight() {
        return this.height;
    }

    public int getLoadedSize() {
        if (this.rawBuffer != null) {
            return this.bytes_per_pixel == ETC1_BYTES_PER_PIXEL ? this.rawBuffer.capacity() : (this.bytes_per_pixel == 3 && this.num_components == 3) ? this.width * this.height * (this.bytes_per_pixel + 1) : this.width * this.height * this.bytes_per_pixel;
        }
        return 0;
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public int getNumComponents() {
        return this.num_components;
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public int getRGB(int i) {
        if (this.rawBuffer != null) {
            return ((this.rawBuffer.get(i) << 16) & 16711680) | ((this.rawBuffer.get(i + 1) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (this.rawBuffer.get(i + 2) & UnsignedBytes.MAX_VALUE);
        }
        return 0;
    }

    @Override // com.lumiyaviewer.lumiya.render.GLTexture
    public int getWidth() {
        return this.width;
    }

    public boolean hasAlphaLayer() {
        return this.bytes_per_pixel != ETC1_BYTES_PER_PIXEL && (this.num_components >= 4 || this.num_components == 1);
    }

    public void putPixelRow(int i, int[] iArr, int i2) {
        int i3 = 0;
        if (this.rawBuffer != null) {
            int i4 = this.width * this.num_components * i;
            if (this.num_components == 3) {
                while (i3 < i2) {
                    int i5 = iArr[i3];
                    int i6 = i4 + 1;
                    this.rawBuffer.put(i4, (byte) (i5 >> 16));
                    int i7 = i6 + 1;
                    this.rawBuffer.put(i6, (byte) (i5 >> 8));
                    i4 = i7 + 1;
                    this.rawBuffer.put(i7, (byte) i5);
                    i3++;
                }
            } else if (this.num_components == 4) {
                while (i3 < i2) {
                    int i8 = iArr[i3];
                    int i9 = i4 + 1;
                    this.rawBuffer.put(i4, (byte) (i8 >> 16));
                    int i10 = i9 + 1;
                    this.rawBuffer.put(i9, (byte) (i8 >> 8));
                    int i11 = i10 + 1;
                    this.rawBuffer.put(i10, (byte) i8);
                    i4 = i11 + 1;
                    this.rawBuffer.put(i11, (byte) (i8 >> 24));
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
