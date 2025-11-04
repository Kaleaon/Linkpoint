// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolume;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshFace;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.utils.CreateFailureException;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;

public final class DrawableGeometry implements GLCleanable
{
    private final int FaceCount;
    private final int[] FaceIndexStartsCounts;
    private final int[] FaceVertexStartsCounts;
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
    
    public DrawableGeometry(final MeshData meshData) throws CreateFailureException {
        this.vertexArrayObject = null;
        this.isRiggedMesh = meshData.isRiggedMesh();
        this.FaceCount = meshData.getFaceCount();
        int vertexCount = 0;
        int indexCount = 0;
        int n;
        int n2;
        for (int i = 0; i < this.FaceCount; ++i, indexCount = n, vertexCount = n2) {
            final MeshFace face = meshData.getFace(i);
            n = indexCount;
            n2 = vertexCount;
            if (face.getVertices() != null) {
                n2 = vertexCount + face.getNumVertices();
                n = indexCount + face.getNumIndices();
            }
        }
        this.IndexCount = indexCount;
        this.VertexCount = vertexCount;
        if (indexCount == 0 || vertexCount == 0) {
            throw new CreateFailureException("Mesh data has zero indices or vertices");
        }
        this.FaceIndexStartsCounts = new int[this.FaceCount * 3];
        this.FaceVertexStartsCounts = new int[this.FaceCount * 2];
        this.VertexSizeBytes = vertexCount * 4 * 6;
        this.IndexSizeBytes = indexCount * 2;
        final DirectByteBuffer directByteBuffer = new DirectByteBuffer(this.VertexSizeBytes);
        final DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(this.IndexSizeBytes);
        final DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(vertexCount * 4 * 2);
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        this.facesCombined = false;
        for (int j = 0; j < this.FaceCount; ++j) {
            final MeshFace face2 = meshData.getFace(j);
            final DirectByteBuffer vertices = face2.getVertices();
            final DirectByteBuffer texCoords = face2.getTexCoords();
            final int numVertices = face2.getNumVertices();
            if (face2.getNumVertices() == 0 || face2.getNumIndices() == 0) {
                throw new CreateFailureException("Empty mesh");
            }
            if (vertices != null) {
                directByteBuffer.copyFromFloat(n4 * 6, vertices, 0, numVertices * 6);
                if (texCoords != null) {
                    directByteBuffer3.copyFromFloat(n4 * 2, texCoords, 0, numVertices * 2);
                }
                final DirectByteBuffer indices = face2.getIndices();
                for (int numIndices = face2.getNumIndices(), k = 0; k < numIndices; ++k) {
                    if ((indices.getShort(k) & 0xFFFF) >= numVertices) {
                        throw new CreateFailureException("Too many vertices");
                    }
                }
                directByteBuffer2.copyFromShort(n3, face2.getIndices(), 0, face2.getNumIndices());
            }
            final int[] faceIndexStartsCounts = this.FaceIndexStartsCounts;
            final int n7 = n5 + 1;
            faceIndexStartsCounts[n5] = j;
            final int[] faceIndexStartsCounts2 = this.FaceIndexStartsCounts;
            final int n8 = n7 + 1;
            faceIndexStartsCounts2[n7] = n3;
            final int[] faceIndexStartsCounts3 = this.FaceIndexStartsCounts;
            n5 = n8 + 1;
            faceIndexStartsCounts3[n8] = face2.getNumIndices();
            final int[] faceVertexStartsCounts = this.FaceVertexStartsCounts;
            final int n9 = n6 + 1;
            faceVertexStartsCounts[n6] = n4;
            final int[] faceVertexStartsCounts2 = this.FaceVertexStartsCounts;
            n6 = n9 + 1;
            faceVertexStartsCounts2[n9] = numVertices;
            n4 += numVertices;
            n3 += face2.getNumIndices();
        }
        directByteBuffer.position(0);
        directByteBuffer2.position(0);
        directByteBuffer3.position(0);
        this.VertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.IndexBuffer = new GLLoadableBuffer(directByteBuffer2);
        this.TexCoordsBuffer = new GLLoadableBuffer(directByteBuffer3);
        Debug.Printf("Mesh drawable created,  index count %d, vertex count %d", this.IndexCount, this.VertexCount);
        if (this.isRiggedMesh) {
            this.meshData = meshData;
        }
        else {
            this.meshData = null;
        }
    }
    
