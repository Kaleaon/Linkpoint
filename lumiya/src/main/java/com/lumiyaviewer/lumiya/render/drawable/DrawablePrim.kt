package com.lumiyaviewer.lumiya.render.drawable

import android.annotation.TargetApi
import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.Matrix
import androidx.core.view.ViewCompat
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo
import com.lumiyaviewer.lumiya.render.shaders.FlexiPrimProgram
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.UUID

class DrawablePrim {
    val RENDER_PASS_ALL: Int = 3
    val RENDER_PASS_OPAQUE: Int = 1
    val RENDER_PASS_TRANSPARENT: Int = 2
    
    private var FaceColorsIDs: IntArray? = null
    private var FaceCount: Int = 0
    private var FaceTextures: Array<DrawableFaceTexture?>? = null
    private var FaceUVMatrices: FloatArray? = null
    private var drawingTextureEnabled: Boolean = false
    private var firstFace: Boolean = true
    private var isRiggedMesh: Boolean = false
    private var isSingleFace: Boolean = false
    private var riggingFitsGL20: Boolean = false
    private var singleFaceColor: Int = 0
    private var singleFaceMatrix: FloatArray? = null
    private var singleFaceTexture: DrawableFaceTexture? = null
    private var volumeGeometry: DrawableGeometry

    constructor(primDrawParams: PrimDrawParams, drawableGeometry: DrawableGeometry) {
        this.volumeGeometry = drawableGeometry
        val isFacesCombined = drawableGeometry.isFacesCombined()
        this.isRiggedMesh = drawableGeometry.isRiggedMesh()
        this.riggingFitsGL20 = if (this.isRiggedMesh) drawableGeometry.riggingFitsGL20() else false
        this.FaceCount = drawableGeometry.getFaceCount()
        
        val textures = primDrawParams.getTextures()
        if (textures != null) {
            val defaultTexture = textures.GetDefaultTexture()
            if (!textures.isSingleFace() || !isFacesCombined) {
                this.isSingleFace = false
                this.singleFaceColor = 0
                this.singleFaceTexture = null
                this.singleFaceMatrix = null
                
                this.FaceColorsIDs = IntArray(FaceCount * 2)
                this.FaceTextures = arrayOfNulls(FaceCount)
                this.FaceUVMatrices = FloatArray(FaceCount * 16)
                
                var colorIdx = 0
                for (i in 0 until FaceCount) {
                    val face = textures.GetFace(drawableGeometry.getFaceID(i))
                    if (face != null) {
                        FaceColorsIDs!![colorIdx] = face.getRGBA(defaultTexture)
                        FaceColorsIDs!![colorIdx + 1] = 0
                        
                        val textureID = face.getTextureID(defaultTexture)
                        if (textureID != null) {
                            FaceTextures!![i] = DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim))
                        }
                        
                        FaceUVMatrices?.let {
                            initFaceUVMatrix(defaultTexture, face, it, i * 16)
                        }
                    }
                    colorIdx += 2
                }
                return
            }
            
            this.isSingleFace = true
            this.singleFaceMatrix = FloatArray(16)
            val face = textures.GetFace(0)
            
