// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.AccessibleObject;
import com.google.common.annotations.Beta;
import java.lang.reflect.GenericDeclaration;

@Beta
public abstract class Invokable<T, R> extends Element implements GenericDeclaration
{
     <M extends AccessibleObject & Member> Invokable(final M m) {
        super(m);
    }
    
    public static <T> Invokable<T, T> from(final Constructor<T> constructor) {
        return (Invokable<T, T>)new ConstructorInvokable((Constructor<?>)constructor);
    }
    
    public static Invokable<?, Object> from(final Method method) {
        return new MethodInvokable<Object>(method);
    }
    
    @Override
    public final Class<? super T> getDeclaringClass() {
        return (Class<? super T>)super.getDeclaringClass();
    }
    
    public final ImmutableList<TypeToken<? extends Throwable>> getExceptionTypes() {
        final ImmutableList.Builder<TypeToken<? extends Throwable>> builder = ImmutableList.builder();
        final Type[] genericExceptionTypes = this.getGenericExceptionTypes();
        for (int length = genericExceptionTypes.length, i = 0; i < length; ++i) {
            builder.add((TypeToken<? extends Throwable>)TypeToken.of(genericExceptionTypes[i]));
        }
        return builder.build();
    }
    
    abstract Type[] getGenericExceptionTypes();
    
    abstract Type[] getGenericParameterTypes();
    
    abstract Type getGenericReturnType();
    
    @Override
    public TypeToken<T> getOwnerType() {
        return TypeToken.of(this.getDeclaringClass());
    }
    
    abstract Annotation[][] getParameterAnnotations();
    
    public final ImmutableList<Parameter> getParameters() {
        final Type[] genericParameterTypes = this.getGenericParameterTypes();
        final Annotation[][] parameterAnnotations = this.getParameterAnnotations();
        final ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
        for (int i = 0; i < genericParameterTypes.length; ++i) {
            builder.add(new Parameter(this, i, TypeToken.of(genericParameterTypes[i]), parameterAnnotations[i]));
        }
        return builder.build();
    }
    
    public final TypeToken<? extends R> getReturnType() {
        return (TypeToken<? extends R>)TypeToken.of(this.getGenericReturnType());
    }
    
    public final R invoke(@Nullable final T t, final Object... array) throws InvocationTargetException, IllegalAccessException {
        return (R)this.invokeInternal(t, Preconditions.checkNotNull(array));
    }
    
    abstract Object invokeInternal(@Nullable final Object p0, final Object[] p1) throws InvocationTargetException, IllegalAccessException;
    
    public abstract boolean isOverridable();
    
    public abstract boolean isVarArgs();
    
    public final <R1 extends R> Invokable<T, R1> returning(final TypeToken<R1> obj) {
        if (obj.isSupertypeOf(this.getReturnType())) {
            return (Invokable<T, R1>)this;
        }
        throw new IllegalArgumentException("Invokable is known to return " + this.getReturnType() + ", not " + obj);
    }
    
    public final <R1 extends R> Invokable<T, R1> returning(final Class<R1> clazz) {
        return this.returning((TypeToken<R1>)TypeToken.of((Class<R1>)clazz));
    }
    
    static class ConstructorInvokable<T> extends Invokable<T, T>
    {
        final Constructor<?> constructor;
        
        ConstructorInvokable(final Constructor<?> constructor) {
            super(constructor);
            this.constructor = constructor;
        }
        
        private boolean mayNeedHiddenThis() {
            final boolean b = true;
            boolean b2 = false;
            final Class<?> declaringClass = this.constructor.getDeclaringClass();
            if (declaringClass.getEnclosingConstructor() != null) {
                return true;
            }
            final Method enclosingMethod = declaringClass.getEnclosingMethod();
            if (enclosingMethod == null) {
                if (declaringClass.getEnclosingClass() != null) {
                    final boolean b3 = b;
                    if (!Modifier.isStatic(declaringClass.getModifiers())) {
                        return b3;
                    }
                }
                return false;
            }
            if (!Modifier.isStatic(enclosingMethod.getModifiers())) {
                b2 = true;
            }
            return b2;
        }
        
