package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;

/* loaded from: classes.dex */
public class GLSyncLoadQueue extends GLLoadQueue implements GLLoadQueue.GLLoadHandler {
    private static final int MAX_LOADS_PER_FRAME = 16;
    private static final int MAX_SIZE_PER_FRAME = 4194304;
    private static final int WAIT_FRAMES_AFTER_LOAD = 3;
    private int framesWait = 0;

    @Override // com.lumiyaviewer.lumiya.render.glres.GLLoadQueue.GLLoadHandler
    public void GLResourceLoaded(GLLoadQueue.GLLoadable gLLoadable) {
        gLLoadable.GLCompleteLoad();
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x005b, code lost:
    
        r3 = r1;
        r1 = r0;
     */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void RunLoadQueue(@javax.annotation.Nonnull com.lumiyaviewer.lumiya.render.RenderContext r6) {
        /*
            r5 = this;
            r2 = 0
            int r0 = r5.framesWait
            if (r0 == 0) goto Lc
            int r0 = r5.framesWait
            int r0 = r0 + (-1)
            r5.framesWait = r0
            return
        Lc:
            r1 = r2
            r3 = r2
        Le:
            boolean r0 = com.lumiyaviewer.lumiya.render.TextureMemoryTracker.canAllocateMemory(r2)
            if (r0 == 0) goto L30
            com.lumiyaviewer.lumiya.res.collections.WeakQueue<com.lumiyaviewer.lumiya.render.glres.GLLoadQueue$GLLoadable> r0 = r5.loadQueue
            java.lang.Object r0 = r0.poll()
            com.lumiyaviewer.lumiya.render.glres.GLLoadQueue$GLLoadable r0 = (com.lumiyaviewer.lumiya.render.glres.GLLoadQueue.GLLoadable) r0
            if (r0 == 0) goto L30
            int r4 = r0.GLGetLoadSize()
            boolean r4 = com.lumiyaviewer.lumiya.render.TextureMemoryTracker.canAllocateMemory(r4)
            if (r4 != 0) goto L49
            com.lumiyaviewer.lumiya.render.TextureMemoryTracker.stall()
            com.lumiyaviewer.lumiya.res.collections.WeakQueue<com.lumiyaviewer.lumiya.render.glres.GLLoadQueue$GLLoadable> r4 = r5.loadQueue
            r4.add(r0)
        L30:
            if (r3 == 0) goto L48
            java.lang.String r0 = "waitForMemory: loadedCount %d, size %d"
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4[r2] = r3
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2 = 1
            r4[r2] = r1
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)
        L48:
            return
        L49:
            int r0 = r0.GLLoad(r6, r5)
            int r0 = r0 + r1
            r1 = 3
            r5.framesWait = r1
            int r1 = r3 + 1
            r3 = 16
            if (r1 >= r3) goto L5b
            r3 = 4194304(0x400000, float:5.877472E-39)
            if (r0 < r3) goto L5e
        L5b:
            r3 = r1
            r1 = r0
            goto L30
        L5e:
            r3 = r1
            r1 = r0
            goto Le
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.glres.GLSyncLoadQueue.RunLoadQueue(com.lumiyaviewer.lumiya.render.RenderContext):void");
    }
}
