package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SLAvatarObjectDisplayInfo extends SLObjectDisplayInfo implements SLObjectDisplayInfo.HasChildrenObjects {
    @Nonnull
    public final ImmutableList<SLObjectDisplayInfo> children;
    private final boolean implicitlyAdded;
    @Nonnull
    public final UUID uuid;

    public SLAvatarObjectDisplayInfo(@Nullable String str, SLObjectInfo sLObjectInfo, float f, @Nonnull ImmutableList<SLObjectDisplayInfo> immutableList, boolean z) {
        super(sLObjectInfo.localID, str, f, sLObjectInfo.hierLevel);
        this.children = immutableList;
        this.implicitlyAdded = z;
        this.uuid = sLObjectInfo.getId();
    }

    public ImmutableList<SLObjectDisplayInfo> getChildren() {
        return this.children;
    }

    public boolean isImplicitlyAdded() {
        return this.implicitlyAdded;
    }
}
