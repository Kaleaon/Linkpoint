// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.base.api;

import android.content.pm.PackageManager$NameNotFoundException;
import android.content.pm.Signature;
import android.content.Context;

public class BuildUtils
{
    private static volatile Boolean isDebug;
    
    private static boolean computeIsDebugBuild(final Context context) {
        synchronized (BuildUtils.class) {
            if (BuildUtils.isDebug == null) {
                try {
                    BuildUtils.isDebug = SignatureUtils.verifySignature(context.getPackageManager().getPackageInfo(context.getPackageName(), 64), SignatureUtils.BLAZE_DEBUG_SIGNATURE, SignatureUtils.ANDROID_DEBUG_SIGNATURE, SignatureUtils.VRCORE_DEBUG_SIGNATURE);
                }
                catch (final PackageManager$NameNotFoundException cause) {
                    throw new IllegalStateException("Unable to find self package info", (Throwable)cause);
                }
            }
            return BuildUtils.isDebug;
        }
    }
    
    public static boolean isDebugBuild(final Context context) {
        if (BuildUtils.isDebug == null) {
            return computeIsDebugBuild(context);
        }
        return BuildUtils.isDebug;
    }
    
    public static void setIsDebugBuild(final boolean b) {
        synchronized (BuildUtils.class) {
            BuildUtils.isDebug = b;
        }
    }
}
