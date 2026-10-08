package com.linkpoint.economy

import java.text.NumberFormat
import java.util.Locale

/**
 * Global Currency Formatter for Linkpoint
 *
 * Provides thread-safe, locale-aware currency and transaction amount formatting
 * with dynamic grid currency symbol support (e.g. L$, OS$) and zero-currency mode handling.
 */
object CurrencyFormatter {

    /**
     * Format a numerical monetary balance or transaction amount.
     *
     * @param amount Numeric balance or transaction amount
     * @param symbol Currency symbol string (default: "L$")
     * @param isZeroCurrency True if active grid operates in zero-currency mode
     * @param showSymbol True to append/prepend the symbol to formatted number
     * @param showSign True to prepend '+' or '−' sign for non-zero values
     * @return Formatted string (e.g. "L$ 1,500", "OS$ 50", "No Currency System")
     */
    fun formatCurrency(
        amount: Number?,
        symbol: String = "L$",
        isZeroCurrency: Boolean = false,
        showSymbol: Boolean = true,
        showSign: Boolean = false
    ): String {
        if (isZeroCurrency) {
            return "No Currency System"
        }

        if (amount == null) {
            val sym = symbol.trim().ifEmpty { "L$" }
            return if (showSymbol) "$sym 0" else "0"
        }

        val longVal = amount.toLong()
        val absVal = Math.abs(longVal)
        val nf = NumberFormat.getNumberInstance(Locale.US)
        val formattedNum = nf.format(absVal)

        val prefix = when {
            showSign -> if (longVal > 0) "+" else if (longVal < 0) "−" else ""
            longVal < 0 -> "−"
            else -> ""
        }

        val activeSymbol = symbol.trim().ifEmpty { "L$" }
        return if (showSymbol) {
            "$prefix$activeSymbol $formattedNum".trim()
        } else {
            "$prefix$formattedNum".trim()
        }
    }

    /**
     * Helper method to format transaction amount rows (e.g. "+L$ 50", "−OS$ 100").
     */
    fun formatTransactionAmount(
        amount: Number?,
        symbol: String = "L$",
        isZeroCurrency: Boolean = false
    ): String {
        return formatCurrency(
            amount = amount,
            symbol = symbol,
            isZeroCurrency = isZeroCurrency,
            showSymbol = true,
            showSign = true
        )
    }
}
