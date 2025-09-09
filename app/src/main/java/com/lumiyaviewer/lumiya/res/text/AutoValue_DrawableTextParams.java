package com.lumiyaviewer.lumiya.res.text;

final class AutoValue_DrawableTextParams extends DrawableTextParams {
    private final int backgroundColor;
    private final String text;

    AutoValue_DrawableTextParams(String str, int i) {
        if (str == null) {
            throw new NullPointerException("Null text");
        }
        this.text = str;
        this.backgroundColor = i;
    }

    public int backgroundColor() {
        return this.backgroundColor;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DrawableTextParams)) {
            return false;
        }
        DrawableTextParams drawableTextParams = (DrawableTextParams) obj;
        if (!this.text.equals(drawableTextParams.text())) {
            z = false;
        } else if (this.backgroundColor != drawableTextParams.backgroundColor()) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.backgroundColor;
    }

    public String text() {
        return this.text;
    }

    public String toString() {
        return "DrawableTextParams{text=" + this.text + ", " + "backgroundColor=" + this.backgroundColor + "}";
    }
}
