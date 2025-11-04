// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IControllerService extends IInterface
{
    int initialize(final int p0) throws RemoteException;
    
    boolean registerListener(final int p0, final String p1, final IControllerListener p2) throws RemoteException;
    
    boolean unregisterListener(final String p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IControllerService
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.controller.api.IControllerService";
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_registerListener = 5;
        static final int TRANSACTION_unregisterListener = 6;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.controller.api.IControllerService");
        }
        
        public static IControllerService asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.controller.api.IControllerService");
            if (queryLocalInterface != null && queryLocalInterface instanceof IControllerService) {
                return (IControllerService)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int initialize, final Parcel parcel, final Parcel parcel2, final int n) throws RemoteException {
            final int n2 = 0;
            final int n3 = 0;
            switch (initialize) {
                default: {
                    return super.onTransact(initialize, parcel, parcel2, n);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.controller.api.IControllerService");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerService");
                    initialize = this.initialize(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(initialize);
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerService");
                    final boolean registerListener = this.registerListener(parcel.readInt(), parcel.readString(), IControllerListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (!registerListener) {
                        initialize = n3;
                    }
                    else {
                        initialize = 1;
                    }
                    parcel2.writeInt(initialize);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerService");
                    final boolean unregisterListener = this.unregisterListener(parcel.readString());
                    parcel2.writeNoException();
                    if (!unregisterListener) {
                        initialize = n2;
                    }
                    else {
                        initialize = 1;
                    }
                    parcel2.writeInt(initialize);
                    return true;
                }
            }
        }
        
        private static class Proxy implements IControllerService
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.controller.api.IControllerService";
            }
            
            @Override
            public int initialize(int int1) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerService");
                    obtain.writeInt(int1);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    int1 = obtain2.readInt();
                    return int1;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean registerListener(int int1, final String s, final IControllerListener controllerListener) throws RemoteException {
                final IBinder binder = null;
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerService");
                    obtain.writeInt(int1);
                    obtain.writeString(s);
                    IBinder binder2;
                    if (controllerListener == null) {
                        binder2 = binder;
                    }
                    else {
                        binder2 = controllerListener.asBinder();
                    }
                    obtain.writeStrongBinder(binder2);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    int1 = obtain2.readInt();
                    if (int1 != 0) {
                        b = true;
                    }
                    return b;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean unregisterListener(final String s) throws RemoteException {
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerService");
                    obtain.writeString(s);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        b = true;
                    }
                    return b;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
