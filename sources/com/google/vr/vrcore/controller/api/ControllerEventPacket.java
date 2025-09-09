package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayDeque;

@Deprecated
public class ControllerEventPacket implements Parcelable {
    public static final Parcelable.Creator<ControllerEventPacket> CREATOR = new Parcelable.Creator<ControllerEventPacket>() {
        public final ControllerEventPacket createFromParcel(Parcel parcel) {
            ControllerEventPacket obtain = ControllerEventPacket.obtain();
            obtain.readFromParcel(parcel);
            return obtain;
        }

        public final ControllerEventPacket[] newArray(int i) {
            return new ControllerEventPacket[i];
        }
    };
    protected static final int MAX_EVENTS = 16;
    private static final int SERIALIZED_FORMAT_VERSION = 1;
    private static ArrayDeque<ControllerEventPacket> pool = new ArrayDeque<>();
    private static Object poolLock = new Object();
    private int accelEventCount;
    private ControllerAccelEvent[] accelEvents;
    private int buttonEventCount;
    private ControllerButtonEvent[] buttonEvents;
    private int gyroEventCount;
    private ControllerGyroEvent[] gyroEvents;
    private int orientationEventCount;
    private ControllerOrientationEvent[] orientationEvents;
    private int touchEventCount;
    private ControllerTouchEvent[] touchEvents;

    public ControllerEventPacket() {
        this.accelEvents = new ControllerAccelEvent[16];
        this.buttonEvents = new ControllerButtonEvent[16];
        this.gyroEvents = new ControllerGyroEvent[16];
        this.orientationEvents = new ControllerOrientationEvent[16];
        this.touchEvents = new ControllerTouchEvent[16];
        for (int i = 0; i < 16; i++) {
            this.accelEvents[i] = new ControllerAccelEvent();
            this.buttonEvents[i] = new ControllerButtonEvent();
            this.gyroEvents[i] = new ControllerGyroEvent();
            this.orientationEvents[i] = new ControllerOrientationEvent();
            this.touchEvents[i] = new ControllerTouchEvent();
        }
        clear();
    }

    protected ControllerEventPacket(Parcel parcel) {
        this();
        readFromParcel(parcel);
    }

    public static ControllerEventPacket obtain() {
        ControllerEventPacket remove;
        synchronized (poolLock) {
            remove = !pool.isEmpty() ? pool.remove() : new ControllerEventPacket();
        }
        return remove;
    }

