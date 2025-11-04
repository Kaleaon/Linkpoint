// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.common.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface ITransitionCallbacks extends IInterface
{
    void onTransitionComplete() throws RemoteException;
    
    public abstract static class Stub extends Binder implements ITransitionCallbacks
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.ITransitionCallbacks";
        static final int TRANSACTION_onTransitionComplete = 1;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.common.api.ITransitionCallbacks");
        }
        
        public static ITransitionCallbacks asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.common.api.ITransitionCallbacks");
            if (queryLocalInterface != null && queryLocalInterface instanceof ITransitionCallbacks) {
                return (ITransitionCallbacks)queryLocalInterface;
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
                    parcel2.writeString("com.google.vr.vrcore.common.api.ITransitionCallbacks");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.ITransitionCallbacks");
                    this.onTransitionComplete();
                    return true;
                }
            }
        }
        
        private static class Proxy implements ITransitionCallbacks
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.common.api.ITransitionCallbacks";
            }
            
            @Override
            public void onTransitionComplete() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.ITransitionCallbacks");
                    this.mRemote.transact(1, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
