// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.library.api;

import android.os.IBinder;
import android.content.pm.PackageManager$NameNotFoundException;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import android.content.Context;

public class VrCoreLoader
{
    private static final String CREATOR_NAME = "com.google.vr.vrcore.library.VrCreator";
    private static final boolean DEBUG = false;
    private static final String LIBRARY_APK_PACKAGE = "com.google.vr.vrcore";
    static final int MIN_TARGET_API_VERSION = 9;
    private static final String TAG;
    private static IVrCreator sCreator;
    private static Context sRemoteContext;
    
    static {
        TAG = VrCoreLoader.class.getSimpleName();
    }
    
    public static Context getRemoteContext(final Context context) throws VrCoreNotAvailableException {
        if (VrCoreLoader.sRemoteContext == null) {
            if (VrCoreUtils.getVrCoreClientApiVersion(context) >= 9) {
                try {
                    VrCoreLoader.sRemoteContext = context.createPackageContext("com.google.vr.vrcore", 3);
                    return VrCoreLoader.sRemoteContext;
                }
                catch (final PackageManager$NameNotFoundException ex) {
                    throw new VrCoreNotAvailableException(1);
                }
            }
            throw new VrCoreNotAvailableException(4);
        }
        return VrCoreLoader.sRemoteContext;
    }
    
    public static IVrCreator getRemoteCreator(final Context context) throws VrCoreNotAvailableException {
        if (VrCoreLoader.sCreator == null) {
            VrCoreLoader.sCreator = IVrCreator.Stub.asInterface(newBinderInstance(getRemoteContext(context).getClassLoader(), "com.google.vr.vrcore.library.VrCreator"));
        }
        return VrCoreLoader.sCreator;
    }
    
    private static IBinder newBinderInstance(final ClassLoader classLoader, final String s) {
        try {
            return (IBinder)classLoader.loadClass(s).newInstance();
        }
        catch (final ClassNotFoundException ex) {
            final String value = String.valueOf(s);
            String concat;
            if (value.length() == 0) {
                concat = new String("Unable to find dynamic class ");
            }
            else {
                concat = "Unable to find dynamic class ".concat(value);
            }
            throw new IllegalStateException(concat);
        }
        catch (final InstantiationException ex2) {
            final String value2 = String.valueOf(s);
            String concat2;
            if (value2.length() == 0) {
                concat2 = new String("Unable to instantiate the remote class ");
            }
            else {
                concat2 = "Unable to instantiate the remote class ".concat(value2);
            }
            throw new IllegalStateException(concat2);
        }
        catch (final IllegalAccessException ex3) {
            final String value3 = String.valueOf(s);
            String concat3;
            if (value3.length() == 0) {
                concat3 = new String("Unable to call the default constructor of ");
            }
            else {
                concat3 = "Unable to call the default constructor of ".concat(value3);
            }
            throw new IllegalStateException(concat3);
        }
    }
}
