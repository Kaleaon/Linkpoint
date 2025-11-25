package com.lumiyaviewer.lumiya.res.text

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.max
import kotlin.math.roundToInt

class DrawableTextBitmap(drawableTextParams: DrawableTextParams, size: Int) {
    private var baselineOffset: Float = 0f
    private var bitmap: Bitmap? = null

    init {
        val paint = Paint()
        val rect = Rect()
        paint.textSize = size.toFloat()
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        val split = drawableTextParams.text.split("\n")
        var maxWidth = 1
        
        for (str in split) {
            paint.getTextBounds(str, 0, str.length, rect)
            if (rect.width() > maxWidth) {
                maxWidth = rect.width()
            }
        }
        
        val descent = paint.descent() - paint.ascent()
        val totalHeight = ((split.size * descent) + 1.0f).roundToInt()
        
        val borderX = if (drawableTextParams.backgroundColor != 0) size else 0
        val borderY = if (drawableTextParams.backgroundColor != 0) size / 2 else 0
        
        var widthPow2 = 1
        while (widthPow2 < maxWidth + borderX && widthPow2 < 512) {
            widthPow2 = widthPow2 shl 1
        }
        
        var heightPow2 = 1
        while (heightPow2 < totalHeight + borderY && heightPow2 < 256) {
            heightPow2 = heightPow2 shl 1
        }
        
        val textureSize = max(widthPow2, heightPow2)
        
        val config = if (drawableTextParams.backgroundColor == 0) Bitmap.Config.ALPHA_8 else Bitmap.Config.ARGB_8888
        this.bitmap = Bitmap.createBitmap(textureSize, textureSize, config)
        
        val canvas = Canvas(this.bitmap!!)
        
        if (drawableTextParams.backgroundColor != 0) {
            paint.color = drawableTextParams.backgroundColor
            val contentWidth = maxWidth + borderX
            val contentHeight = totalHeight + borderY
            
            val left = ((textureSize - contentWidth) / 2).toFloat()
            val top = ((textureSize - contentHeight) / 2).toFloat()
            val right = (textureSize - (textureSize - contentWidth) / 2).toFloat()
            val bottom = (textureSize - (textureSize - contentHeight) / 2).toFloat()
            
            canvas.drawRect(left, top, right, bottom, paint)
        }
        
        paint.setARGB(255, 255, 255, 255)
        
        var y = (textureSize - totalHeight) / 2f - paint.ascent()
        val x = textureSize / 2f
        
        for (line in split) {
            canvas.drawText(line, x, y, paint)
            y += (paint.descent() - paint.ascent())
        }
        
        this.baselineOffset = (totalHeight.toFloat() + descent) / textureSize.toFloat()
    }

    fun getBaselineOffset(): Float {
        return this.baselineOffset
    }

    fun getBitmap(): Bitmap? {
        return this.bitmap
    }
}
