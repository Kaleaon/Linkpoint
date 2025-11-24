package com.lumiyaviewer.lumiya.render.avatar

import android.annotation.SuppressLint
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class DrawableAttachments {
    private var glAnimationDataBuffer: GLLoadableBuffer? = null
    private val nonRigged: ImmutableMultimap<Int, DrawableObject>
    private val rigged: ImmutableList<DrawableObject>

    constructor() {
        this.glAnimationDataBuffer = null
        this.nonRigged = ImmutableMultimap.of()
        this.rigged = ImmutableList.of()
    }

    constructor(multimap: Multimap<Int, DrawableObject>?) {
        this.glAnimationDataBuffer = null
        val builder = ImmutableMultimap.builder<Int, DrawableObject>()
        val builder2 = ImmutableList.builder<DrawableObject>()
        if (multimap != null) {
            for (key in multimap.keySet()) {
                for (obj in multimap.get(key)) {
                    if (obj.isRiggedMesh()) {
                        builder2.add(obj)
                    } else {
                        builder.put(key, obj)
                    }
                }
            }
        }
        this.nonRigged = builder.build()
        this.rigged = builder2.build()
        Debug.Log("Created drawableAttachments: ${this.rigged.size} rigged, ${this.nonRigged.size()} non-rigged")
    }

    constructor(drawableAttachments: DrawableAttachments) {
        this.glAnimationDataBuffer = null
        val builder = ImmutableMultimap.builder<Int, DrawableObject>()
        val builder2 = ImmutableList.builder<DrawableObject>()
        builder2.addAll(drawableAttachments.rigged)
        
        for (key in drawableAttachments.nonRigged.keySet()) {
            for (obj in drawableAttachments.nonRigged.get(key)) {
                if (obj.isRiggedMesh()) {
                    builder2.add(obj)
                } else {
                    builder.put(key, obj)
                }
            }
        }
        this.nonRigged = builder.build()
        this.rigged = builder2.build()
        this.glAnimationDataBuffer = drawableAttachments.glAnimationDataBuffer
        Debug.Log("Updated drawableAttachments: ${this.rigged.size} rigged, ${this.nonRigged.size()} non-rigged")
    }

    @SuppressLint("NewApi")
    fun Draw(renderContext: RenderContext, avatarSkeleton: AvatarSkeleton, z: Boolean): Boolean {
        var z3 = false
        
        if (!this.rigged.isEmpty()) {
            if (renderContext.hasGL30) {
                renderContext.setupRiggedMeshProgram(true)
                var z2 = false
                if (this.glAnimationDataBuffer == null) {
                    this.glAnimationDataBuffer = GLLoadableBuffer(DirectByteBuffer(renderContext.currentRiggedMeshProgram.uAnimationDataBlockSize))
                    z2 = true
                }
                
                if (z || z2) {
                    this.glAnimationDataBuffer?.getRawBuffer()?.loadFromFloatArray(0, avatarSkeleton.jointWorldMatrix, 0, (SLSkeletonBoneID.values().size + 47) * 16)
                }
                
                if (z) {
                    z2 = true
                }
                this.glAnimationDataBuffer?.BindUniformDynamic(renderContext, 1, z2)
                
                var i = 0
                for (obj in this.rigged) {
                    i = obj.DrawRigged30(renderContext, 1) or i
                }
                
                if ((i and 2) != 0) {
                    renderContext.setupRiggedMeshProgram(false)
                    for (obj in this.rigged) {
                        obj.DrawRigged30(renderContext, 2)
                    }
                }
                renderContext.clearRiggedMeshProgram()
            } else {
                for (obj in this.rigged) {
                    obj.DrawRigged(renderContext, avatarSkeleton, 3)
                }
            }
        }
        
        for (key in this.nonRigged.keySet()) {
            val attachmentMatrix = avatarSkeleton.getAttachmentMatrix(key)
            if (attachmentMatrix != null) {
                renderContext.glObjWorldPushAndMultMatrixf(attachmentMatrix, 0)
                for (obj in this.nonRigged.get(key)) {
                    if (obj.isRiggedMesh()) {
                        z3 = true
                    } else {
                        obj.Draw(renderContext, 3)
                    }
                }
                renderContext.glObjWorldPopMatrix()
            }
        }
        return z3
    }
}
