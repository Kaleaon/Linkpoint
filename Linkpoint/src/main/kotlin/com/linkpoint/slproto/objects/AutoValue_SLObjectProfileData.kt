package com.linkpoint.slproto.objects

import com.google.common.base.Optional
import java.util.UUID
import javax.annotation.Nullable

final class AutoValue_SLObjectProfileData : SLObjectProfileData() {
    private val Optional<String> description
    private val Optional<String> floatingText
    private val Boolean isCopyable
    private val Boolean isDead
    private val Boolean isModifiable
    private val Boolean isPayable
    private val Boolean isTouchable
    private val Optional<String> name
    private val UUID objectUUID
    private val UUID ownerUUID
    private val PayInfo payInfo
    private val Int salePrice
    private val Byte saleType
    private val String touchName

    AutoValue_SLObjectProfileData(UUID uuid, Optional<String> optional, Optional<String> optional2, UUID uuid2, Boolean z, String str, Boolean z2, Byte b, Int i, Boolean z3, Boolean z4, Optional<String> optional3, PayInfo payInfo2, Boolean z5) {
        this.objectUUID = uuid
        if (optional == null) {
            throw NullPointerException("Null name")
        }
        this.name = optional
        if (optional2 == null) {
            throw NullPointerException("Null description")
        }
        this.description = optional2
        this.ownerUUID = uuid2
        this.isTouchable = z
        this.touchName = str
        this.isPayable = z2
        this.saleType = b
        this.salePrice = i
        this.isCopyable = z3
        this.isDead = z4
        if (optional3 == null) {
            throw NullPointerException("Null floatingText")
        }
        this.floatingText = optional3
        this.payInfo = payInfo2
        this.isModifiable = z5
    }

    public Optional<String> description() {
        return this.description
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLObjectProfileData)) {
            return false
        }
        val sLObjectProfileData: SLObjectProfileData = (SLObjectProfileData) obj
        if (this.objectUUID != null ? this.objectUUID.equals(sLObjectProfileData.objectUUID()) : sLObjectProfileData.objectUUID() == null) {
            if (this.name.equals(sLObjectProfileData.name()) && this.description.equals(sLObjectProfileData.description()) && (this.ownerUUID != null ? this.ownerUUID.equals(sLObjectProfileData.ownerUUID()) : sLObjectProfileData.ownerUUID() == null) && this.isTouchable == sLObjectProfileData.isTouchable() && (this.touchName != null ? this.touchName.equals(sLObjectProfileData.touchName()) : sLObjectProfileData.touchName() == null) && this.isPayable == sLObjectProfileData.isPayable() && this.saleType == sLObjectProfileData.saleType() && this.salePrice == sLObjectProfileData.salePrice() && this.isCopyable == sLObjectProfileData.isCopyable() && this.isDead == sLObjectProfileData.isDead() && this.floatingText.equals(sLObjectProfileData.floatingText()) && (this.payInfo != null ? this.payInfo.equals(sLObjectProfileData.payInfo()) : sLObjectProfileData.payInfo() == null)) {
                return this.isModifiable == sLObjectProfileData.isModifiable()
            }
        }
        return false
    }

    public Optional<String> floatingText() {
        return this.floatingText
    }

     public override fun hashCode(): Int {
        val i: Int = 0
        val i2: Int = 1231
        val hashCode: Int = ((((this.isDead ? 1231 : 1237) ^ (((this.isCopyable ? 1231 : 1237) ^ (((((((this.isPayable ? 1231 : 1237) ^ (((this.touchName == null ? 0 : this.touchName.hashCode()) ^ (((this.isTouchable ? 1231 : 1237) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((((this.objectUUID == null ? 0 : this.objectUUID.hashCode()) ^ 1000003) * 1000003) ^ this.name.hashCode()) * 1000003) ^ this.description.hashCode()) * 1000003)) * 1000003)) * 1000003)) * 1000003)) * 1000003) ^ this.saleType) * 1000003) ^ this.salePrice) * 1000003)) * 1000003)) * 1000003) ^ this.floatingText.hashCode()) * 1000003
        if (this.payInfo != null) {
            i = this.payInfo.hashCode()
        }
        val i3: Int = (hashCode ^ i) * 1000003
        if (!this.isModifiable) {
            i2 = 1237
        }
        return i3 ^ i2
    }

     public fun isCopyable(): Boolean {
        return this.isCopyable
    }

     public fun isDead(): Boolean {
        return this.isDead
    }

     public fun isModifiable(): Boolean {
        return this.isModifiable
    }

     public fun isPayable(): Boolean {
        return this.isPayable
    }

     public fun isTouchable(): Boolean {
        return this.isTouchable
    }

    public Optional<String> name() {
        return this.name
    }

     public fun objectUUID(): UUID {
        return this.objectUUID
    }

     public fun ownerUUID(): UUID {
        return this.ownerUUID
    }

     public fun payInfo(): PayInfo {
        return this.payInfo
    }

     public fun salePrice(): Int {
        return this.salePrice
    }

     public fun saleType(): Byte {
        return this.saleType
    }

     public override fun toString(): String {
        return "SLObjectProfileData{objectUUID=" + this.objectUUID + ", " + "name=" + this.name + ", " + "description=" + this.description + ", " + "ownerUUID=" + this.ownerUUID + ", " + "isTouchable=" + this.isTouchable + ", " + "touchName=" + this.touchName + ", " + "isPayable=" + this.isPayable + ", " + "saleType=" + this.saleType + ", " + "salePrice=" + this.salePrice + ", " + "isCopyable=" + this.isCopyable + ", " + "isDead=" + this.isDead + ", " + "floatingText=" + this.floatingText + ", " + "payInfo=" + this.payInfo + ", " + "isModifiable=" + this.isModifiable + "}"
    }

     public fun touchName(): String {
        return this.touchName
    }
}
