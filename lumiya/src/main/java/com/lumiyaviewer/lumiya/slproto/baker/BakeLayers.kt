package com.lumiyaviewer.lumiya.slproto.baker

import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex
import java.util.EnumMap

object BakeLayers {
    val layerSets: Map<BakedTextureIndex, BakeLayerSet> = EnumMap(BakedTextureIndex::class.java)

    init {
        // Populate with dummy sets for now to unblock compilation
        // In a real app, this would contain the definitions for all avatar layers (skin, clothes, etc.)
        for (index in BakedTextureIndex.values()) {
            (layerSets as EnumMap)[index] = BakeLayerSet(
                index,
                512, 512,
                true,
                arrayOf(), // Empty layers
                arrayOf()  // Empty mask layers
            )
        }
    }
}
