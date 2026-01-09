package com.linkpoint.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import de.greenrobot.dao.AbstractDaoMaster
import de.greenrobot.dao.identityscope.IdentityScopeType

/**
 * Master database manager using GreenDAO
 */
class DaoMaster(database: SQLiteDatabase) : AbstractDaoMaster(database, SCHEMA_VERSION) {
    
    companion object {
        const val SCHEMA_VERSION = 71
        
        /**
         * Create all database tables
         */
        fun createAllTables(db: SQLiteDatabase, ifNotExists: Boolean) {
            CachedResponseDao.createTable(db, ifNotExists)
            CachedAssetDao.createTable(db, ifNotExists)
            MoneyTransactionDao.createTable(db, ifNotExists)
            MuteListCachedDataDao.createTable(db, ifNotExists)
            SearchGridResultDao.createTable(db, ifNotExists)
            GroupMemberDao.createTable(db, ifNotExists)
            GroupMemberListDao.createTable(db, ifNotExists)
            GroupRoleMemberDao.createTable(db, ifNotExists)
            GroupRoleMemberListDao.createTable(db, ifNotExists)
            UserDao.createTable(db, ifNotExists)
            FriendDao.createTable(db, ifNotExists)
            UserNameDao.createTable(db, ifNotExists)
            UserPicDao.createTable(db, ifNotExists)
            ChatMessageDao.createTable(db, ifNotExists)
            ChatterDao.createTable(db, ifNotExists)
        }

        /**
         * Drop all database tables
         */
        fun dropAllTables(db: SQLiteDatabase, ifExists: Boolean) {
            CachedResponseDao.dropTable(db, ifExists)
            CachedAssetDao.dropTable(db, ifExists)
            MoneyTransactionDao.dropTable(db, ifExists)
            MuteListCachedDataDao.dropTable(db, ifExists)
            SearchGridResultDao.dropTable(db, ifExists)
            GroupMemberDao.dropTable(db, ifExists)
            GroupMemberListDao.dropTable(db, ifExists)
            GroupRoleMemberDao.dropTable(db, ifExists)
            GroupRoleMemberListDao.dropTable(db, ifExists)
            UserDao.dropTable(db, ifExists)
            FriendDao.dropTable(db, ifExists)
            UserNameDao.dropTable(db, ifExists)
            UserPicDao.dropTable(db, ifExists)
            ChatMessageDao.dropTable(db, ifExists)
            ChatterDao.dropTable(db, ifExists)
        }
    }

    /**
     * Base open helper for database
     */
    abstract class OpenHelper(
        context: Context,
        name: String,
        factory: SQLiteDatabase.CursorFactory?
    ) : SQLiteOpenHelper(context, name, factory, SCHEMA_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            Log.i("greenDAO", "Creating tables for schema version $SCHEMA_VERSION")
            createAllTables(db, ifNotExists = false)
        }
    }

    /**
     * Development open helper that drops tables on upgrade
     */
    class DevOpenHelper(
        context: Context,
        name: String,
        factory: SQLiteDatabase.CursorFactory? = null
    ) : OpenHelper(context, name, factory) {

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.i("greenDAO", "Upgrading schema from version $oldVersion to $newVersion by dropping all tables")
            dropAllTables(db, ifExists = true)
            onCreate(db)
        }
    }

    init {
        registerDaoClass(CachedResponseDao::class.java)
        registerDaoClass(CachedAssetDao::class.java)
        registerDaoClass(MoneyTransactionDao::class.java)
        registerDaoClass(MuteListCachedDataDao::class.java)
        registerDaoClass(SearchGridResultDao::class.java)
        registerDaoClass(GroupMemberDao::class.java)
        registerDaoClass(GroupMemberListDao::class.java)
        registerDaoClass(GroupRoleMemberDao::class.java)
        registerDaoClass(GroupRoleMemberListDao::class.java)
        registerDaoClass(UserDao::class.java)
        registerDaoClass(FriendDao::class.java)
        registerDaoClass(UserNameDao::class.java)
        registerDaoClass(UserPicDao::class.java)
        registerDaoClass(ChatMessageDao::class.java)
        registerDaoClass(ChatterDao::class.java)
    }

    /**
     * Create new DAO session
     */
    fun newSession(): DaoSession {
        return DaoSession(db, IdentityScopeType.Session, daoConfigMap)
    }

    /**
     * Create new DAO session with custom identity scope
     */
    fun newSession(identityScopeType: IdentityScopeType): DaoSession {
        return DaoSession(db, identityScopeType, daoConfigMap)
    }
}
