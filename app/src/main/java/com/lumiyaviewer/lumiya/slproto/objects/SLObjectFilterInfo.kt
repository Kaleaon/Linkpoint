package com.lumiyaviewer.lumiya.slproto.objects

import javax.annotation.Nullable

abstract class SLObjectFilterInfo {
    SLObjectFilterInfo create() {
        return AutoValue_SLObjectFilterInfo("", false, false, false, 0.0f)
    }

    SLObjectFilterInfo create(String str, Boolean z, Boolean z2, Boolean z3, Float f) {
        return AutoValue_SLObjectFilterInfo(str, z, z2, z3, f)
    }

    abstract String filterText()

    Boolean nameMatches(@Nullable String str) {
        if (str == null) {
            return false
        }
        String filterText = filterText()
        if (filterText.length() != 0 && !str.toLowerCase().contains(filterText.toLowerCase())) {
            return false
        }
        if (!showNonDescriptive()) {
            return !str.equals("Any") && !str.equals("(loading)") && !str.equals("")
        }
        return true
    }

    Boolean objectMatches(SLObjectInfo sLObjectInfo, Float f, Boolean z) {
        if (z && (!showAttachments())) {
            return false
        }
        if (!showNonTouchable() && !sLObjectInfo.isTouchable()) {
            return false
        }
        if (range() > 0.0f) {
            return !Float.isNaN(f) && f <= range()
        }
        return true
    }

    abstract Float range()

    abstract Boolean showAttachments()

    abstract Boolean showNonDescriptive()

    abstract Boolean showNonTouchable()
}
