// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.common.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.Bundle;
import android.content.ComponentName;
import com.google.vr.vrcore.logging.api.IVrCoreLoggingService;
import android.os.RemoteException;
import android.os.IInterface;

public interface IVrCoreSdkService extends IInterface
{
    IDaydreamManager getDaydreamManager() throws RemoteException;
    
    IVrCoreLoggingService getLoggingService() throws RemoteException;
    
    boolean initialize(final int p0) throws RemoteException;
    
    boolean setClientOptions(final ComponentName p0, final Bundle p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IVrCoreSdkService
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.IVrCoreSdkService";
        static final int TRANSACTION_getDaydreamManager = 2;
        static final int TRANSACTION_getLoggingService = 4;
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_setClientOptions = 3;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.common.api.IVrCoreSdkService");
        }
        
        public static IVrCoreSdkService asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.common.api.IVrCoreSdkService");
            if (queryLocalInterface != null && queryLocalInterface instanceof IVrCoreSdkService) {
                return (IVrCoreSdkService)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            final IBinder binder = null;
            final Bundle bundle = null;
            final int n3 = 0;
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    final boolean initialize = this.initialize(parcel.readInt());
                    parcel2.writeNoException();
                    if (!initialize) {
                        n = 0;
                    }
                    else {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    final IDaydreamManager daydreamManager = this.getDaydreamManager();
                    parcel2.writeNoException();
                    IBinder binder2;
                    if (daydreamManager == null) {
                        binder2 = null;
                    }
                    else {
                        binder2 = daydreamManager.asBinder();
                    }
                    parcel2.writeStrongBinder(binder2);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    ComponentName componentName;
                    if (parcel.readInt() == 0) {
                        componentName = null;
                    }
                    else {
                        componentName = (ComponentName)ComponentName.CREATOR.createFromParcel(parcel);
                    }
                    Bundle bundle2;
                    if (parcel.readInt() == 0) {
                        bundle2 = bundle;
                    }
                    else {
                        bundle2 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    final boolean setClientOptions = this.setClientOptions(componentName, bundle2);
                    parcel2.writeNoException();
                    if (!setClientOptions) {
                        n = n3;
                    }
                    else {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    final IVrCoreLoggingService loggingService = this.getLoggingService();
                    parcel2.writeNoException();
                    IBinder binder3;
                    if (loggingService == null) {
                        binder3 = binder;
                    }
                    else {
                        binder3 = loggingService.asBinder();
                    }
                    parcel2.writeStrongBinder(binder3);
                    return true;
                }
            }
        }
        
        private static class Proxy implements IVrCoreSdkService
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public IDaydreamManager getDaydreamManager() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return IDaydreamManager.Stub.asInterface(obtain2.readStrongBinder());
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.common.api.IVrCoreSdkService";
            }
            
            @Override
            public IVrCoreLoggingService getLoggingService() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrCoreLoggingService.Stub.asInterface(obtain2.readStrongBinder());
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean initialize(int int1) throws RemoteException {
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                    obtain.writeInt(int1);
                    this.mRemote.transact(1, obtain, obtain2, 0);
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
            public boolean setClientOptions(final ComponentName componentName, final Bundle bundle) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IVrCoreSdkService");
                            if (componentName == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                componentName.writeToParcel(obtain, 0);
                            }
                            if (bundle == null) {
                                obtain.writeInt(0);
                                this.mRemote.transact(3, obtain, obtain2, 0);
                                obtain2.readException();
                                if (obtain2.readInt() == 0) {
                                    return b;
                                }
                                return true;
                            }
                        }
                        finally {
                            obtain2.recycle();
                            obtain.recycle();
                        }
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                        continue;
                    }
                    b = true;
                    return b;
                }
            }
        }
    }
}
