// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.collections;

import com.lumiyaviewer.lumiya.utils.HasPriority;

public abstract class PriorityRunnable implements Runnable, HasPriority
{
    private final int priority;
    
    public PriorityRunnable(final int priority) {
        this.priority = priority;
    }
    
    @Override
    public int getPriority() {
        return this.priority;
    }
}
