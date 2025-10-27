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
    private val Int backgroundColor
    private val String hoverText
    private volatile GLLoadedTextTexture hoverTextTexture
    private val GLTextTextureCache textTextureCache
    private Boolean textureRequested = false

    public DrawableHoverText(GLTextTextureCache gLTextTextureCache, String str, Int i) {
        this.textTextureCache = gLTextTextureCache
        this.hoverText = str
        this.backgroundColor = i
    }

    val Unit DrawAtWorld(RenderContext renderContext, Float f, Float f2, Float f3, Float f4, MatrixStack matrixStack, Boolean z, Int i) {
        r0 = Float[8]
        val matrixData: FloatArray = renderContext.modelViewMatrix.getMatrixData()
        val matrixDataOffset: Int = renderContext.modelViewMatrix.getMatrixDataOffset()
        val matrixData2: FloatArray = matrixStack.getMatrixData()
        val matrixDataOffset2: Int = matrixStack.getMatrixDataOffset()
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
            val f5: Float = r0[0] / r0[3]
            val f6: Float = r0[1] / r0[3]
            if (r0[3] != 0.0f) {
                GLDraw(renderContext, f5, f6, r0[2] / r0[3], z, i)
            }
        }
    }

    fun GLCleanup() {
        if (this.textTextureCache != null) {
            this.textTextureCache.CancelRequest(this)
        }
        this.textureRequested = false
        this.hoverTextTexture = null
    }

    val Unit GLDraw(RenderContext renderContext, Float f, Float f2, Float f3, Boolean z, Int i) {
        if (!this.textureRequested) {
            this.textureRequested = true
            this.textTextureCache.RequestResource(DrawableTextParams.create(this.hoverText, this.backgroundColor), this)
        }
        val gLLoadedTextTexture: GLLoadedTextTexture = this.hoverTextTexture
        if (gLLoadedTextTexture != null) {
            val width: Float = (((Float) gLLoadedTextTexture.getWidth()) * 2.0f) / ((Float) renderContext.viewportRect[2])
            val height: Float = (((Float) gLLoadedTextTexture.getHeight()) * 2.0f) / ((Float) renderContext.viewportRect[3])
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

    fun OnResourceReady(obj: Object, z: Boolean) {
        if (obj instanceof GLLoadedTextTexture) {
            this.hoverTextTexture = (GLLoadedTextTexture) obj
        } else if (obj == null) {
            this.hoverTextTexture = null
        }
    }
}
