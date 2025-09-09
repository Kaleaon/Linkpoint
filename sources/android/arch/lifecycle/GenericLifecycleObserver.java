package android.arch.lifecycle;

import android.arch.lifecycle.Lifecycle;

public interface GenericLifecycleObserver extends LifecycleObserver {
    void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event);
}
