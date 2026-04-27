package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifier(applicableTo = Number.class)
public @interface Nonnegative {

    public static class Checker implements TypeQualifierValidator<Nonnegative> {
        public When forConstantValue(Nonnegative nonnegative, Object obj) {
            boolean z = true;
            boolean z2 = false;
            if (!(obj instanceof Number)) {
                return When.NEVER;
            }
            Number number = (Number) obj;
            if (number instanceof Long) {
                if (number.longValue() >= 0) {
                    z = false;
                }
            } else if (!(number instanceof Double)) {
                if (!(number instanceof Float)) {
                    if (number.intValue() < 0) {
                        z2 = true;
                    }
                    z = z2;
                } else if (number.floatValue() >= 0.0f) {
                    z = false;
                }
            } else if (number.doubleValue() >= 0.0d) {
                z = false;
            }
            return !z ? When.ALWAYS : When.NEVER;
        }
    }

    When when() default When.ALWAYS;
}
