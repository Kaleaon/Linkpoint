// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES10;
import android.opengl.GLES20;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;

public class Quad
{
    private static short[] drawOrder;
    private static float[] squareCoords;
    private final GLLoadableBuffer indexBuffer;
    private final GLLoadableBuffer vertexBuffer;
    
    static {
        Quad.squareCoords = new float[] { -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f };
        Quad.drawOrder = new short[] { 0, 1, 2, 0, 2, 3 };
    }
    
    public Quad() {
        final DirectByteBuffer directByteBuffer = new DirectByteBuffer(Quad.squareCoords.length * 4);
        final FloatBuffer floatBuffer = directByteBuffer.asFloatBuffer();
        floatBuffer.put(Quad.squareCoords);
        floatBuffer.position();
        final DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(Quad.drawOrder.length * 2);
        final ShortBuffer shortBuffer = directByteBuffer2.asShortBuffer();
        shortBuffer.put(Quad.drawOrder);
        shortBuffer.position();
        this.vertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.indexBuffer = new GLLoadableBuffer(directByteBuffer2);
    }
    
    public final void DrawQuad(final RenderContext renderContext) {
        if (renderContext.hasGL20) {
            this.indexBuffer.DrawElements20(4, Quad.drawOrder.length, 5123, 0);
        }
        else {
            this.indexBuffer.DrawElements(renderContext, 4, Quad.drawOrder.length, 5123, 0);
        }
    }
    
    public final void DrawSingleQuadShader(final RenderContext renderContext, final int n, final int n2) {
        if (renderContext.hasGL20) {
            this.vertexBuffer.Bind20(renderContext, n, 3, 5126, 20, 0);
            this.vertexBuffer.Bind20(renderContext, n2, 2, 5126, 20, 12);
            this.indexBuffer.DrawElements20(4, Quad.drawOrder.length, 5123, 0);
        }
    }
    
    public void EndDrawQuads(final RenderContext renderContext) {
    }
    
    public void PrepareDrawQuads(final RenderContext renderContext) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.quadProgram.getHandle());
            this.vertexBuffer.Bind20(renderContext, renderContext.quadProgram.vPosition, 3, 5126, 20, 0);
            this.vertexBuffer.Bind20(renderContext, renderContext.quadProgram.vTexCoord, 2, 5126, 20, 12);
            GLES20.glUniform1i(renderContext.quadProgram.sTexture, 0);
            this.indexBuffer.BindElements20(renderContext);
        }
        else {
            GLES10.glEnable(3553);
            GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.vertexBuffer.Bind(renderContext, 32884, 3, 5126, 20, 0);
            this.vertexBuffer.Bind(renderContext, 32888, 2, 5126, 20, 12);
            GLES10.glEnableClientState(32888);
            GLES10.glTexParameterf(3553, 10240, 9728.0f);
            GLES10.glTexParameterf(3553, 10241, 9728.0f);
            GLES10.glTexParameterf(3553, 10242, 10497.0f);
            GLES10.glTexParameterf(3553, 10243, 10497.0f);
            this.indexBuffer.BindElements(renderContext);
        }
    }
}
