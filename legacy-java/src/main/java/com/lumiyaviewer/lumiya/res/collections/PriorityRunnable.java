package com.lumiyaviewer.lumiya.res.collections;

import com.lumiyaviewer.lumiya.utils.HasPriority;

/* loaded from: classes.dex */
public abstract class PriorityRunnable implements Runnable, HasPriority {
    private final int priority;

    public PriorityRunnable(int i) {
        this.priority = i;
    }

    @Override // com.lumiyaviewer.lumiya.utils.HasPriority
    public int getPriority() {
        return this.priority;
    }
}
