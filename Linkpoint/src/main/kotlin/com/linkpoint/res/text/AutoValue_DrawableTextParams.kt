package com.linkpoint.res.text

final class AutoValue_DrawableTextParams : DrawableTextParams() {
    private val Int backgroundColor
    private val String text

    AutoValue_DrawableTextParams(String str, Int i) {
        if (str == null) {
            throw NullPointerException("Null text")
        }
        this.text = str
        this.backgroundColor = i
    }

     public fun backgroundColor(): Int {
        return this.backgroundColor
    }

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof DrawableTextParams)) {
            return false
        }
        val drawableTextParams: DrawableTextParams = (DrawableTextParams) obj
        if (this.text.equals(drawableTextParams.text())) {
            return this.backgroundColor == drawableTextParams.backgroundColor()
        }
        return false
    }

     public fun hashCode(): Int {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.backgroundColor
    }

     public fun text(): String {
        return this.text
    }

     public fun toString(): String {
        return "DrawableTextParams{text=" + this.text + ", " + "backgroundColor=" + this.backgroundColor + "}"
    }
}
