// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES10;
import java.nio.Buffer;
import android.opengl.GLES11;
import android.annotation.TargetApi;
import android.opengl.GLES30;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import android.opengl.GLES20;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGL;
import com.lumiyaviewer.lumiya.render.glres.GLSyncLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLAsyncLoadQueue;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.shaders.ShaderCompileException;
import com.lumiyaviewer.lumiya.render.shaders.ShaderPreprocessor;
import javax.microedition.khronos.egl.EGLConfig;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightDay;
import com.lumiyaviewer.lumiya.render.shaders.WaterProgram;
import com.lumiyaviewer.lumiya.render.shaders.StarsProgram;
import com.lumiyaviewer.lumiya.render.shaders.SkyProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram;
import com.lumiyaviewer.lumiya.render.shaders.QuadProgram;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.shaders.FXAAProgram;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import android.graphics.Bitmap;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.render.shaders.FlexiPrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.RawShaderProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.shaders.BoundingBoxProgram;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshRiggingData;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.render.shaders.AvatarProgram;
import com.lumiyaviewer.lumiya.render.glres.GLQuery;
import java.util.LinkedList;

public class RenderContext
{
    public static final float NEAR_PLANE = 0.5f;
    public static final int UNIFORM_BLOCK_ANIMATION_DATA = 1;
    public static final int UNIFORM_BLOCK_RIGGING_DATA = 2;
    public static final int VIEWPORT_RECT_HEIGHT = 3;
    public static final int VIEWPORT_RECT_LEFT = 0;
    public static final int VIEWPORT_RECT_TOP = 1;
    public static final int VIEWPORT_RECT_WIDTH = 2;
    private static final float[] _tempGluUnProjectData;
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_m = 0;
    private static final int _temp_out = 36;
    public float FOVAngle;
    private final LinkedList<GLQuery> activeOcclusionQueries;
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
    private final List<Bitmap> frameKeepBitmaps;
    private final List<DirectByteBuffer> frameKeepBuffers;
    private final List<OpenJPEG> frameKeepTextures;
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
    public final int[] viewportRect;
    public final WaterProgram waterProgram;
    public float waterTime;
    public final WindlightDay windlightDay;
    public final WindlightPreset windlightPreset;
    public final WindlightSky windlightSky;
    
    static {
        _tempGluUnProjectData = new float[40];
    }
    
