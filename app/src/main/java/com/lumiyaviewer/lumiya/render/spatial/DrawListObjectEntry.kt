package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import kotlin.math.max
import kotlin.math.min

/**
 * Draw list entry for SL objects
 * Maintains object info and calculates bounding boxes
 */
abstract class DrawListObjectEntry(
    val objectInfo: SLObjectInfo
) : DrawListEntry() {

    /**
     * Get the associated object info
     */
    fun getObjectInfo(): SLObjectInfo = objectInfo

    /**
     * Update the bounding box based on object's world matrix and coordinates
     */
    fun updateBoundingBox() {
        val worldMatrix = objectInfo.worldMatrix ?: return
        
        val objectCoords = objectInfo.getObjectCoords()
        val data = objectCoords.getData()
        val elementOffset = objectCoords.getElementOffset(1)
        
        // Initialize bounding box with object position
        for (i in 0..2) {
            val pos = worldMatrix[i + 12]
            boundingBox[i] = pos
            boundingBox[i + 3] = pos
        }
        
        // Calculate bounding box from object dimensions
        for (dim in 0..2) {
            for (axis in 0..2) {
                val scale = data[elementOffset + axis]
                val matrixValue = worldMatrix[dim * 4 + axis]
                
                val negValue = matrixValue * (-scale / 2.0f)
                val posValue = matrixValue * (scale / 2.0f)
                
                val (minValue, maxValue) = if (negValue < posValue) {
                    Pair(negValue, posValue)
                } else {
                    Pair(posValue, negValue)
                }
                
                boundingBox[dim] += minValue
                boundingBox[dim + 3] += maxValue
            }
        }
        
        // Clamp bounding box to valid region bounds
        for (i in 0..2) {
            val maxBound = if (i == 2) 4096.0f else 256.0f
            boundingBox[i] = min(maxBound, max(0.0f, boundingBox[i]))
            boundingBox[i + 3] = min(maxBound, max(0.0f, boundingBox[i + 3]))
        }
    }
}
