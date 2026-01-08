package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class GroupRoleMemberListDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<GroupRoleMemberList, Void>(config, daoSession) {

    companion object {
        const val TABLENAME = "GroupRoleMemberLists"

        object Properties {
            @JvmField val GroupID = Property(0, String::class.java, "groupID", false, "GROUP_ID")
            @JvmField val RoleID = Property(1, String::class.java, "roleID", false, "ROLE_ID")
            @JvmField val MemberID = Property(2, String::class.java, "memberID", false, "MEMBER_ID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'GroupRoleMemberLists' (" +
                    "'GROUP_ID' TEXT NOT NULL ," +
                    "'ROLE_ID' TEXT NOT NULL ," +
                    "'MEMBER_ID' TEXT NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'GroupRoleMemberLists'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: GroupRoleMemberList) {
        stmt.clearBindings()
        stmt.bindString(1, entity.groupID.toString())
        stmt.bindString(2, entity.roleID.toString())
        stmt.bindString(3, entity.memberID.toString())
    }

    override fun getKey(entity: GroupRoleMemberList?): Void? {
        return null
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): GroupRoleMemberList {
        val groupID = if (cursor.isNull(offset + 0)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 0))
        val roleID = if (cursor.isNull(offset + 1)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 1))
        val memberID = if (cursor.isNull(offset + 2)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 2))
        return GroupRoleMemberList(groupID, roleID, memberID)
    }

    override fun readEntity(cursor: Cursor, entity: GroupRoleMemberList, offset: Int) {
        entity.groupID = if (cursor.isNull(offset + 0)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 0))
        entity.roleID = if (cursor.isNull(offset + 1)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 1))
        entity.memberID = if (cursor.isNull(offset + 2)) UUID(0, 0) else UUID.fromString(cursor.getString(offset + 2))
    }

    override fun readKey(cursor: Cursor, offset: Int): Void? {
        return null
    }

    override fun updateKeyAfterInsert(entity: GroupRoleMemberList, rowId: Long): Void? {
        return null
    }
}
