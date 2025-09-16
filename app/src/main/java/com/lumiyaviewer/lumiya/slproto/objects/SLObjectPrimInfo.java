package com.lumiyaviewer.lumiya.slproto.objects;
import java.util.*;

import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import javax.annotation.Nonnull;

public class SLObjectPrimInfo extends SLObjectInfo {
    /* access modifiers changed from: protected */
    @Nonnull
    public DrawListObjectEntry createDrawListEntry() {
        return new DrawListPrimEntry(this);
    }

    public boolean isAvatar() {
        return false;
    }
}
