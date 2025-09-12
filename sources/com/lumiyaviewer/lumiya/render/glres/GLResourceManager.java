// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLCleanable, GLGenericResource, GLResource

public class GLResourceManager
{
    public static abstract class GLGenericResourceReference extends PhantomReference
    {

        public abstract void GLFree();

        public GLGenericResourceReference(GLGenericResource glgenericresource, GLResourceManager glresourcemanager)
        {
            super(glgenericresource, GLResourceManager._2D_get0(glresourcemanager));
            GLResourceManager._2D_get1(glresourcemanager).add(this);
        }
    }

    public static abstract class GLResourceReference extends GLGenericResourceReference
    {

        protected final int handle;

        public GLResourceReference(GLResource glresource, int i, GLResourceManager glresourcemanager)
        {
            super(glresource, glresourcemanager);
            handle = i;
            GLResourceManager._2D_get1(glresourcemanager).add(this);
        }
    }


    private final Object glCleanableLock = new Object();
    private final Set glCleanables = Collections.newSetFromMap(new WeakHashMap());
    private final ReferenceQueue refQueue = new ReferenceQueue();
    private final Set refSet = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap()));

    static ReferenceQueue _2D_get0(GLResourceManager glresourcemanager)
    {
        return glresourcemanager.refQueue;
    }

    static Set _2D_get1(GLResourceManager glresourcemanager)
    {
        return glresourcemanager.refSet;
    }

    public GLResourceManager()
    {
    }

    public void Cleanup()
    {
        Object obj = glCleanableLock;
        obj;
        JVM INSTR monitorenter ;
        glCleanables.size();
        obj;
        JVM INSTR monitorexit ;
          goto _L1
        exception;
        throw exception;
_L1:
        Exception exception;
        do
        {
            obj = refQueue.poll();
            if (obj == null)
            {
                break;
            }
            if (obj instanceof GLGenericResourceReference)
            {
                ((GLGenericResourceReference)obj).GLFree();
                refSet.remove(obj);
            }
        } while (true);
        return;
    }

    public void Flush()
    {
        Object obj = glCleanableLock;
        obj;
        JVM INSTR monitorenter ;
        Iterator iterator = glCleanables.iterator();
_L2:
        GLCleanable glcleanable;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_54;
            }
            glcleanable = (GLCleanable)iterator.next();
        } while (glcleanable == null);
        glcleanable.GLCleanup();
        if (true) goto _L2; else goto _L1
_L1:
        Exception exception;
        exception;
        throw exception;
        glCleanables.clear();
        obj;
        JVM INSTR monitorexit ;
        while (refQueue.poll() != null) ;
        refSet.clear();
        TextureMemoryTracker.releaseAllGLMemory();
        return;
    }

    public void addCleanable(GLCleanable glcleanable)
    {
        Object obj = glCleanableLock;
        obj;
        JVM INSTR monitorenter ;
        glCleanables.add(glcleanable);
        obj;
        JVM INSTR monitorexit ;
        return;
        glcleanable;
        throw glcleanable;
    }
}
