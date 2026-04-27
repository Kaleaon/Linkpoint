// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.net;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.net.NetworkInfo;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build$VERSION;

public final class ConnectivityManagerCompat
{
    private static final ConnectivityManagerCompatImpl IMPL;
    public static final int RESTRICT_BACKGROUND_STATUS_DISABLED = 1;
    public static final int RESTRICT_BACKGROUND_STATUS_ENABLED = 3;
    public static final int RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2;
    
    static {
        if (Build$VERSION.SDK_INT < 24) {
            if (Build$VERSION.SDK_INT < 16) {
                IMPL = (ConnectivityManagerCompatImpl)new ConnectivityManagerCompatBaseImpl();
            }
            else {
                IMPL = (ConnectivityManagerCompatImpl)new ConnectivityManagerCompatApi16Impl();
            }
        }
        else {
            IMPL = (ConnectivityManagerCompatImpl)new ConnectivityManagerCompatApi24Impl();
        }
    }
    
    private ConnectivityManagerCompat() {
    }
    
    public static NetworkInfo getNetworkInfoFromBroadcast(final ConnectivityManager connectivityManager, final Intent intent) {
        final NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
        if (networkInfo == null) {
            return null;
        }
        return connectivityManager.getNetworkInfo(networkInfo.getType());
    }
    
    public static int getRestrictBackgroundStatus(final ConnectivityManager connectivityManager) {
        return ConnectivityManagerCompat.IMPL.getRestrictBackgroundStatus(connectivityManager);
    }
    
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isActiveNetworkMetered(final ConnectivityManager connectivityManager) {
        return ConnectivityManagerCompat.IMPL.isActiveNetworkMetered(connectivityManager);
    }
    
    @RequiresApi(16)
    static class ConnectivityManagerCompatApi16Impl extends ConnectivityManagerCompatBaseImpl
    {
        @Override
        public boolean isActiveNetworkMetered(final ConnectivityManager connectivityManager) {
            return connectivityManager.isActiveNetworkMetered();
        }
    }
    
    static class ConnectivityManagerCompatBaseImpl implements ConnectivityManagerCompatImpl
    {
        @Override
        public int getRestrictBackgroundStatus(final ConnectivityManager connectivityManager) {
            return 3;
        }
        
        @Override
        public boolean isActiveNetworkMetered(final ConnectivityManager connectivityManager) {
            final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return true;
            }
            switch (activeNetworkInfo.getType()) {
                default: {
                    return true;
                }
                case 0:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6: {
                    return true;
                }
                case 1:
                case 7:
                case 9: {
                    return false;
                }
            }
        }
    }
    
    @RequiresApi(24)
    static class ConnectivityManagerCompatApi24Impl extends ConnectivityManagerCompatApi16Impl
    {
        @Override
        public int getRestrictBackgroundStatus(final ConnectivityManager connectivityManager) {
            return connectivityManager.getRestrictBackgroundStatus();
        }
    }
    
    interface ConnectivityManagerCompatImpl
    {
        int getRestrictBackgroundStatus(final ConnectivityManager p0);
        
        boolean isActiveNetworkMetered(final ConnectivityManager p0);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface RestrictBackgroundStatus {
    }
}
