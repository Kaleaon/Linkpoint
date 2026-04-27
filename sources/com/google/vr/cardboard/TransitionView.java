package com.google.vr.cardboard;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TransitionView extends FrameLayout implements View.OnTouchListener {
    public static final int ALREADY_LANDSCAPE_LEFT_TRANSITION_DELAY_MS = 2000;
    private static final int LANDSCAPE_TOLERANCE_DEGREES = 5;
    private static final int PORTRAIT_TOLERANCE_DEGREES = 45;
    public static final int TRANSITION_ANIMATION_DURATION_MS = 500;
    public static final int TRANSITION_BACKGROUND_COLOR = -12232092;
    private static final int VIEW_CORRECTION_ROTATION_DEGREES = 90;
    /* access modifiers changed from: private */
    public AnimationDrawable animationDrawable;
    private ImageButton backButton;
    /* access modifiers changed from: private */
    public Runnable backButtonRunnable;
    /* access modifiers changed from: private */
    public int orientation = -1;
    private OrientationEventListener orientationEventListener;
    /* access modifiers changed from: private */
    public boolean rotationChecked;
    /* access modifiers changed from: private */
    public TransitionListener transitionListener;
    private boolean useCustomTransitionLayout;
    private String viewerName;

    public interface TransitionListener {
        void onSwitchViewer();

        void onTransitionDone();
    }

    public TransitionView(Context context) {
        super(context);
        setOnTouchListener(this);
        setBackground(new ColorDrawable(TRANSITION_BACKGROUND_COLOR));
        inflateContentView(R.layout.transition_view);
        super.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void fadeOutAndRemove(boolean z) {
        stopOrientationMonitor();
        Animation animation = getAnimation();
        if (animation != null) {
            if (!z && animation.getStartOffset() != 0) {
                animation.setAnimationListener((Animation.AnimationListener) null);
                clearAnimation();
            } else {
                return;
            }
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setRepeatCount(0);
        alphaAnimation.setDuration(500);
        if (z) {
            alphaAnimation.setStartOffset(2000);
        }
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                TransitionView.this.setVisibility(8);
                ((ViewGroup) TransitionView.this.getParent()).removeView(TransitionView.this);
                if (TransitionView.this.animationDrawable != null) {
                    TransitionView.this.animationDrawable.stop();
                    AnimationDrawable unused = TransitionView.this.animationDrawable = null;
                }
                if (TransitionView.this.transitionListener != null) {
                    TransitionView.this.transitionListener.onTransitionDone();
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
        startAnimation(alphaAnimation);
    }

    private void inflateContentView(int i) {
        removeAllViews();
        LayoutInflater.from(getContext()).inflate(i, this, true);
        findViewById(R.id.transition_switch_action).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UiUtils.launchOrInstallCardboard(TransitionView.this.getContext());
                if (TransitionView.this.transitionListener != null) {
                    TransitionView.this.transitionListener.onSwitchViewer();
                }
            }
        });
        ((ImageView) findViewById(R.id.transition_icon)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TransitionView.this.fadeOutAndRemove(false);
            }
        });
        updateBackButtonVisibility();
        if (!this.useCustomTransitionLayout && getResources().getConfiguration().orientation == 2) {
            findViewById(R.id.transition_bottom_frame).setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public static boolean isLandscapeLeft(int i) {
        return Math.abs(i + -270) < 5;
    }

    /* access modifiers changed from: private */
    public static boolean isLandscapeRight(int i) {
        return Math.abs(i + -90) < 5;
    }

    private static boolean isPortrait(int i) {
        return Math.abs(i + -180) > 135;
    }

    /* access modifiers changed from: private */
    public void rotateViewIfNeeded() {
        if (getWidth() > 0 && getHeight() > 0 && this.orientation != -1 && this.orientationEventListener != null && !this.rotationChecked) {
            boolean z = getWidth() < getHeight();
            boolean isPortrait = isPortrait(this.orientation);
            if (z != isPortrait) {
                View findViewById = findViewById(R.id.transition_frame);
                int width = findViewById.getWidth();
                int height = findViewById.getHeight();
                if (Build.VERSION.SDK_INT >= 17 && getLayoutDirection() == 1) {
                    findViewById.setPivotX(((float) height) - findViewById.getPivotX());
                    findViewById.setPivotY(((float) width) - findViewById.getPivotY());
                }
                if (!z) {
                    findViewById.setRotation(-90.0f);
                } else {
                    findViewById.setRotation(90.0f);
                }
                findViewById.setTranslationX((float) ((width - height) / 2));
                findViewById.setTranslationY((float) ((height - width) / 2));
                ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
                layoutParams.height = width;
                layoutParams.width = height;
                findViewById.requestLayout();
            }
            if (isPortrait) {
                findViewById(R.id.transition_bottom_frame).setVisibility(0);
            } else {
                findViewById(R.id.transition_bottom_frame).setVisibility(8);
                if (this.useCustomTransitionLayout) {
                    TextView textView = (TextView) findViewById(R.id.transition_text);
                    if (textView != null) {
                        textView.setMaxWidth(textView.getMaxWidth() * 2);
                    }
                    View findViewById2 = findViewById(R.id.transition_icon);
                    if (findViewById2 != null) {
                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) findViewById2.getLayoutParams();
                        layoutParams2.setMargins(layoutParams2.topMargin * -1, 0, 0, 0);
                        findViewById2.requestLayout();
                    }
                }
            }
            this.rotationChecked = true;
            if (isLandscapeLeft(this.orientation)) {
                fadeOutAndRemove(true);
            }
        }
    }

    private void startOrientationMonitor() {
        if (this.orientationEventListener == null) {
            this.orientationEventListener = new OrientationEventListener(getContext()) {
                public void onOrientationChanged(int i) {
                    int unused = TransitionView.this.orientation = i;
                    if (!TransitionView.this.rotationChecked) {
                        TransitionView.this.rotateViewIfNeeded();
                    } else if (!TransitionView.isLandscapeLeft(i)) {
                        boolean unused2 = TransitionView.isLandscapeRight(i);
                    } else {
                        TransitionView.this.fadeOutAndRemove(false);
                    }
                }
            };
            this.orientationEventListener.enable();
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
        this.backButton = (ImageButton) ((ViewGroup) findViewById(R.id.transition_frame)).findViewById(R.id.back_button);
        if (this.backButtonRunnable != null) {
            this.backButton.setTag(this.backButtonRunnable);
            this.backButton.setVisibility(0);
            this.backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    TransitionView.this.backButtonRunnable.run();
                }
            });
            return;
        }
        this.backButton.setVisibility(8);
        this.backButton.setTag((Object) null);
        this.backButton.setOnClickListener((View.OnClickListener) null);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.orientationEventListener != null) {
            this.orientationEventListener.enable();
        }
        rotateViewIfNeeded();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.orientationEventListener != null) {
            this.orientation = -1;
            this.orientationEventListener.disable();
        }
        super.onDetachedFromWindow();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    public void setBackButtonListener(Runnable runnable) {
        this.backButtonRunnable = runnable;
        updateBackButtonVisibility();
    }

    public void setCustomTransitionLayout(int i, int i2) {
        this.useCustomTransitionLayout = true;
        inflateContentView(i);
        setBackground(new ColorDrawable(i2));
        setViewerName(this.viewerName);
        Drawable drawable = ((ImageView) findViewById(R.id.transition_icon)).getDrawable();
        if (drawable != null && (drawable instanceof AnimationDrawable)) {
            this.animationDrawable = (AnimationDrawable) drawable;
            this.animationDrawable.start();
        }
    }

    public void setTransitionListener(TransitionListener transitionListener2) {
        this.transitionListener = transitionListener2;
    }

    public void setViewerName(String str) {
        this.viewerName = str;
        TextView textView = (TextView) findViewById(R.id.transition_text);
        if (str == null) {
            textView.setText(getContext().getString(R.string.place_your_phone_into_cardboard));
            return;
        }
        textView.setText(getContext().getString(R.string.place_your_viewer_into_viewer_format, new Object[]{str}));
    }

    public void setVisibility(int i) {
        int visibility = getVisibility();
        super.setVisibility(i);
        if (visibility != i) {
            if (i != 0) {
                stopOrientationMonitor();
            } else {
                startOrientationMonitor();
            }
        }
    }
}
