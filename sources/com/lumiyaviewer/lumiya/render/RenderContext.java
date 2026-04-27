// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import com.google.common.base.Optional;
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
import com.lumiyaviewer.lumiya.slproto.mesh.MeshRiggingData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightDay;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            MatrixStack, GPUDetection, Quad, DrawableStore, 
//            BoundingBox, WindlightSky

public class RenderContext
{
    private static class Shaders30
    {

        final BoundingBoxProgram boundingBoxProgram;
        final RiggedMeshProgram30 riggedMeshProgram30;
        final RiggedMeshProgram30 riggedMeshProgramOpaque30;

        private Shaders30(ShaderPreprocessor shaderpreprocessor)
            throws ShaderCompileException
        {
            riggedMeshProgram30 = new RiggedMeshProgram30(false);
            riggedMeshProgramOpaque30 = new RiggedMeshProgram30(true);
            boundingBoxProgram = new BoundingBoxProgram();
            riggedMeshProgram30.Compile(shaderpreprocessor);
            riggedMeshProgramOpaque30.Compile(shaderpreprocessor);
            boundingBoxProgram.Compile(shaderpreprocessor);
        }

        Shaders30(ShaderPreprocessor shaderpreprocessor, Shaders30 shaders30)
        {
            this(shaderpreprocessor);
        }
    }


    public static final float NEAR_PLANE = 0.5F;
    public static final int UNIFORM_BLOCK_ANIMATION_DATA = 1;
    public static final int UNIFORM_BLOCK_RIGGING_DATA = 2;
    public static final int VIEWPORT_RECT_HEIGHT = 3;
    public static final int VIEWPORT_RECT_LEFT = 0;
    public static final int VIEWPORT_RECT_TOP = 1;
    public static final int VIEWPORT_RECT_WIDTH = 2;
    private static final float _tempGluUnProjectData[] = new float[40];
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_m = 0;
    private static final int _temp_out = 36;
    public float FOVAngle;
    private final LinkedList activeOcclusionQueries;
    private MatrixStack activeProjectionMatrix;
    public float aspectRatio;
    public final AvatarProgram avatarProgram;
    private DrawableFaceTexture boundFaceTexture;
    private MeshRiggingData boundMeshRiggingData;
    public final BoundingBox boundingBox;
    public final BoundingBoxProgram boundingBoxProgram;
    public final GLLoadedTexture crosshairTexture;
    public PrimProgram curPrimProgram;
    public RiggedMeshProgram30 currentRiggedMeshProgram;
    public float drawDistance;
    public final DrawableStore drawableStore;
    public final RawShaderProgram extTextureProgram;
    public final FlexiPrimProgram flexiPrimOpaqueProgram;
    public final FlexiPrimProgram flexiPrimProgram;
    public final LLVector3 frameCamera;
    public int frameCount;
    private final List frameKeepBitmaps;
    private final List frameKeepBuffers;
    private final List frameKeepTextures;
    public final FXAAProgram fxaaProgram;
    private final String glRenderer;
    public final GLResourceManager glResourceManager;
    public final boolean hasGL11;
    public final boolean hasGL20;
    public final boolean hasGL30;
    public final boolean hasVBO;
    private final GLLoadQueue loadQueue;
    public final MatrixStack modelViewMatrix;
    public final LLVector3 myAviPosition;
    public final float nearPlane;
    public final MatrixStack objWorldMatrix;
    public final PrimProgram primOpaqueProgram;
    public final PrimProgram primProgram;
    public final MatrixStack projectionHUDMatrix;
    public final MatrixStack projectionMatrix;
    public final Quad quad;
    public final QuadProgram quadProgram;
    public final RawShaderProgram rawShaderProgram;
    public final RiggedMeshProgram riggedMeshProgram;
    private final RiggedMeshProgram30 riggedMeshProgram30;
    private final RiggedMeshProgram30 riggedMeshProgramOpaque30;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    private final boolean shaderCompileErrors;
    public final SkyProgram skyProgram;
    public final StarsProgram starsProgram;
    public boolean underWater;
    public final boolean useFXAA;
    public final boolean useVBO;
    public final int viewportRect[];
    public final WaterProgram waterProgram;
    public float waterTime;
    public final WindlightDay windlightDay;
    public final WindlightPreset windlightPreset;
    public final WindlightSky windlightSky;

