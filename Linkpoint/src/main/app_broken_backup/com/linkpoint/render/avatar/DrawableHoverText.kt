package com.linkpoint.render.avatar

import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.Matrix
import com.linkpoint.render.MatrixStack
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLCleanable
import com.linkpoint.render.glres.textures.GLLoadedTextTexture
import com.linkpoint.render.glres.textures.GLTextTextureCache
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.text.DrawableTextParams

class DrawableHoverText : ResourceConsumer, GLCleanable {
    private Int backgroundColor
    private String hoverText
    private volatile GLLoadedTextTexture hoverTextTexture
    private GLTextTextureCache textTextureCache
    private var textureRequested: Boolean = false

    constructor(gLTextTextureCache: GLTextTextureCache, str: String, i: Int) {
        this.textTextureCache = gLTextTextureCache
        this.hoverText = str
        this.backgroundColor = i
    }

    fun DrawAtWorld(RenderContext renderContext, Float f, Float f2, Float f3, Float f4, MatrixStack matrixStack, Boolean z, Int i)  {
        r0 = FloatArray(8)
        FloatArray matrixData = renderContext.modelViewMatrix.getMatrixData()
        var matrixDataOffset: Int = renderContext.modelViewMatrix.getMatrixDataOffset()
        FloatArray matrixData2 = matrixStack.getMatrixData()
        var matrixDataOffset2: Int = matrixStack.getMatrixDataOffset()
        r0[0] = f
        r0[1] = f2
        r0[2] = f3
        r0[3] = 1065353216
        Matrix.multiplyMV(r0, 4, matrixData, matrixDataOffset, r0, 0)
        r0[5] = r0[5] + f4
        if (renderContext.hasGL20) {
            System.arraycopy(r0, 4, r0, 0, 4)
        } else {
            Matrix.multiplyMV(r0, 0, matrixData2, matrixDataOffset2, r0, 4)
        }
        if (r0[3] != 0.0f) {
            var f5: Float = r0[0] / r0[3]
            var f6: Float = r0[1] / r0[3]
            if (r0[3] != 0.0f) {
                GLDraw(renderContext, f5, f6, r0[2] / r0[3], z, i)
            }
        }
    }

    fun GLCleanup()  {
        if (this.textTextureCache != null) {
            this.textTextureCache.CancelRequest(this)
        }
        this.textureRequested = false
        this.hoverTextTexture = null
    }

    fun GLDraw(RenderContext renderContext, Float f, Float f2, Float f3, Boolean z, Int i)  {
        if (!this.textureRequested) {
            this.textureRequested = true
            this.textTextureCache.RequestResource(DrawableTextParams.create(this.hoverText, this.backgroundColor), this)
        }
        GLLoadedTextTexture gLLoadedTextTexture = this.hoverTextTexture
        if (gLLoadedTextTexture != null) {
            var width: Float = ((gLLoadedTextTexture.toFloat().getWidth()) * 2.0f) / (renderContext.toFloat().viewportRect[2])
            var height: Float = ((gLLoadedTextTexture.toFloat().getHeight()) * 2.0f) / (renderContext.toFloat().viewportRect[3])
            if (renderContext.hasGL20) {
                GLES20.glUniform3f(renderContext.quadProgram.uPreTranslate, f, f2, f3)
                GLES20.glUniform3f(renderContext.quadProgram.uScale, width, height, 1.0f)
                GLES20.glUniform3f(renderContext.quadProgram.uPostTranslate, 0.0f, gLLoadedTextTexture.baselineOffset, 0.0f)
                gLLoadedTextTexture.GLDraw()
                if (z) {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, ((Float) ((i >> 0) & 255)) / 255.0f, ((Float) ((i >> 8) & 255)) / 255.0f, ((Float) ((i >> 16) & 255)) / 255.0f, ((Float) (255 - ((i >> 24) & 255))) / 255.0f)
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 1)
                } else {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, 1.0f, 1.0f, 1.0f, 1.0f)
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 0)
                }
            } else {
                GLES10.glLoadIdentity()
                GLES10.glTranslatef(f, f2, f3)
                GLES10.glScalef(width, height, 1.0f)
                GLES10.glTranslatef(0.0f, gLLoadedTextTexture.baselineOffset, 0.0f)
                if (z) {
                    GLES10.glColor4f(((Float) ((i >> 0) & 255)) / 255.0f, ((Float) ((i >> 8) & 255)) / 255.0f, ((Float) ((i >> 16) & 255)) / 255.0f, 1.0f - (((Float) ((i >> 24) & 255)) / 255.0f))
                } else {
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                }
                gLLoadedTextTexture.GLDraw()
            }
            renderContext.quad.DrawQuad(renderContext)
        }
    }

    fun OnResourceReady(obj: Any, z: Boolean)  {
        if (obj is GLLoadedTextTexture) {
            this.hoverTextTexture = (GLLoadedTextTexture) obj
        } else if (obj == null) {
            this.hoverTextTexture = null
        }
    }
}
