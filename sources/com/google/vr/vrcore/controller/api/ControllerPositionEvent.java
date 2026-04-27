package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;

public final class ControllerPositionEvent extends ControllerEvent {
    public static final Parcelable.Creator<ControllerPositionEvent> CREATOR = new Parcelable.Creator<ControllerPositionEvent>() {
        public final ControllerPositionEvent createFromParcel(Parcel parcel) {
            return new ControllerPositionEvent(parcel);
        }

        public final ControllerPositionEvent[] newArray(int i) {
            return new ControllerPositionEvent[i];
        }
    };
    public float x;
    public float y;
    public float z;

    public ControllerPositionEvent() {
    }

    public ControllerPositionEvent(Parcel parcel) {
        readFromParcel(parcel);
    }

    public final void copyFrom(ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerPositionEvent) {
            super.copyFrom(controllerEvent);
            ControllerPositionEvent controllerPositionEvent = (ControllerPositionEvent) controllerEvent;
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

    public final int getByteSize() {
        return super.getByteSize() + 12;
    }

    public final void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
        this.z = parcel.readFloat();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeFloat(this.z);
    }
}
