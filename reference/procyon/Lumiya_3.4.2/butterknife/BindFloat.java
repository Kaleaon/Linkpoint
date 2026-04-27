// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.DimenRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/butterknife/BindFloat.java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/butterknife/BindFloat.java
public @interface BindFloat {
    @DimenRes
    int value();
}
