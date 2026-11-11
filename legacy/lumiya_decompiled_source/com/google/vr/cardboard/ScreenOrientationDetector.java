package com.google.vr.cardboard;

import android.content.Context;
import android.view.OrientationEventListener;
/* loaded from: classes.dex */
public class ScreenOrientationDetector extends OrientationEventListener {
    private static final int DEFAULT_LANDSCAPE_TOLERANCE_DEGREES = 10;
    private static final int DEFAULT_PORTRAIT_TOLERANCE_DEGREES = 30;
    private final Listener clientListener;
    private int currentScreenOrientation;
    private final int landscapeToleranceDegrees;
    private final int portraitToleranceDegrees;

    /* loaded from: classes.dex */
    public interface Listener {
        void onScreenOrientationChanged(int i);
    }

    /* loaded from: classes.dex */
    public abstract class Orientation {
        public static final int LANDSCAPE = 0;
        public static final int LANDSCAPE_REVERSE = 1;
        public static final int PORTRAIT = 2;
        public static final int PORTRAIT_REVERSE = 3;
        public static final int UNKNOWN = -1;

        public Orientation(ScreenOrientationDetector screenOrientationDetector) {
        }
    }

    public ScreenOrientationDetector(Context context, Listener listener) {
        this(context, listener, 30, 10);
    }

    public ScreenOrientationDetector(Context context, Listener listener, int i, int i2) {
        super(context);
        this.currentScreenOrientation = -1;
        if (i + i2 > 90) {
            throw new IllegalArgumentException("Portrait and landscape detection thresholds must sum to to <= 90 degrees");
        }
        this.clientListener = listener;
        this.portraitToleranceDegrees = i;
        this.landscapeToleranceDegrees = i2;
    }

    private int determineScreenOrientation(int i) {
        if (i != -1) {
            int i2 = i % 360;
            if (i2 > this.portraitToleranceDegrees && i2 < 360 - this.portraitToleranceDegrees) {
                if (Math.abs(i2 - 90) > this.landscapeToleranceDegrees) {
                    if (Math.abs(i2 - 180) > this.portraitToleranceDegrees) {
                        if (Math.abs(i2 - 270) > this.landscapeToleranceDegrees) {
                            return this.currentScreenOrientation;
                        }
                        return 0;
                    }
                    return 3;
                }
                return 1;
            }
            return 2;
        }
        return -1;
    }

    @Override // android.view.OrientationEventListener
    public void disable() {
        super.disable();
        this.currentScreenOrientation = -1;
    }

    @Override // android.view.OrientationEventListener
    public void enable() {
        this.currentScreenOrientation = -1;
        super.enable();
    }

    public int getCurrentScreenOrientation() {
        return this.currentScreenOrientation;
    }

    @Override // android.view.OrientationEventListener
    public void onOrientationChanged(int i) {
        int determineScreenOrientation = determineScreenOrientation(i);
        if (determineScreenOrientation == this.currentScreenOrientation) {
            return;
        }
        this.currentScreenOrientation = determineScreenOrientation;
        this.clientListener.onScreenOrientationChanged(determineScreenOrientation);
    }
}
