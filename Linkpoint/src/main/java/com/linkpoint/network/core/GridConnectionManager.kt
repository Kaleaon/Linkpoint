package com.linkpoint.network.core

import android.util.Log
import com.linkpoint.network.NetworkLogger
import java.util.UUID
import java.util.WeakHashMap
import javax.annotation.Nullable
import javax.annotation.Nonnull

/**
 * Grid Connection Manager
 * 
 * Manages multiple SL grid connections using a thread-safe WeakHashMap.
 * Based on Lumiya's GridConnectionManager implementation.
 * 
 * Features:
 * - Thread-safe connection management
 * - UUID-based connection tracking
 * - Automatic cleanup via weak references
 * - Mobile-optimized for resource efficiency
 * 
 * Mobile-First Considerations:
 * - WeakHashMap prevents memory leaks on mobile devices
 * - Thread-safe operations prevent race conditions
 * - Efficient connection pooling for better performance
 */
object GridConnectionManager {
    
    private const val TAG = "GridConnectionManager"
    
    /**
     * Thread-safe lock for connection operations
     */
    private val lock = Any()
    
    /**
     * WeakHashMap for connection storage
     * Automatically cleans up unused connections
     * Prevents memory leaks on mobile devices
     */
    private val connections: MutableMap<UUID, GridConnection> = WeakHashMap()
    
    /**
     * Get a connection by UUID
     * 
     * @param connectionId The UUID of the connection to retrieve
     * @return The connection if found, null otherwise
     */
    @Nullable
    fun getConnection(connectionId: UUID?): GridConnection? {
        if (connectionId == null) {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "getConnection: null connectionId provided")
            return null
        }
        
        synchronized(lock) {
            val connection = connections[connectionId]
            if (connection != null) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Retrieved connection: $connectionId")
            } else {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Connection not found: $connectionId")
            }
            return connection
        }
    }
    
    /**
     * Add a new connection
     * 
     * @param connectionId The UUID for the connection
     * @param connection The GridConnection to add
     */
    fun addConnection(@Nonnull connectionId: UUID, connection: GridConnection) {
        synchronized(lock) {
            connections[connectionId] = connection
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Added connection: $connectionId")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Active connections: ${connections.size}")
        }
    }
    
    /**
     * Remove a connection
     * 
     * @param connectionId The UUID of the connection to remove
     * @param connection The connection being removed (for verification)
     */
    fun removeConnection(@Nonnull connectionId: UUID, connection: GridConnection) {
        synchronized(lock) {
            if (connections.remove(connectionId) == connection) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Removed connection: $connectionId")
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Remaining connections: ${connections.size}")
            } else {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Connection not found or mismatch: $connectionId")
            }
        }
    }
    
    /**
     * Get all active connections
     * 
     * @return List of all active connections
     */
    fun getAllConnections(): List<GridConnection> {
        synchronized(lock) {
            return connections.values.toList()
        }
    }
    
    /**
     * Get the number of active connections
     * 
     * @return Number of active connections
     */
    fun getConnectionCount(): Int {
        synchronized(lock) {
            return connections.size
        }
    }
    
    /**
     * Check if a connection exists
     * 
     * @param connectionId The UUID to check
     * @return true if the connection exists, false otherwise
     */
    fun hasConnection(@Nonnull connectionId: UUID): Boolean {
        synchronized(lock) {
            return connections.containsKey(connectionId)
        }
    }
    
    /**
     * Clear all connections
     * Used for cleanup or testing
     */
    fun clearAllConnections() {
        synchronized(lock) {
            val count = connections.size
            connections.clear()
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Cleared all connections: $count")
        }
    }
    
    /**
     * Get connection statistics
     * 
     * @return Map containing connection statistics
     */
    fun getStatistics(): Map<String, Any> {
        synchronized(lock) {
            return mapOf(
                "activeConnections" to connections.size,
                "connectionIds" to connections.keys.toList(),
                "memoryUsage" to estimateMemoryUsage()
            )
        }
    }
    
    /**
     * Estimate memory usage of connections
     * 
     * @return Estimated memory usage in bytes
     */
    private fun estimateMemoryUsage(): Long {
        synchronized(lock) {
            // Base overhead of WeakHashMap + connections
            val baseOverhead = 1024L
            // Estimate each connection uses ~1KB
            val connectionOverhead = connections.size * 1024L
            return baseOverhead + connectionOverhead
        }
    }
}