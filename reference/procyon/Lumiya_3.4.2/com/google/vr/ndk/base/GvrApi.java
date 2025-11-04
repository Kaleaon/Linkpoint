// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import com.google.vr.cardboard.UsedByNative;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.protobuf.nano.MessageNano;
import com.google.common.logging.nano.Vr;
import android.util.Log;
import android.view.Display;
import android.graphics.RectF;
import android.graphics.Point;
import com.google.vr.cardboard.DisplayUtils;
import android.util.DisplayMetrics;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vr.cardboard.VrParamsProvider;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import com.google.vr.cardboard.DisplaySynchronizer;
import android.content.Context;

public class GvrApi
{
    private static final String TAG;
    private static PoseTracker sPoseTrackerForTesting;
    private final Context context;
    private final DisplaySynchronizer displaySynchronizer;
    private long nativeGvrContext;
    private ArrayList<WeakReference<SwapChain>> swapChainRefs;
    private UserPrefs userPrefs;
    private final VrParamsProvider vrParamsProvider;
    
    static {
        TAG = GvrApi.class.getSimpleName();
        try {
            System.loadLibrary("gvr");
        }
        catch (final UnsatisfiedLinkError unsatisfiedLinkError) {}
    }
    
    public GvrApi(final Context context, final DisplaySynchronizer displaySynchronizer) {
        this.context = context;
        this.displaySynchronizer = displaySynchronizer;
        long nativeDisplaySynchronizer;
        if (displaySynchronizer != null) {
            nativeDisplaySynchronizer = displaySynchronizer.getNativeDisplaySynchronizer();
        }
        else {
            nativeDisplaySynchronizer = 0L;
        }
        this.vrParamsProvider = VrParamsProviderFactory.create(context);
        this.swapChainRefs = new ArrayList<WeakReference<SwapChain>>();
        final DisplayMetrics computeCurrentDisplayMetrics = this.computeCurrentDisplayMetrics();
        this.nativeGvrContext = this.nativeCreate(this.getClass().getClassLoader(), context.getApplicationContext(), nativeDisplaySynchronizer, computeCurrentDisplayMetrics.widthPixels, computeCurrentDisplayMetrics.heightPixels, computeCurrentDisplayMetrics.xdpi, computeCurrentDisplayMetrics.ydpi, GvrApi.sPoseTrackerForTesting);
        if (this.nativeGvrContext == 0L) {
            throw new IllegalStateException("Native GVR context creation failed.");
        }
    }
    
    static DisplaySynchronizer createDefaultDisplaySynchronizer(final Context context) {
        return new DisplaySynchronizer(context, DisplayUtils.getDefaultDisplay(context));
    }
    
    public static String getErrorString(final int n) {
        return nativeGetErrorString(n);
    }
    
    static native long nativeBufferSpecCreate(final long p0);
    
    static native void nativeBufferSpecDestroy(final long p0);
    
    static native int nativeBufferSpecGetSamples(final long p0);
    
    static native void nativeBufferSpecGetSize(final long p0, final Point p1);
    
    static native void nativeBufferSpecSetColorFormat(final long p0, final int p1);
    
    static native void nativeBufferSpecSetDepthStencilFormat(final long p0, final int p1);
    
    static native void nativeBufferSpecSetSamples(final long p0, final int p1);
    
    static native void nativeBufferSpecSetSize(final long p0, final int p1, final int p2);
    
    static native long nativeBufferViewportCreate(final long p0);
    
    static native void nativeBufferViewportDestroy(final long p0);
    
    static native boolean nativeBufferViewportEqual(final long p0, final long p1);
    
    static native int nativeBufferViewportGetExternalSurfaceId(final long p0);
    
    static native int nativeBufferViewportGetReprojection(final long p0);
    
    static native int nativeBufferViewportGetSourceBufferIndex(final long p0);
    
    static native void nativeBufferViewportGetSourceFov(final long p0, final RectF p1);
    
    static native void nativeBufferViewportGetSourceUv(final long p0, final RectF p1);
    
    static native int nativeBufferViewportGetTargetEye(final long p0);
    
