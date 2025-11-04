// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.base.api;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.content.ComponentName;
import android.util.Log;
import android.content.pm.ApplicationInfo;
import java.util.Iterator;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.PackageInstaller$SessionInfo;
import android.os.Build$VERSION;
import android.content.Context;
import com.google.vr.cardboard.annotations.UsedByNative;

@UsedByNative
public final class VrCoreUtils
{
    private static final boolean DEBUG = false;
    private static final String TAG;
    
    static {
        TAG = VrCoreUtils.class.getSimpleName();
    }
    
    public static int checkVrCoreAvailability(final Context context) {
        return checkVrCoreAvailabilityImpl(context);
    }
    
    private static int checkVrCoreAvailabilityImpl(final Context context) {
        if ("com.google.vr.vrcore".equals(context.getPackageName())) {
            return 0;
        }
        try {
            if (!context.getPackageManager().getApplicationInfo("com.google.vr.vrcore", 0).enabled) {
                return 2;
            }
            if (verifyRemotePackageSignature(context)) {
                return 0;
            }
            return 9;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            Label_0079: {
                if (Build$VERSION.SDK_INT >= 21) {
                    break Label_0079;
                }
            Label_0055:
                while (true) {
                    final PackageManager packageManager = context.getPackageManager();
                    try {
                        if (!packageManager.getApplicationInfo("com.google.vr.vrcore", 8192).enabled) {
                            return 1;
                        }
                        return 3;
                        final Iterator iterator;
                        Label_0095: {
                            iftrue(Label_0055:)(!iterator.hasNext());
                        }
                        iftrue(Label_0095:)(!"com.google.vr.vrcore".equals(((PackageInstaller$SessionInfo)iterator.next()).getAppPackageName()));
                        return 3;
                        iterator = context.getPackageManager().getPackageInstaller().getAllSessions().iterator();
                    }
                    catch (final PackageManager$NameNotFoundException ex2) {}
                }
            }
        }
    }
    
    public static String getConnectionResultString(final int i) {
        switch (i) {
            default: {
                return new StringBuilder(38).append("Invalid connection result: ").append(i).toString();
            }
            case 0: {
                return "VR Service present";
            }
            case 1: {
                return "VR Service missing";
            }
            case 2: {
                return "VR Service disabled";
            }
            case 3: {
                return "VR Service updating";
            }
            case 4: {
                return "VR Service obsolete";
            }
            case 5: {
                return "VR Service not connected";
            }
            case 6: {
                return "No permission to do operation";
            }
            case 7: {
                return "This operation is not supported on this device";
            }
            case 8: {
                return "An unknown failure occurred";
            }
        }
    }
    
    @UsedByNative
    public static int getVrCoreClientApiVersion(final Context context) throws VrCoreNotAvailableException {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo("com.google.vr.vrcore", 128);
            if (!applicationInfo.enabled) {
                throw new VrCoreNotAvailableException(2);
            }
        }
        catch (final PackageManager$NameNotFoundException ex) {
            throw new VrCoreNotAvailableException(checkVrCoreAvailability(context));
        }
        if (applicationInfo.metaData == null) {
            return 0;
        }
        return applicationInfo.metaData.getInt("com.google.vr.vrcore.ClientApiVersion", 0);
    }
    
    public static String getVrCoreSdkLibraryVersion(Context string) throws VrCoreNotAvailableException {
        try {
            final ApplicationInfo applicationInfo = string.getPackageManager().getApplicationInfo("com.google.vr.vrcore", 128);
            if (applicationInfo != null) {
                if (!applicationInfo.enabled) {
                    throw new VrCoreNotAvailableException(2);
                }
                if (applicationInfo.metaData == null) {
                    throw new VrCoreNotAvailableException(4);
                }
                string = (Context)applicationInfo.metaData.getString("com.google.vr.vrcore.SdkLibraryVersion", "");
                if (!((String)string).isEmpty()) {
                    return ((String)string).substring(1);
                }
                throw new VrCoreNotAvailableException(4);
            }
        }
        catch (final PackageManager$NameNotFoundException ex) {
            throw new VrCoreNotAvailableException(checkVrCoreAvailability(string));
        }
        throw new VrCoreNotAvailableException(8);
    }
    
    public static int getVrCoreVersionCode(final Context context) throws VrCoreNotAvailableException {
        try {
            return context.getPackageManager().getPackageInfo("com.google.vr.vrcore", 0).versionCode;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            Log.e(VrCoreUtils.TAG, "Could not find VrCore package", (Throwable)ex);
            throw new VrCoreNotAvailableException(checkVrCoreAvailability(context));
        }
    }
    
    public static String getVrCoreVersionName(final Context context) throws VrCoreNotAvailableException {
        try {
            return context.getPackageManager().getPackageInfo("com.google.vr.vrcore", 0).versionName;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            Log.e(VrCoreUtils.TAG, "Could not find VrCore package", (Throwable)ex);
            throw new VrCoreNotAvailableException(checkVrCoreAvailability(context));
        }
    }
    
    public static boolean isVrCoreAvailable(final Context context) {
        return checkVrCoreAvailability(context) == 0;
    }
    
    public static boolean isVrCoreComponent(final ComponentName componentName) {
        return componentName != null && "com.google.vr.vrcore".equals(componentName.getPackageName());
    }
    
    private static boolean verifyRemotePackageSignature(final Context context) throws PackageManager$NameNotFoundException {
        final PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.google.vr.vrcore", 64);
        return SignatureUtils.verifySignature(packageInfo, SignatureUtils.VRCORE_RELEASE_SIGNATURE) || (BuildUtils.isDebugBuild(context) && SignatureUtils.verifySignature(packageInfo, SignatureUtils.VRCORE_DEBUG_SIGNATURE));
    }
    
    public static class ConnectionResult
    {
        public static final int NOT_SUPPORTED = 7;
        public static final int NO_PERMISSION = 6;
        public static final int SERVICE_DISABLED = 2;
        public static final int SERVICE_INVALID = 9;
        public static final int SERVICE_MISSING = 1;
        public static final int SERVICE_NOT_CONNECTED = 5;
        public static final int SERVICE_OBSOLETE = 4;
        public static final int SERVICE_UPDATING = 3;
        public static final int SUCCESS = 0;
        public static final int UNKNOWN = 8;
    }
}
