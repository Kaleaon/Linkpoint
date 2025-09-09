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
        Object obj = null;
        synchronized (this.lock) {
            if (this.isPending) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis >= this.lastTimeFired + this.minInterval) {
                    obj = 1;
                    this.isPending = false;
                    this.lastTimeFired = currentTimeMillis;
                }
            }
        }
        if (obj != null) {
            onActualFire();
            obj = getEventToFire();
            if (obj != null && this.bus != null) {
                this.bus.publish(obj);
            }
        }
    }

    protected abstract Object getEventToFire();

    protected void onActualFire() {
    }
}
