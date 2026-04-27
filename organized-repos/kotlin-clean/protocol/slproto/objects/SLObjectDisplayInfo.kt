package com.linkpoint.slproto.objects
import java.util.*

import com.google.common.collect.ImmutableList
import javax.annotation.Nullable

class SLObjectDisplayInfo {
    val Float distance
    val Int hierarchyLevel
    val Int localID
    val String name

    interface HasChildrenObjects {
        ImmutableList<SLObjectDisplayInfo> getChildren()

        Boolean isImplicitlyAdded()
    }

    public SLObjectDisplayInfo(Int i, String str, Float f, Int i2) {
        this.localID = i
        this.name = str
        this.distance = f
        this.hierarchyLevel = i2
    }
}
