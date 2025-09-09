package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.avatar.SLMoveEvents;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public class TextureMemoryTracker {
    private static final int PAGE_SIZE = 4096;
    private static final int RELEASE_DELAY_FRAMES = 4;
    private static final int TEXTURE_MAX_RESERVED_MEMORY = 33554432;
    @Nullable
    private static WeakReference<Object> activeRendererRef = null;
    private static AtomicInteger bufMemory = new AtomicInteger(0);
    private static final AtomicInteger[] delayedRelease = new AtomicInteger[4];
    private static final AtomicInteger[] delayedReleaseBuf = new AtomicInteger[4];
    private static final AtomicInteger delayedReleaseIndex = new AtomicInteger(0);
    private static AtomicBoolean inflightLowMemory = new AtomicBoolean(false);
    private static AtomicInteger openJpegMemoryMmapped = new AtomicInteger(0);
    private static AtomicInteger openJpegMemoryUsed = new AtomicInteger(0);
    private static final Object rendererLock = new Object();
    private static AtomicBoolean stalled = new AtomicBoolean(false);
    private static AtomicInteger textureMemoryLimit = new AtomicInteger(SLMoveEvents.AGENT_CONTROL_TURN_RIGHT);
    private static volatile int textureMemoryReserved = 33554432;
    private static AtomicInteger textureMemoryUsed = new AtomicInteger(0);

    static {
        for (int i = 0; i < 4; i++) {
            delayedRelease[i] = new AtomicInteger(0);
            delayedReleaseBuf[i] = new AtomicInteger(0);
        }
    }

    public static int actualSize(int i) {
        return (((i + 4096) - 1) / 4096) * 4096;
    }

    public static void allocBufferMemory(int i) {
        bufMemory.addAndGet(actualSize(i));
        printStats();
    }

    public static void allocOpenJpegMemory(int i, boolean z) {
        int actualSize = actualSize(i);
        if (z) {
            openJpegMemoryMmapped.addAndGet(actualSize);
        }
        openJpegMemoryUsed.addAndGet(actualSize);
        updateInflightMemoryLow();
        printStats();
    }

    public static void allocTextureMemory(int i) {
        textureMemoryUsed.addAndGet(actualSize(i));
        updateInflightMemoryLow();
        printStats();
    }

    public static boolean canAllocateMemory(int i) {
        return !stalled.get() && ((textureMemoryUsed.get() + bufMemory.get()) + actualSize(i)) + textureMemoryReserved < textureMemoryLimit.get();
    }

    public static void clearActiveRenderer(Object obj) {
        Object obj2 = null;
        synchronized (rendererLock) {
            if (activeRendererRef != null) {
                obj2 = activeRendererRef.get();
            }
            if (obj2 == obj || obj2 == null) {
                activeRendererRef = null;
            }
        }
    }

    public static boolean hasActiveRenderer() {
        boolean z;
        Object obj = null;
        synchronized (rendererLock) {
            if (activeRendererRef != null) {
                obj = activeRendererRef.get();
            }
            z = obj != null;
        }
        return z;
    }

    private static void printStats() {
        if (Debug.isDebugBuild()) {
            Debug.Printf("Texture mem used: %d Mb oj %d mmap %d tot %d limit %d Mb buf %dk", Integer.valueOf(textureMemoryUsed.get() / 1048576), Integer.valueOf(openJpegMemoryUsed.get() / 1048576), Integer.valueOf(openJpegMemoryMmapped.get() / 1048576), Integer.valueOf((textureMemoryUsed.get() + openJpegMemoryUsed.get()) / 1048576), Integer.valueOf(textureMemoryLimit.get() / 1048576), Integer.valueOf(bufMemory.get() / 1024));
        }
    }

    public static void releaseAllFrameMemory() {
        for (int i = 0; i < 4; i++) {
            delayedRelease[i].set(0);
            delayedReleaseBuf[i].set(0);
        }
        delayedReleaseIndex.set(0);
    }

    public static void releaseAllGLMemory() {
        textureMemoryUsed.set(0);
        bufMemory.set(0);
        stalled.set(false);
        printStats();
    }

    public static void releaseBufferMemory(int i) {
        delayedReleaseBuf[delayedReleaseIndex.get()].addAndGet(actualSize(i));
    }

    public static void releaseFrameMemory() {
        boolean z;
        int incrementAndGet = delayedReleaseIndex.incrementAndGet();
        if (incrementAndGet >= 4) {
            delayedReleaseIndex.set(0);
            incrementAndGet = 0;
        }
        int andSet = delayedRelease[incrementAndGet].getAndSet(0);
        int andSet2 = delayedReleaseBuf[incrementAndGet].getAndSet(0);
        if (andSet != 0) {
            textureMemoryUsed.addAndGet(-andSet);
            z = true;
        } else {
            z = false;
        }
        if (andSet2 != 0) {
            bufMemory.addAndGet(-andSet2);
            z = true;
        }
        if (z) {
            updateInflightMemoryLow();
            printStats();
            stalled.set(false);
        }
    }

    public static void releaseOpenJpegMemory(int i, boolean z) {
        int actualSize = actualSize(i);
        if (z) {
            openJpegMemoryMmapped.addAndGet(-actualSize);
        }
        openJpegMemoryUsed.addAndGet(-actualSize);
        updateInflightMemoryLow();
    }

    public static void releaseTextureMemory(int i) {
        delayedRelease[delayedReleaseIndex.get()].addAndGet(actualSize(i));
    }

    public static void setActiveRenderer(Object obj) {
        synchronized (rendererLock) {
            activeRendererRef = new WeakReference<>(obj);
        }
    }

    private static void setInflightMemoryLow(boolean z) {
        if (inflightLowMemory.getAndSet(z) != z) {
            TextureCache.getInstance().setTextureMemoryState(z);
        }
    }

    public static void setMemoryLimit(int i) {
        int i2 = 33554432;
        textureMemoryLimit.set(i);
        int i3 = i / 4;
        if (i3 <= 33554432) {
            i2 = i3;
        }
        textureMemoryReserved = i2;
    }

    public static void stall() {
        stalled.set(true);
    }

    private static void updateInflightMemoryLow() {
        setInflightMemoryLow(textureMemoryUsed.get() + openJpegMemoryUsed.get() >= (textureMemoryLimit.get() * 3) / 2);
    }
}
