// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.terrain;

import android.opengl.GLES10;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;

public class TerrainPatchGeometry
{
    public static final int DrawPatchSize = 16;
    private static final int index_size_bytes = 3072;
    private static final int vertex_size_bytes = 9248;
    private static float[] waterAmplitude;
    private static float[] waterDirection;
    private static float[] waterFrequency;
    private static float[] waterPhase;
    private static final int water_vertex_size_bytes = 3468;
    private final GLLoadableBuffer indexBuffer;
    private int index_count;
    private final GLLoadableBuffer vertexBuffer;
    private final GLLoadableBuffer waterIndexBuffer;
    private final GLLoadableBuffer waterVertexBuffer;
    private int water_index_count;
    
    static {
        TerrainPatchGeometry.waterFrequency = new float[] { 17.951958f, 12.566371f, 8.975979f, 15.707963f };
        TerrainPatchGeometry.waterPhase = new float[] { 1.73f, 0.64f, 1.27f, 0.9f };
        TerrainPatchGeometry.waterAmplitude = new float[] { 0.5f, 0.5f, 0.3f, 0.4f };
        TerrainPatchGeometry.waterDirection = new float[] { 1.0f, 0.3f, 0.4f, 0.75f, -0.5f, 0.7f, 0.63f, -0.3f };
    }
    
