package com.linkpoint.slproto.baker

import com.google.common.collect.Table
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.eventbus.EventBus
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.avatar.AvatarSkeleton
import com.linkpoint.render.avatar.DrawableAvatarPart
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.textures.TextureCache
import com.linkpoint.slproto.assets.SLWearable
import com.linkpoint.slproto.assets.SLWearableData
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import com.linkpoint.slproto.avatar.BakedTextureIndex
import com.linkpoint.slproto.avatar.SLAvatarParams
import com.linkpoint.slproto.events.SLBakingProgressEvent
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.texuploader.SLTextureUploadRequest
import com.linkpoint.slproto.modules.texuploader.SLTextureUploader
import com.linkpoint.slproto.textures.MutableSLTextureEntryFace
import com.linkpoint.slproto.textures.SLTextureEntry
import com.linkpoint.slproto.textures.SLTextureEntryFace
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.EnumMap
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.UUID

class BakeProcess : SLTextureUploadRequest.TextureUploadCompleteListener {
    private SLAvatarAppearance avatarAppearance
    private Map<BakedTextureIndex, BakedImage> bakedImages = EnumMap(BakedTextureIndex.class)
    private Thread bakingThread
    private EventBus eventBus
    private Map<Integer, Float> paramValues
    private Object textureReadyLock = Object()
    private SLTextureUploader uploader
    private Map<SLWearable, List<WearableTextureData>> wearables = IdentityHashMap()
    private Table<SLWearableType, UUID, SLWearable> wornWearables

    private class BakedImageUploadRequest : SLTextureUploadRequest {
        BakedImage bakedImage
        BakedTextureIndex bakedIndex

        BakedImageUploadRequest(BakedImage bakedImage2, BakedTextureIndex bakedTextureIndex, File file) {
            super(file, bakedTextureIndex.ordinal())
            this.bakedImage = bakedImage2
            this.bakedIndex = bakedTextureIndex
        }
    }

    class DefaultTextureException : Exception {
        DefaultTextureException() {
        }
    }

    private class WearableTextureData : ResourceConsumer {
        private SLWearableData.WearableTexture texture
        /* access modifiers changed from: private */
        volatile OpenJPEG textureData
        private volatile Boolean textureReady = false

        WearableTextureData(SLWearableData.WearableTexture wearableTexture) {
            this.texture = wearableTexture
        }

        Unit OnResourceReady(Object obj, Boolean z) {
            if (obj instanceof OpenJPEG) {
                this.textureData = (OpenJPEG) obj
            }
            this.textureReady = true
            BakeProcess.this.notifyTextureReady()
        }

        /* access modifiers changed from: protected */
        SLWearableData.WearableTexture getTexture() {
            return this.texture
        }

        /* access modifiers changed from: package-private */
        OpenJPEG getTextureData() {
            return this.textureData
        }

        /* access modifiers changed from: package-private */
        Boolean getTextureReady() {
            return this.textureReady
        }

