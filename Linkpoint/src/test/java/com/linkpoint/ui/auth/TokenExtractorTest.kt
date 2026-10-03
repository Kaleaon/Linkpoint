package com.linkpoint.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TokenExtractorTest {

    @Test
    fun testIsCallbackUrlWithCustomScheme() {
        val url = "secondlife://auth/callback?token=test_mfa_token_12345"
        assertTrue(TokenExtractor.isCallbackUrl(url))
    }

    @Test
    fun testIsCallbackUrlWithHttpOAuth() {
        val url = "https://id.secondlife.com/oauth/callback?auth_token=secret_auth_token_6789"
        assertTrue(TokenExtractor.isCallbackUrl(url))
    }

    @Test
    fun testIsCallbackUrlNormalPage() {
        val url = "https://id.secondlife.com/openid/login"
        assertFalse(TokenExtractor.isCallbackUrl(url))
    }

    @Test
    fun testExtractTokenFromCustomScheme() {
        val url = "secondlife://auth/callback?token=abc_token_123&mfa_hash=hash_456"
        val intercepted = TokenExtractor.extractToken(url)
        assertNotNull(intercepted)
        assertEquals("abc_token_123", intercepted?.token)
        assertEquals("hash_456", intercepted?.mfaHash)
    }

    @Test
    fun testExtractTokenFromAuthTokenParam() {
        val url = "https://login.secondlife.com/mfa/callback?auth_token=web_session_token_999"
        val intercepted = TokenExtractor.extractToken(url)
        assertNotNull(intercepted)
        assertEquals("web_session_token_999", intercepted?.token)
        assertNull(intercepted?.mfaHash)
    }

    @Test
    fun testExtractTokenFromSchemeAuthority() {
        val url = "secondlife://token_xyz_987"
        val intercepted = TokenExtractor.extractToken(url)
        assertNotNull(intercepted)
        assertEquals("token_xyz_987", intercepted?.token)
    }

    @Test
    fun testDomainWhitelistAllowedDomains() {
        assertTrue(TokenExtractor.isDomainAllowed("https://id.secondlife.com/login"))
        assertTrue(TokenExtractor.isDomainAllowed("https://login.agni.lindenlab.com/cgi-bin/login.cgi"))
        assertTrue(TokenExtractor.isDomainAllowed("secondlife://auth/callback"))
    }

    @Test
    fun testDomainWhitelistBlockedDomains() {
        assertFalse(TokenExtractor.isDomainAllowed("https://malicious-site.com/phish"))
        assertFalse(TokenExtractor.isDomainAllowed("https://secondlife.com.evil.org/login"))
    }

    @Test
    fun testCustomGridHostAllowed() {
        val customGrid = "https://grid.example.com:9000/"
        assertTrue(TokenExtractor.isDomainAllowed("https://grid.example.com:9000/weblogin", customGrid))
    }

    @Test
    fun testClearCookiesDoesNotThrow() {
        TokenExtractor.clearCookies()
    }
}
