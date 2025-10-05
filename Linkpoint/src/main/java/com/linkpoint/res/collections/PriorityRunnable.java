package com.linkpoint.res.collections;

import com.linkpoint.utils.HasPriority;

public abstract class PriorityRunnable implements Runnable, HasPriority {
    private final int priority;

    public PriorityRunnable(int i) {
        this.priority = i;
    }

    public int getPriority() {
        return this.priority;
    }
}
