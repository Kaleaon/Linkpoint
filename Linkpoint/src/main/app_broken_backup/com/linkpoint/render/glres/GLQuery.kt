package com.linkpoint.render.glres

import android.annotation.TargetApi
import android.opengl.GLES30
import com.linkpoint.Debug
import com.linkpoint.render.RenderContext

/**
 * OpenGL occlusion query wrapper
 * Manages GPU-side visibility testing
 */
@TargetApi(18)
class GLQuery(
    resourceManager: GLResourceManager
) : GLResource(resourceManager) {
    
    private val MIN_OCCLUSION_QUERY_FRAMES = 0
    
    private val idQuery = ThreadLocal.withInitial { IntArray(1) }
    
    private var isQueryRunning = false
    private var queryResult = OcclusionQueryResult.NotReady
    private var queryStartedFrameCount = 0

    /**
     * GL resource reference for cleanup
     */
    private inner class GLQueryReference(
        resource: GLResource,
        handle: Int,
        manager: GLResourceManager
    ) : GLResourceManager.GLResourceReference(resource, handle, manager) {
        
        override fun GLFree() {
            val arr = idQuery.get()
            arr[0] = handle
            Debug.Printf("GLQuery: deleted query %d", arr[0])
            GLES30.glDeleteQueries(1, arr, 0)
        }
    }

    /**
     * Occlusion query result states
     */
    enum class OcclusionQueryResult {
        NotReady,
        Visible,
        Invisible
    }

    init {
        GLQueryReference(this, handle, resourceManager)
    }

    /**
     * Allocate GL query object
     */
    override fun Allocate(manager: GLResourceManager): Int {
        val arr = idQuery.get()
        GLES30.glGenQueries(1, arr, 0)
        return arr[0]
    }

    /**
     * Begin occlusion query
     */
    fun BeginOcclusionQuery(renderContext: RenderContext) {
        GLES30.glBeginQuery(GLES30.GL_ANY_SAMPLES_PASSED, handle)
        isQueryRunning = true
        queryResult = OcclusionQueryResult.NotReady
        queryStartedFrameCount = renderContext.frameCount
        renderContext.enqueueOcclusionQuery(this)
    }

    /**
     * End occlusion query
     */
    fun EndOcclusionQuery() {
        GLES30.glEndQuery(GLES30.GL_ANY_SAMPLES_PASSED)
    }

    /**
     * Check if query result is ready and retrieve it
     * @return true if result is available
     */
    fun checkResult(): Boolean {
        if (!isQueryRunning) {
            queryResult = OcclusionQueryResult.NotReady
            return true
        }
        
        val arr = idQuery.get()
        GLES30.glGetQueryObjectuiv(handle, GLES30.GL_QUERY_RESULT_AVAILABLE, arr, 0)
        
        if (arr[0] == 0) {
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

    /**
     * Get the occlusion query result
     */
    fun getOcclusionQueryResult(): OcclusionQueryResult = queryResult

    /**
     * Check if query is currently running
     */
    fun isQueryRunning(): Boolean = isQueryRunning
}
