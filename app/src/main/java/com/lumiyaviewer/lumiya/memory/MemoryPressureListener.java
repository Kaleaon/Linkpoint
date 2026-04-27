package com.lumiyaviewer.lumiya.memory;
import java.util.*;

/**
 * Interface for listening to memory pressure events
 */
public interface MemoryPressureListener {
    /**
     * Called when memory pressure is detected (usage > 80%)
     */
    void onMemoryPressure();
}