    public ControllerAccelEvent addAccelEvent() {
        if (this.accelEventCount < 16) {
            ControllerAccelEvent[] controllerAccelEventArr = this.accelEvents;
            int i = this.accelEventCount;
            this.accelEventCount = i + 1;
            return controllerAccelEventArr[i];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }

    public ControllerButtonEvent addButtonEvent() {
        if (this.buttonEventCount < 16) {
            ControllerButtonEvent[] controllerButtonEventArr = this.buttonEvents;
            int i = this.buttonEventCount;
            this.buttonEventCount = i + 1;
            return controllerButtonEventArr[i];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }

    public ControllerGyroEvent addGyroEvent() {
        if (this.gyroEventCount < 16) {
            ControllerGyroEvent[] controllerGyroEventArr = this.gyroEvents;
            int i = this.gyroEventCount;
            this.gyroEventCount = i + 1;
            return controllerGyroEventArr[i];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }

    public ControllerOrientationEvent addOrientationEvent() {
        if (this.orientationEventCount < 16) {
            ControllerOrientationEvent[] controllerOrientationEventArr = this.orientationEvents;
            int i = this.orientationEventCount;
            this.orientationEventCount = i + 1;
            return controllerOrientationEventArr[i];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }

    public ControllerTouchEvent addTouchEvent() {
        if (this.touchEventCount < 16) {
            ControllerTouchEvent[] controllerTouchEventArr = this.touchEvents;
            int i = this.touchEventCount;
            this.touchEventCount = i + 1;
            return controllerTouchEventArr[i];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }

    /* access modifiers changed from: protected */
    public int calculateParcelByteLength() {
        int i = 24;
        for (int i2 = 0; i2 < this.accelEventCount; i2++) {
            i += this.accelEvents[i2].getByteSize();
        }
        for (int i3 = 0; i3 < this.buttonEventCount; i3++) {
            i += this.buttonEvents[i3].getByteSize();
        }
        for (int i4 = 0; i4 < this.gyroEventCount; i4++) {
            i += this.gyroEvents[i4].getByteSize();
        }
        for (int i5 = 0; i5 < this.orientationEventCount; i5++) {
            i += this.orientationEvents[i5].getByteSize();
        }
        for (int i6 = 0; i6 < this.touchEventCount; i6++) {
            i += this.touchEvents[i6].getByteSize();
        }
        return i;
    }

    /* access modifiers changed from: protected */
    public void checkIsValidEventCount(int i) {
        if (i < 0 || i >= 16) {
            throw new IllegalArgumentException(new StringBuilder(32).append("Invalid event count: ").append(i).toString());
        }
    }

    public void clear() {
        this.accelEventCount = 0;
        this.buttonEventCount = 0;
        this.gyroEventCount = 0;
        this.orientationEventCount = 0;
        this.touchEventCount = 0;
    }

    public void copyFrom(ControllerEventPacket controllerEventPacket) {
        this.accelEventCount = controllerEventPacket.accelEventCount;
        this.buttonEventCount = controllerEventPacket.buttonEventCount;
        this.gyroEventCount = controllerEventPacket.gyroEventCount;
        this.orientationEventCount = controllerEventPacket.orientationEventCount;
        this.touchEventCount = controllerEventPacket.touchEventCount;
        for (int i = 0; i < 16; i++) {
            this.accelEvents[i].copyFrom(controllerEventPacket.accelEvents[i]);
            this.buttonEvents[i].copyFrom(controllerEventPacket.buttonEvents[i]);
            this.gyroEvents[i].copyFrom(controllerEventPacket.gyroEvents[i]);
            this.orientationEvents[i].copyFrom(controllerEventPacket.orientationEvents[i]);
            this.touchEvents[i].copyFrom(controllerEventPacket.touchEvents[i]);
        }
    }

    public int describeContents() {
        return 0;
    }

    public ControllerAccelEvent getAccelEvent(int i) {
        if (i >= 0 && i < this.accelEventCount) {
            return this.accelEvents[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int getAccelEventCount() {
        return this.accelEventCount;
    }

    public ControllerButtonEvent getButtonEvent(int i) {
        if (i >= 0 && i < this.buttonEventCount) {
            return this.buttonEvents[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int getButtonEventCount() {
        return this.buttonEventCount;
    }

    public ControllerGyroEvent getGyroEvent(int i) {
        if (i >= 0 && i < this.gyroEventCount) {
            return this.gyroEvents[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int getGyroEventCount() {
        return this.gyroEventCount;
    }

    public ControllerOrientationEvent getOrientationEvent(int i) {
        if (i >= 0 && i < this.orientationEventCount) {
            return this.orientationEvents[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int getOrientationEventCount() {
        return this.orientationEventCount;
    }

    public ControllerTouchEvent getTouchEvent(int i) {
        if (i >= 0 && i < this.touchEventCount) {
            return this.touchEvents[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int getTouchEventCount() {
        return this.touchEventCount;
    }

    public void readFromParcel(Parcel parcel) {
        parcel.readInt();
        this.accelEventCount = parcel.readInt();
        checkIsValidEventCount(this.accelEventCount);
        for (int i = 0; i < this.accelEventCount; i++) {
            this.accelEvents[i].readFromParcel(parcel);
        }
        this.buttonEventCount = parcel.readInt();
        checkIsValidEventCount(this.buttonEventCount);
        for (int i2 = 0; i2 < this.buttonEventCount; i2++) {
            this.buttonEvents[i2].readFromParcel(parcel);
        }
        this.gyroEventCount = parcel.readInt();
        checkIsValidEventCount(this.gyroEventCount);
        for (int i3 = 0; i3 < this.gyroEventCount; i3++) {
            this.gyroEvents[i3].readFromParcel(parcel);
        }
        this.orientationEventCount = parcel.readInt();
        checkIsValidEventCount(this.orientationEventCount);
        for (int i4 = 0; i4 < this.orientationEventCount; i4++) {
            this.orientationEvents[i4].readFromParcel(parcel);
        }
        this.touchEventCount = parcel.readInt();
        checkIsValidEventCount(this.touchEventCount);
        for (int i5 = 0; i5 < this.touchEventCount; i5++) {
            this.touchEvents[i5].readFromParcel(parcel);
        }
    }

    public void recycle() {
        clear();
        synchronized (poolLock) {
            if (!pool.contains(this)) {
                pool.add(this);
            }
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(1);
        parcel.writeInt(this.accelEventCount);
        for (int i2 = 0; i2 < this.accelEventCount; i2++) {
            this.accelEvents[i2].writeToParcel(parcel, i);
        }
        parcel.writeInt(this.buttonEventCount);
        for (int i3 = 0; i3 < this.buttonEventCount; i3++) {
            this.buttonEvents[i3].writeToParcel(parcel, i);
        }
        parcel.writeInt(this.gyroEventCount);
        for (int i4 = 0; i4 < this.gyroEventCount; i4++) {
            this.gyroEvents[i4].writeToParcel(parcel, i);
        }
        parcel.writeInt(this.orientationEventCount);
        for (int i5 = 0; i5 < this.orientationEventCount; i5++) {
            this.orientationEvents[i5].writeToParcel(parcel, i);
        }
        parcel.writeInt(this.touchEventCount);
        for (int i6 = 0; i6 < this.touchEventCount; i6++) {
            this.touchEvents[i6].writeToParcel(parcel, i);
        }
    }
}
