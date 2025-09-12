// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshFace;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolume;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import com.lumiyaviewer.lumiya.slproto.types.VertexArray;
import com.lumiyaviewer.lumiya.utils.CreateFailureException;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public final class DrawableGeometry
    implements GLCleanable
{

    private final int FaceCount;
    private final int FaceIndexStartsCounts[];
    private final int FaceVertexStartsCounts[];
    private final GLLoadableBuffer IndexBuffer;
    private final int IndexCount;
    private final int IndexSizeBytes;
    private final GLLoadableBuffer TexCoordsBuffer;
    private final GLLoadableBuffer VertexBuffer;
    private final int VertexCount;
    private final int VertexSizeBytes;
    private final boolean facesCombined;
    private final boolean isRiggedMesh;
    private final MeshData meshData;
    private GLVertexArrayObject vertexArrayObject;

    public DrawableGeometry(MeshData meshdata)
        throws CreateFailureException
    {
        vertexArrayObject = null;
        isRiggedMesh = meshdata.isRiggedMesh();
        FaceCount = meshdata.getFaceCount();
        int l = 0;
        int k = 0;
        for (int i = 0; i < FaceCount;)
        {
            MeshFace meshface = meshdata.getFace(i);
            int l1 = k;
            int j1 = l;
            if (meshface.getVertices() != null)
            {
                j1 = l + meshface.getNumVertices();
                l1 = k + meshface.getNumIndices();
            }
            i++;
            k = l1;
            l = j1;
        }

        IndexCount = k;
        VertexCount = l;
        if (k == 0 || l == 0)
        {
            throw new CreateFailureException("Mesh data has zero indices or vertices");
        }
        FaceIndexStartsCounts = new int[FaceCount * 3];
        FaceVertexStartsCounts = new int[FaceCount * 2];
        VertexSizeBytes = l * 4 * 6;
        IndexSizeBytes = k * 2;
        DirectByteBuffer directbytebuffer = new DirectByteBuffer(VertexSizeBytes);
        DirectByteBuffer directbytebuffer1 = new DirectByteBuffer(IndexSizeBytes);
        DirectByteBuffer directbytebuffer2 = new DirectByteBuffer(l * 4 * 2);
        int j = 0;
        k = 0;
        int i2 = 0;
        int k1 = 0;
        facesCombined = false;
        for (int i1 = 0; i1 < FaceCount; i1++)
        {
            MeshFace meshface1 = meshdata.getFace(i1);
            DirectByteBuffer directbytebuffer3 = meshface1.getVertices();
            DirectByteBuffer directbytebuffer4 = meshface1.getTexCoords();
            int l2 = meshface1.getNumVertices();
            if (meshface1.getNumVertices() == 0 || meshface1.getNumIndices() == 0)
            {
                throw new CreateFailureException("Empty mesh");
            }
            if (directbytebuffer3 != null)
            {
                directbytebuffer.copyFromFloat(k * 6, directbytebuffer3, 0, l2 * 6);
                if (directbytebuffer4 != null)
                {
                    directbytebuffer2.copyFromFloat(k * 2, directbytebuffer4, 0, l2 * 2);
                }
                directbytebuffer3 = meshface1.getIndices();
                int i3 = meshface1.getNumIndices();
                for (int j2 = 0; j2 < i3; j2++)
                {
                    if ((directbytebuffer3.getShort(j2) & 0xffff) >= l2)
                    {
                        throw new CreateFailureException("Too many vertices");
                    }
                }

                directbytebuffer1.copyFromShort(j, meshface1.getIndices(), 0, meshface1.getNumIndices());
            }
            int ai[] = FaceIndexStartsCounts;
            int k2 = i2 + 1;
            ai[i2] = i1;
            ai = FaceIndexStartsCounts;
            int j3 = k2 + 1;
            ai[k2] = j;
            ai = FaceIndexStartsCounts;
            i2 = j3 + 1;
            ai[j3] = meshface1.getNumIndices();
            ai = FaceVertexStartsCounts;
            k2 = k1 + 1;
            ai[k1] = k;
            ai = FaceVertexStartsCounts;
            k1 = k2 + 1;
            ai[k2] = l2;
            k += l2;
            j += meshface1.getNumIndices();
        }

        directbytebuffer.position(0);
        directbytebuffer1.position(0);
        directbytebuffer2.position(0);
        VertexBuffer = new GLLoadableBuffer(directbytebuffer);
        IndexBuffer = new GLLoadableBuffer(directbytebuffer1);
        TexCoordsBuffer = new GLLoadableBuffer(directbytebuffer2);
        Debug.Printf("Mesh drawable created,  index count %d, vertex count %d", new Object[] {
            Integer.valueOf(IndexCount), Integer.valueOf(VertexCount)
        });
        if (isRiggedMesh)
        {
            meshData = meshdata;
            return;
        } else
        {
            meshData = null;
            return;
        }
    }

    public DrawableGeometry(PrimVolumeParams primvolumeparams, OpenJPEG openjpeg)
        throws CreateFailureException
    {
        vertexArrayObject = null;
        Object obj = PrimVolume.create(primvolumeparams, 4F, false, false, openjpeg);
        if (obj == null)
        {
            throw new CreateFailureException("Failed to create volume");
        }
        isRiggedMesh = false;
        meshData = null;
        primvolumeparams = ((PrimVolume) (obj)).VolumeFaces.iterator();
        int i = 0;
        int l = 0;
        while (primvolumeparams.hasNext()) 
        {
            openjpeg = (PrimVolumeFace)primvolumeparams.next();
            l += ((PrimVolumeFace) (openjpeg)).NumVertices;
            i = ((PrimVolumeFace) (openjpeg)).NumIndices + i;
        }
        IndexCount = i;
        VertexCount = l;
        if (i == 0 || l == 0)
        {
            throw new CreateFailureException("Prim data has zero indices or vertices");
        }
        FaceCount = ((PrimVolume) (obj)).VolumeFaces.size();
        FaceIndexStartsCounts = new int[FaceCount * 3];
        FaceVertexStartsCounts = new int[FaceCount * 2];
        VertexSizeBytes = l * 4 * 6;
        IndexSizeBytes = i * 2;
        primvolumeparams = new DirectByteBuffer(VertexSizeBytes);
        openjpeg = new DirectByteBuffer(IndexSizeBytes);
        DirectByteBuffer directbytebuffer = new DirectByteBuffer(l * 4 * 2);
        boolean flag;
        if (l < 32767 && i < 32767)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        facesCombined = flag;
        if (facesCombined)
        {
            obj = ((PrimVolume) (obj)).VolumeFaces.iterator();
            short word0 = 0;
            int i1 = 0;
            int k1 = 0;
            int j = 0;
            while (((Iterator) (obj)).hasNext()) 
            {
                PrimVolumeFace primvolumeface = (PrimVolumeFace)((Iterator) (obj)).next();
                primvolumeparams.loadFromFloatArray(word0 * 6, primvolumeface.vertexArray.getData(), 0, primvolumeface.NumVertices * 6);
                directbytebuffer.loadFromFloatArray(word0 * 2, primvolumeface.vertexArray.getTexCoordsData(), 0, primvolumeface.NumVertices * 2);
                openjpeg.loadFromShortArrayOffset(j, primvolumeface.Indices, 0, primvolumeface.NumIndices, word0);
                int ai[] = FaceIndexStartsCounts;
                int i2 = k1 + 1;
                ai[k1] = primvolumeface.ID;
                ai = FaceIndexStartsCounts;
                k1 = i2 + 1;
                ai[i2] = j;
                FaceIndexStartsCounts[k1] = primvolumeface.NumIndices;
                ai = FaceVertexStartsCounts;
                i2 = i1 + 1;
                ai[i1] = word0;
                FaceVertexStartsCounts[i2] = primvolumeface.NumVertices;
                word0 += primvolumeface.NumVertices;
                j += primvolumeface.NumIndices;
                i1 = i2 + 1;
                k1++;
            }
        } else
        {
            obj = ((PrimVolume) (obj)).VolumeFaces.iterator();
            int j1 = 0;
            int l1 = 0;
            int j2 = 0;
            PrimVolumeFace primvolumeface1;
            for (int k = 0; ((Iterator) (obj)).hasNext(); k = primvolumeface1.NumIndices + k)
            {
                primvolumeface1 = (PrimVolumeFace)((Iterator) (obj)).next();
                primvolumeparams.loadFromFloatArray(j1 * 6, primvolumeface1.vertexArray.getData(), 0, primvolumeface1.NumVertices * 6);
                directbytebuffer.loadFromFloatArray(j1 * 2, primvolumeface1.vertexArray.getTexCoordsData(), 0, primvolumeface1.NumVertices * 2);
                openjpeg.loadFromShortArray(k, primvolumeface1.Indices, 0, primvolumeface1.NumIndices);
                int ai1[] = FaceIndexStartsCounts;
                int k2 = j2 + 1;
                ai1[j2] = primvolumeface1.ID;
                ai1 = FaceIndexStartsCounts;
                int l2 = k2 + 1;
                ai1[k2] = k;
                ai1 = FaceIndexStartsCounts;
                j2 = l2 + 1;
                ai1[l2] = primvolumeface1.NumIndices;
                ai1 = FaceVertexStartsCounts;
                k2 = l1 + 1;
                ai1[l1] = j1;
                ai1 = FaceVertexStartsCounts;
                l1 = k2 + 1;
                ai1[k2] = primvolumeface1.NumVertices;
                j1 += primvolumeface1.NumVertices;
            }

        }
        primvolumeparams.position(0);
        openjpeg.position(0);
        directbytebuffer.position(0);
        VertexBuffer = new GLLoadableBuffer(primvolumeparams);
        IndexBuffer = new GLLoadableBuffer(openjpeg);
        TexCoordsBuffer = new GLLoadableBuffer(directbytebuffer);
    }

    final void ApplyJointTranslations(MeshJointTranslations meshjointtranslations)
    {
        if (isRiggedMesh())
        {
            MeshData meshdata = meshData;
            if (meshdata != null && meshdata.isRiggedMesh())
            {
                meshdata.ApplyJointTranslations(meshjointtranslations);
            }
        }
    }

    final GLLoadableBuffer GLBindBuffers10(RenderContext rendercontext, PrimFlexibleInfo primflexibleinfo)
    {
        if (primflexibleinfo != null)
        {
            primflexibleinfo = primflexibleinfo.getFlexedVertexBuffer(rendercontext, VertexBuffer, VertexCount);
        } else
        {
            primflexibleinfo = VertexBuffer;
        }
        if (facesCombined)
        {
            primflexibleinfo.Bind(rendercontext, 32884, 3, 5126, 24, 0);
            primflexibleinfo.Bind(rendercontext, 32885, 3, 5126, 24, 12);
            TexCoordsBuffer.Bind(rendercontext, 32888, 2, 5126, 8, 0);
        }
        IndexBuffer.BindElements(rendercontext);
        return primflexibleinfo;
    }

    final GLLoadableBuffer GLBindBuffers20(RenderContext rendercontext)
    {
        if (rendercontext.hasGL30)
        {
            if (vertexArrayObject == null)
            {
                if (facesCombined)
                {
                    vertexArrayObject = new GLVertexArrayObject(rendercontext.glResourceManager, 1);
                    rendercontext.glResourceManager.addCleanable(this);
                    vertexArrayObject.Bind(0);
                    VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vPosition, 3, 5126, 24, 0);
                    VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vNormal, 3, 5126, 24, 12);
                    TexCoordsBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vTexCoord, 2, 5126, 8, 0);
                    IndexBuffer.BindElements20(rendercontext);
                    vertexArrayObject.Unbind();
                } else
                {
                    vertexArrayObject = new GLVertexArrayObject(rendercontext.glResourceManager, FaceCount);
                    rendercontext.glResourceManager.addCleanable(this);
                    for (int i = 0; i < FaceCount; i++)
                    {
                        vertexArrayObject.Bind(i);
                        VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vPosition, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24);
                        VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vNormal, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24 + 12);
                        TexCoordsBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vTexCoord, 2, 5126, 8, FaceVertexStartsCounts[i * 2] * 4 * 2);
                        if (isRiggedMesh && meshData != null)
                        {
                            meshData.PrepareInfluencesForFace(rendercontext, FaceVertexStartsCounts[i * 2]);
                        }
                        IndexBuffer.BindElements20(rendercontext);
                    }

                    vertexArrayObject.Unbind();
                }
            }
            return VertexBuffer;
        }
        if (facesCombined)
        {
            VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vPosition, 3, 5126, 24, 0);
            VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vNormal, 3, 5126, 24, 12);
            TexCoordsBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vTexCoord, 2, 5126, 8, 0);
        }
        IndexBuffer.BindElements20(rendercontext);
        return VertexBuffer;
    }

    final void GLBindBuffersRigged30(RenderContext rendercontext)
    {
        if (isRiggedMesh && meshData != null)
        {
            meshData.SetupBuffers30(rendercontext);
            if (vertexArrayObject == null)
            {
                vertexArrayObject = new GLVertexArrayObject(rendercontext.glResourceManager, FaceCount);
                rendercontext.glResourceManager.addCleanable(this);
                for (int i = 0; i < FaceCount; i++)
                {
                    vertexArrayObject.Bind(i);
                    VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vPosition, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24);
                    VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vNormal, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24 + 12);
                    TexCoordsBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vTexCoord, 2, 5126, 8, FaceVertexStartsCounts[i * 2] * 4 * 2);
                    meshData.SetupFace30(rendercontext, FaceVertexStartsCounts[i * 2]);
                    IndexBuffer.BindElements20(rendercontext);
                    vertexArrayObject.Unbind();
                }

            }
        }
    }

    public void GLCleanup()
    {
        vertexArrayObject = null;
    }

    final void GLDrawAll10(RenderContext rendercontext)
    {
        IndexBuffer.DrawElements(rendercontext, 4, IndexCount, 5123, 0);
    }

    final void GLDrawAll20(RenderContext rendercontext)
    {
        if (rendercontext.hasGL30)
        {
            if (vertexArrayObject != null)
            {
                vertexArrayObject.Bind(0);
                IndexBuffer.DrawElements20(4, IndexCount, 5123, 0);
                vertexArrayObject.Unbind();
            }
            return;
        } else
        {
            IndexBuffer.DrawElements20(4, IndexCount, 5123, 0);
            return;
        }
    }

    final void GLDrawFace10(RenderContext rendercontext, int i, GLLoadableBuffer glloadablebuffer)
    {
        int j = i * 3;
        if (!facesCombined)
        {
            glloadablebuffer.Bind(rendercontext, 32884, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24);
            glloadablebuffer.Bind(rendercontext, 32885, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24 + 12);
            TexCoordsBuffer.Bind(rendercontext, 32888, 2, 5126, 8, FaceVertexStartsCounts[i * 2] * 4 * 2);
        }
        IndexBuffer.DrawElements(rendercontext, 4, FaceIndexStartsCounts[j + 2], 5123, FaceIndexStartsCounts[j + 1] * 2);
    }

    final void GLDrawFace20(RenderContext rendercontext, int i)
    {
        int j = i * 3;
        if (rendercontext.hasGL30)
        {
            if (vertexArrayObject != null)
            {
                rendercontext = vertexArrayObject;
                if (facesCombined)
                {
                    i = 0;
                }
                rendercontext.Bind(i);
                IndexBuffer.DrawElements20(4, FaceIndexStartsCounts[j + 2], 5123, FaceIndexStartsCounts[j + 1] * 2);
                vertexArrayObject.Unbind();
            }
            return;
        }
        if (!facesCombined)
        {
            VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vPosition, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24);
            VertexBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vNormal, 3, 5126, 24, FaceVertexStartsCounts[i * 2] * 24 + 12);
            TexCoordsBuffer.Bind20(rendercontext, rendercontext.curPrimProgram.vTexCoord, 2, 5126, 8, FaceVertexStartsCounts[i * 2] * 4 * 2);
            if (isRiggedMesh && meshData != null)
            {
                meshData.PrepareInfluencesForFace(rendercontext, FaceVertexStartsCounts[i * 2]);
            }
        }
        IndexBuffer.DrawElements20(4, FaceIndexStartsCounts[j + 2], 5123, FaceIndexStartsCounts[j + 1] * 2);
    }

    final void GLDrawRiggedFace30(RenderContext rendercontext, int i)
    {
        if (vertexArrayObject != null)
        {
            int j = i * 3;
            vertexArrayObject.Bind(i);
            IndexBuffer.DrawElements20(4, FaceIndexStartsCounts[j + 2], 5123, FaceIndexStartsCounts[j + 1] * 2);
        }
    }

    IntersectInfo IntersectRay(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo rayintersectinfo = null;
        int l1 = -1;
        int k1 = 0;
        float f = 0.0F;
        LLVector3 allvector3[] = new LLVector3[3];
        for (int i = 0; i < 3; i++)
        {
            allvector3[i] = new LLVector3();
        }

        int j = 0;
        do
        {
            if (j >= FaceCount)
            {
                break;
            }
            int l = j * 3;
            int i3 = FaceIndexStartsCounts[l + 1];
            int j3 = FaceIndexStartsCounts[l + 2];
            for (int i1 = 0; i1 < j3;)
            {
                float f2;
                com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo rayintersectinfo1;
                int i2;
                int l2;
label0:
                {
                    i2 = 0;
                    while (i2 < 3) 
                    {
                        int k2;
                        if (facesCombined)
                        {
                            k2 = IndexBuffer.getShort(i3 + i1 + i2) * 6;
                        } else
                        {
                            k2 = (IndexBuffer.getShort(i3 + i1 + i2) + FaceVertexStartsCounts[j * 2]) * 6;
                        }
                        allvector3[i2].set(VertexBuffer.getFloat(k2 + 0), VertexBuffer.getFloat(k2 + 1), VertexBuffer.getFloat(k2 + 2));
                        i2++;
                    }
                    com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo rayintersectinfo2 = GLRayTrace.intersect_RayTriangle(llvector3, llvector3_1, allvector3, 0);
                    f2 = f;
                    l2 = k1;
                    i2 = l1;
                    rayintersectinfo1 = rayintersectinfo;
                    if (rayintersectinfo2 == null)
                    {
                        break label0;
                    }
                    float f4 = rayintersectinfo2.intersectPoint.w;
                    if (rayintersectinfo != null)
                    {
                        f2 = f;
                        l2 = k1;
                        i2 = l1;
                        rayintersectinfo1 = rayintersectinfo;
                        if (f4 >= f)
                        {
                            break label0;
                        }
                    }
                    f2 = f4;
                    l2 = j;
                    i2 = i1;
                    rayintersectinfo1 = rayintersectinfo2;
                }
                i1 += 3;
                f = f2;
                k1 = l2;
                l1 = i2;
                rayintersectinfo = rayintersectinfo1;
            }

            j++;
        } while (true);
        if (rayintersectinfo != null)
        {
            int j2 = FaceIndexStartsCounts[k1 * 3 + 1];
            llvector3 = new LLVector2[3];
            int k = 0;
            while (k < 3) 
            {
                int j1;
                if (facesCombined)
                {
                    j1 = IndexBuffer.getShort(j2 + l1 + k) * 2;
                } else
                {
                    j1 = (IndexBuffer.getShort(j2 + l1 + k) + FaceVertexStartsCounts[k1 * 2]) * 2;
                }
                llvector3[k] = new LLVector2(TexCoordsBuffer.getFloat(j1), TexCoordsBuffer.getFloat(j1 + 1));
                k++;
            }
            float f1 = ((LLVector2) (llvector3[1])).x;
            float f3 = ((LLVector2) (llvector3[0])).x;
            float f5 = rayintersectinfo.s;
            float f6 = ((LLVector2) (llvector3[2])).x;
            float f7 = ((LLVector2) (llvector3[0])).x;
            float f8 = rayintersectinfo.t;
            float f9 = ((LLVector2) (llvector3[0])).x;
            float f10 = ((LLVector2) (llvector3[1])).y;
            float f11 = ((LLVector2) (llvector3[0])).y;
            float f12 = rayintersectinfo.s;
            float f13 = ((LLVector2) (llvector3[2])).y;
            float f14 = ((LLVector2) (llvector3[0])).y;
            float f15 = rayintersectinfo.t;
            float f16 = ((LLVector2) (llvector3[0])).y;
            return new IntersectInfo(rayintersectinfo.intersectPoint, k1, (f1 - f3) * f5 + (f6 - f7) * f8 + f9, (f10 - f11) * f12 + (f13 - f14) * f15 + f16);
        } else
        {
            return null;
        }
    }

    final boolean UpdateRigged(RenderContext rendercontext, AvatarSkeleton avatarskeleton)
    {
        MeshData meshdata;
label0:
        {
            if (isRiggedMesh)
            {
                meshdata = meshData;
                if (meshdata != null && meshdata.isRiggedMesh())
                {
                    meshdata.UpdateRiggedMatrices(avatarskeleton);
                    if (rendercontext.hasGL20 && !(meshdata.riggingFitsGL20() ^ true))
                    {
                        break label0;
                    }
                    avatarskeleton = VertexBuffer.getRawBuffer();
                    for (int i = 0; i < FaceCount; i++)
                    {
                        meshdata.UpdateRigged(i, avatarskeleton, FaceVertexStartsCounts[i * 2]);
                    }

                    VertexBuffer.Reload(rendercontext);
                }
            }
            return false;
        }
        meshdata.PrepareInfluenceBuffers(rendercontext);
        return true;
    }

    final int getFaceCount()
    {
        return FaceCount;
    }

    public final int getFaceFirstVertex(int i)
    {
        return FaceVertexStartsCounts[i * 2];
    }

    final int getFaceID(int i)
    {
        return FaceIndexStartsCounts[i * 3];
    }

    public final int getFaceVertexCount(int i)
    {
        return FaceVertexStartsCounts[i * 2 + 1];
    }

    public final int getVertexCount()
    {
        return VertexCount;
    }

    final boolean hasExtendedBones()
    {
        if (meshData != null)
        {
            return meshData.hasExtendedBones();
        } else
        {
            return false;
        }
    }

    final boolean isFacesCombined()
    {
        return facesCombined;
    }

    final boolean isRiggedMesh()
    {
        return isRiggedMesh;
    }

    final boolean riggingFitsGL20()
    {
        if (isRiggedMesh && meshData != null)
        {
            return meshData.riggingFitsGL20();
        } else
        {
            return false;
        }
    }
}
