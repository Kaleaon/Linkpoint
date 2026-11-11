package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public final class ControllerTouchEvent extends ControllerEvent {
    public static final int ACTION_CANCEL = 4;
    public static final int ACTION_DOWN = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_UP = 3;
    public static final Parcelable.Creator<ControllerTouchEvent> CREATOR = new Parcelable.Creator<ControllerTouchEvent>() { // from class: com.google.vr.vrcore.controller.api.ControllerTouchEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerTouchEvent createFromParcel(Parcel parcel) {
            return new ControllerTouchEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerTouchEvent[] newArray(int i) {
            return new ControllerTouchEvent[i];
        }
    };
    public int action;
    public int fingerId;
    public float x;
    public float y;

    public ControllerTouchEvent() {
    }

    public ControllerTouchEvent(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void copyFrom(ControllerEvent controllerEvent) {
        if (!(controllerEvent instanceof ControllerTouchEvent)) {
            throw new IllegalStateException("Cannot copy ControllerTouchEvent from non-ControllerTouchEvent instance.");
        }
        super.copyFrom(controllerEvent);
        ControllerTouchEvent controllerTouchEvent = (ControllerTouchEvent) controllerEvent;
        this.fingerId = controllerTouchEvent.fingerId;
        this.action = controllerTouchEvent.action;
        this.x = controllerTouchEvent.x;
        this.y = controllerTouchEvent.y;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final int getByteSize() {
        return super.getByteSize() + 8 + 8;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        this.fingerId = parcel.readInt();
        this.action = parcel.readInt();
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.fingerId);
        parcel.writeInt(this.action);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
    }
}