    public RenderContext(EGLConfig eglconfig, String s, boolean flag, boolean flag1, boolean flag2, boolean flag3, int i, 
            boolean flag4, int j, boolean flag5, Object obj)
    {
        viewportRect = new int[4];
        FOVAngle = 60F;
        aspectRatio = 1.0F;
        nearPlane = 0.5F;
        projectionMatrix = new MatrixStack();
        projectionHUDMatrix = new MatrixStack();
        modelViewMatrix = new MatrixStack();
        objWorldMatrix = new MatrixStack();
        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;
        underWater = false;
        windlightDay = new WindlightDay();
        myAviPosition = new LLVector3();
        frameCamera = new LLVector3();
        frameKeepBuffers = new LinkedList();
        frameKeepTextures = new LinkedList();
        frameKeepBitmaps = new LinkedList();
        currentRiggedMeshProgram = null;
        boundMeshRiggingData = null;
        boundFaceTexture = null;
        frameCount = 0;
        activeOcclusionQueries = new LinkedList();
        glRenderer = Strings.nullToEmpty(s);
        HashMap hashmap;
        boolean flag6;
        if (!flag1)
        {
            flag6 = flag;
        } else
        {
            flag6 = true;
        }
        hasGL20 = flag6;
        hasGL11 = flag2;
        hasVBO = flag3;
        if (!flag1 && !flag)
        {
            if (!flag2)
            {
                flag3 = false;
            }
        } else
        {
            flag3 = true;
        }
        useVBO = flag3;
        if (flag1)
        {
            useFXAA = GlobalOptions.getInstance().getUseFXAA();
        } else
        {
            useFXAA = false;
        }
        if (!flag1)
        {
            break MISSING_BLOCK_LABEL_1028;
        }
        s = new GPUDetection(s);
        Debug.AlwaysPrintf("Detected GPU family '%s', version '%s', numeric version %d", new Object[] {
            ((GPUDetection) (s)).detectedFamily.or("unknown"), ((GPUDetection) (s)).detectedVersion.or("unknown"), Integer.valueOf(((GPUDetection) (s)).detectedNumericVersion)
        });
        hashmap = new HashMap();
        hashmap.put("__NUM_BASE_JOINTS__", Integer.toString(26));
        hashmap.put("__NUM_BASE_BONE_VECTORS__", Integer.toString(156));
        hashmap.put("__MAX_RIGGED_MESH_BONES__", Integer.toString(SLSkeletonBoneID.VALUES.length + 47));
        hashmap.put("__MAX_RIGGED_MESH_JOINTS__", Integer.toString(163));
        flag3 = flag;
        if (((String)((GPUDetection) (s)).detectedFamily.or("")).equals("Adreno"))
        {
            hashmap.put("__ADRENO__", "");
            flag3 = flag;
            if (((GPUDetection) (s)).detectedNumericVersion != -1)
            {
                flag3 = flag;
                if (((GPUDetection) (s)).detectedNumericVersion < 330)
                {
                    flag3 = false;
                }
            }
        }
        s = new ShaderPreprocessor(hashmap);
        primProgram = new PrimProgram(false);
        primOpaqueProgram = new PrimProgram(true);
        waterProgram = new WaterProgram();
        avatarProgram = new AvatarProgram();
        flexiPrimProgram = new FlexiPrimProgram(false);
        flexiPrimOpaqueProgram = new FlexiPrimProgram(true);
        riggedMeshProgram = new RiggedMeshProgram(false);
        fxaaProgram = new FXAAProgram();
        if (GlobalOptions.getInstance().getRenderClouds())
        {
            skyProgram = new SkyCloudsProgram();
        } else
        {
            skyProgram = new SkyProgram();
        }
        quadProgram = new QuadProgram();
        starsProgram = new StarsProgram();
        if (flag5)
        {
            extTextureProgram = new RawShaderProgram(true);
            rawShaderProgram = new RawShaderProgram(false);
        } else
        {
            extTextureProgram = null;
            rawShaderProgram = null;
        }
        Debug.Printf("Renderer: Going to compile shaders.", new Object[0]);
        CompileShaders(s);
        flag = false;
_L1:
        Debug.AlwaysPrintf("Renderer: Shaders compiled, errors: %b.", new Object[] {
            Boolean.valueOf(flag)
        });
        if (flag3)
        {
            ShaderCompileException shadercompileexception;
            try
            {
                s = new Shaders30(s, null);
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                Debug.Printf((new StringBuilder()).append("Renderer: 3.0 shaders failed to compile: ").append(s.getMessage()).toString(), new Object[0]);
                s = null;
            }
            if (s != null)
            {
                Debug.AlwaysPrintf("Renderer: 3.0 shaders compiled.", new Object[0]);
                riggedMeshProgram30 = ((Shaders30) (s)).riggedMeshProgram30;
                riggedMeshProgramOpaque30 = ((Shaders30) (s)).riggedMeshProgramOpaque30;
                boundingBoxProgram = ((Shaders30) (s)).boundingBoxProgram;
                flag2 = flag;
                flag = flag3;
            } else
            {
                Debug.AlwaysPrintf("Renderer: 3.0 shaders did not compile.", new Object[0]);
                riggedMeshProgram30 = null;
                riggedMeshProgramOpaque30 = null;
                boundingBoxProgram = null;
                flag3 = false;
                flag2 = flag;
                flag = flag3;
            }
        } else
        {
            riggedMeshProgram30 = null;
            riggedMeshProgramOpaque30 = null;
            boundingBoxProgram = null;
            flag2 = flag;
            flag = flag3;
        }
_L2:
        hasGL30 = flag;
        shaderCompileErrors = flag2;
        quad = new Quad();
        glResourceManager = new GLResourceManager();
        windlightPreset = new WindlightPreset();
        loadQueue = createLoadQueue(eglconfig);
        drawableStore = new DrawableStore(loadQueue, flag1, i, flag4, j, obj);
        if (!flag2 && flag)
        {
            boundingBox = new BoundingBox(this);
        } else
        {
            boundingBox = null;
        }
        if (flag1)
        {
            eglconfig = new WindlightSky(this);
        } else
        {
            eglconfig = null;
        }
        windlightSky = eglconfig;
        if (flag5)
        {
            crosshairTexture = GLLoadedTexture.loadFromAssets(this, LumiyaApp.getContext(), "misc/crosshair.png");
            return;
        } else
        {
            crosshairTexture = null;
            return;
        }
        shadercompileexception;
        Debug.Warning(shadercompileexception);
        flag = true;
          goto _L1
        primProgram = null;
        primOpaqueProgram = null;
        waterProgram = null;
        avatarProgram = null;
        flexiPrimProgram = null;
        flexiPrimOpaqueProgram = null;
        riggedMeshProgram = null;
        riggedMeshProgram30 = null;
        riggedMeshProgramOpaque30 = null;
        boundingBoxProgram = null;
        fxaaProgram = null;
        skyProgram = null;
        quadProgram = null;
        starsProgram = null;
        extTextureProgram = null;
        rawShaderProgram = null;
        flag2 = false;
          goto _L2
    }

