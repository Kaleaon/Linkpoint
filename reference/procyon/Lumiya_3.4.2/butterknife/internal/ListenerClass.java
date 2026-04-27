// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/butterknife/internal/ListenerClass.java
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
public @interface ListenerClass {

    /* loaded from: classes.dex */
    public enum NONE {
    }

========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface ListenerClass {
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/butterknife/internal/ListenerClass.java
    Class<? extends Enum<?>> callbacks() default NONE.class;
    
    ListenerMethod[] method() default {};
    
    String remover() default "";
    
    String setter();
    
    String targetType();
    
    String type();
    
    public enum NONE
    {
    }
}
