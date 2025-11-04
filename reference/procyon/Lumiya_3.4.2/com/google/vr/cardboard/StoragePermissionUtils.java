// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.app.Activity;
import android.util.Log;
import android.os.Build$VERSION;
import android.content.Context;

public class StoragePermissionUtils
{
    private static final String STORAGE_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE";
    public static final int STORAGE_PERMISSION_DUMMY_REQUEST_CODE = 239;
    private static final String TAG;
    
    static {
        TAG = StoragePermissionUtils.class.getSimpleName();
    }
    
    public void requestStoragePermission(final Context context) {
        if (Build$VERSION.SDK_INT < 23) {
            return;
        }
        if (context.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            return;
        }
        final Activity activity = ContextUtils.getActivity(context);
        if (activity != null) {
            activity.requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 239);
            return;
        }
        Log.w(StoragePermissionUtils.TAG, "An Activity Context is required, aborting storage permission request.");
    }
}
