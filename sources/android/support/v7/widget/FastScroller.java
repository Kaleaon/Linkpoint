package android.support.v7.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import com.google.vr.cardboard.TransitionView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@VisibleForTesting
class FastScroller extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_FADING_OUT = 3;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_OUT = 0;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_X = 1;
    private static final int DRAG_Y = 2;
    private static final int[] EMPTY_STATE_SET = new int[0];
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DURATION_MS = 500;
    private static final int[] PRESSED_STATE_SET = {16842919};
    private static final int SCROLLBAR_FULL_OPAQUE = 255;
    private static final int SHOW_DURATION_MS = 500;
    private static final int STATE_DRAGGING = 2;
    private static final int STATE_HIDDEN = 0;
    private static final int STATE_VISIBLE = 1;
    /* access modifiers changed from: private */
    public int mAnimationState = 0;
    private int mDragState = 0;
    private final Runnable mHideRunnable = new Runnable() {
        public void run() {
            FastScroller.this.hide(TransitionView.TRANSITION_ANIMATION_DURATION_MS);
        }
    };
    @VisibleForTesting
    float mHorizontalDragX;
    private final int[] mHorizontalRange = new int[2];
    @VisibleForTesting
    int mHorizontalThumbCenterX;
    private final StateListDrawable mHorizontalThumbDrawable;
    private final int mHorizontalThumbHeight;
    @VisibleForTesting
    int mHorizontalThumbWidth;
    private final Drawable mHorizontalTrackDrawable;
    private final int mHorizontalTrackHeight;
    private final int mMargin;
    private boolean mNeedHorizontalScrollbar = false;
    private boolean mNeedVerticalScrollbar = false;
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            FastScroller.this.updateScrollPosition(recyclerView.computeHorizontalScrollOffset(), recyclerView.computeVerticalScrollOffset());
        }
    };
    private RecyclerView mRecyclerView;
    private int mRecyclerViewHeight = 0;
    private int mRecyclerViewWidth = 0;
    private final int mScrollbarMinimumRange;
    /* access modifiers changed from: private */
    public final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
    private int mState = 0;
    @VisibleForTesting
    float mVerticalDragY;
    private final int[] mVerticalRange = new int[2];
    @VisibleForTesting
    int mVerticalThumbCenterY;
    /* access modifiers changed from: private */
    public final StateListDrawable mVerticalThumbDrawable;
    @VisibleForTesting
    int mVerticalThumbHeight;
    private final int mVerticalThumbWidth;
    /* access modifiers changed from: private */
    public final Drawable mVerticalTrackDrawable;
    private final int mVerticalTrackWidth;

    @Retention(RetentionPolicy.SOURCE)
    private @interface AnimationState {
    }

    private class AnimatorListener extends AnimatorListenerAdapter {
        private boolean mCanceled;

        private AnimatorListener() {
            this.mCanceled = false;
        }

        public void onAnimationCancel(Animator animator) {
            this.mCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mCanceled) {
                this.mCanceled = false;
            } else if (((Float) FastScroller.this.mShowHideAnimator.getAnimatedValue()).floatValue() == 0.0f) {
                int unused = FastScroller.this.mAnimationState = 0;
                FastScroller.this.setState(0);
            } else {
                int unused2 = FastScroller.this.mAnimationState = 2;
                FastScroller.this.requestRedraw();
            }
        }
    }

    private class AnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {
        private AnimatorUpdater() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int floatValue = (int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f);
            FastScroller.this.mVerticalThumbDrawable.setAlpha(floatValue);
            FastScroller.this.mVerticalTrackDrawable.setAlpha(floatValue);
            FastScroller.this.requestRedraw();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface DragState {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    FastScroller(RecyclerView recyclerView, StateListDrawable stateListDrawable, Drawable drawable, StateListDrawable stateListDrawable2, Drawable drawable2, int i, int i2, int i3) {
        this.mVerticalThumbDrawable = stateListDrawable;
        this.mVerticalTrackDrawable = drawable;
        this.mHorizontalThumbDrawable = stateListDrawable2;
        this.mHorizontalTrackDrawable = drawable2;
        this.mVerticalThumbWidth = Math.max(i, stateListDrawable.getIntrinsicWidth());
        this.mVerticalTrackWidth = Math.max(i, drawable.getIntrinsicWidth());
        this.mHorizontalThumbHeight = Math.max(i, stateListDrawable2.getIntrinsicWidth());
        this.mHorizontalTrackHeight = Math.max(i, drawable2.getIntrinsicWidth());
        this.mScrollbarMinimumRange = i2;
        this.mMargin = i3;
        this.mVerticalThumbDrawable.setAlpha(255);
        this.mVerticalTrackDrawable.setAlpha(255);
        this.mShowHideAnimator.addListener(new AnimatorListener());
        this.mShowHideAnimator.addUpdateListener(new AnimatorUpdater());
        attachToRecyclerView(recyclerView);
    }

    private void cancelHide() {
        this.mRecyclerView.removeCallbacks(this.mHideRunnable);
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this);
        this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
        cancelHide();
    }

    private void drawHorizontalScrollbar(Canvas canvas) {
        int i = this.mRecyclerViewHeight - this.mHorizontalThumbHeight;
        int i2 = this.mHorizontalThumbCenterX - (this.mHorizontalThumbWidth / 2);
        this.mHorizontalThumbDrawable.setBounds(0, 0, this.mHorizontalThumbWidth, this.mHorizontalThumbHeight);
        this.mHorizontalTrackDrawable.setBounds(0, 0, this.mRecyclerViewWidth, this.mHorizontalTrackHeight);
        canvas.translate(0.0f, (float) i);
        this.mHorizontalTrackDrawable.draw(canvas);
        canvas.translate((float) i2, 0.0f);
        this.mHorizontalThumbDrawable.draw(canvas);
        canvas.translate((float) (-i2), (float) (-i));
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        int i = this.mRecyclerViewWidth - this.mVerticalThumbWidth;
        int i2 = this.mVerticalThumbCenterY - (this.mVerticalThumbHeight / 2);
        this.mVerticalThumbDrawable.setBounds(0, 0, this.mVerticalThumbWidth, this.mVerticalThumbHeight);
        this.mVerticalTrackDrawable.setBounds(0, 0, this.mVerticalTrackWidth, this.mRecyclerViewHeight);
        if (!isLayoutRTL()) {
            canvas.translate((float) i, 0.0f);
            this.mVerticalTrackDrawable.draw(canvas);
            canvas.translate(0.0f, (float) i2);
            this.mVerticalThumbDrawable.draw(canvas);
            canvas.translate((float) (-i), (float) (-i2));
            return;
        }
        this.mVerticalTrackDrawable.draw(canvas);
        canvas.translate((float) this.mVerticalThumbWidth, (float) i2);
        canvas.scale(-1.0f, 1.0f);
        this.mVerticalThumbDrawable.draw(canvas);
        canvas.scale(1.0f, 1.0f);
        canvas.translate((float) (-this.mVerticalThumbWidth), (float) (-i2));
    }

    private int[] getHorizontalRange() {
        this.mHorizontalRange[0] = this.mMargin;
        this.mHorizontalRange[1] = this.mRecyclerViewWidth - this.mMargin;
        return this.mHorizontalRange;
    }

    private int[] getVerticalRange() {
        this.mVerticalRange[0] = this.mMargin;
        this.mVerticalRange[1] = this.mRecyclerViewHeight - this.mMargin;
        return this.mVerticalRange;
    }

    private void horizontalScrollTo(float f) {
        int[] horizontalRange = getHorizontalRange();
        float max = Math.max((float) horizontalRange[0], Math.min((float) horizontalRange[1], f));
        if (Math.abs(((float) this.mHorizontalThumbCenterX) - max) >= 2.0f) {
            int scrollTo = scrollTo(this.mHorizontalDragX, max, horizontalRange, this.mRecyclerView.computeHorizontalScrollRange(), this.mRecyclerView.computeHorizontalScrollOffset(), this.mRecyclerViewWidth);
            if (scrollTo != 0) {
                this.mRecyclerView.scrollBy(scrollTo, 0);
            }
            this.mHorizontalDragX = max;
        }
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(this.mRecyclerView) == 1;
    }

    /* access modifiers changed from: private */
    public void requestRedraw() {
        this.mRecyclerView.invalidate();
    }

    private void resetHideDelay(int i) {
        cancelHide();
        this.mRecyclerView.postDelayed(this.mHideRunnable, (long) i);
    }

    private int scrollTo(float f, float f2, int[] iArr, int i, int i2, int i3) {
        int i4 = iArr[1] - iArr[0];
        if (i4 == 0) {
            return 0;
        }
        int i5 = i - i3;
        int i6 = (int) (((f2 - f) / ((float) i4)) * ((float) i5));
        int i7 = i2 + i6;
        if (i7 < i5 && i7 >= 0) {
            return i6;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void setState(int i) {
        if (i == 2 && this.mState != 2) {
            this.mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            cancelHide();
        }
        if (i != 0) {
            show();
        } else {
            requestRedraw();
        }
        if (this.mState == 2 && i != 2) {
            this.mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (i == 1) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS);
        }
        this.mState = i;
    }

    private void setupCallbacks() {
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this);
        this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    private void verticalScrollTo(float f) {
        int[] verticalRange = getVerticalRange();
        float max = Math.max((float) verticalRange[0], Math.min((float) verticalRange[1], f));
        if (Math.abs(((float) this.mVerticalThumbCenterY) - max) >= 2.0f) {
            int scrollTo = scrollTo(this.mVerticalDragY, max, verticalRange, this.mRecyclerView.computeVerticalScrollRange(), this.mRecyclerView.computeVerticalScrollOffset(), this.mRecyclerViewHeight);
            if (scrollTo != 0) {
                this.mRecyclerView.scrollBy(0, scrollTo);
            }
            this.mVerticalDragY = max;
        }
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (this.mRecyclerView != recyclerView) {
            if (this.mRecyclerView != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (this.mRecyclerView != null) {
                setupCallbacks();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Drawable getHorizontalThumbDrawable() {
        return this.mHorizontalThumbDrawable;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Drawable getHorizontalTrackDrawable() {
        return this.mHorizontalTrackDrawable;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Drawable getVerticalThumbDrawable() {
        return this.mVerticalThumbDrawable;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Drawable getVerticalTrackDrawable() {
        return this.mVerticalTrackDrawable;
    }

    public void hide() {
        hide(0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void hide(int i) {
        switch (this.mAnimationState) {
            case 1:
                this.mShowHideAnimator.cancel();
                break;
            case 2:
                break;
            default:
                return;
        }
        this.mAnimationState = 3;
        this.mShowHideAnimator.setFloatValues(new float[]{((Float) this.mShowHideAnimator.getAnimatedValue()).floatValue(), 0.0f});
        this.mShowHideAnimator.setDuration((long) i);
        this.mShowHideAnimator.start();
    }

    public boolean isDragging() {
        return this.mState == 2;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isHidden() {
        return this.mState == 0;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isPointInsideHorizontalThumb(float f, float f2) {
        return f2 >= ((float) (this.mRecyclerViewHeight - this.mHorizontalThumbHeight)) && f >= ((float) (this.mHorizontalThumbCenterX - (this.mHorizontalThumbWidth / 2))) && f <= ((float) (this.mHorizontalThumbCenterX + (this.mHorizontalThumbWidth / 2)));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isPointInsideVerticalThumb(float f, float f2) {
        if (!isLayoutRTL()) {
            if (f < ((float) (this.mRecyclerViewWidth - this.mVerticalThumbWidth))) {
                return false;
            }
        } else if (f > ((float) (this.mVerticalThumbWidth / 2))) {
            return false;
        }
        return f2 >= ((float) (this.mVerticalThumbCenterY - (this.mVerticalThumbHeight / 2))) && f2 <= ((float) (this.mVerticalThumbCenterY + (this.mVerticalThumbHeight / 2)));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isVisible() {
        return this.mState == 1;
    }

    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        if (this.mRecyclerViewWidth != this.mRecyclerView.getWidth() || this.mRecyclerViewHeight != this.mRecyclerView.getHeight()) {
            this.mRecyclerViewWidth = this.mRecyclerView.getWidth();
            this.mRecyclerViewHeight = this.mRecyclerView.getHeight();
            setState(0);
        } else if (this.mAnimationState != 0) {
            if (this.mNeedVerticalScrollbar) {
                drawVerticalScrollbar(canvas);
            }
            if (this.mNeedHorizontalScrollbar) {
                drawHorizontalScrollbar(canvas);
            }
        }
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (this.mState != 1) {
            return this.mState == 2;
        }
        boolean isPointInsideVerticalThumb = isPointInsideVerticalThumb(motionEvent.getX(), motionEvent.getY());
        boolean isPointInsideHorizontalThumb = isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
        if (motionEvent.getAction() != 0 || (!isPointInsideVerticalThumb && !isPointInsideHorizontalThumb)) {
            return false;
        }
        if (isPointInsideHorizontalThumb) {
            this.mDragState = 1;
            this.mHorizontalDragX = (float) ((int) motionEvent.getX());
        } else if (isPointInsideVerticalThumb) {
            this.mDragState = 2;
            this.mVerticalDragY = (float) ((int) motionEvent.getY());
        }
        setState(2);
        return true;
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (this.mState == 0) {
            return;
        }
        if (motionEvent.getAction() == 0) {
            boolean isPointInsideVerticalThumb = isPointInsideVerticalThumb(motionEvent.getX(), motionEvent.getY());
            boolean isPointInsideHorizontalThumb = isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
            if (isPointInsideVerticalThumb || isPointInsideHorizontalThumb) {
                if (isPointInsideHorizontalThumb) {
                    this.mDragState = 1;
                    this.mHorizontalDragX = (float) ((int) motionEvent.getX());
                } else if (isPointInsideVerticalThumb) {
                    this.mDragState = 2;
                    this.mVerticalDragY = (float) ((int) motionEvent.getY());
                }
                setState(2);
            }
        } else if (motionEvent.getAction() == 1 && this.mState == 2) {
            this.mVerticalDragY = 0.0f;
            this.mHorizontalDragX = 0.0f;
            setState(1);
            this.mDragState = 0;
        } else if (motionEvent.getAction() == 2 && this.mState == 2) {
            show();
            if (this.mDragState == 1) {
                horizontalScrollTo(motionEvent.getX());
            }
            if (this.mDragState == 2) {
                verticalScrollTo(motionEvent.getY());
            }
        }
    }

    public void show() {
        switch (this.mAnimationState) {
            case 0:
                break;
            case 3:
                this.mShowHideAnimator.cancel();
                break;
            default:
                return;
        }
        this.mAnimationState = 1;
        this.mShowHideAnimator.setFloatValues(new float[]{((Float) this.mShowHideAnimator.getAnimatedValue()).floatValue(), 1.0f});
        this.mShowHideAnimator.setDuration(500);
        this.mShowHideAnimator.setStartDelay(0);
        this.mShowHideAnimator.start();
    }

    /* access modifiers changed from: package-private */
    public void updateScrollPosition(int i, int i2) {
        int computeVerticalScrollRange = this.mRecyclerView.computeVerticalScrollRange();
        int i3 = this.mRecyclerViewHeight;
        this.mNeedVerticalScrollbar = computeVerticalScrollRange - i3 > 0 && this.mRecyclerViewHeight >= this.mScrollbarMinimumRange;
        int computeHorizontalScrollRange = this.mRecyclerView.computeHorizontalScrollRange();
        int i4 = this.mRecyclerViewWidth;
        this.mNeedHorizontalScrollbar = computeHorizontalScrollRange - i4 > 0 && this.mRecyclerViewWidth >= this.mScrollbarMinimumRange;
        if (this.mNeedVerticalScrollbar || this.mNeedHorizontalScrollbar) {
            if (this.mNeedVerticalScrollbar) {
                this.mVerticalThumbCenterY = (int) (((((float) i2) + (((float) i3) / 2.0f)) * ((float) i3)) / ((float) computeVerticalScrollRange));
                this.mVerticalThumbHeight = Math.min(i3, (i3 * i3) / computeVerticalScrollRange);
            }
            if (this.mNeedHorizontalScrollbar) {
                this.mHorizontalThumbCenterX = (int) (((((float) i) + (((float) i4) / 2.0f)) * ((float) i4)) / ((float) computeHorizontalScrollRange));
                this.mHorizontalThumbWidth = Math.min(i4, (i4 * i4) / computeHorizontalScrollRange);
            }
            if (this.mState == 0 || this.mState == 1) {
                setState(1);
            }
        } else if (this.mState != 0) {
            setState(0);
        }
    }
}
