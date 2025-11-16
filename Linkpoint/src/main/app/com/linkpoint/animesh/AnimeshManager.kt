package com.linkpoint.animesh

import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

/**
 * Runtime manager that keeps track of all animesh instances currently active
 * inside the Linkpoint viewer. The implementation favours thread-safety and
 * determinism so it can be safely accessed from protocol, rendering and UI
 * layers without additional locking.
 */
class AnimeshManager(
    private val clock: () -> Long = { System.currentTimeMillis() }
) {

    private val objects = ConcurrentHashMap<UUID, AnimeshInstance>()
    private val animationCache = ConcurrentHashMap<UUID, AnimeshAnimation>()
    private val lastError = AtomicReference<String?>(null)

    fun registerAnimesh(
        objectId: UUID,
        attachmentId: UUID,
        skeletonData: ByteBuffer
    ): AnimeshInstance {
        val skeleton = SkeletonParser.parse(skeletonData)
        val instance = AnimeshInstance(
            objectId = objectId,
            attachmentId = attachmentId,
            skeleton = skeleton,
            activeAnimations = CopyOnWriteArrayList()
        )
        objects[objectId] = instance
        return instance
    }

    fun unregisterAnimesh(objectId: UUID) {
        objects.remove(objectId)
    }

    fun getAnimesh(objectId: UUID): AnimeshInstance? = objects[objectId]

    fun cacheAnimation(animationId: UUID, animation: AnimeshAnimation) {
        animationCache[animationId] = animation
    }

    fun removeCachedAnimation(animationId: UUID) {
        animationCache.remove(animationId)
        objects.values.forEach { instance ->
            instance.activeAnimations.removeAll { it.animation.animationId == animationId }
        }
    }

    fun addAnimation(
        objectId: UUID,
        animationId: UUID,
        restartIfRunning: Boolean = false
    ) {
        val instance = objects[objectId] ?: error("Unknown animesh object $objectId")
        val animation = animationCache[animationId] ?: error("Animation $animationId not cached")

        synchronized(instance.activeAnimations) {
            val existingIndex = instance.activeAnimations.indexOfFirst {
                it.animation.animationId == animationId
            }
            if (existingIndex >= 0) {
                if (!restartIfRunning) {
                    return
                }
                instance.activeAnimations.removeAt(existingIndex)
            }
            instance.activeAnimations.add(ActiveAnimation(animation, clock()))
        }
    }

    fun removeAnimation(objectId: UUID, animationId: UUID) {
        val instance = objects[objectId] ?: return
        synchronized(instance.activeAnimations) {
            instance.activeAnimations.removeAll { it.animation.animationId == animationId }
        }
    }

    fun getStats(): AnimeshStats =
        AnimeshStats(
            activeAnimeshCount = objects.size,
            cachedAnimationCount = animationCache.size
        )

    fun getLastError(): String? = lastError.get()

    fun cleanup() {
        objects.clear()
        animationCache.clear()
        lastError.set(null)
    }

    /**
     * Convenience helper that allows protocol or rendering code to safely
     * attempt a registration without propagating exceptions across threads.
     */
    fun tryRegister(
        objectId: UUID,
        attachmentId: UUID,
        skeletonData: ByteBuffer
    ): Boolean = runCatching {
        registerAnimesh(objectId, attachmentId, skeletonData)
        true
    }.getOrElse { error ->
        lastError.set(error.message)
        false
    }
}
