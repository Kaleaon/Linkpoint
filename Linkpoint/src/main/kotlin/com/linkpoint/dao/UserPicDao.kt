package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class UserPicDao : AbstractDao()<UserPic, Long> {
    const val String TABLENAME = "USER_PIC"

    @JvmStatic
    class Properties {
        const val Property Bitmap = Property(2, Byte[].class, "bitmap", false, "BITMAP")
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
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'USER_PIC' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'BITMAP' BLOB);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_USER_PIC_UUID ON USER_PIC" + " (UUID);")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'USER_PIC'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, UserPic userPic) {
        sQLiteStatement.clearBindings()
        Long id = userPic.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        String uuid = userPic.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid)
        }
        Byte[] bitmap = userPic.getBitmap()
        if (bitmap != null) {
            sQLiteStatement.bindBlob(3, bitmap)
        }
    }

    public Long getKey(UserPic userPic) {
        return userPic != null ? userPic.getId() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public UserPic readEntity(Cursor cursor, Int i) {
        Byte[] bArr = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        String string = cursor.isNull(i + 1) ? null : cursor.getString(i + 1)
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        return UserPic(valueOf, string, bArr)
    }

    public Unit readEntity(Cursor cursor, UserPic userPic, Int i) {
        Byte[] bArr = null
        userPic.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        userPic.setUuid(cursor.isNull(i + 1) ? null : cursor.getString(i + 1))
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        userPic.setBitmap(bArr)
    }

    public Long readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected Long updateKeyAfterInsert(UserPic userPic, Long j) {
        userPic.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
