// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;
import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.v4.view.AbsSavedState;
import android.content.res.TypedArray;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.content.ContextCompat;
import android.support.annotation.DrawableRes;
import android.os.Parcelable;
import android.util.Log;
import android.view.View$MeasureSpec;
import android.view.MotionEvent;
import android.support.annotation.ColorInt;
import android.view.ViewGroup$MarginLayoutParams;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff$Mode;
import android.graphics.Paint;
import android.graphics.ColorFilter;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.graphics.Rect;
import android.view.View;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import android.view.ViewGroup;

public class SlidingPaneLayout extends ViewGroup
{
    private static final int DEFAULT_FADE_COLOR = -858993460;
    private static final int DEFAULT_OVERHANG_SIZE = 32;
    static final SlidingPanelLayoutImpl IMPL;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final String TAG = "SlidingPaneLayout";
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    final ViewDragHelper mDragHelper;
    private boolean mFirstLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    boolean mIsUnableToDrag;
    private final int mOverhangSize;
    private PanelSlideListener mPanelSlideListener;
    private int mParallaxBy;
    private float mParallaxOffset;
    final ArrayList<DisableLayerRunnable> mPostedRunnables;
    boolean mPreservedOpenState;
    private Drawable mShadowDrawableLeft;
    private Drawable mShadowDrawableRight;
    float mSlideOffset;
    int mSlideRange;
    View mSlideableView;
    private int mSliderFadeColor;
    private final Rect mTmpRect;
    
    static {
        if (Build$VERSION.SDK_INT < 17) {
            if (Build$VERSION.SDK_INT < 16) {
                IMPL = (SlidingPanelLayoutImpl)new SlidingPanelLayoutImplBase();
            }
            else {
                IMPL = (SlidingPanelLayoutImpl)new SlidingPanelLayoutImplJB();
            }
        }
        else {
            IMPL = (SlidingPanelLayoutImpl)new SlidingPanelLayoutImplJBMR1();
        }
    }
    
    public SlidingPaneLayout(final Context context) {
        this(context, null);
    }
    
    public SlidingPaneLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public SlidingPaneLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mSliderFadeColor = -858993460;
        this.mFirstLayout = true;
        this.mTmpRect = new Rect();
        this.mPostedRunnables = new ArrayList<DisableLayerRunnable>();
        final float density = context.getResources().getDisplayMetrics().density;
        this.mOverhangSize = (int)(32.0f * density + 0.5f);
        this.setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate((View)this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility((View)this, 1);
        (this.mDragHelper = ViewDragHelper.create(this, 0.5f, (ViewDragHelper.Callback)new DragHelperCallback())).setMinVelocity(density * 400.0f);
    }
    
    private boolean closePane(final View view, final int n) {
        if (!this.mFirstLayout && !this.smoothSlideTo(0.0f, n)) {
            return false;
        }
        this.mPreservedOpenState = false;
        return true;
    }
    
