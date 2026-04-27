package com.google.vr.vrcore.common.api;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class HeadTrackingState implements Parcelable {
    public static final Parcelable.Creator<HeadTrackingState> CREATOR = new Parcelable.Creator<HeadTrackingState>() {
        public final HeadTrackingState createFromParcel(Parcel parcel) {
            return new HeadTrackingState(parcel);
        }

        public final HeadTrackingState[] newArray(int i) {
            return new HeadTrackingState[i];
        }
    };
    private byte[] data;

    public HeadTrackingState() {
        this.data = new byte[0];
    }

    private HeadTrackingState(Parcel parcel) {
        this.data = new byte[0];
        readFromParcel(parcel);
    }

    public HeadTrackingState(byte[] bArr) {
        this.data = new byte[0];
        this.data = bArr;
    }

    public void clear() {
        this.data = new byte[0];
    }

    public void copyFrom(HeadTrackingState headTrackingState) {
        Parcel obtain = Parcel.obtain();
        headTrackingState.writeToParcel(obtain, 0);
        obtain.setDataPosition(0);
        readFromParcel(obtain);
        obtain.recycle();
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HeadTrackingState)) {
            return false;
        }
        return Arrays.equals(((HeadTrackingState) obj).data, this.data);
    }

    public byte[] getData() {
        return this.data;
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public boolean isEmpty() {
        return this.data.length == 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.data = new byte[parcel.readInt()];
        parcel.readByteArray(this.data);
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public String toString() {
        return new StringBuilder(36).append("HeadTrackingState[").append(this.data.length).append(" bytes]").toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.data.length);
        parcel.writeByteArray(this.data);
    }
}
