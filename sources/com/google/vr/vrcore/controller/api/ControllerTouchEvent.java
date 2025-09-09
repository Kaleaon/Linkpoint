package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;

public final class ControllerTouchEvent extends ControllerEvent {
    public static final int ACTION_CANCEL = 4;
    public static final int ACTION_DOWN = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_UP = 3;
    public static final Parcelable.Creator<ControllerTouchEvent> CREATOR = new Parcelable.Creator<ControllerTouchEvent>() {
        public final ControllerTouchEvent createFromParcel(Parcel parcel) {
            return new ControllerTouchEvent(parcel);
        }

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

    public final void copyFrom(ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerTouchEvent) {
            super.copyFrom(controllerEvent);
            ControllerTouchEvent controllerTouchEvent = (ControllerTouchEvent) controllerEvent;
            this.fingerId = controllerTouchEvent.fingerId;
            this.action = controllerTouchEvent.action;
            this.x = controllerTouchEvent.x;
            this.y = controllerTouchEvent.y;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerTouchEvent from non-ControllerTouchEvent instance.");
    }

    public final int describeContents() {
        return 0;
    }

    public final int getByteSize() {
        return super.getByteSize() + 8 + 8;
    }

    public final void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        this.fingerId = parcel.readInt();
        this.action = parcel.readInt();
        this.x = parcel.readFloat();
        this.y = parcel.readFloat();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.fingerId);
        parcel.writeInt(this.action);
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
    }
}
