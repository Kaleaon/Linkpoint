// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.GlobalOptions;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumPlanes;
import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import java.util.ConcurrentModificationException;
import javax.microedition.khronos.opengles.GL10;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import android.opengl.GLES20;
import android.os.Build$VERSION;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGL10;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import android.graphics.Paint$Align;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import java.nio.Buffer;
import android.opengl.GLES10;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.avatar.DrawableHUD;
import java.util.Collections;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import android.os.Handler;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawList;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView$EGLContextFactory;
import android.opengl.GLSurfaceView$Renderer;

@SuppressLint({ "InlinedApi" })
public class WorldViewRenderer implements GLSurfaceView$Renderer, GLSurfaceView$EGLContextFactory
{
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EMPTY_FRAMES_COUNT = 1;
    private static final int EXT_TEXTURE_MATRIX = 0;
    private static final int EXT_TEXTURE_MATRIX_EYE = 16;
    private static final int EXT_TEXTURE_MATRIX_INVERSE = 48;
    private static final int EXT_TEXTURE_MATRIX_RESULT = 32;
    private static final int MIN_DRAW_LIST_UPDATE_FRAMES = 4;
    private static final long MIN_DRAW_LIST_UPDATE_INTERVAL = 100L;
    public static final int MSG_SCREENSHOT = 5;
    public static final int MSG_SET_PICKED_OBJECT = 1;
    public static final int MSG_SET_TOUCHED_OBJECT = 2;
    public static final int MSG_SHADER_COMPILE_ERROR = 4;
    public static final int MSG_SURFACE_CREATED = 3;
    private int[] Colorbuffers;
    private int[] Framebuffers;
    private int[] Renderbuffers;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    @Nullable
    private SLAvatarControl avatarControl;
    private int avatarCountLimit;
    private final CameraParams cameraParams;
    private boolean createdGL30;
    private DrawList currentDrawList;
    @Nullable
    private FrustrumInfo currentFrustrumInfo;
    private volatile int displayedHUDid;
    private int drawDistance;
    private int drawListUpdateFrameCount;
    private SLObjectInfo drawPickedObject;
    private AtomicBoolean drawingEnabled;
    private final float[] extTextureHitVector;
    private final float[] extTextureMatrix;
    private final float[] extTextureResultVector;
    private AtomicInteger firstFrameCount;
    private long firstFrameTime;
    private final int fontSize;
    private volatile float forcedTime;
    private int fpsFrameCount;
    private HeadTransformCompat headTransformCompat;
    private boolean hoverTextEnableHUDs;
    private boolean hoverTextEnableObjects;
    private float hudOffsetX;
    private float hudOffsetY;
    private float hudScaleFactor;
    private final Runnable initSpatialIndexRunnable;
    private volatile boolean initialUpdateDone;
    private boolean isFlinging;
    private boolean isInteracting;
    private volatile boolean isResponsiveMode;
    private long lastDrawListUpdateTime;
    private long lastFrameTime;
    private boolean needPickObject;
    private float needPickX;
    private float needPickY;
    private boolean ownAvatarHidden;
    @Nullable
    private SLParcelInfo parcelInfo;
    private Handler pickHandler;
    private final Object pickLock;
    private long previousFrameTime;
    private final AtomicReference<RenderContext> renderContext;
    private final SynchronousExecutor renderThreadExecutor;
    private final boolean requestGL20;
    private final Object responsiveModeLock;
    private Handler screenshotHandler;
    private float[] simSunHour;
    private final Handler stateHandler;
    private final int[] systemFramebuffer;
    private long thisFrameTime;
    private List<TouchHUDEvent> touchHUDEvents;
    private Handler touchHandler;
    
    public WorldViewRenderer(final Handler stateHandler, final boolean requestGL20, @Nonnull final UserManager userManager, final int fontSize) {
        this.renderThreadExecutor = new SynchronousExecutor();
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(this.renderThreadExecutor, new _$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA(this));
        this.renderContext = new AtomicReference<RenderContext>();
        this.avatarControl = null;
        this.parcelInfo = null;
        this.initialUpdateDone = false;
        this.drawingEnabled = new AtomicBoolean(true);
        this.firstFrameCount = new AtomicInteger(1);
        this.lastDrawListUpdateTime = 0L;
        this.drawListUpdateFrameCount = 0;
        this.drawPickedObject = null;
        this.simSunHour = new float[] { Float.NaN };
        this.forcedTime = Float.NaN;
        this.pickLock = new Object();
        this.needPickObject = false;
        this.needPickX = 0.0f;
        this.needPickY = 0.0f;
        this.pickHandler = null;
        this.touchHUDEvents = Collections.synchronizedList(new LinkedList<TouchHUDEvent>());
        this.touchHandler = null;
        this.ownAvatarHidden = false;
        this.drawDistance = 20;
        this.avatarCountLimit = 5;
        this.cameraParams = new CameraParams();
        this.responsiveModeLock = new Object();
        this.isInteracting = false;
        this.isFlinging = false;
        this.isResponsiveMode = false;
        this.screenshotHandler = null;
        this.displayedHUDid = 0;
        this.hudScaleFactor = 1.0f;
        this.hudOffsetX = 0.0f;
        this.hudOffsetY = 0.0f;
        this.hoverTextEnableHUDs = true;
        this.hoverTextEnableObjects = false;
        this.createdGL30 = false;
        this.Framebuffers = null;
        this.Renderbuffers = null;
        this.Colorbuffers = null;
        this.headTransformCompat = null;
        this.systemFramebuffer = new int[1];
        this.initSpatialIndexRunnable = new _$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA$1(this);
        this.thisFrameTime = 0L;
        this.fpsFrameCount = 0;
        this.previousFrameTime = 0L;
        this.currentDrawList = null;
        this.extTextureMatrix = new float[64];
        this.extTextureHitVector = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
        this.extTextureResultVector = new float[4];
        this.stateHandler = stateHandler;
        this.requestGL20 = requestGL20;
        this.fontSize = fontSize;
        this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
    }
    
