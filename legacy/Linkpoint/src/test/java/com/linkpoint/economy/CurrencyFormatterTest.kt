package com.linkpoint.economy

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testFormatCurrencyStandard() {
        assertEquals("L$ 1,500", CurrencyFormatter.formatCurrency(1500, "L$"))
        assertEquals("OS$ 250", CurrencyFormatter.formatCurrency(250, "OS$"))
        assertEquals("D$ 0", CurrencyFormatter.formatCurrency(0, "D$"))
    }

    @Test
    fun testFormatCurrencyZeroCurrencyMode() {
        assertEquals("No Currency System", CurrencyFormatter.formatCurrency(1000, "L$", isZeroCurrency = true))
        assertEquals("No Currency System", CurrencyFormatter.formatCurrency(0, "OS$", isZeroCurrency = true))
    }

    @Test
    fun testFormatTransactionAmount() {
        assertEquals("+L$ 50", CurrencyFormatter.formatTransactionAmount(50, "L$"))
        assertEquals("−L$ 100", CurrencyFormatter.formatTransactionAmount(-100, "L$"))
        assertEquals("No Currency System", CurrencyFormatter.formatTransactionAmount(50, "L$", isZeroCurrency = true))
    }
}
