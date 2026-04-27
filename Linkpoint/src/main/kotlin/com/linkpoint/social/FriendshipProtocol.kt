package com.linkpoint.social

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

/**
 * OfferFriendship - Send friend request
 */
class OfferFriendship(
    val agentID: UUID,
    val sessionID: UUID,
    val destID: UUID,
    val message: String = "Will you be my friend?"
) : SLMessage() {
    
    override fun getMessageName() = "OfferFriendship"
    
    override fun encode(buffer: ByteBuffer) {
        // AgentData block
        writeUUID(buffer, agentID)
        writeUUID(buffer, sessionID)
        
        // FriendData block (Variable)
        buffer.put(1) // 1 block
        writeUUID(buffer, destID)
        
        // Online (always true when sending)
        buffer.put(1)
        
        // Message
        writeVariableString(buffer, message)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Received when someone offers friendship to us
    }
}

/**
 * AcceptFriendship - Accept friend request
 */
class AcceptFriendship(
    val agentID: UUID,
    val sessionID: UUID,
    val transactionID: UUID,
    val folderID: UUID = UUID(0, 0)  // Calling card folder
) : SLMessage() {
    
    override fun getMessageName() = "AcceptFriendship"
    
    override fun encode(buffer: ByteBuffer) {
        // AgentData block
        writeUUID(buffer, agentID)
        writeUUID(buffer, sessionID)
        
        // TransactionBlock
        writeUUID(buffer, transactionID)
        
        // FolderData block (Variable)
        buffer.put(1) // 1 block
        writeUUID(buffer, folderID)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Not typically received
    }
}

/**
 * DeclineFriendship - Decline friend request
 */
class DeclineFriendship(
    val agentID: UUID,
    val sessionID: UUID,
    val transactionID: UUID
) : SLMessage() {
    
    override fun getMessageName() = "DeclineFriendship"
    
    override fun encode(buffer: ByteBuffer) {
        // AgentData block
        writeUUID(buffer, agentID)
        writeUUID(buffer, sessionID)
        
        // TransactionBlock
        writeUUID(buffer, transactionID)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Not typically received
    }
}

/**
 * TerminateFriendship - Remove friend
 */
class TerminateFriendship(
    val agentID: UUID,
    val sessionID: UUID,
    val exFriendID: UUID
) : SLMessage() {
    
    override fun getMessageName() = "TerminateFriendship"
    
    override fun encode(buffer: ByteBuffer) {
        // AgentData block
        writeUUID(buffer, agentID)
        writeUUID(buffer, sessionID)
        
        // ExBlock
        writeUUID(buffer, exFriendID)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Not typically received
    }
}

/**
 * OnlineNotification - Received when friend comes online
 */
class OnlineNotification : SLMessage() {
    lateinit var agentIDs: List<UUID>
    
    override fun getMessageName() = "OnlineNotification"
    
    override fun encode(buffer: ByteBuffer) {
        // Not typically sent
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Read agent count
        val count = buffer.get().toInt() and 0xFF
        val ids = mutableListOf<UUID>()
        
        for (i in 0 until count) {
            ids.add(readUUID(buffer))
        }
        
        agentIDs = ids
    }
}

/**
 * OfflineNotification - Received when friend goes offline
 */
class OfflineNotification : SLMessage() {
    lateinit var agentIDs: List<UUID>
    
    override fun getMessageName() = "OfflineNotification"
    
    override fun encode(buffer: ByteBuffer) {
        // Not typically sent
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Read agent count
        val count = buffer.get().toInt() and 0xFF
        val ids = mutableListOf<UUID>()
        
        for (i in 0 until count) {
            ids.add(readUUID(buffer))
        }
        
        agentIDs = ids
    }
}
