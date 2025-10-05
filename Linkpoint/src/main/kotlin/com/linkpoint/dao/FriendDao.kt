package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class FriendDao : AbstractDao()<Friend, UUID> {
    const val TABLENAME: String = "Friends"

    @JvmStatic
    class Properties {
        const val Property IsOnline = Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE")
        const val Property RightsGiven = Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN")
        const val Property RightsHas = Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS")
        const val Property Uuid = Property(0, UUID.class, "uuid", true, "UUID")
    }

    public FriendDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public FriendDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'Friends' (" + "'UUID' TEXT PRIMARY KEY ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL ," + "'IS_ONLINE' INTEGER NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Friends'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, Friend friend) {
        sQLiteStatement.clearBindings()
        UUID uuid = friend.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString())
        }
        sQLiteStatement.bindLong(2, (Long) friend.getRightsGiven())
        sQLiteStatement.bindLong(3, (Long) friend.getRightsHas())
        sQLiteStatement.bindLong(4, friend.getIsOnline() ? 1 : 0)
    }

    public UUID getKey(Friend friend) {
        return friend != null ? friend.getUuid() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public Friend readEntity(Cursor cursor, Int i) {
        Boolean z = false
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
        Int i2 = cursor.getInt(i + 1)
        Int i3 = cursor.getInt(i + 2)
        if (cursor.getShort(i + 3) != (Short) 0) {
            z = true
        }
        return Friend(fromString, i2, i3, z)
    }

    public Unit readEntity(Cursor cursor, Friend friend, Int i) {
        friend.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        friend.setRightsGiven(cursor.getInt(i + 1))
        friend.setRightsHas(cursor.getInt(i + 2))
        friend.setIsOnline(cursor.getShort(i + 3) != (Short) 0)
    }

    public UUID readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected UUID updateKeyAfterInsert(Friend friend, Long j) {
        return friend.getUuid()
    }
}
