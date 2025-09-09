package com.lumiyaviewer.lumiya.render.drawable;

import android.support.v4.internal.view.SupportMenu;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshFace;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolume;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.CreateFailureException;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public final class DrawableGeometry implements GLCleanable {
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
    private GLVertexArrayObject vertexArrayObject = null;

    public DrawableGeometry(MeshData meshData) throws CreateFailureException {
        int i;
        this.isRiggedMesh = meshData.isRiggedMesh();
        this.FaceCount = meshData.getFaceCount();
        int i2 = 0;
        int i3 = 0;
        for (i = 0; i < this.FaceCount; i++) {
            MeshFace face = meshData.getFace(i);
            if (face.getVertices() != null) {
                i2 += face.getNumVertices();
                i3 += face.getNumIndices();
            }
        }
        this.IndexCount = i3;
        this.VertexCount = i2;
        if (i3 == 0 || i2 == 0) {
            throw new CreateFailureException("Mesh data has zero indices or vertices");
        }
        this.FaceIndexStartsCounts = new int[(this.FaceCount * 3)];
        this.FaceVertexStartsCounts = new int[(this.FaceCount * 2)];
        this.VertexSizeBytes = (i2 * 4) * 6;
        this.IndexSizeBytes = i3 * 2;
        i = (i2 * 4) * 2;
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(this.VertexSizeBytes);
        DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(this.IndexSizeBytes);
        DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(i);
        int i4 = 0;
        int i5 = 0;
        i2 = 0;
        i3 = 0;
        this.facesCombined = false;
        for (i = 0; i < this.FaceCount; i++) {
            MeshFace face2 = meshData.getFace(i);
            DirectByteBuffer vertices = face2.getVertices();
            DirectByteBuffer texCoords = face2.getTexCoords();
            int numVertices = face2.getNumVertices();
            if (face2.getNumVertices() == 0 || face2.getNumIndices() == 0) {
                throw new CreateFailureException("Empty mesh");
            }
            int i6;
            if (vertices != null) {
                directByteBuffer.copyFromFloat(i5 * 6, vertices, 0, numVertices * 6);
                if (texCoords != null) {
                    directByteBuffer3.copyFromFloat(i5 * 2, texCoords, 0, numVertices * 2);
                }
                texCoords = face2.getIndices();
                int numIndices = face2.getNumIndices();
                for (i6 = 0; i6 < numIndices; i6++) {
                    if ((texCoords.getShort(i6) & SupportMenu.USER_MASK) >= numVertices) {
                        throw new CreateFailureException("Too many vertices");
                    }
                }
                directByteBuffer2.copyFromShort(i4, face2.getIndices(), 0, face2.getNumIndices());
            }
            int i7 = i2 + 1;
            this.FaceIndexStartsCounts[i2] = i;
            i6 = i7 + 1;
            this.FaceIndexStartsCounts[i7] = i4;
            i2 = i6 + 1;
            this.FaceIndexStartsCounts[i6] = face2.getNumIndices();
            i7 = i3 + 1;
            this.FaceVertexStartsCounts[i3] = i5;
            i3 = i7 + 1;
            this.FaceVertexStartsCounts[i7] = numVertices;
            i5 += numVertices;
            i4 += face2.getNumIndices();
        }
        directByteBuffer.position(0);
        directByteBuffer2.position(0);
        directByteBuffer3.position(0);
        this.VertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.IndexBuffer = new GLLoadableBuffer(directByteBuffer2);
        this.TexCoordsBuffer = new GLLoadableBuffer(directByteBuffer3);
        Debug.Printf("Mesh drawable created,  index count %d, vertex count %d", Integer.valueOf(this.IndexCount), Integer.valueOf(this.VertexCount));
        if (this.isRiggedMesh) {
            this.meshData = meshData;
        } else {
            this.meshData = null;
        }
    }

    public DrawableGeometry(PrimVolumeParams primVolumeParams, OpenJPEG openJPEG) throws CreateFailureException {
        PrimVolume create = PrimVolume.create(primVolumeParams, 4.0f, false, false, openJPEG);
        if (create == null) {
            throw new CreateFailureException("Failed to create volume");
        }
        this.isRiggedMesh = false;
        this.meshData = null;
        int i = 0;
        int i2 = 0;
        for (PrimVolumeFace primVolumeFace : create.VolumeFaces) {
            i2 += primVolumeFace.NumVertices;
            i = primVolumeFace.NumIndices + i;
        }
        this.IndexCount = i;
        this.VertexCount = i2;
        if (i == 0 || i2 == 0) {
            throw new CreateFailureException("Prim data has zero indices or vertices");
        }
        this.FaceCount = create.VolumeFaces.size();
        this.FaceIndexStartsCounts = new int[(this.FaceCount * 3)];
        this.FaceVertexStartsCounts = new int[(this.FaceCount * 2)];
        this.VertexSizeBytes = (i2 * 4) * 6;
        this.IndexSizeBytes = i * 2;
        int i3 = (i2 * 4) * 2;
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(this.VertexSizeBytes);
        DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(this.IndexSizeBytes);
        DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(i3);
        boolean z = i2 < 32767 && i < 32767;
        this.facesCombined = z;
        int i4;
        int i5;
        if (this.facesCombined) {
            short s = (short) 0;
            int i6 = 0;
            i4 = 0;
            i = 0;
            for (PrimVolumeFace primVolumeFace2 : create.VolumeFaces) {
                directByteBuffer.loadFromFloatArray(s * 6, primVolumeFace2.vertexArray.getData(), 0, primVolumeFace2.NumVertices * 6);
                directByteBuffer3.loadFromFloatArray(s * 2, primVolumeFace2.vertexArray.getTexCoordsData(), 0, primVolumeFace2.NumVertices * 2);
                directByteBuffer2.loadFromShortArrayOffset(i, primVolumeFace2.Indices, 0, primVolumeFace2.NumIndices, s);
                i5 = i4 + 1;
                this.FaceIndexStartsCounts[i4] = primVolumeFace2.ID;
                i4 = i5 + 1;
                this.FaceIndexStartsCounts[i5] = i;
                i5 = i4 + 1;
                this.FaceIndexStartsCounts[i4] = primVolumeFace2.NumIndices;
                i4 = i6 + 1;
                this.FaceVertexStartsCounts[i6] = s;
                i2 = i4 + 1;
                this.FaceVertexStartsCounts[i4] = primVolumeFace2.NumVertices;
                s = (short) (s + primVolumeFace2.NumVertices);
                i += primVolumeFace2.NumIndices;
                i6 = i2;
                i4 = i5;
            }
        } else {
            i2 = 0;
            i5 = 0;
            i3 = 0;
            int i7 = 0;
            for (PrimVolumeFace primVolumeFace3 : create.VolumeFaces) {
                directByteBuffer.loadFromFloatArray(i2 * 6, primVolumeFace3.vertexArray.getData(), 0, primVolumeFace3.NumVertices * 6);
                directByteBuffer3.loadFromFloatArray(i2 * 2, primVolumeFace3.vertexArray.getTexCoordsData(), 0, primVolumeFace3.NumVertices * 2);
                directByteBuffer2.loadFromShortArray(i7, primVolumeFace3.Indices, 0, primVolumeFace3.NumIndices);
                int i8 = i3 + 1;
                this.FaceIndexStartsCounts[i3] = primVolumeFace3.ID;
                i4 = i8 + 1;
                this.FaceIndexStartsCounts[i8] = i7;
                i3 = i4 + 1;
                this.FaceIndexStartsCounts[i4] = primVolumeFace3.NumIndices;
                i8 = i5 + 1;
                this.FaceVertexStartsCounts[i5] = i2;
                i5 = i8 + 1;
                this.FaceVertexStartsCounts[i8] = primVolumeFace3.NumVertices;
                i2 += primVolumeFace3.NumVertices;
                i7 = primVolumeFace3.NumIndices + i7;
            }
        }
        directByteBuffer.position(0);
        directByteBuffer2.position(0);
        directByteBuffer3.position(0);
        this.VertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.IndexBuffer = new GLLoadableBuffer(directByteBuffer2);
        this.TexCoordsBuffer = new GLLoadableBuffer(directByteBuffer3);
    }

    final void ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        if (isRiggedMesh()) {
            MeshData meshData = this.meshData;
            if (meshData != null && meshData.isRiggedMesh()) {
                meshData.ApplyJointTranslations(meshJointTranslations);
            }
        }
    }

    final GLLoadableBuffer GLBindBuffers10(RenderContext renderContext, PrimFlexibleInfo primFlexibleInfo) {
        GLLoadableBuffer flexedVertexBuffer = primFlexibleInfo != null ? primFlexibleInfo.getFlexedVertexBuffer(renderContext, this.VertexBuffer, this.VertexCount) : this.VertexBuffer;
        if (this.facesCombined) {
            flexedVertexBuffer.Bind(renderContext, 32884, 3, 5126, 24, 0);
            flexedVertexBuffer.Bind(renderContext, 32885, 3, 5126, 24, 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, 0);
        }
        this.IndexBuffer.BindElements(renderContext);
        return flexedVertexBuffer;
    }

    final GLLoadableBuffer GLBindBuffers20(RenderContext renderContext) {
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
                } else {
                    this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount);
                    renderContext.glResourceManager.addCleanable(this);
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 >= this.FaceCount) {
                            break;
                        }
                        this.vertexArrayObject.Bind(i2);
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i2 * 2] * 24);
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i2 * 2] * 24) + 12);
                        this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, (this.FaceVertexStartsCounts[i2 * 2] * 4) * 2);
                        if (this.isRiggedMesh && this.meshData != null) {
                            this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[i2 * 2]);
                        }
                        this.IndexBuffer.BindElements20(renderContext);
                        i = i2 + 1;
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

    final void GLBindBuffersRigged30(RenderContext renderContext) {
        if (this.isRiggedMesh && this.meshData != null) {
            this.meshData.SetupBuffers30(renderContext);
            if (this.vertexArrayObject == null) {
                this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount);
                renderContext.glResourceManager.addCleanable(this);
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < this.FaceCount) {
                        this.vertexArrayObject.Bind(i2);
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i2 * 2] * 24);
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i2 * 2] * 24) + 12);
                        this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, (this.FaceVertexStartsCounts[i2 * 2] * 4) * 2);
                        this.meshData.SetupFace30(renderContext, this.FaceVertexStartsCounts[i2 * 2]);
                        this.IndexBuffer.BindElements20(renderContext);
                        this.vertexArrayObject.Unbind();
                        i = i2 + 1;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void GLCleanup() {
        this.vertexArrayObject = null;
    }

    final void GLDrawAll10(RenderContext renderContext) {
        this.IndexBuffer.DrawElements(renderContext, 4, this.IndexCount, 5123, 0);
    }

    final void GLDrawAll20(RenderContext renderContext) {
        if (!renderContext.hasGL30) {
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
        } else if (this.vertexArrayObject != null) {
            this.vertexArrayObject.Bind(0);
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
            this.vertexArrayObject.Unbind();
        }
    }

    final void GLDrawFace10(RenderContext renderContext, int i, GLLoadableBuffer gLLoadableBuffer) {
        int i2 = i * 3;
        if (!this.facesCombined) {
            gLLoadableBuffer.Bind(renderContext, 32884, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
            gLLoadableBuffer.Bind(renderContext, 32885, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, (this.FaceVertexStartsCounts[i * 2] * 4) * 2);
        }
        this.IndexBuffer.DrawElements(renderContext, 4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
    }

    final void GLDrawFace20(RenderContext renderContext, int i) {
        int i2 = i * 3;
        if (!renderContext.hasGL30) {
            if (!this.facesCombined) {
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12);
                this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, (this.FaceVertexStartsCounts[i * 2] * 4) * 2);
                if (this.isRiggedMesh && this.meshData != null) {
                    this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[i * 2]);
                }
            }
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
        } else if (this.vertexArrayObject != null) {
            GLVertexArrayObject gLVertexArrayObject = this.vertexArrayObject;
            if (this.facesCombined) {
                i = 0;
            }
            gLVertexArrayObject.Bind(i);
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
            this.vertexArrayObject.Unbind();
        }
    }

    final void GLDrawRiggedFace30(RenderContext renderContext, int i) {
        if (this.vertexArrayObject != null) {
            int i2 = i * 3;
            this.vertexArrayObject.Bind(i);
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
        }
    }

    IntersectInfo IntersectRay(LLVector3 lLVector3, LLVector3 lLVector32) {
        int i;
        int i2;
        RayIntersectInfo rayIntersectInfo = null;
        int i3 = -1;
        int i4 = 0;
        float f = 0.0f;
        LLVector3[] lLVector3Arr = new LLVector3[3];
        for (i = 0; i < 3; i++) {
            lLVector3Arr[i] = new LLVector3();
        }
        for (i = 0; i < this.FaceCount; i++) {
            i2 = i * 3;
            int i5 = this.FaceIndexStartsCounts[i2 + 1];
            int i6 = this.FaceIndexStartsCounts[i2 + 2];
            for (int i7 = 0; i7 < i6; i7 += 3) {
                i2 = 0;
                while (true) {
                    int i8 = i2;
                    if (i8 >= 3) {
                        break;
                    }
                    i2 = (this.facesCombined ? this.IndexBuffer.getShort((i5 + i7) + i8) : this.IndexBuffer.getShort((i5 + i7) + i8) + this.FaceVertexStartsCounts[i * 2]) * 6;
                    lLVector3Arr[i8].set(this.VertexBuffer.getFloat(i2 + 0), this.VertexBuffer.getFloat(i2 + 1), this.VertexBuffer.getFloat(i2 + 2));
                    i2 = i8 + 1;
                }
                RayIntersectInfo intersect_RayTriangle = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, 0);
                if (intersect_RayTriangle != null) {
                    float f2 = intersect_RayTriangle.intersectPoint.w;
                    if (rayIntersectInfo == null || f2 < f) {
                        f = f2;
                        i4 = i;
                        i3 = i7;
                        rayIntersectInfo = intersect_RayTriangle;
                    }
                }
            }
        }
        if (rayIntersectInfo == null) {
            return null;
        }
        i2 = this.FaceIndexStartsCounts[(i4 * 3) + 1];
        LLVector2[] lLVector2Arr = new LLVector2[3];
        i = 0;
        while (true) {
            int i9 = i;
            if (i9 < 3) {
                i = (this.facesCombined ? this.IndexBuffer.getShort((i2 + i3) + i9) : this.IndexBuffer.getShort((i2 + i3) + i9) + this.FaceVertexStartsCounts[i4 * 2]) * 2;
                lLVector2Arr[i9] = new LLVector2(this.TexCoordsBuffer.getFloat(i), this.TexCoordsBuffer.getFloat(i + 1));
                i = i9 + 1;
            } else {
                return new IntersectInfo(rayIntersectInfo.intersectPoint, i4, (((lLVector2Arr[1].x - lLVector2Arr[0].x) * rayIntersectInfo.s) + ((lLVector2Arr[2].x - lLVector2Arr[0].x) * rayIntersectInfo.t)) + lLVector2Arr[0].x, (((lLVector2Arr[1].y - lLVector2Arr[0].y) * rayIntersectInfo.s) + ((lLVector2Arr[2].y - lLVector2Arr[0].y) * rayIntersectInfo.t)) + lLVector2Arr[0].y);
            }
        }
    }

    final boolean UpdateRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton) {
        if (!this.isRiggedMesh) {
            return false;
        }
        MeshData meshData = this.meshData;
        if (meshData == null || !meshData.isRiggedMesh()) {
            return false;
        }
        meshData.UpdateRiggedMatrices(avatarSkeleton);
        if (renderContext.hasGL20 && (meshData.riggingFitsGL20() ^ 1) == 0) {
            meshData.PrepareInfluenceBuffers(renderContext);
            return true;
        }
        DirectByteBuffer rawBuffer = this.VertexBuffer.getRawBuffer();
        for (int i = 0; i < this.FaceCount; i++) {
            meshData.UpdateRigged(i, rawBuffer, this.FaceVertexStartsCounts[i * 2]);
        }
        this.VertexBuffer.Reload(renderContext);
        return false;
    }

    final int getFaceCount() {
        return this.FaceCount;
    }

    public final int getFaceFirstVertex(int i) {
        return this.FaceVertexStartsCounts[i * 2];
    }

    final int getFaceID(int i) {
        return this.FaceIndexStartsCounts[i * 3];
    }

    public final int getFaceVertexCount(int i) {
        return this.FaceVertexStartsCounts[(i * 2) + 1];
    }

    public final int getVertexCount() {
        return this.VertexCount;
    }

    final boolean hasExtendedBones() {
        return this.meshData != null ? this.meshData.hasExtendedBones() : false;
    }

    final boolean isFacesCombined() {
        return this.facesCombined;
    }

    final boolean isRiggedMesh() {
        return this.isRiggedMesh;
    }

    final boolean riggingFitsGL20() {
        return (!this.isRiggedMesh || this.meshData == null) ? false : this.meshData.riggingFitsGL20();
    }
}
