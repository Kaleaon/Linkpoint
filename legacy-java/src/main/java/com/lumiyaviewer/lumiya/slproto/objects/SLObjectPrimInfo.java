package com.lumiyaviewer.lumiya.slproto.objects;

import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class SLObjectPrimInfo extends SLObjectInfo {
    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
    @Nonnull
    protected DrawListObjectEntry createDrawListEntry() {
        return new DrawListPrimEntry(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
    public boolean isAvatar() {
        return false;
    }
}
