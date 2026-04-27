package com.lumiyaviewer.lumiya.render.picking;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class GLRayTrace {

    public static class RayIntersectInfo {
        public final LLVector4 intersectPoint;
        public final float s;
        public final float t;

        RayIntersectInfo(LLVector4 lLVector4, float f, float f2) {
            this.intersectPoint = lLVector4;
            this.s = f;
            this.t = f2;
        }

        public String toString() {
            return "RayIntersectInfo{intersectPoint=" + this.intersectPoint + ", s=" + this.s + ", t=" + this.t + '}';
        }
    }

    public static float getIntersectionDepth(RenderContext renderContext, LLVector4 lLVector4, float[] fArr) {
        r4 = new float[4];
        float[] fArr2 = new float[8];
        r4[0] = lLVector4.x;
        r4[1] = lLVector4.y;
        r4[2] = lLVector4.z;
        r4[3] = 1.0f;
        Matrix.multiplyMV(fArr2, 0, fArr, 0, r4, 0);
        if (renderContext.hasGL20) {
            Matrix.multiplyMV(fArr2, 4, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), fArr2, 0);
        } else {
            Matrix.multiplyMV(fArr2, 4, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), fArr2, 0);
        }
        return fArr2[6];
    }

    public static RayIntersectInfo intersect_RayTriangle(LLVector3 lLVector3, LLVector3 lLVector32, LLVector3[] lLVector3Arr, int i) {
        LLVector3 sub = LLVector3.sub(lLVector3Arr[i + 1], lLVector3Arr[i + 0]);
        LLVector3 sub2 = LLVector3.sub(lLVector3Arr[i + 2], lLVector3Arr[i + 0]);
        LLVector3 cross = LLVector3.cross(sub, sub2);
        if (cross.isZero()) {
            return null;
        }
        LLVector3 sub3 = LLVector3.sub(lLVector32, lLVector3);
        float f = -cross.dot(LLVector3.sub(lLVector3, lLVector3Arr[i + 0]));
        float dot = cross.dot(sub3);
        if (Math.abs(dot) < 1.0E-7f) {
            return null;
        }
        dot = f / dot;
        if (((double) dot) < 0.0d) {
            return null;
        }
        LLVector3 lLVector33 = new LLVector3(sub3);
        lLVector33.mul(dot);
        lLVector33.add(lLVector3);
        float dot2 = sub.dot(sub);
        float dot3 = sub.dot(sub2);
        float dot4 = sub2.dot(sub2);
        LLVector3 sub4 = LLVector3.sub(lLVector33, lLVector3Arr[i + 0]);
        float dot5 = sub4.dot(sub);
        float dot6 = sub4.dot(sub2);
        float f2 = (dot3 * dot3) - (dot2 * dot4);
        if (Math.abs(f2) < 1.0E-7f) {
            return null;
        }
        dot4 = ((dot3 * dot6) - (dot4 * dot5)) / f2;
        if (((double) dot4) < 0.0d || ((double) dot4) > 1.0d) {
            return null;
        }
        dot5 = ((dot5 * dot3) - (dot6 * dot2)) / f2;
        return (((double) dot5) < 0.0d || ((double) (dot4 + dot5)) > 1.0d) ? null : new RayIntersectInfo(new LLVector4(lLVector33.x, lLVector33.y, lLVector33.z, dot), dot4, dot5);
    }
}
