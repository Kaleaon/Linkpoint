package com.linkpoint.dao;

import java.util.UUID;

/**
 * Stub class for UserManager to resolve dependencies
 */
public class UserManager {
    public static UserManager getUserManager(UUID agentId) {
        return new UserManager();
    }
    
    public Object getInventoryManager() {
        return null;
    }
}
