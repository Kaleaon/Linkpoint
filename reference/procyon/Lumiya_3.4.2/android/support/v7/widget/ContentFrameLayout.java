// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.View;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View$MeasureSpec;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.content.Context;
import android.util.TypedValue;
import android.graphics.Rect;
import android.widget.FrameLayout;

public class ContentFrameLayout extends FrameLayout
{
    private OnAttachListener mAttachListener;
    private final Rect mDecorPadding;
    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private TypedValue mMinWidthMajor;
    private TypedValue mMinWidthMinor;
    
    public ContentFrameLayout(final Context context) {
        this(context, null);
    }
    
    public ContentFrameLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ContentFrameLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDecorPadding = new Rect();
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void dispatchFitSystemWindows(final Rect rect) {
        this.fitSystemWindows(rect);
    }
    
    public TypedValue getFixedHeightMajor() {
        if (this.mFixedHeightMajor == null) {
            this.mFixedHeightMajor = new TypedValue();
        }
        return this.mFixedHeightMajor;
    }
    
    public TypedValue getFixedHeightMinor() {
        if (this.mFixedHeightMinor == null) {
            this.mFixedHeightMinor = new TypedValue();
        }
        return this.mFixedHeightMinor;
    }
    
    public TypedValue getFixedWidthMajor() {
        if (this.mFixedWidthMajor == null) {
            this.mFixedWidthMajor = new TypedValue();
        }
        return this.mFixedWidthMajor;
    }
    
    public TypedValue getFixedWidthMinor() {
        if (this.mFixedWidthMinor == null) {
            this.mFixedWidthMinor = new TypedValue();
        }
        return this.mFixedWidthMinor;
    }
    
    public TypedValue getMinWidthMajor() {
        if (this.mMinWidthMajor == null) {
            this.mMinWidthMajor = new TypedValue();
        }
        return this.mMinWidthMajor;
    }
    
    public TypedValue getMinWidthMinor() {
        if (this.mMinWidthMinor == null) {
            this.mMinWidthMinor = new TypedValue();
        }
        return this.mMinWidthMinor;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAttachListener != null) {
            this.mAttachListener.onAttachedFromWindow();
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttachListener != null) {
            this.mAttachListener.onDetachedFromWindow();
        }
    }
    
    protected void onMeasure(int measureSpec, int measureSpec2) {
        final int n = 0;
        final DisplayMetrics displayMetrics = this.getContext().getResources().getDisplayMetrics();
        final boolean b = displayMetrics.widthPixels < displayMetrics.heightPixels;
        final int mode = View$MeasureSpec.getMode(measureSpec);
        final int mode2 = View$MeasureSpec.getMode(measureSpec2);
        boolean b2;
        int measureSpec3;
        if (mode != Integer.MIN_VALUE) {
            b2 = false;
            measureSpec3 = measureSpec;
        }
        else {
            TypedValue typedValue;
            if (!b) {
                typedValue = this.mFixedWidthMajor;
            }
            else {
                typedValue = this.mFixedWidthMinor;
            }
            if (typedValue != null && typedValue.type != 0) {
                int n2;
                if (typedValue.type != 5) {
                    if (typedValue.type != 6) {
                        n2 = 0;
                    }
                    else {
                        n2 = (int)typedValue.getFraction((float)displayMetrics.widthPixels, (float)displayMetrics.widthPixels);
                    }
                }
                else {
                    n2 = (int)typedValue.getDimension(displayMetrics);
                }
                if (n2 <= 0) {
                    b2 = false;
                    measureSpec3 = measureSpec;
                }
                else {
                    measureSpec3 = View$MeasureSpec.makeMeasureSpec(Math.min(n2 - (this.mDecorPadding.left + this.mDecorPadding.right), View$MeasureSpec.getSize(measureSpec)), 1073741824);
                    b2 = true;
                }
            }
            else {
                b2 = false;
                measureSpec3 = measureSpec;
            }
        }
        int measureSpec4;
        if (mode2 != Integer.MIN_VALUE) {
            measureSpec4 = measureSpec2;
        }
        else {
            TypedValue typedValue2;
            if (!b) {
                typedValue2 = this.mFixedHeightMinor;
            }
            else {
                typedValue2 = this.mFixedHeightMajor;
            }
            measureSpec4 = measureSpec2;
            if (typedValue2 != null) {
                measureSpec4 = measureSpec2;
                if (typedValue2.type != 0) {
                    if (typedValue2.type != 5) {
                        if (typedValue2.type != 6) {
                            measureSpec = 0;
                        }
                        else {
                            measureSpec = (int)typedValue2.getFraction((float)displayMetrics.heightPixels, (float)displayMetrics.heightPixels);
                        }
                    }
                    else {
                        measureSpec = (int)typedValue2.getDimension(displayMetrics);
                    }
                    measureSpec4 = measureSpec2;
                    if (measureSpec > 0) {
                        measureSpec4 = View$MeasureSpec.makeMeasureSpec(Math.min(measureSpec - (this.mDecorPadding.top + this.mDecorPadding.bottom), View$MeasureSpec.getSize(measureSpec2)), 1073741824);
                    }
                }
            }
        }
        super.onMeasure(measureSpec3, measureSpec4);
        final int measuredWidth = this.getMeasuredWidth();
        measureSpec2 = View$MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        if (!b2 && mode == Integer.MIN_VALUE) {
            TypedValue typedValue3;
            if (!b) {
                typedValue3 = this.mMinWidthMajor;
            }
            else {
                typedValue3 = this.mMinWidthMinor;
            }
            if (typedValue3 != null && typedValue3.type != 0) {
                if (typedValue3.type != 5) {
                    if (typedValue3.type != 6) {
                        measureSpec = 0;
                    }
                    else {
                        measureSpec = (int)typedValue3.getFraction((float)displayMetrics.widthPixels, (float)displayMetrics.widthPixels);
                    }
                }
                else {
                    measureSpec = (int)typedValue3.getDimension(displayMetrics);
                }
                if (measureSpec > 0) {
                    measureSpec -= this.mDecorPadding.left + this.mDecorPadding.right;
                }
                if (measuredWidth >= measureSpec) {
                    measureSpec = measureSpec2;
                    measureSpec2 = n;
                }
                else {
                    measureSpec = View$MeasureSpec.makeMeasureSpec(measureSpec, 1073741824);
                    measureSpec2 = 1;
                }
            }
            else {
                measureSpec = measureSpec2;
                measureSpec2 = n;
            }
        }
        else {
            measureSpec = measureSpec2;
            measureSpec2 = n;
        }
        if (measureSpec2 != 0) {
            super.onMeasure(measureSpec, measureSpec4);
        }
    }
    
    public void setAttachListener(final OnAttachListener mAttachListener) {
        this.mAttachListener = mAttachListener;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setDecorPadding(final int n, final int n2, final int n3, final int n4) {
        this.mDecorPadding.set(n, n2, n3, n4);
        if (ViewCompat.isLaidOut((View)this)) {
            this.requestLayout();
        }
    }
    
    public interface OnAttachListener
    {
        void onAttachedFromWindow();
        
        void onDetachedFromWindow();
    }
}
