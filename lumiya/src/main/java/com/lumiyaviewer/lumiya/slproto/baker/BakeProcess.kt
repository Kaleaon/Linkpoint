package com.lumiyaviewer.lumiya.slproto.baker

import com.google.common.collect.Table
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarPart
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.textures.TextureCache
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploadRequest
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader
import com.lumiyaviewer.lumiya.slproto.textures.MutableSLTextureEntryFace
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BakeProcess(
    table: Table<SLWearableType, UUID, SLWearable>,
    private val avatarAppearance: SLAvatarAppearance,
    private val uploader: SLTextureUploader,
    private val eventBus: EventBus
) : SLTextureUploadRequest.TextureUploadCompleteListener {

    private val bakedImages: MutableMap<BakedTextureIndex, BakedImage> = EnumMap(BakedTextureIndex::class.java)
    private var bakingThread: Thread? = null
    private lateinit var paramValues: Map<Int, Float>
    private val textureReadyLock = Object()
    private val wearables: MutableMap<SLWearable, List<WearableTextureData>> = IdentityHashMap()
    private val wornWearables: Table<SLWearableType, UUID, SLWearable> = table

    private class BakedImageUploadRequest(
        val bakedImage: BakedImage,
        val bakedIndex: BakedTextureIndex,
        file: File
    ) : SLTextureUploadRequest(file, bakedIndex.ordinal)

    class DefaultTextureException : Exception()

    private inner class WearableTextureData(private val texture: SLWearableData.WearableTexture) : ResourceConsumer {
        @Volatile
        var textureData: OpenJPEG? = null
            private set
        @Volatile
        var isTextureReady: Boolean = false
            private set

        override fun OnResourceReady(resource: Any?, isSuccess: Boolean) {
            if (resource is OpenJPEG) {
                textureData = resource
            }
            isTextureReady = true
            this@BakeProcess.notifyTextureReady()
        }

        fun getTexture(): SLWearableData.WearableTexture = texture

        fun requestData() {
            TextureCache.getInstance().RequestResource(
                DrawableTextureParams.create(texture.textureID, TextureClass.Asset), this
            )
        }
    }

    init {
        Debug.Printf("Baking: BakeProcess created")
        for (wearable in table.values()) {
            val wearableData = wearable.wearableData
            if (wearableData != null) {
                val list = ArrayList<WearableTextureData>(wearableData.textures.size)
                for (texture in wearableData.textures) {
                    list.add(WearableTextureData(texture))
                }
                wearables[wearable] = list
            }
        }
        bakingThread = Thread({ bakeAppearance() }, "Baker")
        bakingThread?.start()
    }

    private fun prepareAvatarTextureEntry(): SLTextureEntry {
        val face0 = SLTextureEntryFace.create(MutableSLTextureEntryFace(-1))
        val faces = arrayOfNulls<SLTextureEntryFace>(32)
        
        for (index in BakedTextureIndex.values()) {
            val ordinal = index.faceIndex.ordinal
            val image = bakedImages[index]
            val uploadedID = image?.uploadedID
            
            if (uploadedID != null) {
                val mutableFace = MutableSLTextureEntryFace(0)
                mutableFace.textureID = uploadedID
                faces[ordinal] = SLTextureEntryFace.create(mutableFace)
            }
        }
        // Handle nullable array elements if SLTextureEntry.create expects non-nulls or filter
        // Assuming create handles nulls or we need to fill defaults.
        // For now passing as is, assuming API compatibility
        return SLTextureEntry.create(face0, faces)
    }

    private fun bakeAppearance() {
        Debug.Printf("Baking: Requesting texture data.")
        for (list in wearables.values) {
            for (data in list) {
                data.requestData()
            }
        }

        synchronized(textureReadyLock) {
            while (!isTexturesReady()) {
                try {
                    textureReadyLock.wait()
                } catch (e: InterruptedException) {
                    finishBaking(null)
                    Debug.Printf("Baking: Interrupted before textures were ready.")
                    return
                }
            }
        }

        Debug.Log("Baking: calculating param values...")
        paramValues = calcAllParamValues(wornWearables)
        Debug.Log("Baking: baking...")
        
        val isWearingSkirt = isWearingSkirt()
        val cacheDir = GlobalOptions.getInstance().getCacheDir("baker")
        cacheDir.mkdirs()

        for (index in BakedTextureIndex.values()) {
            if (Thread.interrupted()) {
                Debug.Log("Baking: interrupted.")
                eventBus.publish(SLBakingProgressEvent(false, true, 0))
                finishBaking(null)
                return
            }

            if (index != BakedTextureIndex.BAKED_SKIRT || isWearingSkirt) {
                Debug.Log("Baking: Baking layer $index")
                val layerSet = BakeLayers.layerSets[index]
                if (layerSet != null) {
                    val bakedImage = BakedImage(layerSet)
                    bakedImages[index] = bakedImage
                    bakedImage.Bake(this)

                    if (Thread.interrupted()) {
                        Debug.Log("Baking: interrupted.")
                        eventBus.publish(SLBakingProgressEvent(false, true, 0))
                        finishBaking(null)
                        return
                    }

                    try {
                        val file = File(cacheDir, "$index.j2k")
                        bakedImage.SaveToJPEG2K(file)
                        val request = BakedImageUploadRequest(bakedImage, index, file)
                        request.setOnUploadComplete(this)
                        uploader.BeginUpload(request)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    Debug.Log("Baking: Done layer $index")
                }
            }
        }
        Debug.Log("Baking: Baked all layers.")
    }

    private fun calcAllParamValues(table: Table<SLWearableType, UUID, SLWearable>): Map<Int, Float> {
        val map = HashMap<Int, Float>()
        for ((key, value) in SLAvatarParams.paramByIDs.entries) {
            if (value.params.isNotEmpty()) {
                map[key] = value.params[0].defValue
            }
        }

        for (wearable in table.values()) {
            val data = wearable.wearableData
            if (data != null) {
                for (param in data.params) {
                    map[param.paramIndex] = param.paramValue
                    val paramSet = SLAvatarParams.paramByIDs[param.paramIndex]
                    if (paramSet != null && paramSet.params.isNotEmpty()) {
                        val avatarParam = paramSet.params[0]
                        if (avatarParam.drivenParams != null) {
                            for (driven in avatarParam.drivenParams) {
                                val drivenSet = SLAvatarParams.paramByIDs[driven.drivenID]
                                if (drivenSet != null) {
                                    for (drivenParam in drivenSet.params) {
                                        map[driven.drivenID] = AvatarSkeleton.getDrivenWeight(
                                            param.paramValue, avatarParam, driven, drivenParam
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return map
    }

    private fun finishBaking(entry: SLTextureEntry?) {
        avatarAppearance.finishBaking(this, entry)
    }

    private fun isTexturesReady(): Boolean {
        for (list in wearables.values) {
            for (data in list) {
                if (!data.isTextureReady) return false
            }
        }
        return true
    }

    private fun isWearingSkirt(): Boolean {
        return !wornWearables.row(SLWearableType.WT_SKIRT).isEmpty()
    }

    private fun notifyTextureReady() {
        synchronized(textureReadyLock) {
            textureReadyLock.notifyAll()
        }
    }

    override fun OnTextureUploadComplete(request: SLTextureUploadRequest) {
        if (request is BakedImageUploadRequest) {
            Debug.Log("Baking: texture ${request.bakedIndex} uploaded, UUID = ${request.getTextureID()}")
            request.bakedImage.uploadedID = request.getTextureID()
            bakedImages[request.bakedIndex] = request.bakedImage
            
            val isWearingSkirt = isWearingSkirt()
            var allUploaded = true
            var total = 0
            var uploaded = 0
            
            for (index in BakedTextureIndex.values()) {
                if (index != BakedTextureIndex.BAKED_SKIRT || isWearingSkirt) {
                    total++
                    if (!bakedImages.containsKey(index) || bakedImages[index]?.uploadedID == null) {
                        allUploaded = false
                    } else {
                        uploaded++
                    }
                }
            }

            if (allUploaded) {
                eventBus.publish(SLBakingProgressEvent(false, true, 100))
                Debug.Log("Baking: all textures uploaded.")
                finishBaking(prepareAvatarTextureEntry())
            } else {
                eventBus.publish(SLBakingProgressEvent(false, false, (uploaded * 100) / total))
            }
        }
    }

    fun cancel() {
        bakingThread?.interrupt()
    }

    @Throws(DefaultTextureException::class)
    fun getLocalTexture(faceIndex: AvatarTextureFaceIndex): List<OpenJPEG>? {
        var useDefault = false
        var list: LinkedList<OpenJPEG>? = null

        for (wearableList in wearables.values) {
            for (data in wearableList) {
                if (data.getTexture().layer == faceIndex.ordinal) {
                    if (DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE == data.getTexture().textureID) {
                        useDefault = true
                    } else {
                        val texData = data.textureData
                        if (texData != null) {
                            if (list == null) list = LinkedList()
                            list.add(texData)
                        }
                    }
                }
            }
        }

        if (list != null || !useDefault) {
            return list
        }
        throw DefaultTextureException()
    }

    fun getParamWeight(id: Int, param: SLAvatarParams.AvatarParam): Float {
        return paramValues[id] ?: param.defValue
    }
}
