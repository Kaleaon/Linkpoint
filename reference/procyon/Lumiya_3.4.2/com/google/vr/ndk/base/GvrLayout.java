// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import java.util.Iterator;
import android.view.WindowManager$InvalidDisplayException;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import android.widget.RelativeLayout$LayoutParams;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager$DisplayListener;
import android.app.Presentation;
import android.view.Display;
import android.os.Looper;
import android.view.MotionEvent;
import android.content.res.Configuration;
import android.app.PendingIntent;
import android.view.Surface;
import android.os.Handler;
import com.google.vr.cardboard.ContextUtils;
import com.google.vr.cardboard.DisplayUtils;
import android.os.Build$VERSION;
import android.opengl.GLSurfaceView$Renderer;
import android.util.Log;
import android.opengl.GLSurfaceView$EGLWindowSurfaceFactory;
import android.opengl.GLSurfaceView$EGLContextFactory;
import android.opengl.GLSurfaceView$EGLConfigChooser;
import com.google.vr.cardboard.MutableEglConfigChooser;
import android.util.AttributeSet;
import android.content.Context;
import com.google.vr.cardboard.ScanlineRacingRenderer;
import android.view.View;
import com.google.vr.cardboard.EglFactory;
import com.google.vr.cardboard.DisplaySynchronizer;
import android.widget.FrameLayout;

public class GvrLayout extends FrameLayout
{
    private static final boolean DEBUG = false;
    private static final int EXTERNAL_PRESENTATION_MIN_API = 16;
    private static final int SHOW_RENDERING_VIEWS_DELAY_FOR_FADE = 50;
    private static final String TAG = "GvrLayout";
    private static PresentationFactory sOptionalPresentationFactory;
    private CardboardEmulator cardboardEmulator;
    private DaydreamAlignment daydreamAlignment;
    private DaydreamUtilsWrapper daydreamUtils;
    private DisplaySynchronizer displaySynchronizer;
    private EglFactory eglFactory;
    private FadeOverlayView fadeOverlayView;
    private GvrApi gvrApi;
    private boolean isAsyncReprojectionUsingProtectedBuffers;
    private boolean isAsyncReprojectionVideoEnabled;
    private boolean isResumed;
    private PresentationHelper presentationHelper;
    private FrameLayout presentationLayout;
    private View presentationView;
    private ScanlineRacingRenderer scanlineRacingRenderer;
    private GvrSurfaceView scanlineRacingView;
    private final Runnable showRenderingViewsRunnable;
    private boolean stereoModeEnabled;
    private GvrUiLayout uiLayout;
    private int videoSurfaceId;
    private VrCoreSdkClient vrCoreSdkClient;
    
    static {
        GvrLayout.sOptionalPresentationFactory = null;
    }
    
    public GvrLayout(final Context context) {
        super(context);
        this.isResumed = false;
        this.videoSurfaceId = -1;
        this.stereoModeEnabled = true;
        this.showRenderingViewsRunnable = new Runnable() {
            @Override
            public void run() {
                GvrLayout.this.updateRenderingViewsVisibility(0);
            }
        };
        this.init(null, null, null, null);
    }
    
    public GvrLayout(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.isResumed = false;
        this.videoSurfaceId = -1;
        this.stereoModeEnabled = true;
        this.showRenderingViewsRunnable = new Runnable() {
            @Override
            public void run() {
                GvrLayout.this.updateRenderingViewsVisibility(0);
            }
        };
        this.init(null, null, null, null);
    }
    
    GvrLayout(final Context context, final GvrApi gvrApi, final DisplaySynchronizer displaySynchronizer, final FadeOverlayView fadeOverlayView, final DaydreamUtilsWrapper daydreamUtilsWrapper) {
        super(context);
        this.isResumed = false;
        this.videoSurfaceId = -1;
        this.stereoModeEnabled = true;
        this.showRenderingViewsRunnable = new Runnable() {
            @Override
            public void run() {
                GvrLayout.this.updateRenderingViewsVisibility(0);
            }
        };
        this.init(gvrApi, displaySynchronizer, fadeOverlayView, daydreamUtilsWrapper);
    }
    
