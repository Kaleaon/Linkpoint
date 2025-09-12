// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES10;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.shaders.QuadProgram;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            RenderContext

public class Quad
{

    private static short drawOrder[] = {
        0, 1, 2, 0, 2, 3
    };
    private static float squareCoords[] = {
        -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 
        0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F
    };
    private final GLLoadableBuffer indexBuffer;
    private final GLLoadableBuffer vertexBuffer;

    public Quad()
    {
        DirectByteBuffer directbytebuffer = new DirectByteBuffer(squareCoords.length * 4);
        Object obj = directbytebuffer.asFloatBuffer();
        ((FloatBuffer) (obj)).put(squareCoords);
        ((FloatBuffer) (obj)).position(0);
        obj = new DirectByteBuffer(drawOrder.length * 2);
        ShortBuffer shortbuffer = ((DirectByteBuffer) (obj)).asShortBuffer();
        shortbuffer.put(drawOrder);
        shortbuffer.position(0);
        vertexBuffer = new GLLoadableBuffer(directbytebuffer);
        indexBuffer = new GLLoadableBuffer(((DirectByteBuffer) (obj)));
    }

    public final void DrawQuad(RenderContext rendercontext)
    {
        if (rendercontext.hasGL20)
        {
            indexBuffer.DrawElements20(4, drawOrder.length, 5123, 0);
            return;
        } else
        {
            indexBuffer.DrawElements(rendercontext, 4, drawOrder.length, 5123, 0);
            return;
        }
    }

    public final void DrawSingleQuadShader(RenderContext rendercontext, int i, int j)
    {
        if (rendercontext.hasGL20)
        {
            vertexBuffer.Bind20(rendercontext, i, 3, 5126, 20, 0);
            vertexBuffer.Bind20(rendercontext, j, 2, 5126, 20, 12);
            indexBuffer.DrawElements20(4, drawOrder.length, 5123, 0);
        }
    }

    public void EndDrawQuads(RenderContext rendercontext)
    {
    }

    public void PrepareDrawQuads(RenderContext rendercontext)
    {
        if (rendercontext.hasGL20)
        {
            GLES20.glUseProgram(rendercontext.quadProgram.getHandle());
            vertexBuffer.Bind20(rendercontext, rendercontext.quadProgram.vPosition, 3, 5126, 20, 0);
            vertexBuffer.Bind20(rendercontext, rendercontext.quadProgram.vTexCoord, 2, 5126, 20, 12);
            GLES20.glUniform1i(rendercontext.quadProgram.sTexture, 0);
            indexBuffer.BindElements20(rendercontext);
            return;
        } else
        {
            GLES10.glEnable(3553);
            GLES10.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            vertexBuffer.Bind(rendercontext, 32884, 3, 5126, 20, 0);
            vertexBuffer.Bind(rendercontext, 32888, 2, 5126, 20, 12);
            GLES10.glEnableClientState(32888);
            GLES10.glTexParameterf(3553, 10240, 9728F);
            GLES10.glTexParameterf(3553, 10241, 9728F);
            GLES10.glTexParameterf(3553, 10242, 10497F);
            GLES10.glTexParameterf(3553, 10243, 10497F);
            indexBuffer.BindElements(rendercontext);
            return;
        }
    }

}
