// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.content.res.ColorStateList;
import android.content.Context;
import android.graphics.RectF;

class CardViewBaseImpl implements CardViewImpl
{
    private final RectF mCornerRect;
    
    CardViewBaseImpl() {
        this.mCornerRect = new RectF();
    }
    
    private RoundRectDrawableWithShadow createBackground(final Context context, final ColorStateList list, final float n, final float n2, final float n3) {
        return new RoundRectDrawableWithShadow(context.getResources(), list, n, n2, n3);
    }
    
    private RoundRectDrawableWithShadow getShadowBackground(final CardViewDelegate cardViewDelegate) {
        return (RoundRectDrawableWithShadow)cardViewDelegate.getCardBackground();
    }
    
    @Override
    public ColorStateList getBackgroundColor(final CardViewDelegate cardViewDelegate) {
        return this.getShadowBackground(cardViewDelegate).getColor();
    }
    
    @Override
    public float getElevation(final CardViewDelegate cardViewDelegate) {
        return this.getShadowBackground(cardViewDelegate).getShadowSize();
    }
    
    @Override
    public float getMaxElevation(final CardViewDelegate cardViewDelegate) {
        return this.getShadowBackground(cardViewDelegate).getMaxShadowSize();
    }
    
    @Override
    public float getMinHeight(final CardViewDelegate cardViewDelegate) {
        return this.getShadowBackground(cardViewDelegate).getMinHeight();
    }
    
    @Override
    public float getMinWidth(final CardViewDelegate cardViewDelegate) {
        return this.getShadowBackground(cardViewDelegate).getMinWidth();
    }
    
    @Override
    public float getRadius(final CardViewDelegate cardViewDelegate) {
        return this.getShadowBackground(cardViewDelegate).getCornerRadius();
    }
    
    @Override
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper = (RoundRectDrawableWithShadow.RoundRectHelper)new RoundRectDrawableWithShadow.RoundRectHelper() {
            @Override
            public void drawRoundRect(final Canvas canvas, final RectF rectF, final float n, final Paint paint) {
                final float n2 = 2.0f * n;
                final float n3 = rectF.width() - n2 - 1.0f;
                final float height = rectF.height();
                if (n >= 1.0f) {
                    final float n4 = n + 0.5f;
                    CardViewBaseImpl.this.mCornerRect.set(-n4, -n4, n4, n4);
                    final int save = canvas.save();
                    canvas.translate(rectF.left + n4, rectF.top + n4);
                    canvas.drawArc(CardViewBaseImpl.this.mCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.translate(n3, 0.0f);
                    canvas.rotate(90.0f);
                    canvas.drawArc(CardViewBaseImpl.this.mCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.translate(height - n2 - 1.0f, 0.0f);
                    canvas.rotate(90.0f);
                    canvas.drawArc(CardViewBaseImpl.this.mCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.translate(n3, 0.0f);
                    canvas.rotate(90.0f);
                    canvas.drawArc(CardViewBaseImpl.this.mCornerRect, 180.0f, 90.0f, true, paint);
                    canvas.restoreToCount(save);
                    canvas.drawRect(rectF.left + n4 - 1.0f, rectF.top, 1.0f + (rectF.right - n4), rectF.top + n4, paint);
                    canvas.drawRect(rectF.left + n4 - 1.0f, rectF.bottom - n4, 1.0f + (rectF.right - n4), rectF.bottom, paint);
                }
                canvas.drawRect(rectF.left, rectF.top + n, rectF.right, rectF.bottom - n, paint);
            }
        };
    }
    
    @Override
    public void initialize(final CardViewDelegate cardViewDelegate, final Context context, final ColorStateList list, final float n, final float n2, final float n3) {
        final RoundRectDrawableWithShadow background = this.createBackground(context, list, n, n2, n3);
        background.setAddPaddingForCorners(cardViewDelegate.getPreventCornerOverlap());
        cardViewDelegate.setCardBackground(background);
        this.updatePadding(cardViewDelegate);
    }
    
    @Override
    public void onCompatPaddingChanged(final CardViewDelegate cardViewDelegate) {
    }
    
    @Override
    public void onPreventCornerOverlapChanged(final CardViewDelegate cardViewDelegate) {
        this.getShadowBackground(cardViewDelegate).setAddPaddingForCorners(cardViewDelegate.getPreventCornerOverlap());
        this.updatePadding(cardViewDelegate);
    }
    
    @Override
    public void setBackgroundColor(final CardViewDelegate cardViewDelegate, @Nullable final ColorStateList color) {
        this.getShadowBackground(cardViewDelegate).setColor(color);
    }
    
    @Override
    public void setElevation(final CardViewDelegate cardViewDelegate, final float shadowSize) {
        this.getShadowBackground(cardViewDelegate).setShadowSize(shadowSize);
    }
    
    @Override
    public void setMaxElevation(final CardViewDelegate cardViewDelegate, final float maxShadowSize) {
        this.getShadowBackground(cardViewDelegate).setMaxShadowSize(maxShadowSize);
        this.updatePadding(cardViewDelegate);
    }
    
    @Override
    public void setRadius(final CardViewDelegate cardViewDelegate, final float cornerRadius) {
        this.getShadowBackground(cardViewDelegate).setCornerRadius(cornerRadius);
        this.updatePadding(cardViewDelegate);
    }
    
    @Override
    public void updatePadding(final CardViewDelegate cardViewDelegate) {
        final Rect rect = new Rect();
        this.getShadowBackground(cardViewDelegate).getMaxShadowAndCornerPadding(rect);
        cardViewDelegate.setMinWidthHeightInternal((int)Math.ceil(this.getMinWidth(cardViewDelegate)), (int)Math.ceil(this.getMinHeight(cardViewDelegate)));
        cardViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
    }
}
