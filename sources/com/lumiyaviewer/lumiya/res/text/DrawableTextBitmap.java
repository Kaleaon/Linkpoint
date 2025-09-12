// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// Referenced classes of package com.lumiyaviewer.lumiya.res.text:
//            DrawableTextParams

public class DrawableTextBitmap
{

    private final float baselineOffset;
    private final Bitmap bitmap;

    DrawableTextBitmap(DrawableTextParams drawabletextparams, int i)
    {
        boolean flag = false;
        super();
        Paint paint = new Paint();
        Object obj = new Rect();
        paint.setTextSize(i);
        paint.setAntiAlias(true);
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        String as[] = drawabletextparams.text().split("\n");
        int l = as.length;
        int k = 0;
        int j = 1;
        for (; k < l; k++)
        {
            String s = as[k];
            paint.getTextBounds(s, 0, s.length(), ((Rect) (obj)));
            if (((Rect) (obj)).width() > j)
            {
                j = ((Rect) (obj)).width();
            }
        }

        float f = paint.descent() - paint.ascent();
        int j1 = Math.round((float)as.length * f + 1.0F);
        if (drawabletextparams.backgroundColor() != 0)
        {
            k = i;
        } else
        {
            k = 0;
        }
        if (drawabletextparams.backgroundColor() != 0)
        {
            i /= 2;
        } else
        {
            i = 0;
        }
        for (l = 1; l < j + k && l < 512; l <<= 1) { }
        int i1;
        for (i1 = 1; i1 < j1 + i && i1 < 256; i1 <<= 1) { }
        l = Math.max(l, i1);
        if (drawabletextparams.backgroundColor() == 0)
        {
            obj = android.graphics.Bitmap.Config.ALPHA_8;
        } else
        {
            obj = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = Bitmap.createBitmap(l, l, ((android.graphics.Bitmap.Config) (obj)));
        obj = new Canvas(bitmap);
        if (drawabletextparams.backgroundColor() != 0)
        {
            paint.setColor(drawabletextparams.backgroundColor());
            j += k;
            i = j1 + i;
            ((Canvas) (obj)).drawRect((l - j) / 2, (l - i) / 2, l - (l - j) / 2, l - (l - i) / 2, paint);
        }
        paint.setARGB(255, 255, 255, 255);
        j = (l - j1) / 2;
        k = as.length;
        for (i = ((flag) ? 1 : 0); i < k; i++)
        {
            ((Canvas) (obj)).drawText(as[i], l / 2, (float)j - paint.ascent(), paint);
            j = (int)((float)j + (paint.descent() - paint.ascent()));
        }

        baselineOffset = ((float)j1 + f) / (float)l;
    }

    public float getBaselineOffset()
    {
        return baselineOffset;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }
}
