// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class TextureMemoryTracker
{
    private static final int PAGE_SIZE = 4096;
    private static final int RELEASE_DELAY_FRAMES = 4;
    private static final int TEXTURE_MAX_RESERVED_MEMORY = 33554432;
    @Nullable
    private static WeakReference<Object> activeRendererRef;
    private static AtomicInteger bufMemory;
    private static final AtomicInteger[] delayedRelease;
    private static final AtomicInteger[] delayedReleaseBuf;
    private static final AtomicInteger delayedReleaseIndex;
    private static AtomicBoolean inflightLowMemory;
    private static AtomicInteger openJpegMemoryMmapped;
    private static AtomicInteger openJpegMemoryUsed;
    private static final Object rendererLock;
    private static AtomicBoolean stalled;
    private static AtomicInteger textureMemoryLimit;
    private static volatile int textureMemoryReserved;
    private static AtomicInteger textureMemoryUsed;
    
    static {
        TextureMemoryTracker.textureMemoryReserved = 33554432;
        TextureMemoryTracker.textureMemoryUsed = new AtomicInteger(0);
        TextureMemoryTracker.openJpegMemoryUsed = new AtomicInteger(0);
        TextureMemoryTracker.openJpegMemoryMmapped = new AtomicInteger(0);
        TextureMemoryTracker.textureMemoryLimit = new AtomicInteger(67108864);
        TextureMemoryTracker.inflightLowMemory = new AtomicBoolean(false);
        TextureMemoryTracker.bufMemory = new AtomicInteger(0);
        delayedRelease = new AtomicInteger[4];
        delayedReleaseBuf = new AtomicInteger[4];
        delayedReleaseIndex = new AtomicInteger(0);
        TextureMemoryTracker.stalled = new AtomicBoolean(false);
        rendererLock = new Object();
        TextureMemoryTracker.activeRendererRef = null;
        for (int i = 0; i < 4; ++i) {
            TextureMemoryTracker.delayedRelease[i] = new AtomicInteger(0);
            TextureMemoryTracker.delayedReleaseBuf[i] = new AtomicInteger(0);
        }
    }
    
    public static int actualSize(final int n) {
        return (n + 4096 - 1) / 4096 * 4096;
    }
    
    public static void allocBufferMemory(final int n) {
        TextureMemoryTracker.bufMemory.addAndGet(actualSize(n));
        printStats();
    }
    
    public static void allocOpenJpegMemory(int actualSize, final boolean b) {
        actualSize = actualSize(actualSize);
        if (b) {
            TextureMemoryTracker.openJpegMemoryMmapped.addAndGet(actualSize);
        }
        TextureMemoryTracker.openJpegMemoryUsed.addAndGet(actualSize);
        updateInflightMemoryLow();
        printStats();
    }
    
    public static void allocTextureMemory(final int n) {
        TextureMemoryTracker.textureMemoryUsed.addAndGet(actualSize(n));
        updateInflightMemoryLow();
        printStats();
    }
    
    public static boolean canAllocateMemory(final int n) {
        boolean b = false;
        if (!TextureMemoryTracker.stalled.get()) {
            if (TextureMemoryTracker.textureMemoryUsed.get() + TextureMemoryTracker.bufMemory.get() + actualSize(n) + TextureMemoryTracker.textureMemoryReserved < TextureMemoryTracker.textureMemoryLimit.get()) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    public static void clearActiveRenderer(final Object o) {
        Object value = null;
        synchronized (TextureMemoryTracker.rendererLock) {
            if (TextureMemoryTracker.activeRendererRef != null) {
                value = TextureMemoryTracker.activeRendererRef.get();
            }
            if (value == o || value == null) {
                TextureMemoryTracker.activeRendererRef = null;
            }
        }
    }
    
    public static boolean hasActiveRenderer() {
        Object value = null;
        synchronized (TextureMemoryTracker.rendererLock) {
            if (TextureMemoryTracker.activeRendererRef != null) {
                value = TextureMemoryTracker.activeRendererRef.get();
            }
            return value != null;
        }
    }
    
    private static void printStats() {
        if (Debug.isDebugBuild()) {
            Debug.Printf("Texture mem used: %d Mb oj %d mmap %d tot %d limit %d Mb buf %dk", TextureMemoryTracker.textureMemoryUsed.get() / 1048576, TextureMemoryTracker.openJpegMemoryUsed.get() / 1048576, TextureMemoryTracker.openJpegMemoryMmapped.get() / 1048576, (TextureMemoryTracker.textureMemoryUsed.get() + TextureMemoryTracker.openJpegMemoryUsed.get()) / 1048576, TextureMemoryTracker.textureMemoryLimit.get() / 1048576, TextureMemoryTracker.bufMemory.get() / 1024);
        }
    }
    
    public static void releaseAllFrameMemory() {
        for (int i = 0; i < 4; ++i) {
            TextureMemoryTracker.delayedRelease[i].set(0);
            TextureMemoryTracker.delayedReleaseBuf[i].set(0);
        }
        TextureMemoryTracker.delayedReleaseIndex.set(0);
    }
    
    public static void releaseAllGLMemory() {
        TextureMemoryTracker.textureMemoryUsed.set(0);
        TextureMemoryTracker.bufMemory.set(0);
        TextureMemoryTracker.stalled.set(false);
        printStats();
    }
    
    public static void releaseBufferMemory(final int n) {
        TextureMemoryTracker.delayedReleaseBuf[TextureMemoryTracker.delayedReleaseIndex.get()].addAndGet(actualSize(n));
    }
    
    public static void releaseFrameMemory() {
        int incrementAndGet;
        if ((incrementAndGet = TextureMemoryTracker.delayedReleaseIndex.incrementAndGet()) >= 4) {
            TextureMemoryTracker.delayedReleaseIndex.set(0);
            incrementAndGet = 0;
        }
        final int andSet = TextureMemoryTracker.delayedRelease[incrementAndGet].getAndSet(0);
        final int andSet2 = TextureMemoryTracker.delayedReleaseBuf[incrementAndGet].getAndSet(0);
        int n;
        if (andSet != 0) {
            TextureMemoryTracker.textureMemoryUsed.addAndGet(-andSet);
            n = 1;
        }
        else {
            n = 0;
        }
        if (andSet2 != 0) {
            TextureMemoryTracker.bufMemory.addAndGet(-andSet2);
            n = 1;
        }
        if (n != 0) {
            updateInflightMemoryLow();
            printStats();
            TextureMemoryTracker.stalled.set(false);
        }
    }
    
    public static void releaseOpenJpegMemory(int actualSize, final boolean b) {
        actualSize = actualSize(actualSize);
        if (b) {
            TextureMemoryTracker.openJpegMemoryMmapped.addAndGet(-actualSize);
        }
        TextureMemoryTracker.openJpegMemoryUsed.addAndGet(-actualSize);
        updateInflightMemoryLow();
    }
    
    public static void releaseTextureMemory(final int n) {
        TextureMemoryTracker.delayedRelease[TextureMemoryTracker.delayedReleaseIndex.get()].addAndGet(actualSize(n));
    }
    
    public static void setActiveRenderer(final Object referent) {
        synchronized (TextureMemoryTracker.rendererLock) {
            TextureMemoryTracker.activeRendererRef = new WeakReference<Object>(referent);
        }
    }
    
    private static void setInflightMemoryLow(final boolean b) {
        if (TextureMemoryTracker.inflightLowMemory.getAndSet(b) != b) {
            TextureCache.getInstance().setTextureMemoryState(b);
        }
    }
    
    public static void setMemoryLimit(int n) {
        final int n2 = 33554432;
        TextureMemoryTracker.textureMemoryLimit.set(n);
        n /= 4;
        if (n > 33554432) {
            n = n2;
        }
        TextureMemoryTracker.textureMemoryReserved = n;
    }
    
    public static void stall() {
        TextureMemoryTracker.stalled.set(true);
    }
    
    private static void updateInflightMemoryLow() {
        setInflightMemoryLow(TextureMemoryTracker.textureMemoryUsed.get() + TextureMemoryTracker.openJpegMemoryUsed.get() >= TextureMemoryTracker.textureMemoryLimit.get() * 3 / 2);
    }
}
