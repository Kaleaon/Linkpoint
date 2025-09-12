// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLResource, GLResourceManager

public class GLQuery extends GLResource
{
    private static class GLQueryReference extends GLResourceManager.GLResourceReference
    {

        public void GLFree()
        {
            int ai[] = (int[])GLQuery._2D_get0().get();
            ai[0] = handle;
            Debug.Printf("GLBuffer: deleted buffer %d", new Object[] {
                Integer.valueOf(ai[0])
            });
            GLES30.glDeleteQueries(1, ai, 0);
        }

        GLQueryReference(GLResource glresource, int i, GLResourceManager glresourcemanager)
        {
            super(glresource, i, glresourcemanager);
        }
    }

    public static final class OcclusionQueryResult extends Enum
    {

        private static final OcclusionQueryResult $VALUES[];
        public static final OcclusionQueryResult Invisible;
        public static final OcclusionQueryResult NotReady;
        public static final OcclusionQueryResult Visible;

        public static OcclusionQueryResult valueOf(String s)
        {
            return (OcclusionQueryResult)Enum.valueOf(com/lumiyaviewer/lumiya/render/glres/GLQuery$OcclusionQueryResult, s);
        }

        public static OcclusionQueryResult[] values()
        {
            return $VALUES;
        }

        static 
        {
            NotReady = new OcclusionQueryResult("NotReady", 0);
            Visible = new OcclusionQueryResult("Visible", 1);
            Invisible = new OcclusionQueryResult("Invisible", 2);
            $VALUES = (new OcclusionQueryResult[] {
                NotReady, Visible, Invisible
            });
        }

        private OcclusionQueryResult(String s, int i)
        {
            super(s, i);
        }
    }


    private static final int MIN_OCCLUSION_QUERY_FRAMES = 0;
    private static ThreadLocal idQuery = new ThreadLocal() {

        protected volatile Object initialValue()
        {
            return initialValue();
        }

        protected int[] initialValue()
        {
            return new int[1];
        }

    };
    private boolean isQueryRunning;
    private OcclusionQueryResult queryResult;
    private int queryStartedFrameCount;

    static ThreadLocal _2D_get0()
    {
        return idQuery;
    }

    public GLQuery(GLResourceManager glresourcemanager)
    {
        super(glresourcemanager);
        isQueryRunning = false;
        queryResult = OcclusionQueryResult.NotReady;
        queryStartedFrameCount = 0;
        new GLQueryReference(this, handle, glresourcemanager);
    }

    protected int Allocate(GLResourceManager glresourcemanager)
    {
        glresourcemanager = (int[])idQuery.get();
        GLES30.glGenQueries(1, glresourcemanager, 0);
        return glresourcemanager[0];
    }

    public void BeginOcclusionQuery(RenderContext rendercontext)
    {
        GLES30.glBeginQuery(35887, handle);
        isQueryRunning = true;
        queryResult = OcclusionQueryResult.NotReady;
        queryStartedFrameCount = rendercontext.frameCount;
        rendercontext.enqueueOcclusionQuery(this);
    }

    public void EndOcclusionQuery()
    {
        GLES30.glEndQuery(35887);
    }

    public boolean checkResult()
    {
        if (!isQueryRunning)
        {
            queryResult = OcclusionQueryResult.NotReady;
            return true;
        }
        int ai[] = (int[])idQuery.get();
        GLES30.glGetQueryObjectuiv(handle, 34919, ai, 0);
        if (ai[0] != 0)
        {
            isQueryRunning = false;
            GLES30.glGetQueryObjectuiv(handle, 34918, ai, 0);
            OcclusionQueryResult occlusionqueryresult;
            if (ai[0] != 0)
            {
                occlusionqueryresult = OcclusionQueryResult.Visible;
            } else
            {
                occlusionqueryresult = OcclusionQueryResult.Invisible;
            }
            queryResult = occlusionqueryresult;
            return true;
        } else
        {
            return false;
        }
    }

    public OcclusionQueryResult getOcclusionQueryResult()
    {
        return queryResult;
    }

    public boolean isQueryRunning()
    {
        return isQueryRunning;
    }

}
