package com.linkpoint.pwa;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Device Info Plugin for Linkpoint PWA
 * Provides comprehensive device and system information
 */
@CapacitorPlugin(name = "DeviceInfo")
public class DeviceInfoPlugin extends Plugin {

    /**
     * Get all device information
     */
    @PluginMethod
    public void getInfo(PluginCall call) {
        JSObject info = new JSObject();

        // Device info
        info.put("model", Build.MODEL);
        info.put("manufacturer", Build.MANUFACTURER);
        info.put("brand", Build.BRAND);
        info.put("device", Build.DEVICE);
        info.put("product", Build.PRODUCT);

        // OS info
        info.put("platform", "android");
        info.put("osVersion", Build.VERSION.RELEASE);
        info.put("sdkVersion", Build.VERSION.SDK_INT);
        
        // App info
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            info.put("appVersion", packageInfo.versionName);
            info.put("appBuild", packageInfo.versionCode);
            info.put("appId", getContext().getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            // Ignore
        }

        // Display info
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        
        info.put("screenWidth", metrics.widthPixels);
        info.put("screenHeight", metrics.heightPixels);
        info.put("screenDensity", metrics.density);
        info.put("screenDpi", metrics.densityDpi);

        // Locale and timezone
        info.put("language", Locale.getDefault().getLanguage());
        info.put("locale", Locale.getDefault().toString());
        info.put("timezone", TimeZone.getDefault().getID());
        info.put("timezoneOffset", TimeZone.getDefault().getRawOffset() / 1000);

        // Unique ID
        String androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        info.put("deviceId", androidId);

        call.resolve(info);
    }

    /**
     * Get battery information
     */
    @PluginMethod
    public void getBatteryInfo(PluginCall call) {
        BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(Context.BATTERY_SERVICE);
        
        JSObject battery = new JSObject();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            battery.put("level", level / 100.0);
            battery.put("isCharging", batteryManager.isCharging());
            
            int status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            battery.put("chargingStatus", status);
        }

        call.resolve(battery);
    }

    /**
     * Get memory information
     */
    @PluginMethod
    public void getMemoryInfo(PluginCall call) {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        JSObject memory = new JSObject();
        memory.put("totalMemory", memoryInfo.totalMem);
        memory.put("availableMemory", memoryInfo.availMem);
        memory.put("usedMemory", memoryInfo.totalMem - memoryInfo.availMem);
        memory.put("lowMemory", memoryInfo.lowMemory);
        memory.put("threshold", memoryInfo.threshold);
        memory.put("percentUsed", (1.0 - ((double) memoryInfo.availMem / memoryInfo.totalMem)) * 100);

        call.resolve(memory);
    }

    /**
     * Get storage information
     */
    @PluginMethod
    public void getStorageInfo(PluginCall call) {
        JSObject storage = new JSObject();

        // Internal storage
        File internalDir = Environment.getDataDirectory();
        StatFs internalStat = new StatFs(internalDir.getPath());
        long internalTotal = internalStat.getBlockCountLong() * internalStat.getBlockSizeLong();
        long internalAvailable = internalStat.getAvailableBlocksLong() * internalStat.getBlockSizeLong();
        
        storage.put("internalTotal", internalTotal);
        storage.put("internalAvailable", internalAvailable);
        storage.put("internalUsed", internalTotal - internalAvailable);

        // External storage (if available)
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalDir = Environment.getExternalStorageDirectory();
            StatFs externalStat = new StatFs(externalDir.getPath());
            long externalTotal = externalStat.getBlockCountLong() * externalStat.getBlockSizeLong();
            long externalAvailable = externalStat.getAvailableBlocksLong() * externalStat.getBlockSizeLong();
            
            storage.put("externalTotal", externalTotal);
            storage.put("externalAvailable", externalAvailable);
            storage.put("externalUsed", externalTotal - externalAvailable);
        }

        call.resolve(storage);
    }

    /**
     * Check if device is rooted
     */
    @PluginMethod
    public void isRooted(PluginCall call) {
        boolean rooted = checkRooted();
        
        JSObject result = new JSObject();
        result.put("rooted", rooted);
        call.resolve(result);
    }

    /**
     * Get network capabilities
     */
    @PluginMethod
    public void getCapabilities(PluginCall call) {
        JSObject capabilities = new JSObject();

        // Check for various features
        PackageManager pm = getContext().getPackageManager();
        
        capabilities.put("hasCamera", pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY));
        capabilities.put("hasBluetooth", pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH));
        capabilities.put("hasNFC", pm.hasSystemFeature(PackageManager.FEATURE_NFC));
        capabilities.put("hasGPS", pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));
        capabilities.put("hasTelephony", pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY));
        capabilities.put("hasWifi", pm.hasSystemFeature(PackageManager.FEATURE_WIFI));
        capabilities.put("hasMicrophone", pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE));
        capabilities.put("hasTouchscreen", pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN));
        capabilities.put("hasAccelerometer", pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER));
        capabilities.put("hasGyroscope", pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE));

        call.resolve(capabilities);
    }

    // Helper methods

    private boolean checkRooted() {
        // Check for common root files
        String[] paths = {
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }

        return false;
    }
}
