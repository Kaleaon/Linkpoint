package com.lumiyaviewer.lumiya.render.drawable
import java.util.*

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData
import com.lumiyaviewer.lumiya.slproto.mesh.MeshFace
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo
import com.lumiyaviewer.lumiya.slproto.types.LLVector2
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.utils.CreateFailureException
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class DrawableGeometry : GLCleanable {
    private Int FaceCount
    private Int[] FaceIndexStartsCounts
    private Int[] FaceVertexStartsCounts
    private GLLoadableBuffer IndexBuffer
    private Int IndexCount
    private Int IndexSizeBytes
    private GLLoadableBuffer TexCoordsBuffer
    private GLLoadableBuffer VertexBuffer
    private Int VertexCount
    private Int VertexSizeBytes
    private Boolean facesCombined
    private Boolean isRiggedMesh
    private MeshData meshData
    private var vertexArrayObject: GLVertexArrayObject = null

    DrawableGeometry(MeshData meshData2) throws CreateFailureException {
        this.isRiggedMesh = meshData2.isRiggedMesh()
        this.FaceCount = meshData2.getFaceCount()
        Int i = 0
        Int i2 = 0
        for (Int i3 = 0; i3 < this.FaceCount; i3++) {
            MeshFace face = meshData2.getFace(i3)
            if (face.getVertices() != null) {
                i += face.getNumVertices()
                i2 += face.getNumIndices()
            }
        }
        this.IndexCount = i2
        this.VertexCount = i
        if (i2 == 0 || i == 0) {
            throw CreateFailureException("Mesh data has zero indices or vertices")
        }
        this.FaceIndexStartsCounts = Int[(this.FaceCount * 3)]
        this.FaceVertexStartsCounts = Int[(this.FaceCount * 2)]
        this.VertexSizeBytes = i * 4 * 6
        this.IndexSizeBytes = i2 * 2
        DirectByteBuffer directByteBuffer = DirectByteBuffer(this.VertexSizeBytes)
        DirectByteBuffer directByteBuffer2 = DirectByteBuffer(this.IndexSizeBytes)
        DirectByteBuffer directByteBuffer3 = DirectByteBuffer(i * 4 * 2)
        Int i4 = 0
        Int i5 = 0
        Int i6 = 0
        Int i7 = 0
        this.facesCombined = false
        for (Int i8 = 0; i8 < this.FaceCount; i8++) {
            MeshFace face2 = meshData2.getFace(i8)
            DirectByteBuffer vertices = face2.getVertices()
            DirectByteBuffer texCoords = face2.getTexCoords()
            Int numVertices = face2.getNumVertices()
            if (face2.getNumVertices() == 0 || face2.getNumIndices() == 0) {
                throw CreateFailureException("Empty mesh")
            }
            if (vertices != null) {
                directByteBuffer.copyFromFloat(i5 * 6, vertices, 0, numVertices * 6)
                if (texCoords != null) {
                    directByteBuffer3.copyFromFloat(i5 * 2, texCoords, 0, numVertices * 2)
                }
                DirectByteBuffer indices = face2.getIndices()
                Int numIndices = face2.getNumIndices()
                for (Int i9 = 0; i9 < numIndices; i9++) {
                    if ((indices.getShort(i9) & 65535) >= numVertices) {
                        throw CreateFailureException("Too many vertices")
                    }
                }
                directByteBuffer2.copyFromShort(i4, face2.getIndices(), 0, face2.getNumIndices())
            }
            Int i10 = i6 + 1
            this.FaceIndexStartsCounts[i6] = i8
            Int i11 = i10 + 1
            this.FaceIndexStartsCounts[i10] = i4
            i6 = i11 + 1
            this.FaceIndexStartsCounts[i11] = face2.getNumIndices()
            Int i12 = i7 + 1
            this.FaceVertexStartsCounts[i7] = i5
            i7 = i12 + 1
            this.FaceVertexStartsCounts[i12] = numVertices
            i5 += numVertices
            i4 += face2.getNumIndices()
        }
        directByteBuffer.position(0)
        directByteBuffer2.position(0)
        directByteBuffer3.position(0)
        this.VertexBuffer = GLLoadableBuffer(directByteBuffer)
        this.IndexBuffer = GLLoadableBuffer(directByteBuffer2)
        this.TexCoordsBuffer = GLLoadableBuffer(directByteBuffer3)
        Debug.Printf("Mesh drawable created,  index count %d, vertex count %d", Int.valueOf(this.IndexCount), Int.valueOf(this.VertexCount))
        if (this.isRiggedMesh) {
            this.meshData = meshData2
        } else {
            this.meshData = null
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: Int[]} */
    /* JADX WARNING: type inference failed for: r5v6 */
    /* JADX WARNING: type inference failed for: r5v7, types: [Short, Int] */
    /* JADX WARNING: type inference failed for: r5v10 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    DrawableGeometry(com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams r14, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r15) throws com.lumiyaviewer.lumiya.utils.CreateFailureException {
        /*
            r13 = this
            r7 = 32767(0x7fff, Float:4.5916E-41)
            r1 = 0
            r3 = 0
            r13.<init>()
            r13.vertexArrayObject = r1
            r0 = 1082130432(0x40800000, Float:4.0)
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolume r4 = com.lumiyaviewer.lumiya.slproto.prims.PrimVolume.create(r14, r0, r3, r3, r15)
            if (r4 != 0) goto L_0x001a
            com.lumiyaviewer.lumiya.utils.CreateFailureException r0 = com.lumiyaviewer.lumiya.utils.CreateFailureException
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
            Boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x003a
            java.lang.Any r0 = r5.next()
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace r0 = (com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace) r0
            Int r6 = r0.NumVertices
            Int r2 = r2 + r6
            Int r0 = r0.NumIndices
            Int r0 = r0 + r1
            r1 = r0
            goto L_0x0026
        L_0x003a:
            r13.IndexCount = r1
            r13.VertexCount = r2
            if (r1 == 0) goto L_0x0042
            if (r2 != 0) goto L_0x004b
        L_0x0042:
            com.lumiyaviewer.lumiya.utils.CreateFailureException r0 = com.lumiyaviewer.lumiya.utils.CreateFailureException
            java.lang.String r1 = "Prim data has zero indices or vertices"
            r0.<init>(r1)
            throw r0
        L_0x004b:
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace> r0 = r4.VolumeFaces
            Int r0 = r0.size()
            r13.FaceCount = r0
            Int r0 = r13.FaceCount
            Int r0 = r0 * 3
            Int[] r0 = Int[r0]
            r13.FaceIndexStartsCounts = r0
            Int r0 = r13.FaceCount
            Int r0 = r0 * 2
            Int[] r0 = Int[r0]
            r13.FaceVertexStartsCounts = r0
            Int r0 = r2 * 4
            Int r0 = r0 * 6
            r13.VertexSizeBytes = r0
            Int r0 = r1 * 2
            r13.IndexSizeBytes = r0
            Int r0 = r2 * 4
            Int r5 = r0 * 2
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r9 = com.lumiyaviewer.rawbuffers.DirectByteBuffer
            Int r0 = r13.VertexSizeBytes
            r9.<init>((Int) r0)
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r0 = com.lumiyaviewer.rawbuffers.DirectByteBuffer
            Int r6 = r13.IndexSizeBytes
            r0.<init>((Int) r6)
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r10 = com.lumiyaviewer.rawbuffers.DirectByteBuffer
            r10.<init>((Int) r5)
            if (r2 >= r7) goto L_0x00f9
            if (r1 >= r7) goto L_0x00f9
            r1 = 1
        L_0x0089:
            r13.facesCombined = r1
            Boolean r1 = r13.facesCombined
            if (r1 == 0) goto L_0x00fb
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace> r1 = r4.VolumeFaces
            java.util.Iterator r11 = r1.iterator()
            r5 = r3
            r7 = r3
            r8 = r3
            r1 = r3
        L_0x0099:
            Boolean r2 = r11.hasNext()
            if (r2 == 0) goto L_0x0162
            java.lang.Any r2 = r11.next()
            r6 = r2
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace r6 = (com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace) r6
            Int r2 = r5 * 6
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r4 = r6.vertexArray
            Float[] r4 = r4.getData()
            Int r12 = r6.NumVertices
            Int r12 = r12 * 6
            r9.loadFromFloatArray(r2, r4, r3, r12)
            Int r2 = r5 * 2
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r4 = r6.vertexArray
            Float[] r4 = r4.getTexCoordsData()
            Int r12 = r6.NumVertices
            Int r12 = r12 * 2
            r10.loadFromFloatArray(r2, r4, r3, r12)
            Short[] r2 = r6.Indices
            Int r4 = r6.NumIndices
            r0.loadFromShortArrayOffset(r1, r2, r3, r4, r5)
            Int[] r2 = r13.FaceIndexStartsCounts
            Int r4 = r8 + 1
            Int r12 = r6.ID
            r2[r8] = r12
            Int[] r2 = r13.FaceIndexStartsCounts
            Int r8 = r4 + 1
            r2[r4] = r1
            Int[] r2 = r13.FaceIndexStartsCounts
            Int r4 = r8 + 1
            Int r12 = r6.NumIndices
            r2[r8] = r12
            Int[] r2 = r13.FaceVertexStartsCounts
            Int r8 = r7 + 1
            r2[r7] = r5
            Int[] r7 = r13.FaceVertexStartsCounts
            Int r2 = r8 + 1
            Int r12 = r6.NumVertices
            r7[r8] = r12
            Int r7 = r6.NumVertices
            Int r5 = r5 + r7
            Short r5 = (Short) r5
            Int r6 = r6.NumIndices
            Int r1 = r1 + r6
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
            Boolean r1 = r7.hasNext()
            if (r1 == 0) goto L_0x0162
            java.lang.Any r1 = r7.next()
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace r1 = (com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeFace) r1
            Int r8 = r2 * 6
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r11 = r1.vertexArray
            Float[] r11 = r11.getData()
            Int r12 = r1.NumVertices
            Int r12 = r12 * 6
            r9.loadFromFloatArray(r8, r11, r3, r12)
            Int r8 = r2 * 2
            com.lumiyaviewer.lumiya.slproto.types.VertexArray r11 = r1.vertexArray
            Float[] r11 = r11.getTexCoordsData()
            Int r12 = r1.NumVertices
            Int r12 = r12 * 2
            r10.loadFromFloatArray(r8, r11, r3, r12)
            Short[] r8 = r1.Indices
            Int r11 = r1.NumIndices
            r0.loadFromShortArray(r6, r8, r3, r11)
            Int[] r8 = r13.FaceIndexStartsCounts
            Int r11 = r5 + 1
            Int r12 = r1.ID
            r8[r5] = r12
            Int[] r5 = r13.FaceIndexStartsCounts
            Int r8 = r11 + 1
            r5[r11] = r6
            Int[] r11 = r13.FaceIndexStartsCounts
            Int r5 = r8 + 1
            Int r12 = r1.NumIndices
            r11[r8] = r12
            Int[] r8 = r13.FaceVertexStartsCounts
            Int r11 = r4 + 1
            r8[r4] = r2
            Int[] r8 = r13.FaceVertexStartsCounts
            Int r4 = r11 + 1
            Int r12 = r1.NumVertices
            r8[r11] = r12
            Int r8 = r1.NumVertices
            Int r2 = r2 + r8
            Int r1 = r1.NumIndices
            Int r1 = r1 + r6
            r6 = r1
            goto L_0x0105
        L_0x0162:
            r9.position(r3)
            r0.position(r3)
            r10.position(r3)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer r1 = com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
            r1.<init>(r9)
            r13.VertexBuffer = r1
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer r1 = com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
            r1.<init>(r0)
            r13.IndexBuffer = r1
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer r0 = com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
            r0.<init>(r10)
            r13.TexCoordsBuffer = r0
            return
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry.<init>(com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG):Unit")
    }

    /* access modifiers changed from: package-private */
    Unit ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        MeshData meshData2
        if (isRiggedMesh() && (meshData2 = this.meshData) != null && meshData2.isRiggedMesh()) {
            meshData2.ApplyJointTranslations(meshJointTranslations)
        }
    }

    /* access modifiers changed from: package-private */
    GLLoadableBuffer GLBindBuffers10(RenderContext renderContext, PrimFlexibleInfo primFlexibleInfo) {
        GLLoadableBuffer flexedVertexBuffer = primFlexibleInfo != null ? primFlexibleInfo.getFlexedVertexBuffer(renderContext, this.VertexBuffer, this.VertexCount) : this.VertexBuffer
        if (this.facesCombined) {
            flexedVertexBuffer.Bind(renderContext, 32884, 3, 5126, 24, 0)
            flexedVertexBuffer.Bind(renderContext, 32885, 3, 5126, 24, 12)
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, 0)
        }
        this.IndexBuffer.BindElements(renderContext)
        return flexedVertexBuffer
    }

    /* access modifiers changed from: package-private */
    GLLoadableBuffer GLBindBuffers20(RenderContext renderContext) {
        if (renderContext.hasGL30) {
            if (this.vertexArrayObject == null) {
                if (this.facesCombined) {
                    this.vertexArrayObject = GLVertexArrayObject(renderContext.glResourceManager, 1)
                    renderContext.glResourceManager.addCleanable(this)
                    this.vertexArrayObject.Bind(0)
                    this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0)
                    this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12)
                    this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0)
                    this.IndexBuffer.BindElements20(renderContext)
                    this.vertexArrayObject.Unbind()
                } else {
                    this.vertexArrayObject = GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount)
                    renderContext.glResourceManager.addCleanable(this)
                    Int i = 0
                    while (true) {
                        Int i2 = i
                        if (i2 >= this.FaceCount) {
                            break
                        }
                        this.vertexArrayObject.Bind(i2)
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i2 * 2] * 24)
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i2 * 2] * 24) + 12)
                        this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i2 * 2] * 4 * 2)
                        if (this.isRiggedMesh && this.meshData != null) {
                            this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[i2 * 2])
                        }
                        this.IndexBuffer.BindElements20(renderContext)
                        i = i2 + 1
                    }
                    this.vertexArrayObject.Unbind()
                }
            }
            return this.VertexBuffer
        }
        if (this.facesCombined) {
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0)
            this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12)
            this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0)
        }
        this.IndexBuffer.BindElements20(renderContext)
        return this.VertexBuffer
    }

    /* access modifiers changed from: package-private */
    Unit GLBindBuffersRigged30(RenderContext renderContext) {
        if (this.isRiggedMesh && this.meshData != null) {
            this.meshData.SetupBuffers30(renderContext)
            if (this.vertexArrayObject == null) {
                this.vertexArrayObject = GLVertexArrayObject(renderContext.glResourceManager, this.FaceCount)
                renderContext.glResourceManager.addCleanable(this)
                Int i = 0
                while (true) {
                    Int i2 = i
                    if (i2 < this.FaceCount) {
                        this.vertexArrayObject.Bind(i2)
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i2 * 2] * 24)
                        this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i2 * 2] * 24) + 12)
                        this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i2 * 2] * 4 * 2)
                        this.meshData.SetupFace30(renderContext, this.FaceVertexStartsCounts[i2 * 2])
                        this.IndexBuffer.BindElements20(renderContext)
                        this.vertexArrayObject.Unbind()
                        i = i2 + 1
                    } else {
                        return
                    }
                }
            }
        }
    }

    fun GLCleanup(): Unit {
        this.vertexArrayObject = null
    }

    /* access modifiers changed from: package-private */
    Unit GLDrawAll10(RenderContext renderContext) {
        this.IndexBuffer.DrawElements(renderContext, 4, this.IndexCount, 5123, 0)
    }

    /* access modifiers changed from: package-private */
    Unit GLDrawAll20(RenderContext renderContext) {
        if (!renderContext.hasGL30) {
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0)
        } else if (this.vertexArrayObject != null) {
            this.vertexArrayObject.Bind(0)
            this.IndexBuffer.DrawElements20(4, this.IndexCount, 5123, 0)
            this.vertexArrayObject.Unbind()
        }
    }

    /* access modifiers changed from: package-private */
    Unit GLDrawFace10(RenderContext renderContext, Int i, GLLoadableBuffer gLLoadableBuffer) {
        Int i2 = i * 3
        if (!this.facesCombined) {
            gLLoadableBuffer.Bind(renderContext, 32884, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24)
            gLLoadableBuffer.Bind(renderContext, 32885, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12)
            this.TexCoordsBuffer.Bind(renderContext, 32888, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2)
        }
        this.IndexBuffer.DrawElements(renderContext, 4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2)
    }

    /* access modifiers changed from: package-private */
    Unit GLDrawFace20(RenderContext renderContext, Int i) {
        Int i2 = i * 3
        if (!renderContext.hasGL30) {
            if (!this.facesCombined) {
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, this.FaceVertexStartsCounts[i * 2] * 24)
                this.VertexBuffer.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, (this.FaceVertexStartsCounts[i * 2] * 24) + 12)
                this.TexCoordsBuffer.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, this.FaceVertexStartsCounts[i * 2] * 4 * 2)
                if (this.isRiggedMesh && this.meshData != null) {
                    this.meshData.PrepareInfluencesForFace(renderContext, this.FaceVertexStartsCounts[i * 2])
                }
            }
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2)
        } else if (this.vertexArrayObject != null) {
            GLVertexArrayObject gLVertexArrayObject = this.vertexArrayObject
            if (this.facesCombined) {
                i = 0
            }
            gLVertexArrayObject.Bind(i)
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2)
            this.vertexArrayObject.Unbind()
        }
    }

    /* access modifiers changed from: package-private */
    Unit GLDrawRiggedFace30(RenderContext renderContext, Int i) {
        if (this.vertexArrayObject != null) {
            Int i2 = i * 3
            this.vertexArrayObject.Bind(i)
            this.IndexBuffer.DrawElements20(4, this.FaceIndexStartsCounts[i2 + 2], 5123, this.FaceIndexStartsCounts[i2 + 1] * 2)
        }
    }

    /* access modifiers changed from: package-private */
    fun IntersectRay(lLVector3: LLVector3, lLVector32: LLVector3): IntersectInfo {
        GLRayTrace.RayIntersectInfo rayIntersectInfo = null
        Int i = -1
        Int i2 = 0
        Float f = 0.0f
        LLVector3[] lLVector3Arr = LLVector3[3]
        for (Int i3 = 0; i3 < 3; i3++) {
            lLVector3Arr[i3] = LLVector3()
        }
        for (Int i4 = 0; i4 < this.FaceCount; i4++) {
            Int i5 = i4 * 3
            Int i6 = this.FaceIndexStartsCounts[i5 + 1]
            Int i7 = this.FaceIndexStartsCounts[i5 + 2]
            for (Int i8 = 0; i8 < i7; i8 += 3) {
                Int i9 = 0
                while (true) {
                    Int i10 = i9
                    if (i10 >= 3) {
                        break
                    }
                    Int i11 = (this.facesCombined ? this.IndexBuffer.getShort(i6 + i8 + i10) : this.IndexBuffer.getShort(i6 + i8 + i10) + this.FaceVertexStartsCounts[i4 * 2]) * 6
                    lLVector3Arr[i10].set(this.VertexBuffer.getFloat(i11 + 0), this.VertexBuffer.getFloat(i11 + 1), this.VertexBuffer.getFloat(i11 + 2))
                    i9 = i10 + 1
                }
                GLRayTrace.RayIntersectInfo intersect_RayTriangle = GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, 0)
                if (intersect_RayTriangle != null) {
                    Float f2 = intersect_RayTriangle.intersectPoint.w
                    if (rayIntersectInfo == null || f2 < f) {
                        f = f2
                        i2 = i4
                        i = i8
                        rayIntersectInfo = intersect_RayTriangle
                    }
                }
            }
        }
        if (rayIntersectInfo == null) {
            return null
        }
        Int i12 = this.FaceIndexStartsCounts[(i2 * 3) + 1]
        LLVector2[] lLVector2Arr = LLVector2[3]
        Int i13 = 0
        while (true) {
            Int i14 = i13
            if (i14 < 3) {
                Int i15 = (this.facesCombined ? this.IndexBuffer.getShort(i12 + i + i14) : this.IndexBuffer.getShort(i12 + i + i14) + this.FaceVertexStartsCounts[i2 * 2]) * 2
                lLVector2Arr[i14] = LLVector2(this.TexCoordsBuffer.getFloat(i15), this.TexCoordsBuffer.getFloat(i15 + 1))
                i13 = i14 + 1
            } else {
                return IntersectInfo(rayIntersectInfo.intersectPoint, i2, ((lLVector2Arr[1].x - lLVector2Arr[0].x) * rayIntersectInfo.s) + ((lLVector2Arr[2].x - lLVector2Arr[0].x) * rayIntersectInfo.t) + lLVector2Arr[0].x, ((lLVector2Arr[1].y - lLVector2Arr[0].y) * rayIntersectInfo.s) + ((lLVector2Arr[2].y - lLVector2Arr[0].y) * rayIntersectInfo.t) + lLVector2Arr[0].y)
            }
        }
    }

    /* access modifiers changed from: package-private */
    Boolean UpdateRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton) {
        MeshData meshData2
        if (!this.isRiggedMesh || (meshData2 = this.meshData) == null || !meshData2.isRiggedMesh()) {
            return false
        }
        meshData2.UpdateRiggedMatrices(avatarSkeleton)
        if (!renderContext.hasGL20 || (!meshData2.riggingFitsGL20())) {
            DirectByteBuffer rawBuffer = this.VertexBuffer.getRawBuffer()
            for (Int i = 0; i < this.FaceCount; i++) {
                meshData2.UpdateRigged(i, rawBuffer, this.FaceVertexStartsCounts[i * 2])
            }
            this.VertexBuffer.Reload(renderContext)
            return false
        }
        meshData2.PrepareInfluenceBuffers(renderContext)
        return true
    }

    /* access modifiers changed from: package-private */
    Int getFaceCount() {
        return this.FaceCount
    }

    Int getFaceFirstVertex(Int i) {
        return this.FaceVertexStartsCounts[i * 2]
    }

    /* access modifiers changed from: package-private */
    Int getFaceID(Int i) {
        return this.FaceIndexStartsCounts[i * 3]
    }

    Int getFaceVertexCount(Int i) {
        return this.FaceVertexStartsCounts[(i * 2) + 1]
    }

    Int getVertexCount() {
        return this.VertexCount
    }

    /* access modifiers changed from: package-private */
    Boolean hasExtendedBones() {
        if (this.meshData != null) {
            return this.meshData.hasExtendedBones()
        }
        return false
    }

    /* access modifiers changed from: package-private */
    Boolean isFacesCombined() {
        return this.facesCombined
    }

    /* access modifiers changed from: package-private */
    Boolean isRiggedMesh() {
        return this.isRiggedMesh
    }

    /* access modifiers changed from: package-private */
    Boolean riggingFitsGL20() {
        if (!this.isRiggedMesh || this.meshData == null) {
            return false
        }
        return this.meshData.riggingFitsGL20()
    }
}
