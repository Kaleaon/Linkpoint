// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.openjpeg;

import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES10;
import android.annotation.TargetApi;
import android.opengl.GLES30;
import java.io.FileOutputStream;
import java.nio.Buffer;
import android.opengl.ETC1;
import android.os.Build$VERSION;
import java.io.InputStream;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.io.IOException;
import java.io.File;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.render.GLTexture;

public class OpenJPEG implements GLTexture
{
    private static final int ETC1_BYTES_PER_PIXEL = 888;
    public int bytes_per_pixel;
    public int error_code;
    public int height;
    private boolean mmapped;
    private long mmappedAddr;
    private long mmappedSize;
    public int num_components;
    public int num_extra_components;
    private ByteBuffer rawBuffer;
    public int width;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues() {
        if (OpenJPEG.-com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues != null) {
            return OpenJPEG.-com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues = new int[ImageFormat.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues[ImageFormat.JPEG2000.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues[ImageFormat.Raw.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues[ImageFormat.TGA.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues = -com-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    static {
        System.loadLibrary("openjpeg");
    }
    
    public OpenJPEG(final int width, final int height, final int num_components, final int bytes_per_pixel, final int num_extra_components, final int n) throws OutOfMemoryError {
        this.mmapped = false;
        this.mmappedAddr = 0L;
        this.mmappedSize = 0L;
        this.width = width;
        this.height = height;
        this.num_components = num_components;
        this.num_extra_components = num_extra_components;
        this.bytes_per_pixel = bytes_per_pixel;
        this.rawBuffer = this.allocateNew(width, height, num_components, bytes_per_pixel, num_extra_components, n);
        if (this.rawBuffer == null) {
            throw new OutOfMemoryError("allocateNew() returned NULL");
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }
    
    public OpenJPEG(final File file, final int n, final int n2, final boolean b) throws IOException {
        this.mmapped = false;
        this.mmappedAddr = 0L;
        this.mmappedSize = 0L;
        if (file == null) {
            throw new IOException("Null source file");
        }
        this.rawBuffer = this.decompress(file.getAbsolutePath(), 0, 0, b, n, n2);
        if (this.rawBuffer == null) {
            throw new IOException("Failed to decompress texture (" + this.error_code + ") " + file.getAbsolutePath());
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }
    
    public OpenJPEG(final File file, final TextureClass obj, final ImageFormat obj2, final boolean b) throws IOException {
        final boolean b2 = true;
        this.mmapped = false;
        this.mmappedAddr = 0L;
        this.mmappedSize = 0L;
        if (file == null) {
            throw new IOException("Null source file");
        }
        Debug.Log("OpenJPEG: decompressing " + file.getName() + " class " + obj + " format " + obj2);
        final boolean b3 = obj == TextureClass.Prim && (b ^ true);
        switch (-getcom-lumiyaviewer-lumiya-openjpeg-OpenJPEG$ImageFormatSwitchesValues()[obj2.ordinal()]) {
            case 2: {
                this.rawBuffer = this.readRaw(file.getAbsolutePath());
                if (this.rawBuffer == null) {
                    throw new IOException("Failed to read raw texture " + file.getAbsolutePath());
                }
                break;
            }
            case 1: {
                final String absolutePath = file.getAbsolutePath();
                int n;
                if (b3) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                int n2;
                if (b3) {
                    n2 = 6;
                }
                else {
                    n2 = 0;
                }
                this.rawBuffer = this.decompress(absolutePath, n, n2, obj == TextureClass.Prim && b2, 0, 0);
                if (this.rawBuffer == null) {
                    throw new IOException("Failed to decompress texture (" + this.error_code + ") " + file.getAbsolutePath());
                }
                break;
            }
            case 3: {
                throw new IOException("TGA not supported for non-asset files");
            }
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }
    
    public OpenJPEG(final InputStream inputStream, final ImageFormat imageFormat, final boolean b, final boolean b2, final float n, final float n2, final boolean b3) throws IOException {
        this.mmapped = false;
        this.mmappedAddr = 0L;
        this.mmappedSize = 0L;
        if (imageFormat != ImageFormat.TGA) {
            throw new IOException("Unsupported format for image stream.");
        }
        final byte[] b4 = new byte[inputStream.available()];
        inputStream.read(b4);
        this.rawBuffer = this.decompressTGA(b4, b, b2, n, n2, b3);
        if (this.rawBuffer == null) {
            throw new IOException("Failed to decompress TGA texture.");
        }
        TextureMemoryTracker.allocOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
    }
    
    private native ByteBuffer allocateNew(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    private native ByteBuffer allocateRaw(final int p0);
    
    public static native void applyFlexibleMorph(final ByteBuffer p0, final ByteBuffer p1, final int p2, final float[] p3);
    
    public static native void applyMeshMorph(final float p0, final ByteBuffer p1, final ByteBuffer p2, final int p3, final ByteBuffer p4, final ByteBuffer p5, final ByteBuffer p6, final int p7, final int p8, final int p9, final ByteBuffer p10);
    
    public static native void applyMorphingTransform(final int p0, final ByteBuffer p1, final ByteBuffer p2, final ByteBuffer p3, final int[] p4, final float[] p5);
    
    public static native void applyRiggedMeshMorph(final ByteBuffer p0, final int p1, final float[] p2, final float[] p3, final ByteBuffer p4, final ByteBuffer p5, final int p6);
    
    public static OpenJPEG bakeTerrain(final int n, final int n2, final OpenJPEG[] array, final float[] array2, final int n3, final int n4) {
        final OpenJPEG openJPEG = new OpenJPEG(n, n2, 3, 2, 0, 0);
        final ByteBuffer[] array3 = new ByteBuffer[array.length];
        final int[] array4 = new int[array.length];
        final int[] array5 = new int[array.length];
        final int[] array6 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                array3[i] = array[i].rawBuffer;
                array4[i] = array[i].width;
                array5[i] = array[i].height;
                array6[i] = array[i].num_components;
            }
            else {
                array3[i] = null;
                array4[i] = 0;
                array6[i] = (array5[i] = 0);
            }
        }
        openJPEG.bakeTerrainRaw(openJPEG.rawBuffer, n, n2, array3, array4, array5, array6, array2, n3, n4);
        return openJPEG;
    }
    
    private native void bakeTerrainRaw(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer[] p3, final int[] p4, final int[] p5, final int[] p6, final float[] p7, final int p8, final int p9);
    
    public static native void calcFlexiSections(final float[] p0, final int p1, final float[] p2, final float[] p3, final int p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final boolean p15);
    
    public static native int checkFrustrumOcclusion(final float[] p0, final float[] p1, final float p2, final float p3, final float p4);
    
    private native ByteBuffer decompress(final String p0, final int p1, final int p2, final boolean p3, final int p4, final int p5);
    
    private native ByteBuffer decompressTGA(final byte[] p0, final boolean p1, final boolean p2, final float p3, final float p4, final boolean p5);
    
    private native void drawBuf(final ByteBuffer p0, final int p1, final int p2, final int p3, final ByteBuffer p4, final int p5, final int p6, final int p7, final int p8, final boolean p9, final boolean p10, final boolean p11, final boolean p12);
    
    public static native int getFlexiDataSize(final int p0);
    
    public static native void meshPrepareInfluenceBuffer(final ByteBuffer p0, final int p1, final ByteBuffer p2, final int p3);
    
    public static native void meshPrepareSeparateInfluenceBuffer(final ByteBuffer p0, final int p1, final ByteBuffer p2, final ByteBuffer p3, final int p4);
    
    private native ByteBuffer readRaw(final String p0);
    
    private native void release(final ByteBuffer p0);
    
    private native void setComponentBuf(final ByteBuffer p0, final int p1, final int p2, final int p3, final int p4, final int p5, final byte p6);
    
    private native int writeJPEG2K(final String p0, final ByteBuffer p1, final int p2, final int p3, final int p4, final int p5);
    
    private native void writeRaw(final ByteBuffer p0, final String p1);
    
    public boolean CompressETC1() throws IOException {
        if (Build$VERSION.SDK_INT < 8 || this.rawBuffer == null || this.num_components != 3 || this.num_extra_components != 0 || (this.bytes_per_pixel != 2 && this.bytes_per_pixel != 3)) {
            return false;
        }
        final int encodedDataSize = ETC1.getEncodedDataSize(this.width, this.height);
        final ByteBuffer allocateRaw = this.allocateRaw(encodedDataSize);
        if (allocateRaw == null) {
            throw new IOException("Out of memory for " + Integer.toString(encodedDataSize) + " allocation");
        }
        ETC1.encodeImage((Buffer)this.rawBuffer, this.width, this.height, this.bytes_per_pixel, this.width * this.bytes_per_pixel, (Buffer)allocateRaw);
        TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
        this.release(this.rawBuffer);
        this.rawBuffer = allocateRaw;
        TextureMemoryTracker.allocOpenJpegMemory(allocateRaw.capacity(), this.mmapped);
        this.bytes_per_pixel = 888;
        return true;
    }
    
    public void SaveJPEG2K(final File file) throws IOException {
        if (this.rawBuffer != null && this.writeJPEG2K(file.getAbsolutePath(), this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components) != 0) {
            throw new IOException("Failed to save JPEG2k to " + file.getAbsolutePath());
        }
    }
    
    public void SaveRaw(final File file) {
        if (this.rawBuffer != null) {
            this.writeRaw(this.rawBuffer, file.getAbsolutePath());
        }
    }
    
    public void SaveToFile(final File file) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.getChannel().write(this.rawBuffer);
            fileOutputStream.close();
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @TargetApi(18)
    public int SetAsImmutableTexture() {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == 888) {
                GLES30.glTexStorage2D(3553, 1, 37492, this.width, this.height);
                GLES30.glCompressedTexSubImage2D(3553, 0, 0, 0, this.width, this.height, 37492, this.rawBuffer.capacity(), (Buffer)this.rawBuffer);
            }
            else {
                int n = 5121;
                int n2 = 0;
                int n3 = 0;
                switch (this.num_components) {
                    default: {
                        return this.SetAsTexture();
                    }
                    case 4: {
                        n2 = 32856;
                        n3 = 6408;
                        break;
                    }
                    case 3: {
                        int n4;
                        if (this.bytes_per_pixel == 2) {
                            n4 = 36194;
                        }
                        else {
                            n4 = 32849;
                        }
                        final int n5 = 6407;
                        n2 = n4;
                        n3 = n5;
                        if (this.bytes_per_pixel == 2) {
                            n = 33635;
                            n2 = n4;
                            n3 = n5;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        n2 = 33321;
                        n3 = 6403;
                        break;
                    }
                }
                if (this.bytes_per_pixel == 2 && this.num_components == 3) {
                    n = 33635;
                }
                GLES30.glTexStorage2D(3553, 1, n2, this.width, this.height);
                GLES30.glTexSubImage2D(3553, 0, 0, 0, this.width, this.height, n3, n, (Buffer)this.rawBuffer);
                if (this.num_components == 1) {
                    GLES30.glTexParameteri(3553, 36418, 1);
                    GLES30.glTexParameteri(3553, 36419, 1);
                    GLES30.glTexParameteri(3553, 36420, 1);
                    GLES30.glTexParameteri(3553, 36421, 6403);
                }
            }
        }
        return this.getLoadedSize();
    }
    
    @Override
    public int SetAsTexture() {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == 888) {
                GLES10.glCompressedTexImage2D(3553, 0, 36196, this.width, this.height, 0, this.rawBuffer.capacity(), (Buffer)this.rawBuffer);
            }
            else {
                final int n = 5121;
                int num_components = 0;
                switch (this.num_components) {
                    default: {
                        num_components = this.num_components;
                        break;
                    }
                    case 4: {
                        num_components = 6408;
                        break;
                    }
                    case 3: {
                        num_components = 6407;
                        break;
                    }
                    case 1: {
                        num_components = 6406;
                        break;
                    }
                }
                int n2 = n;
                if (this.bytes_per_pixel == 2) {
                    n2 = n;
                    if (this.num_components == 3) {
                        n2 = 33635;
                    }
                }
                GLES10.glTexImage2D(3553, 0, num_components, this.width, this.height, 0, num_components, n2, (Buffer)this.rawBuffer);
            }
        }
        return this.getLoadedSize();
    }
    
    public int SetAsTextureTarget(int n) {
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == 888) {
                final int capacity = this.rawBuffer.capacity();
                GLES20.glCompressedTexImage2D(n, 0, 36196, this.width, this.height, 0, capacity, (Buffer)this.rawBuffer);
                n = capacity;
            }
            else {
                final int n2 = 5121;
                int num_components = 0;
                switch (this.num_components) {
                    default: {
                        num_components = this.num_components;
                        break;
                    }
                    case 4: {
                        num_components = 6408;
                        break;
                    }
                    case 3: {
                        num_components = 6407;
                        break;
                    }
                    case 1: {
                        num_components = 6406;
                        break;
                    }
                }
                int n3 = n2;
                if (this.bytes_per_pixel == 2) {
                    n3 = n2;
                    if (this.num_components == 3) {
                        n3 = 33635;
                    }
                }
                GLES20.glTexImage2D(n, 0, num_components, this.width, this.height, 0, num_components, n3, (Buffer)this.rawBuffer);
                n = this.width * this.height * this.bytes_per_pixel;
            }
        }
        else {
            n = 0;
        }
        return n;
    }
    
    public void blendAlpha(final OpenJPEG openJPEG, final boolean b) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null && this.num_components >= 4 && openJPEG.num_components >= 4) {
            this.drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, true, b, false);
        }
    }
    
