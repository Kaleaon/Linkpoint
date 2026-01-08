package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class UserPicDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<UserPic, Long>(config, daoSession) {

    companion object {
        const val TABLENAME = "USER_PIC"

        object Properties {
            @JvmField val Bitmap = Property(2, ByteArray::class.java, "bitmap", false, "BITMAP")
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
            @JvmField val Uuid = Property(1, String::class.java, "uuid", false, "UUID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'USER_PIC' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'UUID' TEXT," +
                    "'BITMAP' BLOB);")
            db.execSQL("CREATE INDEX ${constraint}IDX_USER_PIC_UUID ON USER_PIC (UUID);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'USER_PIC'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: UserPic) {
        stmt.clearBindings()
        val id = entity.id
        if (id != null) {
            stmt.bindLong(1, id)
        }
        val uuid = entity.uuid
        if (uuid != null) {
            stmt.bindString(2, uuid)
        }
        val bitmap = entity.bitmap
        if (bitmap != null) {
            stmt.bindBlob(3, bitmap)
        }
    }

    override fun getKey(entity: UserPic?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): UserPic {
        val id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        val uuid = if (cursor.isNull(offset + 1)) null else cursor.getString(offset + 1)
        val bitmap = if (cursor.isNull(offset + 2)) null else cursor.getBlob(offset + 2)
        return UserPic(id, uuid, bitmap)
    }

    override fun readEntity(cursor: Cursor, entity: UserPic, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        entity.uuid = if (cursor.isNull(offset + 1)) null else cursor.getString(offset + 1)
        entity.bitmap = if (cursor.isNull(offset + 2)) null else cursor.getBlob(offset + 2)
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: UserPic, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
}
