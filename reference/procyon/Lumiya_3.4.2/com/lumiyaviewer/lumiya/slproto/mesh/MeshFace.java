// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import java.nio.ShortBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class MeshFace
{
    private final DirectByteBuffer indexBuffer;
    private final int numIndices;
    private final int numVertices;
    private final DirectByteBuffer texCoordsBuffer;
    private final DirectByteBuffer vertexBuffer;
    private final DirectByteBuffer weightBuffer;
    
    MeshFace(final LLSDNode llsdNode) throws LLSDException {
        if (llsdNode.keyExists("NoGeometry") || (llsdNode.keyExists("Position") ^ true) || (llsdNode.keyExists("TriangleList") ^ true)) {
            this.vertexBuffer = null;
            this.indexBuffer = null;
            this.weightBuffer = null;
            this.texCoordsBuffer = null;
            this.numIndices = 0;
            this.numVertices = 0;
            return;
        }
        final byte[] binary = llsdNode.byKey("Position").asBinary();
        byte[] binary2;
        if (llsdNode.keyExists("Normal")) {
            binary2 = llsdNode.byKey("Normal").asBinary();
        }
        else {
            binary2 = null;
        }
        byte[] binary3;
        if (llsdNode.keyExists("TexCoord0")) {
            binary3 = llsdNode.byKey("TexCoord0").asBinary();
        }
        else {
            binary3 = null;
        }
        this.numVertices = binary.length / 6;
        this.vertexBuffer = new DirectByteBuffer(this.numVertices * 6 * 4);
        final LLVector3 llVector3 = new LLVector3(-0.5f, -0.5f, -0.5f);
        final LLVector3 llVector4 = new LLVector3(0.5f, 0.5f, 0.5f);
        if (llsdNode.keyExists("PositionDomain")) {
            if (llsdNode.byKey("PositionDomain").keyExists("Min")) {
                final LLSDNode byKey = llsdNode.byKey("PositionDomain").byKey("Min");
                llVector3.set((float)byKey.byIndex(0).asDouble(), (float)byKey.byIndex(1).asDouble(), (float)byKey.byIndex(2).asDouble());
            }
            if (llsdNode.byKey("PositionDomain").keyExists("Max")) {
                final LLSDNode byKey2 = llsdNode.byKey("PositionDomain").byKey("Max");
                llVector4.set((float)byKey2.byIndex(0).asDouble(), (float)byKey2.byIndex(1).asDouble(), (float)byKey2.byIndex(2).asDouble());
            }
        }
        LLVector2 llVector5 = null;
        LLVector2 llVector6 = null;
        if (binary3 != null) {
            final LLVector2 llVector7 = new LLVector2(0.0f, 0.0f);
            final LLVector2 llVector8 = llVector6 = new LLVector2(0.0f, 0.0f);
            llVector5 = llVector7;
            if (llsdNode.keyExists("TexCoord0Domain")) {
                if (llsdNode.byKey("TexCoord0Domain").keyExists("Min")) {
                    final LLSDNode byKey3 = llsdNode.byKey("TexCoord0Domain").byKey("Min");
                    llVector7.set((float)byKey3.byIndex(0).asDouble(), (float)byKey3.byIndex(1).asDouble());
                }
                llVector6 = llVector8;
                llVector5 = llVector7;
                if (llsdNode.byKey("TexCoord0Domain").keyExists("Max")) {
                    final LLSDNode byKey4 = llsdNode.byKey("TexCoord0Domain").byKey("Max");
                    llVector8.set((float)byKey4.byIndex(0).asDouble(), (float)byKey4.byIndex(1).asDouble());
                    llVector5 = llVector7;
                    llVector6 = llVector8;
                }
            }
        }
        final ShortBuffer shortBuffer = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        ShortBuffer shortBuffer2;
        if (binary2 != null) {
            shortBuffer2 = ByteBuffer.wrap(binary2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        }
        else {
            shortBuffer2 = null;
        }
        ShortBuffer shortBuffer3;
        if (binary3 != null) {
            shortBuffer3 = ByteBuffer.wrap(binary3).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        }
        else {
            shortBuffer3 = null;
        }
        this.vertexBuffer.position(0);
        for (int i = 0; i < this.numVertices; ++i) {
            final float n = (shortBuffer.get() & 0xFFFF) * (llVector4.x - llVector3.x) / 65535.0f;
            final float x = llVector3.x;
            final float n2 = (shortBuffer.get() & 0xFFFF) * (llVector4.y - llVector3.y) / 65535.0f;
            final float y = llVector3.y;
            final float n3 = (shortBuffer.get() & 0xFFFF) * (llVector4.z - llVector3.z) / 65535.0f;
            final float z = llVector3.z;
            this.vertexBuffer.putFloat(n + x);
            this.vertexBuffer.putFloat(n2 + y);
            this.vertexBuffer.putFloat(n3 + z);
            if (shortBuffer2 != null) {
                final float n4 = (shortBuffer2.get() & 0xFFFF) * 2.0f / 65535.0f;
                final float n5 = (shortBuffer2.get() & 0xFFFF) * 2.0f / 65535.0f;
                final float n6 = (shortBuffer2.get() & 0xFFFF) * 2.0f / 65535.0f;
                this.vertexBuffer.putFloat(n4 - 1.0f);
                this.vertexBuffer.putFloat(n5 - 1.0f);
                this.vertexBuffer.putFloat(n6 - 1.0f);
            }
            else {
                this.vertexBuffer.putFloat(0.0f);
                this.vertexBuffer.putFloat(0.0f);
                this.vertexBuffer.putFloat(0.0f);
            }
        }
        if (shortBuffer3 != null) {
            (this.texCoordsBuffer = new DirectByteBuffer(this.numVertices * 2 * 4)).position(0);
            for (int j = 0; j < this.numVertices; ++j) {
                final float n7 = (shortBuffer3.get() & 0xFFFF) * (llVector6.x - llVector5.x) / 65535.0f;
                final float x2 = llVector5.x;
                final float n8 = (shortBuffer3.get() & 0xFFFF) * (llVector6.y - llVector5.y) / 65535.0f;
                final float y2 = llVector5.y;
                this.texCoordsBuffer.putFloat(n7 + x2);
                this.texCoordsBuffer.putFloat(n8 + y2);
            }
        }
        else {
            this.texCoordsBuffer = null;
        }
        final byte[] binary4 = llsdNode.byKey("TriangleList").asBinary();
        this.numIndices = binary4.length / 2;
        (this.indexBuffer = new DirectByteBuffer(this.numIndices * 2)).loadFromByteArray(0, binary4, 0, this.numIndices * 2);
        if (llsdNode.keyExists("Weights")) {
            final byte[] binary5 = llsdNode.byKey("Weights").asBinary();
            (this.weightBuffer = new DirectByteBuffer(binary5.length)).loadFromByteArray(0, binary5, 0, binary5.length);
        }
        else {
            this.weightBuffer = null;
        }
    }
    
    void PrepareInfluenceBuffer(@Nonnull final MeshWeightsBuffer meshWeightsBuffer, final int n) {
        OpenJPEG.meshPrepareSeparateInfluenceBuffer(this.weightBuffer.asByteBuffer(), this.numVertices, meshWeightsBuffer.jointIndexBuffer.asByteBuffer(), meshWeightsBuffer.weightsBuffer.asByteBuffer(), n);
    }
    
    void PrepareInfluenceBuffer(final DirectByteBuffer directByteBuffer, final int n) {
        if (this.weightBuffer != null) {
            OpenJPEG.meshPrepareInfluenceBuffer(this.weightBuffer.asByteBuffer(), this.numVertices, directByteBuffer.asByteBuffer(), n);
        }
    }
    
    final void UpdateRigged(final DirectByteBuffer directByteBuffer, final int n, final float[] array, final float[] array2) {
        if (this.weightBuffer != null && this.vertexBuffer != null && directByteBuffer != null) {
            OpenJPEG.applyRiggedMeshMorph(directByteBuffer.asByteBuffer(), n, array, array2, this.vertexBuffer.asByteBuffer(), this.weightBuffer.asByteBuffer(), this.numVertices);
        }
    }
    
    public final DirectByteBuffer getIndices() {
        return this.indexBuffer;
    }
    
    public final int getNumIndices() {
        return this.numIndices;
    }
    
    public final int getNumVertices() {
        return this.numVertices;
    }
    
    public final DirectByteBuffer getTexCoords() {
        return this.texCoordsBuffer;
    }
    
    public final DirectByteBuffer getVertices() {
        return this.vertexBuffer;
    }
}
