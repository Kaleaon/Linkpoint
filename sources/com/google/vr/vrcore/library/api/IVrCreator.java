package com.google.vr.vrcore.library.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.vr.vrcore.library.api.IObjectWrapper;
import com.google.vr.vrcore.library.api.IVrNativeLibraryLoader;

public interface IVrCreator extends IInterface {

    public static abstract class Stub extends Binder implements IVrCreator {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.library.api.IVrCreator";
        static final int TRANSACTION_DEPRECATED_newNativeLibraryLoader = 3;
        static final int TRANSACTION_newNativeLibraryLoader = 4;

        private static class Proxy implements IVrCreator {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IVrNativeLibraryLoader DEPRECATED_newNativeLibraryLoader(IObjectWrapper iObjectWrapper) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (iObjectWrapper != null) {
                        iBinder = iObjectWrapper.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrNativeLibraryLoader.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public IVrNativeLibraryLoader newNativeLibraryLoader(IObjectWrapper iObjectWrapper, IObjectWrapper iObjectWrapper2) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iObjectWrapper == null ? null : iObjectWrapper.asBinder());
                    if (iObjectWrapper2 != null) {
                        iBinder = iObjectWrapper2.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrNativeLibraryLoader.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVrCreator asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface != null && (queryLocalInterface instanceof IVrCreator)) ? (IVrCreator) queryLocalInterface : new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            IBinder iBinder = null;
            switch (i) {
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    IVrNativeLibraryLoader DEPRECATED_newNativeLibraryLoader = DEPRECATED_newNativeLibraryLoader(IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (DEPRECATED_newNativeLibraryLoader != null) {
                        iBinder = DEPRECATED_newNativeLibraryLoader.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    IVrNativeLibraryLoader newNativeLibraryLoader = newNativeLibraryLoader(IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (newNativeLibraryLoader != null) {
                        iBinder = newNativeLibraryLoader.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    IVrNativeLibraryLoader DEPRECATED_newNativeLibraryLoader(IObjectWrapper iObjectWrapper) throws RemoteException;

    IVrNativeLibraryLoader newNativeLibraryLoader(IObjectWrapper iObjectWrapper, IObjectWrapper iObjectWrapper2) throws RemoteException;
}
