// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.common.api;

import java.util.Arrays;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class HeadTrackingState implements Parcelable
{
    public static final Parcelable$Creator<HeadTrackingState> CREATOR;
    private byte[] data;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<HeadTrackingState>() {
            public final HeadTrackingState createFromParcel(final Parcel parcel) {
                return new HeadTrackingState(parcel, null);
            }
            
            public final HeadTrackingState[] newArray(final int n) {
                return new HeadTrackingState[n];
            }
        };
    }
    
    public HeadTrackingState() {
        this.data = new byte[0];
    }
    
    private HeadTrackingState(final Parcel parcel) {
        this.data = new byte[0];
        this.readFromParcel(parcel);
    }
    
    public HeadTrackingState(final byte[] data) {
        this.data = new byte[0];
        this.data = data;
    }
    
    public void clear() {
        this.data = new byte[0];
    }
    
    public void copyFrom(final HeadTrackingState headTrackingState) {
        final Parcel obtain = Parcel.obtain();
        headTrackingState.writeToParcel(obtain, 0);
        obtain.setDataPosition(0);
        this.readFromParcel(obtain);
        obtain.recycle();
    }
    
    public int describeContents() {
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof HeadTrackingState && Arrays.equals(((HeadTrackingState)o).data, this.data));
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    public boolean isEmpty() {
        return this.data.length == 0;
    }
    
    public void readFromParcel(final Parcel parcel) {
        parcel.readByteArray(this.data = new byte[parcel.readInt()]);
    }
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return new StringBuilder(36).append("HeadTrackingState[").append(this.data.length).append(" bytes]").toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.data.length);
        parcel.writeByteArray(this.data);
    }
}
