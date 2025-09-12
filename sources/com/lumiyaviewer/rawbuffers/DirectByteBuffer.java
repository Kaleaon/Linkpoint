// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.rawbuffers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class DirectByteBuffer
{

    private ByteBuffer buf;

    public DirectByteBuffer(int i)
    {
        buf = allocate(i);
        if (buf != null)
        {
            buf.order(ByteOrder.nativeOrder());
            return;
        } else
        {
            throw new OutOfMemoryError((new StringBuilder()).append("DirectByteBuffer: Failed to allocate ").append(i).append(" bytes").toString());
        }
    }

    public DirectByteBuffer(DirectByteBuffer directbytebuffer)
    {
        buf = allocate(directbytebuffer.asByteBuffer().capacity());
        if (buf != null)
        {
            buf.order(ByteOrder.nativeOrder());
            copyFrom(0, directbytebuffer, 0, directbytebuffer.asByteBuffer().capacity());
            return;
        } else
        {
            throw new OutOfMemoryError((new StringBuilder()).append("DirectByteBuffer: Failed to allocate ").append(directbytebuffer.asByteBuffer().capacity()).append(" bytes").toString());
        }
    }

    private native ByteBuffer allocate(int i);

    private native void copyByteArray(ByteBuffer bytebuffer, int i, byte abyte0[], int j, int k);

    private native void copyFloatArray(ByteBuffer bytebuffer, int i, float af[], int j, int k);

    private native void copyPart(ByteBuffer bytebuffer, ByteBuffer bytebuffer1, int i, int j, int k);

    private native void copyShortArray(ByteBuffer bytebuffer, int i, short aword0[], int j, int k);

    private native void copyShortArrayOffset(ByteBuffer bytebuffer, int i, short aword0[], int j, int k, short word0);

    private native void release(ByteBuffer bytebuffer);

    public static int zeroDecode(byte abyte0[], int i, int j, byte abyte1[], int k, int l)
        throws IndexOutOfBoundsException
    {
        k = zeroDecodeArray(abyte0, i, j, abyte1, k, l);
        if (k >= 0)
        {
            return k;
        } else
        {
            throw new IndexOutOfBoundsException((new StringBuilder()).append("zeroDecode: out of dest buffer, destStart ").append(Integer.toString(i)).append(" destMaxLen ").append(Integer.toString(j)).toString());
        }
    }

    private static native int zeroDecodeArray(byte abyte0[], int i, int j, byte abyte1[], int k, int l);

    public ByteBuffer asByteBuffer()
    {
        return buf;
    }

    public FloatBuffer asFloatBuffer()
    {
        return buf.asFloatBuffer();
    }

    public IntBuffer asIntBuffer()
    {
        return buf.asIntBuffer();
    }

    public ShortBuffer asShortBuffer()
    {
        return buf.asShortBuffer();
    }

    public void copyFrom(int i, DirectByteBuffer directbytebuffer, int j, int k)
    {
        copyPart(buf, directbytebuffer.buf, i, j, k);
    }

    public void copyFromFloat(int i, DirectByteBuffer directbytebuffer, int j, int k)
    {
        copyPart(buf, directbytebuffer.buf, i * 4, j * 4, k * 4);
    }

    public void copyFromShort(int i, DirectByteBuffer directbytebuffer, int j, int k)
    {
        copyPart(buf, directbytebuffer.buf, i * 2, j * 2, k * 2);
    }

    protected void finalize()
        throws Throwable
    {
        if (buf != null)
        {
            release(buf);
            buf = null;
        }
        super.finalize();
    }

    public int getCapacity()
    {
        return buf.capacity();
    }

    public float getFloat(int i)
    {
        return buf.getFloat(i * 4);
    }

    public short getShort(int i)
    {
        return buf.getShort(i * 2);
    }

    public void loadFromByteArray(int i, byte abyte0[], int j, int k)
    {
        for (int l = i + k; i < 0 || i > buf.capacity() || l < 0 || l > buf.capacity();)
        {
            throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[] {
                Integer.valueOf(buf.capacity()), Integer.valueOf(i), Integer.valueOf(l)
            }));
        }

        copyByteArray(buf, i, abyte0, j, k);
    }

    public void loadFromFloatArray(int i, float af[], int j, int k)
    {
        int l = i * 4;
        for (int i1 = (i + k) * 4; l < 0 || l > buf.capacity() || i1 < 0 || i1 > buf.capacity();)
        {
            throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[] {
                Integer.valueOf(buf.capacity()), Integer.valueOf(l), Integer.valueOf(i1)
            }));
        }

        copyFloatArray(buf, i, af, j, k);
    }

    public void loadFromShortArray(int i, short aword0[], int j, int k)
    {
        int l = i * 2;
        for (int i1 = (i + k) * 2; l < 0 || l > buf.capacity() || i1 < 0 || i1 > buf.capacity();)
        {
            throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[] {
                Integer.valueOf(buf.capacity()), Integer.valueOf(l), Integer.valueOf(i1)
            }));
        }

        copyShortArray(buf, i, aword0, j, k);
    }

    public void loadFromShortArrayOffset(int i, short aword0[], int j, int k, short word0)
    {
        int l = i * 2;
        for (int i1 = (i + k) * 2; l < 0 || l > buf.capacity() || i1 < 0 || i1 > buf.capacity();)
        {
            throw new IndexOutOfBoundsException(String.format("capacity %d, posStart %d, posEnd %d", new Object[] {
                Integer.valueOf(buf.capacity()), Integer.valueOf(l), Integer.valueOf(i1)
            }));
        }

        copyShortArrayOffset(buf, i, aword0, j, k, word0);
    }

    public int position()
    {
        return buf.position();
    }

    public Buffer position(int i)
    {
        return buf.position(i);
    }

    public int positionFloat()
    {
        return buf.position() / 4;
    }

    public Buffer positionFloat(int i)
    {
        return buf.position(i * 4);
    }

    public Buffer positionShort(int i)
    {
        return buf.position(i * 2);
    }

    public void putFloat(float f)
    {
        buf.putFloat(f);
    }

    public void putFloat(int i, float f)
    {
        buf.putFloat(i * 4, f);
    }

    public void putRawFloat(int i, float f)
    {
        buf.putFloat(i, f);
    }

    public void putRawInt(int i, int j)
    {
        buf.putInt(i, j);
    }

    public void putShort(short word0)
    {
        buf.putShort(word0);
    }

    public void read(InputStream inputstream)
        throws IOException
    {
        ByteBuffer bytebuffer = asByteBuffer();
        byte abyte0[] = new byte[bytebuffer.capacity()];
        inputstream.read(abyte0);
        int i = bytebuffer.position();
        bytebuffer.position(0);
        bytebuffer.put(abyte0);
        bytebuffer.position(i);
    }

    static 
    {
        System.loadLibrary("rawbuf");
    }
}
