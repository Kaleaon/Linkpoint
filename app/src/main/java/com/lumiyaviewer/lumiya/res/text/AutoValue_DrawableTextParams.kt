package com.lumiyaviewer.lumiya.res.text

class AutoValue_DrawableTextParams : DrawableTextParams {
    private Int backgroundColor
    private String text

    AutoValue_DrawableTextParams(String str, Int i) {
        if (str == null) {
            throw NullPointerException("Null text")
        }
        this.text = str
        this.backgroundColor = i
    }

    fun backgroundColor(): Int {
        return this.backgroundColor
    }

    fun equals(obj: Any): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof DrawableTextParams)) {
            return false
        }
        DrawableTextParams drawableTextParams = (DrawableTextParams) obj
        if (this.text == drawableTextParams.text()) {
            return this.backgroundColor == drawableTextParams.backgroundColor()
        }
        return false
    }

    fun hashCode(): Int {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.backgroundColor
    }

    fun text(): String {
        return this.text
    }

    fun toString(): String {
        return "DrawableTextParams{text=" + this.text + ", " + "backgroundColor=" + this.backgroundColor + "}"
    }
}
