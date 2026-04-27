// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable$Creator;

public final class ControllerAccelEvent extends ControllerEvent
{
    public static final Parcelable$Creator<ControllerAccelEvent> CREATOR;
    public float x;
    public float y;
    public float z;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerAccelEvent>() {
            public final ControllerAccelEvent createFromParcel(final Parcel parcel) {
                return new ControllerAccelEvent(parcel);
            }
            
            public final ControllerAccelEvent[] newArray(final int n) {
                return new ControllerAccelEvent[n];
            }
        };
    }
    
    public ControllerAccelEvent() {
    }
    
    public ControllerAccelEvent(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    @Override
    public final void copyFrom(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerAccelEvent) {
            super.copyFrom(controllerEvent);
            final ControllerAccelEvent controllerAccelEvent = (ControllerAccelEvent)controllerEvent;
            this.x = controllerAccelEvent.x;
            this.y = controllerAccelEvent.y;
            this.z = controllerAccelEvent.z;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerAccelEvent from non-ControllerAccelEvent instance.");
    }
    
    public final int describeContents() {
        return 0;
    }
    
    @Override
    public final int getByteSize() {
        return super.getByteSize() + 12;
    }
    
    @Override
    public final void readFromParcel(final Parcel parcel) {
        super.readFromParcel(parcel);
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
        this.z = parcel.readFloat();
    }
    
    @Override
    public final void writeToParcel(final Parcel parcel, final int n) {
        super.writeToParcel(parcel, n);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeFloat(this.z);
    }
}
