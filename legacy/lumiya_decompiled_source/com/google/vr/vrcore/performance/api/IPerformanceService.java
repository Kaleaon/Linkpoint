package com.google.vr.vrcore.performance.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IPerformanceService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPerformanceService {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.performance.api.IPerformanceService";
        static final int TRANSACTION_getCurrentThrottlingRelativeTemperature = 1;
        static final int TRANSACTION_reportFrameDrops = 2;

        /* loaded from: classes.dex */
        private static class Proxy implements IPerformanceService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.google.vr.vrcore.performance.api.IPerformanceService
            public float getCurrentThrottlingRelativeTemperature() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readFloat();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.google.vr.vrcore.performance.api.IPerformanceService
            public void reportFrameDrops(long j, long j2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    obtain.writeLong(j2);
                    obtain.writeInt(i);
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPerformanceService asInterface(IBinder iBinder) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
                return (queryLocalInterface != null && (queryLocalInterface instanceof IPerformanceService)) ? (IPerformanceService) queryLocalInterface : new Proxy(iBinder);
            }
            return null;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    float currentThrottlingRelativeTemperature = getCurrentThrottlingRelativeTemperature();
                    parcel2.writeNoException();
                    parcel2.writeFloat(currentThrottlingRelativeTemperature);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    reportFrameDrops(parcel.readLong(), parcel.readLong(), parcel.readInt());
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    float getCurrentThrottlingRelativeTemperature() throws RemoteException;

    void reportFrameDrops(long j, long j2, int i) throws RemoteException;
}
