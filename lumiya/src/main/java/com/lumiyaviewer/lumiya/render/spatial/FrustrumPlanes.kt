package com.lumiyaviewer.lumiya.render.spatial

import kotlin.math.sqrt

class FrustrumPlanes(fArr: FloatArray) {
    companion object {
        const val INSIDE = 1
        const val INTERSECT = 0
        const val OUTSIDE = -1
    }

    data class Plane(val a: Float, val b: Float, val c: Float, val d: Float)

    val nearPlane: Plane
    val farPlane: Plane
    val leftPlane: Plane
    val rightPlane: Plane
    val topPlane: Plane
    val bottomPlane: Plane

    init {
        // Extract planes from VP matrix (fArr)
        // Assuming fArr is column-major VP matrix
        // Planes: Left, Right, Bottom, Top, Near, Far
        
        val planes = Array(6) { Plane(0f, 0f, 0f, 0f) }
        
        // Left
        planes[0] = extractPlane(fArr, 3, 0, 1f)
        // Right
        planes[1] = extractPlane(fArr, 3, 0, -1f)
        // Bottom
        planes[2] = extractPlane(fArr, 3, 1, 1f)
        // Top
        planes[3] = extractPlane(fArr, 3, 1, -1f)
        // Near
        planes[4] = extractPlane(fArr, 3, 2, 1f)
        // Far
        planes[5] = extractPlane(fArr, 3, 2, -1f)

        leftPlane = planes[0]
        rightPlane = planes[1]
        bottomPlane = planes[2]
        topPlane = planes[3]
        nearPlane = planes[4]
        farPlane = planes[5]
    }

    private fun extractPlane(mat: FloatArray, row: Int, axis: Int, sign: Float): Plane {
        // This logic in original file was:
        // params[i5 + i3] = fArr[(i3 * 4) + 3] + (fArr[(i3 * 4) + i2] * f)
        // This corresponds to adding/subtracting a row from the W row (3).
        
        val a = mat[3] + sign * mat[0 + axis]
        val b = mat[7] + sign * mat[4 + axis]
        val c = mat[11] + sign * mat[8 + axis]
        val d = mat[15] + sign * mat[12 + axis]
        
        val len = sqrt((a * a + b * b + c * c).toDouble()).toFloat()
        return Plane(a / len, b / len, c / len, d / len)
    }
}
