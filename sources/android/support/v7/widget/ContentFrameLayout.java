package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

public class ContentFrameLayout extends FrameLayout {
    private OnAttachListener mAttachListener;
    private final Rect mDecorPadding;
    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private TypedValue mMinWidthMajor;
    private TypedValue mMinWidthMinor;

    public interface OnAttachListener {
        void onAttachedFromWindow();

        void onDetachedFromWindow();
    }

    public ContentFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ContentFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ContentFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDecorPadding = new Rect();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void dispatchFitSystemWindows(Rect rect) {
        fitSystemWindows(rect);
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAttachListener != null) {
            this.mAttachListener.onAttachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttachListener != null) {
            this.mAttachListener.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        boolean z;
        int makeMeasureSpec;
        boolean z2 = false;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        boolean z3 = displayMetrics.widthPixels < displayMetrics.heightPixels;
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (mode != Integer.MIN_VALUE) {
            z = false;
        } else {
            TypedValue typedValue = !z3 ? this.mFixedWidthMajor : this.mFixedWidthMinor;
            if (typedValue == null || typedValue.type == 0) {
                z = false;
            } else {
                int fraction = typedValue.type != 5 ? typedValue.type != 6 ? 0 : (int) typedValue.getFraction((float) displayMetrics.widthPixels, (float) displayMetrics.widthPixels) : (int) typedValue.getDimension(displayMetrics);
                if (fraction <= 0) {
                    z = false;
                } else {
                    i = View.MeasureSpec.makeMeasureSpec(Math.min(fraction - (this.mDecorPadding.left + this.mDecorPadding.right), View.MeasureSpec.getSize(i)), 1073741824);
                    z = true;
                }
            }
        }
        if (mode2 == Integer.MIN_VALUE) {
            TypedValue typedValue2 = !z3 ? this.mFixedHeightMinor : this.mFixedHeightMajor;
            if (!(typedValue2 == null || typedValue2.type == 0)) {
                int fraction2 = typedValue2.type != 5 ? typedValue2.type != 6 ? 0 : (int) typedValue2.getFraction((float) displayMetrics.heightPixels, (float) displayMetrics.heightPixels) : (int) typedValue2.getDimension(displayMetrics);
                if (fraction2 > 0) {
                    i2 = View.MeasureSpec.makeMeasureSpec(Math.min(fraction2 - (this.mDecorPadding.top + this.mDecorPadding.bottom), View.MeasureSpec.getSize(i2)), 1073741824);
                }
            }
        }
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth();
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        if (!z && mode == Integer.MIN_VALUE) {
            TypedValue typedValue3 = !z3 ? this.mMinWidthMajor : this.mMinWidthMinor;
            if (typedValue3 == null || typedValue3.type == 0) {
                makeMeasureSpec = makeMeasureSpec2;
            } else {
                int fraction3 = typedValue3.type != 5 ? typedValue3.type != 6 ? 0 : (int) typedValue3.getFraction((float) displayMetrics.widthPixels, (float) displayMetrics.widthPixels) : (int) typedValue3.getDimension(displayMetrics);
                if (fraction3 > 0) {
                    fraction3 -= this.mDecorPadding.left + this.mDecorPadding.right;
                }
                if (measuredWidth >= fraction3) {
                    makeMeasureSpec = makeMeasureSpec2;
                } else {
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(fraction3, 1073741824);
                    z2 = true;
                }
            }
        } else {
            makeMeasureSpec = makeMeasureSpec2;
        }
        if (z2) {
            super.onMeasure(makeMeasureSpec, i2);
        }
    }

    public void setAttachListener(OnAttachListener onAttachListener) {
        this.mAttachListener = onAttachListener;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setDecorPadding(int i, int i2, int i3, int i4) {
        this.mDecorPadding.set(i, i2, i3, i4);
        if (ViewCompat.isLaidOut(this)) {
            requestLayout();
        }
    }
}
