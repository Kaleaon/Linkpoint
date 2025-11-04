// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.text;

final class AutoValue_DrawableTextParams extends DrawableTextParams
{
    private final int backgroundColor;
    private final String text;
    
    AutoValue_DrawableTextParams(final String text, final int backgroundColor) {
        if (text == null) {
            throw new NullPointerException("Null text");
        }
        this.text = text;
        this.backgroundColor = backgroundColor;
    }
    
    @Override
    public int backgroundColor() {
        return this.backgroundColor;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof DrawableTextParams) {
            final DrawableTextParams drawableTextParams = (DrawableTextParams)o;
            if (this.text.equals(drawableTextParams.text())) {
                if (this.backgroundColor != drawableTextParams.backgroundColor()) {
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
    public int hashCode() {
        return (this.text.hashCode() ^ 0xF4243) * 1000003 ^ this.backgroundColor;
    }
    
    @Override
    public String text() {
        return this.text;
    }
    
    @Override
    public String toString() {
        return "DrawableTextParams{text=" + this.text + ", " + "backgroundColor=" + this.backgroundColor + "}";
    }
}
