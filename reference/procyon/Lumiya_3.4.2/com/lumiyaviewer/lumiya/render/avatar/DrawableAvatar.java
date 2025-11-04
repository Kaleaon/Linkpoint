// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.CollisionBox;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.google.common.collect.Multimap;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import android.opengl.GLES11;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.EnumMap;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;
import com.lumiyaviewer.lumiya.render.spatial.DrawListEntry;
import java.util.Set;
import java.util.UUID;
import java.util.Map;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;

public class DrawableAvatar extends DrawableAvatarStub implements IntersectPickable, EntryRemovalListener
{
    private final Object animationLock;
    private final AnimationSkeletonData animationSkeletonData;
    private final Map<UUID, AvatarAnimationState> animations;
    private volatile boolean animationsInitialized;
    private final Set<DrawListEntry> deadAttachmentsList;
    private final Object deadAttachmentsLock;
    private final AtomicInteger displayedHUDid;
    @Nonnull
    private volatile DrawableAttachments drawableAttachmentList;
    private final DrawEntryList drawableAttachments;
    private volatile DrawableHUD drawableHUD;
    private LLVector3 headPosition;
    private boolean jointMatrixUpdated;
    private final float[] localAviWorldMatrix;
    private final Map<MeshIndex, DrawableAvatarPart> parts;
    private float pelvisTranslateX;
    private float pelvisTranslateY;
    private float pelvisTranslateZ;
    private final Set<DrawableObject> riggedMeshes;
    private volatile AvatarAnimationList runningAnimations;
    private volatile AvatarShapeParams shapeParams;
    private final Runnable shapeParamsUpdate;
    private volatile AvatarSkeleton skeleton;
    private final Runnable updateAttachmentsRunnable;
    private final AtomicReference<AvatarSkeleton> updatedSkeleton;
    
    public DrawableAvatar(DrawableStore animationLock, final UUID uuid, final SLObjectAvatarInfo slObjectAvatarInfo, final UUID uuid2, final Map<UUID, AnimationSequenceInfo> map) {
        int i = 0;
        super(animationLock, uuid, slObjectAvatarInfo);
        this.updatedSkeleton = new AtomicReference<AvatarSkeleton>(null);
        this.parts = new EnumMap<MeshIndex, DrawableAvatarPart>(MeshIndex.class);
        this.animationLock = new Object();
        this.animations = new HashMap<UUID, AvatarAnimationState>();
        this.runningAnimations = null;
        this.animationsInitialized = false;
        this.drawableAttachmentList = new DrawableAttachments();
        this.displayedHUDid = new AtomicInteger();
        this.drawableAttachments = new DrawEntryList((DrawEntryList.EntryRemovalListener)this);
        this.deadAttachmentsLock = new Object();
        this.deadAttachmentsList = Collections.newSetFromMap(new IdentityHashMap<DrawListEntry, Boolean>());
        this.riggedMeshes = Collections.newSetFromMap(new ConcurrentHashMap<DrawableObject, Boolean>());
        this.animationSkeletonData = new AnimationSkeletonData();
        this.pelvisTranslateX = 0.0f;
        this.pelvisTranslateY = 0.0f;
        this.pelvisTranslateZ = 0.0f;
        this.localAviWorldMatrix = new float[16];
        this.jointMatrixUpdated = false;
        this.updateAttachmentsRunnable = new _$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU(this);
        this.shapeParamsUpdate = new _$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU$1(this);
        final SLBaseAvatar instance = SLBaseAvatar.getInstance();
        for (MeshIndex[] values = MeshIndex.VALUES; i < values.length; ++i) {
            final MeshIndex meshIndex = values[i];
            this.parts.put(meshIndex, new DrawableAvatarPart(uuid2, instance.getMeshEntry(meshIndex).textureFaceIndex, instance.getMeshEntry(meshIndex).polyMesh, animationLock.hasGL20));
        }
        animationLock = (DrawableStore)this.animationLock;
        monitorenter(animationLock);
        if (map != null) {
            try {
                for (final AnimationSequenceInfo animationSequenceInfo : map.values()) {
                    this.animations.put(animationSequenceInfo.animationID, new AvatarAnimationState(animationSequenceInfo, this));
                }
            }
            finally {
                monitorexit(animationLock);
            }
        }
        this.animationsInitialized = true;
        this.updateRunningAnimations();
        monitorexit(animationLock);
        this.updateAttachments();
    }
    
