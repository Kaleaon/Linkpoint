package com.lumiyaviewer.lumiya.render.picking;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class IntersectInfo {
    public final int faceID;
    public final boolean faceKnown;
    public final LLVector4 intersectPoint;
    public final float s;
    public final float t;
    public final float u;
    public final float v;

    public IntersectInfo(IntersectInfo intersectInfo, float[] fArr, int i) {
        this.intersectPoint = intersectInfo.intersectPoint;
        this.faceID = intersectInfo.faceID;
        this.s = intersectInfo.s;
        this.t = intersectInfo.t;
        this.faceKnown = intersectInfo.faceKnown;
        if (this.faceKnown) {
            float[] fArr2 = new float[8];
            fArr2[0] = this.s;
            fArr2[1] = this.t;
            fArr2[3] = 1.0f;
            Matrix.multiplyMV(fArr2, 4, fArr, i, fArr2, 0);
            this.u = fArr2[4];
            this.v = fArr2[5];
            return;
        }
        this.u = intersectInfo.u;
        this.v = intersectInfo.v;
    }

    public IntersectInfo(LLVector4 lLVector4) {
        this.intersectPoint = lLVector4;
        this.faceID = 0;
        this.u = 0.0f;
        this.v = 0.0f;
        this.s = 0.0f;
        this.t = 0.0f;
        this.faceKnown = false;
    }

    public IntersectInfo(LLVector4 lLVector4, int i, float f, float f2) {
        this.intersectPoint = lLVector4;
        this.faceID = i;
        this.u = f;
        this.v = f2;
        this.s = f;
        this.t = f2;
        this.faceKnown = true;
    }
}
