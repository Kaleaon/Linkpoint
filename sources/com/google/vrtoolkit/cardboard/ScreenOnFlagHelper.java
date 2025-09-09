package com.google.vrtoolkit.cardboard;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ScreenOnFlagHelper implements SensorEventListener {
    private static final boolean DEBUG = false;
    private static final long IDLE_TIMEOUT_MS = 30000;
    private static final int NUM_SAMPLES = 120;
    private static final long SAMPLE_INTERVAL_MS = 250;
    private static final float SENSOR_THRESHOLD = 0.2f;
    private static final String TAG = "ScreenOnFlagHelper";
    private Activity activity;
    private boolean isFlagSet = false;
    private long lastSampleTimestamp = 0;
    private boolean screenAlwaysOn = false;
    private Sensor sensor;
    private SensorManager sensorManager;
    private SensorReadingStats sensorStats = new SensorReadingStats(120, 3);

    public ScreenOnFlagHelper(Activity activity2) {
        this.activity = activity2;
    }

    private void setKeepScreenOnFlag(boolean z) {
        if (z != this.isFlagSet) {
            if (!z) {
                this.activity.getWindow().clearFlags(128);
            } else {
                this.activity.getWindow().addFlags(128);
            }
            this.isFlagSet = z;
        }
    }

    private void updateFlag() {
        boolean z = true;
        if (!this.screenAlwaysOn && this.sensorStats.statsAvailable()) {
            if (this.sensorStats.getMaxAbsoluteDeviation() <= SENSOR_THRESHOLD) {
                z = false;
            }
            setKeepScreenOnFlag(z);
            return;
        }
        setKeepScreenOnFlag(true);
    }

    public void onAccuracyChanged(Sensor sensor2, int i) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if ((sensorEvent.timestamp - this.lastSampleTimestamp) / 1000000 >= SAMPLE_INTERVAL_MS) {
            this.sensorStats.addSample(sensorEvent.values);
            this.lastSampleTimestamp = sensorEvent.timestamp;
            updateFlag();
        }
    }

    public void setScreenAlwaysOn(boolean z) {
        this.screenAlwaysOn = z;
        updateFlag();
    }

    /* access modifiers changed from: package-private */
    public void setSensorManager(SensorManager sensorManager2) {
        this.sensorManager = sensorManager2;
    }

    public void start() {
        if (this.sensorManager == null) {
            this.sensorManager = (SensorManager) this.activity.getSystemService("sensor");
        }
        if (this.sensor == null) {
            this.sensor = this.sensorManager.getDefaultSensor(1);
        }
        this.isFlagSet = false;
        setKeepScreenOnFlag(true);
        this.sensorStats.reset();
        this.sensorManager.registerListener(this, this.sensor, 250000);
    }

    public void stop() {
        if (this.sensorManager != null) {
            this.sensorManager.unregisterListener(this);
        }
        setKeepScreenOnFlag(false);
    }
}
