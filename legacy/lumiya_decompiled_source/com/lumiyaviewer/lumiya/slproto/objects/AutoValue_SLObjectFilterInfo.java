package com.lumiyaviewer.lumiya.slproto.objects;
/* loaded from: classes.dex */
final class AutoValue_SLObjectFilterInfo extends SLObjectFilterInfo {
    private final String filterText;
    private final float range;
    private final boolean showAttachments;
    private final boolean showNonDescriptive;
    private final boolean showNonTouchable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_SLObjectFilterInfo(String str, boolean z, boolean z2, boolean z3, float f) {
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
        if (obj instanceof SLObjectFilterInfo) {
            SLObjectFilterInfo sLObjectFilterInfo = (SLObjectFilterInfo) obj;
            if (this.filterText.equals(sLObjectFilterInfo.filterText()) && this.showAttachments == sLObjectFilterInfo.showAttachments() && this.showNonDescriptive == sLObjectFilterInfo.showNonDescriptive() && this.showNonTouchable == sLObjectFilterInfo.showNonTouchable()) {
                return Float.floatToIntBits(this.range) == Float.floatToIntBits(sLObjectFilterInfo.range());
            }
            return false;
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo
    public String filterText() {
        return this.filterText;
    }

    public int hashCode() {
        return (((((this.showNonDescriptive ? 1231 : 1237) ^ (((this.showAttachments ? 1231 : 1237) ^ ((this.filterText.hashCode() ^ 1000003) * 1000003)) * 1000003)) * 1000003) ^ (this.showNonTouchable ? 1231 : 1237)) * 1000003) ^ Float.floatToIntBits(this.range);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo
    public float range() {
        return this.range;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo
    public boolean showAttachments() {
        return this.showAttachments;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo
    public boolean showNonDescriptive() {
        return this.showNonDescriptive;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo
    public boolean showNonTouchable() {
        return this.showNonTouchable;
    }

    public String toString() {
        return "SLObjectFilterInfo{filterText=" + this.filterText + ", showAttachments=" + this.showAttachments + ", showNonDescriptive=" + this.showNonDescriptive + ", showNonTouchable=" + this.showNonTouchable + ", range=" + this.range + "}";
    }
}
