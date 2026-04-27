// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import java.util.ArrayDeque;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

@Deprecated
public class ControllerEventPacket implements Parcelable
{
    public static final Parcelable$Creator<ControllerEventPacket> CREATOR;
    protected static final int MAX_EVENTS = 16;
    private static final int SERIALIZED_FORMAT_VERSION = 1;
    private static ArrayDeque<ControllerEventPacket> pool;
    private static Object poolLock;
    private int accelEventCount;
    private ControllerAccelEvent[] accelEvents;
    private int buttonEventCount;
    private ControllerButtonEvent[] buttonEvents;
    private int gyroEventCount;
    private ControllerGyroEvent[] gyroEvents;
    private int orientationEventCount;
    private ControllerOrientationEvent[] orientationEvents;
    private int touchEventCount;
    private ControllerTouchEvent[] touchEvents;
    
    static {
        ControllerEventPacket.pool = new ArrayDeque<ControllerEventPacket>();
        ControllerEventPacket.poolLock = new Object();
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerEventPacket>() {
            public final ControllerEventPacket createFromParcel(final Parcel parcel) {
                final ControllerEventPacket obtain = ControllerEventPacket.obtain();
                obtain.readFromParcel(parcel);
                return obtain;
            }
            
            public final ControllerEventPacket[] newArray(final int n) {
                return new ControllerEventPacket[n];
            }
        };
    }
    
    public ControllerEventPacket() {
        this.accelEvents = new ControllerAccelEvent[16];
        this.buttonEvents = new ControllerButtonEvent[16];
        this.gyroEvents = new ControllerGyroEvent[16];
        this.orientationEvents = new ControllerOrientationEvent[16];
        this.touchEvents = new ControllerTouchEvent[16];
        for (int i = 0; i < 16; ++i) {
            this.accelEvents[i] = new ControllerAccelEvent();
            this.buttonEvents[i] = new ControllerButtonEvent();
            this.gyroEvents[i] = new ControllerGyroEvent();
            this.orientationEvents[i] = new ControllerOrientationEvent();
            this.touchEvents[i] = new ControllerTouchEvent();
        }
        this.clear();
    }
    
    protected ControllerEventPacket(final Parcel parcel) {
        this();
        this.readFromParcel(parcel);
    }
    
    public static ControllerEventPacket obtain() {
        synchronized (ControllerEventPacket.poolLock) {
            ControllerEventPacket controllerEventPacket;
            if (!ControllerEventPacket.pool.isEmpty()) {
                controllerEventPacket = ControllerEventPacket.pool.remove();
            }
            else {
                controllerEventPacket = new ControllerEventPacket();
            }
            return controllerEventPacket;
        }
    }
    
