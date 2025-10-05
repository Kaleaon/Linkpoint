package com.linkpoint.slproto.objects;
import java.util.*;

import com.linkpoint.render.spatial.DrawListObjectEntry;
import com.linkpoint.render.spatial.DrawListPrimEntry;
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
