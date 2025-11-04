// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

final class AutoValue_SLObjectFilterInfo extends SLObjectFilterInfo
{
    private final String filterText;
    private final float range;
    private final boolean showAttachments;
    private final boolean showNonDescriptive;
    private final boolean showNonTouchable;
    
    AutoValue_SLObjectFilterInfo(final String filterText, final boolean showAttachments, final boolean showNonDescriptive, final boolean showNonTouchable, final float range) {
        if (filterText == null) {
            throw new NullPointerException("Null filterText");
        }
        this.filterText = filterText;
        this.showAttachments = showAttachments;
        this.showNonDescriptive = showNonDescriptive;
        this.showNonTouchable = showNonTouchable;
        this.range = range;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof SLObjectFilterInfo) {
            final SLObjectFilterInfo slObjectFilterInfo = (SLObjectFilterInfo)o;
            if (this.filterText.equals(slObjectFilterInfo.filterText()) && this.showAttachments == slObjectFilterInfo.showAttachments() && this.showNonDescriptive == slObjectFilterInfo.showNonDescriptive() && this.showNonTouchable == slObjectFilterInfo.showNonTouchable()) {
                if (Float.floatToIntBits(this.range) != Float.floatToIntBits(slObjectFilterInfo.range())) {
                    b = false;
                }
            }
            else {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    @Override
    public String filterText() {
        return this.filterText;
    }
    
    @Override
    public int hashCode() {
        int n = 1231;
        final int hashCode = this.filterText.hashCode();
        int n2;
        if (this.showAttachments) {
            n2 = 1231;
        }
        else {
            n2 = 1237;
        }
        int n3;
        if (this.showNonDescriptive) {
            n3 = 1231;
        }
        else {
            n3 = 1237;
        }
        if (!this.showNonTouchable) {
            n = 1237;
        }
        return ((n3 ^ (n2 ^ (hashCode ^ 0xF4243) * 1000003) * 1000003) * 1000003 ^ n) * 1000003 ^ Float.floatToIntBits(this.range);
    }
    
    @Override
    public float range() {
        return this.range;
    }
    
    @Override
    public boolean showAttachments() {
        return this.showAttachments;
    }
    
    @Override
    public boolean showNonDescriptive() {
        return this.showNonDescriptive;
    }
    
    @Override
    public boolean showNonTouchable() {
        return this.showNonTouchable;
    }
    
    @Override
    public String toString() {
        return "SLObjectFilterInfo{filterText=" + this.filterText + ", " + "showAttachments=" + this.showAttachments + ", " + "showNonDescriptive=" + this.showNonDescriptive + ", " + "showNonTouchable=" + this.showNonTouchable + ", " + "range=" + this.range + "}";
    }
}
