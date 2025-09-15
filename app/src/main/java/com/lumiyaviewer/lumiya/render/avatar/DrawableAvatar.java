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
import com.lumiyaviewer.lumiya.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.AnonymousClass1;
import com.lumiyaviewer.lumiya.render.picking.CollisionBox;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList.EntryRemovalListener;
import com.lumiyaviewer.lumiya.render.spatial.DrawListEntry;
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
import com.lumiyaviewer.lumiya.utils.InlineListEntry;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class DrawableAvatar extends DrawableAvatarStub implements IntersectPickable, EntryRemovalListener {
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
    private final Runnable shapeParamsUpdate = new AnonymousClass1(this);
    private volatile AvatarSkeleton skeleton;
    private final Runnable updateAttachmentsRunnable = () -> { /* TODO: fix lambda */ };
    private final AtomicReference<AvatarSkeleton> updatedSkeleton = new AtomicReference(null);

    public DrawableAvatar(DrawableStore drawableStore, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo, UUID uuid2, Map<UUID, AnimationSequenceInfo> map) {
        int i = 0;
        super(drawableStore, uuid, sLObjectAvatarInfo);
        SLBaseAvatar instance = SLBaseAvatar.getInstance();
        MeshIndex[] meshIndexArr = MeshIndex.VALUES;
        int length = meshIndexArr.length;
        while (i < length) {
            MeshIndex meshIndex = meshIndexArr[i];
            this.parts.put(meshIndex, new DrawableAvatarPart(uuid2, instance.getMeshEntry(meshIndex).textureFaceIndex, instance.getMeshEntry(meshIndex).polyMesh, drawableStore.hasGL20));
            i++;
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
            float pelvisToFoot = this.avatarObject.parentID == 0 ? (((-avatarSkeleton.getBodySize()) / 2.0f) + avatarSkeleton.getPelvisToFoot()) + avatarSkeleton.getPelvisOffset() : 0.0f;
            this.pelvisTranslateX = -avatarSkeleton.rootBone.getPositionX();
            this.pelvisTranslateY = -avatarSkeleton.rootBone.getPositionY();
            this.pelvisTranslateZ = pelvisToFoot + (-avatarSkeleton.rootBone.getPositionZ());
            renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ);
            renderContext.objWorldMatrix.getMatrix(this.localAviWorldMatrix, 0);
            GLPrepare(renderContext, avatarSkeleton.jointMatrix);
            for (MeshIndex meshIndex : MeshIndex.VALUES) {
                DrawableAvatarPart drawableAvatarPart = (DrawableAvatarPart) this.parts.get(meshIndex);
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
                pelvisToFoot = matrixData[matrixDataOffset + 14];
                if (this.headPosition == null) {
                    this.headPosition = new LLVector3();
                }
                this.headPosition.set(f, f2, pelvisToFoot);
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

    private void processUpdateAttachments() {
        DrawListEntry[] drawListEntryArr;
        DrawableObject drawableObject;
        int i2 = 0;
        Multimap create = ArrayListMultimap.create();
        Set newSetFromMap = Collections.newSetFromMap(new IdentityHashMap());
        int i3 = this.displayedHUDid.get();
        LinkedTreeNode firstChild = this.avatarObject.treeNode.getFirstChild();
        DrawableHUD drawableHUD = null;
        while (firstChild != null) {
            DrawableHUD drawableHUD2;
            SLObjectInfo sLObjectInfo = (SLObjectInfo) firstChild.getDataObject();
            if (!sLObjectInfo.isDead) {
                i = sLObjectInfo.attachmentID;
                if (i < 0 || i >= 56) {
                    drawableHUD2 = drawableHUD;
                    firstChild = firstChild.getNextChild();
                    drawableHUD = drawableHUD2;
                } else {
                    SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i];
                    if (sLAttachmentPoint != null) {
                        if (!sLAttachmentPoint.isHUD) {
                            updateAttachmentParts(sLObjectInfo, create, i);
                            drawableHUD2 = drawableHUD;
                        } else if (i3 == sLObjectInfo.localID) {
                            drawableHUD2 = new DrawableHUD(sLAttachmentPoint, this.drawableAttachments, sLObjectInfo, this.drawableStore, this);
                        }
                        firstChild = firstChild.getNextChild();
                        drawableHUD = drawableHUD2;
                    }
                }
            }
            drawableHUD2 = drawableHUD;
            firstChild = firstChild.getNextChild();
            drawableHUD = drawableHUD2;
        }
        synchronized (this.deadAttachmentsLock) {
            if (this.deadAttachmentsList.isEmpty()) {
                drawListEntryArr = null;
            } else {
                DrawListEntry[] drawListEntryArr2 = (DrawListEntry[]) this.deadAttachmentsList.toArray(new DrawListEntry[this.deadAttachmentsList.size()]);
                this.deadAttachmentsList.clear();
                drawListEntryArr = drawListEntryArr2;
            }
        }
        int i4 = 0;
        for (DrawableObject drawableObject2 : create.values()) {
            if (drawableObject2.isRiggedMesh()) {
                if (this.riggedMeshes.add(drawableObject2)) {
                    i4 = 1;
                }
                newSetFromMap.add(drawableObject2);
            }
            i4 = i4;
        }
        if (drawListEntryArr != null) {
            int length = drawListEntryArr.length;
            while (i2 < length) {
                InlineListEntry inlineListEntry = drawListEntryArr[i2];
                this.drawableAttachments.removeEntry(inlineListEntry);
                if (inlineListEntry instanceof DrawListPrimEntry) {
                    drawableObject2 = ((DrawListPrimEntry) inlineListEntry).getDrawableObject();
                    if (drawableObject2 != null) {
                        newSetFromMap.remove(drawableObject2);
                        if (this.riggedMeshes.remove(drawableObject2)) {
                            i = 1;
                            i2++;
                            i4 = i;
                        }
                    }
                }
                i = i4;
                i2++;
                i4 = i;
            }
        }
        for (DrawableObject drawableObject22 : this.riggedMeshes) {
            if (!newSetFromMap.contains(drawableObject22)) {
                this.riggedMeshes.remove(drawableObject22);
                i4 = 1;
            }
        }
        this.drawableHUD = drawableHUD;
        this.drawableAttachmentList = new DrawableAttachments(create);
        if (i4 != 0) {
            updateRiggedMeshes();
        }
    }

    private void updateAttachmentParts(SLObjectInfo sLObjectInfo, Multimap<Integer, DrawableObject> multimap, int i) {
        InlineListEntry drawListEntry = sLObjectInfo.getDrawListEntry();
        this.drawableAttachments.addEntry(drawListEntry);
        if (drawListEntry instanceof DrawListPrimEntry) {
            multimap.put(Integer.valueOf(i), ((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this));
        }
        for (LinkedTreeNode firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            SLObjectInfo sLObjectInfo2 = (SLObjectInfo) firstChild.getDataObject();
            if (sLObjectInfo2 != null) {
                updateAttachmentParts(sLObjectInfo2, multimap, i);
            }
        }
    }

    private void updateRiggedMeshes() {
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate);
    }

    /* renamed from: -com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar-mthref-0 */
    /* synthetic */ void m75-com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar-mthref-0() {
        processUpdateAttachments();
    }

    void AnimationRemove(UUID uuid) {
        synchronized (this.animationLock) {
            obj = this.animations.remove(uuid) != null ? 1 : null;
        }
        if (obj != null) {
            updateRunningAnimations();
        }
    }

    void AnimationUpdate(AnimationSequenceInfo animationSequenceInfo) {
        UUID uuid = animationSequenceInfo.animationID;
        synchronized (this.animationLock) {
            AvatarAnimationState avatarAnimationState = (AvatarAnimationState) this.animations.get(uuid);
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
            } catch (Throwable e) {
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

    boolean IsAnimationStopped(UUID uuid) {
        boolean hasStopped;
        synchronized (this.animationLock) {
            AvatarAnimationState avatarAnimationState = (AvatarAnimationState) this.animations.get(uuid);
            hasStopped = avatarAnimationState != null ? avatarAnimationState.hasStopped() : false;
        }
        return hasStopped;
    }

    public ObjectIntersectInfo PickObject(RenderContext renderContext, float f, float f2, float f3) {
        float[] worldMatrix = getWorldMatrix(renderContext);
        AvatarSkeleton avatarSkeleton = this.skeleton;
        if (worldMatrix == null || avatarSkeleton == null) {
            return null;
        }
        ObjectIntersectInfo objectIntersectInfo;
        int[] iArr = renderContext.viewportRect;
        float[] fArr = new float[32];
        float[] fArr2 = new float[6];
        float f4 = ((float) iArr[3]) - f2;
        renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
        renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ);
        for (SLSkeletonBone sLSkeletonBone : avatarSkeleton.bones.values()) {
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
                RayIntersectInfo rayIntersectInfo = null;
                for (int i = 0; i < 12; i++) {
                    rayIntersectInfo = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, i * 3);
                    if (rayIntersectInfo != null) {
                        break;
                    }
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
        objectIntersectInfo = null;
        renderContext.glObjWorldPopMatrix();
        return objectIntersectInfo;
    }

    public void RunAnimations() {
        AvatarSkeleton avatarSkeleton = (AvatarSkeleton) this.updatedSkeleton.getAndSet(null);
        if (avatarSkeleton != null) {
            this.skeleton = avatarSkeleton;
        }
        avatarSkeleton = this.skeleton;
        if (avatarSkeleton != null && animate(avatarSkeleton)) {
            avatarSkeleton.UpdateGlobalPositions(this.animationSkeletonData);
            this.jointMatrixUpdated |= true;
        }
    }

    void UpdateShapeParams(AvatarShapeParams avatarShapeParams) {
        this.shapeParams = avatarShapeParams;
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate);
    }

    void UpdateTextures(AvatarTextures avatarTextures) {
        for (Entry entry : this.parts.entrySet()) {
            ((DrawableAvatarPart) entry.getValue()).setTexture(this.drawableStore.glTextureCache, avatarTextures.getTexture(((DrawableAvatarPart) entry.getValue()).getFaceIndex()));
        }
    }

    public DrawableHUD getDrawableHUD() {
        return this.drawableHUD;
    }

    /* renamed from: updateAvatarShape */
    /* synthetic */ void updateAvatarShape() {
        boolean z = false;
        AvatarShapeParams avatarShapeParams = this.shapeParams;
        if (avatarShapeParams != null) {
            Debug.Printf("Avatar: shapeParamsUpdate: %d rigged meshes", Integer.valueOf(this.riggedMeshes.size()));
            MeshJointTranslations meshJointTranslations = new MeshJointTranslations();
            Iterator it = this.riggedMeshes.iterator();
            while (true) {
                z2 = z;
                if (!it.hasNext()) {
                    break;
                }
                DrawableObject drawableObject = (DrawableObject) it.next();
                drawableObject.ApplyJointTranslations(meshJointTranslations);
                z = drawableObject.hasExtendedBones() | z2;
            }
            AvatarSkeleton avatarSkeleton = new AvatarSkeleton(avatarShapeParams, meshJointTranslations, z2);
            this.updatedSkeleton.set(avatarSkeleton);
            for (Entry entry : this.parts.entrySet()) {
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

    void updateRunningAnimations() {
        if (this.animationsInitialized) {
            this.runningAnimations = getRunningAnimations();
            AvatarSkeleton avatarSkeleton = this.skeleton;
            if (avatarSkeleton != null) {
                avatarSkeleton.setForceAnimate();
            }
        }
    }
}
