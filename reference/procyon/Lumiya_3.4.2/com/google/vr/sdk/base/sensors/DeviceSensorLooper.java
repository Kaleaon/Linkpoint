// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors;

import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread;
import android.hardware.SensorEvent;
import java.util.Iterator;
import android.os.Build;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Looper;
import android.hardware.SensorEventListener;
import java.util.ArrayList;

public class DeviceSensorLooper implements SensorEventProvider
{
    private static final String LOG_TAG;
    private boolean isRunning;
    private final ArrayList<SensorEventListener> registeredListeners;
    private SensorEventListener sensorEventListener;
    private Looper sensorLooper;
    private SensorManager sensorManager;
    
    static {
        LOG_TAG = DeviceSensorLooper.class.getSimpleName();
    }
    
    public DeviceSensorLooper(final SensorManager sensorManager) {
        this.registeredListeners = new ArrayList<SensorEventListener>();
        this.sensorManager = sensorManager;
    }
    
    private Sensor getUncalibratedGyro() {
        if (!Build.MANUFACTURER.equals("HTC")) {
            return this.sensorManager.getDefaultSensor(16);
        }
        return null;
    }
    
    @Override
    public void registerListener(final SensorEventListener e) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.add(e);
        }
    }
    
    @Override
    public void start() {
        if (!this.isRunning) {
            this.sensorEventListener = (SensorEventListener)new SensorEventListener() {
                public void onAccuracyChanged(final Sensor sensor, final int n) {
                    synchronized (DeviceSensorLooper.this.registeredListeners) {
                        final Iterator iterator = DeviceSensorLooper.this.registeredListeners.iterator();
                        while (iterator.hasNext()) {
                            ((SensorEventListener)iterator.next()).onAccuracyChanged(sensor, n);
                        }
                    }
                }
                
                public void onSensorChanged(final SensorEvent sensorEvent) {
                    synchronized (DeviceSensorLooper.this.registeredListeners) {
                        final Iterator iterator = DeviceSensorLooper.this.registeredListeners.iterator();
                        while (iterator.hasNext()) {
                            ((SensorEventListener)iterator.next()).onSensorChanged(sensorEvent);
                        }
                    }
                }
            };
            final HandlerThread handlerThread = new HandlerThread("sensor") {
                protected void onLooperPrepared() {
                    final Handler handler = new Handler(Looper.myLooper());
                    DeviceSensorLooper.this.sensorManager.registerListener(DeviceSensorLooper.this.sensorEventListener, DeviceSensorLooper.this.sensorManager.getDefaultSensor(1), 0, handler);
                    Sensor sensor = DeviceSensorLooper.this.getUncalibratedGyro();
                    if (sensor == null) {
                        Log.i(DeviceSensorLooper.LOG_TAG, "Uncalibrated gyroscope unavailable, default to regular gyroscope.");
                        sensor = DeviceSensorLooper.this.sensorManager.getDefaultSensor(4);
                    }
                    DeviceSensorLooper.this.sensorManager.registerListener(DeviceSensorLooper.this.sensorEventListener, sensor, 0, handler);
                }
            };
            handlerThread.start();
            this.sensorLooper = handlerThread.getLooper();
            this.isRunning = true;
        }
    }
    
    @Override
    public void stop() {
        if (this.isRunning) {
            this.sensorManager.unregisterListener(this.sensorEventListener);
            this.sensorEventListener = null;
            this.sensorLooper.quit();
            this.sensorLooper = null;
            this.isRunning = false;
        }
    }
    
    @Override
    public void unregisterListener(final SensorEventListener o) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.remove(o);
        }
    }
}
