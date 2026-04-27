package com.google.vr.ndk.base;

import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import com.google.vr.cardboard.DisplayUtils;
import com.google.vr.cardboard.VrParamsProvider;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;

class DaydreamAlignment {
    private static final double MAX_TOUCH_DISTANCE_SQUARED_METERS = 2.25E-4d;
    private static final String TAG = "DaydreamAlignment";
    /* access modifiers changed from: private */
    public float borderSizeMeters;
    /* access modifiers changed from: private */
    public double[] currentMarkerBestDists;
    /* access modifiers changed from: private */
    public DisplayMetrics displayMetrics;
    private boolean enabled = true;
    private final boolean isDaydreamImageAlignmentEnabled;
    private boolean lastMotionEventInHeadset;
    /* access modifiers changed from: private */
    public int[] markerBestTouch;
    /* access modifiers changed from: private */
    public float[][] markersInPixels;
    private int mostTouchesSeen;
    private float[] pixelTranslation = new float[2];
    private int[] touchBestMarker;
    /* access modifiers changed from: private */
    public final VrParamsProvider vrParamsProvider;
    /* access modifiers changed from: private */
    public float xMetersPerPixel;
    /* access modifiers changed from: private */
    public float yMetersPerPixel;

    public static class DefaultTouchListener implements View.OnTouchListener {
        private final DaydreamAlignment daydreamAlignment;
        private final GvrApi gvrApi;
        private float[] lastTranslation = new float[2];
        private float[] translation = new float[2];

