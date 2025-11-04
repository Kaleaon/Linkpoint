// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.logging.api;

import android.os.Parcelable;
import android.os.Parcelable$Creator;
import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IVrCoreLoggingService extends IInterface
{
    void log(final VREventParcelable p0) throws RemoteException;
    
    void logBatched(final VREventParcelable[] p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IVrCoreLoggingService
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.logging.api.IVrCoreLoggingService";
        static final int TRANSACTION_log = 2;
        static final int TRANSACTION_logBatched = 3;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
        }
        
        public static IVrCoreLoggingService asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
            if (queryLocalInterface != null && queryLocalInterface instanceof IVrCoreLoggingService) {
                return (IVrCoreLoggingService)queryLocalInterface;
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
                    parcel2.writeString("com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
                    VREventParcelable vrEventParcelable;
                    if (parcel.readInt() == 0) {
                        vrEventParcelable = null;
                    }
                    else {
                        vrEventParcelable = (VREventParcelable)VREventParcelable.CREATOR.createFromParcel(parcel);
                    }
                    this.log(vrEventParcelable);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
                    this.logBatched((VREventParcelable[])parcel.createTypedArray((Parcelable$Creator)VREventParcelable.CREATOR));
                    return true;
                }
            }
        }
        
        private static class Proxy implements IVrCoreLoggingService
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.logging.api.IVrCoreLoggingService";
            }
            
            @Override
            public void log(final VREventParcelable vrEventParcelable) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
                    if (vrEventParcelable == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        vrEventParcelable.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(2, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void logBatched(final VREventParcelable[] array) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.logging.api.IVrCoreLoggingService");
                    obtain.writeTypedArray((Parcelable[])array, 0);
                    this.mRemote.transact(3, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
