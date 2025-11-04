// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.logging.api;

import com.google.protobuf.nano.MessageNano;
import android.util.Log;
import android.os.Parcel;
import com.google.common.logging.nano.Vr;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class VREventParcelable implements Parcelable
{
    public static final Parcelable$Creator<VREventParcelable> CREATOR;
    private static final String TAG;
    private Vr.VREvent event;
    private int eventCode;
    
    static {
        TAG = VREventParcelable.class.getSimpleName();
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<VREventParcelable>() {
            public final VREventParcelable createFromParcel(final Parcel parcel) {
                return new VREventParcelable(parcel, null);
            }
            
            public final VREventParcelable[] newArray(final int n) {
                return new VREventParcelable[n];
            }
        };
    }
    
    public VREventParcelable(final int eventCode, final Vr.VREvent event) {
        this.eventCode = eventCode;
        this.event = event;
    }
    
    private VREventParcelable(final Parcel parcel) {
        this.eventCode = parcel.readInt();
        try {
            final byte[] byteArray = parcel.createByteArray();
            if (byteArray.length > 0) {
                this.event = Vr.VREvent.parseFrom(byteArray);
            }
        }
        catch (final Exception obj) {
            final String tag = VREventParcelable.TAG;
            final String value = String.valueOf(obj);
            Log.i(tag, new StringBuilder(String.valueOf(value).length() + 35).append("Logging with empty VREvent. Error: ").append(value).toString());
        }
    }
    
    public int describeContents() {
        return 0;
    }
    
    public Vr.VREvent getEvent() {
        return this.event;
    }
    
    public int getEventCode() {
        return this.eventCode;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.eventCode);
        if (this.event != null) {
            parcel.writeByteArray(MessageNano.toByteArray(this.event));
        }
    }
}
