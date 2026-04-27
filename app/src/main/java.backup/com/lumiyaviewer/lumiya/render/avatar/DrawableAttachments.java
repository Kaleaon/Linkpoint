package com.lumiyaviewer.lumiya.render.avatar;

import android.annotation.SuppressLint;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
class DrawableAttachments {
    private GLLoadableBuffer glAnimationDataBuffer;
    @Nonnull
    private final ImmutableMultimap<Integer, DrawableObject> nonRigged;
    @Nonnull
    private final ImmutableList<DrawableObject> rigged;

    DrawableAttachments() {
        this.glAnimationDataBuffer = null;
        this.nonRigged = ImmutableMultimap.of();
        this.rigged = ImmutableList.of();
    }

    DrawableAttachments(@Nullable Multimap<Integer, DrawableObject> multimap) {
        this.glAnimationDataBuffer = null;
        Builder builder = ImmutableMultimap.builder();
        ImmutableList.Builder builder2 = ImmutableList.builder();
        if (multimap != null) {
            for (Integer num : multimap.keySet()) {
                for (Object obj : multimap.get(num)) {
                    if (obj.isRiggedMesh()) {
                        builder2.add(obj);
                    } else {
                        builder.put(num, obj);
                    }
                }
            }
        }
        this.nonRigged = builder.build();
        this.rigged = builder2.build();
        Debug.Printf("Created drawableAttachments: %d rigged, %d non-rigged", Integer.valueOf(this.rigged.size()), Integer.valueOf(this.nonRigged.size()));
    }

    DrawableAttachments(@Nonnull DrawableAttachments drawableAttachments) {
        this.glAnimationDataBuffer = null;
        Builder builder = ImmutableMultimap.builder();
        ImmutableList.Builder builder2 = ImmutableList.builder();
        builder2.addAll(drawableAttachments.rigged);
        for (Object obj : drawableAttachments.nonRigged.keySet()) {
            for (Object obj2 : drawableAttachments.nonRigged.get(obj)) {
                if (obj2.isRiggedMesh()) {
                    builder2.add(obj2);
                } else {
                    builder.put(obj, obj2);
                }
            }
        }
        this.nonRigged = builder.build();
        this.rigged = builder2.build();
        this.glAnimationDataBuffer = drawableAttachments.glAnimationDataBuffer;
        Debug.Printf("Updated drawableAttachments: %d rigged, %d non-rigged", Integer.valueOf(this.rigged.size()), Integer.valueOf(this.nonRigged.size()));
    }

    @SuppressLint({"NewApi"})
    boolean Draw(RenderContext renderContext, AvatarSkeleton avatarSkeleton, boolean z) {
        if (!this.rigged.isEmpty()) {
            if (renderContext.hasGL30) {
                boolean z2;
                renderContext.setupRiggedMeshProgram(true);
                if (this.glAnimationDataBuffer == null) {
                    this.glAnimationDataBuffer = new GLLoadableBuffer(new DirectByteBuffer(renderContext.currentRiggedMeshProgram.uAnimationDataBlockSize));
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (z || r0) {
                    this.glAnimationDataBuffer.getRawBuffer().loadFromFloatArray(0, avatarSkeleton.jointWorldMatrix, 0, (SLSkeletonBoneID.VALUES.length + 47) * 16);
                }
                GLLoadableBuffer gLLoadableBuffer = this.glAnimationDataBuffer;
                if (z) {
                    z2 = true;
                }
                gLLoadableBuffer.BindUniformDynamic(renderContext, 1, z2);
                int i = 0;
                for (DrawableObject DrawRigged30 : this.rigged) {
                    i = DrawRigged30.DrawRigged30(renderContext, 1) | i;
                }
                if ((i & 2) != 0) {
                    renderContext.setupRiggedMeshProgram(false);
                    for (DrawableObject DrawRigged302 : this.rigged) {
                        DrawRigged302.DrawRigged30(renderContext, 2);
                    }
                }
                renderContext.clearRiggedMeshProgram();
            } else {
                for (DrawableObject DrawRigged3022 : this.rigged) {
                    DrawRigged3022.DrawRigged(renderContext, avatarSkeleton, 3);
                }
            }
        }
        boolean z3 = false;
        for (Object obj : this.nonRigged.keySet()) {
            float[] attachmentMatrix = avatarSkeleton.getAttachmentMatrix(obj.intValue());
            if (attachmentMatrix != null) {
                renderContext.glObjWorldPushAndMultMatrixf(attachmentMatrix, 0);
                for (DrawableObject DrawRigged30222 : this.nonRigged.get(obj)) {
                    if (DrawRigged30222.isRiggedMesh()) {
                        z3 = true;
                    } else {
                        DrawRigged30222.Draw(renderContext, 3);
                    }
                }
                renderContext.glObjWorldPopMatrix();
            }
            z3 = z3;
        }
        return z3;
    }
}
