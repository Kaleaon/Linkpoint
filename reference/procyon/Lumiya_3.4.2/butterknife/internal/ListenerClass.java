// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface ListenerClass {
    Class<? extends Enum<?>> callbacks() default NONE.class;
    
    ListenerMethod[] method() default {};
    
    String remover() default "";
    
    String setter();
    
    String targetType();
    
    String type();
    
    public enum NONE
    {
    }
}
