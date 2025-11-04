// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IControllerListener extends IInterface
{
    void deprecatedOnControllerAccelEvent(final ControllerAccelEvent p0) throws RemoteException;
    
    void deprecatedOnControllerButtonEvent(final ControllerButtonEvent p0) throws RemoteException;
    
    boolean deprecatedOnControllerButtonEventV1(final ControllerButtonEvent p0) throws RemoteException;
    
    void deprecatedOnControllerGyroEvent(final ControllerGyroEvent p0) throws RemoteException;
    
    void deprecatedOnControllerOrientationEvent(final ControllerOrientationEvent p0) throws RemoteException;
    
    void deprecatedOnControllerTouchEvent(final ControllerTouchEvent p0) throws RemoteException;
    
    int getApiVersion() throws RemoteException;
    
    ControllerListenerOptions getOptions() throws RemoteException;
    
    void onControllerEventPacket(final ControllerEventPacket p0) throws RemoteException;
    
    void onControllerEventPacket2(final ControllerEventPacket2 p0) throws RemoteException;
    
    void onControllerRecentered(final ControllerOrientationEvent p0) throws RemoteException;
    
    void onControllerStateChanged(final int p0, final int p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IControllerListener
    {
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
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.vr.vrcore.controller.api.IControllerListener");
        }
        
        public static IControllerListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.vr.vrcore.controller.api.IControllerListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IControllerListener) {
                return (IControllerListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int apiVersion, final Parcel parcel, final Parcel parcel2, final int n) throws RemoteException {
            final ControllerOrientationEvent controllerOrientationEvent = null;
            final ControllerButtonEvent controllerButtonEvent = null;
            final ControllerButtonEvent controllerButtonEvent2 = null;
            final ControllerAccelEvent controllerAccelEvent = null;
            final ControllerGyroEvent controllerGyroEvent = null;
            final ControllerEventPacket controllerEventPacket = null;
            final ControllerOrientationEvent controllerOrientationEvent2 = null;
            final ControllerEventPacket2 controllerEventPacket2 = null;
            final ControllerTouchEvent controllerTouchEvent = null;
            switch (apiVersion) {
                default: {
                    return super.onTransact(apiVersion, parcel, parcel2, n);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.vr.vrcore.controller.api.IControllerListener");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    apiVersion = this.getApiVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(apiVersion);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    this.onControllerStateChanged(parcel.readInt(), parcel.readInt());
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerTouchEvent controllerTouchEvent2;
                    if (parcel.readInt() == 0) {
                        controllerTouchEvent2 = controllerTouchEvent;
                    }
                    else {
                        controllerTouchEvent2 = (ControllerTouchEvent)ControllerTouchEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.deprecatedOnControllerTouchEvent(controllerTouchEvent2);
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerOrientationEvent controllerOrientationEvent3;
                    if (parcel.readInt() == 0) {
                        controllerOrientationEvent3 = controllerOrientationEvent;
                    }
                    else {
                        controllerOrientationEvent3 = (ControllerOrientationEvent)ControllerOrientationEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.deprecatedOnControllerOrientationEvent(controllerOrientationEvent3);
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerButtonEvent controllerButtonEvent3;
                    if (parcel.readInt() == 0) {
                        controllerButtonEvent3 = controllerButtonEvent;
                    }
                    else {
                        controllerButtonEvent3 = (ControllerButtonEvent)ControllerButtonEvent.CREATOR.createFromParcel(parcel);
                    }
                    final boolean deprecatedOnControllerButtonEventV1 = this.deprecatedOnControllerButtonEventV1(controllerButtonEvent3);
                    parcel2.writeNoException();
                    if (!deprecatedOnControllerButtonEventV1) {
                        apiVersion = 0;
                    }
                    else {
                        apiVersion = 1;
                    }
                    parcel2.writeInt(apiVersion);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerButtonEvent controllerButtonEvent4;
                    if (parcel.readInt() == 0) {
                        controllerButtonEvent4 = controllerButtonEvent2;
                    }
                    else {
                        controllerButtonEvent4 = (ControllerButtonEvent)ControllerButtonEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.deprecatedOnControllerButtonEvent(controllerButtonEvent4);
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerAccelEvent controllerAccelEvent2;
                    if (parcel.readInt() == 0) {
                        controllerAccelEvent2 = controllerAccelEvent;
                    }
                    else {
                        controllerAccelEvent2 = (ControllerAccelEvent)ControllerAccelEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.deprecatedOnControllerAccelEvent(controllerAccelEvent2);
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerGyroEvent controllerGyroEvent2;
                    if (parcel.readInt() == 0) {
                        controllerGyroEvent2 = controllerGyroEvent;
                    }
                    else {
                        controllerGyroEvent2 = (ControllerGyroEvent)ControllerGyroEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.deprecatedOnControllerGyroEvent(controllerGyroEvent2);
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    final ControllerListenerOptions options = this.getOptions();
                    parcel2.writeNoException();
                    if (options == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        options.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerEventPacket controllerEventPacket3;
                    if (parcel.readInt() == 0) {
                        controllerEventPacket3 = controllerEventPacket;
                    }
                    else {
                        controllerEventPacket3 = (ControllerEventPacket)ControllerEventPacket.CREATOR.createFromParcel(parcel);
                    }
                    this.onControllerEventPacket(controllerEventPacket3);
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerOrientationEvent controllerOrientationEvent4;
                    if (parcel.readInt() == 0) {
                        controllerOrientationEvent4 = controllerOrientationEvent2;
                    }
                    else {
                        controllerOrientationEvent4 = (ControllerOrientationEvent)ControllerOrientationEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.onControllerRecentered(controllerOrientationEvent4);
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("com.google.vr.vrcore.controller.api.IControllerListener");
                    ControllerEventPacket2 controllerEventPacket4;
                    if (parcel.readInt() == 0) {
                        controllerEventPacket4 = controllerEventPacket2;
                    }
                    else {
                        controllerEventPacket4 = (ControllerEventPacket2)ControllerEventPacket2.CREATOR.createFromParcel(parcel);
                    }
                    this.onControllerEventPacket2(controllerEventPacket4);
                    return true;
                }
            }
        }
        
        private static class Proxy implements IControllerListener
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void deprecatedOnControllerAccelEvent(final ControllerAccelEvent controllerAccelEvent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerAccelEvent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerAccelEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(7, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void deprecatedOnControllerButtonEvent(final ControllerButtonEvent controllerButtonEvent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerButtonEvent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerButtonEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(6, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean deprecatedOnControllerButtonEventV1(final ControllerButtonEvent controllerButtonEvent) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                        if (controllerButtonEvent == null) {
                            obtain.writeInt(0);
                        }
                        else {
                            obtain.writeInt(1);
                            controllerButtonEvent.writeToParcel(obtain, 0);
                        }
                        this.mRemote.transact(5, obtain, obtain2, 0);
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
            public void deprecatedOnControllerGyroEvent(final ControllerGyroEvent controllerGyroEvent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerGyroEvent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerGyroEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(8, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void deprecatedOnControllerOrientationEvent(final ControllerOrientationEvent controllerOrientationEvent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerOrientationEvent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerOrientationEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(4, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void deprecatedOnControllerTouchEvent(final ControllerTouchEvent controllerTouchEvent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerTouchEvent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerTouchEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(3, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public int getApiVersion() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.vr.vrcore.controller.api.IControllerListener";
            }
            
            @Override
            public ControllerListenerOptions getOptions() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    ControllerListenerOptions controllerListenerOptions;
                    if (obtain2.readInt() == 0) {
                        controllerListenerOptions = null;
                    }
                    else {
                        controllerListenerOptions = (ControllerListenerOptions)ControllerListenerOptions.CREATOR.createFromParcel(obtain2);
                    }
                    return controllerListenerOptions;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void onControllerEventPacket(final ControllerEventPacket controllerEventPacket) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerEventPacket == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerEventPacket.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(10, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onControllerEventPacket2(final ControllerEventPacket2 controllerEventPacket2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerEventPacket2 == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerEventPacket2.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(12, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onControllerRecentered(final ControllerOrientationEvent controllerOrientationEvent) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    if (controllerOrientationEvent == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        controllerOrientationEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(11, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onControllerStateChanged(final int n, final int n2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.vr.vrcore.controller.api.IControllerListener");
                    obtain.writeInt(n);
                    obtain.writeInt(n2);
                    this.mRemote.transact(2, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
