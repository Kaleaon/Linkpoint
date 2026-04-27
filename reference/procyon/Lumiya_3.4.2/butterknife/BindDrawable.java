// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.DrawableRes;
import android.support.annotation.AttrRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/butterknife/BindDrawable.java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
/* loaded from: classes.dex */
========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD })
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/butterknife/BindDrawable.java
public @interface BindDrawable {
    @AttrRes
    int tint() default 0;
    
    @DrawableRes
    int value();
}
