package com.google.vr.vrcore.common.api;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.vr.vrcore.common.api.IDaydreamListener;
import com.google.vr.vrcore.common.api.ITransitionCallbacks;
/* loaded from: classes.dex */
public interface IDaydreamManager extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDaydreamManager {
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

        /* loaded from: classes.dex */
        private static class Proxy implements IDaydreamManager {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean deprecatedLaunchInVr(PendingIntent pendingIntent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        pendingIntent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean exitFromVr(PendingIntent pendingIntent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        pendingIntent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public void handleInsertionIntoHeadset(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    this.mRemote.transact(11, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public void handleRemovalFromHeadset() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean launchInVr(PendingIntent pendingIntent, ComponentName componentName) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        pendingIntent.writeToParcel(obtain, 0);
                    }
                    if (componentName == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean launchVrHome() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean launchVrTransition(ITransitionCallbacks iTransitionCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iTransitionCallbacks != null ? iTransitionCallbacks.asBinder() : null);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public int prepareVr(ComponentName componentName, HeadTrackingState headTrackingState) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    if (obtain2.readInt() != 0) {
                        headTrackingState.readFromParcel(obtain2);
                    }
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public void registerDaydreamIntent(PendingIntent pendingIntent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingIntent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        pendingIntent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(5, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean registerListener(ComponentName componentName, IDaydreamListener iDaydreamListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    }
                    obtain.writeStrongBinder(iDaydreamListener != null ? iDaydreamListener.asBinder() : null);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public void unregisterDaydreamIntent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.vr.vrcore.common.api.IDaydreamManager
            public boolean unregisterListener(ComponentName componentName) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDaydreamManager asInterface(IBinder iBinder) {
            if (iBinder != null) {
                IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
                return (queryLocalInterface != null && (queryLocalInterface instanceof IDaydreamManager)) ? (IDaydreamManager) queryLocalInterface : new Proxy(iBinder);
            }
            return null;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean registerListener = registerListener(parcel.readInt() == 0 ? null : (ComponentName) ComponentName.CREATOR.createFromParcel(parcel), IDaydreamListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(!registerListener ? 0 : 1);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean unregisterListener = unregisterListener(parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(unregisterListener ? 1 : 0);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    ComponentName componentName = parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null;
                    HeadTrackingState headTrackingState = new HeadTrackingState();
                    int prepareVr = prepareVr(componentName, headTrackingState);
                    parcel2.writeNoException();
                    parcel2.writeInt(prepareVr);
                    parcel2.writeInt(1);
                    headTrackingState.writeToParcel(parcel2, 1);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean deprecatedLaunchInVr = deprecatedLaunchInVr(parcel.readInt() != 0 ? (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(deprecatedLaunchInVr ? 1 : 0);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerDaydreamIntent(parcel.readInt() != 0 ? (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterDaydreamIntent();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean launchInVr = launchInVr(parcel.readInt() == 0 ? null : (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel), parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(launchInVr ? 1 : 0);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean launchVrHome = launchVrHome();
                    parcel2.writeNoException();
                    parcel2.writeInt(launchVrHome ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean launchVrTransition = launchVrTransition(ITransitionCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(launchVrTransition ? 1 : 0);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean exitFromVr = exitFromVr(parcel.readInt() != 0 ? (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(exitFromVr ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    handleInsertionIntoHeadset(parcel.createByteArray());
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    handleRemovalFromHeadset();
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean deprecatedLaunchInVr(PendingIntent pendingIntent) throws RemoteException;

    boolean exitFromVr(PendingIntent pendingIntent) throws RemoteException;

    void handleInsertionIntoHeadset(byte[] bArr) throws RemoteException;

    void handleRemovalFromHeadset() throws RemoteException;

    boolean launchInVr(PendingIntent pendingIntent, ComponentName componentName) throws RemoteException;

    boolean launchVrHome() throws RemoteException;

    boolean launchVrTransition(ITransitionCallbacks iTransitionCallbacks) throws RemoteException;

    int prepareVr(ComponentName componentName, HeadTrackingState headTrackingState) throws RemoteException;

    void registerDaydreamIntent(PendingIntent pendingIntent) throws RemoteException;

    boolean registerListener(ComponentName componentName, IDaydreamListener iDaydreamListener) throws RemoteException;

    void unregisterDaydreamIntent() throws RemoteException;

    boolean unregisterListener(ComponentName componentName) throws RemoteException;
}
