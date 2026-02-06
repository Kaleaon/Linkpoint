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
/* loaded from: classes.dex */
public class GridLayoutManager extends LinearLayoutManager {
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

    /* loaded from: classes.dex */
    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        @Override // android.support.v7.widget.GridLayoutManager.SpanSizeLookup
        public int getSpanIndex(int i, int i2) {
            return i % i2;
        }

        @Override // android.support.v7.widget.GridLayoutManager.SpanSizeLookup
        public int getSpanSize(int i) {
            return 1;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        int mSpanIndex;
        int mSpanSize;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mSpanIndex = -1;
            this.mSpanSize = 0;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
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

    /* loaded from: classes.dex */
    public static abstract class SpanSizeLookup {
        final SparseIntArray mSpanIndexCache = new SparseIntArray();
        private boolean mCacheSpanIndices = false;

        int findReferenceIndexFromCache(int i) {
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

        int getCachedSpanIndex(int i, int i2) {
            if (this.mCacheSpanIndices) {
                int i3 = this.mSpanIndexCache.get(i, -1);
                if (i3 == -1) {
                    int spanIndex = getSpanIndex(i, i2);
                    this.mSpanIndexCache.put(i, spanIndex);
                    return spanIndex;
                }
                return i3;
            }
            return getSpanIndex(i, i2);
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
            if (spanSize2 != i2) {
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
            return 0;
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
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i2, z);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mPendingSpanCountChange = false;
        this.mSpanCount = -1;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(getProperties(context, attributeSet, i, i2).spanCount);
    }

    private void assignSpans(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, boolean z) {
        int i3;
        int i4;
        if (z) {
            i3 = 1;
            i4 = 0;
        } else {
            int i5 = i - 1;
            i = -1;
            i4 = i5;
            i3 = -1;
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
        if (iArr == null || iArr.length != i + 1 || iArr[iArr.length - 1] != i2) {
            iArr = new int[i + 1];
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
        boolean z = i == 1;
        int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (z) {
            while (spanIndex > 0 && anchorInfo.mPosition > 0) {
                anchorInfo.mPosition--;
                spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
            }
            return;
        }
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
    }

    private void ensureViewSet() {
        if (this.mSet != null && this.mSet.length == this.mSpanCount) {
            return;
        }
        this.mSet = new View[this.mSpanCount];
    }

    private int getSpanGroupIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (state.isPreLayout()) {
            int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
            if (convertPreLayoutPositionToPostLayout != -1) {
                return this.mSpanSizeLookup.getSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
            }
            Log.w(TAG, "Cannot find span size for pre layout position. " + i);
            return 0;
        }
        return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
    }

    private int getSpanIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (state.isPreLayout()) {
            int i2 = this.mPreLayoutSpanIndexCache.get(i, -1);
            if (i2 == -1) {
                int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
                if (convertPreLayoutPositionToPostLayout != -1) {
                    return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
                }
                Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
                return 0;
            }
            return i2;
        }
        return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
    }

    private int getSpanSize(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (state.isPreLayout()) {
            int i2 = this.mPreLayoutSpanSizeCache.get(i, -1);
            if (i2 == -1) {
                int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
                if (convertPreLayoutPositionToPostLayout != -1) {
                    return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
                }
                Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
                return 1;
            }
            return i2;
        }
        return this.mSpanSizeLookup.getSpanSize(i);
    }

    private void guessMeasurement(float f, int i) {
        calculateItemBorders(Math.max(Math.round(this.mSpanCount * f), i));
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

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public boolean checkLayoutParams(RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.support.v7.widget.LinearLayoutManager
    void collectPrefetchPositionsForLayoutState(RecyclerView.State state, LinearLayoutManager.LayoutState layoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i = this.mSpanCount;
        for (int i2 = 0; i2 < this.mSpanCount && layoutState.hasMore(state) && i > 0; i2++) {
            int i3 = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(i3, Math.max(0, layoutState.mScrollingOffset));
            i -= this.mSpanSizeLookup.getSpanSize(i3);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
        }
    }

    @Override // android.support.v7.widget.LinearLayoutManager
    View findReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3) {
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
            } else if (((RecyclerView.LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                if (view3 != null) {
                    view = view2;
                    childAt = view3;
                } else {
                    view = view2;
                }
            } else if (this.mOrientationHelper.getDecoratedStart(childAt) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(childAt) >= startAfterPadding) {
                return childAt;
            } else {
                if (view2 != null) {
                    view = view2;
                    childAt = view3;
                } else {
                    view = childAt;
                    childAt = view3;
                }
            }
            i += i4;
            view2 = view;
            view3 = childAt;
        }
        return view2 == null ? view3 : view2;
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return this.mOrientation != 0 ? new LayoutParams(-1, -2) : new LayoutParams(-2, -1);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return !(layoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams(layoutParams) : new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation != 1) {
            if (state.getItemCount() >= 1) {
                return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
            }
            return 0;
        }
        return this.mSpanCount;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation != 0) {
            if (state.getItemCount() >= 1) {
                return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
            }
            return 0;
        }
        return this.mSpanCount;
    }

    int getSpaceForSpanRange(int i, int i2) {
        return (this.mOrientation == 1 && isLayoutRTL()) ? this.mCachedBorders[this.mSpanCount - i] - this.mCachedBorders[(this.mSpanCount - i) - i2] : this.mCachedBorders[i + i2] - this.mCachedBorders[i];
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x002a, code lost:
        assignSpans(r14, r15, r4, r5, r6);
        r5 = 0;
        r3 = 0.0f;
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0036, code lost:
        if (r5 < r4) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0038, code lost:
        if (r7 != false) goto L95;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003a, code lost:
        r2 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003b, code lost:
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x003d, code lost:
        if (r5 < r4) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x003f, code lost:
        r17.mConsumed = r2;
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x004a, code lost:
        if (r13.mOrientation == 1) goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0051, code lost:
        if (r16.mLayoutDirection == (-1)) goto L90;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0053, code lost:
        r3 = r16.mOffset;
        r2 = r2 + r3;
        r5 = r3;
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x005b, code lost:
        r10 = r1;
        r9 = r2;
        r8 = r3;
        r7 = r5;
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0061, code lost:
        if (r2 < r4) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0063, code lost:
        java.util.Arrays.fill(r13.mSet, (java.lang.Object) null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0069, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00f6, code lost:
        r9 = r13.mSet[r5];
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00fe, code lost:
        if (r16.mScrapList == null) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0100, code lost:
        if (r6 != false) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0102, code lost:
        addDisappearingView(r9, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0106, code lost:
        calculateItemDecorationsForChild(r9, r13.mDecorInsets);
        measureChild(r9, r11, false);
        r2 = r13.mOrientationHelper.getDecoratedMeasurement(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0115, code lost:
        if (r2 > r1) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0117, code lost:
        r2 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0118, code lost:
        r1 = (r13.mOrientationHelper.getDecoratedMeasurementInOther(r9) * 1.0f) / ((android.support.v7.widget.GridLayoutManager.LayoutParams) r9.getLayoutParams()).mSpanSize;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x012f, code lost:
        if (r1 <= r3) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0131, code lost:
        r5 = r5 + 1;
        r3 = r1;
        r1 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0138, code lost:
        if (r6 != false) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x013a, code lost:
        addView(r9, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x013f, code lost:
        addView(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0143, code lost:
        addDisappearingView(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0147, code lost:
        r1 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0149, code lost:
        guessMeasurement(r3, r8);
        r3 = 0;
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0150, code lost:
        if (r3 < r4) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0152, code lost:
        r2 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0155, code lost:
        r2 = r13.mSet[r3];
        measureChild(r2, 1073741824, true);
        r2 = r13.mOrientationHelper.getDecoratedMeasurement(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x0165, code lost:
        if (r2 > r1) goto L99;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0167, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x016b, code lost:
        r1 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x016d, code lost:
        r6 = r13.mSet[r5];
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0177, code lost:
        if (r13.mOrientationHelper.getDecoratedMeasurement(r6) != r2) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0179, code lost:
        r5 = r5 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x017e, code lost:
        r1 = (android.support.v7.widget.GridLayoutManager.LayoutParams) r6.getLayoutParams();
        r3 = r1.mDecorInsets;
        r7 = ((r3.top + r3.bottom) + r1.topMargin) + r1.bottomMargin;
        r3 = ((r3.right + r3.left) + r1.leftMargin) + r1.rightMargin;
        r8 = getSpaceForSpanRange(r1.mSpanIndex, r1.mSpanSize);
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01a7, code lost:
        if (r13.mOrientation == 1) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01a9, code lost:
        r3 = android.view.View.MeasureSpec.makeMeasureSpec(r2 - r3, 1073741824);
        r1 = getChildMeasureSpec(r8, 1073741824, r7, r1.height, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x01ba, code lost:
        measureChildWithDecorationsAndMargin(r6, r3, r1, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01bf, code lost:
        r3 = getChildMeasureSpec(r8, 1073741824, r3, r1.width, false);
        r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2 - r7, 1073741824);
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x01d6, code lost:
        if (r16.mLayoutDirection == (-1)) goto L94;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x01d8, code lost:
        r5 = r16.mOffset;
        r1 = r5 + r2;
        r2 = 0;
        r3 = r5;
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01e3, code lost:
        r1 = r16.mOffset;
        r2 = r1 - r2;
        r5 = 0;
        r2 = 0;
        r3 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x01ef, code lost:
        r3 = r16.mOffset;
        r2 = r3 - r2;
        r3 = 0;
        r5 = r2;
        r2 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01fb, code lost:
        r6 = r13.mSet[r2];
        r1 = (android.support.v7.widget.GridLayoutManager.LayoutParams) r6.getLayoutParams();
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0208, code lost:
        if (r13.mOrientation == 1) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x020a, code lost:
        r8 = getPaddingTop() + r13.mCachedBorders[r1.mSpanIndex];
        r10 = r8 + r13.mOrientationHelper.getDecoratedMeasurementInOther(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x021e, code lost:
        layoutDecoratedWithMargins(r6, r7, r8, r9, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0226, code lost:
        if (r1.isItemRemoved() == false) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x0228, code lost:
        r17.mIgnoreConsumed = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x022d, code lost:
        r17.mFocusable |= r6.hasFocusable();
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0243, code lost:
        if (isLayoutRTL() != false) goto L85;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0245, code lost:
        r7 = getPaddingLeft() + r13.mCachedBorders[r1.mSpanIndex];
        r9 = r7 + r13.mOrientationHelper.getDecoratedMeasurementInOther(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x025a, code lost:
        r9 = getPaddingLeft() + r13.mCachedBorders[r13.mSpanCount - r1.mSpanIndex];
        r7 = r9 - r13.mOrientationHelper.getDecoratedMeasurementInOther(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x0276, code lost:
        if (r1.isItemChanged() != false) goto L76;
     */
    @Override // android.support.v7.widget.LinearLayoutManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void layoutChunk(android.support.v7.widget.RecyclerView.Recycler r14, android.support.v7.widget.RecyclerView.State r15, android.support.v7.widget.LinearLayoutManager.LayoutState r16, android.support.v7.widget.LinearLayoutManager.LayoutChunkResult r17) {
        /*
            Method dump skipped, instructions count: 633
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.GridLayoutManager.layoutChunk(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State, android.support.v7.widget.LinearLayoutManager$LayoutState, android.support.v7.widget.LinearLayoutManager$LayoutChunkResult):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.widget.LinearLayoutManager
    public void onAnchorReady(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.AnchorInfo anchorInfo, int i) {
        super.onAnchorReady(recycler, state, anchorInfo, i);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, i);
        }
        ensureViewSet();
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
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
        if (findContainingItemView != null) {
            LayoutParams layoutParams = (LayoutParams) findContainingItemView.getLayoutParams();
            int i8 = layoutParams.mSpanIndex;
            int i9 = layoutParams.mSpanIndex + layoutParams.mSpanSize;
            if (super.onFocusSearchFailed(view, i, recycler, state) != null) {
                if ((convertFocusDirectionToLayoutDirection(i) == 1) != this.mShouldReverseLayout) {
                    childCount = getChildCount() - 1;
                    i2 = -1;
                    i3 = -1;
                } else {
                    childCount = 0;
                    i2 = 1;
                    i3 = getChildCount();
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
                    if (!childAt.hasFocusable() || spanGroupIndex2 == spanGroupIndex) {
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
                            if (childAt.hasFocusable()) {
                                if (min > i11) {
                                    z2 = true;
                                } else if (min == i11) {
                                    if (z == (i15 > i10)) {
                                        z2 = true;
                                    }
                                }
                            } else if (view4 == null && isViewPartiallyVisible(childAt, false, true)) {
                                if (min > i13) {
                                    z2 = true;
                                } else if (min == i13) {
                                    if (z == (i15 > i12)) {
                                        z2 = true;
                                    }
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
                        } else if (childAt.hasFocusable()) {
                            int i17 = layoutParams2.mSpanIndex;
                            int i18 = i13;
                            i6 = i12;
                            view2 = view5;
                            i7 = Math.min(i16, i9) - Math.max(i15, i8);
                            i4 = i18;
                            view3 = childAt;
                            i5 = i17;
                        } else {
                            i6 = layoutParams2.mSpanIndex;
                            i4 = Math.min(i16, i9) - Math.max(i15, i8);
                            view2 = childAt;
                            i7 = i11;
                            i5 = i10;
                            view3 = view4;
                        }
                    } else if (view4 != null) {
                        break;
                    } else {
                        i4 = i13;
                        i5 = i10;
                        i6 = i12;
                        view2 = view5;
                        i7 = i11;
                        view3 = view4;
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
            return null;
        }
        return null;
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
            return;
        }
        LayoutParams layoutParams2 = (LayoutParams) layoutParams;
        int spanGroupIndex = getSpanGroupIndex(recycler, state, layoutParams2.getViewLayoutPosition());
        if (this.mOrientation != 0) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(spanGroupIndex, 1, layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
        } else {
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), spanGroupIndex, 1, this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
        }
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(i, recycler, state);
    }

    @Override // android.support.v7.widget.RecyclerView.LayoutManager
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
            if (i < 1) {
                throw new IllegalArgumentException("Span count should be at least 1. Provided " + i);
            }
            this.mSpanCount = i;
            this.mSpanSizeLookup.invalidateSpanIndexCache();
            requestLayout();
        }
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    @Override // android.support.v7.widget.LinearLayoutManager
    public void setStackFromEnd(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
        }
        super.setStackFromEnd(false);
    }

    @Override // android.support.v7.widget.LinearLayoutManager, android.support.v7.widget.RecyclerView.LayoutManager
    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }
}
