package com.google.vr.vrcore.common.api;

import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.vr.vrcore.common.api.IDaydreamManager;
import com.google.vr.vrcore.logging.api.IVrCoreLoggingService;
/* loaded from: classes.dex */
public interface IVrCoreSdkService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVrCoreSdkService {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.IVrCoreSdkService";
        static final int TRANSACTION_getDaydreamManager = 2;
        static final int TRANSACTION_getLoggingService = 4;
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_setClientOptions = 3;

        /* loaded from: classes.dex */
        private static class Proxy implements IVrCoreSdkService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.google.vr.vrcore.common.api.IVrCoreSdkService
            public IDaydreamManager getDaydreamManager() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return IDaydreamManager.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.google.vr.vrcore.common.api.IVrCoreSdkService
            public IVrCoreLoggingService getLoggingService() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrCoreLoggingService.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IVrCoreSdkService
            public boolean initialize(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IVrCoreSdkService
            public boolean setClientOptions(ComponentName componentName, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    }
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVrCoreSdkService asInterface(IBinder iBinder) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
                return (queryLocalInterface != null && (queryLocalInterface instanceof IVrCoreSdkService)) ? (IVrCoreSdkService) queryLocalInterface : new Proxy(iBinder);
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
                    boolean initialize = initialize(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(!initialize ? 0 : 1);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    IDaydreamManager daydreamManager = getDaydreamManager();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(daydreamManager == null ? null : daydreamManager.asBinder());
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean clientOptions = setClientOptions(parcel.readInt() == 0 ? null : (ComponentName) ComponentName.CREATOR.createFromParcel(parcel), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(clientOptions ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    IVrCoreLoggingService loggingService = getLoggingService();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(loggingService != null ? loggingService.asBinder() : null);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    IDaydreamManager getDaydreamManager() throws RemoteException;

    IVrCoreLoggingService getLoggingService() throws RemoteException;

    boolean initialize(int i) throws RemoteException;

    boolean setClientOptions(ComponentName componentName, Bundle bundle) throws RemoteException;
}
