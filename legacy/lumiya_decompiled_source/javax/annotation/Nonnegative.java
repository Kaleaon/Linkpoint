package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
@TypeQualifier(applicableTo = Number.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
public @interface Nonnegative {

    /* loaded from: classes.dex */
    public static class Checker implements TypeQualifierValidator<Nonnegative> {
        @Override // javax.annotation.meta.TypeQualifierValidator
        public When forConstantValue(Nonnegative nonnegative, Object obj) {
            boolean z = true;
            if (obj instanceof Number) {
                Number number = (Number) obj;
                if (number instanceof Long) {
                    if (number.longValue() >= 0) {
                        z = false;
                    }
                } else if (number instanceof Double) {
                    if (number.doubleValue() >= 0.0d) {
                        z = false;
                    }
                } else if (!(number instanceof Float)) {
                    z = number.intValue() < 0;
                } else if (number.floatValue() >= 0.0f) {
                    z = false;
                }
                return !z ? When.ALWAYS : When.NEVER;
            }
            return When.NEVER;
        }
    }

    When when() default When.ALWAYS;
}
