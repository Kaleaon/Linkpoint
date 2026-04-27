// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.escape;

import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import com.google.common.base.Preconditions;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class ArrayBasedEscaperMap
{
    private static final char[][] EMPTY_REPLACEMENT_ARRAY;
    private final char[][] replacementArray;
    
    static {
        EMPTY_REPLACEMENT_ARRAY = new char[0][0];
    }
    
    private ArrayBasedEscaperMap(final char[][] replacementArray) {
        this.replacementArray = replacementArray;
    }
    
    public static ArrayBasedEscaperMap create(final Map<Character, String> map) {
        return new ArrayBasedEscaperMap(createReplacementArray(map));
    }
    
    @VisibleForTesting
    static char[][] createReplacementArray(final Map<Character, String> map) {
        Preconditions.checkNotNull(map);
        if (!map.isEmpty()) {
            final char[][] array = new char[(char)Collections.max((Collection<? extends Character>)map.keySet()) + '\u0001'][];
            for (final char charValue : map.keySet()) {
                array[charValue] = ((String)map.get(charValue)).toCharArray();
            }
            return array;
        }
        return ArrayBasedEscaperMap.EMPTY_REPLACEMENT_ARRAY;
    }
    
    char[][] getReplacementArray() {
        return this.replacementArray;
    }
}
