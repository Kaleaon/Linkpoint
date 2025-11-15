package com.lumiyaviewer.lumiya.slproto.events

data class SLBalanceChangedEvent(
    val oldBalanceValid: Boolean,
    val oldBalance: Int,
    val newBalance: Int,
)
