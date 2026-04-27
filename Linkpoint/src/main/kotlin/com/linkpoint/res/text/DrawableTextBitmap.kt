package com.linkpoint.res.text

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class DrawableTextBitmap {
    private val Float baselineOffset
    private val Bitmap bitmap

    DrawableTextBitmap(DrawableTextParams drawableTextParams, Int i) {
        val paint: Paint = Paint()
        val rect: Rect = Rect()
        paint.setTextSize((Float) i)
        paint.setAntiAlias(true)
        paint.setTextAlign(Paint.Align.CENTER)
        val split: Array<String> = drawableTextParams.text().split("\n")
        val length: Int = split.length
        val i2: Int = 0
        val i3: Int = 1
        while (i2 < length) {
            val str: String = split[i2]
            paint.getTextBounds(str, 0, str.length(), rect)
            i2++
            i3 = rect.width() > i3 ? rect.width() : i3
        }
        val descent: Float = paint.descent() - paint.ascent()
        val round: Int = Math.round((((Float) split.length) * descent) + 1.0f)
        val i4: Int = drawableTextParams.backgroundColor() != 0 ? i : 0
        val i5: Int = drawableTextParams.backgroundColor() != 0 ? i / 2 : 0
        val i6: Int = 1
        while (i6 < i3 + i4 && i6 < 512) {
            i6 <<= 1
        }
        val i7: Int = 1
        while (i7 < round + i5 && i7 < 256) {
            i7 <<= 1
        }
        val max: Int = Math.max(i6, i7)
        this.bitmap = Bitmap.createBitmap(max, max, drawableTextParams.backgroundColor() == 0 ? Bitmap.Config.ALPHA_8 : Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(this.bitmap)
        if (drawableTextParams.backgroundColor() != 0) {
            paint.setColor(drawableTextParams.backgroundColor())
            val i8: Int = i3 + i4
            val i9: Int = round + i5
            canvas.drawRect((Float) ((max - i8) / 2), (Float) ((max - i9) / 2), (Float) (max - ((max - i8) / 2)), (Float) (max - ((max - i9) / 2)), paint)
        }
        paint.setARGB(255, 255, 255, 255)
        val i10: Int = (max - round) / 2
        for (String drawText : split) {
            canvas.drawText(drawText, (Float) (max / 2), ((Float) i10) - paint.ascent(), paint)
            i10 = (Int) (((Float) i10) + (paint.descent() - paint.ascent()))
        }
        this.baselineOffset = (((Float) round) + descent) / ((Float) max)
    }

     public fun getBaselineOffset(): Float {
        return this.baselineOffset
    }

     public fun getBitmap(): Bitmap {
        return this.bitmap
    }
}
