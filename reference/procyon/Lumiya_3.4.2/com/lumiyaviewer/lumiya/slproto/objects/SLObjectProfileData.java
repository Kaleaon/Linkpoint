// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.UUID;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import javax.annotation.Nonnull;

public abstract class SLObjectProfileData
{
    public static SLObjectProfileData create(@Nonnull final SLObjectInfo slObjectInfo) {
        final HoverText hoverText = slObjectInfo.getHoverText();
        String emptyToNull;
        if (hoverText != null) {
            emptyToNull = Strings.emptyToNull(hoverText.text());
        }
        else {
            emptyToNull = null;
        }
        final UUID id = slObjectInfo.getId();
        Serializable s;
        if (slObjectInfo.nameKnown) {
            s = Optional.of(Strings.nullToEmpty(slObjectInfo.name));
        }
        else {
            s = Optional.absent();
        }
        return new AutoValue_SLObjectProfileData(id, (Optional<String>)s, Optional.fromNullable(slObjectInfo.getDescription()), slObjectInfo.getOwnerUUID(), slObjectInfo.isTouchable(), slObjectInfo.getTouchName(), slObjectInfo.isPayable(), slObjectInfo.saleType, slObjectInfo.salePrice, (slObjectInfo.UpdateFlags & 0x8) != 0x0, slObjectInfo.isDead, Optional.fromNullable(emptyToNull), slObjectInfo.getPayInfo(), (slObjectInfo.UpdateFlags & 0x4) != 0x0);
    }
    
    public abstract Optional<String> description();
    
    public abstract Optional<String> floatingText();
    
    public abstract boolean isCopyable();
    
    public abstract boolean isDead();
    
    public abstract boolean isModifiable();
    
    public abstract boolean isPayable();
    
    public abstract boolean isTouchable();
    
    public abstract Optional<String> name();
    
    @Nullable
    public abstract UUID objectUUID();
    
    @Nullable
    public abstract UUID ownerUUID();
    
    @Nullable
    public abstract PayInfo payInfo();
    
    public abstract int salePrice();
    
    public abstract byte saleType();
    
    @Nullable
    public abstract String touchName();
}
