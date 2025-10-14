package com.lumiyaviewer.lumiya.res.text

data class DrawableTextParams(
    val text: String,
    val backgroundColor: Int,
) {
    companion object {
        @JvmStatic
        fun create(str: String, i: Int): DrawableTextParams {
            return DrawableTextParams(str, i)
        }
    }
}