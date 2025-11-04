// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.eventbus;

public abstract class EventRateLimiter
{
    private final EventBus bus;
    private volatile boolean isPending;
    private volatile long lastTimeFired;
    private final Object lock;
    private final long minInterval;
    
    protected EventRateLimiter(final EventBus bus, final long minInterval) {
        this.lock = new Object();
        this.lastTimeFired = 0L;
        this.isPending = false;
        this.bus = bus;
        this.minInterval = minInterval;
    }
    
    public void fire() {
        synchronized (this.lock) {
            this.isPending = true;
            monitorexit(this.lock);
            this.firePending();
        }
    }
    
    public void firePending() {
        final boolean b = false;
        Object o = this.lock;
        monitorenter(o);
        int n = b ? 1 : 0;
        try {
            if (this.isPending) {
                final long currentTimeMillis = System.currentTimeMillis();
                n = (b ? 1 : 0);
                if (currentTimeMillis >= this.lastTimeFired + this.minInterval) {
                    n = 1;
                    this.isPending = false;
                    this.lastTimeFired = currentTimeMillis;
                }
            }
            monitorexit(o);
            if (n != 0) {
                this.onActualFire();
                o = this.getEventToFire();
                if (o != null && this.bus != null) {
                    this.bus.publish(o);
                }
            }
        }
        finally {
            monitorexit(o);
        }
    }
    
    protected abstract Object getEventToFire();
    
    protected void onActualFire() {
    }
}