        @Override
        Type[] getGenericExceptionTypes() {
            return this.constructor.getGenericExceptionTypes();
        }
        
        @Override
        Type[] getGenericParameterTypes() {
            final Type[] genericParameterTypes = this.constructor.getGenericParameterTypes();
            if (genericParameterTypes.length > 0 && this.mayNeedHiddenThis()) {
                final Class<?>[] parameterTypes = this.constructor.getParameterTypes();
                if (genericParameterTypes.length == parameterTypes.length && parameterTypes[0] == this.getDeclaringClass().getEnclosingClass()) {
                    return Arrays.copyOfRange(genericParameterTypes, 1, genericParameterTypes.length);
                }
            }
            return genericParameterTypes;
        }
        
        @Override
        Type getGenericReturnType() {
            final Class<? super T> declaringClass = this.getDeclaringClass();
            final TypeVariable<Class<? super T>>[] typeParameters = declaringClass.getTypeParameters();
            if (typeParameters.length <= 0) {
                return declaringClass;
            }
            return Types.newParameterizedType(declaringClass, (Type[])typeParameters);
        }
        
        @Override
        final Annotation[][] getParameterAnnotations() {
            return this.constructor.getParameterAnnotations();
        }
        
        @Override
        public final TypeVariable<?>[] getTypeParameters() {
            final TypeVariable<Class<? super T>>[] typeParameters = this.getDeclaringClass().getTypeParameters();
            final TypeVariable<Constructor<?>>[] typeParameters2 = this.constructor.getTypeParameters();
            final TypeVariable[] array = new TypeVariable[typeParameters.length + typeParameters2.length];
            System.arraycopy(typeParameters, 0, array, 0, typeParameters.length);
            System.arraycopy(typeParameters2, 0, array, typeParameters.length, typeParameters2.length);
            return array;
        }
        
        @Override
        final Object invokeInternal(@Nullable Object instance, final Object[] initargs) throws InvocationTargetException, IllegalAccessException {
            try {
                instance = this.constructor.newInstance(initargs);
                return instance;
            }
            catch (final InstantiationException cause) {
                throw new RuntimeException(this.constructor + " failed.", cause);
            }
        }
        
        @Override
        public final boolean isOverridable() {
            return false;
        }
        
        @Override
        public final boolean isVarArgs() {
            return this.constructor.isVarArgs();
        }
    }
    
    static class MethodInvokable<T> extends Invokable<T, Object>
    {
        final Method method;
        
        MethodInvokable(final Method method) {
            super(method);
            this.method = method;
        }
        
        @Override
        Type[] getGenericExceptionTypes() {
            return this.method.getGenericExceptionTypes();
        }
        
        @Override
        Type[] getGenericParameterTypes() {
            return this.method.getGenericParameterTypes();
        }
        
        @Override
        Type getGenericReturnType() {
            return this.method.getGenericReturnType();
        }
        
        @Override
        final Annotation[][] getParameterAnnotations() {
            return this.method.getParameterAnnotations();
        }
        
        @Override
        public final TypeVariable<?>[] getTypeParameters() {
            return this.method.getTypeParameters();
        }
        
        @Override
        final Object invokeInternal(@Nullable final Object obj, final Object[] args) throws InvocationTargetException, IllegalAccessException {
            return this.method.invoke(obj, args);
        }
        
        @Override
        public final boolean isOverridable() {
            final boolean b = false;
            boolean b2;
            if (this.isFinal()) {
                b2 = b;
            }
            else {
                b2 = b;
                if (!this.isPrivate()) {
                    b2 = b;
                    if (!this.isStatic()) {
                        b2 = b;
                        if (!Modifier.isFinal(this.getDeclaringClass().getModifiers())) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
        
        @Override
        public final boolean isVarArgs() {
            return this.method.isVarArgs();
        }
    }
}
