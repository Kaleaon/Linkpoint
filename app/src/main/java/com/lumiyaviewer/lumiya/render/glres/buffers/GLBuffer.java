package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class GLBuffer extends GLResource {
    /* access modifiers changed from: private */
    public static ThreadLocal<int[]> idBuffer = new ThreadLocal<int[]>() {
        /* access modifiers changed from: protected */
        public int[] initialValue() {
            return new int[1];
        }
    };
    private final DirectByteBuffer rawBuffer;

    private static class GLResourceBufferReference extends GLResourceManager.GLResourceReference {
        private final DirectByteBuffer rawBuffer;

        public GLResourceBufferReference(GLResource gLResource, int i, GLResourceManager gLResourceManager, DirectByteBuffer directByteBuffer) {
            super(gLResource, i, gLResourceManager);
            this.rawBuffer = directByteBuffer;
        }

        public void GLFree() {
            int[] iArr = (int[]) GLBuffer.idBuffer.get();
            iArr[0] = this.handle;
            Debug.Printf("GLBuffer: deleted buffer %d", Integer.valueOf(iArr[0]));
            GLES11.glDeleteBuffers(1, iArr, 0);
            if (this.rawBuffer != null) {
                TextureMemoryTracker.releaseBufferMemory(this.rawBuffer.getCapacity());
            }
        }
    }

    public GLBuffer(GLResourceManager gLResourceManager, DirectByteBuffer directByteBuffer) {
        super(gLResourceManager);
        this.rawBuffer = directByteBuffer;
        if (directByteBuffer != null) {
            TextureMemoryTracker.allocBufferMemory(directByteBuffer.getCapacity());
        }
        new GLResourceBufferReference(this, this.handle, gLResourceManager, this.rawBuffer);
    }

    /* access modifiers changed from: protected */
    public int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = idBuffer.get();
        GLES11.glGenBuffers(1, iArr, 0);
        Debug.Printf("GLBuffer: allocated buffer %d", Integer.valueOf(iArr[0]));
        return iArr[0];
    }
}