    public void draw(final OpenJPEG openJPEG, final int n, final boolean b) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null) {
            this.drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, n, b, false, false, false);
        }
    }
    
    public void drawBump(final OpenJPEG openJPEG, final int n, final boolean b, final boolean b2) {
        if (this.rawBuffer != null && openJPEG.rawBuffer != null && this.num_extra_components >= 1 && openJPEG.num_components >= 4) {
            this.drawBuf(this.rawBuffer, this.width, this.height, this.num_components, openJPEG.rawBuffer, openJPEG.width, openJPEG.height, openJPEG.num_components, 0, false, false, b2, true);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.rawBuffer != null) {
            TextureMemoryTracker.releaseOpenJpegMemory(this.rawBuffer.capacity(), this.mmapped);
            this.release(this.rawBuffer);
            this.rawBuffer = null;
        }
        super.finalize();
    }
    
    @Override
    public Bitmap getAsBitmap() {
        final Bitmap bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap$Config.ARGB_8888);
        if (bitmap == null) {
            return null;
        }
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                int n2;
                if (this.num_components == 1) {
                    final int n = this.getByte((this.width * i + j) * this.num_components) & 0xFF;
                    n2 = (n | (n << 16 | 0xFF000000 | n << 8));
                }
                else {
                    final byte byte1 = this.getByte((this.width * i + j) * this.num_components + 0);
                    final byte byte2 = this.getByte((this.width * i + j) * this.num_components + 1);
                    final byte byte3 = this.getByte((this.width * i + j) * this.num_components + 2);
                    int n3 = 255;
                    if (this.num_components >= 4) {
                        n3 = (this.getByte((this.width * i + j) * this.num_components + 3) & 0xFF);
                    }
                    n2 = (n3 << 24 | (byte1 & 0xFF) << 16 | (byte2 & 0xFF) << 8 | (byte3 & 0xFF));
                }
                bitmap.setPixel(j, this.height - 1 - i, n2);
            }
        }
        return bitmap;
    }
    
    @Override
    public byte getByte(final int n) {
        if (this.rawBuffer != null) {
            return this.rawBuffer.get(n);
        }
        return 0;
    }
    
    public Bitmap getExtraAsBitmap() {
        final Bitmap bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap$Config.ARGB_8888);
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                int n2;
                if (this.num_extra_components == 1) {
                    final int n = this.getByte(this.width * this.height * this.num_components + this.width * i + j) & 0xFF;
                    n2 = (n | (n << 16 | 0xFF000000 | n << 8));
                }
                else {
                    n2 = 0;
                }
                bitmap.setPixel(j, this.height - 1 - i, n2);
            }
        }
        return bitmap;
    }
    
    @Override
    public ByteBuffer getExtraComponentsBuffer() {
        if (this.num_extra_components != 0 && this.rawBuffer != null) {
            final ByteBuffer readOnlyBuffer = this.rawBuffer.asReadOnlyBuffer();
            final int n = this.width * this.height * this.num_components;
            if (n >= 0 && n <= readOnlyBuffer.limit()) {
                readOnlyBuffer.position();
                return readOnlyBuffer;
            }
        }
        return null;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    public int getLoadedSize() {
        int capacity = 0;
        if (this.rawBuffer != null) {
            if (this.bytes_per_pixel == 888) {
                capacity = this.rawBuffer.capacity();
            }
            else if (this.bytes_per_pixel == 3 && this.num_components == 3) {
                capacity = this.width * this.height * (this.bytes_per_pixel + 1);
            }
            else {
                capacity = this.width * this.height * this.bytes_per_pixel;
            }
        }
        return capacity;
    }
    
    @Override
    public int getNumComponents() {
        return this.num_components;
    }
    
    @Override
    public int getRGB(final int n) {
        if (this.rawBuffer != null) {
            return (this.rawBuffer.get(n) << 16 & 0xFF0000) | (this.rawBuffer.get(n + 1) << 8 & 0xFF00) | (this.rawBuffer.get(n + 2) & 0xFF);
        }
        return 0;
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    public boolean hasAlphaLayer() {
        final boolean b = true;
        if (this.bytes_per_pixel == 888) {
            return false;
        }
        boolean b2 = b;
        if (this.num_components < 4) {
            if (this.num_components != 1) {
                return false;
            }
            b2 = b;
        }
        return b2;
        b2 = false;
        return b2;
    }
    
    public void putPixelRow(int i, final int[] array, final int n) {
        final int n2 = 0;
        final int n3 = 0;
        if (this.rawBuffer != null) {
            int n4 = this.width * this.num_components * i;
            if (this.num_components == 3) {
                int n5;
                ByteBuffer rawBuffer;
                int n6;
                ByteBuffer rawBuffer2;
                int n7;
                ByteBuffer rawBuffer3;
                for (i = n3; i < n; ++i) {
                    n5 = array[i];
                    rawBuffer = this.rawBuffer;
                    n6 = n4 + 1;
                    rawBuffer.put(n4, (byte)(n5 >> 16));
                    rawBuffer2 = this.rawBuffer;
                    n7 = n6 + 1;
                    rawBuffer2.put(n6, (byte)(n5 >> 8));
                    rawBuffer3 = this.rawBuffer;
                    n4 = n7 + 1;
                    rawBuffer3.put(n7, (byte)n5);
                }
            }
            else if (this.num_components == 4) {
                int n8;
                ByteBuffer rawBuffer4;
                int n9;
                ByteBuffer rawBuffer5;
                int n10;
                ByteBuffer rawBuffer6;
                int n11;
                ByteBuffer rawBuffer7;
                for (i = n2; i < n; ++i) {
                    n8 = array[i];
                    rawBuffer4 = this.rawBuffer;
                    n9 = n4 + 1;
                    rawBuffer4.put(n4, (byte)(n8 >> 16));
                    rawBuffer5 = this.rawBuffer;
                    n10 = n9 + 1;
                    rawBuffer5.put(n9, (byte)(n8 >> 8));
                    rawBuffer6 = this.rawBuffer;
                    n11 = n10 + 1;
                    rawBuffer6.put(n10, (byte)n8);
                    rawBuffer7 = this.rawBuffer;
                    n4 = n11 + 1;
                    rawBuffer7.put(n11, (byte)(n8 >> 24));
                }
            }
        }
    }
    
    public void setComponent(final int n, final byte b) {
        if (this.rawBuffer != null) {
            this.setComponentBuf(this.rawBuffer, this.width, this.height, this.num_components, this.num_extra_components, n, b);
        }
    }
    
    public enum ImageFormat
    {
        JPEG2000("JPEG2000", 1), 
        Raw("Raw", 0), 
        TGA("TGA", 2);
        
        private ImageFormat(final String name, final int ordinal) {
        }
    }
}
