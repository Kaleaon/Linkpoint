// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.GLTexture;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.io.InputStream;
import java.io.DataInputStream;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class SLPolyMorphData
{
    private DirectByteBuffer indexBuffer;
    private boolean isMasked;
    private SLPolyMesh mesh;
    private SLVisualParamID morphID;
    private int numVertices;
    private DirectByteBuffer texCoordsBuffer;
    private DirectByteBuffer vertexBuffer;
    
    public SLPolyMorphData(final SLVisualParamID slVisualParamID, final SLPolyMesh mesh, final DataInputStream dataInputStream) throws IOException {
        boolean isMasked = false;
        this.morphID = slVisualParamID;
        this.mesh = mesh;
        if (dataInputStream.readByte() != 0) {
            isMasked = true;
        }
        this.isMasked = isMasked;
        this.numVertices = dataInputStream.readInt();
        this.vertexBuffer = new DirectByteBuffer(this.numVertices * 24);
        this.texCoordsBuffer = new DirectByteBuffer(this.numVertices * 8);
        this.indexBuffer = new DirectByteBuffer(this.numVertices * 4);
        this.vertexBuffer.read(dataInputStream);
        this.texCoordsBuffer.read(dataInputStream);
        this.indexBuffer.read(dataInputStream);
        Debug.Log("SLPolyMorphData: Loaded morph '" + slVisualParamID + "', vertices = " + this.numVertices);
    }
    
    public void applyMorphData(final SLMeshData slMeshData, final float n, final GLTexture glTexture) {
        final ByteBuffer byteBuffer = null;
        int position = 0;
        int n2;
        if (this.isMasked && glTexture != null) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        int n3;
        int n4;
        ByteBuffer byteBuffer2;
        if (n2 != 0) {
            final int width = glTexture.getWidth();
            final int height = glTexture.getHeight();
            final ByteBuffer extraComponentsBuffer = glTexture.getExtraComponentsBuffer();
            n3 = width;
            n4 = height;
            if ((byteBuffer2 = extraComponentsBuffer) != null) {
                position = extraComponentsBuffer.position();
                byteBuffer2 = extraComponentsBuffer;
                n4 = height;
                n3 = width;
            }
        }
        else {
            n4 = 0;
            n3 = 0;
            byteBuffer2 = byteBuffer;
        }
        OpenJPEG.applyMeshMorph(n, slMeshData.vertexBuffer.asByteBuffer(), slMeshData.texCoordsBuffer.asByteBuffer(), this.numVertices, this.indexBuffer.asByteBuffer(), this.vertexBuffer.asByteBuffer(), this.texCoordsBuffer.asByteBuffer(), n3, n4, position, byteBuffer2);
    }
    
    public void applyMorphDataSlow(final SLMeshData slMeshData, final float n, final GLTexture glTexture) {
        final FloatBuffer floatBuffer = this.vertexBuffer.asFloatBuffer();
        final FloatBuffer floatBuffer2 = this.texCoordsBuffer.asFloatBuffer();
        final IntBuffer intBuffer = this.indexBuffer.asIntBuffer();
        final FloatBuffer floatBuffer3 = slMeshData.vertexBuffer.asFloatBuffer();
        final FloatBuffer floatBuffer4 = slMeshData.texCoordsBuffer.asFloatBuffer();
        boolean b;
        if (this.isMasked && glTexture != null) {
            b = true;
        }
        else {
            b = false;
        }
        int width = 0;
        int height = 0;
        ByteBuffer extraComponentsBuffer = null;
        int position = 0;
        int n2 = b ? 1 : 0;
        if (b) {
            width = glTexture.getWidth();
            height = glTexture.getHeight();
            extraComponentsBuffer = glTexture.getExtraComponentsBuffer();
            if (extraComponentsBuffer != null) {
                position = extraComponentsBuffer.position();
                n2 = (b ? 1 : 0);
            }
            else {
                n2 = 0;
                position = position;
            }
        }
        for (int i = 0; i < this.numVertices; ++i) {
            final int value = intBuffer.get(i);
            float n3;
            if (n2 != 0) {
                n3 = (extraComponentsBuffer.get((int)Math.floor(floatBuffer4.get(value * 2 + 0) * width) + ((int)Math.floor(floatBuffer4.get(value * 2 + 1) * height) * width + position)) & 0xFF) / 255.0f * n;
            }
            else {
                n3 = n;
            }
            for (int j = 0; j < 6; ++j) {
                floatBuffer3.put(value * 6 + j, floatBuffer3.get(value * 6 + j) + floatBuffer.get(i * 6 + j) * n3);
            }
            for (int k = 0; k < 2; ++k) {
                floatBuffer4.put(value * 2 + k, floatBuffer4.get(value * 2 + k) + floatBuffer2.get(i * 2 + k) * n3);
            }
        }
    }
}
