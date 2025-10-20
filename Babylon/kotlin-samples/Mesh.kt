package com.babylonkt.core

import com.babylonkt.math.Vector3
import com.babylonkt.math.Matrix4x4
import com.babylonkt.math.Quaternion

/**
 * Represents a 3D mesh in the scene.
 */
class Mesh(
    var name: String,
    var scene: Scene? = null
) : TransformNode(name) {
    
    // ==================== Mesh Properties ====================
    
    var material: Material? = null
    var geometry: Geometry? = null
    
    var isVisible = true
    var isPickable = true
    var renderingGroupId = 0
    var receiveShadows = false
    var castShadows = true
    
    val tags = mutableSetOf<String>()
    
    // Instancing
    private val instances = mutableListOf<InstancedMesh>()
    
    // ==================== Geometry Data ====================
    
    private var vertexData: VertexData? = null
    
    /**
     * Sets vertex data for this mesh.
     */
    fun setVerticesData(kind: VertexBufferKind, data: FloatArray, updatable: Boolean = false) {
        if (vertexData == null) {
            vertexData = VertexData()
        }
        
        when (kind) {
            VertexBufferKind.POSITION -> vertexData?.positions = data
            VertexBufferKind.NORMAL -> vertexData?.normals = data
            VertexBufferKind.UV -> vertexData?.uvs = data
            VertexBufferKind.COLOR -> vertexData?.colors = data
            VertexBufferKind.TANGENT -> vertexData?.tangents = data
        }
        
        // Upload to GPU
        geometry?.updateVertexBuffer(kind, data)
    }
    
    /**
     * Sets index data for this mesh.
     */
    fun setIndices(indices: IntArray, updatable: Boolean = false) {
        vertexData?.indices = indices
        geometry?.updateIndexBuffer(indices)
    }
    
    /**
     * Gets vertex data from this mesh.
     */
    fun getVerticesData(kind: VertexBufferKind): FloatArray? {
        return when (kind) {
            VertexBufferKind.POSITION -> vertexData?.positions
            VertexBufferKind.NORMAL -> vertexData?.normals
            VertexBufferKind.UV -> vertexData?.uvs
            VertexBufferKind.COLOR -> vertexData?.colors
            VertexBufferKind.TANGENT -> vertexData?.tangents
        }
    }
    
    /**
     * Gets index data from this mesh.
     */
    fun getIndices(): IntArray? = vertexData?.indices
    
    // ==================== Bounding ====================
    
    private var boundingBox: BoundingBox? = null
    private var boundingSphere: BoundingSphere? = null
    
    /**
     * Computes the bounding box for this mesh.
     */
    fun computeBoundingBox() {
        val positions = vertexData?.positions ?: return
        
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        var maxZ = Float.MIN_VALUE
        
        for (i in positions.indices step 3) {
            val x = positions[i]
            val y = positions[i + 1]
            val z = positions[i + 2]
            
            minX = minOf(minX, x)
            minY = minOf(minY, y)
            minZ = minOf(minZ, z)
            maxX = maxOf(maxX, x)
            maxY = maxOf(maxY, y)
            maxZ = maxOf(maxZ, z)
        }
        
        boundingBox = BoundingBox(
            Vector3(minX, minY, minZ),
            Vector3(maxX, maxY, maxZ)
        )
    }
    
    /**
     * Computes the bounding sphere for this mesh.
     */
    fun computeBoundingSphere() {
        val positions = vertexData?.positions ?: return
        
        // Calculate center
        var centerX = 0f
        var centerY = 0f
        var centerZ = 0f
        val vertexCount = positions.size / 3
        
        for (i in positions.indices step 3) {
            centerX += positions[i]
            centerY += positions[i + 1]
            centerZ += positions[i + 2]
        }
        
        centerX /= vertexCount
        centerY /= vertexCount
        centerZ /= vertexCount
        
        val center = Vector3(centerX, centerY, centerZ)
        
        // Calculate radius
        var maxRadiusSquared = 0f
        for (i in positions.indices step 3) {
            val dx = positions[i] - centerX
            val dy = positions[i + 1] - centerY
            val dz = positions[i + 2] - centerZ
            val distSquared = dx * dx + dy * dy + dz * dz
            maxRadiusSquared = maxOf(maxRadiusSquared, distSquared)
        }
        
        boundingSphere = BoundingSphere(center, kotlin.math.sqrt(maxRadiusSquared))
    }
    
    fun getBoundingBox(): BoundingBox? = boundingBox
    fun getBoundingSphere(): BoundingSphere? = boundingSphere
    
    // ==================== Instancing ====================
    
    /**
     * Creates an instance of this mesh.
     */
    fun createInstance(name: String): InstancedMesh {
        val instance = InstancedMesh(name, this)
        instances.add(instance)
        scene?.addMesh(instance)
        return instance
    }
    
    /**
     * Gets all instances of this mesh.
     */
    fun getInstances(): List<InstancedMesh> = instances
    
    // ==================== Collision Detection ====================
    
    /**
     * Tests if a ray intersects this mesh.
     */
    fun intersects(ray: Ray): PickingInfo? {
        // Transform ray to local space
        val worldMatrix = getWorldMatrix()
        val inverseWorld = worldMatrix.inverse()
        val localOrigin = inverseWorld.transformPoint(ray.origin)
        val localDirection = inverseWorld.transformDirection(ray.direction).normalize()
        val localRay = Ray(localOrigin, localDirection)
        
        // Test against bounding sphere first (fast rejection)
        val sphere = boundingSphere
        if (sphere != null && !sphere.intersectsRay(localRay)) {
            return null
        }
        
        // Test against triangles
        val positions = vertexData?.positions ?: return null
        val indices = vertexData?.indices
        
        var closestDistance = Float.MAX_VALUE
        var closestPoint: Vector3? = null
        
        if (indices != null) {
            // Indexed geometry
            for (i in indices.indices step 3) {
                val i0 = indices[i] * 3
                val i1 = indices[i + 1] * 3
                val i2 = indices[i + 2] * 3
                
                val v0 = Vector3(positions[i0], positions[i0 + 1], positions[i0 + 2])
                val v1 = Vector3(positions[i1], positions[i1 + 1], positions[i1 + 2])
                val v2 = Vector3(positions[i2], positions[i2 + 1], positions[i2 + 2])
                
                val intersection = rayTriangleIntersection(localRay, v0, v1, v2)
                if (intersection != null && intersection.first < closestDistance) {
                    closestDistance = intersection.first
                    closestPoint = intersection.second
                }
            }
        }
        
        if (closestPoint != null) {
            // Transform back to world space
            val worldPoint = worldMatrix.transformPoint(closestPoint)
            val worldDistance = ray.origin.distanceTo(worldPoint)
            
            return PickingInfo(
                hit = true,
                distance = worldDistance,
                pickedMesh = this,
                pickedPoint = worldPoint,
                ray = ray
            )
        }
        
        return null
    }
    
    private fun rayTriangleIntersection(
        ray: Ray,
        v0: Vector3,
        v1: Vector3,
        v2: Vector3
    ): Pair<Float, Vector3>? {
        // Möller–Trumbore intersection algorithm
        val edge1 = v1 - v0
        val edge2 = v2 - v0
        val h = ray.direction.cross(edge2)
        val a = edge1.dot(h)
        
        if (a > -0.00001f && a < 0.00001f) {
            return null // Ray is parallel to triangle
        }
        
        val f = 1f / a
        val s = ray.origin - v0
        val u = f * s.dot(h)
        
        if (u < 0f || u > 1f) {
            return null
        }
        
        val q = s.cross(edge1)
        val v = f * ray.direction.dot(q)
        
        if (v < 0f || u + v > 1f) {
            return null
        }
        
        val t = f * edge2.dot(q)
        
        if (t > 0.00001f) {
            val intersectionPoint = ray.origin + ray.direction * t
            return Pair(t, intersectionPoint)
        }
        
        return null
    }
    
    // ==================== Rendering ====================
    
    /**
     * Draws this mesh.
     */
    fun draw() {
        geometry?.draw()
    }
    
    // ==================== Disposal ====================
    
    fun dispose() {
        geometry?.dispose()
        instances.forEach { it.dispose() }
        instances.clear()
    }
    
    override fun toString(): String {
        return "Mesh(name='$name', vertices=${vertexData?.positions?.size?.div(3) ?: 0})"
    }
}

