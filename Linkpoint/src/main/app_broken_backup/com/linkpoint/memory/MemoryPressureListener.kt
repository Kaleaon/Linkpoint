package com.linkpoint.memory

/**
 * Interface for listening to memory pressure events
 */
interface MemoryPressureListener {
    /**
     * Called when memory pressure is detected (usage > 80%)
     */
    fun onMemoryPressure()
}