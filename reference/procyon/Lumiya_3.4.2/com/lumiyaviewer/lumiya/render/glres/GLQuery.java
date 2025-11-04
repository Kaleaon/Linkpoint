// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import android.opengl.GLES30;
import javax.annotation.Nonnull;
import android.annotation.TargetApi;

@TargetApi(18)
public class GLQuery extends GLResource
{
    private static final int MIN_OCCLUSION_QUERY_FRAMES = 0;
    private static ThreadLocal<int[]> idQuery;
    private boolean isQueryRunning;
    @Nonnull
    private OcclusionQueryResult queryResult;
    private int queryStartedFrameCount;
    
    static {
        GLQuery.idQuery = new ThreadLocal<int[]>() {
            @Override
            protected int[] initialValue() {
                return new int[1];
            }
        };
    }
    
    public GLQuery(final GLResourceManager glResourceManager) {
        super(glResourceManager);
        this.isQueryRunning = false;
        this.queryResult = OcclusionQueryResult.NotReady;
        this.queryStartedFrameCount = 0;
        new GLQueryReference(this, this.handle, glResourceManager);
    }
    
    @Override
    protected int Allocate(final GLResourceManager glResourceManager) {
        final int[] array = GLQuery.idQuery.get();
        GLES30.glGenQueries(1, array, 0);
        return array[0];
    }
    
    public void BeginOcclusionQuery(final RenderContext renderContext) {
        GLES30.glBeginQuery(35887, this.handle);
        this.isQueryRunning = true;
        this.queryResult = OcclusionQueryResult.NotReady;
        this.queryStartedFrameCount = renderContext.frameCount;
        renderContext.enqueueOcclusionQuery(this);
    }
    
    public void EndOcclusionQuery() {
        GLES30.glEndQuery(35887);
    }
    
    public boolean checkResult() {
        if (!this.isQueryRunning) {
            this.queryResult = OcclusionQueryResult.NotReady;
            return true;
        }
        final int[] array = GLQuery.idQuery.get();
        GLES30.glGetQueryObjectuiv(this.handle, 34919, array, 0);
        if (array[0] != 0) {
            this.isQueryRunning = false;
            GLES30.glGetQueryObjectuiv(this.handle, 34918, array, 0);
            OcclusionQueryResult queryResult;
            if (array[0] != 0) {
                queryResult = OcclusionQueryResult.Visible;
            }
            else {
                queryResult = OcclusionQueryResult.Invisible;
            }
            this.queryResult = queryResult;
            return true;
        }
        return false;
    }
    
    @Nonnull
    public OcclusionQueryResult getOcclusionQueryResult() {
        return this.queryResult;
    }
    
    public boolean isQueryRunning() {
        return this.isQueryRunning;
    }
    
    private static class GLQueryReference extends GLResourceReference
    {
        GLQueryReference(final GLResource glResource, final int n, final GLResourceManager glResourceManager) {
            super(glResource, n, glResourceManager);
        }
        
        @Override
        public void GLFree() {
            final int[] array = GLQuery.idQuery.get();
            array[0] = this.handle;
            Debug.Printf("GLBuffer: deleted buffer %d", array[0]);
            GLES30.glDeleteQueries(1, array, 0);
        }
    }
    
    public enum OcclusionQueryResult
    {
        Invisible("Invisible", 2), 
        NotReady("NotReady", 0), 
        Visible("Visible", 1);
        
        private OcclusionQueryResult(final String name, final int ordinal) {
        }
    }
}
