// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.library.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IVrNativeLibraryLoader extends IInterface
{
    void closeNativeGvrLibrary(final long p0) throws RemoteException;
    
    long loadNativeGvrLibrary(final int p0, final int p1, final int p2) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IVrNativeLibraryLoader
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.library.api.IVrNativeLibraryLoader";
        static final int TRANSACTION_closeNativeGvrLibrary = 3;
        static final int TRANSACTION_loadNativeGvrLibrary = 2;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
        }
        
        public static IVrNativeLibraryLoader asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
            if (queryLocalInterface != null && queryLocalInterface instanceof IVrNativeLibraryLoader) {
                return (IVrNativeLibraryLoader)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
                    final long loadNativeGvrLibrary = this.loadNativeGvrLibrary(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeLong(loadNativeGvrLibrary);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
                    this.closeNativeGvrLibrary(parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                }
            }
        }
        
        private static class Proxy implements IVrNativeLibraryLoader
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void closeNativeGvrLibrary(final long n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
                    obtain.writeLong(n);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.library.api.IVrNativeLibraryLoader";
            }
            
            @Override
            public long loadNativeGvrLibrary(final int n, final int n2, final int n3) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.library.api.IVrNativeLibraryLoader");
                    obtain.writeInt(n);
                    obtain.writeInt(n2);
                    obtain.writeInt(n3);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
