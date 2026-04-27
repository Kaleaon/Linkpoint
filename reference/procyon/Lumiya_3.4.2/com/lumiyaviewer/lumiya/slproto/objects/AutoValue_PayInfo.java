// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import android.support.annotation.Nullable;
import com.google.common.collect.ImmutableList;

final class AutoValue_PayInfo extends PayInfo
{
    private final int defaultPayPrice;
    private final ImmutableList<Integer> payPrices;
    
    AutoValue_PayInfo(final int defaultPayPrice, @Nullable final ImmutableList<Integer> payPrices) {
        this.defaultPayPrice = defaultPayPrice;
        this.payPrices = payPrices;
    }
    
    @Override
    public int defaultPayPrice() {
        return this.defaultPayPrice;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (o == this) {
            return true;
        }
        if (o instanceof PayInfo) {
            final PayInfo payInfo = (PayInfo)o;
            if (this.defaultPayPrice == payInfo.defaultPayPrice()) {
                if (this.payPrices == null) {
                    if (payInfo.payPrices() != null) {
                        equals = false;
                    }
                }
                else {
                    equals = this.payPrices.equals(payInfo.payPrices());
                }
            }
            else {
                equals = false;
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int defaultPayPrice = this.defaultPayPrice;
        int hashCode;
        if (this.payPrices == null) {
            hashCode = 0;
        }
        else {
            hashCode = this.payPrices.hashCode();
        }
        return hashCode ^ 1000003 * (defaultPayPrice ^ 0xF4243);
    }
    
    @Nullable
    @Override
    public ImmutableList<Integer> payPrices() {
        return this.payPrices;
    }
    
    @Override
    public String toString() {
        return "PayInfo{defaultPayPrice=" + this.defaultPayPrice + ", " + "payPrices=" + this.payPrices + "}";
    }
}
