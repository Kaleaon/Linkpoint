package com.linkpoint.slproto.objects

import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SLAvatarObjectDisplayInfo : SLObjectDisplayInfo : SLObjectDisplayInfo.HasChildrenObjects {
    @NonNull
    ImmutableList<SLObjectDisplayInfo> children
    private Boolean implicitlyAdded
    @NonNull
    UUID uuid

    SLAvatarObjectDisplayInfo(@Nullable String str, SLObjectInfo sLObjectInfo, Float f, @NonNull ImmutableList<SLObjectDisplayInfo> immutableList, Boolean z) {
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
