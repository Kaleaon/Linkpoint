// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import android.opengl.GLES10;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTextTexture;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class DrawableHoverText implements ResourceConsumer, GLCleanable
{
    private final int backgroundColor;
    private final String hoverText;
    private volatile GLLoadedTextTexture hoverTextTexture;
    private final GLTextTextureCache textTextureCache;
    private boolean textureRequested;
    
    public DrawableHoverText(final GLTextTextureCache textTextureCache, final String hoverText, final int backgroundColor) {
        this.textureRequested = false;
        this.textTextureCache = textTextureCache;
        this.hoverText = hoverText;
        this.backgroundColor = backgroundColor;
    }
    
    public final void DrawAtWorld(final RenderContext renderContext, float n, float n2, final float n3, final float n4, final MatrixStack matrixStack, final boolean b, final int n5) {
        final float[] array = new float[8];
        final float[] matrixData = renderContext.modelViewMatrix.getMatrixData();
        final int matrixDataOffset = renderContext.modelViewMatrix.getMatrixDataOffset();
        final float[] matrixData2 = matrixStack.getMatrixData();
        final int matrixDataOffset2 = matrixStack.getMatrixDataOffset();
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = 1.0f;
        Matrix.multiplyMV(array, 4, matrixData, matrixDataOffset, array, 0);
        array[5] += n4;
        if (renderContext.hasGL20) {
            System.arraycopy(array, 4, array, 0, 4);
        }
        else {
            Matrix.multiplyMV(array, 0, matrixData2, matrixDataOffset2, array, 4);
        }
        if (array[3] != 0.0f) {
            n2 = array[0] / array[3];
            n = array[1] / array[3];
            if (array[3] != 0.0f) {
                this.GLDraw(renderContext, n2, n, array[2] / array[3], b, n5);
            }
        }
    }
    
    @Override
    public void GLCleanup() {
        if (this.textTextureCache != null) {
            this.textTextureCache.CancelRequest(this);
        }
        this.textureRequested = false;
        this.hoverTextTexture = null;
    }
    
    public final void GLDraw(final RenderContext renderContext, final float n, final float n2, final float n3, final boolean b, final int n4) {
        if (!this.textureRequested) {
            this.textureRequested = true;
            ((ResourceMemoryCache<ResourceParams, ResourceType>)this.textTextureCache).RequestResource((ResourceParams)DrawableTextParams.create(this.hoverText, this.backgroundColor), this);
        }
        final GLLoadedTextTexture hoverTextTexture = this.hoverTextTexture;
        if (hoverTextTexture != null) {
            final float n5 = hoverTextTexture.getWidth() * 2.0f / renderContext.viewportRect[2];
            final float n6 = hoverTextTexture.getHeight() * 2.0f / renderContext.viewportRect[3];
            if (renderContext.hasGL20) {
                GLES20.glUniform3f(renderContext.quadProgram.uPreTranslate, n, n2, n3);
                GLES20.glUniform3f(renderContext.quadProgram.uScale, n5, n6, 1.0f);
                GLES20.glUniform3f(renderContext.quadProgram.uPostTranslate, 0.0f, hoverTextTexture.baselineOffset, 0.0f);
                hoverTextTexture.GLDraw();
                if (b) {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, (n4 >> 0 & 0xFF) / 255.0f, (n4 >> 8 & 0xFF) / 255.0f, (n4 >> 16 & 0xFF) / 255.0f, (255 - (n4 >> 24 & 0xFF)) / 255.0f);
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 1);
                }
                else {
                    GLES20.glUniform4f(renderContext.quadProgram.uColor, 1.0f, 1.0f, 1.0f, 1.0f);
                    GLES20.glUniform1i(renderContext.quadProgram.uColorize, 0);
                }
            }
            else {
                GLES10.glLoadIdentity();
                GLES10.glTranslatef(n, n2, n3);
                GLES10.glScalef(n5, n6, 1.0f);
                GLES10.glTranslatef(0.0f, hoverTextTexture.baselineOffset, 0.0f);
                if (b) {
                    GLES10.glColor4f((n4 >> 0 & 0xFF) / 255.0f, (n4 >> 8 & 0xFF) / 255.0f, (n4 >> 16 & 0xFF) / 255.0f, 1.0f - (n4 >> 24 & 0xFF) / 255.0f);
                }
                else {
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                hoverTextTexture.GLDraw();
            }
            renderContext.quad.DrawQuad(renderContext);
        }
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        if (o instanceof GLLoadedTextTexture) {
            this.hoverTextTexture = (GLLoadedTextTexture)o;
        }
        else if (o == null) {
            this.hoverTextTexture = null;
        }
    }
}
