// 
// Decompiled by Procyon v0.6.0
// 

package android.arch.lifecycle;

import android.support.annotation.MainThread;

public abstract class Lifecycle
{
    @MainThread
    public abstract void addObserver(final LifecycleObserver p0);
    
    @MainThread
    public abstract State getCurrentState();
    
    @MainThread
    public abstract void removeObserver(final LifecycleObserver p0);
    
    public enum Event
    {
        ON_ANY, 
        ON_CREATE, 
        ON_DESTROY, 
        ON_PAUSE, 
        ON_RESUME, 
        ON_START, 
        ON_STOP;
    }
    
    public enum State
    {
        CREATED, 
        DESTROYED, 
        INITIALIZED, 
        RESUMED, 
        STARTED;
        
        public boolean isAtLeast(final State o) {
            boolean b = false;
            if (this.compareTo(o) >= 0) {
                b = true;
            }
            return b;
        }
    }
}
