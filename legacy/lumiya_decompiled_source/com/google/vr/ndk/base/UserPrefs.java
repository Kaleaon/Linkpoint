package com.google.vr.ndk.base;
/* loaded from: classes.dex */
public class UserPrefs {
    private static final String TAG = UserPrefs.class.getSimpleName();
    private final long nativeUserPrefs;

    /* loaded from: classes.dex */
    public static abstract class ControllerHandedness {
        public static final int LEFT_HANDED = 1;
        public static final int RIGHT_HANDED = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UserPrefs(long j) {
        this.nativeUserPrefs = j;
    }

    public int getControllerHandedness() {
        return GvrApi.nativeUserPrefsGetControllerHandedness(this.nativeUserPrefs);
    }
}