    private void DrawParts(final RenderContext renderContext) {
        final AvatarSkeleton skeleton = this.skeleton;
        if (skeleton == null) {
            return;
        }
        final float bodySize = skeleton.getBodySize();
        final float pelvisToFoot = skeleton.getPelvisToFoot();
        final float pelvisOffset = skeleton.getPelvisOffset();
        float n;
        if (this.avatarObject.parentID == 0) {
            n = -bodySize / 2.0f + pelvisToFoot + pelvisOffset;
        }
        else {
            n = 0.0f;
        }
        this.pelvisTranslateX = -skeleton.rootBone.getPositionX();
        this.pelvisTranslateY = -skeleton.rootBone.getPositionY();
        this.pelvisTranslateZ = n + -skeleton.rootBone.getPositionZ();
        renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ);
        renderContext.objWorldMatrix.getMatrix(this.localAviWorldMatrix, 0);
        this.GLPrepare(renderContext, skeleton.jointMatrix);
        for (final MeshIndex meshIndex : MeshIndex.VALUES) {
            final DrawableAvatarPart drawableAvatarPart = this.parts.get(meshIndex);
            SLSkeletonBone slSkeletonBone;
            if (meshIndex == MeshIndex.MESH_ID_EYEBALL_LEFT) {
                slSkeletonBone = skeleton.bones.get(SLSkeletonBoneID.mEyeLeft);
            }
            else if (meshIndex == MeshIndex.MESH_ID_EYEBALL_RIGHT) {
                slSkeletonBone = skeleton.bones.get(SLSkeletonBoneID.mEyeRight);
            }
            else {
                slSkeletonBone = null;
            }
            if (slSkeletonBone != null) {
                renderContext.glObjWorldPushAndMultMatrixf(slSkeletonBone.getGlobalMatrix(), 0);
            }
            drawableAvatarPart.GLDraw(renderContext, skeleton.jointMatrix, this.jointMatrixUpdated);
            if (slSkeletonBone != null) {
                renderContext.glObjWorldPopMatrix();
            }
        }
        final SLSkeletonBone slSkeletonBone2 = skeleton.bones.get(SLSkeletonBoneID.mHead);
        if (slSkeletonBone2 != null) {
            renderContext.glObjWorldPushAndMultMatrixf(slSkeletonBone2.getGlobalMatrix(), 0);
            final float[] matrixData = renderContext.objWorldMatrix.getMatrixData();
            final int matrixDataOffset = renderContext.objWorldMatrix.getMatrixDataOffset();
            final float n2 = matrixData[matrixDataOffset + 12];
            final float n3 = matrixData[matrixDataOffset + 13];
            final float n4 = matrixData[matrixDataOffset + 14];
            if (this.headPosition == null) {
                this.headPosition = new LLVector3();
            }
            this.headPosition.set(n2, n3, n4);
            renderContext.glObjWorldPopMatrix();
        }
        renderContext.curPrimProgram = null;
        if (this.drawableAttachmentList.Draw(renderContext, skeleton, this.jointMatrixUpdated)) {
            this.drawableAttachmentList = new DrawableAttachments(this.drawableAttachmentList);
        }
        this.jointMatrixUpdated = false;
    }
    
    private void GLPrepare(final RenderContext renderContext, final float[] array) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.avatarProgram.getHandle());
            GLES20.glUniform1i(renderContext.avatarProgram.sTexture, 0);
            GLES20.glUniform4f(renderContext.avatarProgram.uObjCoordScale, 1.0f, 1.0f, 1.0f, 1.0f);
            renderContext.glModelApplyMatrix(renderContext.avatarProgram.uMVPMatrix);
            renderContext.avatarProgram.SetupLighting(renderContext, renderContext.windlightPreset);
            GLES20.glUniformMatrix4fv(renderContext.avatarProgram.uJointMatrix, 133, false, array, 0);
        }
        else {
            GLES11.glMatrixMode(5890);
            GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0);
            GLES11.glMatrixMode(5888);
        }
    }
    
    private boolean animate(final AvatarSkeleton avatarSkeleton) {
        final boolean needForceAnimate = avatarSkeleton.needForceAnimate();
        final AvatarAnimationList runningAnimations = this.runningAnimations;
        if (runningAnimations.needAnimate(System.currentTimeMillis()) || needForceAnimate) {
            this.animationSkeletonData.animate(avatarSkeleton, runningAnimations);
            return true;
        }
        return false;
    }
    
    private AvatarAnimationList getRunningAnimations() {
        synchronized (this.animationLock) {
            return new AvatarAnimationList(this.animations.values());
        }
    }
    
    private void processUpdateAttachments() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: invokestatic    com/google/common/collect/ArrayListMultimap.create:()Lcom/google/common/collect/ArrayListMultimap;
        //     5: astore_2       
        //     6: new             Ljava/util/IdentityHashMap;
        //     9: dup            
        //    10: invokespecial   java/util/IdentityHashMap.<init>:()V
        //    13: invokestatic    java/util/Collections.newSetFromMap:(Ljava/util/Map;)Ljava/util/Set;
        //    16: astore_3       
        //    17: aload_0        
        //    18: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.displayedHUDid:Ljava/util/concurrent/atomic/AtomicInteger;
        //    21: invokevirtual   java/util/concurrent/atomic/AtomicInteger.get:()I
        //    24: istore          4
        //    26: aload_0        
        //    27: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.avatarObject:Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo;
        //    30: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo.treeNode:Lcom/lumiyaviewer/lumiya/utils/LinkedTreeNode;
        //    33: invokevirtual   com/lumiyaviewer/lumiya/utils/LinkedTreeNode.getFirstChild:()Lcom/lumiyaviewer/lumiya/utils/LinkedTreeNode;
        //    36: astore          5
        //    38: aconst_null    
        //    39: astore          6
        //    41: aload           5
        //    43: ifnull          161
        //    46: aload           5
        //    48: invokevirtual   com/lumiyaviewer/lumiya/utils/LinkedTreeNode.getDataObject:()Ljava/lang/Object;
        //    51: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;
        //    54: astore          7
        //    56: aload           7
        //    58: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.isDead:Z
        //    61: ifne            499
        //    64: aload           7
        //    66: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.attachmentID:I
        //    69: istore          8
        //    71: iload           8
        //    73: iflt            146
        //    76: iload           8
        //    78: bipush          56
        //    80: if_icmpge       146
        //    83: getstatic       com/lumiyaviewer/lumiya/slproto/avatar/SLAttachmentPoint.attachmentPoints:[Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAttachmentPoint;
        //    86: iload           8
        //    88: aaload         
        //    89: astore          9
        //    91: aload           9
        //    93: ifnull          499
        //    96: aload           9
        //    98: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAttachmentPoint.isHUD:Z
        //   101: ifeq            149
        //   104: iload           4
        //   106: aload           7
        //   108: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.localID:I
        //   111: if_icmpne       499
        //   114: new             Lcom/lumiyaviewer/lumiya/render/avatar/DrawableHUD;
        //   117: dup            
        //   118: aload           9
        //   120: aload_0        
        //   121: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.drawableAttachments:Lcom/lumiyaviewer/lumiya/render/spatial/DrawEntryList;
        //   124: aload           7
        //   126: aload_0        
        //   127: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.drawableStore:Lcom/lumiyaviewer/lumiya/render/DrawableStore;
        //   130: aload_0        
        //   131: invokespecial   com/lumiyaviewer/lumiya/render/avatar/DrawableHUD.<init>:(Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAttachmentPoint;Lcom/lumiyaviewer/lumiya/render/spatial/DrawEntryList;Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;Lcom/lumiyaviewer/lumiya/render/DrawableStore;Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatar;)V
        //   134: astore          6
        //   136: aload           5
        //   138: invokevirtual   com/lumiyaviewer/lumiya/utils/LinkedTreeNode.getNextChild:()Lcom/lumiyaviewer/lumiya/utils/LinkedTreeNode;
        //   141: astore          5
        //   143: goto            41
        //   146: goto            136
        //   149: aload_0        
        //   150: aload           7
        //   152: aload_2        
        //   153: iload           8
        //   155: invokespecial   com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.updateAttachmentParts:(Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;Lcom/google/common/collect/Multimap;I)V
        //   158: goto            136
        //   161: aload_0        
        //   162: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.deadAttachmentsLock:Ljava/lang/Object;
        //   165: astore          7
        //   167: aload           7
        //   169: monitorenter   
        //   170: aload_0        
        //   171: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.deadAttachmentsList:Ljava/util/Set;
        //   174: invokeinterface java/util/Set.isEmpty:()Z
        //   179: ifne            493
        //   182: aload_0        
        //   183: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.deadAttachmentsList:Ljava/util/Set;
        //   186: aload_0        
        //   187: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.deadAttachmentsList:Ljava/util/Set;
        //   190: invokeinterface java/util/Set.size:()I
        //   195: anewarray       Lcom/lumiyaviewer/lumiya/render/spatial/DrawListEntry;
        //   198: invokeinterface java/util/Set.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   203: checkcast       [Lcom/lumiyaviewer/lumiya/render/spatial/DrawListEntry;
        //   206: astore          5
        //   208: aload_0        
        //   209: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.deadAttachmentsList:Ljava/util/Set;
        //   212: invokeinterface java/util/Set.clear:()V
        //   217: aload           7
        //   219: monitorexit    
        //   220: aload_2        
        //   221: invokeinterface com/google/common/collect/Multimap.values:()Ljava/util/Collection;
        //   226: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   231: astore          9
        //   233: iconst_0       
        //   234: istore          4
        //   236: aload           9
        //   238: invokeinterface java/util/Iterator.hasNext:()Z
        //   243: ifeq            303
        //   246: aload           9
        //   248: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   253: checkcast       Lcom/lumiyaviewer/lumiya/render/DrawableObject;
        //   256: astore          7
        //   258: aload           7
        //   260: invokevirtual   com/lumiyaviewer/lumiya/render/DrawableObject.isRiggedMesh:()Z
        //   263: ifeq            490
        //   266: aload_0        
        //   267: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.riggedMeshes:Ljava/util/Set;
        //   270: aload           7
        //   272: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //   277: ifeq            283
        //   280: iconst_1       
        //   281: istore          4
        //   283: aload_3        
        //   284: aload           7
        //   286: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //   291: pop            
        //   292: goto            236
        //   295: astore          6
        //   297: aload           7
        //   299: monitorexit    
        //   300: aload           6
        //   302: athrow         
        //   303: iload           4
        //   305: istore          8
        //   307: aload           5
        //   309: ifnull          397
        //   312: aload           5
        //   314: arraylength    
        //   315: istore          10
        //   317: iload           4
        //   319: istore          8
        //   321: iload_1        
        //   322: iload           10
        //   324: if_icmpge       397
        //   327: aload           5
        //   329: iload_1        
        //   330: aaload         
        //   331: astore          7
        //   333: aload_0        
        //   334: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.drawableAttachments:Lcom/lumiyaviewer/lumiya/render/spatial/DrawEntryList;
        //   337: aload           7
        //   339: invokevirtual   com/lumiyaviewer/lumiya/render/spatial/DrawEntryList.removeEntry:(Lcom/lumiyaviewer/lumiya/utils/InlineListEntry;)V
        //   342: aload           7
        //   344: instanceof      Lcom/lumiyaviewer/lumiya/render/spatial/DrawListPrimEntry;
        //   347: ifeq            487
        //   350: aload           7
        //   352: checkcast       Lcom/lumiyaviewer/lumiya/render/spatial/DrawListPrimEntry;
        //   355: invokevirtual   com/lumiyaviewer/lumiya/render/spatial/DrawListPrimEntry.getDrawableObject:()Lcom/lumiyaviewer/lumiya/render/DrawableObject;
        //   358: astore          7
        //   360: aload           7
        //   362: ifnull          487
        //   365: aload_3        
        //   366: aload           7
        //   368: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
        //   373: pop            
        //   374: aload_0        
        //   375: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.riggedMeshes:Ljava/util/Set;
        //   378: aload           7
        //   380: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
        //   385: ifeq            487
        //   388: iconst_1       
        //   389: istore          4
        //   391: iinc            1, 1
        //   394: goto            317
        //   397: aload_0        
        //   398: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.riggedMeshes:Ljava/util/Set;
        //   401: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   406: astore          5
        //   408: aload           5
        //   410: invokeinterface java/util/Iterator.hasNext:()Z
        //   415: ifeq            459
        //   418: aload           5
        //   420: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   425: checkcast       Lcom/lumiyaviewer/lumiya/render/DrawableObject;
        //   428: astore          7
        //   430: aload_3        
        //   431: aload           7
        //   433: invokeinterface java/util/Set.contains:(Ljava/lang/Object;)Z
        //   438: ifne            408
        //   441: aload_0        
        //   442: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.riggedMeshes:Ljava/util/Set;
        //   445: aload           7
        //   447: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
        //   452: pop            
        //   453: iconst_1       
        //   454: istore          8
        //   456: goto            408
        //   459: aload_0        
        //   460: aload           6
        //   462: putfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.drawableHUD:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableHUD;
        //   465: aload_0        
        //   466: new             Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAttachments;
        //   469: dup            
        //   470: aload_2        
        //   471: invokespecial   com/lumiyaviewer/lumiya/render/avatar/DrawableAttachments.<init>:(Lcom/google/common/collect/Multimap;)V
        //   474: putfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.drawableAttachmentList:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAttachments;
        //   477: iload           8
        //   479: ifeq            486
        //   482: aload_0        
        //   483: invokespecial   com/lumiyaviewer/lumiya/render/avatar/DrawableAvatar.updateRiggedMeshes:()V
        //   486: return         
        //   487: goto            391
        //   490: goto            292
        //   493: aconst_null    
        //   494: astore          5
        //   496: goto            217
        //   499: goto            136
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  170    217    295    303    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
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
    
    private void updateAttachmentParts(final SLObjectInfo slObjectInfo, final Multimap<Integer, DrawableObject> multimap, final int i) {
        final DrawListObjectEntry drawListEntry = slObjectInfo.getDrawListEntry();
        ((InlineList<DrawListPrimEntry>)this.drawableAttachments).addEntry(drawListEntry);
        if (drawListEntry instanceof DrawListPrimEntry) {
            multimap.put(i, ((DrawListPrimEntry)drawListEntry).getDrawableAttachment(this.drawableStore, this));
        }
        for (LinkedTreeNode<SLObjectInfo> linkedTreeNode = slObjectInfo.treeNode.getFirstChild(); linkedTreeNode != null; linkedTreeNode = linkedTreeNode.getNextChild()) {
            final SLObjectInfo slObjectInfo2 = linkedTreeNode.getDataObject();
            if (slObjectInfo2 != null) {
                this.updateAttachmentParts(slObjectInfo2, multimap, i);
            }
        }
    }
    
    private void updateRiggedMeshes() {
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate);
    }
    
    void AnimationRemove(final UUID uuid) {
        synchronized (this.animationLock) {
            int n;
            if (this.animations.remove(uuid) != null) {
                n = 1;
            }
            else {
                n = 0;
            }
            monitorexit(this.animationLock);
            if (n != 0) {
                this.updateRunningAnimations();
            }
        }
    }
    
    void AnimationUpdate(final AnimationSequenceInfo animationSequenceInfo) {
        final UUID animationID = animationSequenceInfo.animationID;
        synchronized (this.animationLock) {
            final AvatarAnimationState avatarAnimationState = this.animations.get(animationID);
            if (avatarAnimationState == null) {
                this.animations.put(animationID, new AvatarAnimationState(animationSequenceInfo, this));
            }
            else {
                avatarAnimationState.updateSequenceInfo(animationSequenceInfo);
            }
            monitorexit(this.animationLock);
            this.updateRunningAnimations();
        }
    }
    
    public void Draw(final RenderContext renderContext) {
        final float[] worldMatrix = this.getWorldMatrix(renderContext);
        if (worldMatrix == null) {
            return;
        }
        try {
            renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
            this.DrawParts(renderContext);
            renderContext.glObjWorldPopMatrix();
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
        }
    }
    
    @Override
    public void DrawNameTag(final RenderContext renderContext) {
        final DrawableHoverText drawableNameTag = this.drawableNameTag;
        if (drawableNameTag != null) {
            final LLVector3 headPosition = this.headPosition;
            if (headPosition != null) {
                drawableNameTag.DrawAtWorld(renderContext, headPosition.x, headPosition.y, headPosition.z, 0.5f, renderContext.projectionMatrix, false, 0);
            }
            else {
                super.DrawNameTag(renderContext);
            }
        }
    }
    
    boolean IsAnimationStopped(final UUID uuid) {
        synchronized (this.animationLock) {
            final AvatarAnimationState avatarAnimationState = this.animations.get(uuid);
            return avatarAnimationState != null && avatarAnimationState.hasStopped();
        }
    }
    
    @Override
    public ObjectIntersectInfo PickObject(final RenderContext renderContext, final float n, float n2, final float n3) {
        final float[] worldMatrix = this.getWorldMatrix(renderContext);
        final AvatarSkeleton skeleton = this.skeleton;
        ObjectIntersectInfo objectIntersectInfo = null;
        if (worldMatrix != null) {
            objectIntersectInfo = objectIntersectInfo;
            if (skeleton != null) {
                final int[] viewportRect = renderContext.viewportRect;
                final float[] array = new float[32];
                final float[] array2 = new float[6];
                n2 = viewportRect[3] - n2;
                renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
                renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ);
                while (true) {
                    for (final SLSkeletonBone slSkeletonBone : skeleton.bones.values()) {
                        if (!slSkeletonBone.boneID.isJoint) {
                            renderContext.glObjWorldPushAndMultMatrixf(skeleton.jointWorldMatrix, slSkeletonBone.boneID.ordinal() * 16);
                            if (renderContext.hasGL20) {
                                Matrix.scaleM(array, 0, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), 1.0f, 1.0f, 1.0f);
                                RenderContext.gluUnProject(n, n2, 0.0f, array, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 0);
                                RenderContext.gluUnProject(n, n2, 1.0f, array, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 3);
                            }
                            else {
                                Matrix.scaleM(array, 16, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), 1.0f, 1.0f, 1.0f);
                                Matrix.multiplyMM(array, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), array, 16);
                                RenderContext.gluUnProject(n, n2, 0.0f, array, 0, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 0);
                                RenderContext.gluUnProject(n, n2, 1.0f, array, 0, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 3);
                            }
                            renderContext.glObjWorldPopMatrix();
                            final LLVector3 llVector3 = new LLVector3(array2[0], array2[1], array2[2]);
                            final LLVector3 llVector4 = new LLVector3(array2[3], array2[4], array2[5]);
                            final LLVector3[] vertices = CollisionBox.getInstance().vertices;
                            Object intersect_RayTriangle = null;
                            for (int i = 0; i < 12; ++i) {
                                intersect_RayTriangle = GLRayTrace.intersect_RayTriangle(llVector3, llVector4, vertices, i * 3);
                                if (intersect_RayTriangle != null) {
                                    break;
                                }
                            }
                            if (intersect_RayTriangle == null) {
                                continue;
                            }
                            final float intersectionDepth = GLRayTrace.getIntersectionDepth(renderContext, ((GLRayTrace.RayIntersectInfo)intersect_RayTriangle).intersectPoint, array);
                            if (intersectionDepth >= n3) {
                                objectIntersectInfo = new ObjectIntersectInfo(new IntersectInfo(((GLRayTrace.RayIntersectInfo)intersect_RayTriangle).intersectPoint), this.avatarObject, intersectionDepth);
                                renderContext.glObjWorldPopMatrix();
                                return objectIntersectInfo;
                            }
                            continue;
                        }
                    }
                    objectIntersectInfo = null;
                    continue;
                }
            }
        }
        return objectIntersectInfo;
    }
    
    public void RunAnimations() {
        final AvatarSkeleton skeleton = this.updatedSkeleton.getAndSet(null);
        if (skeleton != null) {
            this.skeleton = skeleton;
        }
        final AvatarSkeleton skeleton2 = this.skeleton;
        if (skeleton2 != null && this.animate(skeleton2)) {
            skeleton2.UpdateGlobalPositions(this.animationSkeletonData);
            this.jointMatrixUpdated |= true;
        }
    }
    
    void UpdateShapeParams(final AvatarShapeParams shapeParams) {
        this.shapeParams = shapeParams;
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate);
    }
    
    void UpdateTextures(final AvatarTextures avatarTextures) {
        for (final Map.Entry<K, DrawableAvatarPart> entry : this.parts.entrySet()) {
            entry.getValue().setTexture(this.drawableStore.glTextureCache, avatarTextures.getTexture(entry.getValue().getFaceIndex()));
        }
    }
    
    public DrawableHUD getDrawableHUD() {
        return this.drawableHUD;
    }
    
    @Override
    public void onEntryRemovalRequested(final DrawListEntry drawListEntry) {
        synchronized (this.deadAttachmentsLock) {
            this.deadAttachmentsList.add(drawListEntry);
            monitorexit(this.deadAttachmentsLock);
            this.updateAttachments();
        }
    }
    
    public void onRiggedMeshReady(final DrawableObject drawableObject) {
        if (this.riggedMeshes.add(drawableObject)) {
            this.updateRiggedMeshes();
        }
    }
    
    public void setDisplayedHUDid(final int newValue) {
        if (this.displayedHUDid.getAndSet(newValue) != newValue) {
            this.updateAttachments();
        }
    }
    
    public void updateAttachments() {
        PrimComputeExecutor.getInstance().execute(this.updateAttachmentsRunnable);
    }
    
    void updateRunningAnimations() {
        if (this.animationsInitialized) {
            this.runningAnimations = this.getRunningAnimations();
            final AvatarSkeleton skeleton = this.skeleton;
            if (skeleton != null) {
                skeleton.setForceAnimate();
            }
        }
    }
}
