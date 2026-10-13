package com.linkpoint.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.objects.ObjectManager
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SoundManagerTest {

    private lateinit var context: Context
    private lateinit var cacheManager: CacheManager
    private lateinit var assetCache: AssetCache
    private lateinit var objectManager: ObjectManager
    private lateinit var soundManager: SoundManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        cacheManager = CacheManager(context)
        assetCache = AssetCache(context, cacheManager)
        objectManager = ObjectManager(UDPConnectionFixed())
        soundManager = SoundManager(context, assetCache, objectManager)
    }

    @Test
    fun playAttachedSound_computesPositionFromObjectManager_whenObjectExists() = runBlocking {
        val soundId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val objectPos = LLVector3(128f, 150f, 30f)

        objectManager.handleObjectUpdate(
            ObjectUpdateData(
                localId = 1001,
                fullId = objectId,
                ownerId = ownerId,
                position = objectPos
            )
        )

        val dummyAudio = ByteArray(100) { 0x00 }
        assetCache.put(soundId, AssetType.SOUND, dummyAudio)

        soundManager.updateListener(LLVector3(128f, 128f, 30f), LLVector3(1f, 0f, 0f))

        val job = soundManager.playAttachedSound(soundId, objectId, ownerId, gain = 1.0f)
        job.join()

        val activeSounds = soundManager.getPlayingSounds()
        assertTrue(activeSounds.isNotEmpty())
        val playback = activeSounds.values.first { it.soundId == soundId }
        assertEquals(objectId, playback.objectId)
        assertEquals(objectPos.x, playback.position.x, 0.001f)
        assertEquals(objectPos.y, playback.position.y, 0.001f)
        assertEquals(objectPos.z, playback.position.z, 0.001f)
    }

    @Test
    fun playAttachedSound_appliesAttenuationFallback_whenObjectIsUnlisted() = runBlocking {
        val soundId = UUID.randomUUID()
        val unlistedObjectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val dummyAudio = ByteArray(100) { 0x00 }
        assetCache.put(soundId, AssetType.SOUND, dummyAudio)

        val listenerPos = LLVector3(128f, 128f, 30f)
        val listenerForward = LLVector3(1f, 0f, 0f)
        soundManager.updateListener(listenerPos, listenerForward)

        val job = soundManager.playAttachedSound(soundId, unlistedObjectId, ownerId, gain = 1.0f)
        job.join()

        val activeSounds = soundManager.getPlayingSounds()
        assertTrue(activeSounds.isNotEmpty())
        val playback = activeSounds.values.first { it.soundId == soundId }
        assertEquals(unlistedObjectId, playback.objectId)

        val expectedFallbackPos = listenerPos + (listenerForward * 15f)
        assertEquals(expectedFallbackPos.x, playback.position.x, 0.001f)
        assertEquals(expectedFallbackPos.y, playback.position.y, 0.001f)
        assertEquals(expectedFallbackPos.z, playback.position.z, 0.001f)
    }

    @Test
    fun updateListener_refreshesSpatialPosition_whenUnlistedObjectBecomesRegistered() = runBlocking {
        val soundId = UUID.randomUUID()
        val objectId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val dummyAudio = ByteArray(100) { 0x00 }
        assetCache.put(soundId, AssetType.SOUND, dummyAudio)

        val listenerPos = LLVector3(128f, 128f, 30f)
        soundManager.updateListener(listenerPos, LLVector3(1f, 0f, 0f))

        val job = soundManager.playAttachedSound(soundId, objectId, ownerId, gain = 1.0f)
        job.join()

        var playback = soundManager.getPlayingSounds().values.first { it.soundId == soundId }
        val fallbackX = (listenerPos + LLVector3(15f, 0f, 0f)).x
        assertEquals(fallbackX, playback.position.x, 0.001f)

        val realObjectPos = LLVector3(128f, 140f, 30f)
        objectManager.handleObjectUpdate(
            ObjectUpdateData(
                localId = 2002,
                fullId = objectId,
                ownerId = ownerId,
                position = realObjectPos
            )
        )

        soundManager.updateListener(listenerPos, LLVector3(1f, 0f, 0f))

        playback = soundManager.getPlayingSounds().values.first { it.soundId == soundId }
        assertEquals(realObjectPos.x, playback.position.x, 0.001f)
        assertEquals(realObjectPos.y, playback.position.y, 0.001f)
        assertEquals(realObjectPos.z, playback.position.z, 0.001f)
    }
}
