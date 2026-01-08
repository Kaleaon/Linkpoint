package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class UserNameDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<UserName, UUID>(config, daoSession) {

    companion object {
        const val TABLENAME = "UserNames"

        object Properties {
            @JvmField val Uuid = Property(0, String::class.java, "uuid", true, "UUID")
            @JvmField val Name = Property(1, String::class.java, "userName", false, "NAME")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'UserNames' (" +
                    "'UUID' TEXT PRIMARY KEY ," +
                    "'NAME' TEXT NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'UserNames'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: UserName) {
        stmt.clearBindings()
        val uuid = entity.uuid
        stmt.bindString(1, uuid.toString())
        val name = entity.userName
        if (name != null) {
            stmt.bindString(2, name)
        } else {
            stmt.bindString(2, "")
        }
    }

    override fun getKey(entity: UserName?): UUID? {
        return entity?.uuid
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): UserName {
        val uuidStr = cursor.getString(offset + 0)
        val uuid = if (cursor.isNull(offset + 0) || uuidStr == null) UUID(0, 0) else UUID.fromString(uuidStr)
        val name = cursor.getString(offset + 1)
        return UserName(uuid, name)
    }

    override fun readEntity(cursor: Cursor, entity: UserName, offset: Int) {
        val uuidStr = cursor.getString(offset + 0)
        entity.uuid = if (cursor.isNull(offset + 0) || uuidStr == null) UUID(0, 0) else UUID.fromString(uuidStr)
        entity.userName = cursor.getString(offset + 1)
    }

    override fun readKey(cursor: Cursor, offset: Int): UUID? {
        val uuidStr = cursor.getString(offset + 0)
        return if (cursor.isNull(offset + 0) || uuidStr == null) null else UUID.fromString(uuidStr)
    }

    override fun updateKeyAfterInsert(entity: UserName, rowId: Long): UUID? {
        return entity.uuid
    }
}
