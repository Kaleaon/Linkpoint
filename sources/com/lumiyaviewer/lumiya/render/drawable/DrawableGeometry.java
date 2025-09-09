package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.Debug;
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

    public DrawableGeometry(MeshData meshData2) throws CreateFailureException {
        this.isRiggedMesh = meshData2.isRiggedMesh();
        this.FaceCount = meshData2.getFaceCount();
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.FaceCount; i3++) {
            MeshFace face = meshData2.getFace(i3);
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
        this.FaceIndexStartsCounts = new int[(this.FaceCount * 3)];
        this.FaceVertexStartsCounts = new int[(this.FaceCount * 2)];
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
            MeshFace face2 = meshData2.getFace(i8);
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
            this.meshData = meshData2;
        } else {
            this.meshData = null;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: int[]} */
    /* JADX WARNING: type inference failed for: r5v6 */
    /* JADX WARNING: type inference failed for: r5v7, types: [short, int] */
    /* JADX WARNING: type inference failed for: r5v10 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DrawableGeometry(com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams r14, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r15) throws com.lumiyaviewer.lumiya.utils.CreateFailureException {
        /*
            r13 = this;
            r7 = 32767(0x7fff, float:4.5916E-41)
            r1 = 0
            r3 = 0
            r13.<init>()
            r13.vertexArrayObject = r1
            r0 = 1082130432(0x40800000, float:4.0)
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolume r4 = com.lumiyaviewer.lumiya.slproto.prims.PrimVolume.create(r14, r0, r3, r3, r15)
            if (r4 != 0) goto L_0x001a
            com.lumiyaviewer.lumiya.utils.CreateFailureException r0 = new com.lumiyaviewer.lumiya.utils.CreateFailureException
            java.lang.String r1 = "Failed to create volume"
            r0.<init>(r1)
            throw r0
        L_0x001a:
            r13.isRiggedMesh = r3
            r13.meshData = r1
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace> r0 = r4.VolumeFaces
            java.util.Iterator r5 = r0.iterator()
            r1 = r3
            r2 = r3
        L_0x0026:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x003a
            java.lang.Object r0 = r5.next()
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace r0 = (com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace) r0
            int r6 = r0.NumVertices
            int r2 = r2 + r6
            int r0 = r0.NumIndices
            int r0 = r0 + r1
            r1 = r0
            goto L_0x0026
        L_0x003a:
            r13.IndexCount = r1
            r13.VertexCount = r2
            if (r1 == 0) goto L_0x0042
            if (r2 != 0) goto L_0x004b
        L_0x0042:
            com.lumiyaviewer.lumiya.utils.CreateFailureException r0 = new com.lumiyaviewer.lumiya.utils.CreateFailureException
            java.lang.String r1 = "Prim data has zero indices or vertices"
            r0.<init>(r1)
            throw r0
        L_0x004b:
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace> r0 = r4.VolumeFaces
            int r0 = r0.size()
            r13.FaceCount = r0
            int r0 = r13.FaceCount
            int r0 = r0 * 3
            int[] r0 = new int[r0]
            r13.FaceIndexStartsCounts = r0
            int r0 = r13.FaceCount
            int r0 = r0 * 2
            int[] r0 = new int[r0]
            r13.FaceVertexStartsCounts = r0
            int r0 = r2 * 4
            int r0 = r0 * 6
            r13.VertexSizeBytes = r0
            int r0 = r1 * 2
            r13.IndexSizeBytes = r0
            int r0 = r2 * 4
            int r5 = r0 * 2
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r9 = new com.lumiyaviewer.rawbuffers.DirectByteBuffer
            int r0 = r13.VertexSizeBytes
            r9.<init>((int) r0)
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r0 = new com.lumiyaviewer.rawbuffers.DirectByteBuffer
            int r6 = r13.IndexSizeBytes
            r0.<init>((int) r6)
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r10 = new com.lumiyaviewer.rawbuffers.DirectByteBuffer
            r10.<init>((int) r5)
            if (r2 >= r7) goto L_0x00f9
            if (r1 >= r7) goto L_0x00f9
            r1 = 1
        L_0x0089:
            r13.facesCombined = r1
            boolean r1 = r13.facesCombined
            if (r1 == 0) goto L_0x00fb
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace> r1 = r4.VolumeFaces
            java.util.Iterator r11 = r1.iterator()
            r5 = r3
            r7 = r3
            r8 = r3
            r1 = r3
        L_0x0099:
            boolean r2 = r11.hasNext()
            if (r2 == 0) goto L_0x0162
            java.lang.Object r2 = r11.next()
            r6 = r2
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace r6 = (com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace) r6
            int r2 = r5 * 6
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r4 = r6.vertexArray
            float[] r4 = r4.getData()
            int r12 = r6.NumVertices
            int r12 = r12 * 6
            r9.loadFromFloatArray(r2, r4, r3, r12)
            int r2 = r5 * 2
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r4 = r6.vertexArray
            float[] r4 = r4.getTexCoordsData()
            int r12 = r6.NumVertices
            int r12 = r12 * 2
            r10.loadFromFloatArray(r2, r4, r3, r12)
            short[] r2 = r6.Indices
            int r4 = r6.NumIndices
            r0.loadFromShortArrayOffset(r1, r2, r3, r4, r5)
            int[] r2 = r13.FaceIndexStartsCounts
            int r4 = r8 + 1
            int r12 = r6.ID
            r2[r8] = r12
            int[] r2 = r13.FaceIndexStartsCounts
            int r8 = r4 + 1
            r2[r4] = r1
            int[] r2 = r13.FaceIndexStartsCounts
            int r4 = r8 + 1
            int r12 = r6.NumIndices
            r2[r8] = r12
            int[] r2 = r13.FaceVertexStartsCounts
            int r8 = r7 + 1
            r2[r7] = r5
            int[] r7 = r13.FaceVertexStartsCounts
            int r2 = r8 + 1
            int r12 = r6.NumVertices
            r7[r8] = r12
            int r7 = r6.NumVertices
            int r5 = r5 + r7
            short r5 = (short) r5
            int r6 = r6.NumIndices
            int r1 = r1 + r6
            r7 = r2
            r8 = r4
            goto L_0x0099
        L_0x00f9:
            r1 = r3
            goto L_0x0089
        L_0x00fb:
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace> r1 = r4.VolumeFaces
            java.util.Iterator r7 = r1.iterator()
            r2 = r3
            r4 = r3
            r5 = r3
            r6 = r3
        L_0x0105:
            boolean r1 = r7.hasNext()
            if (r1 == 0) goto L_0x0162
            java.lang.Object r1 = r7.next()
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace r1 = (com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace) r1
            int r8 = r2 * 6
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r11 = r1.vertexArray
            float[] r11 = r11.getData()
            int r12 = r1.NumVertices
            int r12 = r12 * 6
            r9.loadFromFloatArray(r8, r11, r3, r12)
            int r8 = r2 * 2
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r11 = r1.vertexArray
            float[] r11 = r11.getTexCoordsData()
            int r12 = r1.NumVertices
            int r12 = r12 * 2
            r10.loadFromFloatArray(r8, r11, r3, r12)
            short[] r8 = r1.Indices
            int r11 = r1.NumIndices
            r0.loadFromShortArray(r6, r8, r3, r11)
            int[] r8 = r13.FaceIndexStartsCounts
            int r11 = r5 + 1
            int r12 = r1.ID
            r8[r5] = r12
            int[] r5 = r13.FaceIndexStartsCounts
            int r8 = r11 + 1
            r5[r11] = r6
            int[] r11 = r13.FaceIndexStartsCounts
            int r5 = r8 + 1
            int r12 = r1.NumIndices
            r11[r8] = r12
            int[] r8 = r13.FaceVertexStartsCounts
            int r11 = r4 + 1
            r8[r4] = r2
            int[] r8 = r13.FaceVertexStartsCounts
            int r4 = r11 + 1
            int r12 = r1.NumVertices
            r8[r11] = r12
            int r8 = r1.NumVertices
            int r2 = r2 + r8
            int r1 = r1.NumIndices
            int r1 = r1 + r6
            r6 = r1
            goto L_0x0105
        L_0x0162:
            r9.position(r3)
            r0.position(r3)
            r10.position(r3)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer r1 = new com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
            r1.<init>(r9)
            r13.VertexBuffer = r1
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer r1 = new com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
            r1.<init>(r0)
            r13.IndexBuffer = r1
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer r0 = new com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
            r0.<init>(r10)
            r13.TexCoordsBuffer = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry.<init>(com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG):void");
    }

    /* access modifiers changed from: package-private */
    public final void ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        MeshData meshData2;
        if (isRiggedMesh() && (meshData2 = this.meshData) != null && meshData2.isRiggedMesh()) {
            meshData2.ApplyJointTranslations(meshJointTranslations);
        }
    }

    /* access modifiers changed from: package-private */
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

    /* access modifiers changed from: package-private */
    public final GLLoadableBuffer GLBindBuffers20(RenderContext renderContext) {
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
        if (this.facesCombined) {
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0);
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12);
            this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0);
        }
        this.IndexBuffer.BindElements20(renderContext);
        return this.VertexBuffer;
    }

    /* access modifiers changed from: package-private */
    public final void GLBindBuffersRigged30(RenderContext renderContext) {
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
                        this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i2 * 2] * 4 * 2);
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

    /* access modifiers changed from: package-private */
    public final void GLDrawAll10(RenderContext renderContext) {
        this.IndexBuffer.DrawElements(renderContext, 4, this.IndexCount, 5123, 0);
    }

    /* access modifiers changed from: package-private */
    public final void GLDrawAll20(RenderContext renderContext) {
        if (!renderContext.hasGL30) {
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
        } else if (this.vertexArrayObject != null) {
            this.vertexArrayObject.Bind(0);
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0);
            this.vertexArrayObject.Unbind();
        }
    }

    /* access modifiers changed from: package-private */
    public final void GLDrawFace10(RenderContext renderContext, int i, GLLoadableBuffer gLLoadableBuffer) {
        int i2 = i * 3;
        if (!this.facesCombined) {
            gLLoadableBuffer.Bind(renderContext, 32884, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
            gLLoadableBuffer.Bind(renderContext, 32885, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12);
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2);
        }
        this.IndexBuffer.DrawElements(renderContext, 4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
    }

    /* access modifiers changed from: package-private */
    public final void GLDrawFace20(RenderContext renderContext, int i) {
        int i2 = i * 3;
        if (!renderContext.hasGL30) {
            if (!this.facesCombined) {
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24);
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12);
                this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2);
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

    /* access modifiers changed from: package-private */
    public final void GLDrawRiggedFace30(RenderContext renderContext, int i) {
        if (this.vertexArrayObject != null) {
            int i2 = i * 3;
            this.vertexArrayObject.Bind(i);
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2);
        }
    }

    /* access modifiers changed from: package-private */
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
            if (i14 < 3) {
                int i15 = (this.facesCombined ? this.IndexBuffer.getShort(i12 + i + i14) : this.IndexBuffer.getShort(i12 + i + i14) + this.FaceVertexStartsCounts[i2 * 2]) * 2;
                lLVector2Arr[i14] = new LLVector2(this.TexCoordsBuffer.getFloat(i15), this.TexCoordsBuffer.getFloat(i15 + 1));
                i13 = i14 + 1;
            } else {
                return new IntersectInfo(rayIntersectInfo.intersectPoint, i2, ((lLVector2Arr[1].x - lLVector2Arr[0].x) * rayIntersectInfo.s) + ((lLVector2Arr[2].x - lLVector2Arr[0].x) * rayIntersectInfo.t) + lLVector2Arr[0].x, ((lLVector2Arr[1].y - lLVector2Arr[0].y) * rayIntersectInfo.s) + ((lLVector2Arr[2].y - lLVector2Arr[0].y) * rayIntersectInfo.t) + lLVector2Arr[0].y);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean UpdateRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton) {
        MeshData meshData2;
        if (!this.isRiggedMesh || (meshData2 = this.meshData) == null || !meshData2.isRiggedMesh()) {
            return false;
        }
        meshData2.UpdateRiggedMatrices(avatarSkeleton);
        if (!renderContext.hasGL20 || (!meshData2.riggingFitsGL20())) {
            DirectByteBuffer rawBuffer = this.VertexBuffer.getRawBuffer();
            for (int i = 0; i < this.FaceCount; i++) {
                meshData2.UpdateRigged(i, rawBuffer, this.FaceVertexStartsCounts[i * 2]);
            }
            this.VertexBuffer.Reload(renderContext);
            return false;
        }
        meshData2.PrepareInfluenceBuffers(renderContext);
        return true;
    }

    /* access modifiers changed from: package-private */
    public final int getFaceCount() {
        return this.FaceCount;
    }

    public final int getFaceFirstVertex(int i) {
        return this.FaceVertexStartsCounts[i * 2];
    }

    /* access modifiers changed from: package-private */
    public final int getFaceID(int i) {
        return this.FaceIndexStartsCounts[i * 3];
    }

    public final int getFaceVertexCount(int i) {
        return this.FaceVertexStartsCounts[(i * 2) + 1];
    }

    public final int getVertexCount() {
        return this.VertexCount;
    }

    /* access modifiers changed from: package-private */
    public final boolean hasExtendedBones() {
        if (this.meshData != null) {
            return this.meshData.hasExtendedBones();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public final boolean isFacesCombined() {
        return this.facesCombined;
    }

    /* access modifiers changed from: package-private */
    public final boolean isRiggedMesh() {
        return this.isRiggedMesh;
    }

    /* access modifiers changed from: package-private */
    public final boolean riggingFitsGL20() {
        if (!this.isRiggedMesh || this.meshData == null) {
            return false;
        }
        return this.meshData.riggingFitsGL20();
    }
}
