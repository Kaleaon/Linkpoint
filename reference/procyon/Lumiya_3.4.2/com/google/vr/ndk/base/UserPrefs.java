// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

public class UserPrefs
{
    private static final String TAG;
    private final long nativeUserPrefs;
    
    static {
        TAG = UserPrefs.class.getSimpleName();
    }
    
    UserPrefs(final long nativeUserPrefs) {
        this.nativeUserPrefs = nativeUserPrefs;
    }
    
    public int getControllerHandedness() {
        return GvrApi.nativeUserPrefsGetControllerHandedness(this.nativeUserPrefs);
    }
    
    public abstract static class ControllerHandedness
    {
        public static final int LEFT_HANDED = 1;
        public static final int RIGHT_HANDED = 0;
    }
}
