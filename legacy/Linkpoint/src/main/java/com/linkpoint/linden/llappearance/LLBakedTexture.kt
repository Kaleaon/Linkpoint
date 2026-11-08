package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llimage.ImageRaw

enum class BakedTextureIndex {
    HEAD,
    UPPER,
    LOWER,
    EYES,
    SKIRT,
    HAIR,
    LEFT_ARM,
    LEFT_LEG,
    AUX1,
    AUX2,
    AUX3,
    NUM_INDICES,
}

class LLBakedTexture(val index: BakedTextureIndex) {

    var resultId: LLUUID = LLUUID.NULL
        private set
    var isLocallyDirty: Boolean = true
        private set
    var isUploaded: Boolean = false
        private set
    var needsUpdate: Boolean = true
        private set

    private val layerList: MutableList<String> = mutableListOf()
    private var bakedImage: ImageRaw? = null

    fun addLayer(layerName: String) {
        layerList.add(layerName)
        isLocallyDirty = true
    }

    fun removeLayer(layerName: String) {
        layerList.remove(layerName)
        isLocallyDirty = true
    }

    fun getLayers(): List<String> = layerList.toList()

    fun requestUpdate() {
        needsUpdate = true
        isLocallyDirty = true
    }

    fun bake(width: Int = 512, height: Int = 512): Boolean {
        bakedImage = ImageRaw(width, height, 4)
        isLocallyDirty = false
        needsUpdate = false
        resultId = LLUUID.generate()
        return true
    }

    fun uploadComplete(id: LLUUID) {
        resultId = id
        isUploaded = true
    }

    fun getBakedImage(): ImageRaw? = bakedImage

    fun invalidate() {
        resultId = LLUUID.NULL
        isUploaded = false
        isLocallyDirty = true
        needsUpdate = true
        bakedImage = null
    }

    fun isValid(): Boolean = resultId != LLUUID.NULL

    override fun toString(): String =
        "LLBakedTexture(index=$index, id=$resultId, dirty=$isLocallyDirty, uploaded=$isUploaded)"
}
