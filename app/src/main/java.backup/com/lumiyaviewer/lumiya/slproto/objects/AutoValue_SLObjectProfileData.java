package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

final class AutoValue_SLObjectProfileData extends SLObjectProfileData {
    private final Optional<String> description;
    private final Optional<String> floatingText;
    private final boolean isCopyable;
    private final boolean isDead;
    private final boolean isModifiable;
    private final boolean isPayable;
    private final boolean isTouchable;
    private final Optional<String> name;
    private final UUID objectUUID;
    private final UUID ownerUUID;
    private final PayInfo payInfo;
    private final int salePrice;
    private final byte saleType;
    private final String touchName;

    AutoValue_SLObjectProfileData(@Nullable UUID uuid, Optional<String> optional, Optional<String> optional2, @Nullable UUID uuid2, boolean z, @Nullable String str, boolean z2, byte b, int i, boolean z3, boolean z4, Optional<String> optional3, @Nullable PayInfo payInfo2, boolean z5) {
        this.objectUUID = uuid;
        if (optional == null) {
            throw new NullPointerException("Null name");
        }
        this.name = optional;
        if (optional2 == null) {
            throw new NullPointerException("Null description");
        }
        this.description = optional2;
        this.ownerUUID = uuid2;
        this.isTouchable = z;
        this.touchName = str;
        this.isPayable = z2;
        this.saleType = b;
        this.salePrice = i;
        this.isCopyable = z3;
        this.isDead = z4;
        if (optional3 == null) {
            throw new NullPointerException("Null floatingText");
        }
        this.floatingText = optional3;
        this.payInfo = payInfo2;
        this.isModifiable = z5;
    }

    public Optional<String> description() {
        return this.description;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SLObjectProfileData)) {
            return false;
        }
        SLObjectProfileData sLObjectProfileData = (SLObjectProfileData) obj;
        if (this.objectUUID != null ? this.objectUUID.equals(sLObjectProfileData.objectUUID()) : sLObjectProfileData.objectUUID() == null) {
            if (this.name.equals(sLObjectProfileData.name()) && this.description.equals(sLObjectProfileData.description()) && (this.ownerUUID != null ? this.ownerUUID.equals(sLObjectProfileData.ownerUUID()) : sLObjectProfileData.ownerUUID() == null) && this.isTouchable == sLObjectProfileData.isTouchable() && (this.touchName != null ? this.touchName.equals(sLObjectProfileData.touchName()) : sLObjectProfileData.touchName() == null) && this.isPayable == sLObjectProfileData.isPayable() && this.saleType == sLObjectProfileData.saleType() && this.salePrice == sLObjectProfileData.salePrice() && this.isCopyable == sLObjectProfileData.isCopyable() && this.isDead == sLObjectProfileData.isDead() && this.floatingText.equals(sLObjectProfileData.floatingText()) && (this.payInfo != null ? this.payInfo.equals(sLObjectProfileData.payInfo()) : sLObjectProfileData.payInfo() == null)) {
                return this.isModifiable == sLObjectProfileData.isModifiable();
            }
        }
        return false;
    }

    public Optional<String> floatingText() {
        return this.floatingText;
    }

    public int hashCode() {
        int i = 0;
        int i2 = 1231;
        int hashCode = ((((this.isDead ? 1231 : 1237) ^ (((this.isCopyable ? 1231 : 1237) ^ (((((((this.isPayable ? 1231 : 1237) ^ (((this.touchName == null ? 0 : this.touchName.hashCode()) ^ (((this.isTouchable ? 1231 : 1237) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((((this.objectUUID == null ? 0 : this.objectUUID.hashCode()) ^ 1000003) * 1000003) ^ this.name.hashCode()) * 1000003) ^ this.description.hashCode()) * 1000003)) * 1000003)) * 1000003)) * 1000003)) * 1000003) ^ this.saleType) * 1000003) ^ this.salePrice) * 1000003)) * 1000003)) * 1000003) ^ this.floatingText.hashCode()) * 1000003;
        if (this.payInfo != null) {
            i = this.payInfo.hashCode();
        }
        int i3 = (hashCode ^ i) * 1000003;
        if (!this.isModifiable) {
            i2 = 1237;
        }
        return i3 ^ i2;
    }

    public boolean isCopyable() {
        return this.isCopyable;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public boolean isModifiable() {
        return this.isModifiable;
    }

    public boolean isPayable() {
        return this.isPayable;
    }

    public boolean isTouchable() {
        return this.isTouchable;
    }

    public Optional<String> name() {
        return this.name;
    }

    @Nullable
    public UUID objectUUID() {
        return this.objectUUID;
    }

    @Nullable
    public UUID ownerUUID() {
        return this.ownerUUID;
    }

    @Nullable
    public PayInfo payInfo() {
        return this.payInfo;
    }

    public int salePrice() {
        return this.salePrice;
    }

    public byte saleType() {
        return this.saleType;
    }

    public String toString() {
        return "SLObjectProfileData{objectUUID=" + this.objectUUID + ", " + "name=" + this.name + ", " + "description=" + this.description + ", " + "ownerUUID=" + this.ownerUUID + ", " + "isTouchable=" + this.isTouchable + ", " + "touchName=" + this.touchName + ", " + "isPayable=" + this.isPayable + ", " + "saleType=" + this.saleType + ", " + "salePrice=" + this.salePrice + ", " + "isCopyable=" + this.isCopyable + ", " + "isDead=" + this.isDead + ", " + "floatingText=" + this.floatingText + ", " + "payInfo=" + this.payInfo + ", " + "isModifiable=" + this.isModifiable + "}";
    }

    @Nullable
    public String touchName() {
        return this.touchName;
    }
}