// ==================== Supporting Classes ====================

/**
 * Represents vertex data for a mesh.
 */
data class VertexData(
    var positions: FloatArray? = null,
    var normals: FloatArray? = null,
    var uvs: FloatArray? = null,
    var colors: FloatArray? = null,
    var tangents: FloatArray? = null,
    var indices: IntArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VertexData) return false
        
        if (positions != null) {
            if (other.positions == null) return false
            if (!positions.contentEquals(other.positions)) return false
        } else if (other.positions != null) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        return positions?.contentHashCode() ?: 0
    }
}

/**
 * Types of vertex buffers.
 */
enum class VertexBufferKind {
    POSITION,
    NORMAL,
    UV,
    COLOR,
    TANGENT
}

/**
 * Represents an axis-aligned bounding box.
 */
data class BoundingBox(
    val min: Vector3,
    val max: Vector3
) {
    val center: Vector3 get() = (min + max) * 0.5f
    val size: Vector3 get() = max - min
    
    fun intersectsRay(ray: Ray): Boolean {
        var tmin = (min.x - ray.origin.x) / ray.direction.x
        var tmax = (max.x - ray.origin.x) / ray.direction.x
        
        if (tmin > tmax) {
            val temp = tmin
            tmin = tmax
            tmax = temp
        }
        
        var tymin = (min.y - ray.origin.y) / ray.direction.y
        var tymax = (max.y - ray.origin.y) / ray.direction.y
        
        if (tymin > tymax) {
            val temp = tymin
            tymin = tymax
            tymax = temp
        }
        
        if (tmin > tymax || tymin > tmax) return false
        
        if (tymin > tmin) tmin = tymin
        if (tymax < tmax) tmax = tymax
        
        var tzmin = (min.z - ray.origin.z) / ray.direction.z
        var tzmax = (max.z - ray.origin.z) / ray.direction.z
        
        if (tzmin > tzmax) {
            val temp = tzmin
            tzmin = tzmax
            tzmax = temp
        }
        
        if (tmin > tzmax || tzmin > tmax) return false
        
        return true
    }
}

