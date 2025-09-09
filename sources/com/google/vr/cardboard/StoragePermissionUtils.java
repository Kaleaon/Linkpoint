package com.google.vr.cardboard;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class StoragePermissionUtils {
    private static final String STORAGE_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE";
    public static final int STORAGE_PERMISSION_DUMMY_REQUEST_CODE = 239;
    private static final String TAG = StoragePermissionUtils.class.getSimpleName();

    public void requestStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(STORAGE_PERMISSION) != 0) {
            Activity activity = ContextUtils.getActivity(context);
            if (activity != null) {
                activity.requestPermissions(new String[]{STORAGE_PERMISSION}, STORAGE_PERMISSION_DUMMY_REQUEST_CODE);
                return;
            }
            Log.w(TAG, "An Activity Context is required, aborting storage permission request.");
        }
    }
}
