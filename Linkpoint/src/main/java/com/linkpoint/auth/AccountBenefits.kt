package com.linkpoint.auth

import android.util.Log

/**
 * Account level benefits returned by the Second Life login server.
 * 
 * These values define what the account is allowed to do and various costs.
 * They are parsed from the `account_level_benefits` field in the login response.
 * 
 * Based on LibreMetaverse AccountLevelBenefits class.
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/Current_login_protocols">SL Login Protocol</a>
 */
data class AccountBenefits(
    /** Cost in L$ to upload a texture */
    val textureUploadCost: Int = 10,
    
    /** Cost in L$ to upload an animation */
    val animationUploadCost: Int = 10,
    
    /** Cost in L$ to upload a sound */
    val soundUploadCost: Int = 10,
    
    /** Cost in L$ to upload a mesh */
    val meshUploadCost: Int = 10,
    
    /** Maximum number of groups the agent can belong to */
    val groupMembershipLimit: Int = 42,
    
    /** Maximum number of attachments the agent can wear */
    val attachmentLimit: Int = 38,
    
    /** Maximum number of animated objects the agent can have */
    val animatedObjectLimit: Int = 1,
    
    /** Maximum number of picks in profile */
    val picksLimit: Int = 10,
    
    /** Weekly stipend amount (for premium accounts) */
    val stipend: Int = 0,
    
    /** Signup bonus amount */
    val signupBonus: Int = 0,
    
    /** Whether this is a premium account */
    val premiumAccess: Boolean = false,
    
    /** Cost to create a group */
    val createGroupCost: Int = 0,
    
    /** Cost to partner */
    val partnerFee: Int = 10,
    
    /** Cost to unpartner */
    val unpartnerFee: Int = 25,
    
    /** Whether land auctions are allowed */
    val landAuctionsAllowed: Boolean = false,
    
    /** Whether voice morphing is available */
    val voiceMorphing: Boolean = false,
    
    /** Limit for stored IMs */
    val storedImLimit: Int = 15,
    
    /** Transaction history limit in days */
    val transactionHistoryLimit: Int = 30,
    
    /** Script limit per parcel */
    val scriptLimit: Int = 0,
    
    /** Mainland tier allowed (in sqm) */
    val mainlandTier: Int = 0,
    
    /** Maximum local experiences */
    val localExperiences: Int = 0,
    
    /** Maximum grid-wide experiences */
    val gridwideExperienceLimit: Int = 0,
    
    /** Marketplace listing limit */
    val marketplaceListingLimit: Int = 0,
    
    /** Premium alts allowed */
    val premiumAlts: Int = 0,
    
    /** Priority entry to full regions */
    val priorityEntry: Boolean = false,
    
    /** Live chat support available */
    val liveChat: Boolean = false,
    
    /** Phone support available */
    val phoneSupport: Boolean = false,
    
    /** Beta grid land available */
    val betaGridLand: Boolean = false,
    
    /** Premium gifts available */
    val premiumGifts: Boolean = false,
    
    /** Can use animesh */
    val useAnimesh: Boolean = true,
    
    /** Linden buy fee percentage */
    val lindenBuyFee: Double = 0.0,
    
    /** Costs for large texture uploads (array of costs by size tier) */
    val largeTextureUploadCost: List<Int> = listOf(10),
    
    /** Estate access token for premium estates */
    val estateAccessToken: String = ""
) {
    companion object {
        private const val TAG = "AccountBenefits"
        
        /**
         * Parse account benefits from login response map.
         */
        fun parse(map: Map<String, Any>?): AccountBenefits {
            if (map == null) {
                Log.d(TAG, "No account benefits in response, using defaults")
                return AccountBenefits()
            }
            
            return try {
                AccountBenefits(
                    textureUploadCost = parseIntOrDefault(map["texture_upload_cost"], 10),
                    animationUploadCost = parseIntOrDefault(map["animation_upload_cost"], 10),
                    soundUploadCost = parseIntOrDefault(map["sound_upload_cost"], 10),
                    meshUploadCost = parseIntOrDefault(map["mesh_upload_cost"], 10),
                    groupMembershipLimit = parseIntOrDefault(map["group_membership_limit"], 42),
                    attachmentLimit = parseIntOrDefault(map["attachment_limit"], 38),
                    animatedObjectLimit = parseIntOrDefault(map["animated_object_limit"], 1),
                    picksLimit = parseIntOrDefault(map["picks_limit"], 10),
                    stipend = parseIntOrDefault(map["stipend"], 0),
                    signupBonus = parseIntOrDefault(map["signup_bonus"], 0),
                    premiumAccess = parseBoolOrDefault(map["premium_access"], false),
                    createGroupCost = parseIntOrDefault(map["create_group_cost"], 0),
                    partnerFee = parseIntOrDefault(map["partner_fee"], 10),
                    unpartnerFee = parseIntOrDefault(map["unpartner_fee"], 25),
                    landAuctionsAllowed = parseBoolOrDefault(map["land_auctions_allowed"], false),
                    voiceMorphing = parseBoolOrDefault(map["voice_morphing"], false),
                    storedImLimit = parseIntOrDefault(map["stored_im_limit"], 15),
                    transactionHistoryLimit = parseIntOrDefault(map["transaction_history_limit"], 30),
                    scriptLimit = parseIntOrDefault(map["script_limit"], 0),
                    mainlandTier = parseIntOrDefault(map["mainland_tier"], 0),
                    localExperiences = parseIntOrDefault(map["local_experiences"], 0),
                    gridwideExperienceLimit = parseIntOrDefault(map["gridwide_experience_limit"], 0),
                    marketplaceListingLimit = parseIntOrDefault(map["marketplace_listing_limit"], 0),
                    premiumAlts = parseIntOrDefault(map["premium_alts"], 0),
                    priorityEntry = parseBoolOrDefault(map["priority_entry"], false),
                    liveChat = parseBoolOrDefault(map["live_chat"], false),
                    phoneSupport = parseBoolOrDefault(map["phone_support"], false),
                    betaGridLand = parseBoolOrDefault(map["beta_grid_land"], false),
                    premiumGifts = parseBoolOrDefault(map["premium_gifts"], false),
                    useAnimesh = parseBoolOrDefault(map["use_animesh"], true),
                    lindenBuyFee = parseDoubleOrDefault(map["linden_buy_fee"], 0.0),
                    largeTextureUploadCost = parseIntListOrDefault(map["large_texture_upload_cost"], listOf(10)),
                    estateAccessToken = parseStringOrDefault(map["estate_access_token"], "")
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse account benefits", e)
                AccountBenefits()
            }
        }
        
        private fun parseIntOrDefault(value: Any?, default: Int): Int {
            return when (value) {
                is Number -> value.toInt()
                is String -> value.toIntOrNull() ?: default
                else -> default
            }
        }
        
        private fun parseBoolOrDefault(value: Any?, default: Boolean): Boolean {
            return when (value) {
                is Boolean -> value
                is Number -> value.toInt() != 0
                is String -> value.lowercase() in listOf("true", "1", "yes")
                else -> default
            }
        }
        
        private fun parseDoubleOrDefault(value: Any?, default: Double): Double {
            return when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull() ?: default
                else -> default
            }
        }
        
        private fun parseStringOrDefault(value: Any?, default: String): String {
            return value?.toString() ?: default
        }
        
        @Suppress("UNCHECKED_CAST")
        private fun parseIntListOrDefault(value: Any?, default: List<Int>): List<Int> {
            return when (value) {
                is List<*> -> value.mapNotNull { 
                    when (it) {
                        is Number -> it.toInt()
                        is String -> it.toIntOrNull()
                        else -> null
                    }
                }.ifEmpty { default }
                else -> default
            }
        }
    }
    
    /**
     * Check if this is a premium account.
     */
    val isPremium: Boolean get() = premiumAccess || stipend > 0 || mainlandTier > 0
    
    /**
     * Get a human-readable account type.
     */
    val accountType: String get() = when {
        mainlandTier >= 65536 -> "Premium Plus"
        premiumAccess -> "Premium"
        else -> "Basic"
    }
    
    override fun toString(): String {
        return "AccountBenefits(type=$accountType, groups=$groupMembershipLimit, attachments=$attachmentLimit)"
    }
}
