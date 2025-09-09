package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ControllerEvent implements Parcelable {
    public static final int CONTROLLER_ID_DEFAULT = 0;
    public int controllerId = 0;
    public long timestampNanos;

    public void copyFrom(ControllerEvent controllerEvent) {
        this.timestampNanos = controllerEvent.timestampNanos;
        this.controllerId = controllerEvent.controllerId;
    }

    public int getByteSize() {
        return 12;
    }

    public void readFromParcel(Parcel parcel) {
        this.timestampNanos = parcel.readLong();
        this.controllerId = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.timestampNanos);
        parcel.writeInt(this.controllerId);
    }
}
