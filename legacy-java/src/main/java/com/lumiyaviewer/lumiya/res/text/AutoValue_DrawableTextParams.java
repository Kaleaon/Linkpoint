package com.lumiyaviewer.lumiya.res.text;

/* loaded from: classes.dex */
final class AutoValue_DrawableTextParams extends DrawableTextParams {
    private final int backgroundColor;
    private final String text;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_DrawableTextParams(String str, int i) {
        if (str == null) {
            throw new NullPointerException("Null text");
        }
        this.text = str;
        this.backgroundColor = i;
    }

    @Override // com.lumiyaviewer.lumiya.res.text.DrawableTextParams
    public int backgroundColor() {
        return this.backgroundColor;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DrawableTextParams)) {
            return false;
        }
        DrawableTextParams drawableTextParams = (DrawableTextParams) obj;
        return this.text.equals(drawableTextParams.text()) && this.backgroundColor == drawableTextParams.backgroundColor();
    }

    public int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.backgroundColor;
    }

    @Override // com.lumiyaviewer.lumiya.res.text.DrawableTextParams
    public String text() {
        return this.text;
    }

    public String toString() {
        return "DrawableTextParams{text=" + this.text + ", backgroundColor=" + this.backgroundColor + "}";
    }
}
