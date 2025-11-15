package com.lumiyaviewer.lumiya.database.dao

import androidx.room.*
import com.lumiyaviewer.lumiya.database.entities.MoneyTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

/**
 * Modern Room DAO for MoneyTransaction entities
 */
@Dao
interface MoneyTransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: MoneyTransactionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<MoneyTransactionEntity>): List<Long>
    
    @Update
    suspend fun update(transaction: MoneyTransactionEntity)
    
    @Delete
    suspend fun delete(transaction: MoneyTransactionEntity)
    
    @Query("SELECT * FROM money_transactions WHERE id = :id")
    suspend fun getById(id: Long): MoneyTransactionEntity?
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC")
    suspend fun getAll(): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<MoneyTransactionEntity>>
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentFlow(limit: Int): Flow<List<MoneyTransactionEntity>>
    
    @Query("SELECT * FROM money_transactions WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getByDateRange(startDate: Date, endDate: Date): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions WHERE agentUUID = :agentUUID ORDER BY timestamp DESC")
    suspend fun getByAgent(agentUUID: UUID): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions WHERE transactionAmount > 0 ORDER BY timestamp DESC")
    suspend fun getCredits(): List<MoneyTransactionEntity>
    
    @Query("SELECT * FROM money_transactions WHERE transactionAmount < 0 ORDER BY timestamp DESC")
    suspend fun getDebits(): List<MoneyTransactionEntity>
    
    @Query("SELECT SUM(transactionAmount) FROM money_transactions")
    suspend fun getTotalChange(): Int?
    
    @Query("SELECT SUM(transactionAmount) FROM money_transactions WHERE timestamp BETWEEN :startDate AND :endDate")
    suspend fun getTotalChangeInRange(startDate: Date, endDate: Date): Int?
    
    @Query("SELECT COUNT(*) FROM money_transactions")
    suspend fun getCount(): Int
    
    @Query("DELETE FROM money_transactions WHERE timestamp < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: Date): Int
    
    @Query("DELETE FROM money_transactions")
    suspend fun deleteAll()
}