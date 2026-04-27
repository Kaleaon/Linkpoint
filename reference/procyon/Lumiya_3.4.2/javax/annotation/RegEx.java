// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import java.lang.annotation.Annotation;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Syntax("RegEx")
@TypeQualifierNickname
public @interface RegEx {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<RegEx>
    {
        @Override
        public When forConstantValue(final RegEx regEx, final Object o) {
            Label_0019: {
                if (!(o instanceof String)) {
                    break Label_0019;
                }
                try {
                    Pattern.compile((String)o);
                    return When.ALWAYS;
                    return When.NEVER;
                }
                catch (final PatternSyntaxException ex) {
                    return When.NEVER;
                }
            }
        }
    }
}
