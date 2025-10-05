package com.linkpoint.slproto.avatar

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class SLMeshData {
    protected DirectByteBuffer indexBuffer
    protected Int numFaces
    protected Int numVertices
    protected LLVector3 position
    protected SLPolyMesh referenceData
    protected LLQuaternion rotation
    protected LLVector3 scale
    protected DirectByteBuffer texCoordsBuffer
    protected DirectByteBuffer vertexBuffer

    public SLMeshData() {
    }

    public SLMeshData(SLPolyMesh sLPolyMesh) {
        this.referenceData = sLPolyMesh
        this.position = LLVector3(sLPolyMesh.position)
        this.scale = LLVector3(sLPolyMesh.scale)
        this.rotation = LLQuaternion(sLPolyMesh.rotation)
        this.numVertices = sLPolyMesh.numVertices
        this.vertexBuffer = DirectByteBuffer(sLPolyMesh.vertexBuffer)
        this.texCoordsBuffer = DirectByteBuffer(sLPolyMesh.texCoordsBuffer)
        this.numFaces = sLPolyMesh.numFaces
        this.indexBuffer = DirectByteBuffer(sLPolyMesh.indexBuffer)
    }

    public Unit initFromReference() {
        this.vertexBuffer.copyFrom(0, this.referenceData.vertexBuffer, 0, this.referenceData.vertexBuffer.asByteBuffer().capacity())
        this.texCoordsBuffer.copyFrom(0, this.referenceData.texCoordsBuffer, 0, this.referenceData.texCoordsBuffer.asByteBuffer().capacity())
        this.indexBuffer.copyFrom(0, this.referenceData.indexBuffer, 0, this.referenceData.indexBuffer.asByteBuffer().capacity())
    }
}
