// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.view.Display;
import android.os.AsyncTask;
import android.view.View;
import android.view.View$OnTouchListener;
import android.view.MotionEvent;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vr.cardboard.DisplayUtils;
import com.google.vr.cardboard.VrParamsProviderFactory;
import android.content.Context;
import com.google.vr.cardboard.VrParamsProvider;
import android.util.DisplayMetrics;

class DaydreamAlignment
{
    private static final double MAX_TOUCH_DISTANCE_SQUARED_METERS = 2.25E-4;
    private static final String TAG = "DaydreamAlignment";
    private float borderSizeMeters;
    private double[] currentMarkerBestDists;
    private DisplayMetrics displayMetrics;
    private boolean enabled;
    private final boolean isDaydreamImageAlignmentEnabled;
    private boolean lastMotionEventInHeadset;
    private int[] markerBestTouch;
    private float[][] markersInPixels;
    private int mostTouchesSeen;
    private float[] pixelTranslation;
    private int[] touchBestMarker;
    private final VrParamsProvider vrParamsProvider;
    private float xMetersPerPixel;
    private float yMetersPerPixel;
    
    public DaydreamAlignment(final Context context, final GvrApi gvrApi) {
        boolean isDaydreamImageAlignmentEnabled = true;
        this.pixelTranslation = new float[2];
        this.enabled = true;
        if (gvrApi.getSdkConfigurationParams().daydreamImageAlignment == 1) {
            isDaydreamImageAlignmentEnabled = false;
        }
        this.isDaydreamImageAlignmentEnabled = isDaydreamImageAlignmentEnabled;
        this.vrParamsProvider = VrParamsProviderFactory.create(context);
        final FinishInitilizationTask finishInitilizationTask = new FinishInitilizationTask();
        finishInitilizationTask.display = DisplayUtils.getDefaultDisplay(context);
        finishInitilizationTask.execute((Object[])new Void[0]);
    }
    
    DaydreamAlignment(final VrParamsProvider vrParamsProvider, final DisplayMetrics displayMetrics, final Phone.PhoneParams phoneParams, final boolean isDaydreamImageAlignmentEnabled) {
        this.pixelTranslation = new float[2];
        this.enabled = true;
        this.isDaydreamImageAlignmentEnabled = isDaydreamImageAlignmentEnabled;
        this.vrParamsProvider = vrParamsProvider;
        this.init(displayMetrics, phoneParams);
    }
    
    private void init(final DisplayMetrics displayMetrics, final Phone.PhoneParams phoneParams) {
        this.displayMetrics = displayMetrics;
        this.borderSizeMeters = DisplayUtils.getBorderSizeMeters(phoneParams);
        this.xMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(this.displayMetrics.xdpi);
        this.yMetersPerPixel = DisplayUtils.getMetersPerPixelFromDotsPerInch(this.displayMetrics.ydpi);
        this.resetTrackingState();
        this.refreshViewerProfile();
    }
    
    private void resetTrackingState() {
        this.lastMotionEventInHeadset = false;
        this.pixelTranslation[0] = 0.0f;
        this.pixelTranslation[1] = 0.0f;
        this.mostTouchesSeen = 0;
    }
    
    void getTranslationInPixels(final float[] array) {
        if (array.length >= 2) {
            array[0] = this.pixelTranslation[0];
            array[1] = this.pixelTranslation[1];
            return;
        }
        throw new IllegalArgumentException("Translation array too small");
    }
    
    public void getTranslationInScreenSpace(final float[] array) {
        if (array.length >= 2) {
            array[0] = this.pixelTranslation[0] / this.displayMetrics.widthPixels;
            array[1] = this.pixelTranslation[1] / this.displayMetrics.heightPixels;
            array[0] *= 4.0f;
            array[1] *= -2.0f;
            return;
        }
        throw new IllegalArgumentException("Translation array too small");
    }
    
    boolean isDaydreamImageAlignmentEnabled() {
        return this.isDaydreamImageAlignmentEnabled;
    }
    
    public boolean processMotionEvent(final MotionEvent motionEvent) {
        if (!this.viewerNeedsTouchProcessing()) {
            return this.lastMotionEventInHeadset = false;
        }
        if (this.isDaydreamImageAlignmentEnabled()) {
            final int pointerCount = motionEvent.getPointerCount();
            if (pointerCount > this.mostTouchesSeen) {
                this.touchBestMarker = new int[pointerCount];
                this.mostTouchesSeen = pointerCount;
            }
            for (int i = 0; i < this.markersInPixels.length; ++i) {
                this.markerBestTouch[i] = -1;
                this.currentMarkerBestDists[i] = 2.25E-4;
            }
            for (int j = 0; j < pointerCount; ++j) {
                double n = 2.25E-4;
                this.touchBestMarker[j] = -1;
                double n5;
                for (int k = 0; k < this.markersInPixels.length; ++k, n = n5) {
                    final float n2 = (this.markersInPixels[k][0] - motionEvent.getX(j)) * this.xMetersPerPixel;
                    final float n3 = (this.markersInPixels[k][1] - motionEvent.getY(j)) * this.yMetersPerPixel;
                    final double n4 = n2 * n2 + n3 * n3;
                    n5 = n;
                    if (n4 < n) {
                        this.touchBestMarker[j] = k;
                        n5 = n4;
                    }
                    if (n4 < this.currentMarkerBestDists[k]) {
                        this.currentMarkerBestDists[k] = n4;
                        this.markerBestTouch[k] = j;
                    }
                }
            }
            float n6 = 0.0f;
            float n7 = 0.0f;
            int n8 = 0;
            for (int l = 0; l < this.markerBestTouch.length; ++l) {
                if (this.markerBestTouch[l] != -1) {
                    if (this.touchBestMarker[this.markerBestTouch[l]] == l) {
                        ++n8;
                        n6 += motionEvent.getX(this.markerBestTouch[l]) - this.markersInPixels[l][0];
                        n7 += motionEvent.getY(this.markerBestTouch[l]) - this.markersInPixels[l][1];
                    }
                    else {
                        this.markerBestTouch[l] = -1;
                    }
                }
            }
            if (n8 <= 0) {
                this.lastMotionEventInHeadset = false;
            }
            else {
                this.lastMotionEventInHeadset = true;
                this.pixelTranslation[0] = n6 / n8;
                this.pixelTranslation[1] = n7 / n8;
            }
            return true;
        }
        return true;
    }
    
