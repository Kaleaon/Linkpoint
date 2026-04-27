// 
// Decompiled by Procyon v0.6.0
// 

package android.arch.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/android/arch/lifecycle/OnLifecycleEvent.java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/android/arch/lifecycle/OnLifecycleEvent.java
public @interface OnLifecycleEvent {
    Lifecycle.Event value();
}
