package com.lumiyaviewer.lumiya.res.collections

import com.lumiyaviewer.lumiya.utils.HasPriority

abstract class PriorityRunnable(
    private val priority: Int
) : Runnable, HasPriority {
    
    override fun getPriority(): Int = priority
}