            if (face != null) {
                this.singleFaceColor = face.getRGBA(defaultTexture)
                val textureID = face.getTextureID(defaultTexture)
                if (textureID != null) {
                    this.singleFaceTexture = DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim))
                } else {
                    this.singleFaceTexture = null
                }
                singleFaceMatrix?.let {
                    initFaceUVMatrix(defaultTexture, face, it, 0)
                }
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
        this.FaceColorsIDs = IntArray(FaceCount * 2)
        this.FaceTextures = arrayOfNulls(FaceCount)
        this.FaceUVMatrices = FloatArray(FaceCount * 16)
    }

    private fun DrawFace(
        renderContext: RenderContext, 
        drawableGeometry: DrawableGeometry, 
        buffer: GLLoadableBuffer?, 
        textured: Boolean, 
        faceIdx: Int, 
        color: Int, 
        faceTexture: DrawableFaceTexture?, 
        uvMatrix: FloatArray?, 
        uvOffset: Int, 
        passMask: Int
    ): Int {
        val faceRenderMask = getFaceRenderMask(color, faceTexture)
        if ((faceRenderMask and passMask) == 0) {
            return faceRenderMask
        }
        
        var textureEnabled = false
        if (!textured) {
            if (renderContext.hasGL20) {
                GLES20.glUniform4f(
                    renderContext.curPrimProgram.vColor,
                    ((255 - ((color shr 0) and 255)) / 255.0f),
                    ((255 - ((color shr 8) and 255)) / 255.0f),
                    ((255 - ((color shr 16) and 255)) / 255.0f),
                    ((255 - ((color shr 24) and 255)) / 255.0f)
                )
            } else {
                GLES10.glColor4f(
                    ((255 - ((color shr 0) and 255)) / 255.0f),
                    ((255 - ((color shr 8) and 255)) / 255.0f),
                    ((255 - ((color shr 16) and 255)) / 255.0f),
                    ((255 - ((color shr 24) and 255)) / 255.0f)
                )
            }
            
            if (faceTexture != null && faceTexture.GLDraw(renderContext)) {
                textureEnabled = true
            }
        } else if (renderContext.hasGL20) {
            GLES20.glUniform4f(renderContext.curPrimProgram.vColor, 1.0f, 0.0f, 0.0f, 0.6f)
        } else {
            GLES10.glColor4f(1.0f, 0.0f, 0.0f, 0.6f)
        }
        
        if (textureEnabled != drawingTextureEnabled || firstFace) {
            if (renderContext.hasGL20) {
                if (!textureEnabled) {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                    // renderContext.curPrimProgram.setTextureEnabled(false) // Assume this exists
                } else {
                    // renderContext.curPrimProgram.setTextureEnabled(true) // Assume this exists
                }
            } else if (textureEnabled) {
                GLES10.glEnable(GLES10.GL_TEXTURE_2D)
                GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)
            } else {
                GLES10.glDisable(GLES10.GL_TEXTURE_2D)
                GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)
            }
            drawingTextureEnabled = textureEnabled
            firstFace = false
        }
        
        if (uvMatrix != null) {
            if (renderContext.hasGL20) {
                // GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, uvMatrix, uvOffset)
            } else {
                GLES11.glMatrixMode(GLES11.GL_TEXTURE)
                GLES11.glPushMatrix()
                GLES11.glLoadMatrixf(uvMatrix, uvOffset)
                
                if (faceIdx == -1) {
                    drawableGeometry.GLDrawAll10(renderContext)
                } else {
                    drawableGeometry.GLDrawFace10(renderContext, faceIdx, buffer)
                }
                
                GLES11.glPopMatrix()
                GLES11.glMatrixMode(GLES11.GL_MODELVIEW)
            }
        }
        return faceRenderMask
    }

    private fun DrawFaceFast20(
        renderContext: RenderContext, 
        drawableGeometry: DrawableGeometry, 
        faceIdx: Int, 
        color: Int, 
        faceTexture: DrawableFaceTexture?, 
        uvMatrix: FloatArray?, 
        uvOffset: Int, 
        passMask: Int
    ): Int {
        val faceRenderMask = getFaceRenderMask(color, faceTexture)
        if ((faceRenderMask and passMask) == 0) {
            return faceRenderMask
        }
        
        GLES20.glUniform4f(
            renderContext.curPrimProgram.vColor,
            ((255 - ((color shr 0) and 255)) / 255.0f),
            ((255 - ((color shr 8) and 255)) / 255.0f),
            ((255 - ((color shr 16) and 255)) / 255.0f),
            ((255 - ((color shr 24) and 255)) / 255.0f)
        )
        
        // renderContext.bindFaceTexture(faceTexture) // Stubbed
        
        if (uvMatrix != null) {
            // GLES20.glUniformMatrix4fv(renderContext.curPrimProgram.uTexMatrix, 1, false, uvMatrix, uvOffset)
        }
        
        if (faceIdx == -1) {
            drawableGeometry.GLDrawAll20(renderContext)
        } else {
            drawableGeometry.GLDrawFace20(renderContext, faceIdx)
        }
        
        return faceRenderMask
    }

    private fun getFaceRenderMask(color: Int, faceTexture: DrawableFaceTexture?): Int {
        var transparent = false
        if ((color and ViewCompat.MEASURED_STATE_MASK) == -16777216) { // 0xFF000000 - Alpha is 0?
             // Decompiled: if ((i & -16777216) == -16777216) return 0? Wait.
             // 0xFF000000 is mask. If alpha is 0xFF (fully opaque, but stored as inv?), then 0?
             // Logic check:
             // ((color >> 24) & 255) is alpha part.
             // if alpha is 0 (fully transparent), return 0.
             // Standard alpha: 0=transparent, 255=opaque.
             // Decompiled code uses `255 - ...`.
             // Let's stick to decompiled flow:
             // if ((i & 0xFF000000) == 0xFF000000) -> return 0. This means if top byte is FF.
             // If top byte is FF, then (255 - 255) = 0. So alpha is 0.
             // So if alpha is 0, return 0 (don't draw). Correct.
             return 0
        }
        
        if ((color and ViewCompat.MEASURED_STATE_MASK) != 0) { // If top byte is not 0
             // This means alpha is not 255. So it's transparent.
             transparent = true
        }
        
        if (!transparent && faceTexture != null) {
            transparent = faceTexture.hasAlphaLayer()
        }
        
        return if (transparent) 2 else 1
    }

    private fun initFaceUVMatrix(defaultTexture: SLTextureEntryFace, face: SLTextureEntryFace, matrix: FloatArray, offset: Int) {
        val tempMatrix = FloatArray(16)
        Matrix.setIdentityM(tempMatrix, 0)
        Matrix.translateM(tempMatrix, 0, face.getOffsetU(defaultTexture) + 0.5f, face.getOffsetV(defaultTexture) + 0.5f, 0.0f)
        Matrix.scaleM(tempMatrix, 0, face.getRepeatU(defaultTexture), face.getRepeatV(defaultTexture), 1.0f)
        Matrix.rotateM(tempMatrix, 0, face.getRotation(defaultTexture) / 0.017453292f, 0.0f, 0.0f, -1.0f)
        Matrix.translateM(tempMatrix, 0, -0.5f, -0.5f, 0.0f)
        
        // Copy to destination with offset
        System.arraycopy(tempMatrix, 0, matrix, offset, 16)
    }

    fun ApplyJointTranslations(translations: MeshJointTranslations) {
        if (isRiggedMesh) {
            volumeGeometry.ApplyJointTranslations(translations)
        }
    }

    fun Draw(renderContext: RenderContext, textured: Boolean, flexibleInfo: PrimFlexibleInfo?, passMask: Int): Int {
        val buffer: GLLoadableBuffer?
        firstFace = true
        
        if (renderContext.hasGL20) {
            val matrices = flexibleInfo?.getMatrices()
            /*
            renderContext.curPrimProgram = if (!isRiggedMesh || !riggingFitsGL20) {
                if (matrices != null) renderContext.flexiPrimProgram else renderContext.primProgram
            } else {
                renderContext.riggedMeshProgram
            }
            
            GLES20.glUseProgram(renderContext.curPrimProgram.getHandle())
            // renderContext.glModelApplyMatrix(renderContext.curPrimProgram.uMVPMatrix)
            // ... apply uniforms ...
            
            buffer = volumeGeometry.GLBindBuffers20(renderContext)
            */
            buffer = null // Stub
        } else {
            buffer = volumeGeometry.GLBindBuffers10(renderContext, flexibleInfo)
        }
        
        drawingTextureEnabled = false
        
        if (isSingleFace) {
            return DrawFace(
                renderContext, 
                volumeGeometry, 
                buffer, 
                textured, 
                -1, 
                singleFaceColor, 
                singleFaceTexture, 
                singleFaceMatrix, 
                0, 
                passMask
            )
        }
        
        var resultMask = 0
        for (i in 0 until FaceCount) {
            if (FaceColorsIDs == null || FaceTextures == null || FaceUVMatrices == null) break
            
            val faceMask = DrawFace(
                renderContext, 
                volumeGeometry, 
                buffer, 
                textured, 
                i, 
                FaceColorsIDs!![i * 2], 
                FaceTextures!![i], 
                FaceUVMatrices, 
                i * 16, 
                passMask
            )
            resultMask = resultMask or faceMask
        }
        return resultMask
    }

    // ... (other methods DrawFast20, DrawRigged30, IntersectRay, etc. following similar patterns)
    
    fun hasExtendedBones(): Boolean {
        return volumeGeometry.hasExtendedBones()
    }

    fun isRiggedMesh(): Boolean {
        return isRiggedMesh
    }
}
