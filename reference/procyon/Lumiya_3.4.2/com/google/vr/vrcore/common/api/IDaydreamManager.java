// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.common.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.content.ComponentName;
import android.os.RemoteException;
import android.app.PendingIntent;
import android.os.IInterface;

public interface IDaydreamManager extends IInterface
{
    boolean deprecatedLaunchInVr(final PendingIntent p0) throws RemoteException;
    
    boolean exitFromVr(final PendingIntent p0) throws RemoteException;
    
    void handleInsertionIntoHeadset(final byte[] p0) throws RemoteException;
    
    void handleRemovalFromHeadset() throws RemoteException;
    
    boolean launchInVr(final PendingIntent p0, final ComponentName p1) throws RemoteException;
    
    boolean launchVrHome() throws RemoteException;
    
    boolean launchVrTransition(final ITransitionCallbacks p0) throws RemoteException;
    
    int prepareVr(final ComponentName p0, final HeadTrackingState p1) throws RemoteException;
    
    void registerDaydreamIntent(final PendingIntent p0) throws RemoteException;
    
    boolean registerListener(final ComponentName p0, final IDaydreamListener p1) throws RemoteException;
    
    void unregisterDaydreamIntent() throws RemoteException;
    
    boolean unregisterListener(final ComponentName p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IDaydreamManager
    {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.IDaydreamManager";
        static final int TRANSACTION_deprecatedLaunchInVr = 4;
        static final int TRANSACTION_exitFromVr = 10;
        static final int TRANSACTION_handleInsertionIntoHeadset = 11;
        static final int TRANSACTION_handleRemovalFromHeadset = 12;
        static final int TRANSACTION_launchInVr = 7;
        static final int TRANSACTION_launchVrHome = 8;
        static final int TRANSACTION_launchVrTransition = 9;
        static final int TRANSACTION_prepareVr = 3;
        static final int TRANSACTION_registerDaydreamIntent = 5;
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_unregisterDaydreamIntent = 6;
        static final int TRANSACTION_unregisterListener = 2;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.common.api.IDaydreamManager");
        }
        
        public static IDaydreamManager asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
            if (queryLocalInterface != null && queryLocalInterface instanceof IDaydreamManager) {
                return (IDaydreamManager)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int prepareVr, final Parcel parcel, final Parcel parcel2, final int n) throws RemoteException {
            final ComponentName componentName = null;
            final PendingIntent pendingIntent = null;
            final PendingIntent pendingIntent2 = null;
            final ComponentName componentName2 = null;
            final PendingIntent pendingIntent3 = null;
            final ComponentName componentName3 = null;
            final int n2 = 0;
            final int n3 = 0;
            final int n4 = 0;
            final int n5 = 0;
            final int n6 = 0;
            final int n7 = 0;
            switch (prepareVr) {
                default: {
                    return super.onTransact(prepareVr, parcel, parcel2, n);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.common.api.IDaydreamManager");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    ComponentName componentName4;
                    if (parcel.readInt() == 0) {
                        componentName4 = null;
                    }
                    else {
                        componentName4 = (ComponentName)ComponentName.CREATOR.createFromParcel(parcel);
                    }
                    final boolean registerListener = this.registerListener(componentName4, IDaydreamListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (!registerListener) {
                        prepareVr = 0;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    ComponentName componentName5;
                    if (parcel.readInt() == 0) {
                        componentName5 = componentName3;
                    }
                    else {
                        componentName5 = (ComponentName)ComponentName.CREATOR.createFromParcel(parcel);
                    }
                    final boolean unregisterListener = this.unregisterListener(componentName5);
                    parcel2.writeNoException();
                    if (!unregisterListener) {
                        prepareVr = n7;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    ComponentName componentName6;
                    if (parcel.readInt() == 0) {
                        componentName6 = componentName;
                    }
                    else {
                        componentName6 = (ComponentName)ComponentName.CREATOR.createFromParcel(parcel);
                    }
                    final HeadTrackingState headTrackingState = new HeadTrackingState();
                    prepareVr = this.prepareVr(componentName6, headTrackingState);
                    parcel2.writeNoException();
                    parcel2.writeInt(prepareVr);
                    parcel2.writeInt(1);
                    headTrackingState.writeToParcel(parcel2, 1);
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    PendingIntent pendingIntent4;
                    if (parcel.readInt() == 0) {
                        pendingIntent4 = pendingIntent;
                    }
                    else {
                        pendingIntent4 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(parcel);
                    }
                    final boolean deprecatedLaunchInVr = this.deprecatedLaunchInVr(pendingIntent4);
                    parcel2.writeNoException();
                    if (!deprecatedLaunchInVr) {
                        prepareVr = n2;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    PendingIntent pendingIntent5;
                    if (parcel.readInt() == 0) {
                        pendingIntent5 = pendingIntent2;
                    }
                    else {
                        pendingIntent5 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(parcel);
                    }
                    this.registerDaydreamIntent(pendingIntent5);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    this.unregisterDaydreamIntent();
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    PendingIntent pendingIntent6;
                    if (parcel.readInt() == 0) {
                        pendingIntent6 = null;
                    }
                    else {
                        pendingIntent6 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(parcel);
                    }
                    ComponentName componentName7;
                    if (parcel.readInt() == 0) {
                        componentName7 = componentName2;
                    }
                    else {
                        componentName7 = (ComponentName)ComponentName.CREATOR.createFromParcel(parcel);
                    }
                    final boolean launchInVr = this.launchInVr(pendingIntent6, componentName7);
                    parcel2.writeNoException();
                    if (!launchInVr) {
                        prepareVr = n3;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    final boolean launchVrHome = this.launchVrHome();
                    parcel2.writeNoException();
                    if (!launchVrHome) {
                        prepareVr = n4;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    final boolean launchVrTransition = this.launchVrTransition(ITransitionCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    if (!launchVrTransition) {
                        prepareVr = n5;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    PendingIntent pendingIntent7;
                    if (parcel.readInt() == 0) {
                        pendingIntent7 = pendingIntent3;
                    }
                    else {
                        pendingIntent7 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(parcel);
                    }
                    final boolean exitFromVr = this.exitFromVr(pendingIntent7);
                    parcel2.writeNoException();
                    if (!exitFromVr) {
                        prepareVr = n6;
                    }
                    else {
                        prepareVr = 1;
                    }
                    parcel2.writeInt(prepareVr);
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    this.handleInsertionIntoHeadset(parcel.createByteArray());
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("com.google.vr.vrcore.common.api.IDaydreamManager");
                    this.handleRemovalFromHeadset();
                    return true;
                }
            }
        }
        
        private static class Proxy implements IDaydreamManager
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public boolean deprecatedLaunchInVr(final PendingIntent pendingIntent) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                        if (pendingIntent == null) {
                            obtain.writeInt(0);
                        }
                        else {
                            obtain.writeInt(1);
                            pendingIntent.writeToParcel(obtain, 0);
                        }
                        this.mRemote.transact(4, obtain, obtain2, 0);
                        obtain2.readException();
                        if (obtain2.readInt() == 0) {
                            return b;
                        }
                    }
                    finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                    b = true;
                    return b;
                }
            }
            
            @Override
            public boolean exitFromVr(final PendingIntent pendingIntent) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                        if (pendingIntent == null) {
                            obtain.writeInt(0);
                        }
                        else {
                            obtain.writeInt(1);
                            pendingIntent.writeToParcel(obtain, 0);
                        }
                        this.mRemote.transact(10, obtain, obtain2, 0);
                        obtain2.readException();
                        if (obtain2.readInt() == 0) {
                            return b;
                        }
                    }
                    finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                    b = true;
                    return b;
                }
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.common.api.IDaydreamManager";
            }
            
            @Override
            public void handleInsertionIntoHeadset(final byte[] array) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                    obtain.writeByteArray(array);
                    this.mRemote.transact(11, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void handleRemovalFromHeadset() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                    this.mRemote.transact(12, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean launchInVr(final PendingIntent pendingIntent, final ComponentName componentName) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                            if (pendingIntent == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                pendingIntent.writeToParcel(obtain, 0);
                            }
                            if (componentName == null) {
                                obtain.writeInt(0);
                                this.mRemote.transact(7, obtain, obtain2, 0);
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
                        componentName.writeToParcel(obtain, 0);
                        continue;
                    }
                    b = true;
                    return b;
                }
            }
            
            @Override
            public boolean launchVrHome() throws RemoteException {
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                    this.mRemote.transact(8, obtain, obtain2, 0);
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
            
            @Override
            public boolean launchVrTransition(final ITransitionCallbacks transitionCallbacks) throws RemoteException {
                final IBinder binder = null;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                    IBinder binder2;
                    if (transitionCallbacks == null) {
                        binder2 = binder;
                    }
                    else {
                        binder2 = transitionCallbacks.asBinder();
                    }
                    obtain.writeStrongBinder(binder2);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public int prepareVr(final ComponentName componentName, final HeadTrackingState headTrackingState) throws RemoteException {
                while (true) {
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    int int1;
                    try {
                        obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                        if (componentName == null) {
                            obtain.writeInt(0);
                        }
                        else {
                            obtain.writeInt(1);
                            componentName.writeToParcel(obtain, 0);
                        }
                        this.mRemote.transact(3, obtain, obtain2, 0);
                        obtain2.readException();
                        int1 = obtain2.readInt();
                        if (obtain2.readInt() == 0) {
                            return int1;
                        }
                    }
                    finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                    headTrackingState.readFromParcel(obtain2);
                    return int1;
                }
            }
            
            @Override
            public void registerDaydreamIntent(final PendingIntent pendingIntent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                    if (pendingIntent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        pendingIntent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(5, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean registerListener(final ComponentName componentName, final IDaydreamListener daydreamListener) throws RemoteException {
                while (true) {
                    final IBinder binder = null;
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                            if (componentName == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                componentName.writeToParcel(obtain, 0);
                            }
                            if (daydreamListener == null) {
                                final IBinder binder2 = binder;
                                obtain.writeStrongBinder(binder2);
                                this.mRemote.transact(1, obtain, obtain2, 0);
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
                        final IBinder binder2 = daydreamListener.asBinder();
                        continue;
                    }
                    b = true;
                    return b;
                }
            }
            
            @Override
            public void unregisterDaydreamIntent() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                    this.mRemote.transact(6, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean unregisterListener(final ComponentName componentName) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken("com.google.vr.vrcore.common.api.IDaydreamManager");
                        if (componentName == null) {
                            obtain.writeInt(0);
                        }
                        else {
                            obtain.writeInt(1);
                            componentName.writeToParcel(obtain, 0);
                        }
                        this.mRemote.transact(2, obtain, obtain2, 0);
                        obtain2.readException();
                        if (obtain2.readInt() == 0) {
                            return b;
                        }
                    }
                    finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                    b = true;
                    return b;
                }
            }
        }
    }
}
