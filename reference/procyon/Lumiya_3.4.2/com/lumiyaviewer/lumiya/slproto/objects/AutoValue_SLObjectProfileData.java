// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nullable;
import java.util.UUID;
import com.google.common.base.Optional;

final class AutoValue_SLObjectProfileData extends SLObjectProfileData
{
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
    
    AutoValue_SLObjectProfileData(@Nullable final UUID objectUUID, final Optional<String> name, final Optional<String> description, @Nullable final UUID ownerUUID, final boolean isTouchable, @Nullable final String touchName, final boolean isPayable, final byte b, final int salePrice, final boolean isCopyable, final boolean isDead, final Optional<String> floatingText, @Nullable final PayInfo payInfo, final boolean isModifiable) {
        this.objectUUID = objectUUID;
        if (name == null) {
            throw new NullPointerException("Null name");
        }
        this.name = name;
        if (description == null) {
            throw new NullPointerException("Null description");
        }
        this.description = description;
        this.ownerUUID = ownerUUID;
        this.isTouchable = isTouchable;
        this.touchName = touchName;
        this.isPayable = isPayable;
        this.saleType = b;
        this.salePrice = salePrice;
        this.isCopyable = isCopyable;
        this.isDead = isDead;
        if (floatingText == null) {
            throw new NullPointerException("Null floatingText");
        }
        this.floatingText = floatingText;
        this.payInfo = payInfo;
        this.isModifiable = isModifiable;
    }
    
    @Override
    public Optional<String> description() {
        return this.description;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof SLObjectProfileData) {
            final SLObjectProfileData slObjectProfileData = (SLObjectProfileData)o;
            if (this.objectUUID == null) {
                if (slObjectProfileData.objectUUID() != null) {
                    return false;
                }
            }
            else if (!this.objectUUID.equals(slObjectProfileData.objectUUID())) {
                return false;
            }
            if (!this.name.equals(slObjectProfileData.name()) || !this.description.equals(slObjectProfileData.description())) {
                return false;
            }
            if (this.ownerUUID == null) {
                if (slObjectProfileData.ownerUUID() != null) {
                    return false;
                }
            }
            else if (!this.ownerUUID.equals(slObjectProfileData.ownerUUID())) {
                return false;
            }
            if (this.isTouchable != slObjectProfileData.isTouchable()) {
                return false;
            }
            if (this.touchName == null) {
                if (slObjectProfileData.touchName() != null) {
                    return false;
                }
            }
            else if (!this.touchName.equals(slObjectProfileData.touchName())) {
                return false;
            }
            if (this.isPayable != slObjectProfileData.isPayable() || this.saleType != slObjectProfileData.saleType() || this.salePrice != slObjectProfileData.salePrice() || this.isCopyable != slObjectProfileData.isCopyable() || this.isDead != slObjectProfileData.isDead() || !this.floatingText.equals(slObjectProfileData.floatingText())) {
                return false;
            }
            if (this.payInfo == null) {
                if (slObjectProfileData.payInfo() != null) {
                    return false;
                }
            }
            else if (!this.payInfo.equals(slObjectProfileData.payInfo())) {
                return false;
            }
            if (this.isModifiable != slObjectProfileData.isModifiable()) {
                b = false;
            }
            return b;
            b = false;
            return b;
        }
        return false;
    }
    
    @Override
    public Optional<String> floatingText() {
        return this.floatingText;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int n = 1231;
        int hashCode2;
        if (this.objectUUID == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = this.objectUUID.hashCode();
        }
        final int hashCode3 = this.name.hashCode();
        final int hashCode4 = this.description.hashCode();
        int hashCode5;
        if (this.ownerUUID == null) {
            hashCode5 = 0;
        }
        else {
            hashCode5 = this.ownerUUID.hashCode();
        }
        int n2;
        if (this.isTouchable) {
            n2 = 1231;
        }
        else {
            n2 = 1237;
        }
        int hashCode6;
        if (this.touchName == null) {
            hashCode6 = 0;
        }
        else {
            hashCode6 = this.touchName.hashCode();
        }
        int n3;
        if (this.isPayable) {
            n3 = 1231;
        }
        else {
            n3 = 1237;
        }
        final byte saleType = this.saleType;
        final int salePrice = this.salePrice;
        int n4;
        if (this.isCopyable) {
            n4 = 1231;
        }
        else {
            n4 = 1237;
        }
        int n5;
        if (this.isDead) {
            n5 = 1231;
        }
        else {
            n5 = 1237;
        }
        final int hashCode7 = this.floatingText.hashCode();
        if (this.payInfo != null) {
            hashCode = this.payInfo.hashCode();
        }
        if (!this.isModifiable) {
            n = 1237;
        }
        return (((n5 ^ (n4 ^ (((n3 ^ (hashCode6 ^ (n2 ^ (hashCode5 ^ (((hashCode2 ^ 0xF4243) * 1000003 ^ hashCode3) * 1000003 ^ hashCode4) * 1000003) * 1000003) * 1000003) * 1000003) * 1000003 ^ saleType) * 1000003 ^ salePrice) * 1000003) * 1000003) * 1000003 ^ hashCode7) * 1000003 ^ hashCode) * 1000003 ^ n;
    }
    
    @Override
    public boolean isCopyable() {
        return this.isCopyable;
    }
    
    @Override
    public boolean isDead() {
        return this.isDead;
    }
    
    @Override
    public boolean isModifiable() {
        return this.isModifiable;
    }
    
    @Override
    public boolean isPayable() {
        return this.isPayable;
    }
    
    @Override
    public boolean isTouchable() {
        return this.isTouchable;
    }
    
    @Override
    public Optional<String> name() {
        return this.name;
    }
    
    @Nullable
    @Override
    public UUID objectUUID() {
        return this.objectUUID;
    }
    
    @Nullable
    @Override
    public UUID ownerUUID() {
        return this.ownerUUID;
    }
    
    @Nullable
    @Override
    public PayInfo payInfo() {
        return this.payInfo;
    }
    
    @Override
    public int salePrice() {
        return this.salePrice;
    }
    
    @Override
    public byte saleType() {
        return this.saleType;
    }
    
    @Override
    public String toString() {
        return "SLObjectProfileData{objectUUID=" + this.objectUUID + ", " + "name=" + this.name + ", " + "description=" + this.description + ", " + "ownerUUID=" + this.ownerUUID + ", " + "isTouchable=" + this.isTouchable + ", " + "touchName=" + this.touchName + ", " + "isPayable=" + this.isPayable + ", " + "saleType=" + this.saleType + ", " + "salePrice=" + this.salePrice + ", " + "isCopyable=" + this.isCopyable + ", " + "isDead=" + this.isDead + ", " + "floatingText=" + this.floatingText + ", " + "payInfo=" + this.payInfo + ", " + "isModifiable=" + this.isModifiable + "}";
    }
    
    @Nullable
    @Override
    public String touchName() {
        return this.touchName;
    }
}
