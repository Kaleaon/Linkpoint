package com.lumiyaviewer.lumiya.render.avatar;

import android.annotation.SuppressLint;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
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
/* JADX INFO: Access modifiers changed from: package-private */
@Immutable
/* loaded from: classes.dex */
public class DrawableAttachments {
    private GLLoadableBuffer glAnimationDataBuffer;
    @Nonnull
    private final ImmutableMultimap<Integer, DrawableObject> nonRigged;
    @Nonnull
    private final ImmutableList<DrawableObject> rigged;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableAttachments() {
        this.glAnimationDataBuffer = null;
        this.nonRigged = ImmutableMultimap.of();
        this.rigged = ImmutableList.of();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableAttachments(@Nullable Multimap<Integer, DrawableObject> multimap) {
        this.glAnimationDataBuffer = null;
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        ImmutableList.Builder builder2 = ImmutableList.builder();
        if (multimap != null) {
            for (Integer num : multimap.keySet()) {
                for (DrawableObject drawableObject : multimap.get(num)) {
                    if (drawableObject.isRiggedMesh()) {
                        builder2.add((ImmutableList.Builder) drawableObject);
                    } else {
                        builder.put(num, drawableObject);
                    }
                }
            }
        }
        this.nonRigged = builder.build();
        this.rigged = builder2.build();
        Debug.Printf("Created drawableAttachments: %d rigged, %d non-rigged", Integer.valueOf(this.rigged.size()), Integer.valueOf(this.nonRigged.size()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableAttachments(@Nonnull DrawableAttachments drawableAttachments) {
        this.glAnimationDataBuffer = null;
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        ImmutableList.Builder builder2 = ImmutableList.builder();
        builder2.addAll((Iterable) drawableAttachments.rigged);
        for (Integer num : drawableAttachments.nonRigged.keySet()) {
            for (DrawableObject drawableObject : drawableAttachments.nonRigged.get((ImmutableMultimap<Integer, DrawableObject>) num)) {
                if (drawableObject.isRiggedMesh()) {
                    builder2.add((ImmutableList.Builder) drawableObject);
                } else {
                    builder.put(num, drawableObject);
                }
            }
        }
        this.nonRigged = builder.build();
        this.rigged = builder2.build();
        this.glAnimationDataBuffer = drawableAttachments.glAnimationDataBuffer;
        Debug.Printf("Updated drawableAttachments: %d rigged, %d non-rigged", Integer.valueOf(this.rigged.size()), Integer.valueOf(this.nonRigged.size()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"NewApi"})
    public boolean Draw(RenderContext renderContext, AvatarSkeleton avatarSkeleton, boolean z) {
        boolean z2;
        if (!this.rigged.isEmpty()) {
            if (renderContext.hasGL30) {
                renderContext.setupRiggedMeshProgram(true);
                if (this.glAnimationDataBuffer == null) {
                    this.glAnimationDataBuffer = new GLLoadableBuffer(new DirectByteBuffer(renderContext.currentRiggedMeshProgram.uAnimationDataBlockSize));
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (z || z2) {
                    this.glAnimationDataBuffer.getRawBuffer().loadFromFloatArray(0, avatarSkeleton.jointWorldMatrix, 0, (SLSkeletonBoneID.VALUES.length + 47) * 16);
                }
                GLLoadableBuffer gLLoadableBuffer = this.glAnimationDataBuffer;
                if (z) {
                    z2 = true;
                }
                gLLoadableBuffer.BindUniformDynamic(renderContext, 1, z2);
                int i = 0;
                for (DrawableObject drawableObject : this.rigged) {
                    i = drawableObject.DrawRigged30(renderContext, 1) | i;
                }
                if ((i & 2) != 0) {
                    renderContext.setupRiggedMeshProgram(false);
                    for (DrawableObject drawableObject2 : this.rigged) {
                        drawableObject2.DrawRigged30(renderContext, 2);
                    }
                }
                renderContext.clearRiggedMeshProgram();
            } else {
                for (DrawableObject drawableObject3 : this.rigged) {
                    drawableObject3.DrawRigged(renderContext, avatarSkeleton, 3);
                }
            }
        }
        boolean z3 = false;
        for (Integer num : this.nonRigged.keySet()) {
            float[] attachmentMatrix = avatarSkeleton.getAttachmentMatrix(num.intValue());
            if (attachmentMatrix != null) {
                renderContext.glObjWorldPushAndMultMatrixf(attachmentMatrix, 0);
                for (DrawableObject drawableObject4 : this.nonRigged.get((ImmutableMultimap<Integer, DrawableObject>) num)) {
                    if (drawableObject4.isRiggedMesh()) {
                        z3 = true;
                    } else {
                        drawableObject4.Draw(renderContext, 3);
                    }
                }
                renderContext.glObjWorldPopMatrix();
            }
            z3 = z3;
        }
        return z3;
    }
}
