package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
@TypeQualifier(applicableTo = String.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
public @interface MatchesPattern {

    /* loaded from: classes.dex */
    public static class Checker implements TypeQualifierValidator<MatchesPattern> {
        @Override // javax.annotation.meta.TypeQualifierValidator
        public When forConstantValue(MatchesPattern matchesPattern, Object obj) {
            return !Pattern.compile(matchesPattern.value(), matchesPattern.flags()).matcher((String) obj).matches() ? When.NEVER : When.ALWAYS;
        }
    }

    int flags() default 0;

    @RegEx
    String value();
}
