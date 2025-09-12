// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLMeshData, SLVisualParamID, SLPolyMorphData, SLAnimatedMeshData

public class SLPolyMesh extends SLMeshData
{

    protected boolean hasWeights;
    public int jointMap[];
    private Map morphIndices;
    private SLPolyMorphData morphs[];
    protected DirectByteBuffer weightsBuffer;

    public SLPolyMesh(DataInputStream datainputstream, DataInputStream datainputstream1)
        throws IOException
    {
        boolean flag = false;
        super();
        morphIndices = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/SLVisualParamID);
        position = new LLVector3(datainputstream.readFloat(), datainputstream.readFloat(), datainputstream.readFloat());
        scale = new LLVector3(datainputstream.readFloat(), datainputstream.readFloat(), datainputstream.readFloat());
        rotation = new LLQuaternion(datainputstream.readFloat(), datainputstream.readFloat(), datainputstream.readFloat(), datainputstream.readFloat());
        Object obj;
        int i;
        int k;
        int i1;
        boolean flag1;
        if (datainputstream.readByte() != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        hasWeights = flag1;
        numVertices = datainputstream.readInt();
        vertexBuffer = new DirectByteBuffer(numVertices * 24);
        texCoordsBuffer = new DirectByteBuffer(numVertices * 8);
        vertexBuffer.read(datainputstream);
        texCoordsBuffer.read(datainputstream);
        if (hasWeights)
        {
            weightsBuffer = new DirectByteBuffer(numVertices * 4);
            weightsBuffer.read(datainputstream);
        }
        numFaces = datainputstream.readInt();
        indexBuffer = new DirectByteBuffer(numFaces * 2 * 3);
        indexBuffer.read(datainputstream);
        i1 = datainputstream.readInt();
        morphs = new SLPolyMorphData[i1];
        k = 0;
        i = 0;
        for (obj = datainputstream; k < i1; obj = datainputstream)
        {
            int l = i;
            datainputstream = ((DataInputStream) (obj));
            if (i >= 50)
            {
                l = i;
                datainputstream = ((DataInputStream) (obj));
                if (datainputstream1 != null)
                {
                    l = 0;
                    datainputstream = datainputstream1;
                }
            }
            obj = SLVisualParamID.values()[datainputstream.readInt()];
            SLPolyMorphData slpolymorphdata = new SLPolyMorphData(((SLVisualParamID) (obj)), this, datainputstream);
            morphs[k] = slpolymorphdata;
            morphIndices.put(obj, Integer.valueOf(k));
            k++;
            i = l + 1;
        }

        k = ((DataInputStream) (obj)).readInt();
        jointMap = new int[k];
        for (int j = ((flag) ? 1 : 0); j < k; j++)
        {
            jointMap[j] = ((DataInputStream) (obj)).readInt();
        }

        Debug.Log((new StringBuilder()).append("SLPolyMesh: Loaded, numVerts = ").append(numVertices).append(", faces = ").append(numFaces).append(", morphs = ").append(morphs.length).toString());
    }

    public void applyMorphData(SLMeshData slmeshdata, float af[], GLTexture gltexture)
    {
        for (int i = 0; i < af.length; i++)
        {
            morphs[i].applyMorphData(slmeshdata, af[i], gltexture);
        }

    }

    public void applySkeleton(SLAnimatedMeshData slanimatedmeshdata, float af[])
    {
        if (hasWeights && jointMap != null)
        {
            DirectByteBuffer directbytebuffer = slanimatedmeshdata.getAnimatedVertexData();
            if (directbytebuffer != null)
            {
                OpenJPEG.applyMorphingTransform(numVertices, slanimatedmeshdata.vertexBuffer.asByteBuffer(), directbytebuffer.asByteBuffer(), weightsBuffer.asByteBuffer(), jointMap, af);
            }
        }
    }

    public void applySkeletonSlow(SLAnimatedMeshData slanimatedmeshdata, float af[])
    {
        double d;
        FloatBuffer floatbuffer;
        Object obj;
        float af1[];
        float af2[];
        int k;
        if (!hasWeights || jointMap == null)
        {
            break MISSING_BLOCK_LABEL_402;
        }
        obj = slanimatedmeshdata.getAnimatedVertexData();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_402;
        }
        floatbuffer = weightsBuffer.asFloatBuffer();
        slanimatedmeshdata = slanimatedmeshdata.vertexBuffer.asFloatBuffer();
        obj = ((DirectByteBuffer) (obj)).asFloatBuffer();
        af1 = new float[16];
        af2 = new float[16];
        d = -1D;
        k = 0;
_L2:
        double d1;
        float f2;
        int l;
        int i1;
        if (k >= numVertices)
        {
            break MISSING_BLOCK_LABEL_402;
        }
        float f = floatbuffer.get(k);
        if ((double)f != d)
        {
            float f1 = (float)Math.floor(f);
            f2 = f - f1;
            i1 = (int)f1 - 1;
            l = 0;
            int i = l;
            if (i1 >= 0)
            {
                i = l;
                if (i1 < jointMap.length)
                {
                    i = jointMap[i1];
                }
            }
            if (i1 + 1 >= 0 && i1 + 1 < jointMap.length)
            {
                l = jointMap[i1 + 1];
            } else
            {
                l = i;
            }
            d1 = f;
            i1 = i * 16;
            l *= 16;
            if (i1 != l)
            {
                break; /* Loop/switch isn't completed */
            }
            System.arraycopy(af, i1, af2, 0, 16);
            d = d1;
        }
_L4:
        af1[0] = slanimatedmeshdata.get(k * 6 + 0);
        af1[1] = slanimatedmeshdata.get(k * 6 + 1);
        af1[2] = slanimatedmeshdata.get(k * 6 + 2);
        af1[3] = 1.0F;
        Matrix.multiplyMV(af1, 4, af2, 0, af1, 0);
        ((FloatBuffer) (obj)).put(k * 6 + 0, af1[4]);
        ((FloatBuffer) (obj)).put(k * 6 + 1, af1[5]);
        ((FloatBuffer) (obj)).put(k * 6 + 2, af1[6]);
        k++;
        if (true) goto _L2; else goto _L1
_L1:
        int j = 0;
_L5:
        d = d1;
        if (j >= 16) goto _L4; else goto _L3
_L3:
        af2[j] = af[i1 + j] * (1.0F - f2) + af[l + j] * f2;
        j++;
          goto _L5
          goto _L4
    }

    public int getMorphIndex(SLVisualParamID slvisualparamid)
    {
        slvisualparamid = (Integer)morphIndices.get(slvisualparamid);
        if (slvisualparamid == null)
        {
            return -1;
        } else
        {
            return slvisualparamid.intValue();
        }
    }

    public int getNumMorphs()
    {
        return morphs.length;
    }
}
