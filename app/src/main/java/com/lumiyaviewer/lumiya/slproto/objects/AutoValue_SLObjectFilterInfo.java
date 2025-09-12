package com.lumiyaviewer.lumiya.slproto.objects;

final class AutoValue_SLObjectFilterInfo extends SLObjectFilterInfo {
    private final String filterText;
    private final float range;
    private final boolean showAttachments;
    private final boolean showNonDescriptive;
    private final boolean showNonTouchable;

    AutoValue_SLObjectFilterInfo(String str, boolean z, boolean z2, boolean z3, float f) {
        if (str == null) {
            throw new NullPointerException("Null filterText");
        }
        this.filterText = str;
        this.showAttachments = z;
        this.showNonDescriptive = z2;
        this.showNonTouchable = z3;
        this.range = f;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SLObjectFilterInfo)) {
            return false;
        }
        SLObjectFilterInfo sLObjectFilterInfo = (SLObjectFilterInfo) obj;
        if (this.filterText.equals(sLObjectFilterInfo.filterText()) && this.showAttachments == sLObjectFilterInfo.showAttachments() && this.showNonDescriptive == sLObjectFilterInfo.showNonDescriptive() && this.showNonTouchable == sLObjectFilterInfo.showNonTouchable()) {
            return Float.floatToIntBits(this.range) == Float.floatToIntBits(sLObjectFilterInfo.range());
        }
        return false;
    }

    public String filterText() {
        return this.filterText;
    }

    public int hashCode() {
        int i = 1231;
        int hashCode = ((this.showNonDescriptive ? 1231 : 1237) ^ (((this.showAttachments ? 1231 : 1237) ^ ((this.filterText.hashCode() ^ 1000003) * 1000003)) * 1000003)) * 1000003;
        if (!this.showNonTouchable) {
            i = 1237;
        }
        return ((hashCode ^ i) * 1000003) ^ Float.floatToIntBits(this.range);
    }

    public float range() {
        return this.range;
    }

    public boolean showAttachments() {
        return this.showAttachments;
    }

    public boolean showNonDescriptive() {
        return this.showNonDescriptive;
    }

    public boolean showNonTouchable() {
        return this.showNonTouchable;
    }

    public String toString() {
        return "SLObjectFilterInfo{filterText=" + this.filterText + ", " + "showAttachments=" + this.showAttachments + ", " + "showNonDescriptive=" + this.showNonDescriptive + ", " + "showNonTouchable=" + this.showNonTouchable + ", " + "range=" + this.range + "}";
    }
}
