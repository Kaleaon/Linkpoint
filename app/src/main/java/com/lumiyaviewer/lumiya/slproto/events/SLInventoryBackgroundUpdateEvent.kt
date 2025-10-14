package com.lumiyaviewer.lumiya.slproto.events

/**
 * Event fired when inventory background update status changes
 */
data class SLInventoryBackgroundUpdateEvent(
    val backgroundUpdateActive: Boolean,
)
