package com.linkpoint.res.text

abstract class DrawableTextParams {
    @JvmStatic
     fun create(str: String, i: Int): DrawableTextParams {
        return AutoValue_DrawableTextParams(str, i)
    }

    public abstract Int backgroundColor()

    public abstract String text()
}
