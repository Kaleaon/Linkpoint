package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;

public class SLObjectDisplayInfo {
    public final float distance;
    public final int hierarchyLevel;
    public final int localID;
    @Nullable
    public final String name;

    public interface HasChildrenObjects {
        ImmutableList<SLObjectDisplayInfo> getChildren();

        boolean isImplicitlyAdded();
    }

    public SLObjectDisplayInfo(int i, @Nullable String str, float f, int i2) {
        this.localID = i;
        this.name = str;
        this.distance = f;
        this.hierarchyLevel = i2;
    }
}