    private void handleHUDTouch(final RenderContext renderContext, final DrawableHUD drawableHUD, final TouchHUDEvent touchHUDEvent, final ObjectIntersectInfo objectIntersectInfo) {
        final float n = (float)renderContext.viewportRect[2];
        final float n2 = (float)renderContext.viewportRect[2];
        final float n3 = (n2 / 2.0f - touchHUDEvent.y) / n2;
        final float n4 = (n / 2.0f - touchHUDEvent.x) / n2;
        final SLAttachmentPoint attachmentPoint = drawableHUD.getAttachmentPoint();
        final float y = attachmentPoint.position.y;
        final float z = attachmentPoint.position.z;
        try {
            final Handler touchHandler = this.touchHandler;
            if (touchHandler != null) {
                touchHandler.sendMessage(touchHandler.obtainMessage(2, (Object)objectIntersectInfo.objInfo));
            }
            this.agentCircuit.get().TouchObjectFace(objectIntersectInfo.objInfo, objectIntersectInfo.intersectInfo.faceID, 0.0f, y + n4, n3 + z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        if (slAgentCircuit != null) {
            Debug.Printf("WorldViewRenderer: got new agentCircuit.", new Object[0]);
            this.initialUpdateDone = false;
            this.avatarControl = slAgentCircuit.getModules().avatarControl;
            this.parcelInfo = slAgentCircuit.getGridConnection().parcelInfo;
            this.initialUpdateDone = false;
            final RenderContext renderContext = this.renderContext.get();
            if (renderContext != null) {
                renderContext.setMeshCapURL(slAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.GetMesh));
                if (this.parcelInfo != null) {
                    PrimComputeExecutor.getInstance().execute(this.initSpatialIndexRunnable);
                }
            }
        }
        else {
            this.avatarControl = null;
            this.parcelInfo = null;
        }
    }
    
    private void processObjectPick() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: ldc             NaN
        //     4: fstore_2       
        //     5: aload_0        
        //     6: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.pickLock:Ljava/lang/Object;
        //     9: astore_3       
        //    10: aload_3        
        //    11: monitorenter   
        //    12: aload_0        
        //    13: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.needPickObject:Z
        //    16: ifeq            230
        //    19: aload_0        
        //    20: iconst_0       
        //    21: putfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.needPickObject:Z
        //    24: aload_0        
        //    25: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.needPickX:F
        //    28: fstore          4
        //    30: aload_0        
        //    31: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.needPickY:F
        //    34: fstore_2       
        //    35: aload_0        
        //    36: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.pickHandler:Landroid/os/Handler;
        //    39: astore          5
        //    41: iconst_1       
        //    42: istore_1       
        //    43: aload_3        
        //    44: monitorexit    
        //    45: iload_1        
        //    46: ifeq            225
        //    49: aload_0        
        //    50: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.renderContext:Ljava/util/concurrent/atomic/AtomicReference;
        //    53: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //    56: checkcast       Lcom/lumiyaviewer/lumiya/render/RenderContext;
        //    59: astore          6
        //    61: aload           6
        //    63: ifnull          225
        //    66: aload_0        
        //    67: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.currentDrawList:Lcom/lumiyaviewer/lumiya/render/spatial/DrawList;
        //    70: getfield        com/lumiyaviewer/lumiya/render/spatial/DrawList.objects:Ljava/util/ArrayList;
        //    73: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //    78: astore          7
        //    80: aconst_null    
        //    81: astore_3       
        //    82: aload_3        
        //    83: astore          8
        //    85: aload           7
        //    87: invokeinterface java/util/Iterator.hasNext:()Z
        //    92: ifeq            129
        //    95: aload_3        
        //    96: astore          8
        //    98: aload_0        
        //    99: aload           6
        //   101: fload           4
        //   103: fload_2        
        //   104: aload           7
        //   106: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   111: checkcast       Lcom/lumiyaviewer/lumiya/render/DrawableObject;
        //   114: aload_3        
        //   115: invokespecial   com/lumiyaviewer/lumiya/render/WorldViewRenderer.tryPickObject:(Lcom/lumiyaviewer/lumiya/render/RenderContext;FFLcom/lumiyaviewer/lumiya/render/picking/IntersectPickable;Lcom/lumiyaviewer/lumiya/render/picking/ObjectIntersectInfo;)Lcom/lumiyaviewer/lumiya/render/picking/ObjectIntersectInfo;
        //   118: astore_3       
        //   119: goto            82
        //   122: astore          8
        //   124: aload_3        
        //   125: monitorexit    
        //   126: aload           8
        //   128: athrow         
        //   129: aload_3        
        //   130: astore          8
        //   132: aload_0        
        //   133: getfield        com/lumiyaviewer/lumiya/render/WorldViewRenderer.currentDrawList:Lcom/lumiyaviewer/lumiya/render/spatial/DrawList;
        //   136: getfield        com/lumiyaviewer/lumiya/render/spatial/DrawList.avatars:Ljava/util/ArrayList;
        //   139: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   144: astore          9
        //   146: aload_3        
        //   147: astore          7
        //   149: aload_3        
        //   150: astore          8
        //   152: aload           9
        //   154: invokeinterface java/util/Iterator.hasNext:()Z
        //   159: ifeq            201
        //   162: aload_3        
        //   163: astore          8
        //   165: aload_0        
        //   166: aload           6
        //   168: fload           4
        //   170: fload_2        
        //   171: aload           9
        //   173: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   178: checkcast       Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatar;
        //   181: aload_3        
        //   182: invokespecial   com/lumiyaviewer/lumiya/render/WorldViewRenderer.tryPickObject:(Lcom/lumiyaviewer/lumiya/render/RenderContext;FFLcom/lumiyaviewer/lumiya/render/picking/IntersectPickable;Lcom/lumiyaviewer/lumiya/render/picking/ObjectIntersectInfo;)Lcom/lumiyaviewer/lumiya/render/picking/ObjectIntersectInfo;
        //   185: astore_3       
        //   186: goto            146
        //   189: astore_3       
        //   190: aconst_null    
        //   191: astore          8
        //   193: aload_3        
        //   194: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   197: aload           8
        //   199: astore          7
        //   201: aload           7
        //   203: ifnull          225
        //   206: aload           5
        //   208: ifnull          225
        //   211: aload           5
        //   213: aload           5
        //   215: iconst_1       
        //   216: aload           7
        //   218: invokevirtual   android/os/Handler.obtainMessage:(ILjava/lang/Object;)Landroid/os/Message;
        //   221: invokevirtual   android/os/Handler.sendMessage:(Landroid/os/Message;)Z
        //   224: pop            
        //   225: return         
        //   226: astore_3       
        //   227: goto            193
        //   230: aconst_null    
        //   231: astore          5
        //   233: ldc             NaN
        //   235: fstore          4
        //   237: goto            43
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  12     41     122    129    Any
        //  66     80     189    193    Ljava/lang/Exception;
        //  85     95     226    230    Ljava/lang/Exception;
        //  98     119    226    230    Ljava/lang/Exception;
        //  132    146    226    230    Ljava/lang/Exception;
        //  152    162    226    230    Ljava/lang/Exception;
        //  165    186    226    230    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 120 out of bounds for length 120
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
    
    private void setIsFlinging(final boolean isFlinging) {
        boolean b = false;
        synchronized (this.responsiveModeLock) {
            if (this.isFlinging != isFlinging) {
                this.isFlinging = isFlinging;
                b = true;
            }
            monitorexit(this.responsiveModeLock);
            if (b) {
                this.updateResponsive();
            }
        }
    }
    
    private void takeScreenshot(final RenderContext renderContext, final Handler handler) {
        int i = 0;
        final int n = renderContext.viewportRect[2];
        final int n2 = renderContext.viewportRect[3];
        final DirectByteBuffer directByteBuffer = new DirectByteBuffer(n * n2 * 4);
        GLES10.glReadPixels(0, 0, n, n2, 6408, 5121, (Buffer)directByteBuffer.asByteBuffer());
        final Bitmap bitmap = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer((Buffer)directByteBuffer.asByteBuffer());
        final Bitmap bitmap2 = Bitmap.createBitmap(n, n2, Bitmap$Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap2);
        final Matrix matrix = new Matrix();
        matrix.setScale(1.0f, -1.0f);
        matrix.postTranslate(0.0f, (float)n2);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(bitmap, matrix, paint);
        bitmap.recycle();
        paint.setTextSize((float)this.fontSize);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint$Align.LEFT);
        paint.setColor(-1);
        final String[] array = { "Lumiya Viewer", "http://lumiyaviewer.com" };
        float n3 = this.fontSize * 2.0f;
        while (i < array.length) {
            canvas.drawText(array[i], (float)this.fontSize, n3, paint);
            n3 = n3 + (paint.descent() - paint.ascent()) + this.fontSize * 0.5f;
            ++i;
        }
        handler.sendMessage(handler.obtainMessage(5, (Object)bitmap2));
    }
    
