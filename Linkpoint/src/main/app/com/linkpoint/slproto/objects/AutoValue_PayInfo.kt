package com.linkpoint.slproto.objects
import java.util.*

import androidx.annotation.Nullable
import com.google.common.collect.ImmutableList

class AutoValue_PayInfo : PayInfo {
    private Int defaultPayPrice
    private ImmutableList<Int> payPrices

    AutoValue_PayInfo(Int i, @Nullable ImmutableList<Int> immutableList) {
        this.defaultPayPrice = i
        this.payPrices = immutableList
    }

    Int defaultPayPrice() {
        return this.defaultPayPrice
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PayInfo)) {
            return false
        }
        PayInfo payInfo = (PayInfo) obj
        if (this.defaultPayPrice == payInfo.defaultPayPrice()) {
            return this.payPrices == null ? payInfo.payPrices() == null : this.payPrices.equals(payInfo.payPrices())
        }
        return false
    }

    Int hashCode() {
        return (this.payPrices == null ? 0 : this.payPrices.hashCode()) ^ (1000003 * (this.defaultPayPrice ^ 1000003))
    }

    @Nullable
    ImmutableList<Int> payPrices() {
        return this.payPrices
    }

    String toString() {
        return "PayInfo{defaultPayPrice=" + this.defaultPayPrice + ", " + "payPrices=" + this.payPrices + "}"
    }
}
