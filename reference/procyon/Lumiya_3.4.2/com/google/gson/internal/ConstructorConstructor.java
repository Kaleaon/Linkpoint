// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedHashSet;
import java.util.Set;
import com.google.gson.JsonIOException;
import java.util.EnumSet;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.LinkedHashMap;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;
import java.util.Map;

public final class ConstructorConstructor
{
    private final Map<Type, InstanceCreator<?>> instanceCreators;
    
    public ConstructorConstructor(final Map<Type, InstanceCreator<?>> instanceCreators) {
        this.instanceCreators = instanceCreators;
    }
    
    private <T> ObjectConstructor<T> newDefaultConstructor(final Class<? super T> clazz) {
        try {
            final Constructor<? super T> declaredConstructor = clazz.getDeclaredConstructor((Class<?>[])new Class[0]);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    try {
                        return declaredConstructor.newInstance((Object[])null);
                    }
                    catch (final InstantiationException cause) {
                        throw new RuntimeException("Failed to invoke " + declaredConstructor + " with no args", cause);
                    }
                    catch (final InvocationTargetException ex) {
                        throw new RuntimeException("Failed to invoke " + declaredConstructor + " with no args", ex.getTargetException());
                    }
                    catch (final IllegalAccessException detailMessage) {
                        throw new AssertionError((Object)detailMessage);
                    }
                }
            };
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, final Class<? super T> clazz) {
        if (!Collection.class.isAssignableFrom(clazz)) {
            if (!Map.class.isAssignableFrom(clazz)) {
                return null;
            }
            if (ConcurrentNavigableMap.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new ConcurrentSkipListMap();
                    }
                };
            }
            if (ConcurrentMap.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new ConcurrentHashMap();
                    }
                };
            }
            if (SortedMap.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new TreeMap();
                    }
                };
            }
            if (type instanceof ParameterizedType && !String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType())) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new LinkedHashMap();
                    }
                };
            }
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return (T)new LinkedTreeMap();
                }
            };
        }
        else {
            if (SortedSet.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new TreeSet();
                    }
                };
            }
            if (EnumSet.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        if (!(type instanceof ParameterizedType)) {
                            throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                        }
                        final Type type = ((ParameterizedType)type).getActualTypeArguments()[0];
                        if (!(type instanceof Class)) {
                            throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                        }
                        return (T)EnumSet.noneOf((Class<Enum>)type);
                    }
                };
            }
            if (Set.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new LinkedHashSet();
                    }
                };
            }
            if (!Queue.class.isAssignableFrom(clazz)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T)new ArrayList();
                    }
                };
            }
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return (T)new ArrayDeque();
                }
            };
        }
    }
    
    private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> clazz) {
        return new ObjectConstructor<T>() {
            private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
            
            @Override
            public T construct() {
                try {
                    return this.unsafeAllocator.newInstance(clazz);
                }
                catch (final Exception cause) {
                    throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", cause);
                }
            }
        };
    }
    
    public <T> ObjectConstructor<T> get(final TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        final Class rawType = typeToken.getRawType();
        final InstanceCreator instanceCreator = this.instanceCreators.get(type);
        if (instanceCreator != null) {
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return instanceCreator.createInstance(type);
                }
            };
        }
        final InstanceCreator instanceCreator2 = this.instanceCreators.get(rawType);
        if (instanceCreator2 != null) {
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return instanceCreator2.createInstance(type);
                }
            };
        }
        final ObjectConstructor<Object> defaultConstructor = this.newDefaultConstructor(rawType);
        if (defaultConstructor != null) {
            return (ObjectConstructor<T>)defaultConstructor;
        }
        final ObjectConstructor<Object> defaultImplementationConstructor = this.newDefaultImplementationConstructor(type, rawType);
        if (defaultImplementationConstructor == null) {
            return (ObjectConstructor<T>)this.newUnsafeAllocator(type, rawType);
        }
        return (ObjectConstructor<T>)defaultImplementationConstructor;
    }
    
    @Override
    public String toString() {
        return this.instanceCreators.toString();
    }
}
