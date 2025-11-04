// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import android.opengl.GLES20;
import android.opengl.GLES11;
import android.opengl.GLES10;
import java.nio.Buffer;
import com.lumiyaviewer.lumiya.render.RenderContext;
import javax.annotation.Nonnull;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;

public class GLLoadableBuffer implements GLCleanable
{
    private GLBuffer glBuffer;
    @Nonnull
    private final DirectByteBuffer rawBuffer;
    
    public GLLoadableBuffer(@Nonnull final DirectByteBuffer rawBuffer) {
        this.glBuffer = null;
        rawBuffer.position(0);
        this.rawBuffer = rawBuffer;
    }
    
    public final void Bind(final RenderContext renderContext, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (renderContext.useVBO) {
            if (this.glBuffer == null) {
                renderContext.KeepBuffer(this.rawBuffer);
                this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
                renderContext.glBindArrayBuffer(this.glBuffer.handle);
                renderContext.glBufferArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false);
                renderContext.glResourceManager.addCleanable(this);
            }
            else {
                renderContext.glBindArrayBuffer(this.glBuffer.handle);
            }
            GLES10.glEnableClientState(n);
            switch (n) {
                case 32884: {
                    GLES11.glVertexPointer(n2, n3, n4, n5);
                    break;
                }
                case 32885: {
                    GLES11.glNormalPointer(n3, n4, n5);
                    break;
                }
                case 32888: {
                    GLES11.glTexCoordPointer(n2, n3, n4, n5);
                    break;
                }
            }
        }
        else {
            renderContext.KeepBuffer(this.rawBuffer);
            GLES10.glEnableClientState(n);
            switch (n) {
                case 32884: {
                    GLES10.glVertexPointer(n2, n3, n4, this.rawBuffer.positionFloat(n5));
                    break;
                }
                case 32885: {
                    GLES10.glNormalPointer(n3, n4, this.rawBuffer.positionFloat(n5));
                    break;
                }
                case 32888: {
                    GLES10.glTexCoordPointer(n2, n3, n4, this.rawBuffer.positionFloat(n5));
                    break;
                }
            }
        }
    }
    
    public final void Bind20(final RenderContext renderContext, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer);
            this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
            GLES20.glBindBuffer(34962, this.glBuffer.handle);
            GLES20.glBufferData(34962, this.rawBuffer.getCapacity(), (Buffer)this.rawBuffer.asByteBuffer(), 35044);
            renderContext.glResourceManager.addCleanable(this);
        }
        else {
            GLES20.glBindBuffer(34962, this.glBuffer.handle);
        }
        GLES20.glEnableVertexAttribArray(n);
        GLES20.glVertexAttribPointer(n, n2, n3, false, n4, n5);
    }
    
    @TargetApi(18)
    public final void Bind30Integer(final RenderContext renderContext, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer);
            this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
            GLES20.glBindBuffer(34962, this.glBuffer.handle);
            GLES20.glBufferData(34962, this.rawBuffer.getCapacity(), (Buffer)this.rawBuffer.asByteBuffer(), 35044);
            renderContext.glResourceManager.addCleanable(this);
        }
        else {
            GLES20.glBindBuffer(34962, this.glBuffer.handle);
        }
        GLES30.glEnableVertexAttribArray(n);
        GLES30.glVertexAttribIPointer(n, n2, n3, n4, n5);
    }
    
    public final void BindElements(final RenderContext renderContext) {
        if (renderContext.useVBO) {
            if (this.glBuffer == null) {
                renderContext.KeepBuffer(this.rawBuffer);
                this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
                renderContext.glBindElementArrayBuffer(this.glBuffer.handle);
                renderContext.glBufferElementArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false);
                renderContext.glResourceManager.addCleanable(this);
            }
            else {
                renderContext.glBindElementArrayBuffer(this.glBuffer.handle);
            }
        }
    }
    
    public final void BindElements20(final RenderContext renderContext) {
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer);
            this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
            GLES20.glBindBuffer(34963, this.glBuffer.handle);
            GLES20.glBufferData(34963, this.rawBuffer.getCapacity(), (Buffer)this.rawBuffer.asByteBuffer(), 35044);
            renderContext.glResourceManager.addCleanable(this);
        }
        else {
            GLES20.glBindBuffer(34963, this.glBuffer.handle);
        }
    }
    
    @TargetApi(18)
    public final void BindUniform(final RenderContext renderContext, final int n) {
        boolean b = false;
        if (this.glBuffer == null) {
            this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
            renderContext.glResourceManager.addCleanable(this);
            b = true;
        }
        GLES30.glBindBufferBase(35345, n, this.glBuffer.handle);
        if (b) {
            GLES20.glBufferData(35345, this.rawBuffer.getCapacity(), (Buffer)this.rawBuffer.asByteBuffer(), 35044);
        }
    }
    
    @TargetApi(18)
    public final void BindUniformDynamic(final RenderContext renderContext, final int n, final boolean b) {
        int n2;
        if ((this.glBuffer == null || b) && this.glBuffer == null) {
            this.glBuffer = new GLBuffer(renderContext.glResourceManager, this.rawBuffer);
            renderContext.glResourceManager.addCleanable(this);
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        GLES30.glBindBufferBase(35345, n, this.glBuffer.handle);
        if (n2 != 0) {
            GLES20.glBufferData(35345, this.rawBuffer.getCapacity(), (Buffer)this.rawBuffer.asByteBuffer(), 35048);
        }
        else if (b) {
            GLES20.glBufferSubData(35345, 0, this.rawBuffer.getCapacity(), (Buffer)this.rawBuffer.asByteBuffer());
        }
    }
    
    public final void DrawElements(final RenderContext renderContext, final int n, final int n2, final int n3, final int n4) {
        if (renderContext.useVBO) {
            GLES11.glDrawElements(n, n2, n3, n4);
        }
        else {
            GLES10.glDrawElements(n, n2, 5123, this.rawBuffer.position(n4));
        }
    }
    
    public final void DrawElements20(final int n, final int n2, final int n3, final int n4) {
        GLES20.glDrawElements(n, n2, n3, n4);
    }
    
    @Override
    public void GLCleanup() {
        this.glBuffer = null;
    }
    
    public final void Reload(final RenderContext renderContext) {
        if (renderContext.useVBO && this.glBuffer != null) {
            renderContext.KeepBuffer(this.rawBuffer);
            renderContext.glBindArrayBuffer(this.glBuffer.handle);
            renderContext.glBufferArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false);
        }
    }
    
    public float getFloat(final int n) {
        return this.rawBuffer.getFloat(n);
    }
    
    @Nonnull
    public DirectByteBuffer getRawBuffer() {
        return this.rawBuffer;
    }
    
    public int getShort(final int n) {
        return this.rawBuffer.getShort(n);
    }
}
