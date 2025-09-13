package com.lumiyaviewer.lumiya.memory;

/**
 * Interface for listening to memory pressure events
 */
public interface MemoryPressureListener {
    /**
     * Called when memory pressure is detected (usage > 80%)
     */
    void onMemoryPressure();
}