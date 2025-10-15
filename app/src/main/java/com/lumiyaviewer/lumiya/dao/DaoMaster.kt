package com.lumiyaviewer.lumiya.dao
import java.util.*

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import de.greenrobot.dao.AbstractDaoMaster
import de.greenrobot.dao.identityscope.IdentityScopeType

class DaoMaster extends AbstractDaoMaster {
    int SCHEMA_VERSION = 71

    abstract class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context, String str, CursorFactory cursorFactory) {
            super(context, str, cursorFactory, 71)
        }

        fun onCreate(sQLiteDatabase: SQLiteDatabase): Unit {
            Log.i("greenDAO", "Creating tables for schema version 71")
            DaoMaster.createAllTables(sQLiteDatabase, false)
        }
    }

    class DevOpenHelper extends OpenHelper {
        DevOpenHelper(Context context, String str, CursorFactory cursorFactory) {
            super(context, str, cursorFactory)
        }

        fun onUpgrade(sQLiteDatabase: SQLiteDatabase, i: Int, i2: Int): Unit {
            Log.i("greenDAO", "Upgrading schema from version " + i + " to " + i2 + " by dropping all tables")
            DaoMaster.dropAllTables(sQLiteDatabase, true)
            onCreate(sQLiteDatabase)
        }
    }

    constructor(sQLiteDatabase: SQLiteDatabase) {
        super(sQLiteDatabase, 71)
        registerDaoClass(CachedResponseDao.class)
        registerDaoClass(CachedAssetDao.class)
        registerDaoClass(MoneyTransactionDao.class)
        registerDaoClass(MuteListCachedDataDao.class)
        registerDaoClass(SearchGridResultDao.class)
        registerDaoClass(GroupMemberDao.class)
        registerDaoClass(GroupMemberListDao.class)
        registerDaoClass(GroupRoleMemberDao.class)
        registerDaoClass(GroupRoleMemberListDao.class)
        registerDaoClass(UserDao.class)
        registerDaoClass(FriendDao.class)
        registerDaoClass(UserNameDao.class)
        registerDaoClass(UserPicDao.class)
        registerDaoClass(ChatMessageDao.class)
        registerDaoClass(ChatterDao.class)
    }

    fun createAllTables(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        CachedResponseDao.createTable(sQLiteDatabase, z)
        CachedAssetDao.createTable(sQLiteDatabase, z)
        MoneyTransactionDao.createTable(sQLiteDatabase, z)
        MuteListCachedDataDao.createTable(sQLiteDatabase, z)
        SearchGridResultDao.createTable(sQLiteDatabase, z)
        GroupMemberDao.createTable(sQLiteDatabase, z)
        GroupMemberListDao.createTable(sQLiteDatabase, z)
        GroupRoleMemberDao.createTable(sQLiteDatabase, z)
        GroupRoleMemberListDao.createTable(sQLiteDatabase, z)
        UserDao.createTable(sQLiteDatabase, z)
        FriendDao.createTable(sQLiteDatabase, z)
        UserNameDao.createTable(sQLiteDatabase, z)
        UserPicDao.createTable(sQLiteDatabase, z)
        ChatMessageDao.createTable(sQLiteDatabase, z)
        ChatterDao.createTable(sQLiteDatabase, z)
    }

    fun dropAllTables(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        CachedResponseDao.dropTable(sQLiteDatabase, z)
        CachedAssetDao.dropTable(sQLiteDatabase, z)
        MoneyTransactionDao.dropTable(sQLiteDatabase, z)
        MuteListCachedDataDao.dropTable(sQLiteDatabase, z)
        SearchGridResultDao.dropTable(sQLiteDatabase, z)
        GroupMemberDao.dropTable(sQLiteDatabase, z)
        GroupMemberListDao.dropTable(sQLiteDatabase, z)
        GroupRoleMemberDao.dropTable(sQLiteDatabase, z)
        GroupRoleMemberListDao.dropTable(sQLiteDatabase, z)
        UserDao.dropTable(sQLiteDatabase, z)
        FriendDao.dropTable(sQLiteDatabase, z)
        UserNameDao.dropTable(sQLiteDatabase, z)
        UserPicDao.dropTable(sQLiteDatabase, z)
        ChatMessageDao.dropTable(sQLiteDatabase, z)
        ChatterDao.dropTable(sQLiteDatabase, z)
    }

    fun newSession(): DaoSession {
        return new DaoSession(this.db, IdentityScopeType.Session, this.daoConfigMap)
    }

    fun newSession(identityScopeType: IdentityScopeType): DaoSession {
        return new DaoSession(this.db, identityScopeType, this.daoConfigMap)
    }
}
