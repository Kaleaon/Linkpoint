// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.support.annotation.RequiresApi;

@RequiresApi(17)
class CardViewApi17Impl extends CardViewBaseImpl
{
    @Override
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper = (RoundRectDrawableWithShadow.RoundRectHelper)new RoundRectDrawableWithShadow.RoundRectHelper() {
            @Override
            public void drawRoundRect(final Canvas canvas, final RectF rectF, final float n, final Paint paint) {
                canvas.drawRoundRect(rectF, n, n, paint);
            }
        };
    }
}
