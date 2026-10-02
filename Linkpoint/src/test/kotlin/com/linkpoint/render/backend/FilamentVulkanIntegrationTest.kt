package com.linkpoint.render.backend

import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.PrimShapeParams
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.render.SceneGraph
import com.linkpoint.render.driver.GraphicsDriverProbe
import com.linkpoint.render.materials.FilamentMaterialTranslator
import com.linkpoint.render.materials.MaterialDescriptor
import com.linkpoint.render.scene.commands.ParallelCommandBufferRecorder
import com.linkpoint.render.scene.commands.PreparedRenderCommand
import com.linkpoint.render.scene.commands.SceneRenderCommand
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FilamentVulkanIntegrationTest {

    @Test
    fun `driver profile detects vulkan availability correctly`() {
        val vulkanProfile = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3,
            glesMinor = 2,
            vulkanLevel = 1,
            vulkanVersion = 0x401000,
            vulkanDeqp = 20210301,
            hardware = "adreno",
            manufacturer = "qualcomm"
        )
        assertTrue(vulkanProfile.hasVulkan)
        assertEquals(1, vulkanProfile.vulkanHardwareLevel)
        assertEquals(GraphicsDriverProbe.VendorFamily.ADRENO, vulkanProfile.vendorFamily)

        val noVulkanProfile = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3,
            glesMinor = 0,
            vulkanLevel = -1
        )
        assertTrue(!noVulkanProfile.hasVulkan)
    }

    @Test
    fun `parallel command buffer recorder prepares commands concurrently without race conditions`() = runBlocking {
        val recorder = ParallelCommandBufferRecorder()
        val dummyUpdate = ObjectUpdateData(
            localId = 101,
            fullId = UUID.randomUUID(),
            parentId = 0,
            position = LLVector3(128f, 128f, 25f),
            rotation = LLQuaternion(0f, 0f, 0f, 1f),
            velocity = LLVector3.zero(),
            scale = LLVector3(1f, 1f, 1f),
            pcode = 9,
            material = 1,
            clickAction = 0,
            updateFlags = 0,
            textureEntry = byteArrayOf(1, 2, 3, 4),
            hoverText = "",
            hoverTextColor = LLColor4(1f, 1f, 1f, 1f),
            mediaUrl = ""
        )

        val commands = (1..50).map { id ->
            SceneRenderCommand.UpsertPrim(
                dummyUpdate.copy(localId = id, fullId = UUID.randomUUID())
            )
        }

        val prepared = recorder.recordBatchInParallel(commands)

        assertEquals(50, prepared.size)
        val first = prepared.first() as PreparedRenderCommand.PreparedUpsertPrim
        assertEquals(1, first.update.localId)
        assertEquals(16, first.transformMatrix.size)
        // Check scale factors on diagonal of transform matrix
        assertEquals(1f, first.transformMatrix[0], 0.001f)
        assertEquals(1f, first.transformMatrix[5], 0.001f)
        assertEquals(1f, first.transformMatrix[10], 0.001f)
    }

    @Test
    fun `material translator computes descriptor keys and caches descriptor sets`() {
        val descriptor1 = MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(1f, 0.5f, 0.2f, 1f),
            metallicFactor = 0.1f,
            roughnessFactor = 0.8f
        )
        val descriptor2 = MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(1f, 0.5f, 0.2f, 1f),
            metallicFactor = 0.1f,
            roughnessFactor = 0.8f
        )
        val bindings = FilamentMaterialTranslator.TextureBindings()

        val key1 = FilamentMaterialTranslator.computeDescriptorKey(descriptor1, bindings)
        val key2 = FilamentMaterialTranslator.computeDescriptorKey(descriptor2, bindings)

        assertEquals(key1, key2)
    }

    @Test
    fun `scene graph updates spatial nodes and statistics correctly`() {
        val sceneGraph = SceneGraph()
        val objectId = UUID.randomUUID()

        val sceneObject = com.linkpoint.protocol.scenery.SceneObject(
            id = objectId,
            name = "Test Prim",
            description = "Filament Primitive",
            position = com.linkpoint.protocol.scenery.Vector3(10f, 20f, 30f),
            rotation = com.linkpoint.protocol.scenery.Quaternion(1f, 0f, 0f, 0f)
        )

        sceneGraph.updateObject(sceneObject)

        val stats = sceneGraph.statistics.value
        assertEquals(1, stats.objectCount)

        val objects = sceneGraph.objects.value
        assertNotNull(objects[objectId])
        assertEquals("Test Prim", objects[objectId]?.name)

        sceneGraph.removeObject(objectId)
        assertEquals(0, sceneGraph.statistics.value.objectCount)
    }
}
