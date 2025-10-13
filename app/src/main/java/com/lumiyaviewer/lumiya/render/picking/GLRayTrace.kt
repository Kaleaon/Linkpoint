package com.lumiyaviewer.lumiya.render.picking

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.LLVector4
import kotlin.math.abs

object GLRayTrace {

    data class RayIntersectInfo(
        val intersectPoint: LLVector4,
        val s: Float,
        val t: Float
    ) {
        override fun toString(): String {
            return "RayIntersectInfo{intersectPoint=$intersectPoint, s=$s, t=$t}"
        }
    }

    @JvmStatic
    fun getIntersectionDepth(
        renderContext: RenderContext,
        point: LLVector4,
        matrix: FloatArray
    ): Float {
        val v1 = FloatArray(4)
        val result = FloatArray(8)
        
        v1[0] = point.x
        v1[1] = point.y
        v1[2] = point.z
        v1[3] = 1.0f
        
        Matrix.multiplyMV(result, 0, matrix, 0, v1, 0)
        
        if (renderContext.hasGL20) {
            Matrix.multiplyMV(
                result, 4,
                renderContext.modelViewMatrix.matrixData,
                renderContext.modelViewMatrix.matrixDataOffset,
                result, 0
            )
        } else {
            Matrix.multiplyMV(
                result, 4,
                renderContext.projectionMatrix.matrixData,
                renderContext.projectionMatrix.matrixDataOffset,
                result, 0
            )
        }
        
        return result[6]
    }

    @JvmStatic
    fun intersect_RayTriangle(
        rayOrigin: LLVector3,
        rayEnd: LLVector3,
        vertices: Array<LLVector3>,
        offset: Int
    ): RayIntersectInfo? {
        // Triangle edges
        val edge1 = LLVector3.sub(vertices[offset + 1], vertices[offset + 0])
        val edge2 = LLVector3.sub(vertices[offset + 2], vertices[offset + 0])
        
        // Calculate normal
        val normal = LLVector3.cross(edge1, edge2)
        if (normal.isZero()) {
            return null // Degenerate triangle
        }
        
        // Ray direction
        val rayDir = LLVector3.sub(rayEnd, rayOrigin)
        
        // Calculate intersection distance
        val d = -normal.dot(LLVector3.sub(rayOrigin, vertices[offset + 0]))
        val denominator = normal.dot(rayDir)
        
        if (abs(denominator) < 1.0E-7f) {
            return null // Ray parallel to triangle
        }
        
        val t = d / denominator
        if (t < 0.0) {
            return null // Triangle behind ray origin
        }
        
        // Calculate intersection point
        val intersectionPoint = LLVector3(rayDir).apply {
            mul(t)
            add(rayOrigin)
        }
        
        // Barycentric coordinates test
        val dot00 = edge1.dot(edge1)
        val dot01 = edge1.dot(edge2)
        val dot11 = edge2.dot(edge2)
        
        val v = LLVector3.sub(intersectionPoint, vertices[offset + 0])
        val dot20 = v.dot(edge1)
        val dot21 = v.dot(edge2)
        
        val invDenom = (dot01 * dot01) - (dot00 * dot11)
        if (abs(invDenom) < 1.0E-7f) {
            return null
        }
        
        val u = ((dot01 * dot21) - (dot11 * dot20)) / invDenom
        if (u < 0.0 || u > 1.0) {
            return null
        }
        
        val w = ((dot20 * dot01) - (dot21 * dot00)) / invDenom
        if (w < 0.0 || u + w > 1.0) {
            return null
        }
        
        return RayIntersectInfo(
            LLVector4(intersectionPoint.x, intersectionPoint.y, intersectionPoint.z, t),
            u,
            w
        )
    }
}