    public ControllerAccelEvent addAccelEvent() {
        if (this.accelEventCount < 16) {
            return this.accelEvents[this.accelEventCount++];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }
    
    public ControllerButtonEvent addButtonEvent() {
        if (this.buttonEventCount < 16) {
            return this.buttonEvents[this.buttonEventCount++];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }
    
    public ControllerGyroEvent addGyroEvent() {
        if (this.gyroEventCount < 16) {
            return this.gyroEvents[this.gyroEventCount++];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }
    
    public ControllerOrientationEvent addOrientationEvent() {
        if (this.orientationEventCount < 16) {
            return this.orientationEvents[this.orientationEventCount++];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }
    
    public ControllerTouchEvent addTouchEvent() {
        if (this.touchEventCount < 16) {
            return this.touchEvents[this.touchEventCount++];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }
    
    protected int calculateParcelByteLength() {
        final int n = 0;
        int n2 = 24;
        for (int i = 0; i < this.accelEventCount; ++i) {
            n2 += this.accelEvents[i].getByteSize();
        }
        for (int j = 0; j < this.buttonEventCount; ++j) {
            n2 += this.buttonEvents[j].getByteSize();
        }
        for (int k = 0; k < this.gyroEventCount; ++k) {
            n2 += this.gyroEvents[k].getByteSize();
        }
        for (int l = 0; l < this.orientationEventCount; ++l) {
            n2 += this.orientationEvents[l].getByteSize();
        }
        for (int n3 = n; n3 < this.touchEventCount; ++n3) {
            n2 += this.touchEvents[n3].getByteSize();
        }
        return n2;
    }
    
    protected void checkIsValidEventCount(final int i) {
        if (i >= 0 && i < 16) {
            return;
        }
        throw new IllegalArgumentException(new StringBuilder(32).append("Invalid event count: ").append(i).toString());
    }
    
    public void clear() {
        this.accelEventCount = 0;
        this.buttonEventCount = 0;
        this.gyroEventCount = 0;
        this.orientationEventCount = 0;
        this.touchEventCount = 0;
    }
    
    public void copyFrom(final ControllerEventPacket controllerEventPacket) {
        this.accelEventCount = controllerEventPacket.accelEventCount;
        this.buttonEventCount = controllerEventPacket.buttonEventCount;
        this.gyroEventCount = controllerEventPacket.gyroEventCount;
        this.orientationEventCount = controllerEventPacket.orientationEventCount;
        this.touchEventCount = controllerEventPacket.touchEventCount;
        for (int i = 0; i < 16; ++i) {
            this.accelEvents[i].copyFrom(controllerEventPacket.accelEvents[i]);
            this.buttonEvents[i].copyFrom(controllerEventPacket.buttonEvents[i]);
            this.gyroEvents[i].copyFrom(controllerEventPacket.gyroEvents[i]);
            this.orientationEvents[i].copyFrom(controllerEventPacket.orientationEvents[i]);
            this.touchEvents[i].copyFrom(controllerEventPacket.touchEvents[i]);
        }
    }
    
    public int describeContents() {
        return 0;
    }
    
    public ControllerAccelEvent getAccelEvent(final int n) {
        if (n >= 0 && n < this.accelEventCount) {
            return this.accelEvents[n];
        }
        throw new IndexOutOfBoundsException();
    }
    
    public int getAccelEventCount() {
        return this.accelEventCount;
    }
    
    public ControllerButtonEvent getButtonEvent(final int n) {
        if (n >= 0 && n < this.buttonEventCount) {
            return this.buttonEvents[n];
        }
        throw new IndexOutOfBoundsException();
    }
    
    public int getButtonEventCount() {
        return this.buttonEventCount;
    }
    
    public ControllerGyroEvent getGyroEvent(final int n) {
        if (n >= 0 && n < this.gyroEventCount) {
            return this.gyroEvents[n];
        }
        throw new IndexOutOfBoundsException();
    }
    
    public int getGyroEventCount() {
        return this.gyroEventCount;
    }
    
    public ControllerOrientationEvent getOrientationEvent(final int n) {
        if (n >= 0 && n < this.orientationEventCount) {
            return this.orientationEvents[n];
        }
        throw new IndexOutOfBoundsException();
    }
    
    public int getOrientationEventCount() {
        return this.orientationEventCount;
    }
    
    public ControllerTouchEvent getTouchEvent(final int n) {
        if (n >= 0 && n < this.touchEventCount) {
            return this.touchEvents[n];
        }
        throw new IndexOutOfBoundsException();
    }
    
    public int getTouchEventCount() {
        return this.touchEventCount;
    }
    
    public void readFromParcel(final Parcel parcel) {
        final int n = 0;
        parcel.readInt();
        this.checkIsValidEventCount(this.accelEventCount = parcel.readInt());
        for (int i = 0; i < this.accelEventCount; ++i) {
            this.accelEvents[i].readFromParcel(parcel);
        }
        this.checkIsValidEventCount(this.buttonEventCount = parcel.readInt());
        for (int j = 0; j < this.buttonEventCount; ++j) {
            this.buttonEvents[j].readFromParcel(parcel);
        }
        this.checkIsValidEventCount(this.gyroEventCount = parcel.readInt());
        for (int k = 0; k < this.gyroEventCount; ++k) {
            this.gyroEvents[k].readFromParcel(parcel);
        }
        this.checkIsValidEventCount(this.orientationEventCount = parcel.readInt());
        for (int l = 0; l < this.orientationEventCount; ++l) {
            this.orientationEvents[l].readFromParcel(parcel);
        }
        this.checkIsValidEventCount(this.touchEventCount = parcel.readInt());
        for (int n2 = n; n2 < this.touchEventCount; ++n2) {
            this.touchEvents[n2].readFromParcel(parcel);
        }
    }
    
    public void recycle() {
        this.clear();
        synchronized (ControllerEventPacket.poolLock) {
            if (!ControllerEventPacket.pool.contains(this)) {
                ControllerEventPacket.pool.add(this);
            }
        }
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        final int n2 = 0;
        parcel.writeInt(1);
        parcel.writeInt(this.accelEventCount);
        for (int i = 0; i < this.accelEventCount; ++i) {
            this.accelEvents[i].writeToParcel(parcel, n);
        }
        parcel.writeInt(this.buttonEventCount);
        for (int j = 0; j < this.buttonEventCount; ++j) {
            this.buttonEvents[j].writeToParcel(parcel, n);
        }
        parcel.writeInt(this.gyroEventCount);
        for (int k = 0; k < this.gyroEventCount; ++k) {
            this.gyroEvents[k].writeToParcel(parcel, n);
        }
        parcel.writeInt(this.orientationEventCount);
        for (int l = 0; l < this.orientationEventCount; ++l) {
            this.orientationEvents[l].writeToParcel(parcel, n);
        }
        parcel.writeInt(this.touchEventCount);
        for (int n3 = n2; n3 < this.touchEventCount; ++n3) {
            this.touchEvents[n3].writeToParcel(parcel, n);
        }
    }
}
