package com.google.vr.vrcore.controller.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IControllerListener extends IInterface {

    public static abstract class Stub extends Binder implements IControllerListener {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.controller.api.IControllerListener";
        static final int TRANSACTION_deprecatedOnControllerAccelEvent = 7;
        static final int TRANSACTION_deprecatedOnControllerButtonEvent = 6;
        static final int TRANSACTION_deprecatedOnControllerButtonEventV1 = 5;
        static final int TRANSACTION_deprecatedOnControllerGyroEvent = 8;
        static final int TRANSACTION_deprecatedOnControllerOrientationEvent = 4;
        static final int TRANSACTION_deprecatedOnControllerTouchEvent = 3;
        static final int TRANSACTION_getApiVersion = 1;
        static final int TRANSACTION_getOptions = 9;
        static final int TRANSACTION_onControllerEventPacket = 10;
        static final int TRANSACTION_onControllerEventPacket2 = 12;
        static final int TRANSACTION_onControllerRecentered = 11;
        static final int TRANSACTION_onControllerStateChanged = 2;

        private static class Proxy implements IControllerListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void deprecatedOnControllerAccelEvent(ControllerAccelEvent controllerAccelEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerAccelEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerAccelEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(7, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void deprecatedOnControllerButtonEvent(ControllerButtonEvent controllerButtonEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerButtonEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerButtonEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(6, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public boolean deprecatedOnControllerButtonEventV1(ControllerButtonEvent controllerButtonEvent) throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerButtonEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerButtonEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(5, obtain, obtain2, 0);
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

            public void deprecatedOnControllerGyroEvent(ControllerGyroEvent controllerGyroEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerGyroEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerGyroEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(8, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void deprecatedOnControllerOrientationEvent(ControllerOrientationEvent controllerOrientationEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerOrientationEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerOrientationEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(4, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void deprecatedOnControllerTouchEvent(ControllerTouchEvent controllerTouchEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerTouchEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerTouchEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(3, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public int getApiVersion() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public ControllerListenerOptions getOptions() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : ControllerListenerOptions.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onControllerEventPacket(ControllerEventPacket controllerEventPacket) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerEventPacket == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerEventPacket.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(10, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onControllerEventPacket2(ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerEventPacket2 == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerEventPacket2.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(12, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (controllerOrientationEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        controllerOrientationEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(11, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onControllerStateChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(2, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IControllerListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface != null && (queryLocalInterface instanceof IControllerListener)) ? (IControllerListener) queryLocalInterface : new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: com.google.vr.vrcore.controller.api.ControllerEventPacket2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: com.google.vr.vrcore.controller.api.ControllerOrientationEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: com.google.vr.vrcore.controller.api.ControllerGyroEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: com.google.vr.vrcore.controller.api.ControllerAccelEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: com.google.vr.vrcore.controller.api.ControllerButtonEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v30, resolved type: com.google.vr.vrcore.controller.api.ControllerButtonEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v38, resolved type: com.google.vr.vrcore.controller.api.ControllerOrientationEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v42, resolved type: com.google.vr.vrcore.controller.api.ControllerTouchEvent} */
        /* JADX WARNING: type inference failed for: r0v0 */
        /* JADX WARNING: type inference failed for: r0v12, types: [com.google.vr.vrcore.controller.api.ControllerEventPacket] */
        /* JADX WARNING: type inference failed for: r0v49 */
        /* JADX WARNING: type inference failed for: r0v50 */
        /* JADX WARNING: type inference failed for: r0v51 */
        /* JADX WARNING: type inference failed for: r0v52 */
        /* JADX WARNING: type inference failed for: r0v53 */
        /* JADX WARNING: type inference failed for: r0v54 */
        /* JADX WARNING: type inference failed for: r0v55 */
        /* JADX WARNING: type inference failed for: r0v56 */
        /* JADX WARNING: type inference failed for: r0v57 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                r0 = 0
                r1 = 0
                r2 = 1
                switch(r5) {
                    case 1: goto L_0x0012;
                    case 2: goto L_0x0023;
                    case 3: goto L_0x0035;
                    case 4: goto L_0x004e;
                    case 5: goto L_0x0067;
                    case 6: goto L_0x008c;
                    case 7: goto L_0x00a5;
                    case 8: goto L_0x00be;
                    case 9: goto L_0x00d7;
                    case 10: goto L_0x00f1;
                    case 11: goto L_0x010a;
                    case 12: goto L_0x0123;
                    case 1598968902: goto L_0x000b;
                    default: goto L_0x0006;
                }
            L_0x0006:
                boolean r0 = super.onTransact(r5, r6, r7, r8)
                return r0
            L_0x000b:
                java.lang.String r0 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r7.writeString(r0)
                return r2
            L_0x0012:
                java.lang.String r0 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r0)
                int r0 = r4.getApiVersion()
                r7.writeNoException()
                r7.writeInt(r0)
                return r2
            L_0x0023:
                java.lang.String r0 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r0)
                int r0 = r6.readInt()
                int r1 = r6.readInt()
                r4.onControllerStateChanged(r0, r1)
                return r2
            L_0x0035:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x0045
            L_0x0041:
                r4.deprecatedOnControllerTouchEvent(r0)
                return r2
            L_0x0045:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerTouchEvent> r0 = com.google.vr.vrcore.controller.api.ControllerTouchEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerTouchEvent r0 = (com.google.vr.vrcore.controller.api.ControllerTouchEvent) r0
                goto L_0x0041
            L_0x004e:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x005e
            L_0x005a:
                r4.deprecatedOnControllerOrientationEvent(r0)
                return r2
            L_0x005e:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerOrientationEvent> r0 = com.google.vr.vrcore.controller.api.ControllerOrientationEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerOrientationEvent r0 = (com.google.vr.vrcore.controller.api.ControllerOrientationEvent) r0
                goto L_0x005a
            L_0x0067:
                java.lang.String r3 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r3)
                int r3 = r6.readInt()
                if (r3 != 0) goto L_0x0081
            L_0x0073:
                boolean r0 = r4.deprecatedOnControllerButtonEventV1(r0)
                r7.writeNoException()
                if (r0 != 0) goto L_0x008a
                r0 = r1
            L_0x007d:
                r7.writeInt(r0)
                return r2
            L_0x0081:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerButtonEvent> r0 = com.google.vr.vrcore.controller.api.ControllerButtonEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerButtonEvent r0 = (com.google.vr.vrcore.controller.api.ControllerButtonEvent) r0
                goto L_0x0073
            L_0x008a:
                r0 = r2
                goto L_0x007d
            L_0x008c:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x009c
            L_0x0098:
                r4.deprecatedOnControllerButtonEvent(r0)
                return r2
            L_0x009c:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerButtonEvent> r0 = com.google.vr.vrcore.controller.api.ControllerButtonEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerButtonEvent r0 = (com.google.vr.vrcore.controller.api.ControllerButtonEvent) r0
                goto L_0x0098
            L_0x00a5:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x00b5
            L_0x00b1:
                r4.deprecatedOnControllerAccelEvent(r0)
                return r2
            L_0x00b5:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerAccelEvent> r0 = com.google.vr.vrcore.controller.api.ControllerAccelEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerAccelEvent r0 = (com.google.vr.vrcore.controller.api.ControllerAccelEvent) r0
                goto L_0x00b1
            L_0x00be:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x00ce
            L_0x00ca:
                r4.deprecatedOnControllerGyroEvent(r0)
                return r2
            L_0x00ce:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerGyroEvent> r0 = com.google.vr.vrcore.controller.api.ControllerGyroEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerGyroEvent r0 = (com.google.vr.vrcore.controller.api.ControllerGyroEvent) r0
                goto L_0x00ca
            L_0x00d7:
                java.lang.String r0 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r0)
                com.google.vr.vrcore.controller.api.ControllerListenerOptions r0 = r4.getOptions()
                r7.writeNoException()
                if (r0 != 0) goto L_0x00ea
                r7.writeInt(r1)
            L_0x00e9:
                return r2
            L_0x00ea:
                r7.writeInt(r2)
                r0.writeToParcel(r7, r2)
                goto L_0x00e9
            L_0x00f1:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x0101
            L_0x00fd:
                r4.onControllerEventPacket(r0)
                return r2
            L_0x0101:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerEventPacket> r0 = com.google.vr.vrcore.controller.api.ControllerEventPacket.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerEventPacket r0 = (com.google.vr.vrcore.controller.api.ControllerEventPacket) r0
                goto L_0x00fd
            L_0x010a:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x011a
            L_0x0116:
                r4.onControllerRecentered(r0)
                return r2
            L_0x011a:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerOrientationEvent> r0 = com.google.vr.vrcore.controller.api.ControllerOrientationEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerOrientationEvent r0 = (com.google.vr.vrcore.controller.api.ControllerOrientationEvent) r0
                goto L_0x0116
            L_0x0123:
                java.lang.String r1 = "com.google.vr.vrcore.controller.api.IControllerListener"
                r6.enforceInterface(r1)
                int r1 = r6.readInt()
                if (r1 != 0) goto L_0x0133
            L_0x012f:
                r4.onControllerEventPacket2(r0)
                return r2
            L_0x0133:
                android.os.Parcelable$Creator<com.google.vr.vrcore.controller.api.ControllerEventPacket2> r0 = com.google.vr.vrcore.controller.api.ControllerEventPacket2.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                com.google.vr.vrcore.controller.api.ControllerEventPacket2 r0 = (com.google.vr.vrcore.controller.api.ControllerEventPacket2) r0
                goto L_0x012f
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.vrcore.controller.api.IControllerListener.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void deprecatedOnControllerAccelEvent(ControllerAccelEvent controllerAccelEvent) throws RemoteException;

    void deprecatedOnControllerButtonEvent(ControllerButtonEvent controllerButtonEvent) throws RemoteException;

    boolean deprecatedOnControllerButtonEventV1(ControllerButtonEvent controllerButtonEvent) throws RemoteException;

    void deprecatedOnControllerGyroEvent(ControllerGyroEvent controllerGyroEvent) throws RemoteException;

    void deprecatedOnControllerOrientationEvent(ControllerOrientationEvent controllerOrientationEvent) throws RemoteException;

    void deprecatedOnControllerTouchEvent(ControllerTouchEvent controllerTouchEvent) throws RemoteException;

    int getApiVersion() throws RemoteException;

    ControllerListenerOptions getOptions() throws RemoteException;

    void onControllerEventPacket(ControllerEventPacket controllerEventPacket) throws RemoteException;

    void onControllerEventPacket2(ControllerEventPacket2 controllerEventPacket2) throws RemoteException;

    void onControllerRecentered(ControllerOrientationEvent controllerOrientationEvent) throws RemoteException;

    void onControllerStateChanged(int i, int i2) throws RemoteException;
}
