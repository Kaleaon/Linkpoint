package com.lumiyaviewer.lumiya.render.drawable

import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.Matrix
import androidx.core.view.ViewCompat
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace

class DrawablePrim(
    primDrawParams: PrimDrawParams,
    private val volumeGeometry: DrawableGeometry
) {
    val RENDER_PASS_ALL = 3
    val RENDER_PASS_OPAQUE = 1
    val RENDER_PASS_TRANSPARENT = 2
    
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

    init {
        val isFacesCombined = volumeGeometry.isFacesCombined()
        this.isRiggedMesh = volumeGeometry.isRiggedMesh()
        this.riggingFitsGL20 = if (this.isRiggedMesh) volumeGeometry.riggingFitsGL20() else false
        this.FaceCount = volumeGeometry.getFaceCount()
        
        val textures = primDrawParams.getTextures()
        
        if (textures != null) {
            val defaultTexture = textures.GetDefaultTexture()
            
            // Simplified logic for initialization
            if (!textures.isSingleFace() || !isFacesCombined) {
                this.isSingleFace = false
                this.FaceColorsIDs = IntArray(FaceCount * 2)
                this.FaceTextures = arrayOfNulls(FaceCount)
                this.FaceUVMatrices = FloatArray(FaceCount * 16)
                
                for (i in 0 until FaceCount) {
                    val face = textures.GetFace(volumeGeometry.getFaceID(i))
                    if (face != null) {
                        FaceColorsIDs!![i * 2] = face.getRGBA(defaultTexture)
                        FaceColorsIDs!![i * 2 + 1] = 0
                        val textureID = face.getTextureID(defaultTexture)
                        if (textureID != null) {
                            FaceTextures!![i] = DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim))
                        }
                        FaceUVMatrices?.let {
                            initFaceUVMatrix(defaultTexture, face, it, i * 16)
                        }
                    }
                }
            } else {
                this.isSingleFace = true
                this.singleFaceMatrix = FloatArray(16)
                val face = textures.GetFace(0)
                if (face != null) {
                    this.singleFaceColor = face.getRGBA(defaultTexture)
                    val textureID = face.getTextureID(defaultTexture)
                    if (textureID != null) {
                        this.singleFaceTexture = DrawableFaceTexture(DrawableTextureParams.create(textureID, TextureClass.Prim))
                    }
                    singleFaceMatrix?.let {
                        initFaceUVMatrix(defaultTexture, face, it, 0)
                    }
                }
            }
        }
    }

    private fun getFaceRenderMask(color: Int, faceTexture: DrawableFaceTexture?): Int {
        var transparent = false
        if ((color and ViewCompat.MEASURED_STATE_MASK) == -16777216) { 
             return 0
        }
        
        if ((color and ViewCompat.MEASURED_STATE_MASK) != 0) {
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
        System.arraycopy(tempMatrix, 0, matrix, offset, 16)
    }

    fun ApplyJointTranslations(translations: MeshJointTranslations) {
        if (isRiggedMesh) {
            volumeGeometry.ApplyJointTranslations(translations)
        }
    }

    fun Draw(renderContext: RenderContext, textured: Boolean, flexibleInfo: PrimFlexibleInfo?, passMask: Int): Int {
        // Stub implementation
        return 0
    }
    
    fun hasExtendedBones(): Boolean = volumeGeometry.hasExtendedBones()
    fun isRiggedMesh(): Boolean = isRiggedMesh
}
