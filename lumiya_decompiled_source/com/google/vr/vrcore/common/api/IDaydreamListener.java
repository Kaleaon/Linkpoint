package com.google.vr.vrcore.common.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IDaydreamListener extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDaydreamListener {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.IDaydreamListener";
        static final int TRANSACTION_applyFade = 3;
        static final int TRANSACTION_dumpDebugData = 5;
        static final int TRANSACTION_getTargetApiVersion = 1;
        static final int TRANSACTION_recenterHeadTracking = 4;
        static final int TRANSACTION_requestStopTracking = 2;
        static final int TRANSACTION_resumeHeadTracking = 6;

        /* loaded from: classes.dex */
        private static class Proxy implements IDaydreamListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamListener
            public void applyFade(int i, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeLong(j);
                    this.mRemote.transact(3, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamListener
            public void dumpDebugData() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamListener
            public int getTargetApiVersion() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamListener
            public void recenterHeadTracking() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamListener
            public HeadTrackingState requestStopTracking() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : HeadTrackingState.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamListener
            public void resumeHeadTracking(HeadTrackingState headTrackingState) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (headTrackingState == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        headTrackingState.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(6, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDaydreamListener asInterface(IBinder iBinder) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
                return (queryLocalInterface != null && (queryLocalInterface instanceof IDaydreamListener)) ? (IDaydreamListener) queryLocalInterface : new Proxy(iBinder);
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
                    int targetApiVersion = getTargetApiVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(targetApiVersion);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    HeadTrackingState requestStopTracking = requestStopTracking();
                    parcel2.writeNoException();
                    if (requestStopTracking == null) {
                        parcel2.writeInt(0);
                    } else {
                        parcel2.writeInt(1);
                        requestStopTracking.writeToParcel(parcel2, 1);
                    }
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    applyFade(parcel.readInt(), parcel.readLong());
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    recenterHeadTracking();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    dumpDebugData();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    resumeHeadTracking(parcel.readInt() != 0 ? HeadTrackingState.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void applyFade(int i, long j) throws RemoteException;

    void dumpDebugData() throws RemoteException;

    int getTargetApiVersion() throws RemoteException;

    void recenterHeadTracking() throws RemoteException;

    HeadTrackingState requestStopTracking() throws RemoteException;

    void resumeHeadTracking(HeadTrackingState headTrackingState) throws RemoteException;
}
