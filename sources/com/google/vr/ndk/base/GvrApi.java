package com.google.vr.ndk.base;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import com.google.common.logging.nano.Vr;
import com.google.protobuf.nano.MessageNano;
import com.google.vr.cardboard.DisplaySynchronizer;
import com.google.vr.cardboard.DisplayUtils;
import com.google.vr.cardboard.UsedByNative;
import com.google.vr.cardboard.VrParamsProvider;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GvrApi {
    private static final String TAG = GvrApi.class.getSimpleName();
    private static PoseTracker sPoseTrackerForTesting;
    private final Context context;
    private final DisplaySynchronizer displaySynchronizer;
    private long nativeGvrContext;
    private ArrayList<WeakReference<SwapChain>> swapChainRefs;
    private UserPrefs userPrefs;
    private final VrParamsProvider vrParamsProvider;

    public static abstract class Error {
        public static final int CONTROLLER_CREATE_FAILED = 2;
        public static final int NONE = 0;
        public static final int NO_FRAME_AVAILABLE = 3;
    }

    @UsedByNative
    public interface PoseTracker {
        @UsedByNative
        void getHeadPoseInStartSpace(float[] fArr, long j);
    }

    public static abstract class ViewerType {
        public static final int CARDBOARD = 0;
        public static final int DAYDREAM = 1;
        static final int DEFAULT = 0;
    }

    static {
        try {
            System.loadLibrary("gvr");
        } catch (UnsatisfiedLinkError e) {
        }
    }

    public GvrApi(Context context2, DisplaySynchronizer displaySynchronizer2) {
        this.context = context2;
        this.displaySynchronizer = displaySynchronizer2;
        long nativeDisplaySynchronizer = displaySynchronizer2 != null ? displaySynchronizer2.getNativeDisplaySynchronizer() : 0;
        this.vrParamsProvider = VrParamsProviderFactory.create(context2);
        this.swapChainRefs = new ArrayList<>();
        DisplayMetrics computeCurrentDisplayMetrics = computeCurrentDisplayMetrics();
        this.nativeGvrContext = nativeCreate(getClass().getClassLoader(), context2.getApplicationContext(), nativeDisplaySynchronizer, computeCurrentDisplayMetrics.widthPixels, computeCurrentDisplayMetrics.heightPixels, computeCurrentDisplayMetrics.xdpi, computeCurrentDisplayMetrics.ydpi, sPoseTrackerForTesting);
        if (this.nativeGvrContext == 0) {
            throw new IllegalStateException("Native GVR context creation failed.");
        }
    }

    static DisplaySynchronizer createDefaultDisplaySynchronizer(Context context2) {
        return new DisplaySynchronizer(context2, DisplayUtils.getDefaultDisplay(context2));
    }

    public static String getErrorString(int i) {
        return nativeGetErrorString(i);
    }

    static native long nativeBufferSpecCreate(long j);

    static native void nativeBufferSpecDestroy(long j);

    static native int nativeBufferSpecGetSamples(long j);

    static native void nativeBufferSpecGetSize(long j, Point point);

    static native void nativeBufferSpecSetColorFormat(long j, int i);

    static native void nativeBufferSpecSetDepthStencilFormat(long j, int i);

    static native void nativeBufferSpecSetSamples(long j, int i);

    static native void nativeBufferSpecSetSize(long j, int i, int i2);

    static native long nativeBufferViewportCreate(long j);

    static native void nativeBufferViewportDestroy(long j);

    static native boolean nativeBufferViewportEqual(long j, long j2);

    static native int nativeBufferViewportGetExternalSurfaceId(long j);

    static native int nativeBufferViewportGetReprojection(long j);

    static native int nativeBufferViewportGetSourceBufferIndex(long j);

    static native void nativeBufferViewportGetSourceFov(long j, RectF rectF);

    static native void nativeBufferViewportGetSourceUv(long j, RectF rectF);

    static native int nativeBufferViewportGetTargetEye(long j);

    static native void nativeBufferViewportGetTransform(long j, float[] fArr);

    private native long nativeBufferViewportListCreate(long j);

    static native void nativeBufferViewportListDestroy(long j);

    static native void nativeBufferViewportListGetItem(long j, int i, long j2);

    static native int nativeBufferViewportListGetSize(long j);

    static native void nativeBufferViewportListSetItem(long j, int i, long j2);

    static native void nativeBufferViewportSetExternalSurfaceId(long j, int i);

    static native void nativeBufferViewportSetReprojection(long j, int i);

    static native void nativeBufferViewportSetSourceBufferIndex(long j, int i);

    static native void nativeBufferViewportSetSourceFov(long j, float f, float f2, float f3, float f4);

    static native void nativeBufferViewportSetSourceUv(long j, float f, float f2, float f3, float f4);

    static native void nativeBufferViewportSetTargetEye(long j, int i);

    static native void nativeBufferViewportSetTransform(long j, float[] fArr);

    private native int nativeClearError(long j);

    private native float[] nativeComputeDistortedPoint(long j, int i, float[] fArr);

    private native long nativeCreate(ClassLoader classLoader, Context context2, long j, int i, int i2, float f, float f2, PoseTracker poseTracker);

    private native void nativeDistortToScreen(long j, int i, long j2, float[] fArr, long j3);

    private native void nativeDumpDebugData(long j);

    static native void nativeFrameBindBuffer(long j, int i);

    static native void nativeFrameGetBufferSize(long j, int i, Point point);

    static native int nativeFrameGetFramebufferObject(long j, int i);

    static native void nativeFrameSubmit(long j, long j2, float[] fArr);

    static native void nativeFrameUnbind(long j);

    private native boolean nativeGetAsyncReprojectionEnabled(long j);

    private native float nativeGetBorderSizeMeters(long j);

    private native int nativeGetError(long j);

    private static native String nativeGetErrorString(int i);

    private native void nativeGetEyeFromHeadMatrix(long j, int i, float[] fArr);

    private native void nativeGetHeadSpaceFromStartSpaceRotation(long j, float[] fArr, long j2);

    private native void nativeGetMaximumEffectiveRenderTargetSize(long j, Point point);

    private native void nativeGetRecommendedBufferViewports(long j, long j2);

    private native void nativeGetScreenBufferViewports(long j, long j2);

    private native void nativeGetScreenTargetSize(long j, Point point);

    private native long nativeGetUserPrefs(long j);

    private native String nativeGetViewerModel(long j);

    private native int nativeGetViewerType(long j);

    private native String nativeGetViewerVendor(long j);

    private native int[] nativeGetWindowBounds(long j);

    private native void nativeInitializeGl(long j);

    private native void nativeOnPauseReprojectionThread(long j);

    private native void nativeOnSurfaceCreatedReprojectionThread(long j);

    private native void nativePause(long j);

    private native byte[] nativePauseTracking(long j);

    private native void nativeRecenterTracking(long j);

    private native void nativeReconnectSensors(long j);

    private native void nativeReleaseGvrContext(long j);

    private native void nativeRemoveAllSurfacesReprojectionThread(long j);

    private native Point nativeRenderReprojectionThread(long j);

    private native void nativeResetTracking(long j);

    private native void nativeResume(long j);

    private native void nativeResumeTracking(long j, byte[] bArr);

    private native boolean nativeSetAsyncReprojectionEnabled(long j, boolean z);

    private native void nativeSetDefaultFramebufferActive(long j);

    private native boolean nativeSetDefaultViewerProfile(long j, String str);

    private native void nativeSetDisplayMetrics(long j, int i, int i2, float f, float f2);

    private native void nativeSetIgnoreManualPauseResumeTracker(long j, boolean z);

    private native void nativeSetLensOffset(long j, float f, float f2);

    private native void nativeSetSurfaceSize(long j, int i, int i2);

    private native boolean nativeSetViewerParams(long j, byte[] bArr);

    static native long nativeSwapChainAcquireFrame(long j);

    static native long nativeSwapChainCreate(long j, long[] jArr);

    static native void nativeSwapChainDestroy(long j);

    static native int nativeSwapChainGetBufferCount(long j);

    static native void nativeSwapChainGetBufferSize(long j, int i, Point point);

    static native void nativeSwapChainResizeBuffer(long j, int i, int i2, int i3);

    private native void nativeUpdateSurfaceReprojectionThread(long j, int i, int i2, long j2, float[] fArr);

    static native int nativeUserPrefsGetControllerHandedness(long j);

    private native boolean nativeUsingVrDisplayService(long j);

    public static void setPoseTrackerForTesting(PoseTracker poseTracker) {
        sPoseTrackerForTesting = poseTracker;
    }

    private boolean setViewerParams(byte[] bArr) {
        return nativeSetViewerParams(this.nativeGvrContext, bArr);
    }

    public int clearError() {
        return nativeClearError(this.nativeGvrContext);
    }

    public DisplayMetrics computeCurrentDisplayMetrics() {
        return DisplayUtils.getDisplayMetricsLandscapeWithOverride(this.displaySynchronizer != null ? this.displaySynchronizer.getDisplay() : DisplayUtils.getDefaultDisplay(this.context), this.vrParamsProvider.readPhoneParams());
    }

    public float[] computeDistortedPoint(int i, float[] fArr) {
        float[] nativeComputeDistortedPoint = nativeComputeDistortedPoint(this.nativeGvrContext, i, fArr);
        if (nativeComputeDistortedPoint.length == 6) {
            return nativeComputeDistortedPoint;
        }
        throw new AssertionError("Implementation error: invalid UV coordinates output.");
    }

    public BufferSpec createBufferSpec() {
        return new BufferSpec(nativeBufferSpecCreate(this.nativeGvrContext));
    }

    public BufferViewport createBufferViewport() {
        return new BufferViewport(nativeBufferViewportCreate(this.nativeGvrContext));
    }

    public BufferViewportList createBufferViewportList() {
        return new BufferViewportList(nativeBufferViewportListCreate(this.nativeGvrContext));
    }

    public SwapChain createSwapChain(BufferSpec[] bufferSpecArr) {
        long[] jArr = new long[bufferSpecArr.length];
        for (int i = 0; i < bufferSpecArr.length; i++) {
            jArr[i] = bufferSpecArr[i].nativeBufferSpec;
        }
        SwapChain swapChain = new SwapChain(nativeSwapChainCreate(this.nativeGvrContext, jArr));
        this.swapChainRefs.add(new WeakReference(swapChain));
        return swapChain;
    }

    public void distortToScreen(int i, BufferViewportList bufferViewportList, float[] fArr, long j) {
        if (fArr != null) {
            nativeDistortToScreen(this.nativeGvrContext, i, bufferViewportList.nativeBufferViewportList, fArr, j);
            return;
        }
        throw new IllegalArgumentException("Head pose must not be null.");
    }

    /* access modifiers changed from: package-private */
    public void dumpDebugData() {
        nativeDumpDebugData(this.nativeGvrContext);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.nativeGvrContext != 0) {
                Log.w(TAG, "GvrApi.shutdown() should be called to ensure resource cleanup");
                shutdown();
            }
        } finally {
            super.finalize();
        }
    }

    public boolean getAsyncReprojectionEnabled() {
        return nativeGetAsyncReprojectionEnabled(this.nativeGvrContext);
    }

    public float getBorderSizeMeters() {
        return nativeGetBorderSizeMeters(this.nativeGvrContext);
    }

    public int getError() {
        return nativeGetError(this.nativeGvrContext);
    }

    public void getEyeFromHeadMatrix(int i, float[] fArr) {
        nativeGetEyeFromHeadMatrix(this.nativeGvrContext, i, fArr);
    }

    @Deprecated
    public float[] getEyeFromHeadMatrix(int i) {
        float[] fArr = new float[16];
        getEyeFromHeadMatrix(i, fArr);
        return fArr;
    }

    public void getHeadSpaceFromStartSpaceRotation(float[] fArr, long j) {
        if (fArr != null && fArr.length == 16) {
            nativeGetHeadSpaceFromStartSpaceRotation(this.nativeGvrContext, fArr, j);
            return;
        }
        throw new IllegalArgumentException("Invalid head rotation argument, must be a float[16].");
    }

    public void getMaximumEffectiveRenderTargetSize(Point point) {
        nativeGetMaximumEffectiveRenderTargetSize(this.nativeGvrContext, point);
    }

    public long getNativeGvrContext() {
        return this.nativeGvrContext;
    }

    public void getRecommendedBufferViewports(BufferViewportList bufferViewportList) {
        nativeGetRecommendedBufferViewports(this.nativeGvrContext, bufferViewportList.nativeBufferViewportList);
    }

    public void getScreenBufferViewports(BufferViewportList bufferViewportList) {
        nativeGetScreenBufferViewports(this.nativeGvrContext, bufferViewportList.nativeBufferViewportList);
    }

    public void getScreenTargetSize(Point point) {
        nativeGetScreenTargetSize(this.nativeGvrContext, point);
    }

    /* access modifiers changed from: package-private */
    public Vr.VREvent.SdkConfigurationParams getSdkConfigurationParams() {
        return SdkConfigurationReader.getParams(this.context);
    }

    public UserPrefs getUserPrefs() {
        if (this.userPrefs == null) {
            this.userPrefs = new UserPrefs(nativeGetUserPrefs(this.nativeGvrContext));
        }
        return this.userPrefs;
    }

    public String getViewerModel() {
        return nativeGetViewerModel(this.nativeGvrContext);
    }

    public int getViewerType() {
        return nativeGetViewerType(this.nativeGvrContext);
    }

    public String getViewerVendor() {
        return nativeGetViewerVendor(this.nativeGvrContext);
    }

    public int[] getWindowBounds() {
        int[] nativeGetWindowBounds = nativeGetWindowBounds(this.nativeGvrContext);
        if (nativeGetWindowBounds.length == 4) {
            return nativeGetWindowBounds;
        }
        throw new AssertionError("Implementation error: invalid window bounds.");
    }

    public void initializeGl() {
        nativeInitializeGl(this.nativeGvrContext);
    }

    public void onPauseReprojectionThread() {
        nativeOnPauseReprojectionThread(this.nativeGvrContext);
    }

    public void onSurfaceCreatedReprojectionThread() {
        nativeOnSurfaceCreatedReprojectionThread(this.nativeGvrContext);
    }

    /* access modifiers changed from: package-private */
    public void pause() {
        nativePause(this.nativeGvrContext);
    }

    public void pauseTracking() {
        nativePauseTracking(this.nativeGvrContext);
    }

    public byte[] pauseTrackingGetState() {
        return nativePauseTracking(this.nativeGvrContext);
    }

    public void recenterTracking() {
        nativeRecenterTracking(this.nativeGvrContext);
    }

    public void reconnectSensors() {
        nativeReconnectSensors(this.nativeGvrContext);
    }

    public void refreshDisplayMetrics() {
        setDisplayMetrics(computeCurrentDisplayMetrics());
    }

    public void refreshViewerProfile() {
        CardboardDevice.DeviceParams readDeviceParams = this.vrParamsProvider.readDeviceParams();
        if (readDeviceParams != null) {
            setViewerParams(MessageNano.toByteArray(readDeviceParams));
        }
    }

    public void removeAllSurfacesReprojectionThread() {
        nativeRemoveAllSurfacesReprojectionThread(this.nativeGvrContext);
    }

    public Point renderReprojectionThread() {
        return nativeRenderReprojectionThread(this.nativeGvrContext);
    }

    public void resetTracking() {
        nativeResetTracking(this.nativeGvrContext);
    }

    /* access modifiers changed from: package-private */
    public void resume() {
        nativeResume(this.nativeGvrContext);
    }

    public void resumeTracking() {
        nativeResumeTracking(this.nativeGvrContext, (byte[]) null);
    }

    public void resumeTrackingSetState(byte[] bArr) {
        if (bArr != null) {
            nativeResumeTracking(this.nativeGvrContext, bArr);
        } else {
            nativeResumeTracking(this.nativeGvrContext, (byte[]) null);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean setAsyncReprojectionEnabled(boolean z) {
        return nativeSetAsyncReprojectionEnabled(this.nativeGvrContext, z);
    }

    public void setDefaultFramebufferActive() {
        nativeSetDefaultFramebufferActive(this.nativeGvrContext);
    }

    public boolean setDefaultViewerProfile(String str) {
        return nativeSetDefaultViewerProfile(this.nativeGvrContext, str);
    }

    public void setDisplayMetrics(DisplayMetrics displayMetrics) {
        nativeSetDisplayMetrics(this.nativeGvrContext, displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.xdpi, displayMetrics.ydpi);
    }

    /* access modifiers changed from: package-private */
    public void setIgnoreManualTrackerPauseResume(boolean z) {
        nativeSetIgnoreManualPauseResumeTracker(this.nativeGvrContext, z);
    }

    public void setLensOffset(float f, float f2) {
        nativeSetLensOffset(this.nativeGvrContext, f, f2);
    }

    public void setSurfaceSize(int i, int i2) {
        boolean z = false;
        boolean z2 = i == 0;
        if (i2 == 0) {
            z = true;
        }
        if (z2 == z) {
            nativeSetSurfaceSize(this.nativeGvrContext, i, i2);
            return;
        }
        throw new IllegalArgumentException("Custom surface dimensions should both either be zero or non-zero");
    }

    public void shutdown() {
        ArrayList arrayList = this.swapChainRefs;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            SwapChain swapChain = (SwapChain) ((WeakReference) obj).get();
            if (swapChain != null) {
                swapChain.shutdown();
            }
        }
        if (this.nativeGvrContext != 0) {
            this.vrParamsProvider.close();
            nativeReleaseGvrContext(this.nativeGvrContext);
            this.nativeGvrContext = 0;
        }
    }

    public void updateSurfaceReprojectionThread(int i, int i2, long j, float[] fArr) {
        nativeUpdateSurfaceReprojectionThread(this.nativeGvrContext, i, i2, j, fArr);
    }

    /* access modifiers changed from: package-private */
    public boolean usingVrDisplayService() {
        return nativeUsingVrDisplayService(this.nativeGvrContext);
    }
}
