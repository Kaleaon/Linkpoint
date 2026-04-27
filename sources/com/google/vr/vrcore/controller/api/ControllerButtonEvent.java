package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;

public final class ControllerButtonEvent extends ControllerEvent {
    public static final int BUTTON_APP = 3;
    public static final int BUTTON_CLICK = 1;
    public static final int BUTTON_COUNT = 7;
    public static final int BUTTON_HOME = 2;
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_VOLUME_DOWN = 6;
    public static final int BUTTON_VOLUME_UP = 5;
    public static final Parcelable.Creator<ControllerButtonEvent> CREATOR = new Parcelable.Creator<ControllerButtonEvent>() {
        public final ControllerButtonEvent createFromParcel(Parcel parcel) {
            return new ControllerButtonEvent(parcel);
        }

        public final ControllerButtonEvent[] newArray(int i) {
            return new ControllerButtonEvent[i];
        }
    };
    public int button;
    public boolean down;

    public ControllerButtonEvent() {
    }

    public ControllerButtonEvent(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static String buttonToString(int i) {
        switch (i) {
            case 0:
                return "BUTTON_NONE";
            case 1:
                return "BUTTON_CLICK";
            case 2:
                return "BUTTON_HOME";
            case 3:
                return "BUTTON_APP";
            case 5:
                return "BUTTON_VOLUME_UP";
            case 6:
                return "BUTTON_VOLUME_DOWN";
            default:
                return new StringBuilder(29).append("[Unknown button: ").append(i).append("]").toString();
        }
    }

    public final void copyFrom(ControllerEvent controllerEvent) {
        if (controllerEvent instanceof ControllerButtonEvent) {
            super.copyFrom(controllerEvent);
            ControllerButtonEvent controllerButtonEvent = (ControllerButtonEvent) controllerEvent;
            this.button = controllerButtonEvent.button;
            this.down = controllerButtonEvent.down;
            return;
        }
        throw new IllegalStateException("Cannot copy ControllerButtonEvent from non-ControllerButtonEvent instance.");
    }

    public final int describeContents() {
        return 0;
    }

    public final int getByteSize() {
        return super.getByteSize() + 8;
    }

    public final void readFromParcel(Parcel parcel) {
        boolean z = false;
        super.readFromParcel(parcel);
        this.button = parcel.readInt();
        if (parcel.readInt() != 0) {
            z = true;
        }
        this.down = z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int i2 = 0;
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.button);
        if (this.down) {
            i2 = 1;
        }
        parcel.writeInt(i2);
    }
}
