// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import java.util.regex.Pattern;
import javax.annotation.meta.When;
import java.lang.annotation.Annotation;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifier(applicableTo = String.class)
public @interface MatchesPattern {
    int flags() default 0;
    
    @RegEx
    String value();
    
    public static class Checker implements TypeQualifierValidator<MatchesPattern>
    {
        @Override
        public When forConstantValue(final MatchesPattern matchesPattern, final Object o) {
            if (!Pattern.compile(matchesPattern.value(), matchesPattern.flags()).matcher((CharSequence)o).matches()) {
                return When.NEVER;
            }
            return When.ALWAYS;
        }
    }
}
