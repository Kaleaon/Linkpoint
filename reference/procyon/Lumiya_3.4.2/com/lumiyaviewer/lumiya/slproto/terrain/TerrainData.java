// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.BitBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.RegionHandshake;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;

public class TerrainData
{
    public static final int PatchesPerEdge = 16;
    public static final int PatchesSize = 16;
    public static final int TerrainPerEdge = 256;
    private final float[] heightMap;
    private final boolean[] patchDirtyMap;
    private volatile TerrainTextures terrainTextures;
    private int validCount;
    private final boolean[] validMap;
    private final float[] vertexHeights;
    private final Object vertexLock;
    private final float[] vertexNormals;
    private final boolean[] vertexValids;
    private float waterHeight;
    private boolean waterHeightValid;
    
    public TerrainData() {
        this.vertexLock = new Object();
        this.heightMap = new float[65536];
        this.validMap = new boolean[65536];
        this.vertexHeights = new float[66049];
        this.vertexNormals = new float[132098];
        this.vertexValids = new boolean[66049];
        this.patchDirtyMap = new boolean[256];
        this.waterHeight = 0.0f;
        this.waterHeightValid = false;
        this.validCount = 0;
        this.terrainTextures = new TerrainTextures();
    }
    
    private void SetWaterHeight(final float waterHeight) {
        synchronized (this) {
            if (this.waterHeight != waterHeight || (this.waterHeightValid ^ true)) {
                this.waterHeight = waterHeight;
                this.waterHeightValid = true;
                this.updateEntireTerrain();
            }
        }
    }
    
    private void markVerticesDirty(int n, int n2, int n3, int n4) {
        n /= 16;
        n2 /= 16;
        final int n5 = n3 / 16;
        final int n6 = n4 / 16;
        final Object vertexLock = this.vertexLock;
        monitorenter(vertexLock);
        n3 = n2;
    Label_0085_Outer:
        while (true) {
            while (true) {
            Label_0091_Outer:
                while (true) {
                    while (true) {
                        Label_0043: {
                            if (n3 <= n6) {
                                n4 = n;
                                break Label_0043;
                            }
                            Label_0097: {
                                break Label_0097;
                            Label_0100_Outer:
                                while (true) {
                                Label_0100:
                                    while (true) {
                                        try {
                                            this.patchDirtyMap[n3 * 16 + n4] = true;
                                            ++n4;
                                            break;
                                            while (true) {
                                                iftrue(Label_0143:)(n3 < 0 || n3 >= 16 || n2 < 0 || n2 >= 16);
                                                Label_0108: {
                                                    while (true) {
                                                        Block_13: {
                                                            break Block_13;
                                                            iftrue(Label_0163:)(n2 > n6);
                                                            Block_8: {
                                                                break Block_8;
                                                                ++n3;
                                                                break Label_0108;
                                                            }
                                                            n3 = n;
                                                            break Label_0108;
                                                        }
                                                        SpatialIndex.getInstance().updateTerrainPatch(n3, n2, this);
                                                        continue Label_0091_Outer;
                                                    }
                                                    monitorexit(vertexLock);
                                                    continue Label_0100;
                                                }
                                                iftrue(Label_0157:)(n3 > n5);
                                                continue Label_0100_Outer;
                                            }
                                            ++n3;
                                            continue Label_0085_Outer;
                                        }
                                        finally {
                                            monitorexit(vertexLock);
                                        }
                                        Label_0157: {
                                            ++n2;
                                        }
                                        continue Label_0100;
                                    }
                                }
                            }
                        }
                        if (n4 > n5) {
                            continue;
                        }
                        break;
                    }
                    if (n4 >= 0 && n4 < 16 && n3 >= 0 && n3 < 16) {
                        continue Label_0085_Outer;
                    }
                    break;
                }
                continue;
            }
        }
        Label_0163:;
    }
    
