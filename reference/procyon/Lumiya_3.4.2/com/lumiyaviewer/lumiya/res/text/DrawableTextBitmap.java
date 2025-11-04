// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.text;

import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Paint$Align;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Bitmap;

public class DrawableTextBitmap
{
    private final float baselineOffset;
    private final Bitmap bitmap;
    
    DrawableTextBitmap(final DrawableTextParams drawableTextParams, int i) {
        final int n = 0;
        final Paint paint = new Paint();
        final Rect rect = new Rect();
        paint.setTextSize((float)i);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint$Align.CENTER);
        final String[] split = drawableTextParams.text().split("\n");
        final int length = split.length;
        int j = 0;
        int width = 1;
        while (j < length) {
            final String s = split[j];
            paint.getTextBounds(s, 0, s.length(), rect);
            if (rect.width() > width) {
                width = rect.width();
            }
            ++j;
        }
        final float n2 = paint.descent() - paint.ascent();
        final int round = Math.round(split.length * n2 + 1.0f);
        int n3;
        if (drawableTextParams.backgroundColor() != 0) {
            n3 = i;
        }
        else {
            n3 = 0;
        }
        if (drawableTextParams.backgroundColor() != 0) {
            i /= 2;
        }
        else {
            i = 0;
        }
        int a;
        for (a = 1; a < width + n3 && a < 512; a <<= 1) {}
        int b;
        for (b = 1; b < round + i && b < 256; b <<= 1) {}
        final int max = Math.max(a, b);
        Bitmap$Config bitmap$Config;
        if (drawableTextParams.backgroundColor() == 0) {
            bitmap$Config = Bitmap$Config.ALPHA_8;
        }
        else {
            bitmap$Config = Bitmap$Config.ARGB_8888;
        }
        this.bitmap = Bitmap.createBitmap(max, max, bitmap$Config);
        final Canvas canvas = new Canvas(this.bitmap);
        if (drawableTextParams.backgroundColor() != 0) {
            paint.setColor(drawableTextParams.backgroundColor());
            final int n4 = width + n3;
            i += round;
            canvas.drawRect((float)((max - n4) / 2), (float)((max - i) / 2), (float)(max - (max - n4) / 2), (float)(max - (max - i) / 2), paint);
        }
        paint.setARGB(255, 255, 255, 255);
        int n5 = (max - round) / 2;
        int length2;
        for (length2 = split.length, i = n; i < length2; ++i) {
            canvas.drawText(split[i], (float)(max / 2), n5 - paint.ascent(), paint);
            n5 += (int)(paint.descent() - paint.ascent());
        }
        this.baselineOffset = (round + n2) / max;
    }
    
    public float getBaselineOffset() {
        return this.baselineOffset;
    }
    
    public Bitmap getBitmap() {
        return this.bitmap;
    }
}
