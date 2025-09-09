package android.arch.core.internal;

import android.arch.core.internal.SafeIterableMap;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.util.HashMap;
import java.util.Map;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class FastSafeIterableMap<K, V> extends SafeIterableMap<K, V> {
    private HashMap<K, SafeIterableMap.Entry<K, V>> mHashMap = new HashMap<>();

    public Map.Entry<K, V> ceil(K k) {
        if (!contains(k)) {
            return null;
        }
        return this.mHashMap.get(k).mPrevious;
    }

    public boolean contains(K k) {
        return this.mHashMap.containsKey(k);
    }

    /* access modifiers changed from: protected */
    public SafeIterableMap.Entry<K, V> get(K k) {
        return this.mHashMap.get(k);
    }

    public V putIfAbsent(@NonNull K k, @NonNull V v) {
        SafeIterableMap.Entry entry = get(k);
        if (entry != null) {
            return entry.mValue;
        }
        this.mHashMap.put(k, put(k, v));
        return null;
    }

    public V remove(@NonNull K k) {
        V remove = super.remove(k);
        this.mHashMap.remove(k);
        return remove;
    }
}
