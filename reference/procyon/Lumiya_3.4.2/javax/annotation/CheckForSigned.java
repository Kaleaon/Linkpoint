// 
// Decompiled by Procyon v0.6.0
// 

package javax.annotation;

import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
<<<<<<<< HEAD:lumiya_decompiled_source/javax/annotation/CheckForSigned.java
@Nonnegative(when = When.MAYBE)
@TypeQualifierNickname
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Nonnegative(when = When.MAYBE)
@TypeQualifierNickname
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/javax/annotation/CheckForSigned.java
public @interface CheckForSigned {
}
