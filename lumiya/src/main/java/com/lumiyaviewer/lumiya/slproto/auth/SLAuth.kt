package com.lumiyaviewer.lumiya.slproto.auth

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection
import com.lumiyaviewer.lumiya.utils.HashUtils
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.io.IOException

object SLAuth {

    private data class LoginRequestField(val name: String, val value: String)

    @Throws(IOException::class)
    private fun SendLoginRequest(params: SLAuthParams): SLAuthReply {
        val passwordHash = params.passwordHash
        Debug.Log("md5 hash: $passwordHash")

        var startLocation = params.startLocation
        if (startLocation == "first") {
            startLocation = "home"
        }

        val fields = ArrayList<LoginRequestField>()
        
        // Parse first/last name
        val loginName = params.loginName.trim()
        var firstName = loginName
        var lastName = "Resident"
        
        // Simple logic to split first/last name if space exists
        // The original decompiled code had logic for " ._" delimiters
        if (loginName.contains(" ")) {
            val parts = loginName.split(" ", limit = 2)
            firstName = parts[0]
            lastName = parts[1]
        }

        fields.add(LoginRequestField("first", firstName))
        fields.add(LoginRequestField("last", lastName))
        fields.add(LoginRequestField("passwd", passwordHash))
        fields.add(LoginRequestField("start", startLocation))

        val version = LumiyaApp.getAppVersion()
        val channel = if (Debug.isDebugBuild()) "Lumiya Test" else "Lumiya Release"
        
        Debug.Printf("Auth: viewer channel '%s', version '%s'", arrayOf(channel as Any, version as Any))

        fields.add(LoginRequestField("channel", channel))
        fields.add(LoginRequestField("version", version))
        fields.add(LoginRequestField("platform", "Android"))
        fields.add(LoginRequestField("platform_version", android.os.Build.VERSION.RELEASE))
        fields.add(LoginRequestField("mac", HashUtils.MD5_Hash("android_id"))) // Simplification
        fields.add(LoginRequestField("user-agent", "Lumiya"))
        fields.add(LoginRequestField("id0", params.clientID.toString()))
        fields.add(LoginRequestField("agree_to_tos", "true"))
        fields.add(LoginRequestField("viewer_digest", "f50cfcc3-d6ce-4f16-a822-b91271de4c48"))

        // Build XML
        val xmlBuilder = StringBuilder()
        xmlBuilder.append("<?xml version=\"1.0\"?>\n")
        xmlBuilder.append("<methodCall><methodName>login_to_simulator</methodName><params><param><value><struct>")

        for (field in fields) {
            xmlBuilder.append("<member><name>${field.name}</name><value><string>${field.value}</string></value></member>")
        }
        
        // Options array
        xmlBuilder.append("<member><name>options</name><value><array><data>")
        xmlBuilder.append("<value><string>buddy-list</string></value>")
        xmlBuilder.append("<value><string>display_names</string></value>")
        xmlBuilder.append("<value><string>inventory-root</string></value>")
        xmlBuilder.append("<value><string>inventory-lib-root</string></value>")
        xmlBuilder.append("<value><string>max-agent-groups</string></value>")
        xmlBuilder.append("</data></array></value></member>")

        xmlBuilder.append("</struct></value></param></params></methodCall>")

        val requestBody = RequestBody.create(MediaType.parse("text/xml"), xmlBuilder.toString())
        
        val request = Request.Builder()
            .url(params.loginURL)
            .header("Connection", "close")
            .header("Content-Type", "text/xml")
            .post(requestBody)
            .build()

        val client = SLHTTPSConnection.getOkHttpClient()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Login error code ${response.code()}")
        }

        val responseBody = response.body() ?: throw IOException("Null response body")
        
        try {
             val parserFactory = XmlPullParserFactory.newInstance()
             val parser = parserFactory.newPullParser()
             parser.setInput(BufferedInputStream(responseBody.byteStream()), null)
             
             return SLAuthReply(params.gridName, params.loginURL, parser)
        } catch (e: Exception) {
            Debug.Warning(e)
            throw IOException("Login reply parse error", e)
        } finally {
            response.close()
        }
    }

    fun getPasswordHash(str: String): String {
        var trim = str.trim()
        if (trim.length > 16) {
            trim = trim.substring(0, 16)
        }
        return "$1$" + HashUtils.MD5_Hash(trim)
    }

    @Throws(IOException::class)
    fun Login(params: SLAuthParams): SLAuthReply {
        return try {
            SendLoginRequest(params)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is IOException) throw e
            throw IOException("Failed to login to simulator", e)
        }
    }
}
