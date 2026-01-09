package com.linkpoint.render.drawable

import android.annotation.TargetApi
import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.Matrix
import androidx.core.view.ViewCompat
import com.linkpoint.render.RenderContext
import com.linkpoint.render.avatar.AvatarSkeleton
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.render.picking.IntersectInfo
import com.linkpoint.render.shaders.FlexiPrimProgram
import com.linkpoint.render.shaders.PrimProgram
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.prims.PrimDrawParams
import com.linkpoint.slproto.prims.PrimFlexibleInfo
import com.linkpoint.slproto.textures.SLTextureEntry
import com.linkpoint.slproto.textures.SLTextureEntryFace
import com.linkpoint.slproto.types.LLVector3
import java.util.UUID

class DrawablePrim {
    val RENDER_PASS_ALL: Int = 3
    val RENDER_PASS_OPAQUE: Int = 1
    val RENDER_PASS_TRANSPARENT: Int = 2
    private IntArray FaceColorsIDs
    private Int FaceCount
    private DrawableFaceTexture[] FaceTextures
    private FloatArray FaceUVMatrices
    private var drawingTextureEnabled: Boolean? = null
    private var firstFace: Boolean? = null
    private Boolean isRiggedMesh
    private Boolean isSingleFace
    private Boolean riggingFitsGL20
    private Int singleFaceColor
    private FloatArray singleFaceMatrix
    private DrawableFaceTexture singleFaceTexture
    private DrawableGeometry volumeGeometry

