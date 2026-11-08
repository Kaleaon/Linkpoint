package com.linkpoint.world.profile

import android.util.Log
import com.linkpoint.eventbus.AppEventBus
import com.linkpoint.eventbus.Subscription
import com.linkpoint.eventbus.events.AvatarPropertiesReplyEvent
import com.linkpoint.eventbus.events.AvatarPropertiesUpdateEvent
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.world.AvatarProfile
import com.linkpoint.world.ProfileInterests
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * UDP-based implementation of ProfileResolverStrategy (Priority Tier 2).
 * Dispatches UDP requests and listens on AppEventBus for AvatarPropertiesReply/Update events.
 */
class UdpProfileStrategy(
    private val udpConnection: UDPConnectionFixed? = null,
    private val agentId: UUID? = null,
    private val sendRequestOverride: (suspend (UUID) -> Unit)? = null,
    private val sendUpdateOverride: (suspend (imageId: UUID, flImageId: UUID, aboutText: String, flAboutText: String, allowPublish: Boolean, maturePublish: Boolean, profileUrl: String) -> Boolean)? = null
) : ProfileResolverStrategy {

    companion object {
        private const val TAG = "UdpProfileStrategy"
        private const val TIMEOUT_MS = 5000L

        private fun logD(msg: String) {
            try { Log.d(TAG, msg) } catch (_: Throwable) {}
        }
        private fun logI(msg: String) {
            try { Log.i(TAG, msg) } catch (_: Throwable) {}
        }
        private fun logW(msg: String) {
            try { Log.w(TAG, msg) } catch (_: Throwable) {}
        }
        private fun logE(msg: String, tr: Throwable? = null) {
            try { Log.e(TAG, msg, tr) } catch (_: Throwable) {}
        }
    }

    override val name: String = "UdpProfileStrategy"
    override val priority: Int = 2

    private val replyCache = ConcurrentHashMap<UUID, AvatarProfile>()
    private val pendingRequests = ConcurrentHashMap<UUID, CompletableDeferred<AvatarProfile>>()

    private var replySubscription: Subscription? = null
    private var updateSubscription: Subscription? = null

    init {
        registerEventBusListeners()
    }

    private fun registerEventBusListeners() {
        replySubscription = AppEventBus.subscribe<AvatarPropertiesReplyEvent> { event ->
            val profile = AvatarProfile(
                agentId = event.avatarId,
                displayName = "",
                userName = "",
                aboutText = event.aboutText,
                firstLifeText = event.firstLifeAboutText,
                profileImage = if (event.imageId != UUID(0, 0)) event.imageId else null,
                firstLifeImage = if (event.firstLifeImageId != UUID(0, 0)) event.firstLifeImageId else null,
                partner = if (event.partnerId != UUID(0, 0)) event.partnerId else null,
                bornOn = event.bornOn,
                memberOf = emptyList(),
                groups = emptyList(),
                picks = emptyList(),
                interests = ProfileInterests()
            )
            replyCache[event.avatarId] = profile

            val deferred = pendingRequests.remove(event.avatarId)
            deferred?.complete(profile)
        }

        updateSubscription = AppEventBus.subscribe<AvatarPropertiesUpdateEvent> { event ->
            logD("Received AvatarPropertiesUpdateEvent for agent ${event.agentId}")
        }
    }

    override fun isAvailable(): Boolean {
        return sendRequestOverride != null || (udpConnection != null && udpConnection.isConnected.value)
    }

    override suspend fun getAvatarProfile(agentId: UUID): AvatarProfile? {
        replyCache[agentId]?.let { return it }

        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<AvatarProfile>()
            pendingRequests[agentId] = deferred

            try {
                if (sendRequestOverride != null) {
                    sendRequestOverride.invoke(agentId)
                } else if (udpConnection != null && this@UdpProfileStrategy.agentId != null) {
                    sendPropertiesRequest(agentId)
                } else {
                    pendingRequests.remove(agentId)
                    return@withContext null
                }

                val result = withTimeoutOrNull(TIMEOUT_MS) {
                    deferred.await()
                }

                if (result == null) {
                    pendingRequests.remove(agentId)
                    logW("UDP AvatarPropertiesRequest timed out for $agentId")
                }
                result
            } catch (e: Exception) {
                pendingRequests.remove(agentId)
                logE("Failed UDP profile resolution for $agentId", e)
                null
            }
        }
    }

    override suspend fun getDisplayName(agentId: UUID): String? {
        return replyCache[agentId]?.displayName?.takeIf { it.isNotBlank() }
    }

    override suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String> {
        val results = mutableMapOf<UUID, String>()
        for (id in agentIds) {
            replyCache[id]?.displayName?.takeIf { it.isNotBlank() }?.let { results[id] = it }
        }
        return results
    }

    override suspend fun updateProfile(
        aboutText: String?,
        firstLifeText: String?,
        profileImage: UUID?,
        firstLifeImage: UUID?,
        interests: ProfileInterests?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val image = profileImage ?: UUID(0, 0)
                val flImage = firstLifeImage ?: UUID(0, 0)
                val about = aboutText ?: ""
                val flAbout = firstLifeText ?: ""

                if (sendUpdateOverride != null) {
                    return@withContext sendUpdateOverride.invoke(image, flImage, about, flAbout, true, false, "")
                } else if (udpConnection != null) {
                    udpConnection.sendAvatarPropertiesUpdate(
                        imageId = image,
                        flImageId = flImage,
                        aboutText = about,
                        firstLifeAboutText = flAbout,
                        allowPublish = true,
                        maturePublish = false,
                        profileUrl = ""
                    )
                    logI("Sent AvatarPropertiesUpdate packet over UDP")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                logE("Failed UDP profile update", e)
                false
            }
        }
    }

    private suspend fun sendPropertiesRequest(targetAgentId: UUID) {
        val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
        payload.putLong(agentId!!.mostSignificantBits)
        payload.putLong(agentId.leastSignificantBits)
        val sessionId = udpConnection!!.getSessionId()
        payload.putLong(sessionId.mostSignificantBits)
        payload.putLong(sessionId.leastSignificantBits)
        payload.putLong(targetAgentId.mostSignificantBits)
        payload.putLong(targetAgentId.leastSignificantBits)

        udpConnection.sendPacket(MessageIdRegistry.AVATAR_PROPERTIES_REQUEST, payload.array(), reliable = true)
    }

    fun dispose() {
        replySubscription?.unsubscribe()
        updateSubscription?.unsubscribe()
        pendingRequests.clear()
        replyCache.clear()
    }
}
