package com.google.vr.vrcore.logging.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.common.logging.nano.Vr;
/* loaded from: classes.dex */
public class VREventParcelable implements Parcelable {
    private Vr.VREvent event;
    private int eventCode;
    private static final String TAG = VREventParcelable.class.getSimpleName();
    public static final Parcelable.Creator<VREventParcelable> CREATOR = new Parcelable.Creator<VREventParcelable>() { // from class: com.google.vr.vrcore.logging.api.VREventParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final VREventParcelable createFromParcel(Parcel parcel) {
            return new VREventParcelable(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final VREventParcelable[] newArray(int i) {
            return new VREventParcelable[i];
        }
    };

    public VREventParcelable(int i, Vr.VREvent vREvent) {
        this.eventCode = i;
        this.event = vREvent;
    }

    private VREventParcelable(Parcel parcel) {
        this.eventCode = parcel.readInt();
        try {
            byte[] createByteArray = parcel.createByteArray();
            if (createByteArray.length <= 0) {
                return;
            }
            this.event = Vr.VREvent.parseFrom(createByteArray);
        } catch (Exception e) {
            String str = TAG;
            String valueOf = String.valueOf(e);
            Log.i(str, new StringBuilder(String.valueOf(valueOf).length() + 35).append("Logging with empty VREvent. Error: ").append(valueOf).toString());
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Vr.VREvent getEvent() {
        return this.event;
    }

    public int getEventCode() {
        return this.eventCode;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.eventCode);
        if (this.event == null) {
            return;
        }
        parcel.writeByteArray(Vr.VREvent.toByteArray(this.event));
    }
}