    private void addScanlineRacingView() {
        if (this.scanlineRacingView == null) {
            (this.eglFactory = new EglFactory()).setUsePriorityContext(true);
            this.eglFactory.setUseProtectedBuffers(this.isAsyncReprojectionUsingProtectedBuffers);
            this.eglFactory.setEGLContextClientVersion(2);
            (this.scanlineRacingView = new GvrSurfaceView(this.getContext())).setEGLContextClientVersion(2);
            this.scanlineRacingView.setEGLConfigChooser((GLSurfaceView$EGLConfigChooser)new MutableEglConfigChooser());
            this.scanlineRacingView.setZOrderMediaOverlay(true);
            this.scanlineRacingView.setEGLContextFactory((GLSurfaceView$EGLContextFactory)this.eglFactory);
            this.scanlineRacingView.setEGLWindowSurfaceFactory((GLSurfaceView$EGLWindowSurfaceFactory)this.eglFactory);
            if (!this.stereoModeEnabled) {
                Log.w("GvrLayout", "Disabling stereo mode with async reprojection enabled may not work properly.");
                this.scanlineRacingView.setVisibility(8);
            }
            if (this.scanlineRacingRenderer == null) {
                this.scanlineRacingRenderer = new ScanlineRacingRenderer(this.gvrApi);
            }
            this.scanlineRacingRenderer.setSurfaceView(this.scanlineRacingView);
            this.scanlineRacingView.setRenderer((GLSurfaceView$Renderer)this.scanlineRacingRenderer);
            this.scanlineRacingView.setSwapMode(1);
            this.presentationLayout.addView((View)this.scanlineRacingView, 0);
        }
    }
    
