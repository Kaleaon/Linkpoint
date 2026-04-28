package com.linkpoint.linden.llmath

import kotlin.math.*

class Rect(var left: Int, var top: Int, var right: Int, var bottom: Int) {

    constructor() : this(0, 0, 0, 0)

    val width: Int  get() = right - left
    val height: Int get() = top - bottom
    val centerX: Int get() = (left + right) / 2
    val centerY: Int get() = (top + bottom) / 2

    fun pointInRect(x: Int, y: Int): Boolean = left <= x && x < right && bottom <= y && y < top
    fun localPointInRect(x: Int, y: Int): Boolean = 0 <= x && x < width && 0 <= y && y < height

    fun clampPoint(x: Int, y: Int): Pair<Int, Int> =
        x.coerceIn(left, right) to y.coerceIn(bottom, top)

    fun overlaps(other: Rect): Boolean =
        !(left > other.right || right < other.left || bottom > other.top || top < other.bottom)

    fun contains(other: Rect): Boolean =
        left <= other.left && right >= other.right && bottom <= other.bottom && top >= other.top

    fun set(l: Int, t: Int, r: Int, b: Int): Rect { left = l; top = t; right = r; bottom = b; return this }

    fun setOriginAndSize(l: Int, b: Int, w: Int, h: Int): Rect {
        left = l; bottom = b; right = l + w; top = b + h; return this
    }

    fun setLeftTopAndSize(l: Int, t: Int, w: Int, h: Int): Rect {
        left = l; top = t; right = l + w; bottom = t - h; return this
    }

    fun setCenterAndSize(x: Int, y: Int, w: Int, h: Int): Rect {
        left = x - w / 2; bottom = y - h / 2; top = bottom + h; right = left + w; return this
    }

    fun translate(horiz: Int, vertical: Int): Rect {
        left += horiz; right += horiz; top += vertical; bottom += vertical; return this
    }

    fun stretch(dx: Int, dy: Int): Rect {
        left -= dx; right += dx; top += dy; bottom -= dy; return makeValid()
    }

    fun stretch(delta: Int): Rect = stretch(delta, delta)

    fun makeValid(): Rect {
        left = minOf(left, right); bottom = minOf(bottom, top); return this
    }

    fun isValid(): Boolean = left <= right && bottom <= top
    fun isEmpty(): Boolean = left == right || bottom == top
    fun notEmpty(): Boolean = !isEmpty()

    fun unionWith(other: Rect) {
        left   = minOf(left, other.left)
        right  = maxOf(right, other.right)
        bottom = minOf(bottom, other.bottom)
        top    = maxOf(top, other.top)
    }

    fun intersect(other: Rect) {
        left   = maxOf(left, other.left)
        right  = minOf(right, other.right)
        bottom = maxOf(bottom, other.bottom)
        top    = minOf(top, other.top)
        if (left > right) left = right
        if (bottom > top) bottom = top
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rect) return false
        return left == other.left && top == other.top && right == other.right && bottom == other.bottom
    }
    override fun hashCode(): Int = listOf(left, top, right, bottom).fold(0) { acc, v -> 31 * acc + v }
    override fun toString(): String = "{ L $left B $bottom W $width H $height }"

    companion object {
        val NULL = Rect(0, 0, 0, 0)
    }
}

class RectF(var left: Float, var top: Float, var right: Float, var bottom: Float) {

    constructor() : this(0f, 0f, 0f, 0f)

    val width: Float  get() = right - left
    val height: Float get() = top - bottom
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f

    fun pointInRect(x: Float, y: Float): Boolean = left <= x && x < right && bottom <= y && y < top
    fun localPointInRect(x: Float, y: Float): Boolean = 0f <= x && x < width && 0f <= y && y < height

    fun clampPoint(x: Float, y: Float): Pair<Float, Float> =
        x.coerceIn(left, right) to y.coerceIn(bottom, top)

    fun overlaps(other: RectF): Boolean =
        !(left > other.right || right < other.left || bottom > other.top || top < other.bottom)

    fun contains(other: RectF): Boolean =
        left <= other.left && right >= other.right && bottom <= other.bottom && top >= other.top

    fun set(l: Float, t: Float, r: Float, b: Float): RectF { left = l; top = t; right = r; bottom = b; return this }

    fun setOriginAndSize(l: Float, b: Float, w: Float, h: Float): RectF {
        left = l; bottom = b; right = l + w; top = b + h; return this
    }

    fun setLeftTopAndSize(l: Float, t: Float, w: Float, h: Float): RectF {
        left = l; top = t; right = l + w; bottom = t - h; return this
    }

    fun setCenterAndSize(x: Float, y: Float, w: Float, h: Float): RectF {
        left = x - w / 2f; bottom = y - h / 2f; top = bottom + h; right = left + w; return this
    }

    fun translate(horiz: Float, vertical: Float): RectF {
        left += horiz; right += horiz; top += vertical; bottom += vertical; return this
    }

    fun stretch(dx: Float, dy: Float): RectF {
        left -= dx; right += dx; top += dy; bottom -= dy; return makeValid()
    }

    fun stretch(delta: Float): RectF = stretch(delta, delta)

    fun makeValid(): RectF {
        left = minOf(left, right); bottom = minOf(bottom, top); return this
    }

    fun isValid(): Boolean = left <= right && bottom <= top
    fun isEmpty(): Boolean = left == right || bottom == top
    fun notEmpty(): Boolean = !isEmpty()

    fun unionWith(other: RectF) {
        left   = minOf(left, other.left)
        right  = maxOf(right, other.right)
        bottom = minOf(bottom, other.bottom)
        top    = maxOf(top, other.top)
    }

    fun intersect(other: RectF) {
        left   = maxOf(left, other.left)
        right  = minOf(right, other.right)
        bottom = maxOf(bottom, other.bottom)
        top    = minOf(top, other.top)
        if (left > right) left = right
        if (bottom > top) bottom = top
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RectF) return false
        return left == other.left && top == other.top && right == other.right && bottom == other.bottom
    }
    override fun hashCode(): Int = listOf(left, top, right, bottom).fold(0) { acc, v -> 31 * acc + v.hashCode() }
    override fun toString(): String = "{ L $left B $bottom W $width H $height }"

    companion object {
        val NULL = RectF(0f, 0f, 0f, 0f)
    }
}
