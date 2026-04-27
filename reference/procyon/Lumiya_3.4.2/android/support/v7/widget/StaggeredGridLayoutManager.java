// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.annotation.RestrictTo;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcelable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.support.annotation.Nullable;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.graphics.PointF;
import java.util.Arrays;
import android.view.View$MeasureSpec;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import java.util.BitSet;
import android.support.annotation.NonNull;

public class StaggeredGridLayoutManager extends LayoutManager implements ScrollVectorProvider
{
    static final boolean DEBUG = false;
    @Deprecated
    public static final int GAP_HANDLING_LAZY = 1;
    public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
    public static final int GAP_HANDLING_NONE = 0;
    public static final int HORIZONTAL = 0;
    static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    private static final String TAG = "StaggeredGridLManager";
    public static final int VERTICAL = 1;
    private final AnchorInfo mAnchorInfo;
    private final Runnable mCheckForGapsRunnable;
    private int mFullSizeSpec;
    private int mGapStrategy;
    private boolean mLaidOutInvalidFullSpan;
    private boolean mLastLayoutFromEnd;
    private boolean mLastLayoutRTL;
    @NonNull
    private final android.support.v7.widget.LayoutState mLayoutState;
    LazySpanLookup mLazySpanLookup;
    private int mOrientation;
    private SavedState mPendingSavedState;
    int mPendingScrollPosition;
    int mPendingScrollPositionOffset;
    private int[] mPrefetchDistances;
    @NonNull
    OrientationHelper mPrimaryOrientation;
    private BitSet mRemainingSpans;
    boolean mReverseLayout;
    @NonNull
    OrientationHelper mSecondaryOrientation;
    boolean mShouldReverseLayout;
    private int mSizePerSpan;
    private boolean mSmoothScrollbarEnabled;
    private int mSpanCount;
    Span[] mSpans;
    private final Rect mTmpRect;
    
    public StaggeredGridLayoutManager(final int spanCount, final int mOrientation) {
        boolean autoMeasureEnabled = false;
        this.mSpanCount = -1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mLazySpanLookup = new LazySpanLookup();
        this.mGapStrategy = 2;
        this.mTmpRect = new Rect();
        this.mAnchorInfo = new AnchorInfo();
        this.mLaidOutInvalidFullSpan = false;
        this.mSmoothScrollbarEnabled = true;
        this.mCheckForGapsRunnable = new Runnable() {
            @Override
            public void run() {
                StaggeredGridLayoutManager.this.checkForGaps();
            }
        };
        this.mOrientation = mOrientation;
        this.setSpanCount(spanCount);
        if (this.mGapStrategy != 0) {
            autoMeasureEnabled = true;
        }
        ((RecyclerView.LayoutManager)this).setAutoMeasureEnabled(autoMeasureEnabled);
        this.mLayoutState = new android.support.v7.widget.LayoutState();
        this.createOrientationHelpers();
    }
    
    public StaggeredGridLayoutManager(final Context context, final AttributeSet set, final int n, final int n2) {
        boolean autoMeasureEnabled = false;
        this.mSpanCount = -1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mLazySpanLookup = new LazySpanLookup();
        this.mGapStrategy = 2;
        this.mTmpRect = new Rect();
        this.mAnchorInfo = new AnchorInfo();
        this.mLaidOutInvalidFullSpan = false;
        this.mSmoothScrollbarEnabled = true;
        this.mCheckForGapsRunnable = new Runnable() {
            @Override
            public void run() {
                StaggeredGridLayoutManager.this.checkForGaps();
            }
        };
        final Properties properties = RecyclerView.LayoutManager.getProperties(context, set, n, n2);
        this.setOrientation(properties.orientation);
        this.setSpanCount(properties.spanCount);
        this.setReverseLayout(properties.reverseLayout);
        if (this.mGapStrategy != 0) {
            autoMeasureEnabled = true;
        }
        ((RecyclerView.LayoutManager)this).setAutoMeasureEnabled(autoMeasureEnabled);
        this.mLayoutState = new android.support.v7.widget.LayoutState();
        this.createOrientationHelpers();
    }
    
    private void appendViewToAllSpans(final View view) {
        int mSpanCount = this.mSpanCount;
        while (--mSpanCount >= 0) {
            this.mSpans[mSpanCount].appendToSpan(view);
        }
    }
    
