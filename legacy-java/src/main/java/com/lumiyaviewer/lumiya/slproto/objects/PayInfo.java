package com.lumiyaviewer.lumiya.slproto.objects;

import android.support.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import java.util.Collection;

/* loaded from: classes.dex */
public abstract class PayInfo {
    public static final int MAX_PAY_PRICES = 4;

    public static PayInfo create(int i, int[] iArr) {
        return new AutoValue_PayInfo(i, iArr != null ? ImmutableList.copyOf((Collection) Ints.asList(iArr)) : null);
    }

    public abstract int defaultPayPrice();

    @Nullable
    public abstract ImmutableList<Integer> payPrices();
}
