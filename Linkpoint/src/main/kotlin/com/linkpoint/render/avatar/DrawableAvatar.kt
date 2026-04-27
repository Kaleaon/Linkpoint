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
    private val FloatArray localAviWorldMatrix = Float[16]
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
        val i: Int = 0
        super(drawableStore, uuid, sLObjectAvatarInfo)
        val instance: SLBaseAvatar = SLBaseAvatar.getInstance()
        val meshIndexArr: Array<MeshIndex> = MeshIndex.VALUES
        val length: Int = meshIndexArr.length
        while (i < length) {
            val meshIndex: MeshIndex = meshIndexArr[i]
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

    private fun DrawParts(renderContext: RenderContext) {
        val avatarSkeleton: AvatarSkeleton = this.skeleton
        if (avatarSkeleton != null) {
            val pelvisToFoot: Float = this.avatarObject.parentID == 0 ? (((-avatarSkeleton.getBodySize()) / 2.0f) + avatarSkeleton.getPelvisToFoot()) + avatarSkeleton.getPelvisOffset() : 0.0f
            this.pelvisTranslateX = -avatarSkeleton.rootBone.getPositionX()
            this.pelvisTranslateY = -avatarSkeleton.rootBone.getPositionY()
            this.pelvisTranslateZ = pelvisToFoot + (-avatarSkeleton.rootBone.getPositionZ())
            renderContext.glObjWorldTranslatef(this.pelvisTranslateX, this.pelvisTranslateY, this.pelvisTranslateZ)
            renderContext.objWorldMatrix.getMatrix(this.localAviWorldMatrix, 0)
            GLPrepare(renderContext, avatarSkeleton.jointMatrix)
            for (MeshIndex meshIndex : MeshIndex.VALUES) {
                val drawableAvatarPart: DrawableAvatarPart = (DrawableAvatarPart) this.parts.get(meshIndex)
                val sLSkeletonBone: SLSkeletonBone = meshIndex == MeshIndex.MESH_ID_EYEBALL_LEFT ? (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mEyeLeft) : meshIndex == MeshIndex.MESH_ID_EYEBALL_RIGHT ? (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mEyeRight) : null
                if (sLSkeletonBone != null) {
                    renderContext.glObjWorldPushAndMultMatrixf(sLSkeletonBone.getGlobalMatrix(), 0)
                }
                drawableAvatarPart.GLDraw(renderContext, avatarSkeleton.jointMatrix, this.jointMatrixUpdated)
                if (sLSkeletonBone != null) {
                    renderContext.glObjWorldPopMatrix()
                }
            }
            val sLSkeletonBone2: SLSkeletonBone = (SLSkeletonBone) avatarSkeleton.bones.get(SLSkeletonBoneID.mHead)
            if (sLSkeletonBone2 != null) {
                renderContext.glObjWorldPushAndMultMatrixf(sLSkeletonBone2.getGlobalMatrix(), 0)
                val matrixData: FloatArray = renderContext.objWorldMatrix.getMatrixData()
                val matrixDataOffset: Int = renderContext.objWorldMatrix.getMatrixDataOffset()
                val f: Float = matrixData[matrixDataOffset + 12]
                val f2: Float = matrixData[matrixDataOffset + 13]
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

    private fun GLPrepare(renderContext: RenderContext, fArr: FloatArray) {
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

     private fun animate(avatarSkeleton: AvatarSkeleton): Boolean {
        val needForceAnimate: Boolean = avatarSkeleton.needForceAnimate()
        val avatarAnimationList: AvatarAnimationList = this.runningAnimations
        if (!avatarAnimationList.needAnimate(System.currentTimeMillis()) && !needForceAnimate) {
            return false
        }
        this.animationSkeletonData.animate(avatarSkeleton, avatarAnimationList)
        return true
    }

     private fun getRunningAnimations(): AvatarAnimationList {
        AvatarAnimationList avatarAnimationList
        synchronized (this.animationLock) {
            avatarAnimationList = AvatarAnimationList(this.animations.values())
        }
        return avatarAnimationList
    }

     private fun processUpdateAttachments() {
        Array<DrawListEntry> drawListEntryArr
        DrawableObject drawableObject
        val i2: Int = 0
        val create: Multimap = ArrayListMultimap.create()
        val newSetFromMap: Set = Collections.newSetFromMap(IdentityHashMap())
        val i3: Int = this.displayedHUDid.get()
        val firstChild: LinkedTreeNode = this.avatarObject.treeNode.getFirstChild()
        val drawableHUD: DrawableHUD = null
        while (firstChild != null) {
            DrawableHUD drawableHUD2
            val sLObjectInfo: SLObjectInfo = (SLObjectInfo) firstChild.getDataObject()
            if (!sLObjectInfo.isDead) {
                i = sLObjectInfo.attachmentID
                if (i < 0 || i >= 56) {
                    drawableHUD2 = drawableHUD
                    firstChild = firstChild.getNextChild()
                    drawableHUD = drawableHUD2
                } else {
                    val sLAttachmentPoint: SLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]
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
                val drawListEntryArr2: Array<DrawListEntry> = (Array<DrawListEntry>) this.deadAttachmentsList.toArray(DrawListEntry[this.deadAttachmentsList.size()])
                this.deadAttachmentsList.clear()
                drawListEntryArr = drawListEntryArr2
            }
        }
        val i4: Int = 0
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
            val length: Int = drawListEntryArr.length
            while (i2 < length) {
                val inlineListEntry: InlineListEntry = drawListEntryArr[i2]
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

     private fun updateAttachmentParts(sLObjectInfo: SLObjectInfo, multimap: Multimap<Integer, DrawableObject>, i: Int) {
        val drawListEntry: InlineListEntry = sLObjectInfo.getDrawListEntry()
        this.drawableAttachments.addEntry(drawListEntry)
        if (drawListEntry instanceof DrawListPrimEntry) {
            multimap.put(Integer.valueOf(i), ((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this))
        }
        for (LinkedTreeNode firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            val sLObjectInfo2: SLObjectInfo = (SLObjectInfo) firstChild.getDataObject()
            if (sLObjectInfo2 != null) {
                updateAttachmentParts(sLObjectInfo2, multimap, i)
            }
        }
    }

     private fun updateRiggedMeshes() {
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate)
    }

    /* renamed from: -com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar-mthref-0 */
    /* synthetic */ Unit m75-com_lumiyaviewer_lumiya_render_avatar_DrawableAvatar-mthref-0() {
        processUpdateAttachments()
    }

    fun AnimationRemove(uuid: UUID) {
        synchronized (this.animationLock) {
            obj = this.animations.remove(uuid) != null ? 1 : null
        }
        if (obj != null) {
            updateRunningAnimations()
        }
    }

    fun AnimationUpdate(animationSequenceInfo: AnimationSequenceInfo) {
        val uuid: UUID = animationSequenceInfo.animationID
        synchronized (this.animationLock) {
            val avatarAnimationState: AvatarAnimationState = (AvatarAnimationState) this.animations.get(uuid)
            if (avatarAnimationState == null) {
                this.animations.put(uuid, AvatarAnimationState(animationSequenceInfo, this))
            } else {
                avatarAnimationState.updateSequenceInfo(animationSequenceInfo)
            }
        }
        updateRunningAnimations()
    }

    fun Draw(renderContext: RenderContext) {
        val worldMatrix: FloatArray = getWorldMatrix(renderContext)
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

    fun DrawNameTag(renderContext: RenderContext) {
        val drawableHoverText: DrawableHoverText = this.drawableNameTag
        if (drawableHoverText != null) {
            val lLVector3: LLVector3 = this.headPosition
            if (lLVector3 != null) {
                drawableHoverText.DrawAtWorld(renderContext, lLVector3.x, lLVector3.y, lLVector3.z, 0.5f, renderContext.projectionMatrix, false, 0)
                return
            }
            super.DrawNameTag(renderContext)
        }
    }

    fun IsAnimationStopped(uuid: UUID): Boolean {
        Boolean hasStopped
        synchronized (this.animationLock) {
            val avatarAnimationState: AvatarAnimationState = (AvatarAnimationState) this.animations.get(uuid)
            hasStopped = avatarAnimationState != null ? avatarAnimationState.hasStopped() : false
        }
        return hasStopped
    }

    public fun PickObject(renderContext: RenderContext, f: Float, f2: Float, f3: Float): ObjectIntersectInfo {
        val worldMatrix: FloatArray = getWorldMatrix(renderContext)
        val avatarSkeleton: AvatarSkeleton = this.skeleton
        if (worldMatrix == null || avatarSkeleton == null) {
            return null
        }
        ObjectIntersectInfo objectIntersectInfo
        val iArr: IntArray = renderContext.viewportRect
        val fArr: FloatArray = Float[32]
        val fArr2: FloatArray = Float[6]
        val f4: Float = ((Float) iArr[3]) - f2
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
                val lLVector3: LLVector3 = LLVector3(fArr2[0], fArr2[1], fArr2[2])
                val lLVector32: LLVector3 = LLVector3(fArr2[3], fArr2[4], fArr2[5])
                val lLVector3Arr: Array<LLVector3> = CollisionBox.getInstance().vertices
                val rayIntersectInfo: RayIntersectInfo = null
                for (Int i = 0; i < 12; i++) {
                    rayIntersectInfo = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, i * 3)
                    if (rayIntersectInfo != null) {
                        break
                    }
                }
                if (rayIntersectInfo != null) {
                    val intersectionDepth: Float = GLRayTrace.getIntersectionDepth(renderContext, rayIntersectInfo.intersectPoint, fArr)
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

    fun RunAnimations() {
        val avatarSkeleton: AvatarSkeleton = (AvatarSkeleton) this.updatedSkeleton.getAndSet(null)
        if (avatarSkeleton != null) {
            this.skeleton = avatarSkeleton
        }
        avatarSkeleton = this.skeleton
        if (avatarSkeleton != null && animate(avatarSkeleton)) {
            avatarSkeleton.UpdateGlobalPositions(this.animationSkeletonData)
            this.jointMatrixUpdated |= true
        }
    }

    fun UpdateShapeParams(avatarShapeParams: AvatarShapeParams) {
        this.shapeParams = avatarShapeParams
        PrimComputeExecutor.getInstance().execute(this.shapeParamsUpdate)
    }

    fun UpdateTextures(avatarTextures: AvatarTextures) {
        for (Entry entry : this.parts.entrySet()) {
            ((DrawableAvatarPart) entry.getValue()).setTexture(this.drawableStore.glTextureCache, avatarTextures.getTexture(((DrawableAvatarPart) entry.getValue()).getFaceIndex()))
        }
    }

     public fun getDrawableHUD(): DrawableHUD {
        return this.drawableHUD
    }

    /* renamed from: updateAvatarShape */
    /* synthetic */ Unit updateAvatarShape() {
        val z: Boolean = false
        val avatarShapeParams: AvatarShapeParams = this.shapeParams
        if (avatarShapeParams != null) {
            Debug.Printf("Avatar: shapeParamsUpdate: %d rigged meshes", Integer.valueOf(this.riggedMeshes.size()))
            val meshJointTranslations: MeshJointTranslations = MeshJointTranslations()
            val it: Iterator = this.riggedMeshes.iterator()
            while (true) {
                z2 = z
                if (!it.hasNext()) {
                    break
                }
                val drawableObject: DrawableObject = (DrawableObject) it.next()
                drawableObject.ApplyJointTranslations(meshJointTranslations)
                z = drawableObject.hasExtendedBones() | z2
            }
            val avatarSkeleton: AvatarSkeleton = AvatarSkeleton(avatarShapeParams, meshJointTranslations, z2)
            this.updatedSkeleton.set(avatarSkeleton)
            for (Entry entry : this.parts.entrySet()) {
                ((DrawableAvatarPart) entry.getValue()).setPartMorphParams(avatarSkeleton.getMorphParams((MeshIndex) entry.getKey()))
            }
        }
    }

    fun onEntryRemovalRequested(drawListEntry: DrawListEntry) {
        synchronized (this.deadAttachmentsLock) {
            this.deadAttachmentsList.add(drawListEntry)
        }
        updateAttachments()
    }

    fun onRiggedMeshReady(drawableObject: DrawableObject) {
        if (this.riggedMeshes.add(drawableObject)) {
            updateRiggedMeshes()
        }
    }

    fun setDisplayedHUDid(i: Int) {
        if (this.displayedHUDid.getAndSet(i) != i) {
            updateAttachments()
        }
    }

    fun updateAttachments() {
        PrimComputeExecutor.getInstance().execute(this.updateAttachmentsRunnable)
    }

     fun updateRunningAnimations() {
        if (this.animationsInitialized) {
            this.runningAnimations = getRunningAnimations()
            val avatarSkeleton: AvatarSkeleton = this.skeleton
            if (avatarSkeleton != null) {
                avatarSkeleton.setForceAnimate()
            }
        }
    }
}
