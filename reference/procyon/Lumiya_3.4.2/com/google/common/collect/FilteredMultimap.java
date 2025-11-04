// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Map;
import com.google.common.base.Predicate;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface FilteredMultimap<K, V> extends Multimap<K, V>
{
    Predicate<? super Map.Entry<K, V>> entryPredicate();
    
    Multimap<K, V> unfiltered();
}
