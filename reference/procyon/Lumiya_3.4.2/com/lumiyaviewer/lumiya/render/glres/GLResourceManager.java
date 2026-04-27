// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

import java.lang.ref.PhantomReference;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import java.lang.ref.Reference;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.lang.ref.ReferenceQueue;
import java.util.Set;

public class GLResourceManager
{
    private final Object glCleanableLock;
    private final Set<GLCleanable> glCleanables;
    private final ReferenceQueue<GLGenericResource> refQueue;
    private final Set<GLGenericResourceReference> refSet;
    
    public GLResourceManager() {
        this.refQueue = new ReferenceQueue<GLGenericResource>();
        this.refSet = Collections.synchronizedSet((Set<GLGenericResourceReference>)Collections.newSetFromMap((Map<T, Boolean>)new IdentityHashMap<Object, Boolean>()));
        this.glCleanableLock = new Object();
        this.glCleanables = Collections.newSetFromMap(new WeakHashMap<GLCleanable, Boolean>());
    }
    
    public void Cleanup() {
        synchronized (this.glCleanableLock) {
            this.glCleanables.size();
            monitorexit(this.glCleanableLock);
            while (true) {
                final Reference<? extends GLGenericResource> poll = this.refQueue.poll();
                if (poll == null) {
                    break;
                }
                if (!(poll instanceof GLGenericResourceReference)) {
                    continue;
                }
                ((GLGenericResourceReference)poll).GLFree();
                this.refSet.remove(poll);
            }
        }
    }
    
    public void Flush() {
        synchronized (this.glCleanableLock) {
            for (final GLCleanable glCleanable : this.glCleanables) {
                if (glCleanable != null) {
                    glCleanable.GLCleanup();
                }
            }
        }
        this.glCleanables.clear();
        final Object o;
        monitorexit(o);
        while (this.refQueue.poll() != null) {}
        this.refSet.clear();
        TextureMemoryTracker.releaseAllGLMemory();
    }
    
    public void addCleanable(final GLCleanable glCleanable) {
        synchronized (this.glCleanableLock) {
            this.glCleanables.add(glCleanable);
        }
    }
    
    public abstract static class GLGenericResourceReference extends PhantomReference<GLGenericResource>
    {
        public GLGenericResourceReference(final GLGenericResource referent, final GLResourceManager glResourceManager) {
            super(referent, glResourceManager.refQueue);
            glResourceManager.refSet.add(this);
        }
        
        public abstract void GLFree();
    }
    
    public abstract static class GLResourceReference extends GLGenericResourceReference
    {
        protected final int handle;
        
        public GLResourceReference(final GLResource glResource, final int handle, final GLResourceManager glResourceManager) {
            super(glResource, glResourceManager);
            this.handle = handle;
            glResourceManager.refSet.add(this);
        }
    }
}
