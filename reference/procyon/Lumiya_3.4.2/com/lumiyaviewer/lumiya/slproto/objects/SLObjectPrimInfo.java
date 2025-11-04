// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;

public class SLObjectPrimInfo extends SLObjectInfo
{
    @Nonnull
    @Override
    protected DrawListObjectEntry createDrawListEntry() {
        return new DrawListPrimEntry(this);
    }
    
    @Override
    public boolean isAvatar() {
        return false;
    }
}
