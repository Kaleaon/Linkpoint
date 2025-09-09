package com.lumiyaviewer.lumiya.eventbus;

public abstract class EventRateLimiter {
    private final EventBus bus;
    private volatile boolean isPending = false;
    private volatile long lastTimeFired = 0;
    private final Object lock = new Object();
    private final long minInterval;

    protected EventRateLimiter(EventBus eventBus, long j) {
        this.bus = eventBus;
        this.minInterval = j;
    }

    public void fire() {
        synchronized (this.lock) {
            this.isPending = true;
        }
        firePending();
    }

    public void firePending() {
        boolean z = false;
        synchronized (this.lock) {
            if (this.isPending) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis >= this.lastTimeFired + this.minInterval) {
                    z = true;
                    this.isPending = false;
                    this.lastTimeFired = currentTimeMillis;
                }
            }
        }
        if (z) {
            onActualFire();
            Object eventToFire = getEventToFire();
            if (eventToFire != null && this.bus != null) {
                this.bus.publish(eventToFire);
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract Object getEventToFire();

    /* access modifiers changed from: protected */
    public void onActualFire() {
    }
}
