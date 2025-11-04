// 
// Decompiled by Procyon v0.6.0
// 

package android.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
public @interface RequiresApi {
    @IntRange(from = 1L)
    int api() default 1;
    
    @IntRange(from = 1L)
    int value() default 1;
}
