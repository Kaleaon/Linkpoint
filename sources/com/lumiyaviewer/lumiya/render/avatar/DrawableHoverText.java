package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTextTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;

public class DrawableHoverText implements ResourceConsumer, GLCleanable {
    private final int backgroundColor;
    private final String hoverText;
    private volatile GLLoadedTextTexture hoverTextTexture;
    private final GLTextTextureCache textTextureCache;
    private boolean textureRequested = false;

    public DrawableHoverText(GLTextTextureCache gLTextTextureCache, String str, int i) {
        this.textTextureCache = gLTextTextureCache;
        this.hoverText = str;
        this.backgroundColor = i;
    }

    public final void DrawAtWorld(RenderContext renderContext, float f, float f2, float f3, float f4, MatrixStack matrixStack, boolean z, int i) {
        float[] fArr = new float[8];
        float[] matrixData = renderContext.modelViewMatrix.getMatrixData();
        int matrixDataOffset = renderContext.modelViewMatrix.getMatrixDataOffset();
        float[] matrixData2 = matrixStack.getMatrixData();
        int matrixDataOffset2 = matrixStack.getMatrixDataOffset();
        fArr[0] = f;
        fArr[1] = f2;
        fArr[2] = f3;
        fArr[3] = 1.0f;
        Matrix.multiplyMV(fArr, 4, matrixData, matrixDataOffset, fArr, 0);
        fArr[5] = fArr[5] + f4;
        if (renderContext.hasGL20) {
            System.arraycopy(fArr, 4, fArr, 0, 4);
        } else {
            Matrix.multiplyMV(fArr, 0, matrixData2, matrixDataOffset2, fArr, 4);
        }
        if (fArr[3] != 0.0f) {
            float f5 = fArr[0] / fArr[3];
            float f6 = fArr[1] / fArr[3];
            if (fArr[3] != 0.0f) {
                GLDraw(renderContext, f5, f6, fArr[2] / fArr[3], z, i);
            }
        }
    }

    public void GLCleanup() {
        if (this.textTextureCache != null) {
            this.textTextureCache.CancelRequest(this);
        }
        this.textureRequested = false;
        this.hoverTextTexture = null;
    }

    public final void GLDraw(RenderContext renderContext, float f, float f2, float f3, boolean z, int i) {
        if (!this.textureRequested) {
            this.textureRequested = true;
            this.textTextureCache.RequestResource(DrawableTextParams.create(this.hoverText, this.backgroundColor), this);
        }
        GLLoadedTextTexture gLLoadedTextTexture = this.hoverTextTexture;
        if (gLLoadedTextTexture != null) {
            float width = (((float) gLLoadedTextTexture.getWidth()) * 2.0f) / ((float) renderContext.viewportRect[2]);
            float height = (((float) gLLoadedTextTexture.getHeight()) * 2.0f) / ((float) renderContext.viewportRect[3]);
            if (renderContext.hasGL20) {
                GLES20.glUniform3f(renderContext.quadProgram.uPreTranslate, f, f2, f3);
                GLES20.glUniform3f(renderContext.quadProgram.uScale, width, height, 1.0f);
                GLES20.glUniform3f(renderContext.quadProgram.uPostTranslate, 0.0f, gLLoadedTextTexture.baselineOffset, 0.0f);
                gLLoadedTextTexture.GLDraw();
                if (z) {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, ((float) ((i >> 0) & 255)) / 255.0f, ((float) ((i >> 8) & 255)) / 255.0f, ((float) ((i >> 16) & 255)) / 255.0f, ((float) (255 - ((i >> 24) & 255))) / 255.0f);
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 1);
                } else {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, 1.0f, 1.0f, 1.0f, 1.0f);
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 0);
                }
            } else {
                GLES10.glLoadIdentity();
                GLES10.glTranslatef(f, f2, f3);
                GLES10.glScalef(width, height, 1.0f);
                GLES10.glTranslatef(0.0f, gLLoadedTextTexture.baselineOffset, 0.0f);
                if (z) {
                    GLES10.glColor4f(((float) ((i >> 0) & 255)) / 255.0f, ((float) ((i >> 8) & 255)) / 255.0f, ((float) ((i >> 16) & 255)) / 255.0f, 1.0f - (((float) ((i >> 24) & 255)) / 255.0f));
                } else {
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                gLLoadedTextTexture.GLDraw();
            }
            renderContext.quad.DrawQuad(renderContext);
        }
    }

    public void OnResourceReady(Object obj, boolean z) {
        if (obj instanceof GLLoadedTextTexture) {
            this.hoverTextTexture = (GLLoadedTextTexture) obj;
        } else if (obj == null) {
            this.hoverTextTexture = null;
        }
    }
}
