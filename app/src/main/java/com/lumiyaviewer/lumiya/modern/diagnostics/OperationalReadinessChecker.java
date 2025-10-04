package com.lumiyaviewer.lumiya.modern.diagnostics;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.llsd.integration.LLSDIntegrationBridge;
import com.lumiyaviewer.lumiya.slproto.llsd.kotlin.KotlinLLSDDemo;
import com.lumiyaviewer.lumiya.slproto.llsd.kotlin.KotlinLLSDValue;
import com.lumiyaviewer.lumiya.slproto.https.CapSeedFetcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Runs a battery of checks to determine if the app is operational on this device.
 */
public class OperationalReadinessChecker {
    public static class CheckResult {
        public final String name;
        public final boolean passed;
        public final String details;

        public CheckResult(String name, boolean passed, String details) {
            this.name = name;
            this.passed = passed;
            this.details = details;
        }
    }

    public static class Report {
        public final List<CheckResult> results = new ArrayList<>();

        public int getPassedCount() {
            int c = 0;
            for (CheckResult r : results) if (r.passed) c++;
            return c;
        }

        public int getTotalCount() {
            return results.size();
        }

        public boolean isOperational() {
            for (CheckResult r : results) if (!r.passed) return false;
            return true;
        }

        public String toMultilineString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Operational Diagnostics Report\n");
            sb.append("================================\n");
            for (CheckResult r : results) {
                sb.append(r.passed ? "✅ " : "❌ ").append(r.name).append("\n");
                if (r.details != null && !r.details.isEmpty()) {
                    sb.append("   ").append(r.details.replace("\n", "\n   ")).append("\n");
                }
            }
            sb.append("\nSummary: ").append(getPassedCount()).append("/").append(getTotalCount()).append(" checks passed");
            return sb.toString();
        }
    }

    private static void add(Report report, String name, boolean passed, String details) {
        report.results.add(new CheckResult(name, passed, details == null ? "" : details));
    }

    public static Report runAllChecks(Context context) {
        Report report = new Report();
        try {
            // App context
            add(report, "Application context available", context != null, "");

            // SDK level
            add(report, "Android SDK level >= 24", Build.VERSION.SDK_INT >= 24, "SDK: " + Build.VERSION.SDK_INT);

            // Package info
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                add(report, "Package info accessible", true, "versionCode=" + pi.versionCode + ", versionName=" + pi.versionName);
            } catch (Exception e) {
                add(report, "Package info accessible", false, e.toString());
            }

            // Network connectivity
            boolean networkOk = false;
            String netDetails = "";
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                        networkOk = nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                        netDetails = nc == null ? "No active network" : nc.toString();
                    } else {
                        NetworkInfo ni = cm.getActiveNetworkInfo();
                        networkOk = ni != null && ni.isConnected();
                        netDetails = ni == null ? "No active network" : (ni.getTypeName() + "/" + ni.getState());
                    }
                }
            } catch (Exception e) {
                netDetails = e.toString();
            }
            add(report, "Network connectivity available", networkOk, netDetails);

            // Permissions (best-effort – may be granted at runtime)
            boolean recordGranted = hasPermission(context, android.Manifest.permission.RECORD_AUDIO);
            add(report, "RECORD_AUDIO permission", recordGranted, recordGranted ? "granted" : "not granted");
            boolean cameraGranted = hasPermission(context, android.Manifest.permission.CAMERA);
            add(report, "CAMERA permission (optional)", cameraGranted, cameraGranted ? "granted" : "not granted");

            // OpenGL ES support
            boolean gl3 = isGles3Supported(context);
            add(report, "OpenGL ES 3.0 support", gl3, gl3 ? "ES3 available" : "Falling back to compatibility");

            // OkHttp availability
            try {
                OkHttpClient client = new OkHttpClient.Builder().build();
                add(report, "OkHttp available", client != null, "");
            } catch (Throwable t) {
                add(report, "OkHttp available", false, t.toString());
            }

            // Kotlin LLSD demo
            try {
                KotlinLLSDValue.Map map = KotlinLLSDDemo.Companion.createAgentData();
                String s = map.toLinkpointLLSD();
                add(report, "Kotlin LLSD operational", s != null && s.length() > 0, "LLSD length=" + (s == null ? 0 : s.length()));
            } catch (Throwable t) {
                add(report, "Kotlin LLSD operational", false, t.toString());
            }

            // LLSD XML parse
            try {
                String xml = "<?xml version='1.0' encoding='UTF-8'?><llsd><map><key>name</key><string>diag</string></map></llsd>";
                LLSDIntegrationBridge.parseFromXML(xml);
                add(report, "LLSD XML parsing", true, "external bridge ok");
            } catch (Throwable t) {
                add(report, "LLSD XML parsing", false, t.toString());
            }

            // Services declared
            add(report, "GridConnectionService declared", hasService(context, "com.lumiyaviewer.lumiya.GridConnectionService"), "");

            // Activities declared
            add(report, "ModernWorldActivity declared", hasActivity(context, "com.lumiyaviewer.lumiya.ui.modern.ModernWorldActivity"), "");

            // Storage access
            try {
                File dir = context.getExternalFilesDir(null);
                boolean ok = dir != null && (dir.exists() || dir.mkdirs());
                add(report, "External files directory accessible", ok, ok ? dir.getAbsolutePath() : "not available");
            } catch (Throwable t) {
                add(report, "External files directory accessible", false, t.toString());
            }

            // App singletons
            try {
                Object demo = LumiyaApp.getModernDemo();
                add(report, "Modern components accessible", demo != null, demo != null ? demo.getClass().getSimpleName() : "null");
            } catch (Throwable t) {
                add(report, "Modern components accessible", false, t.toString());
            }

            // CAPS seed (optional placeholder test)
            try {
                // Do not actually call network here to keep it quick; simulate by parsing a minimal seed map
                String fakeSeed = "<?xml version='1.0'?><llsd><map><key>seed_capability</key><string>https://example/cap/seed</string></map></llsd>";
                LLSDIntegrationBridge.parseFromXML(fakeSeed);
                add(report, "CAPS seed parsing support", true, "bridge ok");
            } catch (Throwable t) {
                add(report, "CAPS seed parsing support", false, t.toString());
            }
        } catch (Throwable fatal) {
            Log.e("OperationalDiag", "Fatal diagnostics error", fatal);
            add(report, "Diagnostics fatal error", false, fatal.toString());
        }
        return report;
    }

    private static boolean hasPermission(Context ctx, String permission) {
        try {
            if (Build.VERSION.SDK_INT < 23) return true;
            return ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean isGles3Supported(Context ctx) {
        try {
            android.app.ActivityManager am = (android.app.ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return false;
            android.content.pm.ConfigurationInfo info = am.getDeviceConfigurationInfo();
            return info != null && info.reqGlEsVersion >= 0x00030000;
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean hasService(Context ctx, String className) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_SERVICES);
            if (pi.services != null) {
                for (ServiceInfo si : pi.services) {
                    if (className.equals(si.name)) return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean hasActivity(Context ctx, String className) {
        try {
            PackageManager pm = ctx.getPackageManager();
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(ctx.getPackageName(), className));
            ActivityInfo ai = pm.getActivityInfo(intent.getComponent(), 0);
            return ai != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}