    private void init(final GvrApi p0, final DisplaySynchronizer p1, final FadeOverlayView p2, final DaydreamUtilsWrapper p3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore          5
        //     3: aload_0        
        //     4: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //     7: invokestatic    com/google/vr/cardboard/ContextUtils.getActivity:(Landroid/content/Context;)Landroid/app/Activity;
        //    10: astore          6
        //    12: aload           6
        //    14: ifnull          217
        //    17: ldc             "GvrLayout.init"
        //    19: invokestatic    com/google/vr/ndk/base/TraceCompat.beginSection:(Ljava/lang/String;)V
        //    22: aload_2        
        //    23: astore          7
        //    25: aload_2        
        //    26: ifnonnull       38
        //    29: aload_0        
        //    30: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //    33: invokestatic    com/google/vr/ndk/base/GvrApi.createDefaultDisplaySynchronizer:(Landroid/content/Context;)Lcom/google/vr/cardboard/DisplaySynchronizer;
        //    36: astore          7
        //    38: aload_1        
        //    39: astore_2       
        //    40: aload_1        
        //    41: ifnonnull       58
        //    44: new             Lcom/google/vr/ndk/base/GvrApi;
        //    47: astore_2       
        //    48: aload_2        
        //    49: aload_0        
        //    50: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //    53: aload           7
        //    55: invokespecial   com/google/vr/ndk/base/GvrApi.<init>:(Landroid/content/Context;Lcom/google/vr/cardboard/DisplaySynchronizer;)V
        //    58: aload           4
        //    60: astore_1       
        //    61: aload           4
        //    63: ifnonnull       74
        //    66: new             Lcom/google/vr/ndk/base/DaydreamUtilsWrapper;
        //    69: astore_1       
        //    70: aload_1        
        //    71: invokespecial   com/google/vr/ndk/base/DaydreamUtilsWrapper.<init>:()V
        //    74: aload_0        
        //    75: aload_1        
        //    76: putfield        com/google/vr/ndk/base/GvrLayout.daydreamUtils:Lcom/google/vr/ndk/base/DaydreamUtilsWrapper;
        //    79: new             Landroid/widget/FrameLayout;
        //    82: astore          4
        //    84: aload           4
        //    86: aload_0        
        //    87: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //    90: invokespecial   android/widget/FrameLayout.<init>:(Landroid/content/Context;)V
        //    93: aload_0        
        //    94: aload           4
        //    96: putfield        com/google/vr/ndk/base/GvrLayout.presentationLayout:Landroid/widget/FrameLayout;
        //    99: new             Lcom/google/vr/ndk/base/GvrUiLayout;
        //   102: astore          4
        //   104: aload           4
        //   106: aload_0        
        //   107: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //   110: invokespecial   com/google/vr/ndk/base/GvrUiLayout.<init>:(Landroid/content/Context;)V
        //   113: aload_0        
        //   114: aload           4
        //   116: putfield        com/google/vr/ndk/base/GvrLayout.uiLayout:Lcom/google/vr/ndk/base/GvrUiLayout;
        //   119: aload_0        
        //   120: aload_2        
        //   121: putfield        com/google/vr/ndk/base/GvrLayout.gvrApi:Lcom/google/vr/ndk/base/GvrApi;
        //   124: aload_0        
        //   125: aload           7
        //   127: putfield        com/google/vr/ndk/base/GvrLayout.displaySynchronizer:Lcom/google/vr/cardboard/DisplaySynchronizer;
        //   130: aload_0        
        //   131: aload_0        
        //   132: invokespecial   com/google/vr/ndk/base/GvrLayout.tryCreatePresentationHelper:()Lcom/google/vr/ndk/base/GvrLayout$PresentationHelper;
        //   135: putfield        com/google/vr/ndk/base/GvrLayout.presentationHelper:Lcom/google/vr/ndk/base/GvrLayout$PresentationHelper;
        //   138: aload_0        
        //   139: aload_0        
        //   140: getfield        com/google/vr/ndk/base/GvrLayout.presentationLayout:Landroid/widget/FrameLayout;
        //   143: iconst_0       
        //   144: invokevirtual   com/google/vr/ndk/base/GvrLayout.addView:(Landroid/view/View;I)V
        //   147: aload_0        
        //   148: aload_0        
        //   149: getfield        com/google/vr/ndk/base/GvrLayout.uiLayout:Lcom/google/vr/ndk/base/GvrUiLayout;
        //   152: iconst_1       
        //   153: invokevirtual   com/google/vr/ndk/base/GvrLayout.addView:(Landroid/view/View;I)V
        //   156: aload_0        
        //   157: invokespecial   com/google/vr/ndk/base/GvrLayout.updateUiLayout:()V
        //   160: aload_1        
        //   161: aload_0        
        //   162: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //   165: invokevirtual   com/google/vr/ndk/base/DaydreamUtilsWrapper.isDaydreamPhone:(Landroid/content/Context;)Z
        //   168: istore          8
        //   170: iload           8
        //   172: ifne            228
        //   175: aload_1        
        //   176: aload           6
        //   178: invokevirtual   com/google/vr/ndk/base/DaydreamUtilsWrapper.getActivityDaydreamCompatibility:(Landroid/app/Activity;)I
        //   181: istore          9
        //   183: iload           9
        //   185: ifne            273
        //   188: iconst_0       
        //   189: istore          10
        //   191: iload           9
        //   193: iconst_2       
        //   194: if_icmpeq       279
        //   197: iconst_0       
        //   198: istore          9
        //   200: iload           8
        //   202: ifeq            285
        //   205: iconst_1       
        //   206: istore          9
        //   208: iload           9
        //   210: ifne            297
        //   213: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
        //   216: return         
        //   217: new             Ljava/lang/IllegalArgumentException;
        //   220: dup            
        //   221: ldc_w           "An Activity Context is required for VR functionality."
        //   224: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
        //   227: athrow         
        //   228: aload_0        
        //   229: aload_0        
        //   230: invokevirtual   com/google/vr/ndk/base/GvrLayout.createDaydreamAlignment:()Lcom/google/vr/ndk/base/DaydreamAlignment;
        //   233: putfield        com/google/vr/ndk/base/GvrLayout.daydreamAlignment:Lcom/google/vr/ndk/base/DaydreamAlignment;
        //   236: aload_0        
        //   237: getfield        com/google/vr/ndk/base/GvrLayout.uiLayout:Lcom/google/vr/ndk/base/GvrUiLayout;
        //   240: astore          7
        //   242: new             Lcom/google/vr/ndk/base/DaydreamAlignment$DefaultTouchListener;
        //   245: astore          4
        //   247: aload           4
        //   249: aload_0        
        //   250: getfield        com/google/vr/ndk/base/GvrLayout.daydreamAlignment:Lcom/google/vr/ndk/base/DaydreamAlignment;
        //   253: aload_2        
        //   254: invokespecial   com/google/vr/ndk/base/DaydreamAlignment$DefaultTouchListener.<init>:(Lcom/google/vr/ndk/base/DaydreamAlignment;Lcom/google/vr/ndk/base/GvrApi;)V
        //   257: aload           7
        //   259: aload           4
        //   261: invokevirtual   com/google/vr/ndk/base/GvrUiLayout.setOnTouchListener:(Landroid/view/View$OnTouchListener;)V
        //   264: goto            175
        //   267: astore_1       
        //   268: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
        //   271: aload_1        
        //   272: athrow         
        //   273: iconst_1       
        //   274: istore          10
        //   276: goto            191
        //   279: iconst_1       
        //   280: istore          9
        //   282: goto            200
        //   285: iload           9
        //   287: ifne            205
        //   290: iload           5
        //   292: istore          9
        //   294: goto            208
        //   297: iload           10
        //   299: ifne            323
        //   302: aload_0        
        //   303: aload_0        
        //   304: aload_0        
        //   305: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //   308: aload_2        
        //   309: aload_1        
        //   310: aload_0        
        //   311: getfield        com/google/vr/ndk/base/GvrLayout.fadeOverlayView:Lcom/google/vr/ndk/base/FadeOverlayView;
        //   314: invokevirtual   com/google/vr/ndk/base/GvrLayout.createVrCoreSdkClient:(Landroid/content/Context;Lcom/google/vr/ndk/base/GvrApi;Lcom/google/vr/ndk/base/DaydreamUtilsWrapper;Lcom/google/vr/ndk/base/FadeOverlayView;)Lcom/google/vr/ndk/base/VrCoreSdkClient;
        //   317: putfield        com/google/vr/ndk/base/GvrLayout.vrCoreSdkClient:Lcom/google/vr/ndk/base/VrCoreSdkClient;
        //   320: goto            213
        //   323: aload_3        
        //   324: astore          4
        //   326: aload_3        
        //   327: ifnonnull       344
        //   330: new             Lcom/google/vr/ndk/base/FadeOverlayView;
        //   333: astore          4
        //   335: aload           4
        //   337: aload_0        
        //   338: invokevirtual   com/google/vr/ndk/base/GvrLayout.getContext:()Landroid/content/Context;
        //   341: invokespecial   com/google/vr/ndk/base/FadeOverlayView.<init>:(Landroid/content/Context;)V
        //   344: aload_0        
        //   345: aload           4
        //   347: putfield        com/google/vr/ndk/base/GvrLayout.fadeOverlayView:Lcom/google/vr/ndk/base/FadeOverlayView;
        //   350: aload_0        
        //   351: aload_0        
        //   352: getfield        com/google/vr/ndk/base/GvrLayout.fadeOverlayView:Lcom/google/vr/ndk/base/FadeOverlayView;
        //   355: iconst_2       
        //   356: invokevirtual   com/google/vr/ndk/base/GvrLayout.addView:(Landroid/view/View;I)V
        //   359: goto            302
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  29     38     267    273    Any
        //  44     58     267    273    Any
        //  66     74     267    273    Any
        //  74     170    267    273    Any
        //  175    183    267    273    Any
        //  228    264    267    273    Any
        //  302    320    267    273    Any
        //  330    344    267    273    Any
        //  344    359    267    273    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(var_9_B5:int, ldc:int(0))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static void setPresentationFactory(final PresentationFactory sOptionalPresentationFactory) {
        GvrLayout.sOptionalPresentationFactory = sOptionalPresentationFactory;
    }
    
