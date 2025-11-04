// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import java.util.ArrayDeque;
import android.os.Parcelable$Creator;

public final class ControllerEventPacket2 extends ControllerEventPacket
{
    public static final Parcelable$Creator<ControllerEventPacket2> CREATOR;
    private static ArrayDeque<ControllerEventPacket2> pool;
    private static Object poolLock;
    private int positionEventCount;
    private ControllerPositionEvent[] positionEvents;
    
    static {
        ControllerEventPacket2.pool = new ArrayDeque<ControllerEventPacket2>();
        ControllerEventPacket2.poolLock = new Object();
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ControllerEventPacket2>() {
            public final ControllerEventPacket2 createFromParcel(final Parcel parcel) {
                final ControllerEventPacket2 obtain = ControllerEventPacket2.obtain();
                obtain.readFromParcel(parcel);
                return obtain;
            }
            
            public final ControllerEventPacket2[] newArray(final int n) {
                return new ControllerEventPacket2[n];
            }
        };
    }
    
    public ControllerEventPacket2() {
        this.positionEvents = new ControllerPositionEvent[16];
        for (int i = 0; i < 16; ++i) {
            this.positionEvents[i] = new ControllerPositionEvent();
        }
        this.clear();
    }
    
    public static ControllerEventPacket2 obtain() {
        synchronized (ControllerEventPacket2.poolLock) {
            ControllerEventPacket2 controllerEventPacket2;
            if (!ControllerEventPacket2.pool.isEmpty()) {
                controllerEventPacket2 = ControllerEventPacket2.pool.remove();
            }
            else {
                controllerEventPacket2 = new ControllerEventPacket2();
            }
            return controllerEventPacket2;
        }
    }
    
    public final ControllerPositionEvent addPositionEvent() {
        if (this.positionEventCount < 16) {
            return this.positionEvents[this.positionEventCount++];
        }
        throw new IllegalStateException("ControllerEventPacket capacity exceeded.");
    }
    
    @Override
    protected final int calculateParcelByteLength() {
        int n = super.calculateParcelByteLength() + 4 + 4;
        for (int i = 0; i < this.positionEventCount; ++i) {
            n += this.positionEvents[i].getByteSize();
        }
        return n;
    }
    
    @Override
    public final void clear() {
        super.clear();
        this.positionEventCount = 0;
    }
    
    @Override
    public final int describeContents() {
        return 0;
    }
    
    public final ControllerPositionEvent getPositionEvent(final int n) {
        if (n >= 0 && n < this.positionEventCount) {
            return this.positionEvents[n];
        }
        throw new IndexOutOfBoundsException();
    }
    
    public final int getPositionEventCount() {
        return this.positionEventCount;
    }
    
    @Override
    public final void readFromParcel(final Parcel parcel) {
        final int dataPosition = parcel.dataPosition();
        final int int1 = parcel.readInt();
        super.readFromParcel(parcel);
        this.checkIsValidEventCount(this.positionEventCount = parcel.readInt());
        for (int i = 0; i < this.positionEventCount; ++i) {
            this.positionEvents[i].readFromParcel(parcel);
        }
        parcel.setDataPosition(dataPosition + int1);
    }
    
    @Override
    public final void recycle() {
        this.clear();
        synchronized (ControllerEventPacket2.poolLock) {
            if (!ControllerEventPacket2.pool.contains(this)) {
                ControllerEventPacket2.pool.add(this);
            }
        }
    }
    
    @Override
    public final void writeToParcel(final Parcel parcel, final int n) {
        final int dataPosition = parcel.dataPosition();
        final int calculateParcelByteLength = this.calculateParcelByteLength();
        parcel.writeInt(calculateParcelByteLength);
        super.writeToParcel(parcel, n);
        parcel.writeInt(this.positionEventCount);
        for (int i = 0; i < this.positionEventCount; ++i) {
            this.positionEvents[i].writeToParcel(parcel, n);
        }
        if (parcel.dataPosition() - dataPosition == calculateParcelByteLength) {
            return;
        }
        throw new IllegalStateException("Parcelable implemented incorrectly, getByteSize() must return the correct size for each ControllerEvent subclass.");
    }
}
