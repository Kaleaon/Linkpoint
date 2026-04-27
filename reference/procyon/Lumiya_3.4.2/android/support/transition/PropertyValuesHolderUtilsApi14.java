// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class PropertyValuesHolderUtilsApi14 implements PropertyValuesHolderUtilsImpl
{
    @Override
    public PropertyValuesHolder ofPointF(final Property<?, PointF> property, final Path path) {
        return PropertyValuesHolder.ofFloat((Property)new PathProperty<Object>(property, path), new float[] { 0.0f, 1.0f });
    }
}
