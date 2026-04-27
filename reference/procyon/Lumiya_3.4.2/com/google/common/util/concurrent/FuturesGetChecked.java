// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.CopyOnWriteArraySet;
import java.lang.ref.WeakReference;
import java.util.Set;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import com.google.common.base.Function;
import java.lang.reflect.Constructor;
import com.google.common.collect.Ordering;

final class FuturesGetChecked
{
    private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST;
    
    static {
        WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf((Function<Object, ? extends Comparable>)new Function<Constructor<?>, Boolean>() {
            @Override
            public Boolean apply(final Constructor<?> constructor) {
                return Arrays.asList(constructor.getParameterTypes()).contains(String.class);
            }
        }).reverse();
    }
    
    private FuturesGetChecked() {
    }
    
    private static GetCheckedTypeValidator bestGetCheckedTypeValidator() {
        return GetCheckedTypeValidatorHolder.BEST_VALIDATOR;
    }
    
    @VisibleForTesting
    static void checkExceptionClassValidity(final Class<? extends Exception> clazz) {
        Preconditions.checkArgument(isCheckedException(clazz), "Futures.getChecked exception type (%s) must not be a RuntimeException", clazz);
        Preconditions.checkArgument(hasConstructorUsableByGetChecked(clazz), "Futures.getChecked exception type (%s) must be an accessible class with an accessible constructor whose parameters (if any) must be of type String and/or Throwable", clazz);
    }
    
    @VisibleForTesting
    static GetCheckedTypeValidator classValueValidator() {
        return (GetCheckedTypeValidator)ClassValueValidator.INSTANCE;
    }
    
