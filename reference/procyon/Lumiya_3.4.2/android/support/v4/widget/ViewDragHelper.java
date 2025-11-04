// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.view.MotionEvent;
import android.util.Log;
import android.support.v4.view.ViewCompat;
import java.util.Arrays;
import android.view.ViewConfiguration;
import android.content.Context;
import android.view.VelocityTracker;
import android.widget.OverScroller;
import android.view.ViewGroup;
import android.view.View;
import android.view.animation.Interpolator;

public class ViewDragHelper
{
    private static final int BASE_SETTLE_DURATION = 256;
    public static final int DIRECTION_ALL = 3;
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    public static final int EDGE_ALL = 15;
    public static final int EDGE_BOTTOM = 8;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_RIGHT = 2;
    private static final int EDGE_SIZE = 20;
    public static final int EDGE_TOP = 4;
    public static final int INVALID_POINTER = -1;
    private static final int MAX_SETTLE_DURATION = 600;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "ViewDragHelper";
    private static final Interpolator sInterpolator;
    private int mActivePointerId;
    private final Callback mCallback;
    private View mCapturedView;
    private int mDragState;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mEdgeSize;
    private int[] mInitialEdgesTouched;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private float mMaxVelocity;
    private float mMinVelocity;
    private final ViewGroup mParentView;
    private int mPointersDown;
    private boolean mReleaseInProgress;
    private OverScroller mScroller;
    private final Runnable mSetIdleRunnable;
    private int mTouchSlop;
    private int mTrackingEdges;
    private VelocityTracker mVelocityTracker;
    
