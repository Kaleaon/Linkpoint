package com.lumiyaviewer.lumiya.slproto.objects
import java.util.*

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo
import javax.annotation.Nonnull

class SLPrimObjectDisplayInfoWithChildren : SLPrimObjectDisplayInfo : SLObjectDisplayInfo.HasChildrenObjects {
    @Nonnull
    ImmutableList<SLObjectDisplayInfo> children
    private Boolean implicitlyAdded

    SLPrimObjectDisplayInfoWithChildren(SLObjectInfo sLObjectInfo, Float f, @Nonnull ImmutableList<SLObjectDisplayInfo> immutableList, Boolean z) {
        super(sLObjectInfo, f)
        this.children = immutableList
        this.implicitlyAdded = z
    }

    @Nonnull
    ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children
    }

    Boolean isImplicitlyAdded() {
        return this.implicitlyAdded
    }
}
