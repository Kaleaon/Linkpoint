// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.buffers:
//            GLBuffer

public class GLLoadableBuffer
    implements GLCleanable
{

    private GLBuffer glBuffer;
    private final DirectByteBuffer rawBuffer;

    public GLLoadableBuffer(DirectByteBuffer directbytebuffer)
    {
        glBuffer = null;
        directbytebuffer.position(0);
        rawBuffer = directbytebuffer;
    }

    public final void Bind(RenderContext rendercontext, int i, int j, int k, int l, int i1)
    {
        if (rendercontext.useVBO)
        {
            if (glBuffer == null)
            {
                rendercontext.KeepBuffer(rawBuffer);
                glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
                rendercontext.glBindArrayBuffer(glBuffer.handle);
                rendercontext.glBufferArrayData(rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), false);
                rendercontext.glResourceManager.addCleanable(this);
            } else
            {
                rendercontext.glBindArrayBuffer(glBuffer.handle);
            }
            GLES10.glEnableClientState(i);
            switch (i)
            {
            case 32886: 
            case 32887: 
            default:
                return;

            case 32884: 
                GLES11.glVertexPointer(j, k, l, i1);
                return;

            case 32885: 
                GLES11.glNormalPointer(k, l, i1);
                return;

            case 32888: 
                GLES11.glTexCoordPointer(j, k, l, i1);
                break;
            }
            return;
        }
        rendercontext.KeepBuffer(rawBuffer);
        GLES10.glEnableClientState(i);
        switch (i)
        {
        case 32886: 
        case 32887: 
        default:
            return;

        case 32884: 
            GLES10.glVertexPointer(j, k, l, rawBuffer.positionFloat(i1));
            return;

        case 32885: 
            GLES10.glNormalPointer(k, l, rawBuffer.positionFloat(i1));
            return;

        case 32888: 
            GLES10.glTexCoordPointer(j, k, l, rawBuffer.positionFloat(i1));
            break;
        }
    }

    public final void Bind20(RenderContext rendercontext, int i, int j, int k, int l, int i1)
    {
        if (glBuffer == null)
        {
            rendercontext.KeepBuffer(rawBuffer);
            glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
            GLES20.glBindBuffer(34962, glBuffer.handle);
            GLES20.glBufferData(34962, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), 35044);
            rendercontext.glResourceManager.addCleanable(this);
        } else
        {
            GLES20.glBindBuffer(34962, glBuffer.handle);
        }
        GLES20.glEnableVertexAttribArray(i);
        GLES20.glVertexAttribPointer(i, j, k, false, l, i1);
    }

    public final void Bind30Integer(RenderContext rendercontext, int i, int j, int k, int l, int i1)
    {
        if (glBuffer == null)
        {
            rendercontext.KeepBuffer(rawBuffer);
            glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
            GLES20.glBindBuffer(34962, glBuffer.handle);
            GLES20.glBufferData(34962, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), 35044);
            rendercontext.glResourceManager.addCleanable(this);
        } else
        {
            GLES20.glBindBuffer(34962, glBuffer.handle);
        }
        GLES30.glEnableVertexAttribArray(i);
        GLES30.glVertexAttribIPointer(i, j, k, l, i1);
    }

    public final void BindElements(RenderContext rendercontext)
    {
label0:
        {
            if (rendercontext.useVBO)
            {
                if (glBuffer != null)
                {
                    break label0;
                }
                rendercontext.KeepBuffer(rawBuffer);
                glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
                rendercontext.glBindElementArrayBuffer(glBuffer.handle);
                rendercontext.glBufferElementArrayData(rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), false);
                rendercontext.glResourceManager.addCleanable(this);
            }
            return;
        }
        rendercontext.glBindElementArrayBuffer(glBuffer.handle);
    }

    public final void BindElements20(RenderContext rendercontext)
    {
        if (glBuffer == null)
        {
            rendercontext.KeepBuffer(rawBuffer);
            glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
            GLES20.glBindBuffer(34963, glBuffer.handle);
            GLES20.glBufferData(34963, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), 35044);
            rendercontext.glResourceManager.addCleanable(this);
            return;
        } else
        {
            GLES20.glBindBuffer(34963, glBuffer.handle);
            return;
        }
    }

    public final void BindUniform(RenderContext rendercontext, int i)
    {
        boolean flag = false;
        if (glBuffer == null)
        {
            glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
            rendercontext.glResourceManager.addCleanable(this);
            flag = true;
        }
        GLES30.glBindBufferBase(35345, i, glBuffer.handle);
        if (flag)
        {
            GLES20.glBufferData(35345, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), 35044);
        }
    }

    public final void BindUniformDynamic(RenderContext rendercontext, int i, boolean flag)
    {
        boolean flag1;
        if ((glBuffer == null || flag) && glBuffer == null)
        {
            glBuffer = new GLBuffer(rendercontext.glResourceManager, rawBuffer);
            rendercontext.glResourceManager.addCleanable(this);
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        GLES30.glBindBufferBase(35345, i, glBuffer.handle);
        if (flag1)
        {
            GLES20.glBufferData(35345, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), 35048);
        } else
        if (flag)
        {
            GLES20.glBufferSubData(35345, 0, rawBuffer.getCapacity(), rawBuffer.asByteBuffer());
            return;
        }
    }

    public final void DrawElements(RenderContext rendercontext, int i, int j, int k, int l)
    {
        if (rendercontext.useVBO)
        {
            GLES11.glDrawElements(i, j, k, l);
            return;
        } else
        {
            GLES10.glDrawElements(i, j, 5123, rawBuffer.position(l));
            return;
        }
    }

    public final void DrawElements20(int i, int j, int k, int l)
    {
        GLES20.glDrawElements(i, j, k, l);
    }

    public void GLCleanup()
    {
        glBuffer = null;
    }

    public final void Reload(RenderContext rendercontext)
    {
        if (rendercontext.useVBO && glBuffer != null)
        {
            rendercontext.KeepBuffer(rawBuffer);
            rendercontext.glBindArrayBuffer(glBuffer.handle);
            rendercontext.glBufferArrayData(rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), false);
        }
    }

    public float getFloat(int i)
    {
        return rawBuffer.getFloat(i);
    }

    public DirectByteBuffer getRawBuffer()
    {
        return rawBuffer;
    }

    public int getShort(int i)
    {
        return rawBuffer.getShort(i);
    }
}
