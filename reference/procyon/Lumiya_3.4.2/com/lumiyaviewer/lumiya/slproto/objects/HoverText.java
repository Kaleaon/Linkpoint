// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Objects;
import javax.annotation.Nullable;

public abstract class HoverText
{
    public static HoverText create(final String s, final int n) {
        return new AutoValue_HoverText(s, n);
    }
    
    public abstract int color();
    
    public final boolean sameText(@Nullable final HoverText hoverText) {
        Object text = null;
        final String text2 = this.text();
        if (hoverText != null) {
            text = hoverText.text();
        }
        return Objects.equal(text2, text);
    }
    
    public abstract String text();
}
