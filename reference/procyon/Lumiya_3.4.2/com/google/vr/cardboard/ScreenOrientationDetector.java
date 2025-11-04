// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.content.Context;
import android.view.OrientationEventListener;

public class ScreenOrientationDetector extends OrientationEventListener
{
    private static final int DEFAULT_LANDSCAPE_TOLERANCE_DEGREES = 10;
    private static final int DEFAULT_PORTRAIT_TOLERANCE_DEGREES = 30;
    private final Listener clientListener;
    private int currentScreenOrientation;
    private final int landscapeToleranceDegrees;
    private final int portraitToleranceDegrees;
    
    public ScreenOrientationDetector(final Context context, final Listener listener) {
        this(context, listener, 30, 10);
    }
    
    public ScreenOrientationDetector(final Context context, final Listener clientListener, final int portraitToleranceDegrees, final int landscapeToleranceDegrees) {
        super(context);
        this.currentScreenOrientation = -1;
        if (portraitToleranceDegrees + landscapeToleranceDegrees <= 90) {
            this.clientListener = clientListener;
            this.portraitToleranceDegrees = portraitToleranceDegrees;
            this.landscapeToleranceDegrees = landscapeToleranceDegrees;
            return;
        }
        throw new IllegalArgumentException("Portrait and landscape detection thresholds must sum to to <= 90 degrees");
    }
    
    private int determineScreenOrientation(int n) {
        if (n == -1) {
            return -1;
        }
        n %= 360;
        if (n <= this.portraitToleranceDegrees || n >= 360 - this.portraitToleranceDegrees) {
            return 2;
        }
        if (Math.abs(n - 90) <= this.landscapeToleranceDegrees) {
            return 1;
        }
        if (Math.abs(n - 180) <= this.portraitToleranceDegrees) {
            return 3;
        }
        if (Math.abs(n - 270) > this.landscapeToleranceDegrees) {
            return this.currentScreenOrientation;
        }
        return 0;
    }
    
    public void disable() {
        super.disable();
        this.currentScreenOrientation = -1;
    }
    
    public void enable() {
        this.currentScreenOrientation = -1;
        super.enable();
    }
    
    public int getCurrentScreenOrientation() {
        return this.currentScreenOrientation;
    }
    
    public void onOrientationChanged(int determineScreenOrientation) {
        determineScreenOrientation = this.determineScreenOrientation(determineScreenOrientation);
        if (determineScreenOrientation != this.currentScreenOrientation) {
            this.currentScreenOrientation = determineScreenOrientation;
            this.clientListener.onScreenOrientationChanged(determineScreenOrientation);
        }
    }
    
    public interface Listener
    {
        void onScreenOrientationChanged(final int p0);
    }
    
    public abstract class Orientation
    {
        public static final int LANDSCAPE = 0;
        public static final int LANDSCAPE_REVERSE = 1;
        public static final int PORTRAIT = 2;
        public static final int PORTRAIT_REVERSE = 3;
        public static final int UNKNOWN = -1;
        
        public Orientation(final ScreenOrientationDetector screenOrientationDetector) {
        }
    }
}