    private void CompileShaders(ShaderPreprocessor shaderpreprocessor)
        throws ShaderCompileException
    {
        if (hasGL20)
        {
            primProgram.Compile(shaderpreprocessor);
            primOpaqueProgram.Compile(shaderpreprocessor);
            waterProgram.Compile(shaderpreprocessor);
            avatarProgram.Compile(shaderpreprocessor);
            flexiPrimProgram.Compile(shaderpreprocessor);
            flexiPrimOpaqueProgram.Compile(shaderpreprocessor);
            riggedMeshProgram.Compile(shaderpreprocessor);
            fxaaProgram.Compile(shaderpreprocessor);
            skyProgram.Compile(shaderpreprocessor);
            starsProgram.Compile(shaderpreprocessor);
            quadProgram.Compile(shaderpreprocessor);
            if (extTextureProgram != null)
            {
                extTextureProgram.Compile(shaderpreprocessor);
            }
            if (rawShaderProgram != null)
            {
                rawShaderProgram.Compile(shaderpreprocessor);
            }
        }
    }

    private void clearRiggingMeshData()
    {
        boundMeshRiggingData = null;
    }

    private GLLoadQueue createLoadQueue(EGLConfig eglconfig)
    {
        Object obj;
        Debug.Printf("TexLoad: creating load queue.", new Object[0]);
        if (!hasGL20 || !(glRenderer.toLowerCase().contains("tegra") ^ true))
        {
            break MISSING_BLOCK_LABEL_97;
        }
        obj = EGLContext.getEGL();
        if (!(obj instanceof EGL10))
        {
            break MISSING_BLOCK_LABEL_97;
        }
        obj = (EGL10)obj;
        javax.microedition.khronos.egl.EGLDisplay egldisplay = ((EGL10) (obj)).eglGetCurrentDisplay();
        if (egldisplay == null)
        {
            break MISSING_BLOCK_LABEL_97;
        }
        if (egldisplay == EGL10.EGL_NO_DISPLAY)
        {
            break MISSING_BLOCK_LABEL_97;
        }
        eglconfig = new GLAsyncLoadQueue(this, ((EGL10) (obj)), ((EGL10) (obj)).eglGetCurrentDisplay(), eglconfig, hasGL30);
        return eglconfig;
        eglconfig;
        Debug.Warning(eglconfig);
        return new GLSyncLoadQueue();
    }

