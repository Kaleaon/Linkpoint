// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.lang.reflect.Modifier;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Properties;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.TypeVariable;
import java.io.Serializable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public final class $Gson$Types
{
    static final Type[] EMPTY_TYPE_ARRAY;
    
    static {
        EMPTY_TYPE_ARRAY = new Type[0];
    }
    
    private $Gson$Types() {
        throw new UnsupportedOperationException();
    }
    
    public static GenericArrayType arrayOf(final Type type) {
        return new GenericArrayTypeImpl(type);
    }
    
    public static Type canonicalize(final Type type) {
        if (type instanceof Class) {
            Serializable s = (Class)type;
            if (((Class)s).isArray()) {
                s = new GenericArrayTypeImpl(canonicalize(((Class)s).getComponentType()));
            }
            return (Type)s;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            return new ParameterizedTypeImpl(parameterizedType.getOwnerType(), parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
        }
        if (type instanceof GenericArrayType) {
            return new GenericArrayTypeImpl(((GenericArrayType)type).getGenericComponentType());
        }
        if (!(type instanceof WildcardType)) {
            return type;
        }
        final WildcardType wildcardType = (WildcardType)type;
        return new WildcardTypeImpl(wildcardType.getUpperBounds(), wildcardType.getLowerBounds());
    }
    
    static void checkNotPrimitive(final Type type) {
        boolean b = false;
        if (!(type instanceof Class) || !((Class)type).isPrimitive()) {
            b = true;
        }
        $Gson$Preconditions.checkArgument(b);
    }
    
    private static Class<?> declaringClassOf(final TypeVariable<?> typeVariable) {
        final Object genericDeclaration = typeVariable.getGenericDeclaration();
        Class<?> clazz;
        if (!(genericDeclaration instanceof Class)) {
            clazz = null;
        }
        else {
            clazz = (Class<?>)genericDeclaration;
        }
        return clazz;
    }
    
    static boolean equal(final Object o, final Object obj) {
        final boolean b = false;
        if (o != obj) {
            boolean b2 = b;
            if (o == null) {
                return b2;
            }
            if (!o.equals(obj)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    public static boolean equals(final Type type, final Type obj) {
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = true;
        if (type == obj) {
            return true;
        }
        if (type instanceof Class) {
            return type.equals(obj);
        }
        if (!(type instanceof ParameterizedType)) {
            if (type instanceof GenericArrayType) {
                return obj instanceof GenericArrayType && equals(((GenericArrayType)type).getGenericComponentType(), ((GenericArrayType)obj).getGenericComponentType());
            }
            if (!(type instanceof WildcardType)) {
                if (!(type instanceof TypeVariable)) {
                    return false;
                }
                if (obj instanceof TypeVariable) {
                    final TypeVariable typeVariable = (TypeVariable)type;
                    final TypeVariable typeVariable2 = (TypeVariable)obj;
                    if (typeVariable.getGenericDeclaration() == typeVariable2.getGenericDeclaration()) {
                        final boolean b4 = b2;
                        if (typeVariable.getName().equals(typeVariable2.getName())) {
                            return b4;
                        }
                    }
                    return false;
                }
                return false;
            }
            else {
                if (obj instanceof WildcardType) {
                    final WildcardType wildcardType = (WildcardType)type;
                    final WildcardType wildcardType2 = (WildcardType)obj;
                    if (Arrays.equals(wildcardType.getUpperBounds(), wildcardType2.getUpperBounds())) {
                        final boolean b5 = b;
                        if (Arrays.equals(wildcardType.getLowerBounds(), wildcardType2.getLowerBounds())) {
                            return b5;
                        }
                    }
                    return false;
                }
                return false;
            }
        }
        else {
            if (obj instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType)type;
                final ParameterizedType parameterizedType2 = (ParameterizedType)obj;
                if (!equal(parameterizedType.getOwnerType(), parameterizedType2.getOwnerType()) || !parameterizedType.getRawType().equals(parameterizedType2.getRawType()) || !Arrays.equals(parameterizedType.getActualTypeArguments(), parameterizedType2.getActualTypeArguments())) {
                    b3 = false;
                }
                return b3;
            }
            return false;
        }
    }
    
    public static Type getArrayComponentType(Type type) {
        if (!(type instanceof GenericArrayType)) {
            type = ((Class)type).getComponentType();
        }
        else {
            type = ((GenericArrayType)type).getGenericComponentType();
        }
        return type;
    }
    
    public static Type getCollectionElementType(Type supertype, final Class<?> clazz) {
        supertype = getSupertype(supertype, clazz, Collection.class);
        if (supertype instanceof WildcardType) {
            supertype = ((WildcardType)supertype).getUpperBounds()[0];
        }
        if (!(supertype instanceof ParameterizedType)) {
            return Object.class;
        }
        return ((ParameterizedType)supertype).getActualTypeArguments()[0];
    }
    
    static Type getGenericSupertype(final Type type, final Class<?> clazz, final Class<?> clazz2) {
        int i = 0;
        if (clazz2 != clazz) {
            if (clazz2.isInterface()) {
                for (Class[] interfaces = clazz.getInterfaces(); i < interfaces.length; ++i) {
                    if (interfaces[i] == clazz2) {
                        return clazz.getGenericInterfaces()[i];
                    }
                    if (clazz2.isAssignableFrom(interfaces[i])) {
                        return getGenericSupertype(clazz.getGenericInterfaces()[i], interfaces[i], clazz2);
                    }
                }
            }
            Class<Object> clazz3 = (Class<Object>)clazz;
            if (!clazz.isInterface()) {
                while (clazz3 != Object.class) {
                    final Class<? super Object> superclass = clazz3.getSuperclass();
                    if (superclass == clazz2) {
                        return clazz3.getGenericSuperclass();
                    }
                    if (clazz2.isAssignableFrom(superclass)) {
                        return getGenericSupertype(clazz3.getGenericSuperclass(), superclass, clazz2);
                    }
                    clazz3 = (Class<Object>)superclass;
                }
            }
            return clazz2;
        }
        return type;
    }
    
    public static Type[] getMapKeyAndValueTypes(Type supertype, final Class<?> clazz) {
        if (supertype == Properties.class) {
            return new Type[] { String.class, String.class };
        }
        supertype = getSupertype(supertype, clazz, Map.class);
        if (!(supertype instanceof ParameterizedType)) {
            return new Type[] { Object.class, Object.class };
        }
        return ((ParameterizedType)supertype).getActualTypeArguments();
    }
    
    public static Class<?> getRawType(Type rawType) {
        if (rawType instanceof Class) {
            return (Class)rawType;
        }
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType)rawType).getRawType();
            $Gson$Preconditions.checkArgument(rawType instanceof Class);
            return (Class)rawType;
        }
        if (rawType instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType)rawType).getGenericComponentType()), 0).getClass();
        }
        if (rawType instanceof TypeVariable) {
            return Object.class;
        }
        if (!(rawType instanceof WildcardType)) {
            String name;
            if (rawType != null) {
                name = rawType.getClass().getName();
            }
            else {
                name = "null";
            }
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + rawType + "> is of type " + name);
        }
        return getRawType(((WildcardType)rawType).getUpperBounds()[0]);
    }
    
    static Type getSupertype(final Type type, final Class<?> clazz, final Class<?> clazz2) {
        $Gson$Preconditions.checkArgument(clazz2.isAssignableFrom(clazz));
        return resolve(type, clazz, getGenericSupertype(type, clazz, clazz2));
    }
    
    static int hashCodeOrZero(final Object o) {
        int hashCode;
        if (o == null) {
            hashCode = 0;
        }
        else {
            hashCode = o.hashCode();
        }
        return hashCode;
    }
    
    private static int indexOf(final Object[] array, final Object o) {
        for (int i = 0; i < array.length; ++i) {
            if (o.equals(array[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }
    
    public static ParameterizedType newParameterizedTypeWithOwner(final Type type, final Type type2, final Type... array) {
        return new ParameterizedTypeImpl(type, type2, array);
    }
    
    public static Type resolve(Type type, final Class<?> clazz, Type type2) {
        while (type2 instanceof TypeVariable) {
            final TypeVariable typeVariable = (TypeVariable)type2;
            type2 = resolveTypeVariable(type, clazz, typeVariable);
            if (type2 != typeVariable) {
                continue;
            }
            return type2;
        }
        if (type2 instanceof Class && ((Class)type2).isArray()) {
            final Class clazz2 = (Class)type2;
            final Class componentType = clazz2.getComponentType();
            final Type resolve = resolve(type, clazz, componentType);
            Object array = clazz2;
            if (componentType != resolve) {
                array = arrayOf(resolve);
            }
            return (Type)array;
        }
        if (type2 instanceof GenericArrayType) {
            final GenericArrayType genericArrayType = (GenericArrayType)type2;
            final Type genericComponentType = genericArrayType.getGenericComponentType();
            final Type resolve2 = resolve(type, clazz, genericComponentType);
            GenericArrayType array2 = genericArrayType;
            if (genericComponentType != resolve2) {
                array2 = arrayOf(resolve2);
            }
            return array2;
        }
        if (type2 instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type2;
            type2 = parameterizedType.getOwnerType();
            final Type resolve3 = resolve(type, clazz, type2);
            int n;
            if (resolve3 == type2) {
                n = 0;
            }
            else {
                n = 1;
            }
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int length = actualTypeArguments.length, i = 0; i < length; ++i) {
                final Type resolve4 = resolve(type, clazz, actualTypeArguments[i]);
                if (resolve4 != actualTypeArguments[i]) {
                    if (n == 0) {
                        actualTypeArguments = actualTypeArguments.clone();
                        n = 1;
                    }
                    actualTypeArguments[i] = resolve4;
                }
            }
            ParameterizedType parameterizedTypeWithOwner;
            if (n == 0) {
                parameterizedTypeWithOwner = parameterizedType;
            }
            else {
                parameterizedTypeWithOwner = newParameterizedTypeWithOwner(resolve3, parameterizedType.getRawType(), actualTypeArguments);
            }
            return parameterizedTypeWithOwner;
        }
        if (!(type2 instanceof WildcardType)) {
            return type2;
        }
        final WildcardType wildcardType = (WildcardType)type2;
        final Type[] lowerBounds = wildcardType.getLowerBounds();
        final Type[] upperBounds = wildcardType.getUpperBounds();
        if (lowerBounds.length != 1) {
            if (upperBounds.length == 1) {
                type = resolve(type, clazz, upperBounds[0]);
                if (type != upperBounds[0]) {
                    return subtypeOf(type);
                }
            }
        }
        else {
            type = resolve(type, clazz, lowerBounds[0]);
            if (type != lowerBounds[0]) {
                return supertypeOf(type);
            }
        }
        return wildcardType;
    }
    
    static Type resolveTypeVariable(Type genericSupertype, final Class<?> clazz, final TypeVariable<?> typeVariable) {
        final Class<?> declaringClass = declaringClassOf(typeVariable);
        if (declaringClass == null) {
            return typeVariable;
        }
        genericSupertype = getGenericSupertype(genericSupertype, clazz, declaringClass);
        if (!(genericSupertype instanceof ParameterizedType)) {
            return typeVariable;
        }
        return ((ParameterizedType)genericSupertype).getActualTypeArguments()[indexOf(declaringClass.getTypeParameters(), typeVariable)];
    }
    
    public static WildcardType subtypeOf(final Type type) {
        Type[] upperBounds;
        if (!(type instanceof WildcardType)) {
            upperBounds = new Type[] { type };
        }
        else {
            upperBounds = ((WildcardType)type).getUpperBounds();
        }
        return new WildcardTypeImpl(upperBounds, $Gson$Types.EMPTY_TYPE_ARRAY);
    }
    
    public static WildcardType supertypeOf(final Type type) {
        Type[] lowerBounds;
        if (!(type instanceof WildcardType)) {
            lowerBounds = new Type[] { type };
        }
        else {
            lowerBounds = ((WildcardType)type).getLowerBounds();
        }
        return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
    }
    
    public static String typeToString(final Type type) {
        String s;
        if (!(type instanceof Class)) {
            s = type.toString();
        }
        else {
            s = ((Class)type).getName();
        }
        return s;
    }
    
    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Type componentType;
        
        public GenericArrayTypeImpl(final Type type) {
            this.componentType = $Gson$Types.canonicalize(type);
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = false;
            if (o instanceof GenericArrayType && $Gson$Types.equals(this, (Type)o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Type getGenericComponentType() {
            return this.componentType;
        }
        
        @Override
        public int hashCode() {
            return this.componentType.hashCode();
        }
        
        @Override
        public String toString() {
            return $Gson$Types.typeToString(this.componentType) + "[]";
        }
    }
    
    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;
        
        public ParameterizedTypeImpl(Type canonicalize, final Type type, final Type... array) {
            boolean b = true;
            final int n = 0;
            if (type instanceof Class) {
                final Class clazz = (Class)type;
                final boolean b2 = Modifier.isStatic(clazz.getModifiers()) || clazz.getEnclosingClass() == null;
                if (canonicalize == null && !b2) {
                    b = false;
                }
                $Gson$Preconditions.checkArgument(b);
            }
            if (canonicalize != null) {
                canonicalize = $Gson$Types.canonicalize(canonicalize);
            }
            else {
                canonicalize = null;
            }
            this.ownerType = canonicalize;
            this.rawType = $Gson$Types.canonicalize(type);
            this.typeArguments = array.clone();
            for (int length = this.typeArguments.length, i = n; i < length; ++i) {
                $Gson$Preconditions.checkNotNull(this.typeArguments[i]);
                $Gson$Types.checkNotPrimitive(this.typeArguments[i]);
                this.typeArguments[i] = $Gson$Types.canonicalize(this.typeArguments[i]);
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = false;
            if (o instanceof ParameterizedType && $Gson$Types.equals(this, (Type)o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Type[] getActualTypeArguments() {
            return this.typeArguments.clone();
        }
        
        @Override
        public Type getOwnerType() {
            return this.ownerType;
        }
        
        @Override
        public Type getRawType() {
            return this.rawType;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ $Gson$Types.hashCodeOrZero(this.ownerType);
        }
        
        @Override
        public String toString() {
            final int length = this.typeArguments.length;
            if (length != 0) {
                final StringBuilder sb = new StringBuilder((length + 1) * 30);
                sb.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
                for (int i = 1; i < length; ++i) {
                    sb.append(", ").append($Gson$Types.typeToString(this.typeArguments[i]));
                }
                return sb.append(">").toString();
            }
            return $Gson$Types.typeToString(this.rawType);
        }
    }
    
    private static final class WildcardTypeImpl implements WildcardType, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Type lowerBound;
        private final Type upperBound;
        
        public WildcardTypeImpl(final Type[] array, final Type[] array2) {
            final boolean b = true;
            $Gson$Preconditions.checkArgument(array2.length <= 1);
            $Gson$Preconditions.checkArgument(array.length == 1);
            if (array2.length != 1) {
                $Gson$Preconditions.checkNotNull(array[0]);
                $Gson$Types.checkNotPrimitive(array[0]);
                this.lowerBound = null;
                this.upperBound = $Gson$Types.canonicalize(array[0]);
            }
            else {
                $Gson$Preconditions.checkNotNull(array2[0]);
                $Gson$Types.checkNotPrimitive(array2[0]);
                boolean b2 = b;
                if (array[0] != Object.class) {
                    b2 = false;
                }
                $Gson$Preconditions.checkArgument(b2);
                this.lowerBound = $Gson$Types.canonicalize(array2[0]);
                this.upperBound = Object.class;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = false;
            if (o instanceof WildcardType && $Gson$Types.equals(this, (Type)o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Type[] getLowerBounds() {
            Type[] empty_TYPE_ARRAY;
            if (this.lowerBound == null) {
                empty_TYPE_ARRAY = $Gson$Types.EMPTY_TYPE_ARRAY;
            }
            else {
                empty_TYPE_ARRAY = new Type[] { this.lowerBound };
            }
            return empty_TYPE_ARRAY;
        }
        
        @Override
        public Type[] getUpperBounds() {
            return new Type[] { this.upperBound };
        }
        
        @Override
        public int hashCode() {
            int n;
            if (this.lowerBound == null) {
                n = 1;
            }
            else {
                n = this.lowerBound.hashCode() + 31;
            }
            return n ^ this.upperBound.hashCode() + 31;
        }
        
        @Override
        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + $Gson$Types.typeToString(this.lowerBound);
            }
            if (this.upperBound != Object.class) {
                return "? extends " + $Gson$Types.typeToString(this.upperBound);
            }
            return "?";
        }
    }
}
