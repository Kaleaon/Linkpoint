// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.library.api;

import android.os.RemoteException;
import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.IInterface;

public interface IObjectWrapper extends IInterface
{
    public abstract static class Stub extends Binder implements IObjectWrapper
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.library.api.IObjectWrapper";
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.library.api.IObjectWrapper");
        }
        
        public static IObjectWrapper asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.library.api.IObjectWrapper");
            if (queryLocalInterface != null && queryLocalInterface instanceof IObjectWrapper) {
                return (IObjectWrapper)queryLocalInterface;
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
                    parcel2.writeString("com.google.vr.vrcore.library.api.IObjectWrapper");
                    return true;
                }
            }
        }
        
        private static class Proxy implements IObjectWrapper
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.library.api.IObjectWrapper";
            }
        }
    }
}
