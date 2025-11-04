// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ControllerEvent implements Parcelable
{
    public static final int CONTROLLER_ID_DEFAULT = 0;
    public int controllerId;
    public long timestampNanos;
    
    public ControllerEvent() {
        this.controllerId = 0;
    }
    
    public void copyFrom(final ControllerEvent controllerEvent) {
        this.timestampNanos = controllerEvent.timestampNanos;
        this.controllerId = controllerEvent.controllerId;
    }
    
    public int getByteSize() {
        return 12;
    }
    
    public void readFromParcel(final Parcel parcel) {
        this.timestampNanos = parcel.readLong();
        this.controllerId = parcel.readInt();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeLong(this.timestampNanos);
        parcel.writeInt(this.controllerId);
    }
}
