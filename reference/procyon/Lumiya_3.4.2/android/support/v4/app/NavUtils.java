// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.Context;
import android.content.ComponentName;
import android.os.Build$VERSION;
import android.content.Intent;
import android.app.Activity;

public final class NavUtils
{
    public static final String PARENT_ACTIVITY = "android.support.PARENT_ACTIVITY";
    private static final String TAG = "NavUtils";
    
    private NavUtils() {
    }
    
    public static Intent getParentActivityIntent(final Activity activity) {
        Label_0051: {
            if (Build$VERSION.SDK_INT >= 16) {
                break Label_0051;
            }
            Object str = null;
            Label_0008: {
                str = getParentActivityName(activity);
            }
            Label_0062: {
                if (str == null) {
                    break Label_0062;
                }
                final ComponentName component = new ComponentName((Context)activity, (String)str);
                try {
                    Intent intent;
                    if (getParentActivityName((Context)activity, component) != null) {
                        intent = new Intent().setComponent(component);
                    }
                    else {
                        intent = Intent.makeMainActivity(component);
                    }
                    return intent;
                    str = activity.getParentActivityIntent();
                    iftrue(Label_0008:)(str == null);
                    return (Intent)str;
                    return null;
                }
                catch (final PackageManager$NameNotFoundException ex) {
                    Log.e("NavUtils", "getParentActivityIntent: bad parentActivityName '" + (String)str + "' in manifest");
                    return null;
                }
            }
        }
    }
    
    public static Intent getParentActivityIntent(final Context context, ComponentName component) throws PackageManager$NameNotFoundException {
        final String parentActivityName = getParentActivityName(context, component);
        if (parentActivityName != null) {
            component = new ComponentName(component.getPackageName(), parentActivityName);
            Intent intent;
            if (getParentActivityName(context, component) != null) {
                intent = new Intent().setComponent(component);
            }
            else {
                intent = Intent.makeMainActivity(component);
            }
            return intent;
        }
        return null;
    }
    
    public static Intent getParentActivityIntent(final Context context, final Class<?> clazz) throws PackageManager$NameNotFoundException {
        final String parentActivityName = getParentActivityName(context, new ComponentName(context, (Class)clazz));
        if (parentActivityName != null) {
            final ComponentName component = new ComponentName(context, parentActivityName);
            Intent intent;
            if (getParentActivityName(context, component) != null) {
                intent = new Intent().setComponent(component);
            }
            else {
                intent = Intent.makeMainActivity(component);
            }
            return intent;
        }
        return null;
    }
    
    @Nullable
    public static String getParentActivityName(final Activity activity) {
        try {
            return getParentActivityName((Context)activity, activity.getComponentName());
        }
        catch (final PackageManager$NameNotFoundException cause) {
            throw new IllegalArgumentException((Throwable)cause);
        }
    }
    
    @Nullable
    public static String getParentActivityName(final Context context, final ComponentName componentName) throws PackageManager$NameNotFoundException {
        final ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(componentName, 128);
        if (Build$VERSION.SDK_INT >= 16) {
            final String parentActivityName = activityInfo.parentActivityName;
            if (parentActivityName != null) {
                return parentActivityName;
            }
        }
        if (activityInfo.metaData == null) {
            return null;
        }
        final String string = activityInfo.metaData.getString("android.support.PARENT_ACTIVITY");
        if (string != null) {
            String string2;
            if (string.charAt(0) != '.') {
                string2 = string;
            }
            else {
                string2 = context.getPackageName() + string;
            }
            return string2;
        }
        return null;
    }
    
    public static void navigateUpFromSameTask(final Activity activity) {
        final Intent parentActivityIntent = getParentActivityIntent(activity);
        if (parentActivityIntent != null) {
            navigateUpTo(activity, parentActivityIntent);
            return;
        }
        throw new IllegalArgumentException("Activity " + activity.getClass().getSimpleName() + " does not have a parent activity name specified." + " (Did you forget to add the android.support.PARENT_ACTIVITY <meta-data> " + " element in your manifest?)");
    }
    
    public static void navigateUpTo(final Activity activity, final Intent intent) {
        if (Build$VERSION.SDK_INT < 16) {
            intent.addFlags(67108864);
            activity.startActivity(intent);
            activity.finish();
        }
        else {
            activity.navigateUpTo(intent);
        }
    }
    
    public static boolean shouldUpRecreateTask(final Activity activity, final Intent intent) {
        boolean b = false;
        if (Build$VERSION.SDK_INT < 16) {
            final String action = activity.getIntent().getAction();
            if (action != null && !action.equals("android.intent.action.MAIN")) {
                b = true;
            }
            return b;
        }
        return activity.shouldUpRecreateTask(intent);
    }
}
