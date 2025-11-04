// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class CardViewApi21Impl implements CardViewImpl
{
    private RoundRectDrawable getCardBackground(final CardViewDelegate cardViewDelegate) {
        return (RoundRectDrawable)cardViewDelegate.getCardBackground();
    }
    
    @Override
    public ColorStateList getBackgroundColor(final CardViewDelegate cardViewDelegate) {
        return this.getCardBackground(cardViewDelegate).getColor();
    }
    
    @Override
    public float getElevation(final CardViewDelegate cardViewDelegate) {
        return cardViewDelegate.getCardView().getElevation();
    }
    
    @Override
    public float getMaxElevation(final CardViewDelegate cardViewDelegate) {
        return this.getCardBackground(cardViewDelegate).getPadding();
    }
    
    @Override
    public float getMinHeight(final CardViewDelegate cardViewDelegate) {
        return this.getRadius(cardViewDelegate) * 2.0f;
    }
    
    @Override
    public float getMinWidth(final CardViewDelegate cardViewDelegate) {
        return this.getRadius(cardViewDelegate) * 2.0f;
    }
    
    @Override
    public float getRadius(final CardViewDelegate cardViewDelegate) {
        return this.getCardBackground(cardViewDelegate).getRadius();
    }
    
    @Override
    public void initStatic() {
    }
    
    @Override
    public void initialize(final CardViewDelegate cardViewDelegate, final Context context, final ColorStateList list, final float n, final float elevation, final float n2) {
        cardViewDelegate.setCardBackground(new RoundRectDrawable(list, n));
        final View cardView = cardViewDelegate.getCardView();
        cardView.setClipToOutline(true);
        cardView.setElevation(elevation);
        this.setMaxElevation(cardViewDelegate, n2);
    }
    
    @Override
    public void onCompatPaddingChanged(final CardViewDelegate cardViewDelegate) {
        this.setMaxElevation(cardViewDelegate, this.getMaxElevation(cardViewDelegate));
    }
    
    @Override
    public void onPreventCornerOverlapChanged(final CardViewDelegate cardViewDelegate) {
        this.setMaxElevation(cardViewDelegate, this.getMaxElevation(cardViewDelegate));
    }
    
    @Override
    public void setBackgroundColor(final CardViewDelegate cardViewDelegate, @Nullable final ColorStateList color) {
        this.getCardBackground(cardViewDelegate).setColor(color);
    }
    
    @Override
    public void setElevation(final CardViewDelegate cardViewDelegate, final float elevation) {
        cardViewDelegate.getCardView().setElevation(elevation);
    }
    
    @Override
    public void setMaxElevation(final CardViewDelegate cardViewDelegate, final float n) {
        this.getCardBackground(cardViewDelegate).setPadding(n, cardViewDelegate.getUseCompatPadding(), cardViewDelegate.getPreventCornerOverlap());
        this.updatePadding(cardViewDelegate);
    }
    
    @Override
    public void setRadius(final CardViewDelegate cardViewDelegate, final float radius) {
        this.getCardBackground(cardViewDelegate).setRadius(radius);
    }
    
    @Override
    public void updatePadding(final CardViewDelegate cardViewDelegate) {
        if (cardViewDelegate.getUseCompatPadding()) {
            final float maxElevation = this.getMaxElevation(cardViewDelegate);
            final float radius = this.getRadius(cardViewDelegate);
            final int n = (int)Math.ceil(RoundRectDrawableWithShadow.calculateHorizontalPadding(maxElevation, radius, cardViewDelegate.getPreventCornerOverlap()));
            final int n2 = (int)Math.ceil(RoundRectDrawableWithShadow.calculateVerticalPadding(maxElevation, radius, cardViewDelegate.getPreventCornerOverlap()));
            cardViewDelegate.setShadowPadding(n, n2, n, n2);
            return;
        }
        cardViewDelegate.setShadowPadding(0, 0, 0, 0);
    }
}
