// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.graphics.drawable.Drawable$Callback;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.support.annotation.NonNull;
import android.graphics.Canvas;
import android.content.res.TypedArray;
import android.support.design.R;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.LinearLayoutCompat;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ForegroundLinearLayout extends LinearLayoutCompat
{
    private Drawable mForeground;
    boolean mForegroundBoundsChanged;
    private int mForegroundGravity;
    protected boolean mForegroundInPadding;
    private final Rect mOverlayBounds;
    private final Rect mSelfBounds;
    
    public ForegroundLinearLayout(final Context context) {
        this(context, null);
    }
    
    public ForegroundLinearLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ForegroundLinearLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = 119;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ForegroundLinearLayout, n, 0);
        this.mForegroundGravity = obtainStyledAttributes.getInt(R.styleable.ForegroundLinearLayout_android_foregroundGravity, this.mForegroundGravity);
        final Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.ForegroundLinearLayout_android_foreground);
        if (drawable != null) {
            this.setForeground(drawable);
        }
        this.mForegroundInPadding = obtainStyledAttributes.getBoolean(R.styleable.ForegroundLinearLayout_foregroundInsidePadding, true);
        obtainStyledAttributes.recycle();
    }
    
    public void draw(@NonNull final Canvas canvas) {
        super.draw(canvas);
        if (this.mForeground != null) {
            final Drawable mForeground = this.mForeground;
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                final Rect mSelfBounds = this.mSelfBounds;
                final Rect mOverlayBounds = this.mOverlayBounds;
                final int n = this.getRight() - this.getLeft();
                final int n2 = this.getBottom() - this.getTop();
                if (!this.mForegroundInPadding) {
                    mSelfBounds.set(this.getPaddingLeft(), this.getPaddingTop(), n - this.getPaddingRight(), n2 - this.getPaddingBottom());
                }
                else {
                    mSelfBounds.set(0, 0, n, n2);
                }
                Gravity.apply(this.mForegroundGravity, mForeground.getIntrinsicWidth(), mForeground.getIntrinsicHeight(), mSelfBounds, mOverlayBounds);
                mForeground.setBounds(mOverlayBounds);
            }
            mForeground.draw(canvas);
        }
    }
    
    @RequiresApi(21)
    public void drawableHotspotChanged(final float n, final float n2) {
        super.drawableHotspotChanged(n, n2);
        if (this.mForeground != null) {
            this.mForeground.setHotspot(n, n2);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mForeground != null && this.mForeground.isStateful()) {
            this.mForeground.setState(this.getDrawableState());
        }
    }
    
    public Drawable getForeground() {
        return this.mForeground;
    }
    
    public int getForegroundGravity() {
        return this.mForegroundGravity;
    }
    
    @RequiresApi(11)
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mForeground != null) {
            this.mForeground.jumpToCurrentState();
        }
    }
    
    @Override
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mForegroundBoundsChanged |= b;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        this.mForegroundBoundsChanged = true;
    }
    
    public void setForeground(final Drawable mForeground) {
        if (this.mForeground != mForeground) {
            if (this.mForeground != null) {
                this.mForeground.setCallback((Drawable$Callback)null);
                this.unscheduleDrawable(this.mForeground);
            }
            if ((this.mForeground = mForeground) == null) {
                this.setWillNotDraw(true);
            }
            else {
                this.setWillNotDraw(false);
                mForeground.setCallback((Drawable$Callback)this);
                if (mForeground.isStateful()) {
                    mForeground.setState(this.getDrawableState());
                }
                if (this.mForegroundGravity == 119) {
                    mForeground.getPadding(new Rect());
                }
            }
            this.requestLayout();
            this.invalidate();
        }
    }
    
    public void setForegroundGravity(int mForegroundGravity) {
        if (this.mForegroundGravity != mForegroundGravity) {
            if ((0x800007 & mForegroundGravity) == 0x0) {
                mForegroundGravity |= 0x800003;
            }
            if ((mForegroundGravity & 0x70) == 0x0) {
                mForegroundGravity |= 0x30;
            }
            this.mForegroundGravity = mForegroundGravity;
            if (this.mForegroundGravity == 119 && this.mForeground != null) {
                this.mForeground.getPadding(new Rect());
            }
            this.requestLayout();
        }
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        boolean b = false;
        if (super.verifyDrawable(drawable) || drawable == this.mForeground) {
            b = true;
        }
        return b;
    }
}
