package com.google.vr.sdk.base.sensors;

public class SystemClock implements Clock {
    public long nanoTime() {
        return System.nanoTime();
    }
}