    static native void nativeBufferViewportGetTransform(final long p0, final float[] p1);
    
    private native long nativeBufferViewportListCreate(final long p0);
    
    static native void nativeBufferViewportListDestroy(final long p0);
    
    static native void nativeBufferViewportListGetItem(final long p0, final int p1, final long p2);
    
    static native int nativeBufferViewportListGetSize(final long p0);
    
    static native void nativeBufferViewportListSetItem(final long p0, final int p1, final long p2);
    
    static native void nativeBufferViewportSetExternalSurfaceId(final long p0, final int p1);
    
    static native void nativeBufferViewportSetReprojection(final long p0, final int p1);
    
    static native void nativeBufferViewportSetSourceBufferIndex(final long p0, final int p1);
    
    static native void nativeBufferViewportSetSourceFov(final long p0, final float p1, final float p2, final float p3, final float p4);
    
    static native void nativeBufferViewportSetSourceUv(final long p0, final float p1, final float p2, final float p3, final float p4);
    
    static native void nativeBufferViewportSetTargetEye(final long p0, final int p1);
    
    static native void nativeBufferViewportSetTransform(final long p0, final float[] p1);
    
    private native int nativeClearError(final long p0);
    
    private native float[] nativeComputeDistortedPoint(final long p0, final int p1, final float[] p2);
    
    private native long nativeCreate(final ClassLoader p0, final Context p1, final long p2, final int p3, final int p4, final float p5, final float p6, final PoseTracker p7);
    
    private native void nativeDistortToScreen(final long p0, final int p1, final long p2, final float[] p3, final long p4);
    
    private native void nativeDumpDebugData(final long p0);
    
    static native void nativeFrameBindBuffer(final long p0, final int p1);
    
    static native void nativeFrameGetBufferSize(final long p0, final int p1, final Point p2);
    
    static native int nativeFrameGetFramebufferObject(final long p0, final int p1);
    
    static native void nativeFrameSubmit(final long p0, final long p1, final float[] p2);
    
    static native void nativeFrameUnbind(final long p0);
    
    private native boolean nativeGetAsyncReprojectionEnabled(final long p0);
    
    private native float nativeGetBorderSizeMeters(final long p0);
    
    private native int nativeGetError(final long p0);
    
    private static native String nativeGetErrorString(final int p0);
    
    private native void nativeGetEyeFromHeadMatrix(final long p0, final int p1, final float[] p2);
    
    private native void nativeGetHeadSpaceFromStartSpaceRotation(final long p0, final float[] p1, final long p2);
    
    private native void nativeGetMaximumEffectiveRenderTargetSize(final long p0, final Point p1);
    
    private native void nativeGetRecommendedBufferViewports(final long p0, final long p1);
    
    private native void nativeGetScreenBufferViewports(final long p0, final long p1);
    
    private native void nativeGetScreenTargetSize(final long p0, final Point p1);
    
    private native long nativeGetUserPrefs(final long p0);
    
    private native String nativeGetViewerModel(final long p0);
    
    private native int nativeGetViewerType(final long p0);
    
    private native String nativeGetViewerVendor(final long p0);
    
    private native int[] nativeGetWindowBounds(final long p0);
    
    private native void nativeInitializeGl(final long p0);
    
    private native void nativeOnPauseReprojectionThread(final long p0);
    
    private native void nativeOnSurfaceCreatedReprojectionThread(final long p0);
    
    private native void nativePause(final long p0);
    
    private native byte[] nativePauseTracking(final long p0);
    
    private native void nativeRecenterTracking(final long p0);
    
    private native void nativeReconnectSensors(final long p0);
    
    private native void nativeReleaseGvrContext(final long p0);
    
    private native void nativeRemoveAllSurfacesReprojectionThread(final long p0);
    
    private native Point nativeRenderReprojectionThread(final long p0);
    
    private native void nativeResetTracking(final long p0);
    
    private native void nativeResume(final long p0);
    
    private native void nativeResumeTracking(final long p0, final byte[] p1);
    
    private native boolean nativeSetAsyncReprojectionEnabled(final long p0, final boolean p1);
    
