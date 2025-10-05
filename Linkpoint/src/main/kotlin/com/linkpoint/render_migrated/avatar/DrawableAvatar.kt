package com.linkpoint.render.avatar

import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.Matrix
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.linkpoint.Debug
import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.RenderContext
import com.linkpoint.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.AnonymousClass1
import com.linkpoint.render.picking.CollisionBox
import com.linkpoint.render.picking.GLRayTrace
import com.linkpoint.render.picking.GLRayTrace.RayIntersectInfo
import com.linkpoint.render.picking.IntersectInfo
import com.linkpoint.render.picking.IntersectPickable
import com.linkpoint.render.picking.ObjectIntersectInfo
import com.linkpoint.render.spatial.DrawEntryList
import com.linkpoint.render.spatial.DrawEntryList.EntryRemovalListener
import com.linkpoint.render.spatial.DrawListEntry
import com.linkpoint.render.spatial.DrawListPrimEntry
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.slproto.avatar.MeshIndex
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.avatar.SLBaseAvatar
import com.linkpoint.slproto.avatar.SLSkeletonBone
import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.utils.IdentityMatrix
import com.linkpoint.utils.InlineListEntry
import com.linkpoint.utils.LinkedTreeNode
import java.util.Collections
import java.util.EnumMap
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.Iterator
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull

class DrawableAvatar : DrawableAvatarStub(), IntersectPickable, EntryRemovalListener {
    private val Object animationLock = Object()
    private val AnimationSkeletonData animationSkeletonData = AnimationSkeletonData()
    private val Map<UUID, AvatarAnimationState> animations = HashMap()
    private volatile Boolean animationsInitialized = false
    private val Set<DrawListEntry> deadAttachmentsList = Collections.newSetFromMap(IdentityHashMap())
    private val Object deadAttachmentsLock = Object()
    private val AtomicInteger displayedHUDid = AtomicInteger()
    private volatile DrawableAttachments drawableAttachmentList = DrawableAttachments()
    private val DrawEntryList drawableAttachments = DrawEntryList(this)
    private volatile DrawableHUD drawableHUD
    private LLVector3 headPosition
    private Boolean jointMatrixUpdated = false
    private val Float[] localAviWorldMatrix = Float[16]
    private val Map<MeshIndex, DrawableAvatarPart> parts = EnumMap(MeshIndex.class)
    private Float pelvisTranslateX = 0.0f
    private Float pelvisTranslateY = 0.0f
    private Float pelvisTranslateZ = 0.0f
    private val Set<DrawableObject> riggedMeshes = Collections.newSetFromMap(ConcurrentHashMap())
    private volatile AvatarAnimationList runningAnimations = null
    private volatile AvatarShapeParams shapeParams
    private val Runnable shapeParamsUpdate = AnonymousClass1(this)
    private volatile AvatarSkeleton skeleton
    private val Runnable updateAttachmentsRunnable = () -> { // Lambda implementation }
    private val AtomicReference<AvatarSkeleton> updatedSkeleton = AtomicReference(null)

    public DrawableAvatar(DrawableStore drawableStore, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo, UUID uuid2, Map<UUID, AnimationSequenceInfo> map) {
        Int i = 0
        super(drawableStore, uuid, sLObjectAvatarInfo)
        SLBaseAvatar instance = SLBaseAvatar.getInstance()
        MeshIndex[] meshIndexArr = MeshIndex.VALUES
        Int length = meshIndexArr.length
        while (i < length) {
            MeshIndex meshIndex = meshIndexArr[i]
            this.parts.put(meshIndex, DrawableAvatarPart(uuid2, instance.getMeshEntry(meshIndex).textureFaceIndex, instance.getMeshEntry(meshIndex).polyMesh, drawableStore.hasGL20))
            i++
        }
        synchronized (this.animationLock) {
            if (map != null) {
                for (AnimationSequenceInfo animationSequenceInfo : map.values()) {
                    this.animations.put(animationSequenceInfo.animationID, AvatarAnimationState(animationSequenceInfo, this))
                }
            }
            this.animationsInitialized = true
            updateRunningAnimations()
        }
        updateAttachments()
    }

    private Unit DrawParts(RenderContext renderContext) {
        AvatarSkeleton avatarSkeleton = this.skeleton
        if (avatarSkeleton != null) {
            Float pelvisToFoot = this.avatarObject.parentID == 0 ? (((-avatarSkeleton.getBodySize()) / 2.0f) + avatarSkeleton.getPelvisToFoot()) + avatarSkeleton.getPelvisOffset() : 0.0f
            this.pelvisTranslateX = -avatarSkeleton.rootBone.getPositionX()
            this.pelvisTranslateY = -avatarSkeleton.rootBone.getPositionY()
            this.pelvisTranslateZ = pelvisToFoot + (-avatarSkeleton.rootBone.getPositionZ())
            renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ)
            renderContext.objWorldMatrix.getMatrix(this.localAviWorldMatrix, 0)
            GLPrepare(renderContext, avatarSkeleton.jointMatrix)
            for (MeshIndex meshIndex : MeshIndex.VALUES) {
                DrawableAvatarPart drawableAvatarPart = (DrawableAvatarPart) this.parts.get(meshIndex)
                SLSkeletonBone sLSkeletonBone = meshIndex == MeshIndex.MESH_ID_EYEBALL_LEFT ? (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mEyeLeft) : meshIndex == MeshIndex.MESH_ID_EYEBALL_RIGHT ? (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mEyeRight) : null
                if (sLSkeletonBone != null) {
                    renderContext.glObjWorldPushAndMultMatrixf(sLSkeletonBone.getGlobalMatrix(), 0)
                }
                drawableAvatarPart.GLDraw(renderContext, avatarSkeleton.jointMatrix, this.jointMatrixUpdated)
                if (sLSkeletonBone != null) {
                    renderContext.glObjWorldPopMatrix()
                }
            }
            SLSkeletonBone sLSkeletonBone2 = (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mHead)
            if (sLSkeletonBone2 != null) {
                renderContext.glObjWorldPushAndMultMatrixf(sLSkeletonBone2.getGlobalMatrix(), 0)
                Float[] matrixData = renderContext.objWorldMatrix.getMatrixData()
                Int matrixDataOffset = renderContext.objWorldMatrix.getMatrixDataOffset()
                Float f = matrixData[matrixDataOffset + 12]
                Float f2 = matrixData[matrixDataOffset + 13]
                pelvisToFoot = matrixData[matrixDataOffset + 14]
                if (this.headPosition == null) {
                    this.headPosition = LLVector3()
                }
                this.headPosition.set(f, f2, pelvisToFoot)
                renderContext.glObjWorldPopMatrix()
            }
            renderContext.curPrimProgram = null
            if (this.drawableAttachmentList.Draw(renderContext, avatarSkeleton, this.jointMatrixUpdated)) {
                this.drawableAttachmentList = DrawableAttachments(this.drawableAttachmentList)
            }
            this.jointMatrixUpdated = false
        }
    }

