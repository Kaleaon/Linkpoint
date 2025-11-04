// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.Arrays;
import android.view.View$MeasureSpec;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.util.Log;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.util.SparseIntArray;
import android.graphics.Rect;

public class GridLayoutManager extends LinearLayoutManager
{
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    int[] mCachedBorders;
    final Rect mDecorInsets;
    boolean mPendingSpanCountChange;
    final SparseIntArray mPreLayoutSpanIndexCache;
    final SparseIntArray mPreLayoutSpanSizeCache;
    View[] mSet;
    int mSpanCount;
    SpanSizeLookup mSpanSizeLookup;
    
    public GridLayoutManager(final Context context, final int spanCount) {
        super(context);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = (SpanSizeLookup)new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        this.setSpanCount(spanCount);
    }
    
    public GridLayoutManager(final Context context, final int spanCount, final int n, final boolean b) {
        super(context, n, b);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = (SpanSizeLookup)new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        this.setSpanCount(spanCount);
    }
    
    public GridLayoutManager(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = (SpanSizeLookup)new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        this.setSpanCount(RecyclerView.LayoutManager.getProperties(context, set, n, n2).spanCount);
    }
    
    private void assignSpans(final Recycler recycler, final State state, int n, int i, final boolean b) {
        int n4;
        if (!b) {
            final int n2 = -1;
            i = n - 1;
            final int n3 = -1;
            n = n2;
            n4 = n3;
        }
        else {
            n4 = 1;
            i = 0;
        }
        int mSpanIndex = 0;
        while (i != n) {
            final View view = this.mSet[i];
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            layoutParams.mSpanSize = this.getSpanSize(recycler, state, ((RecyclerView.LayoutManager)this).getPosition(view));
            layoutParams.mSpanIndex = mSpanIndex;
            mSpanIndex += layoutParams.mSpanSize;
            i += n4;
        }
    }
    
    private void cachePreLayoutSpanMapping() {
        for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
            final LayoutParams layoutParams = (LayoutParams)((RecyclerView.LayoutManager)this).getChildAt(i).getLayoutParams();
            final int viewLayoutPosition = ((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }
    
    private void calculateItemBorders(final int n) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, n);
    }
    
