package com.lumiyaviewer.lumiya.slproto.objects

import com.google.common.base.Optional
import com.google.common.base.Strings
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class SLObjectProfileData {
    SLObjectProfileData create(@Nonnull SLObjectInfo sLObjectInfo) {
        HoverText hoverText = sLObjectInfo.getHoverText()
        return AutoValue_SLObjectProfileData(sLObjectInfo.getId(), sLObjectInfo.nameKnown ? Optional.of(Strings.nullToEmpty(sLObjectInfo.name)) : Optional.absent(), Optional.fromNullable(sLObjectInfo.getDescription()), sLObjectInfo.getOwnerUUID(), sLObjectInfo.isTouchable(), sLObjectInfo.getTouchName(), sLObjectInfo.isPayable(), sLObjectInfo.saleType, sLObjectInfo.salePrice, (sLObjectInfo.UpdateFlags & 8) != 0, sLObjectInfo.isDead, Optional.fromNullable(hoverText != null ? Strings.emptyToNull(hoverText.text()) : null), sLObjectInfo.getPayInfo(), (sLObjectInfo.UpdateFlags & 4) != 0)
    }

    abstract Optional<String> description()

    abstract Optional<String> floatingText()

    abstract Boolean isCopyable()

    abstract Boolean isDead()

    abstract Boolean isModifiable()

    abstract Boolean isPayable()

    abstract Boolean isTouchable()

    abstract Optional<String> name()

    @Nullable
    abstract UUID objectUUID()

    @Nullable
    abstract UUID ownerUUID()

    @Nullable
    abstract PayInfo payInfo()

    abstract Int salePrice()

    abstract Byte saleType()

    @Nullable
    abstract String touchName()
}