    public DrawableGeometry(final PrimVolumeParams primVolumeParams, final OpenJPEG openJPEG) throws CreateFailureException {
        this.vertexArrayObject = null;
        final PrimVolume create = PrimVolume.create(primVolumeParams, 4.0f, false, false, openJPEG);
        if (create == null) {
            throw new CreateFailureException("Failed to create volume");
        }
        this.isRiggedMesh = false;
        this.meshData = null;
        final Iterator<Object> iterator = create.VolumeFaces.iterator();
        int indexCount = 0;
        int vertexCount = 0;
        while (iterator.hasNext()) {
            final PrimVolumeFace primVolumeFace = iterator.next();
            vertexCount += primVolumeFace.NumVertices;
            indexCount += primVolumeFace.NumIndices;
        }
        this.IndexCount = indexCount;
        this.VertexCount = vertexCount;
        if (indexCount == 0 || vertexCount == 0) {
            throw new CreateFailureException("Prim data has zero indices or vertices");
        }
        this.FaceCount = create.VolumeFaces.size();
        this.FaceIndexStartsCounts = new int[this.FaceCount * 3];
        this.FaceVertexStartsCounts = new int[this.FaceCount * 2];
        this.VertexSizeBytes = vertexCount * 4 * 6;
        this.IndexSizeBytes = indexCount * 2;
        final DirectByteBuffer directByteBuffer = new DirectByteBuffer(this.VertexSizeBytes);
        final DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(this.IndexSizeBytes);
        final DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(vertexCount * 4 * 2);
        this.facesCombined = (vertexCount < 32767 && indexCount < 32767);
        if (this.facesCombined) {
            final Iterator<Object> iterator2 = create.VolumeFaces.iterator();
            final short n = 0;
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            short n5 = n;
            while (iterator2.hasNext()) {
                final PrimVolumeFace primVolumeFace2 = iterator2.next();
                directByteBuffer.loadFromFloatArray(n5 * 6, primVolumeFace2.vertexArray.getData(), 0, primVolumeFace2.NumVertices * 6);
                directByteBuffer3.loadFromFloatArray(n5 * 2, primVolumeFace2.vertexArray.getTexCoordsData(), 0, primVolumeFace2.NumVertices * 2);
                directByteBuffer2.loadFromShortArrayOffset(n4, primVolumeFace2.Indices, 0, primVolumeFace2.NumIndices, n5);
                final int[] faceIndexStartsCounts = this.FaceIndexStartsCounts;
                final int n6 = n3 + 1;
                faceIndexStartsCounts[n3] = primVolumeFace2.ID;
                final int[] faceIndexStartsCounts2 = this.FaceIndexStartsCounts;
                n3 = n6 + 1;
                faceIndexStartsCounts2[n6] = n4;
                this.FaceIndexStartsCounts[n3] = primVolumeFace2.NumIndices;
                final int[] faceVertexStartsCounts = this.FaceVertexStartsCounts;
                final int n7 = n2 + 1;
                faceVertexStartsCounts[n2] = n5;
                this.FaceVertexStartsCounts[n7] = primVolumeFace2.NumVertices;
                final short n8 = (short)(n5 + primVolumeFace2.NumVertices);
                n4 += primVolumeFace2.NumIndices;
                n2 = n7 + 1;
                ++n3;
                n5 = n8;
            }
        }
        else {
            final Iterator<Object> iterator3 = create.VolumeFaces.iterator();
            int n9 = 0;
            int n10 = 0;
            int n11 = 0;
            int n12 = 0;
            while (iterator3.hasNext()) {
                final PrimVolumeFace primVolumeFace3 = iterator3.next();
                directByteBuffer.loadFromFloatArray(n9 * 6, primVolumeFace3.vertexArray.getData(), 0, primVolumeFace3.NumVertices * 6);
                directByteBuffer3.loadFromFloatArray(n9 * 2, primVolumeFace3.vertexArray.getTexCoordsData(), 0, primVolumeFace3.NumVertices * 2);
                directByteBuffer2.loadFromShortArray(n12, primVolumeFace3.Indices, 0, primVolumeFace3.NumIndices);
                final int[] faceIndexStartsCounts3 = this.FaceIndexStartsCounts;
                final int n13 = n11 + 1;
                faceIndexStartsCounts3[n11] = primVolumeFace3.ID;
                final int[] faceIndexStartsCounts4 = this.FaceIndexStartsCounts;
                final int n14 = n13 + 1;
                faceIndexStartsCounts4[n13] = n12;
                final int[] faceIndexStartsCounts5 = this.FaceIndexStartsCounts;
                n11 = n14 + 1;
                faceIndexStartsCounts5[n14] = primVolumeFace3.NumIndices;
                final int[] faceVertexStartsCounts2 = this.FaceVertexStartsCounts;
                final int n15 = n10 + 1;
                faceVertexStartsCounts2[n10] = n9;
                final int[] faceVertexStartsCounts3 = this.FaceVertexStartsCounts;
                n10 = n15 + 1;
                faceVertexStartsCounts3[n15] = primVolumeFace3.NumVertices;
                n9 += primVolumeFace3.NumVertices;
                n12 += primVolumeFace3.NumIndices;
            }
        }
        directByteBuffer.position(0);
        directByteBuffer2.position(0);
        directByteBuffer3.position(0);
        this.VertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.IndexBuffer = new GLLoadableBuffer(directByteBuffer2);
        this.TexCoordsBuffer = new GLLoadableBuffer(directByteBuffer3);
    }
    
