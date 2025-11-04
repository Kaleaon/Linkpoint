// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.view.MotionEvent;
import android.view.ViewGroup$LayoutParams;
import android.widget.RelativeLayout$LayoutParams;
import android.widget.TextView;
import android.os.Build$VERSION;
import android.widget.ImageView;
import android.view.View$OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation$AnimationListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.content.Context;
import android.view.OrientationEventListener;
import android.widget.ImageButton;
import android.graphics.drawable.AnimationDrawable;
import android.view.View$OnTouchListener;
import android.widget.FrameLayout;

public class TransitionView extends FrameLayout implements View$OnTouchListener
{
    public static final int ALREADY_LANDSCAPE_LEFT_TRANSITION_DELAY_MS = 2000;
    private static final int LANDSCAPE_TOLERANCE_DEGREES = 5;
    private static final int PORTRAIT_TOLERANCE_DEGREES = 45;
    public static final int TRANSITION_ANIMATION_DURATION_MS = 500;
    public static final int TRANSITION_BACKGROUND_COLOR = -12232092;
    private static final int VIEW_CORRECTION_ROTATION_DEGREES = 90;
    private AnimationDrawable animationDrawable;
    private ImageButton backButton;
    private Runnable backButtonRunnable;
    private int orientation;
    private OrientationEventListener orientationEventListener;
    private boolean rotationChecked;
    private TransitionListener transitionListener;
    private boolean useCustomTransitionLayout;
    private String viewerName;
    
    public TransitionView(final Context context) {
        super(context);
        this.orientation = -1;
        this.setOnTouchListener((View$OnTouchListener)this);
        this.setBackground((Drawable)new ColorDrawable(-12232092));
        this.inflateContentView(R.layout.transition_view);
        super.setVisibility(8);
    }
    
