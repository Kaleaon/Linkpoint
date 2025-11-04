// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vrtoolkit.cardboard;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.app.Activity;
import android.hardware.SensorEventListener;

public class ScreenOnFlagHelper implements SensorEventListener
{
    private static final boolean DEBUG = false;
    private static final long IDLE_TIMEOUT_MS = 30000L;
    private static final int NUM_SAMPLES = 120;
    private static final long SAMPLE_INTERVAL_MS = 250L;
    private static final float SENSOR_THRESHOLD = 0.2f;
    private static final String TAG = "ScreenOnFlagHelper";
    private Activity activity;
    private boolean isFlagSet;
    private long lastSampleTimestamp;
    private boolean screenAlwaysOn;
    private Sensor sensor;
    private SensorManager sensorManager;
    private SensorReadingStats sensorStats;
    
    public ScreenOnFlagHelper(final Activity activity) {
        this.screenAlwaysOn = false;
        this.sensorStats = new SensorReadingStats(120, 3);
        this.lastSampleTimestamp = 0L;
        this.isFlagSet = false;
        this.activity = activity;
    }
    
    private void setKeepScreenOnFlag(final boolean isFlagSet) {
        if (isFlagSet != this.isFlagSet) {
            if (!isFlagSet) {
                this.activity.getWindow().clearFlags(128);
            }
            else {
                this.activity.getWindow().addFlags(128);
            }
            this.isFlagSet = isFlagSet;
        }
    }
    
    private void updateFlag() {
        boolean keepScreenOnFlag = true;
        if (!this.screenAlwaysOn && this.sensorStats.statsAvailable()) {
            if (this.sensorStats.getMaxAbsoluteDeviation() <= 0.2f) {
                keepScreenOnFlag = false;
            }
            this.setKeepScreenOnFlag(keepScreenOnFlag);
            return;
        }
        this.setKeepScreenOnFlag(true);
    }
    
    public void onAccuracyChanged(final Sensor sensor, final int n) {
    }
    
    public void onSensorChanged(final SensorEvent sensorEvent) {
        int n;
        if ((sensorEvent.timestamp - this.lastSampleTimestamp) / 1000000L >= 250L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            return;
        }
        this.sensorStats.addSample(sensorEvent.values);
        this.lastSampleTimestamp = sensorEvent.timestamp;
        this.updateFlag();
    }
    
    public void setScreenAlwaysOn(final boolean screenAlwaysOn) {
        this.screenAlwaysOn = screenAlwaysOn;
        this.updateFlag();
    }
    
    void setSensorManager(final SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }
    
    public void start() {
        if (this.sensorManager == null) {
            this.sensorManager = (SensorManager)this.activity.getSystemService("sensor");
        }
        if (this.sensor == null) {
            this.sensor = this.sensorManager.getDefaultSensor(1);
        }
        this.isFlagSet = false;
        this.setKeepScreenOnFlag(true);
        this.sensorStats.reset();
        this.sensorManager.registerListener((SensorEventListener)this, this.sensor, 250000);
    }
    
    public void stop() {
        if (this.sensorManager != null) {
            this.sensorManager.unregisterListener((SensorEventListener)this);
        }
        this.setKeepScreenOnFlag(false);
    }
}
