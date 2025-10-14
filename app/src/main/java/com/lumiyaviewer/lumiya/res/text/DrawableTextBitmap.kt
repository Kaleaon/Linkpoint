package com.lumiyaviewer.lumiya.res.text

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
        paint.setTextSize((Float) i)
        paint.setAntiAlias(true)
        paint.setTextAlign(Paint.Align.CENTER)
        Array<String> split = drawableTextParams.text().split("\n");
        Int length = split.length
        Int i2 = 0
        Int i3 = 1
        while (i2 < length) {
            String str = split[i2]
            paint.getTextBounds(str, 0, str.length, rect)
            i2++
            i3 = rect.width() > i3 ? rect.width() : i3
        }
        Float descent = paint.descent() - paint.ascent()
        Int round = Math.round((((Float) split.length) * descent) + 1.0f)
        Int i4 = drawableTextParams.backgroundColor() != 0 ? i : 0
        Int i5 = drawableTextParams.backgroundColor() != 0 ? i / 2 : 0
        Int i6 = 1
        while (i6 < i3 + i4 && i6 < 512) {
            i6 <<= 1
        }
        Int i7 = 1
        while (i7 < round + i5 && i7 < 256) {
            i7 <<= 1
        }
        Int max = Math.max(i6, i7)
        this.bitmap = Bitmap.createBitmap(max, max, drawableTextParams.backgroundColor() == 0 ? Bitmap.Config.ALPHA_8 : Bitmap.Config.ARGB_8888)
        Canvas canvas = Canvas(this.bitmap)
        if (drawableTextParams.backgroundColor() != 0) {
            paint.setColor(drawableTextParams.backgroundColor())
            Int i8 = i3 + i4
            Int i9 = round + i5
            canvas.drawRect((Float) ((max - i8) / 2), (Float) ((max - i9) / 2), (Float) (max - ((max - i8) / 2)), (Float) (max - ((max - i9) / 2)), paint)
        }
        paint.setARGB(255, 255, 255, 255)
        Int i10 = (max - round) / 2
        for (String drawText : split) {
            canvas.drawText(drawText, (Float) (max / 2), ((Float) i10) - paint.ascent(), paint)
            i10 = (Int) (((Float) i10) + (paint.descent() - paint.ascent()))
        }
        this.baselineOffset = (((Float) round) + descent) / ((Float) max)
    }

    fun getBaselineOffset(): Float {
        return this.baselineOffset
    }

    fun getBitmap(): Bitmap {
        return this.bitmap
    }
}
