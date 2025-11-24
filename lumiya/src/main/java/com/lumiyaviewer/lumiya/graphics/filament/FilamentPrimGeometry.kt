package com.lumiyaviewer.lumiya.graphics.filament

import android.util.Log
import com.google.android.filament.*
import com.lumiyaviewer.lumiya.slproto.prims.*
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/**
 * FilamentPrimGeometry - Generates geometry for Second Life primitives
 */
class FilamentPrimGeometry(
    private val engine: Engine
) {
    companion object {
        private const val TAG = "FilamentPrimGeometry"
        private const val SPHERE_SLICES = 16
        private const val CYLINDER_SLICES = 16
        private const val TORUS_SIDES = 12
        private const val TORUS_RINGS = 16
    }
    
    fun generatePrimMesh(
        volumeParams: PrimVolumeParams,
        scale: LLVector3
    ): Pair<VertexBuffer, IndexBuffer> {
        
        if (volumeParams.isMesh()) {
            Log.d(TAG, "Mesh prim - using placeholder")
            return generateBox(scale)
        }
        
        if (volumeParams.isSculpt()) {
            Log.d(TAG, "Sculpt prim - using placeholder")
            return generateBox(scale)
        }
        
        val profileParams = volumeParams.ProfileParams
        val pathParams = volumeParams.PathParams
        
        if (profileParams == null || pathParams == null) {
             return generateBox(scale)
        }

        // Corrected access to camelCase properties
        val profileType = (profileParams.CurveType.toInt() and PrimProfileParams.LL_PCODE_PROFILE_MASK.toInt()).toByte()
        val pathType = (pathParams.curveType.toInt() and PrimProfileParams.LL_PCODE_HOLE_MASK.toInt()).toByte()
        
        return when (profileType) {
            PrimProfileParams.LL_PCODE_PROFILE_SQUARE -> {
                generateBox(scale, volumeParams)
            }
            PrimProfileParams.LL_PCODE_PROFILE_CIRCLE,
            PrimProfileParams.LL_PCODE_PROFILE_CIRCLE_HALF -> {
                // Corrected constant access
                if (pathType == PrimPathParams.LL_PCODE_PATH_CIRCLE) {
                    generateTorus(scale, volumeParams)
                } else {
                    generateCylinder(scale, volumeParams)
                }
            }
            PrimProfileParams.LL_PCODE_PROFILE_EQUALTRI,
            PrimProfileParams.LL_PCODE_PROFILE_ISOTRI,
            PrimProfileParams.LL_PCODE_PROFILE_RIGHTTRI -> {
                generatePrism(scale, volumeParams)
            }
            else -> {
                Log.w(TAG, "Unknown profile type: $profileType, using box")
                generateBox(scale)
            }
        }
    }
    
    private fun generateBox(
        scale: LLVector3,
        volumeParams: PrimVolumeParams? = null
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val sx = scale.x
        val sy = scale.y
        val sz = scale.z
        
        val vertices = floatArrayOf(
            -sx, -sz,  sy,  0f,  0f,  1f,  0f, 0f,
             sx, -sz,  sy,  0f,  0f,  1f,  1f, 0f,
             sx,  sz,  sy,  0f,  0f,  1f,  1f, 1f,
            -sx,  sz,  sy,  0f,  0f,  1f,  0f, 1f,
            -sx, -sz, -sy,  0f,  0f, -1f,  1f, 0f,
            -sx,  sz, -sy,  0f,  0f, -1f,  1f, 1f,
             sx,  sz, -sy,  0f,  0f, -1f,  0f, 1f,
             sx, -sz, -sy,  0f,  0f, -1f,  0f, 0f,
            -sx,  sz, -sy,  0f,  1f,  0f,  0f, 1f,
            -sx,  sz,  sy,  0f,  1f,  0f,  0f, 0f,
             sx,  sz,  sy,  0f,  1f,  0f,  1f, 0f,
             sx,  sz, -sy,  0f,  1f,  0f,  1f, 1f,
            -sx, -sz, -sy,  0f, -1f,  0f,  1f, 1f,
             sx, -sz, -sy,  0f, -1f,  0f,  0f, 1f,
             sx, -sz,  sy,  0f, -1f,  0f,  0f, 0f,
            -sx, -sz,  sy,  0f, -1f,  0f,  1f, 0f,
             sx, -sz, -sy,  1f,  0f,  0f,  1f, 0f,
             sx,  sz, -sy,  1f,  0f,  0f,  1f, 1f,
             sx,  sz,  sy,  1f,  0f,  0f,  0f, 1f,
             sx, -sz,  sy,  1f,  0f,  0f,  0f, 0f,
            -sx, -sz, -sy, -1f,  0f,  0f,  0f, 0f,
            -sx, -sz,  sy, -1f,  0f,  0f,  1f, 0f,
            -sx,  sz,  sy, -1f,  0f,  0f,  1f, 1f,
            -sx,  sz, -sy, -1f,  0f,  0f,  0f, 1f
        )
        
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8, 9, 10, 8, 10, 11,
            12, 13, 14, 12, 14, 15,
            16, 17, 18, 16, 18, 19,
            20, 21, 22, 20, 22, 23
        )
        
        return createBuffers(vertices, indices)
    }
    
    private fun generateCylinder(
        scale: LLVector3,
        volumeParams: PrimVolumeParams
    ): Pair<VertexBuffer, IndexBuffer> {
        val radius = max(scale.x, scale.y)
        val height = scale.z * 2f
        val slices = CYLINDER_SLICES
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        for (i in 0..slices) {
            val angle = (i.toFloat() / slices) * PI.toFloat() * 2f
            val x = cos(angle) * radius
            val y = sin(angle) * radius
            val u = i.toFloat() / slices
            
            vertices.addAll(listOf(x, y, -height/2f, x/radius, y/radius, 0f, u, 0f))
            vertices.addAll(listOf(x, y, height/2f, x/radius, y/radius, 0f, u, 1f))
        }
        
        for (i in 0 until slices) {
            val base = (i * 2).toShort()
            indices.addAll(listOf(base, (base + 2).toShort(), (base + 1).toShort(), (base + 1).toShort(), (base + 2).toShort(), (base + 3).toShort()))
        }
        
        val baseIndex = vertices.size / 8
        vertices.addAll(listOf(0f, 0f, -height/2f, 0f, 0f, -1f, 0.5f, 0.5f))
        for (i in 0..slices) {
            val angle = (i.toFloat() / slices) * PI.toFloat() * 2f
            val x = cos(angle) * radius
            val y = sin(angle) * radius
            vertices.addAll(listOf(x, y, -height/2f, 0f, 0f, -1f, (cos(angle) + 1f) * 0.5f, (sin(angle) + 1f) * 0.5f))
        }
        for (i in 0 until slices) {
            indices.addAll(listOf(baseIndex.toShort(), (baseIndex + i + 1).toShort(), (baseIndex + i + 2).toShort()))
        }
        
        val topBaseIndex = vertices.size / 8
        vertices.addAll(listOf(0f, 0f, height/2f, 0f, 0f, 1f, 0.5f, 0.5f))
        for (i in 0..slices) {
            val angle = (i.toFloat() / slices) * PI.toFloat() * 2f
            val x = cos(angle) * radius
            val y = sin(angle) * radius
            vertices.addAll(listOf(x, y, height/2f, 0f, 0f, 1f, (cos(angle) + 1f) * 0.5f, (sin(angle) + 1f) * 0.5f))
        }
        for (i in 0 until slices) {
            indices.addAll(listOf(topBaseIndex.toShort(), (topBaseIndex + i + 2).toShort(), (topBaseIndex + i + 1).toShort()))
        }
        
        return createBuffers(vertices.toFloatArray(), indices.toShortArray())
    }
    
    private fun generateTorus(
        scale: LLVector3,
        volumeParams: PrimVolumeParams
    ): Pair<VertexBuffer, IndexBuffer> {
        val majorRadius = max(scale.x, scale.y)
        val minorRadius = scale.z
        val sides = TORUS_SIDES
        val rings = TORUS_RINGS
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        for (i in 0..rings) {
            val theta = (i.toFloat() / rings) * PI.toFloat() * 2f
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)
            for (j in 0..sides) {
                val phi = (j.toFloat() / sides) * PI.toFloat() * 2f
                val cosPhi = cos(phi)
                val sinPhi = sin(phi)
                val x = (majorRadius + minorRadius * cosPhi) * cosTheta
                val y = (majorRadius + minorRadius * cosPhi) * sinTheta
                val z = minorRadius * sinPhi
                val nx = cosPhi * cosTheta
                val ny = cosPhi * sinTheta
                val nz = sinPhi
                val u = i.toFloat() / rings
                val v = j.toFloat() / sides
                vertices.addAll(listOf(x, y, z, nx, ny, nz, u, v))
            }
        }
        
        for (i in 0 until rings) {
            for (j in 0 until sides) {
                val a = (i * (sides + 1) + j).toShort()
                val b = (a + sides + 1).toShort()
                val c = (a + 1).toShort()
                val d = (b + 1).toShort()
                indices.addAll(listOf(a, b, c, c, b, d))
            }
        }
        return createBuffers(vertices.toFloatArray(), indices.toShortArray())
    }
    
    private fun generatePrism(
        scale: LLVector3,
        volumeParams: PrimVolumeParams
    ): Pair<VertexBuffer, IndexBuffer> {
        val sx = scale.x
        val sy = scale.y
        val sz = scale.z
        val vertices = floatArrayOf(
            0f, sy, sz,  0f, 0f, 1f,  0.5f, 1f,
            -sx, -sy, sz,  0f, 0f, 1f,  0f, 0f,
            sx, -sy, sz,  0f, 0f, 1f,  1f, 0f,
            0f, sy, -sz,  0f, 0f, -1f,  0.5f, 1f,
            sx, -sy, -sz,  0f, 0f, -1f,  1f, 0f,
            -sx, -sy, -sz,  0f, 0f, -1f,  0f, 0f,
            -sx, -sy, -sz,  0f, -1f, 0f,  0f, 0f,
            sx, -sy, -sz,  0f, -1f, 0f,  1f, 0f,
            sx, -sy, sz,  0f, -1f, 0f,  1f, 1f,
            -sx, -sy, sz,  0f, -1f, 0f,  0f, 1f,
            -sx, -sy, -sz,  -0.7f, 0.7f, 0f,  0f, 0f,
            -sx, -sy, sz,  -0.7f, 0.7f, 0f,  1f, 0f,
            0f, sy, sz,  -0.7f, 0.7f, 0f,  1f, 1f,
            0f, sy, -sz,  -0.7f, 0.7f, 0f,  0f, 1f,
            sx, -sy, -sz,  0.7f, 0.7f, 0f,  1f, 0f,
            0f, sy, -sz,  0.7f, 0.7f, 0f,  1f, 1f,
            0f, sy, sz,  0.7f, 0.7f, 0f,  0f, 1f,
            sx, -sy, sz,  0.7f, 0.7f, 0f,  0f, 0f
        )
        val indices = shortArrayOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 6, 8, 9, 10, 11, 12, 10, 12, 13, 14, 15, 16, 14, 16, 17
        )
        return createBuffers(vertices, indices)
    }
    
    private fun createBuffers(
        vertices: FloatArray,
        indices: ShortArray
    ): Pair<VertexBuffer, IndexBuffer> {
        val vertexCount = vertices.size / 8
        val vertexSize = 32
        val vertexData = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
        vertices.forEach { vertexData.putFloat(it) }
        vertexData.rewind()
        
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, vertexSize)
            .attribute(VertexBuffer.VertexAttribute.TANGENTS, 0, VertexBuffer.AttributeType.FLOAT3, 12, vertexSize)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 24, vertexSize)
            .build(engine)
        vertexBuffer.setBufferAt(engine, 0, vertexData)
        
        val indexData = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
        indices.forEach { indexData.putShort(it) }
        indexData.rewind()
        
        val indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        indexBuffer.setBuffer(engine, indexData)
        
        return Pair(vertexBuffer, indexBuffer)
    }
}
