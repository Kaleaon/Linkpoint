// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.v4.view.AbsSavedState;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.CollapsibleActionView;
import android.support.annotation.ColorInt;
import android.text.TextUtils$TruncateAt;
import android.view.ContextThemeWrapper;
import android.support.annotation.StyleRes;
import android.support.annotation.StringRes;
import android.support.v7.content.res.AppCompatResources;
import android.support.annotation.DrawableRes;
import android.os.Build$VERSION;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.text.Layout;
import android.support.annotation.MenuRes;
import android.view.Menu;
import android.support.v7.app.ActionBar;
import android.view.View$OnClickListener;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.annotation.RestrictTo;
import android.view.View$MeasureSpec;
import android.support.v7.view.SupportMenuInflater;
import android.view.MenuInflater;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import java.util.List;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v7.appcompat.R;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.widget.ImageView;
import java.util.ArrayList;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.support.v7.view.menu.MenuPresenter;
import android.view.ViewGroup;

public class Toolbar extends ViewGroup
{
    private static final String TAG = "Toolbar";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    int mButtonGravity;
    ImageButton mCollapseButtonView;
    private CharSequence mCollapseDescription;
    private Drawable mCollapseIcon;
    private boolean mCollapsible;
    private int mContentInsetEndWithActions;
    private int mContentInsetStartWithNavigation;
    private RtlSpacingHelper mContentInsets;
    private boolean mEatingHover;
    private boolean mEatingTouch;
    View mExpandedActionView;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private int mGravity;
    private final ArrayList<View> mHiddenViews;
    private ImageView mLogoView;
    private int mMaxButtonHeight;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private ActionMenuView mMenuView;
    private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener;
    private ImageButton mNavButtonView;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private ActionMenuPresenter mOuterActionMenuPresenter;
    private Context mPopupContext;
    private int mPopupTheme;
    private final Runnable mShowOverflowMenuRunnable;
    private CharSequence mSubtitleText;
    private int mSubtitleTextAppearance;
    private int mSubtitleTextColor;
    private TextView mSubtitleTextView;
    private final int[] mTempMargins;
    private final ArrayList<View> mTempViews;
    private int mTitleMarginBottom;
    private int mTitleMarginEnd;
    private int mTitleMarginStart;
    private int mTitleMarginTop;
    private CharSequence mTitleText;
    private int mTitleTextAppearance;
    private int mTitleTextColor;
    private TextView mTitleTextView;
    private ToolbarWidgetWrapper mWrapper;
    
    public Toolbar(final Context context) {
        this(context, null);
    }
    
    public Toolbar(final Context context, @Nullable final AttributeSet set) {
        this(context, set, R.attr.toolbarStyle);
    }
    
