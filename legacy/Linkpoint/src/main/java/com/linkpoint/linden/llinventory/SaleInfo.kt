package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLSD

const val DEFAULT_SALE_PRICE: Int = 10

enum class SaleType(val value: Int, val typeName: String) {
    NOT(0, "not"),
    ORIG(1, "orig"),
    COPY(2, "copy"),
    CONTENTS(3, "cntn");

    companion object {
        private val byValue: Map<Int, SaleType> = entries.associateBy { it.value }
        private val byName: Map<String, SaleType> = entries.associateBy { it.typeName }

        fun fromValue(value: Int): SaleType = byValue[value] ?: NOT
        fun fromName(name: String): SaleType = byName[name] ?: NOT
    }
}

data class SaleInfo(
    val saleType: SaleType = SaleType.NOT,
    val salePrice: Int = DEFAULT_SALE_PRICE
) {
    val isForSale: Boolean get() = saleType != SaleType.NOT

    fun toSD(): LLSD = LLSD.map(
        "sale_type" to LLSD.string(saleType.typeName),
        "sale_price" to LLSD.integer(salePrice)
    )

    fun accumulate(other: SaleInfo): SaleInfo {
        val mergedType = if (saleType == other.saleType) saleType else SaleType.NOT
        return SaleInfo(mergedType, salePrice + other.salePrice)
    }

    companion object {
        val DEFAULT = SaleInfo(SaleType.NOT, DEFAULT_SALE_PRICE)

        fun fromSD(sd: LLSD): SaleInfo {
            val type = SaleType.fromName(sd["sale_type"].asString())
            val price = sd["sale_price"].asInteger()
            return SaleInfo(type, price)
        }
    }
}
