package com.google.vr.sdk.base.testing.sensors;

import android.hardware.SensorEvent;

public class SensorEventAndTime {
    public SensorEvent event;
    public long timeNs;

    public SensorEventAndTime(SensorEvent sensorEvent, long j) {
        this.event = sensorEvent;
        this.timeNs = j;
    }
}
