// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.support.v4.app.AppOpsManagerCompat;
import android.os.Process;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.content.Context;

public final class PermissionChecker
{
    public static final int PERMISSION_DENIED = -1;
    public static final int PERMISSION_DENIED_APP_OP = -2;
    public static final int PERMISSION_GRANTED = 0;
    
    private PermissionChecker() {
    }
    
    public static int checkCallingOrSelfPermission(@NonNull final Context context, @NonNull final String s) {
        String packageName;
        if (Binder.getCallingPid() != Process.myPid()) {
            packageName = null;
        }
        else {
            packageName = context.getPackageName();
        }
        return checkPermission(context, s, Binder.getCallingPid(), Binder.getCallingUid(), packageName);
    }
    
    public static int checkCallingPermission(@NonNull final Context context, @NonNull final String s, final String s2) {
        if (Binder.getCallingPid() != Process.myPid()) {
            return checkPermission(context, s, Binder.getCallingPid(), Binder.getCallingUid(), s2);
        }
        return -1;
    }
    
    public static int checkPermission(@NonNull final Context context, @NonNull String permissionToOp, final int n, final int n2, String s) {
        if (context.checkPermission(permissionToOp, n, n2) == -1) {
            return -1;
        }
        permissionToOp = AppOpsManagerCompat.permissionToOp(permissionToOp);
        if (permissionToOp == null) {
            return 0;
        }
        if (s == null) {
            final String[] packagesForUid = context.getPackageManager().getPackagesForUid(n2);
            if (packagesForUid == null || packagesForUid.length <= 0) {
                return -1;
            }
            s = packagesForUid[0];
        }
        if (AppOpsManagerCompat.noteProxyOp(context, permissionToOp, s) == 0) {
            return 0;
        }
        return -2;
    }
    
    public static int checkSelfPermission(@NonNull final Context context, @NonNull final String s) {
        return checkPermission(context, s, Process.myPid(), Process.myUid(), context.getPackageName());
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface PermissionResult {
    }
}
