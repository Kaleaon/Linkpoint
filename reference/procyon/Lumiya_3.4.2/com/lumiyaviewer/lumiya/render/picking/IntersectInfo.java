// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.picking;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class IntersectInfo
{
    public final int faceID;
    public final boolean faceKnown;
    public final LLVector4 intersectPoint;
    public final float s;
    public final float t;
    public final float u;
    public final float v;
    
    public IntersectInfo(final IntersectInfo intersectInfo, final float[] array, final int n) {
        this.intersectPoint = intersectInfo.intersectPoint;
        this.faceID = intersectInfo.faceID;
        this.s = intersectInfo.s;
        this.t = intersectInfo.t;
        this.faceKnown = intersectInfo.faceKnown;
        if (this.faceKnown) {
            final float[] array2 = { this.s, this.t, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f };
            Matrix.multiplyMV(array2, 4, array, n, array2, 0);
            this.u = array2[4];
            this.v = array2[5];
        }
        else {
            this.u = intersectInfo.u;
            this.v = intersectInfo.v;
        }
    }
    
    public IntersectInfo(final LLVector4 intersectPoint) {
        this.intersectPoint = intersectPoint;
        this.faceID = 0;
        this.u = 0.0f;
        this.v = 0.0f;
        this.s = 0.0f;
        this.t = 0.0f;
        this.faceKnown = false;
    }
    
    public IntersectInfo(final LLVector4 intersectPoint, final int faceID, final float n, final float n2) {
        this.intersectPoint = intersectPoint;
        this.faceID = faceID;
        this.u = n;
        this.v = n2;
        this.s = n;
        this.t = n2;
        this.faceKnown = true;
    }
}
