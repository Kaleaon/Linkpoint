// 
// Decompiled by Procyon v0.6.0
// 

package com.astuetz;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.View$BaseSavedState;
import android.os.Parcelable;
import android.graphics.Canvas;
import android.database.DataSetObserver;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.util.Pair;
import android.view.ViewGroup$LayoutParams;
import android.view.View$OnClickListener;
import android.widget.TextView;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.graphics.Color;
import android.os.Build$VERSION;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.graphics.Paint$Style;
import android.view.View;
import com.astuetz.pagerslidingtabstrip.R;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.content.res.ColorStateList;
import android.widget.LinearLayout$LayoutParams;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.widget.HorizontalScrollView;

public class PagerSlidingTabStrip extends HorizontalScrollView
{
    private static final int[] ANDROID_ATTRS;
    public static final int DEF_VALUE_TAB_TEXT_ALPHA = 150;
    private static final int PADDING_INDEX = 1;
    private static final int PADDING_LEFT_INDEX = 2;
    private static final int PADDING_RIGHT_INDEX = 3;
    private static final int TEXT_COLOR_PRIMARY = 0;
    private boolean isCustomTabs;
    private boolean isExpandTabs;
    private boolean isPaddingMiddle;
    private boolean isTabTextAllCaps;
    private final PagerAdapterObserver mAdapterObserver;
    private int mCurrentPosition;
    private float mCurrentPositionOffset;
    public ViewPager.OnPageChangeListener mDelegatePageListener;
    private int mDividerColor;
    private int mDividerPadding;
    private Paint mDividerPaint;
    private int mDividerWidth;
    private int mIndicatorColor;
    private int mIndicatorHeight;
    private int mLastScrollX;
    private int mPaddingLeft;
    private int mPaddingRight;
    private final PageListener mPageListener;
    private ViewPager mPager;
    private Paint mRectPaint;
    private int mScrollOffset;
    private int mTabBackgroundResId;
    private int mTabCount;
    private LinearLayout$LayoutParams mTabLayoutParams;
    private int mTabPadding;
    private OnTabReselectedListener mTabReselectedListener;
    private ColorStateList mTabTextColor;
    private int mTabTextSize;
    private Typeface mTabTextTypeface;
    private int mTabTextTypefaceStyle;
    private LinearLayout mTabsContainer;
    private int mUnderlineColor;
    private int mUnderlineHeight;
    
    static {
        ANDROID_ATTRS = new int[] { 16842806, 16842965, 16842966, 16842968 };
    }
    
    public PagerSlidingTabStrip(final Context context) {
        this(context, null);
    }
    
