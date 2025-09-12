// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Handler;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.avatar.DrawableHUD;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.shaders.FXAAProgram;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.RawShaderProgram;
import com.lumiyaviewer.lumiya.render.spatial.DrawList;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumInfo;
import com.lumiyaviewer.lumiya.render.spatial.FrustrumPlanes;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex;
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightDay;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            SynchronousExecutor, RenderContext, TouchHUDEvent, DrawableObject, 
//            MatrixStack, Quad, DrawableStore, HeadTransformCompat, 
//            WindlightSky, BoundingBox, TextureMemoryTracker

public class WorldViewRenderer
    implements android.opengl.GLSurfaceView.Renderer, android.opengl.GLSurfaceView.EGLContextFactory
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
    private int Colorbuffers[];
    private int Framebuffers[];
    private int Renderbuffers[];
    private final SubscriptionData agentCircuit;
    private SLAvatarControl avatarControl;
    private int avatarCountLimit;
    private final CameraParams cameraParams = new CameraParams();
    private boolean createdGL30;
    private DrawList currentDrawList;
    private FrustrumInfo currentFrustrumInfo;
    private volatile int displayedHUDid;
    private int drawDistance;
    private int drawListUpdateFrameCount;
    private SLObjectInfo drawPickedObject;
    private AtomicBoolean drawingEnabled;
    private final float extTextureHitVector[] = {
        0.0F, 0.0F, 0.0F, 1.0F
    };
    private final float extTextureMatrix[] = new float[64];
    private final float extTextureResultVector[] = new float[4];
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
    private final Runnable initSpatialIndexRunnable = new _2D_.Lambda._cls8oUVvA5ObkigeJxIgo2HrzT6_jA._cls1(this);
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
    private SLParcelInfo parcelInfo;
    private Handler pickHandler;
    private final Object pickLock = new Object();
    private long previousFrameTime;
    private final AtomicReference renderContext = new AtomicReference();
    private final SynchronousExecutor renderThreadExecutor = new SynchronousExecutor();
    private final boolean requestGL20;
    private final Object responsiveModeLock = new Object();
    private Handler screenshotHandler;
    private float simSunHour[] = {
        (0.0F / 0.0F)
    };
    private final Handler stateHandler;
    private final int systemFramebuffer[] = new int[1];
    private long thisFrameTime;
    private List touchHUDEvents;
    private Handler touchHandler;

    public WorldViewRenderer(Handler handler, boolean flag, UserManager usermanager, int i)
    {
        agentCircuit = new SubscriptionData(renderThreadExecutor, new _2D_.Lambda._cls8oUVvA5ObkigeJxIgo2HrzT6_jA(this));
        avatarControl = null;
        parcelInfo = null;
        initialUpdateDone = false;
        drawingEnabled = new AtomicBoolean(true);
        firstFrameCount = new AtomicInteger(1);
        lastDrawListUpdateTime = 0L;
        drawListUpdateFrameCount = 0;
        drawPickedObject = null;
        forcedTime = (0.0F / 0.0F);
        needPickObject = false;
        needPickX = 0.0F;
        needPickY = 0.0F;
        pickHandler = null;
        touchHUDEvents = Collections.synchronizedList(new LinkedList());
        touchHandler = null;
        ownAvatarHidden = false;
        drawDistance = 20;
        avatarCountLimit = 5;
        isInteracting = false;
        isFlinging = false;
        isResponsiveMode = false;
        screenshotHandler = null;
        displayedHUDid = 0;
        hudScaleFactor = 1.0F;
        hudOffsetX = 0.0F;
        hudOffsetY = 0.0F;
        hoverTextEnableHUDs = true;
        hoverTextEnableObjects = false;
        createdGL30 = false;
        Framebuffers = null;
        Renderbuffers = null;
        Colorbuffers = null;
        headTransformCompat = null;
        thisFrameTime = 0L;
        fpsFrameCount = 0;
        previousFrameTime = 0L;
        currentDrawList = null;
        stateHandler = handler;
        requestGL20 = flag;
        fontSize = i;
        agentCircuit.subscribe(UserManager.agentCircuits(), usermanager.getUserID());
    }

    private void handleHUDTouch(RenderContext rendercontext, DrawableHUD drawablehud, TouchHUDEvent touchhudevent, ObjectIntersectInfo objectintersectinfo)
    {
        float f1 = rendercontext.viewportRect[2];
        float f2 = rendercontext.viewportRect[2];
        float f = (f2 / 2.0F - touchhudevent.y) / f2;
        f1 = (f1 / 2.0F - touchhudevent.x) / f2;
        rendercontext = drawablehud.getAttachmentPoint();
        f2 = ((SLAttachmentPoint) (rendercontext)).position.y;
        float f3 = ((SLAttachmentPoint) (rendercontext)).position.z;
        try
        {
            rendercontext = touchHandler;
        }
        // Misplaced declaration of an exception variable
        catch (RenderContext rendercontext)
        {
            Debug.Warning(rendercontext);
            return;
        }
        if (rendercontext == null)
        {
            break MISSING_BLOCK_LABEL_93;
        }
        rendercontext.sendMessage(rendercontext.obtainMessage(2, objectintersectinfo.objInfo));
        ((SLAgentCircuit)agentCircuit.get()).TouchObjectFace(objectintersectinfo.objInfo, objectintersectinfo.intersectInfo.faceID, 0.0F, f2 + f1, f + f3, objectintersectinfo.intersectInfo.u, objectintersectinfo.intersectInfo.v, objectintersectinfo.intersectInfo.s, objectintersectinfo.intersectInfo.t);
        return;
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        if (slagentcircuit != null)
        {
            Debug.Printf("WorldViewRenderer: got new agentCircuit.", new Object[0]);
            initialUpdateDone = false;
            avatarControl = slagentcircuit.getModules().avatarControl;
            parcelInfo = slagentcircuit.getGridConnection().parcelInfo;
            initialUpdateDone = false;
            RenderContext rendercontext = (RenderContext)renderContext.get();
            if (rendercontext != null)
            {
                rendercontext.setMeshCapURL(slagentcircuit.getCaps().getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GetMesh));
                if (parcelInfo != null)
                {
                    PrimComputeExecutor.getInstance().execute(initSpatialIndexRunnable);
                }
            }
            return;
        } else
        {
            avatarControl = null;
            parcelInfo = null;
            return;
        }
    }

    private void processObjectPick()
    {
        float f1;
        boolean flag;
        flag = false;
        f1 = (0.0F / 0.0F);
        Object obj = pickLock;
        obj;
        JVM INSTR monitorenter ;
        if (!needPickObject) goto _L2; else goto _L1
_L1:
        float f;
        Handler handler;
        needPickObject = false;
        f = needPickX;
        f1 = needPickY;
        handler = pickHandler;
        flag = true;
_L12:
        obj;
        JVM INSTR monitorexit ;
        if (!flag) goto _L4; else goto _L3
_L3:
        RenderContext rendercontext = (RenderContext)renderContext.get();
        if (rendercontext == null) goto _L4; else goto _L5
_L5:
        Object obj2 = currentDrawList.objects.iterator();
        obj = null;
_L7:
        Object obj1 = obj;
        if (!((Iterator) (obj2)).hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj1 = obj;
        obj = tryPickObject(rendercontext, f, f1, (DrawableObject)((Iterator) (obj2)).next(), ((ObjectIntersectInfo) (obj)));
        if (true) goto _L7; else goto _L6
        obj1;
        throw obj1;
_L6:
        obj1 = obj;
        Iterator iterator = currentDrawList.avatars.iterator();
_L9:
        obj2 = obj;
        obj1 = obj;
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj1 = obj;
        obj = tryPickObject(rendercontext, f, f1, (DrawableAvatar)iterator.next(), ((ObjectIntersectInfo) (obj)));
        if (true) goto _L9; else goto _L8
        Exception exception;
        exception;
        obj1 = null;
_L10:
        Debug.Warning(exception);
        obj2 = obj1;
_L8:
        if (obj2 != null && handler != null)
        {
            handler.sendMessage(handler.obtainMessage(1, obj2));
        }
_L4:
        return;
        exception;
        if (true) goto _L10; else goto _L2
_L2:
        handler = null;
        f = (0.0F / 0.0F);
        if (true) goto _L12; else goto _L11
_L11:
    }

    private void setIsFlinging(boolean flag)
    {
        boolean flag1 = false;
        Object obj = responsiveModeLock;
        obj;
        JVM INSTR monitorenter ;
        if (isFlinging == flag)
        {
            break MISSING_BLOCK_LABEL_26;
        }
        isFlinging = flag;
        flag1 = true;
        obj;
        JVM INSTR monitorexit ;
        if (flag1)
        {
            updateResponsive();
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private void takeScreenshot(RenderContext rendercontext, Handler handler)
    {
        int i = 0;
        int j = rendercontext.viewportRect[2];
        int l = rendercontext.viewportRect[3];
        Object obj = new DirectByteBuffer(j * l * 4);
        GLES10.glReadPixels(0, 0, j, l, 6408, 5121, ((DirectByteBuffer) (obj)).asByteBuffer());
        rendercontext = Bitmap.createBitmap(j, l, android.graphics.Bitmap.Config.ARGB_8888);
        rendercontext.copyPixelsFromBuffer(((DirectByteBuffer) (obj)).asByteBuffer());
        obj = Bitmap.createBitmap(j, l, android.graphics.Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(((Bitmap) (obj)));
        Matrix matrix = new Matrix();
        matrix.setScale(1.0F, -1F);
        matrix.postTranslate(0.0F, l);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(rendercontext, matrix, paint);
        rendercontext.recycle();
        paint.setTextSize(fontSize);
        paint.setSubpixelText(true);
        paint.setTextAlign(android.graphics.Paint.Align.LEFT);
        paint.setColor(-1);
        rendercontext = new String[2];
        rendercontext[0] = "Lumiya Viewer";
        rendercontext[1] = "http://lumiyaviewer.com";
        float f = (float)fontSize * 2.0F;
        for (int k = rendercontext.length; i < k; i++)
        {
            canvas.drawText(rendercontext[i], fontSize, f, paint);
            f = f + (paint.descent() - paint.ascent()) + (float)fontSize * 0.5F;
        }

        handler.sendMessage(handler.obtainMessage(5, obj));
    }

    private ObjectIntersectInfo tryPickObject(RenderContext rendercontext, float f, float f1, IntersectPickable intersectpickable, ObjectIntersectInfo objectintersectinfo)
    {
        rendercontext = intersectpickable.PickObject(rendercontext, f, f1, -0.9F);
        if (rendercontext != null)
        {
            if (objectintersectinfo == null)
            {
                return rendercontext;
            }
            if (((ObjectIntersectInfo) (rendercontext)).pickDepth < objectintersectinfo.pickDepth)
            {
                return rendercontext;
            }
        }
        return objectintersectinfo;
    }

    private void updateResponsive()
    {
        Object obj = responsiveModeLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag;
        boolean flag1;
        flag1 = isResponsiveMode;
        if (isInteracting)
        {
            break MISSING_BLOCK_LABEL_49;
        }
        flag = isFlinging;
_L1:
        isResponsiveMode = flag;
        obj;
        JVM INSTR monitorexit ;
        if (flag1 != flag)
        {
            if (!flag)
            {
                break MISSING_BLOCK_LABEL_59;
            }
            PrimComputeExecutor.getInstance().pause();
        }
        return;
        flag = true;
          goto _L1
        Exception exception;
        exception;
        throw exception;
        PrimComputeExecutor.getInstance().resume();
        return;
    }

    void _2D_com_lumiyaviewer_lumiya_render_WorldViewRenderer_2D_mthref_2D_0(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    public EGLContext createContext(EGL10 egl10, EGLDisplay egldisplay, EGLConfig eglconfig)
    {
        Debug.Printf("EGL: createContext called.", new Object[0]);
        if (requestGL20 && android.os.Build.VERSION.SDK_INT >= 18)
        {
            Debug.Printf("EGL: trying to create 3.0 context.", new Object[0]);
            EGLContext eglcontext = egl10.eglCreateContext(egldisplay, eglconfig, EGL10.EGL_NO_CONTEXT, new int[] {
                12440, 3, 12344
            });
            if (eglcontext != null && eglcontext != EGL10.EGL_NO_CONTEXT)
            {
                Debug.Printf("EGL: 3.0 context apparently created.", new Object[0]);
                createdGL30 = true;
                return eglcontext;
            }
            Debug.Printf("EGL: Failed to create 3.0 context.", new Object[0]);
        }
        Debug.Printf("EGL: Creating regular context.", new Object[0]);
        createdGL30 = false;
        int ai[] = {
            12440, 2, 12344
        };
        EGLContext eglcontext1 = EGL10.EGL_NO_CONTEXT;
        if (!requestGL20)
        {
            ai = null;
        }
        return egl10.eglCreateContext(egldisplay, eglconfig, eglcontext1, ai);
    }

    public void destroyContext(EGL10 egl10, EGLDisplay egldisplay, EGLContext eglcontext)
    {
        Debug.Printf("EGL: destroyContext called.", new Object[0]);
        onRendererShutdown();
        if (!egl10.eglDestroyContext(egldisplay, eglcontext))
        {
            throw new RuntimeException(String.format("EGLError code %d", new Object[] {
                Integer.valueOf(egl10.eglGetError())
            }));
        } else
        {
            Debug.Printf("EGL: destroyContext exiting.", new Object[0]);
            return;
        }
    }

    public void disableDrawing()
    {
        drawingEnabled.set(false);
    }

    public void drawCrosshair(float f, float f1)
    {
        RenderContext rendercontext = (RenderContext)renderContext.get();
        if (rendercontext != null && rendercontext.crosshairTexture != null)
        {
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glEnable(3042);
            GLES20.glUseProgram(rendercontext.rawShaderProgram.getHandle());
            GLES20.glActiveTexture(33984);
            rendercontext.crosshairTexture.GLDraw();
            GLES20.glUniform1i(rendercontext.rawShaderProgram.textureSampler, 0);
            android.opengl.Matrix.setIdentityM(extTextureMatrix, 0);
            GLES20.glUniformMatrix4fv(rendercontext.rawShaderProgram.vTextureTransformMatrix, 1, false, extTextureMatrix, 0);
            android.opengl.Matrix.translateM(extTextureMatrix, 0, 0.0F, 0.0F, -1.9F);
            android.opengl.Matrix.translateM(extTextureMatrix, 16, extTextureMatrix, 0, -f1, 0.0F, 0.0F);
            android.opengl.Matrix.scaleM(extTextureMatrix, 16, f, f, 1.0F);
            android.opengl.Matrix.multiplyMM(extTextureMatrix, 32, rendercontext.projectionMatrix.getMatrixData(), rendercontext.projectionMatrix.getMatrixDataOffset(), extTextureMatrix, 16);
            GLES20.glUniformMatrix4fv(rendercontext.rawShaderProgram.uMVPMatrix, 1, false, extTextureMatrix, 32);
            rendercontext.quad.DrawSingleQuadShader(rendercontext, rendercontext.rawShaderProgram.vPosition, rendercontext.rawShaderProgram.vTexCoord);
        }
    }

    public void drawExternalTexture(GLExternalTexture glexternaltexture, float af[], float f, float f1, float f2, float f3, float f4, 
            float af1[], int i)
    {
        RenderContext rendercontext = (RenderContext)renderContext.get();
        if (rendercontext != null && rendercontext.extTextureProgram != null)
        {
            glexternaltexture.update(af);
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glEnable(3042);
            GLES20.glUseProgram(rendercontext.extTextureProgram.getHandle());
            GLES20.glActiveTexture(33984);
            glexternaltexture.bind();
            GLES20.glUniform1i(rendercontext.extTextureProgram.textureSampler, 0);
            GLES20.glUniformMatrix4fv(rendercontext.extTextureProgram.vTextureTransformMatrix, 1, false, af, 0);
            android.opengl.Matrix.setIdentityM(extTextureMatrix, 0);
            android.opengl.Matrix.rotateM(extTextureMatrix, 0, -f1, 1.0F, 0.0F, 0.0F);
            android.opengl.Matrix.rotateM(extTextureMatrix, 0, -f2, 0.0F, 1.0F, 0.0F);
            android.opengl.Matrix.translateM(extTextureMatrix, 0, 0.0F, 0.0F, -2F);
            android.opengl.Matrix.translateM(extTextureMatrix, 16, extTextureMatrix, 0, -f, 0.0F, 0.0F);
            android.opengl.Matrix.scaleM(extTextureMatrix, 16, f3, f4, 1.0F);
            android.opengl.Matrix.multiplyMM(extTextureMatrix, 32, rendercontext.projectionMatrix.getMatrixData(), rendercontext.projectionMatrix.getMatrixDataOffset(), extTextureMatrix, 16);
            android.opengl.Matrix.invertM(extTextureMatrix, 48, extTextureMatrix, 32);
            android.opengl.Matrix.multiplyMV(extTextureResultVector, 0, extTextureMatrix, 48, extTextureHitVector, 0);
            af1[i] = extTextureResultVector[0];
            af1[i + 1] = extTextureResultVector[1];
            GLES20.glUniformMatrix4fv(rendercontext.extTextureProgram.uMVPMatrix, 1, false, extTextureMatrix, 32);
            rendercontext.quad.DrawSingleQuadShader(rendercontext, rendercontext.extTextureProgram.vPosition, rendercontext.extTextureProgram.vTexCoord);
        }
    }

    public void enableDrawing()
    {
        if (!drawingEnabled.getAndSet(true))
        {
            firstFrameCount.set(1);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_render_WorldViewRenderer_6642()
    {
        if (!initialUpdateDone)
        {
            Object obj = parcelInfo;
            if (obj != null)
            {
                Debug.Printf("WorldViewRenderer: making new spatial index.", new Object[0]);
                ((SLParcelInfo) (obj)).initSpatialIndex();
                ((SLParcelInfo) (obj)).terrainData.updateEntireTerrain();
                obj = (RenderContext)renderContext.get();
                if (obj != null)
                {
                    ((RenderContext) (obj)).drawableStore.spatialObjectIndex.completeInitialUpdate();
                }
                initialUpdateDone = true;
            }
        }
    }

    public void onDrawFrame(GL10 gl10)
    {
        onPrepareFrame(null);
        onDrawFrame(gl10, null, null, null, null, null, 0);
        onFinishFrame();
    }

    public void onDrawFrame(GL10 gl10, HeadTransformCompat headtransformcompat, float af[], int ai[], float af1[], float af2[], int i)
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = drawingEnabled.get();
        if (flag)
        {
            break MISSING_BLOCK_LABEL_19;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        RenderContext rendercontext = (RenderContext)renderContext.get();
        if (rendercontext != null)
        {
            break MISSING_BLOCK_LABEL_39;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        if (af1 == null || headtransformcompat == null) goto _L2; else goto _L1
_L1:
        rendercontext.glModelResetIdentity();
        if (af2 == null) goto _L4; else goto _L3
_L3:
        rendercontext.setActiveProjectionMatrix(af2, i);
_L9:
        rendercontext.glModelMultMatrixf(af1, 0);
        rendercontext.glModelRotatef(-headtransformcompat.viewExtraYaw + 90F, 0.0F, 1.0F, 0.0F);
        rendercontext.glModelRotatef(-90F, 1.0F, 0.0F, 0.0F);
        rendercontext.glModelTranslatef(-rendercontext.frameCamera.x, -rendercontext.frameCamera.y, -rendercontext.frameCamera.z);
_L2:
        if (!rendercontext.hasGL20) goto _L6; else goto _L5
_L5:
        if (!rendercontext.useFXAA || Framebuffers == null || Colorbuffers == null) goto _L8; else goto _L7
_L7:
        systemFramebuffer[0] = 0;
        GLES20.glGetIntegerv(36006, systemFramebuffer, 0);
        GLES20.glBindFramebuffer(36160, Framebuffers[0]);
        GLES20.glBindTexture(3553, Colorbuffers[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, Colorbuffers[0], 0);
        if (ai == null)
        {
            break MISSING_BLOCK_LABEL_250;
        }
        GLES20.glViewport(0, 0, rendercontext.viewportRect[2], rendercontext.viewportRect[3]);
_L11:
        GLES20.glEnable(2884);
        GLES20.glEnable(2929);
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        GLES20.glClear(256);
_L13:
        i = 1;
        int j = firstFrameCount.get();
        if (j <= 0)
        {
            break MISSING_BLOCK_LABEL_320;
        }
        Debug.Log("onDrawFrame: drawing empty first frame!");
        firstFrameCount.set(j - 1);
        i = 0;
        if (parcelInfo != null && (i ^ 1) == 0)
        {
            break MISSING_BLOCK_LABEL_481;
        }
        GLES20.glBindFramebuffer(36160, systemFramebuffer[0]);
        this;
        JVM INSTR monitorexit ;
        return;
_L4:
        rendercontext.setActiveProjectionMatrix(rendercontext.projectionMatrix);
          goto _L9
        gl10;
        throw gl10;
_L8:
        if (ai == null) goto _L11; else goto _L10
_L10:
        GLES20.glViewport(ai[0], ai[1], ai[2], ai[3]);
          goto _L11
_L6:
        GLES10.glEnable(2884);
        GLES10.glEnable(2929);
        GLES10.glEnable(3042);
        GLES10.glEnable(3008);
        GLES10.glAlphaFunc(516, 0.4F);
        GLES10.glBlendFunc(770, 771);
        GLES10.glClear(16640);
        GLES10.glTexEnvf(8960, 8704, 8448F);
        if (ai == null) goto _L13; else goto _L12
_L12:
        GLES20.glViewport(ai[0], ai[1], ai[2], ai[3]);
          goto _L13
        if (!drawingEnabled.get() || currentDrawList == null || currentFrustrumInfo == null) goto _L15; else goto _L14
_L14:
        if (af1 != null || af == null)
        {
            break MISSING_BLOCK_LABEL_528;
        }
        rendercontext.glModelTranslatef(af[0], af[1], af[2]);
        rendercontext.initAllPrimPrograms(true);
        rendercontext.curPrimProgram = null;
        headtransformcompat = currentDrawList;
        if (!rendercontext.hasGL20) goto _L17; else goto _L16
_L16:
        GLES20.glDisable(3042);
_L20:
        gl10 = ((DrawList) (headtransformcompat)).objects;
        af = ((DrawList) (headtransformcompat)).renderPasses;
        j = gl10.size();
        rendercontext.clearFaceTexture();
        i = 0;
_L19:
        if (i >= j)
        {
            break; /* Loop/switch isn't completed */
        }
        af[i] = ((DrawableObject)gl10.get(i)).Draw(rendercontext, 1);
        i++;
        if (true) goto _L19; else goto _L18
_L17:
        GLES10.glDisable(3042);
        GLES10.glDisable(3008);
          goto _L20
        gl10;
        Debug.Warning(gl10);
_L15:
        if (!rendercontext.hasGL20 || !rendercontext.useFXAA || Colorbuffers == null) goto _L22; else goto _L21
_L21:
        GLES20.glBindFramebuffer(36160, systemFramebuffer[0]);
        if (ai == null)
        {
            break MISSING_BLOCK_LABEL_697;
        }
        GLES20.glViewport(ai[0], ai[1], ai[2], ai[3]);
        GLES20.glDisable(2929);
        GLES20.glDisable(3042);
        GLES20.glUseProgram(rendercontext.fxaaProgram.getHandle());
        GLES20.glBindTexture(3553, Colorbuffers[0]);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, Colorbuffers[1]);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glActiveTexture(33984);
        rendercontext.quad.DrawSingleQuadShader(rendercontext, rendercontext.fxaaProgram.vPosition, rendercontext.fxaaProgram.vTexCoord);
_L22:
        this;
        JVM INSTR monitorexit ;
        return;
_L18:
        rendercontext.curPrimProgram = null;
        rendercontext.clearFaceTexture();
        DrawableTerrainPatch.GLPrepare(rendercontext);
        for (af1 = ((DrawList) (headtransformcompat)).terrain.iterator(); af1.hasNext(); ((DrawableTerrainPatch)af1.next()).GLDraw(rendercontext)) { }
        break MISSING_BLOCK_LABEL_896;
        gl10;
        Debug.Warning(gl10);
          goto _L15
        rendercontext.curPrimProgram = null;
        rendercontext.clearFaceTexture();
        if (!rendercontext.hasGL20) goto _L24; else goto _L23
_L23:
        GLES20.glEnable(3042);
_L31:
        af1 = ((DrawList) (headtransformcompat)).avatars.iterator();
_L30:
        if (!af1.hasNext()) goto _L26; else goto _L25
_L25:
        af2 = (DrawableAvatar)af1.next();
        if (af2 != ((DrawList) (headtransformcompat)).myAvatar) goto _L28; else goto _L27
_L27:
        flag = ownAvatarHidden;
_L46:
        if (flag) goto _L30; else goto _L29
_L29:
        af2.Draw(rendercontext);
          goto _L30
_L24:
        GLES10.glEnable(3042);
        GLES10.glEnable(3008);
          goto _L31
_L26:
        if (rendercontext.hasGL20)
        {
            rendercontext.windlightSky.GLDraw(rendercontext, cameraParams.getHeading(), cameraParams.getTilt());
        }
        rendercontext.curPrimProgram = null;
        rendercontext.clearFaceTexture();
        i = j - 1;
_L47:
        if (i < 0) goto _L33; else goto _L32
_L32:
        if ((af[i] & 2) == 0) goto _L35; else goto _L34
_L34:
        ((DrawableObject)gl10.get(i)).Draw(rendercontext, 2);
          goto _L35
_L33:
        if (!rendercontext.hasGL30)
        {
            break MISSING_BLOCK_LABEL_1139;
        }
        BoundingBox.PrepareOcclusionQueries(rendercontext);
        for (gl10 = gl10.iterator(); gl10.hasNext(); ((DrawableObject)gl10.next()).TestOcclusion(rendercontext, currentFrustrumInfo.mvpMatrix)) { }
        BoundingBox.EndOcclusionQueries(rendercontext);
        if (screenshotHandler != null)
        {
            takeScreenshot(rendercontext, screenshotHandler);
            screenshotHandler = null;
        }
        if (drawPickedObject != null && drawPickedObject.isAvatar() ^ true)
        {
            for (gl10 = ((DrawList) (headtransformcompat)).objects.iterator(); gl10.hasNext(); ((DrawableObject)gl10.next()).DrawIfPicked(rendercontext, drawPickedObject)) { }
        }
        if (!rendercontext.hasGL20)
        {
            GLES10.glMatrixMode(5889);
            GLES10.glLoadIdentity();
            GLES10.glMatrixMode(5888);
        }
        if (rendercontext.hasGL20 && rendercontext.useFXAA && Colorbuffers != null)
        {
            GLES20.glFinish();
            GLES20.glBindTexture(3553, Colorbuffers[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, Colorbuffers[1], 0);
            GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GLES20.glClear(16384);
        }
        rendercontext.quad.PrepareDrawQuads(rendercontext);
        gl10 = ((DrawList) (headtransformcompat)).avatars.iterator();
_L39:
        if (!gl10.hasNext()) goto _L37; else goto _L36
_L36:
        af = (DrawableAvatar)gl10.next();
        if (af != ((DrawList) (headtransformcompat)).myAvatar)
        {
            break MISSING_BLOCK_LABEL_1749;
        }
        flag = ownAvatarHidden;
_L48:
        if (flag) goto _L39; else goto _L38
_L38:
        af.DrawNameTag(rendercontext);
          goto _L39
_L37:
        for (gl10 = ((DrawList) (headtransformcompat)).avatarStubs.iterator(); gl10.hasNext(); ((DrawableAvatarStub)gl10.next()).DrawNameTag(rendercontext)) { }
        if (hoverTextEnableObjects)
        {
            for (gl10 = ((DrawList) (headtransformcompat)).objects.iterator(); gl10.hasNext(); ((DrawableObject)gl10.next()).DrawHoverText(rendercontext, false)) { }
        }
        rendercontext.quad.EndDrawQuads(rendercontext);
        rendercontext.curPrimProgram = null;
        if (displayedHUDid == 0)
        {
            break MISSING_BLOCK_LABEL_1722;
        }
        gl10 = null;
        if (touchHUDEvents.size() >= 1)
        {
            gl10 = (TouchHUDEvent)touchHUDEvents.remove(0);
        }
        if (!rendercontext.hasGL20) goto _L41; else goto _L40
_L40:
        GLES20.glClear(256);
        rendercontext.initAllPrimPrograms(false);
_L44:
        rendercontext.setActiveProjectionMatrix(rendercontext.projectionHUDMatrix);
        rendercontext.glModelRotatef(90F, 0.0F, 1.0F, 0.0F);
        rendercontext.glModelRotatef(-90F, 1.0F, 0.0F, 0.0F);
        headtransformcompat = ((DrawList) (headtransformcompat)).myAvatar;
        if (headtransformcompat == null) goto _L15; else goto _L42
_L42:
        headtransformcompat.setDisplayedHUDid(displayedHUDid);
        headtransformcompat = headtransformcompat.getDrawableHUD();
        if (headtransformcompat == null) goto _L15; else goto _L43
_L43:
        af = headtransformcompat.Draw(rendercontext, hudScaleFactor, hudOffsetX, hudOffsetY, gl10, false);
        if (af == null)
        {
            break MISSING_BLOCK_LABEL_1624;
        }
        handleHUDTouch(rendercontext, headtransformcompat, gl10, af);
        if (!rendercontext.hasGL20)
        {
            break MISSING_BLOCK_LABEL_1698;
        }
        GLES20.glDisable(2929);
_L45:
        if (hoverTextEnableHUDs)
        {
            rendercontext.quad.PrepareDrawQuads(rendercontext);
            headtransformcompat.Draw(rendercontext, hudScaleFactor, hudOffsetX, hudOffsetY, null, true);
            rendercontext.quad.EndDrawQuads(rendercontext);
        }
          goto _L15
_L41:
        GLES10.glClear(256);
          goto _L44
        GLES10.glDisable(2929);
        GLES10.glMatrixMode(5889);
        GLES10.glLoadIdentity();
        GLES10.glMatrixMode(5888);
          goto _L45
        touchHUDEvents.clear();
          goto _L15
_L28:
        flag = false;
          goto _L46
_L35:
        i--;
          goto _L47
        flag = false;
          goto _L48
    }

    public void onFinishFrame()
    {
        currentDrawList = null;
        RenderContext rendercontext = (RenderContext)renderContext.get();
        if (rendercontext == null)
        {
            return;
        }
        if ((previousFrameTime > lastDrawListUpdateTime + 100L || drawListUpdateFrameCount <= 0) && rendercontext.drawableStore.spatialObjectIndex.updateDrawListIfNeeded())
        {
            lastDrawListUpdateTime = previousFrameTime;
            drawListUpdateFrameCount = 4;
        }
        if (drawListUpdateFrameCount > 0)
        {
            drawListUpdateFrameCount = drawListUpdateFrameCount - 1;
        }
    }

    public void onPrepareFrame(HeadTransformCompat headtransformcompat)
    {
        headTransformCompat = headtransformcompat;
        renderThreadExecutor.runQueuedTasks();
        if (!drawingEnabled.get())
        {
            return;
        }
        Object obj = (RenderContext)renderContext.get();
        if (obj == null)
        {
            return;
        }
        long l = System.currentTimeMillis();
        long l1 = l - previousFrameTime;
        LLVector3 llvector3;
        if (l1 < 33L)
        {
            try
            {
                Thread.sleep(33L - l1);
            }
            catch (InterruptedException interruptedexception) { }
        }
        previousFrameTime = l;
        thisFrameTime = l - firstFrameTime;
        fpsFrameCount = fpsFrameCount + 1;
        if (fpsFrameCount >= 10)
        {
            fpsFrameCount = 0;
            if (thisFrameTime != lastFrameTime)
            {
                Debug.Printf("Renderer: FPS %.2f frame time %d", new Object[] {
                    Float.valueOf(10000F / (float)(thisFrameTime - lastFrameTime)), Long.valueOf(thisFrameTime - lastFrameTime)
                });
                lastFrameTime = thisFrameTime;
            }
        }
        TextureMemoryTracker.releaseFrameMemory();
        ((RenderContext) (obj)).ClearFrameKeeps();
        ((RenderContext) (obj)).glResourceManager.Cleanup();
        obj.frameCount = ((RenderContext) (obj)).frameCount + 1;
        if (((RenderContext) (obj)).hasGL30)
        {
            ((RenderContext) (obj)).processOcclusionQueries();
        }
        ((RenderContext) (obj)).glModelResetIdentity();
        ((RenderContext) (obj)).setActiveProjectionMatrix(((RenderContext) (obj)).projectionMatrix);
        if (avatarControl != null)
        {
            if (headtransformcompat != null)
            {
                avatarControl.getVRCamera(headtransformcompat, ((RenderContext) (obj)).myAviPosition, cameraParams);
                setIsFlinging(false);
            } else
            {
                setIsFlinging(avatarControl.getAgentAndCameraPosition(((RenderContext) (obj)).myAviPosition, cameraParams));
            }
        }
        llvector3 = cameraParams.getPosition();
        ((RenderContext) (obj)).frameCamera.set(llvector3);
        if (headtransformcompat != null)
        {
            ((RenderContext) (obj)).glModelMultMatrixf(headtransformcompat.headTransformMatrix, 0);
            ((RenderContext) (obj)).glModelRotatef(-headtransformcompat.viewExtraYaw + 90F, 0.0F, 1.0F, 0.0F);
            ((RenderContext) (obj)).glModelRotatef(-90F, 1.0F, 0.0F, 0.0F);
        } else
        {
            ((RenderContext) (obj)).glModelRotatef(-90F, 1.0F, 0.0F, 0.0F);
            f = cameraParams.getTilt();
            if (f != 0.0F)
            {
                ((RenderContext) (obj)).glModelRotatef(f, 1.0F, 0.0F, 0.0F);
            }
            ((RenderContext) (obj)).glModelRotatef(-cameraParams.getHeading() + 90F, 0.0F, 0.0F, 1.0F);
        }
        ((RenderContext) (obj)).glModelTranslatef(-((RenderContext) (obj)).frameCamera.x, -((RenderContext) (obj)).frameCamera.y, -((RenderContext) (obj)).frameCamera.z);
        if (((RenderContext) (obj)).hasGL20)
        {
            headtransformcompat = new FrustrumInfo(((RenderContext) (obj)).frameCamera.x, ((RenderContext) (obj)).frameCamera.y, ((RenderContext) (obj)).frameCamera.z, drawDistance, ((RenderContext) (obj)).modelViewMatrix.getMatrixData(), ((RenderContext) (obj)).modelViewMatrix.getMatrixDataOffset());
        } else
        {
            headtransformcompat = new FrustrumInfo(((RenderContext) (obj)).frameCamera.x, ((RenderContext) (obj)).frameCamera.y, ((RenderContext) (obj)).frameCamera.z, drawDistance, ((RenderContext) (obj)).modelViewMatrix.getMatrixData(), ((RenderContext) (obj)).modelViewMatrix.getMatrixDataOffset(), ((RenderContext) (obj)).projectionMatrix.getMatrixData(), ((RenderContext) (obj)).projectionMatrix.getMatrixDataOffset());
        }
        if (!Objects.equal(currentFrustrumInfo, headtransformcompat))
        {
            currentFrustrumInfo = headtransformcompat;
            headtransformcompat = new FrustrumPlanes(currentFrustrumInfo.mvpMatrix);
            ((RenderContext) (obj)).drawableStore.spatialObjectIndex.setViewport(currentFrustrumInfo, headtransformcompat);
        }
        if (!isResponsiveMode)
        {
            ((RenderContext) (obj)).RunLoadQueue();
        }
        if (parcelInfo != null)
        {
            obj.underWater = parcelInfo.terrainData.isUnderWater(((RenderContext) (obj)).frameCamera.z);
            obj.waterTime = (float)(thisFrameTime % 0xf4240L) / 1000F;
            if (((RenderContext) (obj)).hasGL20)
            {
                float f = forcedTime;
                if (Float.isNaN(f))
                {
                    if (parcelInfo.getSunHour(simSunHour, Float.isNaN(simSunHour[0])))
                    {
                        Debug.Printf("Windlight: using sim hour of %f", new Object[] {
                            Float.valueOf(simSunHour[0])
                        });
                        ((RenderContext) (obj)).windlightDay.InterpolatePreset(((RenderContext) (obj)).windlightPreset, simSunHour[0]);
                    }
                } else
                if (Float.isNaN(simSunHour[0]) || simSunHour[0] != f)
                {
                    simSunHour[0] = f;
                    Debug.Printf("Windlight: using forced hour of %f", new Object[] {
                        Float.valueOf(simSunHour[0])
                    });
                    ((RenderContext) (obj)).windlightDay.InterpolatePreset(((RenderContext) (obj)).windlightPreset, simSunHour[0]);
                }
            }
        }
        currentDrawList = ((RenderContext) (obj)).drawableStore.spatialObjectIndex.getObjectsInFrustrum();
        headtransformcompat = currentDrawList.avatars.iterator();
        do
        {
            if (!headtransformcompat.hasNext())
            {
                break;
            }
            obj = (DrawableAvatar)headtransformcompat.next();
            boolean flag;
            if (obj == currentDrawList.myAvatar)
            {
                flag = ownAvatarHidden;
            } else
            {
                flag = false;
            }
            if (!flag)
            {
                ((DrawableAvatar) (obj)).RunAnimations();
            }
        } while (true);
        processObjectPick();
    }

    public void onRendererShutdown()
    {
        PrimComputeExecutor.getInstance().resume();
        SpatialIndex.getInstance().DisableObjectIndex(this);
        RenderContext rendercontext = (RenderContext)renderContext.getAndSet(null);
        if (rendercontext != null)
        {
            rendercontext.StopLoadQueue();
            rendercontext.ClearFrameKeeps();
            Debug.Printf("EGL: destroyContext: calling Flush().", new Object[0]);
            rendercontext.glResourceManager.Flush();
            Debug.Printf("EGL: destroyContext: returned from Flush().", new Object[0]);
        }
        TextureMemoryTracker.releaseAllFrameMemory();
        Debug.Printf("EGL: destroyContext: calling eglDestroyContext ().", new Object[0]);
        initialUpdateDone = false;
        TextureMemoryTracker.clearActiveRenderer(this);
    }

    public void onSurfaceChanged(GL10 gl10, int i, int j)
    {
        gl10 = (RenderContext)renderContext.get();
        if (gl10 == null)
        {
            return;
        }
        ((RenderContext) (gl10)).viewportRect[0] = 0;
        ((RenderContext) (gl10)).viewportRect[1] = 0;
        ((RenderContext) (gl10)).viewportRect[2] = i;
        ((RenderContext) (gl10)).viewportRect[3] = j;
        float f;
        float f1;
        float f2;
        float f3;
        float f4;
        MatrixStack matrixstack;
        if (((RenderContext) (gl10)).hasGL20)
        {
            GLES20.glViewport(0, 0, i, j);
            if (((RenderContext) (gl10)).useFXAA)
            {
                systemFramebuffer[0] = 0;
                GLES20.glGetIntegerv(36006, systemFramebuffer, 0);
                GLES20.glBindFramebuffer(36160, Framebuffers[0]);
                if (Renderbuffers != null)
                {
                    GLES20.glDeleteRenderbuffers(1, Renderbuffers, 0);
                    Renderbuffers = null;
                }
                if (Colorbuffers != null)
                {
                    GLES20.glDeleteTextures(Colorbuffers.length, Colorbuffers, 0);
                    Colorbuffers = null;
                }
                if (Renderbuffers == null)
                {
                    Renderbuffers = new int[1];
                    GLES20.glGenRenderbuffers(1, Renderbuffers, 0);
                    GLES20.glBindRenderbuffer(36161, Renderbuffers[0]);
                    GLES20.glRenderbufferStorage(36161, 33189, i, j);
                }
                if (Colorbuffers == null)
                {
                    Colorbuffers = new int[2];
                    GLES20.glGenTextures(Colorbuffers.length, Colorbuffers, 0);
                    int k = 0;
                    while (k < Colorbuffers.length) 
                    {
                        GLES20.glBindTexture(3553, Colorbuffers[k]);
                        if (k == 0)
                        {
                            GLES20.glTexImage2D(3553, 0, 6407, i, j, 0, 6407, 33635, null);
                        } else
                        {
                            GLES20.glTexImage2D(3553, 0, 6408, i, j, 0, 6408, 5121, null);
                        }
                        GLES20.glTexParameteri(3553, 10242, 33071);
                        GLES20.glTexParameteri(3553, 10243, 33071);
                        GLES20.glTexParameteri(3553, 10240, 9729);
                        GLES20.glTexParameteri(3553, 10241, 9729);
                        k++;
                    }
                }
                GLES20.glViewport(0, 0, i, j);
                GLES20.glBindRenderbuffer(36161, Renderbuffers[0]);
                GLES20.glBindTexture(3553, Colorbuffers[0]);
                GLES20.glFramebufferRenderbuffer(36160, 36096, 36161, Renderbuffers[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, Colorbuffers[0], 0);
                GLES20.glUseProgram(((RenderContext) (gl10)).fxaaProgram.getHandle());
                GLES20.glUniform1i(((RenderContext) (gl10)).fxaaProgram.textureSampler, 0);
                GLES20.glUniform1i(((RenderContext) (gl10)).fxaaProgram.noAAtextureSampler, 1);
                GLES20.glUniform2f(((RenderContext) (gl10)).fxaaProgram.texcoordOffset, 1.0F / (float)i, 1.0F / (float)j);
                float af[] = new float[16];
                android.opengl.Matrix.setIdentityM(af, 0);
                android.opengl.Matrix.scaleM(af, 0, 2.0F, 2.0F, 1.0F);
                GLES20.glUniformMatrix4fv(((RenderContext) (gl10)).fxaaProgram.uMVPMatrix, 1, false, af, 0);
                GLES20.glBindFramebuffer(36160, systemFramebuffer[0]);
            }
        } else
        {
            GLES10.glViewport(0, 0, i, j);
            GLES10.glMatrixMode(5889);
            GLES10.glLoadIdentity();
        }
        gl10.aspectRatio = (float)i / (float)j;
        gl10.FOVAngle = 60F;
        f = (float)Math.tan(((double)((RenderContext) (gl10)).FOVAngle * 3.1415926535897931D) / 360D);
        gl10.getClass();
        f *= 0.5F;
        f1 = drawDistance;
        Debug.Log((new StringBuilder()).append("Renderer: Using drawDistance = ").append(drawDistance).toString());
        ((RenderContext) (gl10)).projectionMatrix.reset();
        matrixstack = ((RenderContext) (gl10)).projectionMatrix;
        f2 = -((RenderContext) (gl10)).aspectRatio;
        f3 = ((RenderContext) (gl10)).aspectRatio;
        f4 = -f;
        gl10.getClass();
        matrixstack.glFrustumf(f2 * f, f3 * f, f4, f, 0.5F, f1);
        gl10.drawDistance = drawDistance;
        ((RenderContext) (gl10)).projectionHUDMatrix.reset();
        ((RenderContext) (gl10)).projectionHUDMatrix.glOrthof(-((RenderContext) (gl10)).aspectRatio * 1.0F, ((RenderContext) (gl10)).aspectRatio * 1.0F, -1F, 1.0F, -1F, 1.0F);
        if (((RenderContext) (gl10)).windlightSky != null)
        {
            ((RenderContext) (gl10)).windlightSky.updateMatrix(gl10);
        }
        firstFrameTime = System.currentTimeMillis();
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig)
    {
        onSurfaceCreated(gl10, eglconfig, false);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig, boolean flag)
    {
        TextureMemoryTracker.setActiveRenderer(this);
        drawingEnabled.set(true);
        firstFrameCount.set(1);
        PrimComputeExecutor.getInstance().resume();
        String s1 = GLES10.glGetString(7936);
        gl10 = GLES10.glGetString(7937);
        String s = GLES10.glGetString(7938);
        String s2 = GLES10.glGetString(7939);
        if (createdGL30)
        {
            int ai[] = new int[1];
            GLES30.glGetIntegerv(33307, ai, 0);
            Debug.Printf("Renderer: Reported major version: %d", new Object[] {
                Integer.valueOf(ai[0])
            });
            if (ai[0] < 3)
            {
                createdGL30 = false;
            }
        }
        Debug.Printf("Renderer: glVendor '%s', glRenderer '%s'", new Object[] {
            s1, gl10
        });
        Debug.Log((new StringBuilder()).append("Renderer: version = '").append(s).append("', extensions = '").append(s2).append("', thread id = ").append(Thread.currentThread().getId()).toString());
        boolean flag1 = createdGL30;
        boolean flag3 = createdGL30;
        String as[] = s2.split(" ");
        int j = as.length;
        for (int i = 0; i < j; i++)
        {
            if (as[i].equals("GL_ARB_vertex_buffer_object"))
            {
                flag1 = true;
            }
        }

        boolean flag2;
        if (s.contains("1.1"))
        {
            flag1 = true;
            flag2 = true;
        } else
        {
            flag2 = flag1;
            flag1 = flag3;
        }
        Debug.Printf("Renderer: VBO support %s, GL11 %s, GL30 %s", new Object[] {
            Boolean.valueOf(flag2), Boolean.valueOf(flag1), Boolean.valueOf(createdGL30)
        });
        gl10 = new RenderContext(eglconfig, gl10, createdGL30, requestGL20, flag1, flag2, avatarCountLimit, GlobalOptions.getInstance().getTerrainTextures(), fontSize, flag, this);
        Debug.AlwaysPrintf("Renderer: created context, GL30 %b, GL20 %b", new Object[] {
            Boolean.valueOf(((RenderContext) (gl10)).hasGL30), Boolean.valueOf(((RenderContext) (gl10)).hasGL20)
        });
        if (((RenderContext) (gl10)).hasGL20)
        {
            if (gl10.getShaderCompileErrors())
            {
                Debug.Printf("Renderer: Shaders did not compile well.", new Object[0]);
                if (stateHandler != null)
                {
                    stateHandler.sendEmptyMessage(4);
                }
                drawingEnabled.set(false);
            }
            Debug.Printf("Renderer: Basic geometry program = %d", new Object[] {
                Integer.valueOf(((RenderContext) (gl10)).primProgram.getHandle())
            });
            GLES20.glClearColor(0.1F, 0.1F, 0.5F, 1.0F);
            if (((RenderContext) (gl10)).useFXAA)
            {
                Framebuffers = new int[1];
                GLES20.glGenFramebuffers(1, Framebuffers, 0);
            } else
            {
                Framebuffers = null;
            }
            Renderbuffers = null;
            Colorbuffers = null;
        } else
        {
            GLES10.glEnableClientState(32884);
            GLES10.glClearColor(0.1F, 0.1F, 0.5F, 1.0F);
        }
        renderContext.set(gl10);
        hoverTextEnableHUDs = GlobalOptions.getInstance().getHoverTextEnableHUDs();
        hoverTextEnableObjects = GlobalOptions.getInstance().getHoverTextEnableObjects();
        if (stateHandler != null)
        {
            stateHandler.sendEmptyMessage(3);
        }
        PrimComputeExecutor.getInstance().execute(initSpatialIndexRunnable);
        firstFrameTime = System.currentTimeMillis();
    }

    public void pickObject(float f, float f1, Handler handler)
    {
        Object obj = pickLock;
        obj;
        JVM INSTR monitorenter ;
        needPickX = f;
        needPickY = f1;
        needPickObject = true;
        pickHandler = handler;
        obj;
        JVM INSTR monitorexit ;
        return;
        handler;
        throw handler;
    }

    public void requestScreenshot(Handler handler)
    {
        screenshotHandler = handler;
    }

    public void setAvatarCountLimit(int i)
    {
        avatarCountLimit = i;
        SpatialIndex.getInstance().setAvatarCountLimit(i);
    }

    public void setDisplayedHUDid(int i)
    {
        displayedHUDid = i;
    }

    public void setDrawDistance(int i)
    {
        drawDistance = i;
        RenderContext rendercontext = (RenderContext)renderContext.get();
        if (rendercontext != null)
        {
            rendercontext.drawDistance = i;
        }
    }

    public void setDrawPickedObject(SLObjectInfo slobjectinfo)
    {
        drawPickedObject = slobjectinfo;
    }

    public void setForcedTime(boolean flag, float f)
    {
        if (flag)
        {
            forcedTime = f;
            return;
        } else
        {
            forcedTime = (0.0F / 0.0F);
            return;
        }
    }

    public void setHUDOffset(float f, float f1)
    {
        hudOffsetX = f;
        hudOffsetY = f1;
    }

    public void setHUDScaleFactor(float f)
    {
        hudScaleFactor = f;
    }

    public void setIsInteracting(boolean flag)
    {
        boolean flag1 = false;
        Object obj = responsiveModeLock;
        obj;
        JVM INSTR monitorenter ;
        if (isInteracting == flag)
        {
            break MISSING_BLOCK_LABEL_26;
        }
        isInteracting = flag;
        flag1 = true;
        obj;
        JVM INSTR monitorexit ;
        if (flag1)
        {
            updateResponsive();
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void setOwnAvatarHidden(boolean flag)
    {
        ownAvatarHidden = flag;
    }

    public void touchHUD(float f, float f1, Handler handler)
    {
        touchHandler = handler;
        if (displayedHUDid != 0)
        {
            touchHUDEvents.add(new TouchHUDEvent(f, f1));
        }
    }
}
