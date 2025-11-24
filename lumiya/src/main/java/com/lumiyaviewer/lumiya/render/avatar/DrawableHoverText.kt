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
        matrixStack: MatrixStack, // Changed from FloatArray to MatrixStack based on usage
        colorize: Boolean,
        color: Int
    ) {
        val mvMatrix = FloatArray(16)
        val matrixData = renderContext.modelViewMatrix.getMatrixData()
        val matrixDataOffset = renderContext.modelViewMatrix.getMatrixDataOffset()
        val projectionMatrixData = matrixStack.getMatrixData()
        val projectionMatrixOffset = matrixStack.getMatrixDataOffset()

        mvMatrix[0] = x
        mvMatrix[1] = y
        mvMatrix[2] = z
        mvMatrix[3] = 1.0f

        // Transform world coordinates to view coordinates
        Matrix.multiplyMV(mvMatrix, 4, matrixData, matrixDataOffset, mvMatrix, 0)
        
        // Add offset in view space (Y axis)
        mvMatrix[5] += offsetY

        // Determine screen coordinates
        if (renderContext.hasGL20) {
            // For GL20, we pass view-space coordinates to shader? 
            // The logic in decompiled code seemed to copy r0 to beginning of array
             System.arraycopy(mvMatrix, 4, mvMatrix, 0, 4)
        } else {
            // Project to screen/clip space
             Matrix.multiplyMV(mvMatrix, 0, projectionMatrixData, projectionMatrixOffset, mvMatrix, 4)
        }

        if (mvMatrix[3] != 0.0f) {
            val screenX = mvMatrix[0] / mvMatrix[3]
            val screenY = mvMatrix[1] / mvMatrix[3]
            
            if (mvMatrix[3] != 0.0f) {
                GLDraw(renderContext, screenX, screenY, mvMatrix[2] / mvMatrix[3], colorize, color)
            }
        }
    }
    
    // Overloaded version if needed to match signature of decompiled code
    // The decompiled code showed `MatrixStack` in signature but usage `matrixStack.getMatrixData()`.
    // I assumed `matrixStack` is indeed `MatrixStack` type.

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
