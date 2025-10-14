package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupMemberListDao : AbstractDao<GroupMemberList, UUID> {
    String TABLENAME = "GroupMemberLists";

    class Properties {
        Property GroupID = Property(0, UUID.class, "groupID", true, "GROUP_ID");
        Property RequestID = Property(1, UUID.class, "requestID", false, "REQUEST_ID");
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL );");
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupMemberLists'");
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, groupMemberList: GroupMemberList): Unit {
        sQLiteStatement.clearBindings()
        UUID groupID = groupMemberList.getGroupID()
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString())
        }
        sQLiteStatement.bindString(2, groupMemberList.getRequestID().toString())
    }

    fun getKey(groupMemberList: GroupMemberList): UUID {
        return groupMemberList != null ? groupMemberList.getGroupID() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): GroupMemberList {
        return GroupMemberList(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)))
    }

    fun readEntity(cursor: Cursor, groupMemberList: GroupMemberList, i: Int): Unit {
        groupMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        groupMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)))
    }

    fun readKey(cursor: Cursor, i: Int): UUID {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected fun updateKeyAfterInsert(groupMemberList: GroupMemberList, j: Long): UUID {
        return groupMemberList.getGroupID()
    }
}
