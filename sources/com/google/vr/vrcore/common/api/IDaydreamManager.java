package com.google.vr.vrcore.common.api;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IDaydreamManager extends IInterface {

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

        private static class Proxy implements IDaydreamManager {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public boolean deprecatedLaunchInVr(PendingIntent pendingIntent) throws RemoteException {
                boolean z = false;
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
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean exitFromVr(PendingIntent pendingIntent) throws RemoteException {
                boolean z = false;
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
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void handleInsertionIntoHeadset(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    this.mRemote.transact(11, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void handleRemovalFromHeadset() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public boolean launchInVr(PendingIntent pendingIntent, ComponentName componentName) throws RemoteException {
                boolean z = false;
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
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean launchVrHome() throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean launchVrTransition(ITransitionCallbacks iTransitionCallbacks) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (iTransitionCallbacks != null) {
                        iBinder = iTransitionCallbacks.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

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
                    this.mRemote.transact(5, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public boolean registerListener(ComponentName componentName, IDaydreamListener iDaydreamListener) throws RemoteException {
                IBinder iBinder = null;
                boolean z = false;
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
                    if (iDaydreamListener != null) {
                        iBinder = iDaydreamListener.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterDaydreamIntent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public boolean unregisterListener(ComponentName componentName) throws RemoteException {
                boolean z = false;
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
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
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
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface != null && (queryLocalInterface instanceof IDaydreamManager)) ? (IDaydreamManager) queryLocalInterface : new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.app.PendingIntent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: android.content.ComponentName} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: android.app.PendingIntent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: android.app.PendingIntent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: android.content.ComponentName} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: android.content.ComponentName} */
        /* JADX WARNING: type inference failed for: r1v0 */
        /* JADX WARNING: type inference failed for: r1v1 */
        /* JADX WARNING: type inference failed for: r1v3 */
        /* JADX WARNING: type inference failed for: r1v5 */
        /* JADX WARNING: type inference failed for: r1v7 */
        /* JADX WARNING: type inference failed for: r1v9 */
        /* JADX WARNING: type inference failed for: r1v12 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r6, android.os.Parcel r7, android.os.Parcel r8, int r9) throws android.os.RemoteException {
            /*
                r5 = this;
                r1 = 0
                r3 = 1
                r2 = 0
                switch(r6) {
                    case 1: goto L_0x0012;
                    case 2: goto L_0x0040;
                    case 3: goto L_0x0065;
                    case 4: goto L_0x0091;
                    case 5: goto L_0x00b6;
                    case 6: goto L_0x00d0;
                    case 7: goto L_0x00da;
                    case 8: goto L_0x0110;
                    case 9: goto L_0x0125;
                    case 10: goto L_0x0142;
                    case 11: goto L_0x0167;
                    case 12: goto L_0x0175;
                    case 1598968902: goto L_0x000b;
                    default: goto L_0x0006;
                }
            L_0x0006:
                boolean r0 = super.onTransact(r6, r7, r8, r9)
                return r0
            L_0x000b:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r8.writeString(r0)
                return r3
            L_0x0012:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0035
                r0 = r1
            L_0x001f:
                android.os.IBinder r1 = r7.readStrongBinder()
                com.google.vr.vrcore.common.api.IDaydreamListener r1 = com.google.vr.vrcore.common.api.IDaydreamListener.Stub.asInterface(r1)
                boolean r0 = r5.registerListener(r0, r1)
                r8.writeNoException()
                if (r0 != 0) goto L_0x003e
                r0 = r2
            L_0x0031:
                r8.writeInt(r0)
                return r3
            L_0x0035:
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                goto L_0x001f
            L_0x003e:
                r0 = r3
                goto L_0x0031
            L_0x0040:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0059
            L_0x004c:
                boolean r0 = r5.unregisterListener(r1)
                r8.writeNoException()
                if (r0 != 0) goto L_0x0063
            L_0x0055:
                r8.writeInt(r2)
                return r3
            L_0x0059:
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r1 = r0
                goto L_0x004c
            L_0x0063:
                r2 = r3
                goto L_0x0055
            L_0x0065:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0087
            L_0x0071:
                com.google.vr.vrcore.common.api.HeadTrackingState r0 = new com.google.vr.vrcore.common.api.HeadTrackingState
                r0.<init>()
                int r1 = r5.prepareVr(r1, r0)
                r8.writeNoException()
                r8.writeInt(r1)
                r8.writeInt(r3)
                r0.writeToParcel(r8, r3)
                return r3
            L_0x0087:
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r1 = r0
                goto L_0x0071
            L_0x0091:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x00aa
            L_0x009d:
                boolean r0 = r5.deprecatedLaunchInVr(r1)
                r8.writeNoException()
                if (r0 != 0) goto L_0x00b4
            L_0x00a6:
                r8.writeInt(r2)
                return r3
            L_0x00aa:
                android.os.Parcelable$Creator r0 = android.app.PendingIntent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.app.PendingIntent r0 = (android.app.PendingIntent) r0
                r1 = r0
                goto L_0x009d
            L_0x00b4:
                r2 = r3
                goto L_0x00a6
            L_0x00b6:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x00c6
            L_0x00c2:
                r5.registerDaydreamIntent(r1)
                return r3
            L_0x00c6:
                android.os.Parcelable$Creator r0 = android.app.PendingIntent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.app.PendingIntent r0 = (android.app.PendingIntent) r0
                r1 = r0
                goto L_0x00c2
            L_0x00d0:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                r5.unregisterDaydreamIntent()
                return r3
            L_0x00da:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x00fa
                r4 = r1
            L_0x00e7:
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0104
            L_0x00ed:
                boolean r0 = r5.launchInVr(r4, r1)
                r8.writeNoException()
                if (r0 != 0) goto L_0x010e
            L_0x00f6:
                r8.writeInt(r2)
                return r3
            L_0x00fa:
                android.os.Parcelable$Creator r0 = android.app.PendingIntent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.app.PendingIntent r0 = (android.app.PendingIntent) r0
                r4 = r0
                goto L_0x00e7
            L_0x0104:
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r1 = r0
                goto L_0x00ed
            L_0x010e:
                r2 = r3
                goto L_0x00f6
            L_0x0110:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                boolean r0 = r5.launchVrHome()
                r8.writeNoException()
                if (r0 != 0) goto L_0x0123
            L_0x011f:
                r8.writeInt(r2)
                return r3
            L_0x0123:
                r2 = r3
                goto L_0x011f
            L_0x0125:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                android.os.IBinder r0 = r7.readStrongBinder()
                com.google.vr.vrcore.common.api.ITransitionCallbacks r0 = com.google.vr.vrcore.common.api.ITransitionCallbacks.Stub.asInterface(r0)
                boolean r0 = r5.launchVrTransition(r0)
                r8.writeNoException()
                if (r0 != 0) goto L_0x0140
            L_0x013c:
                r8.writeInt(r2)
                return r3
            L_0x0140:
                r2 = r3
                goto L_0x013c
            L_0x0142:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x015b
            L_0x014e:
                boolean r0 = r5.exitFromVr(r1)
                r8.writeNoException()
                if (r0 != 0) goto L_0x0165
            L_0x0157:
                r8.writeInt(r2)
                return r3
            L_0x015b:
                android.os.Parcelable$Creator r0 = android.app.PendingIntent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.app.PendingIntent r0 = (android.app.PendingIntent) r0
                r1 = r0
                goto L_0x014e
            L_0x0165:
                r2 = r3
                goto L_0x0157
            L_0x0167:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                byte[] r0 = r7.createByteArray()
                r5.handleInsertionIntoHeadset(r0)
                return r3
            L_0x0175:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IDaydreamManager"
                r7.enforceInterface(r0)
                r5.handleRemovalFromHeadset()
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.vrcore.common.api.IDaydreamManager.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
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
