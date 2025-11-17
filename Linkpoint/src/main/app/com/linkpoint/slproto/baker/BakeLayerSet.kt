package com.linkpoint.slproto.baker

import com.linkpoint.slproto.avatar.BakedTextureIndex

data class BakeLayerSet(
    var bakedTextureIndex: BakedTextureIndex,
    var width: Int,
    var height: Int,
    var clear_alpha: Boolean,
    var layers: Array<BakeLayer>,
    var maskLayers: Array<BakeLayer>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BakeLayerSet

        if (bakedTextureIndex != other.bakedTextureIndex) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (clear_alpha != other.clear_alpha) return false
        if (!layers.contentEquals(other.layers)) return false
        if (!maskLayers.contentEquals(other.maskLayers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bakedTextureIndex.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + clear_alpha.hashCode()
        result = 31 * result + layers.contentHashCode()
        result = 31 * result + maskLayers.contentHashCode()
        return result
    }
}