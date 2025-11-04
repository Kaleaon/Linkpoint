// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.os.Looper;
import android.os.RemoteException;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import android.content.Intent;
import android.os.IBinder;
import android.content.ComponentName;
import com.google.vr.vrcore.performance.api.IPerformanceService;
import android.content.Context;
import android.content.ServiceConnection;

public class PerfMonitor implements AutoCloseable
{
    private static final int STATUS_CONNECTED = 2;
    private static final int STATUS_CONNECTING = 1;
    private static final int STATUS_DISCONNECTED = 0;
    private final ServiceConnection connection;
    private final Context context;
    private final Object lock;
    private IPerformanceService perfService;
    private int status;
    
    protected PerfMonitor(final Context context) {
        this.lock = new Object();
        this.status = 1;
        this.connection = (ServiceConnection)new ServiceConnection() {
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                synchronized (PerfMonitor.this.lock) {
                    PerfMonitor.this.perfService = IPerformanceService.Stub.asInterface(binder);
                    PerfMonitor.this.status = 2;
                    PerfMonitor.this.lock.notifyAll();
                }
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                synchronized (PerfMonitor.this.lock) {
                    PerfMonitor.this.perfService = null;
                    PerfMonitor.this.status = 0;
                    PerfMonitor.this.lock.notifyAll();
                }
            }
        };
        this.context = context;
    }
    
    public static PerfMonitor build(final Context context) {
        final Intent intent = new Intent("com.google.vr.vrcore.performance.BIND");
        intent.setPackage("com.google.vr.vrcore");
        final PerfMonitor perfMonitor = new PerfMonitor(context);
        if (context.bindService(intent, perfMonitor.connection, 1)) {
            return perfMonitor;
        }
        return null;
    }
    
    @Override
    public void close() {
        this.context.unbindService(this.connection);
    }
    
    public boolean isReady() {
        synchronized (this.lock) {
            return this.status == 2;
        }
    }
    
    public float queryRelativeTemperature() throws VrCoreNotAvailableException {
        final Object lock = this.lock;
        Label_0032: {
            final IPerformanceService perfService;
            synchronized (lock) {
                perfService = this.perfService;
                monitorexit(lock);
                if (perfService != null) {
                    final IPerformanceService performanceService = perfService;
                    final float currentThrottlingRelativeTemperature = performanceService.getCurrentThrottlingRelativeTemperature();
                    return currentThrottlingRelativeTemperature;
                }
                break Label_0032;
            }
            try {
                final IPerformanceService performanceService = perfService;
                final float currentThrottlingRelativeTemperature2;
                final float currentThrottlingRelativeTemperature = currentThrottlingRelativeTemperature2 = performanceService.getCurrentThrottlingRelativeTemperature();
                return currentThrottlingRelativeTemperature2;
                throw new VrCoreNotAvailableException(5);
            }
            catch (final RemoteException ex) {
                throw new VrCoreNotAvailableException(8);
            }
            catch (final SecurityException ex2) {
                throw new VrCoreNotAvailableException(6);
            }
            catch (final UnsupportedOperationException ex3) {
                throw new VrCoreNotAvailableException(7);
            }
        }
    }
    
    public void reportFrameDrops(final long n, final long n2, final int n3) throws VrCoreNotAvailableException {
        final Object lock = this.lock;
        Label_0043: {
            final IPerformanceService perfService;
            synchronized (lock) {
                perfService = this.perfService;
                monitorexit(lock);
                if (perfService != null) {
                    final IPerformanceService performanceService = perfService;
                    final long n4 = n;
                    final long n5 = n2;
                    final int n6 = n3;
                    performanceService.reportFrameDrops(n4, n5, n6);
                    return;
                }
                break Label_0043;
            }
            try {
                final IPerformanceService performanceService = perfService;
                final long n4 = n;
                final long n5 = n2;
                final int n6 = n3;
                performanceService.reportFrameDrops(n4, n5, n6);
                return;
                throw new VrCoreNotAvailableException(5);
            }
            catch (final RemoteException ex) {
                throw new VrCoreNotAvailableException(8);
            }
        }
    }
    
    public void waitUntilReady(long currentTimeMillis) throws IllegalStateException, InterruptedException {
        Label_0044: {
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                break Label_0044;
            }
            synchronized (this.lock) {
                final long currentTimeMillis2 = System.currentTimeMillis();
                final long n = currentTimeMillis2 + currentTimeMillis;
                currentTimeMillis = currentTimeMillis2;
                while (this.status == 1) {
                    int n2;
                    if (currentTimeMillis >= n) {
                        n2 = 1;
                    }
                    else {
                        n2 = 0;
                    }
                    if (n2 != 0) {
                        break;
                    }
                    this.lock.wait(n - currentTimeMillis);
                    currentTimeMillis = System.currentTimeMillis();
                }
                return;
                throw new IllegalStateException("waitUntilReady cannot be called from the UI thread");
            }
        }
    }
}
