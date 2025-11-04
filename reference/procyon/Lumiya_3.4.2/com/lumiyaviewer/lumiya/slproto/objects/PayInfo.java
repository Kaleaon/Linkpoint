// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import android.support.annotation.Nullable;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

public abstract class PayInfo
{
    public static final int MAX_PAY_PRICES = 4;
    
    public static PayInfo create(final int n, final int[] array) {
        ImmutableList<Integer> copy = null;
        if (array != null) {
            copy = ImmutableList.copyOf((Collection<? extends Integer>)Ints.asList(array));
        }
        return new AutoValue_PayInfo(n, copy);
    }
    
    public abstract int defaultPayPrice();
    
    @Nullable
    public abstract ImmutableList<Integer> payPrices();
}