    public RenderContext(final EGLConfig p0, final String p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final int p6, final boolean p7, final int p8, final boolean p9, final Object p10) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokespecial   java/lang/Object.<init>:()V
        //     4: aload_0        
        //     5: iconst_4       
        //     6: newarray        I
        //     8: putfield        com/lumiyaviewer/lumiya/render/RenderContext.viewportRect:[I
        //    11: aload_0        
        //    12: ldc             60.0
        //    14: putfield        com/lumiyaviewer/lumiya/render/RenderContext.FOVAngle:F
        //    17: aload_0        
        //    18: fconst_1       
        //    19: putfield        com/lumiyaviewer/lumiya/render/RenderContext.aspectRatio:F
        //    22: aload_0        
        //    23: ldc             0.5
        //    25: putfield        com/lumiyaviewer/lumiya/render/RenderContext.nearPlane:F
        //    28: aload_0        
        //    29: new             Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    32: dup            
        //    33: invokespecial   com/lumiyaviewer/lumiya/render/MatrixStack.<init>:()V
        //    36: putfield        com/lumiyaviewer/lumiya/render/RenderContext.projectionMatrix:Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    39: aload_0        
        //    40: new             Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    43: dup            
        //    44: invokespecial   com/lumiyaviewer/lumiya/render/MatrixStack.<init>:()V
        //    47: putfield        com/lumiyaviewer/lumiya/render/RenderContext.projectionHUDMatrix:Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    50: aload_0        
        //    51: new             Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    54: dup            
        //    55: invokespecial   com/lumiyaviewer/lumiya/render/MatrixStack.<init>:()V
        //    58: putfield        com/lumiyaviewer/lumiya/render/RenderContext.modelViewMatrix:Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    61: aload_0        
        //    62: new             Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    65: dup            
        //    66: invokespecial   com/lumiyaviewer/lumiya/render/MatrixStack.<init>:()V
        //    69: putfield        com/lumiyaviewer/lumiya/render/RenderContext.objWorldMatrix:Lcom/lumiyaviewer/lumiya/render/MatrixStack;
        //    72: aload_0        
        //    73: fconst_1       
        //    74: putfield        com/lumiyaviewer/lumiya/render/RenderContext.scaleX:F
        //    77: aload_0        
        //    78: fconst_1       
        //    79: putfield        com/lumiyaviewer/lumiya/render/RenderContext.scaleY:F
        //    82: aload_0        
        //    83: fconst_1       
        //    84: putfield        com/lumiyaviewer/lumiya/render/RenderContext.scaleZ:F
        //    87: aload_0        
        //    88: iconst_0       
        //    89: putfield        com/lumiyaviewer/lumiya/render/RenderContext.underWater:Z
        //    92: aload_0        
        //    93: new             Lcom/lumiyaviewer/lumiya/slproto/windlight/WindlightDay;
        //    96: dup            
        //    97: invokespecial   com/lumiyaviewer/lumiya/slproto/windlight/WindlightDay.<init>:()V
        //   100: putfield        com/lumiyaviewer/lumiya/render/RenderContext.windlightDay:Lcom/lumiyaviewer/lumiya/slproto/windlight/WindlightDay;
        //   103: aload_0        
        //   104: new             Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   107: dup            
        //   108: invokespecial   com/lumiyaviewer/lumiya/slproto/types/LLVector3.<init>:()V
        //   111: putfield        com/lumiyaviewer/lumiya/render/RenderContext.myAviPosition:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   114: aload_0        
        //   115: new             Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   118: dup            
        //   119: invokespecial   com/lumiyaviewer/lumiya/slproto/types/LLVector3.<init>:()V
        //   122: putfield        com/lumiyaviewer/lumiya/render/RenderContext.frameCamera:Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   125: aload_0        
        //   126: new             Ljava/util/LinkedList;
        //   129: dup            
        //   130: invokespecial   java/util/LinkedList.<init>:()V
        //   133: putfield        com/lumiyaviewer/lumiya/render/RenderContext.frameKeepBuffers:Ljava/util/List;
        //   136: aload_0        
        //   137: new             Ljava/util/LinkedList;
        //   140: dup            
        //   141: invokespecial   java/util/LinkedList.<init>:()V
        //   144: putfield        com/lumiyaviewer/lumiya/render/RenderContext.frameKeepTextures:Ljava/util/List;
        //   147: aload_0        
        //   148: new             Ljava/util/LinkedList;
        //   151: dup            
        //   152: invokespecial   java/util/LinkedList.<init>:()V
        //   155: putfield        com/lumiyaviewer/lumiya/render/RenderContext.frameKeepBitmaps:Ljava/util/List;
        //   158: aload_0        
        //   159: aconst_null    
        //   160: putfield        com/lumiyaviewer/lumiya/render/RenderContext.currentRiggedMeshProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   163: aload_0        
        //   164: aconst_null    
        //   165: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundMeshRiggingData:Lcom/lumiyaviewer/lumiya/slproto/mesh/MeshRiggingData;
        //   168: aload_0        
        //   169: aconst_null    
        //   170: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundFaceTexture:Lcom/lumiyaviewer/lumiya/render/drawable/DrawableFaceTexture;
        //   173: aload_0        
        //   174: iconst_0       
        //   175: putfield        com/lumiyaviewer/lumiya/render/RenderContext.frameCount:I
        //   178: aload_0        
        //   179: new             Ljava/util/LinkedList;
        //   182: dup            
        //   183: invokespecial   java/util/LinkedList.<init>:()V
        //   186: putfield        com/lumiyaviewer/lumiya/render/RenderContext.activeOcclusionQueries:Ljava/util/LinkedList;
        //   189: aload_0        
        //   190: aload_2        
        //   191: invokestatic    com/google/common/base/Strings.nullToEmpty:(Ljava/lang/String;)Ljava/lang/String;
        //   194: putfield        com/lumiyaviewer/lumiya/render/RenderContext.glRenderer:Ljava/lang/String;
        //   197: iload           4
        //   199: ifne            869
        //   202: iload_3        
        //   203: istore          12
        //   205: aload_0        
        //   206: iload           12
        //   208: putfield        com/lumiyaviewer/lumiya/render/RenderContext.hasGL20:Z
        //   211: aload_0        
        //   212: iload           5
        //   214: putfield        com/lumiyaviewer/lumiya/render/RenderContext.hasGL11:Z
        //   217: aload_0        
        //   218: iload           6
        //   220: putfield        com/lumiyaviewer/lumiya/render/RenderContext.hasVBO:Z
        //   223: iload           4
        //   225: ifne            875
        //   228: iload_3        
        //   229: ifne            875
        //   232: iload           5
        //   234: ifeq            881
        //   237: aload_0        
        //   238: iload           6
        //   240: putfield        com/lumiyaviewer/lumiya/render/RenderContext.useVBO:Z
        //   243: iload           4
        //   245: ifeq            887
        //   248: aload_0        
        //   249: invokestatic    com/lumiyaviewer/lumiya/GlobalOptions.getInstance:()Lcom/lumiyaviewer/lumiya/GlobalOptions;
        //   252: invokevirtual   com/lumiyaviewer/lumiya/GlobalOptions.getUseFXAA:()Z
        //   255: putfield        com/lumiyaviewer/lumiya/render/RenderContext.useFXAA:Z
        //   258: iload           4
        //   260: ifeq            1029
        //   263: new             Lcom/lumiyaviewer/lumiya/render/GPUDetection;
        //   266: dup            
        //   267: aload_2        
        //   268: invokespecial   com/lumiyaviewer/lumiya/render/GPUDetection.<init>:(Ljava/lang/String;)V
        //   271: astore_2       
        //   272: ldc             "Detected GPU family '%s', version '%s', numeric version %d"
        //   274: iconst_3       
        //   275: anewarray       Ljava/lang/Object;
        //   278: dup            
        //   279: iconst_0       
        //   280: aload_2        
        //   281: getfield        com/lumiyaviewer/lumiya/render/GPUDetection.detectedFamily:Lcom/google/common/base/Optional;
        //   284: ldc             "unknown"
        //   286: invokevirtual   com/google/common/base/Optional.or:(Ljava/lang/Object;)Ljava/lang/Object;
        //   289: aastore        
        //   290: dup            
        //   291: iconst_1       
        //   292: aload_2        
        //   293: getfield        com/lumiyaviewer/lumiya/render/GPUDetection.detectedVersion:Lcom/google/common/base/Optional;
        //   296: ldc             "unknown"
        //   298: invokevirtual   com/google/common/base/Optional.or:(Ljava/lang/Object;)Ljava/lang/Object;
        //   301: aastore        
        //   302: dup            
        //   303: iconst_2       
        //   304: aload_2        
        //   305: getfield        com/lumiyaviewer/lumiya/render/GPUDetection.detectedNumericVersion:I
        //   308: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   311: aastore        
        //   312: invokestatic    com/lumiyaviewer/lumiya/Debug.AlwaysPrintf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   315: new             Ljava/util/HashMap;
        //   318: dup            
        //   319: invokespecial   java/util/HashMap.<init>:()V
        //   322: astore          13
        //   324: aload           13
        //   326: ldc_w           "__NUM_BASE_JOINTS__"
        //   329: bipush          26
        //   331: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   334: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   339: pop            
        //   340: aload           13
        //   342: ldc_w           "__NUM_BASE_BONE_VECTORS__"
        //   345: sipush          156
        //   348: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   351: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   356: pop            
        //   357: aload           13
        //   359: ldc_w           "__MAX_RIGGED_MESH_BONES__"
        //   362: getstatic       com/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID.VALUES:[Lcom/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID;
        //   365: arraylength    
        //   366: bipush          47
        //   368: iadd           
        //   369: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   372: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   377: pop            
        //   378: aload           13
        //   380: ldc_w           "__MAX_RIGGED_MESH_JOINTS__"
        //   383: sipush          163
        //   386: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   389: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   394: pop            
        //   395: iload_3        
        //   396: istore          6
        //   398: aload_2        
        //   399: getfield        com/lumiyaviewer/lumiya/render/GPUDetection.detectedFamily:Lcom/google/common/base/Optional;
        //   402: ldc_w           ""
        //   405: invokevirtual   com/google/common/base/Optional.or:(Ljava/lang/Object;)Ljava/lang/Object;
        //   408: checkcast       Ljava/lang/String;
        //   411: ldc_w           "Adreno"
        //   414: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   417: ifeq            461
        //   420: aload           13
        //   422: ldc_w           "__ADRENO__"
        //   425: ldc_w           ""
        //   428: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   433: pop            
        //   434: iload_3        
        //   435: istore          6
        //   437: aload_2        
        //   438: getfield        com/lumiyaviewer/lumiya/render/GPUDetection.detectedNumericVersion:I
        //   441: iconst_m1      
        //   442: if_icmpeq       461
        //   445: iload_3        
        //   446: istore          6
        //   448: aload_2        
        //   449: getfield        com/lumiyaviewer/lumiya/render/GPUDetection.detectedNumericVersion:I
        //   452: sipush          330
        //   455: if_icmpge       461
        //   458: iconst_0       
        //   459: istore          6
        //   461: new             Lcom/lumiyaviewer/lumiya/render/shaders/ShaderPreprocessor;
        //   464: dup            
        //   465: aload           13
        //   467: invokespecial   com/lumiyaviewer/lumiya/render/shaders/ShaderPreprocessor.<init>:(Ljava/util/Map;)V
        //   470: astore          13
        //   472: aload_0        
        //   473: new             Lcom/lumiyaviewer/lumiya/render/shaders/PrimProgram;
        //   476: dup            
        //   477: iconst_0       
        //   478: invokespecial   com/lumiyaviewer/lumiya/render/shaders/PrimProgram.<init>:(Z)V
        //   481: putfield        com/lumiyaviewer/lumiya/render/RenderContext.primProgram:Lcom/lumiyaviewer/lumiya/render/shaders/PrimProgram;
        //   484: aload_0        
        //   485: new             Lcom/lumiyaviewer/lumiya/render/shaders/PrimProgram;
        //   488: dup            
        //   489: iconst_1       
        //   490: invokespecial   com/lumiyaviewer/lumiya/render/shaders/PrimProgram.<init>:(Z)V
        //   493: putfield        com/lumiyaviewer/lumiya/render/RenderContext.primOpaqueProgram:Lcom/lumiyaviewer/lumiya/render/shaders/PrimProgram;
        //   496: aload_0        
        //   497: new             Lcom/lumiyaviewer/lumiya/render/shaders/WaterProgram;
        //   500: dup            
        //   501: invokespecial   com/lumiyaviewer/lumiya/render/shaders/WaterProgram.<init>:()V
        //   504: putfield        com/lumiyaviewer/lumiya/render/RenderContext.waterProgram:Lcom/lumiyaviewer/lumiya/render/shaders/WaterProgram;
        //   507: aload_0        
        //   508: new             Lcom/lumiyaviewer/lumiya/render/shaders/AvatarProgram;
        //   511: dup            
        //   512: invokespecial   com/lumiyaviewer/lumiya/render/shaders/AvatarProgram.<init>:()V
        //   515: putfield        com/lumiyaviewer/lumiya/render/RenderContext.avatarProgram:Lcom/lumiyaviewer/lumiya/render/shaders/AvatarProgram;
        //   518: aload_0        
        //   519: new             Lcom/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram;
        //   522: dup            
        //   523: iconst_0       
        //   524: invokespecial   com/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram.<init>:(Z)V
        //   527: putfield        com/lumiyaviewer/lumiya/render/RenderContext.flexiPrimProgram:Lcom/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram;
        //   530: aload_0        
        //   531: new             Lcom/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram;
        //   534: dup            
        //   535: iconst_1       
        //   536: invokespecial   com/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram.<init>:(Z)V
        //   539: putfield        com/lumiyaviewer/lumiya/render/RenderContext.flexiPrimOpaqueProgram:Lcom/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram;
        //   542: aload_0        
        //   543: new             Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram;
        //   546: dup            
        //   547: iconst_0       
        //   548: invokespecial   com/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram.<init>:(Z)V
        //   551: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram;
        //   554: aload_0        
        //   555: new             Lcom/lumiyaviewer/lumiya/render/shaders/FXAAProgram;
        //   558: dup            
        //   559: invokespecial   com/lumiyaviewer/lumiya/render/shaders/FXAAProgram.<init>:()V
        //   562: putfield        com/lumiyaviewer/lumiya/render/RenderContext.fxaaProgram:Lcom/lumiyaviewer/lumiya/render/shaders/FXAAProgram;
        //   565: invokestatic    com/lumiyaviewer/lumiya/GlobalOptions.getInstance:()Lcom/lumiyaviewer/lumiya/GlobalOptions;
        //   568: invokevirtual   com/lumiyaviewer/lumiya/GlobalOptions.getRenderClouds:()Z
        //   571: ifeq            895
        //   574: aload_0        
        //   575: new             Lcom/lumiyaviewer/lumiya/render/shaders/SkyCloudsProgram;
        //   578: dup            
        //   579: invokespecial   com/lumiyaviewer/lumiya/render/shaders/SkyCloudsProgram.<init>:()V
        //   582: putfield        com/lumiyaviewer/lumiya/render/RenderContext.skyProgram:Lcom/lumiyaviewer/lumiya/render/shaders/SkyProgram;
        //   585: aload_0        
        //   586: new             Lcom/lumiyaviewer/lumiya/render/shaders/QuadProgram;
        //   589: dup            
        //   590: invokespecial   com/lumiyaviewer/lumiya/render/shaders/QuadProgram.<init>:()V
        //   593: putfield        com/lumiyaviewer/lumiya/render/RenderContext.quadProgram:Lcom/lumiyaviewer/lumiya/render/shaders/QuadProgram;
        //   596: aload_0        
        //   597: new             Lcom/lumiyaviewer/lumiya/render/shaders/StarsProgram;
        //   600: dup            
        //   601: invokespecial   com/lumiyaviewer/lumiya/render/shaders/StarsProgram.<init>:()V
        //   604: putfield        com/lumiyaviewer/lumiya/render/RenderContext.starsProgram:Lcom/lumiyaviewer/lumiya/render/shaders/StarsProgram;
        //   607: iload           10
        //   609: ifeq            909
        //   612: aload_0        
        //   613: new             Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //   616: dup            
        //   617: iconst_1       
        //   618: invokespecial   com/lumiyaviewer/lumiya/render/shaders/RawShaderProgram.<init>:(Z)V
        //   621: putfield        com/lumiyaviewer/lumiya/render/RenderContext.extTextureProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //   624: aload_0        
        //   625: new             Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //   628: dup            
        //   629: iconst_0       
        //   630: invokespecial   com/lumiyaviewer/lumiya/render/shaders/RawShaderProgram.<init>:(Z)V
        //   633: putfield        com/lumiyaviewer/lumiya/render/RenderContext.rawShaderProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //   636: ldc_w           "Renderer: Going to compile shaders."
        //   639: iconst_0       
        //   640: anewarray       Ljava/lang/Object;
        //   643: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   646: aload_0        
        //   647: aload           13
        //   649: invokespecial   com/lumiyaviewer/lumiya/render/RenderContext.CompileShaders:(Lcom/lumiyaviewer/lumiya/render/shaders/ShaderPreprocessor;)V
        //   652: iconst_0       
        //   653: istore_3       
        //   654: ldc_w           "Renderer: Shaders compiled, errors: %b."
        //   657: iconst_1       
        //   658: anewarray       Ljava/lang/Object;
        //   661: dup            
        //   662: iconst_0       
        //   663: iload_3        
        //   664: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //   667: aastore        
        //   668: invokestatic    com/lumiyaviewer/lumiya/Debug.AlwaysPrintf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   671: iload           6
        //   673: ifeq            1005
        //   676: new             Lcom/lumiyaviewer/lumiya/render/RenderContext$Shaders30;
        //   679: astore_2       
        //   680: aload_2        
        //   681: aload           13
        //   683: aconst_null    
        //   684: invokespecial   com/lumiyaviewer/lumiya/render/RenderContext$Shaders30.<init>:(Lcom/lumiyaviewer/lumiya/render/shaders/ShaderPreprocessor;Lcom/lumiyaviewer/lumiya/render/RenderContext$Shaders30;)V
        //   687: aload_2        
        //   688: ifnull          968
        //   691: ldc_w           "Renderer: 3.0 shaders compiled."
        //   694: iconst_0       
        //   695: anewarray       Ljava/lang/Object;
        //   698: invokestatic    com/lumiyaviewer/lumiya/Debug.AlwaysPrintf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   701: aload_0        
        //   702: aload_2        
        //   703: getfield        com/lumiyaviewer/lumiya/render/RenderContext$Shaders30.riggedMeshProgram30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   706: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgram30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   709: aload_0        
        //   710: aload_2        
        //   711: getfield        com/lumiyaviewer/lumiya/render/RenderContext$Shaders30.riggedMeshProgramOpaque30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   714: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgramOpaque30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   717: aload_0        
        //   718: aload_2        
        //   719: getfield        com/lumiyaviewer/lumiya/render/RenderContext$Shaders30.boundingBoxProgram:Lcom/lumiyaviewer/lumiya/render/shaders/BoundingBoxProgram;
        //   722: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundingBoxProgram:Lcom/lumiyaviewer/lumiya/render/shaders/BoundingBoxProgram;
        //   725: iload_3        
        //   726: istore          5
        //   728: iload           6
        //   730: istore_3       
        //   731: aload_0        
        //   732: iload_3        
        //   733: putfield        com/lumiyaviewer/lumiya/render/RenderContext.hasGL30:Z
        //   736: aload_0        
        //   737: iload           5
        //   739: putfield        com/lumiyaviewer/lumiya/render/RenderContext.shaderCompileErrors:Z
        //   742: aload_0        
        //   743: new             Lcom/lumiyaviewer/lumiya/render/Quad;
        //   746: dup            
        //   747: invokespecial   com/lumiyaviewer/lumiya/render/Quad.<init>:()V
        //   750: putfield        com/lumiyaviewer/lumiya/render/RenderContext.quad:Lcom/lumiyaviewer/lumiya/render/Quad;
        //   753: aload_0        
        //   754: new             Lcom/lumiyaviewer/lumiya/render/glres/GLResourceManager;
        //   757: dup            
        //   758: invokespecial   com/lumiyaviewer/lumiya/render/glres/GLResourceManager.<init>:()V
        //   761: putfield        com/lumiyaviewer/lumiya/render/RenderContext.glResourceManager:Lcom/lumiyaviewer/lumiya/render/glres/GLResourceManager;
        //   764: aload_0        
        //   765: new             Lcom/lumiyaviewer/lumiya/slproto/windlight/WindlightPreset;
        //   768: dup            
        //   769: invokespecial   com/lumiyaviewer/lumiya/slproto/windlight/WindlightPreset.<init>:()V
        //   772: putfield        com/lumiyaviewer/lumiya/render/RenderContext.windlightPreset:Lcom/lumiyaviewer/lumiya/slproto/windlight/WindlightPreset;
        //   775: aload_0        
        //   776: aload_0        
        //   777: aload_1        
        //   778: invokespecial   com/lumiyaviewer/lumiya/render/RenderContext.createLoadQueue:(Ljavax/microedition/khronos/egl/EGLConfig;)Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue;
        //   781: putfield        com/lumiyaviewer/lumiya/render/RenderContext.loadQueue:Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue;
        //   784: aload_0        
        //   785: new             Lcom/lumiyaviewer/lumiya/render/DrawableStore;
        //   788: dup            
        //   789: aload_0        
        //   790: getfield        com/lumiyaviewer/lumiya/render/RenderContext.loadQueue:Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue;
        //   793: iload           4
        //   795: iload           7
        //   797: iload           8
        //   799: iload           9
        //   801: aload           11
        //   803: invokespecial   com/lumiyaviewer/lumiya/render/DrawableStore.<init>:(Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue;ZIZILjava/lang/Object;)V
        //   806: putfield        com/lumiyaviewer/lumiya/render/RenderContext.drawableStore:Lcom/lumiyaviewer/lumiya/render/DrawableStore;
        //   809: iload           5
        //   811: ifne            1115
        //   814: iload_3        
        //   815: ifeq            1115
        //   818: aload_0        
        //   819: new             Lcom/lumiyaviewer/lumiya/render/BoundingBox;
        //   822: dup            
        //   823: aload_0        
        //   824: invokespecial   com/lumiyaviewer/lumiya/render/BoundingBox.<init>:(Lcom/lumiyaviewer/lumiya/render/RenderContext;)V
        //   827: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundingBox:Lcom/lumiyaviewer/lumiya/render/BoundingBox;
        //   830: iload           4
        //   832: ifeq            1123
        //   835: new             Lcom/lumiyaviewer/lumiya/render/WindlightSky;
        //   838: dup            
        //   839: aload_0        
        //   840: invokespecial   com/lumiyaviewer/lumiya/render/WindlightSky.<init>:(Lcom/lumiyaviewer/lumiya/render/RenderContext;)V
        //   843: astore_1       
        //   844: aload_0        
        //   845: aload_1        
        //   846: putfield        com/lumiyaviewer/lumiya/render/RenderContext.windlightSky:Lcom/lumiyaviewer/lumiya/render/WindlightSky;
        //   849: iload           10
        //   851: ifeq            1128
        //   854: aload_0        
        //   855: aload_0        
        //   856: invokestatic    com/lumiyaviewer/lumiya/LumiyaApp.getContext:()Landroid/content/Context;
        //   859: ldc_w           "misc/crosshair.png"
        //   862: invokestatic    com/lumiyaviewer/lumiya/render/glres/textures/GLLoadedTexture.loadFromAssets:(Lcom/lumiyaviewer/lumiya/render/RenderContext;Landroid/content/Context;Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/render/glres/textures/GLLoadedTexture;
        //   865: putfield        com/lumiyaviewer/lumiya/render/RenderContext.crosshairTexture:Lcom/lumiyaviewer/lumiya/render/glres/textures/GLLoadedTexture;
        //   868: return         
        //   869: iconst_1       
        //   870: istore          12
        //   872: goto            205
        //   875: iconst_1       
        //   876: istore          6
        //   878: goto            237
        //   881: iconst_0       
        //   882: istore          6
        //   884: goto            237
        //   887: aload_0        
        //   888: iconst_0       
        //   889: putfield        com/lumiyaviewer/lumiya/render/RenderContext.useFXAA:Z
        //   892: goto            258
        //   895: aload_0        
        //   896: new             Lcom/lumiyaviewer/lumiya/render/shaders/SkyProgram;
        //   899: dup            
        //   900: invokespecial   com/lumiyaviewer/lumiya/render/shaders/SkyProgram.<init>:()V
        //   903: putfield        com/lumiyaviewer/lumiya/render/RenderContext.skyProgram:Lcom/lumiyaviewer/lumiya/render/shaders/SkyProgram;
        //   906: goto            585
        //   909: aload_0        
        //   910: aconst_null    
        //   911: putfield        com/lumiyaviewer/lumiya/render/RenderContext.extTextureProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //   914: aload_0        
        //   915: aconst_null    
        //   916: putfield        com/lumiyaviewer/lumiya/render/RenderContext.rawShaderProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //   919: goto            636
        //   922: astore_2       
        //   923: aload_2        
        //   924: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   927: iconst_1       
        //   928: istore_3       
        //   929: goto            654
        //   932: astore_2       
        //   933: new             Ljava/lang/StringBuilder;
        //   936: dup            
        //   937: invokespecial   java/lang/StringBuilder.<init>:()V
        //   940: ldc_w           "Renderer: 3.0 shaders failed to compile: "
        //   943: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   946: aload_2        
        //   947: invokevirtual   com/lumiyaviewer/lumiya/render/shaders/ShaderCompileException.getMessage:()Ljava/lang/String;
        //   950: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   953: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   956: iconst_0       
        //   957: anewarray       Ljava/lang/Object;
        //   960: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   963: aconst_null    
        //   964: astore_2       
        //   965: goto            687
        //   968: ldc_w           "Renderer: 3.0 shaders did not compile."
        //   971: iconst_0       
        //   972: anewarray       Ljava/lang/Object;
        //   975: invokestatic    com/lumiyaviewer/lumiya/Debug.AlwaysPrintf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   978: aload_0        
        //   979: aconst_null    
        //   980: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgram30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   983: aload_0        
        //   984: aconst_null    
        //   985: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgramOpaque30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //   988: aload_0        
        //   989: aconst_null    
        //   990: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundingBoxProgram:Lcom/lumiyaviewer/lumiya/render/shaders/BoundingBoxProgram;
        //   993: iconst_0       
        //   994: istore          6
        //   996: iload_3        
        //   997: istore          5
        //   999: iload           6
        //  1001: istore_3       
        //  1002: goto            731
        //  1005: aload_0        
        //  1006: aconst_null    
        //  1007: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgram30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //  1010: aload_0        
        //  1011: aconst_null    
        //  1012: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgramOpaque30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //  1015: aload_0        
        //  1016: aconst_null    
        //  1017: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundingBoxProgram:Lcom/lumiyaviewer/lumiya/render/shaders/BoundingBoxProgram;
        //  1020: iload_3        
        //  1021: istore          5
        //  1023: iload           6
        //  1025: istore_3       
        //  1026: goto            731
        //  1029: aload_0        
        //  1030: aconst_null    
        //  1031: putfield        com/lumiyaviewer/lumiya/render/RenderContext.primProgram:Lcom/lumiyaviewer/lumiya/render/shaders/PrimProgram;
        //  1034: aload_0        
        //  1035: aconst_null    
        //  1036: putfield        com/lumiyaviewer/lumiya/render/RenderContext.primOpaqueProgram:Lcom/lumiyaviewer/lumiya/render/shaders/PrimProgram;
        //  1039: aload_0        
        //  1040: aconst_null    
        //  1041: putfield        com/lumiyaviewer/lumiya/render/RenderContext.waterProgram:Lcom/lumiyaviewer/lumiya/render/shaders/WaterProgram;
        //  1044: aload_0        
        //  1045: aconst_null    
        //  1046: putfield        com/lumiyaviewer/lumiya/render/RenderContext.avatarProgram:Lcom/lumiyaviewer/lumiya/render/shaders/AvatarProgram;
        //  1049: aload_0        
        //  1050: aconst_null    
        //  1051: putfield        com/lumiyaviewer/lumiya/render/RenderContext.flexiPrimProgram:Lcom/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram;
        //  1054: aload_0        
        //  1055: aconst_null    
        //  1056: putfield        com/lumiyaviewer/lumiya/render/RenderContext.flexiPrimOpaqueProgram:Lcom/lumiyaviewer/lumiya/render/shaders/FlexiPrimProgram;
        //  1059: aload_0        
        //  1060: aconst_null    
        //  1061: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram;
        //  1064: aload_0        
        //  1065: aconst_null    
        //  1066: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgram30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //  1069: aload_0        
        //  1070: aconst_null    
        //  1071: putfield        com/lumiyaviewer/lumiya/render/RenderContext.riggedMeshProgramOpaque30:Lcom/lumiyaviewer/lumiya/render/shaders/RiggedMeshProgram30;
        //  1074: aload_0        
        //  1075: aconst_null    
        //  1076: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundingBoxProgram:Lcom/lumiyaviewer/lumiya/render/shaders/BoundingBoxProgram;
        //  1079: aload_0        
        //  1080: aconst_null    
        //  1081: putfield        com/lumiyaviewer/lumiya/render/RenderContext.fxaaProgram:Lcom/lumiyaviewer/lumiya/render/shaders/FXAAProgram;
        //  1084: aload_0        
        //  1085: aconst_null    
        //  1086: putfield        com/lumiyaviewer/lumiya/render/RenderContext.skyProgram:Lcom/lumiyaviewer/lumiya/render/shaders/SkyProgram;
        //  1089: aload_0        
        //  1090: aconst_null    
        //  1091: putfield        com/lumiyaviewer/lumiya/render/RenderContext.quadProgram:Lcom/lumiyaviewer/lumiya/render/shaders/QuadProgram;
        //  1094: aload_0        
        //  1095: aconst_null    
        //  1096: putfield        com/lumiyaviewer/lumiya/render/RenderContext.starsProgram:Lcom/lumiyaviewer/lumiya/render/shaders/StarsProgram;
        //  1099: aload_0        
        //  1100: aconst_null    
        //  1101: putfield        com/lumiyaviewer/lumiya/render/RenderContext.extTextureProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //  1104: aload_0        
        //  1105: aconst_null    
        //  1106: putfield        com/lumiyaviewer/lumiya/render/RenderContext.rawShaderProgram:Lcom/lumiyaviewer/lumiya/render/shaders/RawShaderProgram;
        //  1109: iconst_0       
        //  1110: istore          5
        //  1112: goto            731
        //  1115: aload_0        
        //  1116: aconst_null    
        //  1117: putfield        com/lumiyaviewer/lumiya/render/RenderContext.boundingBox:Lcom/lumiyaviewer/lumiya/render/BoundingBox;
        //  1120: goto            830
        //  1123: aconst_null    
        //  1124: astore_1       
        //  1125: goto            844
        //  1128: aload_0        
        //  1129: aconst_null    
        //  1130: putfield        com/lumiyaviewer/lumiya/render/RenderContext.crosshairTexture:Lcom/lumiyaviewer/lumiya/render/glres/textures/GLLoadedTexture;
        //  1133: goto            868
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                           
        //  -----  -----  -----  -----  ---------------------------------------------------------------
        //  646    652    922    932    Lcom/lumiyaviewer/lumiya/render/shaders/ShaderCompileException;
        //  676    687    932    968    Lcom/lumiyaviewer/lumiya/render/shaders/ShaderCompileException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0687:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
    
