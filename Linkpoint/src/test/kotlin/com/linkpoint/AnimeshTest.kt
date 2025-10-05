package com.linkpoint

import com.linkpoint.animesh.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Unit tests for Animesh system
 */
class AnimeshTest {
    
    private lateinit var animeshManager: AnimeshManager
    
    @Before
    fun setup() {
        animeshManager = AnimeshManager()
    }
    
    @After
    fun teardown() {
        animeshManager.cleanup()
    }
    
    @Test
    fun `test animesh registration`() {
        val objectId = UUID.randomUUID()
        val attachmentId = UUID.randomUUID()
        val skeletonData = createMockSkeletonData()
        
        animeshManager.registerAnimesh(objectId, attachmentId, skeletonData)
        
        val animesh = animeshManager.getAnimesh(objectId)
        assertNotNull("Animesh should be registered", animesh)
        assertEquals("Object ID should match", objectId, animesh?.objectId)
    }
    
    @Test
    fun `test animation addition`() {
        val objectId = UUID.randomUUID()
        val attachmentId = UUID.randomUUID()
        val skeletonData = createMockSkeletonData()
        
        animeshManager.registerAnimesh(objectId, attachmentId, skeletonData)
        
        val animation = AnimeshAnimation(
            animationId = UUID.randomUUID(),
            name = "test_anim",
            duration = 2.0f,
            loop = true
        )
        
        animeshManager.cacheAnimation(animation.animationId, animation)
        animeshManager.addAnimation(objectId, animation.animationId)
        
        val animesh = animeshManager.getAnimesh(objectId)
        assertTrue("Animation should be added", animesh!!.activeAnimations.isNotEmpty())
    }
    
    @Test
    fun `test statistics`() {
        val objectId = UUID.randomUUID()
        val attachmentId = UUID.randomUUID()
        val skeletonData = createMockSkeletonData()
        
        animeshManager.registerAnimesh(objectId, attachmentId, skeletonData)
        
        val stats = animeshManager.getStats()
        assertEquals("Should have 1 animesh", 1, stats.activeAnimeshCount)
    }
    
    private fun createMockSkeletonData(): ByteBuffer {
        val buffer = ByteBuffer.allocate(1024)
        
        // Write skeleton data
        buffer.putInt(2) // 2 bones
        
        // Bone 0
        buffer.putInt(0) // bone ID
        buffer.putInt(-1) // parent ID (root)
        buffer.putInt(4) // name length
        buffer.put("root".toByteArray())
        buffer.putFloat(0f) // pos x
        buffer.putFloat(0f) // pos y
        buffer.putFloat(0f) // pos z
        buffer.putFloat(0f) // rot x
        buffer.putFloat(0f) // rot y
        buffer.putFloat(0f) // rot z
        buffer.putFloat(1f) // rot w
        
        // Bone 1
        buffer.putInt(1) // bone ID
        buffer.putInt(0) // parent ID
        buffer.putInt(5) // name length
        buffer.put("child".toByteArray())
        buffer.putFloat(0f) // pos x
        buffer.putFloat(1f) // pos y
        buffer.putFloat(0f) // pos z
        buffer.putFloat(0f) // rot x
        buffer.putFloat(0f) // rot y
        buffer.putFloat(0f) // rot z
        buffer.putFloat(1f) // rot w
        
        buffer.flip()
        return buffer
    }
}

/**
 * Unit tests for BoM system
 */
class BakesOnMeshTest {
    
    private lateinit var bomManager: com.linkpoint.bom.BakesOnMeshManager
    
    @Before
    fun setup() {
        bomManager = com.linkpoint.bom.BakesOnMeshManager()
    }
    
    @After
    fun teardown() {
        bomManager.cleanup()
    }
    
    @Test
    fun `test avatar registration`() {
        val avatarId = UUID.randomUUID()
        bomManager.registerAvatar(avatarId)
        
        val textures = bomManager.getAllBakedTextures(avatarId)
        assertNotNull("Avatar should be registered", textures)
    }
    
    @Test
    fun `test layer addition`() = runBlocking {
        val avatarId = UUID.randomUUID()
        bomManager.registerAvatar(avatarId)
        
        val layer = com.linkpoint.bom.BomTextureLayer(
            layerId = UUID.randomUUID(),
            bakeType = com.linkpoint.bom.BomBakeType.HEAD,
            textureId = UUID.randomUUID()
        )
        
        bomManager.addTextureLayer(avatarId, layer)
        
        // Give time for async bake
        kotlinx.coroutines.delay(1000)
        
        val bakedTexture = bomManager.getBakedTexture(avatarId, com.linkpoint.bom.BomBakeType.HEAD)
        assertNotNull("Baked texture should exist", bakedTexture)
    }
}

/**
 * Unit tests for EEP system
 */
class EEPTest {
    
    private lateinit var eepManager: com.linkpoint.eep.EEPManager
    
    @Before
    fun setup() {
        eepManager = com.linkpoint.eep.EEPManager()
    }
    
    @After
    fun teardown() {
        eepManager.cleanup()
    }
    
    @Test
    fun `test environment creation`() {
        val env = eepManager.createDefaultEnvironment()
        assertNotNull("Environment should be created", env)
        assertEquals("Should have default name", "Default", env.name)
    }
    
    @Test
    fun `test environment setting`() {
        val regionId = UUID.randomUUID()
        val env = eepManager.createDefaultEnvironment()
        
        eepManager.setRegionEnvironment(regionId, env)
        
        val current = eepManager.getCurrentEnvironment()
        assertNotNull("Current environment should be set", current)
    }
}