package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupRoleMemberDao : AbstractDao()<GroupRoleMember, Void> {
    const val TABLENAME: String = "GroupRoleMembers"

    @JvmStatic
    class Properties {
        const val Property GroupID = Property(0, UUID.class, "groupID", false, "GROUP_ID")
        const val Property RequestID = Property(1, UUID.class, "requestID", false, "REQUEST_ID")
        const val Property RoleID = Property(2, UUID.class, "roleID", false, "ROLE_ID")
        const val Property UserID = Property(3, UUID.class, "userID", false, "USER_ID")
    }

    public GroupRoleMemberDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public GroupRoleMemberDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        val str: String = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'GroupRoleMembers' (" + "'GROUP_ID' TEXT NOT NULL ," + "'REQUEST_ID' TEXT NOT NULL ," + "'ROLE_ID' TEXT NOT NULL ," + "'USER_ID' TEXT NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_GroupRoleMembers_GROUP_ID_ROLE_ID_REQUEST_ID ON GroupRoleMembers" + " (GROUP_ID,ROLE_ID,REQUEST_ID);")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupRoleMembers'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, groupRoleMember: GroupRoleMember) {
        sQLiteStatement.clearBindings()
        sQLiteStatement.bindString(1, groupRoleMember.getGroupID().toString())
        sQLiteStatement.bindString(2, groupRoleMember.getRequestID().toString())
        sQLiteStatement.bindString(3, groupRoleMember.getRoleID().toString())
        sQLiteStatement.bindString(4, groupRoleMember.getUserID().toString())
    }

     public fun getKey(groupRoleMember: GroupRoleMember): Void {
        return null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): GroupRoleMember {
        return GroupRoleMember(UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)), UUID.fromString(cursor.getString(i + 2)), UUID.fromString(cursor.getString(i + 3)))
    }

    fun readEntity(cursor: Cursor, groupRoleMember: GroupRoleMember, i: Int) {
        groupRoleMember.setGroupID(UUID.fromString(cursor.getString(i + 0)))
        groupRoleMember.setRequestID(UUID.fromString(cursor.getString(i + 1)))
        groupRoleMember.setRoleID(UUID.fromString(cursor.getString(i + 2)))
        groupRoleMember.setUserID(UUID.fromString(cursor.getString(i + 3)))
    }

     public fun readKey(cursor: Cursor, i: Int): Void {
        return null
    }

     protected fun updateKeyAfterInsert(groupRoleMember: GroupRoleMember, j: Long): Void {
        return null
    }
}
