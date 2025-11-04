// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.testing.sensors;

import java.util.Iterator;
import com.google.vr.sdk.base.sensors.Clock;
import java.util.ArrayList;
import android.hardware.SensorEventListener;
import java.util.ListIterator;
import java.util.List;
import com.google.vr.sdk.base.sensors.SensorEventProvider;

public class SensorEventProviderFromRecordedData implements SensorEventProvider
{
    private EventClock eventClock;
    private final List<SensorEventAndTime> events;
    private ListIterator<SensorEventAndTime> eventsIterator;
    private final List<SensorEventListener> registeredListeners;
    
    public SensorEventProviderFromRecordedData(final List<SensorEventAndTime> events) {
        this.eventsIterator = null;
        this.eventClock = new EventClock();
        this.registeredListeners = new ArrayList<SensorEventListener>();
        this.events = events;
    }
    
    public boolean atEnd() {
        boolean b = false;
        if (!this.eventsIterator.hasNext()) {
            b = true;
        }
        return b;
    }
    
    public Clock getClock() {
        return this.eventClock;
    }
    
    public boolean next() {
        if (this.atEnd()) {
            return false;
        }
        final SensorEventAndTime sensorEventAndTime = this.eventsIterator.next();
        this.eventClock.setTimeNs(sensorEventAndTime.timeNs);
        synchronized (this.registeredListeners) {
            final Iterator<SensorEventListener> iterator = this.registeredListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onSensorChanged(sensorEventAndTime.event);
            }
            return true;
        }
    }
    
    @Override
    public void registerListener(final SensorEventListener sensorEventListener) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.add(sensorEventListener);
        }
    }
    
    public void reset() {
        this.eventsIterator = this.events.listIterator();
    }
    
    @Override
    public void start() {
        this.reset();
        this.next();
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    public void unregisterListener(final SensorEventListener sensorEventListener) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.remove(sensorEventListener);
        }
    }
    
    private static class EventClock implements Clock
    {
        private long currentTimeNs;
        
        @Override
        public long nanoTime() {
            return this.currentTimeNs;
        }
        
        public void setTimeNs(final long currentTimeNs) {
            this.currentTimeNs = currentTimeNs;
        }
    }
}
