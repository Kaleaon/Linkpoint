package android.arch.lifecycle;

import android.arch.lifecycle.Lifecycle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ReflectiveGenericLifecycleObserver implements GenericLifecycleObserver {
    private static final int CALL_TYPE_NO_ARG = 0;
    private static final int CALL_TYPE_PROVIDER = 1;
    private static final int CALL_TYPE_PROVIDER_WITH_EVENT = 2;
    static final Map<Class, CallbackInfo> sInfoCache = new HashMap();
    private final CallbackInfo mInfo = getInfo(this.mWrapped.getClass());
    private final Object mWrapped;

    static class CallbackInfo {
        final Map<Lifecycle.Event, List<MethodReference>> mEventToHandlers = new HashMap();
        final Map<MethodReference, Lifecycle.Event> mHandlerToEvent;

        CallbackInfo(Map<MethodReference, Lifecycle.Event> map) {
            this.mHandlerToEvent = map;
            for (Map.Entry next : map.entrySet()) {
                Lifecycle.Event event = (Lifecycle.Event) next.getValue();
                List list = this.mEventToHandlers.get(event);
                if (list == null) {
                    list = new ArrayList();
                    this.mEventToHandlers.put(event, list);
                }
                list.add(next.getKey());
            }
        }
    }

    static class MethodReference {
        final int mCallType;
        final Method mMethod;

        MethodReference(int i, Method method) {
            this.mCallType = i;
            this.mMethod = method;
            this.mMethod.setAccessible(true);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MethodReference methodReference = (MethodReference) obj;
            return this.mCallType == methodReference.mCallType && this.mMethod.getName().equals(methodReference.mMethod.getName());
        }

        public int hashCode() {
            return (this.mCallType * 31) + this.mMethod.getName().hashCode();
        }
    }

    ReflectiveGenericLifecycleObserver(Object obj) {
        this.mWrapped = obj;
    }

    private static CallbackInfo createInfo(Class cls) {
        CallbackInfo info;
        int i;
        Class superclass = cls.getSuperclass();
        HashMap hashMap = new HashMap();
        if (!(superclass == null || (info = getInfo(superclass)) == null)) {
            hashMap.putAll(info.mHandlerToEvent);
        }
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Class info2 : cls.getInterfaces()) {
            for (Map.Entry next : getInfo(info2).mHandlerToEvent.entrySet()) {
                verifyAndPutHandler(hashMap, (MethodReference) next.getKey(), (Lifecycle.Event) next.getValue(), cls);
            }
        }
        for (Method method : declaredMethods) {
            OnLifecycleEvent onLifecycleEvent = (OnLifecycleEvent) method.getAnnotation(OnLifecycleEvent.class);
            if (onLifecycleEvent != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length <= 0) {
                    i = 0;
                } else if (parameterTypes[0].isAssignableFrom(LifecycleOwner.class)) {
                    i = 1;
                } else {
                    throw new IllegalArgumentException("invalid parameter type. Must be one and instanceof LifecycleOwner");
                }
                Lifecycle.Event value = onLifecycleEvent.value();
                if (parameterTypes.length > 1) {
                    if (!parameterTypes[1].isAssignableFrom(Lifecycle.Event.class)) {
                        throw new IllegalArgumentException("invalid parameter type. second arg must be an event");
                    } else if (value == Lifecycle.Event.ON_ANY) {
                        i = 2;
                    } else {
                        throw new IllegalArgumentException("Second arg is supported only for ON_ANY value");
                    }
                }
                if (parameterTypes.length <= 2) {
                    verifyAndPutHandler(hashMap, new MethodReference(i, method), value, cls);
                } else {
                    throw new IllegalArgumentException("cannot have more than 2 params");
                }
            }
        }
        CallbackInfo callbackInfo = new CallbackInfo(hashMap);
        sInfoCache.put(cls, callbackInfo);
        return callbackInfo;
    }

    private static CallbackInfo getInfo(Class cls) {
        CallbackInfo callbackInfo = sInfoCache.get(cls);
        return callbackInfo == null ? createInfo(cls) : callbackInfo;
    }

    private void invokeCallback(MethodReference methodReference, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        try {
            switch (methodReference.mCallType) {
                case 0:
                    methodReference.mMethod.invoke(this.mWrapped, new Object[0]);
                    return;
                case 1:
                    methodReference.mMethod.invoke(this.mWrapped, new Object[]{lifecycleOwner});
                    return;
                case 2:
                    methodReference.mMethod.invoke(this.mWrapped, new Object[]{lifecycleOwner, event});
                    return;
                default:
                    return;
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to call observer method", e.getCause());
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
    }

    private void invokeCallbacks(CallbackInfo callbackInfo, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        invokeMethodsForEvent(callbackInfo.mEventToHandlers.get(event), lifecycleOwner, event);
        invokeMethodsForEvent(callbackInfo.mEventToHandlers.get(Lifecycle.Event.ON_ANY), lifecycleOwner, event);
    }

    private void invokeMethodsForEvent(List<MethodReference> list, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (list != null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                invokeCallback(list.get(size), lifecycleOwner, event);
            }
        }
    }

    private static void verifyAndPutHandler(Map<MethodReference, Lifecycle.Event> map, MethodReference methodReference, Lifecycle.Event event, Class cls) {
        Lifecycle.Event event2 = map.get(methodReference);
        if (event2 != null && event != event2) {
            throw new IllegalArgumentException("Method " + methodReference.mMethod.getName() + " in " + cls.getName() + " already declared with different @OnLifecycleEvent value: previous" + " value " + event2 + ", new value " + event);
        } else if (event2 == null) {
            map.put(methodReference, event);
        }
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        invokeCallbacks(this.mInfo, lifecycleOwner, event);
    }
}
