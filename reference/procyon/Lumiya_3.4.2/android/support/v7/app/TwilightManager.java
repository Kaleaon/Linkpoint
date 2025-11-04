// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.app;

import java.util.Calendar;
import android.util.Log;
import android.support.v4.content.PermissionChecker;
import android.location.Location;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.NonNull;
import android.location.LocationManager;
import android.content.Context;

class TwilightManager
{
    private static final int SUNRISE = 6;
    private static final int SUNSET = 22;
    private static final String TAG = "TwilightManager";
    private static TwilightManager sInstance;
    private final Context mContext;
    private final LocationManager mLocationManager;
    private final TwilightState mTwilightState;
    
    @VisibleForTesting
    TwilightManager(@NonNull final Context mContext, @NonNull final LocationManager mLocationManager) {
        this.mTwilightState = new TwilightState();
        this.mContext = mContext;
        this.mLocationManager = mLocationManager;
    }
    
    static TwilightManager getInstance(@NonNull Context applicationContext) {
        if (TwilightManager.sInstance == null) {
            applicationContext = applicationContext.getApplicationContext();
            TwilightManager.sInstance = new TwilightManager(applicationContext, (LocationManager)applicationContext.getSystemService("location"));
        }
        return TwilightManager.sInstance;
    }
    
    private Location getLastKnownLocation() {
        boolean b = false;
        Location lastKnownLocationForProvider = null;
        Location lastKnownLocationForProvider2;
        if (PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            lastKnownLocationForProvider2 = null;
        }
        else {
            lastKnownLocationForProvider2 = this.getLastKnownLocationForProvider("network");
        }
        if (PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            lastKnownLocationForProvider = this.getLastKnownLocationForProvider("gps");
        }
        if (lastKnownLocationForProvider != null && lastKnownLocationForProvider2 != null) {
            if (lastKnownLocationForProvider.getTime() <= lastKnownLocationForProvider2.getTime()) {
                b = true;
            }
            if (!b) {
                lastKnownLocationForProvider2 = lastKnownLocationForProvider;
            }
            return lastKnownLocationForProvider2;
        }
        if (lastKnownLocationForProvider != null) {
            lastKnownLocationForProvider2 = lastKnownLocationForProvider;
        }
        return lastKnownLocationForProvider2;
    }
    
    private Location getLastKnownLocationForProvider(final String s) {
        if (this.mLocationManager != null) {
            try {
                if (this.mLocationManager.isProviderEnabled(s)) {
                    return this.mLocationManager.getLastKnownLocation(s);
                }
            }
            catch (final Exception ex) {
                Log.d("TwilightManager", "Failed to get last known location", (Throwable)ex);
            }
        }
        return null;
    }
    
    private boolean isStateValid() {
        boolean b = true;
        if (this.mTwilightState != null) {
            int n;
            if (this.mTwilightState.nextUpdate <= System.currentTimeMillis()) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                return b;
            }
        }
        b = false;
        return b;
    }
    
    @VisibleForTesting
    static void setInstance(final TwilightManager sInstance) {
        TwilightManager.sInstance = sInstance;
    }
    
    private void updateState(@NonNull final Location location) {
        final TwilightState mTwilightState = this.mTwilightState;
        final long currentTimeMillis = System.currentTimeMillis();
        final TwilightCalculator instance = TwilightCalculator.getInstance();
        instance.calculateTwilight(currentTimeMillis - 86400000L, location.getLatitude(), location.getLongitude());
        final long sunset = instance.sunset;
        instance.calculateTwilight(currentTimeMillis, location.getLatitude(), location.getLongitude());
        final boolean isNight = instance.state == 1;
        final long sunrise = instance.sunrise;
        final long sunset2 = instance.sunset;
        instance.calculateTwilight(86400000L + currentTimeMillis, location.getLatitude(), location.getLongitude());
        final long sunrise2 = instance.sunrise;
        long nextUpdate;
        if (sunrise == -1L || sunset2 == -1L) {
            nextUpdate = 43200000L + currentTimeMillis;
        }
        else {
            int n;
            if (currentTimeMillis <= sunset2) {
                n = 1;
            }
            else {
                n = 0;
            }
            long n2;
            if (n == 0) {
                n2 = 0L + sunrise2;
            }
            else {
                int n3;
                if (currentTimeMillis <= sunrise) {
                    n3 = 1;
                }
                else {
                    n3 = 0;
                }
                if (n3 == 0) {
                    n2 = 0L + sunset2;
                }
                else {
                    n2 = 0L + sunrise;
                }
            }
            nextUpdate = n2 + 60000L;
        }
        mTwilightState.isNight = isNight;
        mTwilightState.yesterdaySunset = sunset;
        mTwilightState.todaySunrise = sunrise;
        mTwilightState.todaySunset = sunset2;
        mTwilightState.tomorrowSunrise = sunrise2;
        mTwilightState.nextUpdate = nextUpdate;
    }
    
    boolean isNight() {
        boolean b = false;
        final TwilightState mTwilightState = this.mTwilightState;
        if (this.isStateValid()) {
            return mTwilightState.isNight;
        }
        final Location lastKnownLocation = this.getLastKnownLocation();
        if (lastKnownLocation == null) {
            Log.i("TwilightManager", "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
            final int value = Calendar.getInstance().get(11);
            if (value < 6 || value >= 22) {
                b = true;
            }
            return b;
        }
        this.updateState(lastKnownLocation);
        return mTwilightState.isNight;
    }
    
    private static class TwilightState
    {
        boolean isNight;
        long nextUpdate;
        long todaySunrise;
        long todaySunset;
        long tomorrowSunrise;
        long yesterdaySunset;
        
        TwilightState() {
        }
    }
}
