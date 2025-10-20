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

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLObjectFilterInfo)) {
            return false
        }
        SLObjectFilterInfo sLObjectFilterInfo = (SLObjectFilterInfo) obj
        if (this.filterText.equals(sLObjectFilterInfo.filterText()) && this.showAttachments == sLObjectFilterInfo.showAttachments() && this.showNonDescriptive == sLObjectFilterInfo.showNonDescriptive() && this.showNonTouchable == sLObjectFilterInfo.showNonTouchable()) {
            return Float.floatToIntBits(this.range) == Float.floatToIntBits(sLObjectFilterInfo.range())
        }
        return false
    }

    public String filterText() {
        return this.filterText
    }

    public Int hashCode() {
        Int i = 1231
        Int hashCode = ((this.showNonDescriptive ? 1231 : 1237) ^ (((this.showAttachments ? 1231 : 1237) ^ ((this.filterText.hashCode() ^ 1000003) * 1000003)) * 1000003)) * 1000003
        if (!this.showNonTouchable) {
            i = 1237
        }
        return ((hashCode ^ i) * 1000003) ^ Float.floatToIntBits(this.range)
    }

    public Float range() {
        return this.range
    }

    public Boolean showAttachments() {
        return this.showAttachments
    }

    public Boolean showNonDescriptive() {
        return this.showNonDescriptive
    }

    public Boolean showNonTouchable() {
        return this.showNonTouchable
    }

    public String toString() {
        return "SLObjectFilterInfo{filterText=" + this.filterText + ", " + "showAttachments=" + this.showAttachments + ", " + "showNonDescriptive=" + this.showNonDescriptive + ", " + "showNonTouchable=" + this.showNonTouchable + ", " + "range=" + this.range + "}"
    }
}
