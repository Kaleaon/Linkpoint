// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.content.res.TypedArray;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import android.support.v4.content.ContextCompat;
import android.support.annotation.DrawableRes;
import android.database.DataSetObserver;
import android.content.res.Resources$NotFoundException;
import android.support.annotation.CallSuper;
import android.view.View$MeasureSpec;
import android.view.ViewConfiguration;
import android.graphics.Canvas;
import android.view.accessibility.AccessibilityEvent;
import android.view.KeyEvent;
import android.os.SystemClock;
import android.view.SoundEffectConstants;
import android.view.FocusFinder;
import android.util.Log;
import android.view.ViewGroup$LayoutParams;
import java.util.Collections;
import android.view.MotionEvent;
import android.support.annotation.NonNull;
import android.view.ViewParent;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.content.Context;
import android.view.VelocityTracker;
import android.graphics.Rect;
import android.widget.Scroller;
import android.os.Parcelable;
import android.graphics.drawable.Drawable;
import android.widget.EdgeEffect;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.view.animation.Interpolator;
import java.util.Comparator;
import android.view.ViewGroup;

public class ViewPager extends ViewGroup
{
    private static final int CLOSE_ENOUGH = 2;
    private static final Comparator<ItemInfo> COMPARATOR;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_GUTTER_SIZE = 16;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private static final int INVALID_POINTER = -1;
    static final int[] LAYOUT_ATTRS;
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MIN_FLING_VELOCITY = 400;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "ViewPager";
    private static final boolean USE_CACHE = false;
    private static final Interpolator sInterpolator;
    private static final ViewPositionComparator sPositionComparator;
    private int mActivePointerId;
    PagerAdapter mAdapter;
    private List<OnAdapterChangeListener> mAdapterChangeListeners;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCloseEnough;
    int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable;
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout;
    private float mFirstOffset;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsScrollStarted;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset;
    private EdgeEffect mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mNeedCalculatePageOffsets;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit;
    private OnPageChangeListener mOnPageChangeListener;
    private List<OnPageChangeListener> mOnPageChangeListeners;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private int mPageTransformerLayerType;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private int mRestoredCurItem;
    private EdgeEffect mRightEdge;
    private int mScrollState;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private final ItemInfo mTempItem;
    private final Rect mTempRect;
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    
    static {
        LAYOUT_ATTRS = new int[] { 16842931 };
        COMPARATOR = new Comparator<ItemInfo>() {
            @Override
            public int compare(final ItemInfo itemInfo, final ItemInfo itemInfo2) {
                return itemInfo.position - itemInfo2.position;
            }
        };
        sInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(float n) {
                --n;
                return n * (n * n * n * n) + 1.0f;
            }
        };
        sPositionComparator = new ViewPositionComparator();
    }
    
    public ViewPager(final Context context) {
        super(context);
        this.mItems = new ArrayList<ItemInfo>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.initViewPager();
    }
    
    public ViewPager(final Context context, final AttributeSet set) {
        super(context, set);
        this.mItems = new ArrayList<ItemInfo>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.initViewPager();
    }
    
    private void calculatePageOffsets(ItemInfo itemInfo, int i, ItemInfo itemInfo2) {
        final int count = this.mAdapter.getCount();
        final int clientWidth = this.getClientWidth();
        float n;
        if (clientWidth <= 0) {
            n = 0.0f;
        }
        else {
            n = this.mPageMargin / (float)clientWidth;
        }
        if (itemInfo2 != null) {
            int j = itemInfo2.position;
            if (j >= itemInfo.position) {
                if (j > itemInfo.position) {
                    int size = this.mItems.size();
                    float offset = itemInfo2.offset;
                    --size;
                    --j;
                    while (j >= itemInfo.position && size >= 0) {
                        for (itemInfo2 = this.mItems.get(size); j < itemInfo2.position && size > 0; --size, itemInfo2 = this.mItems.get(size)) {}
                        while (j > itemInfo2.position) {
                            final float pageWidth = this.mAdapter.getPageWidth(j);
                            --j;
                            offset -= pageWidth + n;
                        }
                        offset -= itemInfo2.widthFactor + n;
                        itemInfo2.offset = offset;
                        --j;
                    }
                }
            }
            else {
                float offset2 = itemInfo2.offset + itemInfo2.widthFactor + n;
                int n2 = 0;
                ++j;
                while (j <= itemInfo.position && n2 < this.mItems.size()) {
                    for (itemInfo2 = this.mItems.get(n2); j > itemInfo2.position && n2 < this.mItems.size() - 1; ++n2, itemInfo2 = this.mItems.get(n2)) {}
                    while (j < itemInfo2.position) {
                        final float pageWidth2 = this.mAdapter.getPageWidth(j);
                        ++j;
                        offset2 += pageWidth2 + n;
                    }
                    itemInfo2.offset = offset2;
                    offset2 += itemInfo2.widthFactor + n;
                    ++j;
                }
            }
        }
        final int size2 = this.mItems.size();
        final float offset3 = itemInfo.offset;
        int k = itemInfo.position - 1;
        float offset4;
        if (itemInfo.position != 0) {
            offset4 = -3.4028235E38f;
        }
        else {
            offset4 = itemInfo.offset;
        }
        this.mFirstOffset = offset4;
        float mLastOffset;
        if (itemInfo.position != count - 1) {
            mLastOffset = Float.MAX_VALUE;
        }
        else {
            mLastOffset = itemInfo.offset + itemInfo.widthFactor - 1.0f;
        }
        this.mLastOffset = mLastOffset;
        int l = i - 1;
        float n3 = offset3;
        while (l >= 0) {
            for (itemInfo2 = this.mItems.get(l); k > itemInfo2.position; --k) {
                n3 -= this.mAdapter.getPageWidth(k) + n;
            }
            n3 -= itemInfo2.widthFactor + n;
            itemInfo2.offset = n3;
            if (itemInfo2.position == 0) {
                this.mFirstOffset = n3;
            }
            --k;
            --l;
        }
        float offset5 = itemInfo.offset + itemInfo.widthFactor + n;
        final int n4 = itemInfo.position + 1;
        int index = i + 1;
        i = n4;
        while (index < size2) {
            for (itemInfo = this.mItems.get(index); i < itemInfo.position; ++i) {
                offset5 += this.mAdapter.getPageWidth(i) + n;
            }
            if (itemInfo.position == count - 1) {
                this.mLastOffset = itemInfo.widthFactor + offset5 - 1.0f;
            }
            itemInfo.offset = offset5;
            offset5 += itemInfo.widthFactor + n;
            ++i;
            ++index;
        }
        this.mNeedCalculatePageOffsets = false;
    }
    
    private void completeScroll(final boolean b) {
        boolean b2;
        if (this.mScrollState != 2) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if ((b2 ? 1 : 0) != 0) {
            this.setScrollingCacheEnabled(false);
            int n;
            if (this.mScroller.isFinished()) {
                n = 0;
            }
            else {
                n = 1;
            }
            if (n != 0) {
                this.mScroller.abortAnimation();
                final int scrollX = this.getScrollX();
                final int scrollY = this.getScrollY();
                final int currX = this.mScroller.getCurrX();
                final int currY = this.mScroller.getCurrY();
                if (scrollX != currX || scrollY != currY) {
                    this.scrollTo(currX, currY);
                    if (currX != scrollX) {
                        this.pageScrolled(currX);
                    }
                }
            }
        }
        this.mPopulatePending = false;
        final int n2 = 0;
        int n3 = b2 ? 1 : 0;
        for (int i = n2; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.scrolling) {
                itemInfo.scrolling = false;
                n3 = 1;
            }
        }
        if (n3 != 0) {
            if (!b) {
                this.mEndScrollRunnable.run();
            }
            else {
                ViewCompat.postOnAnimation((View)this, this.mEndScrollRunnable);
            }
        }
    }
    
    private int determineTargetPage(final int n, final float n2, final int a, int max) {
        if (Math.abs(max) > this.mFlingDistance && Math.abs(a) > this.mMinimumVelocity) {
            max = n;
            if (a <= 0) {
                max = n + 1;
            }
        }
        else {
            float n3;
            if (n < this.mCurItem) {
                n3 = 0.6f;
            }
            else {
                n3 = 0.4f;
            }
            max = n + (int)(n3 + n2);
        }
        if (this.mItems.size() > 0) {
            max = Math.max(this.mItems.get(0).position, Math.min(max, this.mItems.get(this.mItems.size() - 1).position));
        }
        return max;
    }
    
    private void dispatchOnPageScrolled(final int n, final float n2, final int n3) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrolled(n, n2, n3);
        }
        if (this.mOnPageChangeListeners != null) {
            for (int size = this.mOnPageChangeListeners.size(), i = 0; i < size; ++i) {
                final OnPageChangeListener onPageChangeListener = this.mOnPageChangeListeners.get(i);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(n, n2, n3);
                }
            }
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrolled(n, n2, n3);
        }
    }
    
    private void dispatchOnPageSelected(final int n) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageSelected(n);
        }
        if (this.mOnPageChangeListeners != null) {
            for (int size = this.mOnPageChangeListeners.size(), i = 0; i < size; ++i) {
                final OnPageChangeListener onPageChangeListener = this.mOnPageChangeListeners.get(i);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(n);
                }
            }
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageSelected(n);
        }
    }
    
    private void dispatchOnScrollStateChanged(final int n) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrollStateChanged(n);
        }
        if (this.mOnPageChangeListeners != null) {
            for (int size = this.mOnPageChangeListeners.size(), i = 0; i < size; ++i) {
                final OnPageChangeListener onPageChangeListener = this.mOnPageChangeListeners.get(i);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(n);
                }
            }
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrollStateChanged(n);
        }
    }
    
    private void enableLayers(final boolean b) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            int mPageTransformerLayerType;
            if (!b) {
                mPageTransformerLayerType = 0;
            }
            else {
                mPageTransformerLayerType = this.mPageTransformerLayerType;
            }
            this.getChildAt(i).setLayerType(mPageTransformerLayerType, (Paint)null);
        }
    }
    
    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    private Rect getChildRectInPagerCoordinates(Rect rect, final View view) {
        if (rect == null) {
            rect = new Rect();
        }
        if (view != null) {
            rect.left = view.getLeft();
            rect.right = view.getRight();
            rect.top = view.getTop();
            rect.bottom = view.getBottom();
            ViewGroup viewGroup;
            for (ViewParent viewParent = view.getParent(); viewParent instanceof ViewGroup && viewParent != this; viewParent = viewGroup.getParent()) {
                viewGroup = (ViewGroup)viewParent;
                rect.left += viewGroup.getLeft();
                rect.right += viewGroup.getRight();
                rect.top += viewGroup.getTop();
                rect.bottom += viewGroup.getBottom();
            }
            return rect;
        }
        rect.set(0, 0, 0, 0);
        return rect;
    }
    
    private int getClientWidth() {
        return this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }
    
    private ItemInfo infoForCurrentScrollPosition() {
        final int clientWidth = this.getClientWidth();
        float n;
        if (clientWidth <= 0) {
            n = 0.0f;
        }
        else {
            n = this.getScrollX() / (float)clientWidth;
        }
        float n2;
        if (clientWidth <= 0) {
            n2 = 0.0f;
        }
        else {
            n2 = this.mPageMargin / (float)clientWidth;
        }
        int n3 = 1;
        float widthFactor = 0.0f;
        float offset = 0.0f;
        int position = -1;
        ItemInfo itemInfo = null;
        ItemInfo mTempItem;
        for (int i = 0; i < this.mItems.size(); ++i, itemInfo = mTempItem) {
            mTempItem = this.mItems.get(i);
            if (n3 == 0 && mTempItem.position != position + 1) {
                mTempItem = this.mTempItem;
                mTempItem.offset = widthFactor + offset + n2;
                mTempItem.position = position + 1;
                mTempItem.widthFactor = this.mAdapter.getPageWidth(mTempItem.position);
                --i;
            }
            offset = mTempItem.offset;
            final float widthFactor2 = mTempItem.widthFactor;
            if (n3 == 0 && n < offset) {
                return itemInfo;
            }
            boolean b;
            if (n < widthFactor2 + offset + n2) {
                b = true;
            }
            else {
                b = false;
            }
            if (b || i == this.mItems.size() - 1) {
                return mTempItem;
            }
            position = mTempItem.position;
            widthFactor = mTempItem.widthFactor;
            n3 = 0;
        }
        return itemInfo;
    }
    
    private static boolean isDecorView(@NonNull final View view) {
        return view.getClass().getAnnotation(DecorView.class) != null;
    }
    
    private boolean isGutterDrag(final float n, final float n2) {
        final boolean b = false;
        if (n < this.mGutterSize) {
            int n3;
            if (n2 > 0.0f) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
                return true;
            }
        }
        boolean b2 = b;
        if (n <= this.getWidth() - this.mGutterSize) {
            return b2;
        }
        b2 = b;
        if (n2 >= 0.0f) {
            return b2;
        }
        b2 = true;
        return b2;
    }
    
    private void onSecondaryPointerUp(final MotionEvent motionEvent) {
        int n = 0;
        final int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            if (actionIndex == 0) {
                n = 1;
            }
            this.mLastMotionX = motionEvent.getX(n);
            this.mActivePointerId = motionEvent.getPointerId(n);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }
    
    private boolean pageScrolled(int n) {
        if (this.mItems.size() != 0) {
            final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
            final int clientWidth = this.getClientWidth();
            final int mPageMargin = this.mPageMargin;
            final float n2 = this.mPageMargin / (float)clientWidth;
            final int position = infoForCurrentScrollPosition.position;
            final float n3 = (n / (float)clientWidth - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + n2);
            n = (int)((mPageMargin + clientWidth) * n3);
            this.mCalledSuper = false;
            this.onPageScrolled(position, n3, n);
            if (this.mCalledSuper) {
                return true;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
        else {
            if (this.mFirstLayout) {
                return false;
            }
            this.mCalledSuper = false;
            this.onPageScrolled(0, 0.0f, 0);
            if (this.mCalledSuper) {
                return false;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
    }
    
    private boolean performDrag(float mLastMotionX) {
        final boolean b = false;
        boolean b2 = false;
        final float mLastMotionX2 = this.mLastMotionX;
        this.mLastMotionX = mLastMotionX;
        final float n = this.getScrollX() + (mLastMotionX2 - mLastMotionX);
        final int clientWidth = this.getClientWidth();
        mLastMotionX = clientWidth * this.mFirstOffset;
        final float n2 = (float)clientWidth;
        final float mLastOffset = this.mLastOffset;
        final ItemInfo itemInfo = this.mItems.get(0);
        final ItemInfo itemInfo2 = this.mItems.get(this.mItems.size() - 1);
        int n3;
        if (itemInfo.position == 0) {
            n3 = 1;
        }
        else {
            mLastMotionX = itemInfo.offset * clientWidth;
            n3 = 0;
        }
        float n4;
        boolean b3;
        if (itemInfo2.position == this.mAdapter.getCount() - 1) {
            n4 = n2 * mLastOffset;
            b3 = true;
        }
        else {
            n4 = itemInfo2.offset * clientWidth;
            b3 = false;
        }
        if (n < mLastMotionX) {
            if (n3 != 0) {
                this.mLeftEdge.onPull(Math.abs(mLastMotionX - n) / clientWidth);
                b2 = true;
            }
        }
        else if (n > n4) {
            if (!b3) {
                b2 = b;
            }
            else {
                this.mRightEdge.onPull(Math.abs(n - n4) / clientWidth);
                b2 = true;
            }
            mLastMotionX = n4;
        }
        else {
            mLastMotionX = n;
        }
        this.mLastMotionX += mLastMotionX - (int)mLastMotionX;
        this.scrollTo((int)mLastMotionX, this.getScrollY());
        this.pageScrolled((int)mLastMotionX);
        return b2;
    }
    
    private void recomputeScrollPosition(int n, final int n2, final int n3, final int n4) {
        if (n2 > 0 && !this.mItems.isEmpty()) {
            if (this.mScroller.isFinished()) {
                this.scrollTo((int)((n - this.getPaddingLeft() - this.getPaddingRight() + n3) * (this.getScrollX() / (float)(n2 - this.getPaddingLeft() - this.getPaddingRight() + n4))), this.getScrollY());
            }
            else {
                this.mScroller.setFinalX(this.getCurrentItem() * this.getClientWidth());
            }
        }
        else {
            final ItemInfo infoForPosition = this.infoForPosition(this.mCurItem);
            float min;
            if (infoForPosition == null) {
                min = 0.0f;
            }
            else {
                min = Math.min(infoForPosition.offset, this.mLastOffset);
            }
            n = (int)(min * (n - this.getPaddingLeft() - this.getPaddingRight()));
            if (n != this.getScrollX()) {
                this.completeScroll(false);
                this.scrollTo(n, this.getScrollY());
            }
        }
    }
    
    private void removeNonDecorViews() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (!((LayoutParams)this.getChildAt(i).getLayoutParams()).isDecor) {
                this.removeViewAt(i);
                --i;
            }
        }
    }
    
    private void requestParentDisallowInterceptTouchEvent(final boolean b) {
        final ViewParent parent = this.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(b);
        }
    }
    
    private boolean resetTouch() {
        boolean b = false;
        this.mActivePointerId = -1;
        this.endDrag();
        this.mLeftEdge.onRelease();
        this.mRightEdge.onRelease();
        if (this.mLeftEdge.isFinished() || this.mRightEdge.isFinished()) {
            b = true;
        }
        return b;
    }
    
    private void scrollToItem(final int n, final boolean b, final int n2, final boolean b2) {
        final ItemInfo infoForPosition = this.infoForPosition(n);
        int n3;
        if (infoForPosition == null) {
            n3 = 0;
        }
        else {
            n3 = (int)(Math.max(this.mFirstOffset, Math.min(infoForPosition.offset, this.mLastOffset)) * this.getClientWidth());
        }
        if (!b) {
            if (b2) {
                this.dispatchOnPageSelected(n);
            }
            this.completeScroll(false);
            this.scrollTo(n3, 0);
            this.pageScrolled(n3);
        }
        else {
            this.smoothScrollTo(n3, 0, n2);
            if (b2) {
                this.dispatchOnPageSelected(n);
            }
        }
    }
    
    private void setScrollingCacheEnabled(final boolean mScrollingCacheEnabled) {
        if (this.mScrollingCacheEnabled != mScrollingCacheEnabled) {
            this.mScrollingCacheEnabled = mScrollingCacheEnabled;
        }
    }
    
    private void sortChildDrawingOrder() {
        int i = 0;
        if (this.mDrawingOrder != 0) {
            if (this.mDrawingOrderedChildren != null) {
                this.mDrawingOrderedChildren.clear();
            }
            else {
                this.mDrawingOrderedChildren = new ArrayList<View>();
            }
            while (i < this.getChildCount()) {
                this.mDrawingOrderedChildren.add(this.getChildAt(i));
                ++i;
            }
            Collections.sort(this.mDrawingOrderedChildren, ViewPager.sPositionComparator);
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        int i = 0;
        final int size = list.size();
        final int descendantFocusability = this.getDescendantFocusability();
        if (descendantFocusability != 393216) {
            while (i < this.getChildCount()) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() == 0) {
                    final ItemInfo infoForChild = this.infoForChild(child);
                    if (infoForChild != null && infoForChild.position == this.mCurItem) {
                        child.addFocusables((ArrayList)list, n, n2);
                    }
                }
                ++i;
            }
        }
        if (descendantFocusability != 262144 || size == list.size()) {
            if (!this.isFocusable()) {
                return;
            }
            if ((n2 & 0x1) == 0x1 && this.isInTouchMode() && !this.isFocusableInTouchMode()) {
                return;
            }
            if (list != null) {
                list.add((View)this);
            }
        }
    }
    
    ItemInfo addNewItem(final int position, final int index) {
        final ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = position;
        itemInfo.object = this.mAdapter.instantiateItem(this, position);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(position);
        if (index >= 0 && index < this.mItems.size()) {
            this.mItems.add(index, itemInfo);
        }
        else {
            this.mItems.add(itemInfo);
        }
        return itemInfo;
    }
    
    public void addOnAdapterChangeListener(@NonNull final OnAdapterChangeListener onAdapterChangeListener) {
        if (this.mAdapterChangeListeners == null) {
            this.mAdapterChangeListeners = new ArrayList<OnAdapterChangeListener>();
        }
        this.mAdapterChangeListeners.add(onAdapterChangeListener);
    }
    
    public void addOnPageChangeListener(final OnPageChangeListener onPageChangeListener) {
        if (this.mOnPageChangeListeners == null) {
            this.mOnPageChangeListeners = new ArrayList<OnPageChangeListener>();
        }
        this.mOnPageChangeListeners.add(onPageChangeListener);
    }
    
    public void addTouchables(final ArrayList<View> list) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem) {
                    child.addTouchables((ArrayList)list);
                }
            }
        }
    }
    
    public void addView(final View view, final int n, ViewGroup$LayoutParams generateLayoutParams) {
        if (!this.checkLayoutParams(generateLayoutParams)) {
            generateLayoutParams = this.generateLayoutParams(generateLayoutParams);
        }
        final LayoutParams layoutParams = (LayoutParams)generateLayoutParams;
        layoutParams.isDecor |= isDecorView(view);
        if (!this.mInLayout) {
            super.addView(view, n, generateLayoutParams);
        }
        else {
            if (layoutParams != null && layoutParams.isDecor) {
                throw new IllegalStateException("Cannot add pager decor view during layout");
            }
            layoutParams.needsMeasure = true;
            this.addViewInLayout(view, n, generateLayoutParams);
        }
    }
    
    public boolean arrowScroll(final int n) {
        View focus = this.findFocus();
        Label_0014: {
            if (focus != this) {
                if (focus != null) {
                    ViewParent viewParent = focus.getParent();
                    while (true) {
                        while (viewParent instanceof ViewGroup) {
                            if (viewParent != this) {
                                viewParent = viewParent.getParent();
                            }
                            else {
                                final int n2 = 1;
                                if (n2 != 0) {
                                    break Label_0014;
                                }
                                final StringBuilder sb = new StringBuilder();
                                sb.append(focus.getClass().getSimpleName());
                                for (ViewParent viewParent2 = focus.getParent(); viewParent2 instanceof ViewGroup; viewParent2 = viewParent2.getParent()) {
                                    sb.append(" => ").append(viewParent2.getClass().getSimpleName());
                                }
                                Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + sb.toString());
                                focus = null;
                                break Label_0014;
                            }
                        }
                        final int n2 = 0;
                        continue;
                    }
                }
            }
            else {
                focus = null;
            }
        }
        final View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup)this, focus, n);
        boolean b;
        if (nextFocus != null && nextFocus != focus) {
            if (n != 17) {
                if (n != 66) {
                    b = false;
                }
                else {
                    final int left = this.getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                    final int left2 = this.getChildRectInPagerCoordinates(this.mTempRect, focus).left;
                    if (focus != null && left <= left2) {
                        b = this.pageRight();
                    }
                    else {
                        b = nextFocus.requestFocus();
                    }
                }
            }
            else {
                final int left3 = this.getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                final int left4 = this.getChildRectInPagerCoordinates(this.mTempRect, focus).left;
                if (focus != null && left3 >= left4) {
                    b = this.pageLeft();
                }
                else {
                    b = nextFocus.requestFocus();
                }
            }
        }
        else if (n != 17 && n != 1) {
            b = ((n == 66 || n == 2) && this.pageRight());
        }
        else {
            b = this.pageLeft();
        }
        if (b) {
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(n));
        }
        return b;
    }
    
    public boolean beginFakeDrag() {
        if (!this.mIsBeingDragged) {
            this.mFakeDragging = true;
            this.setScrollState(1);
            this.mLastMotionX = 0.0f;
            this.mInitialMotionX = 0.0f;
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
            else {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            final long uptimeMillis = SystemClock.uptimeMillis();
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 0.0f, 0.0f, 0);
            this.mVelocityTracker.addMovement(obtain);
            obtain.recycle();
            this.mFakeDragBeginTime = uptimeMillis;
            return true;
        }
        return false;
    }
    
    protected boolean canScroll(final View view, final boolean b, final int n, final int n2, final int n3) {
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
        return b && view.canScrollHorizontally(-n);
    }
    
    public boolean canScrollHorizontally(final int n) {
        final boolean b = false;
        boolean b2 = false;
        if (this.mAdapter == null) {
            return false;
        }
        final int clientWidth = this.getClientWidth();
        final int scrollX = this.getScrollX();
        if (n >= 0) {
            return n > 0 && (scrollX < (int)(clientWidth * this.mLastOffset) || b);
        }
        if (scrollX > (int)(clientWidth * this.mFirstOffset)) {
            b2 = true;
        }
        return b2;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        boolean b = false;
        if (viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams)) {
            b = true;
        }
        return b;
    }
    
    public void clearOnPageChangeListeners() {
        if (this.mOnPageChangeListeners != null) {
            this.mOnPageChangeListeners.clear();
        }
    }
    
    public void computeScroll() {
        this.mIsScrollStarted = true;
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            final int scrollX = this.getScrollX();
            final int scrollY = this.getScrollY();
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            if (scrollX != currX || scrollY != currY) {
                this.scrollTo(currX, currY);
                if (!this.pageScrolled(currX)) {
                    this.mScroller.abortAnimation();
                    this.scrollTo(0, currY);
                }
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
            return;
        }
        this.completeScroll(true);
    }
    
    void dataSetChanged() {
        final int count = this.mAdapter.getCount();
        this.mExpectedAdapterCount = count;
        int n;
        if (this.mItems.size() < this.mOffscreenPageLimit * 2 + 1 && this.mItems.size() < count) {
            n = 1;
        }
        else {
            n = 0;
        }
        final int mCurItem = this.mCurItem;
        final int n2 = 0;
        final int n3 = 0;
        int n4 = n;
        int n5 = mCurItem;
        int n6 = n2;
        int n11;
        int n12;
        int n20;
        int n21;
        for (int i = n3; i < this.mItems.size(); i = n12 + 1, n6 = n11, n5 = n21, n4 = n20) {
            final ItemInfo itemInfo = this.mItems.get(i);
            final int itemPosition = this.mAdapter.getItemPosition(itemInfo.object);
            int n9;
            int n10;
            if (itemPosition != -1) {
                if (itemPosition != -2) {
                    if (itemInfo.position == itemPosition) {
                        final int n7 = i;
                        final int n8 = n6;
                        n9 = n4;
                        n10 = n5;
                        n11 = n8;
                        n12 = n7;
                    }
                    else {
                        if (itemInfo.position == this.mCurItem) {
                            n5 = itemPosition;
                        }
                        itemInfo.position = itemPosition;
                        final int n13 = n5;
                        final int n14 = 1;
                        n12 = i;
                        n11 = n6;
                        n10 = n13;
                        n9 = n14;
                    }
                }
                else {
                    this.mItems.remove(i);
                    --i;
                    if (n6 == 0) {
                        this.mAdapter.startUpdate(this);
                        n6 = 1;
                    }
                    this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
                    if (this.mCurItem != itemInfo.position) {
                        n12 = i;
                        final int n15 = n5;
                        final int n16 = 1;
                        n11 = n6;
                        n10 = n15;
                        n9 = n16;
                    }
                    else {
                        final int max = Math.max(0, Math.min(this.mCurItem, count - 1));
                        n11 = n6;
                        n10 = max;
                        final int n17 = 1;
                        n12 = i;
                        n9 = n17;
                    }
                }
            }
            else {
                final int n18 = i;
                final int n19 = n5;
                n9 = n4;
                n12 = n18;
                n11 = n6;
                n10 = n19;
            }
            n20 = n9;
            n21 = n10;
        }
        if (n6 != 0) {
            this.mAdapter.finishUpdate(this);
        }
        Collections.sort(this.mItems, ViewPager.COMPARATOR);
        if (n4 != 0) {
            for (int childCount = this.getChildCount(), j = 0; j < childCount; ++j) {
                final LayoutParams layoutParams = (LayoutParams)this.getChildAt(j).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            this.setCurrentItemInternal(n5, false, true);
            this.requestLayout();
        }
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        boolean b = false;
        if (super.dispatchKeyEvent(keyEvent) || this.executeKeyEvent(keyEvent)) {
            b = true;
        }
        return b;
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() != 4096) {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() == 0) {
                    final ItemInfo infoForChild = this.infoForChild(child);
                    if (infoForChild != null && infoForChild.position == this.mCurItem && child.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
    }
    
    float distanceInfluenceForSnapDuration(final float n) {
        return (float)Math.sin((n - 0.5f) * 0.47123894f);
    }
    
    public void draw(final Canvas canvas) {
        final boolean b = false;
        boolean b2 = false;
        super.draw(canvas);
        final int overScrollMode = this.getOverScrollMode();
        if (overScrollMode != 0 && (overScrollMode != 1 || this.mAdapter == null || this.mAdapter.getCount() <= 1)) {
            this.mLeftEdge.finish();
            this.mRightEdge.finish();
            b2 = b;
        }
        else {
            if (!this.mLeftEdge.isFinished()) {
                final int save = canvas.save();
                final int n = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                final int width = this.getWidth();
                canvas.rotate(270.0f);
                canvas.translate((float)(-n + this.getPaddingTop()), this.mFirstOffset * width);
                this.mLeftEdge.setSize(n, width);
                b2 = (this.mLeftEdge.draw(canvas) | false);
                canvas.restoreToCount(save);
            }
            if (!this.mRightEdge.isFinished()) {
                final int save2 = canvas.save();
                final int width2 = this.getWidth();
                final int height = this.getHeight();
                final int paddingTop = this.getPaddingTop();
                final int paddingBottom = this.getPaddingBottom();
                canvas.rotate(90.0f);
                canvas.translate((float)(-this.getPaddingTop()), -(this.mLastOffset + 1.0f) * width2);
                this.mRightEdge.setSize(height - paddingTop - paddingBottom, width2);
                b2 |= this.mRightEdge.draw(canvas);
                canvas.restoreToCount(save2);
            }
        }
        if (b2) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final Drawable mMarginDrawable = this.mMarginDrawable;
        if (mMarginDrawable != null && mMarginDrawable.isStateful()) {
            mMarginDrawable.setState(this.getDrawableState());
        }
    }
    
    public void endFakeDrag() {
        if (this.mFakeDragging) {
            if (this.mAdapter != null) {
                final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                final int n = (int)mVelocityTracker.getXVelocity(this.mActivePointerId);
                this.mPopulatePending = true;
                final int clientWidth = this.getClientWidth();
                final int scrollX = this.getScrollX();
                final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
                this.setCurrentItemInternal(this.determineTargetPage(infoForCurrentScrollPosition.position, (scrollX / (float)clientWidth - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, n, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, n);
            }
            this.endDrag();
            this.mFakeDragging = false;
            return;
        }
        throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }
    
    public boolean executeKeyEvent(final KeyEvent keyEvent) {
        boolean b = false;
        if (keyEvent.getAction() == 0) {
            switch (keyEvent.getKeyCode()) {
                case 21: {
                    if (!keyEvent.hasModifiers(2)) {
                        b = this.arrowScroll(17);
                        break;
                    }
                    b = this.pageLeft();
                    break;
                }
                case 22: {
                    if (!keyEvent.hasModifiers(2)) {
                        b = this.arrowScroll(66);
                        break;
                    }
                    b = this.pageRight();
                    break;
                }
                case 61: {
                    if (keyEvent.hasNoModifiers()) {
                        b = this.arrowScroll(2);
                        break;
                    }
                    if (keyEvent.hasModifiers(1)) {
                        b = this.arrowScroll(1);
                        break;
                    }
                    break;
                }
            }
        }
        return b;
    }
    
    public void fakeDragBy(float n) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        if (this.mAdapter != null) {
            this.mLastMotionX += n;
            final float n2 = this.getScrollX() - n;
            final int clientWidth = this.getClientWidth();
            n = (float)clientWidth;
            final float mFirstOffset = this.mFirstOffset;
            final float n3 = (float)clientWidth;
            final float mLastOffset = this.mLastOffset;
            final ItemInfo itemInfo = this.mItems.get(0);
            final ItemInfo itemInfo2 = this.mItems.get(this.mItems.size() - 1);
            if (itemInfo.position == 0) {
                n *= mFirstOffset;
            }
            else {
                n = itemInfo.offset * clientWidth;
            }
            float n4;
            if (itemInfo2.position == this.mAdapter.getCount() - 1) {
                n4 = n3 * mLastOffset;
            }
            else {
                n4 = itemInfo2.offset * clientWidth;
            }
            if (n2 >= n) {
                if (n2 > n4) {
                    n = n4;
                }
                else {
                    n = n2;
                }
            }
            this.mLastMotionX += n - (int)n;
            this.scrollTo((int)n, this.getScrollY());
            this.pageScrolled((int)n);
            final MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
            this.mVelocityTracker.addMovement(obtain);
            obtain.recycle();
        }
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return this.generateDefaultLayoutParams();
    }
    
    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }
    
    protected int getChildDrawingOrder(final int n, int index) {
        if (this.mDrawingOrder == 2) {
            index = n - 1 - index;
        }
        return ((LayoutParams)this.mDrawingOrderedChildren.get(index).getLayoutParams()).childIndex;
    }
    
    public int getCurrentItem() {
        return this.mCurItem;
    }
    
    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }
    
    public int getPageMargin() {
        return this.mPageMargin;
    }
    
    ItemInfo infoForAnyChild(View view) {
        while (true) {
            final ViewParent parent = view.getParent();
            if (parent == this) {
                return this.infoForChild(view);
            }
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            view = (View)parent;
        }
    }
    
    ItemInfo infoForChild(final View view) {
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(view, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }
    
    ItemInfo infoForPosition(final int n) {
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.position == n) {
                return itemInfo;
            }
        }
        return null;
    }
    
    void initViewPager() {
        this.setWillNotDraw(false);
        this.setDescendantFocusability(262144);
        this.setFocusable(true);
        final Context context = this.getContext();
        this.mScroller = new Scroller(context, ViewPager.sInterpolator);
        final ViewConfiguration value = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = value.getScaledPagingTouchSlop();
        this.mMinimumVelocity = (int)(400.0f * density);
        this.mMaximumVelocity = value.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffect(context);
        this.mRightEdge = new EdgeEffect(context);
        this.mFlingDistance = (int)(25.0f * density);
        this.mCloseEnough = (int)(2.0f * density);
        this.mDefaultGutterSize = (int)(16.0f * density);
        ViewCompat.setAccessibilityDelegate((View)this, new MyAccessibilityDelegate());
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
        ViewCompat.setOnApplyWindowInsetsListener((View)this, new OnApplyWindowInsetsListener() {
            private final Rect mTempRect = new Rect();
            
            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat) {
                int i = 0;
                final WindowInsetsCompat onApplyWindowInsets = ViewCompat.onApplyWindowInsets(view, windowInsetsCompat);
                if (!onApplyWindowInsets.isConsumed()) {
                    final Rect mTempRect = this.mTempRect;
                    mTempRect.left = onApplyWindowInsets.getSystemWindowInsetLeft();
                    mTempRect.top = onApplyWindowInsets.getSystemWindowInsetTop();
                    mTempRect.right = onApplyWindowInsets.getSystemWindowInsetRight();
                    mTempRect.bottom = onApplyWindowInsets.getSystemWindowInsetBottom();
                    while (i < ViewPager.this.getChildCount()) {
                        final WindowInsetsCompat dispatchApplyWindowInsets = ViewCompat.dispatchApplyWindowInsets(ViewPager.this.getChildAt(i), onApplyWindowInsets);
                        mTempRect.left = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetLeft(), mTempRect.left);
                        mTempRect.top = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetTop(), mTempRect.top);
                        mTempRect.right = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetRight(), mTempRect.right);
                        mTempRect.bottom = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetBottom(), mTempRect.bottom);
                        ++i;
                    }
                    return onApplyWindowInsets.replaceSystemWindowInsets(mTempRect.left, mTempRect.top, mTempRect.right, mTempRect.bottom);
                }
                return onApplyWindowInsets;
            }
        });
    }
    
    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        this.removeCallbacks(this.mEndScrollRunnable);
        if (this.mScroller != null && !this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        super.onDetachedFromWindow();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPageMargin > 0 && this.mMarginDrawable != null && this.mItems.size() > 0 && this.mAdapter != null) {
            final int scrollX = this.getScrollX();
            final int width = this.getWidth();
            final float n = this.mPageMargin / (float)width;
            ItemInfo itemInfo = this.mItems.get(0);
            float offset = itemInfo.offset;
            final int size = this.mItems.size();
            int i = itemInfo.position;
            final int position = this.mItems.get(size - 1).position;
            int index = 0;
            while (i < position) {
                while (i > itemInfo.position && index < size) {
                    final ArrayList<ItemInfo> mItems = this.mItems;
                    ++index;
                    itemInfo = mItems.get(index);
                }
                float a;
                if (i != itemInfo.position) {
                    final float pageWidth = this.mAdapter.getPageWidth(i);
                    a = (offset + pageWidth) * width;
                    offset += pageWidth + n;
                }
                else {
                    a = (itemInfo.offset + itemInfo.widthFactor) * width;
                    offset = itemInfo.offset + itemInfo.widthFactor + n;
                }
                if (this.mPageMargin + a > scrollX) {
                    this.mMarginDrawable.setBounds(Math.round(a), this.mTopPageBounds, Math.round(this.mPageMargin + a), this.mBottomPageBounds);
                    this.mMarginDrawable.draw(canvas);
                }
                if (a > scrollX + width) {
                    break;
                }
                ++i;
            }
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int n = motionEvent.getAction() & 0xFF;
        if (n != 3 && n != 1) {
            if (n != 0) {
                if (this.mIsBeingDragged) {
                    return true;
                }
                if (this.mIsUnableToDrag) {
                    return false;
                }
            }
            switch (n) {
                case 2: {
                    final int mActivePointerId = this.mActivePointerId;
                    if (mActivePointerId == -1) {
                        break;
                    }
                    final int pointerIndex = motionEvent.findPointerIndex(mActivePointerId);
                    final float x = motionEvent.getX(pointerIndex);
                    final float a = x - this.mLastMotionX;
                    final float abs = Math.abs(a);
                    final float y = motionEvent.getY(pointerIndex);
                    final float abs2 = Math.abs(y - this.mInitialMotionY);
                    if (a != 0.0f && !this.isGutterDrag(this.mLastMotionX, a) && this.canScroll((View)this, false, (int)a, (int)x, (int)y)) {
                        this.mLastMotionX = x;
                        this.mLastMotionY = y;
                        this.mIsUnableToDrag = true;
                        return false;
                    }
                    if (abs > this.mTouchSlop && 0.5f * abs > abs2) {
                        this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                        this.setScrollState(1);
                        float mLastMotionX;
                        if (a > 0.0f) {
                            mLastMotionX = this.mInitialMotionX + this.mTouchSlop;
                        }
                        else {
                            mLastMotionX = this.mInitialMotionX - this.mTouchSlop;
                        }
                        this.mLastMotionX = mLastMotionX;
                        this.mLastMotionY = y;
                        this.setScrollingCacheEnabled(true);
                    }
                    else if (abs2 > this.mTouchSlop) {
                        this.mIsUnableToDrag = true;
                    }
                    if (this.mIsBeingDragged && this.performDrag(x)) {
                        ViewCompat.postInvalidateOnAnimation((View)this);
                        break;
                    }
                    break;
                }
                case 0: {
                    final float x2 = motionEvent.getX();
                    this.mInitialMotionX = x2;
                    this.mLastMotionX = x2;
                    final float y2 = motionEvent.getY();
                    this.mInitialMotionY = y2;
                    this.mLastMotionY = y2;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    this.mIsUnableToDrag = false;
                    this.mIsScrollStarted = true;
                    this.mScroller.computeScrollOffset();
                    if (this.mScrollState == 2 && Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough) {
                        this.mScroller.abortAnimation();
                        this.mPopulatePending = false;
                        this.populate();
                        this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                        this.setScrollState(1);
                        break;
                    }
                    this.completeScroll(false);
                    this.mIsBeingDragged = false;
                    break;
                }
                case 6: {
                    this.onSecondaryPointerUp(motionEvent);
                    break;
                }
            }
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            return this.mIsBeingDragged;
        }
        this.resetTouch();
        return false;
    }
    
    protected void onLayout(final boolean b, int paddingTop, int paddingLeft, int paddingBottom, int i) {
        final int childCount = this.getChildCount();
        final int n = paddingBottom - paddingTop;
        final int n2 = i - paddingLeft;
        paddingLeft = this.getPaddingLeft();
        paddingTop = this.getPaddingTop();
        i = this.getPaddingRight();
        paddingBottom = this.getPaddingBottom();
        final int scrollX = this.getScrollX();
        int mDecorChildCount = 0;
        int n5;
        int n17;
        for (int j = 0; j < childCount; ++j, n17 = n5, mDecorChildCount = paddingTop, paddingTop = i, i = paddingLeft, paddingLeft = n17) {
            final View child = this.getChildAt(j);
            if (child.getVisibility() == 8) {
                final int n3 = mDecorChildCount;
                final int n4 = paddingTop;
                paddingTop = i;
                n5 = paddingLeft;
                i = n4;
                paddingLeft = paddingTop;
                paddingTop = n3;
            }
            else {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.isDecor) {
                    final int n6 = mDecorChildCount;
                    final int n7 = paddingTop;
                    final int n8 = paddingLeft;
                    paddingTop = n6;
                    paddingLeft = i;
                    i = n7;
                    n5 = n8;
                }
                else {
                    final int gravity = layoutParams.gravity;
                    final int gravity2 = layoutParams.gravity;
                    int max = 0;
                    int n9 = 0;
                    switch (gravity & 0x7) {
                        default: {
                            max = paddingLeft;
                            n9 = paddingLeft;
                            break;
                        }
                        case 3: {
                            final int measuredWidth = child.getMeasuredWidth();
                            max = paddingLeft;
                            n9 = measuredWidth + paddingLeft;
                            break;
                        }
                        case 1: {
                            max = Math.max((n - child.getMeasuredWidth()) / 2, paddingLeft);
                            n9 = paddingLeft;
                            break;
                        }
                        case 5: {
                            final int measuredWidth2 = child.getMeasuredWidth();
                            final int n10 = i + child.getMeasuredWidth();
                            max = n - i - measuredWidth2;
                            i = n10;
                            n9 = paddingLeft;
                            break;
                        }
                    }
                    switch (gravity2 & 0x70) {
                        default: {
                            final int n11 = paddingTop;
                            paddingLeft = paddingTop;
                            paddingTop = paddingBottom;
                            paddingBottom = n11;
                            break;
                        }
                        case 48: {
                            final int measuredHeight = child.getMeasuredHeight();
                            paddingLeft = paddingBottom;
                            final int n12 = measuredHeight + paddingTop;
                            paddingBottom = paddingTop;
                            paddingTop = paddingLeft;
                            paddingLeft = n12;
                            break;
                        }
                        case 16: {
                            final int max2 = Math.max((n2 - child.getMeasuredHeight()) / 2, paddingTop);
                            paddingLeft = paddingTop;
                            paddingTop = paddingBottom;
                            paddingBottom = max2;
                            break;
                        }
                        case 80: {
                            final int n13 = n2 - paddingBottom - child.getMeasuredHeight();
                            final int measuredHeight2 = child.getMeasuredHeight();
                            paddingLeft = paddingTop;
                            paddingTop = paddingBottom + measuredHeight2;
                            paddingBottom = n13;
                            break;
                        }
                    }
                    final int n14 = max + scrollX;
                    child.layout(n14, paddingBottom, child.getMeasuredWidth() + n14, child.getMeasuredHeight() + paddingBottom);
                    final int n15 = mDecorChildCount + 1;
                    final int n16 = paddingLeft;
                    paddingBottom = paddingTop;
                    paddingLeft = i;
                    paddingTop = n15;
                    i = n16;
                    n5 = n9;
                }
            }
        }
        final int n18 = n - paddingLeft - i;
        View child2;
        LayoutParams layoutParams2;
        ItemInfo infoForChild;
        int n19;
        for (i = 0; i < childCount; ++i) {
            child2 = this.getChildAt(i);
            if (child2.getVisibility() != 8) {
                layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (!layoutParams2.isDecor) {
                    infoForChild = this.infoForChild(child2);
                    if (infoForChild != null) {
                        n19 = (int)(infoForChild.offset * n18) + paddingLeft;
                        if (layoutParams2.needsMeasure) {
                            layoutParams2.needsMeasure = false;
                            child2.measure(View$MeasureSpec.makeMeasureSpec((int)(layoutParams2.widthFactor * n18), 1073741824), View$MeasureSpec.makeMeasureSpec(n2 - paddingTop - paddingBottom, 1073741824));
                        }
                        child2.layout(n19, paddingTop, child2.getMeasuredWidth() + n19, child2.getMeasuredHeight() + paddingTop);
                    }
                }
            }
        }
        this.mTopPageBounds = paddingTop;
        this.mBottomPageBounds = n2 - paddingBottom;
        this.mDecorChildCount = mDecorChildCount;
        if (this.mFirstLayout) {
            this.scrollToItem(this.mCurItem, false, 0, false);
        }
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(int measuredWidth, int i) {
        this.setMeasuredDimension(getDefaultSize(0, measuredWidth), getDefaultSize(0, i));
        measuredWidth = this.getMeasuredWidth();
        this.mGutterSize = Math.min(measuredWidth / 10, this.mDefaultGutterSize);
        measuredWidth = measuredWidth - this.getPaddingLeft() - this.getPaddingRight();
        i = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
        int n;
        int n2;
        for (int childCount = this.getChildCount(), j = 0; j < childCount; ++j, measuredWidth = n2, i = n) {
            final View child = this.getChildAt(j);
            if (child.getVisibility() == 8) {
                n = i;
                n2 = measuredWidth;
            }
            else {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                n2 = measuredWidth;
                n = i;
                if (layoutParams != null) {
                    n2 = measuredWidth;
                    n = i;
                    if (layoutParams.isDecor) {
                        final int n3 = layoutParams.gravity & 0x7;
                        final int n4 = layoutParams.gravity & 0x70;
                        int n5 = Integer.MIN_VALUE;
                        int n6 = Integer.MIN_VALUE;
                        boolean b;
                        if (n4 != 48 && n4 != 80) {
                            b = false;
                        }
                        else {
                            b = true;
                        }
                        boolean b2;
                        if (n3 != 3 && n3 != 5) {
                            b2 = false;
                        }
                        else {
                            b2 = true;
                        }
                        if (!b) {
                            if (b2) {
                                n6 = 1073741824;
                            }
                        }
                        else {
                            n5 = 1073741824;
                        }
                        int n7;
                        int width;
                        if (layoutParams.width == -2) {
                            n7 = n5;
                            width = measuredWidth;
                        }
                        else {
                            n7 = 1073741824;
                            if (layoutParams.width == -1) {
                                width = measuredWidth;
                            }
                            else {
                                width = layoutParams.width;
                            }
                        }
                        int n9;
                        int height;
                        if (layoutParams.height == -2) {
                            final int n8 = i;
                            n9 = n6;
                            height = n8;
                        }
                        else {
                            n9 = 1073741824;
                            if (layoutParams.height == -1) {
                                height = i;
                            }
                            else {
                                height = layoutParams.height;
                            }
                        }
                        child.measure(View$MeasureSpec.makeMeasureSpec(width, n7), View$MeasureSpec.makeMeasureSpec(height, n9));
                        if (!b) {
                            n2 = measuredWidth;
                            n = i;
                            if (b2) {
                                n2 = measuredWidth - child.getMeasuredWidth();
                                n = i;
                            }
                        }
                        else {
                            n = i - child.getMeasuredHeight();
                            n2 = measuredWidth;
                        }
                    }
                }
            }
        }
        this.mChildWidthMeasureSpec = View$MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        this.mChildHeightMeasureSpec = View$MeasureSpec.makeMeasureSpec(i, 1073741824);
        this.mInLayout = true;
        this.populate();
        this.mInLayout = false;
        int childCount2;
        View child2;
        LayoutParams layoutParams2;
        for (childCount2 = this.getChildCount(), i = 0; i < childCount2; ++i) {
            child2 = this.getChildAt(i);
            if (child2.getVisibility() != 8) {
                layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (layoutParams2 == null || !layoutParams2.isDecor) {
                    child2.measure(View$MeasureSpec.makeMeasureSpec((int)(layoutParams2.widthFactor * measuredWidth), 1073741824), this.mChildHeightMeasureSpec);
                }
            }
        }
    }
    
    @CallSuper
    protected void onPageScrolled(int i, float n, int scrollX) {
        if (this.mDecorChildCount > 0) {
            final int scrollX2 = this.getScrollX();
            int paddingLeft = this.getPaddingLeft();
            int paddingRight = this.getPaddingRight();
            final int width = this.getWidth();
            int n3 = 0;
            int n4 = 0;
            int n10;
            for (int childCount = this.getChildCount(), j = 0; j < childCount; ++j, n10 = n3, paddingRight = n4, paddingLeft = n10) {
                final View child = this.getChildAt(j);
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.isDecor) {
                    int max = 0;
                    switch (layoutParams.gravity & 0x7) {
                        default: {
                            max = paddingLeft;
                            final int n2 = paddingRight;
                            n3 = paddingLeft;
                            n4 = n2;
                            break;
                        }
                        case 3: {
                            final int n5 = child.getWidth() + paddingLeft;
                            max = paddingLeft;
                            n4 = paddingRight;
                            n3 = n5;
                            break;
                        }
                        case 1: {
                            max = Math.max((width - child.getMeasuredWidth()) / 2, paddingLeft);
                            final int n6 = paddingLeft;
                            n4 = paddingRight;
                            n3 = n6;
                            break;
                        }
                        case 5: {
                            max = width - paddingRight - child.getMeasuredWidth();
                            final int measuredWidth = child.getMeasuredWidth();
                            final int n7 = paddingLeft;
                            n4 = paddingRight + measuredWidth;
                            n3 = n7;
                            break;
                        }
                    }
                    final int n8 = max + scrollX2 - child.getLeft();
                    if (n8 != 0) {
                        child.offsetLeftAndRight(n8);
                    }
                }
                else {
                    final int n9 = paddingLeft;
                    n4 = paddingRight;
                    n3 = n9;
                }
            }
        }
        this.dispatchOnPageScrolled(i, n, scrollX);
        if (this.mPageTransformer != null) {
            scrollX = this.getScrollX();
            int childCount2;
            View child2;
            for (childCount2 = this.getChildCount(), i = 0; i < childCount2; ++i) {
                child2 = this.getChildAt(i);
                if (!((LayoutParams)child2.getLayoutParams()).isDecor) {
                    n = (child2.getLeft() - scrollX) / (float)this.getClientWidth();
                    this.mPageTransformer.transformPage(child2, n);
                }
            }
        }
        this.mCalledSuper = true;
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        int n2 = -1;
        int i = this.getChildCount();
        int n3;
        if ((n & 0x2) == 0x0) {
            --i;
            n3 = -1;
        }
        else {
            n2 = i;
            i = 0;
            n3 = 1;
        }
        while (i != n2) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem && child.requestFocus(n, rect)) {
                    return true;
                }
            }
            i += n3;
        }
        return false;
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            if (this.mAdapter == null) {
                this.mRestoredCurItem = savedState.position;
                this.mRestoredAdapterState = savedState.adapterState;
                this.mRestoredClassLoader = savedState.loader;
            }
            else {
                this.mAdapter.restoreState(savedState.adapterState, savedState.loader);
                this.setCurrentItemInternal(savedState.position, false, true);
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        if (this.mAdapter != null) {
            savedState.adapterState = this.mAdapter.saveState();
        }
        return (Parcelable)savedState;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n != n3) {
            this.recomputeScrollPosition(n, n3, this.mPageMargin, this.mPageMargin);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mFakeDragging) {
            return true;
        }
        if (motionEvent.getAction() == 0 && motionEvent.getEdgeFlags() != 0) {
            return false;
        }
        if (this.mAdapter != null && this.mAdapter.getCount() != 0) {
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            int n = 0;
            switch (motionEvent.getAction() & 0xFF) {
                default: {
                    n = 0;
                    break;
                }
                case 0: {
                    this.mScroller.abortAnimation();
                    this.mPopulatePending = false;
                    this.populate();
                    final float x = motionEvent.getX();
                    this.mInitialMotionX = x;
                    this.mLastMotionX = x;
                    final float y = motionEvent.getY();
                    this.mInitialMotionY = y;
                    this.mLastMotionY = y;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    n = 0;
                    break;
                }
                case 2: {
                    if (!this.mIsBeingDragged) {
                        final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (pointerIndex == -1) {
                            n = (this.resetTouch() ? 1 : 0);
                            break;
                        }
                        final float x2 = motionEvent.getX(pointerIndex);
                        final float abs = Math.abs(x2 - this.mLastMotionX);
                        final float y2 = motionEvent.getY(pointerIndex);
                        final float abs2 = Math.abs(y2 - this.mLastMotionY);
                        if (abs > this.mTouchSlop && abs > abs2) {
                            this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                            float mLastMotionX;
                            if (x2 - this.mInitialMotionX > 0.0f) {
                                mLastMotionX = this.mInitialMotionX + this.mTouchSlop;
                            }
                            else {
                                mLastMotionX = this.mInitialMotionX - this.mTouchSlop;
                            }
                            this.mLastMotionX = mLastMotionX;
                            this.mLastMotionY = y2;
                            this.setScrollState(1);
                            this.setScrollingCacheEnabled(true);
                            final ViewParent parent = this.getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                    n = ((this.mIsBeingDragged && (this.performDrag(motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId))) | false)) ? 1 : 0);
                    break;
                }
                case 1: {
                    if (!this.mIsBeingDragged) {
                        n = 0;
                        break;
                    }
                    final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                    mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                    final int n2 = (int)mVelocityTracker.getXVelocity(this.mActivePointerId);
                    this.mPopulatePending = true;
                    final int clientWidth = this.getClientWidth();
                    final int scrollX = this.getScrollX();
                    final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
                    this.setCurrentItemInternal(this.determineTargetPage(infoForCurrentScrollPosition.position, (scrollX / (float)clientWidth - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + this.mPageMargin / (float)clientWidth), n2, (int)(motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX)), true, true, n2);
                    n = (this.resetTouch() ? 1 : 0);
                    break;
                }
                case 3: {
                    if (!this.mIsBeingDragged) {
                        n = 0;
                        break;
                    }
                    this.scrollToItem(this.mCurItem, true, 0, false);
                    n = (this.resetTouch() ? 1 : 0);
                    break;
                }
                case 5: {
                    final int actionIndex = motionEvent.getActionIndex();
                    this.mLastMotionX = motionEvent.getX(actionIndex);
                    this.mActivePointerId = motionEvent.getPointerId(actionIndex);
                    n = 0;
                    break;
                }
                case 6: {
                    this.onSecondaryPointerUp(motionEvent);
                    this.mLastMotionX = motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId));
                    n = 0;
                    break;
                }
            }
            if (n != 0) {
                ViewCompat.postInvalidateOnAnimation((View)this);
            }
            return true;
        }
        return false;
    }
    
    boolean pageLeft() {
        if (this.mCurItem <= 0) {
            return false;
        }
        this.setCurrentItem(this.mCurItem - 1, true);
        return true;
    }
    
    boolean pageRight() {
        if (this.mAdapter != null && this.mCurItem < this.mAdapter.getCount() - 1) {
            this.setCurrentItem(this.mCurItem + 1, true);
            return true;
        }
        return false;
    }
    
    void populate() {
        this.populate(this.mCurItem);
    }
    
    void populate(int i) {
        ItemInfo infoForPosition;
        if (this.mCurItem == i) {
            infoForPosition = null;
        }
        else {
            infoForPosition = this.infoForPosition(this.mCurItem);
            this.mCurItem = i;
        }
        if (this.mAdapter == null) {
            this.sortChildDrawingOrder();
            return;
        }
        if (this.mPopulatePending) {
            this.sortChildDrawingOrder();
            return;
        }
        if (this.getWindowToken() == null) {
            return;
        }
        this.mAdapter.startUpdate(this);
        i = this.mOffscreenPageLimit;
        final int max = Math.max(0, this.mCurItem - i);
        final int count = this.mAdapter.getCount();
        final int min = Math.min(count - 1, i + this.mCurItem);
        while (true) {
            ItemInfo itemInfo6;
            while (true) {
                Label_0315: {
                    if (count != this.mExpectedAdapterCount) {
                        try {
                            final String s = this.getResources().getResourceName(this.getId());
                            throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + count + " Pager id: " + s + " Pager class: " + this.getClass() + " Problematic adapter: " + this.mAdapter.getClass());
                        }
                        catch (final Resources$NotFoundException ex) {
                            final String s = Integer.toHexString(this.getId());
                            throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + count + " Pager id: " + s + " Pager class: " + this.getClass() + " Problematic adapter: " + this.mAdapter.getClass());
                        }
                        break Label_0315;
                    }
                    i = 0;
                    if (i < this.mItems.size()) {
                        break Label_0315;
                    }
                    final ItemInfo itemInfo = null;
                    ItemInfo addNewItem;
                    if (itemInfo == null && count > 0) {
                        addNewItem = this.addNewItem(this.mCurItem, i);
                    }
                    else {
                        addNewItem = itemInfo;
                    }
                    if (addNewItem != null) {
                        int index = i - 1;
                        ItemInfo itemInfo2;
                        if (index < 0) {
                            itemInfo2 = null;
                        }
                        else {
                            itemInfo2 = this.mItems.get(index);
                        }
                        final int clientWidth = this.getClientWidth();
                        float n;
                        if (clientWidth > 0) {
                            n = 2.0f - addNewItem.widthFactor + this.getPaddingLeft() / (float)clientWidth;
                        }
                        else {
                            n = 0.0f;
                        }
                        final int mCurItem = this.mCurItem;
                        float n2 = 0.0f;
                        int j = mCurItem - 1;
                        int n3 = i;
                        ItemInfo itemInfo3 = itemInfo2;
                        while (j >= 0) {
                            float n4;
                            ItemInfo itemInfo4;
                            int n5;
                            if (n2 < n || j >= max) {
                                if (itemInfo3 != null && j == itemInfo3.position) {
                                    n4 = n2 + itemInfo3.widthFactor;
                                    i = index - 1;
                                    if (i < 0) {
                                        itemInfo4 = null;
                                        n5 = n3;
                                    }
                                    else {
                                        itemInfo4 = this.mItems.get(i);
                                        n5 = n3;
                                    }
                                }
                                else {
                                    n4 = n2 + this.addNewItem(j, index + 1).widthFactor;
                                    n5 = n3 + 1;
                                    if (index < 0) {
                                        itemInfo4 = null;
                                        i = index;
                                    }
                                    else {
                                        itemInfo4 = this.mItems.get(index);
                                        i = index;
                                    }
                                }
                            }
                            else {
                                if (itemInfo3 == null) {
                                    break;
                                }
                                itemInfo4 = itemInfo3;
                                i = index;
                                n4 = n2;
                                n5 = n3;
                                if (j == itemInfo3.position) {
                                    itemInfo4 = itemInfo3;
                                    i = index;
                                    n4 = n2;
                                    n5 = n3;
                                    if (!itemInfo3.scrolling) {
                                        this.mItems.remove(index);
                                        this.mAdapter.destroyItem(this, j, itemInfo3.object);
                                        i = index - 1;
                                        n5 = n3 - 1;
                                        if (i < 0) {
                                            itemInfo4 = null;
                                            n4 = n2;
                                        }
                                        else {
                                            itemInfo4 = this.mItems.get(i);
                                            n4 = n2;
                                        }
                                    }
                                }
                            }
                            --j;
                            itemInfo3 = itemInfo4;
                            index = i;
                            n2 = n4;
                            n3 = n5;
                        }
                        float widthFactor = addNewItem.widthFactor;
                        i = n3 + 1;
                        if (widthFactor < 2.0f) {
                            ItemInfo itemInfo5;
                            if (i >= this.mItems.size()) {
                                itemInfo5 = null;
                            }
                            else {
                                itemInfo5 = this.mItems.get(i);
                            }
                            float n6;
                            if (clientWidth > 0) {
                                n6 = this.getPaddingRight() / (float)clientWidth + 2.0f;
                            }
                            else {
                                n6 = 0.0f;
                            }
                            int k = this.mCurItem;
                            ++k;
                            while (k < count) {
                                if (widthFactor < n6 || k <= min) {
                                    if (itemInfo5 != null && k == itemInfo5.position) {
                                        final float widthFactor2 = itemInfo5.widthFactor;
                                        if (++i >= this.mItems.size()) {
                                            itemInfo5 = null;
                                        }
                                        else {
                                            itemInfo5 = this.mItems.get(i);
                                        }
                                        widthFactor += widthFactor2;
                                    }
                                    else {
                                        final ItemInfo addNewItem2 = this.addNewItem(k, i);
                                        ++i;
                                        final float widthFactor3 = addNewItem2.widthFactor;
                                        if (i >= this.mItems.size()) {
                                            itemInfo5 = null;
                                        }
                                        else {
                                            itemInfo5 = this.mItems.get(i);
                                        }
                                        widthFactor += widthFactor3;
                                    }
                                }
                                else {
                                    if (itemInfo5 == null) {
                                        break;
                                    }
                                    if (k == itemInfo5.position && !itemInfo5.scrolling) {
                                        this.mItems.remove(i);
                                        this.mAdapter.destroyItem(this, k, itemInfo5.object);
                                        if (i >= this.mItems.size()) {
                                            itemInfo5 = null;
                                        }
                                        else {
                                            itemInfo5 = this.mItems.get(i);
                                        }
                                    }
                                }
                                ++k;
                            }
                        }
                        this.calculatePageOffsets(addNewItem, n3, infoForPosition);
                    }
                    final PagerAdapter mAdapter = this.mAdapter;
                    i = this.mCurItem;
                    Object object;
                    if (addNewItem == null) {
                        object = null;
                    }
                    else {
                        object = addNewItem.object;
                    }
                    mAdapter.setPrimaryItem(this, i, object);
                    this.mAdapter.finishUpdate(this);
                    int childCount;
                    View child;
                    LayoutParams layoutParams;
                    ItemInfo infoForChild;
                    for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                        child = this.getChildAt(i);
                        layoutParams = (LayoutParams)child.getLayoutParams();
                        layoutParams.childIndex = i;
                        if (!layoutParams.isDecor && layoutParams.widthFactor == 0.0f) {
                            infoForChild = this.infoForChild(child);
                            if (infoForChild != null) {
                                layoutParams.widthFactor = infoForChild.widthFactor;
                                layoutParams.position = infoForChild.position;
                            }
                        }
                    }
                    this.sortChildDrawingOrder();
                    if (this.hasFocus()) {
                        final View focus = this.findFocus();
                        ItemInfo infoForAnyChild;
                        if (focus == null) {
                            infoForAnyChild = null;
                        }
                        else {
                            infoForAnyChild = this.infoForAnyChild(focus);
                        }
                        if (infoForAnyChild == null || infoForAnyChild.position != this.mCurItem) {
                            View child2;
                            ItemInfo infoForChild2;
                            for (i = 0; i < this.getChildCount(); ++i) {
                                child2 = this.getChildAt(i);
                                infoForChild2 = this.infoForChild(child2);
                                if (infoForChild2 != null && infoForChild2.position == this.mCurItem && child2.requestFocus(2)) {
                                    break;
                                }
                            }
                        }
                    }
                    return;
                }
                itemInfo6 = this.mItems.get(i);
                if (itemInfo6.position < this.mCurItem) {
                    ++i;
                    continue;
                }
                break;
            }
            ItemInfo itemInfo = itemInfo6;
            if (itemInfo6.position != this.mCurItem) {
                itemInfo = null;
            }
            continue;
        }
    }
    
    public void removeOnAdapterChangeListener(@NonNull final OnAdapterChangeListener onAdapterChangeListener) {
        if (this.mAdapterChangeListeners != null) {
            this.mAdapterChangeListeners.remove(onAdapterChangeListener);
        }
    }
    
    public void removeOnPageChangeListener(final OnPageChangeListener onPageChangeListener) {
        if (this.mOnPageChangeListeners != null) {
            this.mOnPageChangeListeners.remove(onPageChangeListener);
        }
    }
    
    public void removeView(final View view) {
        if (!this.mInLayout) {
            super.removeView(view);
        }
        else {
            this.removeViewInLayout(view);
        }
    }
    
    public void setAdapter(final PagerAdapter mAdapter) {
        final int n = 0;
        if (this.mAdapter != null) {
            this.mAdapter.setViewPagerObserver(null);
            this.mAdapter.startUpdate(this);
            for (int i = 0; i < this.mItems.size(); ++i) {
                final ItemInfo itemInfo = this.mItems.get(i);
                this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate(this);
            this.mItems.clear();
            this.removeNonDecorViews();
            this.scrollTo(this.mCurItem = 0, 0);
        }
        final PagerAdapter mAdapter2 = this.mAdapter;
        this.mAdapter = mAdapter;
        this.mExpectedAdapterCount = 0;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.setViewPagerObserver(this.mObserver);
            this.mPopulatePending = false;
            final boolean mFirstLayout = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem < 0) {
                if (mFirstLayout) {
                    this.requestLayout();
                }
                else {
                    this.populate();
                }
            }
            else {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                this.setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            }
        }
        if (this.mAdapterChangeListeners != null && !this.mAdapterChangeListeners.isEmpty()) {
            for (int size = this.mAdapterChangeListeners.size(), j = n; j < size; ++j) {
                this.mAdapterChangeListeners.get(j).onAdapterChanged(this, mAdapter2, mAdapter);
            }
        }
    }
    
    public void setCurrentItem(final int n) {
        this.mPopulatePending = false;
        this.setCurrentItemInternal(n, !this.mFirstLayout, false);
    }
    
    public void setCurrentItem(final int n, final boolean b) {
        this.setCurrentItemInternal(n, b, this.mPopulatePending = false);
    }
    
    void setCurrentItemInternal(final int n, final boolean b, final boolean b2) {
        this.setCurrentItemInternal(n, b, b2, 0);
    }
    
    void setCurrentItemInternal(int mCurItem, final boolean b, final boolean b2, final int n) {
        final boolean b3 = false;
        if (this.mAdapter == null || this.mAdapter.getCount() <= 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        if (!b2 && this.mCurItem == mCurItem && this.mItems.size() != 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        if (mCurItem >= 0) {
            if (mCurItem >= this.mAdapter.getCount()) {
                mCurItem = this.mAdapter.getCount() - 1;
            }
        }
        else {
            mCurItem = 0;
        }
        final int mOffscreenPageLimit = this.mOffscreenPageLimit;
        if (mCurItem > this.mCurItem + mOffscreenPageLimit || mCurItem < this.mCurItem - mOffscreenPageLimit) {
            for (int i = 0; i < this.mItems.size(); ++i) {
                this.mItems.get(i).scrolling = true;
            }
        }
        final boolean b4 = this.mCurItem != mCurItem || b3;
        if (!this.mFirstLayout) {
            this.populate(mCurItem);
            this.scrollToItem(mCurItem, b, n, b4);
        }
        else {
            this.mCurItem = mCurItem;
            if (b4) {
                this.dispatchOnPageSelected(mCurItem);
            }
            this.requestLayout();
        }
    }
    
    OnPageChangeListener setInternalPageChangeListener(final OnPageChangeListener mInternalPageChangeListener) {
        final OnPageChangeListener mInternalPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = mInternalPageChangeListener;
        return mInternalPageChangeListener2;
    }
    
    public void setOffscreenPageLimit(int n) {
        if (n < 1) {
            Log.w("ViewPager", "Requested offscreen page limit " + n + " too small; defaulting to " + 1);
            n = 1;
        }
        if (n != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = n;
            this.populate();
        }
    }
    
    @Deprecated
    public void setOnPageChangeListener(final OnPageChangeListener mOnPageChangeListener) {
        this.mOnPageChangeListener = mOnPageChangeListener;
    }
    
    public void setPageMargin(final int mPageMargin) {
        final int mPageMargin2 = this.mPageMargin;
        this.mPageMargin = mPageMargin;
        final int width = this.getWidth();
        this.recomputeScrollPosition(width, width, mPageMargin, mPageMargin2);
        this.requestLayout();
    }
    
    public void setPageMarginDrawable(@DrawableRes final int n) {
        this.setPageMarginDrawable(ContextCompat.getDrawable(this.getContext(), n));
    }
    
    public void setPageMarginDrawable(final Drawable mMarginDrawable) {
        this.mMarginDrawable = mMarginDrawable;
        if (mMarginDrawable != null) {
            this.refreshDrawableState();
        }
        this.setWillNotDraw(mMarginDrawable == null);
        this.invalidate();
    }
    
    public void setPageTransformer(final boolean b, final PageTransformer pageTransformer) {
        this.setPageTransformer(b, pageTransformer, 2);
    }
    
    public void setPageTransformer(final boolean b, final PageTransformer mPageTransformer, final int mPageTransformerLayerType) {
        int mDrawingOrder = 1;
        final boolean childrenDrawingOrderEnabled = mPageTransformer != null;
        int n;
        if (childrenDrawingOrderEnabled == (this.mPageTransformer != null)) {
            n = 0;
        }
        else {
            n = 1;
        }
        this.mPageTransformer = mPageTransformer;
        this.setChildrenDrawingOrderEnabled(childrenDrawingOrderEnabled);
        if (!childrenDrawingOrderEnabled) {
            this.mDrawingOrder = 0;
        }
        else {
            if (b) {
                mDrawingOrder = 2;
            }
            this.mDrawingOrder = mDrawingOrder;
            this.mPageTransformerLayerType = mPageTransformerLayerType;
        }
        if (n != 0) {
            this.populate();
        }
    }
    
    void setScrollState(final int mScrollState) {
        boolean b = false;
        if (this.mScrollState != mScrollState) {
            this.mScrollState = mScrollState;
            if (this.mPageTransformer != null) {
                if (mScrollState != 0) {
                    b = true;
                }
                this.enableLayers(b);
            }
            this.dispatchOnScrollStateChanged(mScrollState);
        }
    }
    
    void smoothScrollTo(final int n, final int n2) {
        this.smoothScrollTo(n, n2, 0);
    }
    
    void smoothScrollTo(int a, int n, int abs) {
        if (this.getChildCount() == 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        int n2;
        if (this.mScroller != null && !this.mScroller.isFinished()) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        int n3;
        if (n2 == 0) {
            n3 = this.getScrollX();
        }
        else {
            if (!this.mIsScrollStarted) {
                n3 = this.mScroller.getStartX();
            }
            else {
                n3 = this.mScroller.getCurrX();
            }
            this.mScroller.abortAnimation();
            this.setScrollingCacheEnabled(false);
        }
        final int scrollY = this.getScrollY();
        final int n4 = a - n3;
        n -= scrollY;
        if (n4 == 0 && n == 0) {
            this.completeScroll(false);
            this.populate();
            this.setScrollState(0);
            return;
        }
        this.setScrollingCacheEnabled(true);
        this.setScrollState(2);
        a = this.getClientWidth();
        final int n5 = a / 2;
        final float min = Math.min(1.0f, Math.abs(n4) * 1.0f / a);
        final float n6 = (float)n5;
        final float n7 = (float)n5;
        final float distanceInfluenceForSnapDuration = this.distanceInfluenceForSnapDuration(min);
        abs = Math.abs(abs);
        if (abs <= 0) {
            a = (int)((Math.abs(n4) / (a * this.mAdapter.getPageWidth(this.mCurItem) + this.mPageMargin) + 1.0f) * 100.0f);
        }
        else {
            a = Math.round(Math.abs((n7 * distanceInfluenceForSnapDuration + n6) / abs) * 1000.0f) * 4;
        }
        a = Math.min(a, 600);
        this.mIsScrollStarted = false;
        this.mScroller.startScroll(n3, scrollY, n4, n, a);
        ViewCompat.postInvalidateOnAnimation((View)this);
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        boolean b = false;
        if (super.verifyDrawable(drawable) || drawable == this.mMarginDrawable) {
            b = true;
        }
        return b;
    }
    
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface DecorView {
    }
    
    static class ItemInfo
    {
        Object object;
        float offset;
        int position;
        boolean scrolling;
        float widthFactor;
    }
    
    public static class LayoutParams extends ViewGroup$LayoutParams
    {
        int childIndex;
        public int gravity;
        public boolean isDecor;
        boolean needsMeasure;
        int position;
        float widthFactor;
        
        public LayoutParams() {
            super(-1, -1);
            this.widthFactor = 0.0f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.widthFactor = 0.0f;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, ViewPager.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }
    
    class MyAccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private boolean canScroll() {
            boolean b = true;
            if (ViewPager.this.mAdapter == null || ViewPager.this.mAdapter.getCount() <= 1) {
                b = false;
            }
            return b;
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)ViewPager.class.getName());
            accessibilityEvent.setScrollable(this.canScroll());
            if (accessibilityEvent.getEventType() == 4096 && ViewPager.this.mAdapter != null) {
                accessibilityEvent.setItemCount(ViewPager.this.mAdapter.getCount());
                accessibilityEvent.setFromIndex(ViewPager.this.mCurItem);
                accessibilityEvent.setToIndex(ViewPager.this.mCurItem);
            }
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
            accessibilityNodeInfoCompat.setScrollable(this.canScroll());
            if (ViewPager.this.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            if (ViewPager.this.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
            }
        }
        
        @Override
        public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
            if (super.performAccessibilityAction(view, n, bundle)) {
                return true;
            }
            switch (n) {
                default: {
                    return false;
                }
                case 4096: {
                    if (!ViewPager.this.canScrollHorizontally(1)) {
                        return false;
                    }
                    ViewPager.this.setCurrentItem(ViewPager.this.mCurItem + 1);
                    return true;
                }
                case 8192: {
                    if (!ViewPager.this.canScrollHorizontally(-1)) {
                        return false;
                    }
                    ViewPager.this.setCurrentItem(ViewPager.this.mCurItem - 1);
                    return true;
                }
            }
        }
    }
    
    public interface OnAdapterChangeListener
    {
        void onAdapterChanged(@NonNull final ViewPager p0, @Nullable final PagerAdapter p1, @Nullable final PagerAdapter p2);
    }
    
    public interface OnPageChangeListener
    {
        void onPageScrollStateChanged(final int p0);
        
        void onPageScrolled(final int p0, final float p1, final int p2);
        
        void onPageSelected(final int p0);
    }
    
    public interface PageTransformer
    {
        void transformPage(final View p0, final float p1);
    }
    
    private class PagerObserver extends DataSetObserver
    {
        PagerObserver() {
        }
        
        public void onChanged() {
            ViewPager.this.dataSetChanged();
        }
        
        public void onInvalidated() {
            ViewPager.this.dataSetChanged();
        }
    }
    
    public static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Parcelable adapterState;
        ClassLoader loader;
        int position;
        
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
        
        SavedState(final Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader);
            this.loader = classLoader;
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, n);
        }
    }
    
    public static class SimpleOnPageChangeListener implements OnPageChangeListener
    {
        @Override
        public void onPageScrollStateChanged(final int n) {
        }
        
        @Override
        public void onPageScrolled(final int n, final float n2, final int n3) {
        }
        
        @Override
        public void onPageSelected(final int n) {
        }
    }
    
    static class ViewPositionComparator implements Comparator<View>
    {
        @Override
        public int compare(final View view, final View view2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final LayoutParams layoutParams2 = (LayoutParams)view2.getLayoutParams();
            if (layoutParams.isDecor == layoutParams2.isDecor) {
                return layoutParams.position - layoutParams2.position;
            }
            int n;
            if (!layoutParams.isDecor) {
                n = -1;
            }
            else {
                n = 1;
            }
            return n;
        }
    }
}
