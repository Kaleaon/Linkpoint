// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import android.annotation.SuppressLint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nullable;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.google.common.collect.ImmutableMultimap;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import javax.annotation.concurrent.Immutable;

@Immutable
class DrawableAttachments
{
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
    
    DrawableAttachments(@Nullable final Multimap<Integer, DrawableObject> multimap) {
        this.glAnimationDataBuffer = null;
        final ImmutableMultimap.Builder<Integer, DrawableObject> builder = ImmutableMultimap.builder();
        final ImmutableList.Builder<DrawableObject> builder2 = ImmutableList.builder();
        if (multimap != null) {
            for (final Integer n : multimap.keySet()) {
                for (final DrawableObject drawableObject : multimap.get(n)) {
                    if (drawableObject.isRiggedMesh()) {
                        builder2.add(drawableObject);
                    }
                    else {
                        builder.put(n, drawableObject);
                    }
                }
            }
        }
        this.nonRigged = builder.build();
        this.rigged = builder2.build();
        Debug.Printf("Created drawableAttachments: %d rigged, %d non-rigged", this.rigged.size(), this.nonRigged.size());
    }
    
    DrawableAttachments(@Nonnull final DrawableAttachments drawableAttachments) {
        this.glAnimationDataBuffer = null;
        final ImmutableMultimap.Builder<Integer, DrawableObject> builder = ImmutableMultimap.builder();
        final ImmutableList.Builder<DrawableObject> builder2 = ImmutableList.builder();
        builder2.addAll(drawableAttachments.rigged);
        for (final Integer n : drawableAttachments.nonRigged.keySet()) {
            for (final DrawableObject drawableObject : drawableAttachments.nonRigged.get(n)) {
                if (drawableObject.isRiggedMesh()) {
                    builder2.add(drawableObject);
                }
                else {
                    builder.put(n, drawableObject);
                }
            }
        }
        this.nonRigged = builder.build();
        this.rigged = builder2.build();
        this.glAnimationDataBuffer = drawableAttachments.glAnimationDataBuffer;
        Debug.Printf("Updated drawableAttachments: %d rigged, %d non-rigged", this.rigged.size(), this.nonRigged.size());
    }
    
    @SuppressLint({ "NewApi" })
    boolean Draw(final RenderContext renderContext, final AvatarSkeleton avatarSkeleton, final boolean b) {
        if (!this.rigged.isEmpty()) {
            if (renderContext.hasGL30) {
                renderContext.setupRiggedMeshProgram(true);
                boolean b2;
                if (this.glAnimationDataBuffer == null) {
                    this.glAnimationDataBuffer = new GLLoadableBuffer(new DirectByteBuffer(renderContext.currentRiggedMeshProgram.uAnimationDataBlockSize));
                    b2 = true;
                }
                else {
                    b2 = false;
                }
                if (b || b2) {
                    this.glAnimationDataBuffer.getRawBuffer().loadFromFloatArray(0, avatarSkeleton.jointWorldMatrix, 0, (SLSkeletonBoneID.VALUES.length + 47) * 16);
                }
                final GLLoadableBuffer glAnimationDataBuffer = this.glAnimationDataBuffer;
                if (b) {
                    b2 = true;
                }
                glAnimationDataBuffer.BindUniformDynamic(renderContext, 1, b2);
                final Iterator<Object> iterator = this.rigged.iterator();
                int n = 0;
                while (iterator.hasNext()) {
                    n |= iterator.next().DrawRigged30(renderContext, 1);
                }
                if ((n & 0x2) != 0x0) {
                    renderContext.setupRiggedMeshProgram(false);
                    final Iterator<Object> iterator2 = this.rigged.iterator();
                    while (iterator2.hasNext()) {
                        iterator2.next().DrawRigged30(renderContext, 2);
                    }
                }
                renderContext.clearRiggedMeshProgram();
            }
            else {
                final Iterator<Object> iterator3 = this.rigged.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().DrawRigged(renderContext, avatarSkeleton, 3);
                }
            }
        }
        final Iterator<Object> iterator4 = this.nonRigged.keySet().iterator();
        boolean b3 = false;
        while (iterator4.hasNext()) {
            final Integer n2 = iterator4.next();
            final float[] attachmentMatrix = avatarSkeleton.getAttachmentMatrix(n2);
            if (attachmentMatrix != null) {
                renderContext.glObjWorldPushAndMultMatrixf(attachmentMatrix, 0);
                for (final DrawableObject drawableObject : this.nonRigged.get(n2)) {
                    if (drawableObject.isRiggedMesh()) {
                        b3 = true;
                    }
                    else {
                        drawableObject.Draw(renderContext, 3);
                    }
                }
                renderContext.glObjWorldPopMatrix();
            }
        }
        return b3;
    }
}
