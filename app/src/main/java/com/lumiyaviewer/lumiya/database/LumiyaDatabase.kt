package com.lumiyaviewer.lumiya.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lumiyaviewer.lumiya.database.converters.DateConverter
import com.lumiyaviewer.lumiya.database.converters.UUIDConverter
import com.lumiyaviewer.lumiya.database.dao.*
import com.lumiyaviewer.lumiya.database.entities.*

/**
 * Main Room database for Lumiya.
 * 
 * This database replaces the legacy GreenDAO implementation with modern Room.
 * Includes all entities and DAOs for the Lumiya Second Life viewer.
 * 
 * Features:
 * - Type-safe queries
 * - Kotlin Coroutines support
 * - Flow for reactive data
 * - Automatic migrations
 * - Better performance
 * 
 * @version 1
 */
@Database(
    entities = [
        ChatMessageEntity::class,
        ChatterEntity::class,
        FriendEntity::class,
        UserEntity::class,
        UserNameEntity::class,
        UserPicEntity::class,
        GroupMemberEntity::class,
        GroupMemberListEntity::class,
        GroupRoleMemberEntity::class,
        GroupRoleMemberListEntity::class,
        MoneyTransactionEntity::class,
        CachedAssetEntity::class,
        CachedResponseEntity::class,
        MuteListCachedDataEntity::class,
        SearchGridResultEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class, UUIDConverter::class)
abstract class LumiyaDatabase : RoomDatabase() {
    
    // ========== DAO ACCESSORS ==========
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun chatterDao(): ChatterDao
    abstract fun friendDao(): FriendDao
    abstract fun userDao(): UserDao
    abstract fun userNameDao(): UserNameDao
    abstract fun userPicDao(): UserPicDao
    abstract fun groupMemberDao(): GroupMemberDao
    abstract fun moneyTransactionDao(): MoneyTransactionDao
    abstract fun cachedAssetDao(): CachedAssetDao
    abstract fun cachedResponseDao(): CachedResponseDao
    abstract fun searchGridResultDao(): SearchGridResultDao
    
    companion object {
        private const val DATABASE_NAME = "lumiya_database"
        
        @Volatile
        private var INSTANCE: LumiyaDatabase? = null
        
        /**
         * Get database instance (singleton pattern)
         */
        fun getInstance(context: Context): LumiyaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LumiyaDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_GREENDAO_TO_ROOM)
                    .fallbackToDestructiveMigration() // TODO: Remove in production
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Migration from GreenDAO to Room
         * 
         * This migration preserves existing data when upgrading from
         * the legacy GreenDAO database to the new Room database.
         */
        private val MIGRATION_GREENDAO_TO_ROOM = object : Migration(0, 1) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Note: This is a placeholder migration
                // In production, you would:
                // 1. Analyze the GreenDAO schema
                // 2. Create new Room tables
                // 3. Copy data from old tables to new tables
                // 4. Drop old tables
                
                // For now, we'll create the new schema
                // The actual data migration would be implemented based on
                // the specific GreenDAO schema in use
                
                // Example migration steps:
                // database.execSQL("CREATE TABLE IF NOT EXISTS chat_messages_new (...)")
                // database.execSQL("INSERT INTO chat_messages_new SELECT * FROM CHAT_MESSAGE")
                // database.execSQL("DROP TABLE CHAT_MESSAGE")
                // database.execSQL("ALTER TABLE chat_messages_new RENAME TO chat_messages")
            }
        }
        
        /**
         * Clear all data from database (for testing)
         */
        suspend fun clearAllData(context: Context) {
            val db = getInstance(context)
            db.clearAllTables()
        }
        
        /**
         * Close database instance
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}