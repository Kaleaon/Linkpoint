package com.linkpoint.slproto.objects

import com.google.common.collect.ImmutableList
import com.google.common.primitives.Ints

data class PayInfo(
    val defaultPayPrice: Int,
    val payPrices: ImmutableList<Int>?,
) {
    companion object {
        const val MAX_PAY_PRICES = 4

        @JvmStatic
        fun create(
            defaultPayPrice: Int,
            payPrices: IntArray?,
        ): PayInfo {
            val pricesList = payPrices?.let { ImmutableList.copyOf(Ints.asList(it)) }
            return PayInfo(defaultPayPrice, pricesList)
        }
    }
}
