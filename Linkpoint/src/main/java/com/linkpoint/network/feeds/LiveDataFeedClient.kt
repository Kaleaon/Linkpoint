package com.linkpoint.network.feeds

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Client for the Linden Lab "Live Data Feeds" published at
 * https://api.secondlife.com/datafeeds/ plus the public Status page
 * incident history at https://status.secondlifegrid.net/history.rss.
 *
 * Per the wiki page, polite consumers should poll no more than once per
 * 15 minutes for the LindeX feed and no more than once per 5 minutes for
 * the homepage stats feed. The status RSS is conservatively polled every
 * 5 minutes; LL's status page updates when incidents occur.
 *
 * Each fetch is cached in memory and surfaced as a [StateFlow] so the
 * Compose UI can subscribe without each call site re-fetching. The
 * client is safe to call before login (no UDP / capability dependencies).
 */
class LiveDataFeedClient(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build(),
) {
    companion object {
        private const val TAG = "LiveDataFeedClient"
        private const val LINDEX_TEXT_URL = "https://api.secondlife.com/datafeeds/lindex.txt"
        private const val STATS_TEXT_URL = "https://api.secondlife.com/datafeeds/homepage.txt"
        private const val STATUS_RSS_URL = "https://status.secondlifegrid.net/history.rss"
        private const val LINDEX_CACHE_MS = 15L * 60 * 1000
        private const val STATS_CACHE_MS = 5L * 60 * 1000
        private const val STATUS_CACHE_MS = 5L * 60 * 1000
    }

    private val _lindex = MutableStateFlow<LindexData?>(null)
    val lindex: StateFlow<LindexData?> = _lindex

    private val _stats = MutableStateFlow<GridStats?>(null)
    val stats: StateFlow<GridStats?> = _stats

    private val _incidents = MutableStateFlow<List<StatusIncident>>(emptyList())
    val incidents: StateFlow<List<StatusIncident>> = _incidents

    @Volatile private var lastLindexFetchAt = 0L
    @Volatile private var lastStatsFetchAt = 0L
    @Volatile private var lastStatusFetchAt = 0L

    /** Fetch LindeX exchange rate data. Honours 15-minute cache. */
    suspend fun fetchLindex(force: Boolean = false): LindexData? = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (!force && now - lastLindexFetchAt < LINDEX_CACHE_MS && _lindex.value != null) {
            return@withContext _lindex.value
        }
        val body = httpGet(LINDEX_TEXT_URL) ?: return@withContext _lindex.value
        val map = parseKeyValueText(body)
        // Documented keys include market_buy_rate, market_sell_rate,
        // limit_buy_rate, limit_sell_rate, last_quote, etc. We surface
        // the two rates the wallet UI cares about (market_buy and
        // last_quote) and keep the raw map for advanced consumers.
        val data = LindexData(
            marketBuyRate = map["market_buy_rate"]?.toDoubleOrNull(),
            marketSellRate = map["market_sell_rate"]?.toDoubleOrNull(),
            limitBuyRate = map["limit_buy_rate"]?.toDoubleOrNull(),
            limitSellRate = map["limit_sell_rate"]?.toDoubleOrNull(),
            lastQuote = map["last_quote"]?.toDoubleOrNull(),
            updatedAt = map["updated_at"] ?: map["timestamp"] ?: "",
            raw = map,
        )
        _lindex.value = data
        lastLindexFetchAt = now
        data
    }

    /** Fetch grid signup / inworld-user stats. Honours 5-minute cache. */
    suspend fun fetchStats(force: Boolean = false): GridStats? = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (!force && now - lastStatsFetchAt < STATS_CACHE_MS && _stats.value != null) {
            return@withContext _stats.value
        }
        val body = httpGet(STATS_TEXT_URL) ?: return@withContext _stats.value
        val map = parseKeyValueText(body)
        val data = GridStats(
            signups = map["signups"]?.toLongOrNull(),
            inworld = map["inworld"]?.toLongOrNull(),
            exchangeRate = map["exchange_rate"]?.toDoubleOrNull(),
            updatedAt = map["signups_updated_slt"]
                ?: map["inworld_updated_slt"]
                ?: map["exchange_rate_updated_slt"]
                ?: "",
            raw = map,
        )
        _stats.value = data
        lastStatsFetchAt = now
        data
    }

    /**
     * Fetch the public status-page incident RSS. Returns the most recent
     * incidents (up to ~30 entries). Honours 5-minute cache.
     */
    suspend fun fetchStatusIncidents(force: Boolean = false): List<StatusIncident> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (!force && now - lastStatusFetchAt < STATUS_CACHE_MS && _incidents.value.isNotEmpty()) {
            return@withContext _incidents.value
        }
        val body = httpGet(STATUS_RSS_URL) ?: return@withContext _incidents.value
        val parsed = runCatching { parseStatusRss(body) }.getOrElse {
            Log.w(TAG, "Status RSS parse failed: ${it.message}")
            emptyList()
        }
        if (parsed.isNotEmpty()) {
            _incidents.value = parsed
            lastStatusFetchAt = now
        }
        parsed
    }

    private fun httpGet(url: String): String? {
        return try {
            val req = Request.Builder().url(url).get().build()
            httpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    Log.w(TAG, "HTTP ${resp.code} fetching $url")
                    null
                } else {
                    resp.body?.string()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fetch failed for $url: ${e.message}")
            null
        }
    }

    internal fun parseKeyValueText(body: String): Map<String, String> {
        // Linden's *.txt feeds are newline-separated `key value` rows where
        // the value may contain spaces. Split on the first whitespace.
        val out = LinkedHashMap<String, String>()
        body.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEach
            val sep = trimmed.indexOfFirst { it.isWhitespace() }
            if (sep <= 0) {
                out[trimmed] = ""
            } else {
                out[trimmed.substring(0, sep)] = trimmed.substring(sep + 1).trim()
            }
        }
        return out
    }

    internal fun parseStatusRss(xml: String): List<StatusIncident> {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        val items = mutableListOf<StatusIncident>()
        var inItem = false
        var title: String? = null
        var description: String? = null
        var pubDate: String? = null
        var link: String? = null

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    when (parser.name.lowercase(Locale.ROOT)) {
                        "item" -> {
                            inItem = true
                            title = null; description = null; pubDate = null; link = null
                        }
                        "title" -> if (inItem) title = parser.nextText().trim()
                        "description" -> if (inItem) description = parser.nextText().trim()
                        "pubdate" -> if (inItem) pubDate = parser.nextText().trim()
                        "link" -> if (inItem) link = parser.nextText().trim()
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("item", ignoreCase = true)) {
                        if (title != null) {
                            items.add(
                                StatusIncident(
                                    title = title!!,
                                    description = description.orEmpty(),
                                    link = link.orEmpty(),
                                    publishedAtMs = parsePubDate(pubDate),
                                    publishedRaw = pubDate.orEmpty(),
                                )
                            )
                        }
                        inItem = false
                    }
                }
            }
            event = parser.next()
        }
        return items
    }

    private fun parsePubDate(raw: String?): Long {
        if (raw.isNullOrBlank()) return 0L
        return try {
            val fmt = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
            fmt.timeZone = TimeZone.getTimeZone("UTC")
            fmt.parse(raw)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}

data class LindexData(
    val marketBuyRate: Double?,
    val marketSellRate: Double?,
    val limitBuyRate: Double?,
    val limitSellRate: Double?,
    val lastQuote: Double?,
    val updatedAt: String,
    val raw: Map<String, String>,
) {
    /** L$ → USD using the market buy rate (most user-relevant for "what's it worth"). */
    fun lindenToUsd(linden: Long): Double {
        val rate = marketBuyRate ?: lastQuote ?: marketSellRate ?: return 0.0
        if (rate <= 0.0) return 0.0
        return linden / rate
    }
}

data class GridStats(
    val signups: Long?,
    val inworld: Long?,
    val exchangeRate: Double?,
    val updatedAt: String,
    val raw: Map<String, String>,
)

data class StatusIncident(
    val title: String,
    val description: String,
    val link: String,
    val publishedAtMs: Long,
    val publishedRaw: String,
)
