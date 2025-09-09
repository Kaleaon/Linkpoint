package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SLObjectProfileData {
    public static SLObjectProfileData create(@Nonnull SLObjectInfo sLObjectInfo) {
        HoverText hoverText = sLObjectInfo.getHoverText();
        return new AutoValue_SLObjectProfileData(sLObjectInfo.getId(), sLObjectInfo.nameKnown ? Optional.of(Strings.nullToEmpty(sLObjectInfo.name)) : Optional.absent(), Optional.fromNullable(sLObjectInfo.getDescription()), sLObjectInfo.getOwnerUUID(), sLObjectInfo.isTouchable(), sLObjectInfo.getTouchName(), sLObjectInfo.isPayable(), sLObjectInfo.saleType, sLObjectInfo.salePrice, (sLObjectInfo.UpdateFlags & 8) != 0, sLObjectInfo.isDead, Optional.fromNullable(hoverText != null ? Strings.emptyToNull(hoverText.text()) : null), sLObjectInfo.getPayInfo(), (sLObjectInfo.UpdateFlags & 4) != 0);
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
