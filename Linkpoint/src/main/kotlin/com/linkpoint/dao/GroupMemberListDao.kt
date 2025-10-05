package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupMemberListDao : AbstractDao()<GroupMemberList, UUID> {
    const val TABLENAME: String = "GroupMemberLists"

    @JvmStatic
    class Properties {
        const val Property GroupID = Property(0, UUID.class, "groupID", true, "GROUP_ID")
        const val Property RequestID = Property(1, UUID.class, "requestID", false, "REQUEST_ID")
    }

    public GroupMemberListDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public GroupMemberListDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupMemberLists'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, GroupMemberList groupMemberList) {
        sQLiteStatement.clearBindings()
        UUID groupID = groupMemberList.getGroupID()
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString())
        }
        sQLiteStatement.bindString(2, groupMemberList.getRequestID().toString())
    }

    public UUID getKey(GroupMemberList groupMemberList) {
        return groupMemberList != null ? groupMemberList.getGroupID() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public GroupMemberList readEntity(Cursor cursor, Int i) {
        return GroupMemberList(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)))
    }

    fun readEntity(Cursor cursor, GroupMemberList groupMemberList, Int i) {
        groupMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        groupMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)))
    }

    public UUID readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected UUID updateKeyAfterInsert(GroupMemberList groupMemberList, Long j) {
        return groupMemberList.getGroupID()
    }
}
