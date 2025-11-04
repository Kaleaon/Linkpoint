// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class PrimMath
{
    public static final float F_DEG_TO_RAD = 0.017453292f;
    public static final float F_PI = 3.1415927f;
    
    public static float lerp(final float n, final float n2, final float n3) {
        return (n2 - n) * n3 + n;
    }
    
    public static float[] lookAt(LLVector3 sub, LLVector3 llVector3, final LLVector3 cross) {
        final float[] array = new float[16];
        sub = LLVector3.sub(llVector3, sub);
        sub.normVec();
        llVector3 = new LLVector3(sub);
        llVector3.setCross(cross);
        array[0] = llVector3.x;
        array[4] = llVector3.y;
        array[8] = llVector3.z;
        array[1] = cross.x;
        array[5] = cross.y;
        array[9] = cross.z;
        array[2] = -sub.x;
        array[6] = -sub.y;
        array[10] = -sub.z;
        array[15] = 1.0f;
        return array;
    }
}
