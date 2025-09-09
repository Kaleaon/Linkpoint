package android.support.v4.app;

import android.view.View;
import android.view.ViewTreeObserver;

class OneShotPreDrawListener implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {
    private final Runnable mRunnable;
    private final View mView;
    private ViewTreeObserver mViewTreeObserver;

    private OneShotPreDrawListener(View view, Runnable runnable) {
        this.mView = view;
        this.mViewTreeObserver = view.getViewTreeObserver();
        this.mRunnable = runnable;
    }

    public static OneShotPreDrawListener add(View view, Runnable runnable) {
        OneShotPreDrawListener oneShotPreDrawListener = new OneShotPreDrawListener(view, runnable);
        view.getViewTreeObserver().addOnPreDrawListener(oneShotPreDrawListener);
        view.addOnAttachStateChangeListener(oneShotPreDrawListener);
        return oneShotPreDrawListener;
    }

    public boolean onPreDraw() {
        removeListener();
        this.mRunnable.run();
        return true;
    }

    public void onViewAttachedToWindow(View view) {
        this.mViewTreeObserver = view.getViewTreeObserver();
    }

    public void onViewDetachedFromWindow(View view) {
        removeListener();
    }

    public void removeListener() {
        if (!this.mViewTreeObserver.isAlive()) {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
        } else {
            this.mViewTreeObserver.removeOnPreDrawListener(this);
        }
        this.mView.removeOnAttachStateChangeListener(this);
    }
}
