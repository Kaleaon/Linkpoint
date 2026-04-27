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

@ListenerClass(method = { @ListenerMethod(defaultReturn = "false", name = "onTouch", parameters = { "android.view.View", "android.view.MotionEvent" }, returnType = "boolean") }, setter = "setOnTouchListener", targetType = "android.view.View", type = "android.view.View.OnTouchListener")
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
public @interface OnTouch {
    @IdRes
    int[] value() default { -1 };
}
