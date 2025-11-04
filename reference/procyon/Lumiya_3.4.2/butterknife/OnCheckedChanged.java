// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.IdRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import butterknife.internal.ListenerMethod;
import butterknife.internal.ListenerClass;

@ListenerClass(method = { @ListenerMethod(name = "onCheckedChanged", parameters = { "android.widget.CompoundButton", "boolean" }) }, setter = "setOnCheckedChangeListener", targetType = "android.widget.CompoundButton", type = "android.widget.CompoundButton.OnCheckedChangeListener")
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
public @interface OnCheckedChanged {
    @IdRes
    int[] value() default { -1 };
}
