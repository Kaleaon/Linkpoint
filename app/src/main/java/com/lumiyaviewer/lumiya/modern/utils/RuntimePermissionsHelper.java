package com.lumiyaviewer.lumiya.modern.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RuntimePermissionsHelper {
    public static final int REQ_AUDIO = 1001;
    public static final int REQ_CAMERA = 1002;

    public static boolean has(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT < 23) return true;
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestIfNeeded(Activity activity, String permission, int reqCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, reqCode);
            }
        }
    }
}

