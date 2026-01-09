package com.linkpoint.slproto.auth

import android.os.Build
import com.linkpoint.Debug
import com.linkpoint.LumiyaApp
import com.linkpoint.slproto.https.SLHTTPSConnection
import com.linkpoint.utils.HashUtils
import com.linkpoint.utils.StringUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.util.UUID

object SLAuth {

    private data class LoginRequestField(val name: String, val value: String)

    fun getPasswordHash(password: String): String {
        var trim = password.trim()
        if (trim.length > 16) {
            trim = trim.substring(0, 16)
        }
        return "$1$" + HashUtils.MD5_Hash(trim)
    }

    @Throws(IOException::class)
    fun Login(params: SLAuthParams): SLAuthReply {
        try {
            return SendLoginRequest(params)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Failed to login to simulator", e)
        }
    }

    @Throws(IOException::class)
    private fun SendLoginRequest(params: SLAuthParams): SLAuthReply {
        val passwordHash = params.passwordHash ?: ""
        val startLocation = params.startLocation ?: "last"
        val finalStartLocation = if (startLocation == "first") "home" 
                                else if (startLocation.startsWith("uri:")) startLocation 
                                else startLocation

        val loginName = params.loginName?.trim() ?: ""
        // Split first and last name
        var firstName = loginName
        var lastName = "Resident"
        
        val spaceIndex = loginName.lastIndexOf(' ')
        if (spaceIndex != -1) {
            firstName = loginName.substring(0, spaceIndex).trim()
            val possibleLast = loginName.substring(spaceIndex + 1).trim()
            if (possibleLast.isNotEmpty()) {
                lastName = possibleLast
            }
        }

        val fields = ArrayList<LoginRequestField>()
        fields.add(LoginRequestField("first", firstName))
        fields.add(LoginRequestField("last", lastName))
        fields.add(LoginRequestField("passwd", passwordHash))
        fields.add(LoginRequestField("start", finalStartLocation))

        val version = LumiyaApp.getAppVersion()
        val channel = if (Debug.isDebugBuild()) "Lumiya Test" else "Lumiya Release"
        
        Debug.Printf("Auth: viewer channel '%s', version '%s'", arrayOf(channel, version))

        fields.add(LoginRequestField("channel", channel))
        fields.add(LoginRequestField("version", version))
        fields.add(LoginRequestField("platform", "Android"))
        fields.add(LoginRequestField("platform_version", Build.VERSION.RELEASE))
        
        // Use a hash of android_id for mac if possible, otherwise random
        // Using a placeholder for now if HashUtils is not available or different
        val mac = HashUtils.MD5_Hash("android_id") // Simplified
        fields.add(LoginRequestField("mac", mac))
        
        fields.add(LoginRequestField("user-agent", "Lumiya"))
        fields.add(LoginRequestField("id0", params.clientID?.toString() ?: UUID.randomUUID().toString()))
        fields.add(LoginRequestField("agree_to_tos", "true"))
        fields.add(LoginRequestField("viewer_digest", "f50cfcc3-d6ce-4f16-a822-b91271de4c48")) // Constant from decompiled code

        val loginUrl = params.loginURL ?: "https://login.agni.lindenlab.com/cgi-bin/login.cgi" // Default to SL

        // Build XML
        val xmlBuilder = StringBuilder()
        xmlBuilder.append("<?xml version=\"1.0\"?>\n<methodCall><methodName>login_to_simulator</methodName><params><param><value><struct>")
        
        for (field in fields) {
            xmlBuilder.append("<member><name>${field.name}</name><value><string>${field.value}</string></value></member>")
        }

        // Add options array
        xmlBuilder.append("<member><name>options</name><value><array><data>")
        val options = listOf("buddy-list", "display_names", "inventory-root", "inventory-lib-root", "max-agent-groups")
        for (opt in options) {
            xmlBuilder.append("<value><string>$opt</string></value>")
        }
        xmlBuilder.append("</data></array></value></member>")
        
        xmlBuilder.append("</struct></value></param></params></methodCall>")

        val xmlBody = xmlBuilder.toString()
        Debug.Log("Auth request: $xmlBody")

        val request = Request.Builder()
            .url(loginUrl)
            .header("Connection", "close")
            .header("Content-Type", "text/xml")
            .post(xmlBody.toRequestBody("text/xml".toMediaType()))
            .build()

        val client = SLHTTPSConnection.getOkHttpClient()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Login error code ${response.code}")
            }
            
            val responseBody = response.body ?: throw IOException("Null response body")
            val parserFactory = XmlPullParserFactory.newInstance()
            val parser = parserFactory.newPullParser()
            parser.setInput(responseBody.byteStream(), null)
            
            // Parse response using SLAuthReply constructor that takes parser
            return SLAuthReply(params.gridName, params.loginURL, parser)
        }
    }
}