    static int[] calculateItemBorders(int[] array, final int n, int n2) {
        final int n3 = 0;
        if (array == null || array.length != n + 1 || array[array.length - 1] != n2) {
            array = new int[n + 1];
        }
        array[0] = 0;
        final int n4 = n2 / n;
        final int n5 = n2 % n;
        int i = 1;
        int n6 = 0;
        n2 = n3;
        while (i <= n) {
            n2 += n5;
            int n7;
            if (n2 > 0 && n - n2 < n5) {
                n7 = n4 + 1;
                n2 -= n;
            }
            else {
                n7 = n4;
            }
            n6 += n7;
            array[i] = n6;
            ++i;
        }
        return array;
    }
    
    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }
    
    private void ensureAnchorIsInCorrectSpan(final Recycler recycler, final State state, final AnchorInfo anchorInfo, int i) {
        int n = 0;
        if (i == 1) {
            n = 1;
        }
        int j;
        i = (j = this.getSpanIndex(recycler, state, anchorInfo.mPosition));
        if (n == 0) {
            final int itemCount = state.getItemCount();
            final int mPosition = anchorInfo.mPosition;
            int n2 = i;
            int spanIndex;
            for (i = mPosition; i < itemCount - 1; ++i, n2 = spanIndex) {
                spanIndex = this.getSpanIndex(recycler, state, i + 1);
                if (spanIndex <= n2) {
                    break;
                }
            }
            anchorInfo.mPosition = i;
        }
        else {
            while (j > 0) {
                if (anchorInfo.mPosition <= 0) {
                    break;
                }
                --anchorInfo.mPosition;
                j = this.getSpanIndex(recycler, state, anchorInfo.mPosition);
            }
        }
    }
    
    private void ensureViewSet() {
        if (this.mSet == null || this.mSet.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }
    
    private int getSpanGroupIndex(final Recycler recycler, final State state, final int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
        }
        final int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. " + i);
        return 0;
    }
    
    private int getSpanIndex(final Recycler recycler, final State state, final int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        final int value = this.mPreLayoutSpanIndexCache.get(i, -1);
        if (value != -1) {
            return value;
        }
        final int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 0;
    }
    
    private int getSpanSize(final Recycler recycler, final State state, final int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        final int value = this.mPreLayoutSpanSizeCache.get(i, -1);
        if (value != -1) {
            return value;
        }
        final int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
        }
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 1;
    }
    
    private void guessMeasurement(final float n, final int b) {
        this.calculateItemBorders(Math.max(Math.round(this.mSpanCount * n), b));
    }
    
    private void measureChild(final View view, int n, final boolean b) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final Rect mDecorInsets = layoutParams.mDecorInsets;
        final int n2 = mDecorInsets.top + mDecorInsets.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
        final int n3 = layoutParams.rightMargin + (mDecorInsets.right + mDecorInsets.left + layoutParams.leftMargin);
        final int spaceForSpanRange = this.getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
        int n4;
        if (this.mOrientation != 1) {
            n4 = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, n, n2, layoutParams.height, false);
            n = RecyclerView.LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), ((RecyclerView.LayoutManager)this).getWidthMode(), n3, layoutParams.width, true);
        }
        else {
            n = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, n, n3, layoutParams.width, false);
            n4 = RecyclerView.LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), ((RecyclerView.LayoutManager)this).getHeightMode(), n2, layoutParams.height, true);
        }
        this.measureChildWithDecorationsAndMargin(view, n, n4, b);
    }
    
    private void measureChildWithDecorationsAndMargin(final View view, final int n, final int n2, final boolean b) {
        final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
        boolean b2;
        if (!b) {
            b2 = ((RecyclerView.LayoutManager)this).shouldMeasureChild(view, n, n2, layoutParams);
        }
        else {
            b2 = ((RecyclerView.LayoutManager)this).shouldReMeasureChild(view, n, n2, layoutParams);
        }
        if (b2) {
            view.measure(n, n2);
        }
    }
    
    private void updateMeasurements() {
        int n;
        if (this.getOrientation() != 1) {
            n = ((RecyclerView.LayoutManager)this).getHeight() - ((RecyclerView.LayoutManager)this).getPaddingBottom() - ((RecyclerView.LayoutManager)this).getPaddingTop();
        }
        else {
            n = ((RecyclerView.LayoutManager)this).getWidth() - ((RecyclerView.LayoutManager)this).getPaddingRight() - ((RecyclerView.LayoutManager)this).getPaddingLeft();
        }
        this.calculateItemBorders(n);
    }
    
    @Override
    public boolean checkLayoutParams(final RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }
    
    @Override
    void collectPrefetchPositionsForLayoutState(final State state, final LayoutState layoutState, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int mCurrentPosition;
        for (int mSpanCount = this.mSpanCount, n = 0; n < this.mSpanCount && layoutState.hasMore(state) && mSpanCount > 0; mSpanCount -= this.mSpanSizeLookup.getSpanSize(mCurrentPosition), layoutState.mCurrentPosition += layoutState.mItemDirection, ++n) {
            mCurrentPosition = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(mCurrentPosition, Math.max(0, layoutState.mScrollingOffset));
        }
    }
    
    @Override
    View findReferenceChild(final Recycler recycler, final State state, int i, final int n, final int n2) {
        View view = null;
        this.ensureLayoutState();
        final int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        final int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int n3;
        if (n <= i) {
            n3 = -1;
        }
        else {
            n3 = 1;
        }
        View view2 = null;
        while (i != n) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int position = ((RecyclerView.LayoutManager)this).getPosition(child);
            View view4;
            View view5;
            if (position >= 0 && position < n2) {
                if (this.getSpanIndex(recycler, state, position) == 0) {
                    if (!((RecyclerView.LayoutParams)child.getLayoutParams()).isItemRemoved()) {
                        if (this.mOrientationHelper.getDecoratedStart(child) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(child) >= startAfterPadding) {
                            return child;
                        }
                        if (view != null) {
                            final View view3 = view;
                            view4 = view2;
                            view5 = view3;
                        }
                        else {
                            view4 = view2;
                            view5 = child;
                        }
                    }
                    else if (view2 != null) {
                        final View view6 = view2;
                        view5 = view;
                        view4 = view6;
                    }
                    else {
                        view5 = view;
                        view4 = child;
                    }
                }
                else {
                    final View view7 = view2;
                    view5 = view;
                    view4 = view7;
                }
            }
            else {
                final View view8 = view;
                view4 = view2;
                view5 = view8;
            }
            i += n3;
            final View view9 = view4;
            view = view5;
            view2 = view9;
        }
        if (view != null) {
            view2 = view;
        }
        return view2;
    }
    
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation != 0) {
            return new LayoutParams(-1, -2);
        }
        return new LayoutParams(-2, -1);
    }
    
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(final Context context, final AttributeSet set) {
        return new LayoutParams(context, set);
    }
    
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (!(viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams)) {
            return new LayoutParams(viewGroup$LayoutParams);
        }
        return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
    }
    
    @Override
    public int getColumnCountForAccessibility(final Recycler recycler, final State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() >= 1) {
            return this.getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
        }
        return 0;
    }
    
    @Override
    public int getRowCountForAccessibility(final Recycler recycler, final State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() >= 1) {
            return this.getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
        }
        return 0;
    }
    
    int getSpaceForSpanRange(final int n, final int n2) {
        if (this.mOrientation == 1 && this.isLayoutRTL()) {
            return this.mCachedBorders[this.mSpanCount - n] - this.mCachedBorders[this.mSpanCount - n - n2];
        }
        return this.mCachedBorders[n + n2] - this.mCachedBorders[n];
    }
    
    public int getSpanCount() {
        return this.mSpanCount;
    }
    
    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }
    
    @Override
    void layoutChunk(final Recycler recycler, final State state, final LayoutState layoutState, final LayoutChunkResult layoutChunkResult) {
        final int modeInOther = this.mOrientationHelper.getModeInOther();
        boolean b;
        if (modeInOther == 1073741824) {
            b = false;
        }
        else {
            b = true;
        }
        int n;
        if (((RecyclerView.LayoutManager)this).getChildCount() <= 0) {
            n = 0;
        }
        else {
            n = this.mCachedBorders[this.mSpanCount];
        }
        if (b) {
            this.updateMeasurements();
        }
        final boolean b2 = layoutState.mItemDirection == 1;
        int n2 = 0;
        int n3 = 0;
        int mSpanCount = this.mSpanCount;
        if (!b2) {
            mSpanCount = this.getSpanIndex(recycler, state, layoutState.mCurrentPosition) + this.getSpanSize(recycler, state, layoutState.mCurrentPosition);
        }
        while (n2 < this.mSpanCount && layoutState.hasMore(state) && mSpanCount > 0) {
            final int mCurrentPosition = layoutState.mCurrentPosition;
            final int spanSize = this.getSpanSize(recycler, state, mCurrentPosition);
            if (spanSize > this.mSpanCount) {
                throw new IllegalArgumentException("Item at position " + mCurrentPosition + " requires " + spanSize + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
            }
            mSpanCount -= spanSize;
            if (mSpanCount < 0) {
                break;
            }
            final View next = layoutState.next(recycler);
            if (next == null) {
                break;
            }
            n3 += spanSize;
            this.mSet[n2] = next;
            ++n2;
        }
        if (n2 != 0) {
            this.assignSpans(recycler, state, n2, n3, b2);
            int i = 0;
            float n4 = 0.0f;
            int mConsumed = 0;
            while (i < n2) {
                final View view = this.mSet[i];
                if (layoutState.mScrapList != null) {
                    if (!b2) {
                        ((RecyclerView.LayoutManager)this).addDisappearingView(view, 0);
                    }
                    else {
                        ((RecyclerView.LayoutManager)this).addDisappearingView(view);
                    }
                }
                else if (!b2) {
                    ((RecyclerView.LayoutManager)this).addView(view, 0);
                }
                else {
                    ((RecyclerView.LayoutManager)this).addView(view);
                }
                ((RecyclerView.LayoutManager)this).calculateItemDecorationsForChild(view, this.mDecorInsets);
                this.measureChild(view, modeInOther, false);
                int decoratedMeasurement;
                if ((decoratedMeasurement = this.mOrientationHelper.getDecoratedMeasurement(view)) <= mConsumed) {
                    decoratedMeasurement = mConsumed;
                }
                final float n5 = this.mOrientationHelper.getDecoratedMeasurementInOther(view) * 1.0f / ((LayoutParams)view.getLayoutParams()).mSpanSize;
                if (n5 > n4) {
                    n4 = n5;
                }
                ++i;
                mConsumed = decoratedMeasurement;
            }
            if (b) {
                this.guessMeasurement(n4, n);
                int j = 0;
                mConsumed = 0;
                while (j < n2) {
                    final View view2 = this.mSet[j];
                    this.measureChild(view2, 1073741824, true);
                    final int decoratedMeasurement2 = this.mOrientationHelper.getDecoratedMeasurement(view2);
                    if (decoratedMeasurement2 > mConsumed) {
                        mConsumed = decoratedMeasurement2;
                    }
                    ++j;
                }
            }
            for (int k = 0; k < n2; ++k) {
                final View view3 = this.mSet[k];
                if (this.mOrientationHelper.getDecoratedMeasurement(view3) != mConsumed) {
                    final LayoutParams layoutParams = (LayoutParams)view3.getLayoutParams();
                    final Rect mDecorInsets = layoutParams.mDecorInsets;
                    final int n6 = mDecorInsets.top + mDecorInsets.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
                    final int n7 = mDecorInsets.right + mDecorInsets.left + layoutParams.leftMargin + layoutParams.rightMargin;
                    final int spaceForSpanRange = this.getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
                    int n8;
                    int n9;
                    if (this.mOrientation != 1) {
                        n8 = View$MeasureSpec.makeMeasureSpec(mConsumed - n7, 1073741824);
                        n9 = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, 1073741824, n6, layoutParams.height, false);
                    }
                    else {
                        n8 = RecyclerView.LayoutManager.getChildMeasureSpec(spaceForSpanRange, 1073741824, n7, layoutParams.width, false);
                        n9 = View$MeasureSpec.makeMeasureSpec(mConsumed - n6, 1073741824);
                    }
                    this.measureChildWithDecorationsAndMargin(view3, n8, n9, true);
                }
            }
            layoutChunkResult.mConsumed = mConsumed;
            int mOffset = 0;
            int mOffset2;
            int n10;
            int mOffset3;
            if (this.mOrientation != 1) {
                if (layoutState.mLayoutDirection != -1) {
                    mOffset2 = layoutState.mOffset;
                    n10 = mConsumed + mOffset2;
                    mOffset3 = 0;
                }
                else {
                    final int mOffset4 = layoutState.mOffset;
                    mOffset3 = 0;
                    mOffset2 = mOffset4 - mConsumed;
                    n10 = mOffset4;
                }
            }
            else if (layoutState.mLayoutDirection != -1) {
                mOffset3 = layoutState.mOffset;
                mOffset = mOffset3 + mConsumed;
                n10 = 0;
                mOffset2 = 0;
            }
            else {
                mOffset = layoutState.mOffset;
                mOffset2 = 0;
                final int n11 = 0;
                mOffset3 = mOffset - mConsumed;
                n10 = n11;
            }
            final int n12 = mOffset2;
            final int n13 = 0;
            int n14 = mOffset;
            int n15 = mOffset3;
            int n16 = n12;
            for (int l = n13; l < n2; ++l) {
                final View view4 = this.mSet[l];
                final LayoutParams layoutParams2 = (LayoutParams)view4.getLayoutParams();
                if (this.mOrientation != 1) {
                    n15 = ((RecyclerView.LayoutManager)this).getPaddingTop() + this.mCachedBorders[layoutParams2.mSpanIndex];
                    n14 = n15 + this.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                }
                else if (!this.isLayoutRTL()) {
                    n16 = ((RecyclerView.LayoutManager)this).getPaddingLeft() + this.mCachedBorders[layoutParams2.mSpanIndex];
                    n10 = n16 + this.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                }
                else {
                    n10 = ((RecyclerView.LayoutManager)this).getPaddingLeft() + this.mCachedBorders[this.mSpanCount - layoutParams2.mSpanIndex];
                    n16 = n10 - this.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                }
                ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(view4, n16, n15, n10, n14);
                if (((RecyclerView.LayoutParams)layoutParams2).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams2).isItemChanged()) {
                    layoutChunkResult.mIgnoreConsumed = true;
                }
                layoutChunkResult.mFocusable |= view4.hasFocusable();
            }
            Arrays.fill(this.mSet, null);
            return;
        }
        layoutChunkResult.mFinished = true;
    }
    
    @Override
    void onAnchorReady(final Recycler recycler, final State state, final AnchorInfo anchorInfo, final int n) {
        super.onAnchorReady(recycler, state, anchorInfo, n);
        this.updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            this.ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, n);
        }
        this.ensureViewSet();
    }
    
    @Override
    public View onFocusSearchFailed(View view, int n, final Recycler recycler, final State state) {
        final View containingItemView = ((RecyclerView.LayoutManager)this).findContainingItemView(view);
        if (containingItemView == null) {
            return null;
        }
        final LayoutParams layoutParams = (LayoutParams)containingItemView.getLayoutParams();
        final int mSpanIndex = layoutParams.mSpanIndex;
        final int b = layoutParams.mSpanIndex + layoutParams.mSpanSize;
        if (super.onFocusSearchFailed(view, n, recycler, state) != null) {
            if (this.convertFocusDirectionToLayoutDirection(n) == 1 == this.mShouldReverseLayout) {
                n = 0;
            }
            else {
                n = 1;
            }
            int childCount;
            int n2;
            if (n == 0) {
                childCount = ((RecyclerView.LayoutManager)this).getChildCount();
                n = 0;
                n2 = 1;
            }
            else {
                n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
                n2 = -1;
                childCount = -1;
            }
            final boolean b2 = this.mOrientation == 1 && this.isLayoutRTL();
            view = null;
            int n3 = -1;
            int n4 = 0;
            View view2 = null;
            int n5 = -1;
            int n6 = 0;
            final int spanGroupIndex = this.getSpanGroupIndex(recycler, state, n);
            int n14;
            for (int i = n; i != childCount; i = n14) {
                n = this.getSpanGroupIndex(recycler, state, i);
                final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                if (child == containingItemView) {
                    break;
                }
                int n7;
                int n9;
                int n10;
                View view4;
                if (child.hasFocusable() && n != spanGroupIndex) {
                    if (view != null) {
                        break;
                    }
                    n = n6;
                    n7 = n3;
                    final int n8 = n5;
                    n9 = n4;
                    final View view3 = view;
                    n10 = n8;
                    view = view2;
                    view4 = view3;
                }
                else {
                    final LayoutParams layoutParams2 = (LayoutParams)child.getLayoutParams();
                    final int mSpanIndex2 = layoutParams2.mSpanIndex;
                    final int a = layoutParams2.mSpanIndex + layoutParams2.mSpanSize;
                    if (child.hasFocusable() && mSpanIndex2 == mSpanIndex && a == b) {
                        return child;
                    }
                    final int n11 = 0;
                    if ((!child.hasFocusable() || view != null) && (child.hasFocusable() || view2 != null)) {
                        n = Math.max(mSpanIndex2, mSpanIndex);
                        final int n12 = Math.min(a, b) - n;
                        if (!child.hasFocusable()) {
                            if (view != null) {
                                n = n11;
                            }
                            else {
                                n = n11;
                                if (((RecyclerView.LayoutManager)this).isViewPartiallyVisible(child, false, true)) {
                                    if (n12 <= n6) {
                                        n = n11;
                                        if (n12 == n6) {
                                            final boolean b3 = mSpanIndex2 > n5;
                                            n = n11;
                                            if (b2 == b3) {
                                                n = 1;
                                            }
                                        }
                                    }
                                    else {
                                        n = 1;
                                    }
                                }
                            }
                        }
                        else if (n12 <= n4) {
                            n = n11;
                            if (n12 == n4) {
                                final boolean b4 = mSpanIndex2 > n3;
                                n = n11;
                                if (b2 == b4) {
                                    n = 1;
                                }
                            }
                        }
                        else {
                            n = 1;
                        }
                    }
                    else {
                        n = 1;
                    }
                    if (n == 0) {
                        n = n6;
                        n7 = n3;
                        final int n13 = n5;
                        final View view5 = view2;
                        view4 = view;
                        n9 = n4;
                        view = view5;
                        n10 = n13;
                    }
                    else if (!child.hasFocusable()) {
                        final int mSpanIndex3 = layoutParams2.mSpanIndex;
                        n = Math.min(a, b) - Math.max(mSpanIndex2, mSpanIndex);
                        n9 = n4;
                        n7 = n3;
                        view4 = view;
                        n10 = mSpanIndex3;
                        view = child;
                    }
                    else {
                        final int mSpanIndex4 = layoutParams2.mSpanIndex;
                        n = Math.min(a, b);
                        final int max = Math.max(mSpanIndex2, mSpanIndex);
                        n10 = n5;
                        view = view2;
                        n9 = n - max;
                        n = n6;
                        view4 = child;
                        n7 = mSpanIndex4;
                    }
                }
                n14 = i + n2;
                final View view6 = view4;
                final int n15 = n9;
                final int n16 = n7;
                n6 = n;
                n5 = n10;
                view2 = view;
                n4 = n15;
                n3 = n16;
                view = view6;
            }
            if (view == null) {
                view = view2;
            }
            return view;
        }
        return null;
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            final LayoutParams layoutParams2 = (LayoutParams)layoutParams;
            final int spanGroupIndex = this.getSpanGroupIndex(recycler, state, ((RecyclerView.LayoutParams)layoutParams2).getViewLayoutPosition());
            if (this.mOrientation != 0) {
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(spanGroupIndex, 1, layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
            }
            else {
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), spanGroupIndex, 1, this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
            }
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
    }
    
    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }
    
    @Override
    public void onItemsChanged(final RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }
    
    @Override
    public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }
    
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, final int n, final int n2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }
    
    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2, final Object o) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        if (state.isPreLayout()) {
            this.cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        this.clearPreLayoutSpanMappingCache();
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }
    
    @Override
    public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
        this.updateMeasurements();
        this.ensureViewSet();
        return super.scrollHorizontallyBy(n, recycler, state);
    }
    
    @Override
    public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
        this.updateMeasurements();
        this.ensureViewSet();
        return super.scrollVerticallyBy(n, recycler, state);
    }
    
    @Override
    public void setMeasuredDimension(final Rect rect, int n, int n2) {
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, n, n2);
        }
        final int n3 = ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight();
        final int n4 = ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom();
        if (this.mOrientation != 1) {
            n = RecyclerView.LayoutManager.chooseSize(n, n3 + rect.width(), ((RecyclerView.LayoutManager)this).getMinimumWidth());
            n2 = RecyclerView.LayoutManager.chooseSize(n2, n4 + this.mCachedBorders[this.mCachedBorders.length - 1], ((RecyclerView.LayoutManager)this).getMinimumHeight());
        }
        else {
            n2 = RecyclerView.LayoutManager.chooseSize(n2, n4 + rect.height(), ((RecyclerView.LayoutManager)this).getMinimumHeight());
            n = RecyclerView.LayoutManager.chooseSize(n, n3 + this.mCachedBorders[this.mCachedBorders.length - 1], ((RecyclerView.LayoutManager)this).getMinimumWidth());
        }
        ((RecyclerView.LayoutManager)this).setMeasuredDimension(n, n2);
    }
    
    public void setSpanCount(final int n) {
        if (n == this.mSpanCount) {
            return;
        }
        this.mPendingSpanCountChange = true;
        if (n >= 1) {
            this.mSpanCount = n;
            this.mSpanSizeLookup.invalidateSpanIndexCache();
            ((RecyclerView.LayoutManager)this).requestLayout();
            return;
        }
        throw new IllegalArgumentException("Span count should be at least 1. Provided " + n);
    }
    
    public void setSpanSizeLookup(final SpanSizeLookup mSpanSizeLookup) {
        this.mSpanSizeLookup = mSpanSizeLookup;
    }
    
    @Override
    public void setStackFromEnd(final boolean b) {
        if (!b) {
            super.setStackFromEnd(false);
            return;
        }
        throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        boolean b = false;
        if (this.mPendingSavedState == null && !this.mPendingSpanCountChange) {
            b = true;
        }
        return b;
    }
    
    public static final class DefaultSpanSizeLookup extends SpanSizeLookup
    {
        @Override
        public int getSpanIndex(final int n, final int n2) {
            return n % n2;
        }
        
        @Override
        public int getSpanSize(final int n) {
            return 1;
        }
    }
    
    public static class LayoutParams extends RecyclerView.LayoutParams
    {
        public static final int INVALID_SPAN_ID = -1;
        int mSpanIndex;
        int mSpanSize;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }
        
        public int getSpanIndex() {
            return this.mSpanIndex;
        }
        
        public int getSpanSize() {
            return this.mSpanSize;
        }
    }
    
    public abstract static class SpanSizeLookup
    {
        private boolean mCacheSpanIndices;
        final SparseIntArray mSpanIndexCache;
        
        public SpanSizeLookup() {
            this.mSpanIndexCache = new SparseIntArray();
            this.mCacheSpanIndices = false;
        }
        
        int findReferenceIndexFromCache(int n) {
            int i = 0;
            int n2 = this.mSpanIndexCache.size() - 1;
            while (i <= n2) {
                final int n3 = i + n2 >>> 1;
                if (this.mSpanIndexCache.keyAt(n3) >= n) {
                    n2 = n3 - 1;
                }
                else {
                    i = n3 + 1;
                }
            }
            n = i - 1;
            if (n >= 0 && n < this.mSpanIndexCache.size()) {
                return this.mSpanIndexCache.keyAt(n);
            }
            return -1;
        }
        
        int getCachedSpanIndex(final int n, int spanIndex) {
            if (!this.mCacheSpanIndices) {
                return this.getSpanIndex(n, spanIndex);
            }
            final int value = this.mSpanIndexCache.get(n, -1);
            if (value == -1) {
                spanIndex = this.getSpanIndex(n, spanIndex);
                this.mSpanIndexCache.put(n, spanIndex);
                return spanIndex;
            }
            return value;
        }
        
        public int getSpanGroupIndex(final int n, final int n2) {
            final int spanSize = this.getSpanSize(n);
            int i = 0;
            int n3 = 0;
            int n4 = 0;
            while (i < n) {
                final int spanSize2 = this.getSpanSize(i);
                n4 += spanSize2;
                if (n4 != n2) {
                    if (n4 > n2) {
                        ++n3;
                        n4 = spanSize2;
                    }
                }
                else {
                    ++n3;
                    n4 = 0;
                }
                ++i;
            }
            if (n4 + spanSize > n2) {
                ++n3;
            }
            return n3;
        }
        
        public int getSpanIndex(final int n, final int n2) {
            final int spanSize = this.getSpanSize(n);
            if (spanSize == n2) {
                return 0;
            }
            int i;
            int n3;
            if (this.mCacheSpanIndices && this.mSpanIndexCache.size() > 0) {
                i = this.findReferenceIndexFromCache(n);
                if (i < 0) {
                    i = 0;
                    n3 = 0;
                }
                else {
                    n3 = this.mSpanIndexCache.get(i) + this.getSpanSize(i);
                    ++i;
                }
            }
            else {
                i = 0;
                n3 = 0;
            }
            while (i < n) {
                final int spanSize2 = this.getSpanSize(i);
                n3 += spanSize2;
                if (n3 != n2) {
                    if (n3 > n2) {
                        n3 = spanSize2;
                    }
                }
                else {
                    n3 = 0;
                }
                ++i;
            }
            if (n3 + spanSize > n2) {
                return 0;
            }
            return n3;
        }
        
        public abstract int getSpanSize(final int p0);
        
        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }
        
        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }
        
        public void setSpanIndexCacheEnabled(final boolean mCacheSpanIndices) {
            this.mCacheSpanIndices = mCacheSpanIndices;
        }
    }
}
