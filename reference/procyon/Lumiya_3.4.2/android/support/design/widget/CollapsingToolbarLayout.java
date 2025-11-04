// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.v4.math.MathUtils;
import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.annotation.DrawableRes;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable$Callback;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v4.util.ObjectsCompat;
import android.view.View$MeasureSpec;
import android.text.TextUtils;
import android.support.annotation.Nullable;
import android.graphics.Typeface;
import android.widget.FrameLayout$LayoutParams;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import android.support.annotation.NonNull;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.design.R;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.graphics.Rect;
import android.animation.ValueAnimator;
import android.support.v4.view.WindowInsetsCompat;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

public class CollapsingToolbarLayout extends FrameLayout
{
    private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;
    final CollapsingTextHelper mCollapsingTextHelper;
    private boolean mCollapsingTitleEnabled;
    private Drawable mContentScrim;
    int mCurrentOffset;
    private boolean mDrawCollapsingTitle;
    private View mDummyView;
    private int mExpandedMarginBottom;
    private int mExpandedMarginEnd;
    private int mExpandedMarginStart;
    private int mExpandedMarginTop;
    WindowInsetsCompat mLastInsets;
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
    private boolean mRefreshToolbar;
    private int mScrimAlpha;
    private long mScrimAnimationDuration;
    private ValueAnimator mScrimAnimator;
    private int mScrimVisibleHeightTrigger;
    private boolean mScrimsAreShown;
    Drawable mStatusBarScrim;
    private final Rect mTmpRect;
    private Toolbar mToolbar;
    private View mToolbarDirectChild;
    private int mToolbarId;
    
    public CollapsingToolbarLayout(final Context context) {
        this(context, null);
    }
    
