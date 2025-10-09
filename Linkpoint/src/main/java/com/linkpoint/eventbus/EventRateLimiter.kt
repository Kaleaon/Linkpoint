package com.linkpoint.eventbus

/**
 * Rate limiter for event publishing to prevent excessive event firing.
 * Converted from Java to Kotlin with improved thread safety and null handling.
 */
abstract class EventRateLimiter(
    private val bus: EventBus?,
    private val minInterval: Long
) {
    @Volatile
    private var isPending = false
    
    @Volatile
    private var lastTimeFired = 0L
    
    private val lock = Any()

    /**
     * Request to fire an event. The event will be rate-limited based on minInterval.
     */
    fun fire() {
        synchronized(lock) {
            isPending = true
        }
        firePending()
    }

    /**
     * Fire any pending events if enough time has passed since the last firing.
     */
    fun firePending() {
        val shouldFire = synchronized(lock) {
            if (isPending) {
                val currentTime = System.currentTimeMillis()
                if (currentTime >= lastTimeFired + minInterval) {
                    isPending = false
                    lastTimeFired = currentTime
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        if (shouldFire) {
            onActualFire()
            getEventToFire()?.let { event ->
                bus?.publish(event)
            }
        }
    }

    /**
     * Get the event object to fire. Return null if no event should be fired.
     */
    protected abstract fun getEventToFire(): Any?

    /**
     * Called when an event is actually fired (after rate limiting).
     * Override to add custom behavior.
     */
    protected open fun onActualFire() {
        // Empty default implementation
    }
}