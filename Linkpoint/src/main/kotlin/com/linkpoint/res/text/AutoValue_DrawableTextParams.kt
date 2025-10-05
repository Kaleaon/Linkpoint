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

    public Int backgroundColor() {
        return this.backgroundColor
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof DrawableTextParams)) {
            return false
        }
        DrawableTextParams drawableTextParams = (DrawableTextParams) obj
        if (this.text.equals(drawableTextParams.text())) {
            return this.backgroundColor == drawableTextParams.backgroundColor()
        }
        return false
    }

    public Int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.backgroundColor
    }

    public String text() {
        return this.text
    }

    public String toString() {
        return "DrawableTextParams{text=" + this.text + ", " + "backgroundColor=" + this.backgroundColor + "}"
    }
}
