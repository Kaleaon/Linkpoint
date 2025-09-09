package android.support.v4.os;

import android.os.Build;

public final class CancellationSignal {
    private boolean mCancelInProgress;
    private Object mCancellationSignalObj;
    private boolean mIsCanceled;
    private OnCancelListener mOnCancelListener;

    public interface OnCancelListener {
        void onCancel();
    }

    private void waitForCancelFinishedLocked() {
        while (this.mCancelInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r3.mCancelInProgress = false;
        notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r1.onCancel();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0026, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0027, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r3.mCancelInProgress = false;
        notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x002f, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0034, code lost:
        if (android.os.Build.VERSION.SDK_INT < 16) goto L_0x0014;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0036, code lost:
        ((android.os.CancellationSignal) r0).cancel();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0010, code lost:
        if (r1 != null) goto L_0x0022;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0012, code lost:
        if (r0 != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0014, code lost:
        monitor-enter(r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancel() {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.mIsCanceled     // Catch:{ all -> 0x001f }
            if (r0 != 0) goto L_0x001d
            r0 = 1
            r3.mIsCanceled = r0     // Catch:{ all -> 0x001f }
            r0 = 1
            r3.mCancelInProgress = r0     // Catch:{ all -> 0x001f }
            android.support.v4.os.CancellationSignal$OnCancelListener r1 = r3.mOnCancelListener     // Catch:{ all -> 0x001f }
            java.lang.Object r0 = r3.mCancellationSignalObj     // Catch:{ all -> 0x001f }
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            if (r1 != 0) goto L_0x0022
        L_0x0012:
            if (r0 != 0) goto L_0x0030
        L_0x0014:
            monitor-enter(r3)
            r0 = 0
            r3.mCancelInProgress = r0     // Catch:{ all -> 0x003c }
            r3.notifyAll()     // Catch:{ all -> 0x003c }
            monitor-exit(r3)     // Catch:{ all -> 0x003c }
            return
        L_0x001d:
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            return
        L_0x001f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            throw r0
        L_0x0022:
            r1.onCancel()     // Catch:{ all -> 0x0026 }
            goto L_0x0012
        L_0x0026:
            r0 = move-exception
            monitor-enter(r3)
            r1 = 0
            r3.mCancelInProgress = r1     // Catch:{ all -> 0x003f }
            r3.notifyAll()     // Catch:{ all -> 0x003f }
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            throw r0
        L_0x0030:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0026 }
            r2 = 16
            if (r1 < r2) goto L_0x0014
            android.os.CancellationSignal r0 = (android.os.CancellationSignal) r0     // Catch:{ all -> 0x0026 }
            r0.cancel()     // Catch:{ all -> 0x0026 }
            goto L_0x0014
        L_0x003c:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003c }
            throw r0
        L_0x003f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.os.CancellationSignal.cancel():void");
    }

    public Object getCancellationSignalObject() {
        Object obj;
        if (Build.VERSION.SDK_INT < 16) {
            return null;
        }
        synchronized (this) {
            if (this.mCancellationSignalObj == null) {
                this.mCancellationSignalObj = new android.os.CancellationSignal();
                if (this.mIsCanceled) {
                    ((android.os.CancellationSignal) this.mCancellationSignalObj).cancel();
                }
            }
            obj = this.mCancellationSignalObj;
        }
        return obj;
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this) {
            z = this.mIsCanceled;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setOnCancelListener(android.support.v4.os.CancellationSignal.OnCancelListener r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            r1.waitForCancelFinishedLocked()     // Catch:{ all -> 0x0019 }
            android.support.v4.os.CancellationSignal$OnCancelListener r0 = r1.mOnCancelListener     // Catch:{ all -> 0x0019 }
            if (r0 == r2) goto L_0x0010
            r1.mOnCancelListener = r2     // Catch:{ all -> 0x0019 }
            boolean r0 = r1.mIsCanceled     // Catch:{ all -> 0x0019 }
            if (r0 != 0) goto L_0x0012
        L_0x000e:
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            return
        L_0x0010:
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            return
        L_0x0012:
            if (r2 == 0) goto L_0x000e
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            r2.onCancel()
            return
        L_0x0019:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.os.CancellationSignal.setOnCancelListener(android.support.v4.os.CancellationSignal$OnCancelListener):void");
    }

    public void throwIfCanceled() {
        if (isCanceled()) {
            throw new OperationCanceledException();
        }
    }
}
