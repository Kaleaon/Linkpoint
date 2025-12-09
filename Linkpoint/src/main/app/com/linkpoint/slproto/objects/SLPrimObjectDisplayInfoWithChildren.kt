package com.linkpoint.slproto.objects
import java.util.*

import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
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
    fun getChildren(): ImmutableList<SLObjectDisplayInfo> {
        return this.children
    }

    fun isImplicitlyAdded(): Boolean {
        return this.implicitlyAdded
    }
}
