package com.astuetz;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.astuetz.pagerslidingtabstrip.R;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    private static final int[] ANDROID_ATTRS = {16842806, 16842965, 16842966, 16842968};
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
    /* access modifiers changed from: private */
    public int mCurrentPosition;
    /* access modifiers changed from: private */
    public float mCurrentPositionOffset;
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
    /* access modifiers changed from: private */
    public ViewPager mPager;
    private Paint mRectPaint;
    private int mScrollOffset;
    private int mTabBackgroundResId;
    /* access modifiers changed from: private */
    public int mTabCount;
    private LinearLayout.LayoutParams mTabLayoutParams;
    private int mTabPadding;
    /* access modifiers changed from: private */
    public OnTabReselectedListener mTabReselectedListener;
    private ColorStateList mTabTextColor;
    private int mTabTextSize;
    private Typeface mTabTextTypeface;
    private int mTabTextTypefaceStyle;
    /* access modifiers changed from: private */
    public LinearLayout mTabsContainer;
    private int mUnderlineColor;
    private int mUnderlineHeight;

    public interface CustomTabProvider {
        View getCustomTabView(ViewGroup viewGroup, int i);

        void tabSelected(View view);

        void tabUnselected(View view);
    }

    public interface OnTabReselectedListener {
        void onTabReselected(int i);
    }

    private class PageListener implements ViewPager.OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrollStateChanged(int i) {
            if (i == 0) {
                PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.mPager.getCurrentItem(), 0);
            }
            if (PagerSlidingTabStrip.this.mDelegatePageListener != null) {
                PagerSlidingTabStrip.this.mDelegatePageListener.onPageScrollStateChanged(i);
            }
        }

        public void onPageScrolled(int i, float f, int i2) {
            int i3 = 0;
            int unused = PagerSlidingTabStrip.this.mCurrentPosition = i;
            float unused2 = PagerSlidingTabStrip.this.mCurrentPositionOffset = f;
            if (PagerSlidingTabStrip.this.mTabCount > 0) {
                i3 = (int) (((float) PagerSlidingTabStrip.this.mTabsContainer.getChildAt(i).getWidth()) * f);
            }
            PagerSlidingTabStrip.this.scrollToChild(i, i3);
            PagerSlidingTabStrip.this.invalidate();
            if (PagerSlidingTabStrip.this.mDelegatePageListener != null) {
                PagerSlidingTabStrip.this.mDelegatePageListener.onPageScrolled(i, f, i2);
            }
        }

        public void onPageSelected(int i) {
            PagerSlidingTabStrip.this.updateSelection(i);
            PagerSlidingTabStrip.this.select(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(i));
            if (i > 0) {
                PagerSlidingTabStrip.this.unSelect(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(i - 1));
            }
            if (i < PagerSlidingTabStrip.this.mPager.getAdapter().getCount() - 1) {
                PagerSlidingTabStrip.this.unSelect(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(i + 1));
            }
            if (PagerSlidingTabStrip.this.mDelegatePageListener != null) {
                PagerSlidingTabStrip.this.mDelegatePageListener.onPageSelected(i);
            }
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        private boolean attached;

        private PagerAdapterObserver() {
            this.attached = false;
        }

        /* access modifiers changed from: package-private */
        public boolean isAttached() {
            return this.attached;
        }

        public void onChanged() {
            PagerSlidingTabStrip.this.notifyDataSetChanged();
        }

        /* access modifiers changed from: package-private */
        public void setAttached(boolean z) {
            this.attached = z;
        }
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int currentPosition;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.currentPosition = parcel.readInt();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.currentPosition);
        }
    }

    public PagerSlidingTabStrip(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
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
        setFillViewport(true);
        setWillNotDraw(false);
        this.mTabsContainer = new LinearLayout(context);
        this.mTabsContainer.setOrientation(0);
        addView(this.mTabsContainer);
        this.mRectPaint = new Paint();
        this.mRectPaint.setAntiAlias(true);
        this.mRectPaint.setStyle(Paint.Style.FILL);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mScrollOffset = (int) TypedValue.applyDimension(1, (float) this.mScrollOffset, displayMetrics);
        this.mIndicatorHeight = (int) TypedValue.applyDimension(1, (float) this.mIndicatorHeight, displayMetrics);
        this.mUnderlineHeight = (int) TypedValue.applyDimension(1, (float) this.mUnderlineHeight, displayMetrics);
        this.mDividerPadding = (int) TypedValue.applyDimension(1, (float) this.mDividerPadding, displayMetrics);
        this.mTabPadding = (int) TypedValue.applyDimension(1, (float) this.mTabPadding, displayMetrics);
        this.mDividerWidth = (int) TypedValue.applyDimension(1, (float) this.mDividerWidth, displayMetrics);
        this.mTabTextSize = (int) TypedValue.applyDimension(2, (float) this.mTabTextSize, displayMetrics);
        this.mDividerPaint = new Paint();
        this.mDividerPaint.setAntiAlias(true);
        this.mDividerPaint.setStrokeWidth((float) this.mDividerWidth);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ANDROID_ATTRS);
        int color = obtainStyledAttributes.getColor(0, ContextCompat.getColor(context, 17170444));
        this.mUnderlineColor = color;
        this.mDividerColor = color;
        this.mIndicatorColor = color;
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        this.mPaddingLeft = dimensionPixelSize <= 0 ? obtainStyledAttributes.getDimensionPixelSize(2, 0) : dimensionPixelSize;
        this.mPaddingRight = dimensionPixelSize <= 0 ? obtainStyledAttributes.getDimensionPixelSize(3, 0) : dimensionPixelSize;
        obtainStyledAttributes.recycle();
        String str = "sans-serif";
        if (Build.VERSION.SDK_INT >= 21) {
            str = "sans-serif-medium";
            this.mTabTextTypefaceStyle = 0;
        }
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.PagerSlidingTabStrip);
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
        this.mTabTextColor = !obtainStyledAttributes2.hasValue(R.styleable.PagerSlidingTabStrip_pstsTabTextColor) ? null : obtainStyledAttributes2.getColorStateList(R.styleable.PagerSlidingTabStrip_pstsTabTextColor);
        this.mTabTextTypefaceStyle = obtainStyledAttributes2.getInt(R.styleable.PagerSlidingTabStrip_pstsTabTextStyle, this.mTabTextTypefaceStyle);
        this.isTabTextAllCaps = obtainStyledAttributes2.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTabTextAllCaps, this.isTabTextAllCaps);
        int i2 = obtainStyledAttributes2.getInt(R.styleable.PagerSlidingTabStrip_pstsTabTextAlpha, DEF_VALUE_TAB_TEXT_ALPHA);
        String string = obtainStyledAttributes2.getString(R.styleable.PagerSlidingTabStrip_pstsTabTextFontFamily);
        obtainStyledAttributes2.recycle();
        if (this.mTabTextColor == null) {
            this.mTabTextColor = createColorStateList(color, color, Color.argb(i2, Color.red(color), Color.green(color), Color.blue(color)));
        }
        this.mTabTextTypeface = Typeface.create(string != null ? string : str, this.mTabTextTypefaceStyle);
        setTabsContainerParentViewPaddings();
        this.mTabLayoutParams = !this.isExpandTabs ? new LinearLayout.LayoutParams(-2, -1) : new LinearLayout.LayoutParams(0, -1, 1.0f);
    }

    private void addTab(final int i, CharSequence charSequence, View view) {
        TextView textView = (TextView) view.findViewById(R.id.psts_tab_title);
        if (!(textView == null || charSequence == null)) {
            textView.setText(charSequence);
        }
        view.setFocusable(true);
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PagerSlidingTabStrip.this.mPager.getCurrentItem() != i) {
                    PagerSlidingTabStrip.this.unSelect(PagerSlidingTabStrip.this.mTabsContainer.getChildAt(PagerSlidingTabStrip.this.mPager.getCurrentItem()));
                    PagerSlidingTabStrip.this.mPager.setCurrentItem(i);
                } else if (PagerSlidingTabStrip.this.mTabReselectedListener != null) {
                    PagerSlidingTabStrip.this.mTabReselectedListener.onTabReselected(i);
                }
            }
        });
        this.mTabsContainer.addView(view, i, this.mTabLayoutParams);
    }

    private ColorStateList createColorStateList(int i) {
        return new ColorStateList(new int[][]{new int[0]}, new int[]{i});
    }

    private ColorStateList createColorStateList(int i, int i2, int i3) {
        return new ColorStateList(new int[][]{new int[]{16842919}, new int[]{16842913}, new int[0]}, new int[]{i, i2, i3});
    }

    /* access modifiers changed from: private */
    public void scrollToChild(int i, int i2) {
        if (this.mTabCount != 0) {
            int left = this.mTabsContainer.getChildAt(i).getLeft() + i2;
            if (i > 0 || i2 > 0) {
                int i3 = left - this.mScrollOffset;
                Pair<Float, Float> indicatorCoordinates = getIndicatorCoordinates();
                left = (int) (((((Float) indicatorCoordinates.second).floatValue() - ((Float) indicatorCoordinates.first).floatValue()) / 2.0f) + ((float) i3));
            }
            if (left != this.mLastScrollX) {
                this.mLastScrollX = left;
                scrollTo(left, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void select(View view) {
        if (view != null) {
            TextView textView = (TextView) view.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                textView.setSelected(true);
            }
            if (this.isCustomTabs) {
                ((CustomTabProvider) this.mPager.getAdapter()).tabSelected(view);
            }
        }
    }

    private void setTabsContainerParentViewPaddings() {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), this.mIndicatorHeight < this.mUnderlineHeight ? this.mUnderlineHeight : this.mIndicatorHeight);
    }

    /* access modifiers changed from: private */
    public void unSelect(View view) {
        if (view != null) {
            TextView textView = (TextView) view.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                textView.setSelected(false);
            }
            if (this.isCustomTabs) {
                ((CustomTabProvider) this.mPager.getAdapter()).tabUnselected(view);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSelection(int i) {
        int i2 = 0;
        while (i2 < this.mTabCount) {
            View childAt = this.mTabsContainer.getChildAt(i2);
            if (!(i2 == i)) {
                unSelect(childAt);
            } else {
                select(childAt);
            }
            i2++;
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.mTabCount; i++) {
            View childAt = this.mTabsContainer.getChildAt(i);
            childAt.setBackgroundResource(this.mTabBackgroundResId);
            childAt.setPadding(this.mTabPadding, childAt.getPaddingTop(), this.mTabPadding, childAt.getPaddingBottom());
            TextView textView = (TextView) childAt.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                textView.setTextColor(this.mTabTextColor);
                textView.setTypeface(this.mTabTextTypeface, this.mTabTextTypefaceStyle);
                textView.setTextSize(0, (float) this.mTabTextSize);
                if (this.isTabTextAllCaps) {
                    if (Build.VERSION.SDK_INT < 14) {
                        textView.setText(textView.getText().toString().toUpperCase());
                    } else {
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
        View childAt = this.mTabsContainer.getChildAt(this.mCurrentPosition);
        float left = (float) childAt.getLeft();
        float right = (float) childAt.getRight();
        if (this.mCurrentPositionOffset > 0.0f && this.mCurrentPosition < this.mTabCount - 1) {
            View childAt2 = this.mTabsContainer.getChildAt(this.mCurrentPosition + 1);
            left = (left * (1.0f - this.mCurrentPositionOffset)) + (((float) childAt2.getLeft()) * this.mCurrentPositionOffset);
            right = (right * (1.0f - this.mCurrentPositionOffset)) + (((float) childAt2.getRight()) * this.mCurrentPositionOffset);
        }
        return new Pair<>(Float.valueOf(left), Float.valueOf(right));
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
        for (int i = 0; i < this.mTabCount; i++) {
            addTab(i, this.mPager.getAdapter().getPageTitle(i), !this.isCustomTabs ? LayoutInflater.from(getContext()).inflate(R.layout.psts_tab, this, false) : ((CustomTabProvider) this.mPager.getAdapter()).getCustomTabView(this, i));
        }
        updateTabStyles();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mPager != null && !this.mAdapterObserver.isAttached()) {
            this.mPager.getAdapter().registerDataSetObserver(this.mAdapterObserver);
            this.mAdapterObserver.setAttached(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPager != null && this.mAdapterObserver.isAttached()) {
            this.mPager.getAdapter().unregisterDataSetObserver(this.mAdapterObserver);
            this.mAdapterObserver.setAttached(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = 0;
        super.onDraw(canvas);
        if (!isInEditMode() && this.mTabCount != 0) {
            int height = getHeight();
            if (this.mDividerWidth > 0) {
                this.mDividerPaint.setStrokeWidth((float) this.mDividerWidth);
                this.mDividerPaint.setColor(this.mDividerColor);
                while (true) {
                    int i2 = i;
                    if (i2 >= this.mTabCount - 1) {
                        break;
                    }
                    View childAt = this.mTabsContainer.getChildAt(i2);
                    canvas.drawLine((float) childAt.getRight(), (float) this.mDividerPadding, (float) childAt.getRight(), (float) (height - this.mDividerPadding), this.mDividerPaint);
                    i = i2 + 1;
                }
            }
            if (this.mUnderlineHeight > 0) {
                this.mRectPaint.setColor(this.mUnderlineColor);
                canvas.drawRect((float) this.mPaddingLeft, (float) (height - this.mUnderlineHeight), (float) (this.mTabsContainer.getWidth() + this.mPaddingRight), (float) height, this.mRectPaint);
            }
            if (this.mIndicatorHeight > 0) {
                this.mRectPaint.setColor(this.mIndicatorColor);
                Pair<Float, Float> indicatorCoordinates = getIndicatorCoordinates();
                Canvas canvas2 = canvas;
                canvas2.drawRect(((float) this.mPaddingLeft) + ((Float) indicatorCoordinates.first).floatValue(), (float) (height - this.mIndicatorHeight), ((float) this.mPaddingLeft) + ((Float) indicatorCoordinates.second).floatValue(), (float) height, this.mRectPaint);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.isPaddingMiddle && this.mTabsContainer.getChildCount() > 0) {
            int width = (getWidth() / 2) - (this.mTabsContainer.getChildAt(0).getMeasuredWidth() / 2);
            this.mPaddingRight = width;
            this.mPaddingLeft = width;
        }
        if (this.isPaddingMiddle || this.mPaddingLeft > 0 || this.mPaddingRight > 0) {
            this.mTabsContainer.setMinimumWidth(!this.isPaddingMiddle ? (getWidth() - this.mPaddingLeft) - this.mPaddingRight : getWidth());
            setClipToPadding(false);
        }
        setPadding(this.mPaddingLeft, getPaddingTop(), this.mPaddingRight, getPaddingBottom());
        if (this.mScrollOffset == 0) {
            this.mScrollOffset = (getWidth() / 2) - this.mPaddingLeft;
        }
        if (this.mPager != null) {
            this.mCurrentPosition = this.mPager.getCurrentItem();
        }
        this.mCurrentPositionOffset = 0.0f;
        scrollToChild(this.mCurrentPosition, 0);
        updateSelection(this.mCurrentPosition);
        super.onLayout(z, i, i2, i3, i4);
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mCurrentPosition = savedState.currentPosition;
        if (this.mCurrentPosition != 0 && this.mTabsContainer.getChildCount() > 0) {
            unSelect(this.mTabsContainer.getChildAt(0));
            select(this.mTabsContainer.getChildAt(this.mCurrentPosition));
        }
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPosition = this.mCurrentPosition;
        return savedState;
    }

    public void setAllCaps(boolean z) {
        this.isTabTextAllCaps = z;
    }

    public void setDividerColor(int i) {
        this.mDividerColor = i;
        invalidate();
    }

    public void setDividerColorResource(int i) {
        this.mDividerColor = ContextCompat.getColor(getContext(), i);
        invalidate();
    }

    public void setDividerPadding(int i) {
        this.mDividerPadding = i;
        invalidate();
    }

    public void setDividerWidth(int i) {
        this.mDividerWidth = i;
        invalidate();
    }

    public void setIndicatorColor(int i) {
        this.mIndicatorColor = i;
        invalidate();
    }

    public void setIndicatorColorResource(int i) {
        this.mIndicatorColor = ContextCompat.getColor(getContext(), i);
        invalidate();
    }

    public void setIndicatorHeight(int i) {
        this.mIndicatorHeight = i;
        invalidate();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mDelegatePageListener = onPageChangeListener;
    }

    public void setOnTabReselectedListener(OnTabReselectedListener onTabReselectedListener) {
        this.mTabReselectedListener = onTabReselectedListener;
    }

    public void setScrollOffset(int i) {
        this.mScrollOffset = i;
        invalidate();
    }

    public void setShouldExpand(boolean z) {
        this.isExpandTabs = z;
        if (this.mPager != null) {
            requestLayout();
        }
    }

    public void setTabBackground(int i) {
        this.mTabBackgroundResId = i;
    }

    public void setTabPaddingLeftRight(int i) {
        this.mTabPadding = i;
        updateTabStyles();
    }

    public void setTextColor(int i) {
        setTextColor(createColorStateList(i));
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mTabTextColor = colorStateList;
        updateTabStyles();
    }

    public void setTextColorResource(int i) {
        setTextColor(ContextCompat.getColor(getContext(), i));
    }

    public void setTextColorStateListResource(int i) {
        setTextColor(ContextCompat.getColorStateList(getContext(), i));
    }

    public void setTextSize(int i) {
        this.mTabTextSize = i;
        updateTabStyles();
    }

    public void setTypeface(Typeface typeface, int i) {
        this.mTabTextTypeface = typeface;
        this.mTabTextTypefaceStyle = i;
        updateTabStyles();
    }

    public void setUnderlineColor(int i) {
        this.mUnderlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.mUnderlineColor = ContextCompat.getColor(getContext(), i);
        invalidate();
    }

    public void setUnderlineHeight(int i) {
        this.mUnderlineHeight = i;
        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {
        this.mPager = viewPager;
        if (viewPager.getAdapter() != null) {
            this.isCustomTabs = viewPager.getAdapter() instanceof CustomTabProvider;
            viewPager.addOnPageChangeListener(this.mPageListener);
            viewPager.getAdapter().registerDataSetObserver(this.mAdapterObserver);
            this.mAdapterObserver.setAttached(true);
            notifyDataSetChanged();
            return;
        }
        throw new IllegalStateException("ViewPager does not have adapter instance.");
    }
}
