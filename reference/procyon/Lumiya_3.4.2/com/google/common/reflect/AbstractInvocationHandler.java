// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.lang.reflect.Proxy;
import com.google.common.annotations.Beta;
import java.lang.reflect.InvocationHandler;

@Beta
public abstract class AbstractInvocationHandler implements InvocationHandler
{
    private static final Object[] NO_ARGS;
    
    static {
        NO_ARGS = new Object[0];
    }
    
    private static boolean isProxyOfSameInterfaces(final Object o, final Class<?> clazz) {
        final boolean b = false;
        if (!clazz.isInstance(o)) {
            boolean b2 = b;
            if (!Proxy.isProxyClass(o.getClass())) {
                return b2;
            }
            if (!Arrays.equals(o.getClass().getInterfaces(), clazz.getInterfaces())) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    protected abstract Object handleInvocation(final Object p0, final Method p1, final Object[] p2) throws Throwable;
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public final Object invoke(final Object o, final Method method, @Nullable Object[] no_ARGS) throws Throwable {
        boolean b = true;
        if (no_ARGS == null) {
            no_ARGS = AbstractInvocationHandler.NO_ARGS;
        }
        if (no_ARGS.length == 0 && method.getName().equals("hashCode")) {
            return this.hashCode();
        }
        if (no_ARGS.length == 1 && method.getName().equals("equals") && method.getParameterTypes()[0] == Object.class) {
            final Object proxy = no_ARGS[0];
            if (proxy == null) {
                return false;
            }
            if (o != proxy) {
                if (!isProxyOfSameInterfaces(proxy, o.getClass()) || !this.equals(Proxy.getInvocationHandler(proxy))) {
                    b = false;
                }
                return b;
            }
            return true;
        }
        else {
            if (no_ARGS.length == 0 && method.getName().equals("toString")) {
                return this.toString();
            }
            return this.handleInvocation(o, method, no_ARGS);
        }
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
