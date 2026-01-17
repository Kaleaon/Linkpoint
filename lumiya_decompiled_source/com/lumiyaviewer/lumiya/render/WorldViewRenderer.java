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
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.avatar.DrawableHUD;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawList;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumPlanes;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
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
/* loaded from: classes.dex */
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
    @Nullable
    private FrustrumInfo currentFrustrumInfo;
    private long firstFrameTime;
    private final int fontSize;
    private long lastFrameTime;
    private final boolean requestGL20;
    private final Handler stateHandler;
    private final SynchronousExecutor renderThreadExecutor = new SynchronousExecutor();
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(this.renderThreadExecutor, new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.render.-$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA
        private final /* synthetic */ void $m$0(Object obj) {
            ((WorldViewRenderer) this).m60com_lumiyaviewer_lumiya_render_WorldViewRenderermthref0((SLAgentCircuit) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final AtomicReference<RenderContext> renderContext = new AtomicReference<>();
    @Nullable
    private SLAvatarControl avatarControl = null;
    @Nullable
    private SLParcelInfo parcelInfo = null;
    private volatile boolean initialUpdateDone = false;
    private AtomicBoolean drawingEnabled = new AtomicBoolean(true);
    private AtomicInteger firstFrameCount = new AtomicInteger(1);
    private long lastDrawListUpdateTime = 0;
    private int drawListUpdateFrameCount = 0;
    private SLObjectInfo drawPickedObject = null;
    private float[] simSunHour = {Float.NaN};
    private volatile float forcedTime = Float.NaN;
    private final Object pickLock = new Object();
    private boolean needPickObject = false;
    private float needPickX = 0.0f;
    private float needPickY = 0.0f;
    private Handler pickHandler = null;
    private List<TouchHUDEvent> touchHUDEvents = Collections.synchronizedList(new LinkedList());
    private Handler touchHandler = null;
    private boolean ownAvatarHidden = false;
    private int drawDistance = 20;
    private int avatarCountLimit = 5;
    private final CameraParams cameraParams = new CameraParams();
    private final Object responsiveModeLock = new Object();
    private boolean isInteracting = false;
    private boolean isFlinging = false;
    private volatile boolean isResponsiveMode = false;
    private Handler screenshotHandler = null;
    private volatile int displayedHUDid = 0;
    private float hudScaleFactor = 1.0f;
    private float hudOffsetX = 0.0f;
    private float hudOffsetY = 0.0f;
    private boolean hoverTextEnableHUDs = true;
    private boolean hoverTextEnableObjects = false;
    private boolean createdGL30 = false;
    private int[] Framebuffers = null;
    private int[] Renderbuffers = null;
    private int[] Colorbuffers = null;
    private HeadTransformCompat headTransformCompat = null;
    private final int[] systemFramebuffer = new int[1];
    private final Runnable initSpatialIndexRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.render.-$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA.1
        private final /* synthetic */ void $m$0() {
            ((WorldViewRenderer) this).m61lambda$com_lumiyaviewer_lumiya_render_WorldViewRenderer_6642();
        }

        @Override // java.lang.Runnable
        public final void run() {
            $m$0();
        }
    };
    private long thisFrameTime = 0;
    private int fpsFrameCount = 0;
    private long previousFrameTime = 0;
    private DrawList currentDrawList = null;
    private final float[] extTextureMatrix = new float[64];
    private final float[] extTextureHitVector = {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] extTextureResultVector = new float[4];

    public WorldViewRenderer(Handler handler, boolean z, @Nonnull UserManager userManager, int i) {
        this.stateHandler = handler;
        this.requestGL20 = z;
        this.fontSize = i;
        this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
    }

    private void handleHUDTouch(RenderContext renderContext, DrawableHUD drawableHUD, TouchHUDEvent touchHUDEvent, ObjectIntersectInfo objectIntersectInfo) {
        float f = renderContext.viewportRect[2];
        float f2 = ((f / 2.0f) - touchHUDEvent.y) / f;
        float f3 = ((renderContext.viewportRect[2] / 2.0f) - touchHUDEvent.x) / f;
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

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m60com_lumiyaviewer_lumiya_render_WorldViewRenderermthref0(SLAgentCircuit sLAgentCircuit) {
        if (sLAgentCircuit == null) {
            this.avatarControl = null;
            this.parcelInfo = null;
            return;
        }
        Debug.Printf("WorldViewRenderer: got new agentCircuit.", new Object[0]);
        this.initialUpdateDone = false;
        this.avatarControl = sLAgentCircuit.getModules().avatarControl;
        this.parcelInfo = sLAgentCircuit.getGridConnection().parcelInfo;
        this.initialUpdateDone = false;
        RenderContext renderContext = this.renderContext.get();
        if (renderContext != null) {
            renderContext.setMeshCapURL(sLAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.GetMesh));
            if (this.parcelInfo != null) {
                PrimComputeExecutor.getInstance().execute(this.initSpatialIndexRunnable);
            }
        }
    }

    private void processObjectPick() {
        Handler handler;
        float f;
        RenderContext renderContext;
        ObjectIntersectInfo objectIntersectInfo;
        boolean z = false;
        float f2 = Float.NaN;
        synchronized (this.pickLock) {
            if (this.needPickObject) {
                this.needPickObject = false;
                f = this.needPickX;
                f2 = this.needPickY;
                handler = this.pickHandler;
                z = true;
            } else {
                handler = null;
                f = Float.NaN;
            }
        }
        if (!z || (renderContext = this.renderContext.get()) == null) {
            return;
        }
        try {
            objectIntersectInfo = null;
            for (DrawableObject drawableObject : this.currentDrawList.objects) {
                try {
                    objectIntersectInfo = tryPickObject(renderContext, f, f2, drawableObject, objectIntersectInfo);
                } catch (Exception e) {
                    e = e;
                    Debug.Warning(e);
                    if (objectIntersectInfo != null) {
                        return;
                    }
                    return;
                }
            }
            for (DrawableAvatar drawableAvatar : this.currentDrawList.avatars) {
                objectIntersectInfo = tryPickObject(renderContext, f, f2, drawableAvatar, objectIntersectInfo);
            }
        } catch (Exception e2) {
            e = e2;
            objectIntersectInfo = null;
        }
        if (objectIntersectInfo != null || handler == null) {
            return;
        }
        handler.sendMessage(handler.obtainMessage(1, objectIntersectInfo));
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

    private void takeScreenshot(RenderContext renderContext, Handler handler) {
        int i = renderContext.viewportRect[2];
        int i2 = renderContext.viewportRect[3];
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(i * i2 * 4);
        GLES10.glReadPixels(0, 0, i, i2, 6408, 5121, directByteBuffer.asByteBuffer());
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        createBitmap.copyPixelsFromBuffer(directByteBuffer.asByteBuffer());
        Bitmap createBitmap2 = Bitmap.createBitmap(i, i2, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(createBitmap2);
        Matrix matrix = new Matrix();
        matrix.setScale(1.0f, -1.0f);
        matrix.postTranslate(0.0f, i2);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(createBitmap, matrix, paint);
        createBitmap.recycle();
        paint.setTextSize(this.fontSize);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(-1);
        float f = this.fontSize * 2.0f;
        for (String str : new String[]{"Lumiya Viewer", "http://lumiyaviewer.com"}) {
            canvas.drawText(str, this.fontSize, f, paint);
            f = f + (paint.descent() - paint.ascent()) + (this.fontSize * 0.5f);
        }
        handler.sendMessage(handler.obtainMessage(5, createBitmap2));
    }

    private ObjectIntersectInfo tryPickObject(RenderContext renderContext, float f, float f2, IntersectPickable intersectPickable, ObjectIntersectInfo objectIntersectInfo) {
        ObjectIntersectInfo PickObject = intersectPickable.PickObject(renderContext, f, f2, -0.9f);
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
        if (z != z2) {
            if (z2) {
                PrimComputeExecutor.getInstance().pause();
            } else {
                PrimComputeExecutor.getInstance().resume();
            }
        }
    }

    @Override // android.opengl.GLSurfaceView.EGLContextFactory
    public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        Debug.Printf("EGL: createContext called.", new Object[0]);
        if (this.requestGL20 && Build.VERSION.SDK_INT >= 18) {
            Debug.Printf("EGL: trying to create 3.0 context.", new Object[0]);
            EGLContext eglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{EGL_CONTEXT_CLIENT_VERSION, 3, 12344});
            if (eglCreateContext != null && eglCreateContext != EGL10.EGL_NO_CONTEXT) {
                Debug.Printf("EGL: 3.0 context apparently created.", new Object[0]);
                this.createdGL30 = true;
                return eglCreateContext;
            }
            Debug.Printf("EGL: Failed to create 3.0 context.", new Object[0]);
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

    @Override // android.opengl.GLSurfaceView.EGLContextFactory
    @SuppressLint({"DefaultLocale"})
    public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
        Debug.Printf("EGL: destroyContext called.", new Object[0]);
        onRendererShutdown();
        if (!egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
            throw new RuntimeException(String.format("EGLError code %d", Integer.valueOf(egl10.eglGetError())));
        }
        Debug.Printf("EGL: destroyContext exiting.", new Object[0]);
    }

    public void disableDrawing() {
        this.drawingEnabled.set(false);
    }

    public void drawCrosshair(float f, float f2) {
        RenderContext renderContext = this.renderContext.get();
        if (renderContext == null || renderContext.crosshairTexture == null) {
            return;
        }
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
        android.opengl.Matrix.translateM(this.extTextureMatrix, 16, this.extTextureMatrix, 0, -f2, 0.0f, 0.0f);
        android.opengl.Matrix.scaleM(this.extTextureMatrix, 16, f, f, 1.0f);
        android.opengl.Matrix.multiplyMM(this.extTextureMatrix, 32, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), this.extTextureMatrix, 16);
        GLES20.glUniformMatrix4fv(renderContext.rawShaderProgram.uMVPMatrix, 1, false, this.extTextureMatrix, 32);
        renderContext.quad.DrawSingleQuadShader(renderContext, renderContext.rawShaderProgram.vPosition, renderContext.rawShaderProgram.vTexCoord);
    }

    public void drawExternalTexture(@Nonnull GLExternalTexture gLExternalTexture, float[] fArr, float f, float f2, float f3, float f4, float f5, float[] fArr2, int i) {
        RenderContext renderContext = this.renderContext.get();
        if (renderContext == null || renderContext.extTextureProgram == null) {
            return;
        }
        gLExternalTexture.update(fArr);
        GLES20.glDisable(2929);
        GLES20.glDisable(2884);
        GLES20.glEnable(3042);
        GLES20.glUseProgram(renderContext.extTextureProgram.getHandle());
        GLES20.glActiveTexture(33984);
        gLExternalTexture.bind();
        GLES20.glUniform1i(renderContext.extTextureProgram.textureSampler, 0);
        GLES20.glUniformMatrix4fv(renderContext.extTextureProgram.vTextureTransformMatrix, 1, false, fArr, 0);
        android.opengl.Matrix.setIdentityM(this.extTextureMatrix, 0);
        android.opengl.Matrix.rotateM(this.extTextureMatrix, 0, -f2, 1.0f, 0.0f, 0.0f);
        android.opengl.Matrix.rotateM(this.extTextureMatrix, 0, -f3, 0.0f, 1.0f, 0.0f);
        android.opengl.Matrix.translateM(this.extTextureMatrix, 0, 0.0f, 0.0f, -2.0f);
        android.opengl.Matrix.translateM(this.extTextureMatrix, 16, this.extTextureMatrix, 0, -f, 0.0f, 0.0f);
        android.opengl.Matrix.scaleM(this.extTextureMatrix, 16, f4, f5, 1.0f);
        android.opengl.Matrix.multiplyMM(this.extTextureMatrix, 32, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), this.extTextureMatrix, 16);
        android.opengl.Matrix.invertM(this.extTextureMatrix, 48, this.extTextureMatrix, 32);
        android.opengl.Matrix.multiplyMV(this.extTextureResultVector, 0, this.extTextureMatrix, 48, this.extTextureHitVector, 0);
        fArr2[i] = this.extTextureResultVector[0];
        fArr2[i + 1] = this.extTextureResultVector[1];
        GLES20.glUniformMatrix4fv(renderContext.extTextureProgram.uMVPMatrix, 1, false, this.extTextureMatrix, 32);
        renderContext.quad.DrawSingleQuadShader(renderContext, renderContext.extTextureProgram.vPosition, renderContext.extTextureProgram.vTexCoord);
    }

    public void enableDrawing() {
        if (this.drawingEnabled.getAndSet(true)) {
            return;
        }
        this.firstFrameCount.set(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_render_WorldViewRenderer_6642  reason: not valid java name */
    public /* synthetic */ void m61lambda$com_lumiyaviewer_lumiya_render_WorldViewRenderer_6642() {
        SLParcelInfo sLParcelInfo;
        if (this.initialUpdateDone || (sLParcelInfo = this.parcelInfo) == null) {
            return;
        }
        Debug.Printf("WorldViewRenderer: making new spatial index.", new Object[0]);
        sLParcelInfo.initSpatialIndex();
        sLParcelInfo.terrainData.updateEntireTerrain();
        RenderContext renderContext = this.renderContext.get();
        if (renderContext != null) {
            renderContext.drawableStore.spatialObjectIndex.completeInitialUpdate();
        }
        this.initialUpdateDone = true;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        onPrepareFrame(null);
        onDrawFrame(gl10, null, null, null, null, null, 0);
        onFinishFrame();
    }

    public synchronized void onDrawFrame(GL10 gl10, @Nullable HeadTransformCompat headTransformCompat, @Nullable float[] fArr, @Nullable int[] iArr, @Nullable float[] fArr2, @Nullable float[] fArr3, int i) {
        if (this.drawingEnabled.get()) {
            RenderContext renderContext = this.renderContext.get();
            if (renderContext == null) {
                return;
            }
            if (fArr2 != null && headTransformCompat != null) {
                renderContext.glModelResetIdentity();
                if (fArr3 != null) {
                    renderContext.setActiveProjectionMatrix(fArr3, i);
                } else {
                    renderContext.setActiveProjectionMatrix(renderContext.projectionMatrix);
                }
                renderContext.glModelMultMatrixf(fArr2, 0);
                renderContext.glModelRotatef((-headTransformCompat.viewExtraYaw) + 90.0f, 0.0f, 1.0f, 0.0f);
                renderContext.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                renderContext.glModelTranslatef(-renderContext.frameCamera.x, -renderContext.frameCamera.y, -renderContext.frameCamera.z);
            }
            if (renderContext.hasGL20) {
                if (renderContext.useFXAA && this.Framebuffers != null && this.Colorbuffers != null) {
                    this.systemFramebuffer[0] = 0;
                    GLES20.glGetIntegerv(36006, this.systemFramebuffer, 0);
                    GLES20.glBindFramebuffer(36160, this.Framebuffers[0]);
                    GLES20.glBindTexture(3553, this.Colorbuffers[0]);
                    GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.Colorbuffers[0], 0);
                    if (iArr != null) {
                        GLES20.glViewport(0, 0, renderContext.viewportRect[2], renderContext.viewportRect[3]);
                    }
                } else if (iArr != null) {
                    GLES20.glViewport(iArr[0], iArr[1], iArr[2], iArr[3]);
                }
                GLES20.glEnable(2884);
                GLES20.glEnable(2929);
                GLES20.glEnable(3042);
                GLES20.glBlendFunc(770, 771);
                GLES20.glClear(256);
            } else {
                GLES10.glEnable(2884);
                GLES10.glEnable(2929);
                GLES10.glEnable(3042);
                GLES10.glEnable(3008);
                GLES10.glAlphaFunc(516, 0.4f);
                GLES10.glBlendFunc(770, 771);
                GLES10.glClear(16640);
                GLES10.glTexEnvf(8960, 8704, 8448.0f);
                if (iArr != null) {
                    GLES20.glViewport(iArr[0], iArr[1], iArr[2], iArr[3]);
                }
            }
            boolean z = true;
            int i2 = this.firstFrameCount.get();
            if (i2 > 0) {
                Debug.Log("onDrawFrame: drawing empty first frame!");
                this.firstFrameCount.set(i2 - 1);
                z = false;
            }
            if (this.parcelInfo == null || (!z)) {
                GLES20.glBindFramebuffer(36160, this.systemFramebuffer[0]);
                return;
            }
            try {
                if (this.drawingEnabled.get() && this.currentDrawList != null && this.currentFrustrumInfo != null) {
                    if (fArr2 == null && fArr != null) {
                        renderContext.glModelTranslatef(fArr[0], fArr[1], fArr[2]);
                    }
                    renderContext.initAllPrimPrograms(true);
                    renderContext.curPrimProgram = null;
                    DrawList drawList = this.currentDrawList;
                    if (renderContext.hasGL20) {
                        GLES20.glDisable(3042);
                    } else {
                        GLES10.glDisable(3042);
                        GLES10.glDisable(3008);
                    }
                    ArrayList<DrawableObject> arrayList = drawList.objects;
                    int[] iArr2 = drawList.renderPasses;
                    int size = arrayList.size();
                    renderContext.clearFaceTexture();
                    for (int i3 = 0; i3 < size; i3++) {
                        iArr2[i3] = arrayList.get(i3).Draw(renderContext, 1);
                    }
                    renderContext.curPrimProgram = null;
                    renderContext.clearFaceTexture();
                    DrawableTerrainPatch.GLPrepare(renderContext);
                    for (DrawableTerrainPatch drawableTerrainPatch : drawList.terrain) {
                        drawableTerrainPatch.GLDraw(renderContext);
                    }
                    renderContext.curPrimProgram = null;
                    renderContext.clearFaceTexture();
                    if (renderContext.hasGL20) {
                        GLES20.glEnable(3042);
                    } else {
                        GLES10.glEnable(3042);
                        GLES10.glEnable(3008);
                    }
                    for (DrawableAvatar drawableAvatar : drawList.avatars) {
                        if (!(drawableAvatar == drawList.myAvatar ? this.ownAvatarHidden : false)) {
                            drawableAvatar.Draw(renderContext);
                        }
                    }
                    if (renderContext.hasGL20) {
                        renderContext.windlightSky.GLDraw(renderContext, this.cameraParams.getHeading(), this.cameraParams.getTilt());
                    }
                    renderContext.curPrimProgram = null;
                    renderContext.clearFaceTexture();
                    for (int i4 = size - 1; i4 >= 0; i4--) {
                        if ((iArr2[i4] & 2) != 0) {
                            arrayList.get(i4).Draw(renderContext, 2);
                        }
                    }
                    if (renderContext.hasGL30) {
                        BoundingBox.PrepareOcclusionQueries(renderContext);
                        for (DrawableObject drawableObject : arrayList) {
                            drawableObject.TestOcclusion(renderContext, this.currentFrustrumInfo.mvpMatrix);
                        }
                        BoundingBox.EndOcclusionQueries(renderContext);
                    }
                    if (this.screenshotHandler != null) {
                        takeScreenshot(renderContext, this.screenshotHandler);
                        this.screenshotHandler = null;
                    }
                    if (this.drawPickedObject != null && (!this.drawPickedObject.isAvatar())) {
                        for (DrawableObject drawableObject2 : drawList.objects) {
                            drawableObject2.DrawIfPicked(renderContext, this.drawPickedObject);
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
                    for (DrawableAvatar drawableAvatar2 : drawList.avatars) {
                        if (!(drawableAvatar2 == drawList.myAvatar ? this.ownAvatarHidden : false)) {
                            drawableAvatar2.DrawNameTag(renderContext);
                        }
                    }
                    for (DrawableAvatarStub drawableAvatarStub : drawList.avatarStubs) {
                        drawableAvatarStub.DrawNameTag(renderContext);
                    }
                    if (this.hoverTextEnableObjects) {
                        for (DrawableObject drawableObject3 : drawList.objects) {
                            drawableObject3.DrawHoverText(renderContext, false);
                        }
                    }
                    renderContext.quad.EndDrawQuads(renderContext);
                    renderContext.curPrimProgram = null;
                    if (this.displayedHUDid != 0) {
                        TouchHUDEvent remove = this.touchHUDEvents.size() >= 1 ? this.touchHUDEvents.remove(0) : null;
                        if (renderContext.hasGL20) {
                            GLES20.glClear(256);
                            renderContext.initAllPrimPrograms(false);
                        } else {
                            GLES10.glClear(256);
                        }
                        renderContext.setActiveProjectionMatrix(renderContext.projectionHUDMatrix);
                        renderContext.glModelRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        renderContext.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                        DrawableAvatar drawableAvatar3 = drawList.myAvatar;
                        if (drawableAvatar3 != null) {
                            drawableAvatar3.setDisplayedHUDid(this.displayedHUDid);
                            DrawableHUD drawableHUD = drawableAvatar3.getDrawableHUD();
                            if (drawableHUD != null) {
                                ObjectIntersectInfo Draw = drawableHUD.Draw(renderContext, this.hudScaleFactor, this.hudOffsetX, this.hudOffsetY, remove, false);
                                if (Draw != null) {
                                    handleHUDTouch(renderContext, drawableHUD, remove, Draw);
                                }
                                if (renderContext.hasGL20) {
                                    GLES20.glDisable(2929);
                                } else {
                                    GLES10.glDisable(2929);
                                    GLES10.glMatrixMode(5889);
                                    GLES10.glLoadIdentity();
                                    GLES10.glMatrixMode(5888);
                                }
                                if (this.hoverTextEnableHUDs) {
                                    renderContext.quad.PrepareDrawQuads(renderContext);
                                    drawableHUD.Draw(renderContext, this.hudScaleFactor, this.hudOffsetX, this.hudOffsetY, null, true);
                                    renderContext.quad.EndDrawQuads(renderContext);
                                }
                            }
                        }
                    } else {
                        this.touchHUDEvents.clear();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                Debug.Warning(e);
            } catch (ConcurrentModificationException e2) {
                Debug.Warning(e2);
            }
            if (renderContext.hasGL20 && renderContext.useFXAA && this.Colorbuffers != null) {
                GLES20.glBindFramebuffer(36160, this.systemFramebuffer[0]);
                if (iArr != null) {
                    GLES20.glViewport(iArr[0], iArr[1], iArr[2], iArr[3]);
                }
                GLES20.glDisable(2929);
                GLES20.glDisable(3042);
                GLES20.glUseProgram(renderContext.fxaaProgram.getHandle());
                GLES20.glBindTexture(3553, this.Colorbuffers[0]);
                GLES20.glActiveTexture(33985);
                GLES20.glBindTexture(3553, this.Colorbuffers[1]);
                GLES20.glTexParameteri(3553, 10240, 9728);
                GLES20.glTexParameteri(3553, 10241, 9728);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GLES20.glActiveTexture(33984);
                renderContext.quad.DrawSingleQuadShader(renderContext, renderContext.fxaaProgram.vPosition, renderContext.fxaaProgram.vTexCoord);
            }
        }
    }

    public void onFinishFrame() {
        this.currentDrawList = null;
        RenderContext renderContext = this.renderContext.get();
        if (renderContext == null) {
            return;
        }
        if ((this.previousFrameTime > this.lastDrawListUpdateTime + MIN_DRAW_LIST_UPDATE_INTERVAL || this.drawListUpdateFrameCount <= 0) && renderContext.drawableStore.spatialObjectIndex.updateDrawListIfNeeded()) {
            this.lastDrawListUpdateTime = this.previousFrameTime;
            this.drawListUpdateFrameCount = 4;
        }
        if (this.drawListUpdateFrameCount > 0) {
            this.drawListUpdateFrameCount--;
        }
    }

    public void onPrepareFrame(HeadTransformCompat headTransformCompat) {
        RenderContext renderContext;
        this.headTransformCompat = headTransformCompat;
        this.renderThreadExecutor.runQueuedTasks();
        if (this.drawingEnabled.get() && (renderContext = this.renderContext.get()) != null) {
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
            renderContext.ClearFrameKeeps();
            renderContext.glResourceManager.Cleanup();
            renderContext.frameCount++;
            if (renderContext.hasGL30) {
                renderContext.processOcclusionQueries();
            }
            renderContext.glModelResetIdentity();
            renderContext.setActiveProjectionMatrix(renderContext.projectionMatrix);
            if (this.avatarControl != null) {
                if (headTransformCompat != null) {
                    this.avatarControl.getVRCamera(headTransformCompat, renderContext.myAviPosition, this.cameraParams);
                    setIsFlinging(false);
                } else {
                    setIsFlinging(this.avatarControl.getAgentAndCameraPosition(renderContext.myAviPosition, this.cameraParams));
                }
            }
            renderContext.frameCamera.set(this.cameraParams.getPosition());
            if (headTransformCompat != null) {
                renderContext.glModelMultMatrixf(headTransformCompat.headTransformMatrix, 0);
                renderContext.glModelRotatef((-headTransformCompat.viewExtraYaw) + 90.0f, 0.0f, 1.0f, 0.0f);
                renderContext.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            } else {
                renderContext.glModelRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                float tilt = this.cameraParams.getTilt();
                if (tilt != 0.0f) {
                    renderContext.glModelRotatef(tilt, 1.0f, 0.0f, 0.0f);
                }
                renderContext.glModelRotatef((-this.cameraParams.getHeading()) + 90.0f, 0.0f, 0.0f, 1.0f);
            }
            renderContext.glModelTranslatef(-renderContext.frameCamera.x, -renderContext.frameCamera.y, -renderContext.frameCamera.z);
            FrustrumInfo frustrumInfo = renderContext.hasGL20 ? new FrustrumInfo(renderContext.frameCamera.x, renderContext.frameCamera.y, renderContext.frameCamera.z, this.drawDistance, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset()) : new FrustrumInfo(renderContext.frameCamera.x, renderContext.frameCamera.y, renderContext.frameCamera.z, this.drawDistance, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset());
            if (!Objects.equal(this.currentFrustrumInfo, frustrumInfo)) {
                this.currentFrustrumInfo = frustrumInfo;
                renderContext.drawableStore.spatialObjectIndex.setViewport(this.currentFrustrumInfo, new FrustrumPlanes(this.currentFrustrumInfo.mvpMatrix));
            }
            if (!this.isResponsiveMode) {
                renderContext.RunLoadQueue();
            }
            if (this.parcelInfo != null) {
                renderContext.underWater = this.parcelInfo.terrainData.isUnderWater(renderContext.frameCamera.z);
                renderContext.waterTime = ((float) (this.thisFrameTime % 1000000)) / 1000.0f;
                if (renderContext.hasGL20) {
                    float f = this.forcedTime;
                    if (Float.isNaN(f)) {
                        if (this.parcelInfo.getSunHour(this.simSunHour, Float.isNaN(this.simSunHour[0]))) {
                            Debug.Printf("Windlight: using sim hour of %f", Float.valueOf(this.simSunHour[0]));
                            renderContext.windlightDay.InterpolatePreset(renderContext.windlightPreset, this.simSunHour[0]);
                        }
                    } else if (Float.isNaN(this.simSunHour[0]) || this.simSunHour[0] != f) {
                        this.simSunHour[0] = f;
                        Debug.Printf("Windlight: using forced hour of %f", Float.valueOf(this.simSunHour[0]));
                        renderContext.windlightDay.InterpolatePreset(renderContext.windlightPreset, this.simSunHour[0]);
                    }
                }
            }
            this.currentDrawList = renderContext.drawableStore.spatialObjectIndex.getObjectsInFrustrum();
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
        RenderContext andSet = this.renderContext.getAndSet(null);
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

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        RenderContext renderContext = this.renderContext.get();
        if (renderContext == null) {
            return;
        }
        renderContext.viewportRect[0] = 0;
        renderContext.viewportRect[1] = 0;
        renderContext.viewportRect[2] = i;
        renderContext.viewportRect[3] = i2;
        if (renderContext.hasGL20) {
            GLES20.glViewport(0, 0, i, i2);
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
                            GLES20.glTexImage2D(3553, 0, 6407, i, i2, 0, 6407, 33635, null);
                        } else {
                            GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
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
                GLES20.glUseProgram(renderContext.fxaaProgram.getHandle());
                GLES20.glUniform1i(renderContext.fxaaProgram.textureSampler, 0);
                GLES20.glUniform1i(renderContext.fxaaProgram.noAAtextureSampler, 1);
                GLES20.glUniform2f(renderContext.fxaaProgram.texcoordOffset, 1.0f / i, 1.0f / i2);
                float[] fArr = new float[16];
                android.opengl.Matrix.setIdentityM(fArr, 0);
                android.opengl.Matrix.scaleM(fArr, 0, 2.0f, 2.0f, 1.0f);
                GLES20.glUniformMatrix4fv(renderContext.fxaaProgram.uMVPMatrix, 1, false, fArr, 0);
                GLES20.glBindFramebuffer(36160, this.systemFramebuffer[0]);
            }
        } else {
            GLES10.glViewport(0, 0, i, i2);
            GLES10.glMatrixMode(5889);
            GLES10.glLoadIdentity();
        }
        renderContext.aspectRatio = i / i2;
        renderContext.FOVAngle = 60.0f;
        renderContext.getClass();
        float tan = ((float) Math.tan((renderContext.FOVAngle * 3.141592653589793d) / 360.0d)) * 0.5f;
        float f = this.drawDistance;
        Debug.Log("Renderer: Using drawDistance = " + this.drawDistance);
        renderContext.projectionMatrix.reset();
        renderContext.getClass();
        renderContext.projectionMatrix.glFrustumf((-renderContext.aspectRatio) * tan, renderContext.aspectRatio * tan, -tan, tan, 0.5f, f);
        renderContext.drawDistance = this.drawDistance;
        renderContext.projectionHUDMatrix.reset();
        renderContext.projectionHUDMatrix.glOrthof((-renderContext.aspectRatio) * 1.0f, renderContext.aspectRatio * 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        if (renderContext.windlightSky != null) {
            renderContext.windlightSky.updateMatrix(renderContext);
        }
        this.firstFrameTime = System.currentTimeMillis();
    }

    @Override // android.opengl.GLSurfaceView.Renderer
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
        for (String str : glGetString4.split(" ")) {
            if (str.equals("GL_ARB_vertex_buffer_object")) {
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
        RenderContext renderContext = new RenderContext(eGLConfig, glGetString2, this.createdGL30, this.requestGL20, z4, z2, this.avatarCountLimit, GlobalOptions.getInstance().getTerrainTextures(), this.fontSize, z, this);
        Debug.AlwaysPrintf("Renderer: created context, GL30 %b, GL20 %b", Boolean.valueOf(renderContext.hasGL30), Boolean.valueOf(renderContext.hasGL20));
        if (renderContext.hasGL20) {
            if (renderContext.getShaderCompileErrors()) {
                Debug.Printf("Renderer: Shaders did not compile well.", new Object[0]);
                if (this.stateHandler != null) {
                    this.stateHandler.sendEmptyMessage(4);
                }
                this.drawingEnabled.set(false);
            }
            Debug.Printf("Renderer: Basic geometry program = %d", Integer.valueOf(renderContext.primProgram.getHandle()));
            GLES20.glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
            if (renderContext.useFXAA) {
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
        this.renderContext.set(renderContext);
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
        RenderContext renderContext = this.renderContext.get();
        if (renderContext != null) {
            renderContext.drawDistance = i;
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
