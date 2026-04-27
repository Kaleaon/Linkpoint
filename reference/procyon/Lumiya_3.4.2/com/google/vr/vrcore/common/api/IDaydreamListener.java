// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.common.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IDaydreamListener extends IInterface
{
    void applyFade(final int p0, final long p1) throws RemoteException;
    
    void dumpDebugData() throws RemoteException;
    
    int getTargetApiVersion() throws RemoteException;
    
    void recenterHeadTracking() throws RemoteException;
    
    HeadTrackingState requestStopTracking() throws RemoteException;
    
    void resumeHeadTracking(final HeadTrackingState p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IDaydreamListener
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.IDaydreamListener";
        static final int TRANSACTION_applyFade = 3;
        static final int TRANSACTION_dumpDebugData = 5;
        static final int TRANSACTION_getTargetApiVersion = 1;
        static final int TRANSACTION_recenterHeadTracking = 4;
        static final int TRANSACTION_requestStopTracking = 2;
        static final int TRANSACTION_resumeHeadTracking = 6;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.common.api.IDaydreamListener");
        }
        
        public static IDaydreamListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IDaydreamListener) {
                return (IDaydreamListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int targetApiVersion, final Parcel parcel, final Parcel parcel2, final int n) throws RemoteException {
            final HeadTrackingState headTrackingState = null;
            switch (targetApiVersion) {
                default: {
                    return super.onTransact(targetApiVersion, parcel, parcel2, n);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.common.api.IDaydreamListener");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
                    targetApiVersion = this.getTargetApiVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(targetApiVersion);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
                    final HeadTrackingState requestStopTracking = this.requestStopTracking();
                    parcel2.writeNoException();
                    if (requestStopTracking == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        requestStopTracking.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.applyFade(parcel.readInt(), parcel.readLong());
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.recenterHeadTracking();
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.dumpDebugData();
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamListener");
                    HeadTrackingState headTrackingState2;
                    if (parcel.readInt() == 0) {
                        headTrackingState2 = headTrackingState;
                    }
                    else {
                        headTrackingState2 = (HeadTrackingState)HeadTrackingState.CREATOR.createFromParcel(parcel);
                    }
                    this.resumeHeadTracking(headTrackingState2);
                    return true;
                }
            }
        }
        
        private static class Proxy implements IDaydreamListener
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            @Override
            public void applyFade(final int n, final long n2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamListener");
                    obtain.writeInt(n);
                    obtain.writeLong(n2);
                    this.mRemote.transact(3, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void dumpDebugData() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.mRemote.transact(5, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.common.api.IDaydreamListener";
            }
            
            @Override
            public int getTargetApiVersion() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void recenterHeadTracking() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.mRemote.transact(4, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public HeadTrackingState requestStopTracking() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamListener");
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    HeadTrackingState headTrackingState;
                    if (obtain2.readInt() == 0) {
                        headTrackingState = null;
                    }
                    else {
                        headTrackingState = (HeadTrackingState)HeadTrackingState.CREATOR.createFromParcel(obtain2);
                    }
                    return headTrackingState;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void resumeHeadTracking(final HeadTrackingState headTrackingState) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamListener");
                    if (headTrackingState == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        headTrackingState.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(6, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
