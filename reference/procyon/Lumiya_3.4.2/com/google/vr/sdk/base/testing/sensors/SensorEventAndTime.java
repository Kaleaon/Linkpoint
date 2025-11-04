// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.testing.sensors;

import android.hardware.SensorEvent;

public class SensorEventAndTime
{
    public SensorEvent event;
    public long timeNs;
    
    public SensorEventAndTime(final SensorEvent event, final long timeNs) {
        this.event = event;
        this.timeNs = timeNs;
    }
}
