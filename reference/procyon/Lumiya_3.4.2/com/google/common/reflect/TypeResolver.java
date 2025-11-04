// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import com.google.common.base.Joiner;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Arrays;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import com.google.common.collect.Maps;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import com.google.common.base.Preconditions;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.WildcardType;
import java.lang.reflect.Type;
import java.util.Map;
import com.google.common.annotations.Beta;

@Beta
public final class TypeResolver
{
    private final TypeTable typeTable;
    
    public TypeResolver() {
        this.typeTable = new TypeTable();
    }
    
    private TypeResolver(final TypeTable typeTable) {
        this.typeTable = typeTable;
    }
    
    static TypeResolver accordingTo(final Type type) {
        return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings(type));
    }
    
    private static <T> T expectArgument(final Class<T> clazz, final Object o) {
        try {
            return clazz.cast(o);
        }
        catch (final ClassCastException ex) {
            throw new IllegalArgumentException(o + " is not a " + clazz.getSimpleName());
        }
    }
    
    private static void populateTypeMappings(final Map<TypeVariableKey, Type> map, final Type type, final Type obj) {
        if (type.equals(obj)) {
            return;
        }
        if (obj instanceof WildcardType == type instanceof WildcardType) {
            new TypeVisitor() {
                @Override
                void visitClass(final Class<?> obj) {
                    throw new IllegalArgumentException("No type mapping from " + obj);
                }
                
                @Override
                void visitGenericArrayType(final GenericArrayType genericArrayType) {
                    final Type componentType = Types.getComponentType(obj);
                    Preconditions.checkArgument(componentType != null, "%s is not an array type.", obj);
                    populateTypeMappings(map, genericArrayType.getGenericComponentType(), componentType);
                }
                
                @Override
                void visitParameterizedType(final ParameterizedType parameterizedType) {
                    int i = 0;
                    final ParameterizedType parameterizedType2 = (ParameterizedType)expectArgument((Class<Object>)ParameterizedType.class, obj);
                    Preconditions.checkArgument(parameterizedType.getRawType().equals(parameterizedType2.getRawType()), "Inconsistent raw type: %s vs. %s", parameterizedType, obj);
                    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    final Type[] actualTypeArguments2 = parameterizedType2.getActualTypeArguments();
                    Preconditions.checkArgument(actualTypeArguments.length == actualTypeArguments2.length, "%s not compatible with %s", parameterizedType, parameterizedType2);
                    while (i < actualTypeArguments.length) {
                        populateTypeMappings(map, actualTypeArguments[i], actualTypeArguments2[i]);
                        ++i;
                    }
                }
                
                @Override
                void visitTypeVariable(final TypeVariable<?> typeVariable) {
                    map.put(new TypeVariableKey(typeVariable), obj);
                }
                
                @Override
                void visitWildcardType(final WildcardType wildcardType) {
                    final int n = 0;
                    final WildcardType wildcardType2 = (WildcardType)expectArgument((Class<Object>)WildcardType.class, obj);
                    final Type[] upperBounds = wildcardType.getUpperBounds();
                    final Type[] upperBounds2 = wildcardType2.getUpperBounds();
                    final Type[] lowerBounds = wildcardType.getLowerBounds();
                    final Type[] lowerBounds2 = wildcardType2.getLowerBounds();
                    Preconditions.checkArgument(upperBounds.length == upperBounds2.length && lowerBounds.length == lowerBounds2.length, "Incompatible type: %s vs. %s", wildcardType, obj);
                    for (int i = 0; i < upperBounds.length; ++i) {
                        populateTypeMappings(map, upperBounds[i], upperBounds2[i]);
                    }
                    for (int j = n; j < lowerBounds.length; ++j) {
                        populateTypeMappings(map, lowerBounds[j], lowerBounds2[j]);
                    }
                }
            }.visit(type);
        }
    }
    
    private Type resolveGenericArrayType(final GenericArrayType genericArrayType) {
        return Types.newArrayType(this.resolveType(genericArrayType.getGenericComponentType()));
    }
    
    private ParameterizedType resolveParameterizedType(final ParameterizedType parameterizedType) {
        final Type ownerType = parameterizedType.getOwnerType();
        Type resolveType;
        if (ownerType != null) {
            resolveType = this.resolveType(ownerType);
        }
        else {
            resolveType = null;
        }
        return Types.newParameterizedTypeWithOwner(resolveType, (Class<?>)this.resolveType(parameterizedType.getRawType()), this.resolveTypes(parameterizedType.getActualTypeArguments()));
    }
    
    private Type[] resolveTypes(final Type[] array) {
        final Type[] array2 = new Type[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.resolveType(array[i]);
        }
        return array2;
    }
    
    private WildcardType resolveWildcardType(final WildcardType wildcardType) {
        return new Types.WildcardTypeImpl(this.resolveTypes(wildcardType.getLowerBounds()), this.resolveTypes(wildcardType.getUpperBounds()));
    }
    
    public Type resolveType(final Type type) {
        Preconditions.checkNotNull(type);
        if (type instanceof TypeVariable) {
            return this.typeTable.resolve((TypeVariable<?>)type);
        }
        if (type instanceof ParameterizedType) {
            return this.resolveParameterizedType((ParameterizedType)type);
        }
        if (type instanceof GenericArrayType) {
            return this.resolveGenericArrayType((GenericArrayType)type);
        }
        if (!(type instanceof WildcardType)) {
            return type;
        }
        return this.resolveWildcardType((WildcardType)type);
    }
    
    public TypeResolver where(final Type type, final Type type2) {
        final HashMap<Object, Object> hashMap = Maps.newHashMap();
        populateTypeMappings((Map<TypeVariableKey, Type>)hashMap, Preconditions.checkNotNull(type), Preconditions.checkNotNull(type2));
        return this.where((Map<TypeVariableKey, ? extends Type>)hashMap);
    }
    
    TypeResolver where(final Map<TypeVariableKey, ? extends Type> map) {
        return new TypeResolver(this.typeTable.where(map));
    }
    
    private static final class TypeMappingIntrospector extends TypeVisitor
    {
        private static final WildcardCapturer wildcardCapturer;
        private final Map<TypeVariableKey, Type> mappings;
        
        static {
            wildcardCapturer = new WildcardCapturer();
        }
        
        private TypeMappingIntrospector() {
            this.mappings = (Map<TypeVariableKey, Type>)Maps.newHashMap();
        }
        
        static ImmutableMap<TypeVariableKey, Type> getTypeMappings(final Type type) {
            final TypeMappingIntrospector typeMappingIntrospector = new TypeMappingIntrospector();
            typeMappingIntrospector.visit(TypeMappingIntrospector.wildcardCapturer.capture(type));
            return (ImmutableMap<TypeVariableKey, Type>)ImmutableMap.copyOf((Map<?, ?>)typeMappingIntrospector.mappings);
        }
        
        private void map(final TypeVariableKey typeVariableKey, final Type type) {
            if (!this.mappings.containsKey(typeVariableKey)) {
                for (Type type2 = type; type2 != null; type2 = this.mappings.get(TypeVariableKey.forLookup(type2))) {
                    Type type3 = type;
                    if (typeVariableKey.equalsType(type2)) {
                        while (type3 != null) {
                            type3 = this.mappings.remove(TypeVariableKey.forLookup(type3));
                        }
                        return;
                    }
                }
                this.mappings.put(typeVariableKey, type);
            }
        }
        
        @Override
        void visitClass(final Class<?> clazz) {
            this.visit(clazz.getGenericSuperclass());
            this.visit(clazz.getGenericInterfaces());
        }
        
        @Override
        void visitParameterizedType(final ParameterizedType parameterizedType) {
            final Class clazz = (Class)parameterizedType.getRawType();
            final TypeVariable[] typeParameters = clazz.getTypeParameters();
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Preconditions.checkState(typeParameters.length == actualTypeArguments.length);
            for (int i = 0; i < typeParameters.length; ++i) {
                this.map(new TypeVariableKey(typeParameters[i]), actualTypeArguments[i]);
            }
            this.visit(clazz);
            this.visit(parameterizedType.getOwnerType());
        }
        
        @Override
        void visitTypeVariable(final TypeVariable<?> typeVariable) {
            this.visit(typeVariable.getBounds());
        }
        
        @Override
        void visitWildcardType(final WildcardType wildcardType) {
            this.visit(wildcardType.getUpperBounds());
        }
    }
    
    private static class TypeTable
    {
        private final ImmutableMap<TypeVariableKey, Type> map;
        
        TypeTable() {
            this.map = ImmutableMap.of();
        }
        
        private TypeTable(final ImmutableMap<TypeVariableKey, Type> map) {
            this.map = map;
        }
        
        final Type resolve(final TypeVariable<?> typeVariable) {
            return this.resolveInternal(typeVariable, new TypeTable() {
                public Type resolveInternal(final TypeVariable<?> typeVariable, final TypeTable typeTable) {
                    if (!typeVariable.getGenericDeclaration().equals(typeVariable.getGenericDeclaration())) {
                        return TypeTable.this.resolveInternal(typeVariable, typeTable);
                    }
                    return typeVariable;
                }
            });
        }
        
        Type resolveInternal(final TypeVariable<?> typeVariable, final TypeTable typeTable) {
            final Type type = this.map.get(new TypeVariableKey(typeVariable));
            if (type != null) {
                return new TypeResolver(typeTable, null).resolveType(type);
            }
            final Type[] bounds = typeVariable.getBounds();
            if (bounds.length == 0) {
                return typeVariable;
            }
            final Type[] access$300 = new TypeResolver(typeTable, null).resolveTypes(bounds);
            if (Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY && Arrays.equals(bounds, access$300)) {
                return typeVariable;
            }
            return Types.newArtificialTypeVariable(typeVariable.getGenericDeclaration(), typeVariable.getName(), access$300);
        }
        
        final TypeTable where(final Map<TypeVariableKey, ? extends Type> map) {
            final ImmutableMap.Builder<TypeVariableKey, Type> builder = ImmutableMap.builder();
            builder.putAll(this.map);
            for (final Map.Entry<TypeVariableKey, V> entry : map.entrySet()) {
                final TypeVariableKey typeVariableKey = entry.getKey();
                final Type type = (Type)entry.getValue();
                Preconditions.checkArgument(!typeVariableKey.equalsType(type), "Type variable %s bound to itself", typeVariableKey);
                builder.put(typeVariableKey, type);
            }
            return new TypeTable(builder.build());
        }
    }
    
    static final class TypeVariableKey
    {
        private final TypeVariable<?> var;
        
        TypeVariableKey(final TypeVariable<?> typeVariable) {
            this.var = Preconditions.checkNotNull(typeVariable);
        }
        
        private boolean equalsTypeVariable(final TypeVariable<?> typeVariable) {
            boolean b = false;
            if (this.var.getGenericDeclaration().equals(typeVariable.getGenericDeclaration()) && this.var.getName().equals(typeVariable.getName())) {
                b = true;
            }
            return b;
        }
        
        static Object forLookup(final Type type) {
            if (!(type instanceof TypeVariable)) {
                return null;
            }
            return new TypeVariableKey((TypeVariable<?>)type);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof TypeVariableKey && this.equalsTypeVariable(((TypeVariableKey)o).var);
        }
        
        boolean equalsType(final Type type) {
            return type instanceof TypeVariable && this.equalsTypeVariable((TypeVariable<?>)type);
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.var.getGenericDeclaration(), this.var.getName());
        }
        
        @Override
        public String toString() {
            return this.var.toString();
        }
    }
    
    private static final class WildcardCapturer
    {
        private final AtomicInteger id;
        
        private WildcardCapturer() {
            this.id = new AtomicInteger();
        }
        
        private Type[] capture(final Type[] array) {
            final Type[] array2 = new Type[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = this.capture(array[i]);
            }
            return array2;
        }
        
        private Type captureNullable(@Nullable final Type type) {
            if (type != null) {
                return this.capture(type);
            }
            return null;
        }
        
        Type capture(final Type type) {
            Preconditions.checkNotNull(type);
            if (type instanceof Class) {
                return type;
            }
            if (type instanceof TypeVariable) {
                return type;
            }
            if (type instanceof GenericArrayType) {
                return Types.newArrayType(this.capture(((GenericArrayType)type).getGenericComponentType()));
            }
            if (type instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType)type;
                return Types.newParameterizedTypeWithOwner(this.captureNullable(parameterizedType.getOwnerType()), (Class<?>)parameterizedType.getRawType(), this.capture(parameterizedType.getActualTypeArguments()));
            }
            if (!(type instanceof WildcardType)) {
                throw new AssertionError((Object)"must have been one of the known types");
            }
            final WildcardType wildcardType = (WildcardType)type;
            if (wildcardType.getLowerBounds().length != 0) {
                return type;
            }
            return Types.newArtificialTypeVariable(WildcardCapturer.class, "capture#" + this.id.incrementAndGet() + "-of ? extends " + Joiner.on('&').join(wildcardType.getUpperBounds()), wildcardType.getUpperBounds());
        }
    }
}
