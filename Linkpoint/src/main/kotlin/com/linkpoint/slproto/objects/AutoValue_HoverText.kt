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

     public fun color(): Int {
        return this.color
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof HoverText)) {
            return false
        }
        val hoverText: HoverText = (HoverText) obj
        if (this.text.equals(hoverText.text())) {
            return this.color == hoverText.color()
        }
        return false
    }

     public override fun hashCode(): Int {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.color
    }

     public fun text(): String {
        return this.text
    }

     public override fun toString(): String {
        return "HoverText{text=" + this.text + ", " + "color=" + this.color + "}"
    }
}
