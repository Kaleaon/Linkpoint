package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public final class ControllerOrientationEvent extends ControllerEvent {
    public static final Parcelable.Creator<ControllerOrientationEvent> CREATOR = new Parcelable.Creator<ControllerOrientationEvent>() { // from class: com.google.vr.vrcore.controller.api.ControllerOrientationEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerOrientationEvent createFromParcel(Parcel parcel) {
            return new ControllerOrientationEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerOrientationEvent[] newArray(int i) {
            return new ControllerOrientationEvent[i];
        }
    };
    public float qw;
    public float qx;
    public float qy;
    public float qz;

    public ControllerOrientationEvent() {
    }

    public ControllerOrientationEvent(float f, float f2, float f3, float f4) {
        this.qx = f;
        this.qy = f2;
        this.qz = f3;
        this.qw = f4;
    }

    public ControllerOrientationEvent(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void copyFrom(ControllerEvent controllerEvent) {
        if (!(controllerEvent instanceof ControllerOrientationEvent)) {
            throw new IllegalStateException("Cannot copy ControllerOrientationEvent from non-ControllerOrientationEvent instance.");
        }
        super.copyFrom(controllerEvent);
        ControllerOrientationEvent controllerOrientationEvent = (ControllerOrientationEvent) controllerEvent;
        this.qx = controllerOrientationEvent.qx;
        this.qy = controllerOrientationEvent.qy;
        this.qz = controllerOrientationEvent.qz;
        this.qw = controllerOrientationEvent.qw;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final int getByteSize() {
        return super.getByteSize() + 16;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        this.qx = parcel.readFloat();
        this.qy = parcel.readFloat();
        this.qz = parcel.readFloat();
        this.qw = parcel.readFloat();
    }

    public final void set(ControllerOrientationEvent controllerOrientationEvent) {
        this.qx = controllerOrientationEvent.qx;
        this.qy = controllerOrientationEvent.qy;
        this.qz = controllerOrientationEvent.qz;
        this.qw = controllerOrientationEvent.qw;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(this.qx);
        parcel.writeFloat(this.qy);
        parcel.writeFloat(this.qz);
        parcel.writeFloat(this.qw);
    }
}
