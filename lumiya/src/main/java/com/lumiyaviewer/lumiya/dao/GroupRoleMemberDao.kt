package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupRoleMemberDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<GroupRoleMember, Void>(config, daoSession) {

    companion object {
        const val TABLENAME = "GroupRoleMembers"

        object Properties {
            @JvmField val GroupID = Property(0, String::class.java, "groupID", false, "GROUP_ID")
            @JvmField val RequestID = Property(1, String::class.java, "requestID", false, "REQUEST_ID")
            @JvmField val RoleID = Property(2, String::class.java, "roleID", false, "ROLE_ID")
            @JvmField val UserID = Property(3, String::class.java, "userID", false, "USER_ID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'GroupRoleMembers' (" +
                    "'GROUP_ID' TEXT NOT NULL ," +
                    "'REQUEST_ID' TEXT NOT NULL ," +
                    "'ROLE_ID' TEXT NOT NULL ," +
                    "'USER_ID' TEXT NOT NULL );")
            db.execSQL("CREATE INDEX ${constraint}IDX_GroupRoleMembers_GROUP_ID_ROLE_ID_REQUEST_ID ON GroupRoleMembers (GROUP_ID,ROLE_ID,REQUEST_ID);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'GroupRoleMembers'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: GroupRoleMember) {
        stmt.clearBindings()
        stmt.bindString(1, entity.groupID.toString())
        stmt.bindString(2, entity.requestID.toString())
        stmt.bindString(3, entity.roleID.toString())
        stmt.bindString(4, entity.userID.toString())
    }

    override fun getKey(entity: GroupRoleMember?): Void? {
        return null
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): GroupRoleMember {
        return GroupRoleMember(
            UUID.fromString(cursor.getString(offset + 0)),
            UUID.fromString(cursor.getString(offset + 1)),
            UUID.fromString(cursor.getString(offset + 2)),
            UUID.fromString(cursor.getString(offset + 3))
        )
    }

    override fun readEntity(cursor: Cursor, entity: GroupRoleMember, offset: Int) {
        entity.groupID = UUID.fromString(cursor.getString(offset + 0))
        entity.requestID = UUID.fromString(cursor.getString(offset + 1))
        entity.roleID = UUID.fromString(cursor.getString(offset + 2))
        entity.userID = UUID.fromString(cursor.getString(offset + 3))
    }

    override fun readKey(cursor: Cursor, offset: Int): Void? {
        return null
    }

    override fun updateKeyAfterInsert(entity: GroupRoleMember, rowId: Long): Void? {
        return null
    }
}
