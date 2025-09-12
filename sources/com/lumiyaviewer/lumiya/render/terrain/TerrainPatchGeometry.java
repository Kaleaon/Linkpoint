// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.terrain;

import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.WaterProgram;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class TerrainPatchGeometry
{

    public static final int DrawPatchSize = 16;
    private static final int index_size_bytes = 3072;
    private static final int vertex_size_bytes = 9248;
    private static float waterAmplitude[] = {
        0.5F, 0.5F, 0.3F, 0.4F
    };
    private static float waterDirection[] = {
        1.0F, 0.3F, 0.4F, 0.75F, -0.5F, 0.7F, 0.63F, -0.3F
    };
    private static float waterFrequency[] = {
        17.95196F, 12.56637F, 8.975979F, 15.70796F
    };
    private static float waterPhase[] = {
        1.73F, 0.64F, 1.27F, 0.9F
    };
    private static final int water_vertex_size_bytes = 3468;
    private final GLLoadableBuffer indexBuffer;
    private int index_count;
    private final GLLoadableBuffer vertexBuffer;
    private final GLLoadableBuffer waterIndexBuffer;
    private final GLLoadableBuffer waterVertexBuffer;
    private int water_index_count;

    public TerrainPatchGeometry(TerrainPatchHeightMap terrainpatchheightmap)
    {
        index_count = 0;
        water_index_count = 0;
        DirectByteBuffer directbytebuffer = new DirectByteBuffer(9248);
        DirectByteBuffer directbytebuffer1 = new DirectByteBuffer(3468);
        DirectByteBuffer directbytebuffer2 = new DirectByteBuffer(3072);
        DirectByteBuffer directbytebuffer3 = new DirectByteBuffer(3072);
        directbytebuffer.position(0);
        directbytebuffer1.position(0);
        index_count = 0;
        water_index_count = 0;
        LLVector3 llvector3 = new LLVector3();
        float f = terrainpatchheightmap.getWaterHeight();
        float af[] = terrainpatchheightmap.getHeightArray();
        terrainpatchheightmap = terrainpatchheightmap.getNormalArray();
        int k = 0;
        for (int i = 0; i < 17; i++)
        {
            for (int l = 0; l < 17; l++)
            {
                directbytebuffer.putFloat(l);
                directbytebuffer.putFloat(i);
                directbytebuffer.putFloat(af[k + l]);
                float f1 = terrainpatchheightmap[(k + l) * 2];
                float f2 = terrainpatchheightmap[(k + l) * 2 + 1];
                llvector3.set(-f1, f2, 2.0F);
                llvector3.normVec();
                directbytebuffer.putFloat(llvector3.x);
                directbytebuffer.putFloat(llvector3.y);
                directbytebuffer.putFloat(llvector3.z);
                f1 = (float)l / 16F;
                f2 = (float)i / 16F;
                directbytebuffer.putFloat(f1);
                directbytebuffer.putFloat(f2);
                directbytebuffer1.putFloat(l);
                directbytebuffer1.putFloat(i);
                directbytebuffer1.putFloat(f);
            }

            k += 17;
        }

        directbytebuffer2.position(0);
        directbytebuffer3.position(0);
        k = 0;
        for (int j = 0; j < 16; j++)
        {
            for (int i1 = 0; i1 < 16; i1++)
            {
                int j1 = k + i1;
                int k1 = j1 + 1;
                int l1 = j1 + 17;
                int i2 = l1 + 1;
                directbytebuffer2.putShort((short)j1);
                directbytebuffer2.putShort((short)k1);
                directbytebuffer2.putShort((short)l1);
                directbytebuffer2.putShort((short)k1);
                directbytebuffer2.putShort((short)i2);
                directbytebuffer2.putShort((short)l1);
                index_count = index_count + 6;
                if (Math.min(Math.min(Math.min(directbytebuffer.getFloat(j1 * 8 + 2), directbytebuffer.getFloat(k1 * 8 + 2)), directbytebuffer.getFloat(l1 * 8 + 2)), directbytebuffer.getFloat(i2 * 8 + 2)) <= f)
                {
                    directbytebuffer3.putShort((short)j1);
                    directbytebuffer3.putShort((short)k1);
                    directbytebuffer3.putShort((short)l1);
                    directbytebuffer3.putShort((short)k1);
                    directbytebuffer3.putShort((short)i2);
                    directbytebuffer3.putShort((short)l1);
                    water_index_count = water_index_count + 6;
                }
            }

            k += 17;
        }

        vertexBuffer = new GLLoadableBuffer(directbytebuffer);
        indexBuffer = new GLLoadableBuffer(directbytebuffer2);
        waterVertexBuffer = new GLLoadableBuffer(directbytebuffer1);
        waterIndexBuffer = new GLLoadableBuffer(directbytebuffer3);
    }

    public static void GLPrepare(RenderContext rendercontext)
    {
        if (rendercontext.hasGL20)
        {
            GLES20.glUseProgram(rendercontext.primProgram.getHandle());
            rendercontext.glModelApplyMatrix(rendercontext.primProgram.uMVPMatrix);
            rendercontext.primProgram.SetupLighting(rendercontext, rendercontext.windlightPreset);
            GLES20.glUniform4f(rendercontext.primProgram.uObjCoordScale, 1.0F, 1.0F, 1.0F, 1.0F);
            GLES20.glUniformMatrix4fv(rendercontext.primProgram.uTexMatrix, 1, false, IdentityMatrix.getMatrix(), 0);
            GLES20.glUseProgram(rendercontext.waterProgram.getHandle());
            GLES20.glUniform4f(rendercontext.waterProgram.vColor, 0.4F, 0.4F, 0.6F, 1.0F);
            rendercontext.glModelApplyMatrix(rendercontext.waterProgram.uMVPMatrix);
            GLES20.glUniform1f(rendercontext.waterProgram.uTime, rendercontext.waterTime);
            GLES20.glUniform1fv(rendercontext.waterProgram.uFrequency, 4, waterFrequency, 0);
            GLES20.glUniform1fv(rendercontext.waterProgram.uPhase, 4, waterPhase, 0);
            GLES20.glUniform1fv(rendercontext.waterProgram.uAmplitude, 4, waterAmplitude, 0);
            GLES20.glUniform2fv(rendercontext.waterProgram.uDirection, 4, waterDirection, 0);
            return;
        } else
        {
            GLES11.glMatrixMode(5890);
            GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0);
            GLES11.glMatrixMode(5888);
            return;
        }
    }

    public final void GLDraw(RenderContext rendercontext, float af[], GLLoadedTexture glloadedtexture)
    {
        if (index_count == 0)
        {
            return;
        }
        if (!rendercontext.hasGL20)
        {
            rendercontext.glObjWorldPushAndMultMatrixf(af, 0);
        }
        if (rendercontext.hasGL20)
        {
            GLES20.glUseProgram(rendercontext.primProgram.getHandle());
            vertexBuffer.Bind20(rendercontext, rendercontext.primProgram.vPosition, 3, 5126, 32, 0);
            vertexBuffer.Bind20(rendercontext, rendercontext.primProgram.vNormal, 3, 5126, 32, 12);
            GLES20.glUniformMatrix4fv(rendercontext.primProgram.uObjWorldMatrix, 1, false, af, 0);
            if (glloadedtexture != null)
            {
                glloadedtexture.GLDraw();
                vertexBuffer.Bind20(rendercontext, rendercontext.primProgram.vTexCoord, 2, 5126, 32, 24);
                GLES20.glUniform1i(rendercontext.primProgram.sTexture, 0);
                GLES20.glUniform4f(rendercontext.primProgram.vColor, 1.0F, 1.0F, 1.0F, 1.0F);
                rendercontext.primProgram.setTextureEnabled(true);
            } else
            {
                GLES20.glBindTexture(3553, 0);
                GLES20.glDisableVertexAttribArray(rendercontext.primProgram.vTexCoord);
                GLES20.glUniform1i(rendercontext.primProgram.sTexture, 0);
                GLES20.glUniform4f(rendercontext.primProgram.vColor, 0.1F, 0.5F, 0.1F, 1.0F);
                rendercontext.primProgram.setTextureEnabled(false);
            }
            indexBuffer.BindElements20(rendercontext);
            indexBuffer.DrawElements20(4, index_count, 5123, 0);
        } else
        {
            GLES10.glDisableClientState(32885);
            if (glloadedtexture != null)
            {
                GLES10.glEnable(3553);
                glloadedtexture.GLDraw();
                GLES10.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                vertexBuffer.Bind(rendercontext, 32888, 2, 5126, 32, 24);
            } else
            {
                GLES10.glDisableClientState(32888);
                GLES10.glDisable(3553);
                GLES10.glColor4f(0.1F, 0.5F, 0.1F, 1.0F);
            }
            vertexBuffer.Bind(rendercontext, 32884, 3, 5126, 32, 0);
            indexBuffer.BindElements(rendercontext);
            indexBuffer.DrawElements(rendercontext, 4, index_count, 5123, 0);
        }
        if (water_index_count != 0)
        {
            if (rendercontext.hasGL20)
            {
                GLES20.glDisable(2884);
                GLES20.glUseProgram(rendercontext.waterProgram.getHandle());
                GLES20.glUniformMatrix4fv(rendercontext.waterProgram.uObjWorldMatrix, 1, false, af, 0);
                waterVertexBuffer.Bind20(rendercontext, rendercontext.waterProgram.vPosition, 3, 5126, 0, 0);
                waterIndexBuffer.BindElements20(rendercontext);
                waterIndexBuffer.DrawElements20(4, water_index_count, 5123, 0);
                GLES20.glEnable(2884);
            } else
            {
                GLES10.glDisable(2884);
                GLES10.glDisableClientState(32888);
                GLES10.glDisable(3553);
                GLES10.glColor4f(0.4F, 0.4F, 0.6F, 1.0F);
                waterVertexBuffer.Bind(rendercontext, 32884, 3, 5126, 0, 0);
                waterIndexBuffer.BindElements(rendercontext);
                waterIndexBuffer.DrawElements(rendercontext, 4, water_index_count, 5123, 0);
                GLES10.glEnable(2884);
            }
        }
        if (!rendercontext.hasGL20)
        {
            rendercontext.glObjWorldPopMatrix();
        }
    }

}
