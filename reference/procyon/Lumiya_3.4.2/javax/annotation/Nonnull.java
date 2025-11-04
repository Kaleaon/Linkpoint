// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import java.lang.annotation.Annotation;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifier
public @interface Nonnull {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<Nonnull>
    {
        @Override
        public When forConstantValue(final Nonnull nonnull, final Object o) {
            if (o != null) {
                return When.ALWAYS;
            }
            return When.NEVER;
        }
    }
}
