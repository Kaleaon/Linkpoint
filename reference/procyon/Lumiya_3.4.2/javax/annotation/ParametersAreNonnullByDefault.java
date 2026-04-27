// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import java.lang.annotation.ElementType;
import javax.annotation.meta.TypeQualifierDefault;
<<<<<<<< HEAD:lumiya_decompiled_source/javax/annotation/ParametersAreNonnullByDefault.java
@TypeQualifierDefault({ElementType.PARAMETER})
@Nonnull
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnull
@TypeQualifierDefault({ ElementType.PARAMETER })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/javax/annotation/ParametersAreNonnullByDefault.java
public @interface ParametersAreNonnullByDefault {
}
