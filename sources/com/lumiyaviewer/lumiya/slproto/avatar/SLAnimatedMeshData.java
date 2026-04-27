// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.opengl.GLES10;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.shaders.AvatarProgram;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLMeshData, SLPolyMesh

public class SLAnimatedMeshData extends SLMeshData
{

    private static final int BUF_INDEX = 1;
    private static final int BUF_TEXCOORD = 2;
    private static final int BUF_VERTEX = 0;
    private static final int BUF_WEIGHTS = 3;
    private boolean VBOLoaded;
    private final boolean animated;
    private final DirectByteBuffer animatedVertexData;
    private GLLoadableBuffer glBuffers[];
    private boolean texCoordsDirty;
    private boolean verticesDirty;

    public SLAnimatedMeshData(SLPolyMesh slpolymesh, boolean flag)
    {
        boolean flag1 = false;
        super(slpolymesh);
        VBOLoaded = false;
        glBuffers = new GLLoadableBuffer[4];
        texCoordsDirty = false;
        verticesDirty = false;
        if (slpolymesh.hasWeights)
        {
            flag1 = flag ^ true;
        }
        animated = flag1;
        if (animated)
        {
            animatedVertexData = new DirectByteBuffer(vertexBuffer);
            verticesDirty = true;
            return;
        } else
        {
            animatedVertexData = null;
            return;
        }
    }

    private void setupVBOs(RenderContext rendercontext)
    {
        if (!VBOLoaded || texCoordsDirty || verticesDirty)
        {
            if (!VBOLoaded || verticesDirty)
            {
                DirectByteBuffer directbytebuffer;
                if (animated)
                {
                    directbytebuffer = animatedVertexData;
                } else
                {
                    directbytebuffer = vertexBuffer;
                }
                if (glBuffers[0] == null)
                {
                    glBuffers[0] = new GLLoadableBuffer(directbytebuffer);
                } else
                if (animated)
                {
                    glBuffers[0].Reload(rendercontext);
                }
            }
            if (!VBOLoaded)
            {
                if (rendercontext.hasGL20 && referenceData.hasWeights)
                {
                    glBuffers[3] = new GLLoadableBuffer(referenceData.weightsBuffer);
                }
                glBuffers[1] = new GLLoadableBuffer(indexBuffer);
            }
            if (!VBOLoaded || texCoordsDirty)
            {
                if (glBuffers[2] == null)
                {
                    glBuffers[2] = new GLLoadableBuffer(texCoordsBuffer);
                } else
                {
                    glBuffers[2].Reload(rendercontext);
                }
            }
            VBOLoaded = true;
            verticesDirty = false;
            texCoordsDirty = false;
        }
    }

    public final void GLDraw(RenderContext rendercontext, DrawableFaceTexture drawablefacetexture)
    {
        boolean flag;
        flag = false;
        setupVBOs(rendercontext);
        if (drawablefacetexture != null)
        {
            if (!rendercontext.hasGL20)
            {
                GLES20.glEnable(3553);
            }
            flag = true;
            if (!drawablefacetexture.GLDraw(rendercontext))
            {
                if (!rendercontext.hasGL20)
                {
                    GLES20.glDisable(3553);
                }
                flag = false;
            }
        }
        if (!rendercontext.hasGL20) goto _L2; else goto _L1
_L1:
        if (VBOLoaded)
        {
            rendercontext.avatarProgram.setTextureEnabled(flag);
            if (!flag)
            {
                GLES20.glBindTexture(3553, 0);
            }
            rendercontext.glObjWorldApplyMatrix(rendercontext.avatarProgram.uObjWorldMatrix);
            if (flag)
            {
                GLES20.glUniform4f(rendercontext.avatarProgram.vColor, 1.0F, 1.0F, 1.0F, 1.0F);
            } else
            {
                GLES20.glUniform4f(rendercontext.avatarProgram.vColor, 0.5F, 0.5F, 0.5F, 1.0F);
            }
            glBuffers[0].Bind20(rendercontext, rendercontext.avatarProgram.vPosition, 3, 5126, 24, 0);
            glBuffers[0].Bind20(rendercontext, rendercontext.avatarProgram.vNormal, 3, 5126, 24, 12);
            glBuffers[1].BindElements20(rendercontext);
            if (flag)
            {
                glBuffers[2].Bind20(rendercontext, rendercontext.avatarProgram.vTexCoord, 2, 5126, 8, 0);
            } else
            {
                GLES20.glDisableVertexAttribArray(rendercontext.avatarProgram.vTexCoord);
            }
            if (glBuffers[3] != null)
            {
                glBuffers[3].Bind20(rendercontext, rendercontext.avatarProgram.vWeight, 1, 5126, 4, 0);
                GLES20.glUniform1iv(rendercontext.avatarProgram.uJointMap, referenceData.jointMap.length, referenceData.jointMap, 0);
                GLES20.glUniform1i(rendercontext.avatarProgram.uJointMapLength, referenceData.jointMap.length);
                GLES20.glUniform1i(rendercontext.avatarProgram.uUseWeight, 1);
            } else
            {
                GLES20.glDisableVertexAttribArray(rendercontext.avatarProgram.vWeight);
                GLES20.glUniform1i(rendercontext.avatarProgram.uUseWeight, 0);
            }
            glBuffers[1].DrawElements20(4, numFaces * 3, 5123, 0);
        }
_L4:
        if (flag)
        {
            if (!rendercontext.hasGL20)
            {
                break; /* Loop/switch isn't completed */
            }
            GLES20.glBindTexture(3553, 0);
        }
        return;
_L2:
        if (referenceData.hasWeights)
        {
            drawablefacetexture = animatedVertexData;
        } else
        {
            drawablefacetexture = vertexBuffer;
        }
        if (drawablefacetexture != null)
        {
            GLES10.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            glBuffers[0].Bind(rendercontext, 32884, 3, 5126, 24, 0);
            glBuffers[0].Bind(rendercontext, 32885, 3, 5126, 24, 12);
            glBuffers[2].Bind(rendercontext, 32888, 2, 5126, 8, 0);
            glBuffers[1].BindElements(rendercontext);
            glBuffers[1].DrawElements(rendercontext, 4, numFaces * 3, 5123, 0);
            GLES10.glDisableClientState(32885);
            GLES10.glDisableClientState(32888);
        }
        if (true) goto _L4; else goto _L3
_L3:
        GLES10.glBindTexture(3553, 0);
        GLES10.glDisable(3553);
        return;
    }

    public DirectByteBuffer getAnimatedVertexData()
    {
        return animatedVertexData;
    }

    public void setVerticesDirty()
    {
        verticesDirty = true;
    }
}
