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

    fun color(): Int {
        return this.color
    }

    fun equals(Any obj): Boolean {
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

    fun hashCode(): Int {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.color
    }

    fun text(): String {
        return this.text
    }

    fun toString(): String {
        return "HoverText{text=" + this.text + ", " + "color=" + this.color + "}"
    }
}
