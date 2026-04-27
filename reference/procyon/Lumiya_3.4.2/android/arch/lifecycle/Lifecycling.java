// 
// Decompiled by Procyon v0.6.0
// 

package android.arch.lifecycle;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.util.Map;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
class Lifecycling
{
    private static Map<Class, Constructor<? extends GenericLifecycleObserver>> sCallbackCache;
    private static Constructor<? extends GenericLifecycleObserver> sREFLECTIVE;
    
    static {
        while (true) {
            try {
                Lifecycling.sREFLECTIVE = ReflectiveGenericLifecycleObserver.class.getDeclaredConstructor(Object.class);
                Lifecycling.sCallbackCache = new HashMap<Class, Constructor<? extends GenericLifecycleObserver>>();
            }
            catch (final NoSuchMethodException ex) {
                continue;
            }
            break;
        }
    }
    
    static String getAdapterName(final String s) {
        return s.replace(".", "_") + "_LifecycleAdapter";
    }
    
    @NonNull
    static GenericLifecycleObserver getCallback(final Object o) {
        Label_0071: {
            if (o instanceof GenericLifecycleObserver) {
                break Label_0071;
            }
            try {
                final Class<?> class1 = o.getClass();
                final Constructor constructor = Lifecycling.sCallbackCache.get(class1);
                if (constructor == null) {
                    final Constructor<? extends GenericLifecycleObserver> generatedAdapterConstructor = getGeneratedAdapterConstructor(class1);
                    Constructor<? extends GenericLifecycleObserver> sreflective;
                    if (generatedAdapterConstructor == null) {
                        sreflective = Lifecycling.sREFLECTIVE;
                    }
                    else {
                        sreflective = generatedAdapterConstructor;
                        if (!generatedAdapterConstructor.isAccessible()) {
                            generatedAdapterConstructor.setAccessible(true);
                            sreflective = generatedAdapterConstructor;
                        }
                    }
                    Lifecycling.sCallbackCache.put(class1, sreflective);
                    return (GenericLifecycleObserver)sreflective.newInstance(o);
                }
                return (GenericLifecycleObserver)constructor.newInstance(o);
                return (GenericLifecycleObserver)o;
            }
            catch (final IllegalAccessException cause) {
                throw new RuntimeException(cause);
            }
            catch (final InstantiationException cause2) {
                throw new RuntimeException(cause2);
            }
            catch (final InvocationTargetException cause3) {
                throw new RuntimeException(cause3);
            }
        }
    }
    
    @Nullable
    private static Constructor<? extends GenericLifecycleObserver> getGeneratedAdapterConstructor(final Class<?> clazz) {
        final Package package1 = clazz.getPackage();
        Label_0097: {
            if (package1 != null) {
                break Label_0097;
            }
            String name = "";
            while (true) {
                final String canonicalName = clazz.getCanonicalName();
                Label_0105: {
                    if (canonicalName == null) {
                        break Label_0105;
                    }
                    String substring = canonicalName;
                    if (!name.isEmpty()) {
                        substring = canonicalName.substring(name.length() + 1);
                    }
                    final String adapterName = getAdapterName(substring);
                    try {
                        String string;
                        if (!name.isEmpty()) {
                            string = name + "." + adapterName;
                        }
                        else {
                            string = adapterName;
                        }
                        return (Constructor<? extends GenericLifecycleObserver>)Class.forName(string).getDeclaredConstructor(clazz);
                        name = package1.getName();
                        continue;
                        return null;
                    }
                    catch (final ClassNotFoundException ex) {
                        final Class superclass = clazz.getSuperclass();
                        if (superclass == null) {
                            return null;
                        }
                        return getGeneratedAdapterConstructor(superclass);
                    }
                    catch (final NoSuchMethodException cause) {
                        throw new RuntimeException(cause);
                    }
                }
                break;
            }
        }
    }
}
