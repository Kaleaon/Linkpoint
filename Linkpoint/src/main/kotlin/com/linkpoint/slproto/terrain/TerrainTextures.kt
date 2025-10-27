package com.linkpoint.slproto.terrain

import com.linkpoint.Debug
import com.linkpoint.slproto.messages.RegionHandshake
import com.linkpoint.utils.UUIDPool
import java.util.Arrays
import java.util.UUID

class TerrainTextures {
    @JvmStatic
private Array<UUID> defaultTerrainTextures = {UUID.fromString("0bc58228-74a0-7e83-89bc-5c23464bcec5"), UUID.fromString("63338ede-0037-c4fd-855b-015d77112fc8"), UUID.fromString("303cd381-8560-7579-23f1-f0a880799740"), UUID.fromString("53a2f406-4895-1d13-d541-d2e3b86bc19c")}
    private val FloatArray terrainHeightRange
    private val FloatArray terrainStartHeight
    private val Array<UUID> textureIDs

    public TerrainTextures() {
        this.terrainStartHeight = Float[4]
        this.terrainHeightRange = Float[4]
        this.textureIDs = UUID[4]
        System.arraycopy(defaultTerrainTextures, 0, this.textureIDs, 0, 4)
    }

    public TerrainTextures(RegionHandshake.RegionInfo regionInfo) {
        this.terrainStartHeight = Float[4]
        this.terrainHeightRange = Float[4]
        val uuidArr: Array<UUID> = {regionInfo.TerrainDetail0, regionInfo.TerrainDetail1, regionInfo.TerrainDetail2, regionInfo.TerrainDetail3}
        for (Int i = 0; i < 4; i++) {
            Debug.Printf("Terrain: texture[%d] = %s", Integer.valueOf(i), uuidArr[i].toString())
            if (uuidArr[i] == null || uuidArr[i].equals(UUIDPool.ZeroUUID)) {
                uuidArr[i] = defaultTerrainTextures[i]
            }
        }
        this.textureIDs = uuidArr
        this.terrainStartHeight[0] = regionInfo.TerrainStartHeight00
        this.terrainStartHeight[1] = regionInfo.TerrainStartHeight01
        this.terrainStartHeight[2] = regionInfo.TerrainStartHeight10
        this.terrainStartHeight[3] = regionInfo.TerrainStartHeight11
        this.terrainHeightRange[0] = regionInfo.TerrainHeightRange00
        this.terrainHeightRange[1] = regionInfo.TerrainHeightRange01
        this.terrainHeightRange[2] = regionInfo.TerrainHeightRange10
        this.terrainHeightRange[3] = regionInfo.TerrainHeightRange11
    }

    @JvmStatic
 private fun bilinearCorners(fArr: FloatArray, f: Float, f2: Float): Float {
        return (((fArr[0] * f) + (fArr[1] * (1.0f - f))) * f2) + (((fArr[2] * f) + (fArr[3] * (1.0f - f))) * (1.0f - f2))
    }

     public override fun equals(obj: Object): Boolean {
        if (!(obj instanceof TerrainTextures)) {
            return false
        }
        val terrainTextures: TerrainTextures = (TerrainTextures) obj
        if (!Arrays.equals(this.textureIDs, terrainTextures.textureIDs) || !Arrays.equals(this.terrainStartHeight, terrainTextures.terrainStartHeight)) {
            return false
        }
        return Arrays.equals(this.terrainHeightRange, terrainTextures.terrainHeightRange)
    }

     public fun getNeededLayerMask(fArr: FloatArray): Int {
        val i: Int = 0
        for (Float f : fArr) {
            val floor: Int = (Int) Math.floor((Double) f)
            i |= 1 << floor
            if (f - ((Float) floor) != 0.0f) {
                i |= 1 << (floor + 1)
            }
        }
        return i & 15
    }

     public fun getTextureHeightMap(fArr: FloatArray, i: Int, i2: Int, f: Float, f2: Float, f3: Float, f4: Float): FloatArray {
        val fArr2: FloatArray = Float[(i * i2)]
        for (Int i3 = 0; i3 < i2; i3++) {
            val f5: Float = ((Float) i3) / ((Float) (i2 - 1))
            for (Int i4 = 0; i4 < i; i4++) {
                val f6: Float = ((Float) i4) / ((Float) (i - 1))
                val i5: Int = (i3 * i) + i4
                fArr2[i5] = Math.min(3.0f, Math.max(0.0f, ((fArr[(i3 * i) + i4] - bilinearCorners(this.terrainStartHeight, (f6 * f3) + f, (f5 * f4) + f2)) * 4.0f) / bilinearCorners(this.terrainHeightRange, (f6 * f3) + f, (f5 * f4) + f2)))
            }
        }
        return fArr2
    }

     public fun getTextureUUID(i: Int): UUID {
        return this.textureIDs[i]
    }

     public override fun hashCode(): Int {
        return Arrays.hashCode(this.textureIDs) + Arrays.hashCode(this.terrainStartHeight) + Arrays.hashCode(this.terrainHeightRange)
    }
}