    private void dimChildView(final View view, final float n, final int n2) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (n <= 0.0f || n2 == 0) {
            if (view.getLayerType() != 0) {
                if (layoutParams.dimPaint != null) {
                    layoutParams.dimPaint.setColorFilter((ColorFilter)null);
                }
                final DisableLayerRunnable e = new DisableLayerRunnable(view);
                this.mPostedRunnables.add(e);
                ViewCompat.postOnAnimation((View)this, e);
            }
        }
        else {
            final int n3 = (int)(((0xFF000000 & n2) >>> 24) * n);
            if (layoutParams.dimPaint == null) {
                layoutParams.dimPaint = new Paint();
            }
            layoutParams.dimPaint.setColorFilter((ColorFilter)new PorterDuffColorFilter(n3 << 24 | (0xFFFFFF & n2), PorterDuff$Mode.SRC_OVER));
            if (view.getLayerType() != 2) {
                view.setLayerType(2, layoutParams.dimPaint);
            }
            this.invalidateChildRegion(view);
        }
    }
    
    private boolean openPane(final View view, final int n) {
        return (this.mFirstLayout || this.smoothSlideTo(1.0f, n)) && (this.mPreservedOpenState = true);
    }
    
    private void parallaxOtherViews(final float mParallaxOffset) {
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        final LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
        boolean b = false;
        Label_0026: {
            if (layoutParams.dimWhenOffset) {
                int n;
                if (!layoutRtlSupport) {
                    n = layoutParams.leftMargin;
                }
                else {
                    n = layoutParams.rightMargin;
                }
                if (n <= 0) {
                    b = true;
                    break Label_0026;
                }
            }
            b = false;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child != this.mSlideableView) {
                final int n2 = (int)((1.0f - this.mParallaxOffset) * this.mParallaxBy);
                this.mParallaxOffset = mParallaxOffset;
                int n3 = n2 - (int)((1.0f - mParallaxOffset) * this.mParallaxBy);
                if (layoutRtlSupport) {
                    n3 = -n3;
                }
                child.offsetLeftAndRight(n3);
                if (b) {
                    float n4;
                    if (!layoutRtlSupport) {
                        n4 = 1.0f - this.mParallaxOffset;
                    }
                    else {
                        n4 = this.mParallaxOffset - 1.0f;
                    }
                    this.dimChildView(child, n4, this.mCoveredFadeColor);
                }
            }
        }
    }
    
    private static boolean viewIsOpaque(final View view) {
        boolean b = false;
        if (view.isOpaque()) {
            return true;
        }
        if (Build$VERSION.SDK_INT >= 18) {
            return false;
        }
        final Drawable background = view.getBackground();
        if (background == null) {
            return false;
        }
        if (background.getOpacity() == -1) {
            b = true;
        }
        return b;
    }
    
    protected boolean canScroll(final View view, final boolean b, final int n, int n2, final int n3) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            final int scrollX = view.getScrollX();
            final int scrollY = view.getScrollY();
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                if (n2 + scrollX >= child.getLeft() && n2 + scrollX < child.getRight() && n3 + scrollY >= child.getTop() && n3 + scrollY < child.getBottom() && this.canScroll(child, true, n, n2 + scrollX - child.getLeft(), n3 + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        if (b) {
            n2 = n;
            if (!this.isLayoutRtlSupport()) {
                n2 = -n;
            }
            if (view.canScrollHorizontally(n2)) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public boolean canSlide() {
        return this.mCanSlide;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        boolean b = false;
        if (viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams)) {
            b = true;
        }
        return b;
    }
    
    public boolean closePane() {
        return this.closePane(this.mSlideableView, 0);
    }
    
    public void computeScroll() {
        if (this.mDragHelper.continueSettling(true)) {
            if (!this.mCanSlide) {
                this.mDragHelper.abort();
                return;
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void dispatchOnPanelClosed(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelClosed(view);
        }
        this.sendAccessibilityEvent(32);
    }
    
    void dispatchOnPanelOpened(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelOpened(view);
        }
        this.sendAccessibilityEvent(32);
    }
    
    void dispatchOnPanelSlide(final View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelSlide(view, this.mSlideOffset);
        }
    }
    
    public void draw(final Canvas canvas) {
        View child = null;
        super.draw(canvas);
        Drawable drawable;
        if (!this.isLayoutRtlSupport()) {
            drawable = this.mShadowDrawableLeft;
        }
        else {
            drawable = this.mShadowDrawableRight;
        }
        if (this.getChildCount() > 1) {
            child = this.getChildAt(1);
        }
        if (child != null && drawable != null) {
            final int top = child.getTop();
            final int bottom = child.getBottom();
            final int intrinsicWidth = drawable.getIntrinsicWidth();
            int left;
            int right;
            if (!this.isLayoutRtlSupport()) {
                left = child.getLeft();
                right = left - intrinsicWidth;
            }
            else {
                right = child.getRight();
                left = right + intrinsicWidth;
            }
            drawable.setBounds(right, top, left, bottom);
            drawable.draw(canvas);
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int save = canvas.save(2);
        if (this.mCanSlide && !layoutParams.slideable && this.mSlideableView != null) {
            canvas.getClipBounds(this.mTmpRect);
            if (!this.isLayoutRtlSupport()) {
                this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
            }
            else {
                this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
            }
            canvas.clipRect(this.mTmpRect);
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        return drawChild;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new LayoutParams();
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        LayoutParams layoutParams;
        if (!(viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams)) {
            layoutParams = new LayoutParams(viewGroup$LayoutParams);
        }
        else {
            layoutParams = new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)layoutParams;
    }
    
    @ColorInt
    public int getCoveredFadeColor() {
        return this.mCoveredFadeColor;
    }
    
    public int getParallaxDistance() {
        return this.mParallaxBy;
    }
    
    @ColorInt
    public int getSliderFadeColor() {
        return this.mSliderFadeColor;
    }
    
    void invalidateChildRegion(final View view) {
        SlidingPaneLayout.IMPL.invalidateChildRegion(this, view);
    }
    
    boolean isDimmed(final View view) {
        if (view != null) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            return this.mCanSlide && layoutParams.dimWhenOffset && this.mSlideOffset > 0.0f;
        }
        return false;
    }
    
    boolean isLayoutRtlSupport() {
        boolean b = true;
        if (ViewCompat.getLayoutDirection((View)this) != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isOpen() {
        boolean b = false;
        if (!this.mCanSlide || this.mSlideOffset == 1.0f) {
            b = true;
        }
        return b;
    }
    
    public boolean isSlideable() {
        return this.mCanSlide;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
        for (int size = this.mPostedRunnables.size(), i = 0; i < size; ++i) {
            this.mPostedRunnables.get(i).run();
        }
        this.mPostedRunnables.clear();
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final boolean b = false;
        final int actionMasked = motionEvent.getActionMasked();
        if (!this.mCanSlide && actionMasked == 0 && this.getChildCount() > 1) {
            final View child = this.getChildAt(1);
            if (child != null) {
                this.mPreservedOpenState = !this.mDragHelper.isViewUnder(child, (int)motionEvent.getX(), (int)motionEvent.getY());
            }
        }
        if (!this.mCanSlide || (this.mIsUnableToDrag && actionMasked != 0)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (actionMasked != 3 && actionMasked != 1) {
            boolean b2 = false;
            switch (actionMasked) {
                default: {
                    b2 = false;
                    break;
                }
                case 0: {
                    this.mIsUnableToDrag = false;
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    this.mInitialMotionX = x;
                    this.mInitialMotionY = y;
                    b2 = (this.mDragHelper.isViewUnder(this.mSlideableView, (int)x, (int)y) && this.isDimmed(this.mSlideableView));
                    break;
                }
                case 2: {
                    final float x2 = motionEvent.getX();
                    final float y2 = motionEvent.getY();
                    final float abs = Math.abs(x2 - this.mInitialMotionX);
                    final float abs2 = Math.abs(y2 - this.mInitialMotionY);
                    if (abs > this.mDragHelper.getTouchSlop() && abs2 > abs) {
                        this.mDragHelper.cancel();
                        this.mIsUnableToDrag = true;
                        return false;
                    }
                    b2 = false;
                    break;
                }
            }
            return this.mDragHelper.shouldInterceptTouchEvent(motionEvent) || b2 || b;
        }
        this.mDragHelper.cancel();
        return false;
    }
    
    protected void onLayout(final boolean b, int i, int n, int n2, int n3) {
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        if (!layoutRtlSupport) {
            this.mDragHelper.setEdgeTrackingEnabled(1);
        }
        else {
            this.mDragHelper.setEdgeTrackingEnabled(2);
        }
        final int n4 = n2 - i;
        if (!layoutRtlSupport) {
            i = this.getPaddingLeft();
        }
        else {
            i = this.getPaddingRight();
        }
        if (!layoutRtlSupport) {
            n3 = this.getPaddingRight();
        }
        else {
            n3 = this.getPaddingLeft();
        }
        final int paddingTop = this.getPaddingTop();
        final int childCount = this.getChildCount();
        if (this.mFirstLayout) {
            float mSlideOffset;
            if (this.mCanSlide && this.mPreservedOpenState) {
                mSlideOffset = 1.0f;
            }
            else {
                mSlideOffset = 0.0f;
            }
            this.mSlideOffset = mSlideOffset;
        }
        int j;
        View child;
        LayoutParams layoutParams;
        int measuredWidth;
        int mSlideRange;
        int n5;
        int n6;
        for (j = 0, n2 = i, n = i, i = n2; j < childCount; ++j, n2 = n, n = i, i = n2) {
            child = this.getChildAt(j);
            if (child.getVisibility() != 8) {
                layoutParams = (LayoutParams)child.getLayoutParams();
                measuredWidth = child.getMeasuredWidth();
                if (!layoutParams.slideable) {
                    if (this.mCanSlide && this.mParallaxBy != 0) {
                        n2 = (int)((1.0f - this.mSlideOffset) * this.mParallaxBy);
                        n = i;
                    }
                    else {
                        n2 = 0;
                        n = i;
                    }
                }
                else {
                    n2 = layoutParams.leftMargin;
                    mSlideRange = Math.min(i, n4 - n3 - this.mOverhangSize) - n - (n2 + layoutParams.rightMargin);
                    this.mSlideRange = mSlideRange;
                    if (!layoutRtlSupport) {
                        n2 = layoutParams.leftMargin;
                    }
                    else {
                        n2 = layoutParams.rightMargin;
                    }
                    layoutParams.dimWhenOffset = (n + n2 + mSlideRange + measuredWidth / 2 > n4 - n3);
                    n5 = (int)(mSlideRange * this.mSlideOffset);
                    n += n2 + n5;
                    this.mSlideOffset = n5 / (float)this.mSlideRange;
                    n2 = 0;
                }
                if (!layoutRtlSupport) {
                    n6 = n - n2;
                    n2 = n6 + measuredWidth;
                }
                else {
                    n2 += n4 - n;
                    n6 = n2 - measuredWidth;
                }
                child.layout(n6, paddingTop, n2, child.getMeasuredHeight() + paddingTop);
                n2 = child.getWidth() + i;
                i = n;
                n = n2;
            }
            else {
                n2 = i;
                i = n;
                n = n2;
            }
        }
        if (this.mFirstLayout) {
            if (!this.mCanSlide) {
                for (i = 0; i < childCount; ++i) {
                    this.dimChildView(this.getChildAt(i), 0.0f, this.mSliderFadeColor);
                }
            }
            else {
                if (this.mParallaxBy != 0) {
                    this.parallaxOtherViews(this.mSlideOffset);
                }
                if (((LayoutParams)this.mSlideableView.getLayoutParams()).dimWhenOffset) {
                    this.dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
                }
            }
            this.updateObscuredViewsVisibility(this.mSlideableView);
        }
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(int b, int a) {
        final int mode = View$MeasureSpec.getMode(b);
        final int size = View$MeasureSpec.getSize(b);
        final int mode2 = View$MeasureSpec.getMode(a);
        a = View$MeasureSpec.getSize(a);
        int n;
        int n2;
        if (mode == 1073741824) {
            if (mode2 != 0) {
                n = size;
                n2 = mode2;
                b = a;
            }
            else {
                if (!this.isInEditMode()) {
                    throw new IllegalStateException("Height must not be UNSPECIFIED");
                }
                b = a;
                n2 = mode2;
                n = size;
                if (mode2 == 0) {
                    n2 = Integer.MIN_VALUE;
                    b = 300;
                    n = size;
                }
            }
        }
        else {
            if (!this.isInEditMode()) {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            }
            b = a;
            n2 = mode2;
            n = size;
            if (mode != Integer.MIN_VALUE) {
                b = a;
                n2 = mode2;
                n = size;
                if (mode == 0) {
                    n = 300;
                    b = a;
                    n2 = mode2;
                }
            }
        }
        int b2 = 0;
        switch (n2) {
            default: {
                b = 0;
                break;
            }
            case 1073741824: {
                b = (b2 = b - this.getPaddingTop() - this.getPaddingBottom());
                break;
            }
            case Integer.MIN_VALUE: {
                b2 = b - this.getPaddingTop() - this.getPaddingBottom();
                b = 0;
                break;
            }
        }
        boolean mCanSlide = false;
        final int n3 = n - this.getPaddingLeft() - this.getPaddingRight();
        final int childCount = this.getChildCount();
        if (childCount > 2) {
            Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
        }
        this.mSlideableView = null;
        int i = 0;
        a = n3;
        int n4 = b;
        float n5 = 0.0f;
        b = a;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            float n6 = 0.0f;
            Label_0550: {
                if (child.getVisibility() != 8) {
                    n6 = n5;
                    if (layoutParams.weight > 0.0f) {
                        n6 = n5 + layoutParams.weight;
                        if (layoutParams.width == 0) {
                            break Label_0550;
                        }
                    }
                    a = layoutParams.leftMargin + layoutParams.rightMargin;
                    if (layoutParams.width != -2) {
                        if (layoutParams.width != -1) {
                            a = View$MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
                        }
                        else {
                            a = View$MeasureSpec.makeMeasureSpec(n3 - a, 1073741824);
                        }
                    }
                    else {
                        a = View$MeasureSpec.makeMeasureSpec(n3 - a, Integer.MIN_VALUE);
                    }
                    int n7;
                    if (layoutParams.height != -2) {
                        if (layoutParams.height != -1) {
                            n7 = View$MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824);
                        }
                        else {
                            n7 = View$MeasureSpec.makeMeasureSpec(b2, 1073741824);
                        }
                    }
                    else {
                        n7 = View$MeasureSpec.makeMeasureSpec(b2, Integer.MIN_VALUE);
                    }
                    child.measure(a, n7);
                    final int measuredWidth = child.getMeasuredWidth();
                    a = child.getMeasuredHeight();
                    if (n2 == Integer.MIN_VALUE && a > n4) {
                        a = Math.min(a, b2);
                    }
                    else {
                        a = n4;
                    }
                    b -= measuredWidth;
                    final boolean slideable = b < 0;
                    layoutParams.slideable = slideable;
                    mCanSlide |= slideable;
                    if (!layoutParams.slideable) {
                        n4 = a;
                    }
                    else {
                        this.mSlideableView = child;
                        n4 = a;
                    }
                }
                else {
                    layoutParams.dimWhenOffset = false;
                    n6 = n5;
                }
            }
            ++i;
            n5 = n6;
        }
        if (mCanSlide || n5 > 0.0f) {
            final int n8 = n3 - this.mOverhangSize;
            for (int j = 0; j < childCount; ++j) {
                final View child2 = this.getChildAt(j);
                if (child2.getVisibility() != 8) {
                    final LayoutParams layoutParams2 = (LayoutParams)child2.getLayoutParams();
                    if (child2.getVisibility() != 8) {
                        if (layoutParams2.width == 0 && layoutParams2.weight > 0.0f) {
                            a = 1;
                        }
                        else {
                            a = 0;
                        }
                        int measuredWidth2;
                        if (a == 0) {
                            measuredWidth2 = child2.getMeasuredWidth();
                        }
                        else {
                            measuredWidth2 = 0;
                        }
                        if (mCanSlide && child2 != this.mSlideableView) {
                            if (layoutParams2.width < 0 && (measuredWidth2 > n8 || layoutParams2.weight > 0.0f)) {
                                if (a == 0) {
                                    a = View$MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), 1073741824);
                                }
                                else if (layoutParams2.height != -2) {
                                    if (layoutParams2.height != -1) {
                                        a = View$MeasureSpec.makeMeasureSpec(layoutParams2.height, 1073741824);
                                    }
                                    else {
                                        a = View$MeasureSpec.makeMeasureSpec(b2, 1073741824);
                                    }
                                }
                                else {
                                    a = View$MeasureSpec.makeMeasureSpec(b2, Integer.MIN_VALUE);
                                }
                                child2.measure(View$MeasureSpec.makeMeasureSpec(n8, 1073741824), a);
                            }
                        }
                        else if (layoutParams2.weight > 0.0f) {
                            if (layoutParams2.width != 0) {
                                a = View$MeasureSpec.makeMeasureSpec(child2.getMeasuredHeight(), 1073741824);
                            }
                            else if (layoutParams2.height != -2) {
                                if (layoutParams2.height != -1) {
                                    a = View$MeasureSpec.makeMeasureSpec(layoutParams2.height, 1073741824);
                                }
                                else {
                                    a = View$MeasureSpec.makeMeasureSpec(b2, 1073741824);
                                }
                            }
                            else {
                                a = View$MeasureSpec.makeMeasureSpec(b2, Integer.MIN_VALUE);
                            }
                            if (!mCanSlide) {
                                child2.measure(View$MeasureSpec.makeMeasureSpec((int)(layoutParams2.weight * Math.max(0, b) / n5) + measuredWidth2, 1073741824), a);
                            }
                            else {
                                final int n9 = n3 - (layoutParams2.rightMargin + layoutParams2.leftMargin);
                                final int measureSpec = View$MeasureSpec.makeMeasureSpec(n9, 1073741824);
                                if (measuredWidth2 != n9) {
                                    child2.measure(measureSpec, a);
                                }
                            }
                        }
                    }
                }
            }
        }
        this.setMeasuredDimension(n, this.getPaddingTop() + n4 + this.getPaddingBottom());
        this.mCanSlide = mCanSlide;
        if (this.mDragHelper.getViewDragState() != 0 && !mCanSlide) {
            this.mDragHelper.abort();
        }
    }
    
    void onPanelDragged(int n) {
        if (this.mSlideableView != null) {
            final boolean layoutRtlSupport = this.isLayoutRtlSupport();
            final LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
            final int width = this.mSlideableView.getWidth();
            if (layoutRtlSupport) {
                n = this.getWidth() - n - width;
            }
            int n2;
            if (!layoutRtlSupport) {
                n2 = this.getPaddingLeft();
            }
            else {
                n2 = this.getPaddingRight();
            }
            int n3;
            if (!layoutRtlSupport) {
                n3 = layoutParams.leftMargin;
            }
            else {
                n3 = layoutParams.rightMargin;
            }
            this.mSlideOffset = (n - (n2 + n3)) / (float)this.mSlideRange;
            if (this.mParallaxBy != 0) {
                this.parallaxOtherViews(this.mSlideOffset);
            }
            if (layoutParams.dimWhenOffset) {
                this.dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
            }
            this.dispatchOnPanelSlide(this.mSlideableView);
            return;
        }
        this.mSlideOffset = 0.0f;
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            if (!savedState.isOpen) {
                this.closePane();
            }
            else {
                this.openPane();
            }
            this.mPreservedOpenState = savedState.isOpen;
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        boolean isOpen;
        if (!this.isSlideable()) {
            isOpen = this.mPreservedOpenState;
        }
        else {
            isOpen = this.isOpen();
        }
        savedState.isOpen = isOpen;
        return (Parcelable)savedState;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n != n3) {
            this.mFirstLayout = true;
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mCanSlide) {
            this.mDragHelper.processTouchEvent(motionEvent);
            switch (motionEvent.getActionMasked()) {
                case 0: {
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    this.mInitialMotionX = x;
                    this.mInitialMotionY = y;
                    break;
                }
                case 1: {
                    if (!this.isDimmed(this.mSlideableView)) {
                        break;
                    }
                    final float x2 = motionEvent.getX();
                    final float y2 = motionEvent.getY();
                    final float n = x2 - this.mInitialMotionX;
                    final float n2 = y2 - this.mInitialMotionY;
                    final int touchSlop = this.mDragHelper.getTouchSlop();
                    if (n * n + n2 * n2 < touchSlop * touchSlop && this.mDragHelper.isViewUnder(this.mSlideableView, (int)x2, (int)y2)) {
                        this.closePane(this.mSlideableView, 0);
                        break;
                    }
                    break;
                }
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public boolean openPane() {
        return this.openPane(this.mSlideableView, 0);
    }
    
    public void requestChildFocus(final View view, final View view2) {
        boolean mPreservedOpenState = false;
        super.requestChildFocus(view, view2);
        if (!this.isInTouchMode() && !this.mCanSlide) {
            if (view == this.mSlideableView) {
                mPreservedOpenState = true;
            }
            this.mPreservedOpenState = mPreservedOpenState;
        }
    }
    
    void setAllChildrenVisible() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 4) {
                child.setVisibility(0);
            }
        }
    }
    
    public void setCoveredFadeColor(@ColorInt final int mCoveredFadeColor) {
        this.mCoveredFadeColor = mCoveredFadeColor;
    }
    
    public void setPanelSlideListener(final PanelSlideListener mPanelSlideListener) {
        this.mPanelSlideListener = mPanelSlideListener;
    }
    
    public void setParallaxDistance(final int mParallaxBy) {
        this.mParallaxBy = mParallaxBy;
        this.requestLayout();
    }
    
    @Deprecated
    public void setShadowDrawable(final Drawable shadowDrawableLeft) {
        this.setShadowDrawableLeft(shadowDrawableLeft);
    }
    
    public void setShadowDrawableLeft(final Drawable mShadowDrawableLeft) {
        this.mShadowDrawableLeft = mShadowDrawableLeft;
    }
    
    public void setShadowDrawableRight(final Drawable mShadowDrawableRight) {
        this.mShadowDrawableRight = mShadowDrawableRight;
    }
    
    @Deprecated
    public void setShadowResource(@DrawableRes final int n) {
        this.setShadowDrawable(this.getResources().getDrawable(n));
    }
    
    public void setShadowResourceLeft(final int n) {
        this.setShadowDrawableLeft(ContextCompat.getDrawable(this.getContext(), n));
    }
    
    public void setShadowResourceRight(final int n) {
        this.setShadowDrawableRight(ContextCompat.getDrawable(this.getContext(), n));
    }
    
    public void setSliderFadeColor(@ColorInt final int mSliderFadeColor) {
        this.mSliderFadeColor = mSliderFadeColor;
    }
    
    @Deprecated
    public void smoothSlideClosed() {
        this.closePane();
    }
    
    @Deprecated
    public void smoothSlideOpen() {
        this.openPane();
    }
    
    boolean smoothSlideTo(final float n, int n2) {
        if (!this.mCanSlide) {
            return false;
        }
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        final LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
        if (!layoutRtlSupport) {
            n2 = this.getPaddingLeft();
            n2 = (int)(layoutParams.leftMargin + n2 + this.mSlideRange * n);
        }
        else {
            n2 = this.getPaddingRight();
            n2 = (int)(this.getWidth() - (layoutParams.rightMargin + n2 + this.mSlideRange * n + this.mSlideableView.getWidth()));
        }
        if (!this.mDragHelper.smoothSlideViewTo(this.mSlideableView, n2, this.mSlideableView.getTop())) {
            return false;
        }
        this.setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation((View)this);
        return true;
    }
    
    void updateObscuredViewsVisibility(final View view) {
        final boolean layoutRtlSupport = this.isLayoutRtlSupport();
        int paddingLeft;
        if (!layoutRtlSupport) {
            paddingLeft = this.getPaddingLeft();
        }
        else {
            paddingLeft = this.getWidth() - this.getPaddingRight();
        }
        int paddingLeft2;
        if (!layoutRtlSupport) {
            paddingLeft2 = this.getWidth() - this.getPaddingRight();
        }
        else {
            paddingLeft2 = this.getPaddingLeft();
        }
        final int paddingTop = this.getPaddingTop();
        final int height = this.getHeight();
        final int paddingBottom = this.getPaddingBottom();
        int left;
        int right;
        int top;
        int bottom;
        if (view != null && viewIsOpaque(view)) {
            left = view.getLeft();
            right = view.getRight();
            top = view.getTop();
            bottom = view.getBottom();
        }
        else {
            bottom = 0;
            top = 0;
            right = 0;
            left = 0;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child == view) {
                break;
            }
            if (child.getVisibility() != 8) {
                int a;
                if (!layoutRtlSupport) {
                    a = paddingLeft;
                }
                else {
                    a = paddingLeft2;
                }
                final int max = Math.max(a, child.getLeft());
                final int max2 = Math.max(paddingTop, child.getTop());
                int a2;
                if (!layoutRtlSupport) {
                    a2 = paddingLeft2;
                }
                else {
                    a2 = paddingLeft;
                }
                final int min = Math.min(a2, child.getRight());
                final int min2 = Math.min(height - paddingBottom, child.getBottom());
                int visibility;
                if (max >= left && max2 >= top && min <= right && min2 <= bottom) {
                    visibility = 4;
                }
                else {
                    visibility = 0;
                }
                child.setVisibility(visibility);
            }
        }
    }
    
    class AccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private final Rect mTmpRect;
        
        AccessibilityDelegate() {
            this.mTmpRect = new Rect();
        }
        
        private void copyNodeInfoNoChildren(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2) {
            final Rect mTmpRect = this.mTmpRect;
            accessibilityNodeInfoCompat2.getBoundsInParent(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInParent(mTmpRect);
            accessibilityNodeInfoCompat2.getBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setVisibleToUser(accessibilityNodeInfoCompat2.isVisibleToUser());
            accessibilityNodeInfoCompat.setPackageName(accessibilityNodeInfoCompat2.getPackageName());
            accessibilityNodeInfoCompat.setClassName(accessibilityNodeInfoCompat2.getClassName());
            accessibilityNodeInfoCompat.setContentDescription(accessibilityNodeInfoCompat2.getContentDescription());
            accessibilityNodeInfoCompat.setEnabled(accessibilityNodeInfoCompat2.isEnabled());
            accessibilityNodeInfoCompat.setClickable(accessibilityNodeInfoCompat2.isClickable());
            accessibilityNodeInfoCompat.setFocusable(accessibilityNodeInfoCompat2.isFocusable());
            accessibilityNodeInfoCompat.setFocused(accessibilityNodeInfoCompat2.isFocused());
            accessibilityNodeInfoCompat.setAccessibilityFocused(accessibilityNodeInfoCompat2.isAccessibilityFocused());
            accessibilityNodeInfoCompat.setSelected(accessibilityNodeInfoCompat2.isSelected());
            accessibilityNodeInfoCompat.setLongClickable(accessibilityNodeInfoCompat2.isLongClickable());
            accessibilityNodeInfoCompat.addAction(accessibilityNodeInfoCompat2.getActions());
            accessibilityNodeInfoCompat.setMovementGranularities(accessibilityNodeInfoCompat2.getMovementGranularities());
        }
        
        public boolean filter(final View view) {
            return SlidingPaneLayout.this.isDimmed(view);
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)SlidingPaneLayout.class.getName());
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(View child, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
            super.onInitializeAccessibilityNodeInfo(child, obtain);
            this.copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
            obtain.recycle();
            accessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
            accessibilityNodeInfoCompat.setSource(child);
            final ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(child);
            if (parentForAccessibility instanceof View) {
                accessibilityNodeInfoCompat.setParent((View)parentForAccessibility);
            }
            for (int childCount = SlidingPaneLayout.this.getChildCount(), i = 0; i < childCount; ++i) {
                child = SlidingPaneLayout.this.getChildAt(i);
                if (!this.filter(child) && child.getVisibility() == 0) {
                    ViewCompat.setImportantForAccessibility(child, 1);
                    accessibilityNodeInfoCompat.addChild(child);
                }
            }
        }
        
        @Override
        public boolean onRequestSendAccessibilityEvent(final ViewGroup viewGroup, final View view, final AccessibilityEvent accessibilityEvent) {
            return !this.filter(view) && super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }
    
    private class DisableLayerRunnable implements Runnable
    {
        final View mChildView;
        
        DisableLayerRunnable(final View mChildView) {
            this.mChildView = mChildView;
        }
        
        @Override
        public void run() {
            if (this.mChildView.getParent() == SlidingPaneLayout.this) {
                this.mChildView.setLayerType(0, (Paint)null);
                SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
            }
            SlidingPaneLayout.this.mPostedRunnables.remove(this);
        }
    }
    
    private class DragHelperCallback extends Callback
    {
        DragHelperCallback() {
        }
        
        @Override
        public int clampViewPositionHorizontal(final View view, int n, int b) {
            final LayoutParams layoutParams = (LayoutParams)SlidingPaneLayout.this.mSlideableView.getLayoutParams();
            if (!SlidingPaneLayout.this.isLayoutRtlSupport()) {
                b = SlidingPaneLayout.this.getPaddingLeft();
                b += layoutParams.leftMargin;
                n = Math.min(Math.max(n, b), SlidingPaneLayout.this.mSlideRange + b);
            }
            else {
                final int width = SlidingPaneLayout.this.getWidth();
                b = SlidingPaneLayout.this.getPaddingRight();
                final int b2 = width - (layoutParams.rightMargin + b + SlidingPaneLayout.this.mSlideableView.getWidth());
                b = SlidingPaneLayout.this.mSlideRange;
                n = Math.max(Math.min(n, b2), b2 - b);
            }
            return n;
        }
        
        @Override
        public int clampViewPositionVertical(final View view, final int n, final int n2) {
            return view.getTop();
        }
        
        @Override
        public int getViewHorizontalDragRange(final View view) {
            return SlidingPaneLayout.this.mSlideRange;
        }
        
        @Override
        public void onEdgeDragStarted(final int n, final int n2) {
            SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, n2);
        }
        
        @Override
        public void onViewCaptured(final View view, final int n) {
            SlidingPaneLayout.this.setAllChildrenVisible();
        }
        
        @Override
        public void onViewDragStateChanged(final int n) {
            if (SlidingPaneLayout.this.mDragHelper.getViewDragState() == 0) {
                if (SlidingPaneLayout.this.mSlideOffset == 0.0f) {
                    SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
                    SlidingPaneLayout.this.dispatchOnPanelClosed(SlidingPaneLayout.this.mSlideableView);
                    SlidingPaneLayout.this.mPreservedOpenState = false;
                }
                else {
                    SlidingPaneLayout.this.dispatchOnPanelOpened(SlidingPaneLayout.this.mSlideableView);
                    SlidingPaneLayout.this.mPreservedOpenState = true;
                }
            }
        }
        
        @Override
        public void onViewPositionChanged(final View view, final int n, final int n2, final int n3, final int n4) {
            SlidingPaneLayout.this.onPanelDragged(n);
            SlidingPaneLayout.this.invalidate();
        }
        
        @Override
        public void onViewReleased(final View view, final float n, final float n2) {
            final int n3 = 1;
            int n4 = 1;
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            int n6 = 0;
            Label_0094: {
                if (!SlidingPaneLayout.this.isLayoutRtlSupport()) {
                    final int n5 = layoutParams.leftMargin + SlidingPaneLayout.this.getPaddingLeft();
                    if (n <= 0.0f) {
                        n4 = 0;
                    }
                    if (n4 == 0) {
                        n6 = n5;
                        if (n != 0.0f) {
                            break Label_0094;
                        }
                        n6 = n5;
                        if (SlidingPaneLayout.this.mSlideOffset <= 0.5f) {
                            break Label_0094;
                        }
                    }
                    n6 = n5 + SlidingPaneLayout.this.mSlideRange;
                }
                else {
                    final int n7 = layoutParams.rightMargin + SlidingPaneLayout.this.getPaddingRight();
                    int n8;
                    if (n < 0.0f) {
                        n8 = n3;
                    }
                    else {
                        n8 = 0;
                    }
                    int n9 = 0;
                    Label_0192: {
                        if (n8 == 0) {
                            n9 = n7;
                            if (n != 0.0f) {
                                break Label_0192;
                            }
                            n9 = n7;
                            if (SlidingPaneLayout.this.mSlideOffset <= 0.5f) {
                                break Label_0192;
                            }
                        }
                        n9 = n7 + SlidingPaneLayout.this.mSlideRange;
                    }
                    n6 = SlidingPaneLayout.this.getWidth() - n9 - SlidingPaneLayout.this.mSlideableView.getWidth();
                }
            }
            SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(n6, view.getTop());
            SlidingPaneLayout.this.invalidate();
        }
        
        @Override
        public boolean tryCaptureView(final View view, final int n) {
            return !SlidingPaneLayout.this.mIsUnableToDrag && ((LayoutParams)view.getLayoutParams()).slideable;
        }
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        private static final int[] ATTRS;
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean slideable;
        public float weight;
        
        static {
            ATTRS = new int[] { 16843137 };
        }
        
        public LayoutParams() {
            super(-1, -1);
            this.weight = 0.0f;
        }
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.weight = 0.0f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.weight = 0.0f;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, LayoutParams.ATTRS);
            this.weight = obtainStyledAttributes.getFloat(0, 0.0f);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.weight = 0.0f;
            this.weight = layoutParams.weight;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.weight = 0.0f;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.weight = 0.0f;
        }
    }
    
    public interface PanelSlideListener
    {
        void onPanelClosed(final View p0);
        
        void onPanelOpened(final View p0);
        
        void onPanelSlide(final View p0, final float p1);
    }
    
    static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean isOpen;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel, final ClassLoader classLoader) {
            boolean isOpen = false;
            super(parcel, classLoader);
            if (parcel.readInt() != 0) {
                isOpen = true;
            }
            this.isOpen = isOpen;
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, int n) {
            final int n2 = 0;
            super.writeToParcel(parcel, n);
            if (!this.isOpen) {
                n = n2;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
        }
    }
    
    public static class SimplePanelSlideListener implements PanelSlideListener
    {
        @Override
        public void onPanelClosed(final View view) {
        }
        
        @Override
        public void onPanelOpened(final View view) {
        }
        
        @Override
        public void onPanelSlide(final View view, final float n) {
        }
    }
    
    interface SlidingPanelLayoutImpl
    {
        void invalidateChildRegion(final SlidingPaneLayout p0, final View p1);
    }
    
    static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl
    {
        @Override
        public void invalidateChildRegion(final SlidingPaneLayout slidingPaneLayout, final View view) {
            ViewCompat.postInvalidateOnAnimation((View)slidingPaneLayout, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        }
    }
    
    @RequiresApi(16)
    static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase
    {
        private Method mGetDisplayList;
        private Field mRecreateDisplayList;
        
        SlidingPanelLayoutImplJB() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     4: aload_0        
            //     5: ldc             Landroid/view/View;.class
            //     7: ldc             "getDisplayList"
            //     9: aconst_null    
            //    10: checkcast       [Ljava/lang/Class;
            //    13: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
            //    16: putfield        android/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB.mGetDisplayList:Ljava/lang/reflect/Method;
            //    19: aload_0        
            //    20: ldc             Landroid/view/View;.class
            //    22: ldc             "mRecreateDisplayList"
            //    24: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
            //    27: putfield        android/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB.mRecreateDisplayList:Ljava/lang/reflect/Field;
            //    30: aload_0        
            //    31: getfield        android/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB.mRecreateDisplayList:Ljava/lang/reflect/Field;
            //    34: iconst_1       
            //    35: invokevirtual   java/lang/reflect/Field.setAccessible:(Z)V
            //    38: return         
            //    39: astore_1       
            //    40: ldc             "SlidingPaneLayout"
            //    42: ldc             "Couldn't fetch getDisplayList method; dimming won't work right."
            //    44: aload_1        
            //    45: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //    48: pop            
            //    49: goto            19
            //    52: astore_1       
            //    53: ldc             "SlidingPaneLayout"
            //    55: ldc             "Couldn't fetch mRecreateDisplayList field; dimming will be slow."
            //    57: aload_1        
            //    58: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //    61: pop            
            //    62: goto            38
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                             
            //  -----  -----  -----  -----  ---------------------------------
            //  4      19     39     52     Ljava/lang/NoSuchMethodException;
            //  19     38     52     65     Ljava/lang/NoSuchFieldException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0019:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @Override
        public void invalidateChildRegion(final SlidingPaneLayout slidingPaneLayout, final View view) {
            if (this.mGetDisplayList == null || this.mRecreateDisplayList == null) {
                view.invalidate();
                return;
            }
            while (true) {
                try {
                    this.mRecreateDisplayList.setBoolean(view, true);
                    this.mGetDisplayList.invoke(view, (Object[])null);
                    super.invalidateChildRegion(slidingPaneLayout, view);
                }
                catch (final Exception ex) {
                    Log.e("SlidingPaneLayout", "Error refreshing display list state", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @RequiresApi(17)
    static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase
    {
        @Override
        public void invalidateChildRegion(final SlidingPaneLayout slidingPaneLayout, final View view) {
            ViewCompat.setLayerPaint(view, ((LayoutParams)view.getLayoutParams()).dimPaint);
        }
    }
}
