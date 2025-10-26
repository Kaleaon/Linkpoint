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

    Unit DrawAtWorld(RenderContext renderContext, Float f, Float f2, Float f3, Float f4, MatrixStack matrixStack, Boolean z, Int i) {
        r0 = FloatArray(8)
        FloatArray matrixData = renderContext.modelViewMatrix.getMatrixData()
        Int matrixDataOffset = renderContext.modelViewMatrix.getMatrixDataOffset()
        FloatArray matrixData2 = matrixStack.getMatrixData()
        Int matrixDataOffset2 = matrixStack.getMatrixDataOffset()
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
            Float f5 = r0[0] / r0[3]
            Float f6 = r0[1] / r0[3]
            if (r0[3] != 0.0f) {
                GLDraw(renderContext, f5, f6, r0[2] / r0[3], z, i)
            }
        }
    }

    fun GLCleanup(): Unit {
        if (this.textTextureCache != null) {
            this.textTextureCache.CancelRequest(this)
        }
        this.textureRequested = false
        this.hoverTextTexture = null
    }

    Unit GLDraw(RenderContext renderContext, Float f, Float f2, Float f3, Boolean z, Int i) {
        if (!this.textureRequested) {
            this.textureRequested = true
            this.textTextureCache.RequestResource(DrawableTextParams.create(this.hoverText, this.backgroundColor), this)
        }
        GLLoadedTextTexture gLLoadedTextTexture = this.hoverTextTexture
        if (gLLoadedTextTexture != null) {
            Float width = (((Float) gLLoadedTextTexture.getWidth()) * 2.0f) / ((Float) renderContext.viewportRect[2])
            Float height = (((Float) gLLoadedTextTexture.getHeight()) * 2.0f) / ((Float) renderContext.viewportRect[3])
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

    fun OnResourceReady(obj: Any, z: Boolean): Unit {
        if (obj instanceof GLLoadedTextTexture) {
            this.hoverTextTexture = (GLLoadedTextTexture) obj
        } else if (obj == null) {
            this.hoverTextTexture = null
        }
    }
}