    private void fadeOutAndRemove(final boolean b) {
        this.stopOrientationMonitor();
        final Animation animation = this.getAnimation();
        if (animation != null) {
            if (b || animation.getStartOffset() == 0L) {
                return;
            }
            animation.setAnimationListener((Animation$AnimationListener)null);
            this.clearAnimation();
        }
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        ((Animation)alphaAnimation).setInterpolator((Interpolator)new AccelerateInterpolator());
        ((Animation)alphaAnimation).setRepeatCount(0);
        ((Animation)alphaAnimation).setDuration(500L);
        if (b) {
            ((Animation)alphaAnimation).setStartOffset(2000L);
        }
        ((Animation)alphaAnimation).setAnimationListener((Animation$AnimationListener)new Animation$AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                TransitionView.this.setVisibility(8);
                ((ViewGroup)TransitionView.this.getParent()).removeView((View)TransitionView.this);
                if (TransitionView.this.animationDrawable != null) {
                    TransitionView.this.animationDrawable.stop();
                    TransitionView.this.animationDrawable = null;
                }
                if (TransitionView.this.transitionListener != null) {
                    TransitionView.this.transitionListener.onTransitionDone();
                }
            }
            
            public void onAnimationRepeat(final Animation animation) {
            }
            
            public void onAnimationStart(final Animation animation) {
            }
        });
        this.startAnimation((Animation)alphaAnimation);
    }
    
    private void inflateContentView(final int n) {
        this.removeAllViews();
        LayoutInflater.from(this.getContext()).inflate(n, (ViewGroup)this, true);
        this.findViewById(R.id.transition_switch_action).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                UiUtils.launchOrInstallCardboard(TransitionView.this.getContext());
                if (TransitionView.this.transitionListener != null) {
                    TransitionView.this.transitionListener.onSwitchViewer();
                }
            }
        });
        ((ImageView)this.findViewById(R.id.transition_icon)).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                TransitionView.this.fadeOutAndRemove(false);
            }
        });
        this.updateBackButtonVisibility();
        if (!this.useCustomTransitionLayout && this.getResources().getConfiguration().orientation == 2) {
            this.findViewById(R.id.transition_bottom_frame).setVisibility(8);
        }
    }
    
    private static boolean isLandscapeLeft(final int n) {
        return Math.abs(n - 270) < 5;
    }
    
    private static boolean isLandscapeRight(final int n) {
        return Math.abs(n - 90) < 5;
    }
    
    private static boolean isPortrait(final int n) {
        return Math.abs(n - 180) > 135;
    }
    
    private void rotateViewIfNeeded() {
        if (this.getWidth() > 0 && this.getHeight() > 0 && this.orientation != -1 && this.orientationEventListener != null && !this.rotationChecked) {
            boolean b;
            if (this.getWidth() >= this.getHeight()) {
                b = false;
            }
            else {
                b = true;
            }
            final boolean portrait = isPortrait(this.orientation);
            if (b != portrait) {
                final View viewById = this.findViewById(R.id.transition_frame);
                final int width = viewById.getWidth();
                final int height = viewById.getHeight();
                if (Build$VERSION.SDK_INT >= 17 && this.getLayoutDirection() == 1) {
                    viewById.setPivotX(height - viewById.getPivotX());
                    viewById.setPivotY(width - viewById.getPivotY());
                }
                if (!b) {
                    viewById.setRotation(-90.0f);
                }
                else {
                    viewById.setRotation(90.0f);
                }
                viewById.setTranslationX((float)((width - height) / 2));
                viewById.setTranslationY((float)((height - width) / 2));
                final ViewGroup$LayoutParams layoutParams = viewById.getLayoutParams();
                layoutParams.height = width;
                layoutParams.width = height;
                viewById.requestLayout();
            }
            if (portrait) {
                this.findViewById(R.id.transition_bottom_frame).setVisibility(0);
            }
            else {
                this.findViewById(R.id.transition_bottom_frame).setVisibility(8);
                if (this.useCustomTransitionLayout) {
                    final TextView textView = (TextView)this.findViewById(R.id.transition_text);
                    if (textView != null) {
                        textView.setMaxWidth(textView.getMaxWidth() * 2);
                    }
                    final View viewById2 = this.findViewById(R.id.transition_icon);
                    if (viewById2 != null) {
                        final RelativeLayout$LayoutParams relativeLayout$LayoutParams = (RelativeLayout$LayoutParams)viewById2.getLayoutParams();
                        relativeLayout$LayoutParams.setMargins(relativeLayout$LayoutParams.topMargin * -1, 0, 0, 0);
                        viewById2.requestLayout();
                    }
                }
            }
            this.rotationChecked = true;
            if (isLandscapeLeft(this.orientation)) {
                this.fadeOutAndRemove(true);
            }
        }
    }
    
    private void startOrientationMonitor() {
        if (this.orientationEventListener == null) {
            (this.orientationEventListener = new OrientationEventListener(this.getContext()) {
                public void onOrientationChanged(final int n) {
                    TransitionView.this.orientation = n;
                    if (!TransitionView.this.rotationChecked) {
                        TransitionView.this.rotateViewIfNeeded();
                        return;
                    }
                    if (!isLandscapeLeft(n)) {
                        isLandscapeRight(n);
                        return;
                    }
                    TransitionView.this.fadeOutAndRemove(false);
                }
            }).enable();
        }
    }
    
    private void stopOrientationMonitor() {
        if (this.orientationEventListener != null) {
            this.orientation = -1;
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }
    
    private void updateBackButtonVisibility() {
        this.backButton = (ImageButton)((ViewGroup)this.findViewById(R.id.transition_frame)).findViewById(R.id.back_button);
        if (this.backButtonRunnable != null) {
            this.backButton.setTag((Object)this.backButtonRunnable);
            this.backButton.setVisibility(0);
            this.backButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    TransitionView.this.backButtonRunnable.run();
                }
            });
            return;
        }
        this.backButton.setVisibility(8);
        this.backButton.setTag((Object)null);
        this.backButton.setOnClickListener((View$OnClickListener)null);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.orientationEventListener != null) {
            this.orientationEventListener.enable();
        }
        this.rotateViewIfNeeded();
    }
    
    protected void onDetachedFromWindow() {
        if (this.orientationEventListener != null) {
            this.orientation = -1;
            this.orientationEventListener.disable();
        }
        super.onDetachedFromWindow();
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return true;
    }
    
    public void setBackButtonListener(final Runnable backButtonRunnable) {
        this.backButtonRunnable = backButtonRunnable;
        this.updateBackButtonVisibility();
    }
    
    public void setCustomTransitionLayout(final int n, final int n2) {
        this.useCustomTransitionLayout = true;
        this.inflateContentView(n);
        this.setBackground((Drawable)new ColorDrawable(n2));
        this.setViewerName(this.viewerName);
        final Drawable drawable = ((ImageView)this.findViewById(R.id.transition_icon)).getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            (this.animationDrawable = (AnimationDrawable)drawable).start();
        }
    }
    
    public void setTransitionListener(final TransitionListener transitionListener) {
        this.transitionListener = transitionListener;
    }
    
    public void setViewerName(final String viewerName) {
        this.viewerName = viewerName;
        final TextView textView = (TextView)this.findViewById(R.id.transition_text);
        if (viewerName == null) {
            textView.setText((CharSequence)this.getContext().getString(R.string.place_your_phone_into_cardboard));
            return;
        }
        textView.setText((CharSequence)this.getContext().getString(R.string.place_your_viewer_into_viewer_format, new Object[] { viewerName }));
    }
    
    public void setVisibility(final int visibility) {
        final int visibility2 = this.getVisibility();
        super.setVisibility(visibility);
        if (visibility2 != visibility) {
            if (visibility == 0) {
                this.startOrientationMonitor();
                return;
            }
            this.stopOrientationMonitor();
        }
    }
    
    public interface TransitionListener
    {
        void onSwitchViewer();
        
        void onTransitionDone();
    }
}
