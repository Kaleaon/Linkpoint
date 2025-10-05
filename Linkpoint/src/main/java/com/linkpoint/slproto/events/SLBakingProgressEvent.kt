package com.linkpoint.slproto.events

data class SLBakingProgressEvent(
    val first: Boolean,
    val done: Boolean,
    val progress: Int
)