    public TerrainPatchGeometry(final TerrainPatchHeightMap terrainPatchHeightMap) {
        this.index_count = 0;
        this.water_index_count = 0;
        final DirectByteBuffer directByteBuffer = new DirectByteBuffer(9248);
        final DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(3468);
        final DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(3072);
        final DirectByteBuffer directByteBuffer4 = new DirectByteBuffer(3072);
        directByteBuffer.position(0);
        directByteBuffer2.position(0);
        this.index_count = 0;
        this.water_index_count = 0;
        final LLVector3 llVector3 = new LLVector3();
        final float waterHeight = terrainPatchHeightMap.getWaterHeight();
        final float[] heightArray = terrainPatchHeightMap.getHeightArray();
        final float[] normalArray = terrainPatchHeightMap.getNormalArray();
        int n = 0;
        for (int i = 0; i < 17; ++i) {
            for (int j = 0; j < 17; ++j) {
                directByteBuffer.putFloat((float)j);
                directByteBuffer.putFloat((float)i);
                directByteBuffer.putFloat(heightArray[n + j]);
                llVector3.set(-normalArray[(n + j) * 2], normalArray[(n + j) * 2 + 1], 2.0f);
                llVector3.normVec();
                directByteBuffer.putFloat(llVector3.x);
                directByteBuffer.putFloat(llVector3.y);
                directByteBuffer.putFloat(llVector3.z);
                final float n2 = j / 16.0f;
                final float n3 = i / 16.0f;
                directByteBuffer.putFloat(n2);
                directByteBuffer.putFloat(n3);
                directByteBuffer2.putFloat((float)j);
                directByteBuffer2.putFloat((float)i);
                directByteBuffer2.putFloat(waterHeight);
            }
            n += 17;
        }
        directByteBuffer3.position(0);
        directByteBuffer4.position(0);
        int n4 = 0;
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                final int n5 = n4 + l;
                final int n6 = n5 + 1;
                final int n7 = n5 + 17;
                final int n8 = n7 + 1;
                directByteBuffer3.putShort((short)n5);
                directByteBuffer3.putShort((short)n6);
                directByteBuffer3.putShort((short)n7);
                directByteBuffer3.putShort((short)n6);
                directByteBuffer3.putShort((short)n8);
                directByteBuffer3.putShort((short)n7);
                this.index_count += 6;
                if (Math.min(Math.min(Math.min(directByteBuffer.getFloat(n5 * 8 + 2), directByteBuffer.getFloat(n6 * 8 + 2)), directByteBuffer.getFloat(n7 * 8 + 2)), directByteBuffer.getFloat(n8 * 8 + 2)) <= waterHeight) {
                    directByteBuffer4.putShort((short)n5);
                    directByteBuffer4.putShort((short)n6);
                    directByteBuffer4.putShort((short)n7);
                    directByteBuffer4.putShort((short)n6);
                    directByteBuffer4.putShort((short)n8);
                    directByteBuffer4.putShort((short)n7);
                    this.water_index_count += 6;
                }
            }
            n4 += 17;
        }
        this.vertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.indexBuffer = new GLLoadableBuffer(directByteBuffer3);
        this.waterVertexBuffer = new GLLoadableBuffer(directByteBuffer2);
        this.waterIndexBuffer = new GLLoadableBuffer(directByteBuffer4);
    }
    
    public static void GLPrepare(final RenderContext renderContext) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.primProgram.getHandle());
            renderContext.glModelApplyMatrix(renderContext.primProgram.uMVPMatrix);
            renderContext.primProgram.SetupLighting(renderContext, renderContext.windlightPreset);
            GLES20.glUniform4f(renderContext.primProgram.uObjCoordScale, 1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glUniformMatrix4fv(renderContext.primProgram.uTexMatrix, 1, false, IdentityMatrix.getMatrix(), 0);
            GLES20.glUseProgram(renderContext.waterProgram.getHandle());
            GLES20.glUniform4f(renderContext.waterProgram.vColor, 0.4f, 0.4f, 0.6f, 1.0f);
            renderContext.glModelApplyMatrix(renderContext.waterProgram.uMVPMatrix);
            GLES20.glUniform1f(renderContext.waterProgram.uTime, renderContext.waterTime);
            GLES20.glUniform1fv(renderContext.waterProgram.uFrequency, 4, TerrainPatchGeometry.waterFrequency, 0);
            GLES20.glUniform1fv(renderContext.waterProgram.uPhase, 4, TerrainPatchGeometry.waterPhase, 0);
            GLES20.glUniform1fv(renderContext.waterProgram.uAmplitude, 4, TerrainPatchGeometry.waterAmplitude, 0);
            GLES20.glUniform2fv(renderContext.waterProgram.uDirection, 4, TerrainPatchGeometry.waterDirection, 0);
        }
        else {
            GLES11.glMatrixMode(5890);
            GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0);
            GLES11.glMatrixMode(5888);
        }
    }
    
    public final void GLDraw(final RenderContext renderContext, final float[] array, final GLLoadedTexture glLoadedTexture) {
        if (this.index_count == 0) {
            return;
        }
        if (!renderContext.hasGL20) {
            renderContext.glObjWorldPushAndMultMatrixf(array, 0);
        }
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.primProgram.getHandle());
            this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vPosition, 3, 5126, 32, 0);
            this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vNormal, 3, 5126, 32, 12);
            GLES20.glUniformMatrix4fv(renderContext.primProgram.uObjWorldMatrix, 1, false, array, 0);
            if (glLoadedTexture != null) {
                glLoadedTexture.GLDraw();
                this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vTexCoord, 2, 5126, 32, 24);
                GLES20.glUniform1i(renderContext.primProgram.sTexture, 0);
                GLES20.glUniform4f(renderContext.primProgram.vColor, 1.0f, 1.0f, 1.0f, 1.0f);
                renderContext.primProgram.setTextureEnabled(true);
            }
            else {
                GLES20.glBindTexture(3553, 0);
                GLES20.glDisableVertexAttribArray(renderContext.primProgram.vTexCoord);
                GLES20.glUniform1i(renderContext.primProgram.sTexture, 0);
                GLES20.glUniform4f(renderContext.primProgram.vColor, 0.1f, 0.5f, 0.1f, 1.0f);
                renderContext.primProgram.setTextureEnabled(false);
            }
            this.indexBuffer.BindElements20(renderContext);
            this.indexBuffer.DrawElements20(4, this.index_count, 5123, 0);
        }
        else {
            GLES10.glDisableClientState(32885);
            if (glLoadedTexture != null) {
                GLES10.glEnable(3553);
                glLoadedTexture.GLDraw();
                GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                this.vertexBuffer.Bind(renderContext, 32888, 2, 5126, 32, 24);
            }
            else {
                GLES10.glDisableClientState(32888);
                GLES10.glDisable(3553);
                GLES10.glColor4f(0.1f, 0.5f, 0.1f, 1.0f);
            }
            this.vertexBuffer.Bind(renderContext, 32884, 3, 5126, 32, 0);
            this.indexBuffer.BindElements(renderContext);
            this.indexBuffer.DrawElements(renderContext, 4, this.index_count, 5123, 0);
        }
        if (this.water_index_count != 0) {
            if (renderContext.hasGL20) {
                GLES20.glDisable(2884);
                GLES20.glUseProgram(renderContext.waterProgram.getHandle());
                GLES20.glUniformMatrix4fv(renderContext.waterProgram.uObjWorldMatrix, 1, false, array, 0);
                this.waterVertexBuffer.Bind20(renderContext, renderContext.waterProgram.vPosition, 3, 5126, 0, 0);
                this.waterIndexBuffer.BindElements20(renderContext);
                this.waterIndexBuffer.DrawElements20(4, this.water_index_count, 5123, 0);
                GLES20.glEnable(2884);
            }
            else {
                GLES10.glDisable(2884);
                GLES10.glDisableClientState(32888);
                GLES10.glDisable(3553);
                GLES10.glColor4f(0.4f, 0.4f, 0.6f, 1.0f);
                this.waterVertexBuffer.Bind(renderContext, 32884, 3, 5126, 0, 0);
                this.waterIndexBuffer.BindElements(renderContext);
                this.waterIndexBuffer.DrawElements(renderContext, 4, this.water_index_count, 5123, 0);
                GLES10.glEnable(2884);
            }
        }
        if (!renderContext.hasGL20) {
            renderContext.glObjWorldPopMatrix();
        }
    }
}
