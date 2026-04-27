package com.lumiyaviewer.lumiya.render.shaders;

import android.opengl.GLES20;

public class WaterProgram extends ShaderProgram {
    public int uAmplitude;
    public int uDirection;
    public int uFrequency;
    public int uMVPMatrix;
    public int uObjWorldMatrix;
    public int uPhase;
    public int uTime;
    public int vColor;
    public int vPosition;

    public WaterProgram() {
        super(Shader.WaterVertexShader, Shader.WaterFragmentShader);
    }

    protected void bindVariables() {
        this.vPosition = GLES20.glGetAttribLocation(this.handle, "vPosition");
        this.vColor = GLES20.glGetUniformLocation(this.handle, "vColor");
        this.uMVPMatrix = GLES20.glGetUniformLocation(this.handle, "uMVPMatrix");
        this.uObjWorldMatrix = GLES20.glGetUniformLocation(this.handle, "uObjWorldMatrix");
        this.uTime = GLES20.glGetUniformLocation(this.handle, "time");
        this.uFrequency = GLES20.glGetUniformLocation(this.handle, "frequency");
        this.uPhase = GLES20.glGetUniformLocation(this.handle, "phase");
        this.uAmplitude = GLES20.glGetUniformLocation(this.handle, "amplitude");
        this.uDirection = GLES20.glGetUniformLocation(this.handle, "direction");
    }
}
