// 
// Decompiled by Procyon v0.6.0
// 

package android.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE })
public @interface RestrictTo {
    Scope[] value();
    
    public enum Scope
    {
        @Deprecated
        GROUP_ID, 
        LIBRARY, 
        LIBRARY_GROUP, 
        SUBCLASSES, 
        TESTS;
    }
}
