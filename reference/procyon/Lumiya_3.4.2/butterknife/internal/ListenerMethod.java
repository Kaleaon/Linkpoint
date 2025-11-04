// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ListenerMethod {
    String defaultReturn() default "null";
    
    String name();
    
    String[] parameters() default {};
    
    String returnType() default "void";
}
