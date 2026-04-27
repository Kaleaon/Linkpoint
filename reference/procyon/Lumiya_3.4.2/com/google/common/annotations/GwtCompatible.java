// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/com/google/common/annotations/GwtCompatible.java
@Target({ElementType.TYPE, ElementType.METHOD})
@GwtCompatible
@Documented
@Retention(RetentionPolicy.CLASS)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.METHOD })
@GwtCompatible
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/com/google/common/annotations/GwtCompatible.java
public @interface GwtCompatible {
    boolean emulated() default false;
    
    boolean serializable() default false;
}
