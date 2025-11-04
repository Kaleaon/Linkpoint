// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.util.Arrays;
import java.util.AbstractList;
import java.lang.reflect.InvocationTargetException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.google.common.annotations.Beta;
import javax.annotation.CheckReturnValue;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import com.google.common.annotations.VisibleForTesting;

public final class Throwables
{
    private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
    @VisibleForTesting
    static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
    @Nullable
    private static final Method getStackTraceDepthMethod;
    @Nullable
    private static final Method getStackTraceElementMethod;
    @Nullable
    private static final Object jla;
    
    static {
        final Method method = null;
        jla = getJLA();
        Method getMethod;
        if (Throwables.jla != null) {
            getMethod = getGetMethod();
        }
        else {
            getMethod = null;
        }
        getStackTraceElementMethod = getMethod;
        Method sizeMethod = method;
        if (Throwables.jla != null) {
            sizeMethod = getSizeMethod();
        }
        getStackTraceDepthMethod = sizeMethod;
    }
    
    private Throwables() {
    }
    
    @CheckReturnValue
    @Beta
    public static List<Throwable> getCausalChain(Throwable cause) {
        Preconditions.checkNotNull(cause);
        final ArrayList list = new ArrayList(4);
        while (cause != null) {
            list.add(cause);
            cause = cause.getCause();
        }
        return (List<Throwable>)Collections.unmodifiableList((List<?>)list);
    }
    
    @Nullable
    private static Method getGetMethod() {
        return getJlaMethod("getStackTraceElement", Throwable.class, Integer.TYPE);
    }
    
    @Nullable
    private static Object getJLA() {
        try {
            return Class.forName("sun.misc.SharedSecrets", false, null).getMethod("getJavaLangAccess", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            return null;
        }
    }
    
    @Nullable
    private static Method getJlaMethod(final String name, final Class<?>... parameterTypes) throws ThreadDeath {
        try {
            return Class.forName("sun.misc.JavaLangAccess", false, null).getMethod(name, parameterTypes);
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            return null;
        }
    }
    
    @CheckReturnValue
    public static Throwable getRootCause(Throwable t) {
        while (true) {
            final Throwable cause = t.getCause();
            if (cause == null) {
                break;
            }
            t = cause;
        }
        return t;
    }
    
    @Nullable
    private static Method getSizeMethod() {
        return getJlaMethod("getStackTraceDepth", Throwable.class);
    }
    
    @CheckReturnValue
    public static String getStackTraceAsString(final Throwable t) {
        final StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        return out.toString();
    }
    
    private static Object invokeAccessibleNonThrowingMethod(final Method method, final Object obj, final Object... args) {
        try {
            return method.invoke(obj, args);
        }
        catch (final IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
        catch (final InvocationTargetException ex) {
            throw propagate(ex.getCause());
        }
    }
    
    private static List<StackTraceElement> jlaStackTrace(final Throwable t) {
        Preconditions.checkNotNull(t);
        return new AbstractList<StackTraceElement>() {
            @Override
            public StackTraceElement get(final int i) {
                return (StackTraceElement)invokeAccessibleNonThrowingMethod(Throwables.getStackTraceElementMethod, Throwables.jla, new Object[] { t, i });
            }
            
            @Override
            public int size() {
                return (int)invokeAccessibleNonThrowingMethod(Throwables.getStackTraceDepthMethod, Throwables.jla, new Object[] { t });
            }
        };
    }
    
    @CheckReturnValue
    @Beta
    public static List<StackTraceElement> lazyStackTrace(final Throwable t) {
        Object o;
        if (!lazyStackTraceIsLazy()) {
            o = Collections.unmodifiableList((List<?>)Arrays.asList((T[])t.getStackTrace()));
        }
        else {
            o = jlaStackTrace(t);
        }
        return (List<StackTraceElement>)o;
    }
    
    @CheckReturnValue
    @Beta
    public static boolean lazyStackTraceIsLazy() {
        boolean b = false;
        boolean b2;
        if (Throwables.getStackTraceElementMethod == null) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (Throwables.getStackTraceDepthMethod != null) {
            b = true;
        }
        return b2 & b;
    }
    
    public static RuntimeException propagate(final Throwable cause) {
        propagateIfPossible(Preconditions.checkNotNull(cause));
        throw new RuntimeException(cause);
    }
    
    public static <X extends Throwable> void propagateIfInstanceOf(@Nullable final Throwable obj, final Class<X> clazz) throws X, Throwable {
        if (obj != null && clazz.isInstance(obj)) {
            throw (Throwable)clazz.cast(obj);
        }
    }
    
    public static void propagateIfPossible(@Nullable final Throwable t) {
        propagateIfInstanceOf(t, Error.class);
        propagateIfInstanceOf(t, RuntimeException.class);
    }
    
    public static <X extends Throwable> void propagateIfPossible(@Nullable final Throwable t, final Class<X> clazz) throws X, Throwable {
        propagateIfInstanceOf(t, (Class<Throwable>)clazz);
        propagateIfPossible(t);
    }
    
    public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable final Throwable t, final Class<X1> clazz, final Class<X2> clazz2) throws X1, X2, Throwable {
        Preconditions.checkNotNull(clazz2);
        propagateIfInstanceOf(t, clazz);
        propagateIfPossible(t, clazz2);
    }
}
