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
        intersectPoint: LLVector4,
        transformMatrix: FloatArray
    ): Float {
        val point = FloatArray(4)
        val result = FloatArray(8)
        
        point[0] = intersectPoint.x
        point[1] = intersectPoint.y
        point[2] = intersectPoint.z
        point[3] = 1.0f
        
        Matrix.multiplyMV(result, 0, transformMatrix, 0, point, 0)
        
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
        vertexOffset: Int
    ): RayIntersectInfo? {
        val edge1 = LLVector3.sub(vertices[vertexOffset + 1], vertices[vertexOffset + 0])
        val edge2 = LLVector3.sub(vertices[vertexOffset + 2], vertices[vertexOffset + 0])
        val normal = LLVector3.cross(edge1, edge2)
        
        if (normal.isZero()) {
            return null
        }
        
        val rayDirection = LLVector3.sub(rayEnd, rayOrigin)
        val distToPlane = -normal.dot(LLVector3.sub(rayOrigin, vertices[vertexOffset + 0]))
        val dirDotNormal = normal.dot(rayDirection)
        
        if (abs(dirDotNormal) < 1.0E-7f) {
            return null
        }
        
        var t = distToPlane / dirDotNormal
        
        if (t < 0.0) {
            return null
        }
        
        val intersectionPoint = LLVector3(rayDirection)
        intersectionPoint.mul(t)
        intersectionPoint.add(rayOrigin)
        
        val dot11 = edge1.dot(edge1)
        val dot12 = edge1.dot(edge2)
        val dot22 = edge2.dot(edge2)
        val w = LLVector3.sub(intersectionPoint, vertices[vertexOffset + 0])
        val dotW1 = w.dot(edge1)
        val dotW2 = w.dot(edge2)
        
        val denominator = (dot12 * dot12) - (dot11 * dot22)
        
        if (abs(denominator) < 1.0E-7f) {
            return null
        }
        
        var u = ((dot12 * dotW2) - (dot22 * dotW1)) / denominator
        
        if (u < 0.0 || u > 1.0) {
            return null
        }
        
        var v = ((dotW1 * dot12) - (dotW2 * dot11)) / denominator
        
        if (v < 0.0 || (u + v) > 1.0) {
            return null
        }
        
        return RayIntersectInfo(
            LLVector4(intersectionPoint.x, intersectionPoint.y, intersectionPoint.z, t),
            u,
            v
        )
    }
}