    public Toolbar(final Context context, @Nullable final AttributeSet set, int n) {
        super(context, set, n);
        this.mGravity = 8388627;
        this.mTempViews = new ArrayList<View>();
        this.mHiddenViews = new ArrayList<View>();
        this.mTempMargins = new int[2];
        this.mMenuViewItemClickListener = new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {
                return Toolbar.this.mOnMenuItemClickListener != null && Toolbar.this.mOnMenuItemClickListener.onMenuItemClick(menuItem);
            }
        };
        this.mShowOverflowMenuRunnable = new Runnable() {
            @Override
            public void run() {
                Toolbar.this.showOverflowMenu();
            }
        };
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.getContext(), set, R.styleable.Toolbar, n, 0);
        this.mTitleTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        this.mSubtitleTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
        this.mGravity = obtainStyledAttributes.getInteger(R.styleable.Toolbar_android_gravity, this.mGravity);
        this.mButtonGravity = obtainStyledAttributes.getInteger(R.styleable.Toolbar_buttonGravity, 48);
        n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMargin, 0);
        if (obtainStyledAttributes.hasValue(R.styleable.Toolbar_titleMargins)) {
            n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, n);
        }
        this.mTitleMarginBottom = n;
        this.mTitleMarginTop = n;
        this.mTitleMarginEnd = n;
        this.mTitleMarginStart = n;
        n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
        if (n >= 0) {
            this.mTitleMarginStart = n;
        }
        n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
        if (n >= 0) {
            this.mTitleMarginEnd = n;
        }
        n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
        if (n >= 0) {
            this.mTitleMarginTop = n;
        }
        n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, -1);
        if (n >= 0) {
            this.mTitleMarginBottom = n;
        }
        this.mMaxButtonHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);
        final int dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, Integer.MIN_VALUE);
        n = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, Integer.MIN_VALUE);
        final int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0);
        final int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0);
        this.ensureContentInsets();
        this.mContentInsets.setAbsolute(dimensionPixelSize, dimensionPixelSize2);
        if (dimensionPixelOffset != Integer.MIN_VALUE || n != Integer.MIN_VALUE) {
            this.mContentInsets.setRelative(dimensionPixelOffset, n);
        }
        this.mContentInsetStartWithNavigation = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStartWithNavigation, Integer.MIN_VALUE);
        this.mContentInsetEndWithActions = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEndWithActions, Integer.MIN_VALUE);
        this.mCollapseIcon = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_collapseIcon);
        this.mCollapseDescription = obtainStyledAttributes.getText(R.styleable.Toolbar_collapseContentDescription);
        final CharSequence text = obtainStyledAttributes.getText(R.styleable.Toolbar_title);
        if (!TextUtils.isEmpty(text)) {
            this.setTitle(text);
        }
        final CharSequence text2 = obtainStyledAttributes.getText(R.styleable.Toolbar_subtitle);
        if (!TextUtils.isEmpty(text2)) {
            this.setSubtitle(text2);
        }
        this.mPopupContext = this.getContext();
        this.setPopupTheme(obtainStyledAttributes.getResourceId(R.styleable.Toolbar_popupTheme, 0));
        final Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_navigationIcon);
        if (drawable != null) {
            this.setNavigationIcon(drawable);
        }
        final CharSequence text3 = obtainStyledAttributes.getText(R.styleable.Toolbar_navigationContentDescription);
        if (!TextUtils.isEmpty(text3)) {
            this.setNavigationContentDescription(text3);
        }
        final Drawable drawable2 = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_logo);
        if (drawable2 != null) {
            this.setLogo(drawable2);
        }
        final CharSequence text4 = obtainStyledAttributes.getText(R.styleable.Toolbar_logoDescription);
        if (!TextUtils.isEmpty(text4)) {
            this.setLogoDescription(text4);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Toolbar_titleTextColor)) {
            this.setTitleTextColor(obtainStyledAttributes.getColor(R.styleable.Toolbar_titleTextColor, -1));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
            this.setSubtitleTextColor(obtainStyledAttributes.getColor(R.styleable.Toolbar_subtitleTextColor, -1));
        }
        obtainStyledAttributes.recycle();
    }
    
    private void addCustomViewsWithGravity(final List<View> list, int i) {
        boolean b = true;
        final int n = 0;
        if (ViewCompat.getLayoutDirection((View)this) != 1) {
            b = false;
        }
        final int childCount = this.getChildCount();
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection((View)this));
        list.clear();
        if (!b) {
            View child;
            LayoutParams layoutParams;
            for (i = n; i < childCount; ++i) {
                child = this.getChildAt(i);
                layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.mViewType == 0 && this.shouldLayout(child) && this.getChildHorizontalGravity(layoutParams.gravity) == absoluteGravity) {
                    list.add(child);
                }
            }
        }
        else {
            View child2;
            LayoutParams layoutParams2;
            for (i = childCount - 1; i >= 0; --i) {
                child2 = this.getChildAt(i);
                layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (layoutParams2.mViewType == 0 && this.shouldLayout(child2) && this.getChildHorizontalGravity(layoutParams2.gravity) == absoluteGravity) {
                    list.add(child2);
                }
            }
        }
    }
    
    private void addSystemView(final View e, final boolean b) {
        final ViewGroup$LayoutParams layoutParams = e.getLayoutParams();
        LayoutParams layoutParams2;
        if (layoutParams != null) {
            if (this.checkLayoutParams(layoutParams)) {
                layoutParams2 = (LayoutParams)layoutParams;
            }
            else {
                layoutParams2 = this.generateLayoutParams(layoutParams);
            }
        }
        else {
            layoutParams2 = this.generateDefaultLayoutParams();
        }
        layoutParams2.mViewType = 1;
        if (b && this.mExpandedActionView != null) {
            e.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
            this.mHiddenViews.add(e);
        }
        else {
            this.addView(e, (ViewGroup$LayoutParams)layoutParams2);
        }
    }
    
    private void ensureContentInsets() {
        if (this.mContentInsets == null) {
            this.mContentInsets = new RtlSpacingHelper();
        }
    }
    
    private void ensureLogoView() {
        if (this.mLogoView == null) {
            this.mLogoView = new AppCompatImageView(this.getContext());
        }
    }
    
    private void ensureMenu() {
        this.ensureMenuView();
        if (this.mMenuView.peekMenu() == null) {
            final MenuBuilder menuBuilder = (MenuBuilder)this.mMenuView.getMenu();
            if (this.mExpandedMenuPresenter == null) {
                this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            this.mMenuView.setExpandedActionViewsExclusive(true);
            menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
        }
    }
    
    private void ensureMenuView() {
        if (this.mMenuView == null) {
            (this.mMenuView = new ActionMenuView(this.getContext())).setPopupTheme(this.mPopupTheme);
            this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
            this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
            final LayoutParams generateDefaultLayoutParams = this.generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = ((this.mButtonGravity & 0x70) | 0x800005);
            this.mMenuView.setLayoutParams((ViewGroup$LayoutParams)generateDefaultLayoutParams);
            this.addSystemView((View)this.mMenuView, false);
        }
    }
    
    private void ensureNavButtonView() {
        if (this.mNavButtonView == null) {
            this.mNavButtonView = new AppCompatImageButton(this.getContext(), null, R.attr.toolbarNavigationButtonStyle);
            final LayoutParams generateDefaultLayoutParams = this.generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = ((this.mButtonGravity & 0x70) | 0x800003);
            this.mNavButtonView.setLayoutParams((ViewGroup$LayoutParams)generateDefaultLayoutParams);
        }
    }
    
    private int getChildHorizontalGravity(int n) {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        n = (GravityCompat.getAbsoluteGravity(n, layoutDirection) & 0x7);
        switch (n) {
            default: {
                if (layoutDirection != 1) {
                    n = 3;
                }
                else {
                    n = 5;
                }
                return n;
            }
            case 1:
            case 3:
            case 5: {
                return n;
            }
        }
    }
    
    private int getChildTop(final View view, int n) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int measuredHeight = view.getMeasuredHeight();
        if (n <= 0) {
            n = 0;
        }
        else {
            n = (measuredHeight - n) / 2;
        }
        switch (this.getChildVerticalGravity(layoutParams.gravity)) {
            default: {
                final int paddingTop = this.getPaddingTop();
                final int paddingBottom = this.getPaddingBottom();
                final int height = this.getHeight();
                n = (height - paddingTop - paddingBottom - measuredHeight) / 2;
                if (n >= layoutParams.topMargin) {
                    final int n2 = height - paddingBottom - measuredHeight - n - paddingTop;
                    if (n2 < layoutParams.bottomMargin) {
                        n = Math.max(0, n - (layoutParams.bottomMargin - n2));
                    }
                }
                else {
                    n = layoutParams.topMargin;
                }
                return n + paddingTop;
            }
            case 48: {
                return this.getPaddingTop() - n;
            }
            case 80: {
                return this.getHeight() - this.getPaddingBottom() - measuredHeight - layoutParams.bottomMargin - n;
            }
        }
    }
    
    private int getChildVerticalGravity(int n) {
        n &= 0x70;
        switch (n) {
            default: {
                return this.mGravity & 0x70;
            }
            case 16:
            case 48:
            case 80: {
                return n;
            }
        }
    }
    
    private int getHorizontalMargins(final View view) {
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)view.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginEnd(viewGroup$MarginLayoutParams) + MarginLayoutParamsCompat.getMarginStart(viewGroup$MarginLayoutParams);
    }
    
    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.getContext());
    }
    
    private int getVerticalMargins(final View view) {
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)view.getLayoutParams();
        return viewGroup$MarginLayoutParams.bottomMargin + viewGroup$MarginLayoutParams.topMargin;
    }
    
    private int getViewListMeasuredWidth(final List<View> list, final int[] array) {
        int max = array[0];
        int max2 = array[1];
        int size;
        int i;
        int n;
        int max3;
        int max4;
        int measuredWidth;
        for (size = list.size(), i = 0, n = 0; i < size; ++i, n += measuredWidth + max3 + max4) {
            final View view = list.get(i);
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final int b = layoutParams.leftMargin - max;
            final int b2 = layoutParams.rightMargin - max2;
            max3 = Math.max(0, b);
            max4 = Math.max(0, b2);
            max = Math.max(0, -b);
            max2 = Math.max(0, -b2);
            measuredWidth = view.getMeasuredWidth();
        }
        return n;
    }
    
    private boolean isChildOrHidden(final View o) {
        boolean b = false;
        if (o.getParent() == this || this.mHiddenViews.contains(o)) {
            b = true;
        }
        return b;
    }
    
    private static boolean isCustomView(final View view) {
        return ((LayoutParams)view.getLayoutParams()).mViewType == 0;
    }
    
    private int layoutChildLeft(final View view, int n, final int[] array, int measuredWidth) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int b = layoutParams.leftMargin - array[0];
        n += Math.max(0, b);
        array[0] = Math.max(0, -b);
        final int childTop = this.getChildTop(view, measuredWidth);
        measuredWidth = view.getMeasuredWidth();
        view.layout(n, childTop, n + measuredWidth, view.getMeasuredHeight() + childTop);
        return layoutParams.rightMargin + measuredWidth + n;
    }
    
    private int layoutChildRight(final View view, int n, final int[] array, int childTop) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int b = layoutParams.rightMargin - array[1];
        n -= Math.max(0, b);
        array[1] = Math.max(0, -b);
        childTop = this.getChildTop(view, childTop);
        final int measuredWidth = view.getMeasuredWidth();
        view.layout(n - measuredWidth, childTop, n, view.getMeasuredHeight() + childTop);
        return n - (layoutParams.leftMargin + measuredWidth);
    }
    
    private int measureChildCollapseMargins(final View view, final int n, final int n2, final int n3, final int n4, final int[] array) {
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)view.getLayoutParams();
        final int b = viewGroup$MarginLayoutParams.leftMargin - array[0];
        final int b2 = viewGroup$MarginLayoutParams.rightMargin - array[1];
        final int n5 = Math.max(0, b) + Math.max(0, b2);
        array[0] = Math.max(0, -b);
        array[1] = Math.max(0, -b2);
        view.measure(getChildMeasureSpec(n, this.getPaddingLeft() + this.getPaddingRight() + n5 + n2, viewGroup$MarginLayoutParams.width), getChildMeasureSpec(n3, this.getPaddingTop() + this.getPaddingBottom() + viewGroup$MarginLayoutParams.topMargin + viewGroup$MarginLayoutParams.bottomMargin + n4, viewGroup$MarginLayoutParams.height));
        return view.getMeasuredWidth() + n5;
    }
    
    private void measureChildConstrained(final View view, int measureSpec, int childMeasureSpec, int mode, final int n, int min) {
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)view.getLayoutParams();
        final int childMeasureSpec2 = getChildMeasureSpec(measureSpec, this.getPaddingLeft() + this.getPaddingRight() + viewGroup$MarginLayoutParams.leftMargin + viewGroup$MarginLayoutParams.rightMargin + childMeasureSpec, viewGroup$MarginLayoutParams.width);
        childMeasureSpec = getChildMeasureSpec(mode, this.getPaddingTop() + this.getPaddingBottom() + viewGroup$MarginLayoutParams.topMargin + viewGroup$MarginLayoutParams.bottomMargin + n, viewGroup$MarginLayoutParams.height);
        mode = View$MeasureSpec.getMode(childMeasureSpec);
        if (mode == 1073741824) {
            measureSpec = childMeasureSpec;
        }
        else {
            measureSpec = childMeasureSpec;
            if (min >= 0) {
                if (mode != 0) {
                    min = Math.min(View$MeasureSpec.getSize(childMeasureSpec), min);
                }
                measureSpec = View$MeasureSpec.makeMeasureSpec(min, 1073741824);
            }
        }
        view.measure(childMeasureSpec2, measureSpec);
    }
    
    private void postShowOverflowMenu() {
        this.removeCallbacks(this.mShowOverflowMenuRunnable);
        this.post(this.mShowOverflowMenuRunnable);
    }
    
    private boolean shouldCollapse() {
        if (this.mCollapsible) {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (this.shouldLayout(child) && child.getMeasuredWidth() > 0 && child.getMeasuredHeight() > 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean shouldLayout(final View view) {
        return view != null && view.getParent() == this && view.getVisibility() != 8;
    }
    
    void addChildrenForExpandedActionView() {
        for (int i = this.mHiddenViews.size() - 1; i >= 0; --i) {
            this.addView((View)this.mHiddenViews.get(i));
        }
        this.mHiddenViews.clear();
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean canShowOverflowMenu() {
        final boolean b = false;
        boolean b2;
        if (this.getVisibility() != 0) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.mMenuView != null) {
                b2 = b;
                if (this.mMenuView.isOverflowReserved()) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        boolean b = false;
        if (super.checkLayoutParams(viewGroup$LayoutParams) && viewGroup$LayoutParams instanceof LayoutParams) {
            b = true;
        }
        return b;
    }
    
    public void collapseActionView() {
        MenuItemImpl mCurrentExpandedItem = null;
        if (this.mExpandedMenuPresenter != null) {
            mCurrentExpandedItem = this.mExpandedMenuPresenter.mCurrentExpandedItem;
        }
        if (mCurrentExpandedItem != null) {
            mCurrentExpandedItem.collapseActionView();
        }
    }
    
    public void dismissPopupMenus() {
        if (this.mMenuView != null) {
            this.mMenuView.dismissPopupMenus();
        }
    }
    
    void ensureCollapseButtonView() {
        if (this.mCollapseButtonView == null) {
            (this.mCollapseButtonView = new AppCompatImageButton(this.getContext(), null, R.attr.toolbarNavigationButtonStyle)).setImageDrawable(this.mCollapseIcon);
            this.mCollapseButtonView.setContentDescription(this.mCollapseDescription);
            final LayoutParams generateDefaultLayoutParams = this.generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = ((this.mButtonGravity & 0x70) | 0x800003);
            generateDefaultLayoutParams.mViewType = 2;
            this.mCollapseButtonView.setLayoutParams((ViewGroup$LayoutParams)generateDefaultLayoutParams);
            this.mCollapseButtonView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    Toolbar.this.collapseActionView();
                }
            });
        }
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        if (viewGroup$LayoutParams instanceof ActionBar.LayoutParams) {
            return new LayoutParams((ActionBar.LayoutParams)viewGroup$LayoutParams);
        }
        if (!(viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams)) {
            return new LayoutParams(viewGroup$LayoutParams);
        }
        return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
    }
    
    public int getContentInsetEnd() {
        int end;
        if (this.mContentInsets == null) {
            end = 0;
        }
        else {
            end = this.mContentInsets.getEnd();
        }
        return end;
    }
    
    public int getContentInsetEndWithActions() {
        int n;
        if (this.mContentInsetEndWithActions == Integer.MIN_VALUE) {
            n = this.getContentInsetEnd();
        }
        else {
            n = this.mContentInsetEndWithActions;
        }
        return n;
    }
    
    public int getContentInsetLeft() {
        int left;
        if (this.mContentInsets == null) {
            left = 0;
        }
        else {
            left = this.mContentInsets.getLeft();
        }
        return left;
    }
    
    public int getContentInsetRight() {
        int right;
        if (this.mContentInsets == null) {
            right = 0;
        }
        else {
            right = this.mContentInsets.getRight();
        }
        return right;
    }
    
    public int getContentInsetStart() {
        int start;
        if (this.mContentInsets == null) {
            start = 0;
        }
        else {
            start = this.mContentInsets.getStart();
        }
        return start;
    }
    
    public int getContentInsetStartWithNavigation() {
        int n;
        if (this.mContentInsetStartWithNavigation == Integer.MIN_VALUE) {
            n = this.getContentInsetStart();
        }
        else {
            n = this.mContentInsetStartWithNavigation;
        }
        return n;
    }
    
    public int getCurrentContentInsetEnd() {
        int n;
        if (this.mMenuView == null) {
            n = 0;
        }
        else {
            final MenuBuilder peekMenu = this.mMenuView.peekMenu();
            if (peekMenu != null && peekMenu.hasVisibleItems()) {
                n = 1;
            }
            else {
                n = 0;
            }
        }
        int n2;
        if (n == 0) {
            n2 = this.getContentInsetEnd();
        }
        else {
            n2 = Math.max(this.getContentInsetEnd(), Math.max(this.mContentInsetEndWithActions, 0));
        }
        return n2;
    }
    
    public int getCurrentContentInsetLeft() {
        int n;
        if (ViewCompat.getLayoutDirection((View)this) != 1) {
            n = this.getCurrentContentInsetStart();
        }
        else {
            n = this.getCurrentContentInsetEnd();
        }
        return n;
    }
    
    public int getCurrentContentInsetRight() {
        int n;
        if (ViewCompat.getLayoutDirection((View)this) != 1) {
            n = this.getCurrentContentInsetEnd();
        }
        else {
            n = this.getCurrentContentInsetStart();
        }
        return n;
    }
    
    public int getCurrentContentInsetStart() {
        int n;
        if (this.getNavigationIcon() == null) {
            n = this.getContentInsetStart();
        }
        else {
            n = Math.max(this.getContentInsetStart(), Math.max(this.mContentInsetStartWithNavigation, 0));
        }
        return n;
    }
    
    public Drawable getLogo() {
        Drawable drawable = null;
        if (this.mLogoView != null) {
            drawable = this.mLogoView.getDrawable();
        }
        return drawable;
    }
    
    public CharSequence getLogoDescription() {
        CharSequence contentDescription = null;
        if (this.mLogoView != null) {
            contentDescription = this.mLogoView.getContentDescription();
        }
        return contentDescription;
    }
    
    public Menu getMenu() {
        this.ensureMenu();
        return this.mMenuView.getMenu();
    }
    
    @Nullable
    public CharSequence getNavigationContentDescription() {
        CharSequence contentDescription = null;
        if (this.mNavButtonView != null) {
            contentDescription = this.mNavButtonView.getContentDescription();
        }
        return contentDescription;
    }
    
    @Nullable
    public Drawable getNavigationIcon() {
        Drawable drawable = null;
        if (this.mNavButtonView != null) {
            drawable = this.mNavButtonView.getDrawable();
        }
        return drawable;
    }
    
    ActionMenuPresenter getOuterActionMenuPresenter() {
        return this.mOuterActionMenuPresenter;
    }
    
    @Nullable
    public Drawable getOverflowIcon() {
        this.ensureMenu();
        return this.mMenuView.getOverflowIcon();
    }
    
    Context getPopupContext() {
        return this.mPopupContext;
    }
    
    public int getPopupTheme() {
        return this.mPopupTheme;
    }
    
    public CharSequence getSubtitle() {
        return this.mSubtitleText;
    }
    
    public CharSequence getTitle() {
        return this.mTitleText;
    }
    
    public int getTitleMarginBottom() {
        return this.mTitleMarginBottom;
    }
    
    public int getTitleMarginEnd() {
        return this.mTitleMarginEnd;
    }
    
    public int getTitleMarginStart() {
        return this.mTitleMarginStart;
    }
    
    public int getTitleMarginTop() {
        return this.mTitleMarginTop;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public DecorToolbar getWrapper() {
        if (this.mWrapper == null) {
            this.mWrapper = new ToolbarWidgetWrapper(this, true);
        }
        return this.mWrapper;
    }
    
    public boolean hasExpandedActionView() {
        return this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null;
    }
    
    public boolean hideOverflowMenu() {
        boolean b = false;
        if (this.mMenuView != null && this.mMenuView.hideOverflowMenu()) {
            b = true;
        }
        return b;
    }
    
    public void inflateMenu(@MenuRes final int n) {
        this.getMenuInflater().inflate(n, this.getMenu());
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean isOverflowMenuShowPending() {
        boolean b = false;
        if (this.mMenuView != null && this.mMenuView.isOverflowMenuShowPending()) {
            b = true;
        }
        return b;
    }
    
    public boolean isOverflowMenuShowing() {
        boolean b = false;
        if (this.mMenuView != null && this.mMenuView.isOverflowMenuShowing()) {
            b = true;
        }
        return b;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean isTitleTruncated() {
        if (this.mTitleTextView == null) {
            return false;
        }
        final Layout layout = this.mTitleTextView.getLayout();
        if (layout != null) {
            for (int lineCount = layout.getLineCount(), i = 0; i < lineCount; ++i) {
                if (layout.getEllipsisCount(i) > 0) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeCallbacks(this.mShowOverflowMenuRunnable);
    }
    
    public boolean onHoverEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 9) {
            this.mEatingHover = false;
        }
        if (!this.mEatingHover) {
            final boolean onHoverEvent = super.onHoverEvent(motionEvent);
            if (actionMasked == 9 && !onHoverEvent) {
                this.mEatingHover = true;
            }
        }
        if (actionMasked == 10 || actionMasked == 3) {
            this.mEatingHover = false;
        }
        return true;
    }
    
    protected void onLayout(final boolean b, int a, int i, int a2, int min) {
        boolean b2;
        if (ViewCompat.getLayoutDirection((View)this) != 1) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int paddingTop = this.getPaddingTop();
        final int paddingBottom = this.getPaddingBottom();
        a = width - paddingRight;
        final int[] mTempMargins = this.mTempMargins;
        mTempMargins[mTempMargins[1] = 0] = 0;
        a2 = ViewCompat.getMinimumHeight((View)this);
        if (a2 < 0) {
            min = 0;
        }
        else {
            min = Math.min(a2, min - i);
        }
        if (!this.shouldLayout((View)this.mNavButtonView)) {
            i = paddingLeft;
        }
        else if (!b2) {
            i = this.layoutChildLeft((View)this.mNavButtonView, paddingLeft, mTempMargins, min);
        }
        else {
            a = this.layoutChildRight((View)this.mNavButtonView, a, mTempMargins, min);
            i = paddingLeft;
        }
        if (this.shouldLayout((View)this.mCollapseButtonView)) {
            if (!b2) {
                i = this.layoutChildLeft((View)this.mCollapseButtonView, i, mTempMargins, min);
            }
            else {
                a = this.layoutChildRight((View)this.mCollapseButtonView, a, mTempMargins, min);
            }
        }
        if (this.shouldLayout((View)this.mMenuView)) {
            if (!b2) {
                a = this.layoutChildRight((View)this.mMenuView, a, mTempMargins, min);
            }
            else {
                i = this.layoutChildLeft((View)this.mMenuView, i, mTempMargins, min);
            }
        }
        final int currentContentInsetLeft = this.getCurrentContentInsetLeft();
        a2 = this.getCurrentContentInsetRight();
        mTempMargins[0] = Math.max(0, currentContentInsetLeft - i);
        mTempMargins[1] = Math.max(0, a2 - (width - paddingRight - a));
        i = Math.max(i, currentContentInsetLeft);
        a = Math.min(a, width - paddingRight - a2);
        if (this.shouldLayout(this.mExpandedActionView)) {
            if (!b2) {
                i = this.layoutChildLeft(this.mExpandedActionView, i, mTempMargins, min);
            }
            else {
                a = this.layoutChildRight(this.mExpandedActionView, a, mTempMargins, min);
            }
        }
        if (!this.shouldLayout((View)this.mLogoView)) {
            a2 = a;
        }
        else if (!b2) {
            i = this.layoutChildLeft((View)this.mLogoView, i, mTempMargins, min);
            a2 = a;
        }
        else {
            a2 = this.layoutChildRight((View)this.mLogoView, a, mTempMargins, min);
        }
        final boolean shouldLayout = this.shouldLayout((View)this.mTitleTextView);
        final boolean shouldLayout2 = this.shouldLayout((View)this.mSubtitleTextView);
        a = 0;
        if (shouldLayout) {
            final LayoutParams layoutParams = (LayoutParams)this.mTitleTextView.getLayoutParams();
            final int topMargin = layoutParams.topMargin;
            a = this.mTitleTextView.getMeasuredHeight();
            a = layoutParams.bottomMargin + (topMargin + a) + 0;
        }
        if (shouldLayout2) {
            final LayoutParams layoutParams2 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
            a += layoutParams2.bottomMargin + (layoutParams2.topMargin + this.mSubtitleTextView.getMeasuredHeight());
        }
        if (shouldLayout || shouldLayout2) {
            TextView textView;
            if (!shouldLayout) {
                textView = this.mSubtitleTextView;
            }
            else {
                textView = this.mTitleTextView;
            }
            TextView textView2;
            if (!shouldLayout2) {
                textView2 = this.mTitleTextView;
            }
            else {
                textView2 = this.mSubtitleTextView;
            }
            final LayoutParams layoutParams3 = (LayoutParams)((View)textView).getLayoutParams();
            final LayoutParams layoutParams4 = (LayoutParams)((View)textView2).getLayoutParams();
            boolean b3;
            if ((!shouldLayout || this.mTitleTextView.getMeasuredWidth() <= 0) && (!shouldLayout2 || this.mSubtitleTextView.getMeasuredWidth() <= 0)) {
                b3 = false;
            }
            else {
                b3 = true;
            }
            switch (this.mGravity & 0x70) {
                default: {
                    final int n = (height - paddingTop - paddingBottom - a) / 2;
                    if (n >= layoutParams3.topMargin + this.mTitleMarginTop) {
                        a = height - paddingBottom - a - n - paddingTop;
                        if (a >= layoutParams3.bottomMargin + this.mTitleMarginBottom) {
                            a = n;
                        }
                        else {
                            a = Math.max(0, n - (layoutParams4.bottomMargin + this.mTitleMarginBottom - a));
                        }
                    }
                    else {
                        a = layoutParams3.topMargin + this.mTitleMarginTop;
                    }
                    a += paddingTop;
                    break;
                }
                case 48: {
                    a = this.getPaddingTop();
                    a = layoutParams3.topMargin + a + this.mTitleMarginTop;
                    break;
                }
                case 80: {
                    a = height - paddingBottom - layoutParams4.bottomMargin - this.mTitleMarginBottom - a;
                    break;
                }
            }
            if (!b2) {
                int mTitleMarginStart;
                if (!b3) {
                    mTitleMarginStart = 0;
                }
                else {
                    mTitleMarginStart = this.mTitleMarginStart;
                }
                final int b4 = mTitleMarginStart - mTempMargins[0];
                i += Math.max(0, b4);
                mTempMargins[0] = Math.max(0, -b4);
                int n4;
                if (!shouldLayout) {
                    final int n2 = i;
                    final int n3 = a;
                    a = n2;
                    n4 = n3;
                }
                else {
                    final LayoutParams layoutParams5 = (LayoutParams)this.mTitleTextView.getLayoutParams();
                    final int n5 = this.mTitleTextView.getMeasuredWidth() + i;
                    final int n6 = this.mTitleTextView.getMeasuredHeight() + a;
                    this.mTitleTextView.layout(i, a, n5, n6);
                    a = this.mTitleMarginEnd;
                    final int bottomMargin = layoutParams5.bottomMargin;
                    a += n5;
                    n4 = bottomMargin + n6;
                }
                int b5;
                if (!shouldLayout2) {
                    b5 = i;
                }
                else {
                    final LayoutParams layoutParams6 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
                    final int n7 = n4 + layoutParams6.topMargin;
                    final int n8 = this.mSubtitleTextView.getMeasuredWidth() + i;
                    this.mSubtitleTextView.layout(i, n7, n8, this.mSubtitleTextView.getMeasuredHeight() + n7);
                    final int mTitleMarginEnd = this.mTitleMarginEnd;
                    final int bottomMargin2 = layoutParams6.bottomMargin;
                    b5 = mTitleMarginEnd + n8;
                }
                if (b3) {
                    i = Math.max(a, b5);
                }
            }
            else {
                int mTitleMarginStart2;
                if (!b3) {
                    mTitleMarginStart2 = 0;
                }
                else {
                    mTitleMarginStart2 = this.mTitleMarginStart;
                }
                final int b6 = mTitleMarginStart2 - mTempMargins[1];
                a2 -= Math.max(0, b6);
                mTempMargins[1] = Math.max(0, -b6);
                int n10;
                if (!shouldLayout) {
                    final int n9 = a2;
                    n10 = a;
                    a = n9;
                }
                else {
                    final LayoutParams layoutParams7 = (LayoutParams)this.mTitleTextView.getLayoutParams();
                    final int n11 = a2 - this.mTitleTextView.getMeasuredWidth();
                    final int n12 = this.mTitleTextView.getMeasuredHeight() + a;
                    this.mTitleTextView.layout(n11, a, a2, n12);
                    a = this.mTitleMarginEnd;
                    n10 = n12 + layoutParams7.bottomMargin;
                    a = n11 - a;
                }
                int b7;
                if (!shouldLayout2) {
                    b7 = a2;
                }
                else {
                    final LayoutParams layoutParams8 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
                    final int n13 = layoutParams8.topMargin + n10;
                    this.mSubtitleTextView.layout(a2 - this.mSubtitleTextView.getMeasuredWidth(), n13, a2, this.mSubtitleTextView.getMeasuredHeight() + n13);
                    final int mTitleMarginEnd2 = this.mTitleMarginEnd;
                    final int bottomMargin3 = layoutParams8.bottomMargin;
                    b7 = a2 - mTitleMarginEnd2;
                }
                if (!b3) {
                    a = a2;
                }
                else {
                    a = Math.min(a, b7);
                }
                a2 = a;
            }
        }
        this.addCustomViewsWithGravity(this.mTempViews, 3);
        final int size = this.mTempViews.size();
        final int n14 = 0;
        a = i;
        for (i = n14; i < size; ++i) {
            a = this.layoutChildLeft(this.mTempViews.get(i), a, mTempMargins, min);
        }
        this.addCustomViewsWithGravity(this.mTempViews, 5);
        int size2;
        for (size2 = this.mTempViews.size(), i = 0; i < size2; ++i) {
            a2 = this.layoutChildRight(this.mTempViews.get(i), a2, mTempMargins, min);
        }
        this.addCustomViewsWithGravity(this.mTempViews, 1);
        final int viewListMeasuredWidth = this.getViewListMeasuredWidth(this.mTempViews, mTempMargins);
        i = (width - paddingLeft - paddingRight) / 2 + paddingLeft - viewListMeasuredWidth / 2;
        final int n15 = viewListMeasuredWidth + i;
        if (i >= a) {
            if (n15 <= a2) {
                a = i;
            }
            else {
                a = i - (n15 - a2);
            }
        }
        for (a2 = this.mTempViews.size(), i = 0; i < a2; ++i) {
            a = this.layoutChildLeft(this.mTempViews.get(i), a, mTempMargins, min);
        }
        this.mTempViews.clear();
    }
    
    protected void onMeasure(int resolveSizeAndState, final int n) {
        final int[] mTempMargins = this.mTempMargins;
        int n2;
        int n3;
        if (!ViewUtils.isLayoutRtl((View)this)) {
            n2 = 1;
            n3 = 0;
        }
        else {
            n2 = 0;
            n3 = 1;
        }
        int b = 0;
        int n4;
        int n5;
        if (!this.shouldLayout((View)this.mNavButtonView)) {
            n4 = 0;
            n5 = 0;
        }
        else {
            this.measureChildConstrained((View)this.mNavButtonView, resolveSizeAndState, 0, n, 0, this.mMaxButtonHeight);
            b = this.mNavButtonView.getMeasuredWidth() + this.getHorizontalMargins((View)this.mNavButtonView);
            n5 = Math.max(0, this.mNavButtonView.getMeasuredHeight() + this.getVerticalMargins((View)this.mNavButtonView));
            n4 = View.combineMeasuredStates(0, this.mNavButtonView.getMeasuredState());
        }
        if (this.shouldLayout((View)this.mCollapseButtonView)) {
            this.measureChildConstrained((View)this.mCollapseButtonView, resolveSizeAndState, 0, n, 0, this.mMaxButtonHeight);
            b = this.mCollapseButtonView.getMeasuredWidth() + this.getHorizontalMargins((View)this.mCollapseButtonView);
            n5 = Math.max(n5, this.mCollapseButtonView.getMeasuredHeight() + this.getVerticalMargins((View)this.mCollapseButtonView));
            n4 = View.combineMeasuredStates(n4, this.mCollapseButtonView.getMeasuredState());
        }
        final int currentContentInsetStart = this.getCurrentContentInsetStart();
        final int n6 = Math.max(currentContentInsetStart, b) + 0;
        mTempMargins[n3] = Math.max(0, currentContentInsetStart - b);
        int b2 = 0;
        if (this.shouldLayout((View)this.mMenuView)) {
            this.measureChildConstrained((View)this.mMenuView, resolveSizeAndState, n6, n, 0, this.mMaxButtonHeight);
            b2 = this.mMenuView.getMeasuredWidth() + this.getHorizontalMargins((View)this.mMenuView);
            n5 = Math.max(n5, this.mMenuView.getMeasuredHeight() + this.getVerticalMargins((View)this.mMenuView));
            n4 = View.combineMeasuredStates(n4, this.mMenuView.getMeasuredState());
        }
        final int currentContentInsetEnd = this.getCurrentContentInsetEnd();
        final int n7 = n6 + Math.max(currentContentInsetEnd, b2);
        mTempMargins[n2] = Math.max(0, currentContentInsetEnd - b2);
        int n8;
        if (!this.shouldLayout(this.mExpandedActionView)) {
            n8 = n7;
        }
        else {
            n8 = n7 + this.measureChildCollapseMargins(this.mExpandedActionView, resolveSizeAndState, n7, n, 0, mTempMargins);
            n5 = Math.max(n5, this.mExpandedActionView.getMeasuredHeight() + this.getVerticalMargins(this.mExpandedActionView));
            n4 = View.combineMeasuredStates(n4, this.mExpandedActionView.getMeasuredState());
        }
        if (this.shouldLayout((View)this.mLogoView)) {
            n8 += this.measureChildCollapseMargins((View)this.mLogoView, resolveSizeAndState, n8, n, 0, mTempMargins);
            n5 = Math.max(n5, this.mLogoView.getMeasuredHeight() + this.getVerticalMargins((View)this.mLogoView));
            n4 = View.combineMeasuredStates(n4, this.mLogoView.getMeasuredState());
        }
        final int childCount = this.getChildCount();
        int i = 0;
        int n9 = n8;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            int n11;
            int combineMeasuredStates;
            if (((LayoutParams)child.getLayoutParams()).mViewType != 0) {
                final int n10 = n4;
                n11 = n5;
                combineMeasuredStates = n10;
            }
            else if (this.shouldLayout(child)) {
                n9 += this.measureChildCollapseMargins(child, resolveSizeAndState, n9, n, 0, mTempMargins);
                final int max = Math.max(n5, child.getMeasuredHeight() + this.getVerticalMargins(child));
                combineMeasuredStates = View.combineMeasuredStates(n4, child.getMeasuredState());
                n11 = max;
            }
            else {
                final int n12 = n5;
                combineMeasuredStates = n4;
                n11 = n12;
            }
            ++i;
            final int n13 = combineMeasuredStates;
            n5 = n11;
            n4 = n13;
        }
        int max2 = 0;
        final int n14 = 0;
        final int n15 = this.mTitleMarginTop + this.mTitleMarginBottom;
        final int n16 = this.mTitleMarginStart + this.mTitleMarginEnd;
        int n17;
        int b3;
        if (!this.shouldLayout((View)this.mTitleTextView)) {
            n17 = n4;
            b3 = n14;
        }
        else {
            this.measureChildCollapseMargins((View)this.mTitleTextView, resolveSizeAndState, n9 + n16, n, n15, mTempMargins);
            max2 = this.getHorizontalMargins((View)this.mTitleTextView) + this.mTitleTextView.getMeasuredWidth();
            final int n18 = this.mTitleTextView.getMeasuredHeight() + this.getVerticalMargins((View)this.mTitleTextView);
            n17 = View.combineMeasuredStates(n4, this.mTitleTextView.getMeasuredState());
            b3 = n18;
        }
        if (this.shouldLayout((View)this.mSubtitleTextView)) {
            max2 = Math.max(max2, this.measureChildCollapseMargins((View)this.mSubtitleTextView, resolveSizeAndState, n9 + n16, n, n15 + b3, mTempMargins));
            b3 += this.mSubtitleTextView.getMeasuredHeight() + this.getVerticalMargins((View)this.mSubtitleTextView);
            n17 = View.combineMeasuredStates(n17, this.mSubtitleTextView.getMeasuredState());
        }
        final int max3 = Math.max(n5, b3);
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int paddingTop = this.getPaddingTop();
        final int paddingBottom = this.getPaddingBottom();
        final int resolveSizeAndState2 = View.resolveSizeAndState(Math.max(max2 + n9 + (paddingLeft + paddingRight), this.getSuggestedMinimumWidth()), resolveSizeAndState, 0xFF000000 & n17);
        resolveSizeAndState = View.resolveSizeAndState(Math.max(max3 + (paddingTop + paddingBottom), this.getSuggestedMinimumHeight()), n, n17 << 16);
        if (this.shouldCollapse()) {
            resolveSizeAndState = 0;
        }
        this.setMeasuredDimension(resolveSizeAndState2, resolveSizeAndState);
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        final Menu menu = null;
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            Object peekMenu;
            if (this.mMenuView == null) {
                peekMenu = menu;
            }
            else {
                peekMenu = this.mMenuView.peekMenu();
            }
            if (savedState.expandedMenuItemId != 0 && this.mExpandedMenuPresenter != null && peekMenu != null) {
                final MenuItem item = ((Menu)peekMenu).findItem(savedState.expandedMenuItemId);
                if (item != null) {
                    item.expandActionView();
                }
            }
            if (savedState.isOverflowOpen) {
                this.postShowOverflowMenu();
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    public void onRtlPropertiesChanged(final int n) {
        boolean direction = true;
        if (Build$VERSION.SDK_INT >= 17) {
            super.onRtlPropertiesChanged(n);
        }
        this.ensureContentInsets();
        final RtlSpacingHelper mContentInsets = this.mContentInsets;
        if (n != 1) {
            direction = false;
        }
        mContentInsets.setDirection(direction);
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null) {
            savedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }
        savedState.isOverflowOpen = this.isOverflowMenuShowing();
        return (Parcelable)savedState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mEatingTouch = false;
        }
        if (!this.mEatingTouch) {
            final boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (actionMasked == 0 && !onTouchEvent) {
                this.mEatingTouch = true;
            }
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.mEatingTouch = false;
        }
        return true;
    }
    
    void removeChildrenForExpandedActionView() {
        for (int i = this.getChildCount() - 1; i >= 0; --i) {
            final View child = this.getChildAt(i);
            if (((LayoutParams)child.getLayoutParams()).mViewType != 2 && child != this.mMenuView) {
                this.removeViewAt(i);
                this.mHiddenViews.add(child);
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setCollapsible(final boolean mCollapsible) {
        this.mCollapsible = mCollapsible;
        this.requestLayout();
    }
    
    public void setContentInsetEndWithActions(int mContentInsetEndWithActions) {
        if (mContentInsetEndWithActions < 0) {
            mContentInsetEndWithActions = Integer.MIN_VALUE;
        }
        if (mContentInsetEndWithActions != this.mContentInsetEndWithActions) {
            this.mContentInsetEndWithActions = mContentInsetEndWithActions;
            if (this.getNavigationIcon() != null) {
                this.requestLayout();
            }
        }
    }
    
    public void setContentInsetStartWithNavigation(int mContentInsetStartWithNavigation) {
        if (mContentInsetStartWithNavigation < 0) {
            mContentInsetStartWithNavigation = Integer.MIN_VALUE;
        }
        if (mContentInsetStartWithNavigation != this.mContentInsetStartWithNavigation) {
            this.mContentInsetStartWithNavigation = mContentInsetStartWithNavigation;
            if (this.getNavigationIcon() != null) {
                this.requestLayout();
            }
        }
    }
    
    public void setContentInsetsAbsolute(final int n, final int n2) {
        this.ensureContentInsets();
        this.mContentInsets.setAbsolute(n, n2);
    }
    
    public void setContentInsetsRelative(final int n, final int n2) {
        this.ensureContentInsets();
        this.mContentInsets.setRelative(n, n2);
    }
    
    public void setLogo(@DrawableRes final int n) {
        this.setLogo(AppCompatResources.getDrawable(this.getContext(), n));
    }
    
    public void setLogo(final Drawable imageDrawable) {
        if (imageDrawable == null) {
            if (this.mLogoView != null && this.isChildOrHidden((View)this.mLogoView)) {
                this.removeView((View)this.mLogoView);
                this.mHiddenViews.remove(this.mLogoView);
            }
        }
        else {
            this.ensureLogoView();
            if (!this.isChildOrHidden((View)this.mLogoView)) {
                this.addSystemView((View)this.mLogoView, true);
            }
        }
        if (this.mLogoView != null) {
            this.mLogoView.setImageDrawable(imageDrawable);
        }
    }
    
    public void setLogoDescription(@StringRes final int n) {
        this.setLogoDescription(this.getContext().getText(n));
    }
    
    public void setLogoDescription(final CharSequence contentDescription) {
        if (!TextUtils.isEmpty(contentDescription)) {
            this.ensureLogoView();
        }
        if (this.mLogoView != null) {
            this.mLogoView.setContentDescription(contentDescription);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setMenu(final MenuBuilder menuBuilder, final ActionMenuPresenter actionMenuPresenter) {
        if (menuBuilder == null && this.mMenuView == null) {
            return;
        }
        this.ensureMenuView();
        final MenuBuilder peekMenu = this.mMenuView.peekMenu();
        if (peekMenu != menuBuilder) {
            if (peekMenu != null) {
                peekMenu.removeMenuPresenter(this.mOuterActionMenuPresenter);
                peekMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
            }
            if (this.mExpandedMenuPresenter == null) {
                this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            actionMenuPresenter.setExpandedActionViewsExclusive(true);
            if (menuBuilder == null) {
                actionMenuPresenter.initForMenu(this.mPopupContext, null);
                this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
                actionMenuPresenter.updateMenuView(true);
                this.mExpandedMenuPresenter.updateMenuView(true);
            }
            else {
                menuBuilder.addMenuPresenter(actionMenuPresenter, this.mPopupContext);
                menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
            }
            this.mMenuView.setPopupTheme(this.mPopupTheme);
            this.mMenuView.setPresenter(actionMenuPresenter);
            this.mOuterActionMenuPresenter = actionMenuPresenter;
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setMenuCallbacks(final MenuPresenter.Callback mActionMenuPresenterCallback, final MenuBuilder.Callback mMenuBuilderCallback) {
        this.mActionMenuPresenterCallback = mActionMenuPresenterCallback;
        this.mMenuBuilderCallback = mMenuBuilderCallback;
        if (this.mMenuView != null) {
            this.mMenuView.setMenuCallbacks(mActionMenuPresenterCallback, mMenuBuilderCallback);
        }
    }
    
    public void setNavigationContentDescription(@StringRes final int n) {
        CharSequence text;
        if (n == 0) {
            text = null;
        }
        else {
            text = this.getContext().getText(n);
        }
        this.setNavigationContentDescription(text);
    }
    
    public void setNavigationContentDescription(@Nullable final CharSequence contentDescription) {
        if (!TextUtils.isEmpty(contentDescription)) {
            this.ensureNavButtonView();
        }
        if (this.mNavButtonView != null) {
            this.mNavButtonView.setContentDescription(contentDescription);
        }
    }
    
    public void setNavigationIcon(@DrawableRes final int n) {
        this.setNavigationIcon(AppCompatResources.getDrawable(this.getContext(), n));
    }
    
    public void setNavigationIcon(@Nullable final Drawable imageDrawable) {
        if (imageDrawable == null) {
            if (this.mNavButtonView != null && this.isChildOrHidden((View)this.mNavButtonView)) {
                this.removeView((View)this.mNavButtonView);
                this.mHiddenViews.remove(this.mNavButtonView);
            }
        }
        else {
            this.ensureNavButtonView();
            if (!this.isChildOrHidden((View)this.mNavButtonView)) {
                this.addSystemView((View)this.mNavButtonView, true);
            }
        }
        if (this.mNavButtonView != null) {
            this.mNavButtonView.setImageDrawable(imageDrawable);
        }
    }
    
    public void setNavigationOnClickListener(final View$OnClickListener onClickListener) {
        this.ensureNavButtonView();
        this.mNavButtonView.setOnClickListener(onClickListener);
    }
    
    public void setOnMenuItemClickListener(final OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }
    
    public void setOverflowIcon(@Nullable final Drawable overflowIcon) {
        this.ensureMenu();
        this.mMenuView.setOverflowIcon(overflowIcon);
    }
    
    public void setPopupTheme(@StyleRes final int mPopupTheme) {
        if (this.mPopupTheme != mPopupTheme) {
            if ((this.mPopupTheme = mPopupTheme) != 0) {
                this.mPopupContext = (Context)new ContextThemeWrapper(this.getContext(), mPopupTheme);
            }
            else {
                this.mPopupContext = this.getContext();
            }
        }
    }
    
    public void setSubtitle(@StringRes final int n) {
        this.setSubtitle(this.getContext().getText(n));
    }
    
    public void setSubtitle(final CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            if (this.mSubtitleTextView != null && this.isChildOrHidden((View)this.mSubtitleTextView)) {
                this.removeView((View)this.mSubtitleTextView);
                this.mHiddenViews.remove(this.mSubtitleTextView);
            }
        }
        else {
            if (this.mSubtitleTextView == null) {
                final Context context = this.getContext();
                (this.mSubtitleTextView = new AppCompatTextView(context)).setSingleLine();
                this.mSubtitleTextView.setEllipsize(TextUtils$TruncateAt.END);
                if (this.mSubtitleTextAppearance != 0) {
                    this.mSubtitleTextView.setTextAppearance(context, this.mSubtitleTextAppearance);
                }
                if (this.mSubtitleTextColor != 0) {
                    this.mSubtitleTextView.setTextColor(this.mSubtitleTextColor);
                }
            }
            if (!this.isChildOrHidden((View)this.mSubtitleTextView)) {
                this.addSystemView((View)this.mSubtitleTextView, true);
            }
        }
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setText(charSequence);
        }
        this.mSubtitleText = charSequence;
    }
    
    public void setSubtitleTextAppearance(final Context context, @StyleRes final int mSubtitleTextAppearance) {
        this.mSubtitleTextAppearance = mSubtitleTextAppearance;
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setTextAppearance(context, mSubtitleTextAppearance);
        }
    }
    
    public void setSubtitleTextColor(@ColorInt final int n) {
        this.mSubtitleTextColor = n;
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setTextColor(n);
        }
    }
    
    public void setTitle(@StringRes final int n) {
        this.setTitle(this.getContext().getText(n));
    }
    
    public void setTitle(final CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            if (this.mTitleTextView != null && this.isChildOrHidden((View)this.mTitleTextView)) {
                this.removeView((View)this.mTitleTextView);
                this.mHiddenViews.remove(this.mTitleTextView);
            }
        }
        else {
            if (this.mTitleTextView == null) {
                final Context context = this.getContext();
                (this.mTitleTextView = new AppCompatTextView(context)).setSingleLine();
                this.mTitleTextView.setEllipsize(TextUtils$TruncateAt.END);
                if (this.mTitleTextAppearance != 0) {
                    this.mTitleTextView.setTextAppearance(context, this.mTitleTextAppearance);
                }
                if (this.mTitleTextColor != 0) {
                    this.mTitleTextView.setTextColor(this.mTitleTextColor);
                }
            }
            if (!this.isChildOrHidden((View)this.mTitleTextView)) {
                this.addSystemView((View)this.mTitleTextView, true);
            }
        }
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setText(charSequence);
        }
        this.mTitleText = charSequence;
    }
    
    public void setTitleMargin(final int mTitleMarginStart, final int mTitleMarginTop, final int mTitleMarginEnd, final int mTitleMarginBottom) {
        this.mTitleMarginStart = mTitleMarginStart;
        this.mTitleMarginTop = mTitleMarginTop;
        this.mTitleMarginEnd = mTitleMarginEnd;
        this.mTitleMarginBottom = mTitleMarginBottom;
        this.requestLayout();
    }
    
    public void setTitleMarginBottom(final int mTitleMarginBottom) {
        this.mTitleMarginBottom = mTitleMarginBottom;
        this.requestLayout();
    }
    
    public void setTitleMarginEnd(final int mTitleMarginEnd) {
        this.mTitleMarginEnd = mTitleMarginEnd;
        this.requestLayout();
    }
    
    public void setTitleMarginStart(final int mTitleMarginStart) {
        this.mTitleMarginStart = mTitleMarginStart;
        this.requestLayout();
    }
    
    public void setTitleMarginTop(final int mTitleMarginTop) {
        this.mTitleMarginTop = mTitleMarginTop;
        this.requestLayout();
    }
    
    public void setTitleTextAppearance(final Context context, @StyleRes final int mTitleTextAppearance) {
        this.mTitleTextAppearance = mTitleTextAppearance;
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
        }
    }
    
    public void setTitleTextColor(@ColorInt final int n) {
        this.mTitleTextColor = n;
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setTextColor(n);
        }
    }
    
    public boolean showOverflowMenu() {
        boolean b = false;
        if (this.mMenuView != null && this.mMenuView.showOverflowMenu()) {
            b = true;
        }
        return b;
    }
    
    private class ExpandedActionViewMenuPresenter implements MenuPresenter
    {
        MenuItemImpl mCurrentExpandedItem;
        MenuBuilder mMenu;
        
        ExpandedActionViewMenuPresenter() {
        }
        
        @Override
        public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
            if (Toolbar.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewCollapsed();
            }
            Toolbar.this.removeView(Toolbar.this.mExpandedActionView);
            Toolbar.this.removeView((View)Toolbar.this.mCollapseButtonView);
            Toolbar.this.mExpandedActionView = null;
            Toolbar.this.addChildrenForExpandedActionView();
            this.mCurrentExpandedItem = null;
            Toolbar.this.requestLayout();
            menuItemImpl.setActionViewExpanded(false);
            return true;
        }
        
        @Override
        public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl mCurrentExpandedItem) {
            Toolbar.this.ensureCollapseButtonView();
            if (Toolbar.this.mCollapseButtonView.getParent() != Toolbar.this) {
                Toolbar.this.addView((View)Toolbar.this.mCollapseButtonView);
            }
            Toolbar.this.mExpandedActionView = mCurrentExpandedItem.getActionView();
            this.mCurrentExpandedItem = mCurrentExpandedItem;
            if (Toolbar.this.mExpandedActionView.getParent() != Toolbar.this) {
                final LayoutParams generateDefaultLayoutParams = Toolbar.this.generateDefaultLayoutParams();
                generateDefaultLayoutParams.gravity = ((Toolbar.this.mButtonGravity & 0x70) | 0x800003);
                generateDefaultLayoutParams.mViewType = 2;
                Toolbar.this.mExpandedActionView.setLayoutParams((ViewGroup$LayoutParams)generateDefaultLayoutParams);
                Toolbar.this.addView(Toolbar.this.mExpandedActionView);
            }
            Toolbar.this.removeChildrenForExpandedActionView();
            Toolbar.this.requestLayout();
            mCurrentExpandedItem.setActionViewExpanded(true);
            if (Toolbar.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewExpanded();
            }
            return true;
        }
        
        @Override
        public boolean flagActionItems() {
            return false;
        }
        
        @Override
        public int getId() {
            return 0;
        }
        
        @Override
        public MenuView getMenuView(final ViewGroup viewGroup) {
            return null;
        }
        
        @Override
        public void initForMenu(final Context context, final MenuBuilder mMenu) {
            if (this.mMenu != null && this.mCurrentExpandedItem != null) {
                this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
            }
            this.mMenu = mMenu;
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        }
        
        @Override
        public void onRestoreInstanceState(final Parcelable parcelable) {
        }
        
        @Override
        public Parcelable onSaveInstanceState() {
            return null;
        }
        
        @Override
        public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
            return false;
        }
        
        @Override
        public void setCallback(final Callback callback) {
        }
        
        @Override
        public void updateMenuView(final boolean b) {
            final boolean b2 = false;
            if (this.mCurrentExpandedItem != null) {
                int n = 0;
                Label_0019: {
                    if (this.mMenu == null) {
                        n = (b2 ? 1 : 0);
                    }
                    else {
                        final int size = this.mMenu.size();
                        int n2 = 0;
                        while (true) {
                            n = (b2 ? 1 : 0);
                            if (n2 >= size) {
                                break Label_0019;
                            }
                            if (this.mMenu.getItem(n2) == this.mCurrentExpandedItem) {
                                break;
                            }
                            ++n2;
                        }
                        n = 1;
                    }
                }
                if (n == 0) {
                    this.collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
                }
            }
        }
    }
    
    public static class LayoutParams extends ActionBar.LayoutParams
    {
        static final int CUSTOM = 0;
        static final int EXPANDED = 2;
        static final int SYSTEM = 1;
        int mViewType;
        
        public LayoutParams(final int n) {
            this(-2, -1, n);
        }
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mViewType = 0;
            this.gravity = 8388627;
        }
        
        public LayoutParams(final int n, final int n2, final int gravity) {
            super(n, n2);
            this.mViewType = 0;
            this.gravity = gravity;
        }
        
        public LayoutParams(@NonNull final Context context, final AttributeSet set) {
            super(context, set);
            this.mViewType = 0;
        }
        
        public LayoutParams(final ActionBar.LayoutParams layoutParams) {
            super(layoutParams);
            this.mViewType = 0;
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ActionBar.LayoutParams)layoutParams);
            this.mViewType = 0;
            this.mViewType = layoutParams.mViewType;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mViewType = 0;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super((ViewGroup$LayoutParams)viewGroup$MarginLayoutParams);
            this.mViewType = 0;
            this.copyMarginsFromCompat(viewGroup$MarginLayoutParams);
        }
        
        void copyMarginsFromCompat(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            this.leftMargin = viewGroup$MarginLayoutParams.leftMargin;
            this.topMargin = viewGroup$MarginLayoutParams.topMargin;
            this.rightMargin = viewGroup$MarginLayoutParams.rightMargin;
            this.bottomMargin = viewGroup$MarginLayoutParams.bottomMargin;
        }
    }
    
    public interface OnMenuItemClickListener
    {
        boolean onMenuItemClick(final MenuItem p0);
    }
    
    public static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int expandedMenuItemId;
        boolean isOverflowOpen;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        public SavedState(final Parcel parcel) {
            this(parcel, null);
        }
        
        public SavedState(final Parcel parcel, final ClassLoader classLoader) {
            boolean isOverflowOpen = false;
            super(parcel, classLoader);
            this.expandedMenuItemId = parcel.readInt();
            if (parcel.readInt() != 0) {
                isOverflowOpen = true;
            }
            this.isOverflowOpen = isOverflowOpen;
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, int n) {
            final int n2 = 0;
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.expandedMenuItemId);
            if (!this.isOverflowOpen) {
                n = n2;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
        }
    }
}
