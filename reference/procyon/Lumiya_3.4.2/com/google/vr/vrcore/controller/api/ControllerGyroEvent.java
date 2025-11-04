// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable$Creator;

public final class ControllerGyroEvent extends ControllerEvent
{
    public static final Parcelable$Creator<ControllerGyroEvent> CREATOR;
    public float x;
    public float y;
    public float z;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerGyroEvent>() {
            public final ControllerGyroEvent createFromParcel(final Parcel parcel) {
                return new ControllerGyroEvent(parcel);
            }
            
            public final ControllerGyroEvent[] newArray(final int n) {
                return new ControllerGyroEvent[n];
            }
        };
    }
    
    public ControllerGyroEvent() {
    }
    
    public ControllerGyroEvent(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    @Override
    public final void copyFrom(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerGyroEvent) {
            super.copyFrom(controllerEvent);
            final ControllerGyroEvent controllerGyroEvent = (ControllerGyroEvent)controllerEvent;
            this.x = controllerGyroEvent.x;
            this.y = controllerGyroEvent.y;
            this.z = controllerGyroEvent.z;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerGyroEvent from non-ControllerGyroEvent instance.");
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
