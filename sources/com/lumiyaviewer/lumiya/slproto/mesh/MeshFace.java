// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.mesh:
//            MeshWeightsBuffer

public class MeshFace
{

    private final DirectByteBuffer indexBuffer;
    private final int numIndices;
    private final int numVertices;
    private final DirectByteBuffer texCoordsBuffer;
    private final DirectByteBuffer vertexBuffer;
    private final DirectByteBuffer weightBuffer;

    MeshFace(LLSDNode llsdnode)
        throws LLSDException
    {
        if (llsdnode.keyExists("NoGeometry") || llsdnode.keyExists("Position") ^ true || llsdnode.keyExists("TriangleList") ^ true)
        {
            vertexBuffer = null;
            indexBuffer = null;
            weightBuffer = null;
            texCoordsBuffer = null;
            numIndices = 0;
            numVertices = 0;
            return;
        }
        byte abyte1[] = llsdnode.byKey("Position").asBinary();
        Object obj;
        Object obj1;
        Object obj2;
        LLVector2 llvector2;
        ShortBuffer shortbuffer;
        LLVector3 llvector3;
        LLVector3 llvector3_1;
        int i;
        if (llsdnode.keyExists("Normal"))
        {
            obj1 = llsdnode.byKey("Normal").asBinary();
        } else
        {
            obj1 = null;
        }
        if (llsdnode.keyExists("TexCoord0"))
        {
            obj = llsdnode.byKey("TexCoord0").asBinary();
        } else
        {
            obj = null;
        }
        numVertices = abyte1.length / 6;
        vertexBuffer = new DirectByteBuffer(numVertices * 6 * 4);
        llvector3 = new LLVector3(-0.5F, -0.5F, -0.5F);
        llvector3_1 = new LLVector3(0.5F, 0.5F, 0.5F);
        if (llsdnode.keyExists("PositionDomain"))
        {
            if (llsdnode.byKey("PositionDomain").keyExists("Min"))
            {
                LLSDNode llsdnode1 = llsdnode.byKey("PositionDomain").byKey("Min");
                llvector3.set((float)llsdnode1.byIndex(0).asDouble(), (float)llsdnode1.byIndex(1).asDouble(), (float)llsdnode1.byIndex(2).asDouble());
            }
            if (llsdnode.byKey("PositionDomain").keyExists("Max"))
            {
                LLSDNode llsdnode2 = llsdnode.byKey("PositionDomain").byKey("Max");
                llvector3_1.set((float)llsdnode2.byIndex(0).asDouble(), (float)llsdnode2.byIndex(1).asDouble(), (float)llsdnode2.byIndex(2).asDouble());
            }
        }
        llvector2 = null;
        obj2 = null;
        if (obj != null)
        {
            LLVector2 llvector2_1 = new LLVector2(0.0F, 0.0F);
            LLVector2 llvector2_2 = new LLVector2(0.0F, 0.0F);
            obj2 = llvector2_2;
            llvector2 = llvector2_1;
            if (llsdnode.keyExists("TexCoord0Domain"))
            {
                if (llsdnode.byKey("TexCoord0Domain").keyExists("Min"))
                {
                    obj2 = llsdnode.byKey("TexCoord0Domain").byKey("Min");
                    llvector2_1.set((float)((LLSDNode) (obj2)).byIndex(0).asDouble(), (float)((LLSDNode) (obj2)).byIndex(1).asDouble());
                }
                obj2 = llvector2_2;
                llvector2 = llvector2_1;
                if (llsdnode.byKey("TexCoord0Domain").keyExists("Max"))
                {
                    obj2 = llsdnode.byKey("TexCoord0Domain").byKey("Max");
                    llvector2_2.set((float)((LLSDNode) (obj2)).byIndex(0).asDouble(), (float)((LLSDNode) (obj2)).byIndex(1).asDouble());
                    llvector2 = llvector2_1;
                    obj2 = llvector2_2;
                }
            }
        }
        shortbuffer = ByteBuffer.wrap(abyte1).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        if (obj1 != null)
        {
            obj1 = ByteBuffer.wrap(((byte []) (obj1))).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        } else
        {
            obj1 = null;
        }
        if (obj != null)
        {
            obj = ByteBuffer.wrap(((byte []) (obj))).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        } else
        {
            obj = null;
        }
        vertexBuffer.position(0);
        i = 0;
        while (i < numVertices) 
        {
            float f = ((float)(shortbuffer.get() & 0xffff) * (llvector3_1.x - llvector3.x)) / 65535F;
            float f3 = llvector3.x;
            float f6 = ((float)(shortbuffer.get() & 0xffff) * (llvector3_1.y - llvector3.y)) / 65535F;
            float f9 = llvector3.y;
            float f11 = ((float)(shortbuffer.get() & 0xffff) * (llvector3_1.z - llvector3.z)) / 65535F;
            float f12 = llvector3.z;
            vertexBuffer.putFloat(f + f3);
            vertexBuffer.putFloat(f6 + f9);
            vertexBuffer.putFloat(f11 + f12);
            if (obj1 != null)
            {
                float f1 = ((float)(((ShortBuffer) (obj1)).get() & 0xffff) * 2.0F) / 65535F;
                float f4 = ((float)(((ShortBuffer) (obj1)).get() & 0xffff) * 2.0F) / 65535F;
                float f7 = ((float)(((ShortBuffer) (obj1)).get() & 0xffff) * 2.0F) / 65535F;
                vertexBuffer.putFloat(f1 - 1.0F);
                vertexBuffer.putFloat(f4 - 1.0F);
                vertexBuffer.putFloat(f7 - 1.0F);
            } else
            {
                vertexBuffer.putFloat(0.0F);
                vertexBuffer.putFloat(0.0F);
                vertexBuffer.putFloat(0.0F);
            }
            i++;
        }
        if (obj != null)
        {
            texCoordsBuffer = new DirectByteBuffer(numVertices * 2 * 4);
            texCoordsBuffer.position(0);
            for (int j = 0; j < numVertices; j++)
            {
                float f2 = ((float)(((ShortBuffer) (obj)).get() & 0xffff) * (((LLVector2) (obj2)).x - llvector2.x)) / 65535F;
                float f5 = llvector2.x;
                float f8 = ((float)(((ShortBuffer) (obj)).get() & 0xffff) * (((LLVector2) (obj2)).y - llvector2.y)) / 65535F;
                float f10 = llvector2.y;
                texCoordsBuffer.putFloat(f2 + f5);
                texCoordsBuffer.putFloat(f8 + f10);
            }

        } else
        {
            texCoordsBuffer = null;
        }
        byte abyte0[] = llsdnode.byKey("TriangleList").asBinary();
        numIndices = abyte0.length / 2;
        indexBuffer = new DirectByteBuffer(numIndices * 2);
        indexBuffer.loadFromByteArray(0, abyte0, 0, numIndices * 2);
        if (llsdnode.keyExists("Weights"))
        {
            llsdnode = llsdnode.byKey("Weights").asBinary();
            weightBuffer = new DirectByteBuffer(llsdnode.length);
            weightBuffer.loadFromByteArray(0, llsdnode, 0, llsdnode.length);
            return;
        } else
        {
            weightBuffer = null;
            return;
        }
    }

