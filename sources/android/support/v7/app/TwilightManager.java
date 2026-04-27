package android.support.v7.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSequenceInfo;
import java.util.Calendar;

class TwilightManager {
    private static final int SUNRISE = 6;
    private static final int SUNSET = 22;
    private static final String TAG = "TwilightManager";
    private static TwilightManager sInstance;
    private final Context mContext;
    private final LocationManager mLocationManager;
    private final TwilightState mTwilightState = new TwilightState();

    private static class TwilightState {
        boolean isNight;
        long nextUpdate;
        long todaySunrise;
        long todaySunset;
        long tomorrowSunrise;
        long yesterdaySunset;

        TwilightState() {
        }
    }

    @VisibleForTesting
    TwilightManager(@NonNull Context context, @NonNull LocationManager locationManager) {
        this.mContext = context;
        this.mLocationManager = locationManager;
    }

    static TwilightManager getInstance(@NonNull Context context) {
        if (sInstance == null) {
            Context applicationContext = context.getApplicationContext();
            sInstance = new TwilightManager(applicationContext, (LocationManager) applicationContext.getSystemService("location"));
        }
        return sInstance;
    }

    private Location getLastKnownLocation() {
        boolean z = false;
        Location location = null;
        Location lastKnownLocationForProvider = PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") != 0 ? null : getLastKnownLocationForProvider("network");
        if (PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            location = getLastKnownLocationForProvider("gps");
        }
        if (location == null || lastKnownLocationForProvider == null) {
            return location == null ? lastKnownLocationForProvider : location;
        }
        if (location.getTime() <= lastKnownLocationForProvider.getTime()) {
            z = true;
        }
        return !z ? location : lastKnownLocationForProvider;
    }

    private Location getLastKnownLocationForProvider(String str) {
        if (this.mLocationManager != null) {
            try {
                if (this.mLocationManager.isProviderEnabled(str)) {
                    return this.mLocationManager.getLastKnownLocation(str);
                }
            } catch (Exception e) {
                Log.d(TAG, "Failed to get last known location", e);
            }
        }
        return null;
    }

    private boolean isStateValid() {
        if (this.mTwilightState != null) {
            if (!(this.mTwilightState.nextUpdate <= System.currentTimeMillis())) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    static void setInstance(TwilightManager twilightManager) {
        sInstance = twilightManager;
    }

    private void updateState(@NonNull Location location) {
        long j;
        long j2;
        TwilightState twilightState = this.mTwilightState;
        long currentTimeMillis = System.currentTimeMillis();
        TwilightCalculator instance = TwilightCalculator.getInstance();
        instance.calculateTwilight(currentTimeMillis - 86400000, location.getLatitude(), location.getLongitude());
        long j3 = instance.sunset;
        instance.calculateTwilight(currentTimeMillis, location.getLatitude(), location.getLongitude());
        boolean z = instance.state == 1;
        long j4 = instance.sunrise;
        long j5 = instance.sunset;
        instance.calculateTwilight(86400000 + currentTimeMillis, location.getLatitude(), location.getLongitude());
        long j6 = instance.sunrise;
        if (j4 == -1 || j5 == -1) {
            j = 43200000 + currentTimeMillis;
        } else {
            if (!(currentTimeMillis <= j5)) {
                j2 = 0 + j6;
            } else {
                j2 = !((currentTimeMillis > j4 ? 1 : (currentTimeMillis == j4 ? 0 : -1)) <= 0) ? 0 + j5 : 0 + j4;
            }
            j = j2 + AnimationSequenceInfo.MAX_ANIMATION_LENGTH;
        }
        twilightState.isNight = z;
        twilightState.yesterdaySunset = j3;
        twilightState.todaySunrise = j4;
        twilightState.todaySunset = j5;
        twilightState.tomorrowSunrise = j6;
        twilightState.nextUpdate = j;
    }

    /* access modifiers changed from: package-private */
    public boolean isNight() {
        TwilightState twilightState = this.mTwilightState;
        if (isStateValid()) {
            return twilightState.isNight;
        }
        Location lastKnownLocation = getLastKnownLocation();
        if (lastKnownLocation == null) {
            Log.i(TAG, "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
            int i = Calendar.getInstance().get(11);
            return i < 6 || i >= 22;
        }
        updateState(lastKnownLocation);
        return twilightState.isNight;
    }
}
