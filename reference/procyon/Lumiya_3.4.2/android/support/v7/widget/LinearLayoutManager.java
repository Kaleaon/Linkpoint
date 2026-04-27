// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.support.annotation.RestrictTo;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.graphics.PointF;
import android.util.Log;
import java.util.List;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;

public class LinearLayoutManager extends LayoutManager implements ViewDropHandler, ScrollVectorProvider
{
    static final boolean DEBUG = false;
    public static final int HORIZONTAL = 0;
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    private static final String TAG = "LinearLayoutManager";
    public static final int VERTICAL = 1;
    final AnchorInfo mAnchorInfo;
    private int mInitialPrefetchItemCount;
    private boolean mLastStackFromEnd;
    private final LayoutChunkResult mLayoutChunkResult;
    private LayoutState mLayoutState;
    int mOrientation;
    OrientationHelper mOrientationHelper;
    SavedState mPendingSavedState;
    int mPendingScrollPosition;
    int mPendingScrollPositionOffset;
    private boolean mRecycleChildrenOnDetach;
    private boolean mReverseLayout;
    boolean mShouldReverseLayout;
    private boolean mSmoothScrollbarEnabled;
    private boolean mStackFromEnd;
    
    public LinearLayoutManager(final Context context) {
        this(context, 1, false);
    }
    
    public LinearLayoutManager(final Context context, final int orientation, final boolean reverseLayout) {
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        this.setOrientation(orientation);
        this.setReverseLayout(reverseLayout);
        ((RecyclerView.LayoutManager)this).setAutoMeasureEnabled(true);
    }
    
    public LinearLayoutManager(final Context context, final AttributeSet set, final int n, final int n2) {
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        final Properties properties = RecyclerView.LayoutManager.getProperties(context, set, n, n2);
        this.setOrientation(properties.orientation);
        this.setReverseLayout(properties.reverseLayout);
        this.setStackFromEnd(properties.stackFromEnd);
        ((RecyclerView.LayoutManager)this).setAutoMeasureEnabled(true);
    }
    
