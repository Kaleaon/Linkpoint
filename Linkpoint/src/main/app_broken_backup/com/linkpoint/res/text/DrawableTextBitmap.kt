package com.linkpoint.res.text

import kotlin.math.*

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class DrawableTextBitmap {
    private Float baselineOffset
    private Bitmap bitmap

    DrawableTextBitmap(DrawableTextParams drawableTextParams, Int i) {
        Paint paint = Paint()
        Rect rect = Rect()
        paint.setTextSize(i.toFloat())
        paint.setAntiAlias(true)
        paint.setTextAlign(Paint.Align.CENTER)
        Array<String> split = drawableTextParams.text().split("\n")
        var length: Int = split.length
        var i2: Int = 0
        var i3: Int = 1
        while (i2 < length) {
            var str: String = split[i2]
            paint.getTextBounds(str, 0, str.length, rect)
            i2++
            i3 = rect.width() > i3 ? rect.width() : i3
        }
        var descent: Float = paint.descent() - paint.ascent()
        var round: Int = Math.round(((split.toFloat().length) * descent) + 1.0f)
        var i4: Int = drawableTextParams.backgroundColor() != 0 ? i : 0
        var i5: Int = drawableTextParams.backgroundColor() != 0 ? i / 2 : 0
        var i6: Int = 1
        while (i6 < i3 + i4 && i6 < 512) {
            i6 <<= 1
        }
        var i7: Int = 1
        while (i7 < round + i5 && i7 < 256) {
            i7 <<= 1
        }
        var max: Int = max(i6, i7)
        this.bitmap = Bitmap.createBitmap(max, max, drawableTextParams.backgroundColor() == 0 ? Bitmap.Config.ALPHA_8 : Bitmap.Config.ARGB_8888)
        Canvas canvas = Canvas(this.bitmap)
        if (drawableTextParams.backgroundColor() != 0) {
            paint.setColor(drawableTextParams.backgroundColor())
            var i8: Int = i3 + i4
            var i9: Int = round + i5
            canvas.drawRect((Float) ((max - i8) / 2), (Float) ((max - i9) / 2), (Float) (max - ((max - i8) / 2)), (Float) (max - ((max - i9) / 2)), paint)
        }
        paint.setARGB(255, 255, 255, 255)
        var i10: Int = (max - round) / 2
        for (String drawText : split) {
            canvas.drawText(drawText, (Float) (max / 2), (i10.toFloat()) - paint.ascent(), paint)
            i10 = (Int) ((i10.toFloat()) + (paint.descent() - paint.ascent()))
        }
        this.baselineOffset = ((round.toFloat()) + descent) / (max.toFloat())
    }

    fun getBaselineOffset(): Float {
        return this.baselineOffset
    }

    fun getBitmap(): Bitmap {
        return this.bitmap
    }
}
