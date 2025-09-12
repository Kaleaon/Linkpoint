package com.lumiyaviewer.lumiya.slproto.objects;

final class AutoValue_HoverText extends HoverText {
    private final int color;
    private final String text;

    AutoValue_HoverText(String str, int i) {
        if (str == null) {
            throw new NullPointerException("Null text");
        }
        this.text = str;
        this.color = i;
    }

    public int color() {
        return this.color;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HoverText)) {
            return false;
        }
        HoverText hoverText = (HoverText) obj;
        if (this.text.equals(hoverText.text())) {
            return this.color == hoverText.color();
        }
        return false;
    }

    public int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.color;
    }

    public String text() {
        return this.text;
    }

    public String toString() {
        return "HoverText{text=" + this.text + ", " + "color=" + this.color + "}";
    }
}
