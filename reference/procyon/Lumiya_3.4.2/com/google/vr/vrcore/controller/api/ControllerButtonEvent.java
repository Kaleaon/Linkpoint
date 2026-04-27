// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable$Creator;

public final class ControllerButtonEvent extends ControllerEvent
{
    public static final int BUTTON_APP = 3;
    public static final int BUTTON_CLICK = 1;
    public static final int BUTTON_COUNT = 7;
    public static final int BUTTON_HOME = 2;
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_VOLUME_DOWN = 6;
    public static final int BUTTON_VOLUME_UP = 5;
    public static final Parcelable$Creator<ControllerButtonEvent> CREATOR;
    public int button;
    public boolean down;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerButtonEvent>() {
            public final ControllerButtonEvent createFromParcel(final Parcel parcel) {
                return new ControllerButtonEvent(parcel);
            }
            
            public final ControllerButtonEvent[] newArray(final int n) {
                return new ControllerButtonEvent[n];
            }
        };
    }
    
    public ControllerButtonEvent() {
    }
    
    public ControllerButtonEvent(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    public static String buttonToString(final int i) {
        switch (i) {
            default: {
                return new StringBuilder(29).append("[Unknown button: ").append(i).append("]").toString();
            }
            case 0: {
                return "BUTTON_NONE";
            }
            case 1: {
                return "BUTTON_CLICK";
            }
            case 2: {
                return "BUTTON_HOME";
            }
            case 3: {
                return "BUTTON_APP";
            }
            case 5: {
                return "BUTTON_VOLUME_UP";
            }
            case 6: {
                return "BUTTON_VOLUME_DOWN";
            }
        }
    }
    
    @Override
    public final void copyFrom(final ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerButtonEvent) {
            super.copyFrom(controllerEvent);
            final ControllerButtonEvent controllerButtonEvent = (ControllerButtonEvent)controllerEvent;
            this.button = controllerButtonEvent.button;
            this.down = controllerButtonEvent.down;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerButtonEvent from non-ControllerButtonEvent instance.");
    }
    
    public final int describeContents() {
        return 0;
    }
    
    @Override
    public final int getByteSize() {
        return super.getByteSize() + 8;
    }
    
    @Override
    public final void readFromParcel(final Parcel parcel) {
        boolean down = false;
        super.readFromParcel(parcel);
        this.button = parcel.readInt();
        if (parcel.readInt() != 0) {
            down = true;
        }
        this.down = down;
    }
    
    @Override
    public final void writeToParcel(final Parcel parcel, int n) {
        final int n2 = 0;
        super.writeToParcel(parcel, n);
        parcel.writeInt(this.button);
        if (!this.down) {
            n = n2;
        }
        else {
            n = 1;
        }
        parcel.writeInt(n);
    }
}
