package com.linkpoint.protocol.textures

/**
 * Controller interface for managing texture download queue execution.
 * Pauses texture fetching during background execution or full-screen chat overlay
 * states to eliminate modem power draw and conserve battery.
 */
interface TextureQueueController {
    /**
     * Pause texture queue execution, safely cancelling active transfers and freeing
     * concurrency permits.
     */
    fun pauseFetching()

    /**
     * Resume texture queue polling in priority order upon returning to active 3D navigation.
     */
    fun resumeFetching()

    /**
     * True if texture fetching is currently paused.
     */
    val isFetchingPaused: Boolean
}
