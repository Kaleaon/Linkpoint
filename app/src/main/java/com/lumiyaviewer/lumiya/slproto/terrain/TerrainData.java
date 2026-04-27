package com.lumiyaviewer.lumiya.slproto.terrain;
import java.util.*;

import android.support.v4.view.InputDeviceCompat;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import com.lumiyaviewer.lumiya.utils.BitBuffer;

public class TerrainData {
    public static final int PatchesPerEdge = 16;
    public static final int PatchesSize = 16;
    public static final int TerrainPerEdge = 256;
    private final float[] heightMap = new float[65536];
    private final boolean[] patchDirtyMap = new boolean[256];
    private volatile TerrainTextures terrainTextures = new TerrainTextures();
    private int validCount = 0;
    private final boolean[] validMap = new boolean[65536];
    private final float[] vertexHeights = new float[66049];
    private final Object vertexLock = new Object();
    private final float[] vertexNormals = new float[132098];
    private final boolean[] vertexValids = new boolean[66049];
    private float waterHeight = 0.0f;
    private boolean waterHeightValid = false;

    private synchronized void SetWaterHeight(float f) {
        if (this.waterHeight != f || (!this.waterHeightValid)) {
            this.waterHeight = f;
            this.waterHeightValid = true;
            updateEntireTerrain();
        }
    }

    private void markVerticesDirty(int i, int i2, int i3, int i4) {
        int i5 = i / 16;
        int i6 = i2 / 16;
        int i7 = i3 / 16;
        int i8 = i4 / 16;
        synchronized (this.vertexLock) {
            for (int i9 = i6; i9 <= i8; i9++) {
                for (int i10 = i5; i10 <= i7; i10++) {
                    if (i10 >= 0 && i10 < 16 && i9 >= 0 && i9 < 16) {
                        this.patchDirtyMap[(i9 * 16) + i10] = true;
                    }
                }
            }
        }
        for (int i11 = i6; i11 <= i8; i11++) {
            for (int i12 = i5; i12 <= i7; i12++) {
                if (i12 >= 0 && i12 < 16 && i11 >= 0 && i11 < 16) {
                    SpatialIndex.getInstance().updateTerrainPatch(i12, i11, this);
                }
            }
        }
    }

