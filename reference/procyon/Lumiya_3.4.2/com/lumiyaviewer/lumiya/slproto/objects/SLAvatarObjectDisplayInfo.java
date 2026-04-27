// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nullable;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;

public class SLAvatarObjectDisplayInfo extends SLObjectDisplayInfo implements HasChildrenObjects
{
    @Nonnull
    public final ImmutableList<SLObjectDisplayInfo> children;
    private final boolean implicitlyAdded;
    @Nonnull
    public final UUID uuid;
    
    public SLAvatarObjectDisplayInfo(@Nullable final String s, final SLObjectInfo slObjectInfo, final float n, @Nonnull final ImmutableList<SLObjectDisplayInfo> children, final boolean implicitlyAdded) {
        super(slObjectInfo.localID, s, n, slObjectInfo.hierLevel);
        this.children = children;
        this.implicitlyAdded = implicitlyAdded;
        this.uuid = slObjectInfo.getId();
    }
    
    @Override
    public ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children;
    }
    
    @Override
    public boolean isImplicitlyAdded() {
        return this.implicitlyAdded;
    }
}
