// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import com.google.common.base.Preconditions;
import java.lang.reflect.TypeVariable;
import com.google.common.annotations.Beta;

@Beta
public abstract class TypeParameter<T> extends TypeCapture<T>
{
    final TypeVariable<?> typeVariable;
    
    protected TypeParameter() {
        final Type capture = this.capture();
        Preconditions.checkArgument(capture instanceof TypeVariable, "%s should be a type variable.", capture);
        this.typeVariable = (TypeVariable<?>)capture;
    }
    
    @Override
    public final boolean equals(@Nullable final Object o) {
        return o instanceof TypeParameter && this.typeVariable.equals(((TypeParameter)o).typeVariable);
    }
    
    @Override
    public final int hashCode() {
        return this.typeVariable.hashCode();
    }
    
    @Override
    public String toString() {
        return this.typeVariable.toString();
    }
}
