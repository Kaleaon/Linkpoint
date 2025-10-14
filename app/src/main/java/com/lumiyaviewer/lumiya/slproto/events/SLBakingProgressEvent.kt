package com.lumiyaviewer.lumiya.slproto.events

data class SLBakingProgressEvent(
    var first: Boolean,
    var done: Boolean,
    var progress: Int,
)
