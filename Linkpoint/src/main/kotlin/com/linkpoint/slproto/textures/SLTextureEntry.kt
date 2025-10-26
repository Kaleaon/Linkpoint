package com.linkpoint.slproto.textures

import android.support.v4.internal.view.SupportMenu
import com.google.common.base.Ascii
import com.google.common.logging.nano.Vr
import com.linkpoint.Debug
import com.linkpoint.utils.InternPool
import com.linkpoint.utils.UUIDPool
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class SLTextureEntry {
    const val MAX_FACES: Int = 32
    private const val Array<SLTextureEntryFace> emptyFaces = SLTextureEntryFace[0]
    private const val InternPool<SLTextureEntry> pool = InternPool<>()
    private val SLTextureEntryFace DefaultTexture
    private val Array<SLTextureEntryFace> FaceTextures
    private val Int faceMask
    private val Int hashValue

    private SLTextureEntry(SLTextureEntryFace sLTextureEntryFace, Array<SLTextureEntryFace> sLTextureEntryFaceArr) {
        this.DefaultTexture = sLTextureEntryFace
        this.FaceTextures = sLTextureEntryFaceArr
        val i: Int = 0
        for (Int i2 = 0; i2 < sLTextureEntryFaceArr.length; i2++) {
            if (sLTextureEntryFaceArr[i2] != null) {
                i |= 1 << i2
            }
        }
        this.faceMask = i
        this.hashValue = getHashValue()
    }

    private SLTextureEntry(ByteBuffer byteBuffer, Int i) {
        val mutableSLTextureEntryFace: MutableSLTextureEntryFace = MutableSLTextureEntryFace(-1)
        if (byteBuffer.limit() - byteBuffer.position() < 16) {
            this.DefaultTexture = SLTextureEntryFace.create(mutableSLTextureEntryFace)
            this.FaceTextures = emptyFaces
            this.faceMask = 0
            this.hashValue = getHashValue()
            return
        }
        val mutableSLTextureEntryFaceArr: Array<MutableSLTextureEntryFace> = MutableSLTextureEntryFace[32]
        val iArr: IntArray = Int[1]
        val iArr2: IntArray = Int[1]
        mutableSLTextureEntryFace.setTextureID(UUIDPool.getUUID(getUUID(byteBuffer)))
        while (true) {
            val ReadFaceBitfield: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield == 0) {
                break
            }
            val uuid: UUID = UUIDPool.getUUID(getUUID(byteBuffer))
            val i2: Int = 1
            val i3: Int = 0
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
            val ReadFaceBitfield2: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield2 == 0) {
                break
            }
            val i4: Int = byteBuffer.getInt()
            val i5: Int = 1
            val i6: Int = 0
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
            val ReadFaceBitfield3: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield3 == 0) {
                break
            }
            val f: Float = byteBuffer.getFloat()
            val i7: Int = 1
            val i8: Int = 0
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
            val ReadFaceBitfield4: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield4 == 0) {
                break
            }
            val f2: Float = byteBuffer.getFloat()
            val i9: Int = 1
            val i10: Int = 0
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
            val ReadFaceBitfield5: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield5 == 0) {
                break
            }
            val offset: Float = getOffset(byteBuffer)
            val i11: Int = 1
            val i12: Int = 0
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
            val ReadFaceBitfield6: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield6 == 0) {
                break
            }
            val offset2: Float = getOffset(byteBuffer)
            val i13: Int = 1
            val i14: Int = 0
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
            val ReadFaceBitfield7: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield7 == 0) {
                break
            }
            val rotation: Float = getRotation(byteBuffer)
            val i15: Int = 1
            val i16: Int = 0
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
            val ReadFaceBitfield8: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield8 == 0) {
                break
            }
            val b: Byte = byteBuffer.get()
            val i17: Int = 1
            val i18: Int = 0
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
            val ReadFaceBitfield9: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield9 == 0) {
                break
            }
            val b2: Byte = byteBuffer.get()
            val i19: Int = 1
            val i20: Int = 0
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
            val ReadFaceBitfield10: Int = ReadFaceBitfield(byteBuffer, iArr2)
            if (ReadFaceBitfield10 == 0) {
                break
            }
            val glow: Float = getGlow(byteBuffer)
            val i21: Int = 1
            val i22: Int = 0
            while (i22 < iArr2[0]) {
                if ((ReadFaceBitfield10 & i21) != 0) {
                    CreateFace(mutableSLTextureEntryFaceArr, i22, iArr).setGlow(glow)
                }
                i22++
                i21 <<= 1
            }
        }
        this.faceMask = iArr[0]
        val i23: Int = 0
        val i24: Int = -1
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
            for (Int i25 = 0; i25 < i23; i25++) {
                this.FaceTextures[i25] = SLTextureEntryFace.create(mutableSLTextureEntryFaceArr[i25])
            }
        }
        this.hashValue = getHashValue()
    }

    @JvmStatic