    private void applyPendingSavedState(final AnchorInfo anchorInfo) {
        int i = 0;
        if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
            if (this.mPendingSavedState.mSpanOffsetsSize != this.mSpanCount) {
                this.mPendingSavedState.invalidateSpanInfo();
                this.mPendingSavedState.mAnchorPosition = this.mPendingSavedState.mVisibleAnchorPosition;
            }
            else {
                while (i < this.mSpanCount) {
                    this.mSpans[i].clear();
                    int line = this.mPendingSavedState.mSpanOffsets[i];
                    if (line != Integer.MIN_VALUE) {
                        if (!this.mPendingSavedState.mAnchorLayoutFromEnd) {
                            line += this.mPrimaryOrientation.getStartAfterPadding();
                        }
                        else {
                            line += this.mPrimaryOrientation.getEndAfterPadding();
                        }
                    }
                    this.mSpans[i].setLine(line);
                    ++i;
                }
            }
        }
        this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
        this.setReverseLayout(this.mPendingSavedState.mReverseLayout);
        this.resolveShouldLayoutReverse();
        if (this.mPendingSavedState.mAnchorPosition == -1) {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
        }
        else {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
            anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
        }
        if (this.mPendingSavedState.mSpanLookupSize > 1) {
            this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
            this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
        }
    }
    
    private void attachViewToSpans(final View view, final LayoutParams layoutParams, final android.support.v7.widget.LayoutState layoutState) {
        if (layoutState.mLayoutDirection != 1) {
            if (!layoutParams.mFullSpan) {
                layoutParams.mSpan.prependToSpan(view);
            }
            else {
                this.prependViewToAllSpans(view);
            }
        }
        else if (!layoutParams.mFullSpan) {
            layoutParams.mSpan.appendToSpan(view);
        }
        else {
            this.appendViewToAllSpans(view);
        }
    }
    
    private int calculateScrollDirectionForPosition(int n) {
        final int n2 = -1;
        final int n3 = 1;
        boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            if (n < this.getFirstChildPosition()) {
                b = true;
            }
            if (b == this.mShouldReverseLayout) {
                n = n3;
            }
            else {
                n = -1;
            }
            return n;
        }
        if (!this.mShouldReverseLayout) {
            n = n2;
        }
        else {
            n = 1;
        }
        return n;
    }
    
    private boolean checkSpanForGap(final Span span) {
        boolean b = false;
        if (!this.mShouldReverseLayout) {
            if (span.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
                if (!span.getLayoutParams(span.mViews.get(0)).mFullSpan) {
                    b = true;
                }
                return b;
            }
        }
        else if (span.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
            return !span.getLayoutParams(span.mViews.get(span.mViews.size() - 1)).mFullSpan;
        }
        return false;
    }
    
    private int computeScrollExtent(final State state) {
        final boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            return ScrollbarHelper.computeScrollExtent(state, this.mPrimaryOrientation, this.findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled), this.findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled || b), this, this.mSmoothScrollbarEnabled);
        }
        return 0;
    }
    
    private int computeScrollOffset(final State state) {
        final boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            return ScrollbarHelper.computeScrollOffset(state, this.mPrimaryOrientation, this.findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled), this.findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled || b), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
        }
        return 0;
    }
    
    private int computeScrollRange(final State state) {
        final boolean b = false;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            return ScrollbarHelper.computeScrollRange(state, this.mPrimaryOrientation, this.findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled), this.findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled || b), this, this.mSmoothScrollbarEnabled);
        }
        return 0;
    }
    
    private int convertFocusDirectionToLayoutDirection(final int n) {
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
    
    private FullSpanItem createFullSpanItemFromEnd(final int n) {
        final FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; ++i) {
            fullSpanItem.mGapPerSpan[i] = n - this.mSpans[i].getEndLine(n);
        }
        return fullSpanItem;
    }
    
    private FullSpanItem createFullSpanItemFromStart(final int n) {
        final FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; ++i) {
            fullSpanItem.mGapPerSpan[i] = this.mSpans[i].getStartLine(n) - n;
        }
        return fullSpanItem;
    }
    
    private void createOrientationHelpers() {
        this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
    }
    
    private int fill(final Recycler recycler, final android.support.v7.widget.LayoutState layoutState, final State state) {
        this.mRemainingSpans.set(0, this.mSpanCount, true);
        int n;
        if (!this.mLayoutState.mInfinite) {
            if (layoutState.mLayoutDirection != 1) {
                n = layoutState.mStartLine - layoutState.mAvailable;
            }
            else {
                n = layoutState.mEndLine + layoutState.mAvailable;
            }
        }
        else if (layoutState.mLayoutDirection != 1) {
            n = Integer.MIN_VALUE;
        }
        else {
            n = Integer.MAX_VALUE;
        }
        this.updateAllRemainingSpans(layoutState.mLayoutDirection, n);
        int n2;
        if (!this.mShouldReverseLayout) {
            n2 = this.mPrimaryOrientation.getStartAfterPadding();
        }
        else {
            n2 = this.mPrimaryOrientation.getEndAfterPadding();
        }
        int n3 = 0;
        while (layoutState.hasMore(state) && (this.mLayoutState.mInfinite || !this.mRemainingSpans.isEmpty())) {
            final View next = layoutState.next(recycler);
            final LayoutParams layoutParams = (LayoutParams)next.getLayoutParams();
            final int viewLayoutPosition = ((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition();
            final int span = this.mLazySpanLookup.getSpan(viewLayoutPosition);
            int n4;
            if (span != -1) {
                n4 = 0;
            }
            else {
                n4 = 1;
            }
            Span nextSpan;
            if (n4 == 0) {
                nextSpan = this.mSpans[span];
            }
            else {
                if (!layoutParams.mFullSpan) {
                    nextSpan = this.getNextSpan(layoutState);
                }
                else {
                    nextSpan = this.mSpans[0];
                }
                this.mLazySpanLookup.setSpan(viewLayoutPosition, nextSpan);
            }
            layoutParams.mSpan = nextSpan;
            if (layoutState.mLayoutDirection != 1) {
                ((RecyclerView.LayoutManager)this).addView(next, 0);
            }
            else {
                ((RecyclerView.LayoutManager)this).addView(next);
            }
            this.measureChildWithDecorationsAndMargin(next, layoutParams, false);
            int n6;
            int n7;
            if (layoutState.mLayoutDirection != 1) {
                int n5;
                if (!layoutParams.mFullSpan) {
                    n5 = nextSpan.getStartLine(n2);
                }
                else {
                    n5 = this.getMinStart(n2);
                }
                n6 = n5 - this.mPrimaryOrientation.getDecoratedMeasurement(next);
                if (n4 != 0 && layoutParams.mFullSpan) {
                    final FullSpanItem fullSpanItemFromStart = this.createFullSpanItemFromStart(n5);
                    fullSpanItemFromStart.mGapDir = 1;
                    fullSpanItemFromStart.mPosition = viewLayoutPosition;
                    this.mLazySpanLookup.addFullSpanItem(fullSpanItemFromStart);
                    n7 = n5;
                }
                else {
                    n7 = n5;
                }
            }
            else {
                int n8;
                if (!layoutParams.mFullSpan) {
                    n8 = nextSpan.getEndLine(n2);
                }
                else {
                    n8 = this.getMaxEnd(n2);
                }
                n7 = n8 + this.mPrimaryOrientation.getDecoratedMeasurement(next);
                if (n4 != 0 && layoutParams.mFullSpan) {
                    final FullSpanItem fullSpanItemFromEnd = this.createFullSpanItemFromEnd(n8);
                    fullSpanItemFromEnd.mGapDir = -1;
                    fullSpanItemFromEnd.mPosition = viewLayoutPosition;
                    this.mLazySpanLookup.addFullSpanItem(fullSpanItemFromEnd);
                    n6 = n8;
                }
                else {
                    n6 = n8;
                }
            }
            if (layoutParams.mFullSpan && layoutState.mItemDirection == -1) {
                if (n4 == 0) {
                    int n9;
                    if (layoutState.mLayoutDirection != 1) {
                        if (this.areAllStartsEqual()) {
                            n9 = 0;
                        }
                        else {
                            n9 = 1;
                        }
                    }
                    else if (this.areAllEndsEqual()) {
                        n9 = 0;
                    }
                    else {
                        n9 = 1;
                    }
                    if (n9 != 0) {
                        final FullSpanItem fullSpanItem = this.mLazySpanLookup.getFullSpanItem(viewLayoutPosition);
                        if (fullSpanItem != null) {
                            fullSpanItem.mHasUnwantedGapAfter = true;
                        }
                        this.mLaidOutInvalidFullSpan = true;
                    }
                }
                else {
                    this.mLaidOutInvalidFullSpan = true;
                }
            }
            this.attachViewToSpans(next, layoutParams, layoutState);
            int endAfterPadding;
            int n10;
            if (this.isLayoutRTL() && this.mOrientation == 1) {
                if (!layoutParams.mFullSpan) {
                    endAfterPadding = this.mSecondaryOrientation.getEndAfterPadding() - (this.mSpanCount - 1 - nextSpan.mIndex) * this.mSizePerSpan;
                }
                else {
                    endAfterPadding = this.mSecondaryOrientation.getEndAfterPadding();
                }
                n10 = endAfterPadding - this.mSecondaryOrientation.getDecoratedMeasurement(next);
            }
            else {
                int startAfterPadding;
                if (!layoutParams.mFullSpan) {
                    startAfterPadding = nextSpan.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding();
                }
                else {
                    startAfterPadding = this.mSecondaryOrientation.getStartAfterPadding();
                }
                final int n11 = startAfterPadding + this.mSecondaryOrientation.getDecoratedMeasurement(next);
                n10 = startAfterPadding;
                endAfterPadding = n11;
            }
            if (this.mOrientation != 1) {
                ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(next, n6, n10, n7, endAfterPadding);
            }
            else {
                ((RecyclerView.LayoutManager)this).layoutDecoratedWithMargins(next, n10, n6, endAfterPadding, n7);
            }
            if (!layoutParams.mFullSpan) {
                this.updateRemainingSpans(nextSpan, this.mLayoutState.mLayoutDirection, n);
            }
            else {
                this.updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, n);
            }
            this.recycle(recycler, this.mLayoutState);
            if (this.mLayoutState.mStopInFocusable && next.hasFocusable()) {
                if (!layoutParams.mFullSpan) {
                    this.mRemainingSpans.set(nextSpan.mIndex, false);
                }
                else {
                    this.mRemainingSpans.clear();
                }
            }
            n3 = 1;
        }
        if (n3 == 0) {
            this.recycle(recycler, this.mLayoutState);
        }
        int b;
        if (this.mLayoutState.mLayoutDirection != -1) {
            b = this.getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding();
        }
        else {
            b = this.mPrimaryOrientation.getStartAfterPadding() - this.getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
        }
        int min;
        if (b <= 0) {
            min = 0;
        }
        else {
            min = Math.min(layoutState.mAvailable, b);
        }
        return min;
    }
    
    private int findFirstReferenceChildPosition(final int n) {
        for (int childCount = ((RecyclerView.LayoutManager)this).getChildCount(), i = 0; i < childCount; ++i) {
            final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(i));
            if (position >= 0 && position < n) {
                return position;
            }
        }
        return 0;
    }
    
    private int findLastReferenceChildPosition(final int n) {
        int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        while (true) {
            final int n2 = childCount - 1;
            if (n2 < 0) {
                return 0;
            }
            final int position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(n2));
            childCount = n2;
            if (position < 0) {
                continue;
            }
            childCount = n2;
            if (position < n) {
                return position;
            }
        }
    }
    
    private void fixEndGap(final Recycler recycler, final State state, final boolean b) {
        final int maxEnd = this.getMaxEnd(Integer.MIN_VALUE);
        if (maxEnd == Integer.MIN_VALUE) {
            return;
        }
        final int n = this.mPrimaryOrientation.getEndAfterPadding() - maxEnd;
        if (n <= 0) {
            return;
        }
        final int n2 = n - -this.scrollBy(-n, recycler, state);
        if (b && n2 > 0) {
            this.mPrimaryOrientation.offsetChildren(n2);
        }
    }
    
    private void fixStartGap(final Recycler recycler, final State state, final boolean b) {
        final int minStart = this.getMinStart(Integer.MAX_VALUE);
        if (minStart == Integer.MAX_VALUE) {
            return;
        }
        final int n = minStart - this.mPrimaryOrientation.getStartAfterPadding();
        if (n <= 0) {
            return;
        }
        final int n2 = n - this.scrollBy(n, recycler, state);
        if (b && n2 > 0) {
            this.mPrimaryOrientation.offsetChildren(-n2);
        }
    }
    
    private int getMaxEnd(final int n) {
        int endLine = this.mSpans[0].getEndLine(n);
        for (int i = 1; i < this.mSpanCount; ++i) {
            final int endLine2 = this.mSpans[i].getEndLine(n);
            if (endLine2 > endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }
    
    private int getMaxStart(final int n) {
        int startLine = this.mSpans[0].getStartLine(n);
        for (int i = 1; i < this.mSpanCount; ++i) {
            final int startLine2 = this.mSpans[i].getStartLine(n);
            if (startLine2 > startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }
    
    private int getMinEnd(final int n) {
        int endLine = this.mSpans[0].getEndLine(n);
        for (int i = 1; i < this.mSpanCount; ++i) {
            final int endLine2 = this.mSpans[i].getEndLine(n);
            if (endLine2 < endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }
    
    private int getMinStart(final int n) {
        int startLine = this.mSpans[0].getStartLine(n);
        for (int i = 1; i < this.mSpanCount; ++i) {
            final int startLine2 = this.mSpans[i].getStartLine(n);
            if (startLine2 < startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }
    
    private Span getNextSpan(final android.support.v7.widget.LayoutState layoutState) {
        final Span span = null;
        final Span span2 = null;
        int n = -1;
        int mSpanCount;
        int i;
        if (!this.preferLastSpan(layoutState.mLayoutDirection)) {
            mSpanCount = this.mSpanCount;
            i = 0;
            n = 1;
        }
        else {
            i = this.mSpanCount - 1;
            mSpanCount = -1;
        }
        if (layoutState.mLayoutDirection != 1) {
            int n2 = Integer.MIN_VALUE;
            final int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
            Span span3 = span2;
            while (i != mSpanCount) {
                final Span span4 = this.mSpans[i];
                final int startLine = span4.getStartLine(endAfterPadding);
                if (startLine > n2) {
                    n2 = startLine;
                    span3 = span4;
                }
                i += n;
            }
            return span3;
        }
        int n3 = Integer.MAX_VALUE;
        final int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        Span span5 = span;
        while (i != mSpanCount) {
            final Span span6 = this.mSpans[i];
            final int endLine = span6.getEndLine(startAfterPadding);
            if (endLine < n3) {
                n3 = endLine;
                span5 = span6;
            }
            i += n;
        }
        return span5;
    }
    
    private void handleUpdate(int n, final int n2, final int n3) {
        int n4;
        if (!this.mShouldReverseLayout) {
            n4 = this.getFirstChildPosition();
        }
        else {
            n4 = this.getLastChildPosition();
        }
        int n5;
        int n6;
        if (n3 != 8) {
            n5 = n + n2;
            n6 = n;
        }
        else if (n >= n2) {
            n5 = n + 1;
            n6 = n2;
        }
        else {
            n5 = n2 + 1;
            n6 = n;
        }
        this.mLazySpanLookup.invalidateAfter(n6);
        switch (n3) {
            case 1: {
                this.mLazySpanLookup.offsetForAddition(n, n2);
                break;
            }
            case 2: {
                this.mLazySpanLookup.offsetForRemoval(n, n2);
                break;
            }
            case 8: {
                this.mLazySpanLookup.offsetForRemoval(n, 1);
                this.mLazySpanLookup.offsetForAddition(n2, 1);
                break;
            }
        }
        if (n5 > n4) {
            if (!this.mShouldReverseLayout) {
                n = this.getLastChildPosition();
            }
            else {
                n = this.getFirstChildPosition();
            }
            if (n6 <= n) {
                ((RecyclerView.LayoutManager)this).requestLayout();
            }
        }
    }
    
    private void measureChildWithDecorationsAndMargin(final View view, int updateSpecWithExtra, int updateSpecWithExtra2, final boolean b) {
        ((RecyclerView.LayoutManager)this).calculateItemDecorationsForChild(view, this.mTmpRect);
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        updateSpecWithExtra = this.updateSpecWithExtra(updateSpecWithExtra, layoutParams.leftMargin + this.mTmpRect.left, layoutParams.rightMargin + this.mTmpRect.right);
        updateSpecWithExtra2 = this.updateSpecWithExtra(updateSpecWithExtra2, layoutParams.topMargin + this.mTmpRect.top, layoutParams.bottomMargin + this.mTmpRect.bottom);
        boolean b2;
        if (!b) {
            b2 = ((RecyclerView.LayoutManager)this).shouldMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams);
        }
        else {
            b2 = ((RecyclerView.LayoutManager)this).shouldReMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams);
        }
        if (b2) {
            view.measure(updateSpecWithExtra, updateSpecWithExtra2);
        }
    }
    
    private void measureChildWithDecorationsAndMargin(final View view, final LayoutParams layoutParams, final boolean b) {
        if (!layoutParams.mFullSpan) {
            if (this.mOrientation != 1) {
                this.measureChildWithDecorationsAndMargin(view, RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getWidth(), ((RecyclerView.LayoutManager)this).getWidthMode(), 0, layoutParams.width, true), RecyclerView.LayoutManager.getChildMeasureSpec(this.mSizePerSpan, ((RecyclerView.LayoutManager)this).getHeightMode(), 0, layoutParams.height, false), b);
            }
            else {
                this.measureChildWithDecorationsAndMargin(view, RecyclerView.LayoutManager.getChildMeasureSpec(this.mSizePerSpan, ((RecyclerView.LayoutManager)this).getWidthMode(), 0, layoutParams.width, false), RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getHeight(), ((RecyclerView.LayoutManager)this).getHeightMode(), 0, layoutParams.height, true), b);
            }
        }
        else if (this.mOrientation != 1) {
            this.measureChildWithDecorationsAndMargin(view, RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getWidth(), ((RecyclerView.LayoutManager)this).getWidthMode(), 0, layoutParams.width, true), this.mFullSizeSpec, b);
        }
        else {
            this.measureChildWithDecorationsAndMargin(view, this.mFullSizeSpec, RecyclerView.LayoutManager.getChildMeasureSpec(((RecyclerView.LayoutManager)this).getHeight(), ((RecyclerView.LayoutManager)this).getHeightMode(), 0, layoutParams.height, true), b);
        }
    }
    
    private void onLayoutChildren(final Recycler recycler, final State state, final boolean b) {
        final int n = 1;
        final AnchorInfo mAnchorInfo = this.mAnchorInfo;
        if (this.mPendingSavedState != null || this.mPendingScrollPosition != -1) {
            if (state.getItemCount() == 0) {
                ((RecyclerView.LayoutManager)this).removeAndRecycleAllViews(recycler);
                mAnchorInfo.reset();
                return;
            }
        }
        boolean b2;
        if (mAnchorInfo.mValid && this.mPendingScrollPosition == -1 && this.mPendingSavedState == null) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (b2) {
            mAnchorInfo.reset();
            if (this.mPendingSavedState == null) {
                this.resolveShouldLayoutReverse();
                mAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
            }
            else {
                this.applyPendingSavedState(mAnchorInfo);
            }
            this.updateAnchorInfoForLayout(state, mAnchorInfo);
            mAnchorInfo.mValid = true;
        }
        if (this.mPendingSavedState == null && this.mPendingScrollPosition == -1) {
            if (mAnchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd || this.isLayoutRTL() != this.mLastLayoutRTL) {
                this.mLazySpanLookup.clear();
                mAnchorInfo.mInvalidateOffsets = true;
            }
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            if (this.mPendingSavedState == null || this.mPendingSavedState.mSpanOffsetsSize < 1) {
                if (!mAnchorInfo.mInvalidateOffsets) {
                    if (!b2 && this.mAnchorInfo.mSpanReferenceLines != null) {
                        for (int i = 0; i < this.mSpanCount; ++i) {
                            final Span span = this.mSpans[i];
                            span.clear();
                            span.setLine(this.mAnchorInfo.mSpanReferenceLines[i]);
                        }
                    }
                    else {
                        for (int j = 0; j < this.mSpanCount; ++j) {
                            this.mSpans[j].cacheReferenceLineAndClear(this.mShouldReverseLayout, mAnchorInfo.mOffset);
                        }
                        this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
                    }
                }
                else {
                    for (int k = 0; k < this.mSpanCount; ++k) {
                        this.mSpans[k].clear();
                        if (mAnchorInfo.mOffset != Integer.MIN_VALUE) {
                            this.mSpans[k].setLine(mAnchorInfo.mOffset);
                        }
                    }
                }
            }
        }
        ((RecyclerView.LayoutManager)this).detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mRecycle = false;
        this.mLaidOutInvalidFullSpan = false;
        this.updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
        this.updateLayoutState(mAnchorInfo.mPosition, state);
        if (!mAnchorInfo.mLayoutFromEnd) {
            this.setLayoutStateDirection(1);
            this.fill(recycler, this.mLayoutState, state);
            this.setLayoutStateDirection(-1);
            this.mLayoutState.mCurrentPosition = mAnchorInfo.mPosition + this.mLayoutState.mItemDirection;
            this.fill(recycler, this.mLayoutState, state);
        }
        else {
            this.setLayoutStateDirection(-1);
            this.fill(recycler, this.mLayoutState, state);
            this.setLayoutStateDirection(1);
            this.mLayoutState.mCurrentPosition = mAnchorInfo.mPosition + this.mLayoutState.mItemDirection;
            this.fill(recycler, this.mLayoutState, state);
        }
        this.repositionToWrapContentIfNecessary();
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            if (!this.mShouldReverseLayout) {
                this.fixStartGap(recycler, state, true);
                this.fixEndGap(recycler, state, false);
            }
            else {
                this.fixEndGap(recycler, state, true);
                this.fixStartGap(recycler, state, false);
            }
        }
        int n3;
        if (b && !state.isPreLayout()) {
            int n2;
            if (this.mGapStrategy != 0 && ((RecyclerView.LayoutManager)this).getChildCount() > 0 && (this.mLaidOutInvalidFullSpan || this.hasGapsToFix() != null)) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                n3 = 0;
            }
            else {
                ((RecyclerView.LayoutManager)this).removeCallbacks(this.mCheckForGapsRunnable);
                n3 = n;
                if (!this.checkForGaps()) {
                    n3 = 0;
                }
            }
        }
        else {
            n3 = 0;
        }
        if (state.isPreLayout()) {
            this.mAnchorInfo.reset();
        }
        this.mLastLayoutFromEnd = mAnchorInfo.mLayoutFromEnd;
        this.mLastLayoutRTL = this.isLayoutRTL();
        if (n3 != 0) {
            this.mAnchorInfo.reset();
            this.onLayoutChildren(recycler, state, false);
        }
    }
    
    private boolean preferLastSpan(final int n) {
        final boolean b = false;
        final boolean b2 = false;
        if (this.mOrientation != 0) {
            return n == -1 == this.mShouldReverseLayout == this.isLayoutRTL() || b2;
        }
        return n == -1 != this.mShouldReverseLayout || b;
    }
    
    private void prependViewToAllSpans(final View view) {
        int mSpanCount = this.mSpanCount;
        while (--mSpanCount >= 0) {
            this.mSpans[mSpanCount].prependToSpan(view);
        }
    }
    
    private void recycle(final Recycler recycler, final android.support.v7.widget.LayoutState layoutState) {
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            if (layoutState.mAvailable != 0) {
                if (layoutState.mLayoutDirection != -1) {
                    final int a = this.getMinEnd(layoutState.mEndLine) - layoutState.mEndLine;
                    int mStartLine;
                    if (a >= 0) {
                        mStartLine = Math.min(a, layoutState.mAvailable) + layoutState.mStartLine;
                    }
                    else {
                        mStartLine = layoutState.mStartLine;
                    }
                    this.recycleFromStart(recycler, mStartLine);
                }
                else {
                    final int a2 = layoutState.mStartLine - this.getMaxStart(layoutState.mStartLine);
                    int mEndLine;
                    if (a2 >= 0) {
                        mEndLine = layoutState.mEndLine - Math.min(a2, layoutState.mAvailable);
                    }
                    else {
                        mEndLine = layoutState.mEndLine;
                    }
                    this.recycleFromEnd(recycler, mEndLine);
                }
            }
            else if (layoutState.mLayoutDirection != -1) {
                this.recycleFromStart(recycler, layoutState.mStartLine);
            }
            else {
                this.recycleFromEnd(recycler, layoutState.mEndLine);
            }
        }
    }
    
    private void recycleFromEnd(final Recycler recycler, final int n) {
        for (int i = ((RecyclerView.LayoutManager)this).getChildCount() - 1; i >= 0; --i) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            if (this.mPrimaryOrientation.getDecoratedStart(child) < n || this.mPrimaryOrientation.getTransformedStartWithDecoration(child) < n) {
                return;
            }
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (!layoutParams.mFullSpan) {
                if (layoutParams.mSpan.mViews.size() == 1) {
                    return;
                }
                layoutParams.mSpan.popEnd();
            }
            else {
                for (int j = 0; j < this.mSpanCount; ++j) {
                    if (this.mSpans[j].mViews.size() == 1) {
                        return;
                    }
                }
                for (int k = 0; k < this.mSpanCount; ++k) {
                    this.mSpans[k].popEnd();
                }
            }
            ((RecyclerView.LayoutManager)this).removeAndRecycleView(child, recycler);
        }
    }
    
    private void recycleFromStart(final Recycler recycler, final int n) {
        while (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(0);
            if (this.mPrimaryOrientation.getDecoratedEnd(child) > n || this.mPrimaryOrientation.getTransformedEndWithDecoration(child) > n) {
                return;
            }
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (!layoutParams.mFullSpan) {
                if (layoutParams.mSpan.mViews.size() == 1) {
                    return;
                }
                layoutParams.mSpan.popStart();
            }
            else {
                for (int i = 0; i < this.mSpanCount; ++i) {
                    if (this.mSpans[i].mViews.size() == 1) {
                        return;
                    }
                }
                for (int j = 0; j < this.mSpanCount; ++j) {
                    this.mSpans[j].popStart();
                }
            }
            ((RecyclerView.LayoutManager)this).removeAndRecycleView(child, recycler);
        }
    }
    
    private void repositionToWrapContentIfNecessary() {
        if (this.mSecondaryOrientation.getMode() == 1073741824) {
            return;
        }
        float max = 0.0f;
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            float b = (float)this.mSecondaryOrientation.getDecoratedMeasurement(child);
            if (b >= max) {
                if (((LayoutParams)child.getLayoutParams()).isFullSpan()) {
                    b = 1.0f * b / this.mSpanCount;
                }
                max = Math.max(max, b);
            }
        }
        final int mSizePerSpan = this.mSizePerSpan;
        int a = Math.round(this.mSpanCount * max);
        if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
            a = Math.min(a, this.mSecondaryOrientation.getTotalSpace());
        }
        this.updateMeasureSpecs(a);
        if (this.mSizePerSpan != mSizePerSpan) {
            for (int j = 0; j < childCount; ++j) {
                final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(j);
                final LayoutParams layoutParams = (LayoutParams)child2.getLayoutParams();
                if (!layoutParams.mFullSpan) {
                    if (this.isLayoutRTL() && this.mOrientation == 1) {
                        child2.offsetLeftAndRight(-(this.mSpanCount - 1 - layoutParams.mSpan.mIndex) * this.mSizePerSpan - -(this.mSpanCount - 1 - layoutParams.mSpan.mIndex) * mSizePerSpan);
                    }
                    else {
                        final int n = layoutParams.mSpan.mIndex * this.mSizePerSpan;
                        final int n2 = layoutParams.mSpan.mIndex * mSizePerSpan;
                        if (this.mOrientation != 1) {
                            child2.offsetTopAndBottom(n - n2);
                        }
                        else {
                            child2.offsetLeftAndRight(n - n2);
                        }
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
    
    private void setLayoutStateDirection(int n) {
        final int n2 = -1;
        this.mLayoutState.mLayoutDirection = n;
        final android.support.v7.widget.LayoutState mLayoutState = this.mLayoutState;
        if (this.mShouldReverseLayout != (n == -1)) {
            n = n2;
        }
        else {
            n = 1;
        }
        mLayoutState.mItemDirection = n;
    }
    
    private void updateAllRemainingSpans(final int n, final int n2) {
        for (int i = 0; i < this.mSpanCount; ++i) {
            if (!this.mSpans[i].mViews.isEmpty()) {
                this.updateRemainingSpans(this.mSpans[i], n, n2);
            }
        }
    }
    
    private boolean updateAnchorFromChildren(final State state, final AnchorInfo anchorInfo) {
        int mPosition;
        if (!this.mLastLayoutFromEnd) {
            mPosition = this.findFirstReferenceChildPosition(state.getItemCount());
        }
        else {
            mPosition = this.findLastReferenceChildPosition(state.getItemCount());
        }
        anchorInfo.mPosition = mPosition;
        anchorInfo.mOffset = Integer.MIN_VALUE;
        return true;
    }
    
    private void updateLayoutState(int totalSpace, final State state) {
        final boolean b = false;
        this.mLayoutState.mAvailable = 0;
        this.mLayoutState.mCurrentPosition = totalSpace;
        int totalSpace2;
        if (!((RecyclerView.LayoutManager)this).isSmoothScrolling()) {
            totalSpace = 0;
            totalSpace2 = 0;
        }
        else {
            final int targetScrollPosition = state.getTargetScrollPosition();
            if (targetScrollPosition == -1) {
                totalSpace = 0;
                totalSpace2 = 0;
            }
            else if (this.mShouldReverseLayout != targetScrollPosition < totalSpace) {
                totalSpace2 = this.mPrimaryOrientation.getTotalSpace();
                totalSpace = 0;
            }
            else {
                totalSpace = this.mPrimaryOrientation.getTotalSpace();
                totalSpace2 = 0;
            }
        }
        if (!((RecyclerView.LayoutManager)this).getClipToPadding()) {
            this.mLayoutState.mEndLine = totalSpace + this.mPrimaryOrientation.getEnd();
            this.mLayoutState.mStartLine = -totalSpace2;
        }
        else {
            this.mLayoutState.mStartLine = this.mPrimaryOrientation.getStartAfterPadding() - totalSpace2;
            this.mLayoutState.mEndLine = totalSpace + this.mPrimaryOrientation.getEndAfterPadding();
        }
        this.mLayoutState.mStopInFocusable = false;
        this.mLayoutState.mRecycle = true;
        final android.support.v7.widget.LayoutState mLayoutState = this.mLayoutState;
        boolean mInfinite;
        if (this.mPrimaryOrientation.getMode() != 0) {
            mInfinite = b;
        }
        else {
            mInfinite = b;
            if (this.mPrimaryOrientation.getEnd() == 0) {
                mInfinite = true;
            }
        }
        mLayoutState.mInfinite = mInfinite;
    }
    
    private void updateRemainingSpans(final Span span, final int n, final int n2) {
        final int deletedSize = span.getDeletedSize();
        if (n != -1) {
            if (span.getEndLine() - deletedSize >= n2) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        }
        else if (deletedSize + span.getStartLine() <= n2) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }
    
    private int updateSpecWithExtra(final int n, final int n2, final int n3) {
        if (n2 == 0 && n3 == 0) {
            return n;
        }
        final int mode = View$MeasureSpec.getMode(n);
        if (mode != Integer.MIN_VALUE && mode != 1073741824) {
            return n;
        }
        return View$MeasureSpec.makeMeasureSpec(Math.max(0, View$MeasureSpec.getSize(n) - n2 - n3), mode);
    }
    
    boolean areAllEndsEqual() {
        final int endLine = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; ++i) {
            if (this.mSpans[i].getEndLine(Integer.MIN_VALUE) != endLine) {
                return false;
            }
        }
        return true;
    }
    
    boolean areAllStartsEqual() {
        final int startLine = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; ++i) {
            if (this.mSpans[i].getStartLine(Integer.MIN_VALUE) != startLine) {
                return false;
            }
        }
        return true;
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
    
    boolean checkForGaps() {
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0 || this.mGapStrategy == 0 || !((RecyclerView.LayoutManager)this).isAttachedToWindow()) {
            return false;
        }
        int n;
        int n2;
        if (!this.mShouldReverseLayout) {
            n = this.getFirstChildPosition();
            n2 = this.getLastChildPosition();
        }
        else {
            n = this.getLastChildPosition();
            n2 = this.getFirstChildPosition();
        }
        if (n == 0 && this.hasGapsToFix() != null) {
            this.mLazySpanLookup.clear();
            ((RecyclerView.LayoutManager)this).requestSimpleAnimationsInNextLayout();
            ((RecyclerView.LayoutManager)this).requestLayout();
            return true;
        }
        if (!this.mLaidOutInvalidFullSpan) {
            return false;
        }
        int n3;
        if (!this.mShouldReverseLayout) {
            n3 = 1;
        }
        else {
            n3 = -1;
        }
        final FullSpanItem firstFullSpanItemInRange = this.mLazySpanLookup.getFirstFullSpanItemInRange(n, n2 + 1, n3, true);
        if (firstFullSpanItemInRange != null) {
            final FullSpanItem firstFullSpanItemInRange2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(n, firstFullSpanItemInRange.mPosition, n3 * -1, true);
            if (firstFullSpanItemInRange2 != null) {
                this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange2.mPosition + 1);
            }
            else {
                this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange.mPosition);
            }
            ((RecyclerView.LayoutManager)this).requestSimpleAnimationsInNextLayout();
            ((RecyclerView.LayoutManager)this).requestLayout();
            return true;
        }
        this.mLaidOutInvalidFullSpan = false;
        this.mLazySpanLookup.forceInvalidateAfter(n2 + 1);
        return false;
    }
    
    @Override
    public boolean checkLayoutParams(final RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }
    
    @Override
    public void collectAdjacentPrefetchPositions(int toIndex, int i, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final int n = 0;
        if (this.mOrientation != 0) {
            toIndex = i;
        }
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0 && toIndex != 0) {
            this.prepareLayoutStateForDelta(toIndex, state);
            if (this.mPrefetchDistances == null || this.mPrefetchDistances.length < this.mSpanCount) {
                this.mPrefetchDistances = new int[this.mSpanCount];
            }
            i = 0;
            toIndex = 0;
            while (i < this.mSpanCount) {
                int n2;
                if (this.mLayoutState.mItemDirection != -1) {
                    n2 = this.mSpans[i].getEndLine(this.mLayoutState.mEndLine) - this.mLayoutState.mEndLine;
                }
                else {
                    n2 = this.mLayoutState.mStartLine - this.mSpans[i].getStartLine(this.mLayoutState.mStartLine);
                }
                if (n2 >= 0) {
                    this.mPrefetchDistances[toIndex] = n2;
                    ++toIndex;
                }
                ++i;
            }
            Arrays.sort(this.mPrefetchDistances, 0, toIndex);
            android.support.v7.widget.LayoutState mLayoutState;
            for (i = n; i < toIndex && this.mLayoutState.hasMore(state); ++i) {
                layoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[i]);
                mLayoutState = this.mLayoutState;
                mLayoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
            }
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
    public PointF computeScrollVectorForPosition(int calculateScrollDirectionForPosition) {
        calculateScrollDirectionForPosition = this.calculateScrollDirectionForPosition(calculateScrollDirectionForPosition);
        final PointF pointF = new PointF();
        if (calculateScrollDirectionForPosition != 0) {
            if (this.mOrientation != 0) {
                pointF.x = 0.0f;
                pointF.y = (float)calculateScrollDirectionForPosition;
            }
            else {
                pointF.x = (float)calculateScrollDirectionForPosition;
                pointF.y = 0.0f;
            }
            return pointF;
        }
        return null;
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
    
    public int[] findFirstCompletelyVisibleItemPositions(int[] array) {
        if (array != null) {
            if (array.length < this.mSpanCount) {
                throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + array.length);
            }
        }
        else {
            array = new int[this.mSpanCount];
        }
        for (int i = 0; i < this.mSpanCount; ++i) {
            array[i] = this.mSpans[i].findFirstCompletelyVisibleItemPosition();
        }
        return array;
    }
    
    View findFirstVisibleItemClosestToEnd(final boolean b) {
        final int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        final int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        int i = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        View view = null;
        while (i >= 0) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(child);
            final int decoratedEnd = this.mPrimaryOrientation.getDecoratedEnd(child);
            View view2;
            if (decoratedEnd <= startAfterPadding) {
                view2 = view;
            }
            else {
                view2 = view;
                if (decoratedStart < endAfterPadding) {
                    if (decoratedEnd <= endAfterPadding || !b) {
                        return child;
                    }
                    if ((view2 = view) == null) {
                        view2 = child;
                    }
                }
            }
            --i;
            view = view2;
        }
        return view;
    }
    
    View findFirstVisibleItemClosestToStart(final boolean b) {
        final int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        final int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        int i = 0;
        View view = null;
        while (i < childCount) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(child);
            View view2;
            if (this.mPrimaryOrientation.getDecoratedEnd(child) <= startAfterPadding) {
                view2 = view;
            }
            else {
                view2 = view;
                if (decoratedStart < endAfterPadding) {
                    if (decoratedStart >= startAfterPadding || !b) {
                        return child;
                    }
                    if ((view2 = view) == null) {
                        view2 = child;
                    }
                }
            }
            ++i;
            view = view2;
        }
        return view;
    }
    
    int findFirstVisibleItemPositionInt() {
        View view;
        if (!this.mShouldReverseLayout) {
            view = this.findFirstVisibleItemClosestToStart(true);
        }
        else {
            view = this.findFirstVisibleItemClosestToEnd(true);
        }
        int position;
        if (view != null) {
            position = ((RecyclerView.LayoutManager)this).getPosition(view);
        }
        else {
            position = -1;
        }
        return position;
    }
    
    public int[] findFirstVisibleItemPositions(int[] array) {
        if (array != null) {
            if (array.length < this.mSpanCount) {
                throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + array.length);
            }
        }
        else {
            array = new int[this.mSpanCount];
        }
        for (int i = 0; i < this.mSpanCount; ++i) {
            array[i] = this.mSpans[i].findFirstVisibleItemPosition();
        }
        return array;
    }
    
    public int[] findLastCompletelyVisibleItemPositions(int[] array) {
        if (array != null) {
            if (array.length < this.mSpanCount) {
                throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + array.length);
            }
        }
        else {
            array = new int[this.mSpanCount];
        }
        for (int i = 0; i < this.mSpanCount; ++i) {
            array[i] = this.mSpans[i].findLastCompletelyVisibleItemPosition();
        }
        return array;
    }
    
    public int[] findLastVisibleItemPositions(int[] array) {
        if (array != null) {
            if (array.length < this.mSpanCount) {
                throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + array.length);
            }
        }
        else {
            array = new int[this.mSpanCount];
        }
        for (int i = 0; i < this.mSpanCount; ++i) {
            array[i] = this.mSpans[i].findLastVisibleItemPosition();
        }
        return array;
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
        if (this.mOrientation != 1) {
            return super.getColumnCountForAccessibility(recycler, state);
        }
        return this.mSpanCount;
    }
    
    int getFirstChildPosition() {
        int position = 0;
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0) {
            position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(0));
        }
        return position;
    }
    
    public int getGapStrategy() {
        return this.mGapStrategy;
    }
    
    int getLastChildPosition() {
        int position = 0;
        final int childCount = ((RecyclerView.LayoutManager)this).getChildCount();
        if (childCount != 0) {
            position = ((RecyclerView.LayoutManager)this).getPosition(((RecyclerView.LayoutManager)this).getChildAt(childCount - 1));
        }
        return position;
    }
    
    public int getOrientation() {
        return this.mOrientation;
    }
    
    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }
    
    @Override
    public int getRowCountForAccessibility(final Recycler recycler, final State state) {
        if (this.mOrientation != 0) {
            return super.getRowCountForAccessibility(recycler, state);
        }
        return this.mSpanCount;
    }
    
    public int getSpanCount() {
        return this.mSpanCount;
    }
    
    View hasGapsToFix() {
        int n = ((RecyclerView.LayoutManager)this).getChildCount() - 1;
        final BitSet set = new BitSet(this.mSpanCount);
        set.set(0, this.mSpanCount, true);
        int n2;
        if (this.mOrientation == 1 && this.isLayoutRTL()) {
            n2 = 1;
        }
        else {
            n2 = -1;
        }
        int n3;
        if (!this.mShouldReverseLayout) {
            n3 = n + 1;
            n = 0;
        }
        else {
            n3 = -1;
        }
        int n4;
        if (n >= n3) {
            n4 = -1;
        }
        else {
            n4 = 1;
        }
        for (int i = n; i != n3; i += n4) {
            final View child = ((RecyclerView.LayoutManager)this).getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (set.get(layoutParams.mSpan.mIndex)) {
                if (this.checkSpanForGap(layoutParams.mSpan)) {
                    return child;
                }
                set.clear(layoutParams.mSpan.mIndex);
            }
            if (!layoutParams.mFullSpan && i + n4 != n3) {
                final View child2 = ((RecyclerView.LayoutManager)this).getChildAt(i + n4);
                int n5;
                if (!this.mShouldReverseLayout) {
                    final int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(child);
                    final int decoratedStart2 = this.mPrimaryOrientation.getDecoratedStart(child2);
                    if (decoratedStart > decoratedStart2) {
                        return child;
                    }
                    if (decoratedStart != decoratedStart2) {
                        n5 = 0;
                    }
                    else {
                        n5 = 1;
                    }
                }
                else {
                    final int decoratedEnd = this.mPrimaryOrientation.getDecoratedEnd(child);
                    final int decoratedEnd2 = this.mPrimaryOrientation.getDecoratedEnd(child2);
                    if (decoratedEnd < decoratedEnd2) {
                        return child;
                    }
                    if (decoratedEnd != decoratedEnd2) {
                        n5 = 0;
                    }
                    else {
                        n5 = 1;
                    }
                }
                if (n5 != 0) {
                    int n6;
                    if (layoutParams.mSpan.mIndex - ((LayoutParams)child2.getLayoutParams()).mSpan.mIndex >= 0) {
                        n6 = 0;
                    }
                    else {
                        n6 = 1;
                    }
                    int n7;
                    if (n2 >= 0) {
                        n7 = 0;
                    }
                    else {
                        n7 = 1;
                    }
                    if (n6 != n7) {
                        return child;
                    }
                }
            }
        }
        return null;
    }
    
    public void invalidateSpanAssignments() {
        this.mLazySpanLookup.clear();
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    boolean isLayoutRTL() {
        boolean b = true;
        if (((RecyclerView.LayoutManager)this).getLayoutDirection() != 1) {
            b = false;
        }
        return b;
    }
    
    @Override
    public void offsetChildrenHorizontal(final int n) {
        super.offsetChildrenHorizontal(n);
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].onOffset(n);
        }
    }
    
    @Override
    public void offsetChildrenVertical(final int n) {
        super.offsetChildrenVertical(n);
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].onOffset(n);
        }
    }
    
    @Override
    public void onDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
        ((RecyclerView.LayoutManager)this).removeCallbacks(this.mCheckForGapsRunnable);
        for (int i = 0; i < this.mSpanCount; ++i) {
            this.mSpans[i].clear();
        }
        recyclerView.requestLayout();
    }
    
    @Nullable
    @Override
    public View onFocusSearchFailed(View containingItemView, int n, final Recycler recycler, final State state) {
        final int n2 = 0;
        if (((RecyclerView.LayoutManager)this).getChildCount() == 0) {
            return null;
        }
        containingItemView = ((RecyclerView.LayoutManager)this).findContainingItemView(containingItemView);
        if (containingItemView == null) {
            return null;
        }
        this.resolveShouldLayoutReverse();
        final int convertFocusDirectionToLayoutDirection = this.convertFocusDirectionToLayoutDirection(n);
        if (convertFocusDirectionToLayoutDirection != Integer.MIN_VALUE) {
            final LayoutParams layoutParams = (LayoutParams)containingItemView.getLayoutParams();
            final boolean mFullSpan = layoutParams.mFullSpan;
            final Span mSpan = layoutParams.mSpan;
            if (convertFocusDirectionToLayoutDirection != 1) {
                n = this.getFirstChildPosition();
            }
            else {
                n = this.getLastChildPosition();
            }
            this.updateLayoutState(n, state);
            this.setLayoutStateDirection(convertFocusDirectionToLayoutDirection);
            this.mLayoutState.mCurrentPosition = this.mLayoutState.mItemDirection + n;
            this.mLayoutState.mAvailable = (int)(this.mPrimaryOrientation.getTotalSpace() * 0.33333334f);
            this.mLayoutState.mStopInFocusable = true;
            this.mLayoutState.mRecycle = false;
            this.fill(recycler, this.mLayoutState, state);
            this.mLastLayoutFromEnd = this.mShouldReverseLayout;
            if (!mFullSpan) {
                final View focusableViewAfter = mSpan.getFocusableViewAfter(n, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter != null && focusableViewAfter != containingItemView) {
                    return focusableViewAfter;
                }
            }
            Label_0182: {
                if (this.preferLastSpan(convertFocusDirectionToLayoutDirection)) {
                    int mSpanCount = this.mSpanCount;
                    View focusableViewAfter2;
                    while (true) {
                        final int n3 = mSpanCount - 1;
                        if (n3 < 0) {
                            break Label_0182;
                        }
                        focusableViewAfter2 = this.mSpans[n3].getFocusableViewAfter(n, convertFocusDirectionToLayoutDirection);
                        mSpanCount = n3;
                        if (focusableViewAfter2 == null) {
                            continue;
                        }
                        mSpanCount = n3;
                        if (focusableViewAfter2 != containingItemView) {
                            break;
                        }
                    }
                    return focusableViewAfter2;
                }
                for (int i = 0; i < this.mSpanCount; ++i) {
                    final View focusableViewAfter3 = this.mSpans[i].getFocusableViewAfter(n, convertFocusDirectionToLayoutDirection);
                    if (focusableViewAfter3 != null && focusableViewAfter3 != containingItemView) {
                        return focusableViewAfter3;
                    }
                }
            }
            if (this.mReverseLayout) {
                n = 0;
            }
            else {
                n = 1;
            }
            int n4;
            if (convertFocusDirectionToLayoutDirection != -1) {
                n4 = 0;
            }
            else {
                n4 = 1;
            }
            if (n != n4) {
                n = 0;
            }
            else {
                n = 1;
            }
            if (!mFullSpan) {
                int n5;
                if (n == 0) {
                    n5 = mSpan.findLastPartiallyVisibleItemPosition();
                }
                else {
                    n5 = mSpan.findFirstPartiallyVisibleItemPosition();
                }
                final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(n5);
                if (viewByPosition != null && viewByPosition != containingItemView) {
                    return viewByPosition;
                }
            }
            if (!this.preferLastSpan(convertFocusDirectionToLayoutDirection)) {
                for (int j = n2; j < this.mSpanCount; ++j) {
                    int n6;
                    if (n == 0) {
                        n6 = this.mSpans[j].findLastPartiallyVisibleItemPosition();
                    }
                    else {
                        n6 = this.mSpans[j].findFirstPartiallyVisibleItemPosition();
                    }
                    final View viewByPosition2 = ((RecyclerView.LayoutManager)this).findViewByPosition(n6);
                    if (viewByPosition2 != null && viewByPosition2 != containingItemView) {
                        return viewByPosition2;
                    }
                }
            }
            else {
                for (int k = this.mSpanCount - 1; k >= 0; --k) {
                    if (k != mSpan.mIndex) {
                        int n7;
                        if (n == 0) {
                            n7 = this.mSpans[k].findLastPartiallyVisibleItemPosition();
                        }
                        else {
                            n7 = this.mSpans[k].findFirstPartiallyVisibleItemPosition();
                        }
                        final View viewByPosition3 = ((RecyclerView.LayoutManager)this).findViewByPosition(n7);
                        if (viewByPosition3 != null && viewByPosition3 != containingItemView) {
                            return viewByPosition3;
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }
    
    @Override
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (((RecyclerView.LayoutManager)this).getChildCount() > 0) {
            final View firstVisibleItemClosestToStart = this.findFirstVisibleItemClosestToStart(false);
            final View firstVisibleItemClosestToEnd = this.findFirstVisibleItemClosestToEnd(false);
            if (firstVisibleItemClosestToStart == null || firstVisibleItemClosestToEnd == null) {
                return;
            }
            final int position = ((RecyclerView.LayoutManager)this).getPosition(firstVisibleItemClosestToStart);
            final int position2 = ((RecyclerView.LayoutManager)this).getPosition(firstVisibleItemClosestToEnd);
            if (position >= position2) {
                accessibilityEvent.setFromIndex(position2);
                accessibilityEvent.setToIndex(position);
            }
            else {
                accessibilityEvent.setFromIndex(position);
                accessibilityEvent.setToIndex(position2);
            }
        }
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            final LayoutParams layoutParams2 = (LayoutParams)layoutParams;
            if (this.mOrientation != 0) {
                final int spanIndex = layoutParams2.getSpanIndex();
                int mSpanCount;
                if (!layoutParams2.mFullSpan) {
                    mSpanCount = 1;
                }
                else {
                    mSpanCount = this.mSpanCount;
                }
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(-1, -1, spanIndex, mSpanCount, layoutParams2.mFullSpan, false));
            }
            else {
                final int spanIndex2 = layoutParams2.getSpanIndex();
                int mSpanCount2;
                if (!layoutParams2.mFullSpan) {
                    mSpanCount2 = 1;
                }
                else {
                    mSpanCount2 = this.mSpanCount;
                }
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(spanIndex2, mSpanCount2, -1, -1, layoutParams2.mFullSpan, false));
            }
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
    }
    
    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        this.handleUpdate(n, n2, 1);
    }
    
    @Override
    public void onItemsChanged(final RecyclerView recyclerView) {
        this.mLazySpanLookup.clear();
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        this.handleUpdate(n, n2, 8);
    }
    
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, final int n, final int n2) {
        this.handleUpdate(n, n2, 2);
    }
    
    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2, final Object o) {
        this.handleUpdate(n, n2, 4);
    }
    
    @Override
    public void onLayoutChildren(final Recycler recycler, final State state) {
        this.onLayoutChildren(recycler, state, true);
    }
    
    @Override
    public void onLayoutCompleted(final State state) {
        super.onLayoutCompleted(state);
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
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
            savedState.mReverseLayout = this.mReverseLayout;
            savedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
            savedState.mLastLayoutRTL = this.mLastLayoutRTL;
            if (this.mLazySpanLookup != null && this.mLazySpanLookup.mData != null) {
                savedState.mSpanLookup = this.mLazySpanLookup.mData;
                savedState.mSpanLookupSize = savedState.mSpanLookup.length;
                savedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
            }
            else {
                savedState.mSpanLookupSize = 0;
            }
            if (((RecyclerView.LayoutManager)this).getChildCount() <= 0) {
                savedState.mAnchorPosition = -1;
                savedState.mVisibleAnchorPosition = -1;
                savedState.mSpanOffsetsSize = 0;
            }
            else {
                int mAnchorPosition;
                if (!this.mLastLayoutFromEnd) {
                    mAnchorPosition = this.getFirstChildPosition();
                }
                else {
                    mAnchorPosition = this.getLastChildPosition();
                }
                savedState.mAnchorPosition = mAnchorPosition;
                savedState.mVisibleAnchorPosition = this.findFirstVisibleItemPositionInt();
                savedState.mSpanOffsetsSize = this.mSpanCount;
                savedState.mSpanOffsets = new int[this.mSpanCount];
                for (int i = 0; i < this.mSpanCount; ++i) {
                    int startLine;
                    if (!this.mLastLayoutFromEnd) {
                        startLine = this.mSpans[i].getStartLine(Integer.MIN_VALUE);
                        if (startLine != Integer.MIN_VALUE) {
                            startLine -= this.mPrimaryOrientation.getStartAfterPadding();
                        }
                    }
                    else {
                        final int endLine = this.mSpans[i].getEndLine(Integer.MIN_VALUE);
                        if ((startLine = endLine) != Integer.MIN_VALUE) {
                            startLine = endLine - this.mPrimaryOrientation.getEndAfterPadding();
                        }
                    }
                    savedState.mSpanOffsets[i] = startLine;
                }
            }
            return (Parcelable)savedState;
        }
        return (Parcelable)new SavedState(this.mPendingSavedState);
    }
    
    @Override
    public void onScrollStateChanged(final int n) {
        if (n == 0) {
            this.checkForGaps();
        }
    }
    
    void prepareLayoutStateForDelta(final int a, final State state) {
        int layoutStateDirection;
        int n;
        if (a <= 0) {
            layoutStateDirection = -1;
            n = this.getFirstChildPosition();
        }
        else {
            n = this.getLastChildPosition();
            layoutStateDirection = 1;
        }
        this.mLayoutState.mRecycle = true;
        this.updateLayoutState(n, state);
        this.setLayoutStateDirection(layoutStateDirection);
        this.mLayoutState.mCurrentPosition = n + this.mLayoutState.mItemDirection;
        this.mLayoutState.mAvailable = Math.abs(a);
    }
    
    int scrollBy(final int n, final Recycler recycler, final State state) {
        if (((RecyclerView.LayoutManager)this).getChildCount() != 0 && n != 0) {
            this.prepareLayoutStateForDelta(n, state);
            final int fill = this.fill(recycler, this.mLayoutState, state);
            int n2 = n;
            if (this.mLayoutState.mAvailable >= fill) {
                if (n >= 0) {
                    n2 = fill;
                }
                else {
                    n2 = -fill;
                }
            }
            this.mPrimaryOrientation.offsetChildren(-n2);
            this.mLastLayoutFromEnd = this.mShouldReverseLayout;
            this.mLayoutState.mAvailable = 0;
            this.recycle(recycler, this.mLayoutState);
            return n2;
        }
        return 0;
    }
    
    @Override
    public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
        return this.scrollBy(n, recycler, state);
    }
    
    @Override
    public void scrollToPosition(final int mPendingScrollPosition) {
        if (this.mPendingSavedState != null && this.mPendingSavedState.mAnchorPosition != mPendingScrollPosition) {
            this.mPendingSavedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void scrollToPositionWithOffset(final int mPendingScrollPosition, final int mPendingScrollPositionOffset) {
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = mPendingScrollPosition;
        this.mPendingScrollPositionOffset = mPendingScrollPositionOffset;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
        return this.scrollBy(n, recycler, state);
    }
    
    public void setGapStrategy(final int mGapStrategy) {
        boolean autoMeasureEnabled = false;
        this.assertNotInLayoutOrScroll(null);
        if (mGapStrategy == this.mGapStrategy) {
            return;
        }
        if (mGapStrategy != 0 && mGapStrategy != 2) {
            throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
        }
        this.mGapStrategy = mGapStrategy;
        if (this.mGapStrategy != 0) {
            autoMeasureEnabled = true;
        }
        ((RecyclerView.LayoutManager)this).setAutoMeasureEnabled(autoMeasureEnabled);
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    @Override
    public void setMeasuredDimension(final Rect rect, int chooseSize, int chooseSize2) {
        final int n = ((RecyclerView.LayoutManager)this).getPaddingLeft() + ((RecyclerView.LayoutManager)this).getPaddingRight();
        final int n2 = ((RecyclerView.LayoutManager)this).getPaddingTop() + ((RecyclerView.LayoutManager)this).getPaddingBottom();
        if (this.mOrientation != 1) {
            final int chooseSize3 = RecyclerView.LayoutManager.chooseSize(chooseSize, n + rect.width(), ((RecyclerView.LayoutManager)this).getMinimumWidth());
            chooseSize = RecyclerView.LayoutManager.chooseSize(chooseSize2, n2 + this.mSizePerSpan * this.mSpanCount, ((RecyclerView.LayoutManager)this).getMinimumHeight());
            chooseSize2 = chooseSize3;
        }
        else {
            final int chooseSize4 = RecyclerView.LayoutManager.chooseSize(chooseSize2, n2 + rect.height(), ((RecyclerView.LayoutManager)this).getMinimumHeight());
            chooseSize2 = RecyclerView.LayoutManager.chooseSize(chooseSize, n + this.mSizePerSpan * this.mSpanCount, ((RecyclerView.LayoutManager)this).getMinimumWidth());
            chooseSize = chooseSize4;
        }
        ((RecyclerView.LayoutManager)this).setMeasuredDimension(chooseSize2, chooseSize);
    }
    
    public void setOrientation(final int mOrientation) {
        if (mOrientation != 0 && mOrientation != 1) {
            throw new IllegalArgumentException("invalid orientation.");
        }
        this.assertNotInLayoutOrScroll(null);
        if (mOrientation != this.mOrientation) {
            this.mOrientation = mOrientation;
            final OrientationHelper mPrimaryOrientation = this.mPrimaryOrientation;
            this.mPrimaryOrientation = this.mSecondaryOrientation;
            this.mSecondaryOrientation = mPrimaryOrientation;
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    public void setReverseLayout(final boolean b) {
        this.assertNotInLayoutOrScroll(null);
        if (this.mPendingSavedState != null && this.mPendingSavedState.mReverseLayout != b) {
            this.mPendingSavedState.mReverseLayout = b;
        }
        this.mReverseLayout = b;
        ((RecyclerView.LayoutManager)this).requestLayout();
    }
    
    public void setSpanCount(int i) {
        this.assertNotInLayoutOrScroll(null);
        if (i != this.mSpanCount) {
            this.invalidateSpanAssignments();
            this.mSpanCount = i;
            this.mRemainingSpans = new BitSet(this.mSpanCount);
            this.mSpans = new Span[this.mSpanCount];
            for (i = 0; i < this.mSpanCount; ++i) {
                this.mSpans[i] = new Span(i);
            }
            ((RecyclerView.LayoutManager)this).requestLayout();
        }
    }
    
    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int targetPosition) {
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        ((RecyclerView.SmoothScroller)linearSmoothScroller).setTargetPosition(targetPosition);
        ((RecyclerView.LayoutManager)this).startSmoothScroll(linearSmoothScroller);
    }
    
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null;
    }
    
    boolean updateAnchorFromPendingData(final State state, final AnchorInfo anchorInfo) {
        boolean mLayoutFromEnd = false;
        if (state.isPreLayout() || this.mPendingScrollPosition == -1) {
            return false;
        }
        if (this.mPendingScrollPosition >= 0 && this.mPendingScrollPosition < state.getItemCount()) {
            if (this.mPendingSavedState != null && this.mPendingSavedState.mAnchorPosition != -1 && this.mPendingSavedState.mSpanOffsetsSize >= 1) {
                anchorInfo.mOffset = Integer.MIN_VALUE;
                anchorInfo.mPosition = this.mPendingScrollPosition;
            }
            else {
                final View viewByPosition = ((RecyclerView.LayoutManager)this).findViewByPosition(this.mPendingScrollPosition);
                if (viewByPosition == null) {
                    anchorInfo.mPosition = this.mPendingScrollPosition;
                    if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                        anchorInfo.assignCoordinateFromPadding(this.mPendingScrollPositionOffset);
                    }
                    else {
                        if (this.calculateScrollDirectionForPosition(anchorInfo.mPosition) == 1) {
                            mLayoutFromEnd = true;
                        }
                        anchorInfo.mLayoutFromEnd = mLayoutFromEnd;
                        anchorInfo.assignCoordinateFromPadding();
                    }
                    anchorInfo.mInvalidateOffsets = true;
                }
                else {
                    int mPosition;
                    if (!this.mShouldReverseLayout) {
                        mPosition = this.getFirstChildPosition();
                    }
                    else {
                        mPosition = this.getLastChildPosition();
                    }
                    anchorInfo.mPosition = mPosition;
                    if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                        if (!anchorInfo.mLayoutFromEnd) {
                            anchorInfo.mOffset = this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedStart(viewByPosition);
                        }
                        else {
                            anchorInfo.mOffset = this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedEnd(viewByPosition);
                        }
                        return true;
                    }
                    if (this.mPrimaryOrientation.getDecoratedMeasurement(viewByPosition) > this.mPrimaryOrientation.getTotalSpace()) {
                        int mOffset;
                        if (!anchorInfo.mLayoutFromEnd) {
                            mOffset = this.mPrimaryOrientation.getStartAfterPadding();
                        }
                        else {
                            mOffset = this.mPrimaryOrientation.getEndAfterPadding();
                        }
                        anchorInfo.mOffset = mOffset;
                        return true;
                    }
                    final int n = this.mPrimaryOrientation.getDecoratedStart(viewByPosition) - this.mPrimaryOrientation.getStartAfterPadding();
                    if (n < 0) {
                        anchorInfo.mOffset = -n;
                        return true;
                    }
                    final int mOffset2 = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(viewByPosition);
                    if (mOffset2 < 0) {
                        anchorInfo.mOffset = mOffset2;
                        return true;
                    }
                    anchorInfo.mOffset = Integer.MIN_VALUE;
                }
            }
            return true;
        }
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        return false;
    }
    
    void updateAnchorInfoForLayout(final State state, final AnchorInfo anchorInfo) {
        if (this.updateAnchorFromPendingData(state, anchorInfo)) {
            return;
        }
        if (!this.updateAnchorFromChildren(state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = 0;
        }
    }
    
    void updateMeasureSpecs(final int n) {
        this.mSizePerSpan = n / this.mSpanCount;
        this.mFullSizeSpec = View$MeasureSpec.makeMeasureSpec(n, this.mSecondaryOrientation.getMode());
    }
    
    class AnchorInfo
    {
        boolean mInvalidateOffsets;
        boolean mLayoutFromEnd;
        int mOffset;
        int mPosition;
        int[] mSpanReferenceLines;
        boolean mValid;
        
        AnchorInfo() {
            this.reset();
        }
        
        void assignCoordinateFromPadding() {
            int mOffset;
            if (!this.mLayoutFromEnd) {
                mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            }
            else {
                mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            }
            this.mOffset = mOffset;
        }
        
        void assignCoordinateFromPadding(final int n) {
            if (!this.mLayoutFromEnd) {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + n;
            }
            else {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - n;
            }
        }
        
        void reset() {
            this.mPosition = -1;
            this.mOffset = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mInvalidateOffsets = false;
            this.mValid = false;
            if (this.mSpanReferenceLines != null) {
                Arrays.fill(this.mSpanReferenceLines, -1);
            }
        }
        
        void saveSpanReferenceLines(final Span[] array) {
            final int length = array.length;
            if (this.mSpanReferenceLines == null || this.mSpanReferenceLines.length < length) {
                this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
            }
            for (int i = 0; i < length; ++i) {
                this.mSpanReferenceLines[i] = array[i].getStartLine(Integer.MIN_VALUE);
            }
        }
    }
    
    public static class LayoutParams extends RecyclerView.LayoutParams
    {
        public static final int INVALID_SPAN_ID = -1;
        boolean mFullSpan;
        Span mSpan;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public LayoutParams(final RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
        }
        
        public final int getSpanIndex() {
            if (this.mSpan != null) {
                return this.mSpan.mIndex;
            }
            return -1;
        }
        
        public boolean isFullSpan() {
            return this.mFullSpan;
        }
        
        public void setFullSpan(final boolean mFullSpan) {
            this.mFullSpan = mFullSpan;
        }
    }
    
    static class LazySpanLookup
    {
        private static final int MIN_SIZE = 10;
        int[] mData;
        List<FullSpanItem> mFullSpanItems;
        
        private int invalidateFullSpansAfter(final int n) {
            if (this.mFullSpanItems != null) {
                final FullSpanItem fullSpanItem = this.getFullSpanItem(n);
                if (fullSpanItem != null) {
                    this.mFullSpanItems.remove(fullSpanItem);
                }
                final int size = this.mFullSpanItems.size();
                int i = 0;
                while (true) {
                    while (i < size) {
                        final int n2 = i;
                        if (this.mFullSpanItems.get(i).mPosition < n) {
                            ++i;
                        }
                        else {
                            if (n2 == -1) {
                                return -1;
                            }
                            final FullSpanItem fullSpanItem2 = this.mFullSpanItems.get(n2);
                            this.mFullSpanItems.remove(n2);
                            return fullSpanItem2.mPosition;
                        }
                    }
                    final int n2 = -1;
                    continue;
                }
            }
            return -1;
        }
        
        private void offsetFullSpansForAddition(final int n, final int n2) {
            if (this.mFullSpanItems != null) {
                for (int i = this.mFullSpanItems.size() - 1; i >= 0; --i) {
                    final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                    if (fullSpanItem.mPosition >= n) {
                        fullSpanItem.mPosition += n2;
                    }
                }
            }
        }
        
        private void offsetFullSpansForRemoval(final int n, final int n2) {
            if (this.mFullSpanItems != null) {
                for (int i = this.mFullSpanItems.size() - 1; i >= 0; --i) {
                    final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                    if (fullSpanItem.mPosition >= n) {
                        if (fullSpanItem.mPosition >= n + n2) {
                            fullSpanItem.mPosition -= n2;
                        }
                        else {
                            this.mFullSpanItems.remove(i);
                        }
                    }
                }
            }
        }
        
        public void addFullSpanItem(final FullSpanItem fullSpanItem) {
            if (this.mFullSpanItems == null) {
                this.mFullSpanItems = new ArrayList<FullSpanItem>();
            }
            for (int size = this.mFullSpanItems.size(), i = 0; i < size; ++i) {
                final FullSpanItem fullSpanItem2 = this.mFullSpanItems.get(i);
                if (fullSpanItem2.mPosition == fullSpanItem.mPosition) {
                    this.mFullSpanItems.remove(i);
                }
                if (fullSpanItem2.mPosition >= fullSpanItem.mPosition) {
                    this.mFullSpanItems.add(i, fullSpanItem);
                    return;
                }
            }
            this.mFullSpanItems.add(fullSpanItem);
        }
        
        void clear() {
            if (this.mData != null) {
                Arrays.fill(this.mData, -1);
            }
            this.mFullSpanItems = null;
        }
        
        void ensureSize(final int a) {
            if (this.mData != null) {
                if (a >= this.mData.length) {
                    final int[] mData = this.mData;
                    System.arraycopy(mData, 0, this.mData = new int[this.sizeForPosition(a)], 0, mData.length);
                    Arrays.fill(this.mData, mData.length, this.mData.length, -1);
                }
            }
            else {
                Arrays.fill(this.mData = new int[Math.max(a, 10) + 1], -1);
            }
        }
        
        int forceInvalidateAfter(final int n) {
            if (this.mFullSpanItems != null) {
                for (int i = this.mFullSpanItems.size() - 1; i >= 0; --i) {
                    if (this.mFullSpanItems.get(i).mPosition >= n) {
                        this.mFullSpanItems.remove(i);
                    }
                }
            }
            return this.invalidateAfter(n);
        }
        
        public FullSpanItem getFirstFullSpanItemInRange(final int n, final int n2, final int n3, final boolean b) {
            if (this.mFullSpanItems != null) {
                for (int size = this.mFullSpanItems.size(), i = 0; i < size; ++i) {
                    final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                    if (fullSpanItem.mPosition >= n2) {
                        return null;
                    }
                    if (fullSpanItem.mPosition >= n) {
                        if (n3 != 0 && fullSpanItem.mGapDir != n3) {
                            if (!b) {
                                continue;
                            }
                            if (!fullSpanItem.mHasUnwantedGapAfter) {
                                continue;
                            }
                        }
                        return fullSpanItem;
                    }
                }
                return null;
            }
            return null;
        }
        
        public FullSpanItem getFullSpanItem(final int n) {
            if (this.mFullSpanItems != null) {
                for (int i = this.mFullSpanItems.size() - 1; i >= 0; --i) {
                    final FullSpanItem fullSpanItem = this.mFullSpanItems.get(i);
                    if (fullSpanItem.mPosition == n) {
                        return fullSpanItem;
                    }
                }
                return null;
            }
            return null;
        }
        
        int getSpan(final int n) {
            if (this.mData != null && n < this.mData.length) {
                return this.mData[n];
            }
            return -1;
        }
        
        int invalidateAfter(final int n) {
            if (this.mData == null) {
                return -1;
            }
            if (n >= this.mData.length) {
                return -1;
            }
            final int invalidateFullSpansAfter = this.invalidateFullSpansAfter(n);
            if (invalidateFullSpansAfter != -1) {
                Arrays.fill(this.mData, n, invalidateFullSpansAfter + 1, -1);
                return invalidateFullSpansAfter + 1;
            }
            Arrays.fill(this.mData, n, this.mData.length, -1);
            return this.mData.length;
        }
        
        void offsetForAddition(final int fromIndex, final int n) {
            if (this.mData != null && fromIndex < this.mData.length) {
                this.ensureSize(fromIndex + n);
                System.arraycopy(this.mData, fromIndex, this.mData, fromIndex + n, this.mData.length - fromIndex - n);
                Arrays.fill(this.mData, fromIndex, fromIndex + n, -1);
                this.offsetFullSpansForAddition(fromIndex, n);
            }
        }
        
        void offsetForRemoval(final int n, final int n2) {
            if (this.mData != null && n < this.mData.length) {
                this.ensureSize(n + n2);
                System.arraycopy(this.mData, n + n2, this.mData, n, this.mData.length - n - n2);
                Arrays.fill(this.mData, this.mData.length - n2, this.mData.length, -1);
                this.offsetFullSpansForRemoval(n, n2);
            }
        }
        
        void setSpan(final int n, final Span span) {
            this.ensureSize(n);
            this.mData[n] = span.mIndex;
        }
        
        int sizeForPosition(final int n) {
            int i;
            for (i = this.mData.length; i <= n; i *= 2) {}
            return i;
        }
        
        static class FullSpanItem implements Parcelable
        {
            public static final Parcelable$Creator<FullSpanItem> CREATOR;
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;
            
            static {
                CREATOR = (Parcelable$Creator)new Parcelable$Creator<FullSpanItem>() {
                    public FullSpanItem createFromParcel(final Parcel parcel) {
                        return new FullSpanItem(parcel);
                    }
                    
                    public FullSpanItem[] newArray(final int n) {
                        return new FullSpanItem[n];
                    }
                };
            }
            
            FullSpanItem() {
            }
            
            FullSpanItem(final Parcel parcel) {
                boolean mHasUnwantedGapAfter = false;
                this.mPosition = parcel.readInt();
                this.mGapDir = parcel.readInt();
                if (parcel.readInt() == 1) {
                    mHasUnwantedGapAfter = true;
                }
                this.mHasUnwantedGapAfter = mHasUnwantedGapAfter;
                final int int1 = parcel.readInt();
                if (int1 > 0) {
                    parcel.readIntArray(this.mGapPerSpan = new int[int1]);
                }
            }
            
            public int describeContents() {
                return 0;
            }
            
            int getGapForSpan(int n) {
                if (this.mGapPerSpan != null) {
                    n = this.mGapPerSpan[n];
                }
                else {
                    n = 0;
                }
                return n;
            }
            
            @Override
            public String toString() {
                return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
            }
            
            public void writeToParcel(final Parcel parcel, int n) {
                parcel.writeInt(this.mPosition);
                parcel.writeInt(this.mGapDir);
                if (!this.mHasUnwantedGapAfter) {
                    n = 0;
                }
                else {
                    n = 1;
                }
                parcel.writeInt(n);
                if (this.mGapPerSpan != null && this.mGapPerSpan.length > 0) {
                    parcel.writeInt(this.mGapPerSpan.length);
                    parcel.writeIntArray(this.mGapPerSpan);
                }
                else {
                    parcel.writeInt(0);
                }
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;
        
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
            final boolean b = false;
            this.mAnchorPosition = parcel.readInt();
            this.mVisibleAnchorPosition = parcel.readInt();
            this.mSpanOffsetsSize = parcel.readInt();
            if (this.mSpanOffsetsSize > 0) {
                parcel.readIntArray(this.mSpanOffsets = new int[this.mSpanOffsetsSize]);
            }
            this.mSpanLookupSize = parcel.readInt();
            if (this.mSpanLookupSize > 0) {
                parcel.readIntArray(this.mSpanLookup = new int[this.mSpanLookupSize]);
            }
            this.mReverseLayout = (parcel.readInt() == 1);
            this.mAnchorLayoutFromEnd = (parcel.readInt() == 1);
            this.mLastLayoutRTL = (parcel.readInt() == 1 || b);
            this.mFullSpanItems = parcel.readArrayList(FullSpanItem.class.getClassLoader());
        }
        
        public SavedState(final SavedState savedState) {
            this.mSpanOffsetsSize = savedState.mSpanOffsetsSize;
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mVisibleAnchorPosition = savedState.mVisibleAnchorPosition;
            this.mSpanOffsets = savedState.mSpanOffsets;
            this.mSpanLookupSize = savedState.mSpanLookupSize;
            this.mSpanLookup = savedState.mSpanLookup;
            this.mReverseLayout = savedState.mReverseLayout;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = savedState.mLastLayoutRTL;
            this.mFullSpanItems = savedState.mFullSpanItems;
        }
        
        public int describeContents() {
            return 0;
        }
        
        void invalidateAnchorPositionInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mAnchorPosition = -1;
            this.mVisibleAnchorPosition = -1;
        }
        
        void invalidateSpanInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mSpanLookupSize = 0;
            this.mSpanLookup = null;
            this.mFullSpanItems = null;
        }
        
        public void writeToParcel(final Parcel parcel, int n) {
            final int n2 = 0;
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mVisibleAnchorPosition);
            parcel.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                parcel.writeIntArray(this.mSpanOffsets);
            }
            parcel.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                parcel.writeIntArray(this.mSpanLookup);
            }
            if (!this.mReverseLayout) {
                n = 0;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
            if (!this.mAnchorLayoutFromEnd) {
                n = 0;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
            if (!this.mLastLayoutRTL) {
                n = n2;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
            parcel.writeList((List)this.mFullSpanItems);
        }
    }
    
    class Span
    {
        static final int INVALID_LINE = Integer.MIN_VALUE;
        int mCachedEnd;
        int mCachedStart;
        int mDeletedSize;
        final int mIndex;
        ArrayList<View> mViews;
        
        Span(final int mIndex) {
            this.mViews = new ArrayList<View>();
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
            this.mDeletedSize = 0;
            this.mIndex = mIndex;
        }
        
        void appendToSpan(final View e) {
            final LayoutParams layoutParams = this.getLayoutParams(e);
            layoutParams.mSpan = this;
            this.mViews.add(e);
            this.mCachedEnd = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(e);
            }
        }
        
        void cacheReferenceLineAndClear(final boolean b, final int n) {
            int n2;
            if (!b) {
                n2 = this.getStartLine(Integer.MIN_VALUE);
            }
            else {
                n2 = this.getEndLine(Integer.MIN_VALUE);
            }
            this.clear();
            if (n2 == Integer.MIN_VALUE) {
                return;
            }
            if ((!b || n2 >= StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) && (b || n2 <= StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())) {
                if (n != Integer.MIN_VALUE) {
                    n2 += n;
                }
                this.mCachedEnd = n2;
                this.mCachedStart = n2;
            }
        }
        
        void calculateCachedEnd() {
            final View view = this.mViews.get(this.mViews.size() - 1);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
            if (layoutParams.mFullSpan) {
                final FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == 1) {
                    this.mCachedEnd += fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }
        
        void calculateCachedStart() {
            final View view = this.mViews.get(0);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
            if (layoutParams.mFullSpan) {
                final FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(((RecyclerView.LayoutParams)layoutParams).getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == -1) {
                    this.mCachedStart -= fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }
        
        void clear() {
            this.mViews.clear();
            this.invalidateCache();
            this.mDeletedSize = 0;
        }
        
        public int findFirstCompletelyVisibleItemPosition() {
            int n;
            if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOneVisibleChild(0, this.mViews.size(), true);
            }
            else {
                n = this.findOneVisibleChild(this.mViews.size() - 1, -1, true);
            }
            return n;
        }
        
        public int findFirstPartiallyVisibleItemPosition() {
            int n;
            if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOnePartiallyVisibleChild(0, this.mViews.size(), true);
            }
            else {
                n = this.findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
            }
            return n;
        }
        
        public int findFirstVisibleItemPosition() {
            int n;
            if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOneVisibleChild(0, this.mViews.size(), false);
            }
            else {
                n = this.findOneVisibleChild(this.mViews.size() - 1, -1, false);
            }
            return n;
        }
        
        public int findLastCompletelyVisibleItemPosition() {
            int n;
            if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOneVisibleChild(this.mViews.size() - 1, -1, true);
            }
            else {
                n = this.findOneVisibleChild(0, this.mViews.size(), true);
            }
            return n;
        }
        
        public int findLastPartiallyVisibleItemPosition() {
            int n;
            if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
            }
            else {
                n = this.findOnePartiallyVisibleChild(0, this.mViews.size(), true);
            }
            return n;
        }
        
        public int findLastVisibleItemPosition() {
            int n;
            if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                n = this.findOneVisibleChild(this.mViews.size() - 1, -1, false);
            }
            else {
                n = this.findOneVisibleChild(0, this.mViews.size(), false);
            }
            return n;
        }
        
        int findOnePartiallyOrCompletelyVisibleChild(int i, final int n, final boolean b, final boolean b2, final boolean b3) {
            final int startAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            final int endAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            int n2;
            if (n <= i) {
                n2 = -1;
            }
            else {
                n2 = 1;
            }
        Label_0101_Outer:
            while (i != n) {
                final View view = this.mViews.get(i);
                final int decoratedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
                final int decoratedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
                while (true) {
                    Label_0136: {
                        if (!b3) {
                            if (decoratedStart < endAfterPadding) {
                                break Label_0136;
                            }
                        }
                        else if (decoratedStart <= endAfterPadding) {
                            break Label_0136;
                        }
                        final int n3 = 0;
                        while (true) {
                            Label_0149: {
                                if (!b3) {
                                    if (decoratedEnd > startAfterPadding) {
                                        break Label_0149;
                                    }
                                }
                                else if (decoratedEnd >= startAfterPadding) {
                                    break Label_0149;
                                }
                                final boolean b4 = false;
                                if (n3 != 0 && b4) {
                                    if (b && b2) {
                                        if (decoratedStart >= startAfterPadding && decoratedEnd <= endAfterPadding) {
                                            return ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view);
                                        }
                                    }
                                    else {
                                        if (b2) {
                                            return ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view);
                                        }
                                        if (decoratedStart < startAfterPadding || decoratedEnd > endAfterPadding) {
                                            return ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view);
                                        }
                                    }
                                }
                                i += n2;
                                continue Label_0101_Outer;
                            }
                            final boolean b4 = true;
                            continue;
                        }
                    }
                    final int n3 = 1;
                    continue;
                }
            }
            return -1;
        }
        
        int findOnePartiallyVisibleChild(final int n, final int n2, final boolean b) {
            return this.findOnePartiallyOrCompletelyVisibleChild(n, n2, false, false, b);
        }
        
        int findOneVisibleChild(final int n, final int n2, final boolean b) {
            return this.findOnePartiallyOrCompletelyVisibleChild(n, n2, b, true, false);
        }
        
        public int getDeletedSize() {
            return this.mDeletedSize;
        }
        
        int getEndLine() {
            if (this.mCachedEnd == Integer.MIN_VALUE) {
                this.calculateCachedEnd();
                return this.mCachedEnd;
            }
            return this.mCachedEnd;
        }
        
        int getEndLine(final int n) {
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                return this.mCachedEnd;
            }
            if (this.mViews.size() != 0) {
                this.calculateCachedEnd();
                return this.mCachedEnd;
            }
            return n;
        }
        
        public View getFocusableViewAfter(final int n, int i) {
            final View view = null;
            View view2 = null;
            if (i != -1) {
                View view3;
                for (i = this.mViews.size() - 1; i >= 0; --i, view2 = view3) {
                    view3 = this.mViews.get(i);
                    if (StaggeredGridLayoutManager.this.mReverseLayout && ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view3) >= n) {
                        break;
                    }
                    if (!StaggeredGridLayoutManager.this.mReverseLayout && ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view3) <= n) {
                        break;
                    }
                    if (!view3.hasFocusable()) {
                        break;
                    }
                }
            }
            else {
                final int size = this.mViews.size();
                i = 0;
                view2 = view;
                while (i < size) {
                    final View view4 = this.mViews.get(i);
                    if (StaggeredGridLayoutManager.this.mReverseLayout && ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view4) <= n) {
                        break;
                    }
                    if ((!StaggeredGridLayoutManager.this.mReverseLayout && ((RecyclerView.LayoutManager)StaggeredGridLayoutManager.this).getPosition(view4) >= n) || !view4.hasFocusable()) {
                        break;
                    }
                    ++i;
                    view2 = view4;
                }
            }
            return view2;
        }
        
        LayoutParams getLayoutParams(final View view) {
            return (LayoutParams)view.getLayoutParams();
        }
        
        int getStartLine() {
            if (this.mCachedStart == Integer.MIN_VALUE) {
                this.calculateCachedStart();
                return this.mCachedStart;
            }
            return this.mCachedStart;
        }
        
        int getStartLine(final int n) {
            if (this.mCachedStart != Integer.MIN_VALUE) {
                return this.mCachedStart;
            }
            if (this.mViews.size() != 0) {
                this.calculateCachedStart();
                return this.mCachedStart;
            }
            return n;
        }
        
        void invalidateCache() {
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
        }
        
        void onOffset(final int n) {
            if (this.mCachedStart != Integer.MIN_VALUE) {
                this.mCachedStart += n;
            }
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                this.mCachedEnd += n;
            }
        }
        
        void popEnd() {
            final int size = this.mViews.size();
            final View view = this.mViews.remove(size - 1);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            layoutParams.mSpan = null;
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            if (size == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            this.mCachedEnd = Integer.MIN_VALUE;
        }
        
        void popStart() {
            final View view = this.mViews.remove(0);
            final LayoutParams layoutParams = this.getLayoutParams(view);
            layoutParams.mSpan = null;
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            this.mCachedStart = Integer.MIN_VALUE;
        }
        
        void prependToSpan(final View element) {
            final LayoutParams layoutParams = this.getLayoutParams(element);
            layoutParams.mSpan = this;
            this.mViews.add(0, element);
            this.mCachedStart = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (((RecyclerView.LayoutParams)layoutParams).isItemRemoved() || ((RecyclerView.LayoutParams)layoutParams).isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(element);
            }
        }
        
        void setLine(final int n) {
            this.mCachedStart = n;
            this.mCachedEnd = n;
        }
    }
}
