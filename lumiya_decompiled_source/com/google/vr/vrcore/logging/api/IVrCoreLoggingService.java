package com.google.vr.vrcore.logging.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IVrCoreLoggingService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVrCoreLoggingService {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.logging.api.IVrCoreLoggingService";
        static final int TRANSACTION_log = 2;
        static final int TRANSACTION_logBatched = 3;

        /* loaded from: classes.dex */
        private static class Proxy implements IVrCoreLoggingService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.google.vr.vrcore.logging.api.IVrCoreLoggingService
            public void log(VREventParcelable vREventParcelable) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vREventParcelable == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        vREventParcelable.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.logging.api.IVrCoreLoggingService
            public void logBatched(VREventParcelable[] vREventParcelableArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedArray(vREventParcelableArr, 0);
                    this.mRemote.transact(3, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVrCoreLoggingService asInterface(IBinder iBinder) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
                return (queryLocalInterface != null && (queryLocalInterface instanceof IVrCoreLoggingService)) ? (IVrCoreLoggingService) queryLocalInterface : new Proxy(iBinder);
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
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    log(parcel.readInt() == 0 ? null : VREventParcelable.CREATOR.createFromParcel(parcel));
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    logBatched((VREventParcelable[]) parcel.createTypedArray(VREventParcelable.CREATOR));
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void log(VREventParcelable vREventParcelable) throws RemoteException;

    void logBatched(VREventParcelable[] vREventParcelableArr) throws RemoteException;
}
