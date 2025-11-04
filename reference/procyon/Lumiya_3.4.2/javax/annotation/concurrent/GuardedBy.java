// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GuardedBy {
    String value();
}
