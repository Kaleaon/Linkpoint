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

@ListenerClass(callbacks = Callback.class, remover = "removeOnPageChangeListener", setter = "addOnPageChangeListener", targetType = "android.support.v4.view.ViewPager", type = "android.support.v4.view.ViewPager.OnPageChangeListener")
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
public @interface OnPageChange {
    Callback callback() default Callback.PAGE_SELECTED;
    
    @IdRes
    int[] value() default { -1 };
    
    public enum Callback
    {
        @ListenerMethod(name = "onPageScrolled", parameters = { "int", "float", "int" })
        PAGE_SCROLLED, 
        @ListenerMethod(name = "onPageScrollStateChanged", parameters = { "int" })
        PAGE_SCROLL_STATE_CHANGED, 
        @ListenerMethod(name = "onPageSelected", parameters = { "int" })
        PAGE_SELECTED;
    }
}
