// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.hardware.display.DisplayManager;
import android.content.res.Resources;
import android.content.res.Resources$NotFoundException;
import android.os.Build$VERSION;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.Display;
import android.content.Context;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;

public class DisplayUtils
{
    private static final float DEFAULT_BORDER_SIZE_METERS = 0.003f;
    public static final String EXTERNAL_DISPLAY_RESOURCE_NAME = "display_manager_hdmi_display_name";
    private static final float METERS_PER_INCH = 0.0254f;
    
    public static float getBorderSizeMeters(final Phone.PhoneParams phoneParams) {
        if (phoneParams != null && phoneParams.hasBottomBezelHeight()) {
            return phoneParams.getBottomBezelHeight();
        }
        return 0.003f;
    }
    
    public static Display getDefaultDisplay(final Context context) {
        return ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
    }
    
    public static DisplayMetrics getDisplayMetricsLandscape(final Display display) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build$VERSION.SDK_INT < 17) {
            display.getMetrics(displayMetrics);
        }
        else {
            display.getRealMetrics(displayMetrics);
        }
        if (displayMetrics.widthPixels < displayMetrics.heightPixels) {
            final int widthPixels = displayMetrics.widthPixels;
            displayMetrics.widthPixels = displayMetrics.heightPixels;
            displayMetrics.heightPixels = widthPixels;
        }
        final float xdpi = displayMetrics.xdpi;
        displayMetrics.xdpi = displayMetrics.ydpi;
        displayMetrics.ydpi = xdpi;
        return displayMetrics;
    }
    
    public static DisplayMetrics getDisplayMetricsLandscapeWithOverride(final Display display, final Phone.PhoneParams phoneParams) {
        final DisplayMetrics displayMetricsLandscape = getDisplayMetricsLandscape(display);
        if (phoneParams != null) {
            if (phoneParams.hasXPpi()) {
                displayMetricsLandscape.xdpi = phoneParams.getXPpi();
            }
            if (phoneParams.hasYPpi()) {
                displayMetricsLandscape.ydpi = phoneParams.getYPpi();
            }
        }
        return displayMetricsLandscape;
    }
    
    public static String getExternalDisplayName(final Context context) {
        final Resources resources = context.getResources();
        final int identifier = resources.getIdentifier("display_manager_hdmi_display_name", "string", "android");
        try {
            return resources.getString(identifier);
        }
        catch (final Resources$NotFoundException ex) {
            return null;
        }
    }
    
    public static float getMetersPerPixelFromDotsPerInch(final float n) {
        return 0.0254f / n;
    }
    
    public static boolean hasExternalDisplay(final Context context) {
        if (Build$VERSION.SDK_INT <= 16) {
            return false;
        }
        final String externalDisplayName = getExternalDisplayName(context);
        if (externalDisplayName != null) {
            final Display[] displays = ((DisplayManager)context.getSystemService("display")).getDisplays();
            for (int length = displays.length, i = 0; i < length; ++i) {
                if (displays[i].getName().equals(externalDisplayName)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static boolean isSameDisplay(final Display display, final Display display2) {
        if (display == display2) {
            return true;
        }
        if (display == null || display2 == null) {
            return false;
        }
        if (display.getDisplayId() == display2.getDisplayId() && display.getFlags() == display2.getFlags() && display.isValid() == display2.isValid() && display.getName().equals(display2.getName())) {
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            final DisplayMetrics displayMetrics2 = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            display2.getMetrics(displayMetrics2);
            return displayMetrics.equals(displayMetrics2);
        }
        return false;
    }
}
