package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;

public final class ControllerAccelEvent extends ControllerEvent {
    public static final Parcelable.Creator<ControllerAccelEvent> CREATOR = new Parcelable.Creator<ControllerAccelEvent>() {
        public final ControllerAccelEvent createFromParcel(Parcel parcel) {
            return new ControllerAccelEvent(parcel);
        }

        public final ControllerAccelEvent[] newArray(int i) {
            return new ControllerAccelEvent[i];
        }
    };
    public float x;
    public float y;
    public float z;

    public ControllerAccelEvent() {
    }

    public ControllerAccelEvent(Parcel parcel) {
        readFromParcel(parcel);
    }

    public final void copyFrom(ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerAccelEvent) {
            super.copyFrom(controllerEvent);
            ControllerAccelEvent controllerAccelEvent = (ControllerAccelEvent) controllerEvent;
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
