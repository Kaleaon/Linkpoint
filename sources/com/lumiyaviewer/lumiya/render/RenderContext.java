package com.lumiyaviewer.lumiya.render;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.support.v4.os.EnvironmentCompat;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.render.glres.GLAsyncLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLQuery;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLSyncLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.shaders.AvatarProgram;
import com.lumiyaviewer.lumiya.render.shaders.BoundingBoxProgram;
import com.lumiyaviewer.lumiya.render.shaders.FXAAProgram;
import com.lumiyaviewer.lumiya.render.shaders.FlexiPrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.QuadProgram;
import com.lumiyaviewer.lumiya.render.shaders.RawShaderProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.render.shaders.ShaderCompileException;
import com.lumiyaviewer.lumiya.render.shaders.ShaderPreprocessor;
import com.lumiyaviewer.lumiya.render.shaders.SkyCloudsProgram;
import com.lumiyaviewer.lumiya.render.shaders.SkyProgram;
import com.lumiyaviewer.lumiya.render.shaders.StarsProgram;
import com.lumiyaviewer.lumiya.render.shaders.WaterProgram;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshRiggingData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightDay;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class RenderContext {
    public static final float NEAR_PLANE = 0.5f;
    public static final int UNIFORM_BLOCK_ANIMATION_DATA = 1;
    public static final int UNIFORM_BLOCK_RIGGING_DATA = 2;
    public static final int VIEWPORT_RECT_HEIGHT = 3;
    public static final int VIEWPORT_RECT_LEFT = 0;
    public static final int VIEWPORT_RECT_TOP = 1;
    public static final int VIEWPORT_RECT_WIDTH = 2;
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_m = 0;
    private static final int _temp_out = 36;
    public float FOVAngle = 60.0f;
    private final LinkedList<GLQuery> activeOcclusionQueries = new LinkedList<>();
    private MatrixStack activeProjectionMatrix;
    public float aspectRatio = 1.0f;
    public final AvatarProgram avatarProgram;
    private DrawableFaceTexture boundFaceTexture = null;
    private MeshRiggingData boundMeshRiggingData = null;
    public final BoundingBox boundingBox;
    public final BoundingBoxProgram boundingBoxProgram;
    public final GLLoadedTexture crosshairTexture;
    public PrimProgram curPrimProgram;
    public RiggedMeshProgram30 currentRiggedMeshProgram = null;
    public float drawDistance;
    public final DrawableStore drawableStore;
    public final RawShaderProgram extTextureProgram;
    public final FlexiPrimProgram flexiPrimOpaqueProgram;
    public final FlexiPrimProgram flexiPrimProgram;
    public final LLVector3 frameCamera = new LLVector3();
    public int frameCount = 0;
    private final List<Bitmap> frameKeepBitmaps = new LinkedList();
    private final List<DirectByteBuffer> frameKeepBuffers = new LinkedList();
    private final List<OpenJPEG> frameKeepTextures = new LinkedList();
    public final FXAAProgram fxaaProgram;
    private final String glRenderer;
    public final GLResourceManager glResourceManager;
    public final boolean hasGL11;
    public final boolean hasGL20;
    public final boolean hasGL30;
    public final boolean hasVBO;
    private final GLLoadQueue loadQueue;
    public final MatrixStack modelViewMatrix = new MatrixStack();
    public final LLVector3 myAviPosition = new LLVector3();
    public final float nearPlane = 0.5f;
    public final MatrixStack objWorldMatrix = new MatrixStack();
    public final PrimProgram primOpaqueProgram;
    public final PrimProgram primProgram;
    public final MatrixStack projectionHUDMatrix = new MatrixStack();
    public final MatrixStack projectionMatrix = new MatrixStack();
    public final Quad quad;
    public final QuadProgram quadProgram;
    public final RawShaderProgram rawShaderProgram;
    public final RiggedMeshProgram riggedMeshProgram;
    private final RiggedMeshProgram30 riggedMeshProgram30;
    private final RiggedMeshProgram30 riggedMeshProgramOpaque30;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    private final boolean shaderCompileErrors;
    public final SkyProgram skyProgram;
    public final StarsProgram starsProgram;
    public boolean underWater = false;
    public final boolean useFXAA;
    public final boolean useVBO;
    public final int[] viewportRect = new int[4];
    public final WaterProgram waterProgram;
    public float waterTime;
    public final WindlightDay windlightDay = new WindlightDay();
    public final WindlightPreset windlightPreset;
    public final WindlightSky windlightSky;

    private static class Shaders30 {
        final BoundingBoxProgram boundingBoxProgram;
        final RiggedMeshProgram30 riggedMeshProgram30;
        final RiggedMeshProgram30 riggedMeshProgramOpaque30;

        private Shaders30(ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
            this.riggedMeshProgram30 = new RiggedMeshProgram30(false);
            this.riggedMeshProgramOpaque30 = new RiggedMeshProgram30(true);
            this.boundingBoxProgram = new BoundingBoxProgram();
            this.riggedMeshProgram30.Compile(shaderPreprocessor);
            this.riggedMeshProgramOpaque30.Compile(shaderPreprocessor);
            this.boundingBoxProgram.Compile(shaderPreprocessor);
        }

        /* synthetic */ Shaders30(ShaderPreprocessor shaderPreprocessor, Shaders30 shaders30) {
            this(shaderPreprocessor);
        }
    }

    public RenderContext(EGLConfig eGLConfig, String str, boolean z, boolean z2, boolean z3, boolean z4, int i, boolean z5, int i2, boolean z6, Object obj) {
        boolean z7;
        boolean z8;
        Shaders30 shaders30;
        this.glRenderer = Strings.nullToEmpty(str);
        this.hasGL20 = !z2 ? z : true;
        this.hasGL11 = z3;
        this.hasVBO = z4;
        if (z2 || z) {
            z4 = true;
        } else if (!z3) {
            z4 = false;
        }
        this.useVBO = z4;
        if (z2) {
            this.useFXAA = GlobalOptions.getInstance().getUseFXAA();
        } else {
            this.useFXAA = false;
        }
        if (z2) {
            GPUDetection gPUDetection = new GPUDetection(str);
            Debug.AlwaysPrintf("Detected GPU family '%s', version '%s', numeric version %d", gPUDetection.detectedFamily.or(EnvironmentCompat.MEDIA_UNKNOWN), gPUDetection.detectedVersion.or(EnvironmentCompat.MEDIA_UNKNOWN), Integer.valueOf(gPUDetection.detectedNumericVersion));
            HashMap hashMap = new HashMap();
            hashMap.put("__NUM_BASE_JOINTS__", Integer.toString(26));
            hashMap.put("__NUM_BASE_BONE_VECTORS__", Integer.toString(156));
            hashMap.put("__MAX_RIGGED_MESH_BONES__", Integer.toString(SLSkeletonBoneID.VALUES.length + 47));
            hashMap.put("__MAX_RIGGED_MESH_JOINTS__", Integer.toString(MeshData.MAX_RIGGED_MESH_JOINTS));
            if (gPUDetection.detectedFamily.or("").equals(GPUDetection.GPU_FAMILY_ADRENO)) {
                hashMap.put("__ADRENO__", "");
                if (gPUDetection.detectedNumericVersion != -1 && gPUDetection.detectedNumericVersion < 330) {
                    z = false;
                }
            }
            ShaderPreprocessor shaderPreprocessor = new ShaderPreprocessor(hashMap);
            this.primProgram = new PrimProgram(false);
            this.primOpaqueProgram = new PrimProgram(true);
            this.waterProgram = new WaterProgram();
            this.avatarProgram = new AvatarProgram();
            this.flexiPrimProgram = new FlexiPrimProgram(false);
            this.flexiPrimOpaqueProgram = new FlexiPrimProgram(true);
            this.riggedMeshProgram = new RiggedMeshProgram(false);
            this.fxaaProgram = new FXAAProgram();
            if (GlobalOptions.getInstance().getRenderClouds()) {
                this.skyProgram = new SkyCloudsProgram();
            } else {
                this.skyProgram = new SkyProgram();
            }
            this.quadProgram = new QuadProgram();
            this.starsProgram = new StarsProgram();
            if (z6) {
                this.extTextureProgram = new RawShaderProgram(true);
                this.rawShaderProgram = new RawShaderProgram(false);
            } else {
                this.extTextureProgram = null;
                this.rawShaderProgram = null;
            }
            Debug.Printf("Renderer: Going to compile shaders.", new Object[0]);
            try {
                CompileShaders(shaderPreprocessor);
                z8 = false;
            } catch (ShaderCompileException e) {
                Debug.Warning(e);
                z8 = true;
            }
            Debug.AlwaysPrintf("Renderer: Shaders compiled, errors: %b.", Boolean.valueOf(z8));
            if (z) {
                try {
                    shaders30 = new Shaders30(shaderPreprocessor, (Shaders30) null);
                } catch (ShaderCompileException e2) {
                    Debug.Printf("Renderer: 3.0 shaders failed to compile: " + e2.getMessage(), new Object[0]);
                    shaders30 = null;
                }
                if (shaders30 != null) {
                    Debug.AlwaysPrintf("Renderer: 3.0 shaders compiled.", new Object[0]);
                    this.riggedMeshProgram30 = shaders30.riggedMeshProgram30;
                    this.riggedMeshProgramOpaque30 = shaders30.riggedMeshProgramOpaque30;
                    this.boundingBoxProgram = shaders30.boundingBoxProgram;
                    z7 = z8;
                } else {
                    Debug.AlwaysPrintf("Renderer: 3.0 shaders did not compile.", new Object[0]);
                    this.riggedMeshProgram30 = null;
                    this.riggedMeshProgramOpaque30 = null;
                    this.boundingBoxProgram = null;
                    z = false;
                    z7 = z8;
                }
            } else {
                this.riggedMeshProgram30 = null;
                this.riggedMeshProgramOpaque30 = null;
                this.boundingBoxProgram = null;
                z7 = z8;
            }
        } else {
            this.primProgram = null;
            this.primOpaqueProgram = null;
            this.waterProgram = null;
            this.avatarProgram = null;
            this.flexiPrimProgram = null;
            this.flexiPrimOpaqueProgram = null;
            this.riggedMeshProgram = null;
            this.riggedMeshProgram30 = null;
            this.riggedMeshProgramOpaque30 = null;
            this.boundingBoxProgram = null;
            this.fxaaProgram = null;
            this.skyProgram = null;
            this.quadProgram = null;
            this.starsProgram = null;
            this.extTextureProgram = null;
            this.rawShaderProgram = null;
            z7 = false;
        }
        this.hasGL30 = z;
        this.shaderCompileErrors = z7;
        this.quad = new Quad();
        this.glResourceManager = new GLResourceManager();
        this.windlightPreset = new WindlightPreset();
        this.loadQueue = createLoadQueue(eGLConfig);
        this.drawableStore = new DrawableStore(this.loadQueue, z2, i, z5, i2, obj);
        if (z7 || !z) {
            this.boundingBox = null;
        } else {
            this.boundingBox = new BoundingBox(this);
        }
        this.windlightSky = z2 ? new WindlightSky(this) : null;
        if (z6) {
            this.crosshairTexture = GLLoadedTexture.loadFromAssets(this, LumiyaApp.getContext(), "misc/crosshair.png");
        } else {
            this.crosshairTexture = null;
        }
    }

    private void CompileShaders(ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
        if (this.hasGL20) {
            this.primProgram.Compile(shaderPreprocessor);
            this.primOpaqueProgram.Compile(shaderPreprocessor);
            this.waterProgram.Compile(shaderPreprocessor);
            this.avatarProgram.Compile(shaderPreprocessor);
            this.flexiPrimProgram.Compile(shaderPreprocessor);
            this.flexiPrimOpaqueProgram.Compile(shaderPreprocessor);
            this.riggedMeshProgram.Compile(shaderPreprocessor);
            this.fxaaProgram.Compile(shaderPreprocessor);
            this.skyProgram.Compile(shaderPreprocessor);
            this.starsProgram.Compile(shaderPreprocessor);
            this.quadProgram.Compile(shaderPreprocessor);
            if (this.extTextureProgram != null) {
                this.extTextureProgram.Compile(shaderPreprocessor);
            }
            if (this.rawShaderProgram != null) {
                this.rawShaderProgram.Compile(shaderPreprocessor);
            }
        }
    }

    private void clearRiggingMeshData() {
        this.boundMeshRiggingData = null;
    }

    private GLLoadQueue createLoadQueue(EGLConfig eGLConfig) {
        Debug.Printf("TexLoad: creating load queue.", new Object[0]);
        if (this.hasGL20 && (!this.glRenderer.toLowerCase().contains("tegra"))) {
            EGL egl = EGLContext.getEGL();
            if (egl instanceof EGL10) {
                EGL10 egl10 = (EGL10) egl;
                try {
                    EGLDisplay eglGetCurrentDisplay = egl10.eglGetCurrentDisplay();
                    if (!(eglGetCurrentDisplay == null || eglGetCurrentDisplay == EGL10.EGL_NO_DISPLAY)) {
                        return new GLAsyncLoadQueue(this, egl10, egl10.eglGetCurrentDisplay(), eGLConfig, this.hasGL30);
                    }
                } catch (InstantiationException e) {
                    Debug.Warning(e);
                }
            }
        }
        return new GLSyncLoadQueue();
    }

    public static int gluUnProject(float f, float f2, float f3, float[] fArr, int i, float[] fArr2, int i2, int[] iArr, int i3, float[] fArr3, int i4) {
        _tempGluUnProjectData[32] = (((f - ((float) iArr[i3])) * 2.0f) / ((float) iArr[i3 + 2])) - 1.0f;
        _tempGluUnProjectData[33] = (((f2 - ((float) iArr[i3 + 1])) * 2.0f) / ((float) iArr[i3 + 3])) - 1.0f;
        _tempGluUnProjectData[34] = (2.0f * f3) - 1.0f;
        _tempGluUnProjectData[35] = 1.0f;
        Matrix.multiplyMM(_tempGluUnProjectData, 16, fArr2, i2, fArr, i);
        Matrix.invertM(_tempGluUnProjectData, 0, _tempGluUnProjectData, 16);
        Matrix.multiplyMV(_tempGluUnProjectData, 36, _tempGluUnProjectData, 0, _tempGluUnProjectData, 32);
        if (((double) _tempGluUnProjectData[39]) == 0.0d) {
            return 0;
        }
        fArr3[i4] = _tempGluUnProjectData[36] / _tempGluUnProjectData[39];
        fArr3[i4 + 1] = _tempGluUnProjectData[37] / _tempGluUnProjectData[39];
        fArr3[i4 + 2] = _tempGluUnProjectData[38] / _tempGluUnProjectData[39];
        return 1;
    }

    private void initPrimProgram(PrimProgram primProgram2, boolean z) {
        GLES20.glUseProgram(primProgram2.getHandle());
        GLES20.glUniform1i(primProgram2.sTexture, 0);
        primProgram2.SetupLighting(this, z ? this.windlightPreset : null);
    }

    /* access modifiers changed from: package-private */
    public final void ClearFrameKeeps() {
        this.frameKeepTextures.clear();
        this.frameKeepBuffers.clear();
        this.frameKeepBitmaps.clear();
    }

    public final void KeepBuffer(@Nonnull DirectByteBuffer directByteBuffer) {
        this.frameKeepBuffers.add(directByteBuffer);
    }

    public final void KeepTexture(@Nonnull Bitmap bitmap) {
        this.frameKeepBitmaps.add(bitmap);
    }

    public final void KeepTexture(@Nonnull OpenJPEG openJPEG) {
        this.frameKeepTextures.add(openJPEG);
    }

    /* access modifiers changed from: package-private */
    public final void RunLoadQueue() {
        this.loadQueue.RunLoadQueue(this);
    }

    /* access modifiers changed from: package-private */
    public final void StopLoadQueue() {
        this.loadQueue.StopLoadQueue();
    }

    public void bindFaceTexture(@Nullable DrawableFaceTexture drawableFaceTexture) {
        if (this.boundFaceTexture != drawableFaceTexture) {
            this.boundFaceTexture = drawableFaceTexture;
            boolean GLDraw = drawableFaceTexture != null ? drawableFaceTexture.GLDraw(this) : false;
            if (this.curPrimProgram != null) {
                this.curPrimProgram.setTextureEnabled(GLDraw);
            }
            if (!GLDraw) {
                GLES20.glBindTexture(3553, 0);
            }
        }
    }

    public void bindRiggingMeshData(@Nullable MeshRiggingData meshRiggingData) {
        if (this.boundMeshRiggingData != meshRiggingData) {
            if (meshRiggingData != null) {
                meshRiggingData.SetupBuffers30(this);
            }
            this.boundMeshRiggingData = meshRiggingData;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearFaceTexture() {
        this.boundFaceTexture = null;
    }

    @TargetApi(18)
    public void clearRiggedMeshProgram() {
        this.currentRiggedMeshProgram = null;
        this.curPrimProgram = null;
        GLES30.glBindVertexArray(0);
        GLES20.glBindTexture(3553, 0);
        GLES20.glUseProgram(0);
        clearRiggingMeshData();
        clearFaceTexture();
    }

    public void enqueueOcclusionQuery(GLQuery gLQuery) {
        this.activeOcclusionQueries.add(gLQuery);
    }

    /* access modifiers changed from: package-private */
    public final MatrixStack getActiveProjectionMatrix() {
        return this.activeProjectionMatrix;
    }

    /* access modifiers changed from: package-private */
    public boolean getShaderCompileErrors() {
        return this.shaderCompileErrors;
    }

    public void glBindArrayBuffer(int i) {
        if (this.hasGL20) {
            GLES20.glBindBuffer(34962, i);
        } else {
            GLES11.glBindBuffer(34962, i);
        }
    }

    public void glBindElementArrayBuffer(int i) {
        if (this.hasGL20) {
            GLES20.glBindBuffer(34963, i);
        } else {
            GLES11.glBindBuffer(34963, i);
        }
    }

    public void glBufferArrayData(int i, Buffer buffer, boolean z) {
        int i2 = 35048;
        if (this.hasGL20) {
            if (!z) {
                i2 = 35044;
            }
            GLES20.glBufferData(34962, i, buffer, i2);
            return;
        }
        if (!z) {
            i2 = 35044;
        }
        GLES11.glBufferData(34962, i, buffer, i2);
    }

    public void glBufferElementArrayData(int i, Buffer buffer, boolean z) {
        int i2 = 35048;
        if (this.hasGL20) {
            if (!z) {
                i2 = 35044;
            }
            GLES20.glBufferData(34963, i, buffer, i2);
            return;
        }
        if (!z) {
            i2 = 35044;
        }
        GLES11.glBufferData(34963, i, buffer, i2);
    }

    public void glGenBuffers(int i, int[] iArr, int i2) {
        if (this.hasGL20) {
            GLES20.glGenBuffers(i, iArr, i2);
        } else {
            GLES11.glGenBuffers(i, iArr, i2);
        }
    }

    public void glModelApplyMatrix(int i) {
        if (this.hasGL20) {
            this.modelViewMatrix.glApplyUniformMatrix(i);
        }
    }

    public void glModelMultMatrixf(float[] fArr, int i) {
        if (!this.hasGL20) {
            GLES10.glMultMatrixf(fArr, i);
        }
        this.modelViewMatrix.glMultMatrixf(fArr, i);
    }

    public void glModelPopMatrix() {
        if (!this.hasGL20) {
            GLES10.glPopMatrix();
        }
        this.modelViewMatrix.glPopMatrix();
    }

    public void glModelPushAndMultMatrixf(float[] fArr, int i) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glMultMatrixf(fArr, i);
        }
        this.modelViewMatrix.glPushAndMultMatrixf(fArr, i);
    }

    public void glModelPushMatrix() {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
        }
        this.modelViewMatrix.glPushMatrix();
    }

    /* access modifiers changed from: package-private */
    public void glModelResetIdentity() {
        this.modelViewMatrix.reset();
        this.modelViewMatrix.glLoadIdentity();
        this.objWorldMatrix.reset();
        this.objWorldMatrix.glLoadIdentity();
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleZ = 1.0f;
    }

    /* access modifiers changed from: package-private */
    public void glModelRotatef(float f, float f2, float f3, float f4) {
        if (!this.hasGL20) {
            GLES10.glRotatef(f, f2, f3, f4);
        }
        this.modelViewMatrix.glRotatef(f, f2, f3, f4);
    }

    public void glModelScalef(float f, float f2, float f3) {
        if (!this.hasGL20) {
            GLES10.glScalef(f, f2, f3);
        }
        this.modelViewMatrix.glScalef(f, f2, f3);
    }

    public void glModelTranslatef(float f, float f2, float f3) {
        if (!this.hasGL20) {
            GLES10.glTranslatef(f, f2, f3);
        }
        this.modelViewMatrix.glTranslatef(f, f2, f3);
    }

    public void glObjScaleApplyVector(int i) {
        if (this.hasGL20) {
            GLES20.glUniform4f(i, this.scaleX, this.scaleY, this.scaleZ, 1.0f);
        }
    }

    public void glObjWorldApplyMatrix(int i) {
        if (this.hasGL20) {
            this.objWorldMatrix.glApplyUniformMatrix(i);
        }
    }

    public void glObjWorldPopMatrix() {
        if (!this.hasGL20) {
            GLES10.glPopMatrix();
        }
        this.objWorldMatrix.glPopMatrix();
    }

    public void glObjWorldPushAndLoadMatrixf(float[] fArr, int i) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glLoadMatrixf(this.modelViewMatrix.getMatrixData(), this.modelViewMatrix.getMatrixDataOffset());
            GLES10.glMultMatrixf(fArr, i);
        }
        this.objWorldMatrix.glPushAndLoadMatrixf(fArr, i);
    }

    public void glObjWorldPushAndMultMatrixf(float[] fArr, int i) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glMultMatrixf(fArr, i);
        }
        this.objWorldMatrix.glPushAndMultMatrixf(fArr, i);
    }

    public void glObjWorldTranslatef(float f, float f2, float f3) {
        if (!this.hasGL20) {
            GLES10.glTranslatef(f, f2, f3);
        }
        this.objWorldMatrix.glTranslatef(f, f2, f3);
    }

    /* access modifiers changed from: package-private */
    public void glPopObjectScale() {
        if (!this.hasGL20) {
            GLES10.glPopMatrix();
        }
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleZ = 1.0f;
    }

    /* access modifiers changed from: package-private */
    public void glPushObjectScale(float f, float f2, float f3) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glScalef(f, f2, f3);
        }
        this.scaleX *= f;
        this.scaleY *= f2;
        this.scaleZ *= f3;
    }

    /* access modifiers changed from: package-private */
    public void initAllPrimPrograms(boolean z) {
        if (this.hasGL20) {
            initPrimProgram(this.primProgram, z);
            initPrimProgram(this.primOpaqueProgram, z);
            initPrimProgram(this.flexiPrimProgram, z);
            initPrimProgram(this.flexiPrimOpaqueProgram, z);
            initPrimProgram(this.riggedMeshProgram, z);
            if (this.hasGL30) {
                initPrimProgram(this.riggedMeshProgram30, z);
                initPrimProgram(this.riggedMeshProgramOpaque30, z);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void processOcclusionQueries() {
        while (true) {
            GLQuery peekFirst = this.activeOcclusionQueries.peekFirst();
            if (peekFirst != null && peekFirst.checkResult()) {
                this.activeOcclusionQueries.removeFirst();
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void setActiveProjectionMatrix(MatrixStack matrixStack) {
        this.activeProjectionMatrix = matrixStack;
        if (!this.hasGL20) {
            GLES10.glMatrixMode(5889);
            GLES10.glLoadMatrixf(matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset());
            GLES10.glMatrixMode(5888);
            GLES10.glLoadIdentity();
            this.modelViewMatrix.reset();
            this.modelViewMatrix.glLoadIdentity();
            return;
        }
        this.modelViewMatrix.reset();
        this.modelViewMatrix.glLoadMatrixf(matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset());
    }

    /* access modifiers changed from: package-private */
    public final void setActiveProjectionMatrix(float[] fArr, int i) {
        this.modelViewMatrix.reset();
        this.modelViewMatrix.glLoadMatrixf(fArr, i);
    }

    /* access modifiers changed from: package-private */
    public void setMeshCapURL(String str) {
        this.drawableStore.setMeshCapURL(str);
    }

    @TargetApi(18)
    public void setupRiggedMeshProgram(boolean z) {
        this.currentRiggedMeshProgram = z ? this.riggedMeshProgramOpaque30 : this.riggedMeshProgram30;
        clearRiggingMeshData();
        clearFaceTexture();
        this.curPrimProgram = this.currentRiggedMeshProgram;
        GLES20.glUseProgram(this.currentRiggedMeshProgram.getHandle());
        glModelApplyMatrix(this.currentRiggedMeshProgram.uMVPMatrix);
        glObjWorldApplyMatrix(this.currentRiggedMeshProgram.uObjWorldMatrix);
        glObjScaleApplyVector(this.currentRiggedMeshProgram.uObjCoordScale);
    }
}
