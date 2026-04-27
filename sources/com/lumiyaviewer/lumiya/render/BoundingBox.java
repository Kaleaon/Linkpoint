// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES20;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.glres.GLQuery;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject;
import com.lumiyaviewer.lumiya.render.shaders.BoundingBoxProgram;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            RenderContext

public class BoundingBox
{

    private static final float cubeSize = 0.5F;
    private static float cubeVertices[] = {
        -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, 0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 
        0.5F, -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, 0.5F, -0.5F, 0.5F, -0.5F, 
        0.5F, -0.5F, -0.5F, -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 
        0.5F, -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, 
        0.5F, 0.5F, -0.5F, 0.5F, -0.5F, 0.5F, -0.5F, 0.5F, -0.5F, -0.5F, 
        0.5F, -0.5F, -0.5F, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 
        0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 
        0.5F, -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, 
        0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, 0.5F, -0.5F, 
        0.5F, 0.5F, 0.5F, -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 
        0.5F, 0.5F, -0.5F, 0.5F, 0.5F, 0.5F, -0.5F, 0.5F
    };
    private final GLVertexArrayObject vertexArrayObject;
    private final GLLoadableBuffer vertexBuffer;

    public BoundingBox(RenderContext rendercontext)
    {
        DirectByteBuffer directbytebuffer = new DirectByteBuffer(cubeVertices.length * 4);
        directbytebuffer.loadFromFloatArray(0, cubeVertices, 0, cubeVertices.length);
        vertexBuffer = new GLLoadableBuffer(directbytebuffer);
        vertexArrayObject = new GLVertexArrayObject(rendercontext.glResourceManager, 1);
        GLES20.glUseProgram(rendercontext.boundingBoxProgram.getHandle());
        vertexArrayObject.Bind(0);
        vertexBuffer.Bind20(rendercontext, rendercontext.boundingBoxProgram.vPosition, 3, 5126, 12, 0);
        GLES20.glBindBuffer(34963, 0);
        vertexArrayObject.Unbind();
        GLES20.glUseProgram(0);
    }

    public static void EndOcclusionQueries(RenderContext rendercontext)
    {
        GLES20.glUseProgram(0);
        rendercontext.curPrimProgram = null;
        GLES30.glColorMask(true, true, true, true);
        GLES30.glDepthMask(true);
        GLES30.glDepthFunc(513);
        GLES30.glEnable(2884);
    }

    public static void PrepareOcclusionQueries(RenderContext rendercontext)
    {
        GLES30.glColorMask(false, false, false, false);
        GLES30.glDepthMask(false);
        GLES30.glDisable(2884);
        GLES30.glDepthFunc(515);
        GLES20.glUseProgram(rendercontext.boundingBoxProgram.getHandle());
        rendercontext.glModelApplyMatrix(rendercontext.boundingBoxProgram.uMVPMatrix);
    }

    public void OcclusionQuery(RenderContext rendercontext, GLQuery glquery)
    {
        rendercontext.glObjWorldApplyMatrix(rendercontext.boundingBoxProgram.uObjWorldMatrix);
        rendercontext.glObjScaleApplyVector(rendercontext.boundingBoxProgram.uObjCoordScale);
        vertexArrayObject.Bind(0);
        glquery.BeginOcclusionQuery(rendercontext);
        GLES20.glDrawArrays(4, 0, cubeVertices.length / 3);
        glquery.EndOcclusionQuery();
        vertexArrayObject.Unbind();
    }

}
