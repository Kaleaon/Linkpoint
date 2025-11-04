// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnegative(when = When.UNKNOWN)
@TypeQualifierNickname
public @interface Signed {
}
