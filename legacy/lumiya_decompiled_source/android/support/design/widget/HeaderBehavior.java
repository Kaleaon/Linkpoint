package android.support.design.widget;

import android.content.Context;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
/* loaded from: classes.dex */
abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId;
    private Runnable mFlingRunnable;
    private boolean mIsBeingDragged;
    private int mLastMotionY;
    OverScroller mScroller;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FlingRunnable implements Runnable {
        private final V mLayout;
        private final CoordinatorLayout mParent;

        FlingRunnable(CoordinatorLayout coordinatorLayout, V v) {
            this.mParent = coordinatorLayout;
            this.mLayout = v;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mLayout == null || HeaderBehavior.this.mScroller == null) {
                return;
            }
            if (!HeaderBehavior.this.mScroller.computeScrollOffset()) {
                HeaderBehavior.this.onFlingFinished(this.mParent, this.mLayout);
                return;
            }
            HeaderBehavior.this.setHeaderTopBottomOffset(this.mParent, this.mLayout, HeaderBehavior.this.mScroller.getCurrY());
            ViewCompat.postOnAnimation(this.mLayout, this);
        }
    }

    public HeaderBehavior() {
        this.mActivePointerId = -1;
        this.mTouchSlop = -1;
    }

    public HeaderBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mActivePointerId = -1;
        this.mTouchSlop = -1;
    }

    private void ensureVelocityTracker() {
        if (this.mVelocityTracker != null) {
            return;
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    boolean canDragView(V v) {
        return false;
    }

    final boolean fling(CoordinatorLayout coordinatorLayout, V v, int i, int i2, float f) {
        if (this.mFlingRunnable != null) {
            v.removeCallbacks(this.mFlingRunnable);
            this.mFlingRunnable = null;
        }
        if (this.mScroller == null) {
            this.mScroller = new OverScroller(v.getContext());
        }
        this.mScroller.fling(0, getTopAndBottomOffset(), 0, Math.round(f), 0, 0, i, i2);
        if (!this.mScroller.computeScrollOffset()) {
            onFlingFinished(coordinatorLayout, v);
            return false;
        }
        this.mFlingRunnable = new FlingRunnable(coordinatorLayout, v);
        ViewCompat.postOnAnimation(v, this.mFlingRunnable);
        return true;
    }

    int getMaxDragOffset(V v) {
        return -v.getHeight();
    }

    int getScrollRangeForDragFling(V v) {
        return v.getHeight();
    }

    int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    void onFlingFinished(CoordinatorLayout coordinatorLayout, V v) {
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        int findPointerIndex;
        if (this.mTouchSlop < 0) {
            this.mTouchSlop = ViewConfiguration.get(coordinatorLayout.getContext()).getScaledTouchSlop();
        }
        if (motionEvent.getAction() == 2 && this.mIsBeingDragged) {
            return true;
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.mIsBeingDragged = false;
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (canDragView(v) && coordinatorLayout.isPointInChildBounds(v, x, y)) {
                    this.mLastMotionY = y;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    ensureVelocityTracker();
                    break;
                }
                break;
            case 1:
            case 3:
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    break;
                }
                break;
            case 2:
                int i = this.mActivePointerId;
                if (i != -1 && (findPointerIndex = motionEvent.findPointerIndex(i)) != -1) {
                    int y2 = (int) motionEvent.getY(findPointerIndex);
                    if (Math.abs(y2 - this.mLastMotionY) > this.mTouchSlop) {
                        this.mIsBeingDragged = true;
                        this.mLastMotionY = y2;
                        break;
                    }
                }
                break;
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.addMovement(motionEvent);
        }
        return this.mIsBeingDragged;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:34:0x008d  */
    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.support.design.widget.CoordinatorLayout r11, V r12, android.view.MotionEvent r13) {
        /*
            r10 = this;
            r9 = 1
            r8 = -1
            r7 = 0
            r5 = 0
            int r0 = r10.mTouchSlop
            if (r0 < 0) goto L14
        L8:
            int r0 = r13.getActionMasked()
            switch(r0) {
                case 0: goto L23;
                case 1: goto L81;
                case 2: goto L46;
                case 3: goto L85;
                default: goto Lf;
            }
        Lf:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            if (r0 != 0) goto Lb6
        L13:
            return r9
        L14:
            android.content.Context r0 = r11.getContext()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r0)
            int r0 = r0.getScaledTouchSlop()
            r10.mTouchSlop = r0
            goto L8
        L23:
            float r0 = r13.getX()
            int r0 = (int) r0
            float r1 = r13.getY()
            int r1 = (int) r1
            boolean r0 = r11.isPointInChildBounds(r12, r0, r1)
            if (r0 != 0) goto L34
        L33:
            return r5
        L34:
            boolean r0 = r10.canDragView(r12)
            if (r0 == 0) goto L33
            r10.mLastMotionY = r1
            int r0 = r13.getPointerId(r5)
            r10.mActivePointerId = r0
            r10.ensureVelocityTracker()
            goto Lf
        L46:
            int r0 = r10.mActivePointerId
            int r0 = r13.findPointerIndex(r0)
            if (r0 == r8) goto L6c
            float r0 = r13.getY(r0)
            int r0 = (int) r0
            int r1 = r10.mLastMotionY
            int r3 = r1 - r0
            boolean r1 = r10.mIsBeingDragged
            if (r1 == 0) goto L6d
        L5b:
            boolean r1 = r10.mIsBeingDragged
            if (r1 == 0) goto Lf
            r10.mLastMotionY = r0
            int r4 = r10.getMaxDragOffset(r12)
            r0 = r10
            r1 = r11
            r2 = r12
            r0.scroll(r1, r2, r3, r4, r5)
            goto Lf
        L6c:
            return r5
        L6d:
            int r1 = java.lang.Math.abs(r3)
            int r2 = r10.mTouchSlop
            if (r1 <= r2) goto L5b
            r10.mIsBeingDragged = r9
            if (r3 > 0) goto L7d
            int r1 = r10.mTouchSlop
            int r3 = r3 + r1
            goto L5b
        L7d:
            int r1 = r10.mTouchSlop
            int r3 = r3 - r1
            goto L5b
        L81:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            if (r0 != 0) goto L96
        L85:
            r10.mIsBeingDragged = r5
            r10.mActivePointerId = r8
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            if (r0 == 0) goto Lf
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r0.recycle()
            r10.mVelocityTracker = r7
            goto Lf
        L96:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r0.addMovement(r13)
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r1 = 1000(0x3e8, float:1.401E-42)
            r0.computeCurrentVelocity(r1)
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            int r1 = r10.mActivePointerId
            float r6 = r0.getYVelocity(r1)
            int r0 = r10.getScrollRangeForDragFling(r12)
            int r4 = -r0
            r1 = r10
            r2 = r11
            r3 = r12
            r1.fling(r2, r3, r4, r5, r6)
            goto L85
        Lb6:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r0.addMovement(r13)
            goto L13
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.HeaderBehavior.onTouchEvent(android.support.design.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int scroll(CoordinatorLayout coordinatorLayout, V v, int i, int i2, int i3) {
        return setHeaderTopBottomOffset(coordinatorLayout, v, getTopBottomOffsetForScrollingSibling() - i, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, V v, int i) {
        return setHeaderTopBottomOffset(coordinatorLayout, v, i, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, V v, int i, int i2, int i3) {
        int clamp;
        int topAndBottomOffset = getTopAndBottomOffset();
        if (i2 != 0 && topAndBottomOffset >= i2 && topAndBottomOffset <= i3 && topAndBottomOffset != (clamp = MathUtils.clamp(i, i2, i3))) {
            setTopAndBottomOffset(clamp);
            return topAndBottomOffset - clamp;
        }
        return 0;
    }
}
