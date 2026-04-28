package com.linkpoint.linden.llrender

import com.linkpoint.linden.llmath.Vector4

enum class LLFontHAlign { LEFT, CENTER, RIGHT }
enum class LLFontVAlign { TOP, VCENTER, BASELINE, BOTTOM }

data class LLGlyph(
    val codepoint: Int,
    val atlasX: Int,
    val atlasY: Int,
    val atlasW: Int,
    val atlasH: Int,
    val bearingX: Float,
    val bearingY: Float,
    val advance: Float,
)

class LLFontGL(
    val name: String,
    val pointSize: Float = 12f,
) {

    var ascender: Float = pointSize * 0.8f
        private set
    var descender: Float = pointSize * 0.2f
        private set
    var lineHeight: Float = pointSize * 1.2f
        private set

    var atlasWidth: Int = 512
    var atlasHeight: Int = 512

    private val glyphs: MutableMap<Int, LLGlyph> = mutableMapOf()

    fun addGlyph(glyph: LLGlyph) {
        glyphs[glyph.codepoint] = glyph
    }

    fun getGlyph(codepoint: Int): LLGlyph? = glyphs[codepoint]

    fun getStringWidth(text: String): Float =
        text.sumOf { glyphs[it.code]?.advance?.toDouble() ?: (pointSize * 0.6).toDouble() }.toFloat()

    fun getStringHeight(text: String): Float = lineHeight * (text.count { it == '\n' } + 1)

    fun render(
        text: String,
        x: Float,
        y: Float,
        color: Vector4 = Vector4(1f, 1f, 1f, 1f),
        hAlign: LLFontHAlign = LLFontHAlign.LEFT,
        vAlign: LLFontVAlign = LLFontVAlign.BASELINE,
        maxPixels: Int = Int.MAX_VALUE,
    ): Int {
        if (text.isEmpty()) return 0
        var cx = when (hAlign) {
            LLFontHAlign.LEFT   -> x
            LLFontHAlign.CENTER -> x - getStringWidth(text) * 0.5f
            LLFontHAlign.RIGHT  -> x - getStringWidth(text)
        }
        val cy = when (vAlign) {
            LLFontVAlign.TOP      -> y - ascender
            LLFontVAlign.VCENTER  -> y - lineHeight * 0.5f
            LLFontVAlign.BASELINE -> y
            LLFontVAlign.BOTTOM   -> y + descender
        }
        var rendered = 0
        for (ch in text) {
            val glyph = glyphs[ch.code]
            val advance = glyph?.advance ?: (pointSize * 0.6f)
            if (cx - x > maxPixels) break
            cx += advance
            rendered++
        }
        return rendered
    }

    fun wrap(text: String, maxWidth: Float): List<String> {
        if (getStringWidth(text) <= maxWidth) return listOf(text)
        val lines = mutableListOf<String>()
        val words = text.split(' ')
        var line = StringBuilder()
        for (word in words) {
            val candidate = if (line.isEmpty()) word else "${line} $word"
            if (getStringWidth(candidate) <= maxWidth) {
                line.append(if (line.isEmpty()) word else " $word")
            } else {
                if (line.isNotEmpty()) lines.add(line.toString())
                line = StringBuilder(word)
            }
        }
        if (line.isNotEmpty()) lines.add(line.toString())
        return lines
    }

    companion object {
        val DEFAULT = LLFontGL("default", 12f)
        val MONOSPACE = LLFontGL("monospace", 12f)
        val SMALL = LLFontGL("small", 10f)
        val LARGE = LLFontGL("large", 16f)
    }
}
