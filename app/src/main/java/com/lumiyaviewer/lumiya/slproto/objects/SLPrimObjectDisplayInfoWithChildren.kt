package com.lumiyaviewer.lumiya.slproto.objects
import java.util.*

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo
import androidx.annotation.NonNull

class SLPrimObjectDisplayInfoWithChildren : SLPrimObjectDisplayInfo : SLObjectDisplayInfo.HasChildrenObjects {
    @NonNull
    ImmutableList<SLObjectDisplayInfo> children
    private Boolean implicitlyAdded

    SLPrimObjectDisplayInfoWithChildren(SLObjectInfo sLObjectInfo, Float f, @NonNull ImmutableList<SLObjectDisplayInfo> immutableList, Boolean z) {
        super(sLObjectInfo, f)
        this.children = immutableList
        this.implicitlyAdded = z
    }

    @NonNull
    ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children
    }

    Boolean isImplicitlyAdded() {
        return this.implicitlyAdded
    }
}