        /* access modifiers changed from: package-private */
        Unit requestData() {
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(this.texture.textureID, TextureClass.Asset), this)
        }
    }

    BakeProcess(Table<SLWearableType, UUID, SLWearable> table, SLAvatarAppearance sLAvatarAppearance, SLTextureUploader sLTextureUploader, EventBus eventBus2) {
        Debug.Printf("Baking: BakeProcess created", Object[0])
        this.avatarAppearance = sLAvatarAppearance
        this.wornWearables = table
        this.uploader = sLTextureUploader
        this.eventBus = eventBus2
        for (SLWearable sLWearable : table.values()) {
            SLWearableData wearableData = sLWearable.getWearableData()
            if (wearableData != null) {
                ArrayList arrayList = ArrayList(wearableData.textures.size())
                for (SLWearableData.WearableTexture wearableTextureData : wearableData.textures) {
                    arrayList.add(WearableTextureData(wearableTextureData))
                }
                this.wearables.put(sLWearable, arrayList)
            }
        }
        this.bakingThread = Thread($Lambda$qb61PwDoxRPFEOdyYwns3UfUTbM(this), "Baker")
        this.bakingThread.start()
    }

    private SLTextureEntry PrepareAvatarTextureEntry() {
        UUID uploadedID
        SLTextureEntryFace create = SLTextureEntryFace.create(MutableSLTextureEntryFace(-1))
        SLTextureEntryFace[] sLTextureEntryFaceArr = SLTextureEntryFace[32]
        for (BakedTextureIndex bakedTextureIndex : BakedTextureIndex.values()) {
            Int ordinal = bakedTextureIndex.getFaceIndex().ordinal()
            BakedImage bakedImage = this.bakedImages.get(bakedTextureIndex)
            if (!(bakedImage == null || (uploadedID = bakedImage.getUploadedID()) == null)) {
                MutableSLTextureEntryFace mutableSLTextureEntryFace = MutableSLTextureEntryFace(0)
                mutableSLTextureEntryFace.setTextureID(uploadedID)
                sLTextureEntryFaceArr[ordinal] = SLTextureEntryFace.create(mutableSLTextureEntryFace)
            }
        }
        return SLTextureEntry.create(create, sLTextureEntryFaceArr)
    }

    /* access modifiers changed from: private */
    /* renamed from: bakeAppearance */
    Unit m140com_lumiyaviewer_lumiya_slproto_baker_BakeProcessmthref0() {
        Debug.Printf("Baking: Requesting texture data.", Object[0])
        for (List<WearableTextureData> it : this.wearables.values()) {
            for (WearableTextureData requestData : it) {
                requestData.requestData()
            }
        }
        synchronized (this.textureReadyLock) {
            while (!isTexturesReady()) {
                try {
                    this.textureReadyLock.wait()
                } catch (InterruptedException e) {
                    finishBaking((SLTextureEntry) null)
                    Debug.Printf("Baking: Interrupted before textures were ready.", Object[0])
                    return
                }
            }
        }
        Debug.Log("Baking: calculating param values...")
        this.paramValues = calcAllParamValues(this.wornWearables)
        Debug.Log("Baking: baking...")
        Boolean isWearingSkirt = isWearingSkirt()
        File cacheDir = GlobalOptions.getInstance().getCacheDir("baker")
        cacheDir.mkdirs()
        for (BakedTextureIndex bakedTextureIndex : BakedTextureIndex.values()) {
            if (Thread.interrupted()) {
                Debug.Log("Baking: interrupted.")
                this.eventBus.publish(SLBakingProgressEvent(false, true, 0))
                finishBaking((SLTextureEntry) null)
                return
            }
            if (bakedTextureIndex != BakedTextureIndex.BAKED_SKIRT || !(!isWearingSkirt)) {
                Debug.Log("Baking: Baking layer " + bakedTextureIndex)
                BakedImage bakedImage = BakedImage(BakeLayers.layerSets.get(bakedTextureIndex))
                this.bakedImages.put(bakedTextureIndex, bakedImage)
                bakedImage.Bake(this)
                if (Thread.interrupted()) {
                    Debug.Log("Baking: interrupted.")
                    this.eventBus.publish(SLBakingProgressEvent(false, true, 0))
                    finishBaking((SLTextureEntry) null)
                    return
                }
                try {
                    File file = File(cacheDir, bakedTextureIndex.toString() + ".j2k")
                    bakedImage.SaveToJPEG2K(file)
                    BakedImageUploadRequest bakedImageUploadRequest = BakedImageUploadRequest(bakedImage, bakedTextureIndex, file)
                    bakedImageUploadRequest.setOnUploadComplete(this)
                    this.uploader.BeginUpload(bakedImageUploadRequest)
                } catch (IOException e2) {
                    e2.printStackTrace()
                }
                Debug.Log("Baking: Done layer " + bakedTextureIndex)
            }
        }
        Debug.Log("Baking: Baked all layers.")
    }

    private Map<Integer, Float> calcAllParamValues(Table<SLWearableType, UUID, SLWearable> table) {
        HashMap hashMap = HashMap()
        for (Map.Entry entry : SLAvatarParams.paramByIDs.entrySet()) {
            hashMap.put((Integer) entry.getKey(), Float.valueOf(((SLAvatarParams.AvatarParam) ((SLAvatarParams.ParamSet) entry.getValue()).params.get(0)).defValue))
        }
        for (SLWearable wearableData : table.values()) {
            SLWearableData wearableData2 = wearableData.getWearableData()
            if (wearableData2 != null) {
                for (SLWearableData.WearableParam wearableParam : wearableData2.params) {
                    hashMap.put(Integer.valueOf(wearableParam.paramIndex), Float.valueOf(wearableParam.paramValue))
                    SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableParam.paramIndex))
                    if (paramSet != null) {
                        SLAvatarParams.AvatarParam avatarParam = (SLAvatarParams.AvatarParam) paramSet.params.get(0)
                        if (avatarParam.drivenParams != null) {
                            for (SLAvatarParams.DrivenParam drivenParam : avatarParam.drivenParams) {
                                SLAvatarParams.ParamSet paramSet2 = SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenParam.drivenID))
                                if (paramSet2 != null) {
                                    for (SLAvatarParams.AvatarParam drivenWeight : paramSet2.params) {
                                        hashMap.put(Integer.valueOf(drivenParam.drivenID), Float.valueOf(AvatarSkeleton.getDrivenWeight(wearableParam.paramValue, avatarParam, drivenParam, drivenWeight)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return hashMap
    }

    private Unit finishBaking(SLTextureEntry sLTextureEntry) {
        this.avatarAppearance.finishBaking(this, sLTextureEntry)
    }

    private Boolean isTexturesReady() {
        Boolean z = true
        for (List<WearableTextureData> it : this.wearables.values()) {
            for (WearableTextureData textureReady : it) {
                if (!textureReady.getTextureReady()) {
                    z = false
                }
            }
        }
        return z
    }

    private Boolean isWearingSkirt() {
        return !this.wornWearables.row(SLWearableType.WT_SKIRT).isEmpty()
    }

    /* access modifiers changed from: private */
    Unit notifyTextureReady() {
        synchronized (this.textureReadyLock) {
            this.textureReadyLock.notifyAll()
        }
    }

    Unit OnTextureUploadComplete(SLTextureUploadRequest sLTextureUploadRequest) {
        if (sLTextureUploadRequest instanceof BakedImageUploadRequest) {
            BakedImageUploadRequest bakedImageUploadRequest = (BakedImageUploadRequest) sLTextureUploadRequest
            Debug.Log("Baking: texture " + bakedImageUploadRequest.bakedIndex + " uploaded, UUID = " + bakedImageUploadRequest.getTextureID())
            bakedImageUploadRequest.bakedImage.setUploadedID(bakedImageUploadRequest.getTextureID())
            this.bakedImages.put(bakedImageUploadRequest.bakedIndex, bakedImageUploadRequest.bakedImage)
            Boolean isWearingSkirt = isWearingSkirt()
            BakedTextureIndex[] values = BakedTextureIndex.values()
            Int length = values.length
            Int i3 = 0
            Boolean z2 = true
            Int i4 = 0
            Int i5 = 0
            while (i3 < length) {
                BakedTextureIndex bakedTextureIndex = values[i3]
                if (bakedTextureIndex != BakedTextureIndex.BAKED_SKIRT || !(!isWearingSkirt)) {
                    Int i6 = i5 + 1
                    if (!this.bakedImages.containsKey(bakedTextureIndex)) {
                        i = i4 + 1
                        i2 = i6
                        z = false
                    } else if (this.bakedImages.get(bakedTextureIndex).getUploadedID() == null) {
                        i = i4 + 1
                        i2 = i6
                        z = false
                    } else {
                        z = z2
                        i = i4
                        i2 = i6
                    }
                } else {
                    Boolean z3 = z2
                    i = i4
                    i2 = i5
                    z = z3
                }
                i3++
                Boolean z4 = z
                i5 = i2
                i4 = i
                z2 = z4
            }
            if (z2) {
                this.eventBus.publish(SLBakingProgressEvent(false, true, 100))
                Debug.Log("Baking: all textures uploaded.")
                finishBaking(PrepareAvatarTextureEntry())
                return
            }
            this.eventBus.publish(SLBakingProgressEvent(false, false, ((i5 - i4) * 100) / i5))
        }
    }

    Unit cancel() {
        this.bakingThread.interrupt()
    }

    /* access modifiers changed from: package-private */
    List<OpenJPEG> getLocalTexture(AvatarTextureFaceIndex avatarTextureFaceIndex) throws DefaultTextureException {
        OpenJPEG textureData
        Boolean z = false
        LinkedList linkedList = null
        for (List<WearableTextureData> it : this.wearables.values()) {
            for (WearableTextureData wearableTextureData : it) {
                if (wearableTextureData.getTexture().layer == avatarTextureFaceIndex.ordinal()) {
                    if (wearableTextureData.getTexture().textureID != null && wearableTextureData.getTexture().textureID.equals(DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE)) {
                        z = true
                    } else if (!(wearableTextureData.textureData == null || (textureData = wearableTextureData.getTextureData()) == null)) {
                        if (linkedList == null) {
                            linkedList = LinkedList()
                        }
                        linkedList.add(textureData)
                    }
                }
            }
        }
        if (linkedList != null || !z) {
            return linkedList
        }
        throw DefaultTextureException()
    }

    /* access modifiers changed from: package-private */
    float getParamWeight(Int i, SLAvatarParams.AvatarParam avatarParam) {
        Float f = this.paramValues.get(Integer.valueOf(i))
        return f != null ? f.floatValue() : avatarParam.defValue
    }
}
