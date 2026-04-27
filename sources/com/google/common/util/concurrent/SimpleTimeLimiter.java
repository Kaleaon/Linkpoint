package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Beta
public final class SimpleTimeLimiter implements TimeLimiter {
    private final ExecutorService executor;

    public SimpleTimeLimiter() {
        this(Executors.newCachedThreadPool());
    }

    public SimpleTimeLimiter(ExecutorService executorService) {
        this.executor = (ExecutorService) Preconditions.checkNotNull(executorService);
    }

    private static boolean declaresInterruptedEx(Method method) {
        for (Class<InterruptedException> cls : method.getExceptionTypes()) {
            if (cls == InterruptedException.class) {
                return true;
            }
        }
        return false;
    }

    private static Set<Method> findInterruptibleMethods(Class<?> cls) {
        HashSet newHashSet = Sets.newHashSet();
        for (Method method : cls.getMethods()) {
            if (declaresInterruptedEx(method)) {
                newHashSet.add(method);
            }
        }
        return newHashSet;
    }

    private static <T> T newProxy(Class<T> cls, InvocationHandler invocationHandler) {
        return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, invocationHandler));
    }

    /* access modifiers changed from: private */
    public static Exception throwCause(Exception exc, boolean z) throws Exception {
        Throwable cause = exc.getCause();
        if (cause != null) {
            if (z) {
                cause.setStackTrace((StackTraceElement[]) ObjectArrays.concat(cause.getStackTrace(), exc.getStackTrace(), StackTraceElement.class));
            }
            if (cause instanceof Exception) {
                throw ((Exception) cause);
            } else if (!(cause instanceof Error)) {
                throw exc;
            } else {
                throw ((Error) cause);
            }
        } else {
            throw exc;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0036, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r2.cancel(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0041, code lost:
        throw throwCause(r0, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0042, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0043, code lost:
        r2.cancel(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004b, code lost:
        throw new com.google.common.util.concurrent.UncheckedTimeoutException((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:7:0x0028, B:12:0x0031] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T callWithTimeout(java.util.concurrent.Callable<T> r7, long r8, java.util.concurrent.TimeUnit r10, boolean r11) throws java.lang.Exception {
        /*
            r6 = this;
            r2 = 0
            r1 = 1
            com.google.common.base.Preconditions.checkNotNull(r7)
            com.google.common.base.Preconditions.checkNotNull(r10)
            r4 = 0
            int r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r0 > 0) goto L_0x002d
            r0 = r1
        L_0x000f:
            if (r0 != 0) goto L_0x002f
            r0 = r1
        L_0x0012:
            java.lang.Object[] r3 = new java.lang.Object[r1]
            java.lang.Long r4 = java.lang.Long.valueOf(r8)
            r3[r2] = r4
            java.lang.String r2 = "timeout must be positive: %s"
            com.google.common.base.Preconditions.checkArgument(r0, r2, r3)
            java.util.concurrent.ExecutorService r0 = r6.executor
            java.util.concurrent.Future r2 = r0.submit(r7)
            if (r11 != 0) goto L_0x0031
            java.lang.Object r0 = com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly(r2, r8, r10)     // Catch:{ ExecutionException -> 0x003c, TimeoutException -> 0x0042 }
            return r0
        L_0x002d:
            r0 = r2
            goto L_0x000f
        L_0x002f:
            r0 = r2
            goto L_0x0012
        L_0x0031:
            java.lang.Object r0 = r2.get(r8, r10)     // Catch:{ InterruptedException -> 0x0036 }
            return r0
        L_0x0036:
            r0 = move-exception
            r3 = 1
            r2.cancel(r3)     // Catch:{ ExecutionException -> 0x003c, TimeoutException -> 0x0042 }
            throw r0     // Catch:{ ExecutionException -> 0x003c, TimeoutException -> 0x0042 }
        L_0x003c:
            r0 = move-exception
            java.lang.Exception r0 = throwCause(r0, r1)
            throw r0
        L_0x0042:
            r0 = move-exception
            r2.cancel(r1)
            com.google.common.util.concurrent.UncheckedTimeoutException r1 = new com.google.common.util.concurrent.UncheckedTimeoutException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.SimpleTimeLimiter.callWithTimeout(java.util.concurrent.Callable, long, java.util.concurrent.TimeUnit, boolean):java.lang.Object");
    }

    public <T> T newProxy(T t, Class<T> cls, long j, TimeUnit timeUnit) {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(cls);
        Preconditions.checkNotNull(timeUnit);
        Preconditions.checkArgument(!((j > 0 ? 1 : (j == 0 ? 0 : -1)) <= 0), "bad timeout: %s", Long.valueOf(j));
        Preconditions.checkArgument(cls.isInterface(), "interfaceType must be an interface type");
        final Set<Method> findInterruptibleMethods = findInterruptibleMethods(cls);
        final T t2 = t;
        final long j2 = j;
        final TimeUnit timeUnit2 = timeUnit;
        return newProxy(cls, new InvocationHandler() {
            public Object invoke(Object obj, final Method method, final Object[] objArr) throws Throwable {
                return SimpleTimeLimiter.this.callWithTimeout(new Callable<Object>() {
                    public Object call() throws Exception {
                        try {
                            return method.invoke(t2, objArr);
                        } catch (InvocationTargetException e) {
                            Exception unused = SimpleTimeLimiter.throwCause(e, false);
                            throw new AssertionError("can't get here");
                        }
                    }
                }, j2, timeUnit2, findInterruptibleMethods.contains(method));
            }
        });
    }
}
