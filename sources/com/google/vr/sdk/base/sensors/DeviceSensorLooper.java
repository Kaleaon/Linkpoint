package com.google.vr.sdk.base.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public class DeviceSensorLooper implements SensorEventProvider {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = DeviceSensorLooper.class.getSimpleName();
    private boolean isRunning;
    /* access modifiers changed from: private */
    public final ArrayList<SensorEventListener> registeredListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public SensorEventListener sensorEventListener;
    private Looper sensorLooper;
    /* access modifiers changed from: private */
    public SensorManager sensorManager;

    public DeviceSensorLooper(SensorManager sensorManager2) {
        this.sensorManager = sensorManager2;
    }

    /* access modifiers changed from: private */
    public Sensor getUncalibratedGyro() {
        if (!Build.MANUFACTURER.equals("HTC")) {
            return this.sensorManager.getDefaultSensor(16);
        }
        return null;
    }

    public void registerListener(SensorEventListener sensorEventListener2) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.add(sensorEventListener2);
        }
    }

    public void start() {
        if (!this.isRunning) {
            this.sensorEventListener = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int i) {
                    synchronized (DeviceSensorLooper.this.registeredListeners) {
                        Iterator it = DeviceSensorLooper.this.registeredListeners.iterator();
                        while (it.hasNext()) {
                            ((SensorEventListener) it.next()).onAccuracyChanged(sensor, i);
                        }
                    }
                }

                public void onSensorChanged(SensorEvent sensorEvent) {
                    synchronized (DeviceSensorLooper.this.registeredListeners) {
                        Iterator it = DeviceSensorLooper.this.registeredListeners.iterator();
                        while (it.hasNext()) {
                            ((SensorEventListener) it.next()).onSensorChanged(sensorEvent);
                        }
                    }
                }
            };
            AnonymousClass2 r0 = new HandlerThread("sensor") {
                /* access modifiers changed from: protected */
                public void onLooperPrepared() {
                    Handler handler = new Handler(Looper.myLooper());
                    DeviceSensorLooper.this.sensorManager.registerListener(DeviceSensorLooper.this.sensorEventListener, DeviceSensorLooper.this.sensorManager.getDefaultSensor(1), 0, handler);
                    Sensor access$300 = DeviceSensorLooper.this.getUncalibratedGyro();
                    if (access$300 == null) {
                        Log.i(DeviceSensorLooper.LOG_TAG, "Uncalibrated gyroscope unavailable, default to regular gyroscope.");
                        access$300 = DeviceSensorLooper.this.sensorManager.getDefaultSensor(4);
                    }
                    DeviceSensorLooper.this.sensorManager.registerListener(DeviceSensorLooper.this.sensorEventListener, access$300, 0, handler);
                }
            };
            r0.start();
            this.sensorLooper = r0.getLooper();
            this.isRunning = true;
        }
    }

    public void stop() {
        if (this.isRunning) {
            this.sensorManager.unregisterListener(this.sensorEventListener);
            this.sensorEventListener = null;
            this.sensorLooper.quit();
            this.sensorLooper = null;
            this.isRunning = false;
        }
    }

    public void unregisterListener(SensorEventListener sensorEventListener2) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.remove(sensorEventListener2);
        }
    }
}
