package com.lumiyaviewer.lumiya.render.glres

import android.annotation.TargetApi
import android.opengl.GLES30
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext

@TargetApi(18)
class GLQuery(
    resourceManager: GLResourceManager
) : GLResource(resourceManager) {
    
    private val idQuery = object : ThreadLocal<IntArray>() {
        override fun initialValue(): IntArray {
            return IntArray(1)
        }
    }
    
    private var isQueryRunning = false
    private var queryResult = OcclusionQueryResult.NotReady
    private var queryStartedFrameCount = 0

    private inner class GLQueryReference(
        resource: GLResource,
        handle: Int,
        manager: GLResourceManager
    ) : GLResourceManager.GLResourceReference(resource, handle, manager) {
        
        override fun GLFree() {
            val arr = idQuery.get()
            arr!![0] = handle
            Debug.Printf("GLQuery: deleted query %d", arr[0])
            GLES30.glDeleteQueries(1, arr, 0)
        }
    }

    enum class OcclusionQueryResult {
        NotReady,
        Visible,
        Invisible
    }

    init {
        GLQueryReference(this, handle, glResourceManager)
    }

    override fun Allocate(manager: GLResourceManager): Int {
        val arr = idQuery.get()
        GLES30.glGenQueries(1, arr, 0)
        return arr!![0]
    }

    fun BeginOcclusionQuery(renderContext: RenderContext) {
        GLES30.glBeginQuery(GLES30.GL_ANY_SAMPLES_PASSED, handle)
        isQueryRunning = true
        queryResult = OcclusionQueryResult.NotReady
        queryStartedFrameCount = renderContext.frameCount
        glResourceManager.enqueueOcclusionQuery(this)
    }

    fun EndOcclusionQuery() {
        GLES30.glEndQuery(GLES30.GL_ANY_SAMPLES_PASSED)
    }

    fun checkResult(): Boolean {
        if (!isQueryRunning) {
            queryResult = OcclusionQueryResult.NotReady
            return true
        }
        
        val arr = idQuery.get()
        GLES30.glGetQueryObjectuiv(handle, GLES30.GL_QUERY_RESULT_AVAILABLE, arr, 0)
        
        if (arr!![0] == 0) {
            return false
        }
        
        isQueryRunning = false
        GLES30.glGetQueryObjectuiv(handle, GLES30.GL_QUERY_RESULT, arr, 0)
        queryResult = if (arr[0] != 0) {
            OcclusionQueryResult.Visible
        } else {
            OcclusionQueryResult.Invisible
        }
        
        return true
    }

    fun getOcclusionQueryResult(): OcclusionQueryResult = queryResult

    fun isQueryRunning(): Boolean = isQueryRunning
}
