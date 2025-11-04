// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.content.res.TypedArray;
import android.view.ViewGroup$MarginLayoutParams;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Build$VERSION;
import android.view.accessibility.AccessibilityEvent;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.annotation.RestrictTo;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.view.View$MeasureSpec;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

public class LinearLayoutCompat extends ViewGroup
{
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
    
    public LinearLayoutCompat(final Context context) {
        this(context, null);
    }
    
    public LinearLayoutCompat(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public LinearLayoutCompat(final Context context, final AttributeSet set, int n) {
        super(context, set, n);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, set, R.styleable.LinearLayoutCompat, n, 0);
        n = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (n >= 0) {
            this.setOrientation(n);
        }
        n = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (n >= 0) {
            this.setGravity(n);
        }
        final boolean boolean1 = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!boolean1) {
            this.setBaselineAligned(boolean1);
        }
        this.mWeightSum = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        this.setDividerDrawable(obtainStyledAttributes.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        obtainStyledAttributes.recycle();
    }
    
    private void forceUniformHeight(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), 1073741824);
        for (int i = 0; i < n; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                if (layoutParams.height == -1) {
                    final int width = layoutParams.width;
                    layoutParams.width = virtualChild.getMeasuredWidth();
                    this.measureChildWithMargins(virtualChild, n2, 0, measureSpec, 0);
                    layoutParams.width = width;
                }
            }
        }
    }
    
    private void forceUniformWidth(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 1073741824);
        for (int i = 0; i < n; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                if (layoutParams.width == -1) {
                    final int height = layoutParams.height;
                    layoutParams.height = virtualChild.getMeasuredHeight();
                    this.measureChildWithMargins(virtualChild, measureSpec, 0, n2, 0);
                    layoutParams.height = height;
                }
            }
        }
    }
    
    private void setChildFrame(final View view, final int n, final int n2, final int n3, final int n4) {
        view.layout(n, n2, n + n3, n2 + n4);
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    void drawDividersHorizontal(final Canvas canvas) {
        final int virtualChildCount = this.getVirtualChildCount();
        final boolean layoutRtl = ViewUtils.isLayoutRtl((View)this);
        for (int i = 0; i < virtualChildCount; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null && virtualChild.getVisibility() != 8 && this.hasDividerBeforeChildAt(i)) {
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                int n;
                if (!layoutRtl) {
                    n = virtualChild.getLeft() - layoutParams.leftMargin - this.mDividerWidth;
                }
                else {
                    n = layoutParams.rightMargin + virtualChild.getRight();
                }
                this.drawVerticalDivider(canvas, n);
            }
        }
        if (this.hasDividerBeforeChildAt(virtualChildCount)) {
            final View virtualChild2 = this.getVirtualChildAt(virtualChildCount - 1);
            int paddingLeft;
            if (virtualChild2 != null) {
                final LayoutParams layoutParams2 = (LayoutParams)virtualChild2.getLayoutParams();
                if (!layoutRtl) {
                    paddingLeft = layoutParams2.rightMargin + virtualChild2.getRight();
                }
                else {
                    paddingLeft = virtualChild2.getLeft() - layoutParams2.leftMargin - this.mDividerWidth;
                }
            }
            else if (!layoutRtl) {
                paddingLeft = this.getWidth() - this.getPaddingRight() - this.mDividerWidth;
            }
            else {
                paddingLeft = this.getPaddingLeft();
            }
            this.drawVerticalDivider(canvas, paddingLeft);
        }
    }
    
    void drawDividersVertical(final Canvas canvas) {
        final int virtualChildCount = this.getVirtualChildCount();
        for (int i = 0; i < virtualChildCount; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null && virtualChild.getVisibility() != 8 && this.hasDividerBeforeChildAt(i)) {
                this.drawHorizontalDivider(canvas, virtualChild.getTop() - ((LayoutParams)virtualChild.getLayoutParams()).topMargin - this.mDividerHeight);
            }
        }
        if (this.hasDividerBeforeChildAt(virtualChildCount)) {
            final View virtualChild2 = this.getVirtualChildAt(virtualChildCount - 1);
            int n;
            if (virtualChild2 != null) {
                n = ((LayoutParams)virtualChild2.getLayoutParams()).bottomMargin + virtualChild2.getBottom();
            }
            else {
                n = this.getHeight() - this.getPaddingBottom() - this.mDividerHeight;
            }
            this.drawHorizontalDivider(canvas, n);
        }
    }
    
    void drawHorizontalDivider(final Canvas canvas, final int n) {
        this.mDivider.setBounds(this.getPaddingLeft() + this.mDividerPadding, n, this.getWidth() - this.getPaddingRight() - this.mDividerPadding, this.mDividerHeight + n);
        this.mDivider.draw(canvas);
    }
    
    void drawVerticalDivider(final Canvas canvas, final int n) {
        this.mDivider.setBounds(n, this.getPaddingTop() + this.mDividerPadding, this.mDividerWidth + n, this.getHeight() - this.getPaddingBottom() - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (this.mOrientation != 1) {
            return null;
        }
        return new LayoutParams(-1, -2);
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    public int getBaseline() {
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (this.getChildCount() <= this.mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        final View child = this.getChildAt(this.mBaselineAlignedChildIndex);
        final int baseline = child.getBaseline();
        if (baseline != -1) {
            int mBaselineChildTop = this.mBaselineChildTop;
            if (this.mOrientation == 1) {
                final int n = this.mGravity & 0x70;
                if (n != 48) {
                    switch (n) {
                        case 80: {
                            mBaselineChildTop = this.getBottom() - this.getTop() - this.getPaddingBottom() - this.mTotalLength;
                            break;
                        }
                        case 16: {
                            mBaselineChildTop += (this.getBottom() - this.getTop() - this.getPaddingTop() - this.getPaddingBottom() - this.mTotalLength) / 2;
                            break;
                        }
                    }
                }
            }
            return ((LayoutParams)child.getLayoutParams()).topMargin + mBaselineChildTop + baseline;
        }
        if (this.mBaselineAlignedChildIndex != 0) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
        return -1;
    }
    
    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }
    
    int getChildrenSkipCount(final View view, final int n) {
        return 0;
    }
    
    public Drawable getDividerDrawable() {
        return this.mDivider;
    }
    
    public int getDividerPadding() {
        return this.mDividerPadding;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public int getDividerWidth() {
        return this.mDividerWidth;
    }
    
    public int getGravity() {
        return this.mGravity;
    }
    
    int getLocationOffset(final View view) {
        return 0;
    }
    
    int getNextLocationOffset(final View view) {
        return 0;
    }
    
    public int getOrientation() {
        return this.mOrientation;
    }
    
    public int getShowDividers() {
        return this.mShowDividers;
    }
    
    View getVirtualChildAt(final int n) {
        return this.getChildAt(n);
    }
    
    int getVirtualChildCount() {
        return this.getChildCount();
    }
    
    public float getWeightSum() {
        return this.mWeightSum;
    }
    
    protected boolean hasDividerBeforeChildAt(int i) {
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = false;
        if (i == 0) {
            if ((this.mShowDividers & 0x1) != 0x0) {
                b3 = true;
            }
            return b3;
        }
        if (i == this.getChildCount()) {
            return (this.mShowDividers & 0x4) != 0x0 || b;
        }
        if ((this.mShowDividers & 0x2) == 0x0) {
            return false;
        }
        --i;
        while (i >= 0) {
            if (this.getChildAt(i).getVisibility() != 8) {
                return true;
            }
            --i;
        }
        return b2;
    }
    
    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }
    
    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }
    
    void layoutHorizontal(int n, int i, int n2, int n3) {
        final boolean layoutRtl = ViewUtils.isLayoutRtl((View)this);
        final int paddingTop = this.getPaddingTop();
        final int n4 = n3 - i;
        final int paddingBottom = this.getPaddingBottom();
        final int paddingBottom2 = this.getPaddingBottom();
        final int virtualChildCount = this.getVirtualChildCount();
        i = this.mGravity;
        final int mGravity = this.mGravity;
        final boolean mBaselineAligned = this.mBaselineAligned;
        final int[] mMaxAscent = this.mMaxAscent;
        final int[] mMaxDescent = this.mMaxDescent;
        switch (GravityCompat.getAbsoluteGravity(i & 0x800007, ViewCompat.getLayoutDirection((View)this))) {
            default: {
                n = this.getPaddingLeft();
                break;
            }
            case 5: {
                n = this.getPaddingLeft() + n2 - n - this.mTotalLength;
                break;
            }
            case 1: {
                n = this.getPaddingLeft() + (n2 - n - this.mTotalLength) / 2;
                break;
            }
        }
        int n5;
        if (!layoutRtl) {
            n3 = 1;
            n5 = 0;
        }
        else {
            n3 = -1;
            n5 = virtualChildCount - 1;
        }
        i = 0;
        n2 = n;
        while (i < virtualChildCount) {
            final int n6 = n5 + n3 * i;
            final View virtualChild = this.getVirtualChildAt(n6);
            if (virtualChild != null) {
                if (virtualChild.getVisibility() == 8) {
                    n = i;
                }
                else {
                    final int measuredWidth = virtualChild.getMeasuredWidth();
                    final int measuredHeight = virtualChild.getMeasuredHeight();
                    final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                    int baseline;
                    if (mBaselineAligned && layoutParams.height != -1) {
                        baseline = virtualChild.getBaseline();
                    }
                    else {
                        baseline = -1;
                    }
                    n = layoutParams.gravity;
                    if (n < 0) {
                        n = (mGravity & 0x70);
                    }
                    switch (n & 0x70) {
                        default: {
                            n = paddingTop;
                            break;
                        }
                        case 48: {
                            final int n7 = n = paddingTop + layoutParams.topMargin;
                            if (baseline != -1) {
                                n = n7 + (mMaxAscent[1] - baseline);
                                break;
                            }
                            break;
                        }
                        case 16: {
                            n = (n4 - paddingTop - paddingBottom2 - measuredHeight) / 2 + paddingTop + layoutParams.topMargin - layoutParams.bottomMargin;
                            break;
                        }
                        case 80: {
                            final int n8 = n = n4 - paddingBottom - measuredHeight - layoutParams.bottomMargin;
                            if (baseline != -1) {
                                n = virtualChild.getMeasuredHeight();
                                n = n8 - (mMaxDescent[2] - (n - baseline));
                                break;
                            }
                            break;
                        }
                    }
                    if (this.hasDividerBeforeChildAt(n6)) {
                        n2 += this.mDividerWidth;
                    }
                    n2 += layoutParams.leftMargin;
                    this.setChildFrame(virtualChild, n2 + this.getLocationOffset(virtualChild), n, measuredWidth, measuredHeight);
                    n2 += layoutParams.rightMargin + measuredWidth + this.getNextLocationOffset(virtualChild);
                    n = this.getChildrenSkipCount(virtualChild, n6) + i;
                }
            }
            else {
                n2 += this.measureNullChild(n6);
                n = i;
            }
            i = n + 1;
        }
    }
    
    void layoutVertical(int i, int n, int gravity, int measuredHeight) {
        final int paddingLeft = this.getPaddingLeft();
        final int n2 = gravity - i;
        final int paddingRight = this.getPaddingRight();
        final int paddingRight2 = this.getPaddingRight();
        final int virtualChildCount = this.getVirtualChildCount();
        i = this.mGravity;
        final int mGravity = this.mGravity;
        switch (i & 0x70) {
            default: {
                i = this.getPaddingTop();
                break;
            }
            case 80: {
                i = this.getPaddingTop() + measuredHeight - n - this.mTotalLength;
                break;
            }
            case 16: {
                i = this.getPaddingTop() + (measuredHeight - n - this.mTotalLength) / 2;
                break;
            }
        }
        gravity = 0;
        n = i;
        View virtualChild;
        int measuredWidth;
        LayoutParams layoutParams;
        for (i = gravity; i < virtualChildCount; ++i) {
            virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null) {
                if (virtualChild.getVisibility() != 8) {
                    measuredWidth = virtualChild.getMeasuredWidth();
                    measuredHeight = virtualChild.getMeasuredHeight();
                    layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                    gravity = layoutParams.gravity;
                    if (gravity < 0) {
                        gravity = (mGravity & 0x800007);
                    }
                    switch (GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection((View)this)) & 0x7) {
                        default: {
                            gravity = paddingLeft + layoutParams.leftMargin;
                            break;
                        }
                        case 1: {
                            gravity = (n2 - paddingLeft - paddingRight2 - measuredWidth) / 2 + paddingLeft + layoutParams.leftMargin - layoutParams.rightMargin;
                            break;
                        }
                        case 5: {
                            gravity = n2 - paddingRight - measuredWidth - layoutParams.rightMargin;
                            break;
                        }
                    }
                    if (this.hasDividerBeforeChildAt(i)) {
                        n += this.mDividerHeight;
                    }
                    n += layoutParams.topMargin;
                    this.setChildFrame(virtualChild, gravity, n + this.getLocationOffset(virtualChild), measuredWidth, measuredHeight);
                    n += layoutParams.bottomMargin + measuredHeight + this.getNextLocationOffset(virtualChild);
                    i += this.getChildrenSkipCount(virtualChild, i);
                }
            }
            else {
                n += this.measureNullChild(i);
            }
        }
    }
    
    void measureChildBeforeLayout(final View view, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.measureChildWithMargins(view, n2, n3, n4, n5);
    }
    
    void measureHorizontal(final int n, final int n2) {
        this.mTotalLength = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 1;
        float mWeightSum = 0.0f;
        final int virtualChildCount = this.getVirtualChildCount();
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        int n8 = 0;
        boolean b = false;
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        final int[] mMaxAscent = this.mMaxAscent;
        final int[] mMaxDescent = this.mMaxDescent;
        mMaxAscent[2] = (mMaxAscent[3] = -1);
        mMaxAscent[0] = (mMaxAscent[1] = -1);
        mMaxDescent[2] = (mMaxDescent[3] = -1);
        mMaxDescent[0] = (mMaxDescent[1] = -1);
        final boolean mBaselineAligned = this.mBaselineAligned;
        final boolean mUseLargestChild = this.mUseLargestChild;
        final boolean b2 = mode == 1073741824;
        int max = Integer.MIN_VALUE;
        int n12;
        int n13;
        int n14;
        int n17;
        int n18;
        for (int i = 0; i < virtualChildCount; ++i, n17 = n13, n18 = n14, n7 = n12, n4 = n17, n3 = n18) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null) {
                if (virtualChild.getVisibility() != 8) {
                    if (this.hasDividerBeforeChildAt(i)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                    mWeightSum += layoutParams.weight;
                    if (mode == 1073741824 && layoutParams.width == 0 && layoutParams.weight > 0.0f) {
                        if (!b2) {
                            final int mTotalLength = this.mTotalLength;
                            this.mTotalLength = Math.max(mTotalLength, layoutParams.leftMargin + mTotalLength + layoutParams.rightMargin);
                        }
                        else {
                            this.mTotalLength += layoutParams.leftMargin + layoutParams.rightMargin;
                        }
                        if (!mBaselineAligned) {
                            b = true;
                        }
                        else {
                            final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
                            virtualChild.measure(measureSpec, measureSpec);
                        }
                    }
                    else {
                        int width;
                        if (layoutParams.width == 0 && layoutParams.weight > 0.0f) {
                            layoutParams.width = -2;
                            width = 0;
                        }
                        else {
                            width = Integer.MIN_VALUE;
                        }
                        int mTotalLength2;
                        if (mWeightSum == 0.0f) {
                            mTotalLength2 = this.mTotalLength;
                        }
                        else {
                            mTotalLength2 = 0;
                        }
                        this.measureChildBeforeLayout(virtualChild, i, n, mTotalLength2, n2, 0);
                        if (width != Integer.MIN_VALUE) {
                            layoutParams.width = width;
                        }
                        final int measuredWidth = virtualChild.getMeasuredWidth();
                        if (!b2) {
                            final int mTotalLength3 = this.mTotalLength;
                            this.mTotalLength = Math.max(mTotalLength3, mTotalLength3 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + this.getNextLocationOffset(virtualChild));
                        }
                        else {
                            this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + this.getNextLocationOffset(virtualChild);
                        }
                        if (mUseLargestChild) {
                            max = Math.max(measuredWidth, max);
                        }
                    }
                    boolean b3 = false;
                    if (mode2 != 1073741824 && layoutParams.height == -1) {
                        n8 = 1;
                        b3 = true;
                    }
                    final int n9 = layoutParams.topMargin + layoutParams.bottomMargin;
                    int b4 = virtualChild.getMeasuredHeight() + n9;
                    final int combineMeasuredStates = View.combineMeasuredStates(n4, virtualChild.getMeasuredState());
                    if (mBaselineAligned) {
                        final int baseline = virtualChild.getBaseline();
                        if (baseline != -1) {
                            int n10;
                            if (layoutParams.gravity >= 0) {
                                n10 = layoutParams.gravity;
                            }
                            else {
                                n10 = this.mGravity;
                            }
                            final int n11 = ((n10 & 0x70) >> 4 & 0xFFFFFFFE) >> 1;
                            mMaxAscent[n11] = Math.max(mMaxAscent[n11], baseline);
                            mMaxDescent[n11] = Math.max(mMaxDescent[n11], b4 - baseline);
                        }
                    }
                    final int max2 = Math.max(n3, b4);
                    if (n7 != 0 && layoutParams.height == -1) {
                        n12 = 1;
                    }
                    else {
                        n12 = 0;
                    }
                    int max3;
                    int max4;
                    if (layoutParams.weight > 0.0f) {
                        if (b3) {
                            b4 = n9;
                        }
                        max3 = Math.max(n6, b4);
                        max4 = n5;
                    }
                    else {
                        if (b3) {
                            b4 = n9;
                        }
                        max4 = Math.max(n5, b4);
                        max3 = n6;
                    }
                    i += this.getChildrenSkipCount(virtualChild, i);
                    n6 = max3;
                    n5 = max4;
                    n13 = combineMeasuredStates;
                    n14 = max2;
                }
                else {
                    i += this.getChildrenSkipCount(virtualChild, i);
                    final int n15 = n7;
                    n13 = n4;
                    n14 = n3;
                    n12 = n15;
                }
            }
            else {
                this.mTotalLength += this.measureNullChild(i);
                final int n16 = n7;
                n13 = n4;
                n14 = n3;
                n12 = n16;
            }
        }
        if (this.mTotalLength > 0 && this.hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        int max5;
        if (mMaxAscent[1] == -1 && mMaxAscent[0] == -1 && mMaxAscent[2] == -1 && mMaxAscent[3] == -1) {
            max5 = n3;
        }
        else {
            max5 = Math.max(n3, Math.max(mMaxAscent[3], Math.max(mMaxAscent[0], Math.max(mMaxAscent[1], mMaxAscent[2]))) + Math.max(mMaxDescent[3], Math.max(mMaxDescent[0], Math.max(mMaxDescent[1], mMaxDescent[2]))));
        }
        if (mUseLargestChild) {
            if (mode == Integer.MIN_VALUE || mode == 0) {
                this.mTotalLength = 0;
                for (int j = 0; j < virtualChildCount; ++j) {
                    final View virtualChild2 = this.getVirtualChildAt(j);
                    if (virtualChild2 != null) {
                        if (virtualChild2.getVisibility() != 8) {
                            final LayoutParams layoutParams2 = (LayoutParams)virtualChild2.getLayoutParams();
                            if (!b2) {
                                final int mTotalLength4 = this.mTotalLength;
                                this.mTotalLength = Math.max(mTotalLength4, layoutParams2.rightMargin + (mTotalLength4 + max + layoutParams2.leftMargin) + this.getNextLocationOffset(virtualChild2));
                            }
                            else {
                                this.mTotalLength += layoutParams2.rightMargin + (layoutParams2.leftMargin + max) + this.getNextLocationOffset(virtualChild2);
                            }
                        }
                        else {
                            j += this.getChildrenSkipCount(virtualChild2, j);
                        }
                    }
                    else {
                        this.mTotalLength += this.measureNullChild(j);
                    }
                }
            }
        }
        this.mTotalLength += this.getPaddingLeft() + this.getPaddingRight();
        final int resolveSizeAndState = View.resolveSizeAndState(Math.max(this.mTotalLength, this.getSuggestedMinimumWidth()), n, 0);
        final int n19 = (0xFFFFFF & resolveSizeAndState) - this.mTotalLength;
        int n20;
        int n21;
        if (!b && (n19 == 0 || mWeightSum <= 0.0f)) {
            final int max6 = Math.max(n5, n6);
            if (mUseLargestChild && mode != 1073741824) {
                for (int k = 0; k < virtualChildCount; ++k) {
                    final View virtualChild3 = this.getVirtualChildAt(k);
                    if (virtualChild3 != null && virtualChild3.getVisibility() != 8 && ((LayoutParams)virtualChild3.getLayoutParams()).weight > 0.0f) {
                        virtualChild3.measure(View$MeasureSpec.makeMeasureSpec(max, 1073741824), View$MeasureSpec.makeMeasureSpec(virtualChild3.getMeasuredHeight(), 1073741824));
                    }
                }
                n20 = max5;
                n21 = max6;
            }
            else {
                n20 = max5;
                n21 = max6;
            }
        }
        else {
            if (this.mWeightSum > 0.0f) {
                mWeightSum = this.mWeightSum;
            }
            mMaxAscent[2] = (mMaxAscent[3] = -1);
            mMaxAscent[0] = (mMaxAscent[1] = -1);
            mMaxDescent[2] = (mMaxDescent[3] = -1);
            mMaxDescent[0] = (mMaxDescent[1] = -1);
            int max7 = -1;
            this.mTotalLength = 0;
            final int n22 = 0;
            final int n23 = n5;
            int n24 = n4;
            int n25 = n19;
            int l = n22;
            int a = n23;
            while (l < virtualChildCount) {
                final View virtualChild4 = this.getVirtualChildAt(l);
                int n27;
                int n28;
                int n29;
                int n30;
                if (virtualChild4 == null) {
                    final int n26 = n25;
                    n27 = max7;
                    n28 = a;
                    n29 = n7;
                    n30 = n26;
                }
                else if (virtualChild4.getVisibility() != 8) {
                    final LayoutParams layoutParams3 = (LayoutParams)virtualChild4.getLayoutParams();
                    final float weight = layoutParams3.weight;
                    int combineMeasuredStates2;
                    int n35;
                    if (weight > 0.0f) {
                        final int n31 = (int)(n25 * weight / mWeightSum);
                        final int n32 = n25 - n31;
                        final int childMeasureSpec = getChildMeasureSpec(n2, this.getPaddingTop() + this.getPaddingBottom() + layoutParams3.topMargin + layoutParams3.bottomMargin, layoutParams3.height);
                        if (layoutParams3.width == 0 && mode == 1073741824) {
                            int n33;
                            if ((n33 = n31) <= 0) {
                                n33 = 0;
                            }
                            virtualChild4.measure(View$MeasureSpec.makeMeasureSpec(n33, 1073741824), childMeasureSpec);
                        }
                        else {
                            int n34 = n31 + virtualChild4.getMeasuredWidth();
                            if (n34 < 0) {
                                n34 = 0;
                            }
                            virtualChild4.measure(View$MeasureSpec.makeMeasureSpec(n34, 1073741824), childMeasureSpec);
                        }
                        combineMeasuredStates2 = View.combineMeasuredStates(n24, virtualChild4.getMeasuredState() & 0xFF000000);
                        mWeightSum -= weight;
                        n35 = n32;
                    }
                    else {
                        final int n36 = n24;
                        n35 = n25;
                        combineMeasuredStates2 = n36;
                    }
                    if (!b2) {
                        final int mTotalLength5 = this.mTotalLength;
                        this.mTotalLength = Math.max(mTotalLength5, virtualChild4.getMeasuredWidth() + mTotalLength5 + layoutParams3.leftMargin + layoutParams3.rightMargin + this.getNextLocationOffset(virtualChild4));
                    }
                    else {
                        this.mTotalLength += virtualChild4.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin + this.getNextLocationOffset(virtualChild4);
                    }
                    int n37;
                    if (mode2 != 1073741824 && layoutParams3.height == -1) {
                        n37 = 1;
                    }
                    else {
                        n37 = 0;
                    }
                    final int n38 = layoutParams3.bottomMargin + layoutParams3.topMargin;
                    final int b5 = virtualChild4.getMeasuredHeight() + n38;
                    final int max8 = Math.max(max7, b5);
                    int b6;
                    if (n37 == 0) {
                        b6 = b5;
                    }
                    else {
                        b6 = n38;
                    }
                    final int max9 = Math.max(a, b6);
                    int n39;
                    if (n7 != 0 && layoutParams3.height == -1) {
                        n39 = 1;
                    }
                    else {
                        n39 = 0;
                    }
                    if (!mBaselineAligned) {
                        n29 = n39;
                        n30 = n35;
                        n24 = combineMeasuredStates2;
                        n27 = max8;
                        n28 = max9;
                    }
                    else {
                        final int baseline2 = virtualChild4.getBaseline();
                        if (baseline2 == -1) {
                            n29 = n39;
                            n30 = n35;
                            n24 = combineMeasuredStates2;
                            n27 = max8;
                            n28 = max9;
                        }
                        else {
                            int n40;
                            if (layoutParams3.gravity >= 0) {
                                n40 = layoutParams3.gravity;
                            }
                            else {
                                n40 = this.mGravity;
                            }
                            final int n41 = ((n40 & 0x70) >> 4 & 0xFFFFFFFE) >> 1;
                            mMaxAscent[n41] = Math.max(mMaxAscent[n41], baseline2);
                            mMaxDescent[n41] = Math.max(mMaxDescent[n41], b5 - baseline2);
                            n29 = n39;
                            n30 = n35;
                            n24 = combineMeasuredStates2;
                            n27 = max8;
                            n28 = max9;
                        }
                    }
                }
                else {
                    final int n42 = n25;
                    n27 = max7;
                    n28 = a;
                    n29 = n7;
                    n30 = n42;
                }
                final int n43 = l + 1;
                final int n44 = n27;
                n25 = n30;
                n7 = n29;
                a = n28;
                max7 = n44;
                l = n43;
            }
            this.mTotalLength += this.getPaddingLeft() + this.getPaddingRight();
            if (mMaxAscent[1] != -1 || mMaxAscent[0] != -1 || mMaxAscent[2] != -1 || mMaxAscent[3] != -1) {
                max7 = Math.max(max7, Math.max(mMaxAscent[3], Math.max(mMaxAscent[0], Math.max(mMaxAscent[1], mMaxAscent[2]))) + Math.max(mMaxDescent[3], Math.max(mMaxDescent[0], Math.max(mMaxDescent[1], mMaxDescent[2]))));
            }
            final int n45 = a;
            n4 = n24;
            final int n46 = max7;
            n21 = n45;
            n20 = n46;
        }
        if (n7 == 0 && mode2 != 1073741824) {
            n20 = n21;
        }
        this.setMeasuredDimension((0xFF000000 & n4) | resolveSizeAndState, View.resolveSizeAndState(Math.max(n20 + (this.getPaddingTop() + this.getPaddingBottom()), this.getSuggestedMinimumHeight()), n2, n4 << 16));
        if (n8 != 0) {
            this.forceUniformHeight(virtualChildCount, n);
        }
    }
    
    int measureNullChild(final int n) {
        return 0;
    }
    
    void measureVertical(final int n, final int n2) {
        this.mTotalLength = 0;
        int a = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 1;
        float mWeightSum = 0.0f;
        final int virtualChildCount = this.getVirtualChildCount();
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        int n7 = 0;
        int n8 = 0;
        final int mBaselineAlignedChildIndex = this.mBaselineAlignedChildIndex;
        final boolean mUseLargestChild = this.mUseLargestChild;
        int max = Integer.MIN_VALUE;
        int n16;
        int n18;
        int n19;
        int n20;
        int n26;
        int n27;
        for (int i = 0; i < virtualChildCount; ++i, n26 = n16, n3 = n19, n27 = n18, max = n26, n8 = n20, a = n27) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null) {
                if (virtualChild.getVisibility() != 8) {
                    if (this.hasDividerBeforeChildAt(i)) {
                        this.mTotalLength += this.mDividerHeight;
                    }
                    final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                    mWeightSum += layoutParams.weight;
                    int n9;
                    if (mode2 == 1073741824 && layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                        final int mTotalLength = this.mTotalLength;
                        this.mTotalLength = Math.max(mTotalLength, layoutParams.topMargin + mTotalLength + layoutParams.bottomMargin);
                        n9 = 1;
                    }
                    else {
                        int height;
                        if (layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                            layoutParams.height = -2;
                            height = 0;
                        }
                        else {
                            height = Integer.MIN_VALUE;
                        }
                        int mTotalLength2;
                        if (mWeightSum == 0.0f) {
                            mTotalLength2 = this.mTotalLength;
                        }
                        else {
                            mTotalLength2 = 0;
                        }
                        this.measureChildBeforeLayout(virtualChild, i, n, 0, n2, mTotalLength2);
                        if (height != Integer.MIN_VALUE) {
                            layoutParams.height = height;
                        }
                        final int measuredHeight = virtualChild.getMeasuredHeight();
                        final int mTotalLength3 = this.mTotalLength;
                        this.mTotalLength = Math.max(mTotalLength3, mTotalLength3 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + this.getNextLocationOffset(virtualChild));
                        if (!mUseLargestChild) {
                            n9 = n8;
                        }
                        else {
                            max = Math.max(measuredHeight, max);
                            n9 = n8;
                        }
                    }
                    if (mBaselineAlignedChildIndex >= 0 && mBaselineAlignedChildIndex == i + 1) {
                        this.mBaselineChildTop = this.mTotalLength;
                    }
                    if (i < mBaselineAlignedChildIndex && layoutParams.weight > 0.0f) {
                        throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                    }
                    final int n10 = 0;
                    int n11;
                    int n12;
                    if (mode != 1073741824 && layoutParams.width == -1) {
                        n11 = 1;
                        n12 = 1;
                    }
                    else {
                        n11 = n7;
                        n12 = n10;
                    }
                    int b = layoutParams.leftMargin + layoutParams.rightMargin;
                    int n13 = virtualChild.getMeasuredWidth() + b;
                    final int max2 = Math.max(a, n13);
                    final int combineMeasuredStates = View.combineMeasuredStates(n3, virtualChild.getMeasuredState());
                    if (n6 != 0 && layoutParams.width == -1) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    int max3;
                    int max4;
                    if (layoutParams.weight > 0.0f) {
                        if (n12 == 0) {
                            b = n13;
                        }
                        max3 = Math.max(n5, b);
                        max4 = n4;
                    }
                    else {
                        if (n12 != 0) {
                            n13 = b;
                        }
                        max4 = Math.max(n4, n13);
                        max3 = n5;
                    }
                    final int n14 = i + this.getChildrenSkipCount(virtualChild, i);
                    n5 = max3;
                    n4 = max4;
                    final int n15 = max2;
                    n16 = max;
                    final int n17 = combineMeasuredStates;
                    n7 = n11;
                    n18 = n15;
                    n19 = n17;
                    i = n14;
                    n20 = n9;
                }
                else {
                    final int n21 = i + this.getChildrenSkipCount(virtualChild, i);
                    final int n22 = max;
                    final int n23 = n8;
                    n19 = n3;
                    n18 = a;
                    n16 = n22;
                    n20 = n23;
                    i = n21;
                }
            }
            else {
                this.mTotalLength += this.measureNullChild(i);
                final int n24 = max;
                final int n25 = n8;
                n19 = n3;
                n18 = a;
                n16 = n24;
                n20 = n25;
            }
        }
        if (this.mTotalLength > 0 && this.hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerHeight;
        }
        if (mUseLargestChild) {
            if (mode2 == Integer.MIN_VALUE || mode2 == 0) {
                this.mTotalLength = 0;
                for (int j = 0; j < virtualChildCount; ++j) {
                    final View virtualChild2 = this.getVirtualChildAt(j);
                    if (virtualChild2 != null) {
                        if (virtualChild2.getVisibility() != 8) {
                            final LayoutParams layoutParams2 = (LayoutParams)virtualChild2.getLayoutParams();
                            final int mTotalLength4 = this.mTotalLength;
                            this.mTotalLength = Math.max(mTotalLength4, layoutParams2.bottomMargin + (mTotalLength4 + max + layoutParams2.topMargin) + this.getNextLocationOffset(virtualChild2));
                        }
                        else {
                            j += this.getChildrenSkipCount(virtualChild2, j);
                        }
                    }
                    else {
                        this.mTotalLength += this.measureNullChild(j);
                    }
                }
            }
        }
        this.mTotalLength += this.getPaddingTop() + this.getPaddingBottom();
        final int resolveSizeAndState = View.resolveSizeAndState(Math.max(this.mTotalLength, this.getSuggestedMinimumHeight()), n2, 0);
        final int n28 = (0xFFFFFF & resolveSizeAndState) - this.mTotalLength;
        int n29;
        int n30;
        if (n8 == 0 && (n28 == 0 || mWeightSum <= 0.0f)) {
            final int max5 = Math.max(n4, n5);
            if (mUseLargestChild && mode2 != 1073741824) {
                for (int k = 0; k < virtualChildCount; ++k) {
                    final View virtualChild3 = this.getVirtualChildAt(k);
                    if (virtualChild3 != null && virtualChild3.getVisibility() != 8 && ((LayoutParams)virtualChild3.getLayoutParams()).weight > 0.0f) {
                        virtualChild3.measure(View$MeasureSpec.makeMeasureSpec(virtualChild3.getMeasuredWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(max, 1073741824));
                    }
                }
                n29 = max5;
                n30 = n6;
            }
            else {
                n29 = max5;
                n30 = n6;
            }
        }
        else {
            if (this.mWeightSum > 0.0f) {
                mWeightSum = this.mWeightSum;
            }
            this.mTotalLength = 0;
            final int n31 = 0;
            final int n32 = n6;
            int a2 = n4;
            final int n33 = n3;
            final int n34 = n28;
            int l = n31;
            int a3 = a;
            int n35 = n32;
            int n36 = n33;
            int n37 = n34;
            while (l < virtualChildCount) {
                final View virtualChild4 = this.getVirtualChildAt(l);
                int n42;
                int n43;
                int n48;
                int n49;
                if (virtualChild4.getVisibility() != 8) {
                    final LayoutParams layoutParams3 = (LayoutParams)virtualChild4.getLayoutParams();
                    final float weight = layoutParams3.weight;
                    if (weight > 0.0f) {
                        final int n38 = (int)(n37 * weight / mWeightSum);
                        final int childMeasureSpec = getChildMeasureSpec(n, this.getPaddingLeft() + this.getPaddingRight() + layoutParams3.leftMargin + layoutParams3.rightMargin, layoutParams3.width);
                        if (layoutParams3.height == 0 && mode2 == 1073741824) {
                            int n39;
                            if ((n39 = n38) <= 0) {
                                n39 = 0;
                            }
                            virtualChild4.measure(childMeasureSpec, View$MeasureSpec.makeMeasureSpec(n39, 1073741824));
                        }
                        else {
                            int n40 = n38 + virtualChild4.getMeasuredHeight();
                            if (n40 < 0) {
                                n40 = 0;
                            }
                            virtualChild4.measure(childMeasureSpec, View$MeasureSpec.makeMeasureSpec(n40, 1073741824));
                        }
                        final int combineMeasuredStates2 = View.combineMeasuredStates(n36, virtualChild4.getMeasuredState() & 0xFFFFFF00);
                        final int n41 = n37 - n38;
                        n42 = combineMeasuredStates2;
                        mWeightSum -= weight;
                        n43 = n41;
                    }
                    else {
                        final int n44 = n37;
                        n42 = n36;
                        n43 = n44;
                    }
                    final int n45 = layoutParams3.leftMargin + layoutParams3.rightMargin;
                    final int b2 = virtualChild4.getMeasuredWidth() + n45;
                    final int max6 = Math.max(a3, b2);
                    int n46;
                    if (mode != 1073741824 && layoutParams3.width == -1) {
                        n46 = 1;
                    }
                    else {
                        n46 = 0;
                    }
                    int b3;
                    if (n46 == 0) {
                        b3 = b2;
                    }
                    else {
                        b3 = n45;
                    }
                    final int max7 = Math.max(a2, b3);
                    int n47;
                    if (n35 != 0 && layoutParams3.width == -1) {
                        n47 = 1;
                    }
                    else {
                        n47 = 0;
                    }
                    final int mTotalLength5 = this.mTotalLength;
                    this.mTotalLength = Math.max(mTotalLength5, layoutParams3.bottomMargin + (virtualChild4.getMeasuredHeight() + mTotalLength5 + layoutParams3.topMargin) + this.getNextLocationOffset(virtualChild4));
                    n48 = max6;
                    n49 = n47;
                    a2 = max7;
                }
                else {
                    final int n50 = a3;
                    final int n51 = n37;
                    n42 = n36;
                    n43 = n51;
                    n49 = n35;
                    n48 = n50;
                }
                ++l;
                final int n52 = n48;
                final int n53 = n42;
                n37 = n43;
                n36 = n53;
                n35 = n49;
                a3 = n52;
            }
            this.mTotalLength += this.getPaddingTop() + this.getPaddingBottom();
            n30 = n35;
            n3 = n36;
            a = a3;
            n29 = a2;
        }
        if (n30 == 0 && mode != 1073741824) {
            a = n29;
        }
        this.setMeasuredDimension(View.resolveSizeAndState(Math.max(a + (this.getPaddingLeft() + this.getPaddingRight()), this.getSuggestedMinimumWidth()), n, n3), resolveSizeAndState);
        if (n7 != 0) {
            this.forceUniformWidth(virtualChildCount, n2);
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mDivider != null) {
            if (this.mOrientation != 1) {
                this.drawDividersHorizontal(canvas);
            }
            else {
                this.drawDividersVertical(canvas);
            }
        }
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        if (Build$VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)LinearLayoutCompat.class.getName());
        }
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        if (Build$VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName((CharSequence)LinearLayoutCompat.class.getName());
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (this.mOrientation != 1) {
            this.layoutHorizontal(n, n2, n3, n4);
        }
        else {
            this.layoutVertical(n, n2, n3, n4);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (this.mOrientation != 1) {
            this.measureHorizontal(n, n2);
        }
        else {
            this.measureVertical(n, n2);
        }
    }
    
    public void setBaselineAligned(final boolean mBaselineAligned) {
        this.mBaselineAligned = mBaselineAligned;
    }
    
    public void setBaselineAlignedChildIndex(final int mBaselineAlignedChildIndex) {
        if (mBaselineAlignedChildIndex >= 0 && mBaselineAlignedChildIndex < this.getChildCount()) {
            this.mBaselineAlignedChildIndex = mBaselineAlignedChildIndex;
            return;
        }
        throw new IllegalArgumentException("base aligned child index out of range (0, " + this.getChildCount() + ")");
    }
    
    public void setDividerDrawable(final Drawable mDivider) {
        boolean willNotDraw = false;
        if (mDivider != this.mDivider) {
            if ((this.mDivider = mDivider) == null) {
                this.mDividerWidth = 0;
                this.mDividerHeight = 0;
            }
            else {
                this.mDividerWidth = mDivider.getIntrinsicWidth();
                this.mDividerHeight = mDivider.getIntrinsicHeight();
            }
            if (mDivider == null) {
                willNotDraw = true;
            }
            this.setWillNotDraw(willNotDraw);
            this.requestLayout();
        }
    }
    
    public void setDividerPadding(final int mDividerPadding) {
        this.mDividerPadding = mDividerPadding;
    }
    
    public void setGravity(int mGravity) {
        if (this.mGravity != mGravity) {
            if ((0x800007 & mGravity) == 0x0) {
                mGravity |= 0x800003;
            }
            if ((mGravity & 0x70) == 0x0) {
                mGravity |= 0x30;
            }
            this.mGravity = mGravity;
            this.requestLayout();
        }
    }
    
    public void setHorizontalGravity(int n) {
        n &= 0x800007;
        if ((this.mGravity & 0x800007) != n) {
            this.mGravity = (n | (this.mGravity & 0xFF7FFFF8));
            this.requestLayout();
        }
    }
    
    public void setMeasureWithLargestChildEnabled(final boolean mUseLargestChild) {
        this.mUseLargestChild = mUseLargestChild;
    }
    
    public void setOrientation(final int mOrientation) {
        if (this.mOrientation != mOrientation) {
            this.mOrientation = mOrientation;
            this.requestLayout();
        }
    }
    
    public void setShowDividers(final int mShowDividers) {
        if (mShowDividers != this.mShowDividers) {
            this.requestLayout();
        }
        this.mShowDividers = mShowDividers;
    }
    
    public void setVerticalGravity(int n) {
        n &= 0x70;
        if ((this.mGravity & 0x70) != n) {
            this.mGravity = (n | (this.mGravity & 0xFFFFFF8F));
            this.requestLayout();
        }
    }
    
    public void setWeightSum(final float b) {
        this.mWeightSum = Math.max(0.0f, b);
    }
    
    public boolean shouldDelayChildPressedState() {
        return false;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface DividerMode {
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int gravity;
        public float weight;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.gravity = -1;
            this.weight = 0.0f;
        }
        
        public LayoutParams(final int n, final int n2, final float weight) {
            super(n, n2);
            this.gravity = -1;
            this.weight = weight;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.gravity = -1;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.LinearLayoutCompat_Layout);
            this.weight = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.gravity = -1;
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.gravity = -1;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.gravity = -1;
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface OrientationMode {
    }
}
