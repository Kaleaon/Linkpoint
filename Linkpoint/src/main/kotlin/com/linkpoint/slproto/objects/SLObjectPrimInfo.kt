package com.linkpoint.slproto.objects
import java.util.*

import com.linkpoint.render.spatial.DrawListObjectEntry
import com.linkpoint.render.spatial.DrawListPrimEntry
import javax.annotation.Nonnull

class SLObjectPrimInfo : SLObjectInfo() {
    /* access modifiers changed from: protected */
    public DrawListObjectEntry createDrawListEntry() {
        return DrawListPrimEntry(this)
    }

    public Boolean isAvatar() {
        return false
    }
}