    public static int gluUnProject(float f, float f1, float f2, float af[], int i, float af1[], int j, int ai[], 
            int k, float af2[], int l)
    {
        _tempGluUnProjectData[32] = ((f - (float)ai[k]) * 2.0F) / (float)ai[k + 2] - 1.0F;
        _tempGluUnProjectData[33] = ((f1 - (float)ai[k + 1]) * 2.0F) / (float)ai[k + 3] - 1.0F;
        _tempGluUnProjectData[34] = 2.0F * f2 - 1.0F;
        _tempGluUnProjectData[35] = 1.0F;
        Matrix.multiplyMM(_tempGluUnProjectData, 16, af1, j, af, i);
        Matrix.invertM(_tempGluUnProjectData, 0, _tempGluUnProjectData, 16);
        Matrix.multiplyMV(_tempGluUnProjectData, 36, _tempGluUnProjectData, 0, _tempGluUnProjectData, 32);
        if ((double)_tempGluUnProjectData[39] == 0.0D)
        {
            return 0;
        } else
        {
            af2[l] = _tempGluUnProjectData[36] / _tempGluUnProjectData[39];
            af2[l + 1] = _tempGluUnProjectData[37] / _tempGluUnProjectData[39];
            af2[l + 2] = _tempGluUnProjectData[38] / _tempGluUnProjectData[39];
            return 1;
        }
    }

    private void initPrimProgram(PrimProgram primprogram, boolean flag)
    {
        GLES20.glUseProgram(primprogram.getHandle());
        GLES20.glUniform1i(primprogram.sTexture, 0);
        WindlightPreset windlightpreset;
        if (flag)
        {
            windlightpreset = windlightPreset;
        } else
        {
            windlightpreset = null;
        }
        primprogram.SetupLighting(this, windlightpreset);
    }

    final void ClearFrameKeeps()
    {
        frameKeepTextures.clear();
        frameKeepBuffers.clear();
        frameKeepBitmaps.clear();
    }

    public final void KeepBuffer(DirectByteBuffer directbytebuffer)
    {
        frameKeepBuffers.add(directbytebuffer);
    }

    public final void KeepTexture(Bitmap bitmap)
    {
        frameKeepBitmaps.add(bitmap);
    }

    public final void KeepTexture(OpenJPEG openjpeg)
    {
        frameKeepTextures.add(openjpeg);
    }

    final void RunLoadQueue()
    {
        loadQueue.RunLoadQueue(this);
    }

    final void StopLoadQueue()
    {
        loadQueue.StopLoadQueue();
    }

    public void bindFaceTexture(DrawableFaceTexture drawablefacetexture)
    {
        if (boundFaceTexture != drawablefacetexture)
        {
            boundFaceTexture = drawablefacetexture;
            boolean flag;
            if (drawablefacetexture != null)
            {
                flag = drawablefacetexture.GLDraw(this);
            } else
            {
                flag = false;
            }
            if (curPrimProgram != null)
            {
                curPrimProgram.setTextureEnabled(flag);
            }
            if (!flag)
            {
                GLES20.glBindTexture(3553, 0);
            }
        }
    }

    public void bindRiggingMeshData(MeshRiggingData meshriggingdata)
    {
        if (boundMeshRiggingData != meshriggingdata)
        {
            if (meshriggingdata != null)
            {
                meshriggingdata.SetupBuffers30(this);
            }
            boundMeshRiggingData = meshriggingdata;
        }
    }

    void clearFaceTexture()
    {
        boundFaceTexture = null;
    }

