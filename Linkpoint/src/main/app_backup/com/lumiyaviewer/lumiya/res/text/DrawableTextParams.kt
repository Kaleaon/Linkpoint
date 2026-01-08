package com.lumiyaviewer.lumiya.res.text

data class DrawableTextParams(val text: String, val backgroundColor: Int) {
    companion object {
        @JvmStatic
        fun create(text: String, backgroundColor: Int): DrawableTextParams {
            return DrawableTextParams(text, backgroundColor)
        }
    }
}
