package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.WeakHashMap;

public class GLResourceManager {
    private final Object glCleanableLock = new Object();
    private final Set<GLCleanable> glCleanables = Collections.newSetFromMap(new WeakHashMap());
    private final ReferenceQueue<GLGenericResource> refQueue = new ReferenceQueue();
    private final Set<GLGenericResourceReference> refSet = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap()));

    public static abstract class GLGenericResourceReference extends PhantomReference<GLGenericResource> {
        public GLGenericResourceReference(GLGenericResource gLGenericResource, GLResourceManager gLResourceManager) {
            super(gLGenericResource, gLResourceManager.refQueue);
            gLResourceManager.refSet.add(this);
        }

        public abstract void GLFree();
    }

    public static abstract class GLResourceReference extends GLGenericResourceReference {
        protected final int handle;

        public GLResourceReference(GLResource gLResource, int i, GLResourceManager gLResourceManager) {
            super(gLResource, gLResourceManager);
            this.handle = i;
            gLResourceManager.refSet.add(this);
        }
    }

    public void Cleanup() {
        synchronized (this.glCleanableLock) {
            this.glCleanables.size();
        }
        while (true) {
            Reference poll = this.refQueue.poll();
            if (poll == null) {
                return;
            }
            if (poll instanceof GLGenericResourceReference) {
                ((GLGenericResourceReference) poll).GLFree();
                this.refSet.remove(poll);
            }
        }
    }

    public void Flush() {
        synchronized (this.glCleanableLock) {
            for (GLCleanable gLCleanable : this.glCleanables) {
                if (gLCleanable != null) {
                    gLCleanable.GLCleanup();
                }
            }
            this.glCleanables.clear();
        }
        do {
        } while (this.refQueue.poll() != null);
        this.refSet.clear();
        TextureMemoryTracker.releaseAllGLMemory();
    }

    public void addCleanable(GLCleanable gLCleanable) {
        synchronized (this.glCleanableLock) {
            this.glCleanables.add(gLCleanable);
        }
    }
}
