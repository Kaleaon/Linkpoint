package com.linkpoint.slproto.objects

import com.google.common.base.Optional
import java.util.UUID
import androidx.annotation.Nullable

class AutoValue_SLObjectProfileData : SLObjectProfileData {
    private Optional<String> description
    private Optional<String> floatingText
    private Boolean isCopyable
    private Boolean isDead
    private Boolean isModifiable
    private Boolean isPayable
    private Boolean isTouchable
    private Optional<String> name
    private UUID objectUUID
    private UUID ownerUUID
    private PayInfo payInfo
    private Int salePrice
    private Byte saleType
    private String touchName

    AutoValue_SLObjectProfileData(@Nullable UUID uuid, Optional<String> optional, Optional<String> optional2, @Nullable UUID uuid2, Boolean z, @Nullable String str, Boolean z2, Byte b, Int i, Boolean z3, Boolean z4, Optional<String> optional3, @Nullable PayInfo payInfo2, Boolean z5) {
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

    Optional<String> description() {
        return this.description
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLObjectProfileData)) {
            return false
        }
        SLObjectProfileData sLObjectProfileData = (SLObjectProfileData) obj
        if (this.objectUUID != null ? this.objectUUID.equals(sLObjectProfileData.objectUUID()) : sLObjectProfileData.objectUUID() == null) {
            if (this.name.equals(sLObjectProfileData.name()) && this.description.equals(sLObjectProfileData.description()) && (this.ownerUUID != null ? this.ownerUUID.equals(sLObjectProfileData.ownerUUID()) : sLObjectProfileData.ownerUUID() == null) && this.isTouchable == sLObjectProfileData.isTouchable() && (this.touchName != null ? this.touchName.equals(sLObjectProfileData.touchName()) : sLObjectProfileData.touchName() == null) && this.isPayable == sLObjectProfileData.isPayable() && this.saleType == sLObjectProfileData.saleType() && this.salePrice == sLObjectProfileData.salePrice() && this.isCopyable == sLObjectProfileData.isCopyable() && this.isDead == sLObjectProfileData.isDead() && this.floatingText.equals(sLObjectProfileData.floatingText()) && (this.payInfo != null ? this.payInfo.equals(sLObjectProfileData.payInfo()) : sLObjectProfileData.payInfo() == null)) {
                return this.isModifiable == sLObjectProfileData.isModifiable()
            }
        }
        return false
    }

    Optional<String> floatingText() {
        return this.floatingText
    }

    Int hashCode() {
        Int i = 0
        Int i2 = 1231
        Int hashCode = ((((this.isDead ? 1231 : 1237) ^ (((this.isCopyable ? 1231 : 1237) ^ (((((((this.isPayable ? 1231 : 1237) ^ (((this.touchName == null ? 0 : this.touchName.hashCode()) ^ (((this.isTouchable ? 1231 : 1237) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((((this.objectUUID == null ? 0 : this.objectUUID.hashCode()) ^ 1000003) * 1000003) ^ this.name.hashCode()) * 1000003) ^ this.description.hashCode()) * 1000003)) * 1000003)) * 1000003)) * 1000003)) * 1000003) ^ this.saleType) * 1000003) ^ this.salePrice) * 1000003)) * 1000003)) * 1000003) ^ this.floatingText.hashCode()) * 1000003
        if (this.payInfo != null) {
            i = this.payInfo.hashCode()
        }
        Int i3 = (hashCode ^ i) * 1000003
        if (!this.isModifiable) {
            i2 = 1237
        }
        return i3 ^ i2
    }

    Boolean isCopyable() {
        return this.isCopyable
    }

    Boolean isDead() {
        return this.isDead
    }

    Boolean isModifiable() {
        return this.isModifiable
    }

    Boolean isPayable() {
        return this.isPayable
    }

    Boolean isTouchable() {
        return this.isTouchable
    }

    Optional<String> name() {
        return this.name
    }

    @Nullable
    UUID objectUUID() {
        return this.objectUUID
    }

    @Nullable
    UUID ownerUUID() {
        return this.ownerUUID
    }

    @Nullable
    PayInfo payInfo() {
        return this.payInfo
    }

    Int salePrice() {
        return this.salePrice
    }

    Byte saleType() {
        return this.saleType
    }

    String toString() {
        return "SLObjectProfileData{objectUUID=" + this.objectUUID + ", " + "name=" + this.name + ", " + "description=" + this.description + ", " + "ownerUUID=" + this.ownerUUID + ", " + "isTouchable=" + this.isTouchable + ", " + "touchName=" + this.touchName + ", " + "isPayable=" + this.isPayable + ", " + "saleType=" + this.saleType + ", " + "salePrice=" + this.salePrice + ", " + "isCopyable=" + this.isCopyable + ", " + "isDead=" + this.isDead + ", " + "floatingText=" + this.floatingText + ", " + "payInfo=" + this.payInfo + ", " + "isModifiable=" + this.isModifiable + "}"
    }

    @Nullable
    String touchName() {
        return this.touchName
    }
}
