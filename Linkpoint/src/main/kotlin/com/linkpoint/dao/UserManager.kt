package com.linkpoint.dao

import java.util.UUID

/**
 * Stub class for UserManager to resolve dependencies
 */
class UserManager {
    @JvmStatic
    UserManager getUserManager(UUID agentId) {
        return UserManager()
    }
    
    public Object getInventoryManager() {
        return null
    }
}
