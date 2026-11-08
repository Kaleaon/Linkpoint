package com.google.vr.sdk.base.sensors;

import android.hardware.SensorEventListener;
/* loaded from: classes.dex */
public interface SensorEventProvider {
    void registerListener(SensorEventListener sensorEventListener);

    void start();

    void stop();

    void unregisterListener(SensorEventListener sensorEventListener);
}
