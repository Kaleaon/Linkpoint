package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;
/* loaded from: classes.dex */
public class ControllerListenerOptions implements Parcelable {
    public static final Parcelable.Creator<ControllerListenerOptions> CREATOR = new Parcelable.Creator<ControllerListenerOptions>() { // from class: com.google.vr.vrcore.controller.api.ControllerListenerOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerListenerOptions createFromParcel(Parcel parcel) {
            return new ControllerListenerOptions(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
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

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.enableOrientation = parcel.readInt() != 0;
        this.enableGyro = parcel.readInt() != 0;
        this.enableAccel = parcel.readInt() != 0;
        this.enableTouch = parcel.readInt() != 0;
        this.enableGestures = parcel.readInt() != 0;
    }

    public String toString() {
        return String.format(Locale.US, "ori=%b, gyro=%b, accel=%b, touch=%b, gestures=%b", Boolean.valueOf(this.enableOrientation), Boolean.valueOf(this.enableGyro), Boolean.valueOf(this.enableAccel), Boolean.valueOf(this.enableTouch), Boolean.valueOf(this.enableGestures));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(!this.enableOrientation ? 0 : 1);
        parcel.writeInt(!this.enableGyro ? 0 : 1);
        parcel.writeInt(!this.enableAccel ? 0 : 1);
        parcel.writeInt(!this.enableTouch ? 0 : 1);
        parcel.writeInt(this.enableGestures ? 1 : 0);
    }
}
