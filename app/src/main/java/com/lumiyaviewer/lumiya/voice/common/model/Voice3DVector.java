package com.lumiyaviewer.lumiya.voice.common.model;

import android.annotation.SuppressLint;
import android.os.Bundle;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Voice3DVector {
    public final float x;
    public final float y;
    public final float z;

    public Voice3DVector(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public Voice3DVector(Bundle bundle) {
        this.x = bundle.getFloat("x");
        this.y = bundle.getFloat("y");
        this.z = bundle.getFloat("z");
    }

    public static Voice3DVector fromLLCoords(float f, float f2, float f3) {
        return new Voice3DVector(f, f3, -f2);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putFloat("x", this.x);
        bundle.putFloat("y", this.y);
        bundle.putFloat("z", this.z);
        return bundle;
    }

    @SuppressLint({"DefaultLocale"})
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z)});
    }
}
