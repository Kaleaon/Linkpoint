package com.google.vr.vrcore.common.api;

import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.vr.vrcore.common.api.IDaydreamManager;
import com.google.vr.vrcore.logging.api.IVrCoreLoggingService;

public interface IVrCoreSdkService extends IInterface {

    public static abstract class Stub extends Binder implements IVrCoreSdkService {
        private static final String DESCRIPTOR = "com.google.vr.vrcore.common.api.IVrCoreSdkService";
        static final int TRANSACTION_getDaydreamManager = 2;
        static final int TRANSACTION_getLoggingService = 4;
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_setClientOptions = 3;

        private static class Proxy implements IVrCoreSdkService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public IDaydreamManager getDaydreamManager() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return IDaydreamManager.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public IVrCoreLoggingService getLoggingService() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return IVrCoreLoggingService.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean initialize(int i) throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
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

            public boolean setClientOptions(ComponentName componentName, Bundle bundle) throws RemoteException {
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
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
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

        public static IVrCoreSdkService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface != null && (queryLocalInterface instanceof IVrCoreSdkService)) ? (IVrCoreSdkService) queryLocalInterface : new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: android.os.Bundle} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v2, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r3v3 */
        /* JADX WARNING: type inference failed for: r3v5 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r6, android.os.Parcel r7, android.os.Parcel r8, int r9) throws android.os.RemoteException {
            /*
                r5 = this;
                r3 = 0
                r1 = 0
                r2 = 1
                switch(r6) {
                    case 1: goto L_0x0012;
                    case 2: goto L_0x002c;
                    case 3: goto L_0x0045;
                    case 4: goto L_0x007b;
                    case 1598968902: goto L_0x000b;
                    default: goto L_0x0006;
                }
            L_0x0006:
                boolean r0 = super.onTransact(r6, r7, r8, r9)
                return r0
            L_0x000b:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IVrCoreSdkService"
                r8.writeString(r0)
                return r2
            L_0x0012:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IVrCoreSdkService"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                boolean r0 = r5.initialize(r0)
                r8.writeNoException()
                if (r0 != 0) goto L_0x002a
                r0 = r1
            L_0x0026:
                r8.writeInt(r0)
                return r2
            L_0x002a:
                r0 = r2
                goto L_0x0026
            L_0x002c:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IVrCoreSdkService"
                r7.enforceInterface(r0)
                com.google.vr.vrcore.common.api.IDaydreamManager r0 = r5.getDaydreamManager()
                r8.writeNoException()
                if (r0 != 0) goto L_0x0040
                r0 = r3
            L_0x003c:
                r8.writeStrongBinder(r0)
                return r2
            L_0x0040:
                android.os.IBinder r0 = r0.asBinder()
                goto L_0x003c
            L_0x0045:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IVrCoreSdkService"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0065
                r4 = r3
            L_0x0052:
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x006f
            L_0x0058:
                boolean r0 = r5.setClientOptions(r4, r3)
                r8.writeNoException()
                if (r0 != 0) goto L_0x0079
            L_0x0061:
                r8.writeInt(r1)
                return r2
            L_0x0065:
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r4 = r0
                goto L_0x0052
            L_0x006f:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r3 = r0
                goto L_0x0058
            L_0x0079:
                r1 = r2
                goto L_0x0061
            L_0x007b:
                java.lang.String r0 = "com.google.vr.vrcore.common.api.IVrCoreSdkService"
                r7.enforceInterface(r0)
                com.google.vr.vrcore.logging.api.IVrCoreLoggingService r0 = r5.getLoggingService()
                r8.writeNoException()
                if (r0 != 0) goto L_0x008e
            L_0x008a:
                r8.writeStrongBinder(r3)
                return r2
            L_0x008e:
                android.os.IBinder r3 = r0.asBinder()
                goto L_0x008a
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.vr.vrcore.common.api.IVrCoreSdkService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    IDaydreamManager getDaydreamManager() throws RemoteException;

    IVrCoreLoggingService getLoggingService() throws RemoteException;

    boolean initialize(int i) throws RemoteException;

    boolean setClientOptions(ComponentName componentName, Bundle bundle) throws RemoteException;
}
