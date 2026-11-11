package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SubscriberRegistry {
    @Weak
    private final EventBus bus;
    private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = Maps.newConcurrentMap();
    private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableList<Method>>() { // from class: com.google.common.eventbus.SubscriberRegistry.1
        @Override // com.google.common.cache.CacheLoader
        public ImmutableList<Method> load(Class<?> cls) throws Exception {
            return SubscriberRegistry.getAnnotatedMethodsNotCached(cls);
        }
    });
    private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() { // from class: com.google.common.eventbus.SubscriberRegistry.2
        @Override // com.google.common.cache.CacheLoader
        public ImmutableSet<Class<?>> load(Class<?> cls) {
            return ImmutableSet.copyOf((Collection) TypeToken.of((Class) cls).getTypes().rawTypes());
        }
    });

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class MethodIdentifier {
        private final String name;
        private final List<Class<?>> parameterTypes;

        MethodIdentifier(Method method) {
            this.name = method.getName();
            this.parameterTypes = Arrays.asList(method.getParameterTypes());
        }

        public boolean equals(@Nullable Object obj) {
            if (obj instanceof MethodIdentifier) {
                MethodIdentifier methodIdentifier = (MethodIdentifier) obj;
                return this.name.equals(methodIdentifier.name) && this.parameterTypes.equals(methodIdentifier.parameterTypes);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hashCode(this.name, this.parameterTypes);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SubscriberRegistry(EventBus eventBus) {
        this.bus = (EventBus) Preconditions.checkNotNull(eventBus);
    }

    private Multimap<Class<?>, Subscriber> findAllSubscribers(Object obj) {
        HashMultimap create = HashMultimap.create();
        Iterator it = getAnnotatedMethods(obj.getClass()).iterator();
        while (it.hasNext()) {
            Method method = (Method) it.next();
            create.put(method.getParameterTypes()[0], Subscriber.create(this.bus, obj, method));
        }
        return create;
    }

    @VisibleForTesting
    static ImmutableSet<Class<?>> flattenHierarchy(Class<?> cls) {
        try {
            return flattenHierarchyCache.getUnchecked(cls);
        } catch (UncheckedExecutionException e) {
            throw Throwables.propagate(e.getCause());
        }
    }

    private static ImmutableList<Method> getAnnotatedMethods(Class<?> cls) {
        return subscriberMethodsCache.getUnchecked(cls);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> cls) {
        Method[] declaredMethods;
        Set<Class> rawTypes = TypeToken.of((Class) cls).getTypes().rawTypes();
        HashMap newHashMap = Maps.newHashMap();
        for (Class cls2 : rawTypes) {
            for (Method method : cls2.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Subscribe.class) && !method.isSynthetic()) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Preconditions.checkArgument(parameterTypes.length == 1, "Method %s has @Subscribe annotation but has %s parameters.Subscriber methods must have exactly 1 parameter.", method, Integer.valueOf(parameterTypes.length));
                    MethodIdentifier methodIdentifier = new MethodIdentifier(method);
                    if (!newHashMap.containsKey(methodIdentifier)) {
                        newHashMap.put(methodIdentifier, method);
                    }
                }
            }
        }
        return ImmutableList.copyOf(newHashMap.values());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Iterator<Subscriber> getSubscribers(Object obj) {
        ImmutableSet<Class<?>> flattenHierarchy = flattenHierarchy(obj.getClass());
        ArrayList newArrayListWithCapacity = Lists.newArrayListWithCapacity(flattenHierarchy.size());
        Iterator it = flattenHierarchy.iterator();
        while (it.hasNext()) {
            CopyOnWriteArraySet<Subscriber> copyOnWriteArraySet = this.subscribers.get((Class) it.next());
            if (copyOnWriteArraySet != null) {
                newArrayListWithCapacity.add(copyOnWriteArraySet.iterator());
            }
        }
        return Iterators.concat(newArrayListWithCapacity.iterator());
    }

    @VisibleForTesting
    Set<Subscriber> getSubscribersForTesting(Class<?> cls) {
        return (Set) MoreObjects.firstNonNull(this.subscribers.get(cls), ImmutableSet.of());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void register(Object obj) {
        for (Map.Entry<Class<?>, Collection<Subscriber>> entry : findAllSubscribers(obj).asMap().entrySet()) {
            Class<?> key = entry.getKey();
            Collection<Subscriber> value = entry.getValue();
            CopyOnWriteArraySet<Subscriber> copyOnWriteArraySet = this.subscribers.get(key);
            if (copyOnWriteArraySet == null) {
                CopyOnWriteArraySet<Subscriber> copyOnWriteArraySet2 = new CopyOnWriteArraySet<>();
                copyOnWriteArraySet = (CopyOnWriteArraySet) MoreObjects.firstNonNull(this.subscribers.putIfAbsent(key, copyOnWriteArraySet2), copyOnWriteArraySet2);
            }
            copyOnWriteArraySet.addAll(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:6:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void unregister(java.lang.Object r5) {
        /*
            r4 = this;
            com.google.common.collect.Multimap r0 = r4.findAllSubscribers(r5)
            java.util.Map r0 = r0.asMap()
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r2 = r0.iterator()
        L10:
            boolean r0 = r2.hasNext()
            if (r0 != 0) goto L17
            return
        L17:
            java.lang.Object r0 = r2.next()
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
            java.lang.Object r1 = r0.getKey()
            java.lang.Class r1 = (java.lang.Class) r1
            java.lang.Object r0 = r0.getValue()
            java.util.Collection r0 = (java.util.Collection) r0
            java.util.concurrent.ConcurrentMap<java.lang.Class<?>, java.util.concurrent.CopyOnWriteArraySet<com.google.common.eventbus.Subscriber>> r3 = r4.subscribers
            java.lang.Object r1 = r3.get(r1)
            java.util.concurrent.CopyOnWriteArraySet r1 = (java.util.concurrent.CopyOnWriteArraySet) r1
            if (r1 != 0) goto L54
        L33:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "missing event subscriber for an annotated method. Is "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r5)
            java.lang.String r2 = " registered?"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L54:
            boolean r0 = r1.removeAll(r0)
            if (r0 == 0) goto L33
            goto L10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.eventbus.SubscriberRegistry.unregister(java.lang.Object):void");
    }
}
