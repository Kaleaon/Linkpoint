package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import kotlin.math.max
import kotlin.math.min

abstract class DrawListObjectEntry(protected val objectInfo: SLObjectInfo) : DrawListEntry() {

    fun getObjectInfo(): SLObjectInfo = objectInfo

    fun updateBoundingBox() {
        val worldMatrix = objectInfo.worldMatrix ?: return
        
        val objectCoords = objectInfo.objectCoords
        val data = objectCoords.data
        val elementOffset = objectCoords.getElementOffset(1)

        // Initialize bounding box with position
        for (i in 0..2) {
            val pos = worldMatrix[i + 12]
            boundingBox[i] = pos
            boundingBox[i + 3] = pos
        }

        // Expand bounding box based on object size and rotation
        for (axis in 0..2) {
            for (component in 0..2) {
                val matrixValue = worldMatrix[axis * 4 + component]
                val halfSize = data[elementOffset + component] / 2.0f
                
                val negContribution = matrixValue * -halfSize
                val posContribution = matrixValue * halfSize
                
                if (negContribution < posContribution) {
                    boundingBox[axis] += negContribution
                    boundingBox[axis + 3] += posContribution
                } else {
                    boundingBox[axis] += posContribution
                    boundingBox[axis + 3] += negContribution
                }
            }
        }

        // Clamp to world bounds
        for (i in 0..2) {
            val maxBound = if (i == 2) 4096.0f else 256.0f
            boundingBox[i] = min(maxBound, max(0.0f, boundingBox[i]))
            boundingBox[i + 3] = min(maxBound, max(0.0f, boundingBox[i + 3]))
        }
    }
}
