package com.linkpoint.i18n

import android.content.Context
import android.util.Log
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Localization Manager for Linkpoint.
 *
 * Delegates string localization directly to Android's native resource system (`context.getString()`),
 * using standard Android `res/values/strings.xml` and `res/values-{lang}/strings.xml` resource qualifiers.
 */
class LocalizationManager private constructor(private val context: Context) {

    companion object {
        private const val TAG = "LocalizationManager"

        // Supported locales
        private val SUPPORTED_LOCALES = listOf("en", "es", "fr", "de", "ja", "pt", "ru", "zh", "ko", "it", "nl", "pl", "tr")

        @Volatile
        private var instance: LocalizationManager? = null

        fun getInstance(context: Context): LocalizationManager {
            return instance ?: synchronized(this) {
                instance ?: LocalizationManager(context.applicationContext).also { instance = it }
            }
        }
    }

    // Current locale
    private var currentLocale: Locale = Locale.getDefault()

    // Formatters
    private var dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale)
    private var timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, currentLocale)
    private var dateTimeFormat: DateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, currentLocale)
    private var numberFormat: NumberFormat = NumberFormat.getNumberInstance(currentLocale)

    init {
        updateFormatters()
    }

    /**
     * Set the current locale
     */
    fun setLocale(locale: Locale) {
        currentLocale = locale
        updateFormatters()
    }

    /**
     * Set the current locale by language code
     */
    fun setLocale(languageCode: String) {
        currentLocale = Locale(languageCode)
        updateFormatters()
    }

    /**
     * Update formatters for current locale
     */
    private fun updateFormatters() {
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale)
        timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, currentLocale)
        dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, currentLocale)
        numberFormat = NumberFormat.getNumberInstance(currentLocale)
    }

    /**
     * Get current locale
     */
    fun getCurrentLocale(): Locale = currentLocale

    /**
     * Get localized string by key using native context.getString()
     */
    fun getString(key: String): String {
        return getString(key, *emptyArray<Any>())
    }

    /**
     * Get localized string by key with placeholder substitution via context.getString()
     */
    fun getString(key: String, vararg args: Any): String {
        val sanitizedKey = key.replace('.', '_').replace('-', '_')
        var resId = context.resources.getIdentifier(sanitizedKey, "string", context.packageName)
        if (resId == 0) {
            resId = context.resources.getIdentifier(key, "string", context.packageName)
        }
        if (resId == 0) {
            Log.w(TAG, "Missing string resource for key: $key")
            return key
        }
        return try {
            if (args.isEmpty()) {
                context.getString(resId)
            } else {
                context.getString(resId, *args)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to resolve string resource for key: $key", e)
            key
        }
    }

    /**
     * Check if a string key exists in Android resources
     */
    fun hasString(key: String): Boolean {
        val sanitizedKey = key.replace('.', '_').replace('-', '_')
        val resId = context.resources.getIdentifier(sanitizedKey, "string", context.packageName)
        if (resId != 0) return true
        return context.resources.getIdentifier(key, "string", context.packageName) != 0
    }

    /**
     * Format a date
     */
    fun formatDate(date: Date): String = dateFormat.format(date)

    /**
     * Format a time
     */
    fun formatTime(date: Date): String = timeFormat.format(date)

    /**
     * Format a date and time
     */
    fun formatDateTime(date: Date): String = dateTimeFormat.format(date)

    /**
     * Format a date with timezone
     */
    fun formatDateTimeWithZone(date: Date, timeZone: TimeZone): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", currentLocale)
        format.timeZone = timeZone
        return format.format(date)
    }

    /**
     * Format SL time (SLT/PST)
     */
    fun formatSLTime(date: Date): String {
        val sltZone = TimeZone.getTimeZone("America/Los_Angeles")
        val format = SimpleDateFormat("HH:mm", currentLocale)
        format.timeZone = sltZone
        return "${format.format(date)} SLT"
    }

    /**
     * Format a number
     */
    fun formatNumber(number: Number): String = numberFormat.format(number)

    /**
     * Format Linden Dollars (L$)
     */
    fun formatLindenDollars(amount: Int): String = "L\$$amount"

    /**
     * Format Linden Dollars with locale-specific formatting
     */
    fun formatLindenDollarsFormatted(amount: Int): String {
        return "L\$${numberFormat.format(amount)}"
    }

    /**
     * Format a currency amount
     */
    fun formatCurrency(amount: Double, currencyCode: String): String {
        return try {
            val currencyFormat = NumberFormat.getCurrencyInstance(currentLocale)
            currencyFormat.currency = Currency.getInstance(currencyCode)
            currencyFormat.format(amount)
        } catch (e: Exception) {
            "$currencyCode $amount"
        }
    }

    /**
     * Format a percentage
     */
    fun formatPercentage(value: Double): String {
        val percentFormat = NumberFormat.getPercentInstance(currentLocale)
        return percentFormat.format(value)
    }

    /**
     * Format distance in meters
     */
    fun formatDistance(meters: Float): String {
        return when {
            meters < 1f -> {
                val cm = meters * 100
                "${numberFormat.format(cm)} cm"
            }
            meters < 1000f -> {
                "${numberFormat.format(meters)} m"
            }
            else -> {
                val km = meters / 1000
                "${numberFormat.format(km)} km"
            }
        }
    }

    /**
     * Get all supported locales
     */
    fun getAvailableLocales(): List<String> = SUPPORTED_LOCALES

    /**
     * Get all string keys for debugging
     */
    fun getAllKeys(): Set<String> = emptySet()
}