    private native void nativeSetDefaultFramebufferActive(final long p0);
    
    private native boolean nativeSetDefaultViewerProfile(final long p0, final String p1);
    
    private native void nativeSetDisplayMetrics(final long p0, final int p1, final int p2, final float p3, final float p4);
    
    private native void nativeSetIgnoreManualPauseResumeTracker(final long p0, final boolean p1);
    
    private native void nativeSetLensOffset(final long p0, final float p1, final float p2);
    
    private native void nativeSetSurfaceSize(final long p0, final int p1, final int p2);
    
    private native boolean nativeSetViewerParams(final long p0, final byte[] p1);
    
    static native long nativeSwapChainAcquireFrame(final long p0);
    
    static native long nativeSwapChainCreate(final long p0, final long[] p1);
    
    static native void nativeSwapChainDestroy(final long p0);
    
    static native int nativeSwapChainGetBufferCount(final long p0);
    
    static native void nativeSwapChainGetBufferSize(final long p0, final int p1, final Point p2);
    
    static native void nativeSwapChainResizeBuffer(final long p0, final int p1, final int p2, final int p3);
    
    private native void nativeUpdateSurfaceReprojectionThread(final long p0, final int p1, final int p2, final long p3, final float[] p4);
    
    static native int nativeUserPrefsGetControllerHandedness(final long p0);
    
    private native boolean nativeUsingVrDisplayService(final long p0);
    
    public static void setPoseTrackerForTesting(final PoseTracker sPoseTrackerForTesting) {
        GvrApi.sPoseTrackerForTesting = sPoseTrackerForTesting;
    }
    
    private boolean setViewerParams(final byte[] array) {
        return this.nativeSetViewerParams(this.nativeGvrContext, array);
    }
    
    public int clearError() {
        return this.nativeClearError(this.nativeGvrContext);
    }
    
    public DisplayMetrics computeCurrentDisplayMetrics() {
        Display display;
        if (this.displaySynchronizer != null) {
            display = this.displaySynchronizer.getDisplay();
        }
        else {
            display = DisplayUtils.getDefaultDisplay(this.context);
        }
        return DisplayUtils.getDisplayMetricsLandscapeWithOverride(display, this.vrParamsProvider.readPhoneParams());
    }
    
    public float[] computeDistortedPoint(final int n, float[] nativeComputeDistortedPoint) {
        nativeComputeDistortedPoint = this.nativeComputeDistortedPoint(this.nativeGvrContext, n, nativeComputeDistortedPoint);
        if (nativeComputeDistortedPoint.length == 6) {
            return nativeComputeDistortedPoint;
        }
        throw new AssertionError((Object)"Implementation error: invalid UV coordinates output.");
    }
    
    public BufferSpec createBufferSpec() {
        return new BufferSpec(nativeBufferSpecCreate(this.nativeGvrContext));
    }
    
    public BufferViewport createBufferViewport() {
        return new BufferViewport(nativeBufferViewportCreate(this.nativeGvrContext));
    }
    
    public BufferViewportList createBufferViewportList() {
        return new BufferViewportList(this.nativeBufferViewportListCreate(this.nativeGvrContext));
    }
    
