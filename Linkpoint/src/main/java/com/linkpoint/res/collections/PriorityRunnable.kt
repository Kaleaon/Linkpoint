package com.linkpoint.res.collections

import com.linkpoint.utils.HasPriority

abstract class PriorityRunnable(private val priority: Int) : Runnable, HasPriority {
    override fun getPriority(): Int {
        return priority
    }
}