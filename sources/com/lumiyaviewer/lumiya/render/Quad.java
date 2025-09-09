package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES10;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Quad {
    private static short[] drawOrder = {0, 1, 2, 0, 2, 3};
    private static float[] squareCoords = {-0.5f, 0.5f, 0.0f, 0.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f};
    private final GLLoadableBuffer indexBuffer;
    private final GLLoadableBuffer vertexBuffer;

    public Quad() {
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(squareCoords.length * 4);
        FloatBuffer asFloatBuffer = directByteBuffer.asFloatBuffer();
        asFloatBuffer.put(squareCoords);
        asFloatBuffer.position(0);
        DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(drawOrder.length * 2);
        ShortBuffer asShortBuffer = directByteBuffer2.asShortBuffer();
        asShortBuffer.put(drawOrder);
        asShortBuffer.position(0);
        this.vertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.indexBuffer = new GLLoadableBuffer(directByteBuffer2);
    }

    public final void DrawQuad(RenderContext renderContext) {
        if (renderContext.hasGL20) {
            this.indexBuffer.DrawElements20(4, drawOrder.length, 5123, 0);
            return;
        }
        this.indexBuffer.DrawElements(renderContext, 4, drawOrder.length, 5123, 0);
    }

    public final void DrawSingleQuadShader(RenderContext renderContext, int i, int i2) {
        if (renderContext.hasGL20) {
            this.vertexBuffer.Bind20(renderContext, i, 3, 5126, 20, 0);
            this.vertexBuffer.Bind20(renderContext, i2, 2, 5126, 20, 12);
            this.indexBuffer.DrawElements20(4, drawOrder.length, 5123, 0);
        }
    }

    public void EndDrawQuads(RenderContext renderContext) {
    }

    public void PrepareDrawQuads(RenderContext renderContext) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.quadProgram.getHandle());
            this.vertexBuffer.Bind20(renderContext, renderContext.quadProgram.vPosition, 3, 5126, 20, 0);
            this.vertexBuffer.Bind20(renderContext, renderContext.quadProgram.vTexCoord, 2, 5126, 20, 12);
            GLES20.glUniform1i(renderContext.quadProgram.sTexture, 0);
            this.indexBuffer.BindElements20(renderContext);
            return;
        }
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
