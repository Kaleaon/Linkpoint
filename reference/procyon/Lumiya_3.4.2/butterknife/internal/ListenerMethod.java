// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/butterknife/internal/ListenerMethod.java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/butterknife/internal/ListenerMethod.java
public @interface ListenerMethod {
    String defaultReturn() default "null";
    
    String name();
    
    String[] parameters() default {};
    
    String returnType() default "void";
}
