package com.lumiyaviewer.lumiya.slproto.objects;

/* loaded from: classes.dex */
final class AutoValue_HoverText extends HoverText {
    private final int color;
    private final String text;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_HoverText(String str, int i) {
        if (str == null) {
            throw new NullPointerException("Null text");
        }
        this.text = str;
        this.color = i;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.HoverText
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
        return this.text.equals(hoverText.text()) && this.color == hoverText.color();
    }

    public int hashCode() {
        return ((this.text.hashCode() ^ 1000003) * 1000003) ^ this.color;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.objects.HoverText
    public String text() {
        return this.text;
    }

    public String toString() {
        return "HoverText{text=" + this.text + ", color=" + this.color + "}";
    }
}
