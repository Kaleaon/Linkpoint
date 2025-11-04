// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.app.AlertDialog$Builder;
import android.content.pm.PackageManager$NameNotFoundException;
import android.view.Window;
import android.os.PowerManager;
import android.app.Activity;
import android.os.Build$VERSION;
import android.content.ActivityNotFoundException;
import android.util.Log;
import android.net.Uri;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import java.util.Iterator;
import android.content.ComponentName;
import android.provider.Settings$Secure;
import android.content.pm.ApplicationInfo;
import android.content.Context;

public class AndroidNCompat
{
    private static final String ACTION_VR_LISTENER_SETTINGS = "android.settings.VR_LISTENER_SETTINGS";
    private static final boolean DEBUG = false;
    private static final String DEFAULT_VR_MODE_CLASS = "com.google.vr.vrcore.common.VrCoreListenerService";
    private static final String DEFAULT_VR_MODE_PACKAGE = "com.google.vr.vrcore";
    private static final String ENABLED_VR_LISTENERS = "enabled_vr_listeners";
    public static final int FLAG_VR_MODE_ENABLE_FALLBACK = 1;
    public static final int NMR1_SDK_LEVEL = 25;
    public static final int N_SDK_LEVEL = 24;
    private static final int PACKAGE_NOT_ENABLED = -2;
    private static final int PACKAGE_NOT_PRESENT = -1;
    private static final int SUCCESS = 0;
    private static final String TAG;
    private static int sSdkLevelOverride;
    
    static {
        TAG = AndroidNCompat.class.getSimpleName();
        AndroidNCompat.sSdkLevelOverride = 0;
    }
    
    private AndroidNCompat() {
    }
    
    private static int checkForVrCorePresence(final Context context) {
        final Iterator iterator = context.getPackageManager().getInstalledApplications(0).iterator();
        while (true) {
            while (iterator.hasNext()) {
                if (((ApplicationInfo)iterator.next()).packageName.equals("com.google.vr.vrcore")) {
                    final int n = 1;
                    if (n == 0) {
                        return -1;
                    }
                    final String string = Settings$Secure.getString(context.getContentResolver(), "enabled_vr_listeners");
                    final ComponentName componentName = new ComponentName("com.google.vr.vrcore", "com.google.vr.vrcore.common.VrCoreListenerService");
                    if (string != null && string.contains(componentName.flattenToString())) {
                        return 0;
                    }
                    return -2;
                }
            }
            final int n = 0;
            continue;
        }
    }
    
