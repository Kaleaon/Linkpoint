// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.eventbus;

import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Set;
import java.lang.annotation.Annotation;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.base.Throwables;
import java.util.Iterator;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Collection;
import com.google.common.reflect.TypeToken;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ConcurrentMap;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.Method;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.cache.LoadingCache;

final class SubscriberRegistry
{
    private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache;
    private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache;
    @Weak
    private final EventBus bus;
    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers;
    
    static {
        subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build((CacheLoader<? super Class<?>, ImmutableList<Method>>)new CacheLoader<Class<?>, ImmutableList<Method>>() {
            @Override
            public ImmutableList<Method> load(final Class<?> clazz) throws Exception {
                return getAnnotatedMethodsNotCached(clazz);
            }
        });
        flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build((CacheLoader<? super Class<?>, ImmutableSet<Class<?>>>)new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
            @Override
            public ImmutableSet<Class<?>> load(final Class<?> clazz) {
                return ImmutableSet.copyOf((Collection<? extends Class<?>>)TypeToken.of(clazz).getTypes().rawTypes());
            }
        });
    }
    
    SubscriberRegistry(final EventBus eventBus) {
        this.subscribers = Maps.newConcurrentMap();
        this.bus = Preconditions.checkNotNull(eventBus);
    }
    
    private Multimap<Class<?>, Subscriber> findAllSubscribers(final Object o) {
        final HashMultimap<Object, Object> create = HashMultimap.create();
        for (final Method method : getAnnotatedMethods(o.getClass())) {
            create.put(method.getParameterTypes()[0], Subscriber.create(this.bus, o, method));
        }
        return (Multimap<Class<?>, Subscriber>)create;
    }
    
    @VisibleForTesting
    static ImmutableSet<Class<?>> flattenHierarchy(final Class<?> clazz) {
        try {
            return SubscriberRegistry.flattenHierarchyCache.getUnchecked(clazz);
        }
        catch (final UncheckedExecutionException ex) {
            throw Throwables.propagate(ex.getCause());
        }
    }
    
    private static ImmutableList<Method> getAnnotatedMethods(final Class<?> clazz) {
        return SubscriberRegistry.subscriberMethodsCache.getUnchecked(clazz);
    }
    
    private static ImmutableList<Method> getAnnotatedMethodsNotCached(final Class<?> clazz) {
        final Set<Class<? super T>> rawTypes = TypeToken.of(clazz).getTypes().rawTypes();
        final HashMap<Object, Object> hashMap = Maps.newHashMap();
        final Iterator<Class> iterator = rawTypes.iterator();
        while (iterator.hasNext()) {
            for (final Method method : iterator.next().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    Preconditions.checkArgument(parameterTypes.length == 1, "Method %s has @Subscribe annotation but has %s parameters.Subscriber methods must have exactly 1 parameter.", method, parameterTypes.length);
                    final MethodIdentifier methodIdentifier = new MethodIdentifier(method);
                    if (!hashMap.containsKey(methodIdentifier)) {
                        hashMap.put(methodIdentifier, method);
                    }
                }
            }
        }
        return (ImmutableList<Method>)ImmutableList.copyOf((Collection<?>)hashMap.values());
    }
    
    Iterator<Subscriber> getSubscribers(final Object o) {
        final ImmutableSet<Class<?>> flattenHierarchy = flattenHierarchy(o.getClass());
        final ArrayList<Object> arrayListWithCapacity = Lists.newArrayListWithCapacity(flattenHierarchy.size());
        final Iterator iterator = flattenHierarchy.iterator();
        while (iterator.hasNext()) {
            final CopyOnWriteArraySet set = this.subscribers.get(iterator.next());
            if (set != null) {
                arrayListWithCapacity.add(set.iterator());
            }
        }
        return (Iterator<Subscriber>)Iterators.concat((Iterator<? extends Iterator<?>>)arrayListWithCapacity.iterator());
    }
    
    @VisibleForTesting
    Set<Subscriber> getSubscribersForTesting(final Class<?> clazz) {
        return MoreObjects.firstNonNull(this.subscribers.get(clazz), (Set<Subscriber>)ImmutableSet.of());
    }
    
    void register(final Object o) {
        for (final Map.Entry<Class, V> entry : this.findAllSubscribers(o).asMap().entrySet()) {
            final Class clazz = entry.getKey();
            final Collection c = (Collection)entry.getValue();
            CopyOnWriteArraySet set = this.subscribers.get(clazz);
            if (set == null) {
                final CopyOnWriteArraySet set2 = new CopyOnWriteArraySet();
                set = MoreObjects.firstNonNull(this.subscribers.putIfAbsent(clazz, set2), set2);
            }
            set.addAll(c);
        }
    }
    
    void unregister(final Object obj) {
        for (final Map.Entry<Class, V> entry : this.findAllSubscribers(obj).asMap().entrySet()) {
            final Class clazz = entry.getKey();
            final Collection c = (Collection)entry.getValue();
            final CopyOnWriteArraySet set = this.subscribers.get(clazz);
            if (set != null && set.removeAll(c)) {
                continue;
            }
            throw new IllegalArgumentException("missing event subscriber for an annotated method. Is " + obj + " registered?");
        }
    }
    
    private static final class MethodIdentifier
    {
        private final String name;
        private final List<Class<?>> parameterTypes;
        
        MethodIdentifier(final Method method) {
            this.name = method.getName();
            this.parameterTypes = Arrays.asList(method.getParameterTypes());
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof MethodIdentifier)) {
                return false;
            }
            final MethodIdentifier methodIdentifier = (MethodIdentifier)o;
            if (this.name.equals(methodIdentifier.name) && this.parameterTypes.equals(methodIdentifier.parameterTypes)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.name, this.parameterTypes);
        }
    }
}
