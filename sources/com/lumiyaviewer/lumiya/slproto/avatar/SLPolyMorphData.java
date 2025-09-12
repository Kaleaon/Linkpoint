// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLMeshData, SLPolyMesh, SLVisualParamID

public class SLPolyMorphData
{

    private DirectByteBuffer indexBuffer;
    private boolean isMasked;
    private SLPolyMesh mesh;
    private SLVisualParamID morphID;
    private int numVertices;
    private DirectByteBuffer texCoordsBuffer;
    private DirectByteBuffer vertexBuffer;

    public SLPolyMorphData(SLVisualParamID slvisualparamid, SLPolyMesh slpolymesh, DataInputStream datainputstream)
        throws IOException
    {
        boolean flag = false;
        super();
        morphID = slvisualparamid;
        mesh = slpolymesh;
        if (datainputstream.readByte() != 0)
        {
            flag = true;
        }
        isMasked = flag;
        numVertices = datainputstream.readInt();
        vertexBuffer = new DirectByteBuffer(numVertices * 24);
        texCoordsBuffer = new DirectByteBuffer(numVertices * 8);
        indexBuffer = new DirectByteBuffer(numVertices * 4);
        vertexBuffer.read(datainputstream);
        texCoordsBuffer.read(datainputstream);
        indexBuffer.read(datainputstream);
        Debug.Log((new StringBuilder()).append("SLPolyMorphData: Loaded morph '").append(slvisualparamid).append("', vertices = ").append(numVertices).toString());
    }

    public void applyMorphData(SLMeshData slmeshdata, float f, GLTexture gltexture)
    {
        ByteBuffer bytebuffer = null;
        int k = 0;
        int i;
        int j;
        if (isMasked && gltexture != null)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        if (i != 0)
        {
            int l = gltexture.getWidth();
            int i1 = gltexture.getHeight();
            bytebuffer = gltexture.getExtraComponentsBuffer();
            i = l;
            j = i1;
            gltexture = bytebuffer;
            if (bytebuffer != null)
            {
                k = bytebuffer.position();
                gltexture = bytebuffer;
                j = i1;
                i = l;
            }
        } else
        {
            j = 0;
            i = 0;
            gltexture = bytebuffer;
        }
        OpenJPEG.applyMeshMorph(f, slmeshdata.vertexBuffer.asByteBuffer(), slmeshdata.texCoordsBuffer.asByteBuffer(), numVertices, indexBuffer.asByteBuffer(), vertexBuffer.asByteBuffer(), texCoordsBuffer.asByteBuffer(), i, j, k, gltexture);
    }

    public void applyMorphDataSlow(SLMeshData slmeshdata, float f, GLTexture gltexture)
    {
        FloatBuffer floatbuffer = vertexBuffer.asFloatBuffer();
        FloatBuffer floatbuffer1 = texCoordsBuffer.asFloatBuffer();
        IntBuffer intbuffer = indexBuffer.asIntBuffer();
        FloatBuffer floatbuffer2 = slmeshdata.vertexBuffer.asFloatBuffer();
        FloatBuffer floatbuffer3 = slmeshdata.texCoordsBuffer.asFloatBuffer();
        int i;
        int j;
        int k;
        int l;
        boolean flag;
        int i1;
        if (isMasked && gltexture != null)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        l = 0;
        k = 0;
        slmeshdata = null;
        i1 = 0;
        j = i1;
        flag = i;
        if (i != 0)
        {
            l = gltexture.getWidth();
            k = gltexture.getHeight();
            slmeshdata = gltexture.getExtraComponentsBuffer();
            if (slmeshdata != null)
            {
                j = slmeshdata.position();
                flag = i;
            } else
            {
                flag = false;
                j = i1;
            }
        }
        i = 0;
        while (i < numVertices) 
        {
            int k1 = intbuffer.get(i);
            float f1;
            if (flag)
            {
                f1 = ((float)(slmeshdata.get((int)Math.floor(floatbuffer3.get(k1 * 2 + 0) * (float)l) + ((int)Math.floor(floatbuffer3.get(k1 * 2 + 1) * (float)k) * l + j)) & 0xff) / 255F) * f;
            } else
            {
                f1 = f;
            }
            for (i1 = 0; i1 < 6; i1++)
            {
                floatbuffer2.put(k1 * 6 + i1, floatbuffer2.get(k1 * 6 + i1) + floatbuffer.get(i * 6 + i1) * f1);
            }

            for (int j1 = 0; j1 < 2; j1++)
            {
                floatbuffer3.put(k1 * 2 + j1, floatbuffer3.get(k1 * 2 + j1) + floatbuffer1.get(i * 2 + j1) * f1);
            }

            i++;
        }
    }
}
