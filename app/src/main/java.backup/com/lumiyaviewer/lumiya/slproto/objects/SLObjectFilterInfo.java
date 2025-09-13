package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nullable;

public abstract class SLObjectFilterInfo {
    public static SLObjectFilterInfo create() {
        return new AutoValue_SLObjectFilterInfo("", false, false, false, 0.0f);
    }

    public static SLObjectFilterInfo create(String str, boolean z, boolean z2, boolean z3, float f) {
        return new AutoValue_SLObjectFilterInfo(str, z, z2, z3, f);
    }

    public abstract String filterText();

    public boolean nameMatches(@Nullable String str) {
        if (str == null) {
            return false;
        }
        String filterText = filterText();
        if (filterText.length() != 0 && !str.toLowerCase().contains(filterText.toLowerCase())) {
            return false;
        }
        if (!showNonDescriptive()) {
            return !str.equals("Object") && !str.equals("(loading)") && !str.equals("");
        }
        return true;
    }

    public boolean objectMatches(SLObjectInfo sLObjectInfo, float f, boolean z) {
        if (z && (!showAttachments())) {
            return false;
        }
        if (!showNonTouchable() && !sLObjectInfo.isTouchable()) {
            return false;
        }
        if (range() > 0.0f) {
            return !Float.isNaN(f) && f <= range();
        }
        return true;
    }

    public abstract float range();

    public abstract boolean showAttachments();

    public abstract boolean showNonDescriptive();

    public abstract boolean showNonTouchable();
}