    private ObjectIntersectInfo tryPickObject(final RenderContext renderContext, final float n, final float n2, final IntersectPickable intersectPickable, final ObjectIntersectInfo objectIntersectInfo) {
        final ObjectIntersectInfo pickObject = intersectPickable.PickObject(renderContext, n, n2, -0.9f);
        if (pickObject != null) {
            if (objectIntersectInfo == null) {
                return pickObject;
            }
            if (pickObject.pickDepth < objectIntersectInfo.pickDepth) {
                return pickObject;
            }
        }
        return objectIntersectInfo;
    }
    
    private void updateResponsive() {
        while (true) {
            Label_0059: {
                synchronized (this.responsiveModeLock) {
                    final boolean isResponsiveMode = this.isResponsiveMode;
                    final boolean isResponsiveMode2 = this.isInteracting || this.isFlinging;
                    this.isResponsiveMode = isResponsiveMode2;
                    monitorexit(this.responsiveModeLock);
                    if (isResponsiveMode != isResponsiveMode2) {
                        if (!isResponsiveMode2) {
                            break Label_0059;
                        }
                        PrimComputeExecutor.getInstance().pause();
                    }
                    return;
                }
            }
            PrimComputeExecutor.getInstance().resume();
        }
    }
    
    public EGLContext createContext(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLConfig eglConfig) {
        Debug.Printf("EGL: createContext called.", new Object[0]);
        if (this.requestGL20 && Build$VERSION.SDK_INT >= 18) {
            Debug.Printf("EGL: trying to create 3.0 context.", new Object[0]);
            final EGLContext eglCreateContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, new int[] { 12440, 3, 12344 });
            if (eglCreateContext != null && eglCreateContext != EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("EGL: 3.0 context apparently created.", new Object[0]);
                this.createdGL30 = true;
                return eglCreateContext;
            }
            Debug.Printf("EGL: Failed to create 3.0 context.", new Object[0]);
        }
        Debug.Printf("EGL: Creating regular context.", new Object[0]);
        this.createdGL30 = false;
        int[] array = { 12440, 2, 12344 };
        final EGLContext egl_NO_CONTEXT = EGL10.EGL_NO_CONTEXT;
        if (!this.requestGL20) {
            array = null;
        }
        return egl10.eglCreateContext(eglDisplay, eglConfig, egl_NO_CONTEXT, array);
    }
    
    @SuppressLint({ "DefaultLocale" })
    public void destroyContext(final EGL10 egl10, final EGLDisplay eglDisplay, final EGLContext eglContext) {
        Debug.Printf("EGL: destroyContext called.", new Object[0]);
        this.onRendererShutdown();
        if (!egl10.eglDestroyContext(eglDisplay, eglContext)) {
            throw new RuntimeException(String.format("EGLError code %d", egl10.eglGetError()));
        }
        Debug.Printf("EGL: destroyContext exiting.", new Object[0]);
    }
    
    public void disableDrawing() {
        this.drawingEnabled.set(false);
    }
    
    public void drawCrosshair(final float n, final float n2) {
        final RenderContext renderContext = this.renderContext.get();
        if (renderContext != null && renderContext.crosshairTexture != null) {
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glEnable(3042);
            GLES20.glUseProgram(renderContext.rawShaderProgram.getHandle());
            GLES20.glActiveTexture(33984);
            renderContext.crosshairTexture.GLDraw();
            GLES20.glUniform1i(renderContext.rawShaderProgram.textureSampler, 0);
            android.opengl.Matrix.setIdentityM(this.extTextureMatrix, 0);
            GLES20.glUniformMatrix4fv(renderContext.rawShaderProgram.vTextureTransformMatrix, 1, false, this.extTextureMatrix, 0);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 0, 0.0f, 0.0f, -1.9f);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 16, this.extTextureMatrix, 0, -n2, 0.0f, 0.0f);
            android.opengl.Matrix.scaleM(this.extTextureMatrix, 16, n, n, 1.0f);
            android.opengl.Matrix.multiplyMM(this.extTextureMatrix, 32, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), this.extTextureMatrix, 16);
            GLES20.glUniformMatrix4fv(renderContext.rawShaderProgram.uMVPMatrix, 1, false, this.extTextureMatrix, 32);
            renderContext.quad.DrawSingleQuadShader(renderContext, renderContext.rawShaderProgram.vPosition, renderContext.rawShaderProgram.vTexCoord);
        }
    }
    
    public void drawExternalTexture(@Nonnull final GLExternalTexture glExternalTexture, final float[] array, final float n, final float n2, final float n3, final float n4, final float n5, final float[] array2, final int n6) {
        final RenderContext renderContext = this.renderContext.get();
        if (renderContext != null && renderContext.extTextureProgram != null) {
            glExternalTexture.update(array);
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glEnable(3042);
            GLES20.glUseProgram(renderContext.extTextureProgram.getHandle());
            GLES20.glActiveTexture(33984);
            glExternalTexture.bind();
            GLES20.glUniform1i(renderContext.extTextureProgram.textureSampler, 0);
            GLES20.glUniformMatrix4fv(renderContext.extTextureProgram.vTextureTransformMatrix, 1, false, array, 0);
            android.opengl.Matrix.setIdentityM(this.extTextureMatrix, 0);
            android.opengl.Matrix.rotateM(this.extTextureMatrix, 0, -n2, 1.0f, 0.0f, 0.0f);
            android.opengl.Matrix.rotateM(this.extTextureMatrix, 0, -n3, 0.0f, 1.0f, 0.0f);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 0, 0.0f, 0.0f, -2.0f);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 16, this.extTextureMatrix, 0, -n, 0.0f, 0.0f);
            android.opengl.Matrix.scaleM(this.extTextureMatrix, 16, n4, n5, 1.0f);
            android.opengl.Matrix.multiplyMM(this.extTextureMatrix, 32, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), this.extTextureMatrix, 16);
            android.opengl.Matrix.invertM(this.extTextureMatrix, 48, this.extTextureMatrix, 32);
            android.opengl.Matrix.multiplyMV(this.extTextureResultVector, 0, this.extTextureMatrix, 48, this.extTextureHitVector, 0);
            array2[n6] = this.extTextureResultVector[0];
            array2[n6 + 1] = this.extTextureResultVector[1];
            GLES20.glUniformMatrix4fv(renderContext.extTextureProgram.uMVPMatrix, 1, false, this.extTextureMatrix, 32);
            renderContext.quad.DrawSingleQuadShader(renderContext, renderContext.extTextureProgram.vPosition, renderContext.extTextureProgram.vTexCoord);
        }
    }
    
    public void enableDrawing() {
        if (!this.drawingEnabled.getAndSet(true)) {
            this.firstFrameCount.set(1);
        }
    }
    
    public void onDrawFrame(final GL10 gl10) {
        this.onPrepareFrame(null);
        this.onDrawFrame(gl10, null, null, null, null, null, 0);
        this.onFinishFrame();
    }
    
    public void onDrawFrame(final GL10 gl10, @Nullable final HeadTransformCompat headTransformCompat, @Nullable final float[] array, @Nullable final int[] array2, @Nullable final float[] array3, @Nullable final float[] array4, int i) {
        RenderContext renderContext = null;
    Label_0283_Outer:
        while (true) {
            while (true) {
                Label_0394: {
                    while (true) {
                        synchronized (this) {
                            if (!this.drawingEnabled.get()) {
                                return;
                            }
                            renderContext = this.renderContext.get();
                            if (renderContext == null) {
                                return;
                            }
                            if (array3 != null && headTransformCompat != null) {
                                renderContext.glModelResetIdentity();
                                if (array4 != null) {
                                    renderContext.setActiveProjectionMatrix(array4, i);
                                }
                                else {
                                    renderContext.setActiveProjectionMatrix(renderContext.projectionMatrix);
                                }
                                renderContext.glModelMultMatrixf(array3, 0);
                                renderContext.glModelRotatef(-headTransformCompat.viewExtraYaw + 90.0f, 0.0f, 1.0f, 0.0f);
                                renderContext.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                                renderContext.glModelTranslatef(-renderContext.frameCamera.x, -renderContext.frameCamera.y, -renderContext.frameCamera.z);
                            }
                            if (!renderContext.hasGL20) {
                                break Label_0394;
                            }
                            if (renderContext.useFXAA && this.Framebuffers != null && this.Colorbuffers != null) {
                                this.systemFramebuffer[0] = 0;
                                GLES20.glGetIntegerv(36006, this.systemFramebuffer, 0);
                                GLES20.glBindFramebuffer(36160, this.Framebuffers[0]);
                                GLES20.glBindTexture(3553, this.Colorbuffers[0]);
                                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.Colorbuffers[0], 0);
                                if (array2 != null) {
                                    GLES20.glViewport(0, 0, renderContext.viewportRect[2], renderContext.viewportRect[3]);
                                }
                                GLES20.glEnable(2884);
                                GLES20.glEnable(2929);
                                GLES20.glEnable(3042);
                                GLES20.glBlendFunc(770, 771);
                                GLES20.glClear(256);
                                i = 1;
                                final int value = this.firstFrameCount.get();
                                if (value > 0) {
                                    Debug.Log("onDrawFrame: drawing empty first frame!");
                                    this.firstFrameCount.set(value - 1);
                                    i = 0;
                                }
                                if (this.parcelInfo == null || (i ^ 0x1) != 0x0) {
                                    GLES20.glBindFramebuffer(36160, this.systemFramebuffer[0]);
                                    return;
                                }
                                break;
                            }
                        }
                        if (array2 != null) {
                            GLES20.glViewport(array2[0], array2[1], array2[2], array2[3]);
                            continue Label_0283_Outer;
                        }
                        continue Label_0283_Outer;
                    }
                }
                GLES10.glEnable(2884);
                GLES10.glEnable(2929);
                GLES10.glEnable(3042);
                GLES10.glEnable(3008);
                GLES10.glAlphaFunc(516, 0.4f);
                GLES10.glBlendFunc(770, 771);
                GLES10.glClear(16640);
                GLES10.glTexEnvf(8960, 8704, 8448.0f);
                if (array2 != null) {
                    GLES20.glViewport(array2[0], array2[1], array2[2], array2[3]);
                    continue;
                }
                continue;
            }
        }
        DrawList currentDrawList = null;
        ArrayList<DrawableObject> objects = null;
        int[] renderPasses = null;
        int size = 0;
        try {
            if (this.drawingEnabled.get() && this.currentDrawList != null && this.currentFrustrumInfo != null) {
                if (array3 == null && array != null) {
                    renderContext.glModelTranslatef(array[0], array[1], array[2]);
                }
                renderContext.initAllPrimPrograms(true);
                renderContext.curPrimProgram = null;
                currentDrawList = this.currentDrawList;
                if (renderContext.hasGL20) {
                    GLES20.glDisable(3042);
                }
                else {
                    GLES10.glDisable(3042);
                    GLES10.glDisable(3008);
                }
                objects = currentDrawList.objects;
                renderPasses = currentDrawList.renderPasses;
                size = objects.size();
                renderContext.clearFaceTexture();
                for (i = 0; i < size; ++i) {
                    renderPasses[i] = objects.get(i).Draw(renderContext, 1);
                }
                goto Label_0830;
            }
            goto Label_0635;
        }
        catch (final ConcurrentModificationException ex) {
            Debug.Warning(ex);
        }
        catch (final IndexOutOfBoundsException ex2) {
            Debug.Warning(ex2);
            goto Label_0635;
        }
        renderContext.curPrimProgram = null;
        renderContext.clearFaceTexture();
        if (renderContext.hasGL20) {
            GLES20.glEnable(3042);
        }
        else {
            GLES10.glEnable(3042);
            GLES10.glEnable(3008);
        }
        for (final DrawableAvatar drawableAvatar : currentDrawList.avatars) {
            if (drawableAvatar != currentDrawList.myAvatar || !this.ownAvatarHidden) {
                drawableAvatar.Draw(renderContext);
            }
        }
        if (renderContext.hasGL20) {
            renderContext.windlightSky.GLDraw(renderContext, this.cameraParams.getHeading(), this.cameraParams.getTilt());
        }
        renderContext.curPrimProgram = null;
        renderContext.clearFaceTexture();
        for (i = size - 1; i >= 0; --i) {
            if ((renderPasses[i] & 0x2) != 0x0) {
                ((DrawableObject)objects.get(i)).Draw(renderContext, 2);
            }
        }
        if (renderContext.hasGL30) {
            BoundingBox.PrepareOcclusionQueries(renderContext);
            final Iterator iterator2 = objects.iterator();
            while (iterator2.hasNext()) {
                ((DrawableObject)iterator2.next()).TestOcclusion(renderContext, this.currentFrustrumInfo.mvpMatrix);
            }
            BoundingBox.EndOcclusionQueries(renderContext);
        }
        if (this.screenshotHandler != null) {
            this.takeScreenshot(renderContext, this.screenshotHandler);
            this.screenshotHandler = null;
        }
        if (this.drawPickedObject != null && (this.drawPickedObject.isAvatar() ^ true)) {
            final Iterator<Object> iterator3 = currentDrawList.objects.iterator();
            while (iterator3.hasNext()) {
                iterator3.next().DrawIfPicked(renderContext, this.drawPickedObject);
            }
        }
        if (!renderContext.hasGL20) {
            GLES10.glMatrixMode(5889);
            GLES10.glLoadIdentity();
            GLES10.glMatrixMode(5888);
        }
        if (renderContext.hasGL20 && renderContext.useFXAA && this.Colorbuffers != null) {
            GLES20.glFinish();
            GLES20.glBindTexture(3553, this.Colorbuffers[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.Colorbuffers[1], 0);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(16384);
        }
        renderContext.quad.PrepareDrawQuads(renderContext);
        for (final DrawableAvatar drawableAvatar2 : currentDrawList.avatars) {
            if (drawableAvatar2 != currentDrawList.myAvatar || !this.ownAvatarHidden) {
                drawableAvatar2.DrawNameTag(renderContext);
            }
        }
        final Iterator<Object> iterator5 = currentDrawList.avatarStubs.iterator();
        while (iterator5.hasNext()) {
            iterator5.next().DrawNameTag(renderContext);
        }
        if (this.hoverTextEnableObjects) {
            final Iterator<Object> iterator6 = currentDrawList.objects.iterator();
            while (iterator6.hasNext()) {
                iterator6.next().DrawHoverText(renderContext, false);
            }
        }
        renderContext.quad.EndDrawQuads(renderContext);
        renderContext.curPrimProgram = null;
        if (this.displayedHUDid == 0) {
            this.touchHUDEvents.clear();
            goto Label_0635;
        }
        TouchHUDEvent touchHUDEvent = null;
        if (this.touchHUDEvents.size() >= 1) {
            touchHUDEvent = this.touchHUDEvents.remove(0);
        }
        if (renderContext.hasGL20) {
            GLES20.glClear(256);
            renderContext.initAllPrimPrograms(false);
        }
        else {
            GLES10.glClear(256);
        }
        renderContext.setActiveProjectionMatrix(renderContext.projectionHUDMatrix);
        renderContext.glModelRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        renderContext.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        final DrawableAvatar myAvatar = currentDrawList.myAvatar;
        if (myAvatar == null) {
            goto Label_0635;
        }
        myAvatar.setDisplayedHUDid(this.displayedHUDid);
        final DrawableHUD drawableHUD = myAvatar.getDrawableHUD();
        if (drawableHUD == null) {
            goto Label_0635;
        }
        final ObjectIntersectInfo draw = drawableHUD.Draw(renderContext, this.hudScaleFactor, this.hudOffsetX, this.hudOffsetY, touchHUDEvent, false);
        if (draw != null) {
            this.handleHUDTouch(renderContext, drawableHUD, touchHUDEvent, draw);
        }
        if (renderContext.hasGL20) {
            GLES20.glDisable(2929);
        }
        else {
            GLES10.glDisable(2929);
            GLES10.glMatrixMode(5889);
            GLES10.glLoadIdentity();
            GLES10.glMatrixMode(5888);
        }
        if (this.hoverTextEnableHUDs) {
            renderContext.quad.PrepareDrawQuads(renderContext);
            drawableHUD.Draw(renderContext, this.hudScaleFactor, this.hudOffsetX, this.hudOffsetY, null, true);
            renderContext.quad.EndDrawQuads(renderContext);
            goto Label_0635;
        }
        goto Label_0635;
    }
    
    public void onFinishFrame() {
        this.currentDrawList = null;
        final RenderContext renderContext = this.renderContext.get();
        if (renderContext == null) {
            return;
        }
        if ((this.previousFrameTime > this.lastDrawListUpdateTime + 100L || this.drawListUpdateFrameCount <= 0) && renderContext.drawableStore.spatialObjectIndex.updateDrawListIfNeeded()) {
            this.lastDrawListUpdateTime = this.previousFrameTime;
            this.drawListUpdateFrameCount = 4;
        }
        if (this.drawListUpdateFrameCount > 0) {
            --this.drawListUpdateFrameCount;
        }
    }
    
    public void onPrepareFrame(HeadTransformCompat headTransformCompat) {
        this.headTransformCompat = headTransformCompat;
        this.renderThreadExecutor.runQueuedTasks();
        if (!this.drawingEnabled.get()) {
            return;
        }
        Object iterator = this.renderContext.get();
        if (iterator == null) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final long n = currentTimeMillis - this.previousFrameTime;
    Label_0069:
        while (true) {
            if (n < 33L) {
                float forcedTime = 0.0f;
                int ownAvatarHidden;
                float tilt;
                Label_0252_Outer:Label_0309_Outer:Label_0392_Outer:Label_0577_Outer:Label_0640_Outer:
                while (true) {
                    while (true) {
                    Label_0875:
                        while (true) {
                        Label_0801:
                            while (true) {
                            Label_0736:
                                while (true) {
                                Label_0679:
                                    while (true) {
                                        Label_0657: {
                                            try {
                                                Thread.sleep(33L - n);
                                                this.previousFrameTime = currentTimeMillis;
                                                this.thisFrameTime = currentTimeMillis - this.firstFrameTime;
                                                ++this.fpsFrameCount;
                                                if (this.fpsFrameCount >= 10) {
                                                    this.fpsFrameCount = 0;
                                                    if (this.thisFrameTime != this.lastFrameTime) {
                                                        Debug.Printf("Renderer: FPS %.2f frame time %d", 10000.0f / (this.thisFrameTime - this.lastFrameTime), this.thisFrameTime - this.lastFrameTime);
                                                        this.lastFrameTime = this.thisFrameTime;
                                                    }
                                                }
                                                TextureMemoryTracker.releaseFrameMemory();
                                                ((RenderContext)iterator).ClearFrameKeeps();
                                                ((RenderContext)iterator).glResourceManager.Cleanup();
                                                ++((RenderContext)iterator).frameCount;
                                                if (((RenderContext)iterator).hasGL30) {
                                                    ((RenderContext)iterator).processOcclusionQueries();
                                                }
                                                ((RenderContext)iterator).glModelResetIdentity();
                                                ((RenderContext)iterator).setActiveProjectionMatrix(((RenderContext)iterator).projectionMatrix);
                                                if (this.avatarControl != null) {
                                                    if (headTransformCompat == null) {
                                                        break Label_0657;
                                                    }
                                                    this.avatarControl.getVRCamera(headTransformCompat, ((RenderContext)iterator).myAviPosition, this.cameraParams);
                                                    this.setIsFlinging(false);
                                                }
                                                ((RenderContext)iterator).frameCamera.set(this.cameraParams.getPosition());
                                                if (headTransformCompat == null) {
                                                    break Label_0679;
                                                }
                                                ((RenderContext)iterator).glModelMultMatrixf(headTransformCompat.headTransformMatrix, 0);
                                                ((RenderContext)iterator).glModelRotatef(-headTransformCompat.viewExtraYaw + 90.0f, 0.0f, 1.0f, 0.0f);
                                                ((RenderContext)iterator).glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                                                ((RenderContext)iterator).glModelTranslatef(-((RenderContext)iterator).frameCamera.x, -((RenderContext)iterator).frameCamera.y, -((RenderContext)iterator).frameCamera.z);
                                                if (((RenderContext)iterator).hasGL20) {
                                                    headTransformCompat = (HeadTransformCompat)new FrustrumInfo(((RenderContext)iterator).frameCamera.x, ((RenderContext)iterator).frameCamera.y, ((RenderContext)iterator).frameCamera.z, (float)this.drawDistance, ((RenderContext)iterator).modelViewMatrix.getMatrixData(), ((RenderContext)iterator).modelViewMatrix.getMatrixDataOffset());
                                                    if (!Objects.equal(this.currentFrustrumInfo, headTransformCompat)) {
                                                        this.currentFrustrumInfo = (FrustrumInfo)headTransformCompat;
                                                        headTransformCompat = (HeadTransformCompat)new FrustrumPlanes(this.currentFrustrumInfo.mvpMatrix);
                                                        ((RenderContext)iterator).drawableStore.spatialObjectIndex.setViewport(this.currentFrustrumInfo, (FrustrumPlanes)headTransformCompat);
                                                    }
                                                    if (!this.isResponsiveMode) {
                                                        ((RenderContext)iterator).RunLoadQueue();
                                                    }
                                                    if (this.parcelInfo != null) {
                                                        ((RenderContext)iterator).underWater = this.parcelInfo.terrainData.isUnderWater(((RenderContext)iterator).frameCamera.z);
                                                        ((RenderContext)iterator).waterTime = this.thisFrameTime % 1000000L / 1000.0f;
                                                        if (((RenderContext)iterator).hasGL20) {
                                                            forcedTime = this.forcedTime;
                                                            if (!Float.isNaN(forcedTime)) {
                                                                break Label_0801;
                                                            }
                                                            if (this.parcelInfo.getSunHour(this.simSunHour, Float.isNaN(this.simSunHour[0]))) {
                                                                Debug.Printf("Windlight: using sim hour of %f", this.simSunHour[0]);
                                                                ((RenderContext)iterator).windlightDay.InterpolatePreset(((RenderContext)iterator).windlightPreset, this.simSunHour[0]);
                                                            }
                                                        }
                                                    }
                                                    this.currentDrawList = ((RenderContext)iterator).drawableStore.spatialObjectIndex.getObjectsInFrustrum();
                                                    iterator = this.currentDrawList.avatars.iterator();
                                                    while (((Iterator)iterator).hasNext()) {
                                                        headTransformCompat = (HeadTransformCompat)((Iterator)iterator).next();
                                                        if (headTransformCompat != this.currentDrawList.myAvatar) {
                                                            break Label_0875;
                                                        }
                                                        ownAvatarHidden = (this.ownAvatarHidden ? 1 : 0);
                                                        if (ownAvatarHidden != 0) {
                                                            continue Label_0252_Outer;
                                                        }
                                                        ((DrawableAvatar)headTransformCompat).RunAnimations();
                                                    }
                                                    break;
                                                }
                                                break Label_0736;
                                            }
                                            catch (final InterruptedException ex) {
                                                continue Label_0069;
                                            }
                                        }
                                        this.setIsFlinging(this.avatarControl.getAgentAndCameraPosition(((RenderContext)iterator).myAviPosition, this.cameraParams));
                                        continue Label_0309_Outer;
                                    }
                                    ((RenderContext)iterator).glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                                    tilt = this.cameraParams.getTilt();
                                    if (tilt != 0.0f) {
                                        ((RenderContext)iterator).glModelRotatef(tilt, 1.0f, 0.0f, 0.0f);
                                    }
                                    ((RenderContext)iterator).glModelRotatef(-this.cameraParams.getHeading() + 90.0f, 0.0f, 0.0f, 1.0f);
                                    continue Label_0392_Outer;
                                }
                                headTransformCompat = (HeadTransformCompat)new FrustrumInfo(((RenderContext)iterator).frameCamera.x, ((RenderContext)iterator).frameCamera.y, ((RenderContext)iterator).frameCamera.z, (float)this.drawDistance, ((RenderContext)iterator).modelViewMatrix.getMatrixData(), ((RenderContext)iterator).modelViewMatrix.getMatrixDataOffset(), ((RenderContext)iterator).projectionMatrix.getMatrixData(), ((RenderContext)iterator).projectionMatrix.getMatrixDataOffset());
                                continue Label_0577_Outer;
                            }
                            if (Float.isNaN(this.simSunHour[0]) || this.simSunHour[0] != forcedTime) {
                                this.simSunHour[0] = forcedTime;
                                Debug.Printf("Windlight: using forced hour of %f", this.simSunHour[0]);
                                ((RenderContext)iterator).windlightDay.InterpolatePreset(((RenderContext)iterator).windlightPreset, this.simSunHour[0]);
                                continue Label_0640_Outer;
                            }
                            continue Label_0640_Outer;
                        }
                        ownAvatarHidden = 0;
                        continue;
                    }
                }
                this.processObjectPick();
                return;
            }
            continue Label_0069;
        }
    }
    
    public void onRendererShutdown() {
        PrimComputeExecutor.getInstance().resume();
        SpatialIndex.getInstance().DisableObjectIndex(this);
        final RenderContext renderContext = this.renderContext.getAndSet(null);
        if (renderContext != null) {
            renderContext.StopLoadQueue();
            renderContext.ClearFrameKeeps();
            Debug.Printf("EGL: destroyContext: calling Flush().", new Object[0]);
            renderContext.glResourceManager.Flush();
            Debug.Printf("EGL: destroyContext: returned from Flush().", new Object[0]);
        }
        TextureMemoryTracker.releaseAllFrameMemory();
        Debug.Printf("EGL: destroyContext: calling eglDestroyContext ().", new Object[0]);
        this.initialUpdateDone = false;
        TextureMemoryTracker.clearActiveRenderer(this);
    }
    
    public void onSurfaceChanged(final GL10 gl10, final int n, final int n2) {
        final RenderContext renderContext = this.renderContext.get();
        if (renderContext == null) {
            return;
        }
        renderContext.viewportRect[0] = 0;
        renderContext.viewportRect[1] = 0;
        renderContext.viewportRect[2] = n;
        renderContext.viewportRect[3] = n2;
        if (renderContext.hasGL20) {
            GLES20.glViewport(0, 0, n, n2);
            if (renderContext.useFXAA) {
                this.systemFramebuffer[0] = 0;
                GLES20.glGetIntegerv(36006, this.systemFramebuffer, 0);
                GLES20.glBindFramebuffer(36160, this.Framebuffers[0]);
                if (this.Renderbuffers != null) {
                    GLES20.glDeleteRenderbuffers(1, this.Renderbuffers, 0);
                    this.Renderbuffers = null;
                }
                if (this.Colorbuffers != null) {
                    GLES20.glDeleteTextures(this.Colorbuffers.length, this.Colorbuffers, 0);
                    this.Colorbuffers = null;
                }
                if (this.Renderbuffers == null) {
                    GLES20.glGenRenderbuffers(1, this.Renderbuffers = new int[1], 0);
                    GLES20.glBindRenderbuffer(36161, this.Renderbuffers[0]);
                    GLES20.glRenderbufferStorage(36161, 33189, n, n2);
                }
                if (this.Colorbuffers == null) {
                    this.Colorbuffers = new int[2];
                    GLES20.glGenTextures(this.Colorbuffers.length, this.Colorbuffers, 0);
                    for (int i = 0; i < this.Colorbuffers.length; ++i) {
                        GLES20.glBindTexture(3553, this.Colorbuffers[i]);
                        if (i == 0) {
                            GLES20.glTexImage2D(3553, 0, 6407, n, n2, 0, 6407, 33635, (Buffer)null);
                        }
                        else {
                            GLES20.glTexImage2D(3553, 0, 6408, n, n2, 0, 6408, 5121, (Buffer)null);
                        }
                        GLES20.glTexParameteri(3553, 10242, 33071);
                        GLES20.glTexParameteri(3553, 10243, 33071);
                        GLES20.glTexParameteri(3553, 10240, 9729);
                        GLES20.glTexParameteri(3553, 10241, 9729);
                    }
                }
                GLES20.glViewport(0, 0, n, n2);
                GLES20.glBindRenderbuffer(36161, this.Renderbuffers[0]);
                GLES20.glBindTexture(3553, this.Colorbuffers[0]);
                GLES20.glFramebufferRenderbuffer(36160, 36096, 36161, this.Renderbuffers[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.Colorbuffers[0], 0);
                GLES20.glUseProgram(renderContext.fxaaProgram.getHandle());
                GLES20.glUniform1i(renderContext.fxaaProgram.textureSampler, 0);
                GLES20.glUniform1i(renderContext.fxaaProgram.noAAtextureSampler, 1);
                GLES20.glUniform2f(renderContext.fxaaProgram.texcoordOffset, 1.0f / n, 1.0f / n2);
                final float[] array = new float[16];
                android.opengl.Matrix.setIdentityM(array, 0);
                android.opengl.Matrix.scaleM(array, 0, 2.0f, 2.0f, 1.0f);
                GLES20.glUniformMatrix4fv(renderContext.fxaaProgram.uMVPMatrix, 1, false, array, 0);
                GLES20.glBindFramebuffer(36160, this.systemFramebuffer[0]);
            }
        }
        else {
            GLES10.glViewport(0, 0, n, n2);
            GLES10.glMatrixMode(5889);
            GLES10.glLoadIdentity();
        }
        renderContext.aspectRatio = n / (float)n2;
        renderContext.FOVAngle = 60.0f;
        final float n3 = (float)Math.tan(renderContext.FOVAngle * 3.141592653589793 / 360.0);
        renderContext.getClass();
        final float n4 = n3 * 0.5f;
        final float n5 = (float)this.drawDistance;
        Debug.Log("Renderer: Using drawDistance = " + this.drawDistance);
        renderContext.projectionMatrix.reset();
        final MatrixStack projectionMatrix = renderContext.projectionMatrix;
        final float n6 = -renderContext.aspectRatio;
        final float aspectRatio = renderContext.aspectRatio;
        final float n7 = -n4;
        renderContext.getClass();
        projectionMatrix.glFrustumf(n6 * n4, aspectRatio * n4, n7, n4, 0.5f, n5);
        renderContext.drawDistance = (float)this.drawDistance;
        renderContext.projectionHUDMatrix.reset();
        renderContext.projectionHUDMatrix.glOrthof(-renderContext.aspectRatio * 1.0f, renderContext.aspectRatio * 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        if (renderContext.windlightSky != null) {
            renderContext.windlightSky.updateMatrix(renderContext);
        }
        this.firstFrameTime = System.currentTimeMillis();
    }
    
    public void onSurfaceCreated(final GL10 gl10, final EGLConfig eglConfig) {
        this.onSurfaceCreated(gl10, eglConfig, false);
    }
    
    public void onSurfaceCreated(final GL10 gl10, final EGLConfig eglConfig, final boolean b) {
        TextureMemoryTracker.setActiveRenderer(this);
        this.drawingEnabled.set(true);
        this.firstFrameCount.set(1);
        PrimComputeExecutor.getInstance().resume();
        final String glGetString = GLES10.glGetString(7936);
        final String glGetString2 = GLES10.glGetString(7937);
        final String glGetString3 = GLES10.glGetString(7938);
        final String glGetString4 = GLES10.glGetString(7939);
        if (this.createdGL30) {
            final int[] array = { 0 };
            GLES30.glGetIntegerv(33307, array, 0);
            Debug.Printf("Renderer: Reported major version: %d", array[0]);
            if (array[0] < 3) {
                this.createdGL30 = false;
            }
        }
        Debug.Printf("Renderer: glVendor '%s', glRenderer '%s'", glGetString, glGetString2);
        Debug.Log("Renderer: version = '" + glGetString3 + "', extensions = '" + glGetString4 + "', thread id = " + Thread.currentThread().getId());
        boolean createdGL30 = this.createdGL30;
        boolean createdGL31 = this.createdGL30;
        final String[] split = glGetString4.split(" ");
        for (int length = split.length, i = 0; i < length; ++i) {
            if (split[i].equals("GL_ARB_vertex_buffer_object")) {
                createdGL30 = true;
            }
        }
        if (glGetString3.contains("1.1")) {
            createdGL31 = true;
            createdGL30 = true;
        }
        Debug.Printf("Renderer: VBO support %s, GL11 %s, GL30 %s", createdGL30, createdGL31, this.createdGL30);
        final RenderContext newValue = new RenderContext(eglConfig, glGetString2, this.createdGL30, this.requestGL20, createdGL31, createdGL30, this.avatarCountLimit, GlobalOptions.getInstance().getTerrainTextures(), this.fontSize, b, this);
        Debug.AlwaysPrintf("Renderer: created context, GL30 %b, GL20 %b", newValue.hasGL30, newValue.hasGL20);
        if (newValue.hasGL20) {
            if (newValue.getShaderCompileErrors()) {
                Debug.Printf("Renderer: Shaders did not compile well.", new Object[0]);
                if (this.stateHandler != null) {
                    this.stateHandler.sendEmptyMessage(4);
                }
                this.drawingEnabled.set(false);
            }
            Debug.Printf("Renderer: Basic geometry program = %d", newValue.primProgram.getHandle());
            GLES20.glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
            if (newValue.useFXAA) {
                GLES20.glGenFramebuffers(1, this.Framebuffers = new int[1], 0);
            }
            else {
                this.Framebuffers = null;
            }
            this.Renderbuffers = null;
            this.Colorbuffers = null;
        }
        else {
            GLES10.glEnableClientState(32884);
            GLES10.glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
        }
        this.renderContext.set(newValue);
        this.hoverTextEnableHUDs = GlobalOptions.getInstance().getHoverTextEnableHUDs();
        this.hoverTextEnableObjects = GlobalOptions.getInstance().getHoverTextEnableObjects();
        if (this.stateHandler != null) {
            this.stateHandler.sendEmptyMessage(3);
        }
        PrimComputeExecutor.getInstance().execute(this.initSpatialIndexRunnable);
        this.firstFrameTime = System.currentTimeMillis();
    }
    
    public void pickObject(final float needPickX, final float needPickY, final Handler pickHandler) {
        synchronized (this.pickLock) {
            this.needPickX = needPickX;
            this.needPickY = needPickY;
            this.needPickObject = true;
            this.pickHandler = pickHandler;
        }
    }
    
    public void requestScreenshot(final Handler screenshotHandler) {
        this.screenshotHandler = screenshotHandler;
    }
    
    public void setAvatarCountLimit(final int n) {
        this.avatarCountLimit = n;
        SpatialIndex.getInstance().setAvatarCountLimit(n);
    }
    
    public void setDisplayedHUDid(final int displayedHUDid) {
        this.displayedHUDid = displayedHUDid;
    }
    
    public void setDrawDistance(final int drawDistance) {
        this.drawDistance = drawDistance;
        final RenderContext renderContext = this.renderContext.get();
        if (renderContext != null) {
            renderContext.drawDistance = (float)drawDistance;
        }
    }
    
    public void setDrawPickedObject(final SLObjectInfo drawPickedObject) {
        this.drawPickedObject = drawPickedObject;
    }
    
    public void setForcedTime(final boolean b, final float forcedTime) {
        if (b) {
            this.forcedTime = forcedTime;
        }
        else {
            this.forcedTime = Float.NaN;
        }
    }
    
    public void setHUDOffset(final float hudOffsetX, final float hudOffsetY) {
        this.hudOffsetX = hudOffsetX;
        this.hudOffsetY = hudOffsetY;
    }
    
    public void setHUDScaleFactor(final float hudScaleFactor) {
        this.hudScaleFactor = hudScaleFactor;
    }
    
    public void setIsInteracting(final boolean isInteracting) {
        boolean b = false;
        synchronized (this.responsiveModeLock) {
            if (this.isInteracting != isInteracting) {
                this.isInteracting = isInteracting;
                b = true;
            }
            monitorexit(this.responsiveModeLock);
            if (b) {
                this.updateResponsive();
            }
        }
    }
    
    public void setOwnAvatarHidden(final boolean ownAvatarHidden) {
        this.ownAvatarHidden = ownAvatarHidden;
    }
    
    public void touchHUD(final float n, final float n2, final Handler touchHandler) {
        this.touchHandler = touchHandler;
        if (this.displayedHUDid != 0) {
            this.touchHUDEvents.add(new TouchHUDEvent(n, n2));
        }
    }
}