    final void ApplyJointTranslations(final MeshJointTranslations meshJointTranslations) {
        if (this.isRiggedMesh()) {
            final MeshData meshData = this.meshData;
            if (meshData != null && meshData.isRiggedMesh()) {
                meshData.ApplyJointTranslations(meshJointTranslations);
            }
        }
    }
    
    final GLLoadableBuffer GLBindBuffers10(final RenderContext renderContext, final PrimFlexibleInfo primFlexibleInfo) {
        GLLoadableBuffer glLoadableBuffer;
        if (primFlexibleInfo != null) {
            glLoadableBuffer = primFlexibleInfo.getFlexedVertexBuffer(renderContext, this.VertexBuffer, this.VertexCount);
        }
        else {
            glLoadableBuffer = this.VertexBuffer;
        }
        if (this.facesCombined) {
            glLoadableBuffer.Bind(renderContext, 32884, 3, 5126, 24, 0);
            glLoadableBuffer.Bind(renderContext, 32885, 3, 5126, 24, 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, 0);
        }
        this.IndexBuffer.BindElements(renderContext);
        return glLoadableBuffer;
    }
    
    final GLLoadableBuffer GLBindBuffers20(final RenderContext renderContext) {
        if (renderContext.hasGL30) {
            if (this.vertexArrayObject == null) {
                if (this.facesCombined) {
                    this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, 1);
                    renderContext.glResourceManager.addCleanable(this);
                    this.vertexArrayObject.Bind(0);
                    this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0);
                    this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12);
                    this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0);
                    this.IndexBuffer.BindElements20(renderContext);
                    this.vertexArrayObject.Unbind();
                }
                else {
                    this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount);
                    renderContext.glResourceManager.addCleanable(this);
                    for (int i = 0; i < this.FaceCount; ++i) {
                        this.vertexArrayObject.Bind(i);
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24 + 12);
                        this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2);
                        if (this.isRiggedMesh && this.meshData != null) {
                            this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[i * 2]);
                        }
                        this.IndexBuffer.BindElements20(renderContext);
                    }
                    this.vertexArrayObject.Unbind();
                }
            }
            return this.VertexBuffer;
        }
        if (this.facesCombined) {
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0);
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12);
            this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0);
        }
        this.IndexBuffer.BindElements20(renderContext);
        return this.VertexBuffer;
    }
    
    final void GLBindBuffersRigged30(final RenderContext renderContext) {
        if (this.isRiggedMesh && this.meshData != null) {
            this.meshData.SetupBuffers30(renderContext);
            if (this.vertexArrayObject == null) {
                this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount);
                renderContext.glResourceManager.addCleanable(this);
                for (int i = 0; i < this.FaceCount; ++i) {
                    this.vertexArrayObject.Bind(i);
                    this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
                    this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24 + 12);
                    this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2);
                    this.meshData.SetupFace30(renderContext, this.FaceVertexStartsCounts[i * 2]);
                    this.IndexBuffer.BindElements20(renderContext);
                    this.vertexArrayObject.Unbind();
                }
            }
        }
    }
    
    @Override
    public void GLCleanup() {
        this.vertexArrayObject = null;
    }
    
    final void GLDrawAll10(final RenderContext renderContext) {
        this.IndexBuffer.DrawElements(renderContext, 4, this.IndexCount, 5123, 0);
    }
    
    final void GLDrawAll20(final RenderContext renderContext) {
        if (renderContext.hasGL30) {
            if (this.vertexArrayObject != null) {
                this.vertexArrayObject.Bind(0);
                this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
                this.vertexArrayObject.Unbind();
            }
        }
        else {
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
        }
    }
    
    final void GLDrawFace10(final RenderContext renderContext, final int n, final GLLoadableBuffer glLoadableBuffer) {
        final int n2 = n * 3;
        if (!this.facesCombined) {
            glLoadableBuffer.Bind(renderContext, 32884, 3, 5126, 24, this.FaceVertexStartsCounts[n * 2] * 24);
            glLoadableBuffer.Bind(renderContext, 32885, 3, 5126, 24, this.FaceVertexStartsCounts[n * 2] * 24 + 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, this.FaceVertexStartsCounts[n * 2] * 4 * 2);
        }
        this.IndexBuffer.DrawElements(renderContext, 4, this.FaceIndexStartsCounts[n2 + 2], 5123, this.FaceIndexStartsCounts[n2 + 1] * 2);
    }
    
    final void GLDrawFace20(final RenderContext renderContext, int n) {
        final int n2 = n * 3;
        if (renderContext.hasGL30) {
            if (this.vertexArrayObject != null) {
                final GLVertexArrayObject vertexArrayObject = this.vertexArrayObject;
                if (this.facesCombined) {
                    n = 0;
                }
                vertexArrayObject.Bind(n);
                this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[n2 + 2], 5123, this.FaceIndexStartsCounts[n2 + 1] * 2);
                this.vertexArrayObject.Unbind();
            }
        }
        else {
            if (!this.facesCombined) {
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[n * 2] * 24);
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, this.FaceVertexStartsCounts[n * 2] * 24 + 12);
                this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[n * 2] * 4 * 2);
                if (this.isRiggedMesh && this.meshData != null) {
                    this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[n * 2]);
                }
            }
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[n2 + 2], 5123, this.FaceIndexStartsCounts[n2 + 1] * 2);
        }
    }
    
    final void GLDrawRiggedFace30(final RenderContext renderContext, final int n) {
        if (this.vertexArrayObject != null) {
            final int n2 = n * 3;
            this.vertexArrayObject.Bind(n);
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[n2 + 2], 5123, this.FaceIndexStartsCounts[n2 + 1] * 2);
        }
    }
    
    IntersectInfo IntersectRay(final LLVector3 llVector3, final LLVector3 llVector4) {
        GLRayTrace.RayIntersectInfo rayIntersectInfo = null;
        int n = -1;
        int n2 = 0;
        float n3 = 0.0f;
        final LLVector3[] array = new LLVector3[3];
        for (int i = 0; i < 3; ++i) {
            array[i] = new LLVector3();
        }
        for (int j = 0; j < this.FaceCount; ++j) {
            final int n4 = j * 3;
            final int n5 = this.FaceIndexStartsCounts[n4 + 1];
            float n8;
            int n9;
            int n10;
            GLRayTrace.RayIntersectInfo rayIntersectInfo2;
            for (int n6 = this.FaceIndexStartsCounts[n4 + 2], k = 0; k < n6; k += 3, n3 = n8, n2 = n9, n = n10, rayIntersectInfo = rayIntersectInfo2) {
                for (int l = 0; l < 3; ++l) {
                    int n7;
                    if (this.facesCombined) {
                        n7 = this.IndexBuffer.getShort(n5 + k + l) * 6;
                    }
                    else {
                        n7 = (this.IndexBuffer.getShort(n5 + k + l) + this.FaceVertexStartsCounts[j * 2]) * 6;
                    }
                    array[l].set(this.VertexBuffer.getFloat(n7 + 0), this.VertexBuffer.getFloat(n7 + 1), this.VertexBuffer.getFloat(n7 + 2));
                }
                final GLRayTrace.RayIntersectInfo intersect_RayTriangle = GLRayTrace.intersect_RayTriangle(llVector3, llVector4, array, 0);
                n8 = n3;
                n9 = n2;
                n10 = n;
                rayIntersectInfo2 = rayIntersectInfo;
                if (intersect_RayTriangle != null) {
                    final float w = intersect_RayTriangle.intersectPoint.w;
                    if (rayIntersectInfo != null) {
                        n8 = n3;
                        n9 = n2;
                        n10 = n;
                        rayIntersectInfo2 = rayIntersectInfo;
                        if (w >= n3) {
                            continue;
                        }
                    }
                    n8 = w;
                    n9 = j;
                    n10 = k;
                    rayIntersectInfo2 = intersect_RayTriangle;
                }
            }
        }
        if (rayIntersectInfo != null) {
            final int n11 = this.FaceIndexStartsCounts[n2 * 3 + 1];
            final LLVector2[] array2 = new LLVector2[3];
            for (int n12 = 0; n12 < 3; ++n12) {
                int n13;
                if (this.facesCombined) {
                    n13 = this.IndexBuffer.getShort(n11 + n + n12) * 2;
                }
                else {
                    n13 = (this.IndexBuffer.getShort(n11 + n + n12) + this.FaceVertexStartsCounts[n2 * 2]) * 2;
                }
                array2[n12] = new LLVector2(this.TexCoordsBuffer.getFloat(n13), this.TexCoordsBuffer.getFloat(n13 + 1));
            }
            return new IntersectInfo(rayIntersectInfo.intersectPoint, n2, (array2[1].x - array2[0].x) * rayIntersectInfo.s + (array2[2].x - array2[0].x) * rayIntersectInfo.t + array2[0].x, (array2[1].y - array2[0].y) * rayIntersectInfo.s + (array2[2].y - array2[0].y) * rayIntersectInfo.t + array2[0].y);
        }
        return null;
    }
    
    final boolean UpdateRigged(final RenderContext renderContext, final AvatarSkeleton avatarSkeleton) {
        boolean b2;
        final boolean b = b2 = false;
        if (this.isRiggedMesh) {
            final MeshData meshData = this.meshData;
            b2 = b;
            if (meshData != null) {
                b2 = b;
                if (meshData.isRiggedMesh()) {
                    meshData.UpdateRiggedMatrices(avatarSkeleton);
                    if (!renderContext.hasGL20 || (meshData.riggingFitsGL20() ^ true)) {
                        final DirectByteBuffer rawBuffer = this.VertexBuffer.getRawBuffer();
                        for (int i = 0; i < this.FaceCount; ++i) {
                            meshData.UpdateRigged(i, rawBuffer, this.FaceVertexStartsCounts[i * 2]);
                        }
                        this.VertexBuffer.Reload(renderContext);
                        b2 = b;
                    }
                    else {
                        meshData.PrepareInfluenceBuffers(renderContext);
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
    
    final int getFaceCount() {
        return this.FaceCount;
    }
    
    public final int getFaceFirstVertex(final int n) {
        return this.FaceVertexStartsCounts[n * 2];
    }
    
    final int getFaceID(final int n) {
        return this.FaceIndexStartsCounts[n * 3];
    }
    
    public final int getFaceVertexCount(final int n) {
        return this.FaceVertexStartsCounts[n * 2 + 1];
    }
    
    public final int getVertexCount() {
        return this.VertexCount;
    }
    
    final boolean hasExtendedBones() {
        return this.meshData != null && this.meshData.hasExtendedBones();
    }
    
    final boolean isFacesCombined() {
        return this.facesCombined;
    }
    
    final boolean isRiggedMesh() {
        return this.isRiggedMesh;
    }
    
    final boolean riggingFitsGL20() {
        return this.isRiggedMesh && this.meshData != null && this.meshData.riggingFitsGL20();
    }
}
