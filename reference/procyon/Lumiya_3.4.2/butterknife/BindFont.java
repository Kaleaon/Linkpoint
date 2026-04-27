// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.RestrictTo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
<<<<<<<< HEAD:lumiya_decompiled_source/butterknife/BindFont.java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
/* loaded from: classes.dex */
public @interface BindFont {

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    /* loaded from: classes.dex */
    public @interface TypefaceStyle {
    }

========
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD })
public @interface BindFont {
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/butterknife/BindFont.java
    @TypefaceStyle
    int style() default 0;
    
    int value();
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY })
    public @interface TypefaceStyle {
    }
}
