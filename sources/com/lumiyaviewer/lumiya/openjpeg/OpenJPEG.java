// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.openjpeg;

import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class OpenJPEG
    implements GLTexture
{
    public static final class ImageFormat extends Enum
    {

        private static final ImageFormat $VALUES[];
        public static final ImageFormat JPEG2000;
        public static final ImageFormat Raw;
        public static final ImageFormat TGA;

        public static ImageFormat valueOf(String s)
        {
            return (ImageFormat)Enum.valueOf(com/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat, s);
        }

        public static ImageFormat[] values()
        {
            return $VALUES;
        }

        static 
        {
            Raw = new ImageFormat("Raw", 0);
            JPEG2000 = new ImageFormat("JPEG2000", 1);
            TGA = new ImageFormat("TGA", 2);
            $VALUES = (new ImageFormat[] {
                Raw, JPEG2000, TGA
            });
        }

        private ImageFormat(String s, int i)
        {
            super(s, i);
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_openjpeg_2D_OpenJPEG$ImageFormatSwitchesValues[];
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

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_openjpeg_2D_OpenJPEG$ImageFormatSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_openjpeg_2D_OpenJPEG$ImageFormatSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_openjpeg_2D_OpenJPEG$ImageFormatSwitchesValues;
        }
        int ai[] = new int[ImageFormat.values().length];
        try
        {
            ai[ImageFormat.JPEG2000.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ImageFormat.Raw.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ImageFormat.TGA.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_openjpeg_2D_OpenJPEG$ImageFormatSwitchesValues = ai;
        return ai;
    }

    public OpenJPEG(int i, int j, int k, int l, int i1, int j1)
        throws OutOfMemoryError
    {
        mmapped = false;
        mmappedAddr = 0L;
        mmappedSize = 0L;
        width = i;
        height = j;
        num_components = k;
        num_extra_components = i1;
        bytes_per_pixel = l;
        rawBuffer = allocateNew(i, j, k, l, i1, j1);
        if (rawBuffer == null)
        {
            throw new OutOfMemoryError("allocateNew() returned NULL");
        } else
        {
            TextureMemoryTracker.allocOpenJpegMemory(rawBuffer.capacity(), mmapped);
            return;
        }
    }

    public OpenJPEG(File file, int i, int j, boolean flag)
        throws IOException
    {
        mmapped = false;
        mmappedAddr = 0L;
        mmappedSize = 0L;
        if (file == null)
        {
            throw new IOException("Null source file");
        }
        rawBuffer = decompress(file.getAbsolutePath(), 0, 0, flag, i, j);
        if (rawBuffer == null)
        {
            throw new IOException((new StringBuilder()).append("Failed to decompress texture (").append(error_code).append(") ").append(file.getAbsolutePath()).toString());
        } else
        {
            TextureMemoryTracker.allocOpenJpegMemory(rawBuffer.capacity(), mmapped);
            return;
        }
    }

    public OpenJPEG(File file, TextureClass textureclass, ImageFormat imageformat, boolean flag)
        throws IOException
    {
        boolean flag1;
        boolean flag2;
        flag2 = true;
        super();
        mmapped = false;
        mmappedAddr = 0L;
        mmappedSize = 0L;
        if (file == null)
        {
            throw new IOException("Null source file");
        }
        Debug.Log((new StringBuilder()).append("OpenJPEG: decompressing ").append(file.getName()).append(" class ").append(textureclass).append(" format ").append(imageformat).toString());
        if (textureclass == TextureClass.Prim)
        {
            flag1 = flag ^ true;
        } else
        {
            flag1 = false;
        }
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_openjpeg_2D_OpenJPEG$ImageFormatSwitchesValues()[imageformat.ordinal()];
        JVM INSTR tableswitch 1 3: default 128
    //                   1 198
    //                   2 149
    //                   3 315;
           goto _L1 _L2 _L3 _L4
_L1:
        TextureMemoryTracker.allocOpenJpegMemory(rawBuffer.capacity(), mmapped);
        return;
_L3:
        rawBuffer = readRaw(file.getAbsolutePath());
        if (rawBuffer == null)
        {
            throw new IOException((new StringBuilder()).append("Failed to read raw texture ").append(file.getAbsolutePath()).toString());
        }
        continue; /* Loop/switch isn't completed */
_L2:
        imageformat = file.getAbsolutePath();
        int i;
        byte byte0;
        if (flag1)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        if (flag1)
        {
            byte0 = 6;
        } else
        {
            byte0 = 0;
        }
        if (textureclass == TextureClass.Prim)
        {
            flag = flag2;
        } else
        {
            flag = false;
        }
        rawBuffer = decompress(imageformat, i, byte0, flag, 0, 0);
        if (rawBuffer == null)
        {
            throw new IOException((new StringBuilder()).append("Failed to decompress texture (").append(error_code).append(") ").append(file.getAbsolutePath()).toString());
        }
        if (true) goto _L1; else goto _L4
_L4:
        throw new IOException("TGA not supported for non-asset files");
    }

    public OpenJPEG(InputStream inputstream, ImageFormat imageformat, boolean flag, boolean flag1, float f, float f1, boolean flag2)
        throws IOException
    {
        mmapped = false;
        mmappedAddr = 0L;
        mmappedSize = 0L;
        if (imageformat == ImageFormat.TGA)
        {
            imageformat = new byte[inputstream.available()];
            inputstream.read(imageformat);
            rawBuffer = decompressTGA(imageformat, flag, flag1, f, f1, flag2);
            if (rawBuffer == null)
            {
                throw new IOException("Failed to decompress TGA texture.");
            } else
            {
                TextureMemoryTracker.allocOpenJpegMemory(rawBuffer.capacity(), mmapped);
                return;
            }
        } else
        {
            throw new IOException("Unsupported format for image stream.");
        }
    }

    private native ByteBuffer allocateNew(int i, int j, int k, int l, int i1, int j1);

    private native ByteBuffer allocateRaw(int i);

    public static native void applyFlexibleMorph(ByteBuffer bytebuffer, ByteBuffer bytebuffer1, int i, float af[]);

    public static native void applyMeshMorph(float f, ByteBuffer bytebuffer, ByteBuffer bytebuffer1, int i, ByteBuffer bytebuffer2, ByteBuffer bytebuffer3, ByteBuffer bytebuffer4, int j, 
            int k, int l, ByteBuffer bytebuffer5);

    public static native void applyMorphingTransform(int i, ByteBuffer bytebuffer, ByteBuffer bytebuffer1, ByteBuffer bytebuffer2, int ai[], float af[]);

    public static native void applyRiggedMeshMorph(ByteBuffer bytebuffer, int i, float af[], float af1[], ByteBuffer bytebuffer1, ByteBuffer bytebuffer2, int j);

    public static OpenJPEG bakeTerrain(int i, int j, OpenJPEG aopenjpeg[], float af[], int k, int l)
    {
        OpenJPEG openjpeg = new OpenJPEG(i, j, 3, 2, 0, 0);
        ByteBuffer abytebuffer[] = new ByteBuffer[aopenjpeg.length];
        int ai[] = new int[aopenjpeg.length];
        int ai1[] = new int[aopenjpeg.length];
        int ai2[] = new int[aopenjpeg.length];
        int i1 = 0;
        while (i1 < aopenjpeg.length) 
        {
            if (aopenjpeg[i1] != null)
            {
                abytebuffer[i1] = aopenjpeg[i1].rawBuffer;
                ai[i1] = aopenjpeg[i1].width;
                ai1[i1] = aopenjpeg[i1].height;
                ai2[i1] = aopenjpeg[i1].num_components;
            } else
            {
                abytebuffer[i1] = null;
                ai[i1] = 0;
                ai1[i1] = 0;
                ai2[i1] = 0;
            }
            i1++;
        }
        openjpeg.bakeTerrainRaw(openjpeg.rawBuffer, i, j, abytebuffer, ai, ai1, ai2, af, k, l);
        return openjpeg;
    }

    private native void bakeTerrainRaw(ByteBuffer bytebuffer, int i, int j, ByteBuffer abytebuffer[], int ai[], int ai1[], int ai2[], 
            float af[], int k, int l);

    public static native void calcFlexiSections(float af[], int i, float af1[], float af2[], int j, float f, float f1, float f2, 
            float f3, float f4, float f5, float f6, float f7, float f8, float f9, 
            boolean flag);

    public static native int checkFrustrumOcclusion(float af[], float af1[], float f, float f1, float f2);

    private native ByteBuffer decompress(String s, int i, int j, boolean flag, int k, int l);

    private native ByteBuffer decompressTGA(byte abyte0[], boolean flag, boolean flag1, float f, float f1, boolean flag2);

    private native void drawBuf(ByteBuffer bytebuffer, int i, int j, int k, ByteBuffer bytebuffer1, int l, int i1, 
            int j1, int k1, boolean flag, boolean flag1, boolean flag2, boolean flag3);

    public static native int getFlexiDataSize(int i);

    public static native void meshPrepareInfluenceBuffer(ByteBuffer bytebuffer, int i, ByteBuffer bytebuffer1, int j);

    public static native void meshPrepareSeparateInfluenceBuffer(ByteBuffer bytebuffer, int i, ByteBuffer bytebuffer1, ByteBuffer bytebuffer2, int j);

    private native ByteBuffer readRaw(String s);

    private native void release(ByteBuffer bytebuffer);

    private native void setComponentBuf(ByteBuffer bytebuffer, int i, int j, int k, int l, int i1, byte byte0);

    private native int writeJPEG2K(String s, ByteBuffer bytebuffer, int i, int j, int k, int l);

    private native void writeRaw(ByteBuffer bytebuffer, String s);

    public boolean CompressETC1()
        throws IOException
    {
        if (android.os.Build.VERSION.SDK_INT >= 8 && rawBuffer != null && num_components == 3 && num_extra_components == 0 && (bytes_per_pixel == 2 || bytes_per_pixel == 3))
        {
            int i = ETC1.getEncodedDataSize(width, height);
            ByteBuffer bytebuffer = allocateRaw(i);
            if (bytebuffer == null)
            {
                throw new IOException((new StringBuilder()).append("Out of memory for ").append(Integer.toString(i)).append(" allocation").toString());
            } else
            {
                ETC1.encodeImage(rawBuffer, width, height, bytes_per_pixel, width * bytes_per_pixel, bytebuffer);
                TextureMemoryTracker.releaseOpenJpegMemory(rawBuffer.capacity(), mmapped);
                release(rawBuffer);
                rawBuffer = bytebuffer;
                TextureMemoryTracker.allocOpenJpegMemory(bytebuffer.capacity(), mmapped);
                bytes_per_pixel = 888;
                return true;
            }
        } else
        {
            return false;
        }
    }

    public void SaveJPEG2K(File file)
        throws IOException
    {
        if (rawBuffer != null && writeJPEG2K(file.getAbsolutePath(), rawBuffer, width, height, num_components, num_extra_components) != 0)
        {
            throw new IOException((new StringBuilder()).append("Failed to save JPEG2k to ").append(file.getAbsolutePath()).toString());
        } else
        {
            return;
        }
    }

    public void SaveRaw(File file)
    {
        if (rawBuffer != null)
        {
            writeRaw(rawBuffer, file.getAbsolutePath());
        }
    }

    public void SaveToFile(File file)
    {
        try
        {
            file = new FileOutputStream(file, false);
            file.getChannel().write(rawBuffer);
            file.close();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (File file)
        {
            file.printStackTrace();
        }
    }

    public int SetAsImmutableTexture()
    {
        if (rawBuffer == null) goto _L2; else goto _L1
_L1:
        if (bytes_per_pixel != 888) goto _L4; else goto _L3
_L3:
        GLES30.glTexStorage2D(3553, 1, 37492, width, height);
        GLES30.glCompressedTexSubImage2D(3553, 0, 0, 0, width, height, 37492, rawBuffer.capacity(), rawBuffer);
_L2:
        return getLoadedSize();
_L4:
        int i;
        char c;
        int j;
        j = 5121;
        switch (num_components)
        {
        case 2: // '\002'
        default:
            return SetAsTexture();

        case 1: // '\001'
            break MISSING_BLOCK_LABEL_286;

        case 3: // '\003'
            break; /* Loop/switch isn't completed */

        case 4: // '\004'
            i = 32856;
            c = '\u1908';
            break;
        }
_L6:
        if (bytes_per_pixel == 2 && num_components == 3)
        {
            j = 33635;
        }
        GLES30.glTexStorage2D(3553, 1, i, width, height);
        GLES30.glTexSubImage2D(3553, 0, 0, 0, width, height, c, j, rawBuffer);
        if (num_components == 1)
        {
            GLES30.glTexParameteri(3553, 36418, 1);
            GLES30.glTexParameteri(3553, 36419, 1);
            GLES30.glTexParameteri(3553, 36420, 1);
            GLES30.glTexParameteri(3553, 36421, 6403);
        }
        if (true) goto _L2; else goto _L5
_L5:
        int k;
        char c1;
        if (bytes_per_pixel == 2)
        {
            k = 36194;
        } else
        {
            k = 32849;
        }
        c1 = '\u1907';
        i = k;
        c = c1;
        if (bytes_per_pixel == 2)
        {
            j = 33635;
            i = k;
            c = c1;
        }
          goto _L6
        i = 33321;
        c = '\u1903';
          goto _L6
    }

    public int SetAsTexture()
    {
        if (rawBuffer == null) goto _L2; else goto _L1
_L1:
        if (bytes_per_pixel != 888) goto _L4; else goto _L3
_L3:
        GLES10.glCompressedTexImage2D(3553, 0, 36196, width, height, 0, rawBuffer.capacity(), rawBuffer);
_L2:
        return getLoadedSize();
_L4:
        char c = '\u1401';
        num_components;
        JVM INSTR tableswitch 1 4: default 92
    //                   1 161
    //                   2 92
    //                   3 154
    //                   4 147;
           goto _L5 _L6 _L5 _L7 _L8
_L6:
        break MISSING_BLOCK_LABEL_161;
_L8:
        break; /* Loop/switch isn't completed */
_L5:
        int i = num_components;
_L10:
        int j = c;
        if (bytes_per_pixel == 2)
        {
            j = c;
            if (num_components == 3)
            {
                j = 33635;
            }
        }
        GLES10.glTexImage2D(3553, 0, i, width, height, 0, i, j, rawBuffer);
        if (true) goto _L2; else goto _L9
_L9:
        i = 6408;
          goto _L10
_L7:
        i = 6407;
          goto _L10
        i = 6406;
          goto _L10
    }

    public int SetAsTextureTarget(int i)
    {
        if (rawBuffer == null) goto _L2; else goto _L1
_L1:
        char c;
        if (bytes_per_pixel == 888)
        {
            int j = rawBuffer.capacity();
            GLES20.glCompressedTexImage2D(i, 0, 36196, width, height, 0, j, rawBuffer);
            return j;
        }
        c = '\u1401';
        num_components;
        JVM INSTR tableswitch 1 4: default 88
    //                   1 169
    //                   2 88
    //                   3 162
    //                   4 155;
           goto _L3 _L4 _L3 _L5 _L6
_L3:
        int k = num_components;
_L7:
        int l = c;
        if (bytes_per_pixel == 2)
        {
            l = c;
            if (num_components == 3)
            {
                l = 33635;
            }
        }
        GLES20.glTexImage2D(i, 0, k, width, height, 0, k, l, rawBuffer);
        return width * height * bytes_per_pixel;
_L6:
        k = 6408;
        continue; /* Loop/switch isn't completed */
_L5:
        k = 6407;
        continue; /* Loop/switch isn't completed */
_L4:
        k = 6406;
        if (true) goto _L7; else goto _L2
_L2:
        return 0;
    }

    public void blendAlpha(OpenJPEG openjpeg, boolean flag)
    {
        if (rawBuffer != null && openjpeg.rawBuffer != null && num_components >= 4 && openjpeg.num_components >= 4)
        {
            drawBuf(rawBuffer, width, height, num_components, openjpeg.rawBuffer, openjpeg.width, openjpeg.height, openjpeg.num_components, 0, false, true, flag, false);
        }
    }

    public void draw(OpenJPEG openjpeg, int i, boolean flag)
    {
        if (rawBuffer != null && openjpeg.rawBuffer != null)
        {
            drawBuf(rawBuffer, width, height, num_components, openjpeg.rawBuffer, openjpeg.width, openjpeg.height, openjpeg.num_components, i, flag, false, false, false);
        }
    }

    public void drawBump(OpenJPEG openjpeg, int i, boolean flag, boolean flag1)
    {
        if (rawBuffer != null && openjpeg.rawBuffer != null && num_extra_components >= 1 && openjpeg.num_components >= 4)
        {
            drawBuf(rawBuffer, width, height, num_components, openjpeg.rawBuffer, openjpeg.width, openjpeg.height, openjpeg.num_components, 0, false, false, flag1, true);
        }
    }

    protected void finalize()
        throws Throwable
    {
        if (rawBuffer != null)
        {
            TextureMemoryTracker.releaseOpenJpegMemory(rawBuffer.capacity(), mmapped);
            release(rawBuffer);
            rawBuffer = null;
        }
        super.finalize();
    }

    public Bitmap getAsBitmap()
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        if (bitmap == null)
        {
            return null;
        }
        for (int i = 0; i < height; i++)
        {
            int j = 0;
            while (j < width) 
            {
                int k;
                if (num_components == 1)
                {
                    k = getByte((width * i + j) * num_components) & 0xff;
                    k |= k << 16 | 0xff000000 | k << 8;
                } else
                {
                    byte byte0 = getByte((width * i + j) * num_components + 0);
                    byte byte1 = getByte((width * i + j) * num_components + 1);
                    byte byte2 = getByte((width * i + j) * num_components + 2);
                    k = 255;
                    if (num_components >= 4)
                    {
                        k = getByte((width * i + j) * num_components + 3) & 0xff;
                    }
                    k = k << 24 | (byte0 & 0xff) << 16 | (byte1 & 0xff) << 8 | byte2 & 0xff;
                }
                bitmap.setPixel(j, height - 1 - i, k);
                j++;
            }
        }

        return bitmap;
    }

    public byte getByte(int i)
    {
        if (rawBuffer != null)
        {
            return rawBuffer.get(i);
        } else
        {
            return 0;
        }
    }

    public Bitmap getExtraAsBitmap()
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        int i = 0;
        while (i < height) 
        {
            int j = 0;
            while (j < width) 
            {
                int k;
                if (num_extra_components == 1)
                {
                    k = getByte(width * height * num_components + width * i + j) & 0xff;
                    k |= k << 16 | 0xff000000 | k << 8;
                } else
                {
                    k = 0;
                }
                bitmap.setPixel(j, height - 1 - i, k);
                j++;
            }
            i++;
        }
        return bitmap;
    }

    public ByteBuffer getExtraComponentsBuffer()
    {
        if (num_extra_components != 0 && rawBuffer != null)
        {
            ByteBuffer bytebuffer = rawBuffer.asReadOnlyBuffer();
            int i = width * height * num_components;
            if (i >= 0 && i <= bytebuffer.limit())
            {
                bytebuffer.position(i);
                return bytebuffer;
            }
        }
        return null;
    }

    public int getHeight()
    {
        return height;
    }

    public int getLoadedSize()
    {
label0:
        {
            int i = 0;
            if (rawBuffer != null)
            {
                if (bytes_per_pixel != 888)
                {
                    break label0;
                }
                i = rawBuffer.capacity();
            }
            return i;
        }
        if (bytes_per_pixel == 3 && num_components == 3)
        {
            return width * height * (bytes_per_pixel + 1);
        } else
        {
            return width * height * bytes_per_pixel;
        }
    }

    public int getNumComponents()
    {
        return num_components;
    }

    public int getRGB(int i)
    {
        if (rawBuffer != null)
        {
            return rawBuffer.get(i) << 16 & 0xff0000 | rawBuffer.get(i + 1) << 8 & 0xff00 | rawBuffer.get(i + 2) & 0xff;
        } else
        {
            return 0;
        }
    }

    public int getWidth()
    {
        return width;
    }

    public boolean hasAlphaLayer()
    {
        return bytes_per_pixel != 888 && (num_components >= 4 || num_components == 1);
    }

    public void putPixelRow(int i, int ai[], int j)
    {
        int j1 = 0;
        boolean flag = false;
        if (rawBuffer != null)
        {
            int k = width * num_components * i;
            if (num_components == 3)
            {
                for (i = ((flag) ? 1 : 0); i < j; i++)
                {
                    int l = ai[i];
                    ByteBuffer bytebuffer = rawBuffer;
                    j1 = k + 1;
                    bytebuffer.put(k, (byte)(l >> 16));
                    bytebuffer = rawBuffer;
                    int l1 = j1 + 1;
                    bytebuffer.put(j1, (byte)(l >> 8));
                    bytebuffer = rawBuffer;
                    k = l1 + 1;
                    bytebuffer.put(l1, (byte)l);
                }

            } else
            if (num_components == 4)
            {
                for (i = j1; i < j; i++)
                {
                    int i1 = ai[i];
                    ByteBuffer bytebuffer1 = rawBuffer;
                    int k1 = k + 1;
                    bytebuffer1.put(k, (byte)(i1 >> 16));
                    bytebuffer1 = rawBuffer;
                    k = k1 + 1;
                    bytebuffer1.put(k1, (byte)(i1 >> 8));
                    bytebuffer1 = rawBuffer;
                    k1 = k + 1;
                    bytebuffer1.put(k, (byte)i1);
                    bytebuffer1 = rawBuffer;
                    k = k1 + 1;
                    bytebuffer1.put(k1, (byte)(i1 >> 24));
                }

            }
        }
    }

    public void setComponent(int i, byte byte0)
    {
        if (rawBuffer != null)
        {
            setComponentBuf(rawBuffer, width, height, num_components, num_extra_components, i, byte0);
        }
    }

    static 
    {
        System.loadLibrary("openjpeg");
    }
}
