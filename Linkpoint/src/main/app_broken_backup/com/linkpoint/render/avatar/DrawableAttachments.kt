package com.linkpoint.render.avatar
import java.util.*

import android.annotation.SuppressLint
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.ImmutableMultimap.Builder
import com.google.common.collect.Multimap
import com.linkpoint.Debug
import com.linkpoint.render.DrawableObject
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import com.linkpoint.rawbuffers.DirectByteBuffer
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.Immutable

@Immutable
class DrawableAttachments {
    private var glAnimationDataBuffer: GLLoadableBuffer? = null
    @NonNull
    private ImmutableMultimap<Int, DrawableObject> nonRigged
    @NonNull
    private ImmutableList<DrawableObject> rigged

    DrawableAttachments() {
        this.glAnimationDataBuffer = null
        this.nonRigged = ImmutableMultimap.of()
        this.rigged = ImmutableList.of()
    }

    DrawableAttachments(@Nullable Multimap<Int, DrawableObject> multimap) {
        this.glAnimationDataBuffer = null
        Builder builder = ImmutableMultimap.builder()
        ImmutableList.Builder builder2 = ImmutableList.builder()
        if (multimap != null) {
            for (Int num : multimap.keySet()) {
                for (Any obj : multimap.get(num)) {
                    if (obj.isRiggedMesh()) {
                        builder2.add(obj)
                    } else {
                        builder.put(num, obj)
                    }
                }
            }
        }
        this.nonRigged = builder.build()
        this.rigged = builder2.build()
        Debug.Printf("Created drawableAttachments: %d rigged, %d non-rigged", Int.valueOf(this.rigged.size()), Int.valueOf(this.nonRigged.size()))
    }

    DrawableAttachments(@NonNull DrawableAttachments drawableAttachments) {
        this.glAnimationDataBuffer = null
        Builder builder = ImmutableMultimap.builder()
        ImmutableList.Builder builder2 = ImmutableList.builder()
        builder2.addAll(drawableAttachments.rigged)
        for (Any obj : drawableAttachments.nonRigged.keySet()) {
            for (Any obj2 : drawableAttachments.nonRigged.get(obj)) {
                if (obj2.isRiggedMesh()) {
                    builder2.add(obj2)
                } else {
                    builder.put(obj, obj2)
                }
            }
        }
        this.nonRigged = builder.build()
        this.rigged = builder2.build()
        this.glAnimationDataBuffer = drawableAttachments.glAnimationDataBuffer
        Debug.Printf("Updated drawableAttachments: %d rigged, %d non-rigged", Int.valueOf(this.rigged.size()), Int.valueOf(this.nonRigged.size()))
    }

    @SuppressLint({"NewApi"})
    fun Draw(RenderContext renderContext, AvatarSkeleton avatarSkeleton, Boolean z): Boolean {
        if (!this.rigged.isEmpty()) {
            if (renderContext.hasGL30) {
                renderContext.setupRiggedMeshProgram(true)
                if (this.glAnimationDataBuffer == null) {
                    this.glAnimationDataBuffer = GLLoadableBuffer(DirectByteBuffer(renderContext.currentRiggedMeshProgram.uAnimationDataBlockSize))
                    z2 = true
                } else {
                    z2 = false
                }
                if (z || r0) {
                    this.glAnimationDataBuffer.getRawBuffer().loadFromFloatArray(0, avatarSkeleton.jointWorldMatrix, 0, (SLSkeletonBoneID.VALUES.size + 47) * 16)
                }
                GLLoadableBuffer gLLoadableBuffer = this.glAnimationDataBuffer
                if (z) {
                    z2 = true
                }
                gLLoadableBuffer.BindUniformDynamic(renderContext, 1, z2)
                var i: Int = 0
                for (DrawableObject DrawRigged30 : this.rigged) {
                    i = DrawRigged30.DrawRigged30(renderContext, 1) | i
                }
                if ((i & 2) != 0) {
                    renderContext.setupRiggedMeshProgram(false)
                    for (DrawableObject DrawRigged302 : this.rigged) {
                        DrawRigged302.DrawRigged30(renderContext, 2)
                    }
                }
                renderContext.clearRiggedMeshProgram()
            } else {
                for (DrawableObject DrawRigged3022 : this.rigged) {
                    DrawRigged3022.DrawRigged(renderContext, avatarSkeleton, 3)
                }
            }
        }
        var z3: Boolean = false
        for (Any obj : this.nonRigged.keySet()) {
            FloatArray attachmentMatrix = avatarSkeleton.getAttachmentMatrix(obj.intValue())
            if (attachmentMatrix != null) {
                renderContext.glObjWorldPushAndMultMatrixf(attachmentMatrix, 0)
                for (DrawableObject DrawRigged30222 : this.nonRigged.get(obj)) {
                    if (DrawRigged30222.isRiggedMesh()) {
                        z3 = true
                    } else {
                        DrawRigged30222.Draw(renderContext, 3)
                    }
                }
                renderContext.glObjWorldPopMatrix()
            }
            z3 = z3
        }
        return z3
    }
}
