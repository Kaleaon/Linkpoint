package com.lumiyaviewer.lumiya.res;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class ResourceRequest<ResourceParams, ResourceType> {
    private final Set<ResourceConsumer> consumers = Collections.newSetFromMap(new WeakHashMap(4, 0.5f));
    private boolean isCancelled = false;
    private boolean isCompleted = false;
    private final ResourceManager<ResourceParams, ResourceType> manager;
    private final ResourceParams params;
    private boolean started = false;

    public ResourceRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        this.params = resourceparams;
        this.manager = resourceManager;
    }

    public void addConsumer(ResourceConsumer resourceConsumer) {
        this.consumers.add(resourceConsumer);
    }

    public void cancelRequest() {
    }

    public void completeRequest(ResourceType resourcetype) {
        this.isCompleted = true;
        this.manager.CompleteRequest(this.params, resourcetype, this.consumers);
    }

    public abstract void execute();

    /* access modifiers changed from: protected */
    public final ResourceParams getParams() {
        return this.params;
    }

    public void intermediateResult(ResourceType resourcetype) {
        this.manager.IntermediateResult(this.params, resourcetype, this.consumers);
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public final boolean isCompleted() {
        return this.isCompleted;
    }

    public boolean isStale() {
        return this.consumers.size() == 0;
    }

    public boolean removeConsumer(ResourceConsumer resourceConsumer) {
        this.consumers.remove(resourceConsumer);
        return this.consumers.size() == 0;
    }

    public void setCancelled(boolean z) {
        this.isCancelled = z;
    }

    public final boolean willStart() {
        if (this.started) {
            return false;
        }
        this.started = true;
        return true;
    }
}