    constructor(primDrawParams: PrimDrawParams, drawableGeometry: DrawableGeometry) {
        this.volumeGeometry = drawableGeometry
        var isFacesCombined: Boolean = drawableGeometry.isFacesCombined()
        this.isRiggedMesh = drawableGeometry.isRiggedMesh()
        this.riggingFitsGL20 = this.isRiggedMesh ? drawableGeometry.riggingFitsGL20() : false
        this.FaceCount = drawableGeometry.getFaceCount()
        SLTextureEntry textures = primDrawParams.getTextures()
        if (textures != null) {
            SLTextureEntryFace GetDefaultTexture = textures.GetDefaultTexture()
            if (!textures.isSingleFace() || !isFacesCombined) {
                this.isSingleFace = false
                this.singleFaceColor = 0
                this.singleFaceTexture = null
                this.singleFaceMatrix = null
                this.FaceColorsIDs = Int[(this.FaceCount * 2)]
                this.FaceTextures = DrawableFaceTexture[this.FaceCount]
                this.FaceUVMatrices = Float[(this.FaceCount * 16)]
                var i: Int = 0
                for (i2 in 0 until this.FaceCount) {
                    SLTextureEntryFace GetFace = textures.GetFace(drawableGeometry.getFaceID(i2))
                    if (GetFace != null) {
                        this.FaceColorsIDs[i] = GetFace.getRGBA(GetDefaultTexture)
                        this.FaceColorsIDs[i + 1] = 0
                        UUID textureID = GetFace.getTextureID(GetDefaultTexture)
                        if (textureID != null) {
                            this.FaceTextures[i2] = DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim))
                        }
                        initFaceUVMatrix(GetDefaultTexture, GetFace, this.FaceUVMatrices, i2 * 16)
                    }
                    i += 2
                }
                return
            }
            this.isSingleFace = true
            this.singleFaceMatrix = FloatArray(16)
            SLTextureEntryFace GetFace2 = textures.GetFace(0)
            if (GetFace2 != null) {
                this.singleFaceColor = GetFace2.getRGBA(GetDefaultTexture)
                UUID textureID2 = GetFace2.getTextureID(GetDefaultTexture)
                if (textureID2 != null) {
                    this.singleFaceTexture = DrawableFaceTexture(DrawableTextureParams.create(textureID2, TextureClass.Prim))
                } else {
                    this.singleFaceTexture = null
                }
                initFaceUVMatrix(GetDefaultTexture, GetFace2, this.singleFaceMatrix, 0)
            } else {
                this.singleFaceColor = 0
                this.singleFaceTexture = null
            }
            this.FaceColorsIDs = null
            this.FaceTextures = null
            this.FaceUVMatrices = null
            return
        }
        this.isSingleFace = false
        this.singleFaceColor = 0
        this.singleFaceTexture = null
        this.singleFaceMatrix = null
        this.FaceColorsIDs = Int[(this.FaceCount * 2)]
        this.FaceTextures = DrawableFaceTexture[this.FaceCount]
        this.FaceUVMatrices = Float[(this.FaceCount * 16)]
    }

    private fun DrawFace(renderContext: RenderContext, drawableGeometry: DrawableGeometry, gLLoadableBuffer: GLLoadableBuffer, z: Boolean, i: Int, i2: Int, drawableFaceTexture: DrawableFaceTexture, fArr: FloatArray, i3: Int, i4: Int): Int {
        var faceRenderMask: Int = getFaceRenderMask(i2, drawableFaceTexture)
        if ((faceRenderMask & i4) == 0) {
            return faceRenderMask
        }
        var z2: Boolean = false
        if (!z) {
            if (renderContext.hasGL20) {
                GLES20.glUniform4f(renderContext.curPrimProgram.vColor, ((Float) (255 - ((i2 >> 0) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 8) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 16) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 24) & 255))) / 255.0f)
            } else {
                GLES10.glColor4f(((Float) (255 - ((i2 >> 0) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 8) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 16) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 24) & 255))) / 255.0f)
            }
            if (drawableFaceTexture != null && drawableFaceTexture.GLDraw(renderContext)) {
                z2 = true
            }
        } else if (renderContext.hasGL20) {
            GLES20.glUniform4f(renderContext.curPrimProgram.vColor, 1.0f, 0.0f, 0.0f, 0.6f)
        } else {
            GLES10.glColor4f(1.0f, 0.0f, 0.0f, 0.6f)
        }
        if (z2 != this.drawingTextureEnabled || this.firstFace) {
            if (renderContext.hasGL20) {
                if (!z2) {
                    GLES20.glBindTexture(3553, 0)
                    renderContext.curPrimProgram.setTextureEnabled(false)
                } else {
                    renderContext.curPrimProgram.setTextureEnabled(true)
                }
            } else if (z2) {
                GLES10.glEnable(3553)
                GLES10.glEnableClientState(32888)
            } else {
                GLES10.glDisable(3553)
                GLES10.glDisableClientState(32888)
            }
            this.drawingTextureEnabled = z2
            this.firstFace = false
        }
        if (renderContext.hasGL20) {
            GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, fArr, i3)
            if (i == -1) {
                drawableGeometry.GLDrawAll20(renderContext)
            } else {
                drawableGeometry.GLDrawFace20(renderContext, i)
            }
        } else {
            GLES11.glMatrixMode(5890)
            GLES11.glPushMatrix()
            GLES11.glLoadMatrixf(fArr, i3)
            if (i == -1) {
                drawableGeometry.GLDrawAll10(renderContext)
            } else {
                drawableGeometry.GLDrawFace10(renderContext, i, gLLoadableBuffer)
            }
            GLES11.glPopMatrix()
            GLES11.glMatrixMode(5888)
        }
        return faceRenderMask
    }

    private fun DrawFaceFast20(renderContext: RenderContext, drawableGeometry: DrawableGeometry, i: Int, i2: Int, drawableFaceTexture: DrawableFaceTexture, fArr: FloatArray, i3: Int, i4: Int): Int {
        var faceRenderMask: Int = getFaceRenderMask(i2, drawableFaceTexture)
        if ((faceRenderMask & i4) == 0) {
            return faceRenderMask
        }
        GLES20.glUniform4f(renderContext.curPrimProgram.vColor, ((Float) (255 - ((i2 >> 0) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 8) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 16) & 255))) / 255.0f, ((Float) (255 - ((i2 >> 24) & 255))) / 255.0f)
        renderContext.bindFaceTexture(drawableFaceTexture)
        GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, fArr, i3)
        if (i == -1) {
            drawableGeometry.GLDrawAll20(renderContext)
        } else {
            drawableGeometry.GLDrawFace20(renderContext, i)
        }
        return faceRenderMask
    }

    private fun getFaceRenderMask(i: Int, drawableFaceTexture: DrawableFaceTexture): Int {
        var z: Boolean = false
        if ((i & ViewCompat.MEASURED_STATE_MASK) == -16777216) {
            return 0
        }
        if ((i & ViewCompat.MEASURED_STATE_MASK) != 0) {
            z = true
        }
        if (!z && drawableFaceTexture != null) {
            z = drawableFaceTexture.hasAlphaLayer()
        }
        return z ? 2 : 1
    }

    private fun initFaceUVMatrix(sLTextureEntryFace: SLTextureEntryFace, sLTextureEntryFace2: SLTextureEntryFace, fArr: FloatArray, i: Int)  {
        FloatArray fArr2 = FloatArray(16)
        Matrix.setIdentityM(fArr2, 0)
        Matrix.translateM(fArr2, 0, sLTextureEntryFace2.getOffsetU(sLTextureEntryFace) + 0.5f, sLTextureEntryFace2.getOffsetV(sLTextureEntryFace) + 0.5f, 0.0f)
        Matrix.scaleM(fArr2, 0, sLTextureEntryFace2.getRepeatU(sLTextureEntryFace), sLTextureEntryFace2.getRepeatV(sLTextureEntryFace), 1.0f)
        Matrix.rotateM(fArr, i, fArr2, 0, sLTextureEntryFace2.getRotation(sLTextureEntryFace) / 0.017453292f, 0.0f, 0.0f, -1.0f)
        Matrix.translateM(fArr, i, -0.5f, -0.5f, 0.0f)
    }

    fun ApplyJointTranslations(MeshJointTranslations meshJointTranslations)  {
        if (this.isRiggedMesh) {
            this.volumeGeometry.ApplyJointTranslations(meshJointTranslations)
        }
    }

    fun Draw(RenderContext renderContext, Boolean z, PrimFlexibleInfo primFlexibleInfo, Int i): Int {
        GLLoadableBuffer GLBindBuffers10
        DrawableGeometry drawableGeometry = this.volumeGeometry
        this.firstFace = true
        if (renderContext.hasGL20) {
            FloatArray matrices = primFlexibleInfo != null ? primFlexibleInfo.getMatrices() : null
            renderContext.curPrimProgram = (!this.isRiggedMesh || !this.riggingFitsGL20) ? matrices != null ? renderContext.flexiPrimProgram : renderContext.primProgram : renderContext.riggedMeshProgram
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle())
            renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix)
            renderContext.glObjWorldApplyMatrix(renderContext.curPrimProgram.uObjWorldMatrix)
            renderContext.glObjScaleApplyVector(renderContext.curPrimProgram.uObjCoordScale)
            if (matrices != null && (renderContext.curPrimProgram is FlexiPrimProgram)) {
                FlexiPrimProgram flexiPrimProgram = (renderContext as FlexiPrimProgram).curPrimProgram
                GLES20.glUniform1i(flexiPrimProgram.uNumSectionMatrices, matrices.size / 16)
                GLES20.glUniformMatrix4fv(flexiPrimProgram.uSectionMatrices, matrices.size / 16, false, matrices, 0)
            }
            GLBindBuffers10 = drawableGeometry.GLBindBuffers20(renderContext)
        } else {
            GLBindBuffers10 = drawableGeometry.GLBindBuffers10(renderContext, primFlexibleInfo)
        }
        this.drawingTextureEnabled = false
        if (this.isSingleFace) {
            return DrawFace(renderContext, drawableGeometry, GLBindBuffers10, z, -1, this.singleFaceColor, this.singleFaceTexture, this.singleFaceMatrix, 0, i)
        }
        var i2: Int = 0
        var i3: Int = 0
        while (true) {
            var i4: Int = i2
            if (i3 >= this.FaceCount) {
                return i4
            }
            i2 = DrawFace(renderContext, drawableGeometry, GLBindBuffers10, z, i3, this.FaceColorsIDs[i3 * 2], this.FaceTextures[i3], this.FaceUVMatrices, i3 * 16, i) | i4
            i3++
        }
    }

    fun DrawFast20(RenderContext renderContext, Boolean z, PrimFlexibleInfo primFlexibleInfo, Int i): Int {
        var z2: Boolean = true
        FloatArray fArr = null
        var i2: Int = 0
        DrawableGeometry drawableGeometry = this.volumeGeometry
        if (primFlexibleInfo != null) {
            fArr = primFlexibleInfo.getMatrices()
        }
        if (i != 1) {
            z2 = false
        }
        PrimProgram primProgram = (!this.isRiggedMesh || !this.riggingFitsGL20) ? fArr != null ? z2 ? renderContext.flexiPrimOpaqueProgram : renderContext.flexiPrimProgram : z2 ? renderContext.primOpaqueProgram : renderContext.primProgram : renderContext.riggedMeshProgram
        if (renderContext.curPrimProgram != primProgram) {
            renderContext.curPrimProgram = primProgram
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle())
            renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix)
        }
        renderContext.glObjWorldApplyMatrix(renderContext.curPrimProgram.uObjWorldMatrix)
        renderContext.glObjScaleApplyVector(renderContext.curPrimProgram.uObjCoordScale)
        if (fArr != null) {
            GLES20.glUniform1i(renderContext.flexiPrimProgram.uNumSectionMatrices, fArr.size / 16)
            GLES20.glUniformMatrix4fv(renderContext.flexiPrimProgram.uSectionMatrices, fArr.size / 16, false, fArr, 0)
        }
        drawableGeometry.GLBindBuffers20(renderContext)
        if (this.isSingleFace) {
            return DrawFaceFast20(renderContext, drawableGeometry, -1, this.singleFaceColor, this.singleFaceTexture, this.singleFaceMatrix, 0, i)
        }
        var i3: Int = 0
        while (true) {
            var i4: Int = i2
            if (i3 >= this.FaceCount) {
                return i4
            }
            i2 = i4 | DrawFaceFast20(renderContext, drawableGeometry, i3, this.FaceColorsIDs[i3 * 2], this.FaceTextures[i3], this.FaceUVMatrices, i3 * 16, i)
            i3++
        }
    }

    @TargetApi(18)
    fun DrawRigged30(RenderContext renderContext, Int i): Int {
        DrawableGeometry drawableGeometry = this.volumeGeometry
        var z: Boolean = false
        var i2: Int = 0
        for (i3 in 0 until this.FaceCount) {
            var i4: Int = this.FaceColorsIDs[i3 * 2]
            DrawableFaceTexture drawableFaceTexture = this.FaceTextures[i3]
            var faceRenderMask: Int = getFaceRenderMask(i4, drawableFaceTexture)
            i2 |= faceRenderMask
            if ((faceRenderMask & i) != 0) {
                if (!z) {
                    drawableGeometry.GLBindBuffersRigged30(renderContext)
                    z = true
                }
                GLES20.glUniform4f(renderContext.curPrimProgram.vColor, ((Float) (255 - ((i4 >> 0) & 255))) / 255.0f, ((Float) (255 - ((i4 >> 8) & 255))) / 255.0f, ((Float) (255 - ((i4 >> 16) & 255))) / 255.0f, ((Float) (255 - ((i4 >> 24) & 255))) / 255.0f)
                renderContext.bindFaceTexture(drawableFaceTexture)
                GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, this.FaceUVMatrices, i3 * 16)
                drawableGeometry.GLDrawRiggedFace30(renderContext, i3)
            }
        }
        return i2
    }

    fun IntersectRay(lLVector3: LLVector3, lLVector32: LLVector3): IntersectInfo {
        IntersectInfo IntersectRay = this.volumeGeometry.IntersectRay(lLVector3, lLVector32)
        return (IntersectRay == null || !IntersectRay.faceKnown) ? IntersectRay : (!this.isSingleFace || this.singleFaceMatrix == null) ? (this.isSingleFace || this.FaceUVMatrices == null) ? IntersectRay : IntersectInfo(IntersectRay, this.FaceUVMatrices, IntersectRay.faceID * 16) : IntersectInfo(IntersectRay, this.singleFaceMatrix, 0)
    }

    fun UpdateRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton): Boolean {
        if (this.isRiggedMesh) {
            return this.volumeGeometry.UpdateRigged(renderContext, avatarSkeleton)
        }
        return false
    }

    fun hasExtendedBones(): Boolean {
        return this.volumeGeometry.hasExtendedBones()
    }

    fun isRiggedMesh(): Boolean {
        return this.isRiggedMesh
    }
}
