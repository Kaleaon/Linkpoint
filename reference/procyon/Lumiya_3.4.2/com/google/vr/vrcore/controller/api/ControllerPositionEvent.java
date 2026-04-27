// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable$Creator;

public final class ControllerPositionEvent extends ControllerEvent
{
    public static final Parcelable$Creator<ControllerPositionEvent> CREATOR;
    public float x;
    public float y;
    public float z;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerPositionEvent>() {
            public final ControllerPositionEvent createFromParcel(final Parcel parcel) {
                return new ControllerPositionEvent(parcel);
            }
            
            public final ControllerPositionEvent[] newArray(final int n) {
                return new ControllerPositionEvent[n];
            }
        };
    }
    
    public ControllerPositionEvent() {
    }
    
    public ControllerPositionEvent(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    @Override
    public final void copyFrom(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerPositionEvent) {
            super.copyFrom(controllerEvent);
            final ControllerPositionEvent controllerPositionEvent = (ControllerPositionEvent)controllerEvent;
            this.x = controllerPositionEvent.x;
            this.y = controllerPositionEvent.y;
            this.z = controllerPositionEvent.z;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerPositionEvent from non-ControllerPositionEvent instance.");
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
