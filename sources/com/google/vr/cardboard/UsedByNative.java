package com.google.vr.cardboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Deprecated
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface UsedByNative {
}
