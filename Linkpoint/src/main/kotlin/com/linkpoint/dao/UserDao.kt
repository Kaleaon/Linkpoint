package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class UserDao : AbstractDao()<User, Long> {
    const val TABLENAME: String = "Users"

    @JvmStatic
    class Properties {
        const val Property BadUUID = Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID")
        const val Property DisplayName = Property(3, String.class, "displayName", false, "DISPLAY_NAME")
        const val Property Id = Property(0, Long.class, "id", true, "_id")
        const val Property IsFriend = Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND")
        const val Property RightsGiven = Property(6, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN")
        const val Property RightsHas = Property(7, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS")
        const val Property UserName = Property(2, String.class, "userName", false, "USER_NAME")
        const val Property Uuid = Property(1, UUID.class, "uuid", false, "UUID")
    }

    public UserDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public UserDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'Users' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'BAD_UUID' INTEGER NOT NULL ," + "'IS_FRIEND' INTEGER NOT NULL ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_Users_UUID ON Users" + " (UUID);")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Users'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, User user) {
        Long j = 1
        sQLiteStatement.clearBindings()
        Long id = user.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        UUID uuid = user.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid.toString())
        }
        String userName = user.getUserName()
        if (userName != null) {
            sQLiteStatement.bindString(3, userName)
        }
        userName = user.getDisplayName()
        if (userName != null) {
            sQLiteStatement.bindString(4, userName)
        }
        sQLiteStatement.bindLong(5, user.getBadUUID() ? 1 : 0)
        if (!user.getIsFriend()) {
            j = 0
        }
        sQLiteStatement.bindLong(6, j)
        sQLiteStatement.bindLong(7, (Long) user.getRightsGiven())
        sQLiteStatement.bindLong(8, (Long) user.getRightsHas())
    }

    public Long getKey(User user) {
        return user != null ? user.getId() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public User readEntity(Cursor cursor, Int i) {
        Boolean z = true
        String str = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        UUID fromString = cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1))
        String string = cursor.isNull(i + 2) ? null : cursor.getString(i + 2)
        if (!cursor.isNull(i + 3)) {
            str = cursor.getString(i + 3)
        }
        Boolean z2 = cursor.getShort(i + 4) != (Short) 0
        if (cursor.getShort(i + 5) == (Short) 0) {
            z = false
        }
        return User(valueOf, fromString, string, str, z2, z, cursor.getInt(i + 6), cursor.getInt(i + 7))
    }

    fun readEntity(Cursor cursor, User user, Int i) {
        Boolean z = true
        String str = null
        user.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        user.setUuid(cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1)))
        user.setUserName(cursor.isNull(i + 2) ? null : cursor.getString(i + 2))
        if (!cursor.isNull(i + 3)) {
            str = cursor.getString(i + 3)
        }
        user.setDisplayName(str)
        user.setBadUUID(cursor.getShort(i + 4) != (Short) 0)
        if (cursor.getShort(i + 5) == (Short) 0) {
            z = false
        }
        user.setIsFriend(z)
        user.setRightsGiven(cursor.getInt(i + 6))
        user.setRightsHas(cursor.getInt(i + 7))
    }

    public Long readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected Long updateKeyAfterInsert(User user, Long j) {
        user.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
