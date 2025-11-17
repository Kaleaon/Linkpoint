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

    BakedImage(BakeLayerSet bakeLayerSet) {
        this.layerSet = bakeLayerSet
        this.resultImage = OpenJPEG(bakeLayerSet.width, bakeLayerSet.height, 4, 4, 1, -1)
        this.resultImage.setComponent(4, (byte) -1)
    }

    Unit Bake(BakeProcess bakeProcess) {
        for (BakeLayer Bake : this.layerSet.layers) {
            Bake.Bake(this.resultImage, bakeProcess)
        }
        if (this.layerSet.clear_alpha || this.layerSet.maskLayers.length > 0) {
            this.resultImage.setComponent(3, (byte) -1)
        }
        for (BakeLayer BakeAlpha : this.layerSet.maskLayers) {
            BakeAlpha.BakeAlpha(this.resultImage, bakeProcess)
        }
    }

    Unit SaveToJPEG2K(File file) throws IOException {
        this.resultImage.SaveJPEG2K(file)
    }

    Bitmap getAsBitmap() {
        return this.resultImage.getAsBitmap()
    }

    OpenJPEG getBakedImage() {
        return this.resultImage
    }

    UUID getUploadedID() {
        return this.uploadedID
    }

    Unit setUploadedID(UUID uuid) {
        this.uploadedID = uuid
    }
}
