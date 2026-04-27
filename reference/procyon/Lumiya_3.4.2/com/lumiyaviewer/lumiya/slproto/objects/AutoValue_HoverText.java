// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

final class AutoValue_HoverText extends HoverText
{
    private final int color;
    private final String text;
    
    AutoValue_HoverText(final String text, final int color) {
        if (text == null) {
            throw new NullPointerException("Null text");
        }
        this.text = text;
        this.color = color;
    }
    
    @Override
    public int color() {
        return this.color;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof HoverText) {
            final HoverText hoverText = (HoverText)o;
            if (this.text.equals(hoverText.text())) {
                if (this.color != hoverText.color()) {
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
        return (this.text.hashCode() ^ 0xF4243) * 1000003 ^ this.color;
    }
    
    @Override
    public String text() {
        return this.text;
    }
    
    @Override
    public String toString() {
        return "HoverText{text=" + this.text + ", " + "color=" + this.color + "}";
    }
}
