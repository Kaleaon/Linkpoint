package com.linkpoint.slproto.objects

import com.google.common.collect.ImmutableList
import com.google.common.primitives.Ints

abstract class PayInfo {
    abstract fun defaultPayPrice(): Int
    abstract fun payPrices(): ImmutableList<Int>?

    companion object {
        const val MAX_PAY_PRICES = 4

        @JvmStatic
        fun create(defaultPayPrice: Int, payPricesArray: IntArray?): PayInfo {
            val payPrices = payPricesArray?.let { ImmutableList.copyOf(Ints.asList(it)) }
            return AutoValue_PayInfo(defaultPayPrice, payPrices)
        }
    }
}