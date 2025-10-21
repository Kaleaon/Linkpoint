package com.linkpoint.slproto.objects

final class AutoValue_HoverText : HoverText() {
    private val Int color
    private val String text

    AutoValue_HoverText(String str, Int i) {
        if (str == null) {
            throw NullPointerException("Null text")
        }
        this.text = str
        this.color = i
    }

    public Int color() {
        return this.color
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof HoverText)) {
            return false
        }
        HoverText hoverText = (HoverText) obj
        if (this.text.equals(hoverText.text())) {
            return this.color == hoverText.color()
        }
        return false
    }

    public Int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.color
    }

    public String text() {
        return this.text
    }

    public String toString() {
        return "HoverText{text=" + this.text + ", " + "color=" + this.color + "}"
    }
}
