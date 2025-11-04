// 
// Decompiled by Procyon v0.6.0
// 

package android.arch.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface OnLifecycleEvent {
    Lifecycle.Event value();
}