    @VisibleForTesting
    static <V, X extends Exception> V getChecked(final GetCheckedTypeValidator getCheckedTypeValidator, final Future<V> future, final Class<X> clazz) throws X, Exception {
        getCheckedTypeValidator.validateClass(clazz);
        try {
            return future.get();
        }
        catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw newWithCause(clazz, ex);
        }
        catch (final ExecutionException ex2) {
            wrapAndThrowExceptionOrError(ex2.getCause(), clazz);
            throw new AssertionError();
        }
    }
    
    static <V, X extends Exception> V getChecked(final Future<V> future, final Class<X> clazz) throws X, Exception {
        return getChecked(bestGetCheckedTypeValidator(), future, clazz);
    }
    
    static <V, X extends Exception> V getChecked(final Future<V> future, final Class<X> clazz, final long n, final TimeUnit timeUnit) throws X, Exception {
        bestGetCheckedTypeValidator().validateClass(clazz);
        try {
            return future.get(n, timeUnit);
        }
        catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw newWithCause(clazz, ex);
        }
        catch (final TimeoutException ex2) {
            throw newWithCause(clazz, ex2);
        }
        catch (final ExecutionException ex3) {
            wrapAndThrowExceptionOrError(ex3.getCause(), clazz);
            throw new AssertionError();
        }
    }
    
    private static boolean hasConstructorUsableByGetChecked(final Class<? extends Exception> clazz) {
        try {
            newWithCause(clazz, new Exception());
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    @VisibleForTesting
    static boolean isCheckedException(final Class<? extends Exception> clazz) {
        boolean b = false;
        if (!RuntimeException.class.isAssignableFrom(clazz)) {
            b = true;
        }
        return b;
    }
    
    @Nullable
    private static <X> X newFromConstructor(final Constructor<X> constructor, final Throwable t) {
        int n = 0;
        final Class[] parameterTypes = constructor.getParameterTypes();
        final Object[] initargs = new Object[parameterTypes.length];
    Label_0064_Outer:
        while (true) {
            Label_0029: {
                if (n < parameterTypes.length) {
                    break Label_0029;
                }
                try {
                    return (X)constructor.newInstance(initargs);
                    Label_0056: {
                        initargs[n] = t.toString();
                    }
                Block_3_Outer:
                    while (true) {
                        break Label_0064;
                        while (true) {
                            final Class clazz;
                            iftrue(Label_0070:)(clazz.equals(Throwable.class));
                            return null;
                            clazz = parameterTypes[n];
                            iftrue(Label_0056:)(clazz.equals(String.class));
                            continue;
                        }
                        ++n;
                        continue Label_0064_Outer;
                        Label_0070:
                        initargs[n] = t;
                        continue Block_3_Outer;
                    }
                }
                catch (final IllegalArgumentException ex) {
                    return null;
                }
                catch (final InstantiationException ex2) {
                    return null;
                }
                catch (final IllegalAccessException ex3) {
                    return null;
                }
                catch (final InvocationTargetException ex4) {
                    return null;
                }
            }
        }
    }
    
    private static <X extends Exception> X newWithCause(final Class<X> obj, final Throwable t) {
        final Iterator<Constructor<Exception>> iterator = preferringStrings((List<Constructor<Exception>>)Arrays.asList(obj.getConstructors())).iterator();
        while (iterator.hasNext()) {
            final Exception ex = newFromConstructor((Constructor<X>)iterator.next(), t);
            if (ex != null) {
                if (ex.getCause() == null) {
                    ex.initCause(t);
                }
                return (X)ex;
            }
        }
        throw new IllegalArgumentException("No appropriate constructor for exception of type " + obj + " in response to chained exception", t);
    }
    
    private static <X extends Exception> List<Constructor<X>> preferringStrings(final List<Constructor<X>> list) {
        return FuturesGetChecked.WITH_STRING_PARAM_FIRST.sortedCopy(list);
    }
    
    @VisibleForTesting
    static GetCheckedTypeValidator weakSetValidator() {
        return (GetCheckedTypeValidator)WeakSetValidator.INSTANCE;
    }
    
    private static <X extends Exception> void wrapAndThrowExceptionOrError(final Throwable t, final Class<X> clazz) throws X, Exception {
        if (t instanceof Error) {
            throw new ExecutionError((Error)t);
        }
        if (!(t instanceof RuntimeException)) {
            throw newWithCause(clazz, t);
        }
        throw new UncheckedExecutionException(t);
    }
    
    @VisibleForTesting
    interface GetCheckedTypeValidator
    {
        void validateClass(final Class<? extends Exception> p0);
    }
    
    @VisibleForTesting
    static class GetCheckedTypeValidatorHolder
    {
        static final GetCheckedTypeValidator BEST_VALIDATOR;
        static final String CLASS_VALUE_VALIDATOR_NAME;
        
        static {
            CLASS_VALUE_VALIDATOR_NAME = GetCheckedTypeValidatorHolder.class.getName() + "$ClassValueValidator";
            BEST_VALIDATOR = getBestValidator();
        }
        
        static GetCheckedTypeValidator getBestValidator() {
            try {
                return (GetCheckedTypeValidator)Class.forName(GetCheckedTypeValidatorHolder.CLASS_VALUE_VALIDATOR_NAME).getEnumConstants()[0];
            }
            catch (final Throwable t) {
                return FuturesGetChecked.weakSetValidator();
            }
        }
        
        @IgnoreJRERequirement
        enum ClassValueValidator implements GetCheckedTypeValidator
        {
            INSTANCE;
            
            private static final ClassValue<Boolean> isValidClass;
            
            static {
                isValidClass = new ClassValue<Boolean>() {
                    @Override
                    protected Boolean computeValue(final Class<?> clazz) {
                        FuturesGetChecked.checkExceptionClassValidity(clazz.asSubclass(Exception.class));
                        return true;
                    }
                };
            }
            
            @Override
            public void validateClass(final Class<? extends Exception> type) {
                ClassValueValidator.isValidClass.get(type);
            }
        }
        
        enum WeakSetValidator implements GetCheckedTypeValidator
        {
            INSTANCE;
            
            private static final Set<WeakReference<Class<? extends Exception>>> validClasses;
            
            static {
                validClasses = new CopyOnWriteArraySet<WeakReference<Class<? extends Exception>>>();
            }
            
            @Override
            public void validateClass(final Class<? extends Exception> referent) {
                final Iterator<WeakReference<Class<? extends Exception>>> iterator = WeakSetValidator.validClasses.iterator();
                while (iterator.hasNext()) {
                    if (referent.equals(((WeakReference<Object>)iterator.next()).get())) {
                        return;
                    }
                }
                FuturesGetChecked.checkExceptionClassValidity(referent);
                if (WeakSetValidator.validClasses.size() > 1000) {
                    WeakSetValidator.validClasses.clear();
                }
                WeakSetValidator.validClasses.add(new WeakReference<Class<? extends Exception>>(referent));
            }
        }
    }
}
