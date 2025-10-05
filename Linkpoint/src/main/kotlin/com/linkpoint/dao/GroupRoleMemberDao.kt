package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupRoleMemberDao : AbstractDao()<GroupRoleMember, Void> {
    const val String TABLENAME = "GroupRoleMembers"

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
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'GroupRoleMembers' (" + "'GROUP_ID' TEXT NOT NULL ," + "'REQUEST_ID' TEXT NOT NULL ," + "'ROLE_ID' TEXT NOT NULL ," + "'USER_ID' TEXT NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_GroupRoleMembers_GROUP_ID_ROLE_ID_REQUEST_ID ON GroupRoleMembers" + " (GROUP_ID,ROLE_ID,REQUEST_ID);")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'GroupRoleMembers'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, GroupRoleMember groupRoleMember) {
        sQLiteStatement.clearBindings()
        sQLiteStatement.bindString(1, groupRoleMember.getGroupID().toString())
        sQLiteStatement.bindString(2, groupRoleMember.getRequestID().toString())
        sQLiteStatement.bindString(3, groupRoleMember.getRoleID().toString())
        sQLiteStatement.bindString(4, groupRoleMember.getUserID().toString())
    }

    public Void getKey(GroupRoleMember groupRoleMember) {
        return null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public GroupRoleMember readEntity(Cursor cursor, Int i) {
        return GroupRoleMember(UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)), UUID.fromString(cursor.getString(i + 2)), UUID.fromString(cursor.getString(i + 3)))
    }

    public Unit readEntity(Cursor cursor, GroupRoleMember groupRoleMember, Int i) {
        groupRoleMember.setGroupID(UUID.fromString(cursor.getString(i + 0)))
        groupRoleMember.setRequestID(UUID.fromString(cursor.getString(i + 1)))
        groupRoleMember.setRoleID(UUID.fromString(cursor.getString(i + 2)))
        groupRoleMember.setUserID(UUID.fromString(cursor.getString(i + 3)))
    }

    public Void readKey(Cursor cursor, Int i) {
        return null
    }

    protected Void updateKeyAfterInsert(GroupRoleMember groupRoleMember, Long j) {
        return null
    }
}
