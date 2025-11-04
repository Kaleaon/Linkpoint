// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import java.lang.reflect.Member;
import java.lang.reflect.AccessibleObject;

class Element extends AccessibleObject implements Member
{
    private final AccessibleObject accessibleObject;
    private final Member member;
    
     <M extends AccessibleObject & Member> Element(final M accessibleObject) {
        Preconditions.checkNotNull(accessibleObject);
        this.accessibleObject = accessibleObject;
        this.member = accessibleObject;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof Element)) {
            return false;
        }
        final Element element = (Element)o;
        if (this.getOwnerType().equals(element.getOwnerType()) && this.member.equals(element.member)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public final <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
        return this.accessibleObject.getAnnotation(annotationClass);
    }
    
    @Override
    public final Annotation[] getAnnotations() {
        return this.accessibleObject.getAnnotations();
    }
    
    @Override
    public final Annotation[] getDeclaredAnnotations() {
        return this.accessibleObject.getDeclaredAnnotations();
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this.member.getDeclaringClass();
    }
    
    @Override
    public final int getModifiers() {
        return this.member.getModifiers();
    }
    
    @Override
    public final String getName() {
        return this.member.getName();
    }
    
    public TypeToken<?> getOwnerType() {
        return TypeToken.of(this.getDeclaringClass());
    }
    
    @Override
    public int hashCode() {
        return this.member.hashCode();
    }
    
    public final boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }
    
    @Override
    public final boolean isAccessible() {
        return this.accessibleObject.isAccessible();
    }
    
    @Override
    public final boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return this.accessibleObject.isAnnotationPresent(annotationClass);
    }
    
    public final boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }
    
    public final boolean isNative() {
        return Modifier.isNative(this.getModifiers());
    }
    
    public final boolean isPackagePrivate() {
        final boolean b = false;
        boolean b2;
        if (this.isPrivate()) {
            b2 = b;
        }
        else {
            b2 = b;
            if (!this.isPublic()) {
                b2 = b;
                if (!this.isProtected()) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public final boolean isPrivate() {
        return Modifier.isPrivate(this.getModifiers());
    }
    
    public final boolean isProtected() {
        return Modifier.isProtected(this.getModifiers());
    }
    
    public final boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }
    
    public final boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }
    
    public final boolean isSynchronized() {
        return Modifier.isSynchronized(this.getModifiers());
    }
    
    @Override
    public final boolean isSynthetic() {
        return this.member.isSynthetic();
    }
    
    final boolean isTransient() {
        return Modifier.isTransient(this.getModifiers());
    }
    
    final boolean isVolatile() {
        return Modifier.isVolatile(this.getModifiers());
    }
    
    @Override
    public final void setAccessible(final boolean accessible) throws SecurityException {
        this.accessibleObject.setAccessible(accessible);
    }
    
    @Override
    public String toString() {
        return this.member.toString();
    }
}
