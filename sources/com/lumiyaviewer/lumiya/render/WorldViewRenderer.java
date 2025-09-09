package com.lumiyaviewer.lumiya.render;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableHUD;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawList;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumPlanes;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.Buffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint({"InlinedApi"})
public class WorldViewRenderer implements GLSurfaceView.Renderer, GLSurfaceView.EGLContextFactory {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EMPTY_FRAMES_COUNT = 1;
    private static final int EXT_TEXTURE_MATRIX = 0;
    private static final int EXT_TEXTURE_MATRIX_EYE = 16;
    private static final int EXT_TEXTURE_MATRIX_INVERSE = 48;
    private static final int EXT_TEXTURE_MATRIX_RESULT = 32;
    private static final int MIN_DRAW_LIST_UPDATE_FRAMES = 4;
    private static final long MIN_DRAW_LIST_UPDATE_INTERVAL = 100;
    public static final int MSG_SCREENSHOT = 5;
    public static final int MSG_SET_PICKED_OBJECT = 1;
    public static final int MSG_SET_TOUCHED_OBJECT = 2;
    public static final int MSG_SHADER_COMPILE_ERROR = 4;
    public static final int MSG_SURFACE_CREATED = 3;
    private int[] Colorbuffers = null;
    private int[] Framebuffers = null;
    private int[] Renderbuffers = null;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(this.renderThreadExecutor, new $Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA(this));
    @Nullable
    private SLAvatarControl avatarControl = null;
    private int avatarCountLimit = 5;
    private final CameraParams cameraParams = new CameraParams();
    private boolean createdGL30 = false;
    private DrawList currentDrawList = null;
    @Nullable
    private FrustrumInfo currentFrustrumInfo;
    private volatile int displayedHUDid = 0;
    private int drawDistance = 20;
    private int drawListUpdateFrameCount = 0;
    private SLObjectInfo drawPickedObject = null;
    private AtomicBoolean drawingEnabled = new AtomicBoolean(true);
    private final float[] extTextureHitVector = {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] extTextureMatrix = new float[64];
    private final float[] extTextureResultVector = new float[4];
    private AtomicInteger firstFrameCount = new AtomicInteger(1);
    private long firstFrameTime;
    private final int fontSize;
    private volatile float forcedTime = Float.NaN;
    private int fpsFrameCount = 0;
    private HeadTransformCompat headTransformCompat = null;
    private boolean hoverTextEnableHUDs = true;
    private boolean hoverTextEnableObjects = false;
    private float hudOffsetX = 0.0f;
    private float hudOffsetY = 0.0f;
    private float hudScaleFactor = 1.0f;
    private final Runnable initSpatialIndexRunnable = new Runnable(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f47$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.render.-$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA.1.$m$0():void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.render.-$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA.1.$m$0():void, class status: UNLOADED
        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
        	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
        	at java.util.ArrayList.forEach(ArrayList.java:1259)
        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

        public final void run(
/*
Method generation error in method: com.lumiyaviewer.lumiya.render.-$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA.1.run():void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.render.-$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA.1.run():void, class status: UNLOADED
        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
        	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
        	at java.util.ArrayList.forEach(ArrayList.java:1259)
        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/
    };
    private volatile boolean initialUpdateDone = false;
    private boolean isFlinging = false;
    private boolean isInteracting = false;
    private volatile boolean isResponsiveMode = false;
    private long lastDrawListUpdateTime = 0;
    private long lastFrameTime;
    private boolean needPickObject = false;
    private float needPickX = 0.0f;
    private float needPickY = 0.0f;
    private boolean ownAvatarHidden = false;
    @Nullable
    private SLParcelInfo parcelInfo = null;
    private Handler pickHandler = null;
    private final Object pickLock = new Object();
    private long previousFrameTime = 0;
    private final AtomicReference<RenderContext> renderContext = new AtomicReference<>();
    private final SynchronousExecutor renderThreadExecutor = new SynchronousExecutor();
    private final boolean requestGL20;
    private final Object responsiveModeLock = new Object();
    private Handler screenshotHandler = null;
    private float[] simSunHour = {Float.NaN};
    private final Handler stateHandler;
    private final int[] systemFramebuffer = new int[1];
    private long thisFrameTime = 0;
    private List<TouchHUDEvent> touchHUDEvents = Collections.synchronizedList(new LinkedList());
    private Handler touchHandler = null;

    public WorldViewRenderer(Handler handler, boolean z, @Nonnull UserManager userManager, int i) {
        this.stateHandler = handler;
        this.requestGL20 = z;
        this.fontSize = i;
        this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
    }

    private void handleHUDTouch(RenderContext renderContext2, DrawableHUD drawableHUD, TouchHUDEvent touchHUDEvent, ObjectIntersectInfo objectIntersectInfo) {
        float f = (float) renderContext2.viewportRect[2];
        float f2 = ((f / 2.0f) - touchHUDEvent.y) / f;
        float f3 = ((((float) renderContext2.viewportRect[2]) / 2.0f) - touchHUDEvent.x) / f;
        SLAttachmentPoint attachmentPoint = drawableHUD.getAttachmentPoint();
        float f4 = attachmentPoint.position.y + f3;
        float f5 = f2 + attachmentPoint.position.z;
        try {
            Handler handler = this.touchHandler;
            if (handler != null) {
                handler.sendMessage(handler.obtainMessage(2, objectIntersectInfo.objInfo));
            }
            this.agentCircuit.get().TouchObjectFace(objectIntersectInfo.objInfo, objectIntersectInfo.intersectInfo.faceID, 0.0f, f4, f5, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m49com_lumiyaviewer_lumiya_render_WorldViewRenderermthref0(SLAgentCircuit sLAgentCircuit) {
        if (sLAgentCircuit != null) {
            Debug.Printf("WorldViewRenderer: got new agentCircuit.", new Object[0]);
            this.initialUpdateDone = false;
            this.avatarControl = sLAgentCircuit.getModules().avatarControl;
            this.parcelInfo = sLAgentCircuit.getGridConnection().parcelInfo;
            this.initialUpdateDone = false;
            RenderContext renderContext2 = this.renderContext.get();
            if (renderContext2 != null) {
                renderContext2.setMeshCapURL(sLAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.GetMesh));
                if (this.parcelInfo != null) {
                    PrimComputeExecutor.getInstance().execute(this.initSpatialIndexRunnable);
                    return;
                }
                return;
            }
            return;
        }
        this.avatarControl = null;
        this.parcelInfo = null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0063 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processObjectPick() {
        /*
            r9 = this;
            r7 = 1
            r0 = 0
            r3 = 2143289344(0x7fc00000, float:NaN)
            r4 = 0
            java.lang.Object r1 = r9.pickLock
            monitor-enter(r1)
            boolean r2 = r9.needPickObject     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x006f
            r0 = 0
            r9.needPickObject = r0     // Catch:{ all -> 0x003f }
            float r2 = r9.needPickX     // Catch:{ all -> 0x003f }
            float r3 = r9.needPickY     // Catch:{ all -> 0x003f }
            android.os.Handler r0 = r9.pickHandler     // Catch:{ all -> 0x003f }
            r6 = r0
            r0 = r7
        L_0x0017:
            monitor-exit(r1)
            if (r0 == 0) goto L_0x006c
            java.util.concurrent.atomic.AtomicReference<com.lumiyaviewer.lumiya.render.RenderContext> r0 = r9.renderContext
            java.lang.Object r1 = r0.get()
            com.lumiyaviewer.lumiya.render.RenderContext r1 = (com.lumiyaviewer.lumiya.render.RenderContext) r1
            if (r1 == 0) goto L_0x006c
            com.lumiyaviewer.lumiya.render.spatial.DrawList r0 = r9.currentDrawList     // Catch:{ Exception -> 0x005c }
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.DrawableObject> r0 = r0.objects     // Catch:{ Exception -> 0x005c }
            java.util.Iterator r8 = r0.iterator()     // Catch:{ Exception -> 0x005c }
            r5 = r4
        L_0x002d:
            boolean r0 = r8.hasNext()     // Catch:{ Exception -> 0x006d }
            if (r0 == 0) goto L_0x0042
            java.lang.Object r4 = r8.next()     // Catch:{ Exception -> 0x006d }
            com.lumiyaviewer.lumiya.render.DrawableObject r4 = (com.lumiyaviewer.lumiya.render.DrawableObject) r4     // Catch:{ Exception -> 0x006d }
            r0 = r9
            com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo r5 = r0.tryPickObject(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x006d }
            goto L_0x002d
        L_0x003f:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        L_0x0042:
            com.lumiyaviewer.lumiya.render.spatial.DrawList r0 = r9.currentDrawList     // Catch:{ Exception -> 0x006d }
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r0 = r0.avatars     // Catch:{ Exception -> 0x006d }
            java.util.Iterator r8 = r0.iterator()     // Catch:{ Exception -> 0x006d }
        L_0x004a:
            boolean r0 = r8.hasNext()     // Catch:{ Exception -> 0x006d }
            if (r0 == 0) goto L_0x0061
            java.lang.Object r4 = r8.next()     // Catch:{ Exception -> 0x006d }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r4 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar) r4     // Catch:{ Exception -> 0x006d }
            r0 = r9
            com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo r5 = r0.tryPickObject(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x006d }
            goto L_0x004a
        L_0x005c:
            r0 = move-exception
            r5 = r4
        L_0x005e:
            com.lumiyaviewer.lumiya.Debug.Warning(r0)
        L_0x0061:
            if (r5 == 0) goto L_0x006c
            if (r6 == 0) goto L_0x006c
            android.os.Message r0 = r6.obtainMessage(r7, r5)
            r6.sendMessage(r0)
        L_0x006c:
            return
        L_0x006d:
            r0 = move-exception
            goto L_0x005e
        L_0x006f:
            r6 = r4
            r2 = r3
            goto L_0x0017
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.WorldViewRenderer.processObjectPick():void");
    }

    private void setIsFlinging(boolean z) {
        boolean z2 = false;
        synchronized (this.responsiveModeLock) {
            if (this.isFlinging != z) {
                this.isFlinging = z;
                z2 = true;
            }
        }
        if (z2) {
            updateResponsive();
        }
    }

    private void takeScreenshot(RenderContext renderContext2, Handler handler) {
        int i = renderContext2.viewportRect[2];
        int i2 = renderContext2.viewportRect[3];
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(i * i2 * 4);
        GLES10.glReadPixels(0, 0, i, i2, 6408, 5121, directByteBuffer.asByteBuffer());
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        createBitmap.copyPixelsFromBuffer(directByteBuffer.asByteBuffer());
        Bitmap createBitmap2 = Bitmap.createBitmap(i, i2, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(createBitmap2);
        Matrix matrix = new Matrix();
        matrix.setScale(1.0f, -1.0f);
        matrix.postTranslate(0.0f, (float) i2);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(createBitmap, matrix, paint);
        createBitmap.recycle();
        paint.setTextSize((float) this.fontSize);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(-1);
        float f = ((float) this.fontSize) * 2.0f;
        for (String drawText : new String[]{"Lumiya Viewer", "http://lumiyaviewer.com"}) {
            canvas.drawText(drawText, (float) this.fontSize, f, paint);
            f = f + (paint.descent() - paint.ascent()) + (((float) this.fontSize) * 0.5f);
        }
        handler.sendMessage(handler.obtainMessage(5, createBitmap2));
    }

    private ObjectIntersectInfo tryPickObject(RenderContext renderContext2, float f, float f2, IntersectPickable intersectPickable, ObjectIntersectInfo objectIntersectInfo) {
        ObjectIntersectInfo PickObject = intersectPickable.PickObject(renderContext2, f, f2, -0.9f);
        return (PickObject == null || (objectIntersectInfo != null && PickObject.pickDepth >= objectIntersectInfo.pickDepth)) ? objectIntersectInfo : PickObject;
    }

    private void updateResponsive() {
        boolean z;
        boolean z2;
        synchronized (this.responsiveModeLock) {
            z = this.isResponsiveMode;
            z2 = !this.isInteracting ? this.isFlinging : true;
            this.isResponsiveMode = z2;
        }
        if (z == z2) {
            return;
        }
        if (z2) {
            PrimComputeExecutor.getInstance().pause();
        } else {
            PrimComputeExecutor.getInstance().resume();
        }
    }

    public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        Debug.Printf("EGL: createContext called.", new Object[0]);
        if (this.requestGL20 && Build.VERSION.SDK_INT >= 18) {
            Debug.Printf("EGL: trying to create 3.0 context.", new Object[0]);
            EGLContext eglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{EGL_CONTEXT_CLIENT_VERSION, 3, 12344});
            if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("EGL: Failed to create 3.0 context.", new Object[0]);
            } else {
                Debug.Printf("EGL: 3.0 context apparently created.", new Object[0]);
                this.createdGL30 = true;
                return eglCreateContext;
            }
        }
        Debug.Printf("EGL: Creating regular context.", new Object[0]);
        this.createdGL30 = false;
        int[] iArr = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
        EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
        if (!this.requestGL20) {
            iArr = null;
        }
        return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
    }

    @SuppressLint({"DefaultLocale"})
    public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
        Debug.Printf("EGL: destroyContext called.", new Object[0]);
        onRendererShutdown();
        if (!egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
            throw new RuntimeException(String.format("EGLError code %d", new Object[]{Integer.valueOf(egl10.eglGetError())}));
        } else {
            Debug.Printf("EGL: destroyContext exiting.", new Object[0]);
        }
    }

    public void disableDrawing() {
        this.drawingEnabled.set(false);
    }

    public void drawCrosshair(float f, float f2) {
        RenderContext renderContext2 = this.renderContext.get();
        if (renderContext2 != null && renderContext2.crosshairTexture != null) {
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glEnable(3042);
            GLES20.glUseProgram(renderContext2.rawShaderProgram.getHandle());
            GLES20.glActiveTexture(33984);
            renderContext2.crosshairTexture.GLDraw();
            GLES20.glUniform1i(renderContext2.rawShaderProgram.textureSampler, 0);
            android.opengl.Matrix.setIdentityM(this.extTextureMatrix, 0);
            GLES20.glUniformMatrix4fv(renderContext2.rawShaderProgram.vTextureTransformMatrix, 1, false, this.extTextureMatrix, 0);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 0, 0.0f, 0.0f, -1.9f);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 16, this.extTextureMatrix, 0, -f2, 0.0f, 0.0f);
            android.opengl.Matrix.scaleM(this.extTextureMatrix, 16, f, f, 1.0f);
            android.opengl.Matrix.multiplyMM(this.extTextureMatrix, 32, renderContext2.projectionMatrix.getMatrixData(), renderContext2.projectionMatrix.getMatrixDataOffset(), this.extTextureMatrix, 16);
            GLES20.glUniformMatrix4fv(renderContext2.rawShaderProgram.uMVPMatrix, 1, false, this.extTextureMatrix, 32);
            renderContext2.quad.DrawSingleQuadShader(renderContext2, renderContext2.rawShaderProgram.vPosition, renderContext2.rawShaderProgram.vTexCoord);
        }
    }

    public void drawExternalTexture(@Nonnull GLExternalTexture gLExternalTexture, float[] fArr, float f, float f2, float f3, float f4, float f5, float[] fArr2, int i) {
        RenderContext renderContext2 = this.renderContext.get();
        if (renderContext2 != null && renderContext2.extTextureProgram != null) {
            gLExternalTexture.update(fArr);
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glEnable(3042);
            GLES20.glUseProgram(renderContext2.extTextureProgram.getHandle());
            GLES20.glActiveTexture(33984);
            gLExternalTexture.bind();
            GLES20.glUniform1i(renderContext2.extTextureProgram.textureSampler, 0);
            GLES20.glUniformMatrix4fv(renderContext2.extTextureProgram.vTextureTransformMatrix, 1, false, fArr, 0);
            android.opengl.Matrix.setIdentityM(this.extTextureMatrix, 0);
            android.opengl.Matrix.rotateM(this.extTextureMatrix, 0, -f2, 1.0f, 0.0f, 0.0f);
            android.opengl.Matrix.rotateM(this.extTextureMatrix, 0, -f3, 0.0f, 1.0f, 0.0f);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 0, 0.0f, 0.0f, -2.0f);
            android.opengl.Matrix.translateM(this.extTextureMatrix, 16, this.extTextureMatrix, 0, -f, 0.0f, 0.0f);
            android.opengl.Matrix.scaleM(this.extTextureMatrix, 16, f4, f5, 1.0f);
            android.opengl.Matrix.multiplyMM(this.extTextureMatrix, 32, renderContext2.projectionMatrix.getMatrixData(), renderContext2.projectionMatrix.getMatrixDataOffset(), this.extTextureMatrix, 16);
            android.opengl.Matrix.invertM(this.extTextureMatrix, 48, this.extTextureMatrix, 32);
            android.opengl.Matrix.multiplyMV(this.extTextureResultVector, 0, this.extTextureMatrix, 48, this.extTextureHitVector, 0);
            fArr2[i] = this.extTextureResultVector[0];
            fArr2[i + 1] = this.extTextureResultVector[1];
            GLES20.glUniformMatrix4fv(renderContext2.extTextureProgram.uMVPMatrix, 1, false, this.extTextureMatrix, 32);
            renderContext2.quad.DrawSingleQuadShader(renderContext2, renderContext2.extTextureProgram.vPosition, renderContext2.extTextureProgram.vTexCoord);
        }
    }

    public void enableDrawing() {
        if (!this.drawingEnabled.getAndSet(true)) {
            this.firstFrameCount.set(1);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_render_WorldViewRenderer_6642  reason: not valid java name */
    public /* synthetic */ void m50lambda$com_lumiyaviewer_lumiya_render_WorldViewRenderer_6642() {
        SLParcelInfo sLParcelInfo;
        if (!this.initialUpdateDone && (sLParcelInfo = this.parcelInfo) != null) {
            Debug.Printf("WorldViewRenderer: making new spatial index.", new Object[0]);
            sLParcelInfo.initSpatialIndex();
            sLParcelInfo.terrainData.updateEntireTerrain();
            RenderContext renderContext2 = this.renderContext.get();
            if (renderContext2 != null) {
                renderContext2.drawableStore.spatialObjectIndex.completeInitialUpdate();
            }
            this.initialUpdateDone = true;
        }
    }

    public void onDrawFrame(GL10 gl10) {
        onPrepareFrame((HeadTransformCompat) null);
        onDrawFrame(gl10, (HeadTransformCompat) null, (float[]) null, (int[]) null, (float[]) null, (float[]) null, 0);
        onFinishFrame();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:83:0x023c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onDrawFrame(javax.microedition.khronos.opengles.GL10 r9, @javax.annotation.Nullable com.lumiyaviewer.lumiya.render.HeadTransformCompat r10, @javax.annotation.Nullable float[] r11, @javax.annotation.Nullable int[] r12, @javax.annotation.Nullable float[] r13, @javax.annotation.Nullable float[] r14, int r15) {
        /*
            r8 = this;
            monitor-enter(r8)
            java.util.concurrent.atomic.AtomicBoolean r0 = r8.drawingEnabled     // Catch:{ all -> 0x00f3 }
            boolean r0 = r0.get()     // Catch:{ all -> 0x00f3 }
            if (r0 != 0) goto L_0x000b
            monitor-exit(r8)
            return
        L_0x000b:
            java.util.concurrent.atomic.AtomicReference<com.lumiyaviewer.lumiya.render.RenderContext> r0 = r8.renderContext     // Catch:{ all -> 0x00f3 }
            java.lang.Object r1 = r0.get()     // Catch:{ all -> 0x00f3 }
            com.lumiyaviewer.lumiya.render.RenderContext r1 = (com.lumiyaviewer.lumiya.render.RenderContext) r1     // Catch:{ all -> 0x00f3 }
            if (r1 != 0) goto L_0x0017
            monitor-exit(r8)
            return
        L_0x0017:
            if (r13 == 0) goto L_0x004f
            if (r10 == 0) goto L_0x004f
            r1.glModelResetIdentity()     // Catch:{ all -> 0x00f3 }
            if (r14 == 0) goto L_0x00ec
            r1.setActiveProjectionMatrix(r14, r15)     // Catch:{ all -> 0x00f3 }
        L_0x0023:
            r0 = 0
            r1.glModelMultMatrixf(r13, r0)     // Catch:{ all -> 0x00f3 }
            float r0 = r10.viewExtraYaw     // Catch:{ all -> 0x00f3 }
            float r0 = -r0
            r2 = 1119092736(0x42b40000, float:90.0)
            float r0 = r0 + r2
            r2 = 0
            r3 = 1065353216(0x3f800000, float:1.0)
            r4 = 0
            r1.glModelRotatef(r0, r2, r3, r4)     // Catch:{ all -> 0x00f3 }
            r0 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r2 = 1065353216(0x3f800000, float:1.0)
            r3 = 0
            r4 = 0
            r1.glModelRotatef(r0, r2, r3, r4)     // Catch:{ all -> 0x00f3 }
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r0 = r1.frameCamera     // Catch:{ all -> 0x00f3 }
            float r0 = r0.x     // Catch:{ all -> 0x00f3 }
            float r0 = -r0
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r2 = r1.frameCamera     // Catch:{ all -> 0x00f3 }
            float r2 = r2.y     // Catch:{ all -> 0x00f3 }
            float r2 = -r2
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r3 = r1.frameCamera     // Catch:{ all -> 0x00f3 }
            float r3 = r3.z     // Catch:{ all -> 0x00f3 }
            float r3 = -r3
            r1.glModelTranslatef(r0, r2, r3)     // Catch:{ all -> 0x00f3 }
        L_0x004f:
            boolean r0 = r1.hasGL20     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x0108
            boolean r0 = r1.useFXAA     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x00f6
            int[] r0 = r8.Framebuffers     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x00f6
            int[] r0 = r8.Colorbuffers     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x00f6
            int[] r0 = r8.systemFramebuffer     // Catch:{ all -> 0x00f3 }
            r2 = 0
            r3 = 0
            r0[r3] = r2     // Catch:{ all -> 0x00f3 }
            int[] r0 = r8.systemFramebuffer     // Catch:{ all -> 0x00f3 }
            r2 = 36006(0x8ca6, float:5.0455E-41)
            r3 = 0
            android.opengl.GLES20.glGetIntegerv(r2, r0, r3)     // Catch:{ all -> 0x00f3 }
            int[] r0 = r8.Framebuffers     // Catch:{ all -> 0x00f3 }
            r2 = 0
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            r2 = 36160(0x8d40, float:5.0671E-41)
            android.opengl.GLES20.glBindFramebuffer(r2, r0)     // Catch:{ all -> 0x00f3 }
            int[] r0 = r8.Colorbuffers     // Catch:{ all -> 0x00f3 }
            r2 = 0
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            r2 = 3553(0xde1, float:4.979E-42)
            android.opengl.GLES20.glBindTexture(r2, r0)     // Catch:{ all -> 0x00f3 }
            int[] r0 = r8.Colorbuffers     // Catch:{ all -> 0x00f3 }
            r2 = 0
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            r2 = 36160(0x8d40, float:5.0671E-41)
            r3 = 36064(0x8ce0, float:5.0536E-41)
            r4 = 3553(0xde1, float:4.979E-42)
            r5 = 0
            android.opengl.GLES20.glFramebufferTexture2D(r2, r3, r4, r0, r5)     // Catch:{ all -> 0x00f3 }
            if (r12 == 0) goto L_0x00a5
            int[] r0 = r1.viewportRect     // Catch:{ all -> 0x00f3 }
            r2 = 2
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            int[] r2 = r1.viewportRect     // Catch:{ all -> 0x00f3 }
            r3 = 3
            r2 = r2[r3]     // Catch:{ all -> 0x00f3 }
            r3 = 0
            r4 = 0
            android.opengl.GLES20.glViewport(r3, r4, r0, r2)     // Catch:{ all -> 0x00f3 }
        L_0x00a5:
            r0 = 2884(0xb44, float:4.041E-42)
            android.opengl.GLES20.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 2929(0xb71, float:4.104E-42)
            android.opengl.GLES20.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES20.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 770(0x302, float:1.079E-42)
            r2 = 771(0x303, float:1.08E-42)
            android.opengl.GLES20.glBlendFunc(r0, r2)     // Catch:{ all -> 0x00f3 }
            r0 = 256(0x100, float:3.59E-43)
            android.opengl.GLES20.glClear(r0)     // Catch:{ all -> 0x00f3 }
        L_0x00c0:
            r0 = 1
            java.util.concurrent.atomic.AtomicInteger r2 = r8.firstFrameCount     // Catch:{ all -> 0x00f3 }
            int r2 = r2.get()     // Catch:{ all -> 0x00f3 }
            if (r2 <= 0) goto L_0x00d7
            java.lang.String r0 = "onDrawFrame: drawing empty first frame!"
            com.lumiyaviewer.lumiya.Debug.Log(r0)     // Catch:{ all -> 0x00f3 }
            java.util.concurrent.atomic.AtomicInteger r0 = r8.firstFrameCount     // Catch:{ all -> 0x00f3 }
            int r2 = r2 + -1
            r0.set(r2)     // Catch:{ all -> 0x00f3 }
            r0 = 0
        L_0x00d7:
            com.lumiyaviewer.lumiya.slproto.SLParcelInfo r2 = r8.parcelInfo     // Catch:{ all -> 0x00f3 }
            if (r2 == 0) goto L_0x00df
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x014c
        L_0x00df:
            int[] r0 = r8.systemFramebuffer     // Catch:{ all -> 0x00f3 }
            r1 = 0
            r0 = r0[r1]     // Catch:{ all -> 0x00f3 }
            r1 = 36160(0x8d40, float:5.0671E-41)
            android.opengl.GLES20.glBindFramebuffer(r1, r0)     // Catch:{ all -> 0x00f3 }
            monitor-exit(r8)
            return
        L_0x00ec:
            com.lumiyaviewer.lumiya.render.MatrixStack r0 = r1.projectionMatrix     // Catch:{ all -> 0x00f3 }
            r1.setActiveProjectionMatrix(r0)     // Catch:{ all -> 0x00f3 }
            goto L_0x0023
        L_0x00f3:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        L_0x00f6:
            if (r12 == 0) goto L_0x00a5
            r0 = 0
            r0 = r12[r0]     // Catch:{ all -> 0x00f3 }
            r2 = 1
            r2 = r12[r2]     // Catch:{ all -> 0x00f3 }
            r3 = 2
            r3 = r12[r3]     // Catch:{ all -> 0x00f3 }
            r4 = 3
            r4 = r12[r4]     // Catch:{ all -> 0x00f3 }
            android.opengl.GLES20.glViewport(r0, r2, r3, r4)     // Catch:{ all -> 0x00f3 }
            goto L_0x00a5
        L_0x0108:
            r0 = 2884(0xb44, float:4.041E-42)
            android.opengl.GLES10.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 2929(0xb71, float:4.104E-42)
            android.opengl.GLES10.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES10.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 3008(0xbc0, float:4.215E-42)
            android.opengl.GLES10.glEnable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 516(0x204, float:7.23E-43)
            r2 = 1053609165(0x3ecccccd, float:0.4)
            android.opengl.GLES10.glAlphaFunc(r0, r2)     // Catch:{ all -> 0x00f3 }
            r0 = 770(0x302, float:1.079E-42)
            r2 = 771(0x303, float:1.08E-42)
            android.opengl.GLES10.glBlendFunc(r0, r2)     // Catch:{ all -> 0x00f3 }
            r0 = 16640(0x4100, float:2.3318E-41)
            android.opengl.GLES10.glClear(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 1174667264(0x46040000, float:8448.0)
            r2 = 8960(0x2300, float:1.2556E-41)
            r3 = 8704(0x2200, float:1.2197E-41)
            android.opengl.GLES10.glTexEnvf(r2, r3, r0)     // Catch:{ all -> 0x00f3 }
            if (r12 == 0) goto L_0x00c0
            r0 = 0
            r0 = r12[r0]     // Catch:{ all -> 0x00f3 }
            r2 = 1
            r2 = r12[r2]     // Catch:{ all -> 0x00f3 }
            r3 = 2
            r3 = r12[r3]     // Catch:{ all -> 0x00f3 }
            r4 = 3
            r4 = r12[r4]     // Catch:{ all -> 0x00f3 }
            android.opengl.GLES20.glViewport(r0, r2, r3, r4)     // Catch:{ all -> 0x00f3 }
            goto L_0x00c0
        L_0x014c:
            java.util.concurrent.atomic.AtomicBoolean r0 = r8.drawingEnabled     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            boolean r0 = r0.get()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x01ad
            com.lumiyaviewer.lumiya.render.spatial.DrawList r0 = r8.currentDrawList     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x01ad
            com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo r0 = r8.currentFrustrumInfo     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x01ad
            if (r13 != 0) goto L_0x016c
            if (r11 == 0) goto L_0x016c
            r0 = 0
            r0 = r11[r0]     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 1
            r2 = r11[r2]     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r3 = 2
            r3 = r11[r3]     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r1.glModelTranslatef(r0, r2, r3)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x016c:
            r0 = 1
            r1.initAllPrimPrograms(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 0
            r1.curPrimProgram = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.spatial.DrawList r3 = r8.currentDrawList     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            boolean r0 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x019e
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES20.glDisable(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x017e:
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.DrawableObject> r4 = r3.objects     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int[] r5 = r3.renderPasses     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int r6 = r4.size()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r1.clearFaceTexture()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 0
            r2 = r0
        L_0x018b:
            if (r2 >= r6) goto L_0x023d
            java.lang.Object r0 = r4.get(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.DrawableObject r0 = (com.lumiyaviewer.lumiya.render.DrawableObject) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r7 = 1
            int r0 = r0.Draw(r1, r7)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r5[r2] = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int r0 = r2 + 1
            r2 = r0
            goto L_0x018b
        L_0x019e:
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES10.glDisable(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 3008(0xbc0, float:4.215E-42)
            android.opengl.GLES10.glDisable(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x017e
        L_0x01a9:
            r0 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch:{ all -> 0x00f3 }
        L_0x01ad:
            boolean r0 = r1.hasGL20     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x023b
            boolean r0 = r1.useFXAA     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x023b
            int[] r0 = r8.Colorbuffers     // Catch:{ all -> 0x00f3 }
            if (r0 == 0) goto L_0x023b
            int[] r0 = r8.systemFramebuffer     // Catch:{ all -> 0x00f3 }
            r2 = 0
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            r2 = 36160(0x8d40, float:5.0671E-41)
            android.opengl.GLES20.glBindFramebuffer(r2, r0)     // Catch:{ all -> 0x00f3 }
            if (r12 == 0) goto L_0x01d5
            r0 = 0
            r0 = r12[r0]     // Catch:{ all -> 0x00f3 }
            r2 = 1
            r2 = r12[r2]     // Catch:{ all -> 0x00f3 }
            r3 = 2
            r3 = r12[r3]     // Catch:{ all -> 0x00f3 }
            r4 = 3
            r4 = r12[r4]     // Catch:{ all -> 0x00f3 }
            android.opengl.GLES20.glViewport(r0, r2, r3, r4)     // Catch:{ all -> 0x00f3 }
        L_0x01d5:
            r0 = 2929(0xb71, float:4.104E-42)
            android.opengl.GLES20.glDisable(r0)     // Catch:{ all -> 0x00f3 }
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES20.glDisable(r0)     // Catch:{ all -> 0x00f3 }
            com.lumiyaviewer.lumiya.render.shaders.FXAAProgram r0 = r1.fxaaProgram     // Catch:{ all -> 0x00f3 }
            int r0 = r0.getHandle()     // Catch:{ all -> 0x00f3 }
            android.opengl.GLES20.glUseProgram(r0)     // Catch:{ all -> 0x00f3 }
            int[] r0 = r8.Colorbuffers     // Catch:{ all -> 0x00f3 }
            r2 = 0
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            r2 = 3553(0xde1, float:4.979E-42)
            android.opengl.GLES20.glBindTexture(r2, r0)     // Catch:{ all -> 0x00f3 }
            r0 = 33985(0x84c1, float:4.7623E-41)
            android.opengl.GLES20.glActiveTexture(r0)     // Catch:{ all -> 0x00f3 }
            int[] r0 = r8.Colorbuffers     // Catch:{ all -> 0x00f3 }
            r2 = 1
            r0 = r0[r2]     // Catch:{ all -> 0x00f3 }
            r2 = 3553(0xde1, float:4.979E-42)
            android.opengl.GLES20.glBindTexture(r2, r0)     // Catch:{ all -> 0x00f3 }
            r0 = 3553(0xde1, float:4.979E-42)
            r2 = 10240(0x2800, float:1.4349E-41)
            r3 = 9728(0x2600, float:1.3632E-41)
            android.opengl.GLES20.glTexParameteri(r0, r2, r3)     // Catch:{ all -> 0x00f3 }
            r0 = 3553(0xde1, float:4.979E-42)
            r2 = 10241(0x2801, float:1.435E-41)
            r3 = 9728(0x2600, float:1.3632E-41)
            android.opengl.GLES20.glTexParameteri(r0, r2, r3)     // Catch:{ all -> 0x00f3 }
            r0 = 3553(0xde1, float:4.979E-42)
            r2 = 10242(0x2802, float:1.4352E-41)
            r3 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r0, r2, r3)     // Catch:{ all -> 0x00f3 }
            r0 = 3553(0xde1, float:4.979E-42)
            r2 = 10243(0x2803, float:1.4354E-41)
            r3 = 33071(0x812f, float:4.6342E-41)
            android.opengl.GLES20.glTexParameteri(r0, r2, r3)     // Catch:{ all -> 0x00f3 }
            r0 = 33984(0x84c0, float:4.7622E-41)
            android.opengl.GLES20.glActiveTexture(r0)     // Catch:{ all -> 0x00f3 }
            com.lumiyaviewer.lumiya.render.Quad r0 = r1.quad     // Catch:{ all -> 0x00f3 }
            com.lumiyaviewer.lumiya.render.shaders.FXAAProgram r2 = r1.fxaaProgram     // Catch:{ all -> 0x00f3 }
            int r2 = r2.vPosition     // Catch:{ all -> 0x00f3 }
            com.lumiyaviewer.lumiya.render.shaders.FXAAProgram r3 = r1.fxaaProgram     // Catch:{ all -> 0x00f3 }
            int r3 = r3.vTexCoord     // Catch:{ all -> 0x00f3 }
            r0.DrawSingleQuadShader(r1, r2, r3)     // Catch:{ all -> 0x00f3 }
        L_0x023b:
            monitor-exit(r8)
            return
        L_0x023d:
            r0 = 0
            r1.curPrimProgram = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r1.clearFaceTexture()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch.GLPrepare(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch> r0 = r3.terrain     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x024c:
            boolean r0 = r2.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x0262
            java.lang.Object r0 = r2.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch r0 = (com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.GLDraw(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x024c
        L_0x025c:
            r0 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch:{ all -> 0x00f3 }
            goto L_0x01ad
        L_0x0262:
            r0 = 0
            r1.curPrimProgram = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r1.clearFaceTexture()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            boolean r0 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x028f
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES20.glEnable(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0271:
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r0 = r3.avatars     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r7 = r0.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0277:
            boolean r0 = r7.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x029c
            java.lang.Object r0 = r7.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r2 = r3.myAvatar     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 != r2) goto L_0x029a
            boolean r2 = r8.ownAvatarHidden     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0289:
            if (r2 != 0) goto L_0x0277
            r0.Draw(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x0277
        L_0x028f:
            r0 = 3042(0xbe2, float:4.263E-42)
            android.opengl.GLES10.glEnable(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 3008(0xbc0, float:4.215E-42)
            android.opengl.GLES10.glEnable(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x0271
        L_0x029a:
            r2 = 0
            goto L_0x0289
        L_0x029c:
            boolean r0 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x02b1
            com.lumiyaviewer.lumiya.render.WindlightSky r0 = r1.windlightSky     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.slproto.types.CameraParams r2 = r8.cameraParams     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r2 = r2.getHeading()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.slproto.types.CameraParams r7 = r8.cameraParams     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r7 = r7.getTilt()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.GLDraw(r1, r2, r7)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x02b1:
            r0 = 0
            r1.curPrimProgram = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r1.clearFaceTexture()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int r0 = r6 + -1
            r2 = r0
        L_0x02ba:
            if (r2 < 0) goto L_0x02d0
            r0 = r5[r2]     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = r0 & 2
            if (r0 == 0) goto L_0x02cc
            java.lang.Object r0 = r4.get(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.DrawableObject r0 = (com.lumiyaviewer.lumiya.render.DrawableObject) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r6 = 2
            r0.Draw(r1, r6)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x02cc:
            int r0 = r2 + -1
            r2 = r0
            goto L_0x02ba
        L_0x02d0:
            boolean r0 = r1.hasGL30     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x02f2
            com.lumiyaviewer.lumiya.render.BoundingBox.PrepareOcclusionQueries(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r2 = r4.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x02db:
            boolean r0 = r2.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x02ef
            java.lang.Object r0 = r2.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.DrawableObject r0 = (com.lumiyaviewer.lumiya.render.DrawableObject) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo r4 = r8.currentFrustrumInfo     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float[] r4 = r4.mvpMatrix     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.TestOcclusion(r1, r4)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x02db
        L_0x02ef:
            com.lumiyaviewer.lumiya.render.BoundingBox.EndOcclusionQueries(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x02f2:
            android.os.Handler r0 = r8.screenshotHandler     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x02fe
            android.os.Handler r0 = r8.screenshotHandler     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r8.takeScreenshot(r1, r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 0
            r8.screenshotHandler = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x02fe:
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r8.drawPickedObject     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x0324
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = r8.drawPickedObject     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            boolean r0 = r0.isAvatar()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x0324
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.DrawableObject> r0 = r3.objects     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0312:
            boolean r0 = r2.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x0324
            java.lang.Object r0 = r2.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.DrawableObject r0 = (com.lumiyaviewer.lumiya.render.DrawableObject) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r4 = r8.drawPickedObject     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.DrawIfPicked(r1, r4)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x0312
        L_0x0324:
            boolean r0 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 != 0) goto L_0x0335
            r0 = 5889(0x1701, float:8.252E-42)
            android.opengl.GLES10.glMatrixMode(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            android.opengl.GLES10.glLoadIdentity()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 5888(0x1700, float:8.251E-42)
            android.opengl.GLES10.glMatrixMode(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0335:
            boolean r0 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x036b
            boolean r0 = r1.useFXAA     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x036b
            int[] r0 = r8.Colorbuffers     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x036b
            android.opengl.GLES20.glFinish()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int[] r0 = r8.Colorbuffers     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 1
            r0 = r0[r2]     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 3553(0xde1, float:4.979E-42)
            android.opengl.GLES20.glBindTexture(r2, r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int[] r0 = r8.Colorbuffers     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 1
            r0 = r0[r2]     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 36160(0x8d40, float:5.0671E-41)
            r4 = 36064(0x8ce0, float:5.0536E-41)
            r5 = 3553(0xde1, float:4.979E-42)
            r6 = 0
            android.opengl.GLES20.glFramebufferTexture2D(r2, r4, r5, r0, r6)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 0
            r2 = 0
            r4 = 0
            r5 = 0
            android.opengl.GLES20.glClearColor(r0, r2, r4, r5)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 16384(0x4000, float:2.2959E-41)
            android.opengl.GLES20.glClear(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x036b:
            com.lumiyaviewer.lumiya.render.Quad r0 = r1.quad     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.PrepareDrawQuads(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r0 = r3.avatars     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r4 = r0.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0376:
            boolean r0 = r4.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x0390
            java.lang.Object r0 = r4.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r2 = r3.myAvatar     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 != r2) goto L_0x038e
            boolean r2 = r8.ownAvatarHidden     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0388:
            if (r2 != 0) goto L_0x0376
            r0.DrawNameTag(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x0376
        L_0x038e:
            r2 = 0
            goto L_0x0388
        L_0x0390:
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub> r0 = r3.avatarStubs     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0396:
            boolean r0 = r2.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x03a6
            java.lang.Object r0 = r2.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.DrawNameTag(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x0396
        L_0x03a6:
            boolean r0 = r8.hoverTextEnableObjects     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x03c1
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.DrawableObject> r0 = r3.objects     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x03b0:
            boolean r0 = r2.hasNext()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x03c1
            java.lang.Object r0 = r2.next()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.DrawableObject r0 = (com.lumiyaviewer.lumiya.render.DrawableObject) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r4 = 0
            r0.DrawHoverText(r1, r4)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x03b0
        L_0x03c1:
            com.lumiyaviewer.lumiya.render.Quad r0 = r1.quad     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.EndDrawQuads(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 0
            r1.curPrimProgram = r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int r0 = r8.displayedHUDid     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x0461
            r5 = 0
            java.util.List<com.lumiyaviewer.lumiya.render.TouchHUDEvent> r0 = r8.touchHUDEvents     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            int r0 = r0.size()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 1
            if (r0 < r2) goto L_0x03e1
            java.util.List<com.lumiyaviewer.lumiya.render.TouchHUDEvent> r0 = r8.touchHUDEvents     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 0
            java.lang.Object r0 = r0.remove(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.TouchHUDEvent r0 = (com.lumiyaviewer.lumiya.render.TouchHUDEvent) r0     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r5 = r0
        L_0x03e1:
            boolean r0 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x0448
            r0 = 256(0x100, float:3.59E-43)
            android.opengl.GLES20.glClear(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 0
            r1.initAllPrimPrograms(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x03ee:
            com.lumiyaviewer.lumiya.render.MatrixStack r0 = r1.projectionHUDMatrix     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r1.setActiveProjectionMatrix(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = 1119092736(0x42b40000, float:90.0)
            r2 = 0
            r4 = 1065353216(0x3f800000, float:1.0)
            r6 = 0
            r1.glModelRotatef(r0, r2, r4, r6)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r2 = 1065353216(0x3f800000, float:1.0)
            r4 = 0
            r6 = 0
            r1.glModelRotatef(r0, r2, r4, r6)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r0 = r3.myAvatar     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x01ad
            int r2 = r8.displayedHUDid     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.setDisplayedHUDid(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.avatar.DrawableHUD r0 = r0.getDrawableHUD()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r0 == 0) goto L_0x01ad
            float r2 = r8.hudScaleFactor     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r3 = r8.hudOffsetX     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r4 = r8.hudOffsetY     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r6 = 0
            com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo r2 = r0.Draw(r1, r2, r3, r4, r5, r6)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r2 == 0) goto L_0x0424
            r8.handleHUDTouch(r1, r0, r5, r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x0424:
            boolean r2 = r1.hasGL20     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r2 == 0) goto L_0x044e
            r2 = 2929(0xb71, float:4.104E-42)
            android.opengl.GLES20.glDisable(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
        L_0x042d:
            boolean r2 = r8.hoverTextEnableHUDs     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            if (r2 == 0) goto L_0x01ad
            com.lumiyaviewer.lumiya.render.Quad r2 = r1.quad     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2.PrepareDrawQuads(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r2 = r8.hudScaleFactor     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r3 = r8.hudOffsetX     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            float r4 = r8.hudOffsetY     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r5 = 0
            r6 = 1
            r0.Draw(r1, r2, r3, r4, r5, r6)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            com.lumiyaviewer.lumiya.render.Quad r0 = r1.quad     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.EndDrawQuads(r1)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x01ad
        L_0x0448:
            r0 = 256(0x100, float:3.59E-43)
            android.opengl.GLES10.glClear(r0)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x03ee
        L_0x044e:
            r2 = 2929(0xb71, float:4.104E-42)
            android.opengl.GLES10.glDisable(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 5889(0x1701, float:8.252E-42)
            android.opengl.GLES10.glMatrixMode(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            android.opengl.GLES10.glLoadIdentity()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r2 = 5888(0x1700, float:8.251E-42)
            android.opengl.GLES10.glMatrixMode(r2)     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x042d
        L_0x0461:
            java.util.List<com.lumiyaviewer.lumiya.render.TouchHUDEvent> r0 = r8.touchHUDEvents     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            r0.clear()     // Catch:{ ConcurrentModificationException -> 0x01a9, IndexOutOfBoundsException -> 0x025c }
            goto L_0x01ad
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.WorldViewRenderer.onDrawFrame(javax.microedition.khronos.opengles.GL10, com.lumiyaviewer.lumiya.render.HeadTransformCompat, float[], int[], float[], float[], int):void");
    }

    public void onFinishFrame() {
        this.currentDrawList = null;
        RenderContext renderContext2 = this.renderContext.get();
        if (renderContext2 != null) {
            if ((this.previousFrameTime > this.lastDrawListUpdateTime + MIN_DRAW_LIST_UPDATE_INTERVAL || this.drawListUpdateFrameCount <= 0) && renderContext2.drawableStore.spatialObjectIndex.updateDrawListIfNeeded()) {
                this.lastDrawListUpdateTime = this.previousFrameTime;
                this.drawListUpdateFrameCount = 4;
            }
            if (this.drawListUpdateFrameCount > 0) {
                this.drawListUpdateFrameCount--;
            }
        }
    }

    public void onPrepareFrame(HeadTransformCompat headTransformCompat2) {
        RenderContext renderContext2;
        this.headTransformCompat = headTransformCompat2;
        this.renderThreadExecutor.runQueuedTasks();
        if (this.drawingEnabled.get() && (renderContext2 = this.renderContext.get()) != null) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.previousFrameTime;
            if (j < 33) {
                try {
                    Thread.sleep(33 - j);
                } catch (InterruptedException e) {
                }
            }
            this.previousFrameTime = currentTimeMillis;
            this.thisFrameTime = currentTimeMillis - this.firstFrameTime;
            this.fpsFrameCount++;
            if (this.fpsFrameCount >= 10) {
                this.fpsFrameCount = 0;
                if (this.thisFrameTime != this.lastFrameTime) {
                    Debug.Printf("Renderer: FPS %.2f frame time %d", Float.valueOf(10000.0f / ((float) (this.thisFrameTime - this.lastFrameTime))), Long.valueOf(this.thisFrameTime - this.lastFrameTime));
                    this.lastFrameTime = this.thisFrameTime;
                }
            }
            TextureMemoryTracker.releaseFrameMemory();
            renderContext2.ClearFrameKeeps();
            renderContext2.glResourceManager.Cleanup();
            renderContext2.frameCount++;
            if (renderContext2.hasGL30) {
                renderContext2.processOcclusionQueries();
            }
            renderContext2.glModelResetIdentity();
            renderContext2.setActiveProjectionMatrix(renderContext2.projectionMatrix);
            if (this.avatarControl != null) {
                if (headTransformCompat2 != null) {
                    this.avatarControl.getVRCamera(headTransformCompat2, renderContext2.myAviPosition, this.cameraParams);
                    setIsFlinging(false);
                } else {
                    setIsFlinging(this.avatarControl.getAgentAndCameraPosition(renderContext2.myAviPosition, this.cameraParams));
                }
            }
            renderContext2.frameCamera.set(this.cameraParams.getPosition());
            if (headTransformCompat2 != null) {
                renderContext2.glModelMultMatrixf(headTransformCompat2.headTransformMatrix, 0);
                renderContext2.glModelRotatef((-headTransformCompat2.viewExtraYaw) + 90.0f, 0.0f, 1.0f, 0.0f);
                renderContext2.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            } else {
                renderContext2.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                float tilt = this.cameraParams.getTilt();
                if (tilt != 0.0f) {
                    renderContext2.glModelRotatef(tilt, 1.0f, 0.0f, 0.0f);
                }
                renderContext2.glModelRotatef((-this.cameraParams.getHeading()) + 90.0f, 0.0f, 0.0f, 1.0f);
            }
            renderContext2.glModelTranslatef(-renderContext2.frameCamera.x, -renderContext2.frameCamera.y, -renderContext2.frameCamera.z);
            FrustrumInfo frustrumInfo = renderContext2.hasGL20 ? new FrustrumInfo(renderContext2.frameCamera.x, renderContext2.frameCamera.y, renderContext2.frameCamera.z, (float) this.drawDistance, renderContext2.modelViewMatrix.getMatrixData(), renderContext2.modelViewMatrix.getMatrixDataOffset()) : new FrustrumInfo(renderContext2.frameCamera.x, renderContext2.frameCamera.y, renderContext2.frameCamera.z, (float) this.drawDistance, renderContext2.modelViewMatrix.getMatrixData(), renderContext2.modelViewMatrix.getMatrixDataOffset(), renderContext2.projectionMatrix.getMatrixData(), renderContext2.projectionMatrix.getMatrixDataOffset());
            if (!Objects.equal(this.currentFrustrumInfo, frustrumInfo)) {
                this.currentFrustrumInfo = frustrumInfo;
                renderContext2.drawableStore.spatialObjectIndex.setViewport(this.currentFrustrumInfo, new FrustrumPlanes(this.currentFrustrumInfo.mvpMatrix));
            }
            if (!this.isResponsiveMode) {
                renderContext2.RunLoadQueue();
            }
            if (this.parcelInfo != null) {
                renderContext2.underWater = this.parcelInfo.terrainData.isUnderWater(renderContext2.frameCamera.z);
                renderContext2.waterTime = ((float) (this.thisFrameTime % 1000000)) / 1000.0f;
                if (renderContext2.hasGL20) {
                    float f = this.forcedTime;
                    if (Float.isNaN(f)) {
                        if (this.parcelInfo.getSunHour(this.simSunHour, Float.isNaN(this.simSunHour[0]))) {
                            Debug.Printf("Windlight: using sim hour of %f", Float.valueOf(this.simSunHour[0]));
                            renderContext2.windlightDay.InterpolatePreset(renderContext2.windlightPreset, this.simSunHour[0]);
                        }
                    } else if (Float.isNaN(this.simSunHour[0]) || this.simSunHour[0] != f) {
                        this.simSunHour[0] = f;
                        Debug.Printf("Windlight: using forced hour of %f", Float.valueOf(this.simSunHour[0]));
                        renderContext2.windlightDay.InterpolatePreset(renderContext2.windlightPreset, this.simSunHour[0]);
                    }
                }
            }
            this.currentDrawList = renderContext2.drawableStore.spatialObjectIndex.getObjectsInFrustrum();
            for (DrawableAvatar drawableAvatar : this.currentDrawList.avatars) {
                if (!(drawableAvatar == this.currentDrawList.myAvatar ? this.ownAvatarHidden : false)) {
                    drawableAvatar.RunAnimations();
                }
            }
            processObjectPick();
        }
    }

    public void onRendererShutdown() {
        PrimComputeExecutor.getInstance().resume();
        SpatialIndex.getInstance().DisableObjectIndex(this);
        RenderContext andSet = this.renderContext.getAndSet((Object) null);
        if (andSet != null) {
            andSet.StopLoadQueue();
            andSet.ClearFrameKeeps();
            Debug.Printf("EGL: destroyContext: calling Flush().", new Object[0]);
            andSet.glResourceManager.Flush();
            Debug.Printf("EGL: destroyContext: returned from Flush().", new Object[0]);
        }
        TextureMemoryTracker.releaseAllFrameMemory();
        Debug.Printf("EGL: destroyContext: calling eglDestroyContext ().", new Object[0]);
        this.initialUpdateDone = false;
        TextureMemoryTracker.clearActiveRenderer(this);
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        RenderContext renderContext2 = this.renderContext.get();
        if (renderContext2 != null) {
            renderContext2.viewportRect[0] = 0;
            renderContext2.viewportRect[1] = 0;
            renderContext2.viewportRect[2] = i;
            renderContext2.viewportRect[3] = i2;
            if (renderContext2.hasGL20) {
                GLES20.glViewport(0, 0, i, i2);
                if (renderContext2.useFXAA) {
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
                        this.Renderbuffers = new int[1];
                        GLES20.glGenRenderbuffers(1, this.Renderbuffers, 0);
                        GLES20.glBindRenderbuffer(36161, this.Renderbuffers[0]);
                        GLES20.glRenderbufferStorage(36161, 33189, i, i2);
                    }
                    if (this.Colorbuffers == null) {
                        this.Colorbuffers = new int[2];
                        GLES20.glGenTextures(this.Colorbuffers.length, this.Colorbuffers, 0);
                        int i3 = 0;
                        while (true) {
                            int i4 = i3;
                            if (i4 >= this.Colorbuffers.length) {
                                break;
                            }
                            GLES20.glBindTexture(3553, this.Colorbuffers[i4]);
                            if (i4 == 0) {
                                GLES20.glTexImage2D(3553, 0, 6407, i, i2, 0, 6407, 33635, (Buffer) null);
                            } else {
                                GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, (Buffer) null);
                            }
                            GLES20.glTexParameteri(3553, 10242, 33071);
                            GLES20.glTexParameteri(3553, 10243, 33071);
                            GLES20.glTexParameteri(3553, 10240, 9729);
                            GLES20.glTexParameteri(3553, 10241, 9729);
                            i3 = i4 + 1;
                        }
                    }
                    GLES20.glViewport(0, 0, i, i2);
                    GLES20.glBindRenderbuffer(36161, this.Renderbuffers[0]);
                    GLES20.glBindTexture(3553, this.Colorbuffers[0]);
                    GLES20.glFramebufferRenderbuffer(36160, 36096, 36161, this.Renderbuffers[0]);
                    GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.Colorbuffers[0], 0);
                    GLES20.glUseProgram(renderContext2.fxaaProgram.getHandle());
                    GLES20.glUniform1i(renderContext2.fxaaProgram.textureSampler, 0);
                    GLES20.glUniform1i(renderContext2.fxaaProgram.noAAtextureSampler, 1);
                    GLES20.glUniform2f(renderContext2.fxaaProgram.texcoordOffset, 1.0f / ((float) i), 1.0f / ((float) i2));
                    float[] fArr = new float[16];
                    android.opengl.Matrix.setIdentityM(fArr, 0);
                    android.opengl.Matrix.scaleM(fArr, 0, 2.0f, 2.0f, 1.0f);
                    GLES20.glUniformMatrix4fv(renderContext2.fxaaProgram.uMVPMatrix, 1, false, fArr, 0);
                    GLES20.glBindFramebuffer(36160, this.systemFramebuffer[0]);
                }
            } else {
                GLES10.glViewport(0, 0, i, i2);
                GLES10.glMatrixMode(5889);
                GLES10.glLoadIdentity();
            }
            renderContext2.aspectRatio = ((float) i) / ((float) i2);
            renderContext2.FOVAngle = 60.0f;
            renderContext2.getClass();
            float tan = ((float) Math.tan((((double) renderContext2.FOVAngle) * 3.141592653589793d) / 360.0d)) * 0.5f;
            float f = (float) this.drawDistance;
            Debug.Log("Renderer: Using drawDistance = " + this.drawDistance);
            renderContext2.projectionMatrix.reset();
            renderContext2.getClass();
            renderContext2.projectionMatrix.glFrustumf((-renderContext2.aspectRatio) * tan, renderContext2.aspectRatio * tan, -tan, tan, 0.5f, f);
            renderContext2.drawDistance = (float) this.drawDistance;
            renderContext2.projectionHUDMatrix.reset();
            renderContext2.projectionHUDMatrix.glOrthof((-renderContext2.aspectRatio) * 1.0f, renderContext2.aspectRatio * 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
            if (renderContext2.windlightSky != null) {
                renderContext2.windlightSky.updateMatrix(renderContext2);
            }
            this.firstFrameTime = System.currentTimeMillis();
        }
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        onSurfaceCreated(gl10, eGLConfig, false);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig, boolean z) {
        boolean z2;
        TextureMemoryTracker.setActiveRenderer(this);
        this.drawingEnabled.set(true);
        this.firstFrameCount.set(1);
        PrimComputeExecutor.getInstance().resume();
        String glGetString = GLES10.glGetString(7936);
        String glGetString2 = GLES10.glGetString(7937);
        String glGetString3 = GLES10.glGetString(7938);
        String glGetString4 = GLES10.glGetString(7939);
        if (this.createdGL30) {
            int[] iArr = new int[1];
            GLES30.glGetIntegerv(33307, iArr, 0);
            Debug.Printf("Renderer: Reported major version: %d", Integer.valueOf(iArr[0]));
            if (iArr[0] < 3) {
                this.createdGL30 = false;
            }
        }
        Debug.Printf("Renderer: glVendor '%s', glRenderer '%s'", glGetString, glGetString2);
        Debug.Log("Renderer: version = '" + glGetString3 + "', extensions = '" + glGetString4 + "', thread id = " + Thread.currentThread().getId());
        boolean z3 = this.createdGL30;
        boolean z4 = this.createdGL30;
        boolean z5 = z3;
        for (String equals : glGetString4.split(" ")) {
            if (equals.equals("GL_ARB_vertex_buffer_object")) {
                z5 = true;
            }
        }
        if (glGetString3.contains("1.1")) {
            z4 = true;
            z2 = true;
        } else {
            z2 = z5;
        }
        Debug.Printf("Renderer: VBO support %s, GL11 %s, GL30 %s", Boolean.valueOf(z2), Boolean.valueOf(z4), Boolean.valueOf(this.createdGL30));
        RenderContext renderContext2 = new RenderContext(eGLConfig, glGetString2, this.createdGL30, this.requestGL20, z4, z2, this.avatarCountLimit, GlobalOptions.getInstance().getTerrainTextures(), this.fontSize, z, this);
        Debug.AlwaysPrintf("Renderer: created context, GL30 %b, GL20 %b", Boolean.valueOf(renderContext2.hasGL30), Boolean.valueOf(renderContext2.hasGL20));
        if (renderContext2.hasGL20) {
            if (renderContext2.getShaderCompileErrors()) {
                Debug.Printf("Renderer: Shaders did not compile well.", new Object[0]);
                if (this.stateHandler != null) {
                    this.stateHandler.sendEmptyMessage(4);
                }
                this.drawingEnabled.set(false);
            }
            Debug.Printf("Renderer: Basic geometry program = %d", Integer.valueOf(renderContext2.primProgram.getHandle()));
            GLES20.glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
            if (renderContext2.useFXAA) {
                this.Framebuffers = new int[1];
                GLES20.glGenFramebuffers(1, this.Framebuffers, 0);
            } else {
                this.Framebuffers = null;
            }
            this.Renderbuffers = null;
            this.Colorbuffers = null;
        } else {
            GLES10.glEnableClientState(32884);
            GLES10.glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
        }
        this.renderContext.set(renderContext2);
        this.hoverTextEnableHUDs = GlobalOptions.getInstance().getHoverTextEnableHUDs();
        this.hoverTextEnableObjects = GlobalOptions.getInstance().getHoverTextEnableObjects();
        if (this.stateHandler != null) {
            this.stateHandler.sendEmptyMessage(3);
        }
        PrimComputeExecutor.getInstance().execute(this.initSpatialIndexRunnable);
        this.firstFrameTime = System.currentTimeMillis();
    }

    public void pickObject(float f, float f2, Handler handler) {
        synchronized (this.pickLock) {
            this.needPickX = f;
            this.needPickY = f2;
            this.needPickObject = true;
            this.pickHandler = handler;
        }
    }

    public void requestScreenshot(Handler handler) {
        this.screenshotHandler = handler;
    }

    public void setAvatarCountLimit(int i) {
        this.avatarCountLimit = i;
        SpatialIndex.getInstance().setAvatarCountLimit(i);
    }

    public void setDisplayedHUDid(int i) {
        this.displayedHUDid = i;
    }

    public void setDrawDistance(int i) {
        this.drawDistance = i;
        RenderContext renderContext2 = this.renderContext.get();
        if (renderContext2 != null) {
            renderContext2.drawDistance = (float) i;
        }
    }

    public void setDrawPickedObject(SLObjectInfo sLObjectInfo) {
        this.drawPickedObject = sLObjectInfo;
    }

    public void setForcedTime(boolean z, float f) {
        if (z) {
            this.forcedTime = f;
        } else {
            this.forcedTime = Float.NaN;
        }
    }

    public void setHUDOffset(float f, float f2) {
        this.hudOffsetX = f;
        this.hudOffsetY = f2;
    }

    public void setHUDScaleFactor(float f) {
        this.hudScaleFactor = f;
    }

    public void setIsInteracting(boolean z) {
        boolean z2 = false;
        synchronized (this.responsiveModeLock) {
            if (this.isInteracting != z) {
                this.isInteracting = z;
                z2 = true;
            }
        }
        if (z2) {
            updateResponsive();
        }
    }

    public void setOwnAvatarHidden(boolean z) {
        this.ownAvatarHidden = z;
    }

    public void touchHUD(float f, float f2, Handler handler) {
        this.touchHandler = handler;
        if (this.displayedHUDid != 0) {
            this.touchHUDEvents.add(new TouchHUDEvent(f, f2));
        }
    }
}
