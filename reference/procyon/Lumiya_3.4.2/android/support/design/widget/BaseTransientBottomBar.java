// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.support.annotation.IntRange;
import android.view.MotionEvent;
import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewParent;
import java.util.ArrayList;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.animation.Animation;
import android.view.animation.Animation$AnimationListener;
import android.view.animation.AnimationUtils;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.design.R;
import android.view.LayoutInflater;
import android.view.View;
import android.support.annotation.NonNull;
import android.os.Message;
import android.os.Handler$Callback;
import android.os.Looper;
import android.os.Build$VERSION;
import android.view.ViewGroup;
import android.content.Context;
import java.util.List;
import android.view.accessibility.AccessibilityManager;
import android.os.Handler;

public abstract class BaseTransientBottomBar<B extends BaseTransientBottomBar<B>>
{
    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    static final int MSG_DISMISS = 1;
    static final int MSG_SHOW = 0;
    private static final boolean USE_OFFSET_API;
    static final Handler sHandler;
    private final AccessibilityManager mAccessibilityManager;
    private List<BaseCallback<B>> mCallbacks;
    private final ContentViewCallback mContentViewCallback;
    private final Context mContext;
    private int mDuration;
    final SnackbarManager.Callback mManagerCallback;
    private final ViewGroup mTargetParent;
    final SnackbarBaseLayout mView;
    
