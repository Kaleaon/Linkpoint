package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public final class ControllerAccelEvent extends ControllerEvent {
    public static final Parcelable.Creator<ControllerAccelEvent> CREATOR = new Parcelable.Creator<ControllerAccelEvent>() { // from class: com.google.vr.vrcore.controller.api.ControllerAccelEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerAccelEvent createFromParcel(Parcel parcel) {
            return new ControllerAccelEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
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

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void copyFrom(ControllerEvent controllerEvent) {
        if (!(controllerEvent instanceof ControllerAccelEvent)) {
            throw new IllegalStateException("Cannot copy ControllerAccelEvent from non-ControllerAccelEvent instance.");
        }
        super.copyFrom(controllerEvent);
        ControllerAccelEvent controllerAccelEvent = (ControllerAccelEvent) controllerEvent;
        this.x = controllerAccelEvent.x;
        this.y = controllerAccelEvent.y;
        this.z = controllerAccelEvent.z;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final int getByteSize() {
        return super.getByteSize() + 12;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
        this.z = parcel.readFloat();
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
        parcel.writeFloat(this.z);
    }
}
