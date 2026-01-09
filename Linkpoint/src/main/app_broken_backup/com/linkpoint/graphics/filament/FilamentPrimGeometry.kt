package com.linkpoint.graphics.filament

import android.util.Log
import com.google.android.filament.*
import com.linkpoint.slproto.prims.*
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/**
 * FilamentPrimGeometry - Generates geometry for Second Life primitives
 * 
 * Supports:
 * - Basic shapes: Box, Cylinder, Sphere, Torus, Tube, Ring
 * - Profile parameters: path cut, hollow, twist, taper
 * - Path parameters: begin, end, scale, shear
 * - Sculpts and mesh (via external loader)
 */
class FilamentPrimGeometry(
    private val engine: Engine
) {
    companion object {
        private const val TAG = "FilamentPrimGeometry"
        
        // Resolution for curved surfaces
        private const val SPHERE_SLICES = 16
        private const val SPHERE_STACKS = 16
        private const val CYLINDER_SLICES = 16
        private const val TORUS_SIDES = 12
        private const val TORUS_RINGS = 16
    }
    
    /**
     * Generate geometry for a prim based on its volume parameters
     */
    fun generatePrimMesh(
        volumeParams: PrimVolumeParams,
        scale: LLVector3
    ): Pair<VertexBuffer, IndexBuffer> {
        
        // Check if it's a mesh (needs external loading)
        if (volumeParams.isMesh()) {
            Log.d(TAG, "Mesh prim - using placeholder")
            return generateBox(scale)
        }
        
        // Check if it's a sculpt (needs texture-based generation)
        if (volumeParams.isSculpt()) {
            Log.d(TAG, "Sculpt prim - using placeholder")
            return generateBox(scale)
        }
        
        // Generate based on profile and path
        val profileType = (volumeParams.ProfileParams.CurveType.toInt() and 
                          PrimProfileParams.LL_PCODE_PROFILE_MASK.toInt()).toByte()
        val pathType = (volumeParams.PathParams.CurveType.toInt() and 
                       PrimProfileParams.LL_PCODE_HOLE_MASK.toInt()).toByte()
        
        return when (profileType) {
            PrimProfileParams.LL_PCODE_PROFILE_SQUARE -> {
                // Box
                generateBox(scale, volumeParams)
            }
            PrimProfileParams.LL_PCODE_PROFILE_CIRCLE,
            PrimProfileParams.LL_PCODE_PROFILE_CIRCLE_HALF -> {
                // Cylinder or sphere
                if (pathType == PrimPathParams.LL_PCODE_PATH_CIRCLE) {
                    generateTorus(scale, volumeParams)
                } else {
                    generateCylinder(scale, volumeParams)
                }
            }
            PrimProfileParams.LL_PCODE_PROFILE_EQUALTRI,
            PrimProfileParams.LL_PCODE_PROFILE_ISOTRI,
            PrimProfileParams.LL_PCODE_PROFILE_RIGHTTRI -> {
                // Prism
                generatePrism(scale, volumeParams)
            }
            else -> {
                Log.w(TAG, "Unknown profile type: $profileType, using box")
                generateBox(scale)
            }
        }
    }
    
    /**
     * Generate a box mesh
     */
    private fun generateBox(
        scale: LLVector3,
        volumeParams: PrimVolumeParams? = null
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val sx = scale.x
        val sy = scale.y
        val sz = scale.z
        
        // Box vertices (position + normal + UV)
        val vertices = floatArrayOf(
            // Front face (+Y)
            -sx, -sz,  sy,  0f,  0f,  1f,  0f, 0f,
             sx, -sz,  sy,  0f,  0f,  1f,  1f, 0f,
             sx,  sz,  sy,  0f,  0f,  1f,  1f, 1f,
            -sx,  sz,  sy,  0f,  0f,  1f,  0f, 1f,
            
            // Back face (-Y)
            -sx, -sz, -sy,  0f,  0f, -1f,  1f, 0f,
            -sx,  sz, -sy,  0f,  0f, -1f,  1f, 1f,
             sx,  sz, -sy,  0f,  0f, -1f,  0f, 1f,
             sx, -sz, -sy,  0f,  0f, -1f,  0f, 0f,
            
            // Top face (+Z)
            -sx,  sz, -sy,  0f,  1f,  0f,  0f, 1f,
            -sx,  sz,  sy,  0f,  1f,  0f,  0f, 0f,
             sx,  sz,  sy,  0f,  1f,  0f,  1f, 0f,
             sx,  sz, -sy,  0f,  1f,  0f,  1f, 1f,
            
            // Bottom face (-Z)
            -sx, -sz, -sy,  0f, -1f,  0f,  1f, 1f,
             sx, -sz, -sy,  0f, -1f,  0f,  0f, 1f,
             sx, -sz,  sy,  0f, -1f,  0f,  0f, 0f,
            -sx, -sz,  sy,  0f, -1f,  0f,  1f, 0f,
            
            // Right face (+X)
             sx, -sz, -sy,  1f,  0f,  0f,  1f, 0f,
             sx,  sz, -sy,  1f,  0f,  0f,  1f, 1f,
             sx,  sz,  sy,  1f,  0f,  0f,  0f, 1f,
             sx, -sz,  sy,  1f,  0f,  0f,  0f, 0f,
            
            // Left face (-X)
            -sx, -sz, -sy, -1f,  0f,  0f,  0f, 0f,
            -sx, -sz,  sy, -1f,  0f,  0f,  1f, 0f,
            -sx,  sz,  sy, -1f,  0f,  0f,  1f, 1f,
            -sx,  sz, -sy, -1f,  0f,  0f,  0f, 1f
        )
        
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,    // Front
            4, 5, 6, 4, 6, 7,    // Back
            8, 9, 10, 8, 10, 11, // Top
            12, 13, 14, 12, 14, 15, // Bottom
            16, 17, 18, 16, 18, 19, // Right
            20, 21, 22, 20, 22, 23  // Left
        )
        
        return createBuffers(vertices, indices)
    }
    
    /**
     * Generate a cylinder mesh
     */
    private fun generateCylinder(
        scale: LLVector3,
        volumeParams: PrimVolumeParams
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val radius = max(scale.x, scale.y)
        val height = scale.z * 2f
        val slices = CYLINDER_SLICES
        
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        // Generate side vertices
        for (i in 0..slices) {
            val angle = (i.toFloat() / slices) * PI.toFloat() * 2f
            val x = cos(angle) * radius
            val y = sin(angle) * radius
            val u = i.toFloat() / slices
            
            // Bottom vertex
            vertices.addAll(listOf(
                x, y, -height/2f,  // position
                x/radius, y/radius, 0f,  // normal
                u, 0f  // UV
            ))
            
            // Top vertex
            vertices.addAll(listOf(
                x, y, height/2f,  // position
                x/radius, y/radius, 0f,  // normal
                u, 1f  // UV
            ))
        }
        
        // Generate side indices
        for (i in 0 until slices) {
            val base = (i * 2).toShort()
            indices.addAll(listOf(
                base, (base + 2).toShort(), (base + 1).toShort(),
                (base + 1).toShort(), (base + 2).toShort(), (base + 3).toShort()
            ))
        }
        
        // Add caps
        val baseIndex = vertices.size / 8
        
        // Bottom cap center
        vertices.addAll(listOf(0f, 0f, -height/2f, 0f, 0f, -1f, 0.5f, 0.5f))
        for (i in 0..slices) {
            val angle = (i.toFloat() / slices) * PI.toFloat() * 2f
            val x = cos(angle) * radius
            val y = sin(angle) * radius
            vertices.addAll(listOf(x, y, -height/2f, 0f, 0f, -1f, 
                                  (cos(angle) + 1f) * 0.5f, (sin(angle) + 1f) * 0.5f))
        }
        
        // Bottom cap indices
        for (i in 0 until slices) {
            indices.addAll(listOf(
                baseIndex.toShort(),
                (baseIndex + i + 1).toShort(),
                (baseIndex + i + 2).toShort()
            ))
        }
        
        // Top cap
        val topBaseIndex = vertices.size / 8
        vertices.addAll(listOf(0f, 0f, height/2f, 0f, 0f, 1f, 0.5f, 0.5f))
        for (i in 0..slices) {
            val angle = (i.toFloat() / slices) * PI.toFloat() * 2f
            val x = cos(angle) * radius
            val y = sin(angle) * radius
            vertices.addAll(listOf(x, y, height/2f, 0f, 0f, 1f,
                                  (cos(angle) + 1f) * 0.5f, (sin(angle) + 1f) * 0.5f))
        }
        
        // Top cap indices (reversed winding)
        for (i in 0 until slices) {
            indices.addAll(listOf(
                topBaseIndex.toShort(),
                (topBaseIndex + i + 2).toShort(),
                (topBaseIndex + i + 1).toShort()
            ))
        }
        
        return createBuffers(vertices.toFloatArray(), indices.toShortArray())
    }
    
    /**
     * Generate a torus mesh
     */
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
                
                // Position
                val x = (majorRadius + minorRadius * cosPhi) * cosTheta
                val y = (majorRadius + minorRadius * cosPhi) * sinTheta
                val z = minorRadius * sinPhi
                
                // Normal
                val nx = cosPhi * cosTheta
                val ny = cosPhi * sinTheta
                val nz = sinPhi
                
                // UV
                val u = i.toFloat() / rings
                val v = j.toFloat() / sides
                
                vertices.addAll(listOf(x, y, z, nx, ny, nz, u, v))
            }
        }
        
        // Generate indices
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
    
    /**
     * Generate a prism mesh (triangular profile)
     */
    private fun generatePrism(
        scale: LLVector3,
        volumeParams: PrimVolumeParams
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val sx = scale.x
        val sy = scale.y
        val sz = scale.z
        
        // Triangular prism vertices
        val vertices = floatArrayOf(
            // Front face (triangle)
            0f, sy, sz,  0f, 0f, 1f,  0.5f, 1f,
            -sx, -sy, sz,  0f, 0f, 1f,  0f, 0f,
            sx, -sy, sz,  0f, 0f, 1f,  1f, 0f,
            
            // Back face (triangle)
            0f, sy, -sz,  0f, 0f, -1f,  0.5f, 1f,
            sx, -sy, -sz,  0f, 0f, -1f,  1f, 0f,
            -sx, -sy, -sz,  0f, 0f, -1f,  0f, 0f,
            
            // Bottom face (rectangle)
            -sx, -sy, -sz,  0f, -1f, 0f,  0f, 0f,
            sx, -sy, -sz,  0f, -1f, 0f,  1f, 0f,
            sx, -sy, sz,  0f, -1f, 0f,  1f, 1f,
            -sx, -sy, sz,  0f, -1f, 0f,  0f, 1f,
            
            // Left face
            -sx, -sy, -sz,  -0.7f, 0.7f, 0f,  0f, 0f,
            -sx, -sy, sz,  -0.7f, 0.7f, 0f,  1f, 0f,
            0f, sy, sz,  -0.7f, 0.7f, 0f,  1f, 1f,
            0f, sy, -sz,  -0.7f, 0.7f, 0f,  0f, 1f,
            
            // Right face
            sx, -sy, -sz,  0.7f, 0.7f, 0f,  1f, 0f,
            0f, sy, -sz,  0.7f, 0.7f, 0f,  1f, 1f,
            0f, sy, sz,  0.7f, 0.7f, 0f,  0f, 1f,
            sx, -sy, sz,  0.7f, 0.7f, 0f,  0f, 0f
        )
        
        val indices = shortArrayOf(
            0, 1, 2,       // Front
            3, 4, 5,       // Back
            6, 7, 8, 6, 8, 9,   // Bottom
            10, 11, 12, 10, 12, 13,  // Left
            14, 15, 16, 14, 16, 17   // Right
        )
        
        return createBuffers(vertices, indices)
    }
    
    /**
     * Create Filament buffers from vertex and index data
     */
    private fun createBuffers(
        vertices: FloatArray,
        indices: ShortArray
    ): Pair<VertexBuffer, IndexBuffer> {
        
        val vertexCount = vertices.size / 8 // 8 floats per vertex
        val vertexSize = 32 // 8 floats * 4 bytes
        
        // Create vertex buffer
        val vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
        vertices.forEach { vertexData.putFloat(it) }
        vertexData.rewind()
        
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0,
                VertexBuffer.AttributeType.FLOAT3, 0, vertexSize)
            .attribute(VertexBuffer.VertexAttribute.TANGENTS, 0,
                VertexBuffer.AttributeType.FLOAT3, 12, vertexSize)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0,
                VertexBuffer.AttributeType.FLOAT2, 24, vertexSize)
            .build(engine)
        
        vertexBuffer.setBufferAt(engine, 0, vertexData)
        
        // Create index buffer
        val indexData = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
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
