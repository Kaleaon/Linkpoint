package com.linkpoint.i18n

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
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
 * Provides internationalization (i18n) support including:
 * - String localization with placeholder substitution
 * - Date/time formatting
 * - Currency formatting (including L$ Linden Dollars)
 * - Number formatting
 * 
 * Based on patterns from the Second Life mobile viewer's localization system.
 */
class LocalizationManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "LocalizationManager"
        private const val LOCALIZATION_PATH = "localization"
        
        // Supported locales
        private val SUPPORTED_LOCALES = listOf("en", "es", "fr")
        
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
    
    // String resources by locale
    private val stringResources = mutableMapOf<String, MutableMap<String, String>>()
    
    // Formatters
    private var dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale)
    private var timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, currentLocale)
    private var dateTimeFormat: DateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, currentLocale)
    private var numberFormat: NumberFormat = NumberFormat.getNumberInstance(currentLocale)
    
    init {
        loadAllLocales()
        updateFormatters()
    }
    
    /**
     * Load string resources for all supported locales
     */
    private fun loadAllLocales() {
        SUPPORTED_LOCALES.forEach { locale ->
            loadLocale(locale)
        }
    }
    
    /**
     * Load string resources for a specific locale
     */
    private fun loadLocale(locale: String) {
        try {
            val filename = "$LOCALIZATION_PATH/$locale.json"
            context.assets.open(filename).use { inputStream ->
                val content = inputStream.bufferedReader().readText()
                val json = JSONObject(content)
                val stringsArray = json.getJSONArray("strings")
                
                val localeStrings = mutableMapOf<String, String>()
                for (i in 0 until stringsArray.length()) {
                    val item = stringsArray.getJSONObject(i)
                    val key = item.getString("key")
                    val value = item.getString("value")
                    localeStrings[key] = value
                }
                
                stringResources[locale] = localeStrings
                Log.i(TAG, "Loaded ${localeStrings.size} strings for locale: $locale")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load locale: $locale", e)
        }
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
     * Get localized string by key
     */
    fun getString(key: String): String {
        return getString(key, emptyArray())
    }
    
    /**
     * Get localized string by key with placeholder substitution
     * Placeholders in format {0}, {1}, etc.
     */
    fun getString(key: String, vararg args: Any): String {
        val localeCode = currentLocale.language
        
        // Try current locale first
        var value = stringResources[localeCode]?.get(key)
        
        // Fall back to English
        if (value == null && localeCode != "en") {
            value = stringResources["en"]?.get(key)
        }
        
        // If still not found, return the key
        if (value == null) {
            Log.w(TAG, "Missing string for key: $key")
            return key
        }
        
        // Substitute placeholders
        return substitutePlaceholders(value, args)
    }
    
    /**
     * Substitute placeholders in format string
     */
    private fun substitutePlaceholders(format: String, args: Array<out Any>): String {
        var result = format
        args.forEachIndexed { index, arg ->
            val placeholder = "{$index}"
            result = result.replace(placeholder, arg.toString())
        }
        return result
    }
    
    /**
     * Check if a string key exists
     */
    fun hasString(key: String): Boolean {
        val localeCode = currentLocale.language
        return stringResources[localeCode]?.containsKey(key) == true ||
               stringResources["en"]?.containsKey(key) == true
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
     * Format distance in meters (converts to appropriate unit based on locale)
     */
    fun formatDistance(meters: Float): String {
        return when {
            meters < 1f -> getString("distance.meters", "%.1f".format(meters * 100), "cm")
            meters < 1000f -> getString("distance.meters", "%.1f".format(meters), "m")
            else -> getString("distance.kilometers", "%.2f".format(meters / 1000), "km")
        }.let { if (!hasString("distance.meters")) "%.1f m".format(meters) else it }
    }
    
    /**
     * Get all available locales
     */
    fun getAvailableLocales(): List<String> = stringResources.keys.toList()
    
    /**
     * Get all string keys for debugging
     */
    fun getAllKeys(): Set<String> {
        return stringResources.values.flatMap { it.keys }.toSet()
    }
}
