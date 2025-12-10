package com.linkpoint.slproto.textures

import androidx.v4.internal.view.SupportMenu
import com.google.common.base.Ascii
import com.google.common.logging.nano.Vr
import com.linkpoint.Debug
import com.linkpoint.utils.InternPool
import com.linkpoint.utils.UUIDPool
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class SLTextureEntry {
    val MAX_FACES: Int = 32
    private SLTextureEntryFace[] emptyFaces = SLTextureEntryFace[0]
    private InternPool<SLTextureEntry> pool = InternPool<>()
    private SLTextureEntryFace DefaultTexture
    private SLTextureEntryFace[] FaceTextures
    private Int faceMask
    private Int hashValue

    private SLTextureEntry(SLTextureEntryFace sLTextureEntryFace, SLTextureEntryFace[] sLTextureEntryFaceArr) {
        this.DefaultTexture = sLTextureEntryFace
        this.FaceTextures = sLTextureEntryFaceArr
        Int i = 0
        for (i2 in 0 until sLTextureEntryFaceArr.size) {
            if (sLTextureEntryFaceArr[i2] != null) {
                i |= 1 << i2
            }
        }
        this.faceMask = i
        this.hashValue = getHashValue()
    }

    private SLTextureEntry(ByteBuffer byteBuffer, Int i) {
        MutableSLTextureEntryFace mutableSLTextureEntryFace = MutableSLTextureEntryFace(-1)
        if (byteBuffer.limit() - byteBuffer.position() < 16) {
            this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace)
            this.FaceTextures = emptyFaces
            this.faceMask = 0
            this.hashValue = getHashValue()
            return
        }
        MutableSLTextureEntryFace[] mutableSLTextureEntryFaceArr = MutableSLTextureEntryFace[32]
        IntArray iArr = IntArray(1)
        IntArray iArr2 = IntArray(1)
        mutableSLTextureEntryFace.setTextureID(UUIDPool.getUUID(getUUID(byteBuffer)))
        while (true) {
            Int ReadFaceBitfield = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield == 0) {
                break
            }
            UUID uuid = UUIDPool.getUUID(getUUID(byteBuffer))
            Int i2 = 1
            Int i3 = 0
            while (i3 < iArr2[0]) {
                if ((ReadFaceBitfield & i2) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i3, iArr).setTextureID(uuid)
                }
                i3++
                i2 <<= 1
            }
        }
        mutableSLTextureEntryFace.setRGBA(byteBuffer.getInt())
        while (true) {
            Int ReadFaceBitfield2 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield2 == 0) {
                break
            }
            Int i4 = byteBuffer.getInt()
            Int i5 = 1
            Int i6 = 0
            while (i6 < iArr2[0]) {
                if ((ReadFaceBitfield2 & i5) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i6, iArr).setRGBA(i4)
                }
                i6++
                i5 <<= 1
            }
        }
        mutableSLTextureEntryFace.setRepeatU(byteBuffer.getFloat())
        while (true) {
            Int ReadFaceBitfield3 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield3 == 0) {
                break
            }
            Float f = byteBuffer.getFloat()
            Int i7 = 1
            Int i8 = 0
            while (i8 < iArr2[0]) {
                if ((ReadFaceBitfield3 & i7) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i8, iArr).setRepeatU(f)
                }
                i8++
                i7 <<= 1
            }
        }
        mutableSLTextureEntryFace.setRepeatV(byteBuffer.getFloat())
        while (true) {
            Int ReadFaceBitfield4 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield4 == 0) {
                break
            }
            Float f2 = byteBuffer.getFloat()
            Int i9 = 1
            Int i10 = 0
            while (i10 < iArr2[0]) {
                if ((ReadFaceBitfield4 & i9) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i10, iArr).setRepeatV(f2)
                }
                i10++
                i9 <<= 1
            }
        }
        mutableSLTextureEntryFace.setOffsetU(getOffset(byteBuffer))
        while (true) {
            Int ReadFaceBitfield5 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield5 == 0) {
                break
            }
            Float offset = getOffset(byteBuffer)
            Int i11 = 1
            Int i12 = 0
            while (i12 < iArr2[0]) {
                if ((ReadFaceBitfield5 & i11) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i12, iArr).setOffsetU(offset)
                }
                i12++
                i11 <<= 1
            }
        }
        mutableSLTextureEntryFace.setOffsetV(getOffset(byteBuffer))
        while (true) {
            Int ReadFaceBitfield6 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield6 == 0) {
                break
            }
            Float offset2 = getOffset(byteBuffer)
            Int i13 = 1
            Int i14 = 0
            while (i14 < iArr2[0]) {
                if ((ReadFaceBitfield6 & i13) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i14, iArr).setOffsetV(offset2)
                }
                i14++
                i13 <<= 1
            }
        }
        mutableSLTextureEntryFace.setRotation(getRotation(byteBuffer))
        while (true) {
            Int ReadFaceBitfield7 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield7 == 0) {
                break
            }
            Float rotation = getRotation(byteBuffer)
            Int i15 = 1
            Int i16 = 0
            while (i16 < iArr2[0]) {
                if ((ReadFaceBitfield7 & i15) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i16, iArr).setRotation(rotation)
                }
                i16++
                i15 <<= 1
            }
        }
        mutableSLTextureEntryFace.setMaterial(byteBuffer.get())
        while (true) {
            Int ReadFaceBitfield8 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield8 == 0) {
                break
            }
            Byte b = byteBuffer.get()
            Int i17 = 1
            Int i18 = 0
            while (i18 < iArr2[0]) {
                if ((ReadFaceBitfield8 & i17) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i18, iArr).setMaterial(b)
                }
                i18++
                i17 <<= 1
            }
        }
        mutableSLTextureEntryFace.setMedia(byteBuffer.get())
        while (true) {
            Int ReadFaceBitfield9 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield9 == 0) {
                break
            }
            Byte b2 = byteBuffer.get()
            Int i19 = 1
            Int i20 = 0
            while (i20 < iArr2[0]) {
                if ((ReadFaceBitfield9 & i19) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i20, iArr).setMedia(b2)
                }
                i20++
                i19 <<= 1
            }
        }
        mutableSLTextureEntryFace.setGlow(getGlow(byteBuffer))
        while (true) {
            Int ReadFaceBitfield10 = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield10 == 0) {
                break
            }
            Float glow = getGlow(byteBuffer)
            Int i21 = 1
            Int i22 = 0
            while (i22 < iArr2[0]) {
                if ((ReadFaceBitfield10 & i21) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i22, iArr).setGlow(glow)
                }
                i22++
                i21 <<= 1
            }
        }
        this.faceMask = iArr[0]
        Int i23 = 0
        Int i24 = -1
        while (true) {
            if (i23 >= 33) {
                i23 = 0
                break
            } else if ((this.faceMask & i24) == 0) {
                break
            } else {
                i24 <<= 1
                i23++
            }
        }
        this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace)
        if (i23 == 0) {
            this.FaceTextures = emptyFaces
        } else {
            this.FaceTextures = SLTextureEntryFace[i23]
            for (i25 in 0 until i23) {
                this.FaceTextures[i25] = SLTextureEntryFace.create(mutableSLTextureEntryFaceArr[i25])
            }
        }
        this.hashValue = getHashValue()
    }

    private MutableSLTextureEntryFace CreateFace(MutableSLTextureEntryFace[] mutableSLTextureEntryFaceArr, Int i, IntArray iArr) {
        if (i >= 32) {
            return null
        }
        if (mutableSLTextureEntryFaceArr[i] != null) {
            return mutableSLTextureEntryFaceArr[i]
        }
        iArr[0] = iArr[0] | (1 << i)
        mutableSLTextureEntryFaceArr[i] = MutableSLTextureEntryFace(0)
        return mutableSLTextureEntryFaceArr[i]
    }

    private Int ReadFaceBitfield(ByteBuffer byteBuffer, IntArray iArr) {
        Byte b
        iArr[0] = 0
        if (byteBuffer.position() >= byteBuffer.limit()) {
            return 0
        }
        Byte b2 = 0
        do {
            b = byteBuffer.get()
            b2 = (b2 << 7) | (b & Ascii.DEL)
            iArr[0] = iArr[0] + 7
        } while ((b & 128) != 0)
        return b2
    }

    private Unit WriteFaceBitfield(ByteBuffer byteBuffer, Int i) {
        Int i3 = 0
        Int i4 = i
        while (true) {
            if (i3 >= 6) {
                i2 = 0
                break
            } else if ((i4 & -128) == 0) {
                i2 = i3 + 1
                break
            } else {
                i4 = (i4 >> 7) & 33554431
                i3++
            }
        }
        Debug.Log(String.format("WriteFaceBitfield: faceBits = 0x%08x, count %d", Any[]{Int.valueOf(i), Int.valueOf(i2)}))
        Int i5 = (i2 - 1) * 7
        for (i6 in 0 until i2) {
            Byte b = (Byte) ((i >> i5) & Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSTUCK)
            if (i6 != i2 - 1) {
                b = (Byte) (b | 128)
            }
            Debug.Log(String.format("WriteFaceBitfield: i = %d, shift = %d, Byte 0x%02x", Any[]{Int.valueOf(i6), Int.valueOf(i5), Byte.valueOf(b)}))
            byteBuffer.put(b)
            i5 -= 7
        }
    }

    fun create(SLTextureEntryFace sLTextureEntryFace, SLTextureEntryFace[] sLTextureEntryFaceArr): SLTextureEntry {
        return pool.intern(SLTextureEntry(sLTextureEntryFace, sLTextureEntryFaceArr))
    }

    fun create(ByteBuffer byteBuffer, Int i): SLTextureEntry {
        return pool.intern(SLTextureEntry(byteBuffer, i))
    }

    private Float getGlow(ByteBuffer byteBuffer) {
        return (byteBuffer.toFloat().get()) / 255.0f
    }

    private Int getHashValue() {
        Int length = this.FaceTextures.size + this.faceMask + this.DefaultTexture.hashCode()
        Int i = 1
        for (i2 in 0 until this.FaceTextures.size) {
            if ((this.faceMask & i) != 0) {
                length += this.FaceTextures[i2].hashCode()
            }
            i <<= 1
        }
        return length
    }

    private Float getOffset(ByteBuffer byteBuffer) {
        return (byteBuffer.toFloat().getShort()) / 32767.0f
    }

    private Float getRotation(ByteBuffer byteBuffer) {
        return ((byteBuffer.toFloat().getShort()) / 32767.0f) * 3.1415927f * 2.0f
    }

    private UUID getUUID(ByteBuffer byteBuffer) {
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        UUID uuid = UUID(byteBuffer.getLong(), byteBuffer.getLong())
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return uuid
    }

    private Unit putGlow(ByteBuffer byteBuffer, Float f) {
        byteBuffer.put((Byte) ((Int) (255.0f * f)))
    }

    private Unit putOffset(ByteBuffer byteBuffer, Float f) {
        byteBuffer.putShort((Short) ((Int) (32767.0f * f)))
    }

    private Unit putRotation(ByteBuffer byteBuffer, Float f) {
        byteBuffer.putShort((Short) ((Int) ((f / 6.2831855f) * 32767.0f)))
    }

    private Unit putUUID(ByteBuffer byteBuffer, UUID uuid) {
        Long j
        Long j2 = 0
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        if (uuid != null) {
            j = uuid.getMostSignificantBits()
            j2 = uuid.getLeastSignificantBits()
        } else {
            j = 0
        }
        byteBuffer.putLong(j)
        byteBuffer.putLong(j2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    }

    fun GetDefaultTexture(): SLTextureEntryFace {
        return this.DefaultTexture
    }

    fun GetFace(Int i): SLTextureEntryFace {
        if (i >= 32) {
            return null
        }
        return i >= this.FaceTextures.size ? this.DefaultTexture : this.FaceTextures[i] != null ? this.FaceTextures[i] : this.DefaultTexture
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (obj == null || !(obj instanceof SLTextureEntry)) {
            return false
        }
        SLTextureEntry sLTextureEntry = (SLTextureEntry) obj
        if (this.faceMask != sLTextureEntry.faceMask || this.FaceTextures.size != sLTextureEntry.FaceTextures.size || !this.DefaultTexture.equals(sLTextureEntry.DefaultTexture)) {
            return false
        }
        Int i = 1
        for (i2 in 0 until this.FaceTextures.size) {
            if ((this.faceMask & i) != 0 && !this.FaceTextures[i2].equals(sLTextureEntry.FaceTextures[i2])) {
                return false
            }
            i <<= 1
        }
        return true
    }

    fun getFaceMask(): Int {
        return this.faceMask
    }

    fun hashCode(): Int {
        return this.hashValue
    }

    fun isSingleFace(): Boolean {
        return this.faceMask == 0
    }

    fun packByteArray(): ByteArray {
        ByteBuffer allocate = ByteBuffer.allocate(SupportMenu.USER_MASK)
        putUUID(allocate, this.DefaultTexture.textureID())
        for (i in 0 until this.FaceTextures.size) {
            if (this.FaceTextures[i] != null) {
                if (this.DefaultTexture.textureID() == null ? true : !this.FaceTextures[i].getTextureID(this.DefaultTexture).equals(this.DefaultTexture.textureID())) {
                    WriteFaceBitfield(allocate, 1 << i)
                    putUUID(allocate, this.FaceTextures[i].getTextureID(this.DefaultTexture))
                }
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.putInt(this.DefaultTexture.rgba())
        for (i2 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i2] == null || this.FaceTextures[i2].getRGBA(this.DefaultTexture) == this.DefaultTexture.rgba())) {
                WriteFaceBitfield(allocate, 1 << i2)
                allocate.putInt(this.FaceTextures[i2].getRGBA(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.putFloat(this.DefaultTexture.repeatU())
        for (i3 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i3] == null || this.FaceTextures[i3].getRepeatU(this.DefaultTexture) == this.DefaultTexture.repeatU())) {
                WriteFaceBitfield(allocate, 1 << i3)
                allocate.putFloat(this.FaceTextures[i3].getRepeatU(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.putFloat(this.DefaultTexture.repeatV())
        for (i4 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i4] == null || this.FaceTextures[i4].getRepeatV(this.DefaultTexture) == this.DefaultTexture.repeatV())) {
                WriteFaceBitfield(allocate, 1 << i4)
                allocate.putFloat(this.FaceTextures[i4].getRepeatV(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putOffset(allocate, this.DefaultTexture.offsetU())
        for (i5 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i5] == null || this.FaceTextures[i5].getOffsetU(this.DefaultTexture) == this.DefaultTexture.offsetU())) {
                WriteFaceBitfield(allocate, 1 << i5)
                putOffset(allocate, this.FaceTextures[i5].getOffsetU(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putOffset(allocate, this.DefaultTexture.offsetV())
        for (i6 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i6] == null || this.FaceTextures[i6].getOffsetV(this.DefaultTexture) == this.DefaultTexture.offsetV())) {
                WriteFaceBitfield(allocate, 1 << i6)
                putOffset(allocate, this.FaceTextures[i6].getOffsetV(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putRotation(allocate, this.DefaultTexture.rotation())
        for (i7 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i7] == null || this.FaceTextures[i7].getRotation(this.DefaultTexture) == this.DefaultTexture.rotation())) {
                WriteFaceBitfield(allocate, 1 << i7)
                putRotation(allocate, this.FaceTextures[i7].getRotation(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.put(this.DefaultTexture.materialb())
        for (i8 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i8] == null || this.FaceTextures[i8].getMaterial(this.DefaultTexture) == this.DefaultTexture.materialb())) {
                WriteFaceBitfield(allocate, 1 << i8)
                allocate.put(this.FaceTextures[i8].getMaterial(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.put(this.DefaultTexture.mediab())
        for (i9 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i9] == null || this.FaceTextures[i9].getMedia(this.DefaultTexture) == this.DefaultTexture.mediab())) {
                WriteFaceBitfield(allocate, 1 << i9)
                allocate.put(this.FaceTextures[i9].getMedia(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putGlow(allocate, this.DefaultTexture.glow())
        for (i10 in 0 until this.FaceTextures.size) {
            if (!(this.FaceTextures[i10] == null || this.FaceTextures[i10].getGlow(this.DefaultTexture) == this.DefaultTexture.glow())) {
                WriteFaceBitfield(allocate, 1 << i10)
                putGlow(allocate, this.FaceTextures[i10].getGlow(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        ByteArray bArr = Byte[allocate.position()]
        allocate.position(0)
        allocate.get(bArr)
        Debug.DumpBuffer("Baking: TEpacked: ", bArr)
        return bArr
    }
}
