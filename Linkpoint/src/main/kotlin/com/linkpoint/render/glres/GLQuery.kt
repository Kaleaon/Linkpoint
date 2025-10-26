package com.linkpoint.render.glres

import android.annotation.TargetApi
import android.opengl.GLES30
import com.linkpoint.Debug
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLResourceManager
import javax.annotation.Nonnull

@TargetApi(18)
class GLQuery : GLResource() {
    private const val MIN_OCCLUSION_QUERY_FRAMES: Int = 0
    /* access modifiers changed from: private */
    @JvmStatic
    val idQuery: ThreadLocal<IntArray> = ThreadLocal<IntArray>() {
        /* access modifiers changed from: protected */
         public fun initialValue(): IntArray {
            return Int[1]
        }
    }
    private Boolean isQueryRunning = false
    private OcclusionQueryResult queryResult = OcclusionQueryResult.NotReady
    private Int queryStartedFrameCount = 0

    @JvmStatic
private class GLQueryReference : GLResourceManager().GLResourceReference {
        GLQueryReference(GLResource gLResource, Int i, GLResourceManager gLResourceManager) {
            super(gLResource, i, gLResourceManager)
        }

        fun GLFree() {
            val iArr: IntArray = (IntArray) GLQuery.idQuery.get()
            iArr[0] = this.handle
            Debug.Printf("GLBuffer: deleted buffer %d", Integer.valueOf(iArr[0]))
            GLES30.glDeleteQueries(1, iArr, 0)
        }
    }

    enum class OcclusionQueryResult {
        NotReady,
        Visible,
        Invisible
    }

    public GLQuery(GLResourceManager gLResourceManager) {
        super(gLResourceManager)
        GLQueryReference(this, this.handle, gLResourceManager)
    }

    /* access modifiers changed from: protected */
    public fun Allocate(gLResourceManager: GLResourceManager): Int {
        val iArr: IntArray = idQuery.get()
        GLES30.glGenQueries(1, iArr, 0)
        return iArr[0]
    }

    fun BeginOcclusionQuery(renderContext: RenderContext) {
        GLES30.glBeginQuery(35887, this.handle)
        this.isQueryRunning = true
        this.queryResult = OcclusionQueryResult.NotReady
        this.queryStartedFrameCount = renderContext.frameCount
        renderContext.enqueueOcclusionQuery(this)
    }

    fun EndOcclusionQuery() {
        GLES30.glEndQuery(35887)
    }

     public fun checkResult(): Boolean {
        if (!this.isQueryRunning) {
            this.queryResult = OcclusionQueryResult.NotReady
            return true
        }
        val iArr: IntArray = idQuery.get()
        GLES30.glGetQueryObjectuiv(this.handle, 34919, iArr, 0)
        if (iArr[0] == 0) {
            return false
        }
        this.isQueryRunning = false
        GLES30.glGetQueryObjectuiv(this.handle, 34918, iArr, 0)
        this.queryResult = iArr[0] != 0 ? OcclusionQueryResult.Visible : OcclusionQueryResult.Invisible
        return true
    }

     public fun getOcclusionQueryResult(): OcclusionQueryResult {
        return this.queryResult
    }

     public fun isQueryRunning(): Boolean {
        return this.isQueryRunning
    }
}
