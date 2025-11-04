// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.CheckResult;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import android.app.Dialog;
import android.app.Activity;
import java.util.List;
import android.support.annotation.UiThread;
import android.support.annotation.RequiresApi;
import android.annotation.TargetApi;
import android.view.View;
import android.util.Property;
import android.support.annotation.NonNull;
import java.util.LinkedHashMap;
import android.support.annotation.VisibleForTesting;
import java.lang.reflect.Constructor;
import java.util.Map;

public final class ButterKnife
{
    @VisibleForTesting
    static final Map<Class<?>, Constructor<? extends Unbinder>> BINDINGS;
    private static final String TAG = "ButterKnife";
    private static boolean debug;
    
    static {
        ButterKnife.debug = false;
        BINDINGS = new LinkedHashMap<Class<?>, Constructor<? extends Unbinder>>();
    }
    
    private ButterKnife() {
        throw new AssertionError((Object)"No instances.");
    }
    
    @TargetApi(14)
    @RequiresApi(14)
    @UiThread
    public static <T extends View, V> void apply(@NonNull final T t, @NonNull final Property<? super T, V> property, final V v) {
        property.set((Object)t, (Object)v);
    }
    
    @UiThread
    public static <T extends View> void apply(@NonNull final T t, @NonNull final Action<? super T> action) {
        action.apply(t, 0);
    }
    
    @UiThread
    public static <T extends View, V> void apply(@NonNull final T t, @NonNull final Setter<? super T, V> setter, final V v) {
        setter.set(t, v, 0);
    }
    
