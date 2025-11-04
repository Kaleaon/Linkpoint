// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors;

import android.hardware.SensorEventListener;

public interface SensorEventProvider
{
    void registerListener(final SensorEventListener p0);
    
    void start();
    
    void stop();
    
    void unregisterListener(final SensorEventListener p0);
}
