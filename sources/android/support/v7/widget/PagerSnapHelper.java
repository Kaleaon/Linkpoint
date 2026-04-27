package android.support.v7.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class PagerSnapHelper extends SnapHelper {
    private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
    @Nullable
    private OrientationHelper mHorizontalHelper;
    @Nullable
    private OrientationHelper mVerticalHelper;

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View view, OrientationHelper orientationHelper) {
        return ((orientationHelper.getDecoratedMeasurement(view) / 2) + orientationHelper.getDecoratedStart(view)) - (!layoutManager.getClipToPadding() ? orientationHelper.getEnd() / 2 : orientationHelper.getStartAfterPadding() + (orientationHelper.getTotalSpace() / 2));
    }

    @Nullable
    private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper) {
        View view;
        View view2 = null;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        int end = !layoutManager.getClipToPadding() ? orientationHelper.getEnd() / 2 : orientationHelper.getStartAfterPadding() + (orientationHelper.getTotalSpace() / 2);
        int i = 0;
        int i2 = Integer.MAX_VALUE;
        while (i < childCount) {
            View childAt = layoutManager.getChildAt(i);
            int abs = Math.abs((orientationHelper.getDecoratedStart(childAt) + (orientationHelper.getDecoratedMeasurement(childAt) / 2)) - end);
            if (abs >= i2) {
                view = view2;
            } else {
                i2 = abs;
                view = childAt;
            }
            i++;
            view2 = view;
        }
        return view2;
    }

    @Nullable
    private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper) {
        View view;
        View view2 = null;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        int i = 0;
        int i2 = Integer.MAX_VALUE;
        while (i < childCount) {
            View childAt = layoutManager.getChildAt(i);
            int decoratedStart = orientationHelper.getDecoratedStart(childAt);
            if (decoratedStart >= i2) {
                view = view2;
            } else {
                i2 = decoratedStart;
                view = childAt;
            }
            i++;
            view2 = view;
        }
        return view2;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (this.mHorizontalHelper == null || this.mHorizontalHelper.mLayoutManager != layoutManager) {
            this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return this.mHorizontalHelper;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (this.mVerticalHelper == null || this.mVerticalHelper.mLayoutManager != layoutManager) {
            this.mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mVerticalHelper;
    }

    @Nullable
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View view) {
        int[] iArr = new int[2];
        if (!layoutManager.canScrollHorizontally()) {
            iArr[0] = 0;
        } else {
            iArr[0] = distanceToCenter(layoutManager, view, getHorizontalHelper(layoutManager));
        }
        if (!layoutManager.canScrollVertically()) {
            iArr[1] = 0;
        } else {
            iArr[1] = distanceToCenter(layoutManager, view, getVerticalHelper(layoutManager));
        }
        return iArr;
    }

    /* access modifiers changed from: protected */
    public LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return new LinearSmoothScroller(this.mRecyclerView.getContext()) {
                /* access modifiers changed from: protected */
                public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return 100.0f / ((float) displayMetrics.densityDpi);
                }

                /* access modifiers changed from: protected */
                public int calculateTimeForScrolling(int i) {
                    return Math.min(100, super.calculateTimeForScrolling(i));
                }

                /* access modifiers changed from: protected */
                public void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                    int[] calculateDistanceToFinalSnap = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), view);
                    int i = calculateDistanceToFinalSnap[0];
                    int i2 = calculateDistanceToFinalSnap[1];
                    int calculateTimeForDeceleration = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(i2)));
                    if (calculateTimeForDeceleration > 0) {
                        action.update(i, i2, calculateTimeForDeceleration, this.mDecelerateInterpolator);
                    }
                }
            };
        }
        return null;
    }

    @Nullable
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager));
        }
        if (!layoutManager.canScrollHorizontally()) {
            return null;
        }
        return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
    }

    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int i, int i2) {
        int position;
        PointF computeScrollVectorForPosition;
        View view = null;
        boolean z = false;
        int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return -1;
        }
        if (layoutManager.canScrollVertically()) {
            view = findStartView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            view = findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }
        if (view == null || (position = layoutManager.getPosition(view)) == -1) {
            return -1;
        }
        boolean z2 = !layoutManager.canScrollHorizontally() ? i2 > 0 : i > 0;
        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) && (computeScrollVectorForPosition = ((RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(itemCount - 1)) != null) {
            if ((computeScrollVectorForPosition.x < 0.0f) || computeScrollVectorForPosition.y < 0.0f) {
                z = true;
            }
        }
        if (!z) {
            if (z2) {
                return position + 1;
            }
        } else if (z2) {
            return position - 1;
        }
        return position;
    }
}