    public void refreshViewerProfile() {
        new RefreshViewerProfileTask().execute((Object[])new Void[0]);
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            this.resetTrackingState();
        }
    }
    
    public void shutdown() {
        this.vrParamsProvider.close();
    }
    
    boolean viewerNeedsTouchProcessing() {
        return this.enabled && this.markersInPixels != null && this.markersInPixels.length > 0;
    }
    
    public boolean wasLastMotionEventInViewer() {
        return this.lastMotionEventInHeadset;
    }
    
    public static class DefaultTouchListener implements View$OnTouchListener
    {
        private final DaydreamAlignment daydreamAlignment;
        private final GvrApi gvrApi;
        private float[] lastTranslation;
        private float[] translation;
        
        public DefaultTouchListener(final DaydreamAlignment daydreamAlignment, final GvrApi gvrApi) {
            this.lastTranslation = new float[2];
            this.translation = new float[2];
            this.daydreamAlignment = daydreamAlignment;
            this.gvrApi = gvrApi;
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (this.daydreamAlignment.processMotionEvent(motionEvent)) {
                if (this.daydreamAlignment.wasLastMotionEventInViewer()) {
                    this.daydreamAlignment.getTranslationInScreenSpace(this.translation);
                }
                else {
                    this.translation[0] = 0.0f;
                    this.translation[1] = 0.0f;
                }
                if (this.translation[0] != this.lastTranslation[0] || this.translation[1] != this.lastTranslation[1]) {
                    this.lastTranslation[0] = this.translation[0];
                    this.lastTranslation[1] = this.translation[1];
                    this.gvrApi.setLensOffset(this.translation[0], this.translation[1]);
                }
                return true;
            }
            return false;
        }
    }
    
    private class FinishInitilizationTask extends AsyncTask<Void, Void, Phone.PhoneParams>
    {
        public Display display;
        
        protected Phone.PhoneParams doInBackground(final Void... array) {
            return DaydreamAlignment.this.vrParamsProvider.readPhoneParams();
        }
        
        protected void onPostExecute(final Phone.PhoneParams phoneParams) {
            DaydreamAlignment.this.init(DisplayUtils.getDisplayMetricsLandscapeWithOverride(this.display, phoneParams), phoneParams);
        }
        
        protected void onProgressUpdate(final Void... array) {
        }
    }
    
    private class RefreshViewerProfileTask extends AsyncTask<Void, Void, CardboardDevice.DeviceParams>
    {
        protected CardboardDevice.DeviceParams doInBackground(final Void... array) {
            return DaydreamAlignment.this.vrParamsProvider.readDeviceParams();
        }
        
        protected void onPostExecute(final CardboardDevice.DeviceParams deviceParams) {
            if (deviceParams != null && deviceParams.daydreamInternal != null && deviceParams.daydreamInternal.alignmentMarkers != null) {
                final CardboardDevice.ScreenAlignmentMarker[] alignmentMarkers = deviceParams.daydreamInternal.alignmentMarkers;
                DaydreamAlignment.this.markersInPixels = new float[alignmentMarkers.length][];
                DaydreamAlignment.this.currentMarkerBestDists = new double[alignmentMarkers.length];
                DaydreamAlignment.this.markerBestTouch = new int[alignmentMarkers.length];
                for (int i = 0; i < alignmentMarkers.length; ++i) {
                    final CardboardDevice.ScreenAlignmentMarker screenAlignmentMarker = alignmentMarkers[i];
                    (DaydreamAlignment.this.markersInPixels[i] = new float[2])[0] = DaydreamAlignment.this.displayMetrics.widthPixels / 2 + screenAlignmentMarker.getHorizontal() / DaydreamAlignment.this.xMetersPerPixel;
                    DaydreamAlignment.this.markersInPixels[i][1] = DaydreamAlignment.this.displayMetrics.heightPixels - (screenAlignmentMarker.getVertical() + deviceParams.getTrayToLensDistance() - DaydreamAlignment.this.borderSizeMeters) / DaydreamAlignment.this.yMetersPerPixel;
                }
                return;
            }
            DaydreamAlignment.this.markersInPixels = null;
        }
        
        protected void onProgressUpdate(final Void... array) {
        }
    }
}
