// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.util.DisplayMetrics;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vr.cardboard.DisplayUtils;
import android.view.Display;

public class ScreenParams
{
    private float borderSizeMeters;
    private int height;
    private int width;
    private float xMetersPerPixel;
    private float yMetersPerPixel;
    
    public ScreenParams(final Display display) {
        final DisplayMetrics displayMetricsLandscape = DisplayUtils.getDisplayMetricsLandscape(display);
        this.xMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(displayMetricsLandscape.xdpi);
        this.yMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(displayMetricsLandscape.ydpi);
        this.width = displayMetricsLandscape.widthPixels;
        this.height = displayMetricsLandscape.heightPixels;
        this.borderSizeMeters = DisplayUtils.getBorderSizeMeters(null);
        if (this.height > this.width) {
            final int width = this.width;
            this.width = this.height;
            this.height = width;
            final float xMetersPerPixel = this.xMetersPerPixel;
            this.xMetersPerPixel = this.yMetersPerPixel;
            this.yMetersPerPixel = xMetersPerPixel;
        }
    }
    
    public ScreenParams(final ScreenParams screenParams) {
        this.width = screenParams.width;
        this.height = screenParams.height;
        this.xMetersPerPixel = screenParams.xMetersPerPixel;
        this.yMetersPerPixel = screenParams.yMetersPerPixel;
        this.borderSizeMeters = screenParams.borderSizeMeters;
    }
    
    public static ScreenParams fromProto(final Display display, final Phone.PhoneParams phoneParams) {
        if (phoneParams != null) {
            final ScreenParams screenParams = new ScreenParams(display);
            if (phoneParams.hasXPpi()) {
                screenParams.xMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(phoneParams.getXPpi());
            }
            if (phoneParams.hasYPpi()) {
                screenParams.yMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(phoneParams.getYPpi());
            }
            screenParams.borderSizeMeters = DisplayUtils.getBorderSizeMeters(phoneParams);
            return screenParams;
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof ScreenParams) {
            final ScreenParams screenParams = (ScreenParams)o;
            if (this.width != screenParams.width || this.height != screenParams.height || this.xMetersPerPixel != screenParams.xMetersPerPixel || this.yMetersPerPixel != screenParams.yMetersPerPixel || this.borderSizeMeters != screenParams.borderSizeMeters) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public float getBorderSizeMeters() {
        return this.borderSizeMeters;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public float getHeightMeters() {
        return this.height * this.yMetersPerPixel;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public float getWidthMeters() {
        return this.width * this.xMetersPerPixel;
    }
    
    public void setBorderSizeMeters(final float borderSizeMeters) {
        this.borderSizeMeters = borderSizeMeters;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    @Override
    public String toString() {
        return "{\n" + new StringBuilder(22).append("  width: ").append(this.width).append(",\n").toString() + new StringBuilder(23).append("  height: ").append(this.height).append(",\n").toString() + new StringBuilder(39).append("  x_meters_per_pixel: ").append(this.xMetersPerPixel).append(",\n").toString() + new StringBuilder(39).append("  y_meters_per_pixel: ").append(this.yMetersPerPixel).append(",\n").toString() + new StringBuilder(39).append("  border_size_meters: ").append(this.borderSizeMeters).append(",\n").toString() + "}";
    }
}
