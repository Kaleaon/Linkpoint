
    // ========== FRIENDS ==========
    
    /**
     * Get all friends
     */
    suspend fun getAllFriends(): List<FriendEntity> {
        return friendDao.getAll()
    }
    
    /**
     * Get online friends
     */
    suspend fun getOnlineFriends(): List<FriendEntity> {
        return friendDao.getOnline()
    }
    
    /**
     * Add a friend
     */
    suspend fun addFriend(friend: FriendEntity): Long {
        return friendDao.insert(friend)
    }
    
    /**
     * Remove a friend
     */
    suspend fun removeFriend(uuid: UUID) {
        friendDao.deleteByUUID(uuid)
    }
    
    /**
     * Update friend
     */
    suspend fun updateFriend(friend: FriendEntity) {
        friendDao.update(friend)
    }
    
    /**
     * Get friend by UUID
     */
    suspend fun getFriendByUUID(uuid: UUID): FriendEntity? {
        return friendDao.getByUUID(uuid)
    }

