// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.textures;

import java.nio.ByteOrder;
import com.lumiyaviewer.lumiya.Debug;
import java.util.UUID;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.utils.InternPool;

public class SLTextureEntry
{
    public static final int MAX_FACES = 32;
    private static final SLTextureEntryFace[] emptyFaces;
    private static final InternPool<SLTextureEntry> pool;
    private final SLTextureEntryFace DefaultTexture;
    private final SLTextureEntryFace[] FaceTextures;
    private final int faceMask;
    private final int hashValue;
    
    static {
        emptyFaces = new SLTextureEntryFace[0];
        pool = new InternPool<SLTextureEntry>();
    }
    
    private SLTextureEntry(final SLTextureEntryFace defaultTexture, final SLTextureEntryFace[] faceTextures) {
        int i = 0;
        this.DefaultTexture = defaultTexture;
        this.FaceTextures = faceTextures;
        int faceMask = 0;
        while (i < faceTextures.length) {
            int n = faceMask;
            if (faceTextures[i] != null) {
                n = (faceMask | 1 << i);
            }
            ++i;
            faceMask = n;
        }
        this.faceMask = faceMask;
        this.hashValue = this.getHashValue();
    }
    
    private SLTextureEntry(final ByteBuffer byteBuffer, int i) {
        final int n = 0;
        final MutableSLTextureEntryFace mutableSLTextureEntryFace = new MutableSLTextureEntryFace(-1);
        if (byteBuffer.limit() - byteBuffer.position() < 16) {
            this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace);
            this.FaceTextures = SLTextureEntry.emptyFaces;
            this.faceMask = 0;
            this.hashValue = this.getHashValue();
            return;
        }
        final MutableSLTextureEntryFace[] array = new MutableSLTextureEntryFace[32];
        final int[] array2 = { 0 };
        final int[] array3 = { 0 };
        mutableSLTextureEntryFace.setTextureID(UUIDPool.getUUID(getUUID(byteBuffer)));
        while (true) {
            final int readFaceBitfield = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield == 0) {
                break;
            }
            final UUID uuid = UUIDPool.getUUID(getUUID(byteBuffer));
            int n2;
            for (n2 = 1, i = 0; i < array3[0]; ++i, n2 <<= 1) {
                if ((readFaceBitfield & n2) != 0x0) {
                    CreateFace(array, i, array2).setTextureID(uuid);
                }
            }
        }
        mutableSLTextureEntryFace.setRGBA(byteBuffer.getInt());
        while (true) {
            final int readFaceBitfield2 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield2 == 0) {
                break;
            }
            final int int1 = byteBuffer.getInt();
            i = 1;
            for (int j = 0; j < array3[0]; ++j, i <<= 1) {
                if ((readFaceBitfield2 & i) != 0x0) {
                    CreateFace(array, j, array2).setRGBA(int1);
                }
            }
        }
        mutableSLTextureEntryFace.setRepeatU(byteBuffer.getFloat());
        while (true) {
            final int readFaceBitfield3 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield3 == 0) {
                break;
            }
            final float float1 = byteBuffer.getFloat();
            int n3;
            for (n3 = 1, i = 0; i < array3[0]; ++i, n3 <<= 1) {
                if ((readFaceBitfield3 & n3) != 0x0) {
                    CreateFace(array, i, array2).setRepeatU(float1);
                }
            }
        }
        mutableSLTextureEntryFace.setRepeatV(byteBuffer.getFloat());
        while (true) {
            final int readFaceBitfield4 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield4 == 0) {
                break;
            }
            final float float2 = byteBuffer.getFloat();
            i = 1;
            for (int k = 0; k < array3[0]; ++k, i <<= 1) {
                if ((readFaceBitfield4 & i) != 0x0) {
                    CreateFace(array, k, array2).setRepeatV(float2);
                }
            }
        }
        mutableSLTextureEntryFace.setOffsetU(getOffset(byteBuffer));
        while (true) {
            final int readFaceBitfield5 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield5 == 0) {
                break;
            }
            final float offset = getOffset(byteBuffer);
            int n4;
            for (n4 = 1, i = 0; i < array3[0]; ++i, n4 <<= 1) {
                if ((readFaceBitfield5 & n4) != 0x0) {
                    CreateFace(array, i, array2).setOffsetU(offset);
                }
            }
        }
        mutableSLTextureEntryFace.setOffsetV(getOffset(byteBuffer));
        while (true) {
            final int readFaceBitfield6 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield6 == 0) {
                break;
            }
            final float offset2 = getOffset(byteBuffer);
            i = 1;
            for (int l = 0; l < array3[0]; ++l, i <<= 1) {
                if ((readFaceBitfield6 & i) != 0x0) {
                    CreateFace(array, l, array2).setOffsetV(offset2);
                }
            }
        }
        mutableSLTextureEntryFace.setRotation(getRotation(byteBuffer));
        while (true) {
            final int readFaceBitfield7 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield7 == 0) {
                break;
            }
            final float rotation = getRotation(byteBuffer);
            i = 1;
            for (int n5 = 0; n5 < array3[0]; ++n5, i <<= 1) {
                if ((readFaceBitfield7 & i) != 0x0) {
                    CreateFace(array, n5, array2).setRotation(rotation);
                }
            }
        }
        mutableSLTextureEntryFace.setMaterial(byteBuffer.get());
        while (true) {
            final int readFaceBitfield8 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield8 == 0) {
                break;
            }
            final byte value = byteBuffer.get();
            i = 1;
            for (int n6 = 0; n6 < array3[0]; ++n6, i <<= 1) {
                if ((readFaceBitfield8 & i) != 0x0) {
                    CreateFace(array, n6, array2).setMaterial(value);
                }
            }
        }
        mutableSLTextureEntryFace.setMedia(byteBuffer.get());
        while (true) {
            final int readFaceBitfield9 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield9 == 0) {
                break;
            }
            final byte value2 = byteBuffer.get();
            int n7;
            for (n7 = 1, i = 0; i < array3[0]; ++i, n7 <<= 1) {
                if ((readFaceBitfield9 & n7) != 0x0) {
                    CreateFace(array, i, array2).setMedia(value2);
                }
            }
        }
        mutableSLTextureEntryFace.setGlow(getGlow(byteBuffer));
        while (true) {
            final int readFaceBitfield10 = this.ReadFaceBitfield(byteBuffer, array3);
            if (readFaceBitfield10 == 0) {
                break;
            }
            final float glow = getGlow(byteBuffer);
            i = 1;
            for (int n8 = 0; n8 < array3[0]; ++n8, i <<= 1) {
                if ((readFaceBitfield10 & i) != 0x0) {
                    CreateFace(array, n8, array2).setGlow(glow);
                }
            }
        }
        this.faceMask = array2[0];
        i = 0;
        int n9 = -1;
        while (true) {
            while (i < 33) {
                if ((this.faceMask & n9) == 0x0) {
                    this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace);
                    if (i == 0) {
                        this.FaceTextures = SLTextureEntry.emptyFaces;
                    }
                    else {
                        this.FaceTextures = new SLTextureEntryFace[i];
                        for (int n10 = n; n10 < i; ++n10) {
                            this.FaceTextures[n10] = SLTextureEntryFace.create(array[n10]);
                        }
                    }
                    this.hashValue = this.getHashValue();
                    return;
                }
                n9 <<= 1;
                ++i;
            }
            i = 0;
            continue;
        }
    }
    
    private static MutableSLTextureEntryFace CreateFace(final MutableSLTextureEntryFace[] array, final int n, final int[] array2) {
        if (n >= 32) {
            return null;
        }
        if (array[n] != null) {
            return array[n];
        }
        array2[0] |= 1 << n;
        return array[n] = new MutableSLTextureEntryFace(0);
    }
    
    private int ReadFaceBitfield(final ByteBuffer byteBuffer, final int[] array) {
        array[0] = 0;
        if (byteBuffer.position() >= byteBuffer.limit()) {
            return 0;
        }
        int n = 0;
        byte value;
        int n2;
        do {
            value = byteBuffer.get();
            n2 = (n << 7 | (value & 0x7F));
            array[0] += 7;
            n = n2;
        } while ((value & 0x80) != 0x0);
        return n2;
    }
    
    private void WriteFaceBitfield(final ByteBuffer byteBuffer, final int i) {
        int j = 0;
        int n = i;
        while (true) {
            while (j < 6) {
                if ((n & 0xFFFFFF80) == 0x0) {
                    ++j;
                    Debug.Log(String.format("WriteFaceBitfield: faceBits = 0x%08x, count %d", i, j));
                    int k = 0;
                    int l = (j - 1) * 7;
                    while (k < j) {
                        byte b2;
                        final byte b = b2 = (byte)(i >> l & 0x7F);
                        if (k != j - 1) {
                            b2 = (byte)(b | 0x80);
                        }
                        Debug.Log(String.format("WriteFaceBitfield: i = %d, shift = %d, byte 0x%02x", k, l, b2));
                        byteBuffer.put(b2);
                        l -= 7;
                        ++k;
                    }
                    return;
                }
                n = (n >> 7 & 0x1FFFFFF);
                ++j;
            }
            j = 0;
            continue;
        }
    }
    
    public static SLTextureEntry create(final SLTextureEntryFace slTextureEntryFace, final SLTextureEntryFace[] array) {
        return SLTextureEntry.pool.intern(new SLTextureEntry(slTextureEntryFace, array));
    }
    
    public static SLTextureEntry create(final ByteBuffer byteBuffer, final int n) {
        return SLTextureEntry.pool.intern(new SLTextureEntry(byteBuffer, n));
    }
    
    private static float getGlow(final ByteBuffer byteBuffer) {
        return byteBuffer.get() / 255.0f;
    }
    
    private int getHashValue() {
        int i = 0;
        int n = this.FaceTextures.length + (this.faceMask + this.DefaultTexture.hashCode());
        int n2 = 1;
        while (i < this.FaceTextures.length) {
            int n3 = n;
            if ((this.faceMask & n2) != 0x0) {
                n3 = n + this.FaceTextures[i].hashCode();
            }
            n2 <<= 1;
            ++i;
            n = n3;
        }
        return n;
    }
    
    private static float getOffset(final ByteBuffer byteBuffer) {
        return byteBuffer.getShort() / 32767.0f;
    }
    
    private static float getRotation(final ByteBuffer byteBuffer) {
        return byteBuffer.getShort() / 32767.0f * 3.1415927f * 2.0f;
    }
    
    private static UUID getUUID(final ByteBuffer byteBuffer) {
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        final UUID uuid = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return uuid;
    }
    
    private static void putGlow(final ByteBuffer byteBuffer, final float n) {
        byteBuffer.put((byte)(255.0f * n));
    }
    
    private static void putOffset(final ByteBuffer byteBuffer, final float n) {
        byteBuffer.putShort((short)(32767.0f * n));
    }
    
    private static void putRotation(final ByteBuffer byteBuffer, final float n) {
        byteBuffer.putShort((short)(n / 6.2831855f * 32767.0f));
    }
    
    private static void putUUID(final ByteBuffer byteBuffer, final UUID uuid) {
        long leastSignificantBits = 0L;
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        long mostSignificantBits;
        if (uuid != null) {
            mostSignificantBits = uuid.getMostSignificantBits();
            leastSignificantBits = uuid.getLeastSignificantBits();
        }
        else {
            mostSignificantBits = 0L;
        }
        byteBuffer.putLong(mostSignificantBits);
        byteBuffer.putLong(leastSignificantBits);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public final SLTextureEntryFace GetDefaultTexture() {
        return this.DefaultTexture;
    }
    
    public final SLTextureEntryFace GetFace(final int n) {
        if (n >= 32) {
            return null;
        }
        if (n >= this.FaceTextures.length) {
            return this.DefaultTexture;
        }
        if (this.FaceTextures[n] != null) {
            return this.FaceTextures[n];
        }
        return this.DefaultTexture;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof SLTextureEntry)) {
            return false;
        }
        final SLTextureEntry slTextureEntry = (SLTextureEntry)o;
        if (this.faceMask != slTextureEntry.faceMask) {
            return false;
        }
        if (this.FaceTextures.length != slTextureEntry.FaceTextures.length) {
            return false;
        }
        if (!this.DefaultTexture.equals(slTextureEntry.DefaultTexture)) {
            return false;
        }
        int i = 0;
        int n = 1;
        while (i < this.FaceTextures.length) {
            if ((this.faceMask & n) != 0x0 && !this.FaceTextures[i].equals(slTextureEntry.FaceTextures[i])) {
                return false;
            }
            n <<= 1;
            ++i;
        }
        return true;
    }
    
    public int getFaceMask() {
        return this.faceMask;
    }
    
    @Override
    public final int hashCode() {
        return this.hashValue;
    }
    
    public boolean isSingleFace() {
        boolean b = false;
        if (this.faceMask == 0) {
            b = true;
        }
        return b;
    }
    
    public byte[] packByteArray() {
        final ByteBuffer allocate = ByteBuffer.allocate(65535);
        putUUID(allocate, this.DefaultTexture.textureID());
        for (int i = 0; i < this.FaceTextures.length; ++i) {
            if (this.FaceTextures[i] != null) {
                int n;
                if (this.DefaultTexture.textureID() == null) {
                    n = 1;
                }
                else if (!this.FaceTextures[i].getTextureID(this.DefaultTexture).equals(this.DefaultTexture.textureID())) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    this.WriteFaceBitfield(allocate, 1 << i);
                    putUUID(allocate, this.FaceTextures[i].getTextureID(this.DefaultTexture));
                }
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        allocate.putInt(this.DefaultTexture.rgba());
        for (int j = 0; j < this.FaceTextures.length; ++j) {
            if (this.FaceTextures[j] != null && this.FaceTextures[j].getRGBA(this.DefaultTexture) != this.DefaultTexture.rgba()) {
                this.WriteFaceBitfield(allocate, 1 << j);
                allocate.putInt(this.FaceTextures[j].getRGBA(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        allocate.putFloat(this.DefaultTexture.repeatU());
        for (int k = 0; k < this.FaceTextures.length; ++k) {
            if (this.FaceTextures[k] != null && this.FaceTextures[k].getRepeatU(this.DefaultTexture) != this.DefaultTexture.repeatU()) {
                this.WriteFaceBitfield(allocate, 1 << k);
                allocate.putFloat(this.FaceTextures[k].getRepeatU(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        allocate.putFloat(this.DefaultTexture.repeatV());
        for (int l = 0; l < this.FaceTextures.length; ++l) {
            if (this.FaceTextures[l] != null && this.FaceTextures[l].getRepeatV(this.DefaultTexture) != this.DefaultTexture.repeatV()) {
                this.WriteFaceBitfield(allocate, 1 << l);
                allocate.putFloat(this.FaceTextures[l].getRepeatV(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        putOffset(allocate, this.DefaultTexture.offsetU());
        for (int n2 = 0; n2 < this.FaceTextures.length; ++n2) {
            if (this.FaceTextures[n2] != null && this.FaceTextures[n2].getOffsetU(this.DefaultTexture) != this.DefaultTexture.offsetU()) {
                this.WriteFaceBitfield(allocate, 1 << n2);
                putOffset(allocate, this.FaceTextures[n2].getOffsetU(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        putOffset(allocate, this.DefaultTexture.offsetV());
        for (int n3 = 0; n3 < this.FaceTextures.length; ++n3) {
            if (this.FaceTextures[n3] != null && this.FaceTextures[n3].getOffsetV(this.DefaultTexture) != this.DefaultTexture.offsetV()) {
                this.WriteFaceBitfield(allocate, 1 << n3);
                putOffset(allocate, this.FaceTextures[n3].getOffsetV(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        putRotation(allocate, this.DefaultTexture.rotation());
        for (int n4 = 0; n4 < this.FaceTextures.length; ++n4) {
            if (this.FaceTextures[n4] != null && this.FaceTextures[n4].getRotation(this.DefaultTexture) != this.DefaultTexture.rotation()) {
                this.WriteFaceBitfield(allocate, 1 << n4);
                putRotation(allocate, this.FaceTextures[n4].getRotation(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        allocate.put(this.DefaultTexture.materialb());
        for (int n5 = 0; n5 < this.FaceTextures.length; ++n5) {
            if (this.FaceTextures[n5] != null && this.FaceTextures[n5].getMaterial(this.DefaultTexture) != this.DefaultTexture.materialb()) {
                this.WriteFaceBitfield(allocate, 1 << n5);
                allocate.put(this.FaceTextures[n5].getMaterial(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        allocate.put(this.DefaultTexture.mediab());
        for (int n6 = 0; n6 < this.FaceTextures.length; ++n6) {
            if (this.FaceTextures[n6] != null && this.FaceTextures[n6].getMedia(this.DefaultTexture) != this.DefaultTexture.mediab()) {
                this.WriteFaceBitfield(allocate, 1 << n6);
                allocate.put(this.FaceTextures[n6].getMedia(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        putGlow(allocate, this.DefaultTexture.glow());
        for (int n7 = 0; n7 < this.FaceTextures.length; ++n7) {
            if (this.FaceTextures[n7] != null && this.FaceTextures[n7].getGlow(this.DefaultTexture) != this.DefaultTexture.glow()) {
                this.WriteFaceBitfield(allocate, 1 << n7);
                putGlow(allocate, this.FaceTextures[n7].getGlow(this.DefaultTexture));
            }
        }
        this.WriteFaceBitfield(allocate, 0);
        final byte[] dst = new byte[allocate.position()];
        allocate.position();
        allocate.get(dst);
        Debug.DumpBuffer("Baking: TEpacked: ", dst);
        return dst;
    }
}
