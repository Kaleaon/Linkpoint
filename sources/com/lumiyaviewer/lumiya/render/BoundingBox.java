package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.support.v4.view.InputDeviceCompat;
import com.lumiyaviewer.lumiya.render.glres.GLQuery;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class BoundingBox {
    private static final float cubeSize = 0.5f;
    private static float[] cubeVertices = {-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f};
    private final GLVertexArrayObject vertexArrayObject;
    private final GLLoadableBuffer vertexBuffer;

    public BoundingBox(RenderContext renderContext) {
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(cubeVertices.length * 4);
        directByteBuffer.loadFromFloatArray(0, cubeVertices, 0, cubeVertices.length);
        this.vertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, 1);
        GLES20.glUseProgram(renderContext.boundingBoxProgram.getHandle());
        this.vertexArrayObject.Bind(0);
        this.vertexBuffer.Bind20(renderContext, renderContext.boundingBoxProgram.vPosition, 3, 5126, 12, 0);
        GLES20.glBindBuffer(34963, 0);
        this.vertexArrayObject.Unbind();
        GLES20.glUseProgram(0);
    }

    public static void EndOcclusionQueries(RenderContext renderContext) {
        GLES20.glUseProgram(0);
        renderContext.curPrimProgram = null;
        GLES30.glColorMask(true, true, true, true);
        GLES30.glDepthMask(true);
        GLES30.glDepthFunc(InputDeviceCompat.SOURCE_DPAD);
        GLES30.glEnable(2884);
    }

    public static void PrepareOcclusionQueries(RenderContext renderContext) {
        GLES30.glColorMask(false, false, false, false);
        GLES30.glDepthMask(false);
        GLES30.glDisable(2884);
        GLES30.glDepthFunc(515);
        GLES20.glUseProgram(renderContext.boundingBoxProgram.getHandle());
        renderContext.glModelApplyMatrix(renderContext.boundingBoxProgram.uMVPMatrix);
    }

    public void OcclusionQuery(RenderContext renderContext, GLQuery gLQuery) {
        renderContext.glObjWorldApplyMatrix(renderContext.boundingBoxProgram.uObjWorldMatrix);
        renderContext.glObjScaleApplyVector(renderContext.boundingBoxProgram.uObjCoordScale);
        this.vertexArrayObject.Bind(0);
        gLQuery.BeginOcclusionQuery(renderContext);
        GLES20.glDrawArrays(4, 0, cubeVertices.length / 3);
        gLQuery.EndOcclusionQuery();
        this.vertexArrayObject.Unbind();
    }
}
