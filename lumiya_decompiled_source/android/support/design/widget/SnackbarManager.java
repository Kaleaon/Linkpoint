package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
class SnackbarManager {
    private static final int LONG_DURATION_MS = 2750;
    static final int MSG_TIMEOUT = 0;
    private static final int SHORT_DURATION_MS = 1500;
    private static SnackbarManager sSnackbarManager;
    private SnackbarRecord mCurrentSnackbar;
    private SnackbarRecord mNextSnackbar;
    private final Object mLock = new Object();
    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: android.support.design.widget.SnackbarManager.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    SnackbarManager.this.handleTimeout((SnackbarRecord) message.obj);
                    return true;
                default:
                    return false;
            }
        }
    });

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback {
        void dismiss(int i);

        void show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SnackbarRecord {
        final WeakReference<Callback> callback;
        int duration;
        boolean paused;

        SnackbarRecord(int i, Callback callback) {
            this.callback = new WeakReference<>(callback);
            this.duration = i;
        }

        boolean isSnackbar(Callback callback) {
            return callback != null && this.callback.get() == callback;
        }
    }

    private SnackbarManager() {
    }

    private boolean cancelSnackbarLocked(SnackbarRecord snackbarRecord, int i) {
        Callback callback = snackbarRecord.callback.get();
        if (callback == null) {
            return false;
        }
        this.mHandler.removeCallbacksAndMessages(snackbarRecord);
        callback.dismiss(i);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SnackbarManager getInstance() {
        if (sSnackbarManager == null) {
            sSnackbarManager = new SnackbarManager();
        }
        return sSnackbarManager;
    }

    private boolean isCurrentSnackbarLocked(Callback callback) {
        return this.mCurrentSnackbar != null && this.mCurrentSnackbar.isSnackbar(callback);
    }

    private boolean isNextSnackbarLocked(Callback callback) {
        return this.mNextSnackbar != null && this.mNextSnackbar.isSnackbar(callback);
    }

    private void scheduleTimeoutLocked(SnackbarRecord snackbarRecord) {
        if (snackbarRecord.duration != -2) {
            int i = LONG_DURATION_MS;
            if (snackbarRecord.duration > 0) {
                i = snackbarRecord.duration;
            } else if (snackbarRecord.duration == -1) {
                i = SHORT_DURATION_MS;
            }
            this.mHandler.removeCallbacksAndMessages(snackbarRecord);
            this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 0, snackbarRecord), i);
        }
    }

    private void showNextSnackbarLocked() {
        if (this.mNextSnackbar == null) {
            return;
        }
        this.mCurrentSnackbar = this.mNextSnackbar;
        this.mNextSnackbar = null;
        Callback callback = this.mCurrentSnackbar.callback.get();
        if (callback == null) {
            this.mCurrentSnackbar = null;
        } else {
            callback.show();
        }
    }

    public void dismiss(Callback callback, int i) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                cancelSnackbarLocked(this.mCurrentSnackbar, i);
            } else if (isNextSnackbarLocked(callback)) {
                cancelSnackbarLocked(this.mNextSnackbar, i);
            }
        }
    }

    void handleTimeout(SnackbarRecord snackbarRecord) {
        synchronized (this.mLock) {
            if (this.mCurrentSnackbar == snackbarRecord || this.mNextSnackbar == snackbarRecord) {
                cancelSnackbarLocked(snackbarRecord, 2);
            }
        }
    }

    public boolean isCurrent(Callback callback) {
        boolean isCurrentSnackbarLocked;
        synchronized (this.mLock) {
            isCurrentSnackbarLocked = isCurrentSnackbarLocked(callback);
        }
        return isCurrentSnackbarLocked;
    }

    public boolean isCurrentOrNext(Callback callback) {
        boolean z = false;
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback) || isNextSnackbarLocked(callback)) {
                z = true;
            }
        }
        return z;
    }

    public void onDismissed(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                this.mCurrentSnackbar = null;
                if (this.mNextSnackbar != null) {
                    showNextSnackbarLocked();
                }
            }
        }
    }

    public void onShown(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                scheduleTimeoutLocked(this.mCurrentSnackbar);
            }
        }
    }

    public void pauseTimeout(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback) && !this.mCurrentSnackbar.paused) {
                this.mCurrentSnackbar.paused = true;
                this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
            }
        }
    }

    public void restoreTimeoutIfPaused(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback) && this.mCurrentSnackbar.paused) {
                this.mCurrentSnackbar.paused = false;
                scheduleTimeoutLocked(this.mCurrentSnackbar);
            }
        }
    }

    public void show(int i, Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                this.mCurrentSnackbar.duration = i;
                this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
                scheduleTimeoutLocked(this.mCurrentSnackbar);
                return;
            }
            if (isNextSnackbarLocked(callback)) {
                this.mNextSnackbar.duration = i;
            } else {
                this.mNextSnackbar = new SnackbarRecord(i, callback);
            }
            if (this.mCurrentSnackbar != null && cancelSnackbarLocked(this.mCurrentSnackbar, 4)) {
                return;
            }
            this.mCurrentSnackbar = null;
            showNextSnackbarLocked();
        }
    }
}
