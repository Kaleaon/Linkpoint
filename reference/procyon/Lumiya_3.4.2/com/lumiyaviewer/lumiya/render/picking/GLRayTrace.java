// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;
import com.lumiyaviewer.lumiya.render.RenderContext;

public class GLRayTrace
{
    public static float getIntersectionDepth(final RenderContext renderContext, final LLVector4 llVector4, final float[] array) {
        final float[] array2 = new float[8];
        Matrix.multiplyMV(array2, 0, array, 0, new float[] { llVector4.x, llVector4.y, llVector4.z, 1.0f }, 0);
        if (renderContext.hasGL20) {
            Matrix.multiplyMV(array2, 4, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), array2, 0);
        }
        else {
            Matrix.multiplyMV(array2, 4, renderContext.projectionMatrix.getMatrixData(), renderContext.projectionMatrix.getMatrixDataOffset(), array2, 0);
        }
        return array2[6];
    }
    
    public static RayIntersectInfo intersect_RayTriangle(LLVector3 sub, LLVector3 sub2, final LLVector3[] array, final int n) {
        final LLVector3 sub3 = LLVector3.sub(array[n + 1], array[n + 0]);
        final LLVector3 sub4 = LLVector3.sub(array[n + 2], array[n + 0]);
        final LLVector3 cross = LLVector3.cross(sub3, sub4);
        if (cross.isZero()) {
            return null;
        }
        sub2 = LLVector3.sub(sub2, sub);
        final float n2 = -cross.dot(LLVector3.sub(sub, array[n + 0]));
        final float dot = cross.dot(sub2);
        if (Math.abs(dot) < 1.0E-7f) {
            return null;
        }
        final float n3 = n2 / dot;
        if (n3 < 0.0) {
            return null;
        }
        sub2 = new LLVector3(sub2);
        sub2.mul(n3);
        sub2.add(sub);
        final float dot2 = sub3.dot(sub3);
        final float dot3 = sub3.dot(sub4);
        final float dot4 = sub4.dot(sub4);
        sub = LLVector3.sub(sub2, array[n + 0]);
        final float dot5 = sub.dot(sub3);
        final float dot6 = sub.dot(sub4);
        final float a = dot3 * dot3 - dot2 * dot4;
        if (Math.abs(a) < 1.0E-7f) {
            return null;
        }
        final float n4 = (dot3 * dot6 - dot4 * dot5) / a;
        if (n4 < 0.0 || n4 > 1.0) {
            return null;
        }
        final float n5 = (dot5 * dot3 - dot6 * dot2) / a;
        if (n5 < 0.0 || n4 + n5 > 1.0) {
            return null;
        }
        return new RayIntersectInfo(new LLVector4(sub2.x, sub2.y, sub2.z, n3), n4, n5);
    }
    
    public static class RayIntersectInfo
    {
        public final LLVector4 intersectPoint;
        public final float s;
        public final float t;
        
        RayIntersectInfo(final LLVector4 intersectPoint, final float s, final float t) {
            this.intersectPoint = intersectPoint;
            this.s = s;
            this.t = t;
        }
        
        @Override
        public String toString() {
            return "RayIntersectInfo{intersectPoint=" + this.intersectPoint + ", s=" + this.s + ", t=" + this.t + '}';
        }
    }
}
