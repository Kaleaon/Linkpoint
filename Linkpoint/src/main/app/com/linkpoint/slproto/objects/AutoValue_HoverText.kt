package com.linkpoint.slproto.objects

class AutoValue_HoverText : HoverText {
    private Int color
    private String text

    AutoValue_HoverText(String str, Int i) {
        if (str == null) {
            throw NullPointerException("Null text")
        }
        this.text = str
        this.color = i
    }

    Int color() {
        return this.color
    }

    Boolean equals(Any obj) {
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

    Int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.color
    }

    String text() {
        return this.text
    }

    String toString() {
        return "HoverText{text=" + this.text + ", " + "color=" + this.color + "}"
    }
}
