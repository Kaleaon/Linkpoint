package com.lumiyaviewer.lumiya.render.glres;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLResourceReference;
import javax.annotation.Nonnull;

@TargetApi(18)
public class GLQuery extends GLResource {
    private static final int MIN_OCCLUSION_QUERY_FRAMES = 0;
    private static ThreadLocal<int[]> idQuery = new ThreadLocal<int[]>() {
        protected int[] initialValue() {
            return new int[1];
        }
    };
    private boolean isQueryRunning = false;
    @Nonnull
    private OcclusionQueryResult queryResult = OcclusionQueryResult.NotReady;
    private int queryStartedFrameCount = 0;

    private static class GLQueryReference extends GLResourceReference {
        GLQueryReference(GLResource gLResource, int i, GLResourceManager gLResourceManager) {
            super(gLResource, i, gLResourceManager);
        }

        public void GLFree() {
            int[] iArr = (int[]) GLQuery.idQuery.get();
            iArr[0] = this.handle;
            Debug.Printf("GLBuffer: deleted buffer %d", Integer.valueOf(iArr[0]));
            GLES30.glDeleteQueries(1, iArr, 0);
        }
    }

    public enum OcclusionQueryResult {
        NotReady,
        Visible,
        Invisible
    }

    public GLQuery(GLResourceManager gLResourceManager) {
        super(gLResourceManager);
        GLQueryReference gLQueryReference = new GLQueryReference(this, this.handle, gLResourceManager);
    }

    protected int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = (int[]) idQuery.get();
        GLES30.glGenQueries(1, iArr, 0);
        return iArr[0];
    }

    public void BeginOcclusionQuery(RenderContext renderContext) {
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
        if (this.isQueryRunning) {
            int[] iArr = (int[]) idQuery.get();
            GLES30.glGetQueryObjectuiv(this.handle, 34919, iArr, 0);
            if (iArr[0] == 0) {
                return false;
            }
            this.isQueryRunning = false;
            GLES30.glGetQueryObjectuiv(this.handle, 34918, iArr, 0);
            this.queryResult = iArr[0] != 0 ? OcclusionQueryResult.Visible : OcclusionQueryResult.Invisible;
            return true;
        }
        this.queryResult = OcclusionQueryResult.NotReady;
        return true;
    }

    @Nonnull
    public OcclusionQueryResult getOcclusionQueryResult() {
        return this.queryResult;
    }

    public boolean isQueryRunning() {
        return this.isQueryRunning;
    }
}
