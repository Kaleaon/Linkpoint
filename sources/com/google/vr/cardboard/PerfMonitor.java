package com.google.vr.cardboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.performance.api.IPerformanceService;
import com.google.vr.vrcore.performance.api.PerformanceServiceConsts;

public class PerfMonitor implements AutoCloseable {
    private static final int STATUS_CONNECTED = 2;
    private static final int STATUS_CONNECTING = 1;
    private static final int STATUS_DISCONNECTED = 0;
    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (PerfMonitor.this.lock) {
                IPerformanceService unused = PerfMonitor.this.perfService = IPerformanceService.Stub.asInterface(iBinder);
                int unused2 = PerfMonitor.this.status = 2;
                PerfMonitor.this.lock.notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (PerfMonitor.this.lock) {
                IPerformanceService unused = PerfMonitor.this.perfService = null;
                int unused2 = PerfMonitor.this.status = 0;
                PerfMonitor.this.lock.notifyAll();
            }
        }
    };
    private final Context context;
    /* access modifiers changed from: private */
    public final Object lock = new Object();
    /* access modifiers changed from: private */
    public IPerformanceService perfService;
    /* access modifiers changed from: private */
    public int status = 1;

    protected PerfMonitor(Context context2) {
        this.context = context2;
    }

    public static PerfMonitor build(Context context2) {
        Intent intent = new Intent(PerformanceServiceConsts.BIND_INTENT_ACTION);
        intent.setPackage("com.google.vr.vrcore");
        PerfMonitor perfMonitor = new PerfMonitor(context2);
        if (context2.bindService(intent, perfMonitor.connection, 1)) {
            return perfMonitor;
        }
        return null;
    }

    public void close() {
        this.context.unbindService(this.connection);
    }

    public boolean isReady() {
        boolean z;
        synchronized (this.lock) {
            z = this.status == 2;
        }
        return z;
    }

    public float queryRelativeTemperature() throws VrCoreNotAvailableException {
        IPerformanceService iPerformanceService;
        synchronized (this.lock) {
            iPerformanceService = this.perfService;
        }
        if (iPerformanceService != null) {
            try {
                return iPerformanceService.getCurrentThrottlingRelativeTemperature();
            } catch (RemoteException e) {
                throw new VrCoreNotAvailableException(8);
            } catch (SecurityException e2) {
                throw new VrCoreNotAvailableException(6);
            } catch (UnsupportedOperationException e3) {
                throw new VrCoreNotAvailableException(7);
            }
        } else {
            throw new VrCoreNotAvailableException(5);
        }
    }

    public void reportFrameDrops(long j, long j2, int i) throws VrCoreNotAvailableException {
        IPerformanceService iPerformanceService;
        synchronized (this.lock) {
            iPerformanceService = this.perfService;
        }
        if (iPerformanceService != null) {
            try {
                iPerformanceService.reportFrameDrops(j, j2, i);
            } catch (RemoteException e) {
                throw new VrCoreNotAvailableException(8);
            }
        } else {
            throw new VrCoreNotAvailableException(5);
        }
    }

    public void waitUntilReady(long j) throws IllegalStateException, InterruptedException {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            synchronized (this.lock) {
                long currentTimeMillis = System.currentTimeMillis();
                long j2 = currentTimeMillis + j;
                while (this.status == 1) {
                    if (currentTimeMillis >= j2) {
                        break;
                    }
                    this.lock.wait(j2 - currentTimeMillis);
                    currentTimeMillis = System.currentTimeMillis();
                }
            }
            return;
        }
        throw new IllegalStateException("waitUntilReady cannot be called from the UI thread");
    }
}
