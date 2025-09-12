package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import javax.annotation.Nonnull;

public class SLPrimObjectDisplayInfoWithChildren extends SLPrimObjectDisplayInfo implements SLObjectDisplayInfo.HasChildrenObjects {
    @Nonnull
    public final ImmutableList<SLObjectDisplayInfo> children;
    private final boolean implicitlyAdded;

    public SLPrimObjectDisplayInfoWithChildren(SLObjectInfo sLObjectInfo, float f, @Nonnull ImmutableList<SLObjectDisplayInfo> immutableList, boolean z) {
        super(sLObjectInfo, f);
        this.children = immutableList;
        this.implicitlyAdded = z;
    }

    @Nonnull
    public ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children;
    }

    public boolean isImplicitlyAdded() {
        return this.implicitlyAdded;
    }
}