    private static boolean handleVrCoreAbsence(final Context context, final int n) {
        if (n == -1) {
            showWarningDialog(context, R.string.dialog_vr_core_not_installed, R.string.go_to_playstore_button, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
                public final void onClick(final DialogInterface dialogInterface, final int n) {
                    final Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("market://details?id=com.google.vr.vrcore"));
                    intent.setPackage("com.android.vending");
                    try {
                        context.startActivity(intent);
                    }
                    catch (final ActivityNotFoundException ex) {
                        Log.e(AndroidNCompat.TAG, "Google Play Services is not installed, unable to download VrCore.");
                    }
                }
            });
            return false;
        }
        if (n != -2) {
            return true;
        }
        showWarningDialog(context, R.string.dialog_vr_core_not_enabled, R.string.go_to_vr_listeners_settings_button, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public final void onClick(final DialogInterface dialogInterface, final int n) {
                context.startActivity(new Intent("android.settings.VR_LISTENER_SETTINGS"));
            }
        });
        return false;
    }
    
    private static boolean isAtLeastN() {
        return AndroidNCompat.sSdkLevelOverride >= 24 || Build$VERSION.SDK_INT >= 24;
    }
    
    private static boolean isAtLeastNMR1() {
        return AndroidNCompat.sSdkLevelOverride >= 25 || "NMR1".equals(Build$VERSION.CODENAME) || Build$VERSION.SDK_INT >= 25;
    }
    
    public static boolean isVrModeSupported(final Context context) {
        return isAtLeastN() && context.getPackageManager().hasSystemFeature("android.software.vr.mode");
    }
    
    public static void setSdkLevelForTesting(final int sSdkLevelOverride) {
        AndroidNCompat.sSdkLevelOverride = sSdkLevelOverride;
    }
    
    public static boolean setSustainedPerformanceMode(final Activity activity, final boolean sustainedPerformanceMode) {
        if (!isAtLeastN()) {
            return false;
        }
        if (!((PowerManager)activity.getSystemService("power")).isSustainedPerformanceModeSupported()) {
            Log.d(AndroidNCompat.TAG, "Sustained performance mode is not supported on this device.");
            return false;
        }
        final Window window = activity.getWindow();
        if (window != null) {
            window.setSustainedPerformanceMode(sustainedPerformanceMode);
            return true;
        }
        Log.e(AndroidNCompat.TAG, "Activity does not have a window");
        return false;
    }
    
    public static boolean setVrModeEnabled(final Activity activity, final boolean b) {
        return setVrModeEnabled(activity, b, 1);
    }
    
    public static boolean setVrModeEnabled(final Activity activity, final boolean b, final int n) {
        Label_0027: {
            if (!isVrModeSupported((Context)activity)) {
                break Label_0027;
            }
            final ComponentName componentName = new ComponentName("com.google.vr.vrcore", "com.google.vr.vrcore.common.VrCoreListenerService");
            try {
                activity.setVrModeEnabled(b, componentName);
                return true;
                Label_0035: {
                    Log.d(AndroidNCompat.TAG, "VR mode is not supported on this N device.");
                }
                return false;
                iftrue(Label_0035:)(isAtLeastN());
                return false;
            }
            catch (final PackageManager$NameNotFoundException obj) {
                final String tag = AndroidNCompat.TAG;
                final String value = String.valueOf(obj);
                Log.w(tag, new StringBuilder(String.valueOf(value).length() + 25).append("No VR service component: ").append(value).toString());
                if ((n & 0x1) != 0x0 && handleVrCoreAbsence((Context)activity, checkForVrCorePresence((Context)activity))) {
                    Log.w(AndroidNCompat.TAG, "Failed to handle missing VrCore package.");
                }
                return false;
            }
            catch (final UnsupportedOperationException obj2) {
                final String tag2 = AndroidNCompat.TAG;
                final String value2 = String.valueOf(obj2);
                Log.w(tag2, new StringBuilder(String.valueOf(value2).length() + 23).append("Failed to set VR mode: ").append(value2).toString());
                return false;
            }
        }
    }
    
    public static void setVrThread(final int p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ifeq            44
        //     6: ldc_w           Landroid/app/ActivityManager;.class
        //     9: ldc_w           "setVrThread"
        //    12: iconst_1       
        //    13: anewarray       Ljava/lang/Class;
        //    16: dup            
        //    17: iconst_0       
        //    18: getstatic       java/lang/Integer.TYPE:Ljava/lang/Class;
        //    21: aastore        
        //    22: invokevirtual   java/lang/Class.getMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    25: astore_1       
        //    26: aload_1        
        //    27: aconst_null    
        //    28: iconst_1       
        //    29: anewarray       Ljava/lang/Object;
        //    32: dup            
        //    33: iconst_0       
        //    34: iload_0        
        //    35: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    38: aastore        
        //    39: invokevirtual   java/lang/reflect/Method.invoke:(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        //    42: pop            
        //    43: return         
        //    44: return         
        //    45: astore_1       
        //    46: invokestatic    com/google/vr/cardboard/AndroidNCompat.isAtLeastNMR1:()Z
        //    49: ifne            97
        //    52: getstatic       com/google/vr/cardboard/AndroidNCompat.TAG:Ljava/lang/String;
        //    55: astore_2       
        //    56: aload_1        
        //    57: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    60: astore_1       
        //    61: aload_2        
        //    62: new             Ljava/lang/StringBuilder;
        //    65: dup            
        //    66: aload_1        
        //    67: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //    70: invokevirtual   java/lang/String.length:()I
        //    73: bipush          38
        //    75: iadd           
        //    76: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //    79: ldc_w           "Failed to acquire setVrThread method: "
        //    82: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    85: aload_1        
        //    86: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    89: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    92: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //    95: pop            
        //    96: return         
        //    97: getstatic       com/google/vr/cardboard/AndroidNCompat.TAG:Ljava/lang/String;
        //   100: astore_2       
        //   101: aload_1        
        //   102: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   105: astore_1       
        //   106: aload_2        
        //   107: new             Ljava/lang/StringBuilder;
        //   110: dup            
        //   111: aload_1        
        //   112: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   115: invokevirtual   java/lang/String.length:()I
        //   118: bipush          38
        //   120: iadd           
        //   121: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //   124: ldc_w           "Failed to acquire setVrThread method: "
        //   127: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   130: aload_1        
        //   131: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   134: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   137: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   140: pop            
        //   141: return         
        //   142: astore_2       
        //   143: getstatic       com/google/vr/cardboard/AndroidNCompat.TAG:Ljava/lang/String;
        //   146: astore_1       
        //   147: aload_2        
        //   148: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   151: astore_2       
        //   152: aload_1        
        //   153: new             Ljava/lang/StringBuilder;
        //   156: dup            
        //   157: aload_2        
        //   158: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   161: invokevirtual   java/lang/String.length:()I
        //   164: bipush          30
        //   166: iadd           
        //   167: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //   170: ldc_w           "Failed to invoke setVrThread: "
        //   173: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   176: aload_2        
        //   177: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   180: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   183: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   186: pop            
        //   187: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                         
        //  -----  -----  -----  -----  ---------------------------------------------
        //  6      26     45     142    Ljava/lang/NoSuchMethodException;
        //  6      26     45     142    Ljava/lang/RuntimeException;
        //  26     43     142    188    Ljava/lang/reflect/InvocationTargetException;
        //  26     43     142    188    Ljava/lang/IllegalAccessException;
        //  26     43     142    188    Ljava/lang/RuntimeException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0044:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static void showWarningDialog(final Context context, final int message, final int n, final DialogInterface$OnClickListener dialogInterface$OnClickListener) {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(context, R.style.GvrDialogTheme);
        alertDialog$Builder.setMessage(message).setTitle(R.string.dialog_title_warning).setPositiveButton(n, dialogInterface$OnClickListener).setNegativeButton(R.string.cancel_button, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public final void onClick(final DialogInterface dialogInterface, final int n) {
            }
        });
        alertDialog$Builder.create().show();
    }
}
