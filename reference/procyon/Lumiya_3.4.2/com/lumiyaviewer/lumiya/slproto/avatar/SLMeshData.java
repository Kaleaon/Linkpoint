// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class SLMeshData
{
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
    
    public SLMeshData(final SLPolyMesh referenceData) {
        this.referenceData = referenceData;
        this.position = new LLVector3(referenceData.position);
        this.scale = new LLVector3(referenceData.scale);
        this.rotation = new LLQuaternion(referenceData.rotation);
        this.numVertices = referenceData.numVertices;
        this.vertexBuffer = new DirectByteBuffer(referenceData.vertexBuffer);
        this.texCoordsBuffer = new DirectByteBuffer(referenceData.texCoordsBuffer);
        this.numFaces = referenceData.numFaces;
        this.indexBuffer = new DirectByteBuffer(referenceData.indexBuffer);
    }
    
    public void initFromReference() {
        this.vertexBuffer.copyFrom(0, this.referenceData.vertexBuffer, 0, this.referenceData.vertexBuffer.asByteBuffer().capacity());
        this.texCoordsBuffer.copyFrom(0, this.referenceData.texCoordsBuffer, 0, this.referenceData.texCoordsBuffer.asByteBuffer().capacity());
        this.indexBuffer.copyFrom(0, this.referenceData.indexBuffer, 0, this.referenceData.indexBuffer.asByteBuffer().capacity());
    }
}
