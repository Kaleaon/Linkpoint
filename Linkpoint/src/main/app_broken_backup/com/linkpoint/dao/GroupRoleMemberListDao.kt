package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupRoleMemberListDao : AbstractDao<GroupRoleMemberList, UUID> {
    val TABLENAME: String = "GroupRoleMemberLists"

    class Properties {
        Property GroupID = Property(0, UUID.class, "groupID", true, "GROUP_ID")
        Property MustRevalidate = Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE")
        Property RequestID = Property(1, UUID.class, "requestID", false, "REQUEST_ID")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean)  {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'GroupRoleMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL ," + "'MUST_REVALIDATE' INTEGER NOT NULL );")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean)  {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupRoleMemberLists'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, groupRoleMemberList: GroupRoleMemberList)  {
        sQLiteStatement.clearBindings()
        UUID groupID = groupRoleMemberList.getGroupID()
        if (groupID != null) {
            sQLiteStatement.bindString(1, groupID.toString())
        }
        sQLiteStatement.bindString(2, groupRoleMemberList.getRequestID().toString())
        sQLiteStatement.bindLong(3, groupRoleMemberList.getMustRevalidate() ? 1 : 0)
    }

    fun getKey(groupRoleMemberList: GroupRoleMemberList): UUID {
        return groupRoleMemberList != null ? groupRoleMemberList.getGroupID() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): GroupRoleMemberList {
        var z: Boolean = false
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
        UUID fromString2 = UUID.fromString(cursor.getString(i + 1))
        if (cursor.getShort(i + 2) != (Short) 0) {
            z = true
        }
        return GroupRoleMemberList(fromString, fromString2, z)
    }

    fun readEntity(cursor: Cursor, groupRoleMemberList: GroupRoleMemberList, i: Int)  {
        groupRoleMemberList.setGroupID(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        groupRoleMemberList.setRequestID(UUID.fromString(cursor.getString(i + 1)))
        groupRoleMemberList.setMustRevalidate(cursor.getShort(i + 2) != (Short) 0)
    }

    fun readKey(cursor: Cursor, i: Int): UUID {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected fun updateKeyAfterInsert(groupRoleMemberList: GroupRoleMemberList, j: Long): UUID {
        return groupRoleMemberList.getGroupID()
    }
}
