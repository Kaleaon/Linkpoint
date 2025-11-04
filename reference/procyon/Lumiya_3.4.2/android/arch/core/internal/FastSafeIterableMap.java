// 
// Decompiled by Procyon v0.6.0
// 

package android.arch.core.internal;

import android.support.annotation.NonNull;
import java.util.Map;
import java.util.HashMap;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class FastSafeIterableMap<K, V> extends SafeIterableMap<K, V>
{
    private HashMap<K, Entry<K, V>> mHashMap;
    
    public FastSafeIterableMap() {
        this.mHashMap = new HashMap<K, Entry<K, V>>();
    }
    
    public Map.Entry<K, V> ceil(final K key) {
        if (!this.contains(key)) {
            return null;
        }
        return this.mHashMap.get(key).mPrevious;
    }
    
    public boolean contains(final K key) {
        return this.mHashMap.containsKey(key);
    }
    
    @Override
    protected Entry<K, V> get(final K key) {
        return this.mHashMap.get(key);
    }
    
    @Override
    public V putIfAbsent(@NonNull final K key, @NonNull final V v) {
        final Entry<K, V> value = this.get(key);
        if (value == null) {
            this.mHashMap.put(key, this.put(key, v));
            return null;
        }
        return value.mValue;
    }
    
    @Override
    public V remove(@NonNull final K key) {
        final V remove = super.remove(key);
        this.mHashMap.remove(key);
        return remove;
    }
}