    public SwapChain createSwapChain(final BufferSpec[] array) {
        final long[] array2 = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].nativeBufferSpec;
        }
        final SwapChain referent = new SwapChain(nativeSwapChainCreate(this.nativeGvrContext, array2));
        this.swapChainRefs.add(new WeakReference<SwapChain>(referent));
        return referent;
    }
    
    public void distortToScreen(final int n, final BufferViewportList list, final float[] array, final long n2) {
        if (array != null) {
            this.nativeDistortToScreen(this.nativeGvrContext, n, list.nativeBufferViewportList, array, n2);
            return;
        }
        throw new IllegalArgumentException("Head pose must not be null.");
    }
    
    void dumpDebugData() {
        this.nativeDumpDebugData(this.nativeGvrContext);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.nativeGvrContext != 0L) {
                Log.w(GvrApi.TAG, "GvrApi.shutdown() should be called to ensure resource cleanup");
                this.shutdown();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public boolean getAsyncReprojectionEnabled() {
        return this.nativeGetAsyncReprojectionEnabled(this.nativeGvrContext);
    }
    
    public float getBorderSizeMeters() {
        return this.nativeGetBorderSizeMeters(this.nativeGvrContext);
    }
    
    public int getError() {
        return this.nativeGetError(this.nativeGvrContext);
    }
    
    public void getEyeFromHeadMatrix(final int n, final float[] array) {
        this.nativeGetEyeFromHeadMatrix(this.nativeGvrContext, n, array);
    }
    
    @Deprecated
    public float[] getEyeFromHeadMatrix(final int n) {
        final float[] array = new float[16];
        this.getEyeFromHeadMatrix(n, array);
        return array;
    }
    
    public void getHeadSpaceFromStartSpaceRotation(final float[] array, final long n) {
        if (array != null && array.length == 16) {
            this.nativeGetHeadSpaceFromStartSpaceRotation(this.nativeGvrContext, array, n);
            return;
        }
        throw new IllegalArgumentException("Invalid head rotation argument, must be a float[16].");
    }
    
    public void getMaximumEffectiveRenderTargetSize(final Point point) {
        this.nativeGetMaximumEffectiveRenderTargetSize(this.nativeGvrContext, point);
    }
    
    public long getNativeGvrContext() {
        return this.nativeGvrContext;
    }
    
    public void getRecommendedBufferViewports(final BufferViewportList list) {
        this.nativeGetRecommendedBufferViewports(this.nativeGvrContext, list.nativeBufferViewportList);
    }
    
    public void getScreenBufferViewports(final BufferViewportList list) {
        this.nativeGetScreenBufferViewports(this.nativeGvrContext, list.nativeBufferViewportList);
    }
    
    public void getScreenTargetSize(final Point point) {
        this.nativeGetScreenTargetSize(this.nativeGvrContext, point);
    }
    
    Vr.VREvent.SdkConfigurationParams getSdkConfigurationParams() {
        return SdkConfigurationReader.getParams(this.context);
    }
    
    public UserPrefs getUserPrefs() {
        if (this.userPrefs == null) {
            this.userPrefs = new UserPrefs(this.nativeGetUserPrefs(this.nativeGvrContext));
        }
        return this.userPrefs;
    }
    
    public String getViewerModel() {
        return this.nativeGetViewerModel(this.nativeGvrContext);
    }
    
    public int getViewerType() {
        return this.nativeGetViewerType(this.nativeGvrContext);
    }
    
    public String getViewerVendor() {
        return this.nativeGetViewerVendor(this.nativeGvrContext);
    }
    
    public int[] getWindowBounds() {
        final int[] nativeGetWindowBounds = this.nativeGetWindowBounds(this.nativeGvrContext);
        if (nativeGetWindowBounds.length == 4) {
            return nativeGetWindowBounds;
        }
        throw new AssertionError((Object)"Implementation error: invalid window bounds.");
    }
    
    public void initializeGl() {
        this.nativeInitializeGl(this.nativeGvrContext);
    }
    
    public void onPauseReprojectionThread() {
        this.nativeOnPauseReprojectionThread(this.nativeGvrContext);
    }
    
    public void onSurfaceCreatedReprojectionThread() {
        this.nativeOnSurfaceCreatedReprojectionThread(this.nativeGvrContext);
    }
    
    void pause() {
        this.nativePause(this.nativeGvrContext);
    }
    
    public void pauseTracking() {
        this.nativePauseTracking(this.nativeGvrContext);
    }
    
    public byte[] pauseTrackingGetState() {
        return this.nativePauseTracking(this.nativeGvrContext);
    }
    
    public void recenterTracking() {
        this.nativeRecenterTracking(this.nativeGvrContext);
    }
    
    public void reconnectSensors() {
        this.nativeReconnectSensors(this.nativeGvrContext);
    }
    
    public void refreshDisplayMetrics() {
        this.setDisplayMetrics(this.computeCurrentDisplayMetrics());
    }
    
    public void refreshViewerProfile() {
        final CardboardDevice.DeviceParams deviceParams = this.vrParamsProvider.readDeviceParams();
        if (deviceParams != null) {
            this.setViewerParams(MessageNano.toByteArray(deviceParams));
        }
    }
    
    public void removeAllSurfacesReprojectionThread() {
        this.nativeRemoveAllSurfacesReprojectionThread(this.nativeGvrContext);
    }
    
    public Point renderReprojectionThread() {
        return this.nativeRenderReprojectionThread(this.nativeGvrContext);
    }
    
    public void resetTracking() {
        this.nativeResetTracking(this.nativeGvrContext);
    }
    
    void resume() {
        this.nativeResume(this.nativeGvrContext);
    }
    
    public void resumeTracking() {
        this.nativeResumeTracking(this.nativeGvrContext, null);
    }
    
    public void resumeTrackingSetState(final byte[] array) {
        if (array != null) {
            this.nativeResumeTracking(this.nativeGvrContext, array);
            return;
        }
        this.nativeResumeTracking(this.nativeGvrContext, null);
    }
    
    boolean setAsyncReprojectionEnabled(final boolean b) {
        return this.nativeSetAsyncReprojectionEnabled(this.nativeGvrContext, b);
    }
    
    public void setDefaultFramebufferActive() {
        this.nativeSetDefaultFramebufferActive(this.nativeGvrContext);
    }
    
    public boolean setDefaultViewerProfile(final String s) {
        return this.nativeSetDefaultViewerProfile(this.nativeGvrContext, s);
    }
    
    public void setDisplayMetrics(final DisplayMetrics displayMetrics) {
        this.nativeSetDisplayMetrics(this.nativeGvrContext, displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.xdpi, displayMetrics.ydpi);
    }
    
    void setIgnoreManualTrackerPauseResume(final boolean b) {
        this.nativeSetIgnoreManualPauseResumeTracker(this.nativeGvrContext, b);
    }
    
    public void setLensOffset(final float n, final float n2) {
        this.nativeSetLensOffset(this.nativeGvrContext, n, n2);
    }
    
    public void setSurfaceSize(final int n, final int n2) {
        int n3 = 0;
        int n4;
        if (n != 0) {
            n4 = 0;
        }
        else {
            n4 = 1;
        }
        if (n2 == 0) {
            n3 = 1;
        }
        if (n4 == n3) {
            this.nativeSetSurfaceSize(this.nativeGvrContext, n, n2);
            return;
        }
        throw new IllegalArgumentException("Custom surface dimensions should both either be zero or non-zero");
    }
    
    public void shutdown() {
        final ArrayList<WeakReference<SwapChain>> list = this.swapChainRefs;
        int n;
        for (int size = list.size(), i = 0; i < size; i = n) {
            final Object value = list.get(i);
            n = i + 1;
            final SwapChain swapChain = (SwapChain)((WeakReference)value).get();
            i = n;
            if (swapChain != null) {
                swapChain.shutdown();
            }
        }
        if (this.nativeGvrContext != 0L) {
            this.vrParamsProvider.close();
            this.nativeReleaseGvrContext(this.nativeGvrContext);
            this.nativeGvrContext = 0L;
        }
    }
    
    public void updateSurfaceReprojectionThread(final int n, final int n2, final long n3, final float[] array) {
        this.nativeUpdateSurfaceReprojectionThread(this.nativeGvrContext, n, n2, n3, array);
    }
    
    boolean usingVrDisplayService() {
        return this.nativeUsingVrDisplayService(this.nativeGvrContext);
    }
    
    public abstract static class Error
    {
        public static final int CONTROLLER_CREATE_FAILED = 2;
        public static final int NONE = 0;
        public static final int NO_FRAME_AVAILABLE = 3;
    }
    
    @UsedByNative
    public interface PoseTracker
    {
        @UsedByNative
        void getHeadPoseInStartSpace(final float[] p0, final long p1);
    }
    
    public abstract static class ViewerType
    {
        public static final int CARDBOARD = 0;
        public static final int DAYDREAM = 1;
        static final int DEFAULT = 0;
    }
}
