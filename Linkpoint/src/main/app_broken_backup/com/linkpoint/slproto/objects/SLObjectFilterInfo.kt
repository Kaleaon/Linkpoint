package com.linkpoint.slproto.objects

import androidx.annotation.Nullable

abstract class SLObjectFilterInfo {
    fun create(): SLObjectFilterInfo {
        return AutoValue_SLObjectFilterInfo("", false, false, false, 0.0f)
    }

    fun create(String str, Boolean z, Boolean z2, Boolean z3, Float f): SLObjectFilterInfo {
        return AutoValue_SLObjectFilterInfo(str, z, z2, z3, f)
    }

    abstract String filterText()

    fun nameMatches(@Nullable String str): Boolean {
        if (str == null) {
            return false
        }
        String filterText = filterText()
        if (filterText.size() != 0 && !str.toLowerCase().contains(filterText.toLowerCase())) {
            return false
        }
        if (!showNonDescriptive()) {
            return !str.equals("Any") && !str.equals("(loading)") && !str.equals("")
        }
        return true
    }

    fun objectMatches(SLObjectInfo sLObjectInfo, Float f, Boolean z): Boolean {
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