    void PrepareInfluenceBuffer(MeshWeightsBuffer meshweightsbuffer, int i)
    {
        OpenJPEG.meshPrepareSeparateInfluenceBuffer(weightBuffer.asByteBuffer(), numVertices, meshweightsbuffer.jointIndexBuffer.asByteBuffer(), meshweightsbuffer.weightsBuffer.asByteBuffer(), i);
    }

    void PrepareInfluenceBuffer(DirectByteBuffer directbytebuffer, int i)
    {
        if (weightBuffer != null)
        {
            OpenJPEG.meshPrepareInfluenceBuffer(weightBuffer.asByteBuffer(), numVertices, directbytebuffer.asByteBuffer(), i);
        }
    }

    final void UpdateRigged(DirectByteBuffer directbytebuffer, int i, float af[], float af1[])
    {
        if (weightBuffer != null && vertexBuffer != null && directbytebuffer != null)
        {
            OpenJPEG.applyRiggedMeshMorph(directbytebuffer.asByteBuffer(), i, af, af1, vertexBuffer.asByteBuffer(), weightBuffer.asByteBuffer(), numVertices);
        }
    }

    public final DirectByteBuffer getIndices()
    {
        return indexBuffer;
    }

    public final int getNumIndices()
    {
        return numIndices;
    }

    public final int getNumVertices()
    {
        return numVertices;
    }

    public final DirectByteBuffer getTexCoords()
    {
        return texCoordsBuffer;
    }

    public final DirectByteBuffer getVertices()
    {
        return vertexBuffer;
    }
}