    private void updateVerticesInRegion(int i, int i2, int i3, int i4) {
        while (i2 <= i4) {
            for (int i5 = i; i5 <= i3; i5++) {
                int min = Math.min(Math.max(0, i5 - 1), 255);
                int min2 = Math.min(Math.max(0, i2 - 1), 255);
                int min3 = Math.min(Math.max(0, i5), 255);
                int min4 = Math.min(Math.max(0, i2), 255);
                float f = 0.0f;
                int i6 = 0;
                if (min >= 0 && min < 256 && min2 >= 0 && min2 < 256 && this.validMap[(min2 * 256) + min]) {
                    f = 0.0f + this.heightMap[(min2 * 256) + min];
                    i6 = 1;
                }
                if (min3 >= 0 && min3 < 256 && min2 >= 0 && min2 < 256 && this.validMap[(min2 * 256) + min3]) {
                    f += this.heightMap[(min2 * 256) + min3];
                    i6++;
                }
                if (min >= 0 && min < 256 && min4 >= 0 && min4 < 256 && this.validMap[(min4 * 256) + min]) {
                    f += this.heightMap[(min4 * 256) + min];
                    i6++;
                }
                if (min3 >= 0 && min3 < 256 && min4 >= 0 && min4 < 256 && this.validMap[(min4 * 256) + min3]) {
                    f += this.heightMap[(min4 * 256) + min3];
                    i6++;
                }
                if (i6 == 4) {
                    this.vertexHeights[(i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5] = f / ((float) i6);
                    float f2 = this.heightMap[(min4 * 256) + min3] - this.heightMap[min + (min4 * 256)];
                    float f3 = this.heightMap[(min4 * 256) + min3] - this.heightMap[(min2 * 256) + min3];
                    this.vertexNormals[((i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5) * 2] = f2;
                    this.vertexNormals[(((i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5) * 2) + 1] = f3;
                    this.vertexValids[(i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5] = true;
                } else {
                    this.vertexValids[(i2 * InputDeviceCompat.SOURCE_KEYBOARD) + i5] = false;
                }
            }
            i2++;
        }
    }

    public synchronized void ApplyRegionInfo(RegionHandshake.RegionInfo regionInfo) {
        SetWaterHeight(regionInfo.WaterHeight);
        TerrainTextures terrainTextures2 = new TerrainTextures(regionInfo);
        if (!terrainTextures2.equals(this.terrainTextures)) {
            this.terrainTextures = terrainTextures2;
            updateEntireTerrain();
        }
    }

    public void ProcessLayerData(byte[] bArr) {
        TerrainPatch DecompressPatch;
        BitBuffer bitBuffer = new BitBuffer(bArr);
        int bits = bitBuffer.getBits(16);
        int bits2 = bitBuffer.getBits(8);
        Debug.Log(String.format("Terrain: ProcessLayerData: stride 0x%x patchSize 0x%x type 0x%x", new Object[]{Integer.valueOf(bits), Integer.valueOf(bits2), Integer.valueOf(bitBuffer.getBits(8))}));
        synchronized (this.vertexLock) {
            while (!bitBuffer.isEOF() && (DecompressPatch = TerrainPatch.DecompressPatch(bitBuffer, bits2)) != null) {
                int x = DecompressPatch.getX();
                int y = DecompressPatch.getY();
                if (x < 16 && y < 16) {
                    for (int i = 0; i < bits2; i++) {
                        int i2 = (y * 16) + i;
                        if (i2 >= 0 && i2 < 256) {
                            for (int i3 = 0; i3 < bits2; i3++) {
                                int i4 = (x * 16) + i3;
                                if (i4 >= 0 && i4 < 256) {
                                    this.heightMap[(i2 * 256) + i4] = DecompressPatch.heightMap[(i * bits2) + i3];
                                    if (!this.validMap[(i2 * 256) + i4]) {
                                        this.validCount++;
                                        this.validMap[i4 + (i2 * 256)] = true;
                                    }
                                }
                            }
                        }
                    }
                    markVerticesDirty(x * 16, y * 16, ((x + 1) * 16) + 1, ((y + 1) * 16) + 1);
                }
            }
        }
        Debug.Printf("Terrain: LayerData received, valid count is now %d", Integer.valueOf(this.validCount));
    }

    public TerrainPatchInfo getPatchInfo(int i, int i2) {
        synchronized (this.vertexLock) {
            if (this.patchDirtyMap[(i2 * 16) + i]) {
                this.patchDirtyMap[(i2 * 16) + i] = false;
                updateVerticesInRegion(i * 16, i2 * 16, (i + 1) * 16, (i2 + 1) * 16);
            }
        }
        boolean z = true;
        for (int i3 = 0; i3 < 17; i3++) {
            int i4 = ((i2 * 16) + i3) * InputDeviceCompat.SOURCE_KEYBOARD;
            int i5 = 0;
            while (true) {
                if (i5 >= 17) {
                    break;
                } else if (!this.vertexValids[i4 + i5 + (i * 16)]) {
                    z = false;
                    break;
                } else {
                    i5++;
                }
            }
        }
        if (!z) {
            return null;
        }
        float[] fArr = new float[289];
        float[] fArr2 = new float[578];
        int i6 = 0;
        while (true) {
            int i7 = i6;
            if (i7 < 17) {
                int i8 = ((i2 * 16) + i7) * InputDeviceCompat.SOURCE_KEYBOARD;
                for (int i9 = 0; i9 < 17; i9++) {
                    float f = this.vertexHeights[i8 + i9 + (i * 16)];
                    float f2 = this.vertexNormals[(i8 + i9 + (i * 16)) * 2];
                    float f3 = this.vertexNormals[((i8 + i9 + (i * 16)) * 2) + 1];
                    fArr[(i7 * 17) + i9] = f;
                    fArr2[((i7 * 17) + i9) * 2] = f2;
                    fArr2[(((i7 * 17) + i9) * 2) + 1] = f3;
                }
                i6 = i7 + 1;
            } else {
                return new TerrainPatchInfo(new TerrainPatchHeightMap(this.waterHeight, fArr, fArr2, 17, 17), this.terrainTextures, ((float) i) / 16.0f, ((float) i) / 16.0f, 0.0625f, 0.0625f);
            }
        }
    }

    public final boolean isUnderWater(float f) {
        return this.waterHeightValid && f < this.waterHeight;
    }

    public void reset() {
        synchronized (this.vertexLock) {
            this.waterHeightValid = false;
            this.validCount = 0;
            for (int i = 0; i < 65536; i++) {
                this.validMap[i] = false;
            }
            for (int i2 = 0; i2 < 66049; i2++) {
                this.vertexValids[i2] = false;
            }
            for (int i3 = 0; i3 < 256; i3++) {
                this.patchDirtyMap[i3] = false;
            }
        }
    }

    public void updateEntireTerrain() {
        markVerticesDirty(0, 0, 256, 256);
    }
}
