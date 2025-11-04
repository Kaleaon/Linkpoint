// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import javax.annotation.meta.When;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifier
public @interface Untainted {
    When when() default When.ALWAYS;
}
