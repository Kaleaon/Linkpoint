// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.util.HashMap;
import com.google.common.primitives.Primitives;
import java.util.Map;

public final class MutableClassToInstanceMap<B> extends ConstrainedMap<Class<? extends B>, B> implements ClassToInstanceMap<B>
{
    private static final MapConstraint<Class<?>, Object> VALUE_CAN_BE_CAST_TO_KEY;
    private static final long serialVersionUID = 0L;
    
    static {
        VALUE_CAN_BE_CAST_TO_KEY = new MapConstraint<Class<?>, Object>() {
            @Override
            public void checkKeyValue(final Class<?> clazz, final Object o) {
                cast(clazz, o);
            }
        };
    }
    
    private MutableClassToInstanceMap(final Map<Class<? extends B>, B> map) {
        super(map, MutableClassToInstanceMap.VALUE_CAN_BE_CAST_TO_KEY);
    }
    
    private static <B, T extends B> T cast(final Class<T> clazz, final B obj) {
        return Primitives.wrap(clazz).cast(obj);
    }
    
    public static <B> MutableClassToInstanceMap<B> create() {
        return new MutableClassToInstanceMap<B>(new HashMap<Class<? extends B>, B>());
    }
    
    public static <B> MutableClassToInstanceMap<B> create(final Map<Class<? extends B>, B> map) {
        return new MutableClassToInstanceMap<B>(map);
    }
    
    @Override
    public <T extends B> T getInstance(final Class<T> clazz) {
        return cast(clazz, this.get(clazz));
    }
    
    @Override
    public <T extends B> T putInstance(final Class<T> clazz, final T t) {
        return cast(clazz, this.put((Class<? extends B>)clazz, t));
    }
}