    public PagerSlidingTabStrip(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PagerSlidingTabStrip(final Context context, final AttributeSet set, int n) {
        super(context, set, n);
        this.mAdapterObserver = new PagerAdapterObserver();
        this.mPageListener = new PageListener();
        this.mTabReselectedListener = null;
        this.mCurrentPosition = 0;
        this.mCurrentPositionOffset = 0.0f;
        this.mIndicatorHeight = 2;
        this.mUnderlineHeight = 0;
        this.mDividerWidth = 0;
        this.mDividerPadding = 0;
        this.mTabPadding = 12;
        this.mTabTextSize = 14;
        this.mTabTextColor = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.isExpandTabs = false;
        this.isPaddingMiddle = false;
        this.isTabTextAllCaps = true;
        this.mTabTextTypeface = null;
        this.mTabTextTypefaceStyle = 1;
        this.mLastScrollX = 0;
        this.mTabBackgroundResId = R.drawable.psts_background_tab;
        this.setFillViewport(true);
        this.setWillNotDraw(false);
        (this.mTabsContainer = new LinearLayout(context)).setOrientation(0);
        this.addView((View)this.mTabsContainer);
        (this.mRectPaint = new Paint()).setAntiAlias(true);
        this.mRectPaint.setStyle(Paint$Style.FILL);
        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        this.mScrollOffset = (int)TypedValue.applyDimension(1, (float)this.mScrollOffset, displayMetrics);
        this.mIndicatorHeight = (int)TypedValue.applyDimension(1, (float)this.mIndicatorHeight, displayMetrics);
        this.mUnderlineHeight = (int)TypedValue.applyDimension(1, (float)this.mUnderlineHeight, displayMetrics);
        this.mDividerPadding = (int)TypedValue.applyDimension(1, (float)this.mDividerPadding, displayMetrics);
        this.mTabPadding = (int)TypedValue.applyDimension(1, (float)this.mTabPadding, displayMetrics);
        this.mDividerWidth = (int)TypedValue.applyDimension(1, (float)this.mDividerWidth, displayMetrics);
        this.mTabTextSize = (int)TypedValue.applyDimension(2, (float)this.mTabTextSize, displayMetrics);
        (this.mDividerPaint = new Paint()).setAntiAlias(true);
        this.mDividerPaint.setStrokeWidth((float)this.mDividerWidth);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, PagerSlidingTabStrip.ANDROID_ATTRS);
        final int color = obtainStyledAttributes.getColor(0, ContextCompat.getColor(context, 17170444));
        this.mUnderlineColor = color;
        this.mDividerColor = color;
        this.mIndicatorColor = color;
        n = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        int dimensionPixelSize;
        if (n <= 0) {
            dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(2, 0);
        }
        else {
            dimensionPixelSize = n;
        }
        this.mPaddingLeft = dimensionPixelSize;
        int dimensionPixelSize2 = n;
        if (n <= 0) {
            dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(3, 0);
        }
        this.mPaddingRight = dimensionPixelSize2;
        obtainStyledAttributes.recycle();
        String s = "sans-serif";
        if (Build$VERSION.SDK_INT >= 21) {
            s = "sans-serif-medium";
            this.mTabTextTypefaceStyle = 0;
        }
        final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R.styleable.PagerSlidingTabStrip);
        this.mIndicatorColor = obtainStyledAttributes2.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, this.mIndicatorColor);
        this.mIndicatorHeight = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, this.mIndicatorHeight);
        this.mUnderlineColor = obtainStyledAttributes2.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, this.mUnderlineColor);
        this.mUnderlineHeight = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, this.mUnderlineHeight);
        this.mDividerColor = obtainStyledAttributes2.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, this.mDividerColor);
        this.mDividerWidth = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerWidth, this.mDividerWidth);
        this.mDividerPadding = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, this.mDividerPadding);
        this.isExpandTabs = obtainStyledAttributes2.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, this.isExpandTabs);
        this.mScrollOffset = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, this.mScrollOffset);
        this.isPaddingMiddle = obtainStyledAttributes2.getBoolean(R.styleable.PagerSlidingTabStrip_pstsPaddingMiddle, this.isPaddingMiddle);
        this.mTabPadding = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, this.mTabPadding);
        this.mTabBackgroundResId = obtainStyledAttributes2.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, this.mTabBackgroundResId);
        this.mTabTextSize = obtainStyledAttributes2.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabTextSize, this.mTabTextSize);
        ColorStateList colorStateList;
        if (!obtainStyledAttributes2.hasValue(R.styleable.PagerSlidingTabStrip_pstsTabTextColor)) {
            colorStateList = null;
        }
        else {
            colorStateList = obtainStyledAttributes2.getColorStateList(R.styleable.PagerSlidingTabStrip_pstsTabTextColor);
        }
        this.mTabTextColor = colorStateList;
        this.mTabTextTypefaceStyle = obtainStyledAttributes2.getInt(R.styleable.PagerSlidingTabStrip_pstsTabTextStyle, this.mTabTextTypefaceStyle);
        this.isTabTextAllCaps = obtainStyledAttributes2.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTabTextAllCaps, this.isTabTextAllCaps);
        n = obtainStyledAttributes2.getInt(R.styleable.PagerSlidingTabStrip_pstsTabTextAlpha, 150);
        String string = obtainStyledAttributes2.getString(R.styleable.PagerSlidingTabStrip_pstsTabTextFontFamily);
        obtainStyledAttributes2.recycle();
        if (this.mTabTextColor == null) {
            this.mTabTextColor = this.createColorStateList(color, color, Color.argb(n, Color.red(color), Color.green(color), Color.blue(color)));
        }
        if (string == null) {
            string = s;
        }
        this.mTabTextTypeface = Typeface.create(string, this.mTabTextTypefaceStyle);
        this.setTabsContainerParentViewPaddings();
        LinearLayout$LayoutParams mTabLayoutParams;
        if (!this.isExpandTabs) {
            mTabLayoutParams = new LinearLayout$LayoutParams(-2, -1);
        }
        else {
            mTabLayoutParams = new LinearLayout$LayoutParams(0, -1, 1.0f);
        }
        this.mTabLayoutParams = mTabLayoutParams;
    }
    
    private void addTab(final int n, final CharSequence text, final View view) {
        final TextView textView = (TextView)view.findViewById(R.id.psts_tab_title);
        if (textView != null && text != null) {
            textView.setText(text);
        }
        view.setFocusable(true);
        view.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(View child) {
                if (PagerSlidingTabStrip.this.mPager.getCurrentItem() == n) {
                    if (PagerSlidingTabStrip.this.mTabReselectedListener != null) {
                        PagerSlidingTabStrip.this.mTabReselectedListener.onTabReselected(n);
                    }
                }
                else {
                    child = PagerSlidingTabStrip.this.mTabsContainer.getChildAt(PagerSlidingTabStrip.this.mPager.getCurrentItem());
                    PagerSlidingTabStrip.this.unSelect(child);
                    PagerSlidingTabStrip.this.mPager.setCurrentItem(n);
                }
            }
        });
        this.mTabsContainer.addView(view, n, (ViewGroup$LayoutParams)this.mTabLayoutParams);
    }
    
    private ColorStateList createColorStateList(final int n) {
        return new ColorStateList(new int[][] { new int[0] }, new int[] { n });
    }
    
    private ColorStateList createColorStateList(final int n, final int n2, final int n3) {
        return new ColorStateList(new int[][] { { 16842919 }, { 16842913 }, new int[0] }, new int[] { n, n2, n3 });
    }
    
    private void scrollToChild(int mScrollOffset, final int n) {
        if (this.mTabCount != 0) {
            final int n2 = this.mTabsContainer.getChildAt(mScrollOffset).getLeft() + n;
            if (mScrollOffset <= 0 && n <= 0) {
                mScrollOffset = n2;
            }
            else {
                mScrollOffset = this.mScrollOffset;
                final Pair<Float, Float> indicatorCoordinates = this.getIndicatorCoordinates();
                mScrollOffset = (int)((indicatorCoordinates.second - indicatorCoordinates.first) / 2.0f + (n2 - mScrollOffset));
            }
            if (mScrollOffset != this.mLastScrollX) {
                this.scrollTo(this.mLastScrollX = mScrollOffset, 0);
            }
        }
    }
    
    private void select(final View view) {
        if (view != null) {
            final TextView textView = (TextView)view.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                textView.setSelected(true);
            }
            if (this.isCustomTabs) {
                ((CustomTabProvider)this.mPager.getAdapter()).tabSelected(view);
            }
        }
    }
    
    private void setTabsContainerParentViewPaddings() {
        int n;
        if (this.mIndicatorHeight < this.mUnderlineHeight) {
            n = this.mUnderlineHeight;
        }
        else {
            n = this.mIndicatorHeight;
        }
        this.setPadding(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), n);
    }
    
    private void unSelect(final View view) {
        if (view != null) {
            final TextView textView = (TextView)view.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                textView.setSelected(false);
            }
            if (this.isCustomTabs) {
                ((CustomTabProvider)this.mPager.getAdapter()).tabUnselected(view);
            }
        }
    }
    
    private void updateSelection(final int n) {
        for (int i = 0; i < this.mTabCount; ++i) {
            final View child = this.mTabsContainer.getChildAt(i);
            int n2;
            if (i != n) {
                n2 = 0;
            }
            else {
                n2 = 1;
            }
            if (n2 == 0) {
                this.unSelect(child);
            }
            else {
                this.select(child);
            }
        }
    }
    
    private void updateTabStyles() {
        for (int i = 0; i < this.mTabCount; ++i) {
            final View child = this.mTabsContainer.getChildAt(i);
            child.setBackgroundResource(this.mTabBackgroundResId);
            child.setPadding(this.mTabPadding, child.getPaddingTop(), this.mTabPadding, child.getPaddingBottom());
            final TextView textView = (TextView)child.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                textView.setTextColor(this.mTabTextColor);
                textView.setTypeface(this.mTabTextTypeface, this.mTabTextTypefaceStyle);
                textView.setTextSize(0, (float)this.mTabTextSize);
                if (this.isTabTextAllCaps) {
                    if (Build$VERSION.SDK_INT < 14) {
                        textView.setText((CharSequence)textView.getText().toString().toUpperCase());
                    }
                    else {
                        textView.setAllCaps(true);
                    }
                }
            }
        }
    }
    
    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }
    
    public float getCurrentPositionOffset() {
        return this.mCurrentPositionOffset;
    }
    
    public int getDividerColor() {
        return this.mDividerColor;
    }
    
    public int getDividerPadding() {
        return this.mDividerPadding;
    }
    
    public int getDividerWidth() {
        return this.mDividerWidth;
    }
    
    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }
    
    public Pair<Float, Float> getIndicatorCoordinates() {
        final View child = this.mTabsContainer.getChildAt(this.mCurrentPosition);
        final float n = (float)child.getLeft();
        float f;
        final float n2 = f = (float)child.getRight();
        float f2 = n;
        if (this.mCurrentPositionOffset > 0.0f) {
            if (this.mCurrentPosition >= this.mTabCount - 1) {
                f2 = n;
                f = n2;
            }
            else {
                final View child2 = this.mTabsContainer.getChildAt(this.mCurrentPosition + 1);
                final float n3 = (float)child2.getLeft();
                final float n4 = (float)child2.getRight();
                f2 = n * (1.0f - this.mCurrentPositionOffset) + n3 * this.mCurrentPositionOffset;
                f = n2 * (1.0f - this.mCurrentPositionOffset) + n4 * this.mCurrentPositionOffset;
            }
        }
        return new Pair<Float, Float>(f2, f);
    }
    
    public int getIndicatorHeight() {
        return this.mIndicatorHeight;
    }
    
    public int getScrollOffset() {
        return this.mScrollOffset;
    }
    
    public boolean getShouldExpand() {
        return this.isExpandTabs;
    }
    
    public int getTabBackground() {
        return this.mTabBackgroundResId;
    }
    
    public int getTabCount() {
        return this.mTabCount;
    }
    
    public int getTabPaddingLeftRight() {
        return this.mTabPadding;
    }
    
    public LinearLayout getTabsContainer() {
        return this.mTabsContainer;
    }
    
    public ColorStateList getTextColor() {
        return this.mTabTextColor;
    }
    
    public int getTextSize() {
        return this.mTabTextSize;
    }
    
    public int getUnderlineColor() {
        return this.mUnderlineColor;
    }
    
    public int getUnderlineHeight() {
        return this.mUnderlineHeight;
    }
    
    public boolean isTextAllCaps() {
        return this.isTabTextAllCaps;
    }
    
    public void notifyDataSetChanged() {
        this.mTabsContainer.removeAllViews();
        this.mTabCount = this.mPager.getAdapter().getCount();
        for (int i = 0; i < this.mTabCount; ++i) {
            View view;
            if (!this.isCustomTabs) {
                view = LayoutInflater.from(this.getContext()).inflate(R.layout.psts_tab, (ViewGroup)this, false);
            }
            else {
                view = ((CustomTabProvider)this.mPager.getAdapter()).getCustomTabView((ViewGroup)this, i);
            }
            this.addTab(i, this.mPager.getAdapter().getPageTitle(i), view);
        }
        this.updateTabStyles();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mPager != null && !this.mAdapterObserver.isAttached()) {
            this.mPager.getAdapter().registerDataSetObserver(this.mAdapterObserver);
            this.mAdapterObserver.setAttached(true);
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPager != null && this.mAdapterObserver.isAttached()) {
            this.mPager.getAdapter().unregisterDataSetObserver(this.mAdapterObserver);
            this.mAdapterObserver.setAttached(false);
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (!this.isInEditMode() && this.mTabCount != 0) {
            final int height = this.getHeight();
            if (this.mDividerWidth > 0) {
                this.mDividerPaint.setStrokeWidth((float)this.mDividerWidth);
                this.mDividerPaint.setColor(this.mDividerColor);
                for (int i = 0; i < this.mTabCount - 1; ++i) {
                    final View child = this.mTabsContainer.getChildAt(i);
                    canvas.drawLine((float)child.getRight(), (float)this.mDividerPadding, (float)child.getRight(), (float)(height - this.mDividerPadding), this.mDividerPaint);
                }
            }
            if (this.mUnderlineHeight > 0) {
                this.mRectPaint.setColor(this.mUnderlineColor);
                canvas.drawRect((float)this.mPaddingLeft, (float)(height - this.mUnderlineHeight), (float)(this.mTabsContainer.getWidth() + this.mPaddingRight), (float)height, this.mRectPaint);
            }
            if (this.mIndicatorHeight > 0) {
                this.mRectPaint.setColor(this.mIndicatorColor);
                final Pair<Float, Float> indicatorCoordinates = this.getIndicatorCoordinates();
                canvas.drawRect(this.mPaddingLeft + indicatorCoordinates.first, (float)(height - this.mIndicatorHeight), this.mPaddingLeft + indicatorCoordinates.second, (float)height, this.mRectPaint);
            }
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (this.isPaddingMiddle && this.mTabsContainer.getChildCount() > 0) {
            final int n5 = this.getWidth() / 2 - this.mTabsContainer.getChildAt(0).getMeasuredWidth() / 2;
            this.mPaddingRight = n5;
            this.mPaddingLeft = n5;
        }
        if (this.isPaddingMiddle || this.mPaddingLeft > 0 || this.mPaddingRight > 0) {
            int width;
            if (!this.isPaddingMiddle) {
                width = this.getWidth() - this.mPaddingLeft - this.mPaddingRight;
            }
            else {
                width = this.getWidth();
            }
            this.mTabsContainer.setMinimumWidth(width);
            this.setClipToPadding(false);
        }
        this.setPadding(this.mPaddingLeft, this.getPaddingTop(), this.mPaddingRight, this.getPaddingBottom());
        if (this.mScrollOffset == 0) {
            this.mScrollOffset = this.getWidth() / 2 - this.mPaddingLeft;
        }
        if (this.mPager != null) {
            this.mCurrentPosition = this.mPager.getCurrentItem();
        }
        this.mCurrentPositionOffset = 0.0f;
        this.scrollToChild(this.mCurrentPosition, 0);
        this.updateSelection(this.mCurrentPosition);
        super.onLayout(b, n, n2, n3, n4);
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mCurrentPosition = savedState.currentPosition;
        if (this.mCurrentPosition != 0 && this.mTabsContainer.getChildCount() > 0) {
            this.unSelect(this.mTabsContainer.getChildAt(0));
            this.select(this.mTabsContainer.getChildAt(this.mCurrentPosition));
        }
        this.requestLayout();
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPosition = this.mCurrentPosition;
        return (Parcelable)savedState;
    }
    
    public void setAllCaps(final boolean isTabTextAllCaps) {
        this.isTabTextAllCaps = isTabTextAllCaps;
    }
    
    public void setDividerColor(final int mDividerColor) {
        this.mDividerColor = mDividerColor;
        this.invalidate();
    }
    
    public void setDividerColorResource(final int n) {
        this.mDividerColor = ContextCompat.getColor(this.getContext(), n);
        this.invalidate();
    }
    
    public void setDividerPadding(final int mDividerPadding) {
        this.mDividerPadding = mDividerPadding;
        this.invalidate();
    }
    
    public void setDividerWidth(final int mDividerWidth) {
        this.mDividerWidth = mDividerWidth;
        this.invalidate();
    }
    
    public void setIndicatorColor(final int mIndicatorColor) {
        this.mIndicatorColor = mIndicatorColor;
        this.invalidate();
    }
    
    public void setIndicatorColorResource(final int n) {
        this.mIndicatorColor = ContextCompat.getColor(this.getContext(), n);
        this.invalidate();
    }
    
    public void setIndicatorHeight(final int mIndicatorHeight) {
        this.mIndicatorHeight = mIndicatorHeight;
        this.invalidate();
    }
    
    public void setOnPageChangeListener(final ViewPager.OnPageChangeListener mDelegatePageListener) {
        this.mDelegatePageListener = mDelegatePageListener;
    }
    
    public void setOnTabReselectedListener(final OnTabReselectedListener mTabReselectedListener) {
        this.mTabReselectedListener = mTabReselectedListener;
    }
    
    public void setScrollOffset(final int mScrollOffset) {
        this.mScrollOffset = mScrollOffset;
        this.invalidate();
    }
    
    public void setShouldExpand(final boolean isExpandTabs) {
        this.isExpandTabs = isExpandTabs;
        if (this.mPager != null) {
            this.requestLayout();
        }
    }
    
    public void setTabBackground(final int mTabBackgroundResId) {
        this.mTabBackgroundResId = mTabBackgroundResId;
    }
    
    public void setTabPaddingLeftRight(final int mTabPadding) {
        this.mTabPadding = mTabPadding;
        this.updateTabStyles();
    }
    
    public void setTextColor(final int n) {
        this.setTextColor(this.createColorStateList(n));
    }
    
    public void setTextColor(final ColorStateList mTabTextColor) {
        this.mTabTextColor = mTabTextColor;
        this.updateTabStyles();
    }
    
    public void setTextColorResource(final int n) {
        this.setTextColor(ContextCompat.getColor(this.getContext(), n));
    }
    
    public void setTextColorStateListResource(final int n) {
        this.setTextColor(ContextCompat.getColorStateList(this.getContext(), n));
    }
    
    public void setTextSize(final int mTabTextSize) {
        this.mTabTextSize = mTabTextSize;
        this.updateTabStyles();
    }
    
    public void setTypeface(final Typeface mTabTextTypeface, final int mTabTextTypefaceStyle) {
        this.mTabTextTypeface = mTabTextTypeface;
        this.mTabTextTypefaceStyle = mTabTextTypefaceStyle;
        this.updateTabStyles();
    }
    
    public void setUnderlineColor(final int mUnderlineColor) {
        this.mUnderlineColor = mUnderlineColor;
        this.invalidate();
    }
    
    public void setUnderlineColorResource(final int n) {
        this.mUnderlineColor = ContextCompat.getColor(this.getContext(), n);
        this.invalidate();
    }
    
    public void setUnderlineHeight(final int mUnderlineHeight) {
        this.mUnderlineHeight = mUnderlineHeight;
        this.invalidate();
    }
    
    public void setViewPager(final ViewPager mPager) {
        this.mPager = mPager;
        if (mPager.getAdapter() != null) {
            this.isCustomTabs = (mPager.getAdapter() instanceof CustomTabProvider);
            mPager.addOnPageChangeListener((ViewPager.OnPageChangeListener)this.mPageListener);
            mPager.getAdapter().registerDataSetObserver(this.mAdapterObserver);
            this.mAdapterObserver.setAttached(true);
            this.notifyDataSetChanged();
            return;
        }
        throw new IllegalStateException("ViewPager does not have adapter instance.");
    }
    
    public interface CustomTabProvider
    {
        View getCustomTabView(final ViewGroup p0, final int p1);
        
        void tabSelected(final View p0);
        
        void tabUnselected(final View p0);
    }
    
    public interface OnTabReselectedListener
    {
        void onTabReselected(final int p0);
    }
    
    private class PageListener implements OnPageChangeListener
    {
        @Override
        public void onPageScrollStateChanged(final int n) {
            if (n == 0) {
                PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.mPager.getCurrentItem(), 0);
            }
            if (PagerSlidingTabStrip.this.mDelegatePageListener != null) {
                PagerSlidingTabStrip.this.mDelegatePageListener.onPageScrollStateChanged(n);
            }
        }
        
        @Override
        public void onPageScrolled(final int n, final float n2, final int n3) {
            int n4 = 0;
            PagerSlidingTabStrip.this.mCurrentPosition = n;
            PagerSlidingTabStrip.this.mCurrentPositionOffset = n2;
            if (PagerSlidingTabStrip.this.mTabCount > 0) {
                n4 = (int)(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(n).getWidth() * n2);
            }
            PagerSlidingTabStrip.this.scrollToChild(n, n4);
            PagerSlidingTabStrip.this.invalidate();
            if (PagerSlidingTabStrip.this.mDelegatePageListener != null) {
                PagerSlidingTabStrip.this.mDelegatePageListener.onPageScrolled(n, n2, n3);
            }
        }
        
        @Override
        public void onPageSelected(final int n) {
            PagerSlidingTabStrip.this.updateSelection(n);
            PagerSlidingTabStrip.this.select(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(n));
            if (n > 0) {
                PagerSlidingTabStrip.this.unSelect(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(n - 1));
            }
            if (n < PagerSlidingTabStrip.this.mPager.getAdapter().getCount() - 1) {
                PagerSlidingTabStrip.this.unSelect(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(n + 1));
            }
            if (PagerSlidingTabStrip.this.mDelegatePageListener != null) {
                PagerSlidingTabStrip.this.mDelegatePageListener.onPageSelected(n);
            }
        }
    }
    
    private class PagerAdapterObserver extends DataSetObserver
    {
        private boolean attached;
        
        private PagerAdapterObserver() {
            this.attached = false;
        }
        
        boolean isAttached() {
            return this.attached;
        }
        
        public void onChanged() {
            PagerSlidingTabStrip.this.notifyDataSetChanged();
        }
        
        void setAttached(final boolean attached) {
            this.attached = attached;
        }
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int currentPosition;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        private SavedState(final Parcel parcel) {
            super(parcel);
            this.currentPosition = parcel.readInt();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.currentPosition);
        }
    }
}