    private int computeScrollExtent(final State state) {
        final boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            this.ensureLayoutState();
            return ScrollbarHelper.computeScrollExtent(state, this.mOrientationHelper, this.findFirstVisibleChildClosestToStart(!this.mSmoothScrollbarEnabled, true), this.findFirstVisibleChildClosestToEnd(!this.mSmoothScrollbarEnabled || b, true), this, this.mSmoothScrollbarEnabled);
        }
        return 0;
    }
    
    private int computeScrollOffset(final State state) {
        final boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            this.ensureLayoutState();
            return ScrollbarHelper.computeScrollOffset(state, this.mOrientationHelper, this.findFirstVisibleChildClosestToStart(!this.mSmoothScrollbarEnabled, true), this.findFirstVisibleChildClosestToEnd(!this.mSmoothScrollbarEnabled || b, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
        }
        return 0;
    }
    
    private int computeScrollRange(final State state) {
        final boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            this.ensureLayoutState();
            return ScrollbarHelper.computeScrollRange(state, this.mOrientationHelper, this.findFirstVisibleChildClosestToStart(!this.mSmoothScrollbarEnabled, true), this.findFirstVisibleChildClosestToEnd(!this.mSmoothScrollbarEnabled || b, true), this, this.mSmoothScrollbarEnabled);
        }
        return 0;
    }
    
    private View findFirstPartiallyOrCompletelyInvisibleChild(final Recycler recycler, final State state) {
        return this.findOnePartiallyOrCompletelyInvisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount());
    }
    
    private View findFirstReferenceChild(final Recycler recycler, final State state) {
        return this.findReferenceChild(recycler, state, 0, ((RecyclerView.LayoutManager)this).getChildCount(), state.getItemCount());
    }
    
    private View findFirstVisibleChildClosestToEnd(final boolean b, final boolean b2) {
        if (!this.mShouldReverseLayout) {
            return this.findOneVisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, b, b2);
        }
        return this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), b, b2);
    }
    
    private View findFirstVisibleChildClosestToStart(final boolean b, final boolean b2) {
        if (!this.mShouldReverseLayout) {
            return this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), b, b2);
        }
        return this.findOneVisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, b, b2);
    }
    
    private View findLastPartiallyOrCompletelyInvisibleChild(final Recycler recycler, final State state) {
        return this.findOnePartiallyOrCompletelyInvisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1);
    }
    
    private View findLastReferenceChild(final Recycler recycler, final State state) {
        return this.findReferenceChild(recycler, state, ((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, state.getItemCount());
    }
    
    private View findPartiallyOrCompletelyInvisibleChildClosestToEnd(final Recycler recycler, final State state) {
        View view;
        if (!this.mShouldReverseLayout) {
            view = this.findLastPartiallyOrCompletelyInvisibleChild(recycler, state);
        }
        else {
            view = this.findFirstPartiallyOrCompletelyInvisibleChild(recycler, state);
        }
        return view;
    }
    
    private View findPartiallyOrCompletelyInvisibleChildClosestToStart(final Recycler recycler, final State state) {
        View view;
        if (!this.mShouldReverseLayout) {
            view = this.findFirstPartiallyOrCompletelyInvisibleChild(recycler, state);
        }
        else {
            view = this.findLastPartiallyOrCompletelyInvisibleChild(recycler, state);
        }
        return view;
    }
    
    private View findReferenceChildClosestToEnd(final Recycler recycler, final State state) {
        View view;
        if (!this.mShouldReverseLayout) {
            view = this.findLastReferenceChild(recycler, state);
        }
        else {
            view = this.findFirstReferenceChild(recycler, state);
        }
        return view;
    }
    
    private View findReferenceChildClosestToStart(final Recycler recycler, final State state) {
        View view;
        if (!this.mShouldReverseLayout) {
            view = this.findFirstReferenceChild(recycler, state);
        }
        else {
            view = this.findLastReferenceChild(recycler, state);
        }
        return view;
    }
    
    private int fixLayoutEndGap(int n, final Recycler recycler, final State state, final boolean b) {
        final int n2 = this.mOrientationHelper.getEndAfterPadding() - n;
        if (n2 <= 0) {
            return 0;
        }
        final int n3 = -this.scrollBy(-n2, recycler, state);
        if (b) {
            n = this.mOrientationHelper.getEndAfterPadding() - (n + n3);
            if (n > 0) {
                this.mOrientationHelper.offsetChildren(n);
                return n3 + n;
            }
        }
        return n3;
    }
    
    private int fixLayoutStartGap(int n, final Recycler recycler, final State state, final boolean b) {
        final int n2 = n - this.mOrientationHelper.getStartAfterPadding();
        if (n2 <= 0) {
            return 0;
        }
        final int n3 = -this.scrollBy(n2, recycler, state);
        if (b) {
            n = n + n3 - this.mOrientationHelper.getStartAfterPadding();
            if (n > 0) {
                this.mOrientationHelper.offsetChildren(-n);
                return n3 - n;
            }
        }
        return n3;
    }
    
    private View getChildClosestToEnd() {
        int n = 0;
        if (!this.mShouldReverseLayout) {
            n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        }
        return ((RecyclerView.LayoutManager)this).getChildAt(n);
    }
    
    private View getChildClosestToStart() {
        int n = 0;
        if (this.mShouldReverseLayout) {
            n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        }
        return ((RecyclerView.LayoutManager)this).getChildAt(n);
    }
    
    private void layoutForPredictiveAnimations(final Recycler recycler, final State state, final int n, final int n2) {
        if (state.willRunPredictiveAnimations() && ((RecyclerView.LayoutManager)this).getChildCount() != 0 && !state.isPreLayout() && this.supportsPredictiveItemAnimations()) {
            int mExtra = 0;
            int mExtra2 = 0;
            final List<ViewHolder> scrapList = recycler.getScrapList();
            final int size = scrapList.size();
            final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
            int n9;
            for (int i = 0; i < size; i = n9) {
                final ViewHolder viewHolder = scrapList.get(i);
                int n5;
                int n6;
                if (!viewHolder.isRemoved()) {
                    int n3;
                    if (viewHolder.getLayoutPosition() < position == this.mShouldReverseLayout) {
                        n3 = 1;
                    }
                    else {
                        n3 = -1;
                    }
                    if (n3 != -1) {
                        final int n4 = this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView) + mExtra2;
                        n5 = mExtra;
                        n6 = n4;
                    }
                    else {
                        final int n7 = this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView) + mExtra;
                        n6 = mExtra2;
                        n5 = n7;
                    }
                }
                else {
                    final int n8 = mExtra;
                    n6 = mExtra2;
                    n5 = n8;
                }
                n9 = i + 1;
                final int n10 = n5;
                mExtra2 = n6;
                mExtra = n10;
            }
            this.mLayoutState.mScrapList = scrapList;
            if (mExtra > 0) {
                this.updateLayoutStateToFillStart(((RecyclerView.LayoutManager)this).getPosition(this.getChildClosestToStart()), n);
                this.mLayoutState.mExtra = mExtra;
                this.mLayoutState.mAvailable = 0;
                this.mLayoutState.assignPositionFromScrapList();
                this.fill(recycler, this.mLayoutState, state, false);
            }
            if (mExtra2 > 0) {
                this.updateLayoutStateToFillEnd(((RecyclerView.LayoutManager)this).getPosition(this.getChildClosestToEnd()), n2);
                this.mLayoutState.mExtra = mExtra2;
                this.mLayoutState.mAvailable = 0;
                this.mLayoutState.assignPositionFromScrapList();
                this.fill(recycler, this.mLayoutState, state, false);
            }
            this.mLayoutState.mScrapList = null;
        }
    }
    
    private void logChildren() {
        Log.d("LinearLayoutManager", "internal representation of views on the screen");
        for (int i = 0; i < ((RecyclerView.LayoutManager)this).getChildCount(); ++i) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            Log.d("LinearLayoutManager", "item " + ((RecyclerView.LayoutManager)this).getPosition(child) + ", coord:" + this.mOrientationHelper.getDecoratedStart(child));
        }
        Log.d("LinearLayoutManager", "==============");
    }
    
    private void recycleByLayoutState(final Recycler recycler, final LayoutState layoutState) {
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            if (layoutState.mLayoutDirection != -1) {
                this.recycleViewsFromStart(recycler, layoutState.mScrollingOffset);
            }
            else {
                this.recycleViewsFromEnd(recycler, layoutState.mScrollingOffset);
            }
        }
    }
    
    private void recycleChildren(final Recycler recycler, int i, int j) {
        if (i != j) {
            if (j <= i) {
                while (i > j) {
                    ((RecyclerView.LayoutManager)this).removeAndRecycleViewAt(i, recycler);
                    --i;
                }
            }
            else {
                --j;
                while (j >= i) {
                    ((RecyclerView.LayoutManager)this).removeAndRecycleViewAt(j, recycler);
                    --j;
                }
            }
        }
    }
    
    private void recycleViewsFromEnd(final Recycler recycler, int i) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        if (i >= 0) {
            final int n = this.mOrientationHelper.getEnd() - i;
            if (!this.mShouldReverseLayout) {
                View child;
                for (i = childCount - 1; i >= 0; --i) {
                    child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedStart(child) < n || this.mOrientationHelper.getTransformedStartWithDecoration(child) < n) {
                        this.recycleChildren(recycler, childCount - 1, i);
                        return;
                    }
                }
            }
            else {
                View child2;
                for (i = 0; i < childCount; ++i) {
                    child2 = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedStart(child2) < n || this.mOrientationHelper.getTransformedStartWithDecoration(child2) < n) {
                        this.recycleChildren(recycler, 0, i);
                        return;
                    }
                }
            }
        }
    }
    
    private void recycleViewsFromStart(final Recycler recycler, final int n) {
        if (n >= 0) {
            final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
            if (!this.mShouldReverseLayout) {
                for (int i = 0; i < childCount; ++i) {
                    final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedEnd(child) > n || this.mOrientationHelper.getTransformedEndWithDecoration(child) > n) {
                        this.recycleChildren(recycler, 0, i);
                        return;
                    }
                }
            }
            else {
                for (int j = childCount - 1; j >= 0; --j) {
                    final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(j);
                    if (this.mOrientationHelper.getDecoratedEnd(child2) > n || this.mOrientationHelper.getTransformedEndWithDecoration(child2) > n) {
                        this.recycleChildren(recycler, childCount - 1, j);
                        return;
                    }
                }
            }
        }
    }
    
    private void resolveShouldLayoutReverse() {
        boolean mShouldReverseLayout = false;
        if (this.mOrientation != 1 && this.isLayoutRTL()) {
            if (!this.mReverseLayout) {
                mShouldReverseLayout = true;
            }
            this.mShouldReverseLayout = mShouldReverseLayout;
        }
        else {
            this.mShouldReverseLayout = this.mReverseLayout;
        }
    }
    
    private boolean updateAnchorFromChildren(final Recycler recycler, final State state, final AnchorInfo anchorInfo) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return false;
        }
        final View focusedChild = ((RecyclerView.LayoutManager)this).getFocusedChild();
        if (focusedChild != null && anchorInfo.isViewValidAsAnchor(focusedChild, state)) {
            anchorInfo.assignFromViewAndKeepVisibleRect(focusedChild);
            return true;
        }
        if (this.mLastStackFromEnd != this.mStackFromEnd) {
            return false;
        }
        View view;
        if (!anchorInfo.mLayoutFromEnd) {
            view = this.findReferenceChildClosestToStart(recycler, state);
        }
        else {
            view = this.findReferenceChildClosestToEnd(recycler, state);
        }
        if (view == null) {
            return false;
        }
        anchorInfo.assignFromView(view);
        if (!state.isPreLayout() && this.supportsPredictiveItemAnimations()) {
            int n;
            if (this.mOrientationHelper.getDecoratedStart(view) < this.mOrientationHelper.getEndAfterPadding() && this.mOrientationHelper.getDecoratedEnd(view) >= this.mOrientationHelper.getStartAfterPadding()) {
                n = 0;
            }
            else {
                n = 1;
            }
            if (n != 0) {
                int mCoordinate;
                if (!anchorInfo.mLayoutFromEnd) {
                    mCoordinate = this.mOrientationHelper.getStartAfterPadding();
                }
                else {
                    mCoordinate = this.mOrientationHelper.getEndAfterPadding();
                }
                anchorInfo.mCoordinate = mCoordinate;
            }
        }
        return true;
    }
    
    private boolean updateAnchorFromPendingData(final State state, final AnchorInfo anchorInfo) {
        final boolean b = false;
        if (state.isPreLayout() || this.mPendingScrollPosition == -1) {
            return false;
        }
        if (this.mPendingScrollPosition < 0 || this.mPendingScrollPosition >= state.getItemCount()) {
            this.mPendingScrollPosition = -1;
            this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
            return false;
        }
        anchorInfo.mPosition = this.mPendingScrollPosition;
        if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
            if (!(anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd)) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset;
            }
            else {
                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset;
            }
            return true;
        }
        if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
            if (!this.mShouldReverseLayout) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset;
            }
            else {
                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset;
            }
            return true;
        }
        final View viewByPosition = this.findViewByPosition(this.mPendingScrollPosition);
        if (viewByPosition == null) {
            if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
                anchorInfo.mLayoutFromEnd = (this.mPendingScrollPosition < ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0)) == this.mShouldReverseLayout || b);
            }
            anchorInfo.assignCoordinateFromPadding();
        }
        else {
            if (this.mOrientationHelper.getDecoratedMeasurement(viewByPosition) > this.mOrientationHelper.getTotalSpace()) {
                anchorInfo.assignCoordinateFromPadding();
                return true;
            }
            if (this.mOrientationHelper.getDecoratedStart(viewByPosition) - this.mOrientationHelper.getStartAfterPadding() < 0) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
                anchorInfo.mLayoutFromEnd = false;
                return true;
            }
            if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(viewByPosition) < 0) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
                return anchorInfo.mLayoutFromEnd = true;
            }
            int decoratedStart;
            if (!anchorInfo.mLayoutFromEnd) {
                decoratedStart = this.mOrientationHelper.getDecoratedStart(viewByPosition);
            }
            else {
                decoratedStart = this.mOrientationHelper.getDecoratedEnd(viewByPosition) + this.mOrientationHelper.getTotalSpaceChange();
            }
            anchorInfo.mCoordinate = decoratedStart;
        }
        return true;
    }
    
    private void updateAnchorInfoForLayout(final Recycler recycler, final State state, final AnchorInfo anchorInfo) {
        int mPosition = 0;
        if (this.updateAnchorFromPendingData(state, anchorInfo)) {
            return;
        }
        if (!this.updateAnchorFromChildren(recycler, state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            if (this.mStackFromEnd) {
                mPosition = state.getItemCount() - 1;
            }
            anchorInfo.mPosition = mPosition;
        }
    }
    
    private void updateLayoutState(int n, final int mAvailable, final boolean b, final State state) {
        final int n2 = -1;
        final int n3 = 1;
        this.mLayoutState.mInfinite = this.resolveIsInfinite();
        this.mLayoutState.mExtra = this.getExtraLayoutSpace(state);
        this.mLayoutState.mLayoutDirection = n;
        if (n != 1) {
            final View childClosestToStart = this.getChildClosestToStart();
            final LayoutState mLayoutState = this.mLayoutState;
            mLayoutState.mExtra += this.mOrientationHelper.getStartAfterPadding();
            final LayoutState mLayoutState2 = this.mLayoutState;
            if (!this.mShouldReverseLayout) {
                n = n2;
            }
            else {
                n = 1;
            }
            mLayoutState2.mItemDirection = n;
            this.mLayoutState.mCurrentPosition = ((RecyclerView.LayoutManager)this).getPosition(childClosestToStart) + this.mLayoutState.mItemDirection;
            this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedStart(childClosestToStart);
            n = -this.mOrientationHelper.getDecoratedStart(childClosestToStart) + this.mOrientationHelper.getStartAfterPadding();
        }
        else {
            final LayoutState mLayoutState3 = this.mLayoutState;
            mLayoutState3.mExtra += this.mOrientationHelper.getEndPadding();
            final View childClosestToEnd = this.getChildClosestToEnd();
            final LayoutState mLayoutState4 = this.mLayoutState;
            if (!this.mShouldReverseLayout) {
                n = n3;
            }
            else {
                n = -1;
            }
            mLayoutState4.mItemDirection = n;
            this.mLayoutState.mCurrentPosition = ((RecyclerView.LayoutManager)this).getPosition(childClosestToEnd) + this.mLayoutState.mItemDirection;
            this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedEnd(childClosestToEnd);
            n = this.mOrientationHelper.getDecoratedEnd(childClosestToEnd) - this.mOrientationHelper.getEndAfterPadding();
        }
        this.mLayoutState.mAvailable = mAvailable;
        if (b) {
            final LayoutState mLayoutState5 = this.mLayoutState;
            mLayoutState5.mAvailable -= n;
        }
        this.mLayoutState.mScrollingOffset = n;
    }
    
    private void updateLayoutStateToFillEnd(final int mCurrentPosition, final int mOffset) {
        this.mLayoutState.mAvailable = this.mOrientationHelper.getEndAfterPadding() - mOffset;
        final LayoutState mLayoutState = this.mLayoutState;
        int mItemDirection;
        if (!this.mShouldReverseLayout) {
            mItemDirection = 1;
        }
        else {
            mItemDirection = -1;
        }
        mLayoutState.mItemDirection = mItemDirection;
        this.mLayoutState.mCurrentPosition = mCurrentPosition;
        this.mLayoutState.mLayoutDirection = 1;
        this.mLayoutState.mOffset = mOffset;
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
    }
    
    private void updateLayoutStateToFillEnd(final AnchorInfo anchorInfo) {
        this.updateLayoutStateToFillEnd(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }
    
    private void updateLayoutStateToFillStart(int n, final int mOffset) {
        this.mLayoutState.mAvailable = mOffset - this.mOrientationHelper.getStartAfterPadding();
        this.mLayoutState.mCurrentPosition = n;
        final LayoutState mLayoutState = this.mLayoutState;
        if (!this.mShouldReverseLayout) {
            n = -1;
        }
        else {
            n = 1;
        }
        mLayoutState.mItemDirection = n;
        this.mLayoutState.mLayoutDirection = -1;
        this.mLayoutState.mOffset = mOffset;
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
    }
    
    private void updateLayoutStateToFillStart(final AnchorInfo anchorInfo) {
        this.updateLayoutStateToFillStart(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }
    
    @Override
    public void assertNotInLayoutOrScroll(final String s) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(s);
        }
    }
    
    @Override
    public boolean canScrollHorizontally() {
        boolean b = false;
        if (this.mOrientation == 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean canScrollVertically() {
        boolean b = true;
        if (this.mOrientation != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void collectAdjacentPrefetchPositions(int n, int a, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        if (this.mOrientation == 0) {
            a = n;
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0 && a != 0) {
            this.ensureLayoutState();
            if (a <= 0) {
                n = -1;
            }
            else {
                n = 1;
            }
            this.updateLayoutState(n, Math.abs(a), true, state);
            this.collectPrefetchPositionsForLayoutState(state, this.mLayoutState, layoutPrefetchRegistry);
        }
    }
    
    @Override
    public void collectInitialPrefetchPositions(final int n, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        boolean b;
        int n2;
        if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
            b = this.mPendingSavedState.mAnchorLayoutFromEnd;
            n2 = this.mPendingSavedState.mAnchorPosition;
        }
        else {
            this.resolveShouldLayoutReverse();
            b = this.mShouldReverseLayout;
            if (this.mPendingScrollPosition != -1) {
                n2 = this.mPendingScrollPosition;
            }
            else if (!b) {
                n2 = 0;
            }
            else {
                n2 = n - 1;
            }
        }
        int n3;
        if (!b) {
            n3 = 1;
        }
        else {
            n3 = -1;
        }
        final int n4 = 0;
        for (int n5 = n2, n6 = n4; n6 < this.mInitialPrefetchItemCount && n5 >= 0 && n5 < n; n5 += n3, ++n6) {
            layoutPrefetchRegistry.addPosition(n5, 0);
        }
    }
    
    void collectPrefetchPositionsForLayoutState(final State state, final LayoutState layoutState, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final int mCurrentPosition = layoutState.mCurrentPosition;
        if (mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount()) {
            layoutPrefetchRegistry.addPosition(mCurrentPosition, Math.max(0, layoutState.mScrollingOffset));
        }
    }
    
    @Override
    public int computeHorizontalScrollExtent(final State state) {
        return this.computeScrollExtent(state);
    }
    
    @Override
    public int computeHorizontalScrollOffset(final State state) {
        return this.computeScrollOffset(state);
    }
    
    @Override
    public int computeHorizontalScrollRange(final State state) {
        return this.computeScrollRange(state);
    }
    
    @Override
    public PointF computeScrollVectorForPosition(int n) {
        final int n2 = 1;
        boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return null;
        }
        if (n < ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0))) {
            b = true;
        }
        if (b == this.mShouldReverseLayout) {
            n = n2;
        }
        else {
            n = -1;
        }
        if (this.mOrientation != 0) {
            return new PointF(0.0f, (float)n);
        }
        return new PointF((float)n, 0.0f);
    }
    
    @Override
    public int computeVerticalScrollExtent(final State state) {
        return this.computeScrollExtent(state);
    }
    
    @Override
    public int computeVerticalScrollOffset(final State state) {
        return this.computeScrollOffset(state);
    }
    
    @Override
    public int computeVerticalScrollRange(final State state) {
        return this.computeScrollRange(state);
    }
    
    int convertFocusDirectionToLayoutDirection(final int n) {
        int n2 = Integer.MIN_VALUE;
        switch (n) {
            default: {
                return Integer.MIN_VALUE;
            }
            case 1: {
                if (this.mOrientation == 1) {
                    return -1;
                }
                if (!this.isLayoutRTL()) {
                    return -1;
                }
                return 1;
            }
            case 2: {
                if (this.mOrientation == 1) {
                    return 1;
                }
                if (!this.isLayoutRTL()) {
                    return 1;
                }
                return -1;
            }
            case 33: {
                if (this.mOrientation == 1) {
                    n2 = -1;
                }
                return n2;
            }
            case 130: {
                if (this.mOrientation == 1) {
                    n2 = 1;
                }
                return n2;
            }
            case 17: {
                if (this.mOrientation == 0) {
                    n2 = -1;
                }
                return n2;
            }
            case 66: {
                if (this.mOrientation == 0) {
                    n2 = 1;
                }
                return n2;
            }
        }
    }
    
    LayoutState createLayoutState() {
        return new LayoutState();
    }
    
    void ensureLayoutState() {
        if (this.mLayoutState == null) {
            this.mLayoutState = this.createLayoutState();
        }
        if (this.mOrientationHelper == null) {
            this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        }
    }
    
    int fill(final Recycler recycler, final LayoutState layoutState, final State state, final boolean b) {
        final int mAvailable = layoutState.mAvailable;
        if (layoutState.mScrollingOffset != Integer.MIN_VALUE) {
            if (layoutState.mAvailable < 0) {
                layoutState.mScrollingOffset += layoutState.mAvailable;
            }
            this.recycleByLayoutState(recycler, layoutState);
        }
        int n = layoutState.mAvailable + layoutState.mExtra;
        final LayoutChunkResult mLayoutChunkResult = this.mLayoutChunkResult;
        while (layoutState.mInfinite || n > 0) {
            if (layoutState.hasMore(state)) {
                mLayoutChunkResult.resetInternal();
                this.layoutChunk(recycler, state, layoutState, mLayoutChunkResult);
                if (!mLayoutChunkResult.mFinished) {
                    layoutState.mOffset += mLayoutChunkResult.mConsumed * layoutState.mLayoutDirection;
                    int n2;
                    if (mLayoutChunkResult.mIgnoreConsumed && this.mLayoutState.mScrapList == null && state.isPreLayout()) {
                        n2 = n;
                    }
                    else {
                        layoutState.mAvailable -= mLayoutChunkResult.mConsumed;
                        n2 = n - mLayoutChunkResult.mConsumed;
                    }
                    if (layoutState.mScrollingOffset != Integer.MIN_VALUE) {
                        layoutState.mScrollingOffset += mLayoutChunkResult.mConsumed;
                        if (layoutState.mAvailable < 0) {
                            layoutState.mScrollingOffset += layoutState.mAvailable;
                        }
                        this.recycleByLayoutState(recycler, layoutState);
                    }
                    n = n2;
                    if (!b) {
                        continue;
                    }
                    if (!mLayoutChunkResult.mFocusable) {
                        n = n2;
                        continue;
                    }
                }
            }
            return mAvailable - layoutState.mAvailable;
        }
        return mAvailable - layoutState.mAvailable;
    }
    
    public int findFirstCompletelyVisibleItemPosition() {
        final View oneVisibleChild = this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), true, false);
        int position;
        if (oneVisibleChild != null) {
            position = ((RecyclerView.LayoutManager)this).getPosition(oneVisibleChild);
        }
        else {
            position = -1;
        }
        return position;
    }
    
    public int findFirstVisibleItemPosition() {
        final View oneVisibleChild = this.findOneVisibleChild(0, ((RecyclerView.LayoutManager)this).getChildCount(), false, true);
        int position;
        if (oneVisibleChild != null) {
            position = ((RecyclerView.LayoutManager)this).getPosition(oneVisibleChild);
        }
        else {
            position = -1;
        }
        return position;
    }
    
    public int findLastCompletelyVisibleItemPosition() {
        int position = -1;
        final View oneVisibleChild = this.findOneVisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, true, false);
        if (oneVisibleChild != null) {
            position = ((RecyclerView.LayoutManager)this).getPosition(oneVisibleChild);
        }
        return position;
    }
    
    public int findLastVisibleItemPosition() {
        int position = -1;
        final View oneVisibleChild = this.findOneVisibleChild(((RecyclerView.LayoutManager)this).getChildCount() - 1, -1, false, true);
        if (oneVisibleChild != null) {
            position = ((RecyclerView.LayoutManager)this).getPosition(oneVisibleChild);
        }
        return position;
    }
    
    View findOnePartiallyOrCompletelyInvisibleChild(final int n, final int n2) {
        int n3 = 0;
        this.ensureLayoutState();
        if (n2 <= n) {
            if (n2 < n) {
                n3 = -1;
            }
        }
        else {
            n3 = 1;
        }
        if (n3 != 0) {
            int n4;
            int n5;
            if (this.mOrientationHelper.getDecoratedStart(((RecyclerView.LayoutManager)this).getChildAt(n)) >= this.mOrientationHelper.getStartAfterPadding()) {
                n4 = 4161;
                n5 = 4097;
            }
            else {
                n4 = 16644;
                n5 = 16388;
            }
            View view;
            if (this.mOrientation != 0) {
                view = this.mVerticalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n5);
            }
            else {
                view = this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n5);
            }
            return view;
        }
        return ((RecyclerView.LayoutManager)this).getChildAt(n);
    }
    
    View findOneVisibleChild(final int n, final int n2, final boolean b, final boolean b2) {
        int n3 = 0;
        this.ensureLayoutState();
        int n4;
        if (!b) {
            n4 = 320;
        }
        else {
            n4 = 24579;
        }
        if (b2) {
            n3 = 320;
        }
        View view;
        if (this.mOrientation != 0) {
            view = this.mVerticalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n3);
        }
        else {
            view = this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(n, n2, n4, n3);
        }
        return view;
    }
    
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
                if (!((LayoutParams)child.getLayoutParams()).isItemRemoved()) {
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
                    final View view6 = view;
                    view4 = view2;
                    view5 = view6;
                }
                else {
                    view5 = view;
                    view4 = child;
                }
            }
            else {
                final View view7 = view;
                view4 = view2;
                view5 = view7;
            }
            i += n3;
            final View view8 = view4;
            view = view5;
            view2 = view8;
        }
        if (view != null) {
            view2 = view;
        }
        return view2;
    }
    
    @Override
    public View findViewByPosition(final int n) {
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        if (childCount != 0) {
            final int n2 = n - ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
            if (n2 >= 0 && n2 < childCount) {
                final View child = ((RecyclerView.LayoutManager)this).getChildAt(n2);
                if (((RecyclerView.LayoutManager)this).getPosition(child) == n) {
                    return child;
                }
            }
            return super.findViewByPosition(n);
        }
        return null;
    }
    
    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }
    
    protected int getExtraLayoutSpace(final State state) {
        if (!state.hasTargetScrollPosition()) {
            return 0;
        }
        return this.mOrientationHelper.getTotalSpace();
    }
    
    public int getInitialPrefetchItemCount() {
        return this.mInitialPrefetchItemCount;
    }
    
    public int getOrientation() {
        return this.mOrientation;
    }
    
    public boolean getRecycleChildrenOnDetach() {
        return this.mRecycleChildrenOnDetach;
    }
    
    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }
    
    public boolean getStackFromEnd() {
        return this.mStackFromEnd;
    }
    
    protected boolean isLayoutRTL() {
        boolean b = true;
        if (((RecyclerView.LayoutManager)this).getLayoutDirection() != 1) {
            b = false;
        }
        return b;
    }
    
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }
    
    void layoutChunk(final Recycler recycler, final State state, final LayoutState layoutState, final LayoutChunkResult layoutChunkResult) {
        final View next = layoutState.next(recycler);
        if (next != null) {
            final LayoutParams layoutParams = (LayoutParams)next.getLayoutParams();
            if (layoutState.mScrapList != null) {
                if (this.mShouldReverseLayout != (layoutState.mLayoutDirection == -1)) {
                    ((RecyclerView.LayoutManager)this).addDisappearingView(next, 0);
                }
                else {
                    ((RecyclerView.LayoutManager)this).addDisappearingView(next);
                }
            }
            else if (this.mShouldReverseLayout != (layoutState.mLayoutDirection == -1)) {
                ((RecyclerView.LayoutManager)this).addView(next, 0);
            }
            else {
                ((RecyclerView.LayoutManager)this).addView(next);
            }
            ((RecyclerView.LayoutManager)this).measureChildWithMargins(next, 0, 0);
            layoutChunkResult.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(next);
            int n;
            int mOffset;
            int n2;
            int mOffset2;
            if (this.mOrientation != 1) {
                n = ((RecyclerView.LayoutManager)this).getPaddingTop();
                mOffset = n + this.mOrientationHelper.getDecoratedMeasurementInOther(next);
                if (layoutState.mLayoutDirection != -1) {
                    n2 = layoutState.mOffset;
                    mOffset2 = layoutState.mOffset + layoutChunkResult.mConsumed;
                }
                else {
                    mOffset2 = layoutState.mOffset;
                    n2 = layoutState.mOffset - layoutChunkResult.mConsumed;
                }
            }
            else {
                if (!this.isLayoutRTL()) {
                    n2 = ((RecyclerView.LayoutManager)this).getPaddingLeft();
                    mOffset2 = this.mOrientationHelper.getDecoratedMeasurementInOther(next) + n2;
                }
                else {
                    mOffset2 = ((RecyclerView.LayoutManager)this).getWidth() - ((RecyclerView.LayoutManager)this).getPaddingRight();
                    n2 = mOffset2 - this.mOrientationHelper.getDecoratedMeasurementInOther(next);
                }
                if (layoutState.mLayoutDirection != -1) {
                    n = layoutState.mOffset;
                    mOffset = layoutChunkResult.mConsumed + layoutState.mOffset;
                }
                else {
                    mOffset = layoutState.mOffset;
                    n = layoutState.mOffset - layoutChunkResult.mConsumed;
                }
            }
            ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(next, n2, n, mOffset2, mOffset);
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                layoutChunkResult.mIgnoreConsumed = true;
            }
            layoutChunkResult.mFocusable = next.hasFocusable();
            return;
        }
        layoutChunkResult.mFinished = true;
    }
    
    void onAnchorReady(final Recycler recycler, final State state, final AnchorInfo anchorInfo, final int n) {
    }
    
    @Override
    public void onDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        if (this.mRecycleChildrenOnDetach) {
            ((RecyclerView.LayoutManager)this).removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }
    
    @Override
    public View onFocusSearchFailed(View view, int convertFocusDirectionToLayoutDirection, final Recycler recycler, final State state) {
        this.resolveShouldLayoutReverse();
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return null;
        }
        convertFocusDirectionToLayoutDirection = this.convertFocusDirectionToLayoutDirection(convertFocusDirectionToLayoutDirection);
        if (convertFocusDirectionToLayoutDirection == Integer.MIN_VALUE) {
            return null;
        }
        this.ensureLayoutState();
        this.ensureLayoutState();
        this.updateLayoutState(convertFocusDirectionToLayoutDirection, (int)(this.mOrientationHelper.getTotalSpace() * 0.33333334f), false, state);
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
        this.mLayoutState.mRecycle = false;
        this.fill(recycler, this.mLayoutState, state, true);
        if (convertFocusDirectionToLayoutDirection != -1) {
            view = this.findPartiallyOrCompletelyInvisibleChildClosestToEnd(recycler, state);
        }
        else {
            view = this.findPartiallyOrCompletelyInvisibleChildClosestToStart(recycler, state);
        }
        View view2;
        if (convertFocusDirectionToLayoutDirection != -1) {
            view2 = this.getChildClosestToEnd();
        }
        else {
            view2 = this.getChildClosestToStart();
        }
        if (!view2.hasFocusable()) {
            return view;
        }
        if (view != null) {
            return view2;
        }
        return null;
    }
    
    @Override
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            accessibilityEvent.setFromIndex(this.findFirstVisibleItemPosition());
            accessibilityEvent.setToIndex(this.findLastVisibleItemPosition());
        }
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        int n = -1;
        if (this.mPendingSavedState != null || this.mPendingScrollPosition != -1) {
            if (state.getItemCount() == 0) {
                ((RecyclerView.LayoutManager)this).removeAndRecycleAllViews(recycler);
                return;
            }
        }
        if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
        }
        this.ensureLayoutState();
        this.mLayoutState.mRecycle = false;
        this.resolveShouldLayoutReverse();
        final View focusedChild = ((RecyclerView.LayoutManager)this).getFocusedChild();
        if (this.mAnchorInfo.mValid && this.mPendingScrollPosition == -1 && this.mPendingSavedState == null) {
            if (focusedChild != null) {
                if (this.mOrientationHelper.getDecoratedStart(focusedChild) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(focusedChild) <= this.mOrientationHelper.getStartAfterPadding()) {
                    this.mAnchorInfo.assignFromViewAndKeepVisibleRect(focusedChild);
                }
            }
        }
        else {
            this.mAnchorInfo.reset();
            this.mAnchorInfo.mLayoutFromEnd = (this.mShouldReverseLayout ^ this.mStackFromEnd);
            this.updateAnchorInfoForLayout(recycler, state, this.mAnchorInfo);
            this.mAnchorInfo.mValid = true;
        }
        int extraLayoutSpace = this.getExtraLayoutSpace(state);
        int n2;
        if (this.mLayoutState.mLastScrollDelta < 0) {
            n2 = 0;
        }
        else {
            n2 = extraLayoutSpace;
            extraLayoutSpace = 0;
        }
        final int n3 = extraLayoutSpace + this.mOrientationHelper.getStartAfterPadding();
        final int n4 = n2 + this.mOrientationHelper.getEndPadding();
        int n5;
        int n6;
        if (!state.isPreLayout()) {
            n5 = n4;
            n6 = n3;
        }
        else {
            n6 = n3;
            n5 = n4;
            if (this.mPendingScrollPosition != -1) {
                n6 = n3;
                n5 = n4;
                if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                    final View viewByPosition = this.findViewByPosition(this.mPendingScrollPosition);
                    n6 = n3;
                    n5 = n4;
                    if (viewByPosition != null) {
                        int n7;
                        if (!this.mShouldReverseLayout) {
                            n7 = this.mPendingScrollPositionOffset - (this.mOrientationHelper.getDecoratedStart(viewByPosition) - this.mOrientationHelper.getStartAfterPadding());
                        }
                        else {
                            n7 = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(viewByPosition) - this.mPendingScrollPositionOffset;
                        }
                        if (n7 <= 0) {
                            n5 = n4 - n7;
                            n6 = n3;
                        }
                        else {
                            n6 = n3 + n7;
                            n5 = n4;
                        }
                    }
                }
            }
        }
        if (!this.mAnchorInfo.mLayoutFromEnd) {
            if (!this.mShouldReverseLayout) {
                n = 1;
            }
        }
        else if (this.mShouldReverseLayout) {
            n = 1;
        }
        this.onAnchorReady(recycler, state, this.mAnchorInfo, n);
        ((RecyclerView.LayoutManager)this).detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mInfinite = this.resolveIsInfinite();
        this.mLayoutState.mIsPreLayout = state.isPreLayout();
        int mOffset3;
        int mOffset4;
        if (!this.mAnchorInfo.mLayoutFromEnd) {
            this.updateLayoutStateToFillEnd(this.mAnchorInfo);
            this.mLayoutState.mExtra = n5;
            this.fill(recycler, this.mLayoutState, state, false);
            final int mOffset = this.mLayoutState.mOffset;
            final int mCurrentPosition = this.mLayoutState.mCurrentPosition;
            if (this.mLayoutState.mAvailable > 0) {
                n6 += this.mLayoutState.mAvailable;
            }
            this.updateLayoutStateToFillStart(this.mAnchorInfo);
            this.mLayoutState.mExtra = n6;
            final LayoutState mLayoutState = this.mLayoutState;
            mLayoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
            this.fill(recycler, this.mLayoutState, state, false);
            final int mOffset2 = this.mLayoutState.mOffset;
            if (this.mLayoutState.mAvailable <= 0) {
                mOffset3 = mOffset;
                mOffset4 = mOffset2;
            }
            else {
                final int mAvailable = this.mLayoutState.mAvailable;
                this.updateLayoutStateToFillEnd(mCurrentPosition, mOffset);
                this.mLayoutState.mExtra = mAvailable;
                this.fill(recycler, this.mLayoutState, state, false);
                mOffset3 = this.mLayoutState.mOffset;
                mOffset4 = mOffset2;
            }
        }
        else {
            this.updateLayoutStateToFillStart(this.mAnchorInfo);
            this.mLayoutState.mExtra = n6;
            this.fill(recycler, this.mLayoutState, state, false);
            final int mOffset5 = this.mLayoutState.mOffset;
            final int mCurrentPosition2 = this.mLayoutState.mCurrentPosition;
            if (this.mLayoutState.mAvailable > 0) {
                n5 += this.mLayoutState.mAvailable;
            }
            this.updateLayoutStateToFillEnd(this.mAnchorInfo);
            this.mLayoutState.mExtra = n5;
            final LayoutState mLayoutState2 = this.mLayoutState;
            mLayoutState2.mCurrentPosition += this.mLayoutState.mItemDirection;
            this.fill(recycler, this.mLayoutState, state, false);
            final int mOffset6 = this.mLayoutState.mOffset;
            mOffset4 = mOffset5;
            mOffset3 = mOffset6;
            if (this.mLayoutState.mAvailable > 0) {
                final int mAvailable2 = this.mLayoutState.mAvailable;
                this.updateLayoutStateToFillStart(mCurrentPosition2, mOffset5);
                this.mLayoutState.mExtra = mAvailable2;
                this.fill(recycler, this.mLayoutState, state, false);
                mOffset4 = this.mLayoutState.mOffset;
                mOffset3 = mOffset6;
            }
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            if (!(this.mShouldReverseLayout ^ this.mStackFromEnd)) {
                final int fixLayoutStartGap = this.fixLayoutStartGap(mOffset4, recycler, state, true);
                final int n8 = mOffset3 + fixLayoutStartGap;
                final int fixLayoutEndGap = this.fixLayoutEndGap(n8, recycler, state, false);
                mOffset4 = mOffset4 + fixLayoutStartGap + fixLayoutEndGap;
                mOffset3 = n8 + fixLayoutEndGap;
            }
            else {
                final int fixLayoutEndGap2 = this.fixLayoutEndGap(mOffset3, recycler, state, true);
                final int n9 = mOffset4 + fixLayoutEndGap2;
                final int fixLayoutStartGap2 = this.fixLayoutStartGap(n9, recycler, state, false);
                mOffset4 = n9 + fixLayoutStartGap2;
                mOffset3 = mOffset3 + fixLayoutEndGap2 + fixLayoutStartGap2;
            }
        }
        this.layoutForPredictiveAnimations(recycler, state, mOffset4, mOffset3);
        if (state.isPreLayout()) {
            this.mAnchorInfo.reset();
        }
        else {
            this.mOrientationHelper.onLayoutComplete();
        }
        this.mLastStackFromEnd = this.mStackFromEnd;
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        super.onLayoutCompleted(state);
        this.mPendingSavedState = null;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mAnchorInfo.reset();
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mPendingSavedState = (SavedState)parcelable;
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        if (this.mPendingSavedState == null) {
            final SavedState savedState = new SavedState();
            if (((RecyclerView.LayoutManager)this).getChildCount() <= 0) {
                savedState.invalidateAnchor();
            }
            else {
                this.ensureLayoutState();
                if (!(savedState.mAnchorLayoutFromEnd = (this.mLastStackFromEnd ^ this.mShouldReverseLayout))) {
                    final View childClosestToStart = this.getChildClosestToStart();
                    savedState.mAnchorPosition = ((RecyclerView.LayoutManager)this).getPosition(childClosestToStart);
                    savedState.mAnchorOffset = this.mOrientationHelper.getDecoratedStart(childClosestToStart) - this.mOrientationHelper.getStartAfterPadding();
                }
                else {
                    final View childClosestToEnd = this.getChildClosestToEnd();
                    savedState.mAnchorOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(childClosestToEnd);
                    savedState.mAnchorPosition = ((RecyclerView.LayoutManager)this).getPosition(childClosestToEnd);
                }
            }
            return (Parcelable)savedState;
        }
        return (Parcelable)new SavedState(this.mPendingSavedState);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Override
    public void prepareForDrop(final View view, final View view2, int position, int position2) {
        this.assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
        this.ensureLayoutState();
        this.resolveShouldLayoutReverse();
        position = ((RecyclerView.LayoutManager)this).getPosition(view);
        position2 = ((RecyclerView.LayoutManager)this).getPosition(view2);
        if (position >= position2) {
            position = -1;
        }
        else {
            position = 1;
        }
        if (!this.mShouldReverseLayout) {
            if (position != -1) {
                this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getDecoratedEnd(view2) - this.mOrientationHelper.getDecoratedMeasurement(view));
            }
            else {
                this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getDecoratedStart(view2));
            }
        }
        else if (position != 1) {
            this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(view2));
        }
        else {
            this.scrollToPositionWithOffset(position2, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedStart(view2) + this.mOrientationHelper.getDecoratedMeasurement(view)));
        }
    }
    
    boolean resolveIsInfinite() {
        boolean b = false;
        if (this.mOrientationHelper.getMode() == 0 && this.mOrientationHelper.getEnd() == 0) {
            b = true;
        }
        return b;
    }
    
    int scrollBy(int n, final Recycler recycler, final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0 || n == 0) {
            return 0;
        }
        this.mLayoutState.mRecycle = true;
        this.ensureLayoutState();
        int n2;
        if (n <= 0) {
            n2 = -1;
        }
        else {
            n2 = 1;
        }
        final int abs = Math.abs(n);
        this.updateLayoutState(n2, abs, true, state);
        final int n3 = this.mLayoutState.mScrollingOffset + this.fill(recycler, this.mLayoutState, state, false);
        if (n3 >= 0) {
            if (abs > n3) {
                n = n2 * n3;
            }
            this.mOrientationHelper.offsetChildren(-n);
            return this.mLayoutState.mLastScrollDelta = n;
        }
        return 0;
    }
    
    @Override
    public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
        if (this.mOrientation != 1) {
            return this.scrollBy(n, recycler, state);
        }
        return 0;
    }
    
    @Override
    public void scrollToPosition(final int mPendingScrollPosition) {
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchor();
        }
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void scrollToPositionWithOffset(final int mPendingScrollPosition, final int mPendingScrollPositionOffset) {
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = mPendingScrollPositionOffset;
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchor();
        }
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
        if (this.mOrientation != 0) {
            return this.scrollBy(n, recycler, state);
        }
        return 0;
    }
    
    public void setInitialPrefetchItemCount(final int mInitialPrefetchItemCount) {
        this.mInitialPrefetchItemCount = mInitialPrefetchItemCount;
    }
    
    public void setOrientation(final int n) {
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("invalid orientation:" + n);
        }
        this.assertNotInLayoutOrScroll(null);
        if (n != this.mOrientation) {
            this.mOrientation = n;
            this.mOrientationHelper = null;
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    public void setRecycleChildrenOnDetach(final boolean mRecycleChildrenOnDetach) {
        this.mRecycleChildrenOnDetach = mRecycleChildrenOnDetach;
    }
    
    public void setReverseLayout(final boolean mReverseLayout) {
        this.assertNotInLayoutOrScroll(null);
        if (mReverseLayout != this.mReverseLayout) {
            this.mReverseLayout = mReverseLayout;
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    public void setSmoothScrollbarEnabled(final boolean mSmoothScrollbarEnabled) {
        this.mSmoothScrollbarEnabled = mSmoothScrollbarEnabled;
    }
    
    public void setStackFromEnd(final boolean mStackFromEnd) {
        this.assertNotInLayoutOrScroll(null);
        if (this.mStackFromEnd != mStackFromEnd) {
            this.mStackFromEnd = mStackFromEnd;
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    @Override
    boolean shouldMeasureTwice() {
        final boolean b = false;
        boolean b2;
        if (((RecyclerView.LayoutManager)this).getHeightMode() == 1073741824) {
            b2 = b;
        }
        else {
            b2 = b;
            if (((RecyclerView.LayoutManager)this).getWidthMode() != 1073741824) {
                b2 = b;
                if (((RecyclerView.LayoutManager)this).hasFlexibleChildInBothOrientations()) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int targetPosition) {
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        ((RecyclerView.SmoothScroller)linearSmoothScroller).setTargetPosition(targetPosition);
        ((RecyclerView.LayoutManager)this).startSmoothScroll(linearSmoothScroller);
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && this.mLastStackFromEnd == this.mStackFromEnd;
    }
    
    void validateChildOrder() {
        boolean b = true;
        final boolean b2 = false;
        Log.d("LinearLayoutManager", "validating child count " + ((RecyclerView.LayoutManager)this).getChildCount());
        if (((RecyclerView.LayoutManager)this).getChildCount() >= 1) {
            final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
            final int decoratedStart = this.mOrientationHelper.getDecoratedStart(((RecyclerView.LayoutManager)this).getChildAt(0));
            if (!this.mShouldReverseLayout) {
                for (int i = 1; i < ((RecyclerView.LayoutManager)this).getChildCount(); ++i) {
                    final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
                    final int position2 = ((RecyclerView.LayoutManager)this).getPosition(child);
                    final int decoratedStart2 = this.mOrientationHelper.getDecoratedStart(child);
                    if (position2 < position) {
                        this.logChildren();
                        throw new RuntimeException("detected invalid position. loc invalid? " + (decoratedStart2 < decoratedStart || b2));
                    }
                    if (decoratedStart2 < decoratedStart) {
                        this.logChildren();
                        throw new RuntimeException("detected invalid location");
                    }
                }
            }
            else {
                for (int j = 1; j < ((RecyclerView.LayoutManager)this).getChildCount(); ++j) {
                    final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(j);
                    final int position3 = ((RecyclerView.LayoutManager)this).getPosition(child2);
                    final int decoratedStart3 = this.mOrientationHelper.getDecoratedStart(child2);
                    if (position3 < position) {
                        this.logChildren();
                        final StringBuilder append = new StringBuilder().append("detected invalid position. loc invalid? ");
                        if (decoratedStart3 >= decoratedStart) {
                            b = false;
                        }
                        throw new RuntimeException(append.append(b).toString());
                    }
                    if (decoratedStart3 > decoratedStart) {
                        this.logChildren();
                        throw new RuntimeException("detected invalid location");
                    }
                }
            }
        }
    }
    
    class AnchorInfo
    {
        int mCoordinate;
        boolean mLayoutFromEnd;
        int mPosition;
        boolean mValid;
        
        AnchorInfo() {
            this.reset();
        }
        
        void assignCoordinateFromPadding() {
            int mCoordinate;
            if (!this.mLayoutFromEnd) {
                mCoordinate = LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
            }
            else {
                mCoordinate = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding();
            }
            this.mCoordinate = mCoordinate;
        }
        
        public void assignFromView(final View view) {
            if (!this.mLayoutFromEnd) {
                this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(view);
            }
            else {
                this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(view) + LinearLayoutManager.this.mOrientationHelper.getTotalSpaceChange();
            }
            this.mPosition = ((RecyclerView.LayoutManager)LinearLayoutManager.this).getPosition(view);
        }
        
        public void assignFromViewAndKeepVisibleRect(final View view) {
            final int totalSpaceChange = LinearLayoutManager.this.mOrientationHelper.getTotalSpaceChange();
            if (totalSpaceChange < 0) {
                this.mPosition = ((RecyclerView.LayoutManager)LinearLayoutManager.this).getPosition(view);
                if (!this.mLayoutFromEnd) {
                    final int decoratedStart = LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(view);
                    final int a = decoratedStart - LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
                    this.mCoordinate = decoratedStart;
                    if (a > 0) {
                        final int n = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - Math.min(0, LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - totalSpaceChange - LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(view)) - (decoratedStart + LinearLayoutManager.this.mOrientationHelper.getDecoratedMeasurement(view));
                        if (n < 0) {
                            this.mCoordinate -= Math.min(a, -n);
                        }
                    }
                }
                else {
                    final int a2 = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - totalSpaceChange - LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(view);
                    this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - a2;
                    if (a2 > 0) {
                        final int decoratedMeasurement = LinearLayoutManager.this.mOrientationHelper.getDecoratedMeasurement(view);
                        final int mCoordinate = this.mCoordinate;
                        final int startAfterPadding = LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
                        final int n2 = mCoordinate - decoratedMeasurement - (startAfterPadding + Math.min(LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(view) - startAfterPadding, 0));
                        if (n2 < 0) {
                            this.mCoordinate += Math.min(a2, -n2);
                        }
                    }
                }
                return;
            }
            this.assignFromView(view);
        }
        
        boolean isViewValidAsAnchor(final View view, final State state) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            return !layoutParams.isItemRemoved() && layoutParams.getViewLayoutPosition() >= 0 && layoutParams.getViewLayoutPosition() < state.getItemCount();
        }
        
        void reset() {
            this.mPosition = -1;
            this.mCoordinate = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mValid = false;
        }
        
        @Override
        public String toString() {
            return "AnchorInfo{mPosition=" + this.mPosition + ", mCoordinate=" + this.mCoordinate + ", mLayoutFromEnd=" + this.mLayoutFromEnd + ", mValid=" + this.mValid + '}';
        }
    }
    
    protected static class LayoutChunkResult
    {
        public int mConsumed;
        public boolean mFinished;
        public boolean mFocusable;
        public boolean mIgnoreConsumed;
        
        void resetInternal() {
            this.mConsumed = 0;
            this.mFinished = false;
            this.mIgnoreConsumed = false;
            this.mFocusable = false;
        }
    }
    
    static class LayoutState
    {
        static final int INVALID_LAYOUT = Integer.MIN_VALUE;
        static final int ITEM_DIRECTION_HEAD = -1;
        static final int ITEM_DIRECTION_TAIL = 1;
        static final int LAYOUT_END = 1;
        static final int LAYOUT_START = -1;
        static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;
        static final String TAG = "LLM#LayoutState";
        int mAvailable;
        int mCurrentPosition;
        int mExtra;
        boolean mInfinite;
        boolean mIsPreLayout;
        int mItemDirection;
        int mLastScrollDelta;
        int mLayoutDirection;
        int mOffset;
        boolean mRecycle;
        List<ViewHolder> mScrapList;
        int mScrollingOffset;
        
        LayoutState() {
            this.mRecycle = true;
            this.mExtra = 0;
            this.mIsPreLayout = false;
            this.mScrapList = null;
        }
        
        private View nextViewFromScrapList() {
            for (int size = this.mScrapList.size(), i = 0; i < size; ++i) {
                final View itemView = this.mScrapList.get(i).itemView;
                final LayoutParams layoutParams = (LayoutParams)itemView.getLayoutParams();
                if (!layoutParams.isItemRemoved() && this.mCurrentPosition == layoutParams.getViewLayoutPosition()) {
                    this.assignPositionFromScrapList(itemView);
                    return itemView;
                }
            }
            return null;
        }
        
        public void assignPositionFromScrapList() {
            this.assignPositionFromScrapList(null);
        }
        
        public void assignPositionFromScrapList(View nextViewInLimitedList) {
            nextViewInLimitedList = this.nextViewInLimitedList(nextViewInLimitedList);
            if (nextViewInLimitedList != null) {
                this.mCurrentPosition = ((LayoutParams)nextViewInLimitedList.getLayoutParams()).getViewLayoutPosition();
            }
            else {
                this.mCurrentPosition = -1;
            }
        }
        
        boolean hasMore(final State state) {
            boolean b = false;
            if (this.mCurrentPosition >= 0 && this.mCurrentPosition < state.getItemCount()) {
                b = true;
            }
            return b;
        }
        
        void log() {
            Log.d("LLM#LayoutState", "avail:" + this.mAvailable + ", ind:" + this.mCurrentPosition + ", dir:" + this.mItemDirection + ", offset:" + this.mOffset + ", layoutDir:" + this.mLayoutDirection);
        }
        
        View next(final Recycler recycler) {
            if (this.mScrapList == null) {
                final View viewForPosition = recycler.getViewForPosition(this.mCurrentPosition);
                this.mCurrentPosition += this.mItemDirection;
                return viewForPosition;
            }
            return this.nextViewFromScrapList();
        }
        
        public View nextViewInLimitedList(final View view) {
            final int size = this.mScrapList.size();
            View view2 = null;
            int n = Integer.MAX_VALUE;
            for (int i = 0; i < size; ++i) {
                final View itemView = this.mScrapList.get(i).itemView;
                final LayoutParams layoutParams = (LayoutParams)itemView.getLayoutParams();
                if (itemView != view) {
                    if (!layoutParams.isItemRemoved()) {
                        final int n2 = (layoutParams.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
                        if (n2 >= 0) {
                            if (n2 < n) {
                                if (n2 == 0) {
                                    view2 = itemView;
                                    break;
                                }
                                view2 = itemView;
                                n = n2;
                            }
                        }
                    }
                }
            }
            return view2;
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean mAnchorLayoutFromEnd;
        int mAnchorOffset;
        int mAnchorPosition;
        
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
        
        public SavedState() {
        }
        
        SavedState(final Parcel parcel) {
            boolean mAnchorLayoutFromEnd = true;
            this.mAnchorPosition = parcel.readInt();
            this.mAnchorOffset = parcel.readInt();
            if (parcel.readInt() != 1) {
                mAnchorLayoutFromEnd = false;
            }
            this.mAnchorLayoutFromEnd = mAnchorLayoutFromEnd;
        }
        
        public SavedState(final SavedState savedState) {
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mAnchorOffset = savedState.mAnchorOffset;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
        }
        
        public int describeContents() {
            return 0;
        }
        
        boolean hasValidAnchor() {
            boolean b = false;
            if (this.mAnchorPosition >= 0) {
                b = true;
            }
            return b;
        }
        
        void invalidateAnchor() {
            this.mAnchorPosition = -1;
        }
        
        public void writeToParcel(final Parcel parcel, int n) {
            n = 0;
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mAnchorOffset);
            if (this.mAnchorLayoutFromEnd) {
                n = 1;
            }
            parcel.writeInt(n);
        }
    }
}
