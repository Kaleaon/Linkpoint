// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.text;

public abstract class DrawableTextParams
{
    public static DrawableTextParams create(final String s, final int n) {
        return new AutoValue_DrawableTextParams(s, n);
    }
    
    public abstract int backgroundColor();
    
    public abstract String text();
}
