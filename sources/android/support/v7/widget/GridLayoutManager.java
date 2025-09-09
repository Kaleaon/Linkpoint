package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    int[] mCachedBorders;
    final Rect mDecorInsets = new Rect();
    boolean mPendingSpanCountChange = false;
    final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
    final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
    View[] mSet;
    int mSpanCount = -1;
    SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();

    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        public int getSpanIndex(int i, int i2) {
            return i % i2;
        }

        public int getSpanSize(int i) {
            return 1;
        }
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        int mSpanIndex = -1;
        int mSpanSize = 0;

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }
    }

    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanIndices = false;
        final SparseIntArray mSpanIndexCache = new SparseIntArray();

        /* access modifiers changed from: package-private */
        public int findReferenceIndexFromCache(int i) {
            int i2 = 0;
            int size = this.mSpanIndexCache.size() - 1;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (this.mSpanIndexCache.keyAt(i3) >= i) {
                    size = i3 - 1;
                } else {
                    i2 = i3 + 1;
                }
            }
            int i4 = i2 - 1;
            if (i4 >= 0 && i4 < this.mSpanIndexCache.size()) {
                return this.mSpanIndexCache.keyAt(i4);
            }
            return -1;
        }

        /* access modifiers changed from: package-private */
        public int getCachedSpanIndex(int i, int i2) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(i, i2);
            }
            int i3 = this.mSpanIndexCache.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            int spanIndex = getSpanIndex(i, i2);
            this.mSpanIndexCache.put(i, spanIndex);
            return spanIndex;
        }

        public int getSpanGroupIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int spanSize2 = getSpanSize(i5);
                i4 += spanSize2;
                if (i4 == i2) {
                    i3++;
                    i4 = 0;
                } else if (i4 > i2) {
                    i3++;
                    i4 = spanSize2;
                }
            }
            return i4 + spanSize <= i2 ? i3 : i3 + 1;
        }

        public int getSpanIndex(int i, int i2) {
            int spanSize;
            int i3;
            int spanSize2 = getSpanSize(i);
            if (spanSize2 == i2) {
                return 0;
            }
            if (this.mCacheSpanIndices && this.mSpanIndexCache.size() > 0) {
                int findReferenceIndexFromCache = findReferenceIndexFromCache(i);
                if (findReferenceIndexFromCache < 0) {
                    i3 = 0;
                    spanSize = 0;
                } else {
                    spanSize = this.mSpanIndexCache.get(findReferenceIndexFromCache) + getSpanSize(findReferenceIndexFromCache);
                    i3 = findReferenceIndexFromCache + 1;
                }
            } else {
                i3 = 0;
                spanSize = 0;
            }
            int i4 = spanSize;
            for (int i5 = i3; i5 < i; i5++) {
                int spanSize3 = getSpanSize(i5);
                i4 += spanSize3;
                if (i4 == i2) {
                    i4 = 0;
                } else if (i4 > i2) {
                    i4 = spanSize3;
                }
            }
            if (i4 + spanSize2 > i2) {
                return 0;
            }
            return i4;
        }

        public abstract int getSpanSize(int i);

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        public void setSpanIndexCacheEnabled(boolean z) {
            this.mCacheSpanIndices = z;
        }
    }

    public GridLayoutManager(Context context, int i) {
        super(context);
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i2, z);
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setSpanCount(getProperties(context, attributeSet, i, i2).spanCount);
    }

    private void assignSpans(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, boolean z) {
        int i3;
        int i4;
        if (!z) {
            int i5 = i - 1;
            i = -1;
            i4 = i5;
            i3 = -1;
        } else {
            i3 = 1;
            i4 = 0;
        }
        int i6 = 0;
        for (int i7 = i4; i7 != i; i7 += i3) {
            View view = this.mSet[i7];
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            layoutParams.mSpanIndex = i6;
            i6 += layoutParams.mSpanSize;
        }
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewLayoutPosition = layoutParams.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }

    private void calculateItemBorders(int i) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, i);
    }

    static int[] calculateItemBorders(int[] iArr, int i, int i2) {
        int i3;
        int i4 = 0;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length - 1] == i2)) {
            iArr = new int[(i + 1)];
        }
        iArr[0] = 0;
        int i5 = i2 / i;
        int i6 = i2 % i;
        int i7 = 0;
        for (int i8 = 1; i8 <= i; i8++) {
            i4 += i6;
            if (i4 > 0 && i - i4 < i6) {
                i3 = i5 + 1;
                i4 -= i;
            } else {
                i3 = i5;
            }
            i7 += i3;
            iArr[i8] = i7;
        }
        return iArr;
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.AnchorInfo anchorInfo, int i) {
        boolean z = false;
        if (i == 1) {
            z = true;
        }
        int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (!z) {
            int itemCount = state.getItemCount() - 1;
            int i2 = anchorInfo.mPosition;
            int i3 = spanIndex;
            while (i2 < itemCount) {
                int spanIndex2 = getSpanIndex(recycler, state, i2 + 1);
                if (spanIndex2 <= i3) {
                    break;
                }
                i2++;
                i3 = spanIndex2;
            }
            anchorInfo.mPosition = i2;
            return;
        }
        while (spanIndex > 0 && anchorInfo.mPosition > 0) {
            anchorInfo.mPosition--;
            spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        }
    }

    private void ensureViewSet() {
        if (this.mSet == null || this.mSet.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    private int getSpanGroupIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. " + i);
        return 0;
    }

    private int getSpanIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        int i2 = this.mPreLayoutSpanIndexCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 0;
    }

    private int getSpanSize(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        int i2 = this.mPreLayoutSpanSizeCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 1;
    }

    private void guessMeasurement(float f, int i) {
        calculateItemBorders(Math.max(Math.round(((float) this.mSpanCount) * f), i));
    }

    private void measureChild(View view, int i, boolean z) {
        int childMeasureSpec;
        int childMeasureSpec2;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        Rect rect = layoutParams.mDecorInsets;
        int i2 = rect.top + rect.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
        int i3 = layoutParams.rightMargin + rect.right + rect.left + layoutParams.leftMargin;
        int spaceForSpanRange = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
        if (this.mOrientation != 1) {
            int childMeasureSpec3 = getChildMeasureSpec(spaceForSpanRange, i, i2, layoutParams.height, false);
            childMeasureSpec = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), i3, layoutParams.width, true);
            childMeasureSpec2 = childMeasureSpec3;
        } else {
            childMeasureSpec = getChildMeasureSpec(spaceForSpanRange, i, i3, layoutParams.width, false);
            childMeasureSpec2 = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), i2, layoutParams.height, true);
        }
        measureChildWithDecorationsAndMargin(view, childMeasureSpec, childMeasureSpec2, z);
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (!z ? shouldMeasureChild(view, i, i2, layoutParams) : shouldReMeasureChild(view, i, i2, layoutParams)) {
            view.measure(i, i2);
        }
    }

    private void updateMeasurements() {
        calculateItemBorders(getOrientation() != 1 ? (getHeight() - getPaddingBottom()) - getPaddingTop() : (getWidth() - getPaddingRight()) - getPaddingLeft());
    }

    public boolean checkLayoutParams(RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: package-private */
    public void collectPrefetchPositionsForLayoutState(RecyclerView.State state, LinearLayoutManager.LayoutState layoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i = this.mSpanCount;
        for (int i2 = 0; i2 < this.mSpanCount && layoutState.hasMore(state) && i > 0; i2++) {
            int i3 = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(i3, Math.max(0, layoutState.mScrollingOffset));
            i -= this.mSpanSizeLookup.getSpanSize(i3);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
        }
    }

    /* access modifiers changed from: package-private */
    public View findReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3) {
        View view;
        View view2 = null;
        ensureLayoutState();
        int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int i4 = i2 <= i ? -1 : 1;
        View view3 = null;
        while (i != i2) {
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            if (position < 0 || position >= i3) {
                view = view2;
                childAt = view3;
            } else if (getSpanIndex(recycler, state, position) != 0) {
                view = view2;
                childAt = view3;
            } else if (!((RecyclerView.LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                if (this.mOrientationHelper.getDecoratedStart(childAt) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(childAt) >= startAfterPadding) {
                    return childAt;
                }
                if (view2 != null) {
                    view = view2;
                    childAt = view3;
                } else {
                    view = childAt;
                    childAt = view3;
                }
            } else if (view3 != null) {
                view = view2;
                childAt = view3;
            } else {
                view = view2;
            }
            i += i4;
            view2 = view;
            view3 = childAt;
        }
        return view2 == null ? view3 : view2;
    }

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return this.mOrientation != 0 ? new LayoutParams(-1, -2) : new LayoutParams(-2, -1);
    }

    public RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return !(layoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams(layoutParams) : new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
    }

    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() >= 1) {
            return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
        }
        return 0;
    }

    public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() >= 1) {
            return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int getSpaceForSpanRange(int i, int i2) {
        return (this.mOrientation == 1 && isLayoutRTL()) ? this.mCachedBorders[this.mSpanCount - i] - this.mCachedBorders[(this.mSpanCount - i) - i2] : this.mCachedBorders[i + i2] - this.mCachedBorders[i];
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    /* access modifiers changed from: package-private */
    public void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.LayoutState layoutState, LinearLayoutManager.LayoutChunkResult layoutChunkResult) {
        View next;
        int i;
        int i2;
        int i3;
        int i4;
        int childMeasureSpec;
        int makeMeasureSpec;
        int modeInOther = this.mOrientationHelper.getModeInOther();
        boolean z = modeInOther != 1073741824;
        int i5 = getChildCount() <= 0 ? 0 : this.mCachedBorders[this.mSpanCount];
        if (z) {
            updateMeasurements();
        }
        boolean z2 = layoutState.mItemDirection == 1;
        int i6 = 0;
        int i7 = 0;
        int i8 = this.mSpanCount;
        if (!z2) {
            i8 = getSpanIndex(recycler, state, layoutState.mCurrentPosition) + getSpanSize(recycler, state, layoutState.mCurrentPosition);
        }
        while (i6 < this.mSpanCount && layoutState.hasMore(state) && i8 > 0) {
            int i9 = layoutState.mCurrentPosition;
            int spanSize = getSpanSize(recycler, state, i9);
            if (spanSize <= this.mSpanCount) {
                i8 -= spanSize;
                if (i8 < 0 || (next = layoutState.next(recycler)) == null) {
                    break;
                }
                i7 += spanSize;
                this.mSet[i6] = next;
                i6++;
            } else {
                throw new IllegalArgumentException("Item at position " + i9 + " requires " + spanSize + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
            }
        }
        if (i6 != 0) {
            assignSpans(recycler, state, i6, i7, z2);
            int i10 = 0;
            float f = 0.0f;
            int i11 = 0;
            while (i10 < i6) {
                View view = this.mSet[i10];
                if (layoutState.mScrapList != null) {
                    if (!z2) {
                        addDisappearingView(view, 0);
                    } else {
                        addDisappearingView(view);
                    }
                } else if (!z2) {
                    addView(view, 0);
                } else {
                    addView(view);
                }
                calculateItemDecorationsForChild(view, this.mDecorInsets);
                measureChild(view, modeInOther, false);
                int decoratedMeasurement = this.mOrientationHelper.getDecoratedMeasurement(view);
                if (decoratedMeasurement <= i11) {
                    decoratedMeasurement = i11;
                }
                float decoratedMeasurementInOther = (((float) this.mOrientationHelper.getDecoratedMeasurementInOther(view)) * 1.0f) / ((float) ((LayoutParams) view.getLayoutParams()).mSpanSize);
                if (decoratedMeasurementInOther <= f) {
                    decoratedMeasurementInOther = f;
                }
                i10++;
                f = decoratedMeasurementInOther;
                i11 = decoratedMeasurement;
            }
            if (!z) {
                i = i11;
            } else {
                guessMeasurement(f, i5);
                int i12 = 0;
                for (int i13 = 0; i13 < i6; i13++) {
                    View view2 = this.mSet[i13];
                    measureChild(view2, 1073741824, true);
                    int decoratedMeasurement2 = this.mOrientationHelper.getDecoratedMeasurement(view2);
                    if (decoratedMeasurement2 > i12) {
                        i12 = decoratedMeasurement2;
                    }
                }
                i = i12;
            }
            for (int i14 = 0; i14 < i6; i14++) {
                View view3 = this.mSet[i14];
                if (this.mOrientationHelper.getDecoratedMeasurement(view3) != i) {
                    LayoutParams layoutParams = (LayoutParams) view3.getLayoutParams();
                    Rect rect = layoutParams.mDecorInsets;
                    int i15 = rect.top + rect.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
                    int i16 = rect.right + rect.left + layoutParams.leftMargin + layoutParams.rightMargin;
                    int spaceForSpanRange = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
                    if (this.mOrientation != 1) {
                        childMeasureSpec = View.MeasureSpec.makeMeasureSpec(i - i16, 1073741824);
                        makeMeasureSpec = getChildMeasureSpec(spaceForSpanRange, 1073741824, i15, layoutParams.height, false);
                    } else {
                        childMeasureSpec = getChildMeasureSpec(spaceForSpanRange, 1073741824, i16, layoutParams.width, false);
                        makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i - i15, 1073741824);
                    }
                    measureChildWithDecorationsAndMargin(view3, childMeasureSpec, makeMeasureSpec, true);
                }
            }
            layoutChunkResult.mConsumed = i;
            int i17 = 0;
            if (this.mOrientation != 1) {
                if (layoutState.mLayoutDirection != -1) {
                    int i18 = layoutState.mOffset;
                    i3 = i + i18;
                    i2 = i18;
                    i4 = 0;
                } else {
                    int i19 = layoutState.mOffset;
                    int i20 = i19 - i;
                    int i21 = i19;
                    i4 = 0;
                    i2 = i20;
                    i3 = i21;
                }
            } else if (layoutState.mLayoutDirection != -1) {
                int i22 = layoutState.mOffset;
                i17 = i22 + i;
                i3 = 0;
                i4 = i22;
                i2 = 0;
            } else {
                i17 = layoutState.mOffset;
                i2 = 0;
                int i23 = i17 - i;
                i3 = 0;
                i4 = i23;
            }
            int i24 = i17;
            int i25 = i3;
            int i26 = i4;
            int i27 = i2;
            for (int i28 = 0; i28 < i6; i28++) {
                View view4 = this.mSet[i28];
                LayoutParams layoutParams2 = (LayoutParams) view4.getLayoutParams();
                if (this.mOrientation != 1) {
                    i26 = getPaddingTop() + this.mCachedBorders[layoutParams2.mSpanIndex];
                    i24 = i26 + this.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                } else if (!isLayoutRTL()) {
                    i27 = getPaddingLeft() + this.mCachedBorders[layoutParams2.mSpanIndex];
                    i25 = i27 + this.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                } else {
                    i25 = getPaddingLeft() + this.mCachedBorders[this.mSpanCount - layoutParams2.mSpanIndex];
                    i27 = i25 - this.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                }
                layoutDecoratedWithMargins(view4, i27, i26, i25, i24);
                if (layoutParams2.isItemRemoved() || layoutParams2.isItemChanged()) {
                    layoutChunkResult.mIgnoreConsumed = true;
                }
                layoutChunkResult.mFocusable |= view4.hasFocusable();
            }
            Arrays.fill(this.mSet, (Object) null);
            return;
        }
        layoutChunkResult.mFinished = true;
    }

    /* access modifiers changed from: package-private */
    public void onAnchorReady(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.AnchorInfo anchorInfo, int i) {
        super.onAnchorReady(recycler, state, anchorInfo, i);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, i);
        }
        ensureViewSet();
    }

    public View onFocusSearchFailed(View view, int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int childCount;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        View view2;
        int i7;
        View view3;
        View findContainingItemView = findContainingItemView(view);
        if (findContainingItemView == null) {
            return null;
        }
        LayoutParams layoutParams = (LayoutParams) findContainingItemView.getLayoutParams();
        int i8 = layoutParams.mSpanIndex;
        int i9 = layoutParams.mSpanIndex + layoutParams.mSpanSize;
        if (super.onFocusSearchFailed(view, i, recycler, state) == null) {
            return null;
        }
        if (!((convertFocusDirectionToLayoutDirection(i) == 1) != this.mShouldReverseLayout)) {
            childCount = 0;
            i2 = 1;
            i3 = getChildCount();
        } else {
            childCount = getChildCount() - 1;
            i2 = -1;
            i3 = -1;
        }
        boolean z = this.mOrientation == 1 && isLayoutRTL();
        View view4 = null;
        int i10 = -1;
        int i11 = 0;
        View view5 = null;
        int i12 = -1;
        int i13 = 0;
        int spanGroupIndex = getSpanGroupIndex(recycler, state, childCount);
        int i14 = childCount;
        while (i14 != i3) {
            int spanGroupIndex2 = getSpanGroupIndex(recycler, state, i14);
            View childAt = getChildAt(i14);
            if (childAt == findContainingItemView) {
                break;
            }
            if (childAt.hasFocusable() && spanGroupIndex2 != spanGroupIndex) {
                if (view4 != null) {
                    break;
                }
                i4 = i13;
                i5 = i10;
                i6 = i12;
                view2 = view5;
                i7 = i11;
                view3 = view4;
            } else {
                LayoutParams layoutParams2 = (LayoutParams) childAt.getLayoutParams();
                int i15 = layoutParams2.mSpanIndex;
                int i16 = layoutParams2.mSpanIndex + layoutParams2.mSpanSize;
                if (childAt.hasFocusable() && i15 == i8 && i16 == i9) {
                    return childAt;
                }
                boolean z2 = false;
                if ((childAt.hasFocusable() && view4 == null) || (!childAt.hasFocusable() && view5 == null)) {
                    z2 = true;
                } else {
                    int min = Math.min(i16, i9) - Math.max(i15, i8);
                    if (!childAt.hasFocusable()) {
                        if (view4 == null && isViewPartiallyVisible(childAt, false, true)) {
                            if (min > i13) {
                                z2 = true;
                            } else if (min == i13) {
                                if (z == (i15 > i12)) {
                                    z2 = true;
                                }
                            }
                        }
                    } else if (min > i11) {
                        z2 = true;
                    } else if (min == i11) {
                        if (z == (i15 > i10)) {
                            z2 = true;
                        }
                    }
                }
                if (!z2) {
                    i4 = i13;
                    i5 = i10;
                    i6 = i12;
                    view2 = view5;
                    i7 = i11;
                    view3 = view4;
                } else if (!childAt.hasFocusable()) {
                    i6 = layoutParams2.mSpanIndex;
                    i4 = Math.min(i16, i9) - Math.max(i15, i8);
                    view2 = childAt;
                    i7 = i11;
                    i5 = i10;
                    view3 = view4;
                } else {
                    int i17 = layoutParams2.mSpanIndex;
                    int i18 = i13;
                    i6 = i12;
                    view2 = view5;
                    i7 = Math.min(i16, i9) - Math.max(i15, i8);
                    i4 = i18;
                    int i19 = i17;
                    view3 = childAt;
                    i5 = i19;
                }
            }
            i14 += i2;
            view4 = view3;
            i11 = i7;
            i10 = i5;
            view5 = view2;
            i12 = i6;
            i13 = i4;
        }
        return view4 == null ? view5 : view4;
    }

    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            LayoutParams layoutParams2 = (LayoutParams) layoutParams;
            int spanGroupIndex = getSpanGroupIndex(recycler, state, layoutParams2.getViewLayoutPosition());
            if (this.mOrientation != 0) {
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(spanGroupIndex, 1, layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
            } else {
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), spanGroupIndex, 1, this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
            }
        } else {
            super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
        }
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
    }

    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(i, recycler, state);
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        int chooseSize;
        int chooseSize2;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, i, i2);
        }
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation != 1) {
            chooseSize2 = chooseSize(i, paddingLeft + rect.width(), getMinimumWidth());
            chooseSize = chooseSize(i2, paddingTop + this.mCachedBorders[this.mCachedBorders.length - 1], getMinimumHeight());
        } else {
            chooseSize = chooseSize(i2, paddingTop + rect.height(), getMinimumHeight());
            chooseSize2 = chooseSize(i, paddingLeft + this.mCachedBorders[this.mCachedBorders.length - 1], getMinimumWidth());
        }
        setMeasuredDimension(chooseSize2, chooseSize);
    }

    public void setSpanCount(int i) {
        if (i != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (i >= 1) {
                this.mSpanCount = i;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                requestLayout();
                return;
            }
            throw new IllegalArgumentException("Span count should be at least 1. Provided " + i);
        }
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public void setStackFromEnd(boolean z) {
        if (!z) {
            super.setStackFromEnd(false);
            return;
        }
        throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }
}
