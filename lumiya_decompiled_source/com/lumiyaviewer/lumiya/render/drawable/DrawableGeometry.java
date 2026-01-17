package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
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
/* loaded from: classes.dex */
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
        this.isRiggedMesh = meshData.isRiggedMesh();
        this.FaceCount = meshData.getFaceCount();
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.FaceCount; i3++) {
            MeshFace face = meshData.getFace(i3);
            if (face.getVertices() != null) {
                i += face.getNumVertices();
                i2 += face.getNumIndices();
            }
        }
        this.IndexCount = i2;
        this.VertexCount = i;
        if (i2 == 0 || i == 0) {
            throw new CreateFailureException("Mesh data has zero indices or vertices");
        }
        this.FaceIndexStartsCounts = new int[this.FaceCount * 3];
        this.FaceVertexStartsCounts = new int[this.FaceCount * 2];
        this.VertexSizeBytes = i * 4 * 6;
        this.IndexSizeBytes = i2 * 2;
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(this.VertexSizeBytes);
        DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(this.IndexSizeBytes);
        DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(i * 4 * 2);
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        this.facesCombined = false;
        for (int i8 = 0; i8 < this.FaceCount; i8++) {
            MeshFace face2 = meshData.getFace(i8);
            DirectByteBuffer vertices = face2.getVertices();
            DirectByteBuffer texCoords = face2.getTexCoords();
            int numVertices = face2.getNumVertices();
            if (face2.getNumVertices() == 0 || face2.getNumIndices() == 0) {
                throw new CreateFailureException("Empty mesh");
            }
            if (vertices != null) {
                directByteBuffer.copyFromFloat(i5 * 6, vertices, 0, numVertices * 6);
                if (texCoords != null) {
                    directByteBuffer3.copyFromFloat(i5 * 2, texCoords, 0, numVertices * 2);
                }
                DirectByteBuffer indices = face2.getIndices();
                int numIndices = face2.getNumIndices();
                for (int i9 = 0; i9 < numIndices; i9++) {
                    if ((indices.getShort(i9) & 65535) >= numVertices) {
                        throw new CreateFailureException("Too many vertices");
                    }
                }
                directByteBuffer2.copyFromShort(i4, face2.getIndices(), 0, face2.getNumIndices());
            }
            int i10 = i6 + 1;
            this.FaceIndexStartsCounts[i6] = i8;
            int i11 = i10 + 1;
            this.FaceIndexStartsCounts[i10] = i4;
            i6 = i11 + 1;
            this.FaceIndexStartsCounts[i11] = face2.getNumIndices();
            int i12 = i7 + 1;
            this.FaceVertexStartsCounts[i7] = i5;
            i7 = i12 + 1;
            this.FaceVertexStartsCounts[i12] = numVertices;
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
        this.FaceIndexStartsCounts = new int[this.FaceCount * 3];
        this.FaceVertexStartsCounts = new int[this.FaceCount * 2];
        this.VertexSizeBytes = i2 * 4 * 6;
        this.IndexSizeBytes = i * 2;
        DirectByteBuffer directByteBuffer = new DirectByteBuffer(this.VertexSizeBytes);
        DirectByteBuffer directByteBuffer2 = new DirectByteBuffer(this.IndexSizeBytes);
        DirectByteBuffer directByteBuffer3 = new DirectByteBuffer(i2 * 4 * 2);
        this.facesCombined = i2 < 32767 && i < 32767;
        if (this.facesCombined) {
            short s = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            for (PrimVolumeFace primVolumeFace2 : create.VolumeFaces) {
                directByteBuffer.loadFromFloatArray(s * 6, primVolumeFace2.vertexArray.getData(), 0, primVolumeFace2.NumVertices * 6);
                directByteBuffer3.loadFromFloatArray(s * 2, primVolumeFace2.vertexArray.getTexCoordsData(), 0, primVolumeFace2.NumVertices * 2);
                directByteBuffer2.loadFromShortArrayOffset(i5, primVolumeFace2.Indices, 0, primVolumeFace2.NumIndices, s);
                int i6 = i4 + 1;
                this.FaceIndexStartsCounts[i4] = primVolumeFace2.ID;
                int i7 = i6 + 1;
                this.FaceIndexStartsCounts[i6] = i5;
                int i8 = i7 + 1;
                this.FaceIndexStartsCounts[i7] = primVolumeFace2.NumIndices;
                int i9 = i3 + 1;
                this.FaceVertexStartsCounts[i3] = s;
                this.FaceVertexStartsCounts[i9] = primVolumeFace2.NumVertices;
                s = (short) (s + primVolumeFace2.NumVertices);
                i5 += primVolumeFace2.NumIndices;
                i3 = i9 + 1;
                i4 = i8;
            }
        } else {
            int i10 = 0;
            int i11 = 0;
            int i12 = 0;
            int i13 = 0;
            for (PrimVolumeFace primVolumeFace3 : create.VolumeFaces) {
                directByteBuffer.loadFromFloatArray(i10 * 6, primVolumeFace3.vertexArray.getData(), 0, primVolumeFace3.NumVertices * 6);
                directByteBuffer3.loadFromFloatArray(i10 * 2, primVolumeFace3.vertexArray.getTexCoordsData(), 0, primVolumeFace3.NumVertices * 2);
                directByteBuffer2.loadFromShortArray(i13, primVolumeFace3.Indices, 0, primVolumeFace3.NumIndices);
                int i14 = i12 + 1;
                this.FaceIndexStartsCounts[i12] = primVolumeFace3.ID;
                int i15 = i14 + 1;
                this.FaceIndexStartsCounts[i14] = i13;
                i12 = i15 + 1;
                this.FaceIndexStartsCounts[i15] = primVolumeFace3.NumIndices;
                int i16 = i11 + 1;
                this.FaceVertexStartsCounts[i11] = i10;
                i11 = i16 + 1;
                this.FaceVertexStartsCounts[i16] = primVolumeFace3.NumVertices;
                i10 += primVolumeFace3.NumVertices;
                i13 = primVolumeFace3.NumIndices + i13;
            }
        }
        directByteBuffer.position(0);
        directByteBuffer2.position(0);
        directByteBuffer3.position(0);
        this.VertexBuffer = new GLLoadableBuffer(directByteBuffer);
        this.IndexBuffer = new GLLoadableBuffer(directByteBuffer2);
        this.TexCoordsBuffer = new GLLoadableBuffer(directByteBuffer3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        MeshData meshData;
        if (isRiggedMesh() && (meshData = this.meshData) != null && meshData.isRiggedMesh()) {
            meshData.ApplyJointTranslations(meshJointTranslations);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final GLLoadableBuffer GLBindBuffers10(RenderContext renderContext, PrimFlexibleInfo primFlexibleInfo) {
        GLLoadableBuffer flexedVertexBuffer = primFlexibleInfo != null ? primFlexibleInfo.getFlexedVertexBuffer(renderContext, this.VertexBuffer, this.VertexCount) : this.VertexBuffer;
        if (this.facesCombined) {
            flexedVertexBuffer.Bind(renderContext, 32884, 3, 5126, 24, 0);
            flexedVertexBuffer.Bind(renderContext, 32885, 3, 5126, 24, 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, 0);
        }
        this.IndexBuffer.BindElements(renderContext);
        return flexedVertexBuffer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final GLLoadableBuffer GLBindBuffers20(RenderContext renderContext) {
        if (!renderContext.hasGL30) {
            if (this.facesCombined) {
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0);
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12);
                this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0);
            }
            this.IndexBuffer.BindElements20(renderContext);
            return this.VertexBuffer;
        }
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
                    this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i2 * 2] * 4 * 2);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void GLBindBuffersRigged30(RenderContext renderContext) {
        if (!this.isRiggedMesh || this.meshData == null) {
            return;
        }
        this.meshData.SetupBuffers30(renderContext);
        if (this.vertexArrayObject != null) {
            return;
        }
        this.vertexArrayObject = new GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount);
        renderContext.glResourceManager.addCleanable(this);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.FaceCount) {
                return;
            }
            this.vertexArrayObject.Bind(i2);
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i2 * 2] * 24);
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i2 * 2] * 24) + 12);
            this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i2 * 2] * 4 * 2);
            this.meshData.SetupFace30(renderContext, this.FaceVertexStartsCounts[i2 * 2]);
            this.IndexBuffer.BindElements20(renderContext);
            this.vertexArrayObject.Unbind();
            i = i2 + 1;
        }
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLCleanable
    public void GLCleanup() {
        this.vertexArrayObject = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void GLDrawAll10(RenderContext renderContext) {
        this.IndexBuffer.DrawElements(renderContext, 4, this.IndexCount, 5123, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void GLDrawAll20(RenderContext renderContext) {
        if (!renderContext.hasGL30) {
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
        } else if (this.vertexArrayObject != null) {
            this.vertexArrayObject.Bind(0);
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
            this.vertexArrayObject.Unbind();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void GLDrawFace10(RenderContext renderContext, int i, GLLoadableBuffer gLLoadableBuffer) {
        int i2 = i * 3;
        if (!this.facesCombined) {
            gLLoadableBuffer.Bind(renderContext, 32884, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
            gLLoadableBuffer.Bind(renderContext, 32885, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2);
        }
        this.IndexBuffer.DrawElements(renderContext, 4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void GLDrawFace20(RenderContext renderContext, int i) {
        int i2 = i * 3;
        if (renderContext.hasGL30) {
            if (this.vertexArrayObject != null) {
                GLVertexArrayObject gLVertexArrayObject = this.vertexArrayObject;
                if (this.facesCombined) {
                    i = 0;
                }
                gLVertexArrayObject.Bind(i);
                this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
                this.vertexArrayObject.Unbind();
                return;
            }
            return;
        }
        if (!this.facesCombined) {
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12);
            this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2);
            if (this.isRiggedMesh && this.meshData != null) {
                this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[i * 2]);
            }
        }
        this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void GLDrawRiggedFace30(RenderContext renderContext, int i) {
        if (this.vertexArrayObject != null) {
            int i2 = i * 3;
            this.vertexArrayObject.Bind(i);
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IntersectInfo IntersectRay(LLVector3 lLVector3, LLVector3 lLVector32) {
        GLRayTrace.RayIntersectInfo rayIntersectInfo = null;
        int i = -1;
        int i2 = 0;
        float f = 0.0f;
        LLVector3[] lLVector3Arr = new LLVector3[3];
        for (int i3 = 0; i3 < 3; i3++) {
            lLVector3Arr[i3] = new LLVector3();
        }
        for (int i4 = 0; i4 < this.FaceCount; i4++) {
            int i5 = i4 * 3;
            int i6 = this.FaceIndexStartsCounts[i5 + 1];
            int i7 = this.FaceIndexStartsCounts[i5 + 2];
            for (int i8 = 0; i8 < i7; i8 += 3) {
                int i9 = 0;
                while (true) {
                    int i10 = i9;
                    if (i10 >= 3) {
                        break;
                    }
                    int i11 = (this.facesCombined ? this.IndexBuffer.getShort(i6 + i8 + i10) : this.IndexBuffer.getShort(i6 + i8 + i10) + this.FaceVertexStartsCounts[i4 * 2]) * 6;
                    lLVector3Arr[i10].set(this.VertexBuffer.getFloat(i11 + 0), this.VertexBuffer.getFloat(i11 + 1), this.VertexBuffer.getFloat(i11 + 2));
                    i9 = i10 + 1;
                }
                GLRayTrace.RayIntersectInfo intersect_RayTriangle = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, 0);
                if (intersect_RayTriangle != null) {
                    float f2 = intersect_RayTriangle.intersectPoint.w;
                    if (rayIntersectInfo == null || f2 < f) {
                        f = f2;
                        i2 = i4;
                        i = i8;
                        rayIntersectInfo = intersect_RayTriangle;
                    }
                }
            }
        }
        if (rayIntersectInfo == null) {
            return null;
        }
        int i12 = this.FaceIndexStartsCounts[(i2 * 3) + 1];
        LLVector2[] lLVector2Arr = new LLVector2[3];
        int i13 = 0;
        while (true) {
            int i14 = i13;
            if (i14 >= 3) {
                return new IntersectInfo(rayIntersectInfo.intersectPoint, i2, ((lLVector2Arr[1].x - lLVector2Arr[0].x) * rayIntersectInfo.s) + ((lLVector2Arr[2].x - lLVector2Arr[0].x) * rayIntersectInfo.t) + lLVector2Arr[0].x, ((lLVector2Arr[1].y - lLVector2Arr[0].y) * rayIntersectInfo.s) + ((lLVector2Arr[2].y - lLVector2Arr[0].y) * rayIntersectInfo.t) + lLVector2Arr[0].y);
            }
            int i15 = (this.facesCombined ? this.IndexBuffer.getShort(i12 + i + i14) : this.IndexBuffer.getShort(i12 + i + i14) + this.FaceVertexStartsCounts[i2 * 2]) * 2;
            lLVector2Arr[i14] = new LLVector2(this.TexCoordsBuffer.getFloat(i15), this.TexCoordsBuffer.getFloat(i15 + 1));
            i13 = i14 + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean UpdateRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton) {
        MeshData meshData;
        if (this.isRiggedMesh && (meshData = this.meshData) != null && meshData.isRiggedMesh()) {
            meshData.UpdateRiggedMatrices(avatarSkeleton);
            if (renderContext.hasGL20 && !(!meshData.riggingFitsGL20())) {
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
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getFaceCount() {
        return this.FaceCount;
    }

    public final int getFaceFirstVertex(int i) {
        return this.FaceVertexStartsCounts[i * 2];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getFaceID(int i) {
        return this.FaceIndexStartsCounts[i * 3];
    }

    public final int getFaceVertexCount(int i) {
        return this.FaceVertexStartsCounts[(i * 2) + 1];
    }

    public final int getVertexCount() {
        return this.VertexCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasExtendedBones() {
        if (this.meshData != null) {
            return this.meshData.hasExtendedBones();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isFacesCombined() {
        return this.facesCombined;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isRiggedMesh() {
        return this.isRiggedMesh;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean riggingFitsGL20() {
        if (!this.isRiggedMesh || this.meshData == null) {
            return false;
        }
        return this.meshData.riggingFitsGL20();
    }
}
