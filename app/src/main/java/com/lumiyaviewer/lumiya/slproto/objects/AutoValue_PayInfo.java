package com.lumiyaviewer.lumiya.slproto.objects;
import java.util.*;

import android.support.annotation.Nullable;
import com.google.common.collect.ImmutableList;

final class AutoValue_PayInfo extends PayInfo {
    private final int defaultPayPrice;
    private final ImmutableList<Integer> payPrices;

    AutoValue_PayInfo(int i, @Nullable ImmutableList<Integer> immutableList) {
        this.defaultPayPrice = i;
        this.payPrices = immutableList;
    }

    public int defaultPayPrice() {
        return this.defaultPayPrice;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PayInfo)) {
            return false;
        }
        PayInfo payInfo = (PayInfo) obj;
        if (this.defaultPayPrice == payInfo.defaultPayPrice()) {
            return this.payPrices == null ? payInfo.payPrices() == null : this.payPrices.equals(payInfo.payPrices());
        }
        return false;
    }

    public int hashCode() {
        return (this.payPrices == null ? 0 : this.payPrices.hashCode()) ^ (1000003 * (this.defaultPayPrice ^ 1000003));
    }

    @Nullable
    public ImmutableList<Integer> payPrices() {
        return this.payPrices;
    }

    public String toString() {
        return "PayInfo{defaultPayPrice=" + this.defaultPayPrice + ", " + "payPrices=" + this.payPrices + "}";
    }
}
