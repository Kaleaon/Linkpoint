// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;

public class SLPrimObjectDisplayInfoWithChildren extends SLPrimObjectDisplayInfo implements HasChildrenObjects
{
    @Nonnull
    public final ImmutableList<SLObjectDisplayInfo> children;
    private final boolean implicitlyAdded;
    
    public SLPrimObjectDisplayInfoWithChildren(final SLObjectInfo slObjectInfo, final float n, @Nonnull final ImmutableList<SLObjectDisplayInfo> children, final boolean implicitlyAdded) {
        super(slObjectInfo, n);
        this.children = children;
        this.implicitlyAdded = implicitlyAdded;
    }
    
    @Nonnull
    @Override
    public ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children;
    }
    
    @Override
    public boolean isImplicitlyAdded() {
        return this.implicitlyAdded;
    }
}