    private PresentationHelper tryCreatePresentationHelper() {
        if (Build$VERSION.SDK_INT <= 16) {
            return null;
        }
        final String externalDisplayName = DisplayUtils.getExternalDisplayName(this.getContext());
        if (externalDisplayName != null) {
            return new PresentationHelper(this.getContext(), this, (View)this.presentationLayout, this.displaySynchronizer, externalDisplayName);
        }
        Log.e("GvrLayout", "HDMI display name could not be found, disabling external presentation support");
        return null;
    }
    
    private void updateFadeVisibility() {
        boolean b = false;
        if (this.getWindowVisibility() == 0) {
            b = true;
        }
        if (this.fadeOverlayView != null) {
            if (b && this.isResumed) {
                this.fadeOverlayView.onVisible();
                this.removeCallbacks(this.showRenderingViewsRunnable);
                this.postDelayed(this.showRenderingViewsRunnable, 50L);
                return;
            }
            if (!b && !this.isResumed) {
                this.fadeOverlayView.onInvisible();
                this.updateRenderingViewsVisibility(4);
                this.removeCallbacks(this.showRenderingViewsRunnable);
            }
        }
    }
    
    private void updateRenderingViewsVisibility(int visibility) {
        int visibility2 = 0;
        if (this.presentationView != null) {
            final View presentationView = this.presentationView;
            if (this.stereoModeEnabled) {
                visibility2 = visibility;
            }
            presentationView.setVisibility(visibility2);
        }
        if (this.scanlineRacingView != null) {
            final GvrSurfaceView scanlineRacingView = this.scanlineRacingView;
            if (!this.stereoModeEnabled) {
                visibility = 8;
            }
            scanlineRacingView.setVisibility(visibility);
        }
    }
    
    private void updateUiLayout() {
        boolean daydreamModeEnabled = true;
        if (this.gvrApi.getViewerType() != 1) {
            daydreamModeEnabled = false;
        }
        this.uiLayout.setDaydreamModeEnabled(daydreamModeEnabled);
    }
    
    public void addPresentationListener(final PresentationListener presentationListener) {
        if (this.presentationHelper != null) {
            this.presentationHelper.addListener(presentationListener);
        }
    }
    
    DaydreamAlignment createDaydreamAlignment() {
        return new DaydreamAlignment(this.getContext(), this.gvrApi);
    }
    
    protected VrCoreSdkClient createVrCoreSdkClient(final Context context, final GvrApi gvrApi, final DaydreamUtilsWrapper daydreamUtilsWrapper, final FadeOverlayView fadeOverlayView) {
        return new VrCoreSdkClient(context, gvrApi, ContextUtils.getActivity(context).getComponentName(), daydreamUtilsWrapper, new Runnable() {
            @Override
            public void run() {
                GvrLayout.this.uiLayout.invokeCloseButtonListener();
            }
        }, fadeOverlayView);
    }
    
    public boolean enableAsyncReprojectionVideoSurface(final ExternalSurfaceListener externalSurfaceListener, final Handler handler, final boolean isAsyncReprojectionUsingProtectedBuffers) {
        if (!this.daydreamUtils.isDaydreamPhone(this.getContext())) {
            Log.e("GvrLayout", "Only Daydream devices support async reprojection. Cannot enable video Surface.");
            return false;
        }
        if (this.scanlineRacingView != null) {
            Log.e("GvrLayout", "Async reprojection is already enabled. Cannot call enableAsyncReprojectionVideoSurface after calling setAsyncReprojectionEnabled.");
            return false;
        }
        if (!this.gvrApi.usingVrDisplayService()) {
            this.isAsyncReprojectionVideoEnabled = true;
            this.isAsyncReprojectionUsingProtectedBuffers = isAsyncReprojectionUsingProtectedBuffers;
            this.scanlineRacingRenderer = new ScanlineRacingRenderer(this.gvrApi);
            this.videoSurfaceId = this.scanlineRacingRenderer.getExternalSurfaceManager().createExternalSurface(externalSurfaceListener, handler);
            return true;
        }
        Log.e("GvrLayout", "Async reprojection video is not supported on this device.");
        return false;
    }
    
