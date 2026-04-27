package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class SLMeshData {
    protected DirectByteBuffer indexBuffer;
    protected int numFaces;
    protected int numVertices;
    protected LLVector3 position;
    protected SLPolyMesh referenceData;
    protected LLQuaternion rotation;
    protected LLVector3 scale;
    protected DirectByteBuffer texCoordsBuffer;
    protected DirectByteBuffer vertexBuffer;

    public SLMeshData() {
    }

    public SLMeshData(SLPolyMesh sLPolyMesh) {
        this.referenceData = sLPolyMesh;
        this.position = new LLVector3(sLPolyMesh.position);
        this.scale = new LLVector3(sLPolyMesh.scale);
        this.rotation = new LLQuaternion(sLPolyMesh.rotation);
        this.numVertices = sLPolyMesh.numVertices;
        this.vertexBuffer = new DirectByteBuffer(sLPolyMesh.vertexBuffer);
        this.texCoordsBuffer = new DirectByteBuffer(sLPolyMesh.texCoordsBuffer);
        this.numFaces = sLPolyMesh.numFaces;
        this.indexBuffer = new DirectByteBuffer(sLPolyMesh.indexBuffer);
    }

    public void initFromReference() {
        this.vertexBuffer.copyFrom(0, this.referenceData.vertexBuffer, 0, this.referenceData.vertexBuffer.asByteBuffer().capacity());
        this.texCoordsBuffer.copyFrom(0, this.referenceData.texCoordsBuffer, 0, this.referenceData.texCoordsBuffer.asByteBuffer().capacity());
        this.indexBuffer.copyFrom(0, this.referenceData.indexBuffer, 0, this.referenceData.indexBuffer.asByteBuffer().capacity());
    }
}
