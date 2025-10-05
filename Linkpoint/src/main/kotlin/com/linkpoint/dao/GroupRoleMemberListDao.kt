package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupRoleMemberListDao : AbstractDao()<GroupRoleMemberList, UUID> {
    const val TABLENAME: String = "GroupRoleMemberLists"

    @JvmStatic
    class Properties {
        const val Property GroupID = Property(0, UUID.class, "groupID", true, "GROUP_ID")
        const val Property MustRevalidate = Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE")
        const val Property RequestID = Property(1, UUID.class, "requestID", false, "REQUEST_ID")
    }

    public GroupRoleMemberListDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public GroupRoleMemberListDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupRoleMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL ," + "'MUST_REVALIDATE' INTEGER NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupRoleMemberLists'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, GroupRoleMemberList groupRoleMemberList) {
        sQLiteStatement.clearBindings()
        UUID groupID = groupRoleMemberList.getGroupID()
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString())
        }
        sQLiteStatement.bindString(2, groupRoleMemberList.getRequestID().toString())
        sQLiteStatement.bindLong(3, groupRoleMemberList.getMustRevalidate() ? 1 : 0)
    }

    public UUID getKey(GroupRoleMemberList groupRoleMemberList) {
        return groupRoleMemberList != null ? groupRoleMemberList.getGroupID() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public GroupRoleMemberList readEntity(Cursor cursor, Int i) {
        Boolean z = false
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
        UUID fromString2 = UUID.fromString(cursor.getString(i + 1))
        if (cursor.getShort(i + 2) != (Short) 0) {
            z = true
        }
        return GroupRoleMemberList(fromString, fromString2, z)
    }

    public Unit readEntity(Cursor cursor, GroupRoleMemberList groupRoleMemberList, Int i) {
        groupRoleMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        groupRoleMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)))
        groupRoleMemberList.setMustRevalidate(cursor.getShort(i + 2) != (Short) 0)
    }

    public UUID readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected UUID updateKeyAfterInsert(GroupRoleMemberList groupRoleMemberList, Long j) {
        return groupRoleMemberList.getGroupID()
    }
}
