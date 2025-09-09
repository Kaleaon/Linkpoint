package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayDeque;

public final class ControllerEventPacket2 extends ControllerEventPacket {
    public static final Parcelable.Creator<ControllerEventPacket2> CREATOR = new Parcelable.Creator<ControllerEventPacket2>() {
        public final ControllerEventPacket2 createFromParcel(Parcel parcel) {
            ControllerEventPacket2 obtain = ControllerEventPacket2.obtain();
            obtain.readFromParcel(parcel);
            return obtain;
        }

        public final ControllerEventPacket2[] newArray(int i) {
            return new ControllerEventPacket2[i];
        }
    };
    private static ArrayDeque<ControllerEventPacket2> pool = new ArrayDeque<>();
    private static Object poolLock = new Object();
    private int positionEventCount;
    private ControllerPositionEvent[] positionEvents = new ControllerPositionEvent[16];

    public ControllerEventPacket2() {
        for (int i = 0; i < 16; i++) {
            this.positionEvents[i] = new ControllerPositionEvent();
        }
        clear();
    }

    public static ControllerEventPacket2 obtain() {
        ControllerEventPacket2 remove;
        synchronized (poolLock) {
            remove = !pool.isEmpty() ? pool.remove() : new ControllerEventPacket2();
        }
        return remove;
    }

    public final ControllerPositionEvent addPositionEvent() {
        if (this.positionEventCount < 16) {
            ControllerPositionEvent[] controllerPositionEventArr = this.positionEvents;
            int i = this.positionEventCount;
            this.positionEventCount = i + 1;
            return controllerPositionEventArr[i];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }

    /* access modifiers changed from: protected */
    public final int calculateParcelByteLength() {
        int calculateParcelByteLength = super.calculateParcelByteLength() + 4 + 4;
        for (int i = 0; i < this.positionEventCount; i++) {
            calculateParcelByteLength += this.positionEvents[i].getByteSize();
        }
        return calculateParcelByteLength;
    }

    public final void clear() {
        super.clear();
        this.positionEventCount = 0;
    }

    public final int describeContents() {
        return 0;
    }

    public final ControllerPositionEvent getPositionEvent(int i) {
        if (i >= 0 && i < this.positionEventCount) {
            return this.positionEvents[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public final int getPositionEventCount() {
        return this.positionEventCount;
    }

    public final void readFromParcel(Parcel parcel) {
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        super.readFromParcel(parcel);
        this.positionEventCount = parcel.readInt();
        checkIsValidEventCount(this.positionEventCount);
        for (int i = 0; i < this.positionEventCount; i++) {
            this.positionEvents[i].readFromParcel(parcel);
        }
        parcel.setDataPosition(dataPosition + readInt);
    }

    public final void recycle() {
        clear();
        synchronized (poolLock) {
            if (!pool.contains(this)) {
                pool.add(this);
            }
        }
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        int calculateParcelByteLength = calculateParcelByteLength();
        parcel.writeInt(calculateParcelByteLength);
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.positionEventCount);
        for (int i2 = 0; i2 < this.positionEventCount; i2++) {
            this.positionEvents[i2].writeToParcel(parcel, i);
        }
        if (parcel.dataPosition() - dataPosition != calculateParcelByteLength) {
            throw new IllegalStateException("Parcelable implemented incorrectly, getByteSize() must return the correct size for each ControllerEvent subclass.");
        }
    }
}
