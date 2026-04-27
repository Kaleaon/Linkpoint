package com.google.common.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
abstract class TypeVisitor {
    private final Set<Type> visited = Sets.newHashSet();

    TypeVisitor() {
    }

    public final void visit(Type... typeArr) {
        int i = 0;
        int length = typeArr.length;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                GenericArrayType genericArrayType = typeArr[i2];
                if (genericArrayType != null && this.visited.add(genericArrayType)) {
                    try {
                        if (genericArrayType instanceof TypeVariable) {
                            visitTypeVariable(genericArrayType);
                        } else if (genericArrayType instanceof WildcardType) {
                            visitWildcardType(genericArrayType);
                        } else if (genericArrayType instanceof ParameterizedType) {
                            visitParameterizedType(genericArrayType);
                        } else if (genericArrayType instanceof Class) {
                            visitClass((Class) genericArrayType);
                        } else if (!(genericArrayType instanceof GenericArrayType)) {
                            throw new AssertionError("Unknown type: " + genericArrayType);
                        } else {
                            visitGenericArrayType(genericArrayType);
                        }
                    } catch (Throwable th) {
                        this.visited.remove(genericArrayType);
                        throw th;
                    }
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void visitClass(Class<?> cls) {
    }

    /* access modifiers changed from: package-private */
    public void visitGenericArrayType(GenericArrayType genericArrayType) {
    }

    /* access modifiers changed from: package-private */
    public void visitParameterizedType(ParameterizedType parameterizedType) {
    }

    /* access modifiers changed from: package-private */
    public void visitTypeVariable(TypeVariable<?> typeVariable) {
    }

    /* access modifiers changed from: package-private */
    public void visitWildcardType(WildcardType wildcardType) {
    }
}
