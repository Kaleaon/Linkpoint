package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
@GwtCompatible
/* loaded from: classes.dex */
interface FilteredSetMultimap<K, V> extends FilteredMultimap<K, V>, SetMultimap<K, V> {
    @Override // com.google.common.collect.FilteredMultimap
    SetMultimap<K, V> unfiltered();
}
