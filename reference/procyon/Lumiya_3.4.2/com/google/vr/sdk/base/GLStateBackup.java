// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import java.util.Iterator;
import android.opengl.GLES20;
import java.util.ArrayList;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class GLStateBackup
{
    private IntBuffer arrayBufferBinding;
    private FloatBuffer clearColor;
    private boolean cullFaceEnabled;
    private boolean depthTestEnabled;
    private IntBuffer elementArrayBufferBinding;
    private IntBuffer scissorBox;
    private boolean scissorTestEnabled;
    private IntBuffer shaderProgram;
    private IntBuffer texture2dId;
    private IntBuffer textureUnit;
    private ArrayList<VertexAttributeState> vertexAttributes;
    private IntBuffer viewport;
    
    GLStateBackup() {
        this.viewport = IntBuffer.allocate(4);
        this.texture2dId = IntBuffer.allocate(1);
        this.textureUnit = IntBuffer.allocate(1);
        this.scissorBox = IntBuffer.allocate(4);
        this.shaderProgram = IntBuffer.allocate(1);
        this.arrayBufferBinding = IntBuffer.allocate(1);
        this.elementArrayBufferBinding = IntBuffer.allocate(1);
        this.clearColor = FloatBuffer.allocate(4);
        this.vertexAttributes = new ArrayList<VertexAttributeState>();
    }
    
    void addTrackedVertexAttribute(final int n) {
        this.vertexAttributes.add(new VertexAttributeState(n));
    }
    
    void clearTrackedVertexAttributes() {
        this.vertexAttributes.clear();
    }
    
    void readFromGL() {
        GLES20.glGetIntegerv(2978, this.viewport);
        this.cullFaceEnabled = GLES20.glIsEnabled(2884);
        this.scissorTestEnabled = GLES20.glIsEnabled(3089);
        this.depthTestEnabled = GLES20.glIsEnabled(2929);
        GLES20.glGetFloatv(3106, this.clearColor);
        GLES20.glGetIntegerv(35725, this.shaderProgram);
        GLES20.glGetIntegerv(3088, this.scissorBox);
        GLES20.glGetIntegerv(34016, this.textureUnit);
        GLES20.glGetIntegerv(32873, this.texture2dId);
        GLES20.glGetIntegerv(34964, this.arrayBufferBinding);
        GLES20.glGetIntegerv(34965, this.elementArrayBufferBinding);
        final Iterator<VertexAttributeState> iterator = this.vertexAttributes.iterator();
        while (iterator.hasNext()) {
            iterator.next().readFromGL();
        }
    }
    
    void writeToGL() {
        final Iterator<VertexAttributeState> iterator = this.vertexAttributes.iterator();
        while (iterator.hasNext()) {
            iterator.next().writeToGL();
        }
        GLES20.glBindBuffer(34962, this.arrayBufferBinding.array()[0]);
        GLES20.glBindBuffer(34963, this.elementArrayBufferBinding.array()[0]);
        GLES20.glBindTexture(3553, this.texture2dId.array()[0]);
        GLES20.glActiveTexture(this.textureUnit.array()[0]);
        GLES20.glScissor(this.scissorBox.array()[0], this.scissorBox.array()[1], this.scissorBox.array()[2], this.scissorBox.array()[3]);
        GLES20.glUseProgram(this.shaderProgram.array()[0]);
        GLES20.glClearColor(this.clearColor.array()[0], this.clearColor.array()[1], this.clearColor.array()[2], this.clearColor.array()[3]);
        if (!this.cullFaceEnabled) {
            GLES20.glDisable(2884);
        }
        else {
            GLES20.glEnable(2884);
        }
        if (!this.scissorTestEnabled) {
            GLES20.glDisable(3089);
        }
        else {
            GLES20.glEnable(3089);
        }
        if (!this.depthTestEnabled) {
            GLES20.glDisable(2929);
        }
        else {
            GLES20.glEnable(2929);
        }
        GLES20.glViewport(this.viewport.array()[0], this.viewport.array()[1], this.viewport.array()[2], this.viewport.array()[3]);
    }
    
    private class VertexAttributeState
    {
        private int attributeId;
        private IntBuffer enabled;
        
        VertexAttributeState(final GLStateBackup glStateBackup, final int attributeId) {
            this.enabled = IntBuffer.allocate(1);
            this.attributeId = attributeId;
        }
        
        void readFromGL() {
            GLES20.glGetVertexAttribiv(this.attributeId, 34338, this.enabled);
        }
        
        void writeToGL() {
            if (this.enabled.array()[0] != 0) {
                GLES20.glEnableVertexAttribArray(this.attributeId);
            }
            else {
                GLES20.glDisableVertexAttribArray(this.attributeId);
            }
        }
    }
}
