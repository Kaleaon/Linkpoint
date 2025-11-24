package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class FriendDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<Friend, UUID>(config, daoSession) {

    companion object {
        const val TABLENAME = "Friends"

        object Properties {
            @JvmField val IsOnline = Property(3, Boolean::class.java, "isOnline", false, "IS_ONLINE")
            @JvmField val RightsGiven = Property(1, Int::class.java, "rightsGiven", false, "RIGHTS_GIVEN")
            @JvmField val RightsHas = Property(2, Int::class.java, "rightsHas", false, "RIGHTS_HAS")
            @JvmField val Uuid = Property(0, String::class.java, "uuid", true, "UUID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'Friends' (" +
                    "'UUID' TEXT PRIMARY KEY ," +
                    "'RIGHTS_GIVEN' INTEGER NOT NULL ," +
                    "'RIGHTS_HAS' INTEGER NOT NULL ," +
                    "'IS_ONLINE' INTEGER NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'Friends'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: Friend) {
        stmt.clearBindings()
        val uuid = entity.uuid
        if (uuid != null) {
            stmt.bindString(1, uuid.toString())
        }
        stmt.bindLong(2, entity.rightsGiven.toLong())
        stmt.bindLong(3, entity.rightsHas.toLong())
        stmt.bindLong(4, if (entity.isOnline) 1L else 0L)
    }

    override fun getKey(entity: Friend?): UUID? {
        return entity?.uuid
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): Friend {
        val uuidStr = cursor.getString(offset + 0)
        val uuid = if (cursor.isNull(offset + 0) || uuidStr == null) UUID(0, 0) else UUID.fromString(uuidStr)
        val rightsGiven = cursor.getInt(offset + 1)
        val rightsHas = cursor.getInt(offset + 2)
        val isOnline = cursor.getShort(offset + 3).toInt() != 0
        return Friend(uuid, rightsGiven, rightsHas, isOnline)
    }

    override fun readEntity(cursor: Cursor, entity: Friend, offset: Int) {
        val uuidStr = cursor.getString(offset + 0)
        entity.uuid = if (cursor.isNull(offset + 0) || uuidStr == null) UUID(0, 0) else UUID.fromString(uuidStr)
        entity.rightsGiven = cursor.getInt(offset + 1)
        entity.rightsHas = cursor.getInt(offset + 2)
        entity.isOnline = cursor.getShort(offset + 3).toInt() != 0
    }

    override fun readKey(cursor: Cursor, offset: Int): UUID? {
        val uuidStr = cursor.getString(offset + 0)
        return if (cursor.isNull(offset + 0) || uuidStr == null) null else UUID.fromString(uuidStr)
    }

    override fun updateKeyAfterInsert(entity: Friend, rowId: Long): UUID? {
        return entity.uuid
    }
}