private fun CreateFace(mutableSLTextureEntryFaceArr: Array<MutableSLTextureEntryFace>, i: Int, iArr: IntArray): MutableSLTextureEntryFace {
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

    private fun ReadFaceBitfield(byteBuffer: ByteBuffer, iArr: IntArray): Int {
        Byte b
        iArr[0] = 0
        if (byteBuffer.position() >= byteBuffer.limit()) {
            return 0
        }
        val b2: Byte = 0
        do {
            b = byteBuffer.get()
            b2 = (b2 << 7) | (b & Ascii.DEL)
            iArr[0] = iArr[0] + 7
        } while ((b & 128) != 0)
        return b2
    }

    private fun WriteFaceBitfield(byteBuffer: ByteBuffer, i: Int) {
        val i3: Int = 0
        val i4: Int = i
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
        Debug.Log(String.format("WriteFaceBitfield: faceBits = 0x%08x, count %d", Array<Any>{Integer.valueOf(i), Integer.valueOf(i2)}))
        val i5: Int = (i2 - 1) * 7
        for (Int i6 = 0; i6 < i2; i6++) {
            val b: Byte = (Byte) ((i >> i5) & Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSTUCK)
            if (i6 != i2 - 1) {
                b = (Byte) (b | 128)
            }
            Debug.Log(String.format("WriteFaceBitfield: i = %d, shift = %d, Byte 0x%02x", Array<Any>{Integer.valueOf(i6), Integer.valueOf(i5), Byte.valueOf(b)}))
            byteBuffer.put(b)
            i5 -= 7
        }
    }

    @JvmStatic
     fun create(sLTextureEntryFace: SLTextureEntryFace, sLTextureEntryFaceArr: Array<SLTextureEntryFace>): SLTextureEntry {
        return pool.intern(SLTextureEntry(sLTextureEntryFace, sLTextureEntryFaceArr))
    }

    @JvmStatic
     fun create(byteBuffer: ByteBuffer, i: Int): SLTextureEntry {
        return pool.intern(SLTextureEntry(byteBuffer, i))
    }

    @JvmStatic
 private fun getGlow(byteBuffer: ByteBuffer): Float {
        return ((Float) byteBuffer.get()) / 255.0f
    }

     private fun getHashValue(): Int {
        val length: Int = this.FaceTextures.length + this.faceMask + this.DefaultTexture.hashCode()
        val i: Int = 1
        for (Int i2 = 0; i2 < this.FaceTextures.length; i2++) {
            if ((this.faceMask & i) != 0) {
                length += this.FaceTextures[i2].hashCode()
            }
            i <<= 1
        }
        return length
    }

    @JvmStatic
 private fun getOffset(byteBuffer: ByteBuffer): Float {
        return ((Float) byteBuffer.getShort()) / 32767.0f
    }

    @JvmStatic
 private fun getRotation(byteBuffer: ByteBuffer): Float {
        return (((Float) byteBuffer.getShort()) / 32767.0f) * 3.1415927f * 2.0f
    }

    @JvmStatic
 private fun getUUID(byteBuffer: ByteBuffer): UUID {
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        val uuid: UUID = UUID(byteBuffer.getLong(), byteBuffer.getLong())
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return uuid
    }

    @JvmStatic
 private fun putGlow(byteBuffer: ByteBuffer, f: Float) {
        byteBuffer.put((Byte) ((Int) (255.0f * f)))
    }

    @JvmStatic
 private fun putOffset(byteBuffer: ByteBuffer, f: Float) {
        byteBuffer.putShort((Short) ((Int) (32767.0f * f)))
    }

    @JvmStatic
 private fun putRotation(byteBuffer: ByteBuffer, f: Float) {
        byteBuffer.putShort((Short) ((Int) ((f / 6.2831855f) * 32767.0f)))
    }

    @JvmStatic
 private fun putUUID(byteBuffer: ByteBuffer, uuid: UUID) {
        Long j
        val j2: Long = 0
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

    val SLTextureEntryFace GetDefaultTexture() {
        return this.DefaultTexture
    }

    val SLTextureEntryFace GetFace(Int i) {
        if (i >= 32) {
            return null
        }
        return i >= this.FaceTextures.length ? this.DefaultTexture : this.FaceTextures[i] != null ? this.FaceTextures[i] : this.DefaultTexture
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (obj == null || !(obj instanceof SLTextureEntry)) {
            return false
        }
        val sLTextureEntry: SLTextureEntry = (SLTextureEntry) obj
        if (this.faceMask != sLTextureEntry.faceMask || this.FaceTextures.length != sLTextureEntry.FaceTextures.length || !this.DefaultTexture.equals(sLTextureEntry.DefaultTexture)) {
            return false
        }
        val i: Int = 1
        for (Int i2 = 0; i2 < this.FaceTextures.length; i2++) {
            if ((this.faceMask & i) != 0 && !this.FaceTextures[i2].equals(sLTextureEntry.FaceTextures[i2])) {
                return false
            }
            i <<= 1
        }
        return true
    }

     public fun getFaceMask(): Int {
        return this.faceMask
    }

    val Int hashCode() {
        return this.hashValue
    }

     public fun isSingleFace(): Boolean {
        return this.faceMask == 0
    }

     public fun packByteArray(): ByteArray {
        val allocate: ByteBuffer = ByteBuffer.allocate(SupportMenu.USER_MASK)
        putUUID(allocate, this.DefaultTexture.textureID())
        for (Int i = 0; i < this.FaceTextures.length; i++) {
            if (this.FaceTextures[i] != null) {
                if (this.DefaultTexture.textureID() == null ? true : !this.FaceTextures[i].getTextureID(this.DefaultTexture).equals(this.DefaultTexture.textureID())) {
                    WriteFaceBitfield(allocate, 1 << i)
                    putUUID(allocate, this.FaceTextures[i].getTextureID(this.DefaultTexture))
                }
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.putInt(this.DefaultTexture.rgba())
        for (Int i2 = 0; i2 < this.FaceTextures.length; i2++) {
            if (!(this.FaceTextures[i2] == null || this.FaceTextures[i2].getRGBA(this.DefaultTexture) == this.DefaultTexture.rgba())) {
                WriteFaceBitfield(allocate, 1 << i2)
                allocate.putInt(this.FaceTextures[i2].getRGBA(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.putFloat(this.DefaultTexture.repeatU())
        for (Int i3 = 0; i3 < this.FaceTextures.length; i3++) {
            if (!(this.FaceTextures[i3] == null || this.FaceTextures[i3].getRepeatU(this.DefaultTexture) == this.DefaultTexture.repeatU())) {
                WriteFaceBitfield(allocate, 1 << i3)
                allocate.putFloat(this.FaceTextures[i3].getRepeatU(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.putFloat(this.DefaultTexture.repeatV())
        for (Int i4 = 0; i4 < this.FaceTextures.length; i4++) {
            if (!(this.FaceTextures[i4] == null || this.FaceTextures[i4].getRepeatV(this.DefaultTexture) == this.DefaultTexture.repeatV())) {
                WriteFaceBitfield(allocate, 1 << i4)
                allocate.putFloat(this.FaceTextures[i4].getRepeatV(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putOffset(allocate, this.DefaultTexture.offsetU())
        for (Int i5 = 0; i5 < this.FaceTextures.length; i5++) {
            if (!(this.FaceTextures[i5] == null || this.FaceTextures[i5].getOffsetU(this.DefaultTexture) == this.DefaultTexture.offsetU())) {
                WriteFaceBitfield(allocate, 1 << i5)
                putOffset(allocate, this.FaceTextures[i5].getOffsetU(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putOffset(allocate, this.DefaultTexture.offsetV())
        for (Int i6 = 0; i6 < this.FaceTextures.length; i6++) {
            if (!(this.FaceTextures[i6] == null || this.FaceTextures[i6].getOffsetV(this.DefaultTexture) == this.DefaultTexture.offsetV())) {
                WriteFaceBitfield(allocate, 1 << i6)
                putOffset(allocate, this.FaceTextures[i6].getOffsetV(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putRotation(allocate, this.DefaultTexture.rotation())
        for (Int i7 = 0; i7 < this.FaceTextures.length; i7++) {
            if (!(this.FaceTextures[i7] == null || this.FaceTextures[i7].getRotation(this.DefaultTexture) == this.DefaultTexture.rotation())) {
                WriteFaceBitfield(allocate, 1 << i7)
                putRotation(allocate, this.FaceTextures[i7].getRotation(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.put(this.DefaultTexture.materialb())
        for (Int i8 = 0; i8 < this.FaceTextures.length; i8++) {
            if (!(this.FaceTextures[i8] == null || this.FaceTextures[i8].getMaterial(this.DefaultTexture) == this.DefaultTexture.materialb())) {
                WriteFaceBitfield(allocate, 1 << i8)
                allocate.put(this.FaceTextures[i8].getMaterial(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        allocate.put(this.DefaultTexture.mediab())
        for (Int i9 = 0; i9 < this.FaceTextures.length; i9++) {
            if (!(this.FaceTextures[i9] == null || this.FaceTextures[i9].getMedia(this.DefaultTexture) == this.DefaultTexture.mediab())) {
                WriteFaceBitfield(allocate, 1 << i9)
                allocate.put(this.FaceTextures[i9].getMedia(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        putGlow(allocate, this.DefaultTexture.glow())
        for (Int i10 = 0; i10 < this.FaceTextures.length; i10++) {
            if (!(this.FaceTextures[i10] == null || this.FaceTextures[i10].getGlow(this.DefaultTexture) == this.DefaultTexture.glow())) {
                WriteFaceBitfield(allocate, 1 << i10)
                putGlow(allocate, this.FaceTextures[i10].getGlow(this.DefaultTexture))
            }
        }
        WriteFaceBitfield(allocate, 0)
        val bArr: ByteArray = Byte[allocate.position()]
        allocate.position(0)
        allocate.get(bArr)
        Debug.DumpBuffer("Baking: TEpacked: ", bArr)
        return bArr
    }
}
