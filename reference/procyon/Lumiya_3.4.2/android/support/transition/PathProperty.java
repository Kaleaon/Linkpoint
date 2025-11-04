// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PathMeasure;
import android.util.Property;

class PathProperty<T> extends Property<T, Float>
{
    private float mCurrentFraction;
    private final float mPathLength;
    private final PathMeasure mPathMeasure;
    private final PointF mPointF;
    private final float[] mPosition;
    private final Property<T, PointF> mProperty;
    
    PathProperty(final Property<T, PointF> mProperty, final Path path) {
        super((Class)Float.class, mProperty.getName());
        this.mPosition = new float[2];
        this.mPointF = new PointF();
        this.mProperty = mProperty;
        this.mPathMeasure = new PathMeasure(path, false);
        this.mPathLength = this.mPathMeasure.getLength();
    }
    
    public Float get(final T t) {
        return this.mCurrentFraction;
    }
    
    public void set(final T t, final Float n) {
        this.mCurrentFraction = n;
        this.mPathMeasure.getPosTan(this.mPathLength * n, this.mPosition, (float[])null);
        this.mPointF.x = this.mPosition[0];
        this.mPointF.y = this.mPosition[1];
        this.mProperty.set((Object)t, (Object)this.mPointF);
    }
}