    public boolean enableCardboardTriggerEmulation(final Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("The Cardboard trigger listener must not be null.");
        }
        if (this.cardboardEmulator != null) {
            return true;
        }
        if (this.daydreamUtils.isDaydreamPhone(this.getContext())) {
            this.cardboardEmulator = new CardboardEmulator(this.getContext(), runnable);
            return true;
        }
        return false;
    }
    
    public Surface getAsyncReprojectionVideoSurface() {
        if (this.isAsyncReprojectionVideoEnabled) {
            if (this.scanlineRacingView == null) {
                Log.w("GvrLayout", "No async reprojection view has been set. Cannot get async reprojection managed Surfaces. Have you called setAsyncReprojectionEnabled()?");
            }
            return this.scanlineRacingRenderer.getExternalSurfaceManager().getSurface(this.videoSurfaceId);
        }
        Log.w("GvrLayout", "Async reprojection video is not enabled. Did you call enableAsyncReprojectionVideoSurface()?");
        return null;
    }
    
    public int getAsyncReprojectionVideoSurfaceId() {
        if (!this.isAsyncReprojectionVideoEnabled) {
            Log.w("GvrLayout", "Async reprojection video is not enabled. Did you call enableAsyncReprojectionVideoSurface()?");
        }
        return this.videoSurfaceId;
    }
    
    FadeOverlayView getFadeOverlayView() {
        return this.fadeOverlayView;
    }
    
    public GvrApi getGvrApi() {
        return this.gvrApi;
    }
    
    public GvrUiLayout getUiLayout() {
        return this.uiLayout;
    }
    
    VrCoreSdkClient getVrCoreSdkClient() {
        return this.vrCoreSdkClient;
    }
    
    public boolean isPresenting() {
        return this.presentationView != null && this.presentationHelper != null && this.presentationHelper.isPresenting();
    }
    
    public void launchInVr(final PendingIntent pendingIntent) {
        Label_0012: {
            if (this.vrCoreSdkClient != null) {
                break Label_0012;
            }
            try {
                Label_0007: {
                    pendingIntent.send();
                }
                return;
                iftrue(Label_0007:)(!this.vrCoreSdkClient.launchInVr(pendingIntent));
            }
            catch (final Exception ex) {
                Log.e("GvrLayout", "Error launching PendingIntent.", (Throwable)ex);
            }
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.displaySynchronizer.onConfigurationChanged();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.presentationHelper != null) {
            this.presentationHelper.onDetachedFromWindow();
        }
    }
    
    public void onPause() {
        Label_0041_Outer:Label_0048_Outer:
        while (true) {
            TraceCompat.beginSection("GvrLayout.onPause");
            while (true) {
            Label_0116:
                while (true) {
                Label_0106:
                    while (true) {
                        try {
                            this.gvrApi.pause();
                            if (this.scanlineRacingView != null) {
                                this.scanlineRacingView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        GvrLayout.this.scanlineRacingRenderer.onPause();
                                    }
                                });
                                this.scanlineRacingView.onPause();
                            }
                            if (this.presentationHelper == null) {
                                this.displaySynchronizer.onPause();
                                if (this.vrCoreSdkClient != null) {
                                    break Label_0106;
                                }
                                if (this.cardboardEmulator == null) {
                                    this.isResumed = false;
                                    this.updateFadeVisibility();
                                    return;
                                }
                                break Label_0116;
                            }
                        }
                        finally {
                            TraceCompat.endSection();
                        }
                        this.presentationHelper.onPause();
                        continue Label_0041_Outer;
                    }
                    this.vrCoreSdkClient.onPause();
                    continue Label_0048_Outer;
                }
                this.cardboardEmulator.onPause();
                continue;
            }
        }
    }
    
    public void onResume() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: invokestatic    com/google/vr/ndk/base/TraceCompat.beginSection:(Ljava/lang/String;)V
        //     6: aload_0        
        //     7: getfield        com/google/vr/ndk/base/GvrLayout.gvrApi:Lcom/google/vr/ndk/base/GvrApi;
        //    10: invokevirtual   com/google/vr/ndk/base/GvrApi.resume:()V
        //    13: aload_0        
        //    14: getfield        com/google/vr/ndk/base/GvrLayout.daydreamAlignment:Lcom/google/vr/ndk/base/DaydreamAlignment;
        //    17: ifnonnull       72
        //    20: aload_0        
        //    21: getfield        com/google/vr/ndk/base/GvrLayout.displaySynchronizer:Lcom/google/vr/cardboard/DisplaySynchronizer;
        //    24: invokevirtual   com/google/vr/cardboard/DisplaySynchronizer.onResume:()V
        //    27: aload_0        
        //    28: getfield        com/google/vr/ndk/base/GvrLayout.presentationHelper:Lcom/google/vr/ndk/base/GvrLayout$PresentationHelper;
        //    31: ifnonnull       88
        //    34: aload_0        
        //    35: getfield        com/google/vr/ndk/base/GvrLayout.scanlineRacingView:Lcom/google/vr/ndk/base/GvrSurfaceView;
        //    38: ifnonnull       98
        //    41: aload_0        
        //    42: getfield        com/google/vr/ndk/base/GvrLayout.vrCoreSdkClient:Lcom/google/vr/ndk/base/VrCoreSdkClient;
        //    45: ifnonnull       108
        //    48: aload_0        
        //    49: getfield        com/google/vr/ndk/base/GvrLayout.cardboardEmulator:Lcom/google/vr/ndk/base/CardboardEmulator;
        //    52: ifnonnull       119
        //    55: aload_0        
        //    56: iconst_1       
        //    57: putfield        com/google/vr/ndk/base/GvrLayout.isResumed:Z
        //    60: aload_0        
        //    61: invokespecial   com/google/vr/ndk/base/GvrLayout.updateFadeVisibility:()V
        //    64: aload_0        
        //    65: invokespecial   com/google/vr/ndk/base/GvrLayout.updateUiLayout:()V
        //    68: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
        //    71: return         
        //    72: aload_0        
        //    73: getfield        com/google/vr/ndk/base/GvrLayout.daydreamAlignment:Lcom/google/vr/ndk/base/DaydreamAlignment;
        //    76: invokevirtual   com/google/vr/ndk/base/DaydreamAlignment.refreshViewerProfile:()V
        //    79: goto            20
        //    82: astore_1       
        //    83: invokestatic    com/google/vr/ndk/base/TraceCompat.endSection:()V
        //    86: aload_1        
        //    87: athrow         
        //    88: aload_0        
        //    89: getfield        com/google/vr/ndk/base/GvrLayout.presentationHelper:Lcom/google/vr/ndk/base/GvrLayout$PresentationHelper;
        //    92: invokevirtual   com/google/vr/ndk/base/GvrLayout$PresentationHelper.onResume:()V
        //    95: goto            34
        //    98: aload_0        
        //    99: getfield        com/google/vr/ndk/base/GvrLayout.scanlineRacingView:Lcom/google/vr/ndk/base/GvrSurfaceView;
        //   102: invokevirtual   com/google/vr/ndk/base/GvrSurfaceView.onResume:()V
        //   105: goto            41
        //   108: aload_0        
        //   109: getfield        com/google/vr/ndk/base/GvrLayout.vrCoreSdkClient:Lcom/google/vr/ndk/base/VrCoreSdkClient;
        //   112: invokevirtual   com/google/vr/ndk/base/VrCoreSdkClient.onResume:()Z
        //   115: pop            
        //   116: goto            48
        //   119: aload_0        
        //   120: getfield        com/google/vr/ndk/base/GvrLayout.gvrApi:Lcom/google/vr/ndk/base/GvrApi;
        //   123: invokevirtual   com/google/vr/ndk/base/GvrApi.getViewerType:()I
        //   126: iconst_1       
        //   127: if_icmpne       55
        //   130: aload_0        
        //   131: getfield        com/google/vr/ndk/base/GvrLayout.cardboardEmulator:Lcom/google/vr/ndk/base/CardboardEmulator;
        //   134: invokevirtual   com/google/vr/ndk/base/CardboardEmulator.onResume:()V
        //   137: goto            55
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  6      20     82     88     Any
        //  20     34     82     88     Any
        //  34     41     82     88     Any
        //  41     48     82     88     Any
        //  48     55     82     88     Any
        //  55     68     82     88     Any
        //  72     79     82     88     Any
        //  88     95     82     88     Any
        //  98     105    82     88     Any
        //  108    116    82     88     Any
        //  119    137    82     88     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(invokevirtual:int(GvrApi::getViewerType, getfield:GvrApi(GvrLayout::gvrApi, this:GvrLayout)), ldc:int(1))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return (this.presentationView != null && this.isPresenting() && this.presentationView.dispatchTouchEvent(motionEvent)) || super.onTouchEvent(motionEvent);
    }
    
    public void onWindowVisibilityChanged(final int n) {
        super.onWindowVisibilityChanged(n);
        this.updateFadeVisibility();
    }
    
    public boolean setAsyncReprojectionEnabled(final boolean asyncReprojectionEnabled) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("setAsyncReprojectionEnabled may only be called from the UI thread");
        }
        if (this.scanlineRacingView != null && !asyncReprojectionEnabled) {
            throw new UnsupportedOperationException("Async reprojection cannot be disabled once enabled");
        }
        if (asyncReprojectionEnabled && !this.daydreamUtils.isDaydreamPhone(this.getContext())) {
            return false;
        }
        final boolean setAsyncReprojectionEnabled = this.gvrApi.setAsyncReprojectionEnabled(asyncReprojectionEnabled);
        if (asyncReprojectionEnabled) {
            if (!setAsyncReprojectionEnabled) {
                Log.e("GvrLayout", "Failed to initialize async reprojection, unsupported device.");
                this.isAsyncReprojectionVideoEnabled = false;
                this.scanlineRacingRenderer = null;
            }
            else if (!this.gvrApi.usingVrDisplayService()) {
                this.addScanlineRacingView();
            }
        }
        return setAsyncReprojectionEnabled;
    }
    
    public void setFixedPresentationSurfaceSize(final int n, final int n2) {
        this.gvrApi.setSurfaceSize(n, n2);
    }
    
    public void setPresentationView(final View presentationView) {
        if (this.presentationView != null) {
            this.presentationLayout.removeView(this.presentationView);
        }
        this.presentationLayout.addView(presentationView, 0);
        this.presentationView = presentationView;
    }
    
    public void setStereoModeEnabled(final boolean enabled) {
        if (this.stereoModeEnabled != enabled) {
            this.stereoModeEnabled = enabled;
            this.uiLayout.setEnabled(enabled);
            if (this.vrCoreSdkClient != null) {
                this.vrCoreSdkClient.setEnabled(enabled);
            }
            if (this.fadeOverlayView != null) {
                this.fadeOverlayView.setEnabled(enabled);
            }
            if (this.daydreamAlignment != null) {
                this.daydreamAlignment.setEnabled(enabled);
            }
            this.updateRenderingViewsVisibility(0);
        }
    }
    
    public void shutdown() {
        Label_0060_Outer:Label_0067_Outer:Label_0074_Outer:
        while (true) {
            TraceCompat.beginSection("GvrLayout.shutdown");
        Label_0163:
            while (true) {
            Label_0148:
                while (true) {
                Label_0133:
                    while (true) {
                    Label_0118:
                        while (true) {
                            try {
                                this.displaySynchronizer.shutdown();
                                if (this.daydreamAlignment != null) {
                                    this.daydreamAlignment.shutdown();
                                }
                                this.removeView((View)this.presentationLayout);
                                this.removeView((View)this.uiLayout);
                                if (this.scanlineRacingRenderer == null) {
                                    this.scanlineRacingView = null;
                                    this.presentationView = null;
                                    if (this.presentationHelper != null) {
                                        break Label_0118;
                                    }
                                    if (this.vrCoreSdkClient != null) {
                                        break Label_0133;
                                    }
                                    if (this.cardboardEmulator != null) {
                                        break Label_0148;
                                    }
                                    if (this.gvrApi == null) {
                                        return;
                                    }
                                    break Label_0163;
                                }
                            }
                            finally {
                                TraceCompat.endSection();
                            }
                            this.scanlineRacingRenderer.shutdown();
                            this.scanlineRacingRenderer = null;
                            continue Label_0060_Outer;
                        }
                        this.presentationHelper.shutdown();
                        this.presentationHelper = null;
                        continue Label_0067_Outer;
                    }
                    this.vrCoreSdkClient.onPause();
                    this.vrCoreSdkClient = null;
                    continue Label_0074_Outer;
                }
                this.cardboardEmulator.onPause();
                this.cardboardEmulator = null;
                continue;
            }
            this.gvrApi.shutdown();
            this.gvrApi = null;
        }
    }
    
    public interface ExternalSurfaceListener
    {
        void onFrameAvailable();
        
        void onSurfaceAvailable(final Surface p0);
    }
    
    public interface ExternalSurfaceManager
    {
        int createExternalSurface();
        
        int createExternalSurface(final ExternalSurfaceListener p0, final Handler p1);
        
        Surface getSurface(final int p0);
        
        int getSurfaceCount();
        
        void releaseExternalSurface(final int p0);
    }
    
    interface PresentationFactory
    {
        Presentation create(final Context p0, final Display p1);
    }
    
    private static class PresentationHelper implements DisplayManager$DisplayListener
    {
        private final Context context;
        private final DisplayManager displayManager;
        private final DisplaySynchronizer displaySynchronizer;
        private String externalDisplayName;
        private final RelativeLayout$LayoutParams layout;
        private final List<PresentationListener> listeners;
        private final FrameLayout originalParent;
        private Presentation presentation;
        private final View view;
        
        PresentationHelper(final Context context, final FrameLayout originalParent, final View view, final DisplaySynchronizer displaySynchronizer, final String externalDisplayName) {
            this.layout = new RelativeLayout$LayoutParams(-1, -1);
            this.context = context;
            this.originalParent = originalParent;
            this.view = view;
            this.displaySynchronizer = displaySynchronizer;
            this.externalDisplayName = externalDisplayName;
            this.displayManager = (DisplayManager)context.getSystemService("display");
            this.listeners = new ArrayList<PresentationListener>();
        }
        
        private static void detachViewFromParent(final View view) {
            final ViewGroup viewGroup = (ViewGroup)view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
            }
        }
        
        private boolean hasCurrentPresentationExpired() {
            return this.presentation != null && (!this.presentation.isShowing() || !this.presentation.getDisplay().isValid());
        }
        
        private boolean isValidExternalDisplay(final Display display) {
            return display.isValid() && display.getName().equals(this.externalDisplayName);
        }
        
        private void setDisplay(Display display) {
            Display display2;
            if (this.presentation == null) {
                display2 = null;
            }
            else {
                display2 = this.presentation.getDisplay();
            }
            if (!this.hasCurrentPresentationExpired() && DisplayUtils.isSameDisplay(display, display2)) {
                return;
            }
            final Presentation presentation = this.presentation;
            if (this.presentation != null) {
                this.presentation.dismiss();
                this.presentation = null;
            }
            detachViewFromParent(this.view);
            Label_0051: {
                Label_0039: {
                    if (display != null) {
                        while (true) {
                            Label_0235: {
                                if (GvrLayout.sOptionalPresentationFactory != null) {
                                    break Label_0235;
                                }
                                final Presentation create = new Presentation(this.context, display);
                                (this.presentation = create).addContentView(this.view, (ViewGroup$LayoutParams)this.layout);
                                try {
                                    this.presentation.show();
                                    break Label_0051;
                                }
                                catch (final WindowManager$InvalidDisplayException obj) {
                                    final String value = String.valueOf(obj);
                                    Log.e("GvrLayout", new StringBuilder(String.valueOf(value).length() + 57).append("Attaching Cardboard View to the external display failed: ").append(value).toString());
                                    this.presentation.cancel();
                                    this.presentation = null;
                                    detachViewFromParent(this.view);
                                    break Label_0039;
                                }
                            }
                            final Presentation create = GvrLayout.sOptionalPresentationFactory.create(this.context, display);
                            continue;
                        }
                    }
                }
                this.originalParent.addView(this.view, 0);
            }
            final DisplaySynchronizer displaySynchronizer = this.displaySynchronizer;
            if (this.presentation == null) {
                display = DisplayUtils.getDefaultDisplay(this.context);
            }
            else {
                display = this.presentation.getDisplay();
            }
            displaySynchronizer.setDisplay(display);
            if (presentation != null) {
                final Iterator<PresentationListener> iterator = this.listeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onPresentationStopped();
                }
            }
            if (this.presentation != null) {
                final Iterator<PresentationListener> iterator2 = this.listeners.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().onPresentationStarted(this.presentation.getDisplay());
                }
            }
        }
        
        public void addListener(final PresentationListener presentationListener) {
            if (!this.listeners.contains(presentationListener)) {
                this.listeners.add(presentationListener);
                if (this.presentation != null) {
                    presentationListener.onPresentationStarted(this.presentation.getDisplay());
                }
            }
        }
        
        public boolean isPresenting() {
            return this.presentation != null && this.presentation.isShowing();
        }
        
        public void onDetachedFromWindow() {
            this.displayManager.unregisterDisplayListener((DisplayManager$DisplayListener)this);
            this.setDisplay(null);
        }
        
        public void onDisplayAdded(final int n) {
            final Display display = this.displayManager.getDisplay(n);
            if (this.isValidExternalDisplay(display)) {
                this.setDisplay(display);
            }
        }
        
        public void onDisplayChanged(final int n) {
        }
        
        public void onDisplayRemoved(final int n) {
            if (this.presentation != null && this.presentation.getDisplay().getDisplayId() == n) {
                this.setDisplay(null);
            }
        }
        
        public void onPause() {
            this.displayManager.unregisterDisplayListener((DisplayManager$DisplayListener)this);
        }
        
        public void onResume() {
            Display display = null;
            this.externalDisplayName = DisplayUtils.getExternalDisplayName(this.context);
            if (this.externalDisplayName != null) {
                this.displayManager.registerDisplayListener((DisplayManager$DisplayListener)this, (Handler)null);
                for (final Display display2 : this.displayManager.getDisplays()) {
                    if (this.isValidExternalDisplay(display2)) {
                        display = display2;
                        break;
                    }
                }
                this.setDisplay(display);
                return;
            }
            this.setDisplay(null);
        }
        
        public void shutdown() {
            this.displayManager.unregisterDisplayListener((DisplayManager$DisplayListener)this);
            if (this.presentation != null) {
                this.presentation.cancel();
                this.presentation = null;
                final Iterator<PresentationListener> iterator = this.listeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onPresentationStopped();
                }
            }
        }
    }
    
    public interface PresentationListener
    {
        void onPresentationStarted(final Display p0);
        
        void onPresentationStopped();
    }
}
