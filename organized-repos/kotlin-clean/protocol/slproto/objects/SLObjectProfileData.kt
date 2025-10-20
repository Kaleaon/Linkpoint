package com.linkpoint.slproto.objects

import com.google.common.base.Optional
import com.google.common.base.Strings
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class SLObjectProfileData {
    @JvmStatic
    SLObjectProfileData create(SLObjectInfo sLObjectInfo) {
        HoverText hoverText = sLObjectInfo.getHoverText()
        return AutoValue_SLObjectProfileData(sLObjectInfo.getId(), sLObjectInfo.nameKnown ? Optional.of(Strings.nullToEmpty(sLObjectInfo.name)) : Optional.absent(), Optional.fromNullable(sLObjectInfo.getDescription()), sLObjectInfo.getOwnerUUID(), sLObjectInfo.isTouchable(), sLObjectInfo.getTouchName(), sLObjectInfo.isPayable(), sLObjectInfo.saleType, sLObjectInfo.salePrice, (sLObjectInfo.UpdateFlags & 8) != 0, sLObjectInfo.isDead, Optional.fromNullable(hoverText != null ? Strings.emptyToNull(hoverText.text()) : null), sLObjectInfo.getPayInfo(), (sLObjectInfo.UpdateFlags & 4) != 0)
    }

    public abstract Optional<String> description()

    public abstract Optional<String> floatingText()

    public abstract Boolean isCopyable()

    public abstract Boolean isDead()

    public abstract Boolean isModifiable()

    public abstract Boolean isPayable()

    public abstract Boolean isTouchable()

    public abstract Optional<String> name()

    public abstract UUID objectUUID()

    public abstract UUID ownerUUID()

    public abstract PayInfo payInfo()

    public abstract Int salePrice()

    public abstract Byte saleType()

    public abstract String touchName()
}
