// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.InternPool;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.textures:
//            SLTextureEntryFace, MutableSLTextureEntryFace

public class SLTextureEntry
{

    public static final int MAX_FACES = 32;
    private static final SLTextureEntryFace emptyFaces[] = new SLTextureEntryFace[0];
    private static final InternPool pool = new InternPool();
    private final SLTextureEntryFace DefaultTexture;
    private final SLTextureEntryFace FaceTextures[];
    private final int faceMask;
    private final int hashValue;

    private SLTextureEntry(SLTextureEntryFace sltextureentryface, SLTextureEntryFace asltextureentryface[])
    {
        int i = 0;
        super();
        DefaultTexture = sltextureentryface;
        FaceTextures = asltextureentryface;
        int j;
        int k;
        for (j = 0; i < asltextureentryface.length; j = k)
        {
            k = j;
            if (asltextureentryface[i] != null)
            {
                k = j | 1 << i;
            }
            i++;
        }

        faceMask = j;
        hashValue = getHashValue();
    }

    private SLTextureEntry(ByteBuffer bytebuffer, int i)
    {
        MutableSLTextureEntryFace mutablesltextureentryface;
        MutableSLTextureEntryFace amutablesltextureentryface[];
        int l2;
        boolean flag;
        flag = false;
        super();
        mutablesltextureentryface = new MutableSLTextureEntryFace(-1);
        if (bytebuffer.limit() - bytebuffer.position() < 16)
        {
            DefaultTexture = SLTextureEntryFace.create(mutablesltextureentryface);
            FaceTextures = emptyFaces;
            faceMask = 0;
            hashValue = getHashValue();
            return;
        }
        amutablesltextureentryface = new MutableSLTextureEntryFace[32];
        int ai[] = new int[1];
        int ai1[] = new int[1];
        mutablesltextureentryface.setTextureID(UUIDPool.getUUID(getUUID(bytebuffer)));
        do
        {
            int j3 = ReadFaceBitfield(bytebuffer, ai1);
            if (j3 == 0)
            {
                break;
            }
            UUID uuid = UUIDPool.getUUID(getUUID(bytebuffer));
            i = 1;
            int j = 0;
            while (j < ai1[0]) 
            {
                if ((j3 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, j, ai).setTextureID(uuid);
                }
                j++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setRGBA(bytebuffer.getInt());
        do
        {
            int k3 = ReadFaceBitfield(bytebuffer, ai1);
            if (k3 == 0)
            {
                break;
            }
            int l5 = bytebuffer.getInt();
            i = 1;
            int k = 0;
            while (k < ai1[0]) 
            {
                if ((k3 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, k, ai).setRGBA(l5);
                }
                k++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setRepeatU(bytebuffer.getFloat());
        do
        {
            int l3 = ReadFaceBitfield(bytebuffer, ai1);
            if (l3 == 0)
            {
                break;
            }
            float f = bytebuffer.getFloat();
            i = 1;
            int l = 0;
            while (l < ai1[0]) 
            {
                if ((l3 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, l, ai).setRepeatU(f);
                }
                l++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setRepeatV(bytebuffer.getFloat());
        do
        {
            int i4 = ReadFaceBitfield(bytebuffer, ai1);
            if (i4 == 0)
            {
                break;
            }
            float f1 = bytebuffer.getFloat();
            i = 1;
            int i1 = 0;
            while (i1 < ai1[0]) 
            {
                if ((i4 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, i1, ai).setRepeatV(f1);
                }
                i1++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setOffsetU(getOffset(bytebuffer));
        do
        {
            int j4 = ReadFaceBitfield(bytebuffer, ai1);
            if (j4 == 0)
            {
                break;
            }
            float f2 = getOffset(bytebuffer);
            i = 1;
            int j1 = 0;
            while (j1 < ai1[0]) 
            {
                if ((j4 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, j1, ai).setOffsetU(f2);
                }
                j1++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setOffsetV(getOffset(bytebuffer));
        do
        {
            int k4 = ReadFaceBitfield(bytebuffer, ai1);
            if (k4 == 0)
            {
                break;
            }
            float f3 = getOffset(bytebuffer);
            i = 1;
            int k1 = 0;
            while (k1 < ai1[0]) 
            {
                if ((k4 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, k1, ai).setOffsetV(f3);
                }
                k1++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setRotation(getRotation(bytebuffer));
        do
        {
            int l4 = ReadFaceBitfield(bytebuffer, ai1);
            if (l4 == 0)
            {
                break;
            }
            float f4 = getRotation(bytebuffer);
            i = 1;
            int l1 = 0;
            while (l1 < ai1[0]) 
            {
                if ((l4 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, l1, ai).setRotation(f4);
                }
                l1++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setMaterial(bytebuffer.get());
        do
        {
            int i5 = ReadFaceBitfield(bytebuffer, ai1);
            if (i5 == 0)
            {
                break;
            }
            byte byte0 = bytebuffer.get();
            i = 1;
            int i2 = 0;
            while (i2 < ai1[0]) 
            {
                if ((i5 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, i2, ai).setMaterial(byte0);
                }
                i2++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setMedia(bytebuffer.get());
        do
        {
            int j5 = ReadFaceBitfield(bytebuffer, ai1);
            if (j5 == 0)
            {
                break;
            }
            byte byte1 = bytebuffer.get();
            i = 1;
            int j2 = 0;
            while (j2 < ai1[0]) 
            {
                if ((j5 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, j2, ai).setMedia(byte1);
                }
                j2++;
                i <<= 1;
            }
        } while (true);
        mutablesltextureentryface.setGlow(getGlow(bytebuffer));
        do
        {
            int k5 = ReadFaceBitfield(bytebuffer, ai1);
            if (k5 == 0)
            {
                break;
            }
            float f5 = getGlow(bytebuffer);
            i = 1;
            int k2 = 0;
            while (k2 < ai1[0]) 
            {
                if ((k5 & i) != 0)
                {
                    CreateFace(amutablesltextureentryface, k2, ai).setGlow(f5);
                }
                k2++;
                i <<= 1;
            }
        } while (true);
        faceMask = ai[0];
        i = 0;
        l2 = -1;
_L3:
        if (i >= 33)
        {
            break MISSING_BLOCK_LABEL_955;
        }
        if ((faceMask & l2) != 0) goto _L2; else goto _L1
_L1:
        DefaultTexture = SLTextureEntryFace.create(mutablesltextureentryface);
        if (i == 0)
        {
            FaceTextures = emptyFaces;
        } else
        {
            FaceTextures = new SLTextureEntryFace[i];
            int i3 = ((flag) ? 1 : 0);
            while (i3 < i) 
            {
                FaceTextures[i3] = SLTextureEntryFace.create(amutablesltextureentryface[i3]);
                i3++;
            }
        }
        hashValue = getHashValue();
        return;
_L2:
        l2 <<= 1;
        i++;
          goto _L3
        i = 0;
          goto _L1
    }

    private static MutableSLTextureEntryFace CreateFace(MutableSLTextureEntryFace amutablesltextureentryface[], int i, int ai[])
    {
        if (i >= 32)
        {
            return null;
        }
        if (amutablesltextureentryface[i] != null)
        {
            return amutablesltextureentryface[i];
        } else
        {
            ai[0] = ai[0] | 1 << i;
            amutablesltextureentryface[i] = new MutableSLTextureEntryFace(0);
            return amutablesltextureentryface[i];
        }
    }

    private int ReadFaceBitfield(ByteBuffer bytebuffer, int ai[])
    {
        ai[0] = 0;
        if (bytebuffer.position() >= bytebuffer.limit())
        {
            return 0;
        }
        int i = 0;
        int j;
        byte byte0;
        do
        {
            byte0 = bytebuffer.get();
            j = i << 7 | byte0 & 0x7f;
            ai[0] = ai[0] + 7;
            i = j;
        } while ((byte0 & 0x80) != 0);
        return j;
    }

    private void WriteFaceBitfield(ByteBuffer bytebuffer, int i)
    {
        int j;
        j = 0;
        int k = i;
        do
        {
            if (j >= 6)
            {
                break MISSING_BLOCK_LABEL_180;
            }
            byte byte0;
            byte byte1;
            int l;
            if ((k & 0xffffff80) == 0)
            {
                j++;
                break MISSING_BLOCK_LABEL_27;
            }
            k = k >> 7 & 0x1ffffff;
            j++;
        } while (true);
_L2:
        Debug.Log(String.format("WriteFaceBitfield: faceBits = 0x%08x, count %d", new Object[] {
            Integer.valueOf(i), Integer.valueOf(j)
        }));
        k = 0;
        l = (j - 1) * 7;
        for (; k < j; k++)
        {
            byte1 = (byte)(i >> l & 0x7f);
            byte0 = byte1;
            if (k != j - 1)
            {
                byte0 = (byte)(byte1 | 0x80);
            }
            Debug.Log(String.format("WriteFaceBitfield: i = %d, shift = %d, byte 0x%02x", new Object[] {
                Integer.valueOf(k), Integer.valueOf(l), Byte.valueOf(byte0)
            }));
            bytebuffer.put(byte0);
            l -= 7;
        }

        return;
        j = 0;
        if (true) goto _L2; else goto _L1
_L1:
    }

    public static SLTextureEntry create(SLTextureEntryFace sltextureentryface, SLTextureEntryFace asltextureentryface[])
    {
        return (SLTextureEntry)pool.intern(new SLTextureEntry(sltextureentryface, asltextureentryface));
    }

    public static SLTextureEntry create(ByteBuffer bytebuffer, int i)
    {
        return (SLTextureEntry)pool.intern(new SLTextureEntry(bytebuffer, i));
    }

    private static float getGlow(ByteBuffer bytebuffer)
    {
        return (float)bytebuffer.get() / 255F;
    }

    private int getHashValue()
    {
        int i = 0;
        int j = faceMask;
        int k = DefaultTexture.hashCode();
        k = FaceTextures.length + (j + k);
        j = 1;
        while (i < FaceTextures.length) 
        {
            int l = k;
            if ((faceMask & j) != 0)
            {
                l = k + FaceTextures[i].hashCode();
            }
            j <<= 1;
            i++;
            k = l;
        }
        return k;
    }

    private static float getOffset(ByteBuffer bytebuffer)
    {
        return (float)bytebuffer.getShort() / 32767F;
    }

    private static float getRotation(ByteBuffer bytebuffer)
    {
        return ((float)bytebuffer.getShort() / 32767F) * 3.141593F * 2.0F;
    }

    private static UUID getUUID(ByteBuffer bytebuffer)
    {
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        UUID uuid = new UUID(bytebuffer.getLong(), bytebuffer.getLong());
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        return uuid;
    }

    private static void putGlow(ByteBuffer bytebuffer, float f)
    {
        bytebuffer.put((byte)(int)(255F * f));
    }

    private static void putOffset(ByteBuffer bytebuffer, float f)
    {
        bytebuffer.putShort((short)(int)(32767F * f));
    }

    private static void putRotation(ByteBuffer bytebuffer, float f)
    {
        bytebuffer.putShort((short)(int)((f / 6.283185F) * 32767F));
    }

    private static void putUUID(ByteBuffer bytebuffer, UUID uuid)
    {
        long l = 0L;
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        long l1;
        if (uuid != null)
        {
            l1 = uuid.getMostSignificantBits();
            l = uuid.getLeastSignificantBits();
        } else
        {
            l1 = 0L;
        }
        bytebuffer.putLong(l1);
        bytebuffer.putLong(l);
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public final SLTextureEntryFace GetDefaultTexture()
    {
        return DefaultTexture;
    }

    public final SLTextureEntryFace GetFace(int i)
    {
        if (i >= 32)
        {
            return null;
        }
        if (i >= FaceTextures.length)
        {
            return DefaultTexture;
        }
        if (FaceTextures[i] != null)
        {
            return FaceTextures[i];
        } else
        {
            return DefaultTexture;
        }
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof SLTextureEntry))
        {
            return false;
        }
        obj = (SLTextureEntry)obj;
        if (faceMask != ((SLTextureEntry) (obj)).faceMask)
        {
            return false;
        }
        if (FaceTextures.length != ((SLTextureEntry) (obj)).FaceTextures.length)
        {
            return false;
        }
        if (!DefaultTexture.equals(((SLTextureEntry) (obj)).DefaultTexture))
        {
            return false;
        }
        int i = 0;
        int j = 1;
        for (; i < FaceTextures.length; i++)
        {
            if ((faceMask & j) != 0 && !FaceTextures[i].equals(((SLTextureEntry) (obj)).FaceTextures[i]))
            {
                return false;
            }
            j <<= 1;
        }

        return true;
    }

    public int getFaceMask()
    {
        return faceMask;
    }

    public final int hashCode()
    {
        return hashValue;
    }

    public boolean isSingleFace()
    {
        boolean flag = false;
        if (faceMask == 0)
        {
            flag = true;
        }
        return flag;
    }

    public byte[] packByteArray()
    {
        ByteBuffer bytebuffer = ByteBuffer.allocate(65535);
        putUUID(bytebuffer, DefaultTexture.textureID());
        int j = 0;
        while (j < FaceTextures.length) 
        {
            if (FaceTextures[j] != null)
            {
                byte abyte0[];
                int i;
                if (DefaultTexture.textureID() == null)
                {
                    i = 1;
                } else
                if (!FaceTextures[j].getTextureID(DefaultTexture).equals(DefaultTexture.textureID()))
                {
                    i = 1;
                } else
                {
                    i = 0;
                }
                if (i != 0)
                {
                    WriteFaceBitfield(bytebuffer, 1 << j);
                    putUUID(bytebuffer, FaceTextures[j].getTextureID(DefaultTexture));
                }
            }
            j++;
        }
        WriteFaceBitfield(bytebuffer, 0);
        bytebuffer.putInt(DefaultTexture.rgba());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getRGBA(DefaultTexture) != DefaultTexture.rgba())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                bytebuffer.putInt(FaceTextures[i].getRGBA(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        bytebuffer.putFloat(DefaultTexture.repeatU());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getRepeatU(DefaultTexture) != DefaultTexture.repeatU())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                bytebuffer.putFloat(FaceTextures[i].getRepeatU(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        bytebuffer.putFloat(DefaultTexture.repeatV());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getRepeatV(DefaultTexture) != DefaultTexture.repeatV())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                bytebuffer.putFloat(FaceTextures[i].getRepeatV(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        putOffset(bytebuffer, DefaultTexture.offsetU());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getOffsetU(DefaultTexture) != DefaultTexture.offsetU())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                putOffset(bytebuffer, FaceTextures[i].getOffsetU(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        putOffset(bytebuffer, DefaultTexture.offsetV());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getOffsetV(DefaultTexture) != DefaultTexture.offsetV())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                putOffset(bytebuffer, FaceTextures[i].getOffsetV(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        putRotation(bytebuffer, DefaultTexture.rotation());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getRotation(DefaultTexture) != DefaultTexture.rotation())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                putRotation(bytebuffer, FaceTextures[i].getRotation(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        bytebuffer.put(DefaultTexture.materialb());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getMaterial(DefaultTexture) != DefaultTexture.materialb())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                bytebuffer.put(FaceTextures[i].getMaterial(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        bytebuffer.put(DefaultTexture.mediab());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getMedia(DefaultTexture) != DefaultTexture.mediab())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                bytebuffer.put(FaceTextures[i].getMedia(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        putGlow(bytebuffer, DefaultTexture.glow());
        for (i = 0; i < FaceTextures.length; i++)
        {
            if (FaceTextures[i] != null && FaceTextures[i].getGlow(DefaultTexture) != DefaultTexture.glow())
            {
                WriteFaceBitfield(bytebuffer, 1 << i);
                putGlow(bytebuffer, FaceTextures[i].getGlow(DefaultTexture));
            }
        }

        WriteFaceBitfield(bytebuffer, 0);
        abyte0 = new byte[bytebuffer.position()];
        bytebuffer.position(0);
        bytebuffer.get(abyte0);
        Debug.DumpBuffer("Baking: TEpacked: ", abyte0);
        return abyte0;
    }

}