    static {
        USE_OFFSET_API = (Build$VERSION.SDK_INT >= 16 && Build$VERSION.SDK_INT <= 19);
        sHandler = new Handler(Looper.getMainLooper(), (Handler$Callback)new Handler$Callback() {
            public boolean handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        return false;
                    }
                    case 0: {
                        ((BaseTransientBottomBar)message.obj).showView();
                        return true;
                    }
                    case 1: {
                        ((BaseTransientBottomBar)message.obj).hideView(message.arg1);
                        return true;
                    }
                }
            }
        });
    }
    
    protected BaseTransientBottomBar(@NonNull final ViewGroup mTargetParent, @NonNull final View view, @NonNull final ContentViewCallback mContentViewCallback) {
        this.mManagerCallback = new SnackbarManager.Callback() {
            @Override
            public void dismiss(final int n) {
                BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(1, n, 0, (Object)BaseTransientBottomBar.this));
            }
            
            @Override
            public void show() {
                BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(0, (Object)BaseTransientBottomBar.this));
            }
        };
        if (mTargetParent == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null parent");
        }
        if (view == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null content");
        }
        if (mContentViewCallback != null) {
            this.mTargetParent = mTargetParent;
            this.mContentViewCallback = mContentViewCallback;
            ThemeUtils.checkAppCompatTheme(this.mContext = mTargetParent.getContext());
            (this.mView = (SnackbarBaseLayout)LayoutInflater.from(this.mContext).inflate(R.layout.design_layout_snackbar, this.mTargetParent, false)).addView(view);
            ViewCompat.setAccessibilityLiveRegion((View)this.mView, 1);
            ViewCompat.setImportantForAccessibility((View)this.mView, 1);
            ViewCompat.setFitsSystemWindows((View)this.mView, true);
            ViewCompat.setOnApplyWindowInsetsListener((View)this.mView, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat) {
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), windowInsetsCompat.getSystemWindowInsetBottom());
                    return windowInsetsCompat;
                }
            });
            this.mAccessibilityManager = (AccessibilityManager)this.mContext.getSystemService("accessibility");
            return;
        }
        throw new IllegalArgumentException("Transient bottom bar must have non-null callback");
    }
    
    private void animateViewOut(final int n) {
        if (Build$VERSION.SDK_INT < 12) {
            final Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_out);
            loadAnimation.setInterpolator(android.support.design.widget.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            loadAnimation.setDuration(250L);
            loadAnimation.setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
                public void onAnimationEnd(final Animation animation) {
                    BaseTransientBottomBar.this.onViewHidden(n);
                }
                
                public void onAnimationRepeat(final Animation animation) {
                }
                
                public void onAnimationStart(final Animation animation) {
                }
            });
            this.mView.startAnimation(loadAnimation);
        }
        else {
            final ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setIntValues(new int[] { 0, this.mView.getHeight() });
            valueAnimator.setInterpolator((TimeInterpolator)android.support.design.widget.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            valueAnimator.setDuration(250L);
            valueAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    BaseTransientBottomBar.this.onViewHidden(n);
                }
                
                public void onAnimationStart(final Animator animator) {
                    BaseTransientBottomBar.this.mContentViewCallback.animateContentOut(0, 180);
                }
            });
            valueAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                private int mPreviousAnimatedIntValue = 0;
                
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    final int intValue = (int)valueAnimator.getAnimatedValue();
                    if (!BaseTransientBottomBar.USE_OFFSET_API) {
                        BaseTransientBottomBar.this.mView.setTranslationY((float)intValue);
                    }
                    else {
                        ViewCompat.offsetTopAndBottom((View)BaseTransientBottomBar.this.mView, intValue - this.mPreviousAnimatedIntValue);
                    }
                    this.mPreviousAnimatedIntValue = intValue;
                }
            });
            valueAnimator.start();
        }
    }
    
    @NonNull
    public B addCallback(@NonNull final BaseCallback<B> baseCallback) {
        if (baseCallback != null) {
            if (this.mCallbacks == null) {
                this.mCallbacks = new ArrayList<BaseCallback<B>>();
            }
            this.mCallbacks.add(baseCallback);
            return (B)this;
        }
        return (B)this;
    }
    
    void animateViewIn() {
        if (Build$VERSION.SDK_INT < 12) {
            final Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_in);
            loadAnimation.setInterpolator(android.support.design.widget.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            loadAnimation.setDuration(250L);
            loadAnimation.setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
                public void onAnimationEnd(final Animation animation) {
                    BaseTransientBottomBar.this.onViewShown();
                }
                
                public void onAnimationRepeat(final Animation animation) {
                }
                
                public void onAnimationStart(final Animation animation) {
                }
            });
            this.mView.startAnimation(loadAnimation);
        }
        else {
            final int height = this.mView.getHeight();
            if (!BaseTransientBottomBar.USE_OFFSET_API) {
                this.mView.setTranslationY((float)height);
            }
            else {
                ViewCompat.offsetTopAndBottom((View)this.mView, height);
            }
            final ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setIntValues(new int[] { height, 0 });
            valueAnimator.setInterpolator((TimeInterpolator)android.support.design.widget.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            valueAnimator.setDuration(250L);
            valueAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    BaseTransientBottomBar.this.onViewShown();
                }
                
                public void onAnimationStart(final Animator animator) {
                    BaseTransientBottomBar.this.mContentViewCallback.animateContentIn(70, 180);
                }
            });
            valueAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                private int mPreviousAnimatedIntValue = height;
                
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    final int intValue = (int)valueAnimator.getAnimatedValue();
                    if (!BaseTransientBottomBar.USE_OFFSET_API) {
                        BaseTransientBottomBar.this.mView.setTranslationY((float)intValue);
                    }
                    else {
                        ViewCompat.offsetTopAndBottom((View)BaseTransientBottomBar.this.mView, intValue - this.mPreviousAnimatedIntValue);
                    }
                    this.mPreviousAnimatedIntValue = intValue;
                }
            });
            valueAnimator.start();
        }
    }
    
    public void dismiss() {
        this.dispatchDismiss(3);
    }
    
    void dispatchDismiss(final int n) {
        SnackbarManager.getInstance().dismiss(this.mManagerCallback, n);
    }
    
    @NonNull
    public Context getContext() {
        return this.mContext;
    }
    
    public int getDuration() {
        return this.mDuration;
    }
    
    @NonNull
    public View getView() {
        return (View)this.mView;
    }
    
    final void hideView(final int n) {
        if (this.shouldAnimate() && this.mView.getVisibility() == 0) {
            this.animateViewOut(n);
        }
        else {
            this.onViewHidden(n);
        }
    }
    
    public boolean isShown() {
        return SnackbarManager.getInstance().isCurrent(this.mManagerCallback);
    }
    
    public boolean isShownOrQueued() {
        return SnackbarManager.getInstance().isCurrentOrNext(this.mManagerCallback);
    }
    
    void onViewHidden(final int n) {
        SnackbarManager.getInstance().onDismissed(this.mManagerCallback);
        if (this.mCallbacks != null) {
            for (int i = this.mCallbacks.size() - 1; i >= 0; --i) {
                this.mCallbacks.get(i).onDismissed((Object)this, n);
            }
        }
        if (Build$VERSION.SDK_INT < 11) {
            this.mView.setVisibility(8);
        }
        final ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup)parent).removeView((View)this.mView);
        }
    }
    
    void onViewShown() {
        SnackbarManager.getInstance().onShown(this.mManagerCallback);
        if (this.mCallbacks != null) {
            for (int i = this.mCallbacks.size() - 1; i >= 0; --i) {
                this.mCallbacks.get(i).onShown((Object)this);
            }
        }
    }
    
    @NonNull
    public B removeCallback(@NonNull final BaseCallback<B> baseCallback) {
        if (baseCallback == null) {
            return (B)this;
        }
        if (this.mCallbacks != null) {
            this.mCallbacks.remove(baseCallback);
            return (B)this;
        }
        return (B)this;
    }
    
    @NonNull
    public B setDuration(final int mDuration) {
        this.mDuration = mDuration;
        return (B)this;
    }
    
    boolean shouldAnimate() {
        boolean b = false;
        if (!this.mAccessibilityManager.isEnabled()) {
            b = true;
        }
        return b;
    }
    
    public void show() {
        SnackbarManager.getInstance().show(this.mDuration, this.mManagerCallback);
    }
    
    final void showView() {
        if (this.mView.getParent() == null) {
            final ViewGroup$LayoutParams layoutParams = this.mView.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                final CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams)layoutParams;
                final Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(0);
                behavior.setListener((SwipeDismissBehavior.OnDismissListener)new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(final View view) {
                        view.setVisibility(8);
                        BaseTransientBottomBar.this.dispatchDismiss(0);
                    }
                    
                    @Override
                    public void onDragStateChanged(final int n) {
                        switch (n) {
                            case 1:
                            case 2: {
                                SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                                break;
                            }
                            case 0: {
                                SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                                break;
                            }
                        }
                    }
                });
                layoutParams2.setBehavior(behavior);
                layoutParams2.insetEdge = 80;
            }
            this.mTargetParent.addView((View)this.mView);
        }
        this.mView.setOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(final View view) {
            }
            
            @Override
            public void onViewDetachedFromWindow(final View view) {
                if (BaseTransientBottomBar.this.isShownOrQueued()) {
                    BaseTransientBottomBar.sHandler.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            BaseTransientBottomBar.this.onViewHidden(3);
                        }
                    });
                }
            }
        });
        if (!ViewCompat.isLaidOut((View)this.mView)) {
            this.mView.setOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4) {
                    BaseTransientBottomBar.this.mView.setOnLayoutChangeListener(null);
                    if (!BaseTransientBottomBar.this.shouldAnimate()) {
                        BaseTransientBottomBar.this.onViewShown();
                    }
                    else {
                        BaseTransientBottomBar.this.animateViewIn();
                    }
                }
            });
        }
        else if (!this.shouldAnimate()) {
            this.onViewShown();
        }
        else {
            this.animateViewIn();
        }
    }
    
    public abstract static class BaseCallback<B>
    {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        
        public void onDismissed(final B b, final int n) {
        }
        
        public void onShown(final B b) {
        }
        
        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        public @interface DismissEvent {
        }
    }
    
    final class Behavior extends SwipeDismissBehavior<SnackbarBaseLayout>
    {
        @Override
        public boolean canSwipeDismissView(final View view) {
            return view instanceof SnackbarBaseLayout;
        }
        
        @Override
        public boolean onInterceptTouchEvent(final CoordinatorLayout coordinatorLayout, final SnackbarBaseLayout snackbarBaseLayout, final MotionEvent motionEvent) {
            switch (motionEvent.getActionMasked()) {
                case 0: {
                    if (coordinatorLayout.isPointInChildBounds((View)snackbarBaseLayout, (int)motionEvent.getX(), (int)motionEvent.getY())) {
                        SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                        break;
                    }
                    break;
                }
                case 1:
                case 3: {
                    SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                    break;
                }
            }
            return super.onInterceptTouchEvent(coordinatorLayout, snackbarBaseLayout, motionEvent);
        }
    }
    
    public interface ContentViewCallback
    {
        void animateContentIn(final int p0, final int p1);
        
        void animateContentOut(final int p0, final int p1);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = 1L)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface Duration {
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    interface OnAttachStateChangeListener
    {
        void onViewAttachedToWindow(final View p0);
        
        void onViewDetachedFromWindow(final View p0);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    interface OnLayoutChangeListener
    {
        void onLayoutChange(final View p0, final int p1, final int p2, final int p3, final int p4);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    static class SnackbarBaseLayout extends FrameLayout
    {
        private OnAttachStateChangeListener mOnAttachStateChangeListener;
        private OnLayoutChangeListener mOnLayoutChangeListener;
        
        SnackbarBaseLayout(final Context context) {
            this(context, null);
        }
        
        SnackbarBaseLayout(final Context context, final AttributeSet set) {
            super(context, set);
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.SnackbarLayout);
            if (obtainStyledAttributes.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation((View)this, (float)obtainStyledAttributes.getDimensionPixelSize(R.styleable.SnackbarLayout_elevation, 0));
            }
            obtainStyledAttributes.recycle();
            this.setClickable(true);
        }
        
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewAttachedToWindow((View)this);
            }
            ViewCompat.requestApplyInsets((View)this);
        }
        
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewDetachedFromWindow((View)this);
            }
        }
        
        protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
            super.onLayout(b, n, n2, n3, n4);
            if (this.mOnLayoutChangeListener != null) {
                this.mOnLayoutChangeListener.onLayoutChange((View)this, n, n2, n3, n4);
            }
        }
        
        void setOnAttachStateChangeListener(final OnAttachStateChangeListener mOnAttachStateChangeListener) {
            this.mOnAttachStateChangeListener = mOnAttachStateChangeListener;
        }
        
        void setOnLayoutChangeListener(final OnLayoutChangeListener mOnLayoutChangeListener) {
            this.mOnLayoutChangeListener = mOnLayoutChangeListener;
        }
    }
}
