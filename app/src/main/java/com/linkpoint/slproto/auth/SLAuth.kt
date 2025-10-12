package com.linkpoint.slproto.auth

import com.linkpoint.utils.HashUtils
import java.io.IOException
import java.util.LinkedList

/**
 * Second Life Authentication Handler
 * Modernized from original Lumiya Viewer
 */
class SLAuth {
    
    private data class LoginRequestField(
        val name: String,
        val value: String
    )
    
    @Throws(IOException::class)
    fun sendLoginRequest(params: SLAuthParams): SLAuthReply {
        val passwordHash = params.passwordHash
        println("md5 hash: $passwordHash")
        
        // Determine start location
        val startLocation = when {
            params.startLocation == null -> "last"
            params.startLocation == "first" -> "home"
            params.startLocation.startsWith("uri:") -> params.startLocation
            else -> "last"
        }
        
        val fields = LinkedList<LoginRequestField>()
        
        // Parse login name into first and last name
        val loginName = params.loginName.trim()
        val separators = " ._"
        var splitIndex = loginName.length
        
        for (separator in separators) {
            val index = loginName.indexOf(separator)
            if (index != -1 && index < splitIndex) {
                splitIndex = index
            }
        }
        
        val firstName = loginName.substring(0, splitIndex).trim()
        val lastName = if (splitIndex < loginName.length) {
            loginName.substring(splitIndex + 1).trim()
        } else {
            ""
        }
        
        val finalLastName = if (lastName.isEmpty()) "Resident" else lastName
        
        // Add login fields
        fields.add(LoginRequestField("first", firstName))
        fields.add(LoginRequestField("last", finalLastName))
        fields.add(LoginRequestField("passwd", "\$1\$$passwordHash"))
        fields.add(LoginRequestField("start", startLocation))
        fields.add(LoginRequestField("channel", params.channel))
        fields.add(LoginRequestField("version", params.version))
        fields.add(LoginRequestField("platform", "Android"))
        fields.add(LoginRequestField("mac", params.macAddress))
        fields.add(LoginRequestField("id0", params.id0))
        fields.add(LoginRequestField("agree_to_tos", "true"))
        fields.add(LoginRequestField("read_critical", "true"))
        
        // Add optional fields
        if (params.skipOptionalUpdate) {
            fields.add(LoginRequestField("skipoptional", "true"))
        }
        
        // Build and send request
        return executeLoginRequest(params.loginUri, fields)
    }
    
    private fun executeLoginRequest(
        loginUri: String,
        fields: List<LoginRequestField>
    ): SLAuthReply {
        // Build XMLRPC request
        val xmlRequest = buildXMLRPCRequest(fields)
        
        // Send HTTP POST request
        val response = sendHTTPPost(loginUri, xmlRequest)
        
        // Parse response
        return parseLoginResponse(response)
    }
    
    private fun buildXMLRPCRequest(fields: List<LoginRequestField>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=&quot;1.0&quot;?>\n")
        sb.append("<methodCall>\n")
        sb.append("<methodName>login_to_simulator</methodName>\n")
        sb.append("<params>\n")
        sb.append("<param>\n")
        sb.append("<value><struct>\n")
        
        for (field in fields) {
            sb.append("<member>\n")
            sb.append("<name>${field.name}</name>\n")
            sb.append("<value><string>${field.value}</string></value>\n")
            sb.append("</member>\n")
        }
        
        sb.append("</struct></value>\n")
        sb.append("</param>\n")
        sb.append("</params>\n")
        sb.append("</methodCall>\n")
        
        return sb.toString()
    }
    
    private fun sendHTTPPost(url: String, body: String): String {
        // TODO: Implement HTTP POST using modern networking
        // Use OkHttp or similar modern library
        throw NotImplementedError("HTTP POST implementation needed")
    }
    
    private fun parseLoginResponse(response: String): SLAuthReply {
        // TODO: Implement XMLRPC response parsing
        // Parse the login response and extract all fields
        throw NotImplementedError("Response parsing implementation needed")
    }
}