// 
// Decompiled by Procyon v0.6.0
// 

package android.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface IntDef {
    boolean flag() default false;
    
    long[] value() default {};
}
