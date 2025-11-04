// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable$Creator;

public final class ControllerTouchEvent extends ControllerEvent
{
    public static final int ACTION_CANCEL = 4;
    public static final int ACTION_DOWN = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_UP = 3;
    public static final Parcelable$Creator<ControllerTouchEvent> CREATOR;
    public int action;
    public int fingerId;
    public float x;
    public float y;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerTouchEvent>() {
            public final ControllerTouchEvent createFromParcel(final Parcel parcel) {
                return new ControllerTouchEvent(parcel);
            }
            
            public final ControllerTouchEvent[] newArray(final int n) {
                return new ControllerTouchEvent[n];
            }
        };
    }
    
    public ControllerTouchEvent() {
    }
    
    public ControllerTouchEvent(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    @Override
    public final void copyFrom(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerTouchEvent) {
            super.copyFrom(controllerEvent);
            final ControllerTouchEvent controllerTouchEvent = (ControllerTouchEvent)controllerEvent;
            this.fingerId = controllerTouchEvent.fingerId;
            this.action = controllerTouchEvent.action;
            this.x = controllerTouchEvent.x;
            this.y = controllerTouchEvent.y;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerTouchEvent from non-ControllerTouchEvent instance.");
    }
    
    public final int describeContents() {
        return 0;
    }
    
    @Override
    public final int getByteSize() {
        return super.getByteSize() + 8 + 8;
    }
    
    @Override
    public final void readFromParcel(final Parcel parcel) {
        super.readFromParcel(parcel);
        this.fingerId = parcel.readInt();
        this.action = parcel.readInt();
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
    }
    
    @Override
    public final void writeToParcel(final Parcel parcel, final int n) {
        super.writeToParcel(parcel, n);
        parcel.writeInt(this.fingerId);
        parcel.writeInt(this.action);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
    }
}
