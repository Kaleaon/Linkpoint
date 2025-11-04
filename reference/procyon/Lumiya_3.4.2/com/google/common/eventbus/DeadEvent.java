// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.eventbus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.annotations.Beta;

@Beta
public class DeadEvent
{
    private final Object event;
    private final Object source;
    
    public DeadEvent(final Object o, final Object o2) {
        this.source = Preconditions.checkNotNull(o);
        this.event = Preconditions.checkNotNull(o2);
    }
    
    public Object getEvent() {
        return this.event;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("source", this.source).add("event", this.event).toString();
    }
}
