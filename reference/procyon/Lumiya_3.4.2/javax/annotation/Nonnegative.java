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
@TypeQualifier(applicableTo = Number.class)
public @interface Nonnegative {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<Nonnegative>
    {
        @Override
        public When forConstantValue(final Nonnegative nonnegative, final Object o) {
            int n = 1;
            final int n2 = 0;
            if (!(o instanceof Number)) {
                return When.NEVER;
            }
            final Number n3 = (Number)o;
            if (!(n3 instanceof Long)) {
                if (!(n3 instanceof Double)) {
                    if (!(n3 instanceof Float)) {
                        if (n3.intValue() >= 0) {
                            n = n2;
                        }
                        else {
                            n = 1;
                        }
                    }
                    else if (n3.floatValue() >= 0.0f) {
                        n = 0;
                    }
                }
                else if (n3.doubleValue() >= 0.0) {
                    n = 0;
                }
            }
            else {
                int n4;
                if (n3.longValue() >= 0L) {
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                if (n4 != 0) {
                    n = 0;
                }
            }
            if (n == 0) {
                return When.ALWAYS;
            }
            return When.NEVER;
        }
    }
}
