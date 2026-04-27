// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.performance.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IPerformanceService extends IInterface
{
    float getCurrentThrottlingRelativeTemperature() throws RemoteException;
    
    void reportFrameDrops(final long p0, final long p1, final int p2) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IPerformanceService
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.performance.api.IPerformanceService";
        static final int TRANSACTION_getCurrentThrottlingRelativeTemperature = 1;
        static final int TRANSACTION_reportFrameDrops = 2;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.performance.api.IPerformanceService");
        }
        
        public static IPerformanceService asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.performance.api.IPerformanceService");
            if (queryLocalInterface != null && queryLocalInterface instanceof IPerformanceService) {
                return (IPerformanceService)queryLocalInterface;
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
                    parcel2.writeString("com.google.vr.vrcore.performance.api.IPerformanceService");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.performance.api.IPerformanceService");
                    final float currentThrottlingRelativeTemperature = this.getCurrentThrottlingRelativeTemperature();
                    parcel2.writeNoException();
                    parcel2.writeFloat(currentThrottlingRelativeTemperature);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.performance.api.IPerformanceService");
                    this.reportFrameDrops(parcel.readLong(), parcel.readLong(), parcel.readInt());
                    return true;
                }
            }
        }
        
        private static class Proxy implements IPerformanceService
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public float getCurrentThrottlingRelativeTemperature() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.performance.api.IPerformanceService");
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readFloat();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.performance.api.IPerformanceService";
            }
            
            @Override
            public void reportFrameDrops(final long n, final long n2, final int n3) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.performance.api.IPerformanceService");
                    obtain.writeLong(n);
                    obtain.writeLong(n2);
                    obtain.writeInt(n3);
                    this.mRemote.transact(2, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
