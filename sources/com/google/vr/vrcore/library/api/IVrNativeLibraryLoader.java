package com.google.vr.vrcore.library.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IVrNativeLibraryLoader extends IInterface {

    public static abstract class Stub extends Binder implements IVrNativeLibraryLoader {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.library.api.IVrNativeLibraryLoader";
        static final int TRANSACTION_closeNativeGvrLibrary = 3;
        static final int TRANSACTION_loadNativeGvrLibrary = 2;

        private static class Proxy implements IVrNativeLibraryLoader {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void closeNativeGvrLibrary(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public long loadNativeGvrLibrary(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVrNativeLibraryLoader asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface != null && (queryLocalInterface instanceof IVrNativeLibraryLoader)) ? (IVrNativeLibraryLoader) queryLocalInterface : new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    long loadNativeGvrLibrary = loadNativeGvrLibrary(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeLong(loadNativeGvrLibrary);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    closeNativeGvrLibrary(parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void closeNativeGvrLibrary(long j) throws RemoteException;

    long loadNativeGvrLibrary(int i, int i2, int i3) throws RemoteException;
}
