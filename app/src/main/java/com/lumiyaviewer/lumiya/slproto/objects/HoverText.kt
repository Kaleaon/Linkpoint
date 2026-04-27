package com.lumiyaviewer.lumiya.slproto.objects

data class HoverText(
    val text: String,
    val color: Int,
) {
    fun sameText(other: HoverText?): Boolean {
        return text == other?.text
    }

    companion object {
        @JvmStatic
        fun create(
            text: String,
            color: Int,
        ): HoverText {
            return HoverText(text, color)
        }
    }
}
