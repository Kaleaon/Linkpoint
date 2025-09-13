package com.lumiyaviewer.lumiya.res.text;

public abstract class DrawableTextParams {
    public static DrawableTextParams create(String str, int i) {
        return new AutoValue_DrawableTextParams(str, i);
    }

    public abstract int backgroundColor();

    public abstract String text();
}
