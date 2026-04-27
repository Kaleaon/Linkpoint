// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/javax/annotation/meta/TypeQualifier.java
@Target({ElementType.ANNOTATION_TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/javax/annotation/meta/TypeQualifier.java
public @interface TypeQualifier {
    Class<?> applicableTo() default Object.class;
}
