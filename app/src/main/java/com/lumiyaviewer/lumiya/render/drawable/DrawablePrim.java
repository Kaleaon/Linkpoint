package com.lumiyaviewer.lumiya.render.drawable;

import android.annotation.TargetApi;
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.v4.view.ViewCompat;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.shaders.FlexiPrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

public class DrawablePrim {
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

    public DrawablePrim(PrimDrawParams primDrawParams, DrawableGeometry drawableGeometry) {
        this.volumeGeometry = drawableGeometry;
        boolean isFacesCombined = drawableGeometry.isFacesCombined();
        this.isRiggedMesh = drawableGeometry.isRiggedMesh();
        this.riggingFitsGL20 = this.isRiggedMesh ? drawableGeometry.riggingFitsGL20() : false;
        this.FaceCount = drawableGeometry.getFaceCount();
        SLTextureEntry textures = primDrawParams.getTextures();
        if (textures != null) {
            SLTextureEntryFace GetDefaultTexture = textures.GetDefaultTexture();
            if (textures.isSingleFace() && isFacesCombined) {
                this.isSingleFace = true;
                this.singleFaceMatrix = new float[16];
                SLTextureEntryFace GetFace = textures.GetFace(0);
                if (GetFace != null) {
                    this.singleFaceColor = GetFace.getRGBA(GetDefaultTexture);
                    UUID textureID = GetFace.getTextureID(GetDefaultTexture);
                    if (textureID != null) {
                        this.singleFaceTexture = new DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim));
                    } else {
                        this.singleFaceTexture = null;
                    }
                    initFaceUVMatrix(GetDefaultTexture, GetFace, this.singleFaceMatrix, 0);
                } else {
                    this.singleFaceColor = 0;
                    this.singleFaceTexture = null;
                }
                this.FaceColorsIDs = null;
                this.FaceTextures = null;
                this.FaceUVMatrices = null;
                return;
            }
            this.isSingleFace = false;
            this.singleFaceColor = 0;
            this.singleFaceTexture = null;
            this.singleFaceMatrix = null;
            this.FaceColorsIDs = new int[(this.FaceCount * 2)];
            this.FaceTextures = new DrawableFaceTexture[this.FaceCount];
            this.FaceUVMatrices = new float[(this.FaceCount * 16)];
            int i = 0;
            for (int i2 = 0; i2 < this.FaceCount; i2++) {
                SLTextureEntryFace GetFace2 = textures.GetFace(drawableGeometry.getFaceID(i2));
                if (GetFace2 != null) {
                    this.FaceColorsIDs[i] = GetFace2.getRGBA(GetDefaultTexture);
                    this.FaceColorsIDs[i + 1] = 0;
                    UUID textureID2 = GetFace2.getTextureID(GetDefaultTexture);
                    if (textureID2 != null) {
                        this.FaceTextures[i2] = new DrawableFaceTexture(DrawableTextureParams.create(textureID2, TextureClass.Prim));
                    }
                    initFaceUVMatrix(GetDefaultTexture, GetFace2, this.FaceUVMatrices, i2 * 16);
                }
                i += 2;
            }
            return;
        }
        this.isSingleFace = false;
        this.singleFaceColor = 0;
        this.singleFaceTexture = null;
        this.singleFaceMatrix = null;
        this.FaceColorsIDs = new int[(this.FaceCount * 2)];
        this.FaceTextures = new DrawableFaceTexture[this.FaceCount];
        this.FaceUVMatrices = new float[(this.FaceCount * 16)];
    }

    private int DrawFace(RenderContext renderContext, DrawableGeometry drawableGeometry, GLLoadableBuffer gLLoadableBuffer, boolean z, int i, int i2, DrawableFaceTexture drawableFaceTexture, float[] fArr, int i3, int i4) {
        int faceRenderMask = getFaceRenderMask(i2, drawableFaceTexture);
        if ((faceRenderMask & i4) == 0) {
            return faceRenderMask;
        }
        boolean z2 = false;
        if (!z) {
            if (renderContext.hasGL20) {
                GLES20.glUniform4f(renderContext.curPrimProgram.vColor, ((float) (255 - ((i2 >> 0) & 255))) / 255.0f, ((float) (255 - ((i2 >> 8) & 255))) / 255.0f, ((float) (255 - ((i2 >> 16) & 255))) / 255.0f, ((float) (255 - ((i2 >> 24) & 255))) / 255.0f);
            } else {
                GLES10.glColor4f(((float) (255 - ((i2 >> 0) & 255))) / 255.0f, ((float) (255 - ((i2 >> 8) & 255))) / 255.0f, ((float) (255 - ((i2 >> 16) & 255))) / 255.0f, ((float) (255 - ((i2 >> 24) & 255))) / 255.0f);
            }
            if (drawableFaceTexture != null && drawableFaceTexture.GLDraw(renderContext)) {
                z2 = true;
            }
        } else if (renderContext.hasGL20) {
            GLES20.glUniform4f(renderContext.curPrimProgram.vColor, 1.0f, 0.0f, 0.0f, 0.6f);
        } else {
            GLES10.glColor4f(1.0f, 0.0f, 0.0f, 0.6f);
        }
        if (z2 != this.drawingTextureEnabled || this.firstFace) {
            if (renderContext.hasGL20) {
                if (z2) {
                    renderContext.curPrimProgram.setTextureEnabled(true);
                } else {
                    GLES20.glBindTexture(3553, 0);
                    renderContext.curPrimProgram.setTextureEnabled(false);
                }
            } else if (z2) {
                GLES10.glEnable(3553);
                GLES10.glEnableClientState(32888);
            } else {
                GLES10.glDisable(3553);
                GLES10.glDisableClientState(32888);
            }
            this.drawingTextureEnabled = z2;
            this.firstFace = false;
        }
        if (renderContext.hasGL20) {
            GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, fArr, i3);
            if (i == -1) {
                drawableGeometry.GLDrawAll20(renderContext);
            } else {
                drawableGeometry.GLDrawFace20(renderContext, i);
            }
        } else {
            GLES11.glMatrixMode(5890);
            GLES11.glPushMatrix();
            GLES11.glLoadMatrixf(fArr, i3);
            if (i == -1) {
                drawableGeometry.GLDrawAll10(renderContext);
            } else {
                drawableGeometry.GLDrawFace10(renderContext, i, gLLoadableBuffer);
            }
            GLES11.glPopMatrix();
            GLES11.glMatrixMode(5888);
        }
        return faceRenderMask;
    }

    private int DrawFaceFast20(RenderContext renderContext, DrawableGeometry drawableGeometry, int i, int i2, DrawableFaceTexture drawableFaceTexture, float[] fArr, int i3, int i4) {
        int faceRenderMask = getFaceRenderMask(i2, drawableFaceTexture);
        if ((faceRenderMask & i4) == 0) {
            return faceRenderMask;
        }
        GLES20.glUniform4f(renderContext.curPrimProgram.vColor, ((float) (255 - ((i2 >> 0) & 255))) / 255.0f, ((float) (255 - ((i2 >> 8) & 255))) / 255.0f, ((float) (255 - ((i2 >> 16) & 255))) / 255.0f, ((float) (255 - ((i2 >> 24) & 255))) / 255.0f);
        renderContext.bindFaceTexture(drawableFaceTexture);
        GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, fArr, i3);
        if (i == -1) {
            drawableGeometry.GLDrawAll20(renderContext);
        } else {
            drawableGeometry.GLDrawFace20(renderContext, i);
        }
        return faceRenderMask;
    }

    private int getFaceRenderMask(int i, DrawableFaceTexture drawableFaceTexture) {
        int i2 = 1;
        int i3 = 0;
        if ((i & ViewCompat.MEASURED_STATE_MASK) == ViewCompat.MEASURED_STATE_MASK) {
            return 0;
        }
        if ((i & ViewCompat.MEASURED_STATE_MASK) != 0) {
            i3 = 1;
        }
        if (i3 == 0 && drawableFaceTexture != null) {
            i3 = drawableFaceTexture.hasAlphaLayer();
        }
        if (i3 != 0) {
            i2 = 2;
        }
        return i2;
    }

    private void initFaceUVMatrix(SLTextureEntryFace sLTextureEntryFace, SLTextureEntryFace sLTextureEntryFace2, float[] fArr, int i) {
        float[] fArr2 = new float[16];
        Matrix.setIdentityM(fArr2, 0);
        Matrix.translateM(fArr2, 0, sLTextureEntryFace2.getOffsetU(sLTextureEntryFace) + 0.5f, sLTextureEntryFace2.getOffsetV(sLTextureEntryFace) + 0.5f, 0.0f);
        Matrix.scaleM(fArr2, 0, sLTextureEntryFace2.getRepeatU(sLTextureEntryFace), sLTextureEntryFace2.getRepeatV(sLTextureEntryFace), 1.0f);
        Matrix.rotateM(fArr, i, fArr2, 0, sLTextureEntryFace2.getRotation(sLTextureEntryFace) / 0.017453292f, 0.0f, 0.0f, -1.0f);
        Matrix.translateM(fArr, i, -0.5f, -0.5f, 0.0f);
    }

    public final void ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        if (this.isRiggedMesh) {
            this.volumeGeometry.ApplyJointTranslations(meshJointTranslations);
        }
    }

    public final int Draw(RenderContext renderContext, boolean z, PrimFlexibleInfo primFlexibleInfo, int i) {
        GLLoadableBuffer GLBindBuffers20;
        DrawableGeometry drawableGeometry = this.volumeGeometry;
        this.firstFace = true;
        if (renderContext.hasGL20) {
            float[] matrices = primFlexibleInfo != null ? primFlexibleInfo.getMatrices() : null;
            PrimProgram primProgram = (this.isRiggedMesh && this.riggingFitsGL20) ? renderContext.riggedMeshProgram : matrices != null ? renderContext.flexiPrimProgram : renderContext.primProgram;
            renderContext.curPrimProgram = primProgram;
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle());
            renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix);
            renderContext.glObjWorldApplyMatrix(renderContext.curPrimProgram.uObjWorldMatrix);
            renderContext.glObjScaleApplyVector(renderContext.curPrimProgram.uObjCoordScale);
            if (matrices != null && (renderContext.curPrimProgram instanceof FlexiPrimProgram)) {
                FlexiPrimProgram flexiPrimProgram = (FlexiPrimProgram) renderContext.curPrimProgram;
                GLES20.glUniform1i(flexiPrimProgram.uNumSectionMatrices, matrices.length / 16);
                GLES20.glUniformMatrix4fv(flexiPrimProgram.uSectionMatrices, matrices.length / 16, false, matrices, 0);
            }
            GLBindBuffers20 = drawableGeometry.GLBindBuffers20(renderContext);
        } else {
            GLBindBuffers20 = drawableGeometry.GLBindBuffers10(renderContext, primFlexibleInfo);
        }
        this.drawingTextureEnabled = false;
        if (this.isSingleFace) {
            return DrawFace(renderContext, drawableGeometry, GLBindBuffers20, z, -1, this.singleFaceColor, this.singleFaceTexture, this.singleFaceMatrix, 0, i);
        }
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int i4 = i2;
            if (i3 >= this.FaceCount) {
                return i4;
            }
            i2 = DrawFace(renderContext, drawableGeometry, GLBindBuffers20, z, i3, this.FaceColorsIDs[i3 * 2], this.FaceTextures[i3], this.FaceUVMatrices, i3 * 16, i) | i4;
            i3++;
        }
    }

    public final int DrawFast20(RenderContext renderContext, boolean z, PrimFlexibleInfo primFlexibleInfo, int i) {
        boolean z2 = true;
        float[] fArr = null;
        boolean z3 = false;
        DrawableGeometry drawableGeometry = this.volumeGeometry;
        if (primFlexibleInfo != null) {
            fArr = primFlexibleInfo.getMatrices();
        }
        if (i != 1) {
            z2 = false;
        }
        PrimProgram primProgram = (this.isRiggedMesh && this.riggingFitsGL20) ? renderContext.riggedMeshProgram : fArr != null ? z2 ? renderContext.flexiPrimOpaqueProgram : renderContext.flexiPrimProgram : z2 ? renderContext.primOpaqueProgram : renderContext.primProgram;
        if (renderContext.curPrimProgram != primProgram) {
            renderContext.curPrimProgram = primProgram;
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle());
            renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix);
        }
        renderContext.glObjWorldApplyMatrix(renderContext.curPrimProgram.uObjWorldMatrix);
        renderContext.glObjScaleApplyVector(renderContext.curPrimProgram.uObjCoordScale);
        if (fArr != null) {
            GLES20.glUniform1i(renderContext.flexiPrimProgram.uNumSectionMatrices, fArr.length / 16);
            GLES20.glUniformMatrix4fv(renderContext.flexiPrimProgram.uSectionMatrices, fArr.length / 16, false, fArr, 0);
        }
        drawableGeometry.GLBindBuffers20(renderContext);
        if (this.isSingleFace) {
            return DrawFaceFast20(renderContext, drawableGeometry, -1, this.singleFaceColor, this.singleFaceTexture, this.singleFaceMatrix, 0, i);
        }
        int i2 = 0;
        while (true) {
            boolean z4 = z3;
            if (i2 >= this.FaceCount) {
                return z4;
            }
            z3 = z4 | DrawFaceFast20(renderContext, drawableGeometry, i2, this.FaceColorsIDs[i2 * 2], this.FaceTextures[i2], this.FaceUVMatrices, i2 * 16, i);
            i2++;
        }
    }

    @TargetApi(18)
    public final int DrawRigged30(RenderContext renderContext, int i) {
        DrawableGeometry drawableGeometry = this.volumeGeometry;
        boolean z = false;
        int i2 = 0;
        for (int i3 = 0; i3 < this.FaceCount; i3++) {
            int i4 = this.FaceColorsIDs[i3 * 2];
            DrawableFaceTexture drawableFaceTexture = this.FaceTextures[i3];
            int faceRenderMask = getFaceRenderMask(i4, drawableFaceTexture);
            i2 |= faceRenderMask;
            if ((faceRenderMask & i) != 0) {
                if (!z) {
                    drawableGeometry.GLBindBuffersRigged30(renderContext);
                    z = true;
                }
                GLES20.glUniform4f(renderContext.curPrimProgram.vColor, ((float) (255 - ((i4 >> 0) & 255))) / 255.0f, ((float) (255 - ((i4 >> 8) & 255))) / 255.0f, ((float) (255 - ((i4 >> 16) & 255))) / 255.0f, ((float) (255 - ((i4 >> 24) & 255))) / 255.0f);
                renderContext.bindFaceTexture(drawableFaceTexture);
                GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, this.FaceUVMatrices, i3 * 16);
                drawableGeometry.GLDrawRiggedFace30(renderContext, i3);
            }
        }
        return i2;
    }

    public IntersectInfo IntersectRay(LLVector3 lLVector3, LLVector3 lLVector32) {
        IntersectInfo IntersectRay = this.volumeGeometry.IntersectRay(lLVector3, lLVector32);
        return (IntersectRay == null || !IntersectRay.faceKnown) ? IntersectRay : (!this.isSingleFace || this.singleFaceMatrix == null) ? (this.isSingleFace || this.FaceUVMatrices == null) ? IntersectRay : new IntersectInfo(IntersectRay, this.FaceUVMatrices, IntersectRay.faceID * 16) : new IntersectInfo(IntersectRay, this.singleFaceMatrix, 0);
    }

    public final boolean UpdateRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton) {
        return this.isRiggedMesh ? this.volumeGeometry.UpdateRigged(renderContext, avatarSkeleton) : false;
    }

    public boolean hasExtendedBones() {
        return this.volumeGeometry.hasExtendedBones();
    }

    public final boolean isRiggedMesh() {
        return this.isRiggedMesh;
    }
}
