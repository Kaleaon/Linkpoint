// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import com.google.common.collect.FluentIterable;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import com.google.common.collect.ImmutableList;
import com.google.common.annotations.Beta;
import java.lang.reflect.AnnotatedElement;

@Beta
public final class Parameter implements AnnotatedElement
{
    private final ImmutableList<Annotation> annotations;
    private final Invokable<?, ?> declaration;
    private final int position;
    private final TypeToken<?> type;
    
    Parameter(final Invokable<?, ?> declaration, final int position, final TypeToken<?> type, final Annotation[] array) {
        this.declaration = declaration;
        this.position = position;
        this.type = type;
        this.annotations = ImmutableList.copyOf(array);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof Parameter)) {
            return false;
        }
        final Parameter parameter = (Parameter)o;
        if (this.position == parameter.position && this.declaration.equals(parameter.declaration)) {
            b = true;
        }
        return b;
    }
    
    @Nullable
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> clazz) {
        Preconditions.checkNotNull(clazz);
        for (final Annotation obj : this.annotations) {
            if (clazz.isInstance(obj)) {
                return (A)clazz.cast(obj);
            }
        }
        return null;
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.getDeclaredAnnotations();
    }
    
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> clazz) {
        return (A[])this.getDeclaredAnnotationsByType((Class<Annotation>)clazz);
    }
    
    @Nullable
    @Override
    public <A extends Annotation> A getDeclaredAnnotation(final Class<A> clazz) {
        Preconditions.checkNotNull(clazz);
        return FluentIterable.from(this.annotations).filter(clazz).first().orNull();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.annotations.toArray(new Annotation[this.annotations.size()]);
    }
    
    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(final Class<A> clazz) {
        return FluentIterable.from(this.annotations).filter(clazz).toArray(clazz);
    }
    
    public Invokable<?, ?> getDeclaringInvokable() {
        return this.declaration;
    }
    
    public TypeToken<?> getType() {
        return this.type;
    }
    
    @Override
    public int hashCode() {
        return this.position;
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> clazz) {
        return this.getAnnotation(clazz) != null;
    }
    
    @Override
    public String toString() {
        return this.type + " arg" + this.position;
    }
}
