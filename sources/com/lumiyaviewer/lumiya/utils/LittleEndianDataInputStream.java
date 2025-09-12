// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInputStream
    implements DataInput
{

    private byte buf[];
    private InputStream inputStream;

    public LittleEndianDataInputStream(InputStream inputstream)
    {
        buf = new byte[8];
        inputStream = inputstream;
    }

    public boolean readBoolean()
        throws IOException
    {
        boolean flag = false;
        int i = inputStream.read();
        if (i == -1)
        {
            throw new EOFException("End of stream");
        }
        if (i != 0)
        {
            flag = true;
        }
        return flag;
    }

    public byte readByte()
        throws IOException
    {
        int i = inputStream.read();
        if (i == -1)
        {
            throw new EOFException("End of stream");
        } else
        {
            return (byte)(i & 0xff);
        }
    }

    public char readChar()
        throws IOException
    {
        int i = inputStream.read();
        if (i == -1)
        {
            throw new EOFException("End of stream");
        } else
        {
            return (char)(i & 0xff);
        }
    }

    public double readDouble()
        throws IOException
    {
        return Double.longBitsToDouble(readLong());
    }

    public float readFloat()
        throws IOException
    {
        return Float.intBitsToFloat(readInt());
    }

    public void readFully(byte abyte0[])
        throws IOException
    {
        if (inputStream.read(abyte0, 0, abyte0.length) != abyte0.length)
        {
            throw new EOFException("End of stream");
        } else
        {
            return;
        }
    }

    public void readFully(byte abyte0[], int i, int j)
        throws IOException
    {
        if (inputStream.read(abyte0, i, j) != j)
        {
            throw new EOFException("End of stream");
        } else
        {
            return;
        }
    }

    public int readInt()
        throws IOException
    {
        if (inputStream.read(buf, 0, 4) != 4)
        {
            throw new EOFException("End of stream");
        } else
        {
            return buf[3] << 24 | (buf[2] & 0xff) << 16 | (buf[1] & 0xff) << 8 | buf[0] & 0xff;
        }
    }

    public String readLine()
        throws IOException
    {
        StringBuilder stringbuilder = new StringBuilder();
        do
        {
            char c = readChar();
            if (c == '\n' || c == '\r')
            {
                return stringbuilder.toString();
            }
            stringbuilder.append(c);
        } while (true);
    }

    public long readLong()
        throws IOException
    {
        if (inputStream.read(buf, 0, 8) != 8)
        {
            throw new EOFException("End of stream");
        } else
        {
            return (long)buf[7] << 56 | (long)(buf[6] & 0xff) << 48 | (long)(buf[5] & 0xff) << 40 | (long)(buf[4] & 0xff) << 32 | (long)(buf[3] & 0xff) << 24 | (long)(buf[2] & 0xff) << 16 | (long)(buf[1] & 0xff) << 8 | (long)(buf[0] & 0xff);
        }
    }

    public short readShort()
        throws IOException
    {
        if (inputStream.read(buf, 0, 2) != 2)
        {
            throw new EOFException("End of stream");
        } else
        {
            return (short)((buf[1] & 0xff) << 8 | buf[0] & 0xff);
        }
    }

    public String readUTF()
        throws IOException
    {
        return readLine();
    }

    public int readUnsignedByte()
        throws IOException
    {
        int i = inputStream.read();
        if (i == -1)
        {
            throw new EOFException("End of stream");
        } else
        {
            return i & 0xff;
        }
    }

    public int readUnsignedShort()
        throws IOException
    {
        if (inputStream.read(buf, 0, 2) != 2)
        {
            throw new EOFException("End of stream");
        } else
        {
            return (buf[1] & 0xff) << 8 | buf[0] & 0xff;
        }
    }

    public LLVector3 readVector3()
        throws IOException
    {
        return new LLVector3(readFloat(), readFloat(), readFloat());
    }

    public String readZeroTerminatedString()
        throws IOException
    {
        StringBuilder stringbuilder = new StringBuilder();
        do
        {
            char c = readChar();
            if (c == 0)
            {
                return stringbuilder.toString();
            }
            stringbuilder.append(c);
        } while (true);
    }

    public int skipBytes(int i)
        throws IOException
    {
        return (int)inputStream.skip(i);
    }
}
