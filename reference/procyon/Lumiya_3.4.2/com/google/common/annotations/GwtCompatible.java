// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.METHOD })
@GwtCompatible
public @interface GwtCompatible {
    boolean emulated() default false;
    
    boolean serializable() default false;
}