/**
 * Represents a bounding sphere.
 */
data class BoundingSphere(
    val center: Vector3,
    val radius: Float
) {
    fun intersectsRay(ray: Ray): Boolean {
        val m = ray.origin - center
        val b = m.dot(ray.direction)
        val c = m.dot(m) - radius * radius
        
        if (c > 0f && b > 0f) return false
        
        val discriminant = b * b - c
        return discriminant >= 0f
    }
}

/**
 * Represents an instanced mesh (shares geometry with source mesh).
 */
class InstancedMesh(
    name: String,
    val sourceMesh: Mesh
) : Mesh(name, sourceMesh.scene) {
    
    init {
        // Share geometry and material with source
        this.geometry = sourceMesh.geometry
        this.material = sourceMesh.material
    }
    
    override fun draw() {
        // Instanced meshes are drawn together with their source
        // This is handled by the rendering system
    }
}

/**
 * Base class for transformable nodes.
 */
open class TransformNode(val name: String) {
    var position = Vector3.Zero()
    var rotation = Vector3.Zero()
    var rotationQuaternion: Quaternion? = null
    var scaling = Vector3.One()
    
    private var worldMatrix: Matrix4x4? = null
    private var isDirty = true
    
    /**
     * Gets the world transformation matrix.
     */
    fun getWorldMatrix(): Matrix4x4 {
        if (isDirty || worldMatrix == null) {
            computeWorldMatrix()
        }
        return worldMatrix!!
    }
    
    private fun computeWorldMatrix() {
        val translation = position
        val rotation = rotationQuaternion ?: Quaternion.fromEulerAngles(rotation)
        val scale = scaling
        
        worldMatrix = Matrix4x4.compose(translation, rotation, scale)
        isDirty = false
    }
    
    /**
     * Marks the transformation as dirty (needs recomputation).
     */
    fun markAsDirty() {
        isDirty = true
    }
}

// ==================== Geometry Interface ====================

/**
 * Interface for geometry data on the GPU.
 */
interface Geometry {
    fun updateVertexBuffer(kind: VertexBufferKind, data: FloatArray)
    fun updateIndexBuffer(indices: IntArray)
    fun draw()
    fun dispose()
}