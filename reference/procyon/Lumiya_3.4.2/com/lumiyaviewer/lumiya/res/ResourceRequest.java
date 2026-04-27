// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res;

import java.util.Map;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Set;

public abstract class ResourceRequest<ResourceParams, ResourceType>
{
    private final Set<ResourceConsumer> consumers;
    private boolean isCancelled;
    private boolean isCompleted;
    private final ResourceManager<ResourceParams, ResourceType> manager;
    private final ResourceParams params;
    private boolean started;
    
    public ResourceRequest(final ResourceParams params, final ResourceManager<ResourceParams, ResourceType> manager) {
        this.started = false;
        this.isCompleted = false;
        this.isCancelled = false;
        this.consumers = Collections.newSetFromMap(new WeakHashMap<ResourceConsumer, Boolean>(4, 0.5f));
        this.params = params;
        this.manager = manager;
    }
    
    public void addConsumer(final ResourceConsumer resourceConsumer) {
        this.consumers.add(resourceConsumer);
    }
    
    public void cancelRequest() {
    }
    
    public void completeRequest(final ResourceType resourceType) {
        this.isCompleted = true;
        this.manager.CompleteRequest(this.params, resourceType, this.consumers);
    }
    
    public abstract void execute();
    
    protected final ResourceParams getParams() {
        return this.params;
    }
    
    public void intermediateResult(final ResourceType resourceType) {
        this.manager.IntermediateResult(this.params, resourceType, this.consumers);
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    public final boolean isCompleted() {
        return this.isCompleted;
    }
    
    public boolean isStale() {
        boolean b = false;
        if (this.consumers.size() == 0) {
            b = true;
        }
        return b;
    }
    
    public boolean removeConsumer(final ResourceConsumer resourceConsumer) {
        boolean b = false;
        this.consumers.remove(resourceConsumer);
        if (this.consumers.size() == 0) {
            b = true;
        }
        return b;
    }
    
    public void setCancelled(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    
    public final boolean willStart() {
        return !this.started && (this.started = true);
    }
}
