// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import com.google.common.base.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeCapture<T>
{
    final Type capture() {
        final Type genericSuperclass = this.getClass().getGenericSuperclass();
        Preconditions.checkArgument(genericSuperclass instanceof ParameterizedType, "%s isn't parameterized", genericSuperclass);
        return ((ParameterizedType)genericSuperclass).getActualTypeArguments()[0];
    }
}