        public DefaultTouchListener(DaydreamAlignment daydreamAlignment2, GvrApi gvrApi2) {
            this.daydreamAlignment = daydreamAlignment2;
            this.gvrApi = gvrApi2;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!this.daydreamAlignment.processMotionEvent(motionEvent)) {
                return false;
            }
            if (this.daydreamAlignment.wasLastMotionEventInViewer()) {
                this.daydreamAlignment.getTranslationInScreenSpace(this.translation);
            } else {
                this.translation[0] = 0.0f;
                this.translation[1] = 0.0f;
            }
            if (!(this.translation[0] == this.lastTranslation[0] && this.translation[1] == this.lastTranslation[1])) {
                this.lastTranslation[0] = this.translation[0];
                this.lastTranslation[1] = this.translation[1];
                this.gvrApi.setLensOffset(this.translation[0], this.translation[1]);
            }
            return true;
        }
    }

    private class FinishInitilizationTask extends AsyncTask<Void, Void, Phone.PhoneParams> {
        public Display display;

        private FinishInitilizationTask() {
        }

        /* access modifiers changed from: protected */
        public Phone.PhoneParams doInBackground(Void... voidArr) {
            return DaydreamAlignment.this.vrParamsProvider.readPhoneParams();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Phone.PhoneParams phoneParams) {
            DaydreamAlignment.this.init(DisplayUtils.getDisplayMetricsLandscapeWithOverride(this.display, phoneParams), phoneParams);
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Void... voidArr) {
        }
    }

    private class RefreshViewerProfileTask extends AsyncTask<Void, Void, CardboardDevice.DeviceParams> {
        private RefreshViewerProfileTask() {
        }

        /* access modifiers changed from: protected */
        public CardboardDevice.DeviceParams doInBackground(Void... voidArr) {
            return DaydreamAlignment.this.vrParamsProvider.readDeviceParams();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(CardboardDevice.DeviceParams deviceParams) {
            if (deviceParams == null || deviceParams.daydreamInternal == null || deviceParams.daydreamInternal.alignmentMarkers == null) {
                float[][] unused = DaydreamAlignment.this.markersInPixels = null;
                return;
            }
            CardboardDevice.ScreenAlignmentMarker[] screenAlignmentMarkerArr = deviceParams.daydreamInternal.alignmentMarkers;
            float[][] unused2 = DaydreamAlignment.this.markersInPixels = new float[screenAlignmentMarkerArr.length][];
            double[] unused3 = DaydreamAlignment.this.currentMarkerBestDists = new double[screenAlignmentMarkerArr.length];
            int[] unused4 = DaydreamAlignment.this.markerBestTouch = new int[screenAlignmentMarkerArr.length];
            for (int i = 0; i < screenAlignmentMarkerArr.length; i++) {
                CardboardDevice.ScreenAlignmentMarker screenAlignmentMarker = screenAlignmentMarkerArr[i];
                DaydreamAlignment.this.markersInPixels[i] = new float[2];
                DaydreamAlignment.this.markersInPixels[i][0] = ((float) (DaydreamAlignment.this.displayMetrics.widthPixels / 2)) + (screenAlignmentMarker.getHorizontal() / DaydreamAlignment.this.xMetersPerPixel);
                DaydreamAlignment.this.markersInPixels[i][1] = ((float) DaydreamAlignment.this.displayMetrics.heightPixels) - (((screenAlignmentMarker.getVertical() + deviceParams.getTrayToLensDistance()) - DaydreamAlignment.this.borderSizeMeters) / DaydreamAlignment.this.yMetersPerPixel);
            }
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Void... voidArr) {
        }
    }

    public DaydreamAlignment(Context context, GvrApi gvrApi) {
        boolean z = true;
        this.isDaydreamImageAlignmentEnabled = gvrApi.getSdkConfigurationParams().daydreamImageAlignment.intValue() == 1 ? false : z;
        this.vrParamsProvider = VrParamsProviderFactory.create(context);
        FinishInitilizationTask finishInitilizationTask = new FinishInitilizationTask();
        finishInitilizationTask.display = DisplayUtils.getDefaultDisplay(context);
        finishInitilizationTask.execute(new Void[0]);
    }

    DaydreamAlignment(VrParamsProvider vrParamsProvider2, DisplayMetrics displayMetrics2, Phone.PhoneParams phoneParams, boolean z) {
        this.isDaydreamImageAlignmentEnabled = z;
        this.vrParamsProvider = vrParamsProvider2;
        init(displayMetrics2, phoneParams);
    }

    /* access modifiers changed from: private */
    public void init(DisplayMetrics displayMetrics2, Phone.PhoneParams phoneParams) {
        this.displayMetrics = displayMetrics2;
        this.borderSizeMeters = DisplayUtils.getBorderSizeMeters(phoneParams);
        this.xMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(this.displayMetrics.xdpi);
        this.yMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(this.displayMetrics.ydpi);
        resetTrackingState();
        refreshViewerProfile();
    }

    private void resetTrackingState() {
        this.lastMotionEventInHeadset = false;
        this.pixelTranslation[0] = 0.0f;
        this.pixelTranslation[1] = 0.0f;
        this.mostTouchesSeen = 0;
    }

    /* access modifiers changed from: package-private */
    public void getTranslationInPixels(float[] fArr) {
        if (fArr.length >= 2) {
            fArr[0] = this.pixelTranslation[0];
            fArr[1] = this.pixelTranslation[1];
            return;
        }
        throw new IllegalArgumentException("Translation array too small");
    }

    public void getTranslationInScreenSpace(float[] fArr) {
        if (fArr.length >= 2) {
            fArr[0] = this.pixelTranslation[0] / ((float) this.displayMetrics.widthPixels);
            fArr[1] = this.pixelTranslation[1] / ((float) this.displayMetrics.heightPixels);
            fArr[0] = fArr[0] * 4.0f;
            fArr[1] = fArr[1] * -2.0f;
            return;
        }
        throw new IllegalArgumentException("Translation array too small");
    }

    /* access modifiers changed from: package-private */
    public boolean isDaydreamImageAlignmentEnabled() {
        return this.isDaydreamImageAlignmentEnabled;
    }

    public boolean processMotionEvent(MotionEvent motionEvent) {
        if (!viewerNeedsTouchProcessing()) {
            this.lastMotionEventInHeadset = false;
            return false;
        } else if (!isDaydreamImageAlignmentEnabled()) {
            return true;
        } else {
            int pointerCount = motionEvent.getPointerCount();
            if (pointerCount > this.mostTouchesSeen) {
                this.touchBestMarker = new int[pointerCount];
                this.mostTouchesSeen = pointerCount;
            }
            for (int i = 0; i < this.markersInPixels.length; i++) {
                this.markerBestTouch[i] = -1;
                this.currentMarkerBestDists[i] = 2.25E-4d;
            }
            for (int i2 = 0; i2 < pointerCount; i2++) {
                double d = MAX_TOUCH_DISTANCE_SQUARED_METERS;
                this.touchBestMarker[i2] = -1;
                for (int i3 = 0; i3 < this.markersInPixels.length; i3++) {
                    float x = (this.markersInPixels[i3][0] - motionEvent.getX(i2)) * this.xMetersPerPixel;
                    float y = (this.markersInPixels[i3][1] - motionEvent.getY(i2)) * this.yMetersPerPixel;
                    double d2 = (double) ((x * x) + (y * y));
                    if (d2 < d) {
                        this.touchBestMarker[i2] = i3;
                        d = d2;
                    }
                    if (d2 < this.currentMarkerBestDists[i3]) {
                        this.currentMarkerBestDists[i3] = d2;
                        this.markerBestTouch[i3] = i2;
                    }
                }
            }
            float f = 0.0f;
            float f2 = 0.0f;
            int i4 = 0;
            for (int i5 = 0; i5 < this.markerBestTouch.length; i5++) {
                if (this.markerBestTouch[i5] != -1) {
                    if (this.touchBestMarker[this.markerBestTouch[i5]] == i5) {
                        i4++;
                        f += motionEvent.getX(this.markerBestTouch[i5]) - this.markersInPixels[i5][0];
                        f2 += motionEvent.getY(this.markerBestTouch[i5]) - this.markersInPixels[i5][1];
                    } else {
                        this.markerBestTouch[i5] = -1;
                    }
                }
            }
            if (i4 <= 0) {
                this.lastMotionEventInHeadset = false;
                return true;
            }
            this.lastMotionEventInHeadset = true;
            this.pixelTranslation[0] = f / ((float) i4);
            this.pixelTranslation[1] = f2 / ((float) i4);
            return true;
        }
    }

    public void refreshViewerProfile() {
        new RefreshViewerProfileTask().execute(new Void[0]);
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
        if (!z) {
            resetTrackingState();
        }
    }

    public void shutdown() {
        this.vrParamsProvider.close();
    }

    /* access modifiers changed from: package-private */
    public boolean viewerNeedsTouchProcessing() {
        return this.enabled && this.markersInPixels != null && this.markersInPixels.length > 0;
    }

    public boolean wasLastMotionEventInViewer() {
        return this.lastMotionEventInHeadset;
    }
}
