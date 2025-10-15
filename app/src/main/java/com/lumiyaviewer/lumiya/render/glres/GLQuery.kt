package com.lumiyaviewer.lumiya.render.glres

import android.annotation.TargetApi
import android.opengl.GLES30
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager
import javax.annotation.Nonnull

@TargetApi(18)
class GLQuery extends GLResource {
    private int MIN_OCCLUSION_QUERY_FRAMES = 0
    /* access modifiers changed from: private */
    ThreadLocal<int[]> idQuery = new ThreadLocal<int[]>() {
        /* access modifiers changed from: protected */
        int[] initialValue() {
            return new int[1]
        }
    }
    private boolean isQueryRunning = false
    @Nonnull
    private OcclusionQueryResult queryResult = OcclusionQueryResult.NotReady
    private int queryStartedFrameCount = 0

    private class GLQueryReference extends GLResourceManager.GLResourceReference {
        GLQueryReference(GLResource gLResource, int i, GLResourceManager gLResourceManager) {
            super(gLResource, i, gLResourceManager)
        }

        void GLFree() {
            int[] iArr = (int[]) GLQuery.idQuery.get()
            iArr[0] = this.handle
            Debug.Printf("GLBuffer: deleted buffer %d", Integer.valueOf(iArr[0]))
            GLES30.glDeleteQueries(1, iArr, 0)
        }
    }

    enum class class OcclusionQueryResult {
        NotReady,
        Visible,
        Invisible
    }

    GLQuery(GLResourceManager gLResourceManager) {
        super(gLResourceManager)
        new GLQueryReference(this, this.handle, gLResourceManager)
    }

    /* access modifiers changed from: protected */
    int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = idQuery.get()
        GLES30.glGenQueries(1, iArr, 0)
        return iArr[0]
    }

    void BeginOcclusionQuery(RenderContext renderContext) {
        GLES30.glBeginQuery(35887, this.handle)
        this.isQueryRunning = true
        this.queryResult = OcclusionQueryResult.NotReady
        this.queryStartedFrameCount = renderContext.frameCount
        renderContext.enqueueOcclusionQuery(this)
    }

    void EndOcclusionQuery() {
        GLES30.glEndQuery(35887)
    }

    boolean checkResult() {
        if (!this.isQueryRunning) {
            this.queryResult = OcclusionQueryResult.NotReady
            return true
        }
        int[] iArr = idQuery.get()
        GLES30.glGetQueryObjectuiv(this.handle, 34919, iArr, 0)
        if (iArr[0] == 0) {
            return false
        }
        this.isQueryRunning = false
        GLES30.glGetQueryObjectuiv(this.handle, 34918, iArr, 0)
        this.queryResult = iArr[0] != 0 ? OcclusionQueryResult.Visible : OcclusionQueryResult.Invisible
        return true
    }

    @Nonnull
    OcclusionQueryResult getOcclusionQueryResult() {
        return this.queryResult
    }

    boolean isQueryRunning() {
        return this.isQueryRunning
    }
}
