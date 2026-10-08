package com.linkpoint.render.prims

import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshLOD
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.render.geometry.PrimShape
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class PrimRendererLODTest {

    @Test
    fun testPrimInstanceLODTracking() {
        val meshId = UUID.randomUUID()
        val instance = PrimInstance(
            localId = 101,
            fullId = UUID.randomUUID(),
            entity = 1,
            transformInstance = 1,
            shape = PrimShape.BOX,
            position = LLVector3(10f, 20f, 5f),
            rotation = LLQuaternion.identity(),
            scale = LLVector3(2f, 2f, 2f),
            textureEntry = ByteArray(0),
            materialInstance = org.mockito.kotlin.mock(),
            meshId = meshId,
            activeLod = MeshLOD.HIGH,
            pendingLod = null,
            lastEvaluatedDistance = 15f
        )

        assertEquals(meshId, instance.meshId)
        assertEquals(MeshLOD.HIGH, instance.activeLod)
        assertEquals(15f, instance.lastEvaluatedDistance, 0.001f)
        assertNull(instance.pendingLod)

        // Simulate LOD transition
        instance.pendingLod = MeshLOD.MEDIUM
        assertEquals(MeshLOD.MEDIUM, instance.pendingLod)

        instance.activeLod = MeshLOD.MEDIUM
        instance.pendingLod = null
        assertEquals(MeshLOD.MEDIUM, instance.activeLod)
        assertNull(instance.pendingLod)
    }

    @Test
    fun testMeshDataLODProperty() {
        val meshId = UUID.randomUUID()
        val dataDefault = MeshData(meshId = meshId, faces = emptyList())
        assertEquals(MeshLOD.HIGH, dataDefault.lod)

        val dataMedium = MeshData(meshId = meshId, faces = emptyList(), lod = MeshLOD.MEDIUM)
        assertEquals(MeshLOD.MEDIUM, dataMedium.lod)
    }
}
