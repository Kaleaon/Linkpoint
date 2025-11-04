package com.lumiyaviewer.lumiya.render.glres;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import javax.annotation.Nonnull;

@TargetApi(18)
/* loaded from: classes.dex */
public class GLQuery extends GLResource {
    private static final int MIN_OCCLUSION_QUERY_FRAMES = 0;
    private static ThreadLocal<int[]> idQuery = new ThreadLocal<int[]>() { // from class: com.lumiyaviewer.lumiya.render.glres.GLQuery.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public int[] initialValue() {
            return new int[1];
        }
    };
    private boolean isQueryRunning;

    @Nonnull
    private OcclusionQueryResult queryResult;
    private int queryStartedFrameCount;

    /* loaded from: classes.dex */
    private static class GLQueryReference extends GLResourceManager.GLResourceReference {
        GLQueryReference(GLResource gLResource, int i, GLResourceManager gLResourceManager) {
            super(gLResource, i, gLResourceManager);
        }

        @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLGenericResourceReference
        public void GLFree() {
            int[] iArr = (int[]) GLQuery.idQuery.get();
            iArr[0] = this.handle;
            Debug.Printf("GLBuffer: deleted buffer %d", Integer.valueOf(iArr[0]));
            GLES30.glDeleteQueries(1, iArr, 0);
        }
    }

    /* loaded from: classes.dex */
    public enum OcclusionQueryResult {
        NotReady,
        Visible,
        Invisible;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static OcclusionQueryResult[] valuesCustom() {
            return values();
        }
    }

    public GLQuery(GLResourceManager gLResourceManager) {
        super(gLResourceManager);
        this.isQueryRunning = false;
        this.queryResult = OcclusionQueryResult.NotReady;
        this.queryStartedFrameCount = 0;
        new GLQueryReference(this, this.handle, gLResourceManager);
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLResource
    protected int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = idQuery.get();
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
        if (!this.isQueryRunning) {
            this.queryResult = OcclusionQueryResult.NotReady;
            return true;
        }
        int[] iArr = idQuery.get();
        GLES30.glGetQueryObjectuiv(this.handle, 34919, iArr, 0);
        if (iArr[0] == 0) {
            return false;
        }
        this.isQueryRunning = false;
        GLES30.glGetQueryObjectuiv(this.handle, 34918, iArr, 0);
        this.queryResult = iArr[0] != 0 ? OcclusionQueryResult.Visible : OcclusionQueryResult.Invisible;
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
