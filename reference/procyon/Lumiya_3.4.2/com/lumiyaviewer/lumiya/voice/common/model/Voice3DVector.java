// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.annotation.SuppressLint;
import android.os.Bundle;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Voice3DVector
{
    public final float x;
    public final float y;
    public final float z;
    
    public Voice3DVector(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Voice3DVector(final Bundle bundle) {
        this.x = bundle.getFloat("x");
        this.y = bundle.getFloat("y");
        this.z = bundle.getFloat("z");
    }
    
    public static Voice3DVector fromLLCoords(final float n, final float n2, final float n3) {
        return new Voice3DVector(n, n3, -n2);
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putFloat("x", this.x);
        bundle.putFloat("y", this.y);
        bundle.putFloat("z", this.z);
        return bundle;
    }
    
    @SuppressLint({ "DefaultLocale" })
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", this.x, this.y, this.z);
    }
}
