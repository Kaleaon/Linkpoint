// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import java.util.Locale;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class ControllerListenerOptions implements Parcelable
{
    public static final Parcelable$Creator<ControllerListenerOptions> CREATOR;
    public boolean enableAccel;
    public boolean enableGestures;
    public boolean enableGyro;
    public boolean enableOrientation;
    public boolean enableTouch;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerListenerOptions>() {
            public final ControllerListenerOptions createFromParcel(final Parcel parcel) {
                return new ControllerListenerOptions(parcel);
            }
            
            public final ControllerListenerOptions[] newArray(final int n) {
                return new ControllerListenerOptions[n];
            }
        };
    }
    
    public ControllerListenerOptions() {
        this.enableOrientation = true;
        this.enableTouch = true;
    }
    
    protected ControllerListenerOptions(final Parcel parcel) {
        this.readFromParcel(parcel);
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void readFromParcel(final Parcel parcel) {
        final boolean b = false;
        this.enableOrientation = (parcel.readInt() != 0);
        this.enableGyro = (parcel.readInt() != 0);
        this.enableAccel = (parcel.readInt() != 0);
        this.enableTouch = (parcel.readInt() != 0);
        this.enableGestures = (parcel.readInt() != 0 || b);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.US, "ori=%b, gyro=%b, accel=%b, touch=%b, gestures=%b", this.enableOrientation, this.enableGyro, this.enableAccel, this.enableTouch, this.enableGestures);
    }
    
    public void writeToParcel(final Parcel parcel, int n) {
        final int n2 = 0;
        if (!this.enableOrientation) {
            n = 0;
        }
        else {
            n = 1;
        }
        parcel.writeInt(n);
        if (!this.enableGyro) {
            n = 0;
        }
        else {
            n = 1;
        }
        parcel.writeInt(n);
        if (!this.enableAccel) {
            n = 0;
        }
        else {
            n = 1;
        }
        parcel.writeInt(n);
        if (!this.enableTouch) {
            n = 0;
        }
        else {
            n = 1;
        }
        parcel.writeInt(n);
        if (!this.enableGestures) {
            n = n2;
        }
        else {
            n = 1;
        }
        parcel.writeInt(n);
    }
}
