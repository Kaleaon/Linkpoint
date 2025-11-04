// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.TypeConverter;
import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class PropertyValuesHolderUtilsApi21 implements PropertyValuesHolderUtilsImpl
{
    @Override
    public PropertyValuesHolder ofPointF(final Property<?, PointF> property, final Path path) {
        return PropertyValuesHolder.ofObject((Property)property, (TypeConverter)null, path);
    }
}
