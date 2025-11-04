// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;

public class SLObjectDisplayInfo
{
    public final float distance;
    public final int hierarchyLevel;
    public final int localID;
    @Nullable
    public final String name;
    
    public SLObjectDisplayInfo(final int localID, @Nullable final String name, final float distance, final int hierarchyLevel) {
        this.localID = localID;
        this.name = name;
        this.distance = distance;
        this.hierarchyLevel = hierarchyLevel;
    }
    
    public interface HasChildrenObjects
    {
        ImmutableList<SLObjectDisplayInfo> getChildren();
        
        boolean isImplicitlyAdded();
    }
}