    private void updateVerticesInRegion(final int n, int n2, final int n3, final int n4) {
        for (int i = n2; i <= n4; ++i) {
            for (int j = n; j <= n3; ++j) {
                final int min = Math.min(Math.max(0, j - 1), 255);
                final int min2 = Math.min(Math.max(0, i - 1), 255);
                final int min3 = Math.min(Math.max(0, j), 255);
                final int min4 = Math.min(Math.max(0, i), 255);
                final float n5 = 0.0f;
                final int n6 = n2 = 0;
                float n7 = n5;
                if (min >= 0) {
                    n2 = n6;
                    n7 = n5;
                    if (min < 256) {
                        n2 = n6;
                        n7 = n5;
                        if (min2 >= 0) {
                            n2 = n6;
                            n7 = n5;
                            if (min2 < 256) {
                                n2 = n6;
                                n7 = n5;
                                if (this.validMap[min2 * 256 + min]) {
                                    n7 = 0.0f + this.heightMap[min2 * 256 + min];
                                    n2 = 1;
                                }
                            }
                        }
                    }
                }
                int n8 = n2;
                float n9 = n7;
                if (min3 >= 0) {
                    n8 = n2;
                    n9 = n7;
                    if (min3 < 256) {
                        n8 = n2;
                        n9 = n7;
                        if (min2 >= 0) {
                            n8 = n2;
                            n9 = n7;
                            if (min2 < 256) {
                                n8 = n2;
                                n9 = n7;
                                if (this.validMap[min2 * 256 + min3]) {
                                    n9 = n7 + this.heightMap[min2 * 256 + min3];
                                    n8 = n2 + 1;
                                }
                            }
                        }
                    }
                }
                n2 = n8;
                float n10 = n9;
                if (min >= 0) {
                    n2 = n8;
                    n10 = n9;
                    if (min < 256) {
                        n2 = n8;
                        n10 = n9;
                        if (min4 >= 0) {
                            n2 = n8;
                            n10 = n9;
                            if (min4 < 256) {
                                n2 = n8;
                                n10 = n9;
                                if (this.validMap[min4 * 256 + min]) {
                                    n10 = n9 + this.heightMap[min4 * 256 + min];
                                    n2 = n8 + 1;
                                }
                            }
                        }
                    }
                }
                int n11 = n2;
                float n12 = n10;
                if (min3 >= 0) {
                    n11 = n2;
                    n12 = n10;
                    if (min3 < 256) {
                        n11 = n2;
                        n12 = n10;
                        if (min4 >= 0) {
                            n11 = n2;
                            n12 = n10;
                            if (min4 < 256) {
                                n11 = n2;
                                n12 = n10;
                                if (this.validMap[min4 * 256 + min3]) {
                                    n12 = n10 + this.heightMap[min4 * 256 + min3];
                                    n11 = n2 + 1;
                                }
                            }
                        }
                    }
                }
                if (n11 == 4) {
                    this.vertexHeights[i * 257 + j] = n12 / n11;
                    final float n13 = this.heightMap[min4 * 256 + min3];
                    final float n14 = this.heightMap[min + min4 * 256];
                    final float n15 = this.heightMap[min4 * 256 + min3];
                    final float n16 = this.heightMap[min2 * 256 + min3];
                    this.vertexNormals[(i * 257 + j) * 2] = n13 - n14;
                    this.vertexNormals[(i * 257 + j) * 2 + 1] = n15 - n16;
                    this.vertexValids[i * 257 + j] = true;
                }
                else {
                    this.vertexValids[i * 257 + j] = false;
                }
            }
        }
    }
    
    public void ApplyRegionInfo(final RegionHandshake.RegionInfo regionInfo) {
        synchronized (this) {
            this.SetWaterHeight(regionInfo.WaterHeight);
            final TerrainTextures terrainTextures = new TerrainTextures(regionInfo);
            if (!terrainTextures.equals(this.terrainTextures)) {
                this.terrainTextures = terrainTextures;
                this.updateEntireTerrain();
            }
        }
    }
    
