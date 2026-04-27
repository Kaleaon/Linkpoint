package com.linkpoint.slproto.objects

import javax.annotation.Nullable

abstract class SLObjectFilterInfo {
    @JvmStatic
     fun create(): SLObjectFilterInfo {
        return AutoValue_SLObjectFilterInfo("", false, false, false, 0.0f)
    }

    @JvmStatic
     fun create(str: String, z: Boolean, z2: Boolean, z3: Boolean, f: Float): SLObjectFilterInfo {
        return AutoValue_SLObjectFilterInfo(str, z, z2, z3, f)
    }

    public abstract String filterText()

     public fun nameMatches(str: String): Boolean {
        if (str == null) {
            return false
        }
        val filterText: String = filterText()
        if (filterText.length() != 0 && !str.toLowerCase().contains(filterText.toLowerCase())) {
            return false
        }
        if (!showNonDescriptive()) {
            return !str.equals("Object") && !str.equals("(loading)") && !str.equals("")
        }
        return true
    }

     public fun objectMatches(sLObjectInfo: SLObjectInfo, f: Float, z: Boolean): Boolean {
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

    public abstract Float range()

    public abstract Boolean showAttachments()

    public abstract Boolean showNonDescriptive()

    public abstract Boolean showNonTouchable()
}
