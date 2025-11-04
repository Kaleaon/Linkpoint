// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import com.google.common.collect.ForwardingSet;
import java.util.HashMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.Arrays;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import com.google.common.base.Joiner;
import java.lang.reflect.Constructor;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.primitives.Primitives;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Type;
import com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public abstract class TypeToken<T> extends TypeCapture<T> implements Serializable
{
    private final Type runtimeType;
    private transient TypeResolver typeResolver;
    
    protected TypeToken() {
        this.runtimeType = this.capture();
        Preconditions.checkState(!(this.runtimeType instanceof TypeVariable), "Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.", this.runtimeType);
    }
    
    protected TypeToken(final Class<?> clazz) {
        final Type capture = super.capture();
        if (!(capture instanceof Class)) {
            this.runtimeType = of(clazz).resolveType(capture).runtimeType;
        }
        else {
            this.runtimeType = capture;
        }
    }
    
    private TypeToken(final Type type) {
        this.runtimeType = Preconditions.checkNotNull(type);
    }
    
    private static Bounds any(final Type[] array) {
        return new Bounds(array, true);
    }
    
    @Nullable
    private TypeToken<? super T> boundAsSuperclass(final Type type) {
        final TypeToken<?> of = of(type);
        if (!of.getRawType().isInterface()) {
            return (TypeToken<? super T>)of;
        }
        return null;
    }
    
    private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(final Type[] array) {
        int i = 0;
        final ImmutableList.Builder<TypeToken> builder = ImmutableList.builder();
        while (i < array.length) {
            final TypeToken<?> of = of(array[i]);
            if (of.getRawType().isInterface()) {
                builder.add(of);
            }
            ++i;
        }
        return builder.build();
    }
    
    private static Bounds every(final Type[] array) {
        return new Bounds(array, false);
    }
    
    private TypeToken<? extends T> getArraySubtype(final Class<?> clazz) {
        return (TypeToken<? extends T>)of(newArrayClassOrGenericArrayType(this.getComponentType().getSubtype(clazz.getComponentType()).runtimeType));
    }
    
    private TypeToken<? super T> getArraySupertype(final Class<? super T> clazz) {
        return (TypeToken<? super T>)of(newArrayClassOrGenericArrayType(Preconditions.checkNotNull(this.getComponentType(), "%s isn't a super type of %s", clazz, this).getSupertype(clazz.getComponentType()).runtimeType));
    }
    
    private ImmutableSet<Class<? super T>> getRawTypes() {
        final ImmutableSet.Builder<Object> builder = ImmutableSet.builder();
        new TypeVisitor() {
            @Override
            void visitClass(final Class<?> clazz) {
                builder.add(clazz);
            }
            
            @Override
            void visitGenericArrayType(final GenericArrayType genericArrayType) {
                builder.add(Types.getArrayClass(TypeToken.of(genericArrayType.getGenericComponentType()).getRawType()));
            }
            
            @Override
            void visitParameterizedType(final ParameterizedType parameterizedType) {
                builder.add(parameterizedType.getRawType());
            }
            
            @Override
            void visitTypeVariable(final TypeVariable<?> typeVariable) {
                this.visit(typeVariable.getBounds());
            }
            
            @Override
            void visitWildcardType(final WildcardType wildcardType) {
                this.visit(wildcardType.getUpperBounds());
            }
        }.visit(this.runtimeType);
        return builder.build();
    }
    
    private TypeToken<? extends T> getSubtypeFromLowerBounds(final Class<?> obj, final Type[] array) {
        if (array.length <= 0) {
            throw new IllegalArgumentException(obj + " isn't a subclass of " + this);
        }
        return (TypeToken<? extends T>)of(array[0]).getSubtype(obj);
    }
    
    private TypeToken<? super T> getSupertypeFromUpperBounds(final Class<? super T> obj, final Type[] array) {
        for (int i = 0; i < array.length; ++i) {
            final TypeToken<?> of = of(array[i]);
            if (of.isSubtypeOf(obj)) {
                return (TypeToken<? super T>)of.getSupertype(obj);
            }
        }
        throw new IllegalArgumentException(obj + " isn't a super type of " + this);
    }
    
    private boolean is(final Type obj) {
        return this.runtimeType.equals(obj) || (obj instanceof WildcardType && every(((WildcardType)obj).getUpperBounds()).isSupertypeOf(this.runtimeType) && every(((WildcardType)obj).getLowerBounds()).isSubtypeOf(this.runtimeType));
    }
    
    private boolean isSubTypeOfArrayType(final GenericArrayType genericArrayType) {
        if (!(this.runtimeType instanceof Class)) {
            return this.runtimeType instanceof GenericArrayType && of(((GenericArrayType)this.runtimeType).getGenericComponentType()).isSubtypeOf(genericArrayType.getGenericComponentType());
        }
        final Class clazz = (Class)this.runtimeType;
        return clazz.isArray() && of(clazz.getComponentType()).isSubtypeOf(genericArrayType.getGenericComponentType());
    }
    
    private boolean isSubtypeOfParameterizedType(final ParameterizedType parameterizedType) {
        final Class<?> rawType = of(parameterizedType).getRawType();
        if (this.someRawTypeIsSubclassOf(rawType)) {
            final TypeVariable<Class<?>>[] typeParameters = rawType.getTypeParameters();
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < typeParameters.length; ++i) {
                if (!this.resolveType(typeParameters[i]).is(actualTypeArguments[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean isSuperTypeOfArray(final GenericArrayType genericArrayType) {
        if (!(this.runtimeType instanceof Class)) {
            return this.runtimeType instanceof GenericArrayType && of(genericArrayType.getGenericComponentType()).isSubtypeOf(((GenericArrayType)this.runtimeType).getGenericComponentType());
        }
        final Class clazz = (Class)this.runtimeType;
        if (clazz.isArray()) {
            return of(genericArrayType.getGenericComponentType()).isSubtypeOf(clazz.getComponentType());
        }
        return clazz.isAssignableFrom(Object[].class);
    }
    
    private boolean isWrapper() {
        return Primitives.allWrapperTypes().contains(this.runtimeType);
    }
    
    private static Type newArrayClassOrGenericArrayType(final Type type) {
        return Types.JavaVersion.JAVA7.newArrayType(type);
    }
    
    public static <T> TypeToken<T> of(final Class<T> clazz) {
        return new SimpleTypeToken<T>((Type)clazz);
    }
    
    public static TypeToken<?> of(final Type type) {
        return new SimpleTypeToken<Object>(type);
    }
    
    private static final Type replaceTypeVariablesWithWildcard(final Type newValue, final Class<?> clazz) {
        Preconditions.checkNotNull(clazz);
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(newValue);
        new TypeVisitor() {
            @Override
            void visitClass(final Class<?> clazz) {
            }
            
            @Override
            void visitGenericArrayType(final GenericArrayType genericArrayType) {
            }
            
            @Override
            void visitParameterizedType(final ParameterizedType parameterizedType) {
                final AtomicReference val$result = atomicReference;
                Type type;
                if (clazz.getEnclosingClass() != null) {
                    type = replaceTypeVariablesWithWildcard(parameterizedType.getOwnerType(), clazz.getEnclosingClass());
                }
                else {
                    type = parameterizedType.getOwnerType();
                }
                val$result.set(Types.newParameterizedTypeWithOwner(type, (Class<?>)parameterizedType.getRawType(), replaceTypeVariablesWithWildcard(parameterizedType.getActualTypeArguments(), clazz)));
            }
            
            @Override
            void visitTypeVariable(final TypeVariable<?> typeVariable) {
                if (typeVariable.getGenericDeclaration() == clazz) {
                    atomicReference.set(Types.subtypeOf(Object.class));
                }
            }
            
            @Override
            void visitWildcardType(final WildcardType wildcardType) {
            }
        }.visit(newValue);
        return (Type)atomicReference.get();
    }
    
    private static final Type[] replaceTypeVariablesWithWildcard(final Type[] array, final Class<?> clazz) {
        final Type[] array2 = new Type[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = replaceTypeVariablesWithWildcard(array[i], clazz);
        }
        return array2;
    }
    
    private Type[] resolveInPlace(final Type[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.resolveType(array[i]).getType();
        }
        return array;
    }
    
    private TypeToken<?> resolveSupertype(final Type type) {
        final TypeToken<?> resolveType = this.resolveType(type);
        resolveType.typeResolver = this.typeResolver;
        return resolveType;
    }
    
    private Type resolveTypeArgsForSubclass(final Class<?> clazz) {
        if (!(this.runtimeType instanceof Class)) {
            final TypeToken<?> genericType = toGenericType(clazz);
            return new TypeResolver().where(genericType.getSupertype(this.getRawType()).runtimeType, this.runtimeType).resolveType(genericType.runtimeType);
        }
        return clazz;
    }
    
    private boolean someRawTypeIsSubclassOf(final Class<?> clazz) {
        final Iterator iterator = this.getRawTypes().iterator();
        while (iterator.hasNext()) {
            if (clazz.isAssignableFrom((Class)iterator.next())) {
                return true;
            }
        }
        return false;
    }
    
    @VisibleForTesting
    static <T> TypeToken<? extends T> toGenericType(final Class<T> clazz) {
        if (clazz.isArray()) {
            return (TypeToken<? extends T>)of(Types.newArrayType(toGenericType(clazz.getComponentType()).runtimeType));
        }
        final TypeVariable[] typeParameters = clazz.getTypeParameters();
        Type runtimeType;
        if (!clazz.isMemberClass()) {
            runtimeType = null;
        }
        else {
            runtimeType = toGenericType(clazz.getEnclosingClass()).runtimeType;
        }
        if (typeParameters.length <= 0 && runtimeType == clazz.getEnclosingClass()) {
            return of((Class<? extends T>)clazz);
        }
        return (TypeToken<? extends T>)of(Types.newParameterizedTypeWithOwner(runtimeType, clazz, (Type[])typeParameters));
    }
    
    public final Invokable<T, T> constructor(final Constructor<?> constructor) {
        Preconditions.checkArgument(constructor.getDeclaringClass() == this.getRawType(), "%s not declared by %s", constructor, this.getRawType());
        return (Invokable<T, T>)new Invokable.ConstructorInvokable<T>(constructor) {
            @Override
            Type[] getGenericExceptionTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericExceptionTypes());
            }
            
            @Override
            Type[] getGenericParameterTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericParameterTypes());
            }
            
            @Override
            Type getGenericReturnType() {
                return TypeToken.this.resolveType(super.getGenericReturnType()).getType();
            }
            
            @Override
            public TypeToken<T> getOwnerType() {
                return TypeToken.this;
            }
            
            @Override
            public String toString() {
                return this.getOwnerType() + "(" + Joiner.on(", ").join(this.getGenericParameterTypes()) + ")";
            }
        };
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o instanceof TypeToken && this.runtimeType.equals(((TypeToken)o).runtimeType);
    }
    
    @Nullable
    public final TypeToken<?> getComponentType() {
        final Type componentType = Types.getComponentType(this.runtimeType);
        if (componentType != null) {
            return of(componentType);
        }
        return null;
    }
    
    final ImmutableList<TypeToken<? super T>> getGenericInterfaces() {
        int i = 0;
        if (this.runtimeType instanceof TypeVariable) {
            return this.boundsAsInterfaces(((TypeVariable)this.runtimeType).getBounds());
        }
        if (!(this.runtimeType instanceof WildcardType)) {
            final ImmutableList.Builder<TypeToken<? super T>> builder = ImmutableList.builder();
            for (Type[] genericInterfaces = this.getRawType().getGenericInterfaces(); i < genericInterfaces.length; ++i) {
                builder.add((TypeToken<? super T>)this.resolveSupertype(genericInterfaces[i]));
            }
            return builder.build();
        }
        return this.boundsAsInterfaces(((WildcardType)this.runtimeType).getUpperBounds());
    }
    
    @Nullable
    final TypeToken<? super T> getGenericSuperclass() {
        if (this.runtimeType instanceof TypeVariable) {
            return this.boundAsSuperclass(((TypeVariable)this.runtimeType).getBounds()[0]);
        }
        if (this.runtimeType instanceof WildcardType) {
            return this.boundAsSuperclass(((WildcardType)this.runtimeType).getUpperBounds()[0]);
        }
        final Type genericSuperclass = this.getRawType().getGenericSuperclass();
        if (genericSuperclass != null) {
            return (TypeToken<? super T>)this.resolveSupertype(genericSuperclass);
        }
        return null;
    }
    
    public final Class<? super T> getRawType() {
        return this.getRawTypes().iterator().next();
    }
    
    public final TypeToken<? extends T> getSubtype(final Class<?> clazz) {
        Preconditions.checkArgument(!(this.runtimeType instanceof TypeVariable), "Cannot get subtype of type variable <%s>", this);
        if (this.runtimeType instanceof WildcardType) {
            return this.getSubtypeFromLowerBounds(clazz, ((WildcardType)this.runtimeType).getLowerBounds());
        }
        if (!this.isArray()) {
            Preconditions.checkArgument(this.getRawType().isAssignableFrom(clazz), "%s isn't a subclass of %s", clazz, this);
            return (TypeToken<? extends T>)of(replaceTypeVariablesWithWildcard(this.resolveTypeArgsForSubclass(clazz), clazz));
        }
        return this.getArraySubtype(clazz);
    }
    
    public final TypeToken<? super T> getSupertype(final Class<? super T> clazz) {
        Preconditions.checkArgument(this.someRawTypeIsSubclassOf(clazz), "%s is not a super class of %s", clazz, this);
        if (this.runtimeType instanceof TypeVariable) {
            return this.getSupertypeFromUpperBounds(clazz, ((TypeVariable)this.runtimeType).getBounds());
        }
        if (this.runtimeType instanceof WildcardType) {
            return this.getSupertypeFromUpperBounds(clazz, ((WildcardType)this.runtimeType).getUpperBounds());
        }
        if (!clazz.isArray()) {
            return (TypeToken<? super T>)this.resolveSupertype(toGenericType(clazz).runtimeType);
        }
        return this.getArraySupertype(clazz);
    }
    
    public final Type getType() {
        return this.runtimeType;
    }
    
    public final TypeSet getTypes() {
        return new TypeSet();
    }
    
    @Override
    public int hashCode() {
        return this.runtimeType.hashCode();
    }
    
    public final boolean isArray() {
        return this.getComponentType() != null;
    }
    
    @Deprecated
    public final boolean isAssignableFrom(final TypeToken<?> typeToken) {
        return this.isSupertypeOf(typeToken);
    }
    
    @Deprecated
    public final boolean isAssignableFrom(final Type type) {
        return this.isSupertypeOf(type);
    }
    
    public final boolean isPrimitive() {
        return this.runtimeType instanceof Class && ((Class)this.runtimeType).isPrimitive();
    }
    
    public final boolean isSubtypeOf(final TypeToken<?> typeToken) {
        return this.isSubtypeOf(typeToken.getType());
    }
    
    public final boolean isSubtypeOf(final Type obj) {
        Preconditions.checkNotNull(obj);
        if (obj instanceof WildcardType) {
            return any(((WildcardType)obj).getLowerBounds()).isSupertypeOf(this.runtimeType);
        }
        if (this.runtimeType instanceof WildcardType) {
            return any(((WildcardType)this.runtimeType).getUpperBounds()).isSubtypeOf(obj);
        }
        if (this.runtimeType instanceof TypeVariable) {
            return this.runtimeType.equals(obj) || any(((TypeVariable)this.runtimeType).getBounds()).isSubtypeOf(obj);
        }
        if (this.runtimeType instanceof GenericArrayType) {
            return of(obj).isSuperTypeOfArray((GenericArrayType)this.runtimeType);
        }
        if (obj instanceof Class) {
            return this.someRawTypeIsSubclassOf((Class<?>)obj);
        }
        if (!(obj instanceof ParameterizedType)) {
            return obj instanceof GenericArrayType && this.isSubTypeOfArrayType((GenericArrayType)obj);
        }
        return this.isSubtypeOfParameterizedType((ParameterizedType)obj);
    }
    
    public final boolean isSupertypeOf(final TypeToken<?> typeToken) {
        return typeToken.isSubtypeOf(this.getType());
    }
    
    public final boolean isSupertypeOf(final Type type) {
        return of(type).isSubtypeOf(this.getType());
    }
    
    public final Invokable<T, Object> method(final Method method) {
        Preconditions.checkArgument(this.someRawTypeIsSubclassOf(method.getDeclaringClass()), "%s not declared by %s", method, this);
        return (Invokable<T, Object>)new Invokable.MethodInvokable<T>(method) {
            @Override
            Type[] getGenericExceptionTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericExceptionTypes());
            }
            
            @Override
            Type[] getGenericParameterTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericParameterTypes());
            }
            
            @Override
            Type getGenericReturnType() {
                return TypeToken.this.resolveType(super.getGenericReturnType()).getType();
            }
            
            @Override
            public TypeToken<T> getOwnerType() {
                return TypeToken.this;
            }
            
            @Override
            public String toString() {
                return this.getOwnerType() + "." + super.toString();
            }
        };
    }
    
    final TypeToken<T> rejectTypeVariables() {
        new TypeVisitor() {
            @Override
            void visitGenericArrayType(final GenericArrayType genericArrayType) {
                this.visit(genericArrayType.getGenericComponentType());
            }
            
            @Override
            void visitParameterizedType(final ParameterizedType parameterizedType) {
                this.visit(parameterizedType.getActualTypeArguments());
                this.visit(parameterizedType.getOwnerType());
            }
            
            @Override
            void visitTypeVariable(final TypeVariable<?> typeVariable) {
                throw new IllegalArgumentException(TypeToken.this.runtimeType + "contains a type variable and is not safe for the operation");
            }
            
            @Override
            void visitWildcardType(final WildcardType wildcardType) {
                this.visit(wildcardType.getLowerBounds());
                this.visit(wildcardType.getUpperBounds());
            }
        }.visit(this.runtimeType);
        return this;
    }
    
    public final TypeToken<?> resolveType(final Type type) {
        Preconditions.checkNotNull(type);
        TypeResolver typeResolver = this.typeResolver;
        if (typeResolver == null) {
            typeResolver = TypeResolver.accordingTo(this.runtimeType);
            this.typeResolver = typeResolver;
        }
        return of(typeResolver.resolveType(type));
    }
    
    @Override
    public String toString() {
        return Types.toString(this.runtimeType);
    }
    
    public final TypeToken<T> unwrap() {
        if (!this.isWrapper()) {
            return this;
        }
        return of((Class<T>)Primitives.unwrap((Class<T>)this.runtimeType));
    }
    
    public final <X> TypeToken<T> where(final TypeParameter<X> typeParameter, final TypeToken<X> typeToken) {
        return new SimpleTypeToken<T>(new TypeResolver().where(ImmutableMap.of(new TypeResolver.TypeVariableKey(typeParameter.typeVariable), typeToken.runtimeType)).resolveType(this.runtimeType));
    }
    
    public final <X> TypeToken<T> where(final TypeParameter<X> typeParameter, final Class<X> clazz) {
        return this.where(typeParameter, (TypeToken<X>)of((Class<X>)clazz));
    }
    
    public final TypeToken<T> wrap() {
        if (!this.isPrimitive()) {
            return this;
        }
        return of((Class<T>)Primitives.wrap((Class<T>)this.runtimeType));
    }
    
    protected Object writeReplace() {
        return of(new TypeResolver().resolveType(this.runtimeType));
    }
    
    private static class Bounds
    {
        private final Type[] bounds;
        private final boolean target;
        
        Bounds(final Type[] bounds, final boolean target) {
            this.bounds = bounds;
            this.target = target;
        }
        
        boolean isSubtypeOf(final Type type) {
            boolean b = false;
            final Type[] bounds = this.bounds;
            for (int length = bounds.length, i = 0; i < length; ++i) {
                if (TypeToken.of(bounds[i]).isSubtypeOf(type) == this.target) {
                    return this.target;
                }
            }
            if (!this.target) {
                b = true;
            }
            return b;
        }
        
        boolean isSupertypeOf(final Type type) {
            boolean b = false;
            final TypeToken<?> of = TypeToken.of(type);
            final Type[] bounds = this.bounds;
            for (int length = bounds.length, i = 0; i < length; ++i) {
                if (of.isSubtypeOf(bounds[i]) == this.target) {
                    return this.target;
                }
            }
            if (!this.target) {
                b = true;
            }
            return b;
        }
    }
    
    private final class ClassSet extends TypeSet
    {
        private static final long serialVersionUID = 0L;
        private transient ImmutableSet<TypeToken<? super T>> classes;
        
        private Object readResolve() {
            return TypeToken.this.getTypes().classes();
        }
        
        @Override
        public TypeSet classes() {
            return this;
        }
        
        @Override
        protected Set<TypeToken<? super T>> delegate() {
            final ImmutableSet<TypeToken<? super T>> classes = this.classes;
            if (classes != null) {
                return classes;
            }
            return this.classes = FluentIterable.from(TypeCollector.FOR_GENERIC_TYPE.classesOnly().collectTypes(TypeToken.this)).filter(TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
        }
        
        @Override
        public TypeSet interfaces() {
            throw new UnsupportedOperationException("classes().interfaces() not supported.");
        }
        
        @Override
        public Set<Class<? super T>> rawTypes() {
            return (Set<Class<? super T>>)ImmutableSet.copyOf((Collection<?>)TypeCollector.FOR_RAW_TYPE.classesOnly().collectTypes(TypeToken.this.getRawTypes()));
        }
    }
    
    private final class InterfaceSet extends TypeSet
    {
        private static final long serialVersionUID = 0L;
        private final transient TypeSet allTypes;
        private transient ImmutableSet<TypeToken<? super T>> interfaces;
        
        InterfaceSet(final TypeSet allTypes) {
            this.allTypes = allTypes;
        }
        
        private Object readResolve() {
            return TypeToken.this.getTypes().interfaces();
        }
        
        @Override
        public TypeSet classes() {
            throw new UnsupportedOperationException("interfaces().classes() not supported.");
        }
        
        @Override
        protected Set<TypeToken<? super T>> delegate() {
            final ImmutableSet<TypeToken<? super T>> interfaces = this.interfaces;
            if (interfaces != null) {
                return interfaces;
            }
            return this.interfaces = FluentIterable.from((Iterable<TypeToken<? super T>>)this.allTypes).filter(TypeFilter.INTERFACE_ONLY).toSet();
        }
        
        @Override
        public TypeSet interfaces() {
            return this;
        }
        
        @Override
        public Set<Class<? super T>> rawTypes() {
            return (Set<Class<? super T>>)FluentIterable.from(TypeCollector.FOR_RAW_TYPE.collectTypes(TypeToken.this.getRawTypes())).filter(new Predicate<Class<?>>() {
                @Override
                public boolean apply(final Class<?> clazz) {
                    return clazz.isInterface();
                }
            }).toSet();
        }
    }
    
    private static final class SimpleTypeToken<T> extends TypeToken<T>
    {
        private static final long serialVersionUID = 0L;
        
        SimpleTypeToken(final Type type) {
            super(type, null);
        }
    }
    
    private abstract static class TypeCollector<K>
    {
        static final TypeCollector<TypeToken<?>> FOR_GENERIC_TYPE;
        static final TypeCollector<Class<?>> FOR_RAW_TYPE;
        
        static {
            FOR_GENERIC_TYPE = new TypeCollector<TypeToken<?>>() {
                @Override
                Iterable<? extends TypeToken<?>> getInterfaces(final TypeToken<?> typeToken) {
                    return typeToken.getGenericInterfaces();
                }
                
                @Override
                Class<?> getRawType(final TypeToken<?> typeToken) {
                    return typeToken.getRawType();
                }
                
                @Nullable
                @Override
                TypeToken<?> getSuperclass(final TypeToken<?> typeToken) {
                    return typeToken.getGenericSuperclass();
                }
            };
            FOR_RAW_TYPE = new TypeCollector<Class<?>>() {
                @Override
                Iterable<? extends Class<?>> getInterfaces(final Class<?> clazz) {
                    return Arrays.asList(clazz.getInterfaces());
                }
                
                @Override
                Class<?> getRawType(final Class<?> clazz) {
                    return clazz;
                }
                
                @Nullable
                @Override
                Class<?> getSuperclass(final Class<?> clazz) {
                    return clazz.getSuperclass();
                }
            };
        }
        
        private int collectTypes(final K k, final Map<? super K, Integer> map) {
            final Integer n = map.get(this);
            if (n == null) {
                int n2;
                if (!this.getRawType(k).isInterface()) {
                    n2 = 0;
                }
                else {
                    n2 = 1;
                }
                final Iterator<? extends K> iterator = this.getInterfaces(k).iterator();
                while (iterator.hasNext()) {
                    n2 = Math.max(n2, this.collectTypes(iterator.next(), map));
                }
                final Object superclass = this.getSuperclass(k);
                if (superclass != null) {
                    n2 = Math.max(n2, this.collectTypes((K)superclass, map));
                }
                map.put(k, n2 + 1);
                return n2 + 1;
            }
            return n;
        }
        
        private static <K, V> ImmutableList<K> sortKeysByValue(final Map<K, V> map, final Comparator<? super V> comparator) {
            return new Ordering<K>() {
                @Override
                public int compare(final K k, final K i) {
                    return comparator.compare(map.get(k), map.get(i));
                }
            }.immutableSortedCopy(map.keySet());
        }
        
        final TypeCollector<K> classesOnly() {
            return new ForwardingTypeCollector<K>(this) {
                @Override
                ImmutableList<K> collectTypes(final Iterable<? extends K> iterable) {
                    final ImmutableList.Builder<K> builder = ImmutableList.builder();
                    for (final K next : iterable) {
                        if (!((ForwardingTypeCollector<K>)this).getRawType(next).isInterface()) {
                            builder.add(next);
                        }
                    }
                    return super.collectTypes((Iterable<? extends K>)builder.build());
                }
                
                @Override
                Iterable<? extends K> getInterfaces(final K k) {
                    return (Iterable<? extends K>)ImmutableSet.of();
                }
            };
        }
        
        ImmutableList<K> collectTypes(final Iterable<? extends K> iterable) {
            final HashMap<Object, Object> hashMap = Maps.newHashMap();
            final Iterator<? extends K> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.collectTypes(iterator.next(), (Map<? super K, Integer>)hashMap);
            }
            return sortKeysByValue((Map<K, Object>)hashMap, Ordering.natural().reverse());
        }
        
        final ImmutableList<K> collectTypes(final K k) {
            return this.collectTypes((Iterable<? extends K>)ImmutableList.of(k));
        }
        
        abstract Iterable<? extends K> getInterfaces(final K p0);
        
        abstract Class<?> getRawType(final K p0);
        
        @Nullable
        abstract K getSuperclass(final K p0);
        
        private static class ForwardingTypeCollector<K> extends TypeCollector<K>
        {
            private final TypeCollector<K> delegate;
            
            ForwardingTypeCollector(final TypeCollector<K> delegate) {
                this.delegate = delegate;
            }
            
            @Override
            Iterable<? extends K> getInterfaces(final K k) {
                return this.delegate.getInterfaces(k);
            }
            
            @Override
            Class<?> getRawType(final K k) {
                return this.delegate.getRawType(k);
            }
            
            @Override
            K getSuperclass(final K k) {
                return this.delegate.getSuperclass(k);
            }
        }
    }
    
    private enum TypeFilter implements Predicate<TypeToken<?>>
    {
        IGNORE_TYPE_VARIABLE_OR_WILDCARD {
            @Override
            public boolean apply(final TypeToken<?> typeToken) {
                boolean b = false;
                if (!(((TypeToken<Object>)typeToken).runtimeType instanceof TypeVariable) && !(((TypeToken<Object>)typeToken).runtimeType instanceof WildcardType)) {
                    b = true;
                }
                return b;
            }
        }, 
        INTERFACE_ONLY {
            @Override
            public boolean apply(final TypeToken<?> typeToken) {
                return typeToken.getRawType().isInterface();
            }
        };
    }
    
    public class TypeSet extends ForwardingSet<TypeToken<? super T>> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private transient ImmutableSet<TypeToken<? super T>> types;
        
        TypeSet() {
        }
        
        public TypeSet classes() {
            return (TypeSet)new ClassSet();
        }
        
        @Override
        protected Set<TypeToken<? super T>> delegate() {
            final ImmutableSet<TypeToken<? super T>> types = this.types;
            if (types != null) {
                return types;
            }
            return this.types = FluentIterable.from(TypeCollector.FOR_GENERIC_TYPE.collectTypes(TypeToken.this)).filter(TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
        }
        
        public TypeSet interfaces() {
            return (TypeSet)new InterfaceSet(this);
        }
        
        public Set<Class<? super T>> rawTypes() {
            return (Set<Class<? super T>>)ImmutableSet.copyOf((Collection<?>)TypeCollector.FOR_RAW_TYPE.collectTypes(TypeToken.this.getRawTypes()));
        }
    }
}
