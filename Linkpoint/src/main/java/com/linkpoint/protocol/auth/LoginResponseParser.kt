package com.linkpoint.protocol.auth

import android.util.Log
import java.util.UUID
import java.util.regex.Pattern

/**
 * Parses the full login response XML to extract buddy-list, inventory-skeleton,
 * and other data that isn't captured in the basic AuthReply.
 * 
 * Based on Second Life XMLRPC login response format parsing logic.
 */
object LoginResponseParser {
    private const val TAG = "LoginResponseParser"
    
    /**
     * Represents a friend (buddy) from the login response.
     */
    data class BuddyInfo(
        val agentId: UUID,
        val buddyRightsGiven: Int,
        val buddyRightsHas: Int
    )
    
    /**
     * Represents an inventory folder from the login response skeleton.
     */
    data class InventoryFolderInfo(
        val folderId: UUID,
        val parentId: UUID,
        val name: String,
        val typeDefault: Int,
        val version: Int
    )
    
    /** Null UUID constant for missing parent IDs */
    private val NULL_UUID = UUID(0L, 0L)
    
    /**
     * Parsed login response data.
     */
    data class ParsedLoginData(
        val buddyList: List<BuddyInfo>,
        val inventorySkeleton: List<InventoryFolderInfo>,
        val inventoryRoot: UUID?,
        val agentAccessMax: String?,
        val homeRegion: String?,
        val lookAt: List<Float>?
    )
    
    /**
     * Parse the full login response XML.
     * 
     * @param responseXml The raw XMLRPC login response
     * @return Parsed login data including buddy list and inventory skeleton
     */
    fun parse(responseXml: String): ParsedLoginData {
        Log.d(TAG, "Parsing login response (${responseXml.length} bytes)")
        
        val buddyList = parseBuddyList(responseXml)
        val inventorySkeleton = parseInventorySkeleton(responseXml)
        val inventoryRoot = parseInventoryRoot(responseXml)
        val agentAccessMax = extractXmlValue(responseXml, "agent_access_max")
        val homeRegion = extractXmlValue(responseXml, "home")
        val lookAt = parseLookAt(responseXml)
        
        Log.i(TAG, "Parsed login response: ${buddyList.size} friends, ${inventorySkeleton.size} inventory folders")
        
        return ParsedLoginData(
            buddyList = buddyList,
            inventorySkeleton = inventorySkeleton,
            inventoryRoot = inventoryRoot,
            agentAccessMax = agentAccessMax,
            homeRegion = homeRegion,
            lookAt = lookAt
        )
    }
    
    /**
     * Parse buddy-list array from login response.
     * 
     * Format:
     * <member><name>buddy-list</name><value><array><data>
     *   <value><struct>
     *     <member><name>buddy_id</name><value><string>uuid</string></value></member>
     *     <member><name>buddy_rights_given</name><value><i4>0</i4></value></member>
     *     <member><name>buddy_rights_has</name><value><i4>1</i4></value></member>
     *   </struct></value>
     * </data></array></value></member>
     */
    private fun parseBuddyList(xml: String): List<BuddyInfo> {
        val buddies = mutableListOf<BuddyInfo>()
        
        try {
            // Find the buddy-list array section
            val buddyListStart = xml.indexOf("<name>buddy-list</name>")
            if (buddyListStart == -1) {
                Log.d(TAG, "No buddy-list found in login response")
                return emptyList()
            }
            
            // Find the end of the buddy-list array
            val arrayStart = xml.indexOf("<array>", buddyListStart)
            val arrayEnd = xml.indexOf("</array>", arrayStart)
            if (arrayStart == -1 || arrayEnd == -1) {
                Log.w(TAG, "Malformed buddy-list array")
                return emptyList()
            }
            
            val buddyArrayXml = xml.substring(arrayStart, arrayEnd + "</array>".length)
            
            // Extract each struct in the array
            val structPattern = Pattern.compile("<struct>(.*?)</struct>", Pattern.DOTALL)
            val matcher = structPattern.matcher(buddyArrayXml)
            
            while (matcher.find()) {
                val structXml = matcher.group(1) ?: continue
                
                val buddyIdStr = extractMemberValue(structXml, "buddy_id")
                val rightsGivenStr = extractMemberIntValue(structXml, "buddy_rights_given")
                val rightsHasStr = extractMemberIntValue(structXml, "buddy_rights_has")
                
                if (buddyIdStr != null) {
                    try {
                        val buddy = BuddyInfo(
                            agentId = UUID.fromString(buddyIdStr),
                            buddyRightsGiven = rightsGivenStr ?: 0,
                            buddyRightsHas = rightsHasStr ?: 0
                        )
                        buddies.add(buddy)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse buddy: $buddyIdStr", e)
                    }
                }
            }
            
            Log.d(TAG, "Parsed ${buddies.size} buddies from login response")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing buddy-list", e)
        }
        
        return buddies
    }
    
