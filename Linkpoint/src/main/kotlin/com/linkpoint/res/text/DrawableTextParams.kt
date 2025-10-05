package com.linkpoint.res.text

abstract class DrawableTextParams {
    @JvmStatic
    DrawableTextParams create(String str, Int i) {
        return AutoValue_DrawableTextParams(str, i)
    }

    public abstract Int backgroundColor()

    public abstract String text()
}
