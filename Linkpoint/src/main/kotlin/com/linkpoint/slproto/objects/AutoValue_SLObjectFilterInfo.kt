package com.linkpoint.slproto.objects

final class AutoValue_SLObjectFilterInfo : SLObjectFilterInfo() {
    private val String filterText
    private val Float range
    private val Boolean showAttachments
    private val Boolean showNonDescriptive
    private val Boolean showNonTouchable

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

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLObjectFilterInfo)) {
            return false
        }
        val sLObjectFilterInfo: SLObjectFilterInfo = (SLObjectFilterInfo) obj
        if (this.filterText.equals(sLObjectFilterInfo.filterText()) && this.showAttachments == sLObjectFilterInfo.showAttachments() && this.showNonDescriptive == sLObjectFilterInfo.showNonDescriptive() && this.showNonTouchable == sLObjectFilterInfo.showNonTouchable()) {
            return Float.floatToIntBits(this.range) == Float.floatToIntBits(sLObjectFilterInfo.range())
        }
        return false
    }

     public fun filterText(): String {
        return this.filterText
    }

     public fun hashCode(): Int {
        val i: Int = 1231
        val hashCode: Int = ((this.showNonDescriptive ? 1231 : 1237) ^ (((this.showAttachments ? 1231 : 1237) ^ ((this.filterText.hashCode() ^ 1000003) * 1000003)) * 1000003)) * 1000003
        if (!this.showNonTouchable) {
            i = 1237
        }
        return ((hashCode ^ i) * 1000003) ^ Float.floatToIntBits(this.range)
    }

     public fun range(): Float {
        return this.range
    }

     public fun showAttachments(): Boolean {
        return this.showAttachments
    }

     public fun showNonDescriptive(): Boolean {
        return this.showNonDescriptive
    }

     public fun showNonTouchable(): Boolean {
        return this.showNonTouchable
    }

     public fun toString(): String {
        return "SLObjectFilterInfo{filterText=" + this.filterText + ", " + "showAttachments=" + this.showAttachments + ", " + "showNonDescriptive=" + this.showNonDescriptive + ", " + "showNonTouchable=" + this.showNonTouchable + ", " + "range=" + this.range + "}"
    }
}