    /**
     * Parse inventory-skeleton array from login response.
     */
    private fun parseInventorySkeleton(xml: String): List<InventoryFolderInfo> {
        val folders = mutableListOf<InventoryFolderInfo>()
        
        try {
            // Find the inventory-skeleton array section
            val skeletonStart = xml.indexOf("<name>inventory-skeleton</name>")
            if (skeletonStart == -1) {
                Log.d(TAG, "No inventory-skeleton found in login response")
                return emptyList()
            }
            
            // Find the end of the inventory-skeleton array
            val arrayStart = xml.indexOf("<array>", skeletonStart)
            val arrayEnd = xml.indexOf("</array>", arrayStart)
            if (arrayStart == -1 || arrayEnd == -1) {
                Log.w(TAG, "Malformed inventory-skeleton array")
                return emptyList()
            }
            
            val skeletonArrayXml = xml.substring(arrayStart, arrayEnd + "</array>".length)
            
            // Extract each struct in the array
            val structPattern = Pattern.compile("<struct>(.*?)</struct>", Pattern.DOTALL)
            val matcher = structPattern.matcher(skeletonArrayXml)
            
            while (matcher.find()) {
                val structXml = matcher.group(1) ?: continue
                
                val folderIdStr = extractMemberValue(structXml, "folder_id")
                val parentIdStr = extractMemberValue(structXml, "parent_id")
                val name = extractMemberValue(structXml, "name") ?: "Unnamed"
                val typeDefault = extractMemberIntValue(structXml, "type_default") ?: -1
                val version = extractMemberIntValue(structXml, "version") ?: 0
                
                if (folderIdStr != null) {
                    try {
                        val folder = InventoryFolderInfo(
                            folderId = UUID.fromString(folderIdStr),
                            parentId = if (parentIdStr != null) UUID.fromString(parentIdStr) else NULL_UUID,
                            name = name,
                            typeDefault = typeDefault,
                            version = version
                        )
                        folders.add(folder)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse inventory folder: $folderIdStr", e)
                    }
                }
            }
            
            Log.d(TAG, "Parsed ${folders.size} inventory folders from login response")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing inventory-skeleton", e)
        }
        
        return folders
    }
    
    /**
     * Parse inventory-root from login response.
     */
    private fun parseInventoryRoot(xml: String): UUID? {
        try {
            // Find the inventory-root section
            val rootStart = xml.indexOf("<name>inventory-root</name>")
            if (rootStart == -1) {
                Log.d(TAG, "No inventory-root found in login response")
                return null
            }
            
            // Extract folder_id from the inventory-root struct
            val arrayStart = xml.indexOf("<array>", rootStart)
            val arrayEnd = xml.indexOf("</array>", arrayStart)
            if (arrayStart == -1 || arrayEnd == -1) {
                return null
            }
            
            val rootArrayXml = xml.substring(arrayStart, arrayEnd + "</array>".length)
            val folderId = extractMemberValue(rootArrayXml, "folder_id")
            
            return folderId?.let {
                try {
                    UUID.fromString(it)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing inventory-root", e)
            return null
        }
    }
    
    /**
     * Parse look_at vector from login response.
     */
    private fun parseLookAt(xml: String): List<Float>? {
        try {
            val lookAtStr = extractXmlValue(xml, "look_at") ?: return null
            // Format: [r<float>,r<float>,r<float>]
            val pattern = Pattern.compile("r([\\d.-]+)")
            val matcher = pattern.matcher(lookAtStr)
            val values = mutableListOf<Float>()
            while (matcher.find() && values.size < 3) {
                values.add(matcher.group(1)?.toFloatOrNull() ?: 0f)
            }
            return if (values.size == 3) values else null
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Extract a value from a struct member by name.
     */
    private fun extractMemberValue(structXml: String, name: String): String? {
        // Try string type
        val stringPattern = "<name>$name</name>\\s*<value>\\s*<string>([^<]*)</string>".toRegex()
        stringPattern.find(structXml)?.groupValues?.get(1)?.let { return it }
        
        // Try uuid type
        val uuidPattern = "<name>$name</name>\\s*<value>\\s*<uuid>([^<]*)</uuid>".toRegex()
        uuidPattern.find(structXml)?.groupValues?.get(1)?.let { return it }
        
        return null
    }
    
    /**
     * Extract an integer value from a struct member.
     */
    private fun extractMemberIntValue(structXml: String, name: String): Int? {
        // Try i4 type
        val i4Pattern = "<name>$name</name>\\s*<value>\\s*<i4>([^<]*)</i4>".toRegex()
        i4Pattern.find(structXml)?.groupValues?.get(1)?.toIntOrNull()?.let { return it }
        
        // Try int type
        val intPattern = "<name>$name</name>\\s*<value>\\s*<int>([^<]*)</int>".toRegex()
        intPattern.find(structXml)?.groupValues?.get(1)?.toIntOrNull()?.let { return it }
        
        // Try integer type
        val integerPattern = "<name>$name</name>\\s*<value>\\s*<integer>([^<]*)</integer>".toRegex()
        integerPattern.find(structXml)?.groupValues?.get(1)?.toIntOrNull()?.let { return it }
        
        return null
    }
    
    /**
     * Extract a simple value from XML by name.
     */
    private fun extractXmlValue(xml: String, name: String): String? {
        val stringPattern = "<name>$name</name>\\s*<value>\\s*<string>([^<]*)</string>".toRegex()
        return stringPattern.find(xml)?.groupValues?.get(1)
    }
}