    private void CompileShaders(final ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
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
    
    private GLLoadQueue createLoadQueue(final EGLConfig eglConfig) {
        Debug.Printf("TexLoad: creating load queue.", new Object[0]);
        if (this.hasGL20 && (this.glRenderer.toLowerCase().contains("tegra") ^ true)) {
            final EGL egl = EGLContext.getEGL();
            if (egl instanceof EGL10) {
                final EGL10 egl2 = (EGL10)egl;
                try {
                    final EGLDisplay eglGetCurrentDisplay = egl2.eglGetCurrentDisplay();
                    if (eglGetCurrentDisplay != null && eglGetCurrentDisplay != EGL10.EGL_NO_DISPLAY) {
                        return new GLAsyncLoadQueue(this, egl2, egl2.eglGetCurrentDisplay(), eglConfig, this.hasGL30);
                    }
                }
                catch (final InstantiationException ex) {
                    Debug.Warning(ex);
                }
            }
        }
        return new GLSyncLoadQueue();
    }
    
    public static int gluUnProject(final float n, final float n2, final float n3, final float[] array, final int n4, final float[] array2, final int n5, final int[] array3, final int n6, final float[] array4, final int n7) {
        RenderContext._tempGluUnProjectData[32] = (n - array3[n6]) * 2.0f / array3[n6 + 2] - 1.0f;
        RenderContext._tempGluUnProjectData[33] = (n2 - array3[n6 + 1]) * 2.0f / array3[n6 + 3] - 1.0f;
        RenderContext._tempGluUnProjectData[34] = 2.0f * n3 - 1.0f;
        RenderContext._tempGluUnProjectData[35] = 1.0f;
        Matrix.multiplyMM(RenderContext._tempGluUnProjectData, 16, array2, n5, array, n4);
        Matrix.invertM(RenderContext._tempGluUnProjectData, 0, RenderContext._tempGluUnProjectData, 16);
        Matrix.multiplyMV(RenderContext._tempGluUnProjectData, 36, RenderContext._tempGluUnProjectData, 0, RenderContext._tempGluUnProjectData, 32);
        if (RenderContext._tempGluUnProjectData[39] == 0.0) {
            return 0;
        }
        array4[n7] = RenderContext._tempGluUnProjectData[36] / RenderContext._tempGluUnProjectData[39];
        array4[n7 + 1] = RenderContext._tempGluUnProjectData[37] / RenderContext._tempGluUnProjectData[39];
        array4[n7 + 2] = RenderContext._tempGluUnProjectData[38] / RenderContext._tempGluUnProjectData[39];
        return 1;
    }
    
    private void initPrimProgram(final PrimProgram primProgram, final boolean b) {
        GLES20.glUseProgram(primProgram.getHandle());
        GLES20.glUniform1i(primProgram.sTexture, 0);
        WindlightPreset windlightPreset;
        if (b) {
            windlightPreset = this.windlightPreset;
        }
        else {
            windlightPreset = null;
        }
        primProgram.SetupLighting(this, windlightPreset);
    }
    
    final void ClearFrameKeeps() {
        this.frameKeepTextures.clear();
        this.frameKeepBuffers.clear();
        this.frameKeepBitmaps.clear();
    }
    
    public final void KeepBuffer(@Nonnull final DirectByteBuffer directByteBuffer) {
        this.frameKeepBuffers.add(directByteBuffer);
    }
    
    public final void KeepTexture(@Nonnull final Bitmap bitmap) {
        this.frameKeepBitmaps.add(bitmap);
    }
    
    public final void KeepTexture(@Nonnull final OpenJPEG openJPEG) {
        this.frameKeepTextures.add(openJPEG);
    }
    
    final void RunLoadQueue() {
        this.loadQueue.RunLoadQueue(this);
    }
    
    final void StopLoadQueue() {
        this.loadQueue.StopLoadQueue();
    }
    
    public void bindFaceTexture(@Nullable final DrawableFaceTexture boundFaceTexture) {
        if (this.boundFaceTexture != boundFaceTexture) {
            this.boundFaceTexture = boundFaceTexture;
            final boolean textureEnabled = boundFaceTexture != null && boundFaceTexture.GLDraw(this);
            if (this.curPrimProgram != null) {
                this.curPrimProgram.setTextureEnabled(textureEnabled);
            }
            if (!textureEnabled) {
                GLES20.glBindTexture(3553, 0);
            }
        }
    }
    
    public void bindRiggingMeshData(@Nullable final MeshRiggingData boundMeshRiggingData) {
        if (this.boundMeshRiggingData != boundMeshRiggingData) {
            if (boundMeshRiggingData != null) {
                boundMeshRiggingData.SetupBuffers30(this);
            }
            this.boundMeshRiggingData = boundMeshRiggingData;
        }
    }
    
    void clearFaceTexture() {
        this.boundFaceTexture = null;
    }
    
    @TargetApi(18)
    public void clearRiggedMeshProgram() {
        this.currentRiggedMeshProgram = null;
        this.curPrimProgram = null;
        GLES30.glBindVertexArray(0);
        GLES20.glBindTexture(3553, 0);
        GLES20.glUseProgram(0);
        this.clearRiggingMeshData();
        this.clearFaceTexture();
    }
    
    public void enqueueOcclusionQuery(final GLQuery e) {
        this.activeOcclusionQueries.add(e);
    }
    
    final MatrixStack getActiveProjectionMatrix() {
        return this.activeProjectionMatrix;
    }
    
    boolean getShaderCompileErrors() {
        return this.shaderCompileErrors;
    }
    
    public void glBindArrayBuffer(final int n) {
        if (this.hasGL20) {
            GLES20.glBindBuffer(34962, n);
        }
        else {
            GLES11.glBindBuffer(34962, n);
        }
    }
    
    public void glBindElementArrayBuffer(final int n) {
        if (this.hasGL20) {
            GLES20.glBindBuffer(34963, n);
        }
        else {
            GLES11.glBindBuffer(34963, n);
        }
    }
    
    public void glBufferArrayData(final int n, final Buffer buffer, final boolean b) {
        int n2 = 35048;
        if (this.hasGL20) {
            if (!b) {
                n2 = 35044;
            }
            GLES20.glBufferData(34962, n, buffer, n2);
        }
        else {
            if (!b) {
                n2 = 35044;
            }
            GLES11.glBufferData(34962, n, buffer, n2);
        }
    }
    
    public void glBufferElementArrayData(final int n, final Buffer buffer, final boolean b) {
        int n2 = 35048;
        if (this.hasGL20) {
            if (!b) {
                n2 = 35044;
            }
            GLES20.glBufferData(34963, n, buffer, n2);
        }
        else {
            if (!b) {
                n2 = 35044;
            }
            GLES11.glBufferData(34963, n, buffer, n2);
        }
    }
    
    public void glGenBuffers(final int n, final int[] array, final int n2) {
        if (this.hasGL20) {
            GLES20.glGenBuffers(n, array, n2);
        }
        else {
            GLES11.glGenBuffers(n, array, n2);
        }
    }
    
    public void glModelApplyMatrix(final int n) {
        if (this.hasGL20) {
            this.modelViewMatrix.glApplyUniformMatrix(n);
        }
    }
    
    public void glModelMultMatrixf(final float[] array, final int n) {
        if (!this.hasGL20) {
            GLES10.glMultMatrixf(array, n);
        }
        this.modelViewMatrix.glMultMatrixf(array, n);
    }
    
    public void glModelPopMatrix() {
        if (!this.hasGL20) {
            GLES10.glPopMatrix();
        }
        this.modelViewMatrix.glPopMatrix();
    }
    
    public void glModelPushAndMultMatrixf(final float[] array, final int n) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glMultMatrixf(array, n);
        }
        this.modelViewMatrix.glPushAndMultMatrixf(array, n);
    }
    
    public void glModelPushMatrix() {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
        }
        this.modelViewMatrix.glPushMatrix();
    }
    
    void glModelResetIdentity() {
        this.modelViewMatrix.reset();
        this.modelViewMatrix.glLoadIdentity();
        this.objWorldMatrix.reset();
        this.objWorldMatrix.glLoadIdentity();
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleZ = 1.0f;
    }
    
    void glModelRotatef(final float n, final float n2, final float n3, final float n4) {
        if (!this.hasGL20) {
            GLES10.glRotatef(n, n2, n3, n4);
        }
        this.modelViewMatrix.glRotatef(n, n2, n3, n4);
    }
    
    public void glModelScalef(final float n, final float n2, final float n3) {
        if (!this.hasGL20) {
            GLES10.glScalef(n, n2, n3);
        }
        this.modelViewMatrix.glScalef(n, n2, n3);
    }
    
    public void glModelTranslatef(final float n, final float n2, final float n3) {
        if (!this.hasGL20) {
            GLES10.glTranslatef(n, n2, n3);
        }
        this.modelViewMatrix.glTranslatef(n, n2, n3);
    }
    
    public void glObjScaleApplyVector(final int n) {
        if (this.hasGL20) {
            GLES20.glUniform4f(n, this.scaleX, this.scaleY, this.scaleZ, 1.0f);
        }
    }
    
    public void glObjWorldApplyMatrix(final int n) {
        if (this.hasGL20) {
            this.objWorldMatrix.glApplyUniformMatrix(n);
        }
    }
    
    public void glObjWorldPopMatrix() {
        if (!this.hasGL20) {
            GLES10.glPopMatrix();
        }
        this.objWorldMatrix.glPopMatrix();
    }
    
    public void glObjWorldPushAndLoadMatrixf(final float[] array, final int n) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glLoadMatrixf(this.modelViewMatrix.getMatrixData(), this.modelViewMatrix.getMatrixDataOffset());
            GLES10.glMultMatrixf(array, n);
        }
        this.objWorldMatrix.glPushAndLoadMatrixf(array, n);
    }
    
    public void glObjWorldPushAndMultMatrixf(final float[] array, final int n) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glMultMatrixf(array, n);
        }
        this.objWorldMatrix.glPushAndMultMatrixf(array, n);
    }
    
    public void glObjWorldTranslatef(final float n, final float n2, final float n3) {
        if (!this.hasGL20) {
            GLES10.glTranslatef(n, n2, n3);
        }
        this.objWorldMatrix.glTranslatef(n, n2, n3);
    }
    
    void glPopObjectScale() {
        if (!this.hasGL20) {
            GLES10.glPopMatrix();
        }
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.scaleZ = 1.0f;
    }
    
    void glPushObjectScale(final float n, final float n2, final float n3) {
        if (!this.hasGL20) {
            GLES10.glPushMatrix();
            GLES10.glScalef(n, n2, n3);
        }
        this.scaleX *= n;
        this.scaleY *= n2;
        this.scaleZ *= n3;
    }
    
    void initAllPrimPrograms(final boolean b) {
        if (this.hasGL20) {
            this.initPrimProgram(this.primProgram, b);
            this.initPrimProgram(this.primOpaqueProgram, b);
            this.initPrimProgram(this.flexiPrimProgram, b);
            this.initPrimProgram(this.flexiPrimOpaqueProgram, b);
            this.initPrimProgram(this.riggedMeshProgram, b);
            if (this.hasGL30) {
                this.initPrimProgram(this.riggedMeshProgram30, b);
                this.initPrimProgram(this.riggedMeshProgramOpaque30, b);
            }
        }
    }
    
    void processOcclusionQueries() {
        while (true) {
            final GLQuery glQuery = this.activeOcclusionQueries.peekFirst();
            if (glQuery == null || !glQuery.checkResult()) {
                break;
            }
            this.activeOcclusionQueries.removeFirst();
        }
    }
    
    final void setActiveProjectionMatrix(final MatrixStack activeProjectionMatrix) {
        this.activeProjectionMatrix = activeProjectionMatrix;
        if (!this.hasGL20) {
            GLES10.glMatrixMode(5889);
            GLES10.glLoadMatrixf(activeProjectionMatrix.getMatrixData(), activeProjectionMatrix.getMatrixDataOffset());
            GLES10.glMatrixMode(5888);
            GLES10.glLoadIdentity();
            this.modelViewMatrix.reset();
            this.modelViewMatrix.glLoadIdentity();
        }
        else {
            this.modelViewMatrix.reset();
            this.modelViewMatrix.glLoadMatrixf(activeProjectionMatrix.getMatrixData(), activeProjectionMatrix.getMatrixDataOffset());
        }
    }
    
    final void setActiveProjectionMatrix(final float[] array, final int n) {
        this.modelViewMatrix.reset();
        this.modelViewMatrix.glLoadMatrixf(array, n);
    }
    
    void setMeshCapURL(final String meshCapURL) {
        this.drawableStore.setMeshCapURL(meshCapURL);
    }
    
    @TargetApi(18)
    public void setupRiggedMeshProgram(final boolean b) {
        RiggedMeshProgram30 currentRiggedMeshProgram;
        if (b) {
            currentRiggedMeshProgram = this.riggedMeshProgramOpaque30;
        }
        else {
            currentRiggedMeshProgram = this.riggedMeshProgram30;
        }
        this.currentRiggedMeshProgram = currentRiggedMeshProgram;
        this.clearRiggingMeshData();
        this.clearFaceTexture();
        this.curPrimProgram = this.currentRiggedMeshProgram;
        GLES20.glUseProgram(this.currentRiggedMeshProgram.getHandle());
        this.glModelApplyMatrix(this.currentRiggedMeshProgram.uMVPMatrix);
        this.glObjWorldApplyMatrix(this.currentRiggedMeshProgram.uObjWorldMatrix);
        this.glObjScaleApplyVector(this.currentRiggedMeshProgram.uObjCoordScale);
    }
    
    private static class Shaders30
    {
        final BoundingBoxProgram boundingBoxProgram;
        final RiggedMeshProgram30 riggedMeshProgram30;
        final RiggedMeshProgram30 riggedMeshProgramOpaque30;
        
        private Shaders30(final ShaderPreprocessor shaderPreprocessor) throws ShaderCompileException {
            this.riggedMeshProgram30 = new RiggedMeshProgram30(false);
            this.riggedMeshProgramOpaque30 = new RiggedMeshProgram30(true);
            this.boundingBoxProgram = new BoundingBoxProgram();
            this.riggedMeshProgram30.Compile(shaderPreprocessor);
            this.riggedMeshProgramOpaque30.Compile(shaderPreprocessor);
            this.boundingBoxProgram.Compile(shaderPreprocessor);
        }
    }
}
