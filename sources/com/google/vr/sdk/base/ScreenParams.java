package com.google.vr.sdk.base;

import android.util.DisplayMetrics;
import android.view.Display;
import com.google.vr.cardboard.DisplayUtils;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;

public class ScreenParams {
    private float borderSizeMeters;
    private int height;
    private int width;
    private float xMetersPerPixel;
    private float yMetersPerPixel;

    public ScreenParams(Display display) {
        DisplayMetrics displayMetricsLandscape = DisplayUtils.getDisplayMetricsLandscape(display);
        this.xMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(displayMetricsLandscape.xdpi);
        this.yMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(displayMetricsLandscape.ydpi);
        this.width = displayMetricsLandscape.widthPixels;
        this.height = displayMetricsLandscape.heightPixels;
        this.borderSizeMeters = DisplayUtils.getBorderSizeMeters((Phone.PhoneParams) null);
        if (this.height > this.width) {
            int i = this.width;
            this.width = this.height;
            this.height = i;
            float f = this.xMetersPerPixel;
            this.xMetersPerPixel = this.yMetersPerPixel;
            this.yMetersPerPixel = f;
        }
    }

    public ScreenParams(ScreenParams screenParams) {
        this.width = screenParams.width;
        this.height = screenParams.height;
        this.xMetersPerPixel = screenParams.xMetersPerPixel;
        this.yMetersPerPixel = screenParams.yMetersPerPixel;
        this.borderSizeMeters = screenParams.borderSizeMeters;
    }

    public static ScreenParams fromProto(Display display, Phone.PhoneParams phoneParams) {
        if (phoneParams == null) {
            return null;
        }
        ScreenParams screenParams = new ScreenParams(display);
        if (phoneParams.hasXPpi()) {
            screenParams.xMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(phoneParams.getXPpi());
        }
        if (phoneParams.hasYPpi()) {
            screenParams.yMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(phoneParams.getYPpi());
        }
        screenParams.borderSizeMeters = DisplayUtils.getBorderSizeMeters(phoneParams);
        return screenParams;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ScreenParams)) {
            return false;
        }
        ScreenParams screenParams = (ScreenParams) obj;
        return this.width == screenParams.width && this.height == screenParams.height && this.xMetersPerPixel == screenParams.xMetersPerPixel && this.yMetersPerPixel == screenParams.yMetersPerPixel && this.borderSizeMeters == screenParams.borderSizeMeters;
    }

    public float getBorderSizeMeters() {
        return this.borderSizeMeters;
    }

    public int getHeight() {
        return this.height;
    }

    public float getHeightMeters() {
        return ((float) this.height) * this.yMetersPerPixel;
    }

    public int getWidth() {
        return this.width;
    }

    public float getWidthMeters() {
        return ((float) this.width) * this.xMetersPerPixel;
    }

    public void setBorderSizeMeters(float f) {
        this.borderSizeMeters = f;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public String toString() {
        return "{\n" + new StringBuilder(22).append("  width: ").append(this.width).append(",\n").toString() + new StringBuilder(23).append("  height: ").append(this.height).append(",\n").toString() + new StringBuilder(39).append("  x_meters_per_pixel: ").append(this.xMetersPerPixel).append(",\n").toString() + new StringBuilder(39).append("  y_meters_per_pixel: ").append(this.yMetersPerPixel).append(",\n").toString() + new StringBuilder(39).append("  border_size_meters: ").append(this.borderSizeMeters).append(",\n").toString() + "}";
    }
}
