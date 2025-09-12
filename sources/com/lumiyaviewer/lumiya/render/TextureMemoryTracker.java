// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TextureMemoryTracker
{

    private static final int PAGE_SIZE = 4096;
    private static final int RELEASE_DELAY_FRAMES = 4;
    private static final int TEXTURE_MAX_RESERVED_MEMORY = 0x2000000;
    private static WeakReference activeRendererRef = null;
    private static AtomicInteger bufMemory = new AtomicInteger(0);
    private static final AtomicInteger delayedRelease[];
    private static final AtomicInteger delayedReleaseBuf[];
    private static final AtomicInteger delayedReleaseIndex = new AtomicInteger(0);
    private static AtomicBoolean inflightLowMemory = new AtomicBoolean(false);
    private static AtomicInteger openJpegMemoryMmapped = new AtomicInteger(0);
    private static AtomicInteger openJpegMemoryUsed = new AtomicInteger(0);
    private static final Object rendererLock = new Object();
    private static AtomicBoolean stalled = new AtomicBoolean(false);
    private static AtomicInteger textureMemoryLimit = new AtomicInteger(0x4000000);
    private static volatile int textureMemoryReserved = 0x2000000;
    private static AtomicInteger textureMemoryUsed = new AtomicInteger(0);

    public TextureMemoryTracker()
    {
    }

    public static int actualSize(int i)
    {
        return (((i + 4096) - 1) / 4096) * 4096;
    }

    public static void allocBufferMemory(int i)
    {
        bufMemory.addAndGet(actualSize(i));
        printStats();
    }

    public static void allocOpenJpegMemory(int i, boolean flag)
    {
        i = actualSize(i);
        if (flag)
        {
            openJpegMemoryMmapped.addAndGet(i);
        }
        openJpegMemoryUsed.addAndGet(i);
        updateInflightMemoryLow();
        printStats();
    }

    public static void allocTextureMemory(int i)
    {
        textureMemoryUsed.addAndGet(actualSize(i));
        updateInflightMemoryLow();
        printStats();
    }

    public static boolean canAllocateMemory(int i)
    {
        boolean flag = false;
        if (!stalled.get())
        {
            if (textureMemoryUsed.get() + bufMemory.get() + actualSize(i) + textureMemoryReserved < textureMemoryLimit.get())
            {
                flag = true;
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public static void clearActiveRenderer(Object obj)
    {
        Object obj1 = null;
        Object obj2 = rendererLock;
        obj2;
        JVM INSTR monitorenter ;
        if (activeRendererRef != null)
        {
            obj1 = activeRendererRef.get();
        }
          goto _L1
_L2:
        activeRendererRef = null;
_L3:
        obj2;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
_L1:
        if (obj1 != obj && obj1 != null) goto _L3; else goto _L2
    }

    public static boolean hasActiveRenderer()
    {
        Object obj = null;
        Object obj1 = rendererLock;
        obj1;
        JVM INSTR monitorenter ;
        if (activeRendererRef != null)
        {
            obj = activeRendererRef.get();
        }
        boolean flag;
        if (obj != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        obj1;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    private static void printStats()
    {
        if (Debug.isDebugBuild())
        {
            int i = textureMemoryUsed.get();
            int j = openJpegMemoryUsed.get();
            Debug.Printf("Texture mem used: %d Mb oj %d mmap %d tot %d limit %d Mb buf %dk", new Object[] {
                Integer.valueOf(textureMemoryUsed.get() / 0x100000), Integer.valueOf(openJpegMemoryUsed.get() / 0x100000), Integer.valueOf(openJpegMemoryMmapped.get() / 0x100000), Integer.valueOf((i + j) / 0x100000), Integer.valueOf(textureMemoryLimit.get() / 0x100000), Integer.valueOf(bufMemory.get() / 1024)
            });
        }
    }

    public static void releaseAllFrameMemory()
    {
        for (int i = 0; i < 4; i++)
        {
            delayedRelease[i].set(0);
            delayedReleaseBuf[i].set(0);
        }

        delayedReleaseIndex.set(0);
    }

    public static void releaseAllGLMemory()
    {
        textureMemoryUsed.set(0);
        bufMemory.set(0);
        stalled.set(false);
        printStats();
    }

    public static void releaseBufferMemory(int i)
    {
        delayedReleaseBuf[delayedReleaseIndex.get()].addAndGet(actualSize(i));
    }

    public static void releaseFrameMemory()
    {
        int j = delayedReleaseIndex.incrementAndGet();
        int i = j;
        if (j >= 4)
        {
            delayedReleaseIndex.set(0);
            i = 0;
        }
        int k = delayedRelease[i].getAndSet(0);
        j = delayedReleaseBuf[i].getAndSet(0);
        boolean flag;
        if (k != 0)
        {
            textureMemoryUsed.addAndGet(-k);
            flag = true;
        } else
        {
            flag = false;
        }
        if (j != 0)
        {
            bufMemory.addAndGet(-j);
            flag = true;
        }
        if (flag)
        {
            updateInflightMemoryLow();
            printStats();
            stalled.set(false);
        }
    }

    public static void releaseOpenJpegMemory(int i, boolean flag)
    {
        i = actualSize(i);
        if (flag)
        {
            openJpegMemoryMmapped.addAndGet(-i);
        }
        openJpegMemoryUsed.addAndGet(-i);
        updateInflightMemoryLow();
    }

    public static void releaseTextureMemory(int i)
    {
        delayedRelease[delayedReleaseIndex.get()].addAndGet(actualSize(i));
    }

    public static void setActiveRenderer(Object obj)
    {
        Object obj1 = rendererLock;
        obj1;
        JVM INSTR monitorenter ;
        activeRendererRef = new WeakReference(obj);
        obj1;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    private static void setInflightMemoryLow(boolean flag)
    {
        if (inflightLowMemory.getAndSet(flag) != flag)
        {
            TextureCache.getInstance().setTextureMemoryState(flag);
        }
    }

    public static void setMemoryLimit(int i)
    {
        int j = 0x2000000;
        textureMemoryLimit.set(i);
        i /= 4;
        if (i > 0x2000000)
        {
            i = j;
        }
        textureMemoryReserved = i;
    }

    public static void stall()
    {
        stalled.set(true);
    }

    private static void updateInflightMemoryLow()
    {
        boolean flag;
        if (textureMemoryUsed.get() + openJpegMemoryUsed.get() >= (textureMemoryLimit.get() * 3) / 2)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        setInflightMemoryLow(flag);
    }

    static 
    {
        delayedRelease = new AtomicInteger[4];
        delayedReleaseBuf = new AtomicInteger[4];
        for (int i = 0; i < 4; i++)
        {
            delayedRelease[i] = new AtomicInteger(0);
            delayedReleaseBuf[i] = new AtomicInteger(0);
        }

    }
}
