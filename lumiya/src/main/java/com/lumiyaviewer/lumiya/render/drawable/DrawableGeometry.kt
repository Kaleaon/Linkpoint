package com.lumiyaviewer.lumiya.render.drawable

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.render.glres.buffers.GLVertexArrayObject
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams
import com.lumiyaviewer.lumiya.slproto.types.LLVector2
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.utils.CreateFailureException
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class DrawableGeometry : GLCleanable {
    private var FaceCount: Int = 0
    private var FaceIndexStartsCounts: IntArray? = null
    private var FaceVertexStartsCounts: IntArray? = null
    private var IndexBuffer: GLLoadableBuffer? = null
    private var IndexCount: Int = 0
    private var IndexSizeBytes: Int = 0
    private var TexCoordsBuffer: GLLoadableBuffer? = null
    private var VertexBuffer: GLLoadableBuffer? = null
    private var VertexCount: Int = 0
    private var VertexSizeBytes: Int = 0
    private var facesCombined: Boolean = false
    private var isRiggedMesh: Boolean = false
    private var meshData: MeshData? = null
    private var vertexArrayObject: GLVertexArrayObject? = null

    constructor(meshData: MeshData) {
        this.isRiggedMesh = meshData.isRiggedMesh()
        this.FaceCount = meshData.getFaceCount()
        
        var totalVertices = 0
        var totalIndices = 0
        
        for (i in 0 until FaceCount) {
            val face = meshData.getFace(i)
            if (face?.getVertices() != null) {
                totalVertices += face.getNumVertices()
                totalIndices += face.getNumIndices()
            }
        }
        
        this.IndexCount = totalIndices
        this.VertexCount = totalVertices
        
        if (totalIndices == 0 || totalVertices == 0) {
            throw CreateFailureException("Mesh data has zero indices or vertices")
        }
        
        this.FaceIndexStartsCounts = IntArray(FaceCount * 3)
        this.FaceVertexStartsCounts = IntArray(FaceCount * 2)
        this.VertexSizeBytes = totalVertices * 4 * 6
        this.IndexSizeBytes = totalIndices * 2
        
        val vertexBuffer = DirectByteBuffer(VertexSizeBytes)
        val indexBuffer = DirectByteBuffer(IndexSizeBytes)
        val texCoordsBuffer = DirectByteBuffer(totalVertices * 4 * 2)
        
        var indexOffset = 0
        var vertexOffset = 0
        var indexArrayIdx = 0
        var vertexArrayIdx = 0
        this.facesCombined = false
        
        for (i in 0 until FaceCount) {
            val face = meshData.getFace(i) ?: continue
            val vertices = face.getVertices()
            val texCoords = face.getTexCoords()
            val numVertices = face.getNumVertices()
            
            if (numVertices == 0 || face.getNumIndices() == 0) {
                throw CreateFailureException("Empty mesh")
            }
            
            if (vertices != null) {
                vertexBuffer.copyFromFloat(vertexOffset * 6, vertices, 0, numVertices * 6)
                if (texCoords != null) {
                    texCoordsBuffer.copyFromFloat(vertexOffset * 2, texCoords, 0, numVertices * 2)
                }
                
                val indices = face.getIndices()
                val numIndices = face.getNumIndices()
                
                if (indices != null) {
                    for (j in 0 until numIndices) {
                        if ((indices.getShort(j).toInt() and 0xFFFF) >= numVertices) {
                            throw CreateFailureException("Too many vertices")
                        }
                    }
                    indexBuffer.copyFromShort(indexOffset, indices, 0, numIndices)
                }
            }
            
            FaceIndexStartsCounts!![indexArrayIdx] = i
            FaceIndexStartsCounts!![indexArrayIdx + 1] = indexOffset
            FaceIndexStartsCounts!![indexArrayIdx + 2] = face.getNumIndices()
            indexArrayIdx += 3
            
            FaceVertexStartsCounts!![vertexArrayIdx] = vertexOffset
            FaceVertexStartsCounts!![vertexArrayIdx + 1] = numVertices
            vertexArrayIdx += 2
            
            vertexOffset += numVertices
            indexOffset += face.getNumIndices()
        }
        
        vertexBuffer.position(0)
        indexBuffer.position(0)
        texCoordsBuffer.position(0)
        
        this.VertexBuffer = GLLoadableBuffer(vertexBuffer)
        this.IndexBuffer = GLLoadableBuffer(indexBuffer)
        this.TexCoordsBuffer = GLLoadableBuffer(texCoordsBuffer)
        
        Debug.Printf("Mesh drawable created,  index count %d, vertex count %d", IndexCount, VertexCount)
        
        if (isRiggedMesh) {
            this.meshData = meshData
        } else {
            this.meshData = null
        }
    }

    constructor(params: PrimVolumeParams?, openJPEG: OpenJPEG?) {
        // Stub implementation
        this.isRiggedMesh = false
        this.meshData = null
        this.vertexArrayObject = null
        
        this.FaceCount = 0
        this.IndexCount = 0
        this.VertexCount = 0
        this.VertexBuffer = null
        this.IndexBuffer = null
        this.TexCoordsBuffer = null
        
        throw CreateFailureException("Not implemented yet")
    }

    fun ApplyJointTranslations(translations: MeshJointTranslations?) {
        if (isRiggedMesh && meshData != null && meshData!!.isRiggedMesh() && translations != null) {
            meshData!!.ApplyJointTranslations(translations)
        }
    }

    fun GLBindBuffers10(renderContext: RenderContext?, flexibleInfo: PrimFlexibleInfo?): GLLoadableBuffer? {
        val buffer = VertexBuffer ?: return null
        val flexedBuffer = if (flexibleInfo != null && renderContext != null) 
            flexibleInfo.getFlexedVertexBuffer(renderContext, buffer, VertexCount) 
        else 
            buffer
            
        if (facesCombined && renderContext != null) {
            flexedBuffer.Bind(renderContext, 32884, 3, 5126, 24, 0) // GL_VERTEX_ARRAY
            flexedBuffer.Bind(renderContext, 32885, 3, 5126, 24, 12) // GL_NORMAL_ARRAY
            TexCoordsBuffer?.Bind(renderContext, 32888, 2, 5126, 8, 0) // GL_TEXTURE_COORD_ARRAY
        }
        
        if (renderContext != null) {
            IndexBuffer?.BindElements(renderContext)
        }
        return flexedBuffer
    }

    fun GLBindBuffers20(renderContext: RenderContext?): GLLoadableBuffer? {
        if (renderContext == null || VertexBuffer == null) return null
        
        if (facesCombined) {
            VertexBuffer!!.Bind20(renderContext, renderContext.curPrimProgram.vPosition, 3, 5126, 24, 0)
            VertexBuffer!!.Bind20(renderContext, renderContext.curPrimProgram.vNormal, 3, 5126, 24, 12)
            TexCoordsBuffer?.Bind20(renderContext, renderContext.curPrimProgram.vTexCoord, 2, 5126, 8, 0)
        }
        IndexBuffer?.BindElements20(renderContext)
        return VertexBuffer
    }

    fun GLBindBuffersRigged30(renderContext: RenderContext?) {
        // Stubbed
    }

    override fun GLCleanup() {
        vertexArrayObject = null
    }

    fun GLDrawAll10(renderContext: RenderContext?) {
        if (renderContext != null) { // Added null check
            IndexBuffer?.DrawElements(renderContext, 4, IndexCount, 5123, 0)
        }
    }

    fun GLDrawAll20(renderContext: RenderContext?) {
        IndexBuffer?.DrawElements20(4, IndexCount, 5123, 0)
    }

    fun GLDrawFace10(renderContext: RenderContext?, faceIndex: Int, buffer: GLLoadableBuffer?) {
        if (renderContext == null || FaceIndexStartsCounts == null || FaceVertexStartsCounts == null) return
        
        val idxOffset = faceIndex * 3
        if (!facesCombined && buffer != null) {
            val vertexStart = FaceVertexStartsCounts!![faceIndex * 2] * 24
            buffer.Bind(renderContext, 32884, 3, 5126, 24, vertexStart)
            buffer.Bind(renderContext, 32885, 3, 5126, 24, vertexStart + 12)
            TexCoordsBuffer?.Bind(renderContext, 32888, 2, 5126, 8, FaceVertexStartsCounts!![faceIndex * 2] * 8)
        }
        IndexBuffer?.DrawElements(
            renderContext, 
            4, 
            FaceIndexStartsCounts!![idxOffset + 2], 
            5123, 
            FaceIndexStartsCounts!![idxOffset + 1] * 2
        )
    }

    fun GLDrawFace20(renderContext: RenderContext?, faceIndex: Int) {
        if (renderContext == null || FaceIndexStartsCounts == null) return
        val idxOffset = faceIndex * 3
        IndexBuffer?.DrawElements20(
            4, 
            FaceIndexStartsCounts!![idxOffset + 2], 
            5123, 
            FaceIndexStartsCounts!![idxOffset + 1] * 2
        )
    }

    fun GLDrawRiggedFace30(renderContext: RenderContext?, faceIndex: Int) {
        GLDrawFace20(renderContext, faceIndex)
    }

    fun IntersectRay(start: LLVector3?, end: LLVector3?): IntersectInfo? {
        // Stub implementation
        return null
    }

    fun UpdateRigged(renderContext: RenderContext?, skeleton: AvatarSkeleton?): Boolean {
        // Stub implementation
        return false
    }

    fun getFaceCount(): Int = FaceCount

    fun getFaceFirstVertex(faceIndex: Int): Int {
        return FaceVertexStartsCounts?.get(faceIndex * 2) ?: 0
    }

    fun getFaceID(faceIndex: Int): Int {
        return FaceIndexStartsCounts?.get(faceIndex * 3) ?: 0
    }

    fun getFaceVertexCount(faceIndex: Int): Int {
        return FaceVertexStartsCounts?.get(faceIndex * 2 + 1) ?: 0
    }

    fun getVertexCount(): Int = VertexCount

    fun hasExtendedBones(): Boolean {
        return meshData?.hasExtendedBones() ?: false
    }

    fun isFacesCombined(): Boolean = facesCombined

    fun isRiggedMesh(): Boolean = isRiggedMesh

    fun riggingFitsGL20(): Boolean {
        return if (!isRiggedMesh || meshData == null) false else meshData!!.riggingFitsGL20()
    }
}
