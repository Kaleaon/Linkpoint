package com.lumiyaviewer.lumiya.slproto.objects;

import android.support.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

public abstract class PayInfo {
    public static final int MAX_PAY_PRICES = 4;

    public static PayInfo create(int i, int[] iArr) {
        ImmutableList<Integer> immutableList = null;
        if (iArr != null) {
            immutableList = ImmutableList.copyOf(Ints.asList(iArr));
        }
        return new AutoValue_PayInfo(i, immutableList);
    }

    public abstract int defaultPayPrice();

    @Nullable
    public abstract ImmutableList<Integer> payPrices();
}
