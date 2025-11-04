// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.DrawableRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD })
public @interface BindBitmap {
    @DrawableRes
    int value();
}
