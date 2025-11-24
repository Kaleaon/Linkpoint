package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class UserDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<User, Long>(config, daoSession) {

    companion object {
        const val TABLENAME = "Users"

        object Properties {
            @JvmField val BadUUID = Property(4, Boolean::class.java, "badUUID", false, "BAD_UUID")
            @JvmField val DisplayName = Property(3, String::class.java, "displayName", false, "DISPLAY_NAME")
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
            @JvmField val IsFriend = Property(5, Boolean::class.java, "isFriend", false, "IS_FRIEND")
            @JvmField val RightsGiven = Property(6, Int::class.java, "rightsGiven", false, "RIGHTS_GIVEN")
            @JvmField val RightsHas = Property(7, Int::class.java, "rightsHas", false, "RIGHTS_HAS")
            @JvmField val UserName = Property(2, String::class.java, "userName", false, "USER_NAME")
            @JvmField val Uuid = Property(1, String::class.java, "uuid", false, "UUID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'Users' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'UUID' TEXT," +
                    "'USER_NAME' TEXT," +
                    "'DISPLAY_NAME' TEXT," +
                    "'BAD_UUID' INTEGER NOT NULL ," +
                    "'IS_FRIEND' INTEGER NOT NULL ," +
                    "'RIGHTS_GIVEN' INTEGER NOT NULL ," +
                    "'RIGHTS_HAS' INTEGER NOT NULL );")
            db.execSQL("CREATE INDEX ${constraint}IDX_Users_UUID ON Users (UUID);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'Users'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: User) {
        stmt.clearBindings()
        val id = entity.id
        if (id != null) {
            stmt.bindLong(1, id)
        }
        val uuid = entity.uuid
        if (uuid != null) {
            stmt.bindString(2, uuid.toString())
        }
        val userName = entity.userName
        if (userName != null) {
            stmt.bindString(3, userName)
        }
        val displayName = entity.displayName
        if (displayName != null) {
            stmt.bindString(4, displayName)
        }
        stmt.bindLong(5, if (entity.badUUID) 1L else 0L)
        stmt.bindLong(6, if (entity.isFriend) 1L else 0L)
        stmt.bindLong(7, entity.rightsGiven.toLong())
        stmt.bindLong(8, entity.rightsHas.toLong())
    }

    override fun getKey(entity: User?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): User {
        val id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        val uuid = if (cursor.isNull(offset + 1)) null else UUID.fromString(cursor.getString(offset + 1))
        val userName = if (cursor.isNull(offset + 2)) null else cursor.getString(offset + 2)
        val displayName = if (cursor.isNull(offset + 3)) null else cursor.getString(offset + 3)
        val badUUID = cursor.getShort(offset + 4).toInt() != 0
        val isFriend = cursor.getShort(offset + 5).toInt() != 0
        val rightsGiven = cursor.getInt(offset + 6)
        val rightsHas = cursor.getInt(offset + 7)
        
        return User(id, uuid, userName, displayName, badUUID, isFriend, rightsGiven, rightsHas)
    }

    override fun readEntity(cursor: Cursor, entity: User, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        entity.uuid = if (cursor.isNull(offset + 1)) null else UUID.fromString(cursor.getString(offset + 1))
        entity.userName = if (cursor.isNull(offset + 2)) null else cursor.getString(offset + 2)
        entity.displayName = if (cursor.isNull(offset + 3)) null else cursor.getString(offset + 3)
        entity.badUUID = cursor.getShort(offset + 4).toInt() != 0
        entity.isFriend = cursor.getShort(offset + 5).toInt() != 0
        entity.rightsGiven = cursor.getInt(offset + 6)
        entity.rightsHas = cursor.getInt(offset + 7)
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: User, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
}
