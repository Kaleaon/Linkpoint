package com.google.common.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;
/* JADX INFO: Access modifiers changed from: package-private */
@NotThreadSafe
/* loaded from: classes.dex */
public abstract class TypeVisitor {
    private final Set<Type> visited = Sets.newHashSet();

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0042, code lost:
        throw new java.lang.AssertionError("Unknown type: " + r2);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void visit(java.lang.reflect.Type... r6) {
        /*
            r5 = this;
            r1 = 0
            int r4 = r6.length
        L2:
            r3 = r1
            if (r3 < r4) goto L6
            return
        L6:
            r2 = r6[r3]
            if (r2 != 0) goto Ld
        La:
            int r1 = r3 + 1
            goto L2
        Ld:
            java.util.Set<java.lang.reflect.Type> r1 = r5.visited
            boolean r1 = r1.add(r2)
            if (r1 == 0) goto La
            boolean r1 = r2 instanceof java.lang.reflect.TypeVariable     // Catch: java.lang.Throwable -> L43
            if (r1 != 0) goto L4a
            boolean r1 = r2 instanceof java.lang.reflect.WildcardType     // Catch: java.lang.Throwable -> L43
            if (r1 != 0) goto L52
            boolean r1 = r2 instanceof java.lang.reflect.ParameterizedType     // Catch: java.lang.Throwable -> L43
            if (r1 != 0) goto L5a
            boolean r1 = r2 instanceof java.lang.Class     // Catch: java.lang.Throwable -> L43
            if (r1 != 0) goto L62
            boolean r1 = r2 instanceof java.lang.reflect.GenericArrayType     // Catch: java.lang.Throwable -> L43
            if (r1 != 0) goto L6a
            java.lang.AssertionError r1 = new java.lang.AssertionError     // Catch: java.lang.Throwable -> L43
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L43
            r3.<init>()     // Catch: java.lang.Throwable -> L43
            java.lang.String r4 = "Unknown type: "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L43
            java.lang.StringBuilder r3 = r3.append(r2)     // Catch: java.lang.Throwable -> L43
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L43
            r1.<init>(r3)     // Catch: java.lang.Throwable -> L43
            throw r1     // Catch: java.lang.Throwable -> L43
        L43:
            r1 = move-exception
            java.util.Set<java.lang.reflect.Type> r3 = r5.visited
            r3.remove(r2)
            throw r1
        L4a:
            r0 = r2
            java.lang.reflect.TypeVariable r0 = (java.lang.reflect.TypeVariable) r0     // Catch: java.lang.Throwable -> L43
            r1 = r0
            r5.visitTypeVariable(r1)     // Catch: java.lang.Throwable -> L43
            goto La
        L52:
            r0 = r2
            java.lang.reflect.WildcardType r0 = (java.lang.reflect.WildcardType) r0     // Catch: java.lang.Throwable -> L43
            r1 = r0
            r5.visitWildcardType(r1)     // Catch: java.lang.Throwable -> L43
            goto La
        L5a:
            r0 = r2
            java.lang.reflect.ParameterizedType r0 = (java.lang.reflect.ParameterizedType) r0     // Catch: java.lang.Throwable -> L43
            r1 = r0
            r5.visitParameterizedType(r1)     // Catch: java.lang.Throwable -> L43
            goto La
        L62:
            r0 = r2
            java.lang.Class r0 = (java.lang.Class) r0     // Catch: java.lang.Throwable -> L43
            r1 = r0
            r5.visitClass(r1)     // Catch: java.lang.Throwable -> L43
            goto La
        L6a:
            r0 = r2
            java.lang.reflect.GenericArrayType r0 = (java.lang.reflect.GenericArrayType) r0     // Catch: java.lang.Throwable -> L43
            r1 = r0
            r5.visitGenericArrayType(r1)     // Catch: java.lang.Throwable -> L43
            goto La
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.reflect.TypeVisitor.visit(java.lang.reflect.Type[]):void");
    }

    void visitClass(Class<?> cls) {
    }

    void visitGenericArrayType(GenericArrayType genericArrayType) {
    }

    void visitParameterizedType(ParameterizedType parameterizedType) {
    }

    void visitTypeVariable(TypeVariable<?> typeVariable) {
    }

    void visitWildcardType(WildcardType wildcardType) {
    }
}
