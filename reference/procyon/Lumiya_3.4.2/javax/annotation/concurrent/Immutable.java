// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/javax/annotation/concurrent/Immutable.java
@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.CLASS)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/javax/annotation/concurrent/Immutable.java
public @interface Immutable {
}
