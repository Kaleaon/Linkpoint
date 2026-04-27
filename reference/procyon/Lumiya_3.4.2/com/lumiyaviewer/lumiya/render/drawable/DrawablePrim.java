// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import android.annotation.TargetApi;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.FlexiPrimProgram;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import android.opengl.Matrix;
import android.opengl.GLES11;
import android.opengl.GLES10;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;

public class DrawablePrim
{
    public static final int RENDER_PASS_ALL = 3;
    public static final int RENDER_PASS_OPAQUE = 1;
    public static final int RENDER_PASS_TRANSPARENT = 2;
    private final int[] FaceColorsIDs;
    private final int FaceCount;
    private final DrawableFaceTexture[] FaceTextures;
    private final float[] FaceUVMatrices;
    private boolean drawingTextureEnabled;
    private boolean firstFace;
    private final boolean isRiggedMesh;
    private final boolean isSingleFace;
    private final boolean riggingFitsGL20;
    private final int singleFaceColor;
    private final float[] singleFaceMatrix;
    private final DrawableFaceTexture singleFaceTexture;
    private final DrawableGeometry volumeGeometry;
    
    public DrawablePrim(final PrimDrawParams primDrawParams, final DrawableGeometry volumeGeometry) {
        this.volumeGeometry = volumeGeometry;
        final boolean facesCombined = volumeGeometry.isFacesCombined();
        this.isRiggedMesh = volumeGeometry.isRiggedMesh();
        this.riggingFitsGL20 = (this.isRiggedMesh && volumeGeometry.riggingFitsGL20());
        this.FaceCount = volumeGeometry.getFaceCount();
        final SLTextureEntry textures = primDrawParams.getTextures();
        if (textures != null) {
            final SLTextureEntryFace getDefaultTexture = textures.GetDefaultTexture();
            if (textures.isSingleFace() && facesCombined) {
                this.isSingleFace = true;
                this.singleFaceMatrix = new float[16];
                final SLTextureEntryFace getFace = textures.GetFace(0);
                if (getFace != null) {
                    this.singleFaceColor = getFace.getRGBA(getDefaultTexture);
                    final UUID textureID = getFace.getTextureID(getDefaultTexture);
                    if (textureID != null) {
                        this.singleFaceTexture = new DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim));
                    }
                    else {
                        this.singleFaceTexture = null;
                    }
                    this.initFaceUVMatrix(getDefaultTexture, getFace, this.singleFaceMatrix, 0);
                }
                else {
                    this.singleFaceColor = 0;
                    this.singleFaceTexture = null;
                }
                this.FaceColorsIDs = null;
                this.FaceTextures = null;
                this.FaceUVMatrices = null;
            }
            else {
                this.isSingleFace = false;
                this.singleFaceColor = 0;
                this.singleFaceTexture = null;
                this.singleFaceMatrix = null;
                this.FaceColorsIDs = new int[this.FaceCount * 2];
                this.FaceTextures = new DrawableFaceTexture[this.FaceCount];
                this.FaceUVMatrices = new float[this.FaceCount * 16];
                int i = 0;
                int n = 0;
                while (i < this.FaceCount) {
                    final SLTextureEntryFace getFace2 = textures.GetFace(volumeGeometry.getFaceID(i));
                    if (getFace2 != null) {
                        this.FaceColorsIDs[n] = getFace2.getRGBA(getDefaultTexture);
                        this.FaceColorsIDs[n + 1] = 0;
                        final UUID textureID2 = getFace2.getTextureID(getDefaultTexture);
                        if (textureID2 != null) {
                            this.FaceTextures[i] = new DrawableFaceTexture(DrawableTextureParams.create(textureID2, TextureClass.Prim));
                        }
                        this.initFaceUVMatrix(getDefaultTexture, getFace2, this.FaceUVMatrices, i * 16);
                    }
                    n += 2;
                    ++i;
                }
            }
        }
        else {
            this.isSingleFace = false;
            this.singleFaceColor = 0;
            this.singleFaceTexture = null;
            this.singleFaceMatrix = null;
            this.FaceColorsIDs = new int[this.FaceCount * 2];
            this.FaceTextures = new DrawableFaceTexture[this.FaceCount];
            this.FaceUVMatrices = new float[this.FaceCount * 16];
        }
    }
    
    private int DrawFace(final RenderContext renderContext, final DrawableGeometry drawableGeometry, final GLLoadableBuffer glLoadableBuffer, final boolean b, final int n, final int n2, final DrawableFaceTexture drawableFaceTexture, final float[] array, final int n3, final int n4) {
        final int faceRenderMask = this.getFaceRenderMask(n2, drawableFaceTexture);
        if ((faceRenderMask & n4) == 0x0) {
            return faceRenderMask;
        }
        final boolean b2 = false;
        boolean drawingTextureEnabled;
        if (!b) {
            if (renderContext.hasGL20) {
                GLES20.glUniform4f(renderContext.curPrimProgram.vColor, (255 - (n2 >> 0 & 0xFF)) / 255.0f, (255 - (n2 >> 8 & 0xFF)) / 255.0f, (255 - (n2 >> 16 & 0xFF)) / 255.0f, (255 - (n2 >> 24 & 0xFF)) / 255.0f);
            }
            else {
                GLES10.glColor4f((255 - (n2 >> 0 & 0xFF)) / 255.0f, (255 - (n2 >> 8 & 0xFF)) / 255.0f, (255 - (n2 >> 16 & 0xFF)) / 255.0f, (255 - (n2 >> 24 & 0xFF)) / 255.0f);
            }
            drawingTextureEnabled = b2;
            if (drawableFaceTexture != null) {
                drawingTextureEnabled = b2;
                if (drawableFaceTexture.GLDraw(renderContext)) {
                    drawingTextureEnabled = true;
                }
            }
        }
        else if (renderContext.hasGL20) {
            GLES20.glUniform4f(renderContext.curPrimProgram.vColor, 1.0f, 0.0f, 0.0f, 0.6f);
            drawingTextureEnabled = b2;
        }
        else {
            GLES10.glColor4f(1.0f, 0.0f, 0.0f, 0.6f);
            drawingTextureEnabled = b2;
        }
        if (drawingTextureEnabled != this.drawingTextureEnabled || this.firstFace) {
            if (renderContext.hasGL20) {
                if (!drawingTextureEnabled) {
                    GLES20.glBindTexture(3553, 0);
                    renderContext.curPrimProgram.setTextureEnabled(false);
                }
                else {
                    renderContext.curPrimProgram.setTextureEnabled(true);
                }
            }
            else if (drawingTextureEnabled) {
                GLES10.glEnable(3553);
                GLES10.glEnableClientState(32888);
            }
            else {
                GLES10.glDisable(3553);
                GLES10.glDisableClientState(32888);
            }
            this.drawingTextureEnabled = drawingTextureEnabled;
            this.firstFace = false;
        }
        if (renderContext.hasGL20) {
            GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, array, n3);
            if (n == -1) {
                drawableGeometry.GLDrawAll20(renderContext);
            }
            else {
                drawableGeometry.GLDrawFace20(renderContext, n);
            }
        }
        else {
            GLES11.glMatrixMode(5890);
            GLES11.glPushMatrix();
            GLES11.glLoadMatrixf(array, n3);
            if (n == -1) {
                drawableGeometry.GLDrawAll10(renderContext);
            }
            else {
                drawableGeometry.GLDrawFace10(renderContext, n, glLoadableBuffer);
            }
            GLES11.glPopMatrix();
            GLES11.glMatrixMode(5888);
        }
        return faceRenderMask;
    }
    
    private int DrawFaceFast20(final RenderContext renderContext, final DrawableGeometry drawableGeometry, final int n, final int n2, final DrawableFaceTexture drawableFaceTexture, final float[] array, final int n3, final int n4) {
        final int faceRenderMask = this.getFaceRenderMask(n2, drawableFaceTexture);
        if ((faceRenderMask & n4) == 0x0) {
            return faceRenderMask;
        }
        GLES20.glUniform4f(renderContext.curPrimProgram.vColor, (255 - (n2 >> 0 & 0xFF)) / 255.0f, (255 - (n2 >> 8 & 0xFF)) / 255.0f, (255 - (n2 >> 16 & 0xFF)) / 255.0f, (255 - (n2 >> 24 & 0xFF)) / 255.0f);
        renderContext.bindFaceTexture(drawableFaceTexture);
        GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, array, n3);
        if (n == -1) {
            drawableGeometry.GLDrawAll20(renderContext);
        }
        else {
            drawableGeometry.GLDrawFace20(renderContext, n);
        }
        return faceRenderMask;
    }
    
    private int getFaceRenderMask(int n, final DrawableFaceTexture drawableFaceTexture) {
        final int n2 = 1;
        int n3 = 0;
        if ((n & 0xFF000000) == 0xFF000000) {
            return 0;
        }
        if ((n & 0xFF000000) != 0x0) {
            n3 = 1;
        }
        int hasAlphaLayer = n3;
        if (n3 == 0) {
            hasAlphaLayer = n3;
            if (drawableFaceTexture != null) {
                hasAlphaLayer = (drawableFaceTexture.hasAlphaLayer() ? 1 : 0);
            }
        }
        n = n2;
        if (hasAlphaLayer != 0) {
            n = 2;
        }
        return n;
    }
    
    private void initFaceUVMatrix(final SLTextureEntryFace slTextureEntryFace, final SLTextureEntryFace slTextureEntryFace2, final float[] array, final int n) {
        final float[] array2 = new float[16];
        Matrix.setIdentityM(array2, 0);
        Matrix.translateM(array2, 0, slTextureEntryFace2.getOffsetU(slTextureEntryFace) + 0.5f, slTextureEntryFace2.getOffsetV(slTextureEntryFace) + 0.5f, 0.0f);
        Matrix.scaleM(array2, 0, slTextureEntryFace2.getRepeatU(slTextureEntryFace), slTextureEntryFace2.getRepeatV(slTextureEntryFace), 1.0f);
        Matrix.rotateM(array, n, array2, 0, slTextureEntryFace2.getRotation(slTextureEntryFace) / 0.017453292f, 0.0f, 0.0f, -1.0f);
        Matrix.translateM(array, n, -0.5f, -0.5f, 0.0f);
    }
    
    public final void ApplyJointTranslations(final MeshJointTranslations meshJointTranslations) {
        if (this.isRiggedMesh) {
            this.volumeGeometry.ApplyJointTranslations(meshJointTranslations);
        }
    }
    
    public final int Draw(final RenderContext renderContext, final boolean b, final PrimFlexibleInfo primFlexibleInfo, final int n) {
        final DrawableGeometry volumeGeometry = this.volumeGeometry;
        this.firstFace = true;
        GLLoadableBuffer glLoadableBuffer;
        if (renderContext.hasGL20) {
            float[] matrices;
            if (primFlexibleInfo != null) {
                matrices = primFlexibleInfo.getMatrices();
            }
            else {
                matrices = null;
            }
            PrimProgram curPrimProgram;
            if (this.isRiggedMesh && this.riggingFitsGL20) {
                curPrimProgram = renderContext.riggedMeshProgram;
            }
            else if (matrices != null) {
                curPrimProgram = renderContext.flexiPrimProgram;
            }
            else {
                curPrimProgram = renderContext.primProgram;
            }
            renderContext.curPrimProgram = curPrimProgram;
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle());
            renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix);
            renderContext.glObjWorldApplyMatrix(renderContext.curPrimProgram.uObjWorldMatrix);
            renderContext.glObjScaleApplyVector(renderContext.curPrimProgram.uObjCoordScale);
            if (matrices != null && renderContext.curPrimProgram instanceof FlexiPrimProgram) {
                final FlexiPrimProgram flexiPrimProgram = (FlexiPrimProgram)renderContext.curPrimProgram;
                GLES20.glUniform1i(flexiPrimProgram.uNumSectionMatrices, matrices.length / 16);
                GLES20.glUniformMatrix4fv(flexiPrimProgram.uSectionMatrices, matrices.length / 16, false, matrices, 0);
            }
            glLoadableBuffer = volumeGeometry.GLBindBuffers20(renderContext);
        }
        else {
            glLoadableBuffer = volumeGeometry.GLBindBuffers10(renderContext, primFlexibleInfo);
        }
        this.drawingTextureEnabled = false;
        if (this.isSingleFace) {
            return this.DrawFace(renderContext, volumeGeometry, glLoadableBuffer, b, -1, this.singleFaceColor, this.singleFaceTexture, this.singleFaceMatrix, 0, n);
        }
        int i;
        int n2;
        int drawFace;
        for (i = 0, n2 = 0; i < this.FaceCount; ++i, n2 |= drawFace) {
            drawFace = this.DrawFace(renderContext, volumeGeometry, glLoadableBuffer, b, i, this.FaceColorsIDs[i * 2], this.FaceTextures[i], this.FaceUVMatrices, i * 16, n);
        }
        return n2;
    }
    
    public final int DrawFast20(final RenderContext renderContext, final boolean b, final PrimFlexibleInfo primFlexibleInfo, final int n) {
        boolean b2 = true;
        float[] matrices = null;
        final DrawableGeometry volumeGeometry = this.volumeGeometry;
        if (primFlexibleInfo != null) {
            matrices = primFlexibleInfo.getMatrices();
        }
        if (n != 1) {
            b2 = false;
        }
        PrimProgram curPrimProgram;
        if (this.isRiggedMesh && this.riggingFitsGL20) {
            curPrimProgram = renderContext.riggedMeshProgram;
        }
        else if (matrices != null) {
            if (b2) {
                curPrimProgram = renderContext.flexiPrimOpaqueProgram;
            }
            else {
                curPrimProgram = renderContext.flexiPrimProgram;
            }
        }
        else if (b2) {
            curPrimProgram = renderContext.primOpaqueProgram;
        }
        else {
            curPrimProgram = renderContext.primProgram;
        }
        if (renderContext.curPrimProgram != curPrimProgram) {
            renderContext.curPrimProgram = curPrimProgram;
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle());
            renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix);
        }
        renderContext.glObjWorldApplyMatrix(renderContext.curPrimProgram.uObjWorldMatrix);
        renderContext.glObjScaleApplyVector(renderContext.curPrimProgram.uObjCoordScale);
        if (matrices != null) {
            GLES20.glUniform1i(renderContext.flexiPrimProgram.uNumSectionMatrices, matrices.length / 16);
            GLES20.glUniformMatrix4fv(renderContext.flexiPrimProgram.uSectionMatrices, matrices.length / 16, false, matrices, 0);
        }
        volumeGeometry.GLBindBuffers20(renderContext);
        int drawFaceFast20;
        if (this.isSingleFace) {
            drawFaceFast20 = this.DrawFaceFast20(renderContext, volumeGeometry, -1, this.singleFaceColor, this.singleFaceTexture, this.singleFaceMatrix, 0, n);
        }
        else {
            int n2 = 0;
            int n3 = 0;
            while (true) {
                drawFaceFast20 = n3;
                if (n2 >= this.FaceCount) {
                    break;
                }
                final int drawFaceFast21 = this.DrawFaceFast20(renderContext, volumeGeometry, n2, this.FaceColorsIDs[n2 * 2], this.FaceTextures[n2], this.FaceUVMatrices, n2 * 16, n);
                ++n2;
                n3 |= drawFaceFast21;
            }
        }
        return drawFaceFast20;
    }
    
    @TargetApi(18)
    public final int DrawRigged30(final RenderContext renderContext, final int n) {
        final DrawableGeometry volumeGeometry = this.volumeGeometry;
        int i = 0;
        int n2 = 0;
        int n3 = 0;
        while (i < this.FaceCount) {
            final int n4 = this.FaceColorsIDs[i * 2];
            final DrawableFaceTexture drawableFaceTexture = this.FaceTextures[i];
            final int faceRenderMask = this.getFaceRenderMask(n4, drawableFaceTexture);
            final int n5 = n3 | faceRenderMask;
            int n6 = n2;
            if ((faceRenderMask & n) != 0x0) {
                if ((n6 = n2) == 0) {
                    volumeGeometry.GLBindBuffersRigged30(renderContext);
                    n6 = 1;
                }
                GLES20.glUniform4f(renderContext.curPrimProgram.vColor, (255 - (n4 >> 0 & 0xFF)) / 255.0f, (255 - (n4 >> 8 & 0xFF)) / 255.0f, (255 - (n4 >> 16 & 0xFF)) / 255.0f, (255 - (n4 >> 24 & 0xFF)) / 255.0f);
                renderContext.bindFaceTexture(drawableFaceTexture);
                GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, this.FaceUVMatrices, i * 16);
                volumeGeometry.GLDrawRiggedFace30(renderContext, i);
            }
            ++i;
            n2 = n6;
            n3 = n5;
        }
        return n3;
    }
    
    public IntersectInfo IntersectRay(final LLVector3 llVector3, final LLVector3 llVector4) {
        final IntersectInfo intersectRay = this.volumeGeometry.IntersectRay(llVector3, llVector4);
        if (intersectRay == null || !intersectRay.faceKnown) {
            return intersectRay;
        }
        if (this.isSingleFace && this.singleFaceMatrix != null) {
            return new IntersectInfo(intersectRay, this.singleFaceMatrix, 0);
        }
        if (!this.isSingleFace && this.FaceUVMatrices != null) {
            return new IntersectInfo(intersectRay, this.FaceUVMatrices, intersectRay.faceID * 16);
        }
        return intersectRay;
    }
    
    public final boolean UpdateRigged(final RenderContext renderContext, final AvatarSkeleton avatarSkeleton) {
        return this.isRiggedMesh && this.volumeGeometry.UpdateRigged(renderContext, avatarSkeleton);
    }
    
    public boolean hasExtendedBones() {
        return this.volumeGeometry.hasExtendedBones();
    }
    
    public final boolean isRiggedMesh() {
        return this.isRiggedMesh;
    }
}
