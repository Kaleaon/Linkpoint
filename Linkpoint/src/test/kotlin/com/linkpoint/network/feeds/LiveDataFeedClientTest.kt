package com.linkpoint.network.feeds

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for [LiveDataFeedClient]'s parsing logic.
 *
 * The HTTP layer is not exercised here — these tests focus on the two
 * pure parsing functions ([LiveDataFeedClient.parseKeyValueText] and
 * [LiveDataFeedClient.parseStatusRss]) that convert raw feed bodies into
 * typed data classes.  Each test uses a representative fixture payload
 * that matches the format documented on the Linden Lab wiki / feed URLs.
 */
class LiveDataFeedClientTest {

    private val client = LiveDataFeedClient()

    // -------------------------------------------------------------------------
    // parseKeyValueText — Linden Lab *.txt key/value feeds
    // -------------------------------------------------------------------------

    @Test
    fun `parseKeyValueText parses typical lindex feed`() {
        val body = """
            # LindeX Exchange Rate Data
            market_buy_rate 260.12
            market_sell_rate 259.87
            limit_buy_rate 265.00
            limit_sell_rate 255.00
            last_quote 260.00
            updated_at 2024-01-15T12:00:00Z
        """.trimIndent()

        val map = client.parseKeyValueText(body)

        assertEquals("260.12", map["market_buy_rate"])
        assertEquals("259.87", map["market_sell_rate"])
        assertEquals("265.00", map["limit_buy_rate"])
        assertEquals("255.00", map["limit_sell_rate"])
        assertEquals("260.00", map["last_quote"])
        assertEquals("2024-01-15T12:00:00Z", map["updated_at"])
    }

    @Test
    fun `parseKeyValueText parses typical homepage stats feed`() {
        val body = """
            signups 70000000
            inworld 42000
            exchange_rate 260
            signups_updated_slt 2024-01-15 08:00 PST
        """.trimIndent()

        val map = client.parseKeyValueText(body)

        assertEquals("70000000", map["signups"])
        assertEquals("42000", map["inworld"])
        assertEquals("260", map["exchange_rate"])
        assertEquals("2024-01-15 08:00 PST", map["signups_updated_slt"])
    }

    @Test
    fun `parseKeyValueText ignores comment lines starting with hash`() {
        val body = """
            # This is a comment
            key1 value1
            # Another comment
            key2 value2
        """.trimIndent()

        val map = client.parseKeyValueText(body)

        assertEquals(2, map.size)
        assertEquals("value1", map["key1"])
        assertEquals("value2", map["key2"])
    }

    @Test
    fun `parseKeyValueText ignores blank lines`() {
        val body = "key1 value1\n\n\nkey2 value2\n"

        val map = client.parseKeyValueText(body)

        assertEquals(2, map.size)
    }

    @Test
    fun `parseKeyValueText handles value with embedded spaces`() {
        // Value part is everything after the first whitespace token
        val body = "signups_updated_slt 2024-01-15 08:00 PST"

        val map = client.parseKeyValueText(body)

        assertEquals("2024-01-15 08:00 PST", map["signups_updated_slt"])
    }

    @Test
    fun `parseKeyValueText returns empty map for blank input`() {
        val map = client.parseKeyValueText("")
        assertTrue(map.isEmpty())
    }

    @Test
    fun `parseKeyValueText stores key-only line with empty value`() {
        // A line that has a key but no value should be stored with an empty string.
        val body = "standalone_key"
        val map = client.parseKeyValueText(body)
        assertTrue(map.containsKey("standalone_key"))
        assertEquals("", map["standalone_key"])
    }

    // -------------------------------------------------------------------------
    // parseStatusRss — LL grid-status RSS feed
    // -------------------------------------------------------------------------

    private val MINIMAL_RSS = """
        <?xml version="1.0" encoding="UTF-8"?>
        <rss version="2.0">
          <channel>
            <title>Second Life Grid Status</title>
            <item>
              <title>Scheduled Maintenance</title>
              <description>Rolling restart of RC channels.</description>
              <pubDate>Wed, 15 Jan 2025 14:00:00 +0000</pubDate>
              <link>https://status.secondlifegrid.net/incident/abc123</link>
            </item>
            <item>
              <title>Ongoing Investigation</title>
              <description>We are investigating login issues.</description>
              <pubDate>Tue, 14 Jan 2025 09:30:00 +0000</pubDate>
              <link>https://status.secondlifegrid.net/incident/xyz456</link>
            </item>
          </channel>
        </rss>
    """.trimIndent()

    @Test
    fun `parseStatusRss returns correct number of items`() {
        val items = client.parseStatusRss(MINIMAL_RSS)
        assertEquals(2, items.size)
    }

