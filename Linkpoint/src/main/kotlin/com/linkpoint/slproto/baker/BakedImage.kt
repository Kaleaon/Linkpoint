package com.linkpoint.slproto.baker

import android.graphics.Bitmap
import com.linkpoint.openjpeg.OpenJPEG
import java.io.File
import java.io.IOException
import java.util.UUID

class BakedImage {
    private BakeLayerSet layerSet
    private OpenJPEG resultImage
    private UUID uploadedID

    public BakedImage(BakeLayerSet bakeLayerSet) {
        this.layerSet = bakeLayerSet
        this.resultImage = OpenJPEG(bakeLayerSet.width, bakeLayerSet.height, 4, 4, 1, -1)
        this.resultImage.setComponent(4, (Byte) -1)
    }

    fun Bake(bakeProcess: BakeProcess) {
        for (BakeLayer Bake : this.layerSet.layers) {
            Bake.Bake(this.resultImage, bakeProcess)
        }
        if (this.layerSet.clear_alpha || this.layerSet.maskLayers.length > 0) {
            this.resultImage.setComponent(3, (Byte) -1)
        }
        for (BakeLayer BakeAlpha : this.layerSet.maskLayers) {
            BakeAlpha.BakeAlpha(this.resultImage, bakeProcess)
        }
    }

    fun SaveToJPEG2K(file: File) throws IOException {
        this.resultImage.SaveJPEG2K(file)
    }

     public fun getAsBitmap(): Bitmap {
        return this.resultImage.getAsBitmap()
    }

     public fun getBakedImage(): OpenJPEG {
        return this.resultImage
    }

     public fun getUploadedID(): UUID {
        return this.uploadedID
    }

    fun setUploadedID(uuid: UUID) {
        this.uploadedID = uuid
    }
}
