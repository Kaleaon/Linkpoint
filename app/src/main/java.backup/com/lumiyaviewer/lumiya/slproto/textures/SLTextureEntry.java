package com.lumiyaviewer.lumiya.slproto.textures;

import android.support.v4.internal.view.SupportMenu;
import com.google.common.base.Ascii;
import com.google.common.logging.nano.Vr;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.InternPool;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class SLTextureEntry {
    public static final int MAX_FACES = 32;
    private static final SLTextureEntryFace[] emptyFaces = new SLTextureEntryFace[0];
    private static final InternPool<SLTextureEntry> pool = new InternPool<>();
    private final SLTextureEntryFace DefaultTexture;
    private final SLTextureEntryFace[] FaceTextures;
    private final int faceMask;
    private final int hashValue;

    private SLTextureEntry(SLTextureEntryFace sLTextureEntryFace, SLTextureEntryFace[] sLTextureEntryFaceArr) {
        this.DefaultTexture = sLTextureEntryFace;
        this.FaceTextures = sLTextureEntryFaceArr;
        int i = 0;
        for (int i2 = 0; i2 < sLTextureEntryFaceArr.length; i2++) {
            if (sLTextureEntryFaceArr[i2] != null) {
                i |= 1 << i2;
            }
        }
        this.faceMask = i;
        this.hashValue = getHashValue();
    }

    private SLTextureEntry(ByteBuffer byteBuffer, int i) {
        MutableSLTextureEntryFace mutableSLTextureEntryFace = new MutableSLTextureEntryFace(-1);
        if (byteBuffer.limit() - byteBuffer.position() < 16) {
            this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace);
            this.FaceTextures = emptyFaces;
            this.faceMask = 0;
            this.hashValue = getHashValue();
            return;
        }
        MutableSLTextureEntryFace[] mutableSLTextureEntryFaceArr = new MutableSLTextureEntryFace[32];
        int[] iArr = new int[1];
        int[] iArr2 = new int[1];
        mutableSLTextureEntryFace.setTextureID(UUIDPool.getUUID(getUUID(byteBuffer)));
        while (true) {
            int ReadFaceBitfield = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield == 0) {
                break;
            }
            UUID uuid = UUIDPool.getUUID(getUUID(byteBuffer));
            int i2 = 1;
            int i3 = 0;
            while (i3 < iArr2[0]) {
                if ((ReadFaceBitfield & i2) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i3, iArr).setTextureID(uuid);
                }
                i3++;
                i2 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setRGBA(byteBuffer.getInt());
        while (true) {
            int ReadFaceBitfield2 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield2 == 0) {
                break;
            }
            int i4 = byteBuffer.getInt();
            int i5 = 1;
            int i6 = 0;
            while (i6 < iArr2[0]) {
                if ((ReadFaceBitfield2 & i5) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i6, iArr).setRGBA(i4);
                }
                i6++;
                i5 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setRepeatU(byteBuffer.getFloat());
        while (true) {
            int ReadFaceBitfield3 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield3 == 0) {
                break;
            }
            float f = byteBuffer.getFloat();
            int i7 = 1;
            int i8 = 0;
            while (i8 < iArr2[0]) {
                if ((ReadFaceBitfield3 & i7) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i8, iArr).setRepeatU(f);
                }
                i8++;
                i7 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setRepeatV(byteBuffer.getFloat());
        while (true) {
            int ReadFaceBitfield4 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield4 == 0) {
                break;
            }
            float f2 = byteBuffer.getFloat();
            int i9 = 1;
            int i10 = 0;
            while (i10 < iArr2[0]) {
                if ((ReadFaceBitfield4 & i9) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i10, iArr).setRepeatV(f2);
                }
                i10++;
                i9 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setOffsetU(getOffset(byteBuffer));
        while (true) {
            int ReadFaceBitfield5 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield5 == 0) {
                break;
            }
            float offset = getOffset(byteBuffer);
            int i11 = 1;
            int i12 = 0;
            while (i12 < iArr2[0]) {
                if ((ReadFaceBitfield5 & i11) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i12, iArr).setOffsetU(offset);
                }
                i12++;
                i11 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setOffsetV(getOffset(byteBuffer));
        while (true) {
            int ReadFaceBitfield6 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield6 == 0) {
                break;
            }
            float offset2 = getOffset(byteBuffer);
            int i13 = 1;
            int i14 = 0;
            while (i14 < iArr2[0]) {
                if ((ReadFaceBitfield6 & i13) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i14, iArr).setOffsetV(offset2);
                }
                i14++;
                i13 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setRotation(getRotation(byteBuffer));
        while (true) {
            int ReadFaceBitfield7 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield7 == 0) {
                break;
            }
            float rotation = getRotation(byteBuffer);
            int i15 = 1;
            int i16 = 0;
            while (i16 < iArr2[0]) {
                if ((ReadFaceBitfield7 & i15) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i16, iArr).setRotation(rotation);
                }
                i16++;
                i15 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setMaterial(byteBuffer.get());
        while (true) {
            int ReadFaceBitfield8 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield8 == 0) {
                break;
            }
            byte b = byteBuffer.get();
            int i17 = 1;
            int i18 = 0;
            while (i18 < iArr2[0]) {
                if ((ReadFaceBitfield8 & i17) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i18, iArr).setMaterial(b);
                }
                i18++;
                i17 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setMedia(byteBuffer.get());
        while (true) {
            int ReadFaceBitfield9 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield9 == 0) {
                break;
            }
            byte b2 = byteBuffer.get();
            int i19 = 1;
            int i20 = 0;
            while (i20 < iArr2[0]) {
                if ((ReadFaceBitfield9 & i19) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i20, iArr).setMedia(b2);
                }
                i20++;
                i19 <<= 1;
            }
        }
        mutableSLTextureEntryFace.setGlow(getGlow(byteBuffer));
        while (true) {
            int ReadFaceBitfield10 = ReadFaceBitfield(byteBuffer, iArr2);
            if (ReadFaceBitfield10 == 0) {
                break;
            }
            float glow = getGlow(byteBuffer);
            int i21 = 1;
            int i22 = 0;
            while (i22 < iArr2[0]) {
                if ((ReadFaceBitfield10 & i21) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i22, iArr).setGlow(glow);
                }
                i22++;
                i21 <<= 1;
            }
        }
        this.faceMask = iArr[0];
        int i23 = 0;
        int i24 = -1;
        while (true) {
            if (i23 >= 33) {
                i23 = 0;
                break;
            } else if ((this.faceMask & i24) == 0) {
                break;
            } else {
                i24 <<= 1;
                i23++;
            }
        }
        this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace);
        if (i23 == 0) {
            this.FaceTextures = emptyFaces;
        } else {
            this.FaceTextures = new SLTextureEntryFace[i23];
            for (int i25 = 0; i25 < i23; i25++) {
                this.FaceTextures[i25] = SLTextureEntryFace.create(mutableSLTextureEntryFaceArr[i25]);
            }
        }
        this.hashValue = getHashValue();
    }

    private static MutableSLTextureEntryFace CreateFace(MutableSLTextureEntryFace[] mutableSLTextureEntryFaceArr, int i, int[] iArr) {
        if (i >= 32) {
            return null;
        }
        if (mutableSLTextureEntryFaceArr[i] != null) {
            return mutableSLTextureEntryFaceArr[i];
        }
        iArr[0] = iArr[0] | (1 << i);
        mutableSLTextureEntryFaceArr[i] = new MutableSLTextureEntryFace(0);
        return mutableSLTextureEntryFaceArr[i];
    }

    private int ReadFaceBitfield(ByteBuffer byteBuffer, int[] iArr) {
        byte b;
        iArr[0] = 0;
        if (byteBuffer.position() >= byteBuffer.limit()) {
            return 0;
        }
        byte b2 = 0;
        do {
            b = byteBuffer.get();
            b2 = (b2 << 7) | (b & Ascii.DEL);
            iArr[0] = iArr[0] + 7;
        } while ((b & 128) != 0);
        return b2;
    }

    private void WriteFaceBitfield(ByteBuffer byteBuffer, int i) {
        int i2;
        int i3 = 0;
        int i4 = i;
        while (true) {
            if (i3 >= 6) {
                i2 = 0;
                break;
            } else if ((i4 & -128) == 0) {
                i2 = i3 + 1;
                break;
            } else {
                i4 = (i4 >> 7) & 33554431;
                i3++;
            }
        }
        Debug.Log(String.format("WriteFaceBitfield: faceBits = 0x%08x, count %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        int i5 = (i2 - 1) * 7;
        for (int i6 = 0; i6 < i2; i6++) {
            byte b = (byte) ((i >> i5) & Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSTUCK);
            if (i6 != i2 - 1) {
                b = (byte) (b | 128);
            }
            Debug.Log(String.format("WriteFaceBitfield: i = %d, shift = %d, byte 0x%02x", new Object[]{Integer.valueOf(i6), Integer.valueOf(i5), Byte.valueOf(b)}));
            byteBuffer.put(b);
            i5 -= 7;
        }
    }

    public static SLTextureEntry create(SLTextureEntryFace sLTextureEntryFace, SLTextureEntryFace[] sLTextureEntryFaceArr) {
        return pool.intern(new SLTextureEntry(sLTextureEntryFace, sLTextureEntryFaceArr));
    }

    public static SLTextureEntry create(ByteBuffer byteBuffer, int i) {
        return pool.intern(new SLTextureEntry(byteBuffer, i));
    }

    private static float getGlow(ByteBuffer byteBuffer) {
        return ((float) byteBuffer.get()) / 255.0f;
    }

    private int getHashValue() {
        int length = this.FaceTextures.length + this.faceMask + this.DefaultTexture.hashCode();
        int i = 1;
        for (int i2 = 0; i2 < this.FaceTextures.length; i2++) {
            if ((this.faceMask & i) != 0) {
                length += this.FaceTextures[i2].hashCode();
            }
            i <<= 1;
        }
        return length;
    }

    private static float getOffset(ByteBuffer byteBuffer) {
        return ((float) byteBuffer.getShort()) / 32767.0f;
    }

    private static float getRotation(ByteBuffer byteBuffer) {
        return (((float) byteBuffer.getShort()) / 32767.0f) * 3.1415927f * 2.0f;
    }

    private static UUID getUUID(ByteBuffer byteBuffer) {
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        UUID uuid = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return uuid;
    }

    private static void putGlow(ByteBuffer byteBuffer, float f) {
        byteBuffer.put((byte) ((int) (255.0f * f)));
    }

    private static void putOffset(ByteBuffer byteBuffer, float f) {
        byteBuffer.putShort((short) ((int) (32767.0f * f)));
    }

    private static void putRotation(ByteBuffer byteBuffer, float f) {
        byteBuffer.putShort((short) ((int) ((f / 6.2831855f) * 32767.0f)));
    }

    private static void putUUID(ByteBuffer byteBuffer, UUID uuid) {
        long j;
        long j2 = 0;
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        if (uuid != null) {
            j = uuid.getMostSignificantBits();
            j2 = uuid.getLeastSignificantBits();
        } else {
            j = 0;
        }
        byteBuffer.putLong(j);
        byteBuffer.putLong(j2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public final SLTextureEntryFace GetDefaultTexture() {
        return this.DefaultTexture;
    }

    public final SLTextureEntryFace GetFace(int i) {
        if (i >= 32) {
            return null;
        }
        return i >= this.FaceTextures.length ? this.DefaultTexture : this.FaceTextures[i] != null ? this.FaceTextures[i] : this.DefaultTexture;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof SLTextureEntry)) {
            return false;
        }
        SLTextureEntry sLTextureEntry = (SLTextureEntry) obj;
        if (this.faceMask != sLTextureEntry.faceMask || this.FaceTextures.length != sLTextureEntry.FaceTextures.length || !this.DefaultTexture.equals(sLTextureEntry.DefaultTexture)) {
            return false;
        }
        int i = 1;
        for (int i2 = 0; i2 < this.FaceTextures.length; i2++) {
            if ((this.faceMask & i) != 0 && !this.FaceTextures[i2].equals(sLTextureEntry.FaceTextures[i2])) {
                return false;
            }
            i <<= 1;
        }
        return true;
    }

    public int getFaceMask() {
        return this.faceMask;
    }

    public final int hashCode() {
        return this.hashValue;
    }

    public boolean isSingleFace() {
        return this.faceMask == 0;
    }

    public byte[] packByteArray() {
        ByteBuffer allocate = ByteBuffer.allocate(SupportMenu.USER_MASK);
        putUUID(allocate, this.DefaultTexture.textureID());
        for (int i = 0; i < this.FaceTextures.length; i++) {
            if (this.FaceTextures[i] != null) {
                if (this.DefaultTexture.textureID() == null ? true : !this.FaceTextures[i].getTextureID(this.DefaultTexture).equals(this.DefaultTexture.textureID())) {
                    WriteFaceBitfield(allocate, 1 << i);
                    putUUID(allocate, this.FaceTextures[i].getTextureID(this.DefaultTexture));
                }
            }
        }
        WriteFaceBitfield(allocate, 0);
        allocate.putInt(this.DefaultTexture.rgba());
        for (int i2 = 0; i2 < this.FaceTextures.length; i2++) {
            if (!(this.FaceTextures[i2] == null || this.FaceTextures[i2].getRGBA(this.DefaultTexture) == this.DefaultTexture.rgba())) {
                WriteFaceBitfield(allocate, 1 << i2);
                allocate.putInt(this.FaceTextures[i2].getRGBA(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        allocate.putFloat(this.DefaultTexture.repeatU());
        for (int i3 = 0; i3 < this.FaceTextures.length; i3++) {
            if (!(this.FaceTextures[i3] == null || this.FaceTextures[i3].getRepeatU(this.DefaultTexture) == this.DefaultTexture.repeatU())) {
                WriteFaceBitfield(allocate, 1 << i3);
                allocate.putFloat(this.FaceTextures[i3].getRepeatU(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        allocate.putFloat(this.DefaultTexture.repeatV());
        for (int i4 = 0; i4 < this.FaceTextures.length; i4++) {
            if (!(this.FaceTextures[i4] == null || this.FaceTextures[i4].getRepeatV(this.DefaultTexture) == this.DefaultTexture.repeatV())) {
                WriteFaceBitfield(allocate, 1 << i4);
                allocate.putFloat(this.FaceTextures[i4].getRepeatV(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        putOffset(allocate, this.DefaultTexture.offsetU());
        for (int i5 = 0; i5 < this.FaceTextures.length; i5++) {
            if (!(this.FaceTextures[i5] == null || this.FaceTextures[i5].getOffsetU(this.DefaultTexture) == this.DefaultTexture.offsetU())) {
                WriteFaceBitfield(allocate, 1 << i5);
                putOffset(allocate, this.FaceTextures[i5].getOffsetU(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        putOffset(allocate, this.DefaultTexture.offsetV());
        for (int i6 = 0; i6 < this.FaceTextures.length; i6++) {
            if (!(this.FaceTextures[i6] == null || this.FaceTextures[i6].getOffsetV(this.DefaultTexture) == this.DefaultTexture.offsetV())) {
                WriteFaceBitfield(allocate, 1 << i6);
                putOffset(allocate, this.FaceTextures[i6].getOffsetV(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        putRotation(allocate, this.DefaultTexture.rotation());
        for (int i7 = 0; i7 < this.FaceTextures.length; i7++) {
            if (!(this.FaceTextures[i7] == null || this.FaceTextures[i7].getRotation(this.DefaultTexture) == this.DefaultTexture.rotation())) {
                WriteFaceBitfield(allocate, 1 << i7);
                putRotation(allocate, this.FaceTextures[i7].getRotation(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        allocate.put(this.DefaultTexture.materialb());
        for (int i8 = 0; i8 < this.FaceTextures.length; i8++) {
            if (!(this.FaceTextures[i8] == null || this.FaceTextures[i8].getMaterial(this.DefaultTexture) == this.DefaultTexture.materialb())) {
                WriteFaceBitfield(allocate, 1 << i8);
                allocate.put(this.FaceTextures[i8].getMaterial(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        allocate.put(this.DefaultTexture.mediab());
        for (int i9 = 0; i9 < this.FaceTextures.length; i9++) {
            if (!(this.FaceTextures[i9] == null || this.FaceTextures[i9].getMedia(this.DefaultTexture) == this.DefaultTexture.mediab())) {
                WriteFaceBitfield(allocate, 1 << i9);
                allocate.put(this.FaceTextures[i9].getMedia(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        putGlow(allocate, this.DefaultTexture.glow());
        for (int i10 = 0; i10 < this.FaceTextures.length; i10++) {
            if (!(this.FaceTextures[i10] == null || this.FaceTextures[i10].getGlow(this.DefaultTexture) == this.DefaultTexture.glow())) {
                WriteFaceBitfield(allocate, 1 << i10);
                putGlow(allocate, this.FaceTextures[i10].getGlow(this.DefaultTexture));
            }
        }
        WriteFaceBitfield(allocate, 0);
        byte[] bArr = new byte[allocate.position()];
        allocate.position(0);
        allocate.get(bArr);
        Debug.DumpBuffer("Baking: TEpacked: ", bArr);
        return bArr;
    }
}
