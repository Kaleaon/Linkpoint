// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.DrawableRes;
import android.support.annotation.AttrRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD })
public @interface BindDrawable {
    @AttrRes
    int tint() default 0;
    
    @DrawableRes
    int value();
}
