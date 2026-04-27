package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class UserPicDao : AbstractDao<UserPic, Long> {
    val TABLENAME: String = "USER_PIC"

    class Properties {
        Property Bitmap = Property(2, ByteArray.class, "bitmap", false, "BITMAP")
        Property Id = Property(0, Long.class, "id", true, "_id")
        Property Uuid = Property(1, String.class, "uuid", false, "UUID")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'USER_PIC' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'BITMAP' BLOB);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_USER_PIC_UUID ON USER_PIC" + " (UUID);")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'USER_PIC'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, userPic: UserPic): Unit {
        sQLiteStatement.clearBindings()
        Long id = userPic.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        String uuid = userPic.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid)
        }
        ByteArray bitmap = userPic.getBitmap()
        if (bitmap != null) {
            sQLiteStatement.bindBlob(3, bitmap)
        }
    }

    fun getKey(userPic: UserPic): Long {
        return userPic != null ? userPic.getId() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): UserPic {
        ByteArray bArr = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        String string = cursor.isNull(i + 1) ? null : cursor.getString(i + 1)
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        return UserPic(valueOf, string, bArr)
    }

    fun readEntity(cursor: Cursor, userPic: UserPic, i: Int): Unit {
        ByteArray bArr = null
        userPic.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        userPic.setUuid(cursor.isNull(i + 1) ? null : cursor.getString(i + 1))
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        userPic.setBitmap(bArr)
    }

    fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected fun updateKeyAfterInsert(userPic: UserPic, j: Long): Long {
        userPic.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
