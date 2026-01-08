package com.lumiyaviewer.lumiya.render.avatar

import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.Matrix
import com.lumiyaviewer.lumiya.render.MatrixStack
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTextTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams

class DrawableHoverText(
    private val textTextureCache: GLTextTextureCache,
    private val hoverText: String,
    private val backgroundColor: Int
) : ResourceConsumer, GLCleanable {

    @Volatile
    private var hoverTextTexture: GLLoadedTextTexture? = null
    private var textureRequested: Boolean = false

    fun DrawAtWorld(
        renderContext: RenderContext,
        x: Float,
        y: Float,
        z: Float,
        offsetY: Float,
        projectionMatrix: FloatArray?, // This was confusing in decompiled code, treating as FloatArray for compatibility if needed, but logic used matrixStack
        colorize: Boolean,
        color: Int
    ) {
        // Fallback or alternative signature
        // If projectionMatrix is passed, use it. If not, logic assumes MatrixStack?
        // The decompiled code had "float[] fArr" which was used as projection matrix data.
        // Let's assume standard matrix operations.
        
        val mvMatrix = FloatArray(16)
        val matrixData = renderContext.modelViewMatrix.getMatrixData()
        val matrixDataOffset = renderContext.modelViewMatrix.getMatrixDataOffset()
        
        // Manual construction of translation vector? No, mvMatrix is result.
        // Decompiled logic:
        /*
        r0[0] = f; r0[1] = f2; r0[2] = f3; r0[3] = 1.0f;
        Matrix.multiplyMV(r0, 4, matrixData, matrixDataOffset, r0, 0);
        */
        // r0 is length 8 float array in decompiled code.
        
        val tempVec = FloatArray(8)
        tempVec[0] = x
        tempVec[1] = y
        tempVec[2] = z
        tempVec[3] = 1.0f
        
        Matrix.multiplyMV(tempVec, 4, matrixData, matrixDataOffset, tempVec, 0)
        
        tempVec[5] += offsetY
        
        if (renderContext.hasGL20) {
             System.arraycopy(tempVec, 4, mvMatrix, 0, 4)
        } else {
             if (projectionMatrix != null) {
                 Matrix.multiplyMV(mvMatrix, 0, projectionMatrix, 0, tempVec, 4)
             }
        }

        if (mvMatrix[3] != 0.0f) {
            val screenX = mvMatrix[0] / mvMatrix[3]
            val screenY = mvMatrix[1] / mvMatrix[3]
            val depth = mvMatrix[2] / mvMatrix[3] // Assuming this is what was intended
            
            GLDraw(renderContext, screenX, screenY, depth, colorize, color)
        }
    }

    override fun GLCleanup() {
        textTextureCache.CancelRequest(this)
        textureRequested = false
        hoverTextTexture = null
    }

    fun GLDraw(
        renderContext: RenderContext,
        x: Float,
        y: Float,
        z: Float,
        colorize: Boolean,
        color: Int
    ) {
        if (!textureRequested) {
            textureRequested = true
            textTextureCache.RequestResource(
                DrawableTextParams.create(hoverText, backgroundColor),
                this
            )
        }

        val texture = hoverTextTexture
        if (texture != null) {
            val width = (texture.width * 2.0f) / renderContext.viewportRect[2]
            val height = (texture.height * 2.0f) / renderContext.viewportRect[3]

            if (renderContext.hasGL20) {
                GLES20.glUniform3f(renderContext.quadProgram.uPreTranslate, x, y, z)
                GLES20.glUniform3f(renderContext.quadProgram.uScale, width, height, 1.0f)
                GLES20.glUniform3f(renderContext.quadProgram.uPostTranslate, 0.0f, texture.baselineOffset, 0.0f)
                
                texture.GLDraw()
                
                if (colorize) {
                    GLES20.glUniform4f(
                        renderContext.quadProgram.uColor,
                        ((color shr 0) and 255) / 255.0f,
                        ((color shr 8) and 255) / 255.0f,
                        ((color shr 16) and 255) / 255.0f,
                        (255 - ((color shr 24) and 255)) / 255.0f
                    )
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 1)
                } else {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, 1.0f, 1.0f, 1.0f, 1.0f)
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 0)
                }
            } else {
                GLES10.glLoadIdentity()
                GLES10.glTranslatef(x, y, z)
                GLES10.glScalef(width, height, 1.0f)
                GLES10.glTranslatef(0.0f, texture.baselineOffset, 0.0f)
                
                if (colorize) {
                    GLES10.glColor4f(
                        ((color shr 0) and 255) / 255.0f,
                        ((color shr 8) and 255) / 255.0f,
                        ((color shr 16) and 255) / 255.0f,
                        1.0f - (((color shr 24) and 255) / 255.0f)
                    )
                } else {
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                }
                
                texture.GLDraw()
            }
            renderContext.quad.DrawQuad(renderContext)
        }
    }

    override fun OnResourceReady(resource: Any?, isIntermediate: Boolean) {
        if (resource is GLLoadedTextTexture) {
            hoverTextTexture = resource
        } else if (resource == null) {
            hoverTextTexture = null
        }
    }
}
