package com.linkpoint.objects

import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import java.util.UUID
import kotlin.math.*

/**
 * Build tools for creating and manipulating prims
 */
class BuildTools(
    private val objectManager: ObjectManager
) {
    companion object {
        // Prim types
        const val PRIM_BOX = 0
        const val PRIM_CYLINDER = 1
        const val PRIM_PRISM = 2
        const val PRIM_SPHERE = 3
        const val PRIM_TORUS = 4
        const val PRIM_TUBE = 5
        const val PRIM_RING = 6
        const val PRIM_SCULPT = 7
        const val PRIM_MESH = 8
        
        // Hole types
        const val HOLE_SAME = 0
        const val HOLE_CIRCLE = 16
        const val HOLE_SQUARE = 32
        const val HOLE_TRIANGLE = 48
        
        // Face constants
        const val ALL_SIDES = -1
    }
    
    // Current tool state
    private var currentPrimType = PRIM_BOX
    private var gridSnap = 0.5f
    private var rotationSnap = 15f // degrees
    
    // Build state
    private var isBuilding = false
    private var buildPosition = LLVector3.zero()
    private var buildRotation = LLQuaternion.identity()
    private var buildScale = LLVector3(0.5f, 0.5f, 0.5f)
    
    /**
     * Set prim type for new builds
     */
    fun setPrimType(type: Int) {
        currentPrimType = type
    }
    
    /**
     * Set grid snap size (0 to disable)
     */
    fun setGridSnap(size: Float) {
        gridSnap = size
    }
    
    /**
     * Set rotation snap angle (0 to disable)
     */
    fun setRotationSnap(degrees: Float) {
        rotationSnap = degrees
    }
    
    /**
     * Start building at position
     */
    fun startBuild(position: LLVector3) {
        isBuilding = true
        buildPosition = snapPosition(position)
        buildRotation = LLQuaternion.identity()
        buildScale = LLVector3(0.5f, 0.5f, 0.5f)
    }
    
    /**
     * Update build position during drag
     */
    fun updateBuild(position: LLVector3) {
        if (!isBuilding) return
        
        val newPos = snapPosition(position)
        val delta = newPos - buildPosition
        
        // Scale from drag
        buildScale = LLVector3(
            max(0.01f, abs(delta.x) * 2),
            max(0.01f, abs(delta.y) * 2),
            max(0.01f, abs(delta.z) * 2)
        )
    }
    
    /**
     * Finish building
     */
    fun finishBuild(): PrimCreateParams? {
        if (!isBuilding) return null
        
        isBuilding = false
        
        val params = PrimCreateParams(
            primType = currentPrimType,
            position = buildPosition,
            rotation = buildRotation,
            scale = buildScale,
            pathParams = PathParams(),
            profileParams = ProfileParams()
        )
        
        // Send create request
        // objectManager.createPrim(params)
        
        return params
    }
    
    /**
     * Cancel building
     */
    fun cancelBuild() {
        isBuilding = false
    }
    
    /**
     * Snap position to grid
     */
    fun snapPosition(position: LLVector3): LLVector3 {
        if (gridSnap <= 0) return position
        
        return LLVector3(
            round(position.x / gridSnap) * gridSnap,
            round(position.y / gridSnap) * gridSnap,
            round(position.z / gridSnap) * gridSnap
        )
    }
    
    /**
     * Snap rotation to angle
     */
    fun snapRotation(rotation: LLQuaternion): LLQuaternion {
        if (rotationSnap <= 0) return rotation
        
        // Convert to euler, snap, convert back
        val euler = rotation.toEuler()
        val snapRad = (rotationSnap * PI / 180.0).toFloat()
        
        return LLQuaternion.fromEuler(
            (round(euler.x / snapRad) * snapRad),
            (round(euler.y / snapRad) * snapRad),
            (round(euler.z / snapRad) * snapRad)
        )
    }
    
    /**
     * Duplicate selected objects
     */
    fun duplicateSelection(offset: LLVector3 = LLVector3(0.5f, 0.5f, 0f)) {
        val selected = objectManager.selectedObjects.value
        
        for (localId in selected) {
            val obj = objectManager.getObject(localId) ?: continue
            
            // Would send ObjectDuplicate message
        }
    }
    
    /**
     * Align selected objects
     */
    fun alignSelection(axis: Axis, alignTo: AlignType) {
        val selected = objectManager.selectedObjects.value
        if (selected.size < 2) return
        
        val objects = selected.mapNotNull { objectManager.getObject(it) }
        if (objects.isEmpty()) return
        
        val targetValue = when (alignTo) {
            AlignType.MIN -> objects.minOf { getAxisValue(it.position, axis) }
            AlignType.MAX -> objects.maxOf { getAxisValue(it.position, axis) }
            AlignType.CENTER -> {
                val min = objects.minOf { getAxisValue(it.position, axis) }
                val max = objects.maxOf { getAxisValue(it.position, axis) }
                (min + max) / 2
            }
        }
        
        for (obj in objects) {
            val newPos = setAxisValue(obj.position, axis, targetValue)
            objectManager.moveSelectedObjects(newPos - obj.position)
        }
    }
    
    /**
     * Distribute selected objects evenly
     */
    fun distributeSelection(axis: Axis) {
        val selected = objectManager.selectedObjects.value
        if (selected.size < 3) return
        
        val objects = selected.mapNotNull { objectManager.getObject(it) }
            .sortedBy { getAxisValue(it.position, axis) }
        
        if (objects.size < 3) return
        
        val minVal = getAxisValue(objects.first().position, axis)
        val maxVal = getAxisValue(objects.last().position, axis)
        val step = (maxVal - minVal) / (objects.size - 1)
        
        objects.forEachIndexed { index, obj ->
            val targetValue = minVal + step * index
            val newPos = setAxisValue(obj.position, axis, targetValue)
            // Would update object position
        }
    }
    
    /**
     * Create default path parameters
     */
    fun createPathParams(
        pathType: Int = 0,
        beginScale: Float = 1f,
        endScale: Float = 1f,
        hollow: Float = 0f,
        twist: Float = 0f,
        twistBegin: Float = 0f,
        radiusOffset: Float = 0f,
        taperX: Float = 0f,
        taperY: Float = 0f,
        revolutions: Float = 1f,
        skew: Float = 0f
    ): PathParams {
        return PathParams(
            pathType = pathType,
            beginScale = beginScale,
            endScale = endScale,
            hollow = hollow,
            twist = twist,
            twistBegin = twistBegin,
            radiusOffset = radiusOffset,
            taperX = taperX,
            taperY = taperY,
            revolutions = revolutions,
            skew = skew
        )
    }
    
    /**
     * Create default profile parameters
     */
    fun createProfileParams(
        profileType: Int = 0,
        begin: Float = 0f,
        end: Float = 1f,
        hollow: Float = 0f,
        hollowShape: Int = HOLE_SAME
    ): ProfileParams {
        return ProfileParams(
            profileType = profileType,
            begin = begin,
            end = end,
            hollow = hollow,
            hollowShape = hollowShape
        )
    }
    
    private fun getAxisValue(v: LLVector3, axis: Axis): Float {
        return when (axis) {
            Axis.X -> v.x
            Axis.Y -> v.y
            Axis.Z -> v.z
        }
    }
    
    private fun setAxisValue(v: LLVector3, axis: Axis, value: Float): LLVector3 {
        return when (axis) {
            Axis.X -> LLVector3(value, v.y, v.z)
            Axis.Y -> LLVector3(v.x, value, v.z)
            Axis.Z -> LLVector3(v.x, v.y, value)
        }
    }
}

data class PrimCreateParams(
    val primType: Int,
    val position: LLVector3,
    val rotation: LLQuaternion,
    val scale: LLVector3,
    val pathParams: PathParams,
    val profileParams: ProfileParams
)

data class PathParams(
    val pathType: Int = 0,
    val beginScale: Float = 1f,
    val endScale: Float = 1f,
    val hollow: Float = 0f,
    val twist: Float = 0f,
    val twistBegin: Float = 0f,
    val radiusOffset: Float = 0f,
    val taperX: Float = 0f,
    val taperY: Float = 0f,
    val revolutions: Float = 1f,
    val skew: Float = 0f
)

data class ProfileParams(
    val profileType: Int = 0,
    val begin: Float = 0f,
    val end: Float = 1f,
    val hollow: Float = 0f,
    val hollowShape: Int = 0
)

enum class Axis { X, Y, Z }
enum class AlignType { MIN, MAX, CENTER }
