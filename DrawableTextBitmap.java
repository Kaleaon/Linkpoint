package com.lumiyaviewer.lumiya.res.text;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

public class DrawableTextBitmap {
    private final float baselineOffset;
    private final Bitmap bitmap;

    DrawableTextBitmap(DrawableTextParams drawableTextParams, int i) {
        int i2 = 0;
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize((float) i);
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        String[] split = drawableTextParams.text().split("\n");
        int length = split.length;
        int i3 = 0;
        int i4 = 1;
        while (i3 < length) {
            String str = split[i3];
            paint.getTextBounds(str, 0, str.length(), rect);
            i3++;
            i4 = rect.width() > i4 ? rect.width() : i4;
        }
        float descent = paint.descent() - paint.ascent();
        int round = Math.round((((float) split.length) * descent) + 1.0f);
        i3 = drawableTextParams.backgroundColor() != 0 ? i : 0;
        int i5 = drawableTextParams.backgroundColor() != 0 ? i / 2 : 0;
        length = 1;
        while (length < i4 + i3 && length < 512) {
            length <<= 1;
        }
        int i6 = 1;
        while (i6 < round + i5 && i6 < 256) {
            i6 <<= 1;
        }
        length = Math.max(length, i6);
        this.bitmap = Bitmap.createBitmap(length, length, drawableTextParams.backgroundColor() == 0 ? Config.ALPHA_8 : Config.ARGB_8888);
        Canvas canvas = new Canvas(this.bitmap);
        if (drawableTextParams.backgroundColor() != 0) {
            paint.setColor(drawableTextParams.backgroundColor());
            int i7 = i4 + i3;
            int i8 = round + i5;
            canvas.drawRect((float) ((length - i7) / 2), (float) ((length - i8) / 2), (float) (length - ((length - i7) / 2)), (float) (length - ((length - i8) / 2)), paint);
        }
        paint.setARGB(255, 255, 255, 255);
        i4 = (length - round) / 2;
        i3 = split.length;
        while (i2 < i3) {
            canvas.drawText(split[i2], (float) (length / 2), ((float) i4) - paint.ascent(), paint);
            i4 = (int) (((float) i4) + (paint.descent() - paint.ascent()));
            i2++;
        }
        this.baselineOffset = (((float) round) + descent) / ((float) length);
    }

    public float getBaselineOffset() {
        return this.baselineOffset;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }
}
