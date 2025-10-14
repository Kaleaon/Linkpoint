package com.lumiyaviewer.lumiya.slproto.objects

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLAvatarObjectDisplayInfo : SLObjectDisplayInfo : SLObjectDisplayInfo.HasChildrenObjects {
    @Nonnull
    ImmutableList<SLObjectDisplayInfo> children
    private Boolean implicitlyAdded
    @Nonnull
    UUID uuid

    SLAvatarObjectDisplayInfo(@Nullable String str, SLObjectInfo sLObjectInfo, Float f, @Nonnull ImmutableList<SLObjectDisplayInfo> immutableList, Boolean z) {
        super(sLObjectInfo.localID, str, f, sLObjectInfo.hierLevel)
        this.children = immutableList
        this.implicitlyAdded = z
        this.uuid = sLObjectInfo.getId()
    }

    ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children
    }

    Boolean isImplicitlyAdded() {
        return this.implicitlyAdded
    }
}