    public CollapsingToolbarLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public CollapsingToolbarLayout(final Context context, final AttributeSet set, int dimensionPixelSize) {
        super(context, set, dimensionPixelSize);
        this.mRefreshToolbar = true;
        this.mTmpRect = new Rect();
        this.mScrimVisibleHeightTrigger = -1;
        ThemeUtils.checkAppCompatTheme(context);
        (this.mCollapsingTextHelper = new CollapsingTextHelper((View)this)).setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.CollapsingToolbarLayout, dimensionPixelSize, R.style.Widget_Design_CollapsingToolbar);
        this.mCollapsingTextHelper.setExpandedTextGravity(obtainStyledAttributes.getInt(R.styleable.CollapsingToolbarLayout_expandedTitleGravity, 8388691));
        this.mCollapsingTextHelper.setCollapsedTextGravity(obtainStyledAttributes.getInt(R.styleable.CollapsingToolbarLayout_collapsedTitleGravity, 8388627));
        dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);
        this.mExpandedMarginBottom = dimensionPixelSize;
        this.mExpandedMarginEnd = dimensionPixelSize;
        this.mExpandedMarginTop = dimensionPixelSize;
        this.mExpandedMarginStart = dimensionPixelSize;
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart)) {
            this.mExpandedMarginStart = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd)) {
            this.mExpandedMarginEnd = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop)) {
            this.mExpandedMarginTop = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom)) {
            this.mExpandedMarginBottom = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
        }
        this.mCollapsingTitleEnabled = obtainStyledAttributes.getBoolean(R.styleable.CollapsingToolbarLayout_titleEnabled, true);
        this.setTitle(obtainStyledAttributes.getText(R.styleable.CollapsingToolbarLayout_title));
        this.mCollapsingTextHelper.setExpandedTextAppearance(R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
        this.mCollapsingTextHelper.setCollapsedTextAppearance(android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance)) {
            this.mCollapsingTextHelper.setExpandedTextAppearance(obtainStyledAttributes.getResourceId(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, 0));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
            this.mCollapsingTextHelper.setCollapsedTextAppearance(obtainStyledAttributes.getResourceId(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, 0));
        }
        this.mScrimVisibleHeightTrigger = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_scrimVisibleHeightTrigger, -1);
        this.mScrimAnimationDuration = obtainStyledAttributes.getInt(R.styleable.CollapsingToolbarLayout_scrimAnimationDuration, 600);
        this.setContentScrim(obtainStyledAttributes.getDrawable(R.styleable.CollapsingToolbarLayout_contentScrim));
        this.setStatusBarScrim(obtainStyledAttributes.getDrawable(R.styleable.CollapsingToolbarLayout_statusBarScrim));
        this.mToolbarId = obtainStyledAttributes.getResourceId(R.styleable.CollapsingToolbarLayout_toolbarId, -1);
        obtainStyledAttributes.recycle();
        this.setWillNotDraw(false);
        ViewCompat.setOnApplyWindowInsetsListener((View)this, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat) {
                return CollapsingToolbarLayout.this.onWindowInsetChanged(windowInsetsCompat);
            }
        });
    }
    
    private void animateScrim(final int n) {
        this.ensureToolbar();
        if (this.mScrimAnimator != null) {
            if (this.mScrimAnimator.isRunning()) {
                this.mScrimAnimator.cancel();
            }
        }
        else {
            (this.mScrimAnimator = new ValueAnimator()).setDuration(this.mScrimAnimationDuration);
            final ValueAnimator mScrimAnimator = this.mScrimAnimator;
            Interpolator interpolator;
            if (n <= this.mScrimAlpha) {
                interpolator = AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR;
            }
            else {
                interpolator = AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR;
            }
            mScrimAnimator.setInterpolator((TimeInterpolator)interpolator);
            this.mScrimAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    CollapsingToolbarLayout.this.setScrimAlpha((int)valueAnimator.getAnimatedValue());
                }
            });
        }
        this.mScrimAnimator.setIntValues(new int[] { this.mScrimAlpha, n });
        this.mScrimAnimator.start();
    }
    
    private void ensureToolbar() {
        if (this.mRefreshToolbar) {
            this.mToolbar = null;
            this.mToolbarDirectChild = null;
            if (this.mToolbarId != -1) {
                this.mToolbar = (Toolbar)this.findViewById(this.mToolbarId);
                if (this.mToolbar != null) {
                    this.mToolbarDirectChild = this.findDirectChild((View)this.mToolbar);
                }
            }
            Label_0032: {
                if (this.mToolbar == null) {
                    while (true) {
                        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                            final View child = this.getChildAt(i);
                            if (child instanceof Toolbar) {
                                final Toolbar mToolbar = (Toolbar)child;
                                this.mToolbar = mToolbar;
                                break Label_0032;
                            }
                        }
                        final Toolbar mToolbar = null;
                        continue;
                    }
                }
            }
            this.updateDummyView();
            this.mRefreshToolbar = false;
        }
    }
    
    private View findDirectChild(final View view) {
        final ViewParent parent = view.getParent();
        View view2 = view;
        for (Object parent2 = parent; parent2 != this && parent2 != null; parent2 = ((ViewParent)parent2).getParent()) {
            if (parent2 instanceof View) {
                view2 = (View)parent2;
            }
        }
        return view2;
    }
    
    private static int getHeightWithMargins(@NonNull final View view) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof ViewGroup$MarginLayoutParams)) {
            return view.getHeight();
        }
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)layoutParams;
        return viewGroup$MarginLayoutParams.bottomMargin + (view.getHeight() + viewGroup$MarginLayoutParams.topMargin);
    }
    
    static ViewOffsetHelper getViewOffsetHelper(final View view) {
        final ViewOffsetHelper viewOffsetHelper = (ViewOffsetHelper)view.getTag(R.id.view_offset_helper);
        ViewOffsetHelper viewOffsetHelper2;
        if (viewOffsetHelper != null) {
            viewOffsetHelper2 = viewOffsetHelper;
        }
        else {
            final ViewOffsetHelper viewOffsetHelper3 = new ViewOffsetHelper(view);
            view.setTag(R.id.view_offset_helper, (Object)viewOffsetHelper3);
            viewOffsetHelper2 = viewOffsetHelper3;
        }
        return viewOffsetHelper2;
    }
    
    private boolean isToolbarChild(final View view) {
        boolean b = false;
        if (this.mToolbarDirectChild != null && this.mToolbarDirectChild != this) {
            if (view == this.mToolbarDirectChild) {
                return true;
            }
        }
        else if (view == this.mToolbar) {
            return true;
        }
        return b;
        b = true;
        return b;
    }
    
    private void updateDummyView() {
        if (!this.mCollapsingTitleEnabled && this.mDummyView != null) {
            final ViewParent parent = this.mDummyView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup)parent).removeView(this.mDummyView);
            }
        }
        if (this.mCollapsingTitleEnabled && this.mToolbar != null) {
            if (this.mDummyView == null) {
                this.mDummyView = new View(this.getContext());
            }
            if (this.mDummyView.getParent() == null) {
                this.mToolbar.addView(this.mDummyView, -1, -1);
            }
        }
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        this.ensureToolbar();
        if (this.mToolbar == null && this.mContentScrim != null && this.mScrimAlpha > 0) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
        }
        if (this.mCollapsingTitleEnabled && this.mDrawCollapsingTitle) {
            this.mCollapsingTextHelper.draw(canvas);
        }
        if (this.mStatusBarScrim != null && this.mScrimAlpha > 0) {
            int systemWindowInsetTop;
            if (this.mLastInsets == null) {
                systemWindowInsetTop = 0;
            }
            else {
                systemWindowInsetTop = this.mLastInsets.getSystemWindowInsetTop();
            }
            if (systemWindowInsetTop > 0) {
                this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, this.getWidth(), systemWindowInsetTop - this.mCurrentOffset);
                this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
                this.mStatusBarScrim.draw(canvas);
            }
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        boolean b = false;
        boolean b2;
        if (this.mContentScrim != null && this.mScrimAlpha > 0 && this.isToolbarChild(view)) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (super.drawChild(canvas, view, n) || b2) {
            b = true;
        }
        return b;
    }
    
    protected void drawableStateChanged() {
        int n = 0;
        super.drawableStateChanged();
        final int[] drawableState = this.getDrawableState();
        final Drawable mStatusBarScrim = this.mStatusBarScrim;
        if (mStatusBarScrim != null && mStatusBarScrim.isStateful()) {
            n = ((mStatusBarScrim.setState(drawableState) | false) ? 1 : 0);
        }
        final Drawable mContentScrim = this.mContentScrim;
        int n2;
        if (mContentScrim == null) {
            n2 = n;
        }
        else {
            n2 = n;
            if (mContentScrim.isStateful()) {
                n2 = (n | (mContentScrim.setState(drawableState) ? 1 : 0));
            }
        }
        if (this.mCollapsingTextHelper != null) {
            n2 |= (this.mCollapsingTextHelper.setState(drawableState) ? 1 : 0);
        }
        if (n2 != 0) {
            this.invalidate();
        }
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }
    
    public FrameLayout$LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected FrameLayout$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    public int getCollapsedTitleGravity() {
        return this.mCollapsingTextHelper.getCollapsedTextGravity();
    }
    
    @NonNull
    public Typeface getCollapsedTitleTypeface() {
        return this.mCollapsingTextHelper.getCollapsedTypeface();
    }
    
    @Nullable
    public Drawable getContentScrim() {
        return this.mContentScrim;
    }
    
    public int getExpandedTitleGravity() {
        return this.mCollapsingTextHelper.getExpandedTextGravity();
    }
    
    public int getExpandedTitleMarginBottom() {
        return this.mExpandedMarginBottom;
    }
    
    public int getExpandedTitleMarginEnd() {
        return this.mExpandedMarginEnd;
    }
    
    public int getExpandedTitleMarginStart() {
        return this.mExpandedMarginStart;
    }
    
    public int getExpandedTitleMarginTop() {
        return this.mExpandedMarginTop;
    }
    
    @NonNull
    public Typeface getExpandedTitleTypeface() {
        return this.mCollapsingTextHelper.getExpandedTypeface();
    }
    
    final int getMaxOffsetForPinChild(final View view) {
        return this.getHeight() - getViewOffsetHelper(view).getLayoutTop() - view.getHeight() - ((LayoutParams)view.getLayoutParams()).bottomMargin;
    }
    
    int getScrimAlpha() {
        return this.mScrimAlpha;
    }
    
    public long getScrimAnimationDuration() {
        return this.mScrimAnimationDuration;
    }
    
    public int getScrimVisibleHeightTrigger() {
        int systemWindowInsetTop = 0;
        if (this.mScrimVisibleHeightTrigger >= 0) {
            return this.mScrimVisibleHeightTrigger;
        }
        if (this.mLastInsets != null) {
            systemWindowInsetTop = this.mLastInsets.getSystemWindowInsetTop();
        }
        final int minimumHeight = ViewCompat.getMinimumHeight((View)this);
        if (minimumHeight <= 0) {
            return this.getHeight() / 3;
        }
        return Math.min(systemWindowInsetTop + minimumHeight * 2, this.getHeight());
    }
    
    @Nullable
    public Drawable getStatusBarScrim() {
        return this.mStatusBarScrim;
    }
    
    @Nullable
    public CharSequence getTitle() {
        CharSequence text;
        if (!this.mCollapsingTitleEnabled) {
            text = null;
        }
        else {
            text = this.mCollapsingTextHelper.getText();
        }
        return text;
    }
    
    public boolean isTitleEnabled() {
        return this.mCollapsingTitleEnabled;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = this.getParent();
        if (parent instanceof AppBarLayout) {
            ViewCompat.setFitsSystemWindows((View)this, ViewCompat.getFitsSystemWindows((View)parent));
            if (this.mOnOffsetChangedListener == null) {
                this.mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout)parent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
            ViewCompat.requestApplyInsets((View)this);
        }
    }
    
    protected void onDetachedFromWindow() {
        final ViewParent parent = this.getParent();
        if (this.mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout)parent).removeOnOffsetChangedListener(this.mOnOffsetChangedListener);
        }
        super.onDetachedFromWindow();
    }
    
    protected void onLayout(final boolean b, int i, int childCount, final int n, final int n2) {
        final int n3 = 1;
        final int n4 = 0;
        super.onLayout(b, i, childCount, n, n2);
        if (this.mLastInsets != null) {
            final int systemWindowInsetTop = this.mLastInsets.getSystemWindowInsetTop();
            for (int childCount2 = this.getChildCount(), j = 0; j < childCount2; ++j) {
                final View child = this.getChildAt(j);
                if (!ViewCompat.getFitsSystemWindows(child) && child.getTop() < systemWindowInsetTop) {
                    ViewCompat.offsetTopAndBottom(child, systemWindowInsetTop);
                }
            }
        }
        if (this.mCollapsingTitleEnabled && this.mDummyView != null) {
            this.mDrawCollapsingTitle = (ViewCompat.isAttachedToWindow(this.mDummyView) && this.mDummyView.getVisibility() == 0);
            if (this.mDrawCollapsingTitle) {
                int n5 = n3;
                if (ViewCompat.getLayoutDirection((View)this) != 1) {
                    n5 = 0;
                }
                Object o;
                if (this.mToolbarDirectChild == null) {
                    o = this.mToolbar;
                }
                else {
                    o = this.mToolbarDirectChild;
                }
                final int maxOffsetForPinChild = this.getMaxOffsetForPinChild((View)o);
                ViewGroupUtils.getDescendantRect((ViewGroup)this, this.mDummyView, this.mTmpRect);
                final CollapsingTextHelper mCollapsingTextHelper = this.mCollapsingTextHelper;
                final int left = this.mTmpRect.left;
                int n6;
                if (n5 == 0) {
                    n6 = this.mToolbar.getTitleMarginStart();
                }
                else {
                    n6 = this.mToolbar.getTitleMarginEnd();
                }
                final int top = this.mTmpRect.top;
                final int titleMarginTop = this.mToolbar.getTitleMarginTop();
                final int right = this.mTmpRect.right;
                int n7;
                if (n5 == 0) {
                    n7 = this.mToolbar.getTitleMarginEnd();
                }
                else {
                    n7 = this.mToolbar.getTitleMarginStart();
                }
                mCollapsingTextHelper.setCollapsedBounds(left + n6, titleMarginTop + (top + maxOffsetForPinChild), n7 + right, maxOffsetForPinChild + this.mTmpRect.bottom - this.mToolbar.getTitleMarginBottom());
                final CollapsingTextHelper mCollapsingTextHelper2 = this.mCollapsingTextHelper;
                int n8;
                if (n5 == 0) {
                    n8 = this.mExpandedMarginStart;
                }
                else {
                    n8 = this.mExpandedMarginEnd;
                }
                final int top2 = this.mTmpRect.top;
                final int mExpandedMarginTop = this.mExpandedMarginTop;
                int n9;
                if (n5 == 0) {
                    n9 = this.mExpandedMarginEnd;
                }
                else {
                    n9 = this.mExpandedMarginStart;
                }
                mCollapsingTextHelper2.setExpandedBounds(n8, top2 + mExpandedMarginTop, n - i - n9, n2 - childCount - this.mExpandedMarginBottom);
                this.mCollapsingTextHelper.recalculate();
            }
        }
        for (childCount = this.getChildCount(), i = n4; i < childCount; ++i) {
            getViewOffsetHelper(this.getChildAt(i)).onViewLayout();
        }
        if (this.mToolbar != null) {
            if (this.mCollapsingTitleEnabled && TextUtils.isEmpty(this.mCollapsingTextHelper.getText())) {
                this.mCollapsingTextHelper.setText(this.mToolbar.getTitle());
            }
            if (this.mToolbarDirectChild != null && this.mToolbarDirectChild != this) {
                this.setMinimumHeight(getHeightWithMargins(this.mToolbarDirectChild));
            }
            else {
                this.setMinimumHeight(getHeightWithMargins((View)this.mToolbar));
            }
        }
        this.updateScrimVisibility();
    }
    
    protected void onMeasure(final int n, int systemWindowInsetTop) {
        final int n2 = 0;
        this.ensureToolbar();
        super.onMeasure(n, systemWindowInsetTop);
        final int mode = View$MeasureSpec.getMode(systemWindowInsetTop);
        if (this.mLastInsets == null) {
            systemWindowInsetTop = n2;
        }
        else {
            systemWindowInsetTop = this.mLastInsets.getSystemWindowInsetTop();
        }
        if (mode == 0 && systemWindowInsetTop > 0) {
            super.onMeasure(n, View$MeasureSpec.makeMeasureSpec(systemWindowInsetTop + this.getMeasuredHeight(), 1073741824));
        }
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (this.mContentScrim != null) {
            this.mContentScrim.setBounds(0, 0, n, n2);
        }
    }
    
    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat windowInsetsCompat) {
        WindowInsetsCompat mLastInsets = null;
        if (ViewCompat.getFitsSystemWindows((View)this)) {
            mLastInsets = windowInsetsCompat;
        }
        if (!ObjectsCompat.equals(this.mLastInsets, mLastInsets)) {
            this.mLastInsets = mLastInsets;
            this.requestLayout();
        }
        return windowInsetsCompat.consumeSystemWindowInsets();
    }
    
    public void setCollapsedTitleGravity(final int collapsedTextGravity) {
        this.mCollapsingTextHelper.setCollapsedTextGravity(collapsedTextGravity);
    }
    
    public void setCollapsedTitleTextAppearance(@StyleRes final int collapsedTextAppearance) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(collapsedTextAppearance);
    }
    
    public void setCollapsedTitleTextColor(@ColorInt final int n) {
        this.setCollapsedTitleTextColor(ColorStateList.valueOf(n));
    }
    
    public void setCollapsedTitleTextColor(@NonNull final ColorStateList collapsedTextColor) {
        this.mCollapsingTextHelper.setCollapsedTextColor(collapsedTextColor);
    }
    
    public void setCollapsedTitleTypeface(@Nullable final Typeface collapsedTypeface) {
        this.mCollapsingTextHelper.setCollapsedTypeface(collapsedTypeface);
    }
    
    public void setContentScrim(@Nullable Drawable mutate) {
        final Drawable drawable = null;
        if (this.mContentScrim != mutate) {
            if (this.mContentScrim != null) {
                this.mContentScrim.setCallback((Drawable$Callback)null);
            }
            if (mutate == null) {
                mutate = drawable;
            }
            else {
                mutate = mutate.mutate();
            }
            this.mContentScrim = mutate;
            if (this.mContentScrim != null) {
                this.mContentScrim.setBounds(0, 0, this.getWidth(), this.getHeight());
                this.mContentScrim.setCallback((Drawable$Callback)this);
                this.mContentScrim.setAlpha(this.mScrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public void setContentScrimColor(@ColorInt final int n) {
        this.setContentScrim((Drawable)new ColorDrawable(n));
    }
    
    public void setContentScrimResource(@DrawableRes final int n) {
        this.setContentScrim(ContextCompat.getDrawable(this.getContext(), n));
    }
    
    public void setExpandedTitleColor(@ColorInt final int n) {
        this.setExpandedTitleTextColor(ColorStateList.valueOf(n));
    }
    
    public void setExpandedTitleGravity(final int expandedTextGravity) {
        this.mCollapsingTextHelper.setExpandedTextGravity(expandedTextGravity);
    }
    
    public void setExpandedTitleMargin(final int mExpandedMarginStart, final int mExpandedMarginTop, final int mExpandedMarginEnd, final int mExpandedMarginBottom) {
        this.mExpandedMarginStart = mExpandedMarginStart;
        this.mExpandedMarginTop = mExpandedMarginTop;
        this.mExpandedMarginEnd = mExpandedMarginEnd;
        this.mExpandedMarginBottom = mExpandedMarginBottom;
        this.requestLayout();
    }
    
    public void setExpandedTitleMarginBottom(final int mExpandedMarginBottom) {
        this.mExpandedMarginBottom = mExpandedMarginBottom;
        this.requestLayout();
    }
    
    public void setExpandedTitleMarginEnd(final int mExpandedMarginEnd) {
        this.mExpandedMarginEnd = mExpandedMarginEnd;
        this.requestLayout();
    }
    
    public void setExpandedTitleMarginStart(final int mExpandedMarginStart) {
        this.mExpandedMarginStart = mExpandedMarginStart;
        this.requestLayout();
    }
    
    public void setExpandedTitleMarginTop(final int mExpandedMarginTop) {
        this.mExpandedMarginTop = mExpandedMarginTop;
        this.requestLayout();
    }
    
    public void setExpandedTitleTextAppearance(@StyleRes final int expandedTextAppearance) {
        this.mCollapsingTextHelper.setExpandedTextAppearance(expandedTextAppearance);
    }
    
    public void setExpandedTitleTextColor(@NonNull final ColorStateList expandedTextColor) {
        this.mCollapsingTextHelper.setExpandedTextColor(expandedTextColor);
    }
    
    public void setExpandedTitleTypeface(@Nullable final Typeface expandedTypeface) {
        this.mCollapsingTextHelper.setExpandedTypeface(expandedTypeface);
    }
    
    void setScrimAlpha(final int mScrimAlpha) {
        if (mScrimAlpha != this.mScrimAlpha) {
            if (this.mContentScrim != null && this.mToolbar != null) {
                ViewCompat.postInvalidateOnAnimation((View)this.mToolbar);
            }
            this.mScrimAlpha = mScrimAlpha;
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public void setScrimAnimationDuration(@IntRange(from = 0L) final long mScrimAnimationDuration) {
        this.mScrimAnimationDuration = mScrimAnimationDuration;
    }
    
    public void setScrimVisibleHeightTrigger(@IntRange(from = 0L) final int mScrimVisibleHeightTrigger) {
        if (this.mScrimVisibleHeightTrigger != mScrimVisibleHeightTrigger) {
            this.mScrimVisibleHeightTrigger = mScrimVisibleHeightTrigger;
            this.updateScrimVisibility();
        }
    }
    
    public void setScrimsShown(final boolean b) {
        boolean b2 = false;
        if (ViewCompat.isLaidOut((View)this) && !this.isInEditMode()) {
            b2 = true;
        }
        this.setScrimsShown(b, b2);
    }
    
    public void setScrimsShown(final boolean mScrimsAreShown, final boolean b) {
        final int n = 0;
        int scrimAlpha = 0;
        if (this.mScrimsAreShown != mScrimsAreShown) {
            if (!b) {
                if (mScrimsAreShown) {
                    scrimAlpha = 255;
                }
                this.setScrimAlpha(scrimAlpha);
            }
            else {
                int n2;
                if (!mScrimsAreShown) {
                    n2 = n;
                }
                else {
                    n2 = 255;
                }
                this.animateScrim(n2);
            }
            this.mScrimsAreShown = mScrimsAreShown;
        }
    }
    
    public void setStatusBarScrim(@Nullable Drawable mStatusBarScrim) {
        final Drawable drawable = null;
        if (this.mStatusBarScrim != mStatusBarScrim) {
            if (this.mStatusBarScrim != null) {
                this.mStatusBarScrim.setCallback((Drawable$Callback)null);
            }
            if (mStatusBarScrim == null) {
                mStatusBarScrim = drawable;
            }
            else {
                mStatusBarScrim = mStatusBarScrim.mutate();
            }
            this.mStatusBarScrim = mStatusBarScrim;
            if (this.mStatusBarScrim != null) {
                if (this.mStatusBarScrim.isStateful()) {
                    this.mStatusBarScrim.setState(this.getDrawableState());
                }
                DrawableCompat.setLayoutDirection(this.mStatusBarScrim, ViewCompat.getLayoutDirection((View)this));
                mStatusBarScrim = this.mStatusBarScrim;
                mStatusBarScrim.setVisible(this.getVisibility() == 0, false);
                this.mStatusBarScrim.setCallback((Drawable$Callback)this);
                this.mStatusBarScrim.setAlpha(this.mScrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public void setStatusBarScrimColor(@ColorInt final int n) {
        this.setStatusBarScrim((Drawable)new ColorDrawable(n));
    }
    
    public void setStatusBarScrimResource(@DrawableRes final int n) {
        this.setStatusBarScrim(ContextCompat.getDrawable(this.getContext(), n));
    }
    
    public void setTitle(@Nullable final CharSequence text) {
        this.mCollapsingTextHelper.setText(text);
    }
    
    public void setTitleEnabled(final boolean mCollapsingTitleEnabled) {
        if (mCollapsingTitleEnabled != this.mCollapsingTitleEnabled) {
            this.mCollapsingTitleEnabled = mCollapsingTitleEnabled;
            this.updateDummyView();
            this.requestLayout();
        }
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final boolean b = visibility == 0;
        if (this.mStatusBarScrim != null && this.mStatusBarScrim.isVisible() != b) {
            this.mStatusBarScrim.setVisible(b, false);
        }
        if (this.mContentScrim != null && this.mContentScrim.isVisible() != b) {
            this.mContentScrim.setVisible(b, false);
        }
    }
    
    final void updateScrimVisibility() {
        if (this.mContentScrim != null || this.mStatusBarScrim != null) {
            this.setScrimsShown(this.getHeight() + this.mCurrentOffset < this.getScrimVisibleHeightTrigger());
        }
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        boolean b = false;
        if (super.verifyDrawable(drawable) || drawable == this.mContentScrim || drawable == this.mStatusBarScrim) {
            b = true;
        }
        return b;
    }
    
    public static class LayoutParams extends FrameLayout$LayoutParams
    {
        public static final int COLLAPSE_MODE_OFF = 0;
        public static final int COLLAPSE_MODE_PARALLAX = 2;
        public static final int COLLAPSE_MODE_PIN = 1;
        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;
        int mCollapseMode;
        float mParallaxMult;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mCollapseMode = 0;
            this.mParallaxMult = 0.5f;
        }
        
        public LayoutParams(final int n, final int n2, final int n3) {
            super(n, n2, n3);
            this.mCollapseMode = 0;
            this.mParallaxMult = 0.5f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.mCollapseMode = 0;
            this.mParallaxMult = 0.5f;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.CollapsingToolbarLayout_Layout);
            this.mCollapseMode = obtainStyledAttributes.getInt(R.styleable.CollapsingToolbarLayout_Layout_layout_collapseMode, 0);
            this.setParallaxMultiplier(obtainStyledAttributes.getFloat(R.styleable.CollapsingToolbarLayout_Layout_layout_collapseParallaxMultiplier, 0.5f));
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mCollapseMode = 0;
            this.mParallaxMult = 0.5f;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.mCollapseMode = 0;
            this.mParallaxMult = 0.5f;
        }
        
        @RequiresApi(19)
        public LayoutParams(final FrameLayout$LayoutParams frameLayout$LayoutParams) {
            super(frameLayout$LayoutParams);
            this.mCollapseMode = 0;
            this.mParallaxMult = 0.5f;
        }
        
        public int getCollapseMode() {
            return this.mCollapseMode;
        }
        
        public float getParallaxMultiplier() {
            return this.mParallaxMult;
        }
        
        public void setCollapseMode(final int mCollapseMode) {
            this.mCollapseMode = mCollapseMode;
        }
        
        public void setParallaxMultiplier(final float mParallaxMult) {
            this.mParallaxMult = mParallaxMult;
        }
        
        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @interface CollapseMode {
        }
    }
    
    private class OffsetUpdateListener implements OnOffsetChangedListener
    {
        OffsetUpdateListener() {
        }
        
        @Override
        public void onOffsetChanged(final AppBarLayout appBarLayout, final int n) {
            CollapsingToolbarLayout.this.mCurrentOffset = n;
            int systemWindowInsetTop;
            if (CollapsingToolbarLayout.this.mLastInsets == null) {
                systemWindowInsetTop = 0;
            }
            else {
                systemWindowInsetTop = CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop();
            }
            for (int childCount = CollapsingToolbarLayout.this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = CollapsingToolbarLayout.this.getChildAt(i);
                final CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams)child.getLayoutParams();
                final ViewOffsetHelper viewOffsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(child);
                switch (layoutParams.mCollapseMode) {
                    case 1: {
                        viewOffsetHelper.setTopAndBottomOffset(MathUtils.clamp(-n, 0, CollapsingToolbarLayout.this.getMaxOffsetForPinChild(child)));
                        break;
                    }
                    case 2: {
                        viewOffsetHelper.setTopAndBottomOffset(Math.round(layoutParams.mParallaxMult * -n));
                        break;
                    }
                }
            }
            CollapsingToolbarLayout.this.updateScrimVisibility();
            if (CollapsingToolbarLayout.this.mStatusBarScrim != null && systemWindowInsetTop > 0) {
                ViewCompat.postInvalidateOnAnimation((View)CollapsingToolbarLayout.this);
            }
            CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(Math.abs(n) / (float)(CollapsingToolbarLayout.this.getHeight() - ViewCompat.getMinimumHeight((View)CollapsingToolbarLayout.this) - systemWindowInsetTop));
        }
    }
}
