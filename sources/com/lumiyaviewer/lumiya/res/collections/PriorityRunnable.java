package com.lumiyaviewer.lumiya.res.collections;

import com.lumiyaviewer.lumiya.utils.HasPriority;

public abstract class PriorityRunnable implements Runnable, HasPriority {
    private final int priority;

    public PriorityRunnable(int i) {
        this.priority = i;
    }

    public int getPriority() {
        return this.priority;
    }
}