    private Unit GLPrepare(RenderContext renderContext, Float[] fArr) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.avatarProgram.getHandle())
            GLES20.glUniform1i(renderContext.avatarProgram.sTexture, 0)
            GLES20.glUniform4f(renderContext.avatarProgram.uObjCoordScale, 1.0f, 1.0f, 1.0f, 1.0f)
            renderContext.glModelApplyMatrix(renderContext.avatarProgram.uMVPMatrix)
            renderContext.avatarProgram.SetupLighting(renderContext, renderContext.windlightPreset)
            GLES20.glUniformMatrix4fv(renderContext.avatarProgram.uJointMatrix, 133, false, fArr, 0)
            return
        }
        GLES11.glMatrixMode(5890)
        GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0)
        GLES11.glMatrixMode(5888)
    }

    private Boolean animate(AvatarSkeleton avatarSkeleton) {
        Boolean needForceAnimate = avatarSkeleton.needForceAnimate()
        AvatarAnimationList avatarAnimationList = this.runningAnimations
        if (!avatarAnimationList.needAnimate(System.currentTimeMillis()) && !needForceAnimate) {
            return false
        }
        this.animationSkeletonData.animate(avatarSkeleton, avatarAnimationList)
        return true
    }

    private AvatarAnimationList getRunningAnimations() {
        AvatarAnimationList avatarAnimationList
        synchronized (this.animationLock) {
            avatarAnimationList = AvatarAnimationList(this.animations.values())
        }
        return avatarAnimationList
    }

    private Unit processUpdateAttachments() {
        DrawListEntry[] drawListEntryArr
        DrawableObject drawableObject
        Int i2 = 0
        Multimap create = ArrayListMultimap.create()
        Set newSetFromMap = Collections.newSetFromMap(IdentityHashMap())
        Int i3 = this.displayedHUDid.get()
        LinkedTreeNode firstChild = this.avatarObject.treeNode.getFirstChild()
        DrawableHUD drawableHUD = null
        while (firstChild != null) {
            DrawableHUD drawableHUD2
            SLObjectInfo sLObjectInfo = (SLObjectInfo) firstChild.getDataObject()
            if (!sLObjectInfo.isDead) {
                i = sLObjectInfo.attachmentID
                if (i < 0 || i >= 56) {
                    drawableHUD2 = drawableHUD
                    firstChild = firstChild.getNextChild()
                    drawableHUD = drawableHUD2
                } else {
                    SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]
                    if (sLAttachmentPoint != null) {
                        if (!sLAttachmentPoint.isHUD) {
                            updateAttachmentParts(sLObjectInfo, create, i)
                            drawableHUD2 = drawableHUD
                        } else if (i3 == sLObjectInfo.localID) {
                            drawableHUD2 = DrawableHUD(sLAttachmentPoint, this.drawableAttachments, sLObjectInfo, this.drawableStore, this)
                        }
                        firstChild = firstChild.getNextChild()
                        drawableHUD = drawableHUD2
                    }
                }
            }
            drawableHUD2 = drawableHUD
            firstChild = firstChild.getNextChild()
            drawableHUD = drawableHUD2
        }
        synchronized (this.deadAttachmentsLock) {
            if (this.deadAttachmentsList.isEmpty()) {
                drawListEntryArr = null
            } else {
                DrawListEntry[] drawListEntryArr2 = (DrawListEntry[]) this.deadAttachmentsList.toArray(DrawListEntry[this.deadAttachmentsList.size()])
                this.deadAttachmentsList.clear()
                drawListEntryArr = drawListEntryArr2
            }
        }
        Int i4 = 0
        for (DrawableObject drawableObject2 : create.values()) {
            if (drawableObject2.isRiggedMesh()) {
                if (this.riggedMeshes.add(drawableObject2)) {
                    i4 = 1
                }
                newSetFromMap.add(drawableObject2)
            }
            i4 = i4
        }
        if (drawListEntryArr != null) {
            Int length = drawListEntryArr.length
            while (i2 < length) {
                InlineListEntry inlineListEntry = drawListEntryArr[i2]
                this.drawableAttachments.removeEntry(inlineListEntry)
                if (inlineListEntry instanceof DrawListPrimEntry) {
                    drawableObject2 = ((DrawListPrimEntry) inlineListEntry).getDrawableObject()
                    if (drawableObject2 != null) {
                        newSetFromMap.remove(drawableObject2)
                        if (this.riggedMeshes.remove(drawableObject2)) {
                            i = 1
                            i2++
                            i4 = i
                        }
                    }
                }
                i = i4
                i2++
                i4 = i
            }
        }
        for (DrawableObject drawableObject22 : this.riggedMeshes) {
            if (!newSetFromMap.contains(drawableObject22)) {
                this.riggedMeshes.remove(drawableObject22)
                i4 = 1
            }
        }
        this.drawableHUD = drawableHUD
        this.drawableAttachmentList = DrawableAttachments(create)
        if (i4 != 0) {
            updateRiggedMeshes()
        }
    }

    private Unit updateAttachmentParts(SLObjectInfo sLObjectInfo, Multimap<Integer, DrawableObject> multimap, Int i) {
        InlineListEntry drawListEntry = sLObjectInfo.getDrawListEntry()
        this.drawableAttachments.addEntry(drawListEntry)
        if (drawListEntry instanceof DrawListPrimEntry) {
            multimap.put(Integer.valueOf(i), ((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this))
        }
        for (LinkedTreeNode firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            SLObjectInfo sLObjectInfo2 = (SLObjectInfo) firstChild.getDataObject()
            if (sLObjectInfo2 != null) {
                updateAttachmentParts(sLObjectInfo2, multimap, i)
            }
        }
    }

    private Unit updateRiggedMeshes() {
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate)
    }

    /* renamed from: -com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar-mthref-0 */
    /* synthetic */ Unit m75-com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar-mthref-0() {
        processUpdateAttachments()
    }

    Unit AnimationRemove(UUID uuid) {
        synchronized (this.animationLock) {
            obj = this.animations.remove(uuid) != null ? 1 : null
        }
        if (obj != null) {
            updateRunningAnimations()
        }
    }

    Unit AnimationUpdate(AnimationSequenceInfo animationSequenceInfo) {
        UUID uuid = animationSequenceInfo.animationID
        synchronized (this.animationLock) {
            AvatarAnimationState avatarAnimationState = (AvatarAnimationState) this.animations.get(uuid)
            if (avatarAnimationState == null) {
                this.animations.put(uuid, AvatarAnimationState(animationSequenceInfo, this))
            } else {
                avatarAnimationState.updateSequenceInfo(animationSequenceInfo)
            }
        }
        updateRunningAnimations()
    }

    public Unit Draw(RenderContext renderContext) {
        Float[] worldMatrix = getWorldMatrix(renderContext)
        if (worldMatrix != null) {
            try {
                renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0)
                DrawParts(renderContext)
                renderContext.glObjWorldPopMatrix()
            } catch (Throwable e) {
                Debug.Warning(e)
            }
        }
    }

    public Unit DrawNameTag(RenderContext renderContext) {
        DrawableHoverText drawableHoverText = this.drawableNameTag
        if (drawableHoverText != null) {
            LLVector3 lLVector3 = this.headPosition
            if (lLVector3 != null) {
                drawableHoverText.DrawAtWorld(renderContext, lLVector3.x, lLVector3.y, lLVector3.z, 0.5f, renderContext.projectionMatrix, false, 0)
                return
            }
            super.DrawNameTag(renderContext)
        }
    }

    Boolean IsAnimationStopped(UUID uuid) {
        Boolean hasStopped
        synchronized (this.animationLock) {
            AvatarAnimationState avatarAnimationState = (AvatarAnimationState) this.animations.get(uuid)
            hasStopped = avatarAnimationState != null ? avatarAnimationState.hasStopped() : false
        }
        return hasStopped
    }

    public ObjectIntersectInfo PickObject(RenderContext renderContext, Float f, Float f2, Float f3) {
        Float[] worldMatrix = getWorldMatrix(renderContext)
        AvatarSkeleton avatarSkeleton = this.skeleton
        if (worldMatrix == null || avatarSkeleton == null) {
            return null
        }
        ObjectIntersectInfo objectIntersectInfo
        Int[] iArr = renderContext.viewportRect
        Float[] fArr = Float[32]
        Float[] fArr2 = Float[6]
        Float f4 = ((Float) iArr[3]) - f2
        renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0)
        renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ)
        for (SLSkeletonBone sLSkeletonBone : avatarSkeleton.bones.values()) {
            if (!sLSkeletonBone.boneID.isJoint) {
                renderContext.glObjWorldPushAndMultMatrixf(avatarSkeleton.jointWorldMatrix, sLSkeletonBone.boneID.ordinal() * 16)
                if (renderContext.hasGL20) {
                    Matrix.scaleM(fArr, 0, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), 1.0f, 1.0f, 1.0f)
                    RenderContext.gluUnProject(f, f4, 0.0f, fArr, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 0)
                    RenderContext.gluUnProject(f, f4, 1.0f, fArr, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 3)
                } else {
                    Matrix.scaleM(fArr, 16, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), 1.0f, 1.0f, 1.0f)
                    Matrix.multiplyMM(fArr, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), fArr, 16)
                    RenderContext.gluUnProject(f, f4, 0.0f, fArr, 0, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 0)
                    RenderContext.gluUnProject(f, f4, 1.0f, fArr, 0, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), iArr, 0, fArr2, 3)
                }
                renderContext.glObjWorldPopMatrix()
                LLVector3 lLVector3 = LLVector3(fArr2[0], fArr2[1], fArr2[2])
                LLVector3 lLVector32 = LLVector3(fArr2[3], fArr2[4], fArr2[5])
                LLVector3[] lLVector3Arr = CollisionBox.getInstance().vertices
                RayIntersectInfo rayIntersectInfo = null
                for (Int i = 0; i < 12; i++) {
                    rayIntersectInfo = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, i * 3)
                    if (rayIntersectInfo != null) {
                        break
                    }
                }
                if (rayIntersectInfo != null) {
                    Float intersectionDepth = GLRayTrace.getIntersectionDepth(renderContext, rayIntersectInfo.intersectPoint, fArr)
                    if (intersectionDepth >= f3) {
                        objectIntersectInfo = ObjectIntersectInfo(IntersectInfo(rayIntersectInfo.intersectPoint), this.avatarObject, intersectionDepth)
                        break
                    }
                } else {
                    continue
                }
            }
        }
        objectIntersectInfo = null
        renderContext.glObjWorldPopMatrix()
        return objectIntersectInfo
    }

    public Unit RunAnimations() {
        AvatarSkeleton avatarSkeleton = (AvatarSkeleton) this.updatedSkeleton.getAndSet(null)
        if (avatarSkeleton != null) {
            this.skeleton = avatarSkeleton
        }
        avatarSkeleton = this.skeleton
        if (avatarSkeleton != null && animate(avatarSkeleton)) {
            avatarSkeleton.UpdateGlobalPositions(this.animationSkeletonData)
            this.jointMatrixUpdated |= true
        }
    }

    Unit UpdateShapeParams(AvatarShapeParams avatarShapeParams) {
        this.shapeParams = avatarShapeParams
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate)
    }

    Unit UpdateTextures(AvatarTextures avatarTextures) {
        for (Entry entry : this.parts.entrySet()) {
            ((DrawableAvatarPart) entry.getValue()).setTexture(this.drawableStore.glTextureCache, avatarTextures.getTexture(((DrawableAvatarPart) entry.getValue()).getFaceIndex()))
        }
    }

    public DrawableHUD getDrawableHUD() {
        return this.drawableHUD
    }

    /* renamed from: updateAvatarShape */
    /* synthetic */ Unit updateAvatarShape() {
        Boolean z = false
        AvatarShapeParams avatarShapeParams = this.shapeParams
        if (avatarShapeParams != null) {
            Debug.Printf("Avatar: shapeParamsUpdate: %d rigged meshes", Integer.valueOf(this.riggedMeshes.size()))
            MeshJointTranslations meshJointTranslations = MeshJointTranslations()
            Iterator it = this.riggedMeshes.iterator()
            while (true) {
                z2 = z
                if (!it.hasNext()) {
                    break
                }
                DrawableObject drawableObject = (DrawableObject) it.next()
                drawableObject.ApplyJointTranslations(meshJointTranslations)
                z = drawableObject.hasExtendedBones() | z2
            }
            AvatarSkeleton avatarSkeleton = AvatarSkeleton(avatarShapeParams, meshJointTranslations, z2)
            this.updatedSkeleton.set(avatarSkeleton)
            for (Entry entry : this.parts.entrySet()) {
                ((DrawableAvatarPart) entry.getValue()).setPartMorphParams(avatarSkeleton.getMorphParams((MeshIndex) entry.getKey()))
            }
        }
    }

    public Unit onEntryRemovalRequested(DrawListEntry drawListEntry) {
        synchronized (this.deadAttachmentsLock) {
            this.deadAttachmentsList.add(drawListEntry)
        }
        updateAttachments()
    }

    public Unit onRiggedMeshReady(DrawableObject drawableObject) {
        if (this.riggedMeshes.add(drawableObject)) {
            updateRiggedMeshes()
        }
    }

    public Unit setDisplayedHUDid(Int i) {
        if (this.displayedHUDid.getAndSet(i) != i) {
            updateAttachments()
        }
    }

    public Unit updateAttachments() {
        PrimComputeExecutor.getInstance().execute(this.updateAttachmentsRunnable)
    }

    Unit updateRunningAnimations() {
        if (this.animationsInitialized) {
            this.runningAnimations = getRunningAnimations()
            AvatarSkeleton avatarSkeleton = this.skeleton
            if (avatarSkeleton != null) {
                avatarSkeleton.setForceAnimate()
            }
        }
    }
}