    static {
        sInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(float n) {
                --n;
                return n * (n * n * n * n) + 1.0f;
            }
        };
    }
    
    private ViewDragHelper(final Context context, final ViewGroup mParentView, final Callback mCallback) {
        this.mActivePointerId = -1;
        this.mSetIdleRunnable = new Runnable() {
            @Override
            public void run() {
                ViewDragHelper.this.setDragState(0);
            }
        };
        if (mParentView == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        }
        if (mCallback != null) {
            this.mParentView = mParentView;
            this.mCallback = mCallback;
            final ViewConfiguration value = ViewConfiguration.get(context);
            this.mEdgeSize = (int)(context.getResources().getDisplayMetrics().density * 20.0f + 0.5f);
            this.mTouchSlop = value.getScaledTouchSlop();
            this.mMaxVelocity = (float)value.getScaledMaximumFlingVelocity();
            this.mMinVelocity = (float)value.getScaledMinimumFlingVelocity();
            this.mScroller = new OverScroller(context, ViewDragHelper.sInterpolator);
            return;
        }
        throw new IllegalArgumentException("Callback may not be null");
    }
    
    private boolean checkNewEdgeDrag(float abs, float abs2, final int n, final int n2) {
        boolean b = false;
        abs = Math.abs(abs);
        abs2 = Math.abs(abs2);
        if ((this.mInitialEdgesTouched[n] & n2) != n2 || (this.mTrackingEdges & n2) == 0x0 || (this.mEdgeDragsLocked[n] & n2) == n2 || (this.mEdgeDragsInProgress[n] & n2) == n2 || (abs <= this.mTouchSlop && abs2 <= this.mTouchSlop)) {
            return false;
        }
        if (abs >= abs2 * 0.5f || !this.mCallback.onEdgeLock(n2)) {
            if ((this.mEdgeDragsInProgress[n] & n2) == 0x0 && abs > this.mTouchSlop) {
                b = true;
            }
            return b;
        }
        final int[] mEdgeDragsLocked = this.mEdgeDragsLocked;
        mEdgeDragsLocked[n] |= n2;
        return false;
    }
    
    private boolean checkTouchSlop(final View view, final float a, final float a2) {
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = true;
        if (view == null) {
            return false;
        }
        boolean b4;
        if (this.mCallback.getViewHorizontalDragRange(view) <= 0) {
            b4 = false;
        }
        else {
            b4 = true;
        }
        boolean b5;
        if (this.mCallback.getViewVerticalDragRange(view) <= 0) {
            b5 = false;
        }
        else {
            b5 = true;
        }
        if (b4 && b5) {
            if (a * a + a2 * a2 <= this.mTouchSlop * this.mTouchSlop) {
                b3 = false;
            }
            return b3;
        }
        if (!b4) {
            return b5 && Math.abs(a2) > this.mTouchSlop && b2;
        }
        return Math.abs(a) > this.mTouchSlop && b;
    }
    
    private float clampMag(final float a, final float n, float n2) {
        final float abs = Math.abs(a);
        if (abs < n) {
            return 0.0f;
        }
        if (abs > n2) {
            if (a <= 0.0f) {
                n2 = -n2;
            }
            return n2;
        }
        return a;
    }
    
    private int clampMag(final int a, int n, final int n2) {
        final int abs = Math.abs(a);
        if (abs < n) {
            return 0;
        }
        if (abs <= n2) {
            return a;
        }
        n = n2;
        if (a <= 0) {
            n = -n2;
        }
        return n;
    }
    
    private void clearMotionHistory() {
        if (this.mInitialMotionX != null) {
            Arrays.fill(this.mInitialMotionX, 0.0f);
            Arrays.fill(this.mInitialMotionY, 0.0f);
            Arrays.fill(this.mLastMotionX, 0.0f);
            Arrays.fill(this.mLastMotionY, 0.0f);
            Arrays.fill(this.mInitialEdgesTouched, 0);
            Arrays.fill(this.mEdgeDragsInProgress, 0);
            Arrays.fill(this.mEdgeDragsLocked, 0);
            this.mPointersDown = 0;
        }
    }
    
    private void clearMotionHistory(final int n) {
        if (this.mInitialMotionX != null && this.isPointerDown(n)) {
            this.mInitialMotionX[n] = 0.0f;
            this.mInitialMotionY[n] = 0.0f;
            this.mLastMotionX[n] = 0.0f;
            this.mLastMotionY[n] = 0.0f;
            this.mInitialEdgesTouched[n] = 0;
            this.mEdgeDragsInProgress[n] = 0;
            this.mEdgeDragsLocked[n] = 0;
            this.mPointersDown &= ~(1 << n);
        }
    }
    
    private int computeAxisDuration(int a, int abs, final int n) {
        if (a != 0) {
            final int width = this.mParentView.getWidth();
            final int n2 = width / 2;
            final float min = Math.min(1.0f, Math.abs(a) / (float)width);
            final float n3 = (float)n2;
            final float n4 = (float)n2;
            final float distanceInfluenceForSnapDuration = this.distanceInfluenceForSnapDuration(min);
            abs = Math.abs(abs);
            if (abs <= 0) {
                a = (int)((Math.abs(a) / (float)n + 1.0f) * 256.0f);
            }
            else {
                a = Math.round(Math.abs((distanceInfluenceForSnapDuration * n4 + n3) / abs) * 1000.0f) * 4;
            }
            return Math.min(a, 600);
        }
        return 0;
    }
    
    private int computeSettleDuration(final View view, int computeAxisDuration, int computeAxisDuration2, int clampMag, int abs) {
        final int clampMag2 = this.clampMag(clampMag, (int)this.mMinVelocity, (int)this.mMaxVelocity);
        clampMag = this.clampMag(abs, (int)this.mMinVelocity, (int)this.mMaxVelocity);
        final int abs2 = Math.abs(computeAxisDuration);
        final int abs3 = Math.abs(computeAxisDuration2);
        abs = Math.abs(clampMag2);
        final int abs4 = Math.abs(clampMag);
        final int n = abs + abs4;
        final int n2 = abs2 + abs3;
        float n3;
        if (clampMag2 == 0) {
            n3 = abs2 / (float)n2;
        }
        else {
            n3 = abs / (float)n;
        }
        float n4;
        if (clampMag == 0) {
            n4 = abs3 / (float)n2;
        }
        else {
            n4 = abs4 / (float)n;
        }
        computeAxisDuration = this.computeAxisDuration(computeAxisDuration, clampMag2, this.mCallback.getViewHorizontalDragRange(view));
        computeAxisDuration2 = this.computeAxisDuration(computeAxisDuration2, clampMag, this.mCallback.getViewVerticalDragRange(view));
        return (int)(n3 * computeAxisDuration + n4 * computeAxisDuration2);
    }
    
    public static ViewDragHelper create(final ViewGroup viewGroup, final float n, final Callback callback) {
        final ViewDragHelper create = create(viewGroup, callback);
        create.mTouchSlop *= (int)(1.0f / n);
        return create;
    }
    
    public static ViewDragHelper create(final ViewGroup viewGroup, final Callback callback) {
        return new ViewDragHelper(viewGroup.getContext(), viewGroup, callback);
    }
    
    private void dispatchViewReleased(final float n, final float n2) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased(this.mCapturedView, n, n2);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            this.setDragState(0);
        }
    }
    
    private float distanceInfluenceForSnapDuration(final float n) {
        return (float)Math.sin((n - 0.5f) * 0.47123894f);
    }
    
    private void dragTo(int clampViewPositionHorizontal, int clampViewPositionVertical, final int n, final int n2) {
        final int left = this.mCapturedView.getLeft();
        final int top = this.mCapturedView.getTop();
        if (n != 0) {
            clampViewPositionHorizontal = this.mCallback.clampViewPositionHorizontal(this.mCapturedView, clampViewPositionHorizontal, n);
            ViewCompat.offsetLeftAndRight(this.mCapturedView, clampViewPositionHorizontal - left);
        }
        if (n2 != 0) {
            clampViewPositionVertical = this.mCallback.clampViewPositionVertical(this.mCapturedView, clampViewPositionVertical, n2);
            ViewCompat.offsetTopAndBottom(this.mCapturedView, clampViewPositionVertical - top);
        }
        if (n != 0 || n2 != 0) {
            this.mCallback.onViewPositionChanged(this.mCapturedView, clampViewPositionHorizontal, clampViewPositionVertical, clampViewPositionHorizontal - left, clampViewPositionVertical - top);
        }
    }
    
    private void ensureMotionHistorySizeForId(final int n) {
        if (this.mInitialMotionX == null || this.mInitialMotionX.length <= n) {
            final float[] mInitialMotionX = new float[n + 1];
            final float[] mInitialMotionY = new float[n + 1];
            final float[] mLastMotionX = new float[n + 1];
            final float[] mLastMotionY = new float[n + 1];
            final int[] mInitialEdgesTouched = new int[n + 1];
            final int[] mEdgeDragsInProgress = new int[n + 1];
            final int[] mEdgeDragsLocked = new int[n + 1];
            if (this.mInitialMotionX != null) {
                System.arraycopy(this.mInitialMotionX, 0, mInitialMotionX, 0, this.mInitialMotionX.length);
                System.arraycopy(this.mInitialMotionY, 0, mInitialMotionY, 0, this.mInitialMotionY.length);
                System.arraycopy(this.mLastMotionX, 0, mLastMotionX, 0, this.mLastMotionX.length);
                System.arraycopy(this.mLastMotionY, 0, mLastMotionY, 0, this.mLastMotionY.length);
                System.arraycopy(this.mInitialEdgesTouched, 0, mInitialEdgesTouched, 0, this.mInitialEdgesTouched.length);
                System.arraycopy(this.mEdgeDragsInProgress, 0, mEdgeDragsInProgress, 0, this.mEdgeDragsInProgress.length);
                System.arraycopy(this.mEdgeDragsLocked, 0, mEdgeDragsLocked, 0, this.mEdgeDragsLocked.length);
            }
            this.mInitialMotionX = mInitialMotionX;
            this.mInitialMotionY = mInitialMotionY;
            this.mLastMotionX = mLastMotionX;
            this.mLastMotionY = mLastMotionY;
            this.mInitialEdgesTouched = mInitialEdgesTouched;
            this.mEdgeDragsInProgress = mEdgeDragsInProgress;
            this.mEdgeDragsLocked = mEdgeDragsLocked;
        }
    }
    
    private boolean forceSettleCapturedViewAt(int n, int n2, int computeSettleDuration, final int n3) {
        final int left = this.mCapturedView.getLeft();
        final int top = this.mCapturedView.getTop();
        n -= left;
        n2 -= top;
        if (n == 0 && n2 == 0) {
            this.mScroller.abortAnimation();
            this.setDragState(0);
            return false;
        }
        computeSettleDuration = this.computeSettleDuration(this.mCapturedView, n, n2, computeSettleDuration, n3);
        this.mScroller.startScroll(left, top, n, n2, computeSettleDuration);
        this.setDragState(2);
        return true;
    }
    
    private int getEdgesTouched(final int n, final int n2) {
        int n3 = 0;
        if (n < this.mParentView.getLeft() + this.mEdgeSize) {
            n3 = 1;
        }
        if (n2 < this.mParentView.getTop() + this.mEdgeSize) {
            n3 |= 0x4;
        }
        if (n > this.mParentView.getRight() - this.mEdgeSize) {
            n3 |= 0x2;
        }
        if (n2 > this.mParentView.getBottom() - this.mEdgeSize) {
            n3 |= 0x8;
        }
        return n3;
    }
    
    private boolean isValidPointerForActionMove(final int i) {
        if (this.isPointerDown(i)) {
            return true;
        }
        Log.e("ViewDragHelper", "Ignoring pointerId=" + i + " because ACTION_DOWN was not received " + "for this pointer before ACTION_MOVE. It likely happened because " + " ViewDragHelper did not receive all the events in the event stream.");
        return false;
    }
    
    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        this.dispatchViewReleased(this.clampMag(this.mVelocityTracker.getXVelocity(this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), this.clampMag(this.mVelocityTracker.getYVelocity(this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
    }
    
    private void reportNewEdgeDrags(final float n, final float n2, final int n3) {
        int n4 = 0;
        if (this.checkNewEdgeDrag(n, n2, n3, 1)) {
            n4 = 1;
        }
        if (this.checkNewEdgeDrag(n2, n, n3, 4)) {
            n4 |= 0x4;
        }
        if (this.checkNewEdgeDrag(n, n2, n3, 2)) {
            n4 |= 0x2;
        }
        if (this.checkNewEdgeDrag(n2, n, n3, 8)) {
            n4 |= 0x8;
        }
        if (n4 != 0) {
            final int[] mEdgeDragsInProgress = this.mEdgeDragsInProgress;
            mEdgeDragsInProgress[n3] |= n4;
            this.mCallback.onEdgeDragStarted(n4, n3);
        }
    }
    
    private void saveInitialMotion(final float n, final float n2, final int n3) {
        this.ensureMotionHistorySizeForId(n3);
        this.mInitialMotionX[n3] = (this.mLastMotionX[n3] = n);
        this.mInitialMotionY[n3] = (this.mLastMotionY[n3] = n2);
        this.mInitialEdgesTouched[n3] = this.getEdgesTouched((int)n, (int)n2);
        this.mPointersDown |= 1 << n3;
    }
    
    private void saveLastMotion(final MotionEvent motionEvent) {
        for (int i = 0; i < motionEvent.getPointerCount(); ++i) {
            final int pointerId = motionEvent.getPointerId(i);
            if (this.isValidPointerForActionMove(pointerId)) {
                final float x = motionEvent.getX(i);
                final float y = motionEvent.getY(i);
                this.mLastMotionX[pointerId] = x;
                this.mLastMotionY[pointerId] = y;
            }
        }
    }
    
    public void abort() {
        this.cancel();
        if (this.mDragState == 2) {
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            this.mScroller.abortAnimation();
            final int currX2 = this.mScroller.getCurrX();
            final int currY2 = this.mScroller.getCurrY();
            this.mCallback.onViewPositionChanged(this.mCapturedView, currX2, currY2, currX2 - currX, currY2 - currY);
        }
        this.setDragState(0);
    }
    
    protected boolean canScroll(final View view, final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            final int scrollX = view.getScrollX();
            final int scrollY = view.getScrollY();
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                if (n3 + scrollX >= child.getLeft() && n3 + scrollX < child.getRight() && n4 + scrollY >= child.getTop() && n4 + scrollY < child.getBottom() && this.canScroll(child, true, n, n2, n3 + scrollX - child.getLeft(), n4 + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return b && (view.canScrollHorizontally(-n) || view.canScrollVertically(-n2));
    }
    
    public void cancel() {
        this.mActivePointerId = -1;
        this.clearMotionHistory();
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    public void captureChildView(final View mCapturedView, final int mActivePointerId) {
        if (mCapturedView.getParent() == this.mParentView) {
            this.mCapturedView = mCapturedView;
            this.mActivePointerId = mActivePointerId;
            this.mCallback.onViewCaptured(mCapturedView, mActivePointerId);
            this.setDragState(1);
            return;
        }
        throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
    }
    
    public boolean checkTouchSlop(final int n) {
        for (int length = this.mInitialMotionX.length, i = 0; i < length; ++i) {
            if (this.checkTouchSlop(n, i)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkTouchSlop(int n, final int n2) {
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = true;
        if (!this.isPointerDown(n2)) {
            return false;
        }
        boolean b4;
        if ((n & 0x1) != 0x1) {
            b4 = false;
        }
        else {
            b4 = true;
        }
        if ((n & 0x2) != 0x2) {
            n = 0;
        }
        else {
            n = 1;
        }
        final float a = this.mLastMotionX[n2] - this.mInitialMotionX[n2];
        final float a2 = this.mLastMotionY[n2] - this.mInitialMotionY[n2];
        if (b4 && n != 0) {
            if (a * a + a2 * a2 <= this.mTouchSlop * this.mTouchSlop) {
                b3 = false;
            }
            return b3;
        }
        if (!b4) {
            return n != 0 && Math.abs(a2) > this.mTouchSlop && b2;
        }
        return Math.abs(a) > this.mTouchSlop && b;
    }
    
    public boolean continueSettling(final boolean b) {
        if (this.mDragState == 2) {
            int computeScrollOffset = this.mScroller.computeScrollOffset() ? 1 : 0;
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            final int n = currX - this.mCapturedView.getLeft();
            final int n2 = currY - this.mCapturedView.getTop();
            if (n != 0) {
                ViewCompat.offsetLeftAndRight(this.mCapturedView, n);
            }
            if (n2 != 0) {
                ViewCompat.offsetTopAndBottom(this.mCapturedView, n2);
            }
            if (n != 0 || n2 != 0) {
                this.mCallback.onViewPositionChanged(this.mCapturedView, currX, currY, n, n2);
            }
            if (computeScrollOffset != 0 && currX == this.mScroller.getFinalX() && currY == this.mScroller.getFinalY()) {
                this.mScroller.abortAnimation();
                computeScrollOffset = 0;
            }
            if (computeScrollOffset == 0) {
                if (!b) {
                    this.setDragState(0);
                }
                else {
                    this.mParentView.post(this.mSetIdleRunnable);
                }
            }
        }
        return this.mDragState == 2;
    }
    
    public View findTopChildUnder(final int n, final int n2) {
        int childCount = this.mParentView.getChildCount();
        while (true) {
            final int n3 = childCount - 1;
            if (n3 < 0) {
                return null;
            }
            final View child = this.mParentView.getChildAt(this.mCallback.getOrderedChildIndex(n3));
            childCount = n3;
            if (n < child.getLeft()) {
                continue;
            }
            childCount = n3;
            if (n >= child.getRight()) {
                continue;
            }
            childCount = n3;
            if (n2 < child.getTop()) {
                continue;
            }
            childCount = n3;
            if (n2 < child.getBottom()) {
                return child;
            }
        }
    }
    
    public void flingCapturedView(final int n, final int n2, final int n3, final int n4) {
        if (this.mReleaseInProgress) {
            this.mScroller.fling(this.mCapturedView.getLeft(), this.mCapturedView.getTop(), (int)this.mVelocityTracker.getXVelocity(this.mActivePointerId), (int)this.mVelocityTracker.getYVelocity(this.mActivePointerId), n, n3, n2, n4);
            this.setDragState(2);
            return;
        }
        throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
    }
    
    public int getActivePointerId() {
        return this.mActivePointerId;
    }
    
    public View getCapturedView() {
        return this.mCapturedView;
    }
    
    public int getEdgeSize() {
        return this.mEdgeSize;
    }
    
    public float getMinVelocity() {
        return this.mMinVelocity;
    }
    
    public int getTouchSlop() {
        return this.mTouchSlop;
    }
    
    public int getViewDragState() {
        return this.mDragState;
    }
    
    public boolean isCapturedViewUnder(final int n, final int n2) {
        return this.isViewUnder(this.mCapturedView, n, n2);
    }
    
    public boolean isEdgeTouched(final int n) {
        for (int length = this.mInitialEdgesTouched.length, i = 0; i < length; ++i) {
            if (this.isEdgeTouched(n, i)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEdgeTouched(final int n, final int n2) {
        boolean b = false;
        if (this.isPointerDown(n2) && (this.mInitialEdgesTouched[n2] & n) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public boolean isPointerDown(final int n) {
        boolean b = false;
        if ((this.mPointersDown & 1 << n) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public boolean isViewUnder(final View view, final int n, final int n2) {
        final boolean b = false;
        if (view != null) {
            boolean b2;
            if (n < view.getLeft()) {
                b2 = b;
            }
            else {
                b2 = b;
                if (n < view.getRight()) {
                    b2 = b;
                    if (n2 >= view.getTop()) {
                        b2 = b;
                        if (n2 < view.getBottom()) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
        return false;
    }
    
    public void processTouchEvent(final MotionEvent motionEvent) {
        int i = 0;
        final int n = 0;
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        switch (actionMasked) {
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                final int pointerId = motionEvent.getPointerId(0);
                final View topChildUnder = this.findTopChildUnder((int)x, (int)y);
                this.saveInitialMotion(x, y, pointerId);
                this.tryCaptureViewForDrag(topChildUnder, pointerId);
                final int n2 = this.mInitialEdgesTouched[pointerId];
                if ((this.mTrackingEdges & n2) != 0x0) {
                    this.mCallback.onEdgeTouched(n2 & this.mTrackingEdges, pointerId);
                    break;
                }
                break;
            }
            case 5: {
                final int pointerId2 = motionEvent.getPointerId(actionIndex);
                final float x2 = motionEvent.getX(actionIndex);
                final float y2 = motionEvent.getY(actionIndex);
                this.saveInitialMotion(x2, y2, pointerId2);
                if (this.mDragState != 0) {
                    if (this.isCapturedViewUnder((int)x2, (int)y2)) {
                        this.tryCaptureViewForDrag(this.mCapturedView, pointerId2);
                        break;
                    }
                    break;
                }
                else {
                    this.tryCaptureViewForDrag(this.findTopChildUnder((int)x2, (int)y2), pointerId2);
                    final int n3 = this.mInitialEdgesTouched[pointerId2];
                    if ((this.mTrackingEdges & n3) != 0x0) {
                        this.mCallback.onEdgeTouched(n3 & this.mTrackingEdges, pointerId2);
                        break;
                    }
                    break;
                }
                break;
            }
            case 2: {
                if (this.mDragState != 1) {
                    for (int pointerCount = motionEvent.getPointerCount(), j = n; j < pointerCount; ++j) {
                        final int pointerId3 = motionEvent.getPointerId(j);
                        if (this.isValidPointerForActionMove(pointerId3)) {
                            final float x3 = motionEvent.getX(j);
                            final float y3 = motionEvent.getY(j);
                            final float n4 = x3 - this.mInitialMotionX[pointerId3];
                            final float n5 = y3 - this.mInitialMotionY[pointerId3];
                            this.reportNewEdgeDrags(n4, n5, pointerId3);
                            if (this.mDragState == 1) {
                                break;
                            }
                            final View topChildUnder2 = this.findTopChildUnder((int)x3, (int)y3);
                            if (this.checkTouchSlop(topChildUnder2, n4, n5)) {
                                if (this.tryCaptureViewForDrag(topChildUnder2, pointerId3)) {
                                    break;
                                }
                            }
                        }
                    }
                    this.saveLastMotion(motionEvent);
                    break;
                }
                if (this.isValidPointerForActionMove(this.mActivePointerId)) {
                    final int pointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    final float x4 = motionEvent.getX(pointerIndex);
                    final float y4 = motionEvent.getY(pointerIndex);
                    final int n6 = (int)(x4 - this.mLastMotionX[this.mActivePointerId]);
                    final int n7 = (int)(y4 - this.mLastMotionY[this.mActivePointerId]);
                    this.dragTo(this.mCapturedView.getLeft() + n6, this.mCapturedView.getTop() + n7, n6, n7);
                    this.saveLastMotion(motionEvent);
                    break;
                }
                break;
            }
            case 6: {
                final int pointerId4 = motionEvent.getPointerId(actionIndex);
                Label_0540: {
                    if (this.mDragState == 1 && pointerId4 == this.mActivePointerId) {
                        while (true) {
                            while (i < motionEvent.getPointerCount()) {
                                final int pointerId5 = motionEvent.getPointerId(i);
                                if (pointerId5 != this.mActivePointerId && this.findTopChildUnder((int)motionEvent.getX(i), (int)motionEvent.getY(i)) == this.mCapturedView && this.tryCaptureViewForDrag(this.mCapturedView, pointerId5)) {
                                    final int mActivePointerId = this.mActivePointerId;
                                    if (mActivePointerId == -1) {
                                        this.releaseViewForPointerUp();
                                    }
                                    break Label_0540;
                                }
                                else {
                                    ++i;
                                }
                            }
                            final int mActivePointerId = -1;
                            continue;
                        }
                    }
                }
                this.clearMotionHistory(pointerId4);
                break;
            }
            case 1: {
                if (this.mDragState == 1) {
                    this.releaseViewForPointerUp();
                }
                this.cancel();
                break;
            }
            case 3: {
                if (this.mDragState == 1) {
                    this.dispatchViewReleased(0.0f, 0.0f);
                }
                this.cancel();
                break;
            }
        }
    }
    
    void setDragState(final int mDragState) {
        this.mParentView.removeCallbacks(this.mSetIdleRunnable);
        if (this.mDragState != mDragState) {
            this.mDragState = mDragState;
            this.mCallback.onViewDragStateChanged(mDragState);
            if (this.mDragState == 0) {
                this.mCapturedView = null;
            }
        }
    }
    
    public void setEdgeTrackingEnabled(final int mTrackingEdges) {
        this.mTrackingEdges = mTrackingEdges;
    }
    
    public void setMinVelocity(final float mMinVelocity) {
        this.mMinVelocity = mMinVelocity;
    }
    
    public boolean settleCapturedViewAt(final int n, final int n2) {
        if (this.mReleaseInProgress) {
            return this.forceSettleCapturedViewAt(n, n2, (int)this.mVelocityTracker.getXVelocity(this.mActivePointerId), (int)this.mVelocityTracker.getYVelocity(this.mActivePointerId));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }
    
    public boolean shouldInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        switch (actionMasked) {
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                final int pointerId = motionEvent.getPointerId(0);
                this.saveInitialMotion(x, y, pointerId);
                final View topChildUnder = this.findTopChildUnder((int)x, (int)y);
                if (topChildUnder == this.mCapturedView && this.mDragState == 2) {
                    this.tryCaptureViewForDrag(topChildUnder, pointerId);
                }
                final int n = this.mInitialEdgesTouched[pointerId];
                if ((this.mTrackingEdges & n) != 0x0) {
                    this.mCallback.onEdgeTouched(n & this.mTrackingEdges, pointerId);
                    break;
                }
                break;
            }
            case 5: {
                final int pointerId2 = motionEvent.getPointerId(actionIndex);
                final float x2 = motionEvent.getX(actionIndex);
                final float y2 = motionEvent.getY(actionIndex);
                this.saveInitialMotion(x2, y2, pointerId2);
                if (this.mDragState != 0) {
                    if (this.mDragState != 2) {
                        break;
                    }
                    final View topChildUnder2 = this.findTopChildUnder((int)x2, (int)y2);
                    if (topChildUnder2 == this.mCapturedView) {
                        this.tryCaptureViewForDrag(topChildUnder2, pointerId2);
                        break;
                    }
                    break;
                }
                else {
                    final int n2 = this.mInitialEdgesTouched[pointerId2];
                    if ((this.mTrackingEdges & n2) != 0x0) {
                        this.mCallback.onEdgeTouched(n2 & this.mTrackingEdges, pointerId2);
                        break;
                    }
                    break;
                }
                break;
            }
            case 2: {
                if (this.mInitialMotionX != null && this.mInitialMotionY != null) {
                    for (int pointerCount = motionEvent.getPointerCount(), i = 0; i < pointerCount; ++i) {
                        final int pointerId3 = motionEvent.getPointerId(i);
                        if (this.isValidPointerForActionMove(pointerId3)) {
                            final float x3 = motionEvent.getX(i);
                            final float y3 = motionEvent.getY(i);
                            final float n3 = x3 - this.mInitialMotionX[pointerId3];
                            final float n4 = y3 - this.mInitialMotionY[pointerId3];
                            final View topChildUnder3 = this.findTopChildUnder((int)x3, (int)y3);
                            boolean b;
                            if (topChildUnder3 != null && this.checkTouchSlop(topChildUnder3, n3, n4)) {
                                b = true;
                            }
                            else {
                                b = false;
                            }
                            Label_0419: {
                                if (b) {
                                    final int left = topChildUnder3.getLeft();
                                    final int clampViewPositionHorizontal = this.mCallback.clampViewPositionHorizontal(topChildUnder3, (int)n3 + left, (int)n3);
                                    final int top = topChildUnder3.getTop();
                                    final int clampViewPositionVertical = this.mCallback.clampViewPositionVertical(topChildUnder3, (int)n4 + top, (int)n4);
                                    final int viewHorizontalDragRange = this.mCallback.getViewHorizontalDragRange(topChildUnder3);
                                    final int viewVerticalDragRange = this.mCallback.getViewVerticalDragRange(topChildUnder3);
                                    if (viewHorizontalDragRange != 0) {
                                        if (viewHorizontalDragRange <= 0) {
                                            break Label_0419;
                                        }
                                        if (clampViewPositionHorizontal != left) {
                                            break Label_0419;
                                        }
                                    }
                                    if (viewVerticalDragRange == 0) {
                                        break;
                                    }
                                    if (viewVerticalDragRange > 0) {
                                        if (clampViewPositionVertical == top) {
                                            break;
                                        }
                                    }
                                }
                            }
                            this.reportNewEdgeDrags(n3, n4, pointerId3);
                            if (this.mDragState == 1) {
                                break;
                            }
                            if (b) {
                                if (this.tryCaptureViewForDrag(topChildUnder3, pointerId3)) {
                                    break;
                                }
                            }
                        }
                    }
                    this.saveLastMotion(motionEvent);
                    break;
                }
                break;
            }
            case 6: {
                this.clearMotionHistory(motionEvent.getPointerId(actionIndex));
                break;
            }
            case 1:
            case 3: {
                this.cancel();
                break;
            }
        }
        return this.mDragState == 1;
    }
    
    public boolean smoothSlideViewTo(final View mCapturedView, final int n, final int n2) {
        this.mCapturedView = mCapturedView;
        this.mActivePointerId = -1;
        final boolean forceSettleCapturedView = this.forceSettleCapturedViewAt(n, n2, 0, 0);
        if (!forceSettleCapturedView && this.mDragState == 0 && this.mCapturedView != null) {
            this.mCapturedView = null;
        }
        return forceSettleCapturedView;
    }
    
    boolean tryCaptureViewForDrag(final View view, final int mActivePointerId) {
        if (view == this.mCapturedView && this.mActivePointerId == mActivePointerId) {
            return true;
        }
        if (view != null && this.mCallback.tryCaptureView(view, mActivePointerId)) {
            this.captureChildView(view, this.mActivePointerId = mActivePointerId);
            return true;
        }
        return false;
    }
    
    public abstract static class Callback
    {
        public int clampViewPositionHorizontal(final View view, final int n, final int n2) {
            return 0;
        }
        
        public int clampViewPositionVertical(final View view, final int n, final int n2) {
            return 0;
        }
        
        public int getOrderedChildIndex(final int n) {
            return n;
        }
        
        public int getViewHorizontalDragRange(final View view) {
            return 0;
        }
        
        public int getViewVerticalDragRange(final View view) {
            return 0;
        }
        
        public void onEdgeDragStarted(final int n, final int n2) {
        }
        
        public boolean onEdgeLock(final int n) {
            return false;
        }
        
        public void onEdgeTouched(final int n, final int n2) {
        }
        
        public void onViewCaptured(final View view, final int n) {
        }
        
        public void onViewDragStateChanged(final int n) {
        }
        
        public void onViewPositionChanged(final View view, final int n, final int n2, final int n3, final int n4) {
        }
        
        public void onViewReleased(final View view, final float n, final float n2) {
        }
        
        public abstract boolean tryCaptureView(final View p0, final int p1);
    }
}
