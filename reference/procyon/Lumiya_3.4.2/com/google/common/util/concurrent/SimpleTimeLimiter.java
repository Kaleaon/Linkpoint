// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import com.google.common.collect.ObjectArrays;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.HashSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.lang.reflect.Method;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.google.common.annotations.Beta;

@Beta
public final class SimpleTimeLimiter implements TimeLimiter
{
    private final ExecutorService executor;
    
    public SimpleTimeLimiter() {
        this(Executors.newCachedThreadPool());
    }
    
    public SimpleTimeLimiter(final ExecutorService executorService) {
        this.executor = Preconditions.checkNotNull(executorService);
    }
    
    private static boolean declaresInterruptedEx(final Method method) {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        for (int length = exceptionTypes.length, i = 0; i < length; ++i) {
            if (exceptionTypes[i] == InterruptedException.class) {
                return true;
            }
        }
        return false;
    }
    
    private static Set<Method> findInterruptibleMethods(final Class<?> clazz) {
        int i = 0;
        final HashSet<Object> hashSet = Sets.newHashSet();
        for (Method[] methods = clazz.getMethods(); i < methods.length; ++i) {
            final Method method = methods[i];
            if (declaresInterruptedEx(method)) {
                hashSet.add(method);
            }
        }
        return (Set<Method>)hashSet;
    }
    
    private static <T> T newProxy(final Class<T> clazz, final InvocationHandler h) {
        return clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, h));
    }
    
    private static Exception throwCause(final Exception ex, final boolean b) throws Exception {
        final Throwable cause = ex.getCause();
        if (cause == null) {
            throw ex;
        }
        if (b) {
            cause.setStackTrace(ObjectArrays.concat(cause.getStackTrace(), ex.getStackTrace(), StackTraceElement.class));
        }
        if (cause instanceof Exception) {
            throw (Exception)cause;
        }
        if (!(cause instanceof Error)) {
            throw ex;
        }
        throw (Error)cause;
    }
    
    @Override
    public <T> T callWithTimeout(Callable<T> submit, final long l, final TimeUnit timeUnit, final boolean b) throws Exception {
        Preconditions.checkNotNull((Callable<T>)submit);
        Preconditions.checkNotNull(timeUnit);
        Label_0074: {
            if (l > 0L) {
                break Label_0074;
            }
            int n = 1;
        Label_0028_Outer:
            while (true) {
                Label_0080: {
                    if (n != 0) {
                        break Label_0080;
                    }
                    boolean b2 = true;
                    while (true) {
                        Preconditions.checkArgument(b2, "timeout must be positive: %s", l);
                        submit = this.executor.submit((Callable<T>)submit);
                        Label_0086: {
                            if (b) {
                                break Label_0086;
                            }
                            try {
                                return Uninterruptibles.getUninterruptibly(submit, l, timeUnit);
                                b2 = false;
                                continue;
                                n = 0;
                                continue Label_0028_Outer;
                                try {
                                    return submit.get(l, timeUnit);
                                }
                                catch (final InterruptedException ex) {
                                    submit.cancel(true);
                                    throw ex;
                                }
                            }
                            catch (final ExecutionException ex2) {
                                throw throwCause(ex2, true);
                            }
                            catch (final TimeoutException ex3) {
                                submit.cancel(true);
                                throw new UncheckedTimeoutException(ex3);
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public <T> T newProxy(final T t, final Class<T> clazz, final long l, final TimeUnit timeUnit) {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(timeUnit);
        boolean b;
        if (l <= 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "bad timeout: %s", l);
        Preconditions.checkArgument(clazz.isInterface(), (Object)"interfaceType must be an interface type");
        return newProxy(clazz, new InvocationHandler() {
            final /* synthetic */ Set val$interruptibleMethods = findInterruptibleMethods(clazz);
            
            @Override
            public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
                return SimpleTimeLimiter.this.callWithTimeout((Callable<Object>)new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        try {
                            return method.invoke(t, array);
                        }
                        catch (final InvocationTargetException ex) {
                            throwCause(ex, false);
                            throw new AssertionError((Object)"can't get here");
                        }
                    }
                }, l, timeUnit, this.val$interruptibleMethods.contains(method));
            }
        });
    }
}
