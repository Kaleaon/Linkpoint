package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class UserPicDao : AbstractDao()<UserPic, Long> {
    const val TABLENAME: String = "USER_PIC"

    @JvmStatic
    class Properties {
        const val Property Bitmap = Property(2, ByteArray.class, "bitmap", false, "BITMAP")
        const val Property Id = Property(0, Long.class, "id", true, "_id")
        const val Property Uuid = Property(1, String.class, "uuid", false, "UUID")
    }

    public UserPicDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public UserPicDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        val str: String = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'USER_PIC' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'BITMAP' BLOB);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_USER_PIC_UUID ON USER_PIC" + " (UUID);")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'USER_PIC'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, userPic: UserPic) {
        sQLiteStatement.clearBindings()
        val id: Long = userPic.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        val uuid: String = userPic.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid)
        }
        val bitmap: ByteArray = userPic.getBitmap()
        if (bitmap != null) {
            sQLiteStatement.bindBlob(3, bitmap)
        }
    }

     public fun getKey(userPic: UserPic): Long {
        return userPic != null ? userPic.getId() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): UserPic {
        val bArr: ByteArray = null
        val valueOf: Long = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        val string: String = cursor.isNull(i + 1) ? null : cursor.getString(i + 1)
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        return UserPic(valueOf, string, bArr)
    }

    fun readEntity(cursor: Cursor, userPic: UserPic, i: Int) {
        val bArr: ByteArray = null
        userPic.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        userPic.setUuid(cursor.isNull(i + 1) ? null : cursor.getString(i + 1))
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        userPic.setBitmap(bArr)
    }

     public fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

     protected fun updateKeyAfterInsert(userPic: UserPic, j: Long): Long {
        userPic.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