    public void ProcessLayerData(final byte[] array) {
        final BitBuffer bitBuffer = new BitBuffer(array);
        final int bits = bitBuffer.getBits(16);
        final int bits2 = bitBuffer.getBits(8);
        Debug.Log(String.format("Terrain: ProcessLayerData: stride 0x%x patchSize 0x%x type 0x%x", bits, bits2, bitBuffer.getBits(8)));
        synchronized (this.vertexLock) {
            while (!bitBuffer.isEOF()) {
                final TerrainPatch decompressPatch = TerrainPatch.DecompressPatch(bitBuffer, bits2);
                if (decompressPatch == null) {
                    break;
                }
                final int x = decompressPatch.getX();
                final int y = decompressPatch.getY();
                if (x >= 16 || y >= 16) {
                    continue;
                }
                for (int i = 0; i < bits2; ++i) {
                    final int n = y * 16 + i;
                    if (n >= 0 && n < 256) {
                        for (int j = 0; j < bits2; ++j) {
                            final int n2 = x * 16 + j;
                            if (n2 >= 0 && n2 < 256) {
                                this.heightMap[n * 256 + n2] = decompressPatch.heightMap[i * bits2 + j];
                                if (!this.validMap[n * 256 + n2]) {
                                    ++this.validCount;
                                    this.validMap[n2 + n * 256] = true;
                                }
                            }
                        }
                    }
                }
                this.markVerticesDirty(x * 16, y * 16, (x + 1) * 16 + 1, (y + 1) * 16 + 1);
            }
            monitorexit(this.vertexLock);
            Debug.Printf("Terrain: LayerData received, valid count is now %d", this.validCount);
        }
    }
    
    public TerrainPatchInfo getPatchInfo(final int n, final int n2) {
        int n3;
        while (true) {
            while (true) {
                int n4 = 0;
                Label_0133: {
                    synchronized (this.vertexLock) {
                        if (this.patchDirtyMap[n2 * 16 + n]) {
                            this.patchDirtyMap[n2 * 16 + n] = false;
                            this.updateVerticesInRegion(n * 16, n2 * 16, (n + 1) * 16, (n2 + 1) * 16);
                        }
                        monitorexit(this.vertexLock);
                        int i = 0;
                        n3 = 1;
                        while (i < 17) {
                            n4 = 0;
                            int n5 = n3;
                            if (n4 < 17) {
                                if (this.vertexValids[(n2 * 16 + i) * 257 + n4 + n * 16]) {
                                    break Label_0133;
                                }
                                n5 = 0;
                            }
                            ++i;
                            n3 = n5;
                        }
                        break;
                    }
                }
                ++n4;
                continue;
            }
        }
        if (n3 != 0) {
            final float[] array = new float[289];
            final float[] array2 = new float[578];
            for (int j = 0; j < 17; ++j) {
                final int n6 = (n2 * 16 + j) * 257;
                for (int k = 0; k < 17; ++k) {
                    final float n7 = this.vertexHeights[n6 + k + n * 16];
                    final float n8 = this.vertexNormals[(n6 + k + n * 16) * 2];
                    final float n9 = this.vertexNormals[(n6 + k + n * 16) * 2 + 1];
                    array[j * 17 + k] = n7;
                    array2[(j * 17 + k) * 2] = n8;
                    array2[(j * 17 + k) * 2 + 1] = n9;
                }
            }
            return new TerrainPatchInfo(new TerrainPatchHeightMap(this.waterHeight, array, array2, 17, 17), this.terrainTextures, n / 16.0f, n / 16.0f, 0.0625f, 0.0625f);
        }
        return null;
    }
    
    public final boolean isUnderWater(final float n) {
        return this.waterHeightValid && n < this.waterHeight;
    }
    
    public void reset() {
        final int n = 0;
        synchronized (this.vertexLock) {
            this.waterHeightValid = false;
            this.validCount = 0;
            for (int i = 0; i < 65536; ++i) {
                this.validMap[i] = false;
            }
            int n2 = 0;
            int j;
            while (true) {
                j = n;
                if (n2 >= 66049) {
                    break;
                }
                this.vertexValids[n2] = false;
                ++n2;
            }
            while (j < 256) {
                this.patchDirtyMap[j] = false;
                ++j;
            }
        }
    }
    
    public void updateEntireTerrain() {
        this.markVerticesDirty(0, 0, 256, 256);
    }
}
