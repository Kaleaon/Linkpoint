// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable$Creator;

public final class ControllerOrientationEvent extends ControllerEvent
{
    public static final Parcelable$Creator<ControllerOrientationEvent> CREATOR;
    public float qw;
    public float qx;
    public float qy;
    public float qz;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerOrientationEvent>() {
            public final ControllerOrientationEvent createFromParcel(final Parcel parcel) {
                return new ControllerOrientationEvent(parcel);
            }
            
            public final ControllerOrientationEvent[] newArray(final int n) {
                return new ControllerOrientationEvent[n];
            }
        };
    }
    
    public ControllerOrientationEvent() {
    }
    
    public ControllerOrientationEvent(final float qx, final float qy, final float qz, final float qw) {
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
        this.qw = qw;
    }
    
    public ControllerOrientationEvent(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    @Override
    public final void copyFrom(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerOrientationEvent) {
            super.copyFrom(controllerEvent);
            final ControllerOrientationEvent controllerOrientationEvent = (ControllerOrientationEvent)controllerEvent;
            this.qx = controllerOrientationEvent.qx;
            this.qy = controllerOrientationEvent.qy;
            this.qz = controllerOrientationEvent.qz;
            this.qw = controllerOrientationEvent.qw;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerOrientationEvent from non-ControllerOrientationEvent instance.");
    }
    
    public final int describeContents() {
        return 0;
    }
    
    @Override
    public final int getByteSize() {
        return super.getByteSize() + 16;
    }
    
    @Override
    public final void readFromParcel(final Parcel parcel) {
        super.readFromParcel(parcel);
        this.qx = parcel.readFloat();
        this.qy = parcel.readFloat();
        this.qz = parcel.readFloat();
        this.qw = parcel.readFloat();
    }
    
    public final void set(final ControllerOrientationEvent controllerOrientationEvent) {
        this.qx = controllerOrientationEvent.qx;
        this.qy = controllerOrientationEvent.qy;
        this.qz = controllerOrientationEvent.qz;
        this.qw = controllerOrientationEvent.qw;
    }
    
    @Override
    public final void writeToParcel(final Parcel parcel, final int n) {
        super.writeToParcel(parcel, n);
        parcel.writeFloat(this.qx);
        parcel.writeFloat(this.qy);
        parcel.writeFloat(this.qz);
        parcel.writeFloat(this.qw);
    }
}
