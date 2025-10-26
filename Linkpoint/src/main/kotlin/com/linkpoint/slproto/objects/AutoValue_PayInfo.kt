package com.linkpoint.slproto.objects
import java.util.*

import android.support.annotation.Nullable
import com.google.common.collect.ImmutableList

final class AutoValue_PayInfo : PayInfo() {
    private val Int defaultPayPrice
    private val ImmutableList<Integer> payPrices

    AutoValue_PayInfo(Int i, ImmutableList<Integer> immutableList) {
        this.defaultPayPrice = i
        this.payPrices = immutableList
    }

     public fun defaultPayPrice(): Int {
        return this.defaultPayPrice
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PayInfo)) {
            return false
        }
        val payInfo: PayInfo = (PayInfo) obj
        if (this.defaultPayPrice == payInfo.defaultPayPrice()) {
            return this.payPrices == null ? payInfo.payPrices() == null : this.payPrices.equals(payInfo.payPrices())
        }
        return false
    }

     public override fun hashCode(): Int {
        return (this.payPrices == null ? 0 : this.payPrices.hashCode()) ^ (1000003 * (this.defaultPayPrice ^ 1000003))
    }

    public ImmutableList<Integer> payPrices() {
        return this.payPrices
    }

     public override fun toString(): String {
        return "PayInfo{defaultPayPrice=" + this.defaultPayPrice + ", " + "payPrices=" + this.payPrices + "}"
    }
}
