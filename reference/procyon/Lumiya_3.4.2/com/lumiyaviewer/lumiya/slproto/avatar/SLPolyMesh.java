// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.nio.FloatBuffer;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.GLTexture;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.io.InputStream;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.EnumMap;
import java.io.DataInputStream;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.util.Map;

public class SLPolyMesh extends SLMeshData
{
    protected boolean hasWeights;
    public int[] jointMap;
    private Map<SLVisualParamID, Integer> morphIndices;
    private SLPolyMorphData[] morphs;
    protected DirectByteBuffer weightsBuffer;
    
    public SLPolyMesh(DataInputStream dataInputStream, final DataInputStream dataInputStream2) throws IOException {
        final int n = 0;
        this.morphIndices = new EnumMap<SLVisualParamID, Integer>(SLVisualParamID.class);
        this.position = new LLVector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
        this.scale = new LLVector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
        this.rotation = new LLQuaternion(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
        this.hasWeights = (dataInputStream.readByte() != 0);
        this.numVertices = dataInputStream.readInt();
        this.vertexBuffer = new DirectByteBuffer(this.numVertices * 24);
        this.texCoordsBuffer = new DirectByteBuffer(this.numVertices * 8);
        this.vertexBuffer.read(dataInputStream);
        this.texCoordsBuffer.read(dataInputStream);
        if (this.hasWeights) {
            (this.weightsBuffer = new DirectByteBuffer(this.numVertices * 4)).read(dataInputStream);
        }
        this.numFaces = dataInputStream.readInt();
        (this.indexBuffer = new DirectByteBuffer(this.numFaces * 2 * 3)).read(dataInputStream);
        final int int1 = dataInputStream.readInt();
        this.morphs = new SLPolyMorphData[int1];
        int i = 0;
        int n2 = 0;
        DataInputStream dataInputStream3 = dataInputStream;
        while (i < int1) {
            int n3 = n2;
            dataInputStream = dataInputStream3;
            if (n2 >= 50) {
                n3 = n2;
                dataInputStream = dataInputStream3;
                if (dataInputStream2 != null) {
                    n3 = 0;
                    dataInputStream = dataInputStream2;
                }
            }
            final SLVisualParamID slVisualParamID = SLVisualParamID.values()[dataInputStream.readInt()];
            this.morphs[i] = new SLPolyMorphData(slVisualParamID, this, dataInputStream);
            this.morphIndices.put(slVisualParamID, i);
            ++i;
            n2 = n3 + 1;
            dataInputStream3 = dataInputStream;
        }
        final int int2 = dataInputStream3.readInt();
        this.jointMap = new int[int2];
        for (int j = n; j < int2; ++j) {
            this.jointMap[j] = dataInputStream3.readInt();
        }
        Debug.Log("SLPolyMesh: Loaded, numVerts = " + this.numVertices + ", faces = " + this.numFaces + ", morphs = " + this.morphs.length);
    }
    
    public void applyMorphData(final SLMeshData slMeshData, final float[] array, final GLTexture glTexture) {
        for (int i = 0; i < array.length; ++i) {
            this.morphs[i].applyMorphData(slMeshData, array[i], glTexture);
        }
    }
    
    public void applySkeleton(final SLAnimatedMeshData slAnimatedMeshData, final float[] array) {
        if (this.hasWeights && this.jointMap != null) {
            final DirectByteBuffer animatedVertexData = slAnimatedMeshData.getAnimatedVertexData();
            if (animatedVertexData != null) {
                OpenJPEG.applyMorphingTransform(this.numVertices, slAnimatedMeshData.vertexBuffer.asByteBuffer(), animatedVertexData.asByteBuffer(), this.weightsBuffer.asByteBuffer(), this.jointMap, array);
            }
        }
    }
    
    public void applySkeletonSlow(final SLAnimatedMeshData slAnimatedMeshData, final float[] array) {
        if (this.hasWeights && this.jointMap != null) {
            final DirectByteBuffer animatedVertexData = slAnimatedMeshData.getAnimatedVertexData();
            if (animatedVertexData != null) {
                final FloatBuffer floatBuffer = this.weightsBuffer.asFloatBuffer();
                final FloatBuffer floatBuffer2 = slAnimatedMeshData.vertexBuffer.asFloatBuffer();
                final FloatBuffer floatBuffer3 = animatedVertexData.asFloatBuffer();
                final float[] array2 = new float[16];
                final float[] array3 = new float[16];
                double n = -1.0;
                for (int i = 0; i < this.numVertices; ++i) {
                    final float value = floatBuffer.get(i);
                    if (value != n) {
                        final float n2 = (float)Math.floor(value);
                        final float n3 = value - n2;
                        final int n4 = (int)n2 - 1;
                        int n6;
                        final int n5 = n6 = 0;
                        if (n4 >= 0) {
                            n6 = n5;
                            if (n4 < this.jointMap.length) {
                                n6 = this.jointMap[n4];
                            }
                        }
                        int n7;
                        if (n4 + 1 >= 0 && n4 + 1 < this.jointMap.length) {
                            n7 = this.jointMap[n4 + 1];
                        }
                        else {
                            n7 = n6;
                        }
                        final double n8 = value;
                        final int n9 = n6 * 16;
                        final int n10 = n7 * 16;
                        if (n9 == n10) {
                            System.arraycopy(array, n9, array3, 0, 16);
                            n = n8;
                        }
                        else {
                            int n11 = 0;
                            while (true) {
                                n = n8;
                                if (n11 >= 16) {
                                    break;
                                }
                                array3[n11] = array[n9 + n11] * (1.0f - n3) + array[n10 + n11] * n3;
                                ++n11;
                            }
                        }
                    }
                    array2[0] = floatBuffer2.get(i * 6 + 0);
                    array2[1] = floatBuffer2.get(i * 6 + 1);
                    array2[2] = floatBuffer2.get(i * 6 + 2);
                    array2[3] = 1.0f;
                    Matrix.multiplyMV(array2, 4, array3, 0, array2, 0);
                    floatBuffer3.put(i * 6 + 0, array2[4]);
                    floatBuffer3.put(i * 6 + 1, array2[5]);
                    floatBuffer3.put(i * 6 + 2, array2[6]);
                }
            }
        }
    }
    
    public int getMorphIndex(final SLVisualParamID slVisualParamID) {
        final Integer n = this.morphIndices.get(slVisualParamID);
        if (n == null) {
            return -1;
        }
        return n;
    }
    
    public int getNumMorphs() {
        return this.morphs.length;
    }
}
