package com.linkpoint.slproto.objects

class AutoValue_SLObjectFilterInfo : SLObjectFilterInfo {
    private String filterText
    private Float range
    private Boolean showAttachments
    private Boolean showNonDescriptive
    private Boolean showNonTouchable

    AutoValue_SLObjectFilterInfo(String str, Boolean z, Boolean z2, Boolean z3, Float f) {
        if (str == null) {
            throw NullPointerException("Null filterText")
        }
        this.filterText = str
        this.showAttachments = z
        this.showNonDescriptive = z2
        this.showNonTouchable = z3
        this.range = f
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is SLObjectFilterInfo)) {
            return false
        }
        SLObjectFilterInfo sLObjectFilterInfo = (SLObjectFilterInfo) obj
        if (this.filterText.equals(sLObjectFilterInfo.filterText()) && this.showAttachments == sLObjectFilterInfo.showAttachments() && this.showNonDescriptive == sLObjectFilterInfo.showNonDescriptive() && this.showNonTouchable == sLObjectFilterInfo.showNonTouchable()) {
            return Float.floatToIntBits(this.range) == Float.floatToIntBits(sLObjectFilterInfo.range())
        }
        return false
    }

    fun filterText(): String {
        return this.filterText
    }

    fun hashCode(): Int {
        var i: Int = 1231
        var hashCode: Int = ((this.showNonDescriptive ? 1231 : 1237) ^ (((this.showAttachments ? 1231 : 1237) ^ ((this.filterText.hashCode() ^ 1000003) * 1000003)) * 1000003)) * 1000003
        if (!this.showNonTouchable) {
            i = 1237
        }
        return ((hashCode ^ i) * 1000003) ^ Float.floatToIntBits(this.range)
    }

    fun range(): Float {
        return this.range
    }

    fun showAttachments(): Boolean {
        return this.showAttachments
    }

    fun showNonDescriptive(): Boolean {
        return this.showNonDescriptive
    }

    fun showNonTouchable(): Boolean {
        return this.showNonTouchable
    }

    fun toString(): String {
        return "SLObjectFilterInfo{filterText=" + this.filterText + ", " + "showAttachments=" + this.showAttachments + ", " + "showNonDescriptive=" + this.showNonDescriptive + ", " + "showNonTouchable=" + this.showNonTouchable + ", " + "range=" + this.range + "}"
    }
}
