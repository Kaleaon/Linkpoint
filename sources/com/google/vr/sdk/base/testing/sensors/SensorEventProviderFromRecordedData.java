package com.google.vr.sdk.base.testing.sensors;

import android.hardware.SensorEventListener;
import com.google.vr.sdk.base.sensors.Clock;
import com.google.vr.sdk.base.sensors.SensorEventProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SensorEventProviderFromRecordedData implements SensorEventProvider {
    private EventClock eventClock = new EventClock();
    private final List<SensorEventAndTime> events;
    private ListIterator<SensorEventAndTime> eventsIterator = null;
    private final List<SensorEventListener> registeredListeners = new ArrayList();

    private static class EventClock implements Clock {
        private long currentTimeNs;

        private EventClock() {
        }

        public long nanoTime() {
            return this.currentTimeNs;
        }

        public void setTimeNs(long j) {
            this.currentTimeNs = j;
        }
    }

    public SensorEventProviderFromRecordedData(List<SensorEventAndTime> list) {
        this.events = list;
    }

    public boolean atEnd() {
        return !this.eventsIterator.hasNext();
    }

    public Clock getClock() {
        return this.eventClock;
    }

    public boolean next() {
        if (atEnd()) {
            return false;
        }
        SensorEventAndTime next = this.eventsIterator.next();
        this.eventClock.setTimeNs(next.timeNs);
        synchronized (this.registeredListeners) {
            for (SensorEventListener onSensorChanged : this.registeredListeners) {
                onSensorChanged.onSensorChanged(next.event);
            }
        }
        return true;
    }

    public void registerListener(SensorEventListener sensorEventListener) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.add(sensorEventListener);
        }
    }

    public void reset() {
        this.eventsIterator = this.events.listIterator();
    }

    public void start() {
        reset();
        next();
    }

    public void stop() {
    }

    public void unregisterListener(SensorEventListener sensorEventListener) {
        synchronized (this.registeredListeners) {
            this.registeredListeners.remove(sensorEventListener);
        }
    }
}
