package com.linkpoint.dao

import java.util.UUID

/**
 * Stub class for UserManager to resolve dependencies
 */
class UserManager {
    @JvmStatic
     fun getUserManager(agentId: UUID): UserManager {
        return UserManager()
    }
    
     public fun getInventoryManager(): Object {
        return null
    }
}
