package com.linkpoint.slproto.objects

import com.google.common.base.Objects

abstract class HoverText {
    abstract fun text(): String
    abstract fun color(): Int

    fun sameText(other: HoverText?): Boolean {
        return Objects.equal(text(), other?.text())
    }

    companion object {
        @JvmStatic
        fun create(text: String, color: Int): HoverText {
            return AutoValue_HoverText(text, color)
        }
    }
}