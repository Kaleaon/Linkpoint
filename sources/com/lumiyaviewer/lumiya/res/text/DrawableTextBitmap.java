package com.lumiyaviewer.lumiya.res.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class DrawableTextBitmap {
    private final float baselineOffset;
    private final Bitmap bitmap;

    DrawableTextBitmap(DrawableTextParams drawableTextParams, int i) {
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize((float) i);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        String[] split = drawableTextParams.text().split("\n");
        int length = split.length;
        int i2 = 0;
        int i3 = 1;
        while (i2 < length) {
            String str = split[i2];
            paint.getTextBounds(str, 0, str.length(), rect);
            i2++;
            i3 = rect.width() > i3 ? rect.width() : i3;
        }
        float descent = paint.descent() - paint.ascent();
        int round = Math.round((((float) split.length) * descent) + 1.0f);
        int i4 = drawableTextParams.backgroundColor() != 0 ? i : 0;
        int i5 = drawableTextParams.backgroundColor() != 0 ? i / 2 : 0;
        int i6 = 1;
        while (i6 < i3 + i4 && i6 < 512) {
            i6 <<= 1;
        }
        int i7 = 1;
        while (i7 < round + i5 && i7 < 256) {
            i7 <<= 1;
        }
        int max = Math.max(i6, i7);
        this.bitmap = Bitmap.createBitmap(max, max, drawableTextParams.backgroundColor() == 0 ? Bitmap.Config.ALPHA_8 : Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.bitmap);
        if (drawableTextParams.backgroundColor() != 0) {
            paint.setColor(drawableTextParams.backgroundColor());
            int i8 = i3 + i4;
            int i9 = round + i5;
            canvas.drawRect((float) ((max - i8) / 2), (float) ((max - i9) / 2), (float) (max - ((max - i8) / 2)), (float) (max - ((max - i9) / 2)), paint);
        }
        paint.setARGB(255, 255, 255, 255);
        int i10 = (max - round) / 2;
        for (String drawText : split) {
            canvas.drawText(drawText, (float) (max / 2), ((float) i10) - paint.ascent(), paint);
            i10 = (int) (((float) i10) + (paint.descent() - paint.ascent()));
        }
        this.baselineOffset = (((float) round) + descent) / ((float) max);
    }

    public float getBaselineOffset() {
        return this.baselineOffset;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }
}
