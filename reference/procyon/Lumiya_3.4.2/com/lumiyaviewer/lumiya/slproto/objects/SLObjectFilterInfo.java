// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import javax.annotation.Nullable;

public abstract class SLObjectFilterInfo
{
    public static SLObjectFilterInfo create() {
        return new AutoValue_SLObjectFilterInfo("", false, false, false, 0.0f);
    }
    
    public static SLObjectFilterInfo create(final String s, final boolean b, final boolean b2, final boolean b3, final float n) {
        return new AutoValue_SLObjectFilterInfo(s, b, b2, b3, n);
    }
    
    public abstract String filterText();
    
    public boolean nameMatches(@Nullable final String s) {
        if (s != null) {
            final String filterText = this.filterText();
            return (filterText.length() == 0 || s.toLowerCase().contains(filterText.toLowerCase())) && (this.showNonDescriptive() || (!s.equals("Object") && !s.equals("(loading)") && !s.equals("")));
        }
        return false;
    }
    
    public boolean objectMatches(final SLObjectInfo slObjectInfo, final float v, final boolean b) {
        if (b && (this.showAttachments() ^ true)) {
            return false;
        }
        if (!this.showNonTouchable() && !slObjectInfo.isTouchable()) {
            return false;
        }
        if (this.range() > 0.0f) {
            if (Float.isNaN(v)) {
                return false;
            }
            if (v > this.range()) {
                return false;
            }
        }
        return true;
    }
    
    public abstract float range();
    
    public abstract boolean showAttachments();
    
    public abstract boolean showNonDescriptive();
    
    public abstract boolean showNonTouchable();
}
