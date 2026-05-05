package com.linkpoint.avatar

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.MeshManager
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import java.util.UUID

// AvatarManager and the surrounding scene update flow log via
// android.util.Log; without Robolectric the Log.println_native JNI
// stub throws UnsatisfiedLinkError before we reach any assertion.
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AvatarUpdateFixtureTest {

    @Test
    fun `avatar update fixture populates manager and scene state`() = runTest {
        val manager = AvatarManager(
            context = mock<Context>(),
            meshManager = mock<MeshManager>(),
            textureManager = mock<TextureManager>(),
            animationManager = mock<AnimationManager>(),
            capabilityManager = mock<CapabilityManager>(),
            udpConnection = null
        )

        val sceneAvatarCount = MutableStateFlow(0)
        val sceneIds = linkedSetOf<UUID>()
        val collectJob = launch {
            manager.avatarEvents.collect { event ->
                when (event) {
                    is AvatarManager.AvatarEvent.Added,
                    is AvatarManager.AvatarEvent.Updated -> {
                        val id = when (event) {
                            is AvatarManager.AvatarEvent.Added -> event.agentId
                            is AvatarManager.AvatarEvent.Updated -> event.agentId
                            else -> UUID(0L, 0L)
                        }
                        sceneIds.add(id)
                    }
                    is AvatarManager.AvatarEvent.Removed -> sceneIds.remove(event.agentId)
                }
                sceneAvatarCount.value = sceneIds.size
            }
        }
        // Make sure the collector has actually subscribed to avatarEvents
        // before we start emitting. Without this, the launch{} body is
        // queued in the test scheduler but hasn't reached `.collect`,
        // so the SharedFlow's tryEmit drops on the floor (0 subscribers)
        // and sceneAvatarCount never moves off 0.
        testScheduler.runCurrent()

        val agentId = UUID.randomUUID()
        val localId = 54321
        val fixture = ObjectUpdateData(
            localId = localId,
            fullId = agentId,
            parentId = 0,
            position = LLVector3(128f, 64f, 32f),
            rotation = LLQuaternion.identity(),
            velocity = LLVector3(1f, 0f, 0f),
            scale = LLVector3(1f, 1f, 2f),
            pcode = 47,
            material = 0,
            clickAction = 0,
            updateFlags = 0,
            textureEntry = ByteArray(0),
            hoverText = "",
            hoverTextColor = LLColor4.white(),
            mediaUrl = "",
            ownerId = null,
            nameValue = ""
        )

        manager.updateAvatar(
            agentId = fixture.fullId,
            localId = fixture.localId,
            position = fixture.position,
            rotation = fixture.rotation,
            velocity = fixture.velocity
        )
        advanceUntilIdle()

        // Drain the test dispatcher so the avatarEvents collector
        // observes the Add event before we check sceneAvatarCount.
        // Without this, the assertion races the flow emission and
        // sometimes sees 0 even though updateAvatar succeeded.
        testScheduler.runCurrent()

        val avatar = manager.getAvatar(agentId)
        assertNotNull(avatar)
        assertEquals(localId, avatar?.localId)
        assertNotEquals(LLVector3.zero(), avatar?.position)
        assertNotEquals(0, sceneAvatarCount.value)

        collectJob.cancel()
    }
}