    @SafeVarargs
    @UiThread
    public static <T extends View> void apply(@NonNull final T t, @NonNull final Action<? super T>... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            array[i].apply(t, 0);
        }
    }
    
    @TargetApi(14)
    @RequiresApi(14)
    @UiThread
    public static <T extends View, V> void apply(@NonNull final List<T> list, @NonNull final Property<? super T, V> property, final V v) {
        for (int i = 0; i < list.size(); ++i) {
            property.set(list.get(i), (Object)v);
        }
    }
    
    @UiThread
    public static <T extends View> void apply(@NonNull final List<T> list, @NonNull final Action<? super T> action) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            action.apply((T)list.get(i), i);
        }
    }
    
    @UiThread
    public static <T extends View, V> void apply(@NonNull final List<T> list, @NonNull final Setter<? super T, V> setter, final V v) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            setter.set((T)list.get(i), v, i);
        }
    }
    
    @SafeVarargs
    @UiThread
    public static <T extends View> void apply(@NonNull final List<T> list, @NonNull final Action<? super T>... array) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            for (int length = array.length, j = 0; j < length; ++j) {
                array[j].apply(list.get(i), i);
            }
        }
    }
    
    @TargetApi(14)
    @RequiresApi(14)
    @UiThread
    public static <T extends View, V> void apply(@NonNull final T[] array, @NonNull final Property<? super T, V> property, final V v) {
        for (int i = 0; i < array.length; ++i) {
            property.set((Object)array[i], (Object)v);
        }
    }
    
    @UiThread
    public static <T extends View> void apply(@NonNull final T[] array, @NonNull final Action<? super T> action) {
        for (int i = 0; i < array.length; ++i) {
            action.apply(array[i], i);
        }
    }
    
    @UiThread
    public static <T extends View, V> void apply(@NonNull final T[] array, @NonNull final Setter<? super T, V> setter, final V v) {
        for (int i = 0; i < array.length; ++i) {
            setter.set(array[i], v, i);
        }
    }
    
    @SafeVarargs
    @UiThread
    public static <T extends View> void apply(@NonNull final T[] array, @NonNull final Action<? super T>... array2) {
        for (int length = array.length, i = 0; i < length; ++i) {
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                array2[j].apply(array[i], i);
            }
        }
    }
    
    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull final Activity activity) {
        return createBinding(activity, activity.getWindow().getDecorView());
    }
    
    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull final Dialog dialog) {
        return createBinding(dialog, dialog.getWindow().getDecorView());
    }
    
    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull final View view) {
        return createBinding(view, view);
    }
    
    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull final Object o, @NonNull final Activity activity) {
        return createBinding(o, activity.getWindow().getDecorView());
    }
    
    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull final Object o, @NonNull final Dialog dialog) {
        return createBinding(o, dialog.getWindow().getDecorView());
    }
    
    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull final Object o, @NonNull final View view) {
        return createBinding(o, view);
    }
    
    private static Unbinder createBinding(@NonNull final Object o, @NonNull final View view) {
        GenericDeclaration genericDeclaration = o.getClass();
        Label_0042: {
            if (ButterKnife.debug) {
                break Label_0042;
            }
            while (true) {
                genericDeclaration = findBindingConstructorForClass((Class<?>)genericDeclaration);
                Label_0073: {
                    if (genericDeclaration == null) {
                        break Label_0073;
                    }
                    try {
                        return ((Constructor<Unbinder>)genericDeclaration).newInstance(o, view);
                        Log.d("ButterKnife", "Looking up binding for " + ((Class)genericDeclaration).getName());
                        continue;
                        return Unbinder.EMPTY;
                    }
                    catch (final IllegalAccessException cause) {
                        throw new RuntimeException("Unable to invoke " + genericDeclaration, cause);
                    }
                    catch (final InstantiationException cause2) {
                        throw new RuntimeException("Unable to invoke " + genericDeclaration, cause2);
                    }
                    catch (final InvocationTargetException ex) {
                        final Throwable cause3 = ex.getCause();
                        if (cause3 instanceof RuntimeException) {
                            throw (RuntimeException)cause3;
                        }
                        if (!(cause3 instanceof Error)) {
                            throw new RuntimeException("Unable to create binding instance.", cause3);
                        }
                        throw (Error)cause3;
                    }
                }
                break;
            }
        }
    }
    
    @CheckResult
    @Nullable
    @UiThread
    private static Constructor<? extends Unbinder> findBindingConstructorForClass(final Class<?> clazz) {
        final Constructor constructor = ButterKnife.BINDINGS.get(clazz);
        if (constructor != null) {
            if (ButterKnife.debug) {
                Log.d("ButterKnife", "HIT: Cached in binding map.");
            }
            return constructor;
        }
        final String name = clazz.getName();
        Label_0137: {
            Block_2: {
                if (!name.startsWith("android.") && !name.startsWith("java.")) {
                    break Block_2;
                }
                if (ButterKnife.debug) {
                    break Label_0137;
                }
                return null;
            }
            while (true) {
                try {
                    final Constructor<?> constructor2 = clazz.getClassLoader().loadClass(name + "_ViewBinding").getConstructor(clazz, View.class);
                    if (ButterKnife.debug) {
                        Log.d("ButterKnife", "HIT: Loaded binding class and constructor.");
                    }
                    ButterKnife.BINDINGS.put(clazz, (Constructor<? extends Unbinder>)constructor2);
                    return (Constructor<? extends Unbinder>)constructor2;
                    Log.d("ButterKnife", "MISS: Reached framework class. Abandoning search.");
                    return null;
                }
                catch (final ClassNotFoundException ex) {
                    if (ButterKnife.debug) {
                        Log.d("ButterKnife", "Not found. Trying superclass " + clazz.getSuperclass().getName());
                    }
                    final Constructor<?> constructor2 = findBindingConstructorForClass(clazz.getSuperclass());
                    continue;
                }
                catch (final NoSuchMethodException cause) {
                    throw new RuntimeException("Unable to find binding constructor for " + name, cause);
                }
                break;
            }
        }
    }
    
    @Deprecated
    @CheckResult
    public static <T extends View> T findById(@NonNull final Activity activity, @IdRes final int n) {
        return (T)activity.findViewById(n);
    }
    
    @Deprecated
    @CheckResult
    public static <T extends View> T findById(@NonNull final Dialog dialog, @IdRes final int n) {
        return (T)dialog.findViewById(n);
    }
    
    @Deprecated
    @CheckResult
    public static <T extends View> T findById(@NonNull final View view, @IdRes final int n) {
        return (T)view.findViewById(n);
    }
    
    public static void setDebug(final boolean debug) {
        ButterKnife.debug = debug;
    }
    
    public interface Action<T extends View>
    {
        @UiThread
        void apply(@NonNull final T p0, final int p1);
    }
    
    public interface Setter<T extends View, V>
    {
        @UiThread
        void set(@NonNull final T p0, final V p1, final int p2);
    }
}