    public void clearRiggedMeshProgram()
    {
        currentRiggedMeshProgram = null;
        curPrimProgram = null;
        GLES30.glBindVertexArray(0);
        GLES20.glBindTexture(3553, 0);
        GLES20.glUseProgram(0);
        clearRiggingMeshData();
        clearFaceTexture();
    }

    public void enqueueOcclusionQuery(GLQuery glquery)
    {
        activeOcclusionQueries.add(glquery);
    }

    final MatrixStack getActiveProjectionMatrix()
    {
        return activeProjectionMatrix;
    }

    boolean getShaderCompileErrors()
    {
        return shaderCompileErrors;
    }

    public void glBindArrayBuffer(int i)
    {
        if (hasGL20)
        {
            GLES20.glBindBuffer(34962, i);
            return;
        } else
        {
            GLES11.glBindBuffer(34962, i);
            return;
        }
    }

    public void glBindElementArrayBuffer(int i)
    {
        if (hasGL20)
        {
            GLES20.glBindBuffer(34963, i);
            return;
        } else
        {
            GLES11.glBindBuffer(34963, i);
            return;
        }
    }

    public void glBufferArrayData(int i, Buffer buffer, boolean flag)
    {
        int j = 35048;
        if (hasGL20)
        {
            if (!flag)
            {
                j = 35044;
            }
            GLES20.glBufferData(34962, i, buffer, j);
            return;
        }
        if (!flag)
        {
            j = 35044;
        }
        GLES11.glBufferData(34962, i, buffer, j);
    }

    public void glBufferElementArrayData(int i, Buffer buffer, boolean flag)
    {
        int j = 35048;
        if (hasGL20)
        {
            if (!flag)
            {
                j = 35044;
            }
            GLES20.glBufferData(34963, i, buffer, j);
            return;
        }
        if (!flag)
        {
            j = 35044;
        }
        GLES11.glBufferData(34963, i, buffer, j);
    }

    public void glGenBuffers(int i, int ai[], int j)
    {
        if (hasGL20)
        {
            GLES20.glGenBuffers(i, ai, j);
            return;
        } else
        {
            GLES11.glGenBuffers(i, ai, j);
            return;
        }
    }

    public void glModelApplyMatrix(int i)
    {
        if (hasGL20)
        {
            modelViewMatrix.glApplyUniformMatrix(i);
        }
    }

    public void glModelMultMatrixf(float af[], int i)
    {
        if (!hasGL20)
        {
            GLES10.glMultMatrixf(af, i);
        }
        modelViewMatrix.glMultMatrixf(af, i);
    }

    public void glModelPopMatrix()
    {
        if (!hasGL20)
        {
            GLES10.glPopMatrix();
        }
        modelViewMatrix.glPopMatrix();
    }

    public void glModelPushAndMultMatrixf(float af[], int i)
    {
        if (!hasGL20)
        {
            GLES10.glPushMatrix();
            GLES10.glMultMatrixf(af, i);
        }
        modelViewMatrix.glPushAndMultMatrixf(af, i);
    }

    public void glModelPushMatrix()
    {
        if (!hasGL20)
        {
            GLES10.glPushMatrix();
        }
        modelViewMatrix.glPushMatrix();
    }

    void glModelResetIdentity()
    {
        modelViewMatrix.reset();
        modelViewMatrix.glLoadIdentity();
        objWorldMatrix.reset();
        objWorldMatrix.glLoadIdentity();
        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;
    }

    void glModelRotatef(float f, float f1, float f2, float f3)
    {
        if (!hasGL20)
        {
            GLES10.glRotatef(f, f1, f2, f3);
        }
        modelViewMatrix.glRotatef(f, f1, f2, f3);
    }

    public void glModelScalef(float f, float f1, float f2)
    {
        if (!hasGL20)
        {
            GLES10.glScalef(f, f1, f2);
        }
        modelViewMatrix.glScalef(f, f1, f2);
    }

    public void glModelTranslatef(float f, float f1, float f2)
    {
        if (!hasGL20)
        {
            GLES10.glTranslatef(f, f1, f2);
        }
        modelViewMatrix.glTranslatef(f, f1, f2);
    }

    public void glObjScaleApplyVector(int i)
    {
        if (hasGL20)
        {
            GLES20.glUniform4f(i, scaleX, scaleY, scaleZ, 1.0F);
        }
    }

