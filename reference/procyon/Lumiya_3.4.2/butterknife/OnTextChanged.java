// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import butterknife.internal.ListenerMethod;
import android.support.annotation.IdRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import butterknife.internal.ListenerClass;

@ListenerClass(callbacks = Callback.class, remover = "removeTextChangedListener", setter = "addTextChangedListener", targetType = "android.widget.TextView", type = "android.text.TextWatcher")
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
public @interface OnTextChanged {
    Callback callback() default Callback.TEXT_CHANGED;
    
    @IdRes
    int[] value() default { -1 };
    
    public enum Callback
    {
        @ListenerMethod(name = "afterTextChanged", parameters = { "android.text.Editable" })
        AFTER_TEXT_CHANGED, 
        @ListenerMethod(name = "beforeTextChanged", parameters = { "java.lang.CharSequence", "int", "int", "int" })
        BEFORE_TEXT_CHANGED, 
        @ListenerMethod(name = "onTextChanged", parameters = { "java.lang.CharSequence", "int", "int", "int" })
        TEXT_CHANGED;
    }
}