    @Test
    fun `parseStatusRss maps title correctly`() {
        val items = client.parseStatusRss(MINIMAL_RSS)
        assertEquals("Scheduled Maintenance", items[0].title)
        assertEquals("Ongoing Investigation", items[1].title)
    }

    @Test
    fun `parseStatusRss maps description correctly`() {
        val items = client.parseStatusRss(MINIMAL_RSS)
        assertEquals("Rolling restart of RC channels.", items[0].description)
        assertEquals("We are investigating login issues.", items[1].description)
    }

    @Test
    fun `parseStatusRss maps link correctly`() {
        val items = client.parseStatusRss(MINIMAL_RSS)
        assertEquals("https://status.secondlifegrid.net/incident/abc123", items[0].link)
    }

    @Test
    fun `parseStatusRss parses pubDate into epoch millis`() {
        val items = client.parseStatusRss(MINIMAL_RSS)
        // Wed 15 Jan 2025 14:00:00 UTC → positive epoch value
        assertTrue("publishedAtMs should be > 0", items[0].publishedAtMs > 0L)
        // First item should be newer (larger epoch) than second item.
        assertTrue("First item should be newer than second", items[0].publishedAtMs > items[1].publishedAtMs)
    }

    @Test
    fun `parseStatusRss stores raw pubDate string`() {
        val items = client.parseStatusRss(MINIMAL_RSS)
        assertEquals("Wed, 15 Jan 2025 14:00:00 +0000", items[0].publishedRaw)
    }

    @Test
    fun `parseStatusRss returns empty list for empty channel`() {
        val rss = """
            <?xml version="1.0"?>
            <rss version="2.0"><channel></channel></rss>
        """.trimIndent()
        val items = client.parseStatusRss(rss)
        assertTrue(items.isEmpty())
    }

    @Test
    fun `parseStatusRss skips items without a title`() {
        val rss = """
            <?xml version="1.0"?>
            <rss version="2.0">
              <channel>
                <item>
                  <description>No title item</description>
                  <pubDate>Wed, 15 Jan 2025 14:00:00 +0000</pubDate>
                </item>
                <item>
                  <title>Has Title</title>
                  <description>This one has a title.</description>
                </item>
              </channel>
            </rss>
        """.trimIndent()
        val items = client.parseStatusRss(rss)
        assertEquals(1, items.size)
        assertEquals("Has Title", items[0].title)
    }

    @Test
    fun `parseStatusRss is case-insensitive for element names`() {
        val rss = """
            <?xml version="1.0"?>
            <rss version="2.0">
              <channel>
                <ITEM>
                  <TITLE>Upper Case Tags</TITLE>
                  <DESCRIPTION>Test</DESCRIPTION>
                  <PUBDATE>Wed, 15 Jan 2025 14:00:00 +0000</PUBDATE>
                  <LINK>https://example.com</LINK>
                </ITEM>
              </channel>
            </rss>
        """.trimIndent()
        val items = client.parseStatusRss(rss)
        assertEquals(1, items.size)
        assertEquals("Upper Case Tags", items[0].title)
    }

    // -------------------------------------------------------------------------
    // LindexData.lindenToUsd — derived calculation
    // -------------------------------------------------------------------------

    @Test
    fun `lindenToUsd converts correctly using marketBuyRate`() {
        val data = LindexData(
            marketBuyRate = 260.0,
            marketSellRate = null,
            limitBuyRate = null,
            limitSellRate = null,
            lastQuote = null,
            updatedAt = "",
            raw = emptyMap(),
        )
        val result = data.lindenToUsd(260L)
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun `lindenToUsd falls back to lastQuote when marketBuyRate is null`() {
        val data = LindexData(
            marketBuyRate = null,
            marketSellRate = null,
            limitBuyRate = null,
            limitSellRate = null,
            lastQuote = 250.0,
            updatedAt = "",
            raw = emptyMap(),
        )
        val result = data.lindenToUsd(500L)
        assertEquals(2.0, result, 0.001)
    }

    @Test
    fun `lindenToUsd returns 0 when all rates are null`() {
        val data = LindexData(
            marketBuyRate = null,
            marketSellRate = null,
            limitBuyRate = null,
            limitSellRate = null,
            lastQuote = null,
            updatedAt = "",
            raw = emptyMap(),
        )
        assertEquals(0.0, data.lindenToUsd(1000L), 0.0)
    }

    @Test
    fun `lindenToUsd returns 0 when rate is zero`() {
        val data = LindexData(
            marketBuyRate = 0.0,
            marketSellRate = null,
            limitBuyRate = null,
            limitSellRate = null,
            lastQuote = null,
            updatedAt = "",
            raw = emptyMap(),
        )
        assertEquals(0.0, data.lindenToUsd(1000L), 0.0)
    }
}
