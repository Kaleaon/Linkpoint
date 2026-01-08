package com.lumiyaviewer.lumiya.graphics.filament

import android.util.Log
import com.google.android.filament.Box
import com.google.android.filament.Engine
import com.google.android.filament.Scene
import com.lumiyaviewer.lumiya.render.spatial.FrustrumPlanes
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import kotlin.math.sqrt

class FilamentPerformanceOptimizer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "FilamentPerfOptimizer"
        private const val LOD_HIGH_DISTANCE = 32f
        private const val LOD_MEDIUM_DISTANCE = 64f
        private const val LOD_LOW_DISTANCE = 128f
        private const val CULL_DISTANCE = 256f
    }
    
    var visibleEntities = 0
        private set
    var culledEntities = 0
        private set
    var drawCalls = 0
        private set
    var triangles = 0
        private set
    
    private var cameraPosition = LLVector3(128f, 128f, 25f)
    
    enum class LODLevel {
        HIGH, MEDIUM, LOW, CULL
    }
    
    fun setCameraPosition(position: LLVector3) {
        cameraPosition = position
    }
    
    fun performFrustumCulling(frustum: FrustrumPlanes, entities: List<Int>): List<Int> {
        val visible = mutableListOf<Int>()
        var culled = 0
        val rm = engine.renderableManager
        
        entities.forEach { entity ->
            val instance = rm.getInstance(entity)
            if (instance != 0) {
                val box = Box()
                rm.getAxisAlignedBoundingBox(instance, box)
                if (isBoxInFrustum(box, frustum)) {
                    visible.add(entity)
                } else {
                    culled++
                    rm.setLayerMask(instance, 0x00, 0x00)
                }
            }
        }
        
        visibleEntities = visible.size
        culledEntities = culled
        Log.d(TAG, "Frustum culling: ${visible.size} visible, $culled culled")
        return visible
    }
    
    private fun isBoxInFrustum(box: Box, frustum: FrustrumPlanes): Boolean {
        val center = box.center
        val halfExtent = box.halfExtent
        
        val dx = center[0] - cameraPosition.x
        val dy = center[1] - cameraPosition.y
        val dz = center[2] - cameraPosition.z
        val distance = sqrt(dx * dx + dy * dy + dz * dz)
        
        if (distance > CULL_DISTANCE) {
            return false
        }
        
        try {
            val planes = listOf(
                frustum.nearPlane,
                frustum.farPlane,
                frustum.leftPlane,
                frustum.rightPlane,
                frustum.topPlane,
                frustum.bottomPlane
            )
            
            for (plane in planes) {
                val px = if (plane.a > 0) halfExtent[0] else -halfExtent[0]
                val py = if (plane.b > 0) halfExtent[1] else -halfExtent[1]
                val pz = if (plane.c > 0) halfExtent[2] else -halfExtent[2]
                
                val dist = plane.a * (center[0] + px) +
                          plane.b * (center[1] + py) +
                          plane.c * (center[2] + pz) +
                          plane.d
                
                if (dist < 0) return false
            }
        } catch (e: Exception) {
            Log.w(TAG, "Frustum test failed, using distance culling only", e)
            return true
        }
        return true
    }
    
    fun calculateLOD(position: LLVector3): LODLevel {
        val dx = position.x - cameraPosition.x
        val dy = position.y - cameraPosition.y
        val dz = position.z - cameraPosition.z
        val distance = sqrt(dx * dx + dy * dy + dz * dz)
        
        return when {
            distance < LOD_HIGH_DISTANCE -> LODLevel.HIGH
            distance < LOD_MEDIUM_DISTANCE -> LODLevel.MEDIUM
            distance < LOD_LOW_DISTANCE -> LODLevel.LOW
            else -> LODLevel.CULL
        }
    }
    
    fun optimizeFrame() {
        updateStatistics()
    }
    
    private fun updateStatistics() {
        try {
            drawCalls = visibleEntities
            Log.v(TAG, "Performance stats: $drawCalls draws")
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing stats", e)
        }
    }
}