    public void glObjWorldApplyMatrix(int i)
    {
        if (hasGL20)
        {
            objWorldMatrix.glApplyUniformMatrix(i);
        }
    }

    public void glObjWorldPopMatrix()
    {
        if (!hasGL20)
        {
            GLES10.glPopMatrix();
        }
        objWorldMatrix.glPopMatrix();
    }

    public void glObjWorldPushAndLoadMatrixf(float af[], int i)
    {
        if (!hasGL20)
        {
            GLES10.glPushMatrix();
            GLES10.glLoadMatrixf(modelViewMatrix.getMatrixData(), modelViewMatrix.getMatrixDataOffset());
            GLES10.glMultMatrixf(af, i);
        }
        objWorldMatrix.glPushAndLoadMatrixf(af, i);
    }

    public void glObjWorldPushAndMultMatrixf(float af[], int i)
    {
        if (!hasGL20)
        {
            GLES10.glPushMatrix();
            GLES10.glMultMatrixf(af, i);
        }
        objWorldMatrix.glPushAndMultMatrixf(af, i);
    }

    public void glObjWorldTranslatef(float f, float f1, float f2)
    {
        if (!hasGL20)
        {
            GLES10.glTranslatef(f, f1, f2);
        }
        objWorldMatrix.glTranslatef(f, f1, f2);
    }

    void glPopObjectScale()
    {
        if (!hasGL20)
        {
            GLES10.glPopMatrix();
        }
        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;
    }

    void glPushObjectScale(float f, float f1, float f2)
    {
        if (!hasGL20)
        {
            GLES10.glPushMatrix();
            GLES10.glScalef(f, f1, f2);
        }
        scaleX = scaleX * f;
        scaleY = scaleY * f1;
        scaleZ = scaleZ * f2;
    }

    void initAllPrimPrograms(boolean flag)
    {
        if (hasGL20)
        {
            initPrimProgram(primProgram, flag);
            initPrimProgram(primOpaqueProgram, flag);
            initPrimProgram(flexiPrimProgram, flag);
            initPrimProgram(flexiPrimOpaqueProgram, flag);
            initPrimProgram(riggedMeshProgram, flag);
            if (hasGL30)
            {
                initPrimProgram(riggedMeshProgram30, flag);
                initPrimProgram(riggedMeshProgramOpaque30, flag);
            }
        }
    }

    void processOcclusionQueries()
    {
        do
        {
            GLQuery glquery = (GLQuery)activeOcclusionQueries.peekFirst();
            if (glquery != null && glquery.checkResult())
            {
                activeOcclusionQueries.removeFirst();
            } else
            {
                return;
            }
        } while (true);
    }

    final void setActiveProjectionMatrix(MatrixStack matrixstack)
    {
        activeProjectionMatrix = matrixstack;
        if (!hasGL20)
        {
            GLES10.glMatrixMode(5889);
            GLES10.glLoadMatrixf(matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset());
            GLES10.glMatrixMode(5888);
            GLES10.glLoadIdentity();
            modelViewMatrix.reset();
            modelViewMatrix.glLoadIdentity();
            return;
        } else
        {
            modelViewMatrix.reset();
            modelViewMatrix.glLoadMatrixf(matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset());
            return;
        }
    }

    final void setActiveProjectionMatrix(float af[], int i)
    {
        modelViewMatrix.reset();
        modelViewMatrix.glLoadMatrixf(af, i);
    }

    void setMeshCapURL(String s)
    {
        drawableStore.setMeshCapURL(s);
    }

    public void setupRiggedMeshProgram(boolean flag)
    {
        RiggedMeshProgram30 riggedmeshprogram30;
        if (flag)
        {
            riggedmeshprogram30 = riggedMeshProgramOpaque30;
        } else
        {
            riggedmeshprogram30 = riggedMeshProgram30;
        }
        currentRiggedMeshProgram = riggedmeshprogram30;
        clearRiggingMeshData();
        clearFaceTexture();
        curPrimProgram = currentRiggedMeshProgram;
        GLES20.glUseProgram(currentRiggedMeshProgram.getHandle());
        glModelApplyMatrix(currentRiggedMeshProgram.uMVPMatrix);
        glObjWorldApplyMatrix(currentRiggedMeshProgram.uObjWorldMatrix);
        glObjScaleApplyVector(currentRiggedMeshProgram.uObjCoordScale);
    }

}
