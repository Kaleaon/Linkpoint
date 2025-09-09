package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.picking.CollisionBox;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.render.spatial.DrawListEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class DrawableAvatar extends DrawableAvatarStub implements IntersectPickable, DrawEntryList.EntryRemovalListener {
    private final Object animationLock = new Object();
    private final AnimationSkeletonData animationSkeletonData = new AnimationSkeletonData();
    private final Map<UUID, AvatarAnimationState> animations = new HashMap();
    private volatile boolean animationsInitialized = false;
    private final Set<DrawListEntry> deadAttachmentsList = Collections.newSetFromMap(new IdentityHashMap());
    private final Object deadAttachmentsLock = new Object();
    private final AtomicInteger displayedHUDid = new AtomicInteger();
    @Nonnull
    private volatile DrawableAttachments drawableAttachmentList = new DrawableAttachments();
    private final DrawEntryList drawableAttachments = new DrawEntryList(this);
    private volatile DrawableHUD drawableHUD;
    private LLVector3 headPosition;
    private boolean jointMatrixUpdated = false;
    private final float[] localAviWorldMatrix = new float[16];
    private final Map<MeshIndex, DrawableAvatarPart> parts = new EnumMap(MeshIndex.class);
    private float pelvisTranslateX = 0.0f;
    private float pelvisTranslateY = 0.0f;
    private float pelvisTranslateZ = 0.0f;
    private final Set<DrawableObject> riggedMeshes = Collections.newSetFromMap(new ConcurrentHashMap());
    private volatile AvatarAnimationList runningAnimations = null;
    private volatile AvatarShapeParams shapeParams;
    private final Runnable shapeParamsUpdate = new Runnable(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f49$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.1.$m$0():void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.1.$m$0():void, class status: UNLOADED
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
Method generation error in method: com.lumiyaviewer.lumiya.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.1.run():void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.1.run():void, class status: UNLOADED
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
    private volatile AvatarSkeleton skeleton;
    private final Runnable updateAttachmentsRunnable = new $Lambda$0R0mXpfMxrM5lCygN3JijOMDexU(this);
    private final AtomicReference<AvatarSkeleton> updatedSkeleton = new AtomicReference<>((Object) null);

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DrawableAvatar(DrawableStore drawableStore, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo, UUID uuid2, Map<UUID, AnimationSequenceInfo> map) {
        super(drawableStore, uuid, sLObjectAvatarInfo);
        SLBaseAvatar instance = SLBaseAvatar.getInstance();
        for (MeshIndex meshIndex : MeshIndex.VALUES) {
            this.parts.put(meshIndex, new DrawableAvatarPart(uuid2, instance.getMeshEntry(meshIndex).textureFaceIndex, instance.getMeshEntry(meshIndex).polyMesh, drawableStore.hasGL20));
        }
        synchronized (this.animationLock) {
            if (map != null) {
                for (AnimationSequenceInfo animationSequenceInfo : map.values()) {
                    this.animations.put(animationSequenceInfo.animationID, new AvatarAnimationState(animationSequenceInfo, this));
                }
            }
            this.animationsInitialized = true;
            updateRunningAnimations();
        }
        updateAttachments();
    }

    private void DrawParts(RenderContext renderContext) {
        AvatarSkeleton avatarSkeleton = this.skeleton;
        if (avatarSkeleton != null) {
            float pelvisToFoot = this.avatarObject.parentID == 0 ? ((-avatarSkeleton.getBodySize()) / 2.0f) + avatarSkeleton.getPelvisToFoot() + avatarSkeleton.getPelvisOffset() : 0.0f;
            this.pelvisTranslateX = -avatarSkeleton.rootBone.getPositionX();
            this.pelvisTranslateY = -avatarSkeleton.rootBone.getPositionY();
            this.pelvisTranslateZ = pelvisToFoot + (-avatarSkeleton.rootBone.getPositionZ());
            renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ);
            renderContext.objWorldMatrix.getMatrix(this.localAviWorldMatrix, 0);
            GLPrepare(renderContext, avatarSkeleton.jointMatrix);
            MeshIndex[] meshIndexArr = MeshIndex.VALUES;
            int length = meshIndexArr.length;
            for (int i = 0; i < length; i++) {
                MeshIndex meshIndex = meshIndexArr[i];
                DrawableAvatarPart drawableAvatarPart = this.parts.get(meshIndex);
                SLSkeletonBone sLSkeletonBone = meshIndex == MeshIndex.MESH_ID_EYEBALL_LEFT ? (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mEyeLeft) : meshIndex == MeshIndex.MESH_ID_EYEBALL_RIGHT ? (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mEyeRight) : null;
                if (sLSkeletonBone != null) {
                    renderContext.glObjWorldPushAndMultMatrixf(sLSkeletonBone.getGlobalMatrix(), 0);
                }
                drawableAvatarPart.GLDraw(renderContext, avatarSkeleton.jointMatrix, this.jointMatrixUpdated);
                if (sLSkeletonBone != null) {
                    renderContext.glObjWorldPopMatrix();
                }
            }
            SLSkeletonBone sLSkeletonBone2 = (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mHead);
            if (sLSkeletonBone2 != null) {
                renderContext.glObjWorldPushAndMultMatrixf(sLSkeletonBone2.getGlobalMatrix(), 0);
                float[] matrixData = renderContext.objWorldMatrix.getMatrixData();
                int matrixDataOffset = renderContext.objWorldMatrix.getMatrixDataOffset();
                float f = matrixData[matrixDataOffset + 12];
                float f2 = matrixData[matrixDataOffset + 13];
                float f3 = matrixData[matrixDataOffset + 14];
                if (this.headPosition == null) {
                    this.headPosition = new LLVector3();
                }
                this.headPosition.set(f, f2, f3);
                renderContext.glObjWorldPopMatrix();
            }
            renderContext.curPrimProgram = null;
            if (this.drawableAttachmentList.Draw(renderContext, avatarSkeleton, this.jointMatrixUpdated)) {
                this.drawableAttachmentList = new DrawableAttachments(this.drawableAttachmentList);
            }
            this.jointMatrixUpdated = false;
        }
    }

    private void GLPrepare(RenderContext renderContext, float[] fArr) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.avatarProgram.getHandle());
            GLES20.glUniform1i(renderContext.avatarProgram.sTexture, 0);
            GLES20.glUniform4f(renderContext.avatarProgram.uObjCoordScale, 1.0f, 1.0f, 1.0f, 1.0f);
            renderContext.glModelApplyMatrix(renderContext.avatarProgram.uMVPMatrix);
            renderContext.avatarProgram.SetupLighting(renderContext, renderContext.windlightPreset);
            GLES20.glUniformMatrix4fv(renderContext.avatarProgram.uJointMatrix, 133, false, fArr, 0);
            return;
        }
        GLES11.glMatrixMode(5890);
        GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0);
        GLES11.glMatrixMode(5888);
    }

    private boolean animate(AvatarSkeleton avatarSkeleton) {
        boolean needForceAnimate = avatarSkeleton.needForceAnimate();
        AvatarAnimationList avatarAnimationList = this.runningAnimations;
        if (!avatarAnimationList.needAnimate(System.currentTimeMillis()) && !needForceAnimate) {
            return false;
        }
        this.animationSkeletonData.animate(avatarSkeleton, avatarAnimationList);
        return true;
    }

    private AvatarAnimationList getRunningAnimations() {
        AvatarAnimationList avatarAnimationList;
        synchronized (this.animationLock) {
            avatarAnimationList = new AvatarAnimationList(this.animations.values());
        }
        return avatarAnimationList;
    }

    /* access modifiers changed from: private */
    /* renamed from: processUpdateAttachments */
    public void m52com_lumiyaviewer_lumiya_render_avatar_DrawableAvatarmthref0() {
        DrawListEntry[] drawListEntryArr;
        boolean z;
        DrawableObject drawableObject;
        DrawableHUD drawableHUD2;
        int i = 0;
        ArrayListMultimap create = ArrayListMultimap.create();
        Set newSetFromMap = Collections.newSetFromMap(new IdentityHashMap());
        int i2 = this.displayedHUDid.get();
        LinkedTreeNode firstChild = this.avatarObject.treeNode.getFirstChild();
        DrawableHUD drawableHUD3 = null;
        while (firstChild != null) {
            SLObjectInfo sLObjectInfo = (SLObjectInfo) firstChild.getDataObject();
            if (!sLObjectInfo.isDead) {
                int i3 = sLObjectInfo.attachmentID;
                if (i3 < 0 || i3 >= 56) {
                    drawableHUD2 = drawableHUD3;
                    firstChild = firstChild.getNextChild();
                    drawableHUD3 = drawableHUD2;
                } else {
                    SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i3];
                    if (sLAttachmentPoint != null) {
                        if (!sLAttachmentPoint.isHUD) {
                            updateAttachmentParts(sLObjectInfo, create, i3);
                            drawableHUD2 = drawableHUD3;
                        } else if (i2 == sLObjectInfo.localID) {
                            drawableHUD2 = new DrawableHUD(sLAttachmentPoint, this.drawableAttachments, sLObjectInfo, this.drawableStore, this);
                        }
                        firstChild = firstChild.getNextChild();
                        drawableHUD3 = drawableHUD2;
                    }
                }
            }
            drawableHUD2 = drawableHUD3;
            firstChild = firstChild.getNextChild();
            drawableHUD3 = drawableHUD2;
        }
        synchronized (this.deadAttachmentsLock) {
            if (!this.deadAttachmentsList.isEmpty()) {
                DrawListEntry[] drawListEntryArr2 = (DrawListEntry[]) this.deadAttachmentsList.toArray(new DrawListEntry[this.deadAttachmentsList.size()]);
                this.deadAttachmentsList.clear();
                drawListEntryArr = drawListEntryArr2;
            } else {
                drawListEntryArr = null;
            }
        }
        boolean z2 = false;
        for (DrawableObject drawableObject2 : create.values()) {
            if (drawableObject2.isRiggedMesh()) {
                if (this.riggedMeshes.add(drawableObject2)) {
                    z2 = true;
                }
                newSetFromMap.add(drawableObject2);
            }
            z2 = z2;
        }
        if (drawListEntryArr != null) {
            int length = drawListEntryArr.length;
            while (i < length) {
                DrawListPrimEntry drawListPrimEntry = drawListEntryArr[i];
                this.drawableAttachments.removeEntry(drawListPrimEntry);
                if ((drawListPrimEntry instanceof DrawListPrimEntry) && (drawableObject = drawListPrimEntry.getDrawableObject()) != null) {
                    newSetFromMap.remove(drawableObject);
                    if (this.riggedMeshes.remove(drawableObject)) {
                        z = true;
                        i++;
                        z2 = z;
                    }
                }
                z = z2;
                i++;
                z2 = z;
            }
        }
        for (DrawableObject drawableObject3 : this.riggedMeshes) {
            if (!newSetFromMap.contains(drawableObject3)) {
                this.riggedMeshes.remove(drawableObject3);
                z2 = true;
            }
        }
        this.drawableHUD = drawableHUD3;
        this.drawableAttachmentList = new DrawableAttachments((Multimap<Integer, DrawableObject>) create);
        if (z2) {
            updateRiggedMeshes();
        }
    }

    private void updateAttachmentParts(SLObjectInfo sLObjectInfo, Multimap<Integer, DrawableObject> multimap, int i) {
        DrawListObjectEntry drawListEntry = sLObjectInfo.getDrawListEntry();
        this.drawableAttachments.addEntry(drawListEntry);
        if (drawListEntry instanceof DrawListPrimEntry) {
            multimap.put(Integer.valueOf(i), ((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this));
        }
        for (LinkedTreeNode<SLObjectInfo> firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            SLObjectInfo dataObject = firstChild.getDataObject();
            if (dataObject != null) {
                updateAttachmentParts(dataObject, multimap, i);
            }
        }
    }

    private void updateRiggedMeshes() {
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate);
    }

    /* access modifiers changed from: package-private */
    public void AnimationRemove(UUID uuid) {
        boolean z;
        synchronized (this.animationLock) {
            z = this.animations.remove(uuid) != null;
        }
        if (z) {
            updateRunningAnimations();
        }
    }

    /* access modifiers changed from: package-private */
    public void AnimationUpdate(AnimationSequenceInfo animationSequenceInfo) {
        UUID uuid = animationSequenceInfo.animationID;
        synchronized (this.animationLock) {
            AvatarAnimationState avatarAnimationState = this.animations.get(uuid);
            if (avatarAnimationState == null) {
                this.animations.put(uuid, new AvatarAnimationState(animationSequenceInfo, this));
            } else {
                avatarAnimationState.updateSequenceInfo(animationSequenceInfo);
            }
        }
        updateRunningAnimations();
    }

    public void Draw(RenderContext renderContext) {
        float[] worldMatrix = getWorldMatrix(renderContext);
        if (worldMatrix != null) {
            try {
                renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
                DrawParts(renderContext);
                renderContext.glObjWorldPopMatrix();
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }

    public void DrawNameTag(RenderContext renderContext) {
        DrawableHoverText drawableHoverText = this.drawableNameTag;
        if (drawableHoverText != null) {
            LLVector3 lLVector3 = this.headPosition;
            if (lLVector3 != null) {
                drawableHoverText.DrawAtWorld(renderContext, lLVector3.x, lLVector3.y, lLVector3.z, 0.5f, renderContext.projectionMatrix, false, 0);
                return;
            }
            super.DrawNameTag(renderContext);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean IsAnimationStopped(UUID uuid) {
        boolean hasStopped;
        synchronized (this.animationLock) {
            AvatarAnimationState avatarAnimationState = this.animations.get(uuid);
            hasStopped = avatarAnimationState != null ? avatarAnimationState.hasStopped() : false;
        }
        return hasStopped;
    }

    public ObjectIntersectInfo PickObject(RenderContext renderContext, float f, float f2, float f3) {
        ObjectIntersectInfo objectIntersectInfo;
        float[] worldMatrix = getWorldMatrix(renderContext);
        AvatarSkeleton avatarSkeleton = this.skeleton;
        if (worldMatrix == null || avatarSkeleton == null) {
            return null;
        }
        int[] iArr = renderContext.viewportRect;
        float[] fArr = new float[32];
        float[] fArr2 = new float[6];
        float f4 = ((float) iArr[3]) - f2;
        renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
        renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ);
        Iterator it = avatarSkeleton.bones.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                objectIntersectInfo = null;
                break;
            }
            SLSkeletonBone sLSkeletonBone = (SLSkeletonBone) it.next();
            if (!sLSkeletonBone.boneID.isJoint) {
                renderContext.glObjWorldPushAndMultMatrixf(avatarSkeleton.jointWorldMatrix, sLSkeletonBone.boneID.ordinal() * 16);
                if (renderContext.hasGL20) {
                    Matrix.scaleM(fArr, 0, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), 1.0f, 1.0f, 1.0f);
                    RenderContext.gluUnProject(f, f4, 0.0f, fArr, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 0);
                    RenderContext.gluUnProject(f, f4, 1.0f, fArr, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 3);
                } else {
                    Matrix.scaleM(fArr, 16, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), 1.0f, 1.0f, 1.0f);
                    Matrix.multiplyMM(fArr, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), fArr, 16);
                    RenderContext.gluUnProject(f, f4, 0.0f, fArr, 0, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 0);
                    RenderContext.gluUnProject(f, f4, 1.0f, fArr, 0, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 3);
                }
                renderContext.glObjWorldPopMatrix();
                LLVector3 lLVector3 = new LLVector3(fArr2[0], fArr2[1], fArr2[2]);
                LLVector3 lLVector32 = new LLVector3(fArr2[3], fArr2[4], fArr2[5]);
                LLVector3[] lLVector3Arr = CollisionBox.getInstance().vertices;
                GLRayTrace.RayIntersectInfo rayIntersectInfo = null;
                int i = 0;
                while (i < 12 && (rayIntersectInfo = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, i * 3)) == null) {
                    i++;
                }
                if (rayIntersectInfo != null) {
                    float intersectionDepth = GLRayTrace.getIntersectionDepth(renderContext, rayIntersectInfo.intersectPoint, fArr);
                    if (intersectionDepth >= f3) {
                        objectIntersectInfo = new ObjectIntersectInfo(new IntersectInfo(rayIntersectInfo.intersectPoint), this.avatarObject, intersectionDepth);
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        renderContext.glObjWorldPopMatrix();
        return objectIntersectInfo;
    }

    public void RunAnimations() {
        AvatarSkeleton andSet = this.updatedSkeleton.getAndSet((Object) null);
        if (andSet != null) {
            this.skeleton = andSet;
        }
        AvatarSkeleton avatarSkeleton = this.skeleton;
        if (avatarSkeleton != null && animate(avatarSkeleton)) {
            avatarSkeleton.UpdateGlobalPositions(this.animationSkeletonData);
            this.jointMatrixUpdated |= true;
        }
    }

    /* access modifiers changed from: package-private */
    public void UpdateShapeParams(AvatarShapeParams avatarShapeParams) {
        this.shapeParams = avatarShapeParams;
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate);
    }

    /* access modifiers changed from: package-private */
    public void UpdateTextures(AvatarTextures avatarTextures) {
        for (Map.Entry entry : this.parts.entrySet()) {
            ((DrawableAvatarPart) entry.getValue()).setTexture(this.drawableStore.glTextureCache, avatarTextures.getTexture(((DrawableAvatarPart) entry.getValue()).getFaceIndex()));
        }
    }

    public DrawableHUD getDrawableHUD() {
        return this.drawableHUD;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar_15479  reason: not valid java name */
    public /* synthetic */ void m53lambda$com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar_15479() {
        boolean z;
        boolean z2 = false;
        AvatarShapeParams avatarShapeParams = this.shapeParams;
        if (avatarShapeParams != null) {
            Debug.Printf("Avatar: shapeParamsUpdate: %d rigged meshes", Integer.valueOf(this.riggedMeshes.size()));
            MeshJointTranslations meshJointTranslations = new MeshJointTranslations();
            Iterator<T> it = this.riggedMeshes.iterator();
            while (true) {
                z = z2;
                if (!it.hasNext()) {
                    break;
                }
                DrawableObject drawableObject = (DrawableObject) it.next();
                drawableObject.ApplyJointTranslations(meshJointTranslations);
                z2 = drawableObject.hasExtendedBones() | z;
            }
            AvatarSkeleton avatarSkeleton = new AvatarSkeleton(avatarShapeParams, meshJointTranslations, z);
            this.updatedSkeleton.set(avatarSkeleton);
            for (Map.Entry entry : this.parts.entrySet()) {
                ((DrawableAvatarPart) entry.getValue()).setPartMorphParams(avatarSkeleton.getMorphParams((MeshIndex) entry.getKey()));
            }
        }
    }

    public void onEntryRemovalRequested(DrawListEntry drawListEntry) {
        synchronized (this.deadAttachmentsLock) {
            this.deadAttachmentsList.add(drawListEntry);
        }
        updateAttachments();
    }

    public void onRiggedMeshReady(DrawableObject drawableObject) {
        if (this.riggedMeshes.add(drawableObject)) {
            updateRiggedMeshes();
        }
    }

    public void setDisplayedHUDid(int i) {
        if (this.displayedHUDid.getAndSet(i) != i) {
            updateAttachments();
        }
    }

    public void updateAttachments() {
        PrimComputeExecutor.getInstance().execute(this.updateAttachmentsRunnable);
    }

    /* access modifiers changed from: package-private */
    public void updateRunningAnimations() {
        if (this.animationsInitialized) {
            this.runningAnimations = getRunningAnimations();
            AvatarSkeleton avatarSkeleton = this.skeleton;
            if (avatarSkeleton != null) {
                avatarSkeleton.setForceAnimate();
            }
        }
    }
}
