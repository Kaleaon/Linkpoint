package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

public class ControllerListenerOptions implements Parcelable {
    public static final Parcelable.Creator<ControllerListenerOptions> CREATOR = new Parcelable.Creator<ControllerListenerOptions>() {
        public final ControllerListenerOptions createFromParcel(Parcel parcel) {
            return new ControllerListenerOptions(parcel);
        }

        public final ControllerListenerOptions[] newArray(int i) {
            return new ControllerListenerOptions[i];
        }
    };
    public boolean enableAccel;
    public boolean enableGestures;
    public boolean enableGyro;
    public boolean enableOrientation;
    public boolean enableTouch;

    public ControllerListenerOptions() {
        this.enableOrientation = true;
        this.enableTouch = true;
    }

    protected ControllerListenerOptions(Parcel parcel) {
        readFromParcel(parcel);
    }

    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        boolean z = false;
        this.enableOrientation = parcel.readInt() != 0;
        this.enableGyro = parcel.readInt() != 0;
        this.enableAccel = parcel.readInt() != 0;
        this.enableTouch = parcel.readInt() != 0;
        if (parcel.readInt() != 0) {
            z = true;
        }
        this.enableGestures = z;
    }

    public String toString() {
        return String.format(Locale.US, "ori=%b, gyro=%b, accel=%b, touch=%b, gestures=%b", new Object[]{Boolean.valueOf(this.enableOrientation), Boolean.valueOf(this.enableGyro), Boolean.valueOf(this.enableAccel), Boolean.valueOf(this.enableTouch), Boolean.valueOf(this.enableGestures)});
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 0;
        parcel.writeInt(!this.enableOrientation ? 0 : 1);
        parcel.writeInt(!this.enableGyro ? 0 : 1);
        parcel.writeInt(!this.enableAccel ? 0 : 1);
        parcel.writeInt(!this.enableTouch ? 0 : 1);
        if (this.enableGestures) {
            i2 = 1;
        }
        parcel.writeInt(i2);
    }
}
