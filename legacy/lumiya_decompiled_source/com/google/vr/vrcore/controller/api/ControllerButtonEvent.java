package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public final class ControllerButtonEvent extends ControllerEvent {
    public static final int BUTTON_APP = 3;
    public static final int BUTTON_CLICK = 1;
    public static final int BUTTON_COUNT = 7;
    public static final int BUTTON_HOME = 2;
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_VOLUME_DOWN = 6;
    public static final int BUTTON_VOLUME_UP = 5;
    public static final Parcelable.Creator<ControllerButtonEvent> CREATOR = new Parcelable.Creator<ControllerButtonEvent>() { // from class: com.google.vr.vrcore.controller.api.ControllerButtonEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final ControllerButtonEvent createFromParcel(Parcel parcel) {
            return new ControllerButtonEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
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
            case 4:
            default:
                return new StringBuilder(29).append("[Unknown button: ").append(i).append("]").toString();
            case 5:
                return "BUTTON_VOLUME_UP";
            case 6:
                return "BUTTON_VOLUME_DOWN";
        }
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void copyFrom(ControllerEvent controllerEvent) {
        if (!(controllerEvent instanceof ControllerButtonEvent)) {
            throw new IllegalStateException("Cannot copy ControllerButtonEvent from non-ControllerButtonEvent instance.");
        }
        super.copyFrom(controllerEvent);
        ControllerButtonEvent controllerButtonEvent = (ControllerButtonEvent) controllerEvent;
        this.button = controllerButtonEvent.button;
        this.down = controllerButtonEvent.down;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final int getByteSize() {
        return super.getByteSize() + 8;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent
    public final void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        this.button = parcel.readInt();
        this.down = parcel.readInt() != 0;
    }

    @Override // com.google.vr.vrcore.controller.api.ControllerEvent, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.button);
        parcel.writeInt(this.down ? 1 : 0);
    }
}
