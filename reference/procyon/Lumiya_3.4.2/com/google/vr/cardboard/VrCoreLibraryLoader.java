// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import com.google.vr.vrcore.library.api.IVrNativeLibraryLoader;
import android.os.RemoteException;
import com.google.vr.vrcore.library.api.ObjectWrapper;
import com.google.vr.vrcore.library.api.VrCoreLoader;
import android.util.Log;
import com.google.vr.vrcore.base.api.VrCoreUtils;
import com.google.vr.vrcore.base.api.VrCoreNotAvailableException;
import com.google.vr.ndk.base.Version;
import android.content.Context;

@UsedByNative
public class VrCoreLibraryLoader
{
    private static final String TAG;
    
    static {
        TAG = VrCoreLibraryLoader.class.getSimpleName();
    }
    
    public static void checkVrCoreGvrLibraryAvailable(final Context context) throws VrCoreNotAvailableException {
        checkVrCoreGvrLibraryAvailable(context, Version.CURRENT);
    }
    
    private static void checkVrCoreGvrLibraryAvailable(final Context context, final Version version) throws VrCoreNotAvailableException {
        final String vrCoreSdkLibraryVersion = VrCoreUtils.getVrCoreSdkLibraryVersion(context);
        final Version parse = Version.parse(vrCoreSdkLibraryVersion);
        if (parse == null) {
            Log.i(VrCoreLibraryLoader.TAG, "VrCore version does not support library loading.");
            throw new VrCoreNotAvailableException(4);
        }
        if (parse.isAtLeast(version)) {
            return;
        }
        Log.w(VrCoreLibraryLoader.TAG, String.format("VrCore GVR library version obsolete; VrCore supports %s but target version is %s", vrCoreSdkLibraryVersion, version.toString()));
        throw new VrCoreNotAvailableException(4);
    }
    
    @UsedByNative
    public static long loadNativeGvrLibrary(final Context context, final int n, final int n2, final int n3) {
        try {
            final Version version = new Version(n, n2, n3);
            if (!Version.CURRENT.equals(version)) {
                Log.w(VrCoreLibraryLoader.TAG, String.format("Native SDK version does not match Java; expected %s but received %s", Version.CURRENT, version.toString()));
            }
            checkVrCoreGvrLibraryAvailable(context, version);
            final IVrNativeLibraryLoader nativeLibraryLoader = VrCoreLoader.getRemoteCreator(context).newNativeLibraryLoader(ObjectWrapper.wrap(VrCoreLoader.getRemoteContext(context)), ObjectWrapper.wrap(context));
            if (nativeLibraryLoader != null) {
                return nativeLibraryLoader.loadNativeGvrLibrary(version.majorVersion, version.minorVersion, version.patchVersion);
            }
        }
        catch (final VrCoreNotAvailableException | IllegalArgumentException | IllegalStateException | SecurityException | UnsatisfiedLinkError | RemoteException obj) {
            final String tag = VrCoreLibraryLoader.TAG;
            final String value = String.valueOf(obj);
            Log.e(tag, new StringBuilder(String.valueOf(value).length() + 49).append("Failed to load native GVR library from VrCore:\n  ").append(value).toString());
            return 0L;
        }
        Log.e(VrCoreLibraryLoader.TAG, "Failed to load native GVR library from VrCore: no library loader available.");
        return 0L;
    }
}
