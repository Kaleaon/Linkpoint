// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/com/google/gson/annotations/JsonAdapter.java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/com/google/gson/annotations/JsonAdapter.java
public @interface JsonAdapter {
    boolean nullSafe() default true;
    
    Class<?> value();
}
