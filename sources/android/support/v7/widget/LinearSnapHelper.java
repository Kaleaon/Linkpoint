package android.support.v7.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LinearSnapHelper extends SnapHelper {
    private static final float INVALID_DISTANCE = 1.0f;
    @Nullable
    private OrientationHelper mHorizontalHelper;
    @Nullable
    private OrientationHelper mVerticalHelper;

    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper) {
        int max;
        int i;
        View view;
        View view2;
        View view3 = null;
        int i2 = Integer.MAX_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return 1.0f;
        }
        int i3 = 0;
        View view4 = null;
        int i4 = Integer.MIN_VALUE;
        while (i3 < childCount) {
            View childAt = layoutManager.getChildAt(i3);
            int position = layoutManager.getPosition(childAt);
            if (position != -1) {
                if (position < i2) {
                    i2 = position;
                    view4 = childAt;
                }
                if (position <= i4) {
                    i = i2;
                    view = view3;
                    view2 = view4;
                } else {
                    i4 = position;
                    view2 = view4;
                    i = i2;
                    view = childAt;
                }
            } else {
                i = i2;
                view = view3;
                view2 = view4;
            }
            i3++;
            view4 = view2;
            view3 = view;
            i2 = i;
        }
        if (view4 == null || view3 == null || (max = Math.max(orientationHelper.getDecoratedEnd(view4), orientationHelper.getDecoratedEnd(view3)) - Math.min(orientationHelper.getDecoratedStart(view4), orientationHelper.getDecoratedStart(view3))) == 0) {
            return 1.0f;
        }
        return (((float) max) * 1.0f) / ((float) ((i4 - i2) + 1));
    }

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View view, OrientationHelper orientationHelper) {
        return ((orientationHelper.getDecoratedMeasurement(view) / 2) + orientationHelper.getDecoratedStart(view)) - (!layoutManager.getClipToPadding() ? orientationHelper.getEnd() / 2 : orientationHelper.getStartAfterPadding() + (orientationHelper.getTotalSpace() / 2));
    }

    private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper, int i, int i2) {
        int[] calculateScrollDistance = calculateScrollDistance(i, i2);
        float computeDistancePerChild = computeDistancePerChild(layoutManager, orientationHelper);
        if (computeDistancePerChild <= 0.0f) {
            return 0;
        }
        return Math.round(((float) (Math.abs(calculateScrollDistance[0]) <= Math.abs(calculateScrollDistance[1]) ? calculateScrollDistance[1] : calculateScrollDistance[0])) / computeDistancePerChild);
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
        int itemCount;
        View findSnapView;
        int position;
        PointF computeScrollVectorForPosition;
        int estimateNextPositionDiffForFling;
        int estimateNextPositionDiffForFling2;
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) || (itemCount = layoutManager.getItemCount()) == 0 || (findSnapView = findSnapView(layoutManager)) == null || (position = layoutManager.getPosition(findSnapView)) == -1 || (computeScrollVectorForPosition = ((RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(itemCount - 1)) == null) {
            return -1;
        }
        if (!layoutManager.canScrollHorizontally()) {
            estimateNextPositionDiffForFling = 0;
        } else {
            estimateNextPositionDiffForFling = estimateNextPositionDiffForFling(layoutManager, getHorizontalHelper(layoutManager), i, 0);
            if (computeScrollVectorForPosition.x < 0.0f) {
                estimateNextPositionDiffForFling = -estimateNextPositionDiffForFling;
            }
        }
        if (!layoutManager.canScrollVertically()) {
            estimateNextPositionDiffForFling2 = 0;
        } else {
            estimateNextPositionDiffForFling2 = estimateNextPositionDiffForFling(layoutManager, getVerticalHelper(layoutManager), 0, i2);
            if (computeScrollVectorForPosition.y < 0.0f) {
                estimateNextPositionDiffForFling2 = -estimateNextPositionDiffForFling2;
            }
        }
        if (layoutManager.canScrollVertically()) {
            estimateNextPositionDiffForFling = estimateNextPositionDiffForFling2;
        }
        if (estimateNextPositionDiffForFling == 0) {
            return -1;
        }
        int i3 = estimateNextPositionDiffForFling + position;
        if (i3 < 0) {
            i3 = 0;
        }
        return i3 < itemCount ? i3 : itemCount - 1;
    }
}
