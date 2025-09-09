package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2);
            this.gravity = -1;
            this.weight = f;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayoutCompat_Layout);
            this.weight = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public LinearLayoutCompat(Context context) {
        this(context, (AttributeSet) null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.LinearLayoutCompat, i, 0);
        int i2 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (i2 >= 0) {
            setOrientation(i2);
        }
        int i3 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (i3 >= 0) {
            setGravity(i3);
        }
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!z) {
            setBaselineAligned(z);
        }
        this.mWeightSum = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(obtainStyledAttributes.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        obtainStyledAttributes.recycle();
    }

    private void forceUniformHeight(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i4 = layoutParams.width;
                    layoutParams.width = virtualChildAt.getMeasuredWidth();
                    measureChildWithMargins(virtualChildAt, i2, 0, makeMeasureSpec, 0);
                    layoutParams.width = i4;
                }
            }
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = virtualChildAt.getMeasuredHeight();
                    measureChildWithMargins(virtualChildAt, makeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i + i3, i2 + i4);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: package-private */
    public void drawDividersHorizontal(Canvas canvas) {
        int width;
        int virtualChildCount = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || !hasDividerBeforeChildAt(i))) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                drawVerticalDivider(canvas, !isLayoutRtl ? (virtualChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerWidth : layoutParams.rightMargin + virtualChildAt.getRight());
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 != null) {
                LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                width = !isLayoutRtl ? layoutParams2.rightMargin + virtualChildAt2.getRight() : (virtualChildAt2.getLeft() - layoutParams2.leftMargin) - this.mDividerWidth;
            } else {
                width = !isLayoutRtl ? (getWidth() - getPaddingRight()) - this.mDividerWidth : getPaddingLeft();
            }
            drawVerticalDivider(canvas, width);
        }
    }

    /* access modifiers changed from: package-private */
    public void drawDividersVertical(Canvas canvas) {
        int height;
        int virtualChildCount = getVirtualChildCount();
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || !hasDividerBeforeChildAt(i))) {
                drawHorizontalDivider(canvas, (virtualChildAt.getTop() - ((LayoutParams) virtualChildAt.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 != null) {
                height = ((LayoutParams) virtualChildAt2.getLayoutParams()).bottomMargin + virtualChildAt2.getBottom();
            } else {
                height = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            }
            drawHorizontalDivider(canvas, height);
        }
    }

    /* access modifiers changed from: package-private */
    public void drawHorizontalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, i, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + i);
        this.mDivider.draw(canvas);
    }

    /* access modifiers changed from: package-private */
    public void drawVerticalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(i, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + i, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (this.mOrientation != 1) {
            return null;
        }
        return new LayoutParams(-1, -2);
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    public int getBaseline() {
        int bottom;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (getChildCount() > this.mBaselineAlignedChildIndex) {
            View childAt = getChildAt(this.mBaselineAlignedChildIndex);
            int baseline = childAt.getBaseline();
            if (baseline != -1) {
                int i = this.mBaselineChildTop;
                if (this.mOrientation == 1) {
                    int i2 = this.mGravity & 112;
                    if (i2 != 48) {
                        switch (i2) {
                            case 16:
                                bottom = i + (((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2);
                                break;
                            case 80:
                                bottom = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                                break;
                            default:
                                bottom = i;
                                break;
                        }
                    } else {
                        bottom = i;
                    }
                } else {
                    bottom = i;
                }
                return ((LayoutParams) childAt.getLayoutParams()).topMargin + bottom + baseline;
            } else if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            } else {
                throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
            }
        } else {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    /* access modifiers changed from: package-private */
    public int getChildrenSkipCount(View view, int i) {
        return 0;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    public int getGravity() {
        return this.mGravity;
    }

    /* access modifiers changed from: package-private */
    public int getLocationOffset(View view) {
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int getNextLocationOffset(View view) {
        return 0;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    /* access modifiers changed from: package-private */
    public View getVirtualChildAt(int i) {
        return getChildAt(i);
    }

    /* access modifiers changed from: package-private */
    public int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    /* access modifiers changed from: protected */
    public boolean hasDividerBeforeChildAt(int i) {
        if (i == 0) {
            return (this.mShowDividers & 1) != 0;
        }
        if (i == getChildCount()) {
            return (this.mShowDividers & 4) != 0;
        }
        if ((this.mShowDividers & 2) == 0) {
            return false;
        }
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (getChildAt(i2).getVisibility() != 8) {
                return true;
            }
        }
        return false;
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    /* access modifiers changed from: package-private */
    public void layoutHorizontal(int i, int i2, int i3, int i4) {
        int paddingLeft;
        int i5;
        int i6;
        int i7;
        int i8;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop = getPaddingTop();
        int i9 = i4 - i2;
        int paddingBottom = i9 - getPaddingBottom();
        int paddingBottom2 = (i9 - paddingTop) - getPaddingBottom();
        int virtualChildCount = getVirtualChildCount();
        int i10 = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int i11 = this.mGravity & 112;
        boolean z = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        switch (GravityCompat.getAbsoluteGravity(i10, ViewCompat.getLayoutDirection(this))) {
            case 1:
                paddingLeft = getPaddingLeft() + (((i3 - i) - this.mTotalLength) / 2);
                break;
            case 5:
                paddingLeft = ((getPaddingLeft() + i3) - i) - this.mTotalLength;
                break;
            default:
                paddingLeft = getPaddingLeft();
                break;
        }
        if (!isLayoutRtl) {
            i5 = 1;
            i6 = 0;
        } else {
            i5 = -1;
            i6 = virtualChildCount - 1;
        }
        int i12 = 0;
        int i13 = paddingLeft;
        while (i12 < virtualChildCount) {
            int i14 = i6 + (i5 * i12);
            View virtualChildAt = getVirtualChildAt(i14);
            if (virtualChildAt == null) {
                i13 += measureNullChild(i14);
                i7 = i12;
            } else if (virtualChildAt.getVisibility() == 8) {
                i7 = i12;
            } else {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                int baseline = (z && layoutParams.height != -1) ? virtualChildAt.getBaseline() : -1;
                int i15 = layoutParams.gravity;
                if (i15 < 0) {
                    i15 = i11;
                }
                switch (i15 & 112) {
                    case 16:
                        i8 = ((((paddingBottom2 - measuredHeight) / 2) + paddingTop) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        break;
                    case 48:
                        i8 = paddingTop + layoutParams.topMargin;
                        if (baseline != -1) {
                            i8 += iArr[1] - baseline;
                            break;
                        }
                        break;
                    case 80:
                        i8 = (paddingBottom - measuredHeight) - layoutParams.bottomMargin;
                        if (baseline != -1) {
                            i8 -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - baseline);
                            break;
                        }
                        break;
                    default:
                        i8 = paddingTop;
                        break;
                }
                int i16 = (!hasDividerBeforeChildAt(i14) ? i13 : this.mDividerWidth + i13) + layoutParams.leftMargin;
                setChildFrame(virtualChildAt, i16 + getLocationOffset(virtualChildAt), i8, measuredWidth, measuredHeight);
                i13 = i16 + layoutParams.rightMargin + measuredWidth + getNextLocationOffset(virtualChildAt);
                i7 = getChildrenSkipCount(virtualChildAt, i14) + i12;
            }
            i12 = i7 + 1;
        }
    }

    /* access modifiers changed from: package-private */
    public void layoutVertical(int i, int i2, int i3, int i4) {
        int paddingTop;
        int i5;
        int i6;
        int paddingLeft = getPaddingLeft();
        int i7 = i3 - i;
        int paddingRight = i7 - getPaddingRight();
        int paddingRight2 = (i7 - paddingLeft) - getPaddingRight();
        int virtualChildCount = getVirtualChildCount();
        int i8 = this.mGravity & 112;
        int i9 = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        switch (i8) {
            case 16:
                paddingTop = getPaddingTop() + (((i4 - i2) - this.mTotalLength) / 2);
                break;
            case 80:
                paddingTop = ((getPaddingTop() + i4) - i2) - this.mTotalLength;
                break;
            default:
                paddingTop = getPaddingTop();
                break;
        }
        int i10 = 0;
        int i11 = paddingTop;
        while (i10 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i10);
            if (virtualChildAt == null) {
                i11 += measureNullChild(i10);
                i5 = i10;
            } else if (virtualChildAt.getVisibility() == 8) {
                i5 = i10;
            } else {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                int i12 = layoutParams.gravity;
                if (i12 < 0) {
                    i12 = i9;
                }
                switch (GravityCompat.getAbsoluteGravity(i12, ViewCompat.getLayoutDirection(this)) & 7) {
                    case 1:
                        i6 = ((((paddingRight2 - measuredWidth) / 2) + paddingLeft) + layoutParams.leftMargin) - layoutParams.rightMargin;
                        break;
                    case 5:
                        i6 = (paddingRight - measuredWidth) - layoutParams.rightMargin;
                        break;
                    default:
                        i6 = paddingLeft + layoutParams.leftMargin;
                        break;
                }
                int i13 = (!hasDividerBeforeChildAt(i10) ? i11 : this.mDividerHeight + i11) + layoutParams.topMargin;
                setChildFrame(virtualChildAt, i6, i13 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                i11 = i13 + layoutParams.bottomMargin + measuredHeight + getNextLocationOffset(virtualChildAt);
                i5 = getChildrenSkipCount(virtualChildAt, i10) + i10;
            }
            i10 = i5 + 1;
        }
    }

    /* access modifiers changed from: package-private */
    public void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        measureChildWithMargins(view, i2, i3, i4, i5);
    }

    /* access modifiers changed from: package-private */
    public void measureHorizontal(int i, int i2) {
        int i3;
        int max;
        int i4;
        float f;
        int i5;
        int i6;
        int i7;
        boolean z;
        int i8;
        int i9;
        float f2;
        float f3;
        boolean z2;
        int i10;
        int i11;
        int i12;
        boolean z3;
        boolean z4;
        int max2;
        int i13;
        int i14;
        this.mTotalLength = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int i18 = 0;
        boolean z5 = true;
        float f4 = 0.0f;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        boolean z6 = false;
        boolean z7 = false;
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        iArr[3] = -1;
        iArr[2] = -1;
        iArr[1] = -1;
        iArr[0] = -1;
        iArr2[3] = -1;
        iArr2[2] = -1;
        iArr2[1] = -1;
        iArr2[0] = -1;
        boolean z8 = this.mBaselineAligned;
        boolean z9 = this.mUseLargestChild;
        boolean z10 = mode == 1073741824;
        int i19 = Integer.MIN_VALUE;
        int i20 = 0;
        while (i20 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i20);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(i20);
                f3 = f4;
                z2 = z5;
                i10 = i16;
                i11 = i15;
            } else if (virtualChildAt.getVisibility() != 8) {
                if (hasDividerBeforeChildAt(i20)) {
                    this.mTotalLength += this.mDividerWidth;
                }
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                float f5 = f4 + layoutParams.weight;
                if (mode == 1073741824 && layoutParams.width == 0 && layoutParams.weight > 0.0f) {
                    if (!z10) {
                        int i21 = this.mTotalLength;
                        this.mTotalLength = Math.max(i21, layoutParams.leftMargin + i21 + layoutParams.rightMargin);
                    } else {
                        this.mTotalLength += layoutParams.leftMargin + layoutParams.rightMargin;
                    }
                    if (!z8) {
                        z3 = true;
                        i12 = i19;
                    } else {
                        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                        virtualChildAt.measure(makeMeasureSpec, makeMeasureSpec);
                        i12 = i19;
                        z3 = z7;
                    }
                } else {
                    if (layoutParams.width == 0 && layoutParams.weight > 0.0f) {
                        layoutParams.width = -2;
                        i14 = 0;
                    } else {
                        i14 = Integer.MIN_VALUE;
                    }
                    measureChildBeforeLayout(virtualChildAt, i20, i, f5 == 0.0f ? this.mTotalLength : 0, i2, 0);
                    if (i14 != Integer.MIN_VALUE) {
                        layoutParams.width = i14;
                    }
                    int measuredWidth = virtualChildAt.getMeasuredWidth();
                    if (!z10) {
                        int i22 = this.mTotalLength;
                        this.mTotalLength = Math.max(i22, i22 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt));
                    } else {
                        this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                    }
                    if (!z9) {
                        i12 = i19;
                        z3 = z7;
                    } else {
                        i12 = Math.max(measuredWidth, i19);
                        z3 = z7;
                    }
                }
                boolean z11 = false;
                if (mode2 != 1073741824 && layoutParams.height == -1) {
                    z4 = true;
                    z11 = true;
                } else {
                    z4 = z6;
                }
                int i23 = layoutParams.topMargin + layoutParams.bottomMargin;
                int measuredHeight = virtualChildAt.getMeasuredHeight() + i23;
                int combineMeasuredStates = View.combineMeasuredStates(i16, virtualChildAt.getMeasuredState());
                if (z8) {
                    int baseline = virtualChildAt.getBaseline();
                    if (baseline != -1) {
                        int i24 = ((((layoutParams.gravity >= 0 ? layoutParams.gravity : this.mGravity) & 112) >> 4) & -2) >> 1;
                        iArr[i24] = Math.max(iArr[i24], baseline);
                        iArr2[i24] = Math.max(iArr2[i24], measuredHeight - baseline);
                    }
                }
                int max3 = Math.max(i15, measuredHeight);
                z2 = z5 && layoutParams.height == -1;
                if (layoutParams.weight > 0.0f) {
                    i13 = Math.max(i18, !z11 ? measuredHeight : i23);
                    max2 = i17;
                } else {
                    if (z11) {
                        measuredHeight = i23;
                    }
                    max2 = Math.max(i17, measuredHeight);
                    i13 = i18;
                }
                i20 += getChildrenSkipCount(virtualChildAt, i20);
                i19 = i12;
                z7 = z3;
                i18 = i13;
                i17 = max2;
                i10 = combineMeasuredStates;
                f3 = f5;
                z6 = z4;
                i11 = max3;
            } else {
                i20 += getChildrenSkipCount(virtualChildAt, i20);
                f3 = f4;
                z2 = z5;
                i10 = i16;
                i11 = i15;
            }
            i20++;
            z5 = z2;
            i16 = i10;
            i15 = i11;
            f4 = f3;
        }
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        int max4 = (iArr[1] == -1 && iArr[0] == -1 && iArr[2] == -1 && iArr[3] == -1) ? i15 : Math.max(i15, Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))) + Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))));
        if (z9 && (mode == Integer.MIN_VALUE || mode == 0)) {
            this.mTotalLength = 0;
            int i25 = 0;
            while (i25 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(i25);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(i25);
                    i3 = i25;
                } else if (virtualChildAt2.getVisibility() != 8) {
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    if (!z10) {
                        int i26 = this.mTotalLength;
                        this.mTotalLength = Math.max(i26, layoutParams2.rightMargin + i26 + i19 + layoutParams2.leftMargin + getNextLocationOffset(virtualChildAt2));
                        i3 = i25;
                    } else {
                        this.mTotalLength = layoutParams2.rightMargin + layoutParams2.leftMargin + i19 + getNextLocationOffset(virtualChildAt2) + this.mTotalLength;
                        i3 = i25;
                    }
                } else {
                    i3 = getChildrenSkipCount(virtualChildAt2, i25) + i25;
                }
                i25 = i3 + 1;
            }
        }
        this.mTotalLength += getPaddingLeft() + getPaddingRight();
        int resolveSizeAndState = View.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), i, 0);
        int i27 = (16777215 & resolveSizeAndState) - this.mTotalLength;
        if (!z7 && (i27 == 0 || f4 <= 0.0f)) {
            max = Math.max(i17, i18);
            if (z9 && mode != 1073741824) {
                int i28 = 0;
                while (true) {
                    int i29 = i28;
                    if (i29 >= virtualChildCount) {
                        break;
                    }
                    View virtualChildAt3 = getVirtualChildAt(i29);
                    if (!(virtualChildAt3 == null || virtualChildAt3.getVisibility() == 8 || ((LayoutParams) virtualChildAt3.getLayoutParams()).weight <= 0.0f)) {
                        virtualChildAt3.measure(View.MeasureSpec.makeMeasureSpec(i19, 1073741824), View.MeasureSpec.makeMeasureSpec(virtualChildAt3.getMeasuredHeight(), 1073741824));
                    }
                    i28 = i29 + 1;
                }
                i4 = max4;
            } else {
                i4 = max4;
            }
        } else {
            if (this.mWeightSum > 0.0f) {
                f4 = this.mWeightSum;
            }
            iArr[3] = -1;
            iArr[2] = -1;
            iArr[1] = -1;
            iArr[0] = -1;
            iArr2[3] = -1;
            iArr2[2] = -1;
            iArr2[1] = -1;
            iArr2[0] = -1;
            int i30 = -1;
            this.mTotalLength = 0;
            float f6 = f4;
            int i31 = 0;
            boolean z12 = z5;
            int i32 = i17;
            int i33 = i16;
            int i34 = i27;
            while (i31 < virtualChildCount) {
                View virtualChildAt4 = getVirtualChildAt(i31);
                if (virtualChildAt4 == null) {
                    f = f6;
                    i5 = i34;
                    i6 = i30;
                    i7 = i32;
                    z = z12;
                } else if (virtualChildAt4.getVisibility() != 8) {
                    LayoutParams layoutParams3 = (LayoutParams) virtualChildAt4.getLayoutParams();
                    float f7 = layoutParams3.weight;
                    if (f7 > 0.0f) {
                        int i35 = (int) ((((float) i34) * f7) / f6);
                        float f8 = f6 - f7;
                        i8 = i34 - i35;
                        int childMeasureSpec = getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom() + layoutParams3.topMargin + layoutParams3.bottomMargin, layoutParams3.height);
                        if (layoutParams3.width == 0 && mode == 1073741824) {
                            if (i35 <= 0) {
                                i35 = 0;
                            }
                            virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(i35, 1073741824), childMeasureSpec);
                        } else {
                            int measuredWidth2 = i35 + virtualChildAt4.getMeasuredWidth();
                            if (measuredWidth2 < 0) {
                                measuredWidth2 = 0;
                            }
                            virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth2, 1073741824), childMeasureSpec);
                        }
                        i9 = View.combineMeasuredStates(i33, virtualChildAt4.getMeasuredState() & ViewCompat.MEASURED_STATE_MASK);
                        f2 = f8;
                    } else {
                        i8 = i34;
                        i9 = i33;
                        f2 = f6;
                    }
                    if (!z10) {
                        int i36 = this.mTotalLength;
                        this.mTotalLength = Math.max(i36, virtualChildAt4.getMeasuredWidth() + i36 + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt4));
                    } else {
                        this.mTotalLength += virtualChildAt4.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt4);
                    }
                    boolean z13 = mode2 != 1073741824 && layoutParams3.height == -1;
                    int i37 = layoutParams3.bottomMargin + layoutParams3.topMargin;
                    int measuredHeight2 = virtualChildAt4.getMeasuredHeight() + i37;
                    int max5 = Math.max(i30, measuredHeight2);
                    i7 = Math.max(i32, !z13 ? measuredHeight2 : i37);
                    boolean z14 = z12 && layoutParams3.height == -1;
                    if (!z8) {
                        f = f2;
                        z = z14;
                        i5 = i8;
                        i33 = i9;
                        i6 = max5;
                    } else {
                        int baseline2 = virtualChildAt4.getBaseline();
                        if (baseline2 == -1) {
                            f = f2;
                            z = z14;
                            i5 = i8;
                            i33 = i9;
                            i6 = max5;
                        } else {
                            int i38 = ((((layoutParams3.gravity >= 0 ? layoutParams3.gravity : this.mGravity) & 112) >> 4) & -2) >> 1;
                            iArr[i38] = Math.max(iArr[i38], baseline2);
                            iArr2[i38] = Math.max(iArr2[i38], measuredHeight2 - baseline2);
                            f = f2;
                            z = z14;
                            i5 = i8;
                            i33 = i9;
                            i6 = max5;
                        }
                    }
                } else {
                    f = f6;
                    i5 = i34;
                    i6 = i30;
                    i7 = i32;
                    z = z12;
                }
                i31++;
                i32 = i7;
                i30 = i6;
                z12 = z;
                i34 = i5;
                f6 = f;
            }
            this.mTotalLength += getPaddingLeft() + getPaddingRight();
            if (!(iArr[1] == -1 && iArr[0] == -1 && iArr[2] == -1 && iArr[3] == -1)) {
                i30 = Math.max(i30, Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))) + Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))));
            }
            z5 = z12;
            max = i32;
            i16 = i33;
            i4 = i30;
        }
        if (!z5 && mode2 != 1073741824) {
            i4 = max;
        }
        setMeasuredDimension((-16777216 & i16) | resolveSizeAndState, View.resolveSizeAndState(Math.max(i4 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), i2, i16 << 16));
        if (z6) {
            forceUniformHeight(virtualChildCount, i);
        }
    }

    /* access modifiers changed from: package-private */
    public int measureNullChild(int i) {
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void measureVertical(int i, int i2) {
        int i3;
        int max;
        int i4;
        float f;
        int i5;
        int i6;
        int i7;
        int i8;
        boolean z;
        int i9;
        boolean z2;
        boolean z3;
        int i10;
        int i11;
        boolean z4;
        int max2;
        int i12;
        int i13;
        this.mTotalLength = 0;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        boolean z5 = true;
        float f2 = 0.0f;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        boolean z6 = false;
        boolean z7 = false;
        int i18 = this.mBaselineAlignedChildIndex;
        boolean z8 = this.mUseLargestChild;
        int i19 = Integer.MIN_VALUE;
        int i20 = 0;
        while (i20 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i20);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(i20);
                i9 = i19;
                z2 = z7;
                z3 = z5;
                i10 = i15;
                i11 = i14;
            } else if (virtualChildAt.getVisibility() != 8) {
                if (hasDividerBeforeChildAt(i20)) {
                    this.mTotalLength += this.mDividerHeight;
                }
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                float f3 = f2 + layoutParams.weight;
                if (mode2 == 1073741824 && layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                    int i21 = this.mTotalLength;
                    this.mTotalLength = Math.max(i21, layoutParams.topMargin + i21 + layoutParams.bottomMargin);
                    z7 = true;
                } else {
                    if (layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                        layoutParams.height = -2;
                        i13 = 0;
                    } else {
                        i13 = Integer.MIN_VALUE;
                    }
                    measureChildBeforeLayout(virtualChildAt, i20, i, 0, i2, f3 == 0.0f ? this.mTotalLength : 0);
                    if (i13 != Integer.MIN_VALUE) {
                        layoutParams.height = i13;
                    }
                    int measuredHeight = virtualChildAt.getMeasuredHeight();
                    int i22 = this.mTotalLength;
                    this.mTotalLength = Math.max(i22, i22 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + getNextLocationOffset(virtualChildAt));
                    if (z8) {
                        i19 = Math.max(measuredHeight, i19);
                    }
                }
                if (i18 >= 0 && i18 == i20 + 1) {
                    this.mBaselineChildTop = this.mTotalLength;
                }
                if (i20 < i18 && layoutParams.weight > 0.0f) {
                    throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                }
                boolean z9 = false;
                if (mode != 1073741824 && layoutParams.width == -1) {
                    z4 = true;
                    z9 = true;
                } else {
                    z4 = z6;
                }
                int i23 = layoutParams.leftMargin + layoutParams.rightMargin;
                int measuredWidth = virtualChildAt.getMeasuredWidth() + i23;
                int max3 = Math.max(i14, measuredWidth);
                int combineMeasuredStates = View.combineMeasuredStates(i15, virtualChildAt.getMeasuredState());
                z3 = z5 && layoutParams.width == -1;
                if (layoutParams.weight > 0.0f) {
                    i12 = Math.max(i17, !z9 ? measuredWidth : i23);
                    max2 = i16;
                } else {
                    if (z9) {
                        measuredWidth = i23;
                    }
                    max2 = Math.max(i16, measuredWidth);
                    i12 = i17;
                }
                i20 += getChildrenSkipCount(virtualChildAt, i20);
                z2 = z7;
                i17 = i12;
                i16 = max2;
                i11 = max3;
                i9 = i19;
                i10 = combineMeasuredStates;
                z6 = z4;
                f2 = f3;
            } else {
                i20 += getChildrenSkipCount(virtualChildAt, i20);
                i9 = i19;
                z2 = z7;
                z3 = z5;
                i10 = i15;
                i11 = i14;
            }
            i20++;
            i19 = i9;
            z7 = z2;
            z5 = z3;
            i15 = i10;
            i14 = i11;
        }
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerHeight;
        }
        if (z8 && (mode2 == Integer.MIN_VALUE || mode2 == 0)) {
            this.mTotalLength = 0;
            int i24 = 0;
            while (i24 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(i24);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(i24);
                    i3 = i24;
                } else if (virtualChildAt2.getVisibility() != 8) {
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    int i25 = this.mTotalLength;
                    this.mTotalLength = Math.max(i25, layoutParams2.bottomMargin + i25 + i19 + layoutParams2.topMargin + getNextLocationOffset(virtualChildAt2));
                    i3 = i24;
                } else {
                    i3 = getChildrenSkipCount(virtualChildAt2, i24) + i24;
                }
                i24 = i3 + 1;
            }
        }
        this.mTotalLength += getPaddingTop() + getPaddingBottom();
        int resolveSizeAndState = View.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), i2, 0);
        int i26 = (16777215 & resolveSizeAndState) - this.mTotalLength;
        if (!z7 && (i26 == 0 || f2 <= 0.0f)) {
            max = Math.max(i16, i17);
            if (z8 && mode2 != 1073741824) {
                int i27 = 0;
                while (true) {
                    int i28 = i27;
                    if (i28 >= virtualChildCount) {
                        break;
                    }
                    View virtualChildAt3 = getVirtualChildAt(i28);
                    if (!(virtualChildAt3 == null || virtualChildAt3.getVisibility() == 8 || ((LayoutParams) virtualChildAt3.getLayoutParams()).weight <= 0.0f)) {
                        virtualChildAt3.measure(View.MeasureSpec.makeMeasureSpec(virtualChildAt3.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i19, 1073741824));
                    }
                    i27 = i28 + 1;
                }
                i4 = i14;
            } else {
                i4 = i14;
            }
        } else {
            if (this.mWeightSum > 0.0f) {
                f2 = this.mWeightSum;
            }
            this.mTotalLength = 0;
            int i29 = 0;
            boolean z10 = z5;
            int i30 = i16;
            int i31 = i15;
            int i32 = i14;
            int i33 = i26;
            float f4 = f2;
            int i34 = i33;
            while (i29 < virtualChildCount) {
                View virtualChildAt4 = getVirtualChildAt(i29);
                if (virtualChildAt4.getVisibility() != 8) {
                    LayoutParams layoutParams3 = (LayoutParams) virtualChildAt4.getLayoutParams();
                    float f5 = layoutParams3.weight;
                    if (f5 > 0.0f) {
                        int i35 = (int) ((((float) i34) * f5) / f4);
                        float f6 = f4 - f5;
                        int i36 = i34 - i35;
                        int childMeasureSpec = getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + layoutParams3.leftMargin + layoutParams3.rightMargin, layoutParams3.width);
                        if (layoutParams3.height == 0 && mode2 == 1073741824) {
                            if (i35 <= 0) {
                                i35 = 0;
                            }
                            virtualChildAt4.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(i35, 1073741824));
                        } else {
                            int measuredHeight2 = i35 + virtualChildAt4.getMeasuredHeight();
                            if (measuredHeight2 < 0) {
                                measuredHeight2 = 0;
                            }
                            virtualChildAt4.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(measuredHeight2, 1073741824));
                        }
                        float f7 = f6;
                        i7 = i36;
                        i8 = View.combineMeasuredStates(i31, virtualChildAt4.getMeasuredState() & InputDeviceCompat.SOURCE_ANY);
                        f = f7;
                    } else {
                        f = f4;
                        i7 = i34;
                        i8 = i31;
                    }
                    int i37 = layoutParams3.leftMargin + layoutParams3.rightMargin;
                    int measuredWidth2 = virtualChildAt4.getMeasuredWidth() + i37;
                    int max4 = Math.max(i32, measuredWidth2);
                    int max5 = Math.max(i30, !(mode != 1073741824 && layoutParams3.width == -1) ? measuredWidth2 : i37);
                    z = z10 && layoutParams3.width == -1;
                    int i38 = this.mTotalLength;
                    this.mTotalLength = Math.max(i38, layoutParams3.bottomMargin + virtualChildAt4.getMeasuredHeight() + i38 + layoutParams3.topMargin + getNextLocationOffset(virtualChildAt4));
                    i5 = max5;
                    i6 = max4;
                } else {
                    f = f4;
                    i5 = i30;
                    i6 = i32;
                    i7 = i34;
                    i8 = i31;
                    z = z10;
                }
                i29++;
                z10 = z;
                i30 = i5;
                i32 = i6;
                i31 = i8;
                i34 = i7;
                f4 = f;
            }
            this.mTotalLength += getPaddingTop() + getPaddingBottom();
            z5 = z10;
            max = i30;
            i15 = i31;
            i4 = i32;
        }
        if (!z5 && mode != 1073741824) {
            i4 = max;
        }
        setMeasuredDimension(View.resolveSizeAndState(Math.max(i4 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i, i15), resolveSizeAndState);
        if (z6) {
            forceUniformWidth(virtualChildCount, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation != 1) {
            drawDividersHorizontal(canvas);
        } else {
            drawDividersVertical(canvas);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOrientation != 1) {
            layoutHorizontal(i, i2, i3, i4);
        } else {
            layoutVertical(i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mOrientation != 1) {
            measureHorizontal(i, i2);
        } else {
            measureVertical(i, i2);
        }
    }

    public void setBaselineAligned(boolean z) {
        this.mBaselineAligned = z;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i >= 0 && i < getChildCount()) {
            this.mBaselineAlignedChildIndex = i;
            return;
        }
        throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
    }

    public void setDividerDrawable(Drawable drawable) {
        boolean z = false;
        if (drawable != this.mDivider) {
            this.mDivider = drawable;
            if (drawable == null) {
                this.mDividerWidth = 0;
                this.mDividerHeight = 0;
            } else {
                this.mDividerWidth = drawable.getIntrinsicWidth();
                this.mDividerHeight = drawable.getIntrinsicHeight();
            }
            if (drawable == null) {
                z = true;
            }
            setWillNotDraw(z);
            requestLayout();
        }
    }

    public void setDividerPadding(int i) {
        this.mDividerPadding = i;
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            int i2 = (8388615 & i) != 0 ? i : 8388611 | i;
            if ((i2 & 112) == 0) {
                i2 |= 48;
            }
            this.mGravity = i2;
            requestLayout();
        }
    }

    public void setHorizontalGravity(int i) {
        int i2 = i & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) != i2) {
            this.mGravity = i2 | (this.mGravity & -8388616);
            requestLayout();
        }
    }

    public void setMeasureWithLargestChildEnabled(boolean z) {
        this.mUseLargestChild = z;
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            requestLayout();
        }
    }

    public void setShowDividers(int i) {
        if (i != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = i;
    }

    public void setVerticalGravity(int i) {
        int i2 = i & 112;
        if ((this.mGravity & 112) != i2) {
            this.mGravity = i2 | (this.mGravity & -113);
            requestLayout();
        }
    }

    public void setWeightSum(float f) {
        this.mWeightSum = Math.max(0.0f, f);
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
