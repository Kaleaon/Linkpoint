// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.library.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IVrCreator extends IInterface
{
    IVrNativeLibraryLoader DEPRECATED_newNativeLibraryLoader(final IObjectWrapper p0) throws RemoteException;
    
    IVrNativeLibraryLoader newNativeLibraryLoader(final IObjectWrapper p0, final IObjectWrapper p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IVrCreator
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.library.api.IVrCreator";
        static final int TRANSACTION_DEPRECATED_newNativeLibraryLoader = 3;
        static final int TRANSACTION_newNativeLibraryLoader = 4;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.library.api.IVrCreator");
        }
        
        public static IVrCreator asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.library.api.IVrCreator");
            if (queryLocalInterface != null && queryLocalInterface instanceof IVrCreator) {
                return (IVrCreator)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            final IBinder binder = null;
            final IBinder binder2 = null;
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.library.api.IVrCreator");
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.library.api.IVrCreator");
                    final IVrNativeLibraryLoader deprecated_newNativeLibraryLoader = this.DEPRECATED_newNativeLibraryLoader(IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    IBinder binder3;
                    if (deprecated_newNativeLibraryLoader == null) {
                        binder3 = binder2;
                    }
                    else {
                        binder3 = deprecated_newNativeLibraryLoader.asBinder();
                    }
                    parcel2.writeStrongBinder(binder3);
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.google.vr.vrcore.library.api.IVrCreator");
                    final IVrNativeLibraryLoader nativeLibraryLoader = this.newNativeLibraryLoader(IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    IBinder binder4;
                    if (nativeLibraryLoader == null) {
                        binder4 = binder;
                    }
                    else {
                        binder4 = nativeLibraryLoader.asBinder();
                    }
                    parcel2.writeStrongBinder(binder4);
                    return true;
                }
            }
        }
        
        private static class Proxy implements IVrCreator
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            @Override
            public IVrNativeLibraryLoader DEPRECATED_newNativeLibraryLoader(final IObjectWrapper objectWrapper) throws RemoteException {
                final IBinder binder = null;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.library.api.IVrCreator");
                    IBinder binder2;
                    if (objectWrapper == null) {
                        binder2 = binder;
                    }
                    else {
                        binder2 = objectWrapper.asBinder();
                    }
                    obtain.writeStrongBinder(binder2);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrNativeLibraryLoader.Stub.asInterface(obtain2.readStrongBinder());
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.library.api.IVrCreator";
            }
            
            @Override
            public IVrNativeLibraryLoader newNativeLibraryLoader(final IObjectWrapper objectWrapper, final IObjectWrapper objectWrapper2) throws RemoteException {
                final IBinder binder = null;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.library.api.IVrCreator");
                    IBinder binder2;
                    if (objectWrapper == null) {
                        binder2 = null;
                    }
                    else {
                        binder2 = objectWrapper.asBinder();
                    }
                    obtain.writeStrongBinder(binder2);
                    IBinder binder3;
                    if (objectWrapper2 == null) {
                        binder3 = binder;
                    }
                    else {
                        binder3 = objectWrapper2.asBinder();
                    }
                    obtain.writeStrongBinder(binder3);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrNativeLibraryLoader.Stub.asInterface(obtain2.readStrongBinder());
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
