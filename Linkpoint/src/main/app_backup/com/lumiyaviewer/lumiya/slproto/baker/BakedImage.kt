package com.lumiyaviewer.lumiya.slproto.baker

import android.graphics.Bitmap
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import java.io.File
import java.io.IOException
import java.util.UUID

class BakedImage(private val layerSet: BakeLayerSet) {
    private val resultImage: OpenJPEG = OpenJPEG(layerSet.width, layerSet.height, 4, 4, 1, -1)
    var uploadedID: UUID? = null

    init {
        resultImage.setComponent(4, -1)
    }

    fun Bake(bakeProcess: BakeProcess) {
        for (layer in layerSet.layers) {
            layer.bake(resultImage, bakeProcess)
        }
        if (layerSet.clear_alpha || layerSet.maskLayers.isNotEmpty()) {
            resultImage.setComponent(3, -1)
        }
        for (maskLayer in layerSet.maskLayers) {
            maskLayer.bakeAlpha(resultImage, bakeProcess)
        }
    }

    @Throws(IOException::class)
    fun SaveToJPEG2K(file: File) {
        resultImage.SaveJPEG2K(file)
    }

    fun getAsBitmap(): Bitmap? {
        return resultImage.getAsBitmap()
    }

    fun getBakedImage(): OpenJPEG {
        return resultImage
    }